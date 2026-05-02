package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ActivityMessageListener {

    @Autowired
    private ActivityAIService aiService;
    @Autowired
    private RecommendationRepository recommendationRepository;

    @RabbitListener(queues = "activity.queue")
    public void processActivity(Activity activity) {
        log.info("Received activity for processing: {}",activity.getId());

        // generating AI recommendations
        Recommendation recommendation=aiService.generateRecommendation(activity);

        recommendationRepository.save(recommendation);
    }

}
