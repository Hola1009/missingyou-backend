package com.fancier.missingyou.web.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fancier.missingyou.auth.service.UserService;
import com.fancier.missingyou.common.constant.UserConstant;
import com.fancier.missingyou.common.expection.ErrorCode;
import com.fancier.missingyou.common.expection.ThrowUtils;
import com.fancier.missingyou.common.model.common.BaseResponse;
import com.fancier.missingyou.common.model.common.DeleteRequest;
import com.fancier.missingyou.common.model.common.ResultUtils;
import com.fancier.missingyou.common.model.dto.question.QuestionAddRequest;
import com.fancier.missingyou.common.model.dto.question.QuestionEditRequest;
import com.fancier.missingyou.common.model.dto.question.QuestionQueryRequest;
import com.fancier.missingyou.common.model.dto.question.QuestionUpdateRequest;
import com.fancier.missingyou.common.model.entity.Question;
import com.fancier.missingyou.common.model.entity.User;
import com.fancier.missingyou.common.model.vo.QuestionVO;
import com.fancier.missingyou.web.mapstruct.QuestionConvert;
import com.fancier.missingyou.web.service.QuestionService;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户评论接口
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 *
 */
@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
@Slf4j
public class QuestionController {

    private final QuestionService questionService;

    private final UserService userService;


    /**
     * 创建用户评论
     *
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        Preconditions.checkArgument(questionAddRequest != null);
        Question question = QuestionConvert.INSTANCE.addDTO2DO(questionAddRequest) ;

        // 数据校验
        questionService.validQuestion(question, true);

        // 填充默认值

        Long userId = userService.getLoginUser(request).getId();
        question.setUserId(userId);

        // 写入数据库
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除用户评论
     *
     */
    @PostMapping("/delete")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        Preconditions.checkArgument(deleteRequest != null && deleteRequest.getId() > 0);
        long id = deleteRequest.getId();
        // 判断是否存在
        check(id, request);

        // 操作数据库
        boolean result = questionService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新用户评论（仅管理员可用）
     *
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        Preconditions.checkArgument(questionUpdateRequest != null && questionUpdateRequest.getId() > 0);

        Question question = QuestionConvert.INSTANCE.updateDTO2DO(questionUpdateRequest);

        // 数据校验
        questionService.validQuestion(question, false);

        // 判断是否存在
        long id = questionUpdateRequest.getId();
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户（封装类）
     *
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest ignoredRequest) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Question question = questionService.getById(id);

        ThrowUtils.throwIf(question == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取封装类
        return ResultUtils.success(QuestionConvert.INSTANCE.DO2QuestionVO(question));
    }

    /**
     * 分页获取用户评论列表（仅管理员可用）
     *
     */
    @PostMapping("/list/page")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        Page<Question> questionPage = questionService.pageQuery(questionQueryRequest);
        return ResultUtils.success(questionPage);
    }

    /**
     * 分页获取用户评论列表（封装类）
     *
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest ignoredRequest) {
        Page<QuestionVO> questionVOPage = questionService.VOPageQuery(questionQueryRequest);
            return ResultUtils.success(questionVOPage);
    }

    @PostMapping("/search/page/vo")
    public BaseResponse<Page<QuestionVO>> searchQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest ignoreRequest) {
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 200, ErrorCode.PARAMS_ERROR);
        Page<QuestionVO> questionPage = questionService.searchFromEs(questionQueryRequest);
        return ResultUtils.success(questionPage);
    }


    /**
     * 编辑用户评论（给用户使用）
     *
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {

        Preconditions.checkArgument(questionEditRequest != null && questionEditRequest.getId() > 0);

        Question question = QuestionConvert.INSTANCE.editDTO2DO(questionEditRequest);

        // 判断是否存在
        questionService.validQuestion(question, false);
        long id = questionEditRequest.getId();
        check(id, request);

        // 操作数据库
        boolean result = questionService.updateById(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    private void check(Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        ThrowUtils.throwIf(!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR);
    }

}
