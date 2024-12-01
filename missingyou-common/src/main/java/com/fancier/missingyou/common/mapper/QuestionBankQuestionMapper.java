package com.fancier.missingyou.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fancier.missingyou.common.model.entity.QuestionBankQuestion;

public interface QuestionBankQuestionMapper extends BaseMapper<QuestionBankQuestion> {

    int insert(QuestionBankQuestion record);

}