package com.fancier.missingyou.common.model.dto.questionBank;

import com.fancier.missingyou.common.model.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询题库请求
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 * 
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionBankQueryRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long notId;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 图片
     */
    private String picture;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 是否要关联查询题目列表
     */
    private boolean needQueryQuestionList;

}