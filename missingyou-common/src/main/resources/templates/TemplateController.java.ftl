package ${packageName}.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.google.common.base.Preconditions;
import cn.dev33.satoken.annotation.SaCheckRole;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * ${dataName}接口
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 *
 */
@RestController
@RequestMapping("/${dataKey}")
@RequiredArgsConstructor
@Slf4j
public class ${upperDataKey}Controller {

    private final ${upperDataKey}Service ${dataKey}Service;

    private final UserService userService;

    // region 增删改查

    /**
     * 创建${dataName}
     *
     */
    @PostMapping("/add")
    public BaseResponse<Long> add${upperDataKey}(@RequestBody ${upperDataKey}AddRequest ${dataKey}AddRequest, HttpServletRequest ignoredRequest) {
        Preconditions.checkArgument(${dataKey}AddRequest != null);
        // todo 在此处将实体类和 DTO 进行转换
        ${upperDataKey} ${dataKey} = ${upperDataKey}Convert.INSTANCE.addDTO2DO(${dataKey}AddRequest) ;

        // todo 数据校验

        // todo 填充默认值
        // 写入数据库
        boolean result = ${dataKey}Service.save(${dataKey});
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long new${upperDataKey}Id = ${dataKey}.getId();
        return ResultUtils.success(new${upperDataKey}Id);
    }

    /**
     * 删除${dataName}
     *
     */
    @PostMapping("/delete")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> delete${upperDataKey}(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        Preconditions.checkArgument(deleteRequest != null && deleteRequest.getId() > 0);
        long id = deleteRequest.getId();
        // 判断是否存在
        check(id, request);

        // 操作数据库
        boolean result = ${dataKey}Service.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新${dataName}（仅管理员可用）
     *
     */
    @PostMapping("/update")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> update${upperDataKey}(@RequestBody ${upperDataKey}UpdateRequest ${dataKey}UpdateRequest) {
        Preconditions.checkArgument(${dataKey}UpdateRequest != null && ${dataKey}UpdateRequest.getId() > 0);

        ${upperDataKey} ${dataKey} = ${upperDataKey}Convert.INSTANCE.updateDTO2DO(${dataKey}UpdateRequest);
        // todo 数据校验

        // 判断是否存在
        long id = ${dataKey}UpdateRequest.getId();
        ${upperDataKey} old${upperDataKey} = ${dataKey}Service.getById(id);
        ThrowUtils.throwIf(old${upperDataKey} == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = ${dataKey}Service.updateById(${dataKey});
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取${dataName}（封装类）
     *
     */
    @GetMapping("/get/vo")
    public BaseResponse<${upperDataKey}VO> get${upperDataKey}VOById(long id, HttpServletRequest ignoredRequest) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        ${upperDataKey} ${dataKey} = ${dataKey}Service.getById(id);
        ThrowUtils.throwIf(${dataKey} == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(${upperDataKey}Convert.INSTANCE.DO2${upperDataKey}VO(${dataKey}));
    }

    /**
     * 分页获取${dataName}列表（仅管理员可用）
     *
     */
    @PostMapping("/list/page")
    @SaCheckRole(UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<${upperDataKey}>> list${upperDataKey}ByPage(@RequestBody ${upperDataKey}QueryRequest ${dataKey}QueryRequest) {
        Page<${upperDataKey}> ${dataKey}Page = ${dataKey}Service.pageQuery(${dataKey}QueryRequest);
        return ResultUtils.success(${dataKey}Page);
    }

    /**
     * 分页获取${dataName}列表（封装类）
     *
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<${upperDataKey}VO>> list${upperDataKey}VOByPage(@RequestBody ${upperDataKey}QueryRequest ${dataKey}QueryRequest,
                                                               HttpServletRequest ignoredRequest) {
        Page<${upperDataKey}VO> ${dataKey}VOPage = ${dataKey}Service.VOPageQuery(${dataKey}QueryRequest);
            return ResultUtils.success(${dataKey}VOPage);
    }

    /**
     * 编辑${dataName}（给用户使用）
     *
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> edit${upperDataKey}(@RequestBody ${upperDataKey}EditRequest ${dataKey}EditRequest, HttpServletRequest request) {

        Preconditions.checkArgument(${dataKey}EditRequest != null && ${dataKey}EditRequest.getId() > 0);

        // todo 在此处将实体类和 DTO 进行转换
        ${upperDataKey} ${dataKey} = ${upperDataKey}Convert.INSTANCE.editDTO2DO(${dataKey}EditRequest);
        // todo 数据校验

        // 判断是否存在
        long id = ${dataKey}EditRequest.getId();
        check(id, request);

        // 操作数据库
        boolean result = ${dataKey}Service.updateById(${dataKey});
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    private void check(Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        ${upperDataKey} old${upperDataKey} = ${dataKey}Service.getById(id);
        ThrowUtils.throwIf(old${upperDataKey} == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        ThrowUtils.throwIf(!old${upperDataKey}.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR);
    }

}
