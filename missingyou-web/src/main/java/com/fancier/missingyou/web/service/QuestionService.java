package com.fancier.missingyou.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fancier.missingyou.common.model.dto.question.QuestionQueryRequest;
import com.fancier.missingyou.common.model.entity.Question;
import com.fancier.missingyou.common.model.vo.QuestionVO;

/**
 * 用户评论服务
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 *
 */
public interface QuestionService extends IService<Question> {

    /**
     * 校验数据
     *
     * @param add      对创建的数据进行校验
     */
    void validQuestion(Question question, boolean add);


    /**
     * 获取查询条件
     *
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    /**
    * 分页查询
    */
    Page<Question> pageQuery(QuestionQueryRequest questionQueryRequest);

    /**
    * 分页获取封装列表
    */
    Page<QuestionVO> VOPageQuery(QuestionQueryRequest questionQueryRequest);

    /**
     * 从 ES 查询题目
     *
     */
    Page<QuestionVO> searchFromEs(QuestionQueryRequest questionQueryRequest);

}
