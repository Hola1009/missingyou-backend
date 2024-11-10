package com.fancier.missingyou.common.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 */

public class BaseMapperGeneratorPlugin extends PluginAdapter {

  public boolean validate(List<String> warnings) {
    return true;
  }

  /**
   * 生成dao
   */
  @Override
  public boolean clientGenerated(Interface interfaze,
                                 TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("BaseMapper<"
        + introspectedTable.getBaseRecordType() + ">");
    FullyQualifiedJavaType imp = new FullyQualifiedJavaType(
        "com.baomidou.mybatisplus.core.mapper.BaseMapper");
    /*
      添加 extends MybatisBaseMapper
     */
    interfaze.addSuperInterface(fqjt);

    /*
     * 添加import com.baomidou.mybatisplus.core.mapper.BaseMapper;
     */
    interfaze.addImportedType(imp);
    return true;
  }

}