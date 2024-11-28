package com.fancier.missingyou.web.task;

import cn.hutool.core.collection.CollUtil;
import com.fancier.missingyou.common.mapper.QuestionMapper;
import com.fancier.missingyou.common.model.entity.Question;
import com.fancier.missingyou.web.esdao.QuestionEsDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 定时同步 es
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class IncSyncQuestionToEs {
    private final QuestionMapper questionMapper;
    private final QuestionEsDao questionEsDao;

    /**
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        // 查询最近 5 分钟内的数据
        long FIVE_MINUTES = 5 * 60 * 1000L;
        Date fiveMinutesAgoDate = new Date(new Date().getTime() - FIVE_MINUTES);
        List<Question> questions = questionMapper.listQuestionWithDelete(fiveMinutesAgoDate);
        if (CollUtil.isEmpty(questions)) {
            log.info("no inc question");
            return;
        }

        int total = questions.size();
        log.info("全量同步 es 开始, total: {}", total);
        questionEsDao.batchSave(questions);
        log.info("全量同步结束, total: {}", total);
    }

}
