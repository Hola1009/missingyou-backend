package com.fancier.missingyou.web.esdao;

import com.fancier.missingyou.common.model.dto.question.QuestionEsDTO;
import com.fancier.missingyou.common.model.entity.Question;
import com.fancier.missingyou.web.mapstruct.QuestionConvert;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 题目 ES 操作
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 */
public interface QuestionEsDao extends ElasticsearchRepository<QuestionEsDTO, Long> {
    void findByUserId(Long userId);

    default void batchSave(List<Question> list) {
        List<QuestionEsDTO> DTOList = list.stream()
                .map(QuestionConvert.INSTANCE::DO2EsDTO)
                .collect(Collectors.toList());

        // 分页批量插入到 es
        final int pageSize = 500;
        int total = DTOList.size();

        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            this.saveAll(DTOList.subList(i, end));
        }
    }
}
