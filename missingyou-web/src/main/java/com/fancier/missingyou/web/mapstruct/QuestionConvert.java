package com.fancier.missingyou.web.mapstruct;

import com.fancier.missingyou.common.model.dto.question.QuestionAddRequest;
import com.fancier.missingyou.common.model.dto.question.QuestionEditRequest;
import com.fancier.missingyou.common.model.dto.question.QuestionUpdateRequest;
import com.fancier.missingyou.common.model.entity.Question;
import com.fancier.missingyou.common.model.vo.QuestionVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
* 用户 Pojo 转换器
*
* @author <a href="https://github.com/hola1009">fancier</a>
*/
@Mapper
public interface QuestionConvert {
    QuestionConvert INSTANCE = Mappers.getMapper(QuestionConvert.class);


    QuestionVO DO2QuestionVO(Question question);

    Question updateDTO2DO(QuestionUpdateRequest questionUpdateMyRequest);

    Question addDTO2DO(QuestionAddRequest questionAddMyRequest);

    Question editDTO2DO(QuestionEditRequest questionEditMyRequest);
}
