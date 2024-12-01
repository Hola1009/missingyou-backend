package com.fancier.missingyou.common.model.dto.question;

import lombok.Data;

import java.util.List;
@Data
public class QuestionBatchDeleteRequest {
    /**
     * 题目 id 列表
     */
    private List<Long> questionIdList;
}
