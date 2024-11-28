package com.fancier.missingyou.web.task;

import cn.hutool.core.collection.CollUtil;
import com.fancier.missingyou.common.model.entity.Question;
import com.fancier.missingyou.web.esdao.QuestionEsDao;
import com.fancier.missingyou.web.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author <a href="https://github.com/hola1009">fancier</a>
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class FullSyncQuestionToEs implements CommandLineRunner {

    private final QuestionService questionService;

    private final QuestionEsDao questionEsDao;

    @Override
    public void run(String... args) {
        List<Question> list = questionService.list();
        if (CollUtil.isEmpty(list)) {
            return;
        }

        int total = list.size();
        log.info("全量同步 es 开始, total: {}", total);
        questionEsDao.batchSave(list);
        log.info("全量同步结束, total: {}", total);
    }
}
