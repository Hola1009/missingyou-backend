package com.fancier.missingyou.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fancier.missingyou.common.model.entity.Question;

import java.util.Date;
import java.util.List;

public interface QuestionMapper extends BaseMapper<Question> {

    int insert(Question record);
    /**
     * 查询所有数据 (包括已经逻辑删除了的)
     */
    List<Question> listQuestionWithDelete(Date minUpdateTime);
}