package com.fancier.missingyou.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fancier.missingyou.common.model.dto.questionBankQuestion.QuestionBankQuestionQueryRequest;
import com.fancier.missingyou.common.model.entity.QuestionBankQuestion;
import com.fancier.missingyou.common.model.entity.User;
import com.fancier.missingyou.common.model.vo.QuestionBankQuestionVO;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题库题目关联服务
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 *
 */
public interface QuestionBankQuestionService extends IService<QuestionBankQuestion> {

    /**
    * 校验数据
    *
    * @param add 对创建的数据进行校验
    */
    void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion, boolean add);


    /**
     * 获取查询条件
     *
     */
    QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest);

    /**
     * 获取题库题目关联封装
     */
    QuestionBankQuestionVO getQuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion, HttpServletRequest request);

    /**
     * 批量添加题目到题库
     *
     */
    void batchAddQuestionsToBank(List<Long> questionIdList, long questionBankId, User loginUser);

    /**
     * 批量从题库移除题目
     */
    void batchRemoveQuestionsFromBank(List<Long> questionIdList, long questionBankId);

    /**
     * 批量添加题目到题库（事务，仅供内部调用）
     *
     */
    @Transactional(rollbackFor = Exception.class)
    void batchAddQuestionsToBankInner(List<QuestionBankQuestion> questionBankQuestions);

}
