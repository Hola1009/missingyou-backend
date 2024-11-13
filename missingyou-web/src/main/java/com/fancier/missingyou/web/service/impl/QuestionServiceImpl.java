package com.fancier.missingyou.web.service.impl;

import cn.hutool.core.collection.CollUtil;
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
import org.apache.commons.lang3.StringUtils;
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
     * 校验数据
     *
     * @param add 对创建的数据进行校验
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        ThrowUtils.throwIf(question == null, ErrorCode.PARAMS_ERROR);

        String title = question.getTitle();
        String content = question.getContent();
        // 创建数据时，参数不能为空
        ThrowUtils.throwIf(add && StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);

        // 修改数据时，有参数则校验
        ThrowUtils.throwIf(StringUtils.isNotBlank(title) && title.length() > 80,
                ErrorCode.PARAMS_ERROR, "标题过长");

        ThrowUtils.throwIf(StringUtils.isNotBlank(content) && content.length() > 10240,
                ErrorCode.PARAMS_ERROR, "内容过长");
    }




    /**
     * 获取查询条件
     * sql 语句样例
     * SELECT *
     * FROM question
     * WHERE
     *     (title LIKE '%searchText%' OR content LIKE '%searchText%') -- 从 `searchText` 变量得到的条件
     *     AND title LIKE '%title%' -- 如果 `title` 不为空
     *     AND content LIKE '%content%' -- 如果 `content` 不为空
     *     AND answer LIKE '%answer%'  -- 如果 `answer` 不为空
     *     AND id <> notId -- 如果 `notId` 不为空
     *     AND id = id -- 如果 `id` 不为空
     *     AND user_id = userId -- 如果 `userId` 不为空
     *     AND (tags LIKE '"tag1"' OR tags LIKE '"tag2"') -- 从 tagList 变量生成的条件
     * ORDER BY sortField ASC; -- 假设 `sortField` 指定了某个排序列
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {

        Preconditions.checkArgument(questionQueryRequest != null, "请求参数为空");

        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();

        Long id = questionQueryRequest.getId();
        Long notId = questionQueryRequest.getNotId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        String searchText = questionQueryRequest.getSearchText();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        List<String> tagList = questionQueryRequest.getTags();
        Long userId = questionQueryRequest.getUserId();
        String answer = questionQueryRequest.getAnswer();

        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title)
                .like(StringUtils.isNotBlank(content), "content", content)
                .like(StringUtils.isNotBlank(answer), "answer", answer);

        // JSON 数组查询
        if (CollUtil.isNotEmpty(tagList)) {
            tagList.forEach(tag -> queryWrapper.like("tags", "\"" + tag + "\""));
        }

        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId) && notId != 0, "id", notId)
                .eq(ObjectUtils.isNotEmpty(id) && id != 0, "id", id)
                .eq(ObjectUtils.isNotEmpty(userId) && id != 0, "user_id", userId);

        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }

}
