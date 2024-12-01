package com.fancier.missingyou.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fancier.missingyou.common.model.entity.QuestionBank;

public interface QuestionBankMapper extends BaseMapper<QuestionBank> {
    int insert(QuestionBank record);

}