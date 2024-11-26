package com.fancier.missingyou.web.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fancier.missingyou.common.constant.CommonConstant;
import com.fancier.missingyou.common.expection.BusinessException;
import com.fancier.missingyou.common.expection.ErrorCode;
import com.fancier.missingyou.common.expection.ThrowUtils;
import com.fancier.missingyou.common.mapper.QuestionBankMapper;
import com.fancier.missingyou.common.mapper.QuestionBankQuestionMapper;
import com.fancier.missingyou.common.mapper.QuestionMapper;
import com.fancier.missingyou.common.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.fancier.missingyou.common.model.entity.Question;
import com.fancier.missingyou.common.model.entity.QuestionBank;
import com.fancier.missingyou.common.model.entity.QuestionBankQuestion;
import com.fancier.missingyou.common.model.entity.User;
import com.fancier.missingyou.common.model.vo.QuestionBankQuestionVO;
import com.fancier.missingyou.common.util.SqlUtils;
import com.fancier.missingyou.web.mapstruct.QuestionBankQuestionConvert;
import com.fancier.missingyou.web.service.QuestionBankQuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 题库题目关联服务实现
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion> implements QuestionBankQuestionService {

    private final QuestionMapper questionMapper;

    private final QuestionBankMapper questionBankMapper;

    /**
     *
     * 对创建的数据进行校验
     */
    @Override
    public void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add) {
        ThrowUtils.throwIf(questionBankQuestion == null, ErrorCode.PARAMS_ERROR);
        // 题目和题库必须存在
        Long questionId = questionBankQuestion.getQuestionId();
        if (questionId != null) {
            Question question = questionMapper.selectById(questionId);
            ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        Long questionBankId = questionBankQuestion.getQuestionBankId();
        if (questionBankId != null) {
            QuestionBank questionBank = questionBankMapper.selectById(questionId);
            ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR, "题库不存在");
        }
    }


    /**
     * 获取题库题目关联封装
     *
     */
    @Override
    public QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request) {
        return QuestionBankQuestionConvert.INSTANCE.DO2QuestionBankQuestionVO(questionBankQuestion);
    }



    /**
     * 批量添加题目到题库
     *
     */
    @Override
    public void batchAddQuestionsToBank(List<Long> questionIdList, long questionBankId, User loginUser) {
        // 参数校验
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目列表不能为空");
        ThrowUtils.throwIf(questionBankId <= 0, ErrorCode.PARAMS_ERROR, "题库 id 非法");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        // 检查题库 id 是否存在
        QuestionBank questionBank = questionBankMapper.selectById(questionBankId);
        ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR, "题库不存在");



        // 检查题目 id 是否存在
        LambdaQueryWrapper<Question> questionLambdaQueryWrapper = Wrappers.lambdaQuery(Question.class)
                .select(Question::getId)
                .in(Question::getId, questionIdList);
        // 合法的题目 id 列表
        List<Long> validQuestionIdList = questionMapper.selectList(questionLambdaQueryWrapper)
                .stream()
                .map(Question::getId)
                .collect(Collectors.toList());
        ThrowUtils.throwIf(CollUtil.isEmpty(validQuestionIdList), ErrorCode.PARAMS_ERROR, "合法的题目 id 列表为空");



        // 检查哪些题目还不存在于题库中，避免重复插入
        LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                .eq(QuestionBankQuestion::getQuestionBankId, questionBankId)
                .in(QuestionBankQuestion::getQuestionId, validQuestionIdList);
        // 已存在于题库中的题目 id
        Set<Long> existQuestionIdSet = this.list(lambdaQueryWrapper).stream()
                .map(QuestionBankQuestion::getId)
                .collect(Collectors.toSet());
        // 已存在于题库中的题目 id，不需要再次添加
        validQuestionIdList = validQuestionIdList.stream()
                .filter(questionId -> !existQuestionIdSet.contains(questionId))
                .collect(Collectors.toList());
        ThrowUtils.throwIf(CollUtil.isEmpty(validQuestionIdList), ErrorCode.PARAMS_ERROR, "所有题目都已存在于题库中");



        // 自定义线程池（IO 密集型线程池）
        ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(
                20,             // 核心线程数
                50,                        // 最大线程数
                60L,                       // 线程空闲存活时间
                TimeUnit.SECONDS,           // 存活时间单位
                new LinkedBlockingQueue<>(10000),  // 阻塞队列容量
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略：由调用线程处理任务
        );

        // 保存所有批次任务
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        // 分批处理，避免长事务，假设每次处理 1000 条数据
        int batchSize = 1000;
        int totalQuestionListSize = validQuestionIdList.size();

        // 获取代理
        QuestionBankQuestionService questionBankQuestionService = (QuestionBankQuestionServiceImpl) AopContext.currentProxy();

        for (int i = 0; i < totalQuestionListSize; i += batchSize) {
            // 生成每批次的数据
            List<Long> subList = validQuestionIdList.subList(i, Math.min(i + batchSize, totalQuestionListSize));
            List<QuestionBankQuestion> questionBankQuestions = subList.stream()
                    .map(questionId -> {
                        QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
                        questionBankQuestion.setQuestionBankId(questionBankId);
                        questionBankQuestion.setQuestionId(questionId);
                        questionBankQuestion.setUserId(loginUser.getId());
                        return questionBankQuestion;
                    }).collect(Collectors.toList());

            // 异步处理每批数据，将任务添加到异步任务列表
            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
                questionBankQuestionService.batchAddQuestionsToBankInner(questionBankQuestions)
            , customExecutor);
            futures.add(future);
        }
        // 等待所有批次完成操作
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        // 关闭线程池
        customExecutor.shutdown();
    }

    /**
     * 批量添加题目到题库（事务，仅供内部调用）
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddQuestionsToBankInner(List<QuestionBankQuestion> questionBankQuestions) {
        try {
            boolean result = this.saveBatch(questionBankQuestions);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "向题库添加题目失败");
        } catch (DataIntegrityViolationException e) {
            log.error("数据库唯一键冲突或违反其他完整性约束, 错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目已存在于该题库，无法重复添加");
        } catch (DataAccessException e) {
            log.error("数据库连接问题、事务问题等导致操作失败, 错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库操作失败");
        } catch (Exception e) {
            // 捕获其他异常，做通用处理
            log.error("添加题目到题库时发生未知错误，错误信息: {}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "向题库添加题目失败");
        }
    }

    /**
     * 批量从题库移除题目
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchRemoveQuestionsFromBank(List<Long> questionIdList, long questionBankId) {
        // 参数校验
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目列表不能为空");
        ThrowUtils.throwIf(questionBankId <= 0, ErrorCode.PARAMS_ERROR, "题库 id 非法");
        // 执行删除关联
        for (long questionId : questionIdList) {
            // 构造查询
            LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                    .eq(QuestionBankQuestion::getQuestionId, questionId)
                    .eq(QuestionBankQuestion::getQuestionBankId, questionBankId);
            boolean result = this.remove(lambdaQueryWrapper);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "从题库移除题目失败");
        }
    }

    /**
     * 获取查询条件
     */
    @Override
    public QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest request) {
        QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
        if (request == null) {
            return queryWrapper;
        }
        // Use method references for better readability
        setQueryConditions(queryWrapper, request);
        setSortingRules(queryWrapper, request);
        return queryWrapper;
    }

    /**
     * 设置查询条件
     */
    private void setQueryConditions(QueryWrapper<QuestionBankQuestion> queryWrapper, QuestionBankQuestionQueryRequest request) {
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(request.getNotId()), "id", request.getNotId())
                .eq(ObjectUtils.isNotEmpty(request.getId()), "id", request.getId())
                .eq(ObjectUtils.isNotEmpty(request.getUserId()), "userId", request.getUserId())
                .eq(ObjectUtils.isNotEmpty(request.getQuestionBankId()), "questionBankId", request.getQuestionBankId())
                .eq(ObjectUtils.isNotEmpty(request.getQuestionId()), "questionId", request.getQuestionId());
    }

    /**
     * 设置排序规则
     */
    private void setSortingRules(QueryWrapper<QuestionBankQuestion> queryWrapper, QuestionBankQuestionQueryRequest request) {
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(request.getSortField()),
                request.getSortOrder().equals(CommonConstant.SORT_ORDER_ASC),
                request.getSortField());
    }
}
