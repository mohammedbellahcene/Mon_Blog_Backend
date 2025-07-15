package com.blog.api.repository;

import com.blog.api.entity.GlobalStatistics;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GlobalStatisticsRepository extends MongoRepository<GlobalStatistics, String> {
} 