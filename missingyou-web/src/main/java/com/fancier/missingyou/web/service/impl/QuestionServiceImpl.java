package com.fancier.missingyou.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fancier.missingyou.common.constant.CommonConstant;
import com.fancier.missingyou.common.expection.ErrorCode;
import com.fancier.missingyou.common.expection.ThrowUtils;
import com.fancier.missingyou.common.mapper.QuestionMapper;
import com.fancier.missingyou.common.model.dto.question.QuestionQueryRequest;
import com.fancier.missingyou.common.model.entity.Question;
import com.fancier.missingyou.common.model.vo.QuestionVO;
import com.fancier.missingyou.common.util.SqlUtils;
import com.fancier.missingyou.web.mapstruct.QuestionConvert;
import com.fancier.missingyou.web.service.QuestionService;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户评论服务实现
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    /**
    * 用户分页查询
    *
    */
    @Override
    public Page<Question> pageQuery(QuestionQueryRequest questionQueryRequest) {
        // 空参数检查
        Preconditions.checkArgument(questionQueryRequest != null, "请求参数为空");

        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();

        return this.page(new Page<>(current, size),
        this.getQueryWrapper(questionQueryRequest));
    }

    /**
    * 分页获取用户封装列表
    *
    */
    @Override
    public Page<QuestionVO> VOPageQuery(QuestionQueryRequest questionQueryRequest) {
        // 空参数检查
        Preconditions.checkArgument(questionQueryRequest != null, "请求参数为空");

        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        Page<Question> questionPage = this.pageQuery(questionQueryRequest);

            Page<QuestionVO> questionVOPage = new Page<>(current, size, questionPage.getTotal());

        // DO 列表转 VO 列表
        List<QuestionVO> questionVOS = questionPage.getRecords().stream()
            .map(QuestionConvert.INSTANCE::DO2QuestionVO)
            .collect(Collectors.toList());

        return questionVOPage.setRecords(questionVOS);
    }

    /**
     * 获取查询条件
     *
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {

        Preconditions.checkArgument(questionQueryRequest != null, "请求参数为空");

        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();

        // todo 从对象中取值
        Long id = questionQueryRequest.getId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();

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
