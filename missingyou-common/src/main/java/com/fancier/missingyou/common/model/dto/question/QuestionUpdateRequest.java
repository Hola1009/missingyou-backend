package com.fancier.missingyou.common.model.dto.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 更新用户评论请求
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionUpdateRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 推荐答案
     */
    private String answer;


}