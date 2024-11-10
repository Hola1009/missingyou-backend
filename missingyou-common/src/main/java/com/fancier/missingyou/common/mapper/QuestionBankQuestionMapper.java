package com.fancier.missingyou.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fancier.missingyou.common.model.entity.QuestionBankQuestion;
import java.util.List;

public interface QuestionBankQuestionMapper extends BaseMapper<QuestionBankQuestion> {
    int deleteByPrimaryKey(Long id);

    int insert(QuestionBankQuestion record);

    QuestionBankQuestion selectByPrimaryKey(Long id);

    List<QuestionBankQuestion> selectAll();

    int updateByPrimaryKey(QuestionBankQuestion record);
}