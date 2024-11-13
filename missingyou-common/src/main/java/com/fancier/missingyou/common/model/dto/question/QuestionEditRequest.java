package com.fancier.missingyou.common.model.dto.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 编辑用户评论请求
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionEditRequest {
    private Long id;

    private String title;

    private List<String> tags;

    private String content;

    private String answer;
}