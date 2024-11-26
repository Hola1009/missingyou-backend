package com.fancier.missingyou.web.mapstruct;

import com.fancier.missingyou.common.model.dto.questionBankQuestion.QuestionBankQuestionAddRequest;
import com.fancier.missingyou.common.model.dto.questionBankQuestion.QuestionBankQuestionUpdateRequest;
import com.fancier.missingyou.common.model.entity.QuestionBankQuestion;
import com.fancier.missingyou.common.model.vo.QuestionBankQuestionVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
* 用户 Pojo 转换器
*
* @author <a href="https://github.com/hola1009">fancier</a>
*/
@Mapper
public interface QuestionBankQuestionConvert {
    QuestionBankQuestionConvert INSTANCE = Mappers.getMapper(QuestionBankQuestionConvert.class);


    @Mapping(target = "user", ignore = true)
    QuestionBankQuestionVO DO2QuestionBankQuestionVO(QuestionBankQuestion questionBankQuestion);


    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    QuestionBankQuestion updateDTO2DO(QuestionBankQuestionUpdateRequest questionBankQuestionUpdateRequest);


    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    QuestionBankQuestion addDTO2DO(QuestionBankQuestionAddRequest questionBankQuestionAddRequest);



}
