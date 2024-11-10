package com.fancier.missingyou.common.model.dto.questionBankQuestion;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 创建用户评论请求
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionBankQuestionAddRequest {
    /**
    * content
    */
    private String content;
}