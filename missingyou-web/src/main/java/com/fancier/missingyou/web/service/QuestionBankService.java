package com.fancier.missingyou.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fancier.missingyou.common.model.dto.questionBank.QuestionBankQueryRequest;
import com.fancier.missingyou.common.model.entity.QuestionBank;
import com.fancier.missingyou.common.model.vo.QuestionBankVO;

/**
 * 题库服务
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 *
 */
public interface QuestionBankService extends IService<QuestionBank> {

    /**
     * 校验数据
     *
     * @param add 对创建的数据进行校验
     */
    void validQuestionBank(QuestionBank questionBank, boolean add);

    /**
     * 获取查询条件
     *
     */
    QueryWrapper<QuestionBank> getQueryWrapper(QuestionBankQueryRequest questionBankQueryRequest);

    /**
    * 分页查询
    */
    Page<QuestionBank> pageQuery(QuestionBankQueryRequest questionBankQueryRequest);

    /**
    * 分页获取封装列表
    */
    Page<QuestionBankVO> VOPageQuery(QuestionBankQueryRequest questionBankQueryRequest);

}
