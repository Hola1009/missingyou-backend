package com.fancier.missingyou.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fancier.missingyou.common.model.entity.QuestionBank;
import java.util.List;

public interface QuestionBankMapper extends BaseMapper<QuestionBank> {
    int deleteByPrimaryKey(Long id);

    int insert(QuestionBank record);

    QuestionBank selectByPrimaryKey(Long id);

    List<QuestionBank> selectAll();

    int updateByPrimaryKey(QuestionBank record);
}