package com.fancier.missingyou.common.model.dto.questionBank;

import lombok.Data;

/**
 * 更新题库请求
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 * 
 */
@Data
public class QuestionBankUpdateRequest {

    /**
     * id
     */
    private Long id;

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
}