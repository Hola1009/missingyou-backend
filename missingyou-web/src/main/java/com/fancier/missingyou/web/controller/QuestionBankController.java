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
import com.fancier.missingyou.common.model.dto.question.QuestionQueryRequest;
import com.fancier.missingyou.common.model.dto.questionBank.QuestionBankAddRequest;
import com.fancier.missingyou.common.model.dto.questionBank.QuestionBankEditRequest;
import com.fancier.missingyou.common.model.dto.questionBank.QuestionBankQueryRequest;
import com.fancier.missingyou.common.model.dto.questionBank.QuestionBankUpdateRequest;
import com.fancier.missingyou.common.model.entity.QuestionBank;
import com.fancier.missingyou.common.model.entity.User;
import com.fancier.missingyou.common.model.vo.QuestionBankVO;
import com.fancier.missingyou.common.model.vo.QuestionVO;
import com.fancier.missingyou.web.mapstruct.QuestionBankConvert;
import com.fancier.missingyou.web.service.QuestionBankService;
import com.fancier.missingyou.web.service.QuestionService;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 题库接口
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 *
 */
@RestController
@RequestMapping("/questionBank")
@RequiredArgsConstructor
@Slf4j
public class QuestionBankController {

    private final QuestionBankService questionBankService;

    private final QuestionService questionService;

    private final UserService userService;


    /**
     * 创建题库
     *
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestionBank(@RequestBody QuestionBankAddRequest questionBankAddRequest, HttpServletRequest ignoredRequest) {
        Preconditions.checkArgument(questionBankAddRequest != null);
        // 在此处将实体类和 DTO 进行转换
        QuestionBank questionBank = QuestionBankConvert.INSTANCE.addDTO2DO(questionBankAddRequest) ;

        // 数据校验
        questionBankService.validQuestionBank(questionBank, true);
        // 填充默认值
        User loginUser = userService.getLoginUser(ignoredRequest);
        questionBank.setUserId(loginUser.getId());

        // 写入数据库
        boolean result = questionBankService.save(questionBank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 返回新写入的数据 id
        long newQuestionBankId = questionBank.getId();
        return ResultUtils.success(newQuestionBankId);
    }

    /**
     * 删除题库
     *
     */
    @PostMapping("/delete")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteQuestionBank(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        Preconditions.checkArgument(deleteRequest != null && deleteRequest.getId() > 0);
        long id = deleteRequest.getId();
        // 判断是否存在
        check(id, request);

        // 操作数据库
        boolean result = questionBankService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新题库（仅管理员可用）
     *
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestionBank(@RequestBody QuestionBankUpdateRequest questionBankUpdateRequest) {
        Preconditions.checkArgument(questionBankUpdateRequest != null && questionBankUpdateRequest.getId() > 0);

        QuestionBank questionBank = QuestionBankConvert.INSTANCE.updateDTO2DO(questionBankUpdateRequest);

        questionBankService.validQuestionBank(questionBank, false);

        // 判断是否存在
        long id = questionBankUpdateRequest.getId();
        QuestionBank oldQuestionBank = questionBankService.getById(id);
        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = questionBankService.updateById(questionBank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取题库（封装类）
     *
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionBankVO> getQuestionBankVOById(QuestionBankQueryRequest questionBankQueryRequest, HttpServletRequest ignoredRequest) {
        Long id = questionBankQueryRequest.getId();
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        QuestionBank questionBank = questionBankService.getById(id);
        ThrowUtils.throwIf(questionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        QuestionBankVO questionBankVO = QuestionBankConvert.INSTANCE.DO2QuestionBankVO(questionBank);

        // 如果需要返回关联题目列表的话
        boolean needQueryQuestionList = questionBankQueryRequest.isNeedQueryQuestionList();
        if (needQueryQuestionList) {
            QuestionQueryRequest questionQueryRequest = new QuestionQueryRequest();
            questionQueryRequest.setQuestionBankId(id);
            // 可以按需支持更多的题目搜索参数，比如分页
            questionQueryRequest.setPageSize(questionBankQueryRequest.getPageSize());
            questionQueryRequest.setCurrent(questionBankQueryRequest.getCurrent());
            Page<QuestionVO> questionVOPage = questionService.VOPageQuery(questionQueryRequest);
            questionBankVO.setQuestionPage(questionVOPage);
        }

        return ResultUtils.success(questionBankVO);
    }

    /**
     * 分页获取题库列表（仅管理员可用）
     *
     */
    @PostMapping("/list/page")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionBank>> listQuestionBankByPage(@RequestBody QuestionBankQueryRequest questionBankQueryRequest) {
        Page<QuestionBank> questionBankPage = questionBankService.pageQuery(questionBankQueryRequest);
        return ResultUtils.success(questionBankPage);
    }

    /**
     * 分页获取题库列表（封装类）
     *
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionBankVO>> listQuestionBankVOByPage(@RequestBody QuestionBankQueryRequest questionBankQueryRequest,
                                                               HttpServletRequest ignoredRequest) {
        Page<QuestionBankVO> questionBankVOPage = questionBankService.VOPageQuery(questionBankQueryRequest);
            return ResultUtils.success(questionBankVOPage);
    }

    /**
     * 编辑题库（给用户使用）
     *
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestionBank(@RequestBody QuestionBankEditRequest questionBankEditRequest, HttpServletRequest request) {

        Preconditions.checkArgument(questionBankEditRequest != null && questionBankEditRequest.getId() > 0);

        QuestionBank questionBank = QuestionBankConvert.INSTANCE.editDTO2DO(questionBankEditRequest);
        // 数据校验
        questionBankService.validQuestionBank(questionBank, false);
        // 判断是否存在
        long id = questionBankEditRequest.getId();
        check(id, request);

        // 操作数据库
        boolean result = questionBankService.updateById(questionBank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    private void check(Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        QuestionBank oldQuestionBank = questionBankService.getById(id);
        ThrowUtils.throwIf(oldQuestionBank == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        ThrowUtils.throwIf(!oldQuestionBank.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR);
    }

    /**
     * 分页获取当前登录用户创建的题库列表
     *
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionBankVO>> listMyQuestionBankVOByPage(@RequestBody QuestionBankQueryRequest questionBankQueryRequest,
                                                                         HttpServletRequest request) {
        ThrowUtils.throwIf(questionBankQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        questionBankQueryRequest.setUserId(loginUser.getId());
        // 获取封装类
        return ResultUtils.success(questionBankService.VOPageQuery(questionBankQueryRequest));
    }
}
