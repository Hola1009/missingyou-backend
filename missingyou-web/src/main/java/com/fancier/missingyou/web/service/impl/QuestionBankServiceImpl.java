package com.fancier.missingyou.web.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fancier.missingyou.common.constant.CommonConstant;
import com.fancier.missingyou.common.expection.ErrorCode;
import com.fancier.missingyou.common.expection.ThrowUtils;
import com.fancier.missingyou.common.mapper.QuestionBankMapper;
import com.fancier.missingyou.common.model.dto.questionBank.QuestionBankQueryRequest;
import com.fancier.missingyou.common.model.entity.QuestionBank;
import com.fancier.missingyou.common.model.vo.QuestionBankVO;
import com.fancier.missingyou.common.util.SqlUtils;
import com.fancier.missingyou.web.mapstruct.QuestionBankConvert;
import com.fancier.missingyou.web.service.QuestionBankService;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 题库服务实现
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank> implements QuestionBankService {

    /**
    * 分页查询
    *
    */
    @Override
    public Page<QuestionBank> pageQuery(QuestionBankQueryRequest questionBankQueryRequest) {
        // 空参数检查
        Preconditions.checkArgument(questionBankQueryRequest != null, "请求参数为空");

        long current = questionBankQueryRequest.getCurrent();
        long size = questionBankQueryRequest.getPageSize();

        return this.page(new Page<>(current, size),
            this.getQueryWrapper(questionBankQueryRequest));
    }

    /**
    * 分页获取用户封装列表
    *
    */
    @Override
    public Page<QuestionBankVO> VOPageQuery(QuestionBankQueryRequest questionBankQueryRequest) {
        // 空参数检查
        Preconditions.checkArgument(questionBankQueryRequest != null, "请求参数为空");

        long current = questionBankQueryRequest.getCurrent();
        long size = questionBankQueryRequest.getPageSize();
        // 限制爬虫
        

        Page<QuestionBank> questionBankPage = this.pageQuery(questionBankQueryRequest);

        Page<QuestionBankVO> questionBankVOPage = new Page<>(current, size, questionBankPage.getTotal());

        // DO 列表转 VO 列表
        List<QuestionBankVO> questionBankVOS = questionBankPage.getRecords().stream()
            .map(QuestionBankConvert.INSTANCE::DO2QuestionBankVO)
            .collect(Collectors.toList());

        return questionBankVOPage.setRecords(questionBankVOS);
    }

    /**
     * 校验数据
     *
     * @param add 对创建的数据进行校验
     */
    @Override
    public void validQuestionBank(QuestionBank questionBank, boolean add) {
        ThrowUtils.throwIf(questionBank == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        String title = questionBank.getTitle();
        // 创建数据时，参数不能为空
        if (add) {
            // 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        // 补充校验规则
        if (StringUtils.isNotBlank(title)) {
            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        }
    }

    /**
     * 获取查询条件
     *
     */
    @Override
    public QueryWrapper<QuestionBank> getQueryWrapper(QuestionBankQueryRequest questionBankQueryRequest) {

        Preconditions.checkArgument(questionBankQueryRequest != null, "请求参数为空");

        QueryWrapper<QuestionBank> queryWrapper = new QueryWrapper<>();

        // 从对象中取值
        Long id = questionBankQueryRequest.getId();
        Long notId = questionBankQueryRequest.getNotId();
        String title = questionBankQueryRequest.getTitle();
        String searchText = questionBankQueryRequest.getSearchText();
        String sortField = questionBankQueryRequest.getSortField();
        String sortOrder = questionBankQueryRequest.getSortOrder();
        Long userId = questionBankQueryRequest.getUserId();
        String description = questionBankQueryRequest.getDescription();
        String picture = questionBankQueryRequest.getPicture();

        // 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("description", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(picture), "picture", picture);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                StrUtil.toUnderlineCase(sortField));
        return queryWrapper;
    }

}
