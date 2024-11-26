package com.fancier.missingyou.web.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fancier.missingyou.auth.service.UserService;
import com.fancier.missingyou.common.constant.UserConstant;
import com.fancier.missingyou.common.expection.ErrorCode;
import com.fancier.missingyou.common.expection.ThrowUtils;
import com.fancier.missingyou.common.model.common.BaseResponse;
import com.fancier.missingyou.common.model.common.DeleteRequest;
import com.fancier.missingyou.common.model.common.ResultUtils;
import com.fancier.missingyou.common.model.dto.questionBankQuestion.*;
import com.fancier.missingyou.common.model.entity.QuestionBankQuestion;
import com.fancier.missingyou.common.model.entity.User;
import com.fancier.missingyou.common.model.vo.QuestionBankQuestionVO;
import com.fancier.missingyou.web.mapstruct.QuestionBankQuestionConvert;
import com.fancier.missingyou.web.service.QuestionBankQuestionService;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题库题目关联接口
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 *
 */
@RestController
@RequestMapping("/questionBankQuestion")
@RequiredArgsConstructor
@Slf4j
public class QuestionBankQuestionController {

    private final QuestionBankQuestionService questionBankQuestionService;

    private final UserService userService;

    /**
     * 创建题库题目关联
     *
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestionBankQuestion(@RequestBody QuestionBankQuestionAddRequest questionBankQuestionAddRequest, HttpServletRequest ignoredRequest) {
        Preconditions.checkArgument(questionBankQuestionAddRequest != null);
        // 在此处将实体类和 DTO 进行转换
        QuestionBankQuestion questionBankQuestion = QuestionBankQuestionConvert.INSTANCE.addDTO2DO(questionBankQuestionAddRequest) ;

        // 数据校验
        questionBankQuestionService.validQuestionBankQuestion(questionBankQuestion, true);
        // 填充默认值
        User loginUser = userService.getLoginUser(ignoredRequest);
        questionBankQuestion.setUserId(loginUser.getId());

        // 填充默认值
        // 写入数据库
        boolean result = questionBankQuestionService.save(questionBankQuestion);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionBankQuestionId = questionBankQuestion.getId();
        return ResultUtils.success(newQuestionBankQuestionId);
    }

    /**
     * 删除题库题目关联
     *
     */
    @PostMapping("/delete")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteQuestionBankQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        Preconditions.checkArgument(deleteRequest != null && deleteRequest.getId() > 0);
        long id = deleteRequest.getId();
        // 判断是否存在
        check(id, request);

        // 操作数据库
        boolean result = questionBankQuestionService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新题库题目关联（仅管理员可用）
     *
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestionBankQuestion(@RequestBody QuestionBankQuestionUpdateRequest questionBankQuestionUpdateRequest) {
        Preconditions.checkArgument(questionBankQuestionUpdateRequest != null && questionBankQuestionUpdateRequest.getId() > 0);

        QuestionBankQuestion questionBankQuestion = QuestionBankQuestionConvert.INSTANCE.updateDTO2DO(questionBankQuestionUpdateRequest);
        // 数据校验

        // 判断是否存在
        long id = questionBankQuestionUpdateRequest.getId();
        QuestionBankQuestion oldQuestionBankQuestion = questionBankQuestionService.getById(id);
        ThrowUtils.throwIf(oldQuestionBankQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionBankQuestionService.updateById(questionBankQuestion);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题库题目关联（封装类）
     *
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionBankQuestionVO> getQuestionBankQuestionVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        QuestionBankQuestion questionBankQuestion = questionBankQuestionService.getById(id);
        ThrowUtils.throwIf(questionBankQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(questionBankQuestionService.getQuestionBankQuestionVO(questionBankQuestion, request));
    }

    /**
     * 移除题库题目关联（仅管理员可用）
     *
     */
    @PostMapping("/remove")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> removeQuestionBankQuestion(
            @RequestBody QuestionBankQuestionRemoveRequest removeRequest
    ) {
        // 参数校验
        ThrowUtils.throwIf(removeRequest == null, ErrorCode.PARAMS_ERROR);
        Long questionBankId = removeRequest.getQuestionBankId();
        Long questionId = removeRequest.getQuestionId();
        ThrowUtils.throwIf(questionBankId == null || questionId == null, ErrorCode.PARAMS_ERROR);

        // 构造查询
        LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                .eq(QuestionBankQuestion::getQuestionBankId, questionBankId)
                .eq(QuestionBankQuestion::getQuestionId, questionId);

        boolean result = questionBankQuestionService.remove(lambdaQueryWrapper);

        return ResultUtils.success(result);
    }

    /**
     * 批量添加题目到题库（仅管理员可用）
     *
     */
    @PostMapping("/add/batch")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> batchAddQuestionsToBank(
            @RequestBody QuestionBankQuestionBatchAddRequest addRequest,
            HttpServletRequest request
    ) {
        ThrowUtils.throwIf(addRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Long questionBankId = addRequest.getQuestionBankId();
        List<Long> questionIdList = addRequest.getQuestionIdList();
        questionBankQuestionService.batchAddQuestionsToBank(questionIdList, questionBankId, loginUser);
        return ResultUtils.success(true);
    }

    /**
     * 批量从题库移除题目（仅管理员可用）
     *
     */
    @PostMapping("/remove/batch")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> batchRemoveQuestionsFromBank(
            @RequestBody QuestionBankQuestionBatchRemoveRequest removeRequest,
            HttpServletRequest ignore
    ) {
        ThrowUtils.throwIf(removeRequest == null, ErrorCode.PARAMS_ERROR);
        Long questionBankId = removeRequest.getQuestionBankId();
        List<Long> questionIdList = removeRequest.getQuestionIdList();
        questionBankQuestionService.batchRemoveQuestionsFromBank(questionIdList, questionBankId);
        return ResultUtils.success(true);
    }

    private void check(Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        QuestionBankQuestion oldQuestionBankQuestion = questionBankQuestionService.getById(id);
        ThrowUtils.throwIf(oldQuestionBankQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        ThrowUtils.throwIf(!oldQuestionBankQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR);
    }

}
