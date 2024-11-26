package ${packageName}.mapstruct.${dataKey};

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
* 用户 Pojo 转换器
*
* @author <a href="https://github.com/hola1009">fancier</a>
*/
@Mapper
public interface ${upperDataKey}Convert {
    ${upperDataKey}Convert INSTANCE = Mappers.getMapper(${upperDataKey}Convert.class);


    ${upperDataKey}VO DO2${upperDataKey}VO(${upperDataKey} ${dataKey});

    ${upperDataKey} updateDTO2DO(${upperDataKey}UpdateRequest ${dataKey}UpdateRequest);

    ${upperDataKey} addDTO2DO(${upperDataKey}AddRequest ${dataKey}AddRequest);

    ${upperDataKey} editDTO2DO(${upperDataKey}EditRequest ${dataKey}EditRequest);
}
