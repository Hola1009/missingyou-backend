package ${packageName}.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ${dataName}服务实现
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ${upperDataKey}ServiceImpl extends ServiceImpl<${upperDataKey}Mapper, ${upperDataKey}> implements ${upperDataKey}Service {



    @Override
    public void valid${upperDataKey}(${upperDataKey} ${dataKey}, boolean add) {
        ThrowUtils.throwIf(${dataKey} == null, ErrorCode.PARAMS_ERROR);

        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
    }

    /**
    * 用户分页查询
    *
    */
    @Override
    public Page<${upperDataKey}> pageQuery(${upperDataKey}QueryRequest ${dataKey}QueryRequest) {
        // 空参数检查
        Preconditions.checkArgument(${dataKey}QueryRequest != null, "请求参数为空");

        long current = ${dataKey}QueryRequest.getCurrent();
        long size = ${dataKey}QueryRequest.getPageSize();

        return this.page(new Page<>(current, size),
        this.getQueryWrapper(${dataKey}QueryRequest));
    }

    /**
    * 分页获取用户封装列表
    *
    */
    @Override
    public Page<${upperDataKey}VO> VOPageQuery(${upperDataKey}QueryRequest ${dataKey}QueryRequest) {
        // 空参数检查
        Preconditions.checkArgument(${dataKey}QueryRequest != null, "请求参数为空");

        long current = ${dataKey}QueryRequest.getCurrent();
        long size = ${dataKey}QueryRequest.getPageSize();
        // 限制爬虫


        Page<${upperDataKey}> ${dataKey}Page = this.pageQuery(${dataKey}QueryRequest);

            Page<${upperDataKey}VO> ${dataKey}VOPage = new Page<>(current, size, ${dataKey}Page.getTotal());

        // DO 列表转 VO 列表
        List<${upperDataKey}VO> ${dataKey}VOS = ${dataKey}Page.getRecords().stream()
            .map(${upperDataKey}Convert.INSTANCE::DO2${upperDataKey}VO)
            .collect(Collectors.toList());

        return ${dataKey}VOPage.setRecords(${dataKey}VOS);
    }

    /**
     * 获取查询条件
     *
     */
    @Override
    public QueryWrapper<${upperDataKey}> getQueryWrapper(${upperDataKey}QueryRequest ${dataKey}QueryRequest) {

        Preconditions.checkArgument(${dataKey}QueryRequest != null, "请求参数为空");

        QueryWrapper<${upperDataKey}> queryWrapper = new QueryWrapper<>();

        // todo 从对象中取值
        Long id = ${dataKey}QueryRequest.getId();
        String sortField = ${dataKey}QueryRequest.getSortField();
        String sortOrder = ${dataKey}QueryRequest.getSortOrder();

        // todo 补充需要的查询条件

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

}
