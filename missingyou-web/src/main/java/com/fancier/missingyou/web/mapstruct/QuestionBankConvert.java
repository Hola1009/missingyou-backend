package com.fancier.missingyou.web.mapstruct;

import com.fancier.missingyou.common.model.dto.questionBank.QuestionBankAddRequest;
import com.fancier.missingyou.common.model.dto.questionBank.QuestionBankEditRequest;
import com.fancier.missingyou.common.model.dto.questionBank.QuestionBankUpdateRequest;
import com.fancier.missingyou.common.model.entity.QuestionBank;
import com.fancier.missingyou.common.model.vo.QuestionBankVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
* Pojo 转换器
*
* @author <a href="https://github.com/hola1009">fancier</a>
*/
@Mapper
public interface QuestionBankConvert {
    QuestionBankConvert INSTANCE = Mappers.getMapper(QuestionBankConvert.class);


    @Mapping(target = "user", ignore = true)
    @Mapping(target = "questionPage", ignore = true)
    QuestionBankVO DO2QuestionBankVO(QuestionBank questionBank);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDelete", ignore = true)
    @Mapping(target = "editTime", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    QuestionBank updateDTO2DO(QuestionBankUpdateRequest questionBankUpdateRequest);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDelete", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "editTime", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    QuestionBank addDTO2DO(QuestionBankAddRequest questionBankAddRequest);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDelete", ignore = true)
    @Mapping(target = "editTime", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    QuestionBank editDTO2DO(QuestionBankEditRequest questionBankEditRequest);
}
