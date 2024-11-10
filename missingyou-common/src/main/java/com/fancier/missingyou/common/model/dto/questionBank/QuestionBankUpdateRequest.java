package com.fancier.missingyou.common.model.dto.questionBank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class QuestionBankUpdateRequest {

    /**
     * id
     */
    private Long id;


}