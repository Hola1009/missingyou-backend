package com.fancier.missingyou.web.mapstruct;

import cn.hutool.json.JSONUtil;
import com.fancier.missingyou.common.model.dto.question.QuestionAddRequest;
import com.fancier.missingyou.common.model.dto.question.QuestionEditRequest;
import com.fancier.missingyou.common.model.dto.question.QuestionUpdateRequest;
import com.fancier.missingyou.common.model.entity.Question;
import com.fancier.missingyou.common.model.vo.QuestionVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
* 用户 Pojo 转换器
*
* @author <a href="https://github.com/hola1009">fancier</a>
*/
@Mapper
public interface QuestionConvert {
    QuestionConvert INSTANCE = Mappers.getMapper(QuestionConvert.class);


    @Mapping(target = "tagList", expression = "java(map2TagList(question.getTags()))")
    QuestionVO DO2QuestionVO(Question question);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDelete", ignore = true)
    @Mapping(target = "editTime", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "tags", expression = "java(map2TagsStr(questionUpdateRequest.getTags()))")
    Question updateDTO2DO(QuestionUpdateRequest questionUpdateRequest);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDelete", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "editTime", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "tags", expression = "java(map2TagsStr(questionAddRequest.getTags()))")
    Question addDTO2DO(QuestionAddRequest questionAddRequest);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "isDelete", ignore = true)
    @Mapping(target = "editTime", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "tags", expression = "java(map2TagsStr(questionEditRequest.getTags()))")
    Question editDTO2DO(QuestionEditRequest questionEditRequest);

    /**
     * 将 tags 字符串转换为 List<String>类型
     */
    default List<String> map2TagList(String tags) {
        return JSONUtil.toList(JSONUtil.parseArray(tags), String.class);
    }

    /**
     * 将 List<String> 转换为 tags 字符串
     */
    default String map2TagsStr(List<String> tagList) {
        return JSONUtil.toJsonStr(tagList);
    }
}
