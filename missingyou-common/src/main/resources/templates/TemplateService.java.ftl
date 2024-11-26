package ${packageName}.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * ${dataName}服务
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 *
 */
public interface ${upperDataKey}Service extends IService<${upperDataKey}> {

    /**
    * 校验数据
    *
    * @param add 对创建的数据进行校验
    */
    void valid${upperDataKey}(QuestionBank questionBank, boolean add);


    /**
     * 获取查询条件
     *
     */
    QueryWrapper<${upperDataKey}> getQueryWrapper(${upperDataKey}QueryRequest ${dataKey}QueryRequest);

    /**
    * 分页查询
    */
    Page<${upperDataKey}> pageQuery(${upperDataKey}QueryRequest ${dataKey}QueryRequest);

    /**
    * 分页获取封装列表
    */
    Page<${upperDataKey}VO> VOPageQuery(${upperDataKey}QueryRequest ${dataKey}QueryRequest);

}
