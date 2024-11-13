package com.fancier.missingyou.common.model.dto.question;

import com.fancier.missingyou.common.model.common.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 查询用户评论请求
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionQueryRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long notId;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 推荐答案
     */
    private String answer;

    /**
     * 题库 id
     */
    private Long questionBankId;

    /**
     * 创建用户 id
     */
    private Long userId;
}