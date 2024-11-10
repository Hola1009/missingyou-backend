package com.fancier.missingyou.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("question_bank_question")
public class QuestionBankQuestion {
    private Long id;

    private Long questionBankId;

    private Long questionId;

    private Long userId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}