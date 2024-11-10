package com.fancier.missingyou.common.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.ArrayList;
import java.util.List;

/**
 * 生成 lombok 注解插件
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 */

public class LombokAnnotationPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addAnnotation("@AllArgsConstructor");
        topLevelClass.addAnnotation("@NoArgsConstructor");
        topLevelClass.addAnnotation("@Builder");
        topLevelClass.addAnnotation("@TableName(\""+ introspectedTable.getFullyQualifiedTable() +"\")");
        topLevelClass.addImportedType(new FullyQualifiedJavaType("lombok.Data"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("lombok.AllArgsConstructor"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("lombok.NoArgsConstructor"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("lombok.Builder"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("com.baomidou.mybatisplus.annotation.TableName"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("java.time.LocalDateTime"));



        List<Method> methods =  topLevelClass.getMethods();
        List<Method> remove = new ArrayList<>();
        for (Method method : methods) {
            if (method.getBodyLines().size() < 2) {
                remove.add(method);
                System.out.println("-----------------" + topLevelClass.getType().getShortName() + "'s method=" + method.getName() + " removed cause lombok annotation.");
            }
        }
        methods.removeAll(remove);
        return true;
    }

}