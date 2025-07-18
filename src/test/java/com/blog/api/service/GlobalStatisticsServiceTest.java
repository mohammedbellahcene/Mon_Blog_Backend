package com.blog.api.service;

import com.blog.api.entity.GlobalStatistics;
import com.blog.api.repository.GlobalStatisticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalStatisticsServiceTest {
    @Mock private GlobalStatisticsRepository repository;
    @InjectMocks private GlobalStatisticsService service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getStats_returnsDefaultIfEmpty() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        GlobalStatistics stats = service.getStats();
        assertNotNull(stats);
    }

    @Test
    void decrementUsers_neverBelowZero() {
        GlobalStatistics stats = new GlobalStatistics();
        stats.setTotalUsers(0);
        when(repository.findAll()).thenReturn(Collections.singletonList(stats));
        service.decrementUsers();
        assertEquals(0, stats.getTotalUsers());
    }

    @Test
    void incrementUsers_incrementsValue() {
        GlobalStatistics stats = new GlobalStatistics();
        stats.setTotalUsers(1);
        when(repository.findAll()).thenReturn(Collections.singletonList(stats));
        service.incrementUsers();
        assertEquals(2, stats.getTotalUsers());
    }

    @Test
    void incrementPosts_incrementsValue() {
        GlobalStatistics stats = new GlobalStatistics();
        stats.setTotalPosts(1);
        when(repository.findAll()).thenReturn(Collections.singletonList(stats));
        service.incrementPosts();
        assertEquals(2, stats.getTotalPosts());
    }

    @Test
    void decrementPosts_neverBelowZero() {
        GlobalStatistics stats = new GlobalStatistics();
        stats.setTotalPosts(0);
        when(repository.findAll()).thenReturn(Collections.singletonList(stats));
        service.decrementPosts();
        assertEquals(0, stats.getTotalPosts());
    }

    @Test
    void incrementComments_incrementsValue() {
        GlobalStatistics stats = new GlobalStatistics();
        stats.setTotalComments(1);
        when(repository.findAll()).thenReturn(Collections.singletonList(stats));
        service.incrementComments();
        assertEquals(2, stats.getTotalComments());
    }

    @Test
    void decrementComments_neverBelowZero() {
        GlobalStatistics stats = new GlobalStatistics();
        stats.setTotalComments(0);
        when(repository.findAll()).thenReturn(Collections.singletonList(stats));
        service.decrementComments();
        assertEquals(0, stats.getTotalComments());
    }

    @Test
    void incrementLikes_incrementsValue() {
        GlobalStatistics stats = new GlobalStatistics();
        stats.setTotalLikes(1);
        when(repository.findAll()).thenReturn(Collections.singletonList(stats));
        service.incrementLikes();
        assertEquals(2, stats.getTotalLikes());
    }

    @Test
    void decrementLikes_neverBelowZero() {
        GlobalStatistics stats = new GlobalStatistics();
        stats.setTotalLikes(0);
        when(repository.findAll()).thenReturn(Collections.singletonList(stats));
        service.decrementLikes();
        assertEquals(0, stats.getTotalLikes());
    }

    @Test
    void incrementDislikes_incrementsValue() {
        GlobalStatistics stats = new GlobalStatistics();
        stats.setTotalDislikes(1);
        when(repository.findAll()).thenReturn(Collections.singletonList(stats));
        service.incrementDislikes();
        assertEquals(2, stats.getTotalDislikes());
    }

    @Test
    void decrementDislikes_neverBelowZero() {
        GlobalStatistics stats = new GlobalStatistics();
        stats.setTotalDislikes(0);
        when(repository.findAll()).thenReturn(Collections.singletonList(stats));
        service.decrementDislikes();
        assertEquals(0, stats.getTotalDislikes());
    }

    @Test
    void updateStats_savesStats() {
        GlobalStatistics stats = new GlobalStatistics();
        service.updateStats(stats);
        verify(repository, times(1)).save(stats);
    }

    @Test
    void syncAllStats_setsAllValues() {
        GlobalStatistics stats = new GlobalStatistics();
        when(repository.findAll()).thenReturn(Collections.singletonList(stats));
        service.syncAllStats(1, 2, 3, 4, 5);
        assertEquals(1, stats.getTotalUsers());
        assertEquals(2, stats.getTotalPosts());
        assertEquals(3, stats.getTotalComments());
        assertEquals(4, stats.getTotalLikes());
        assertEquals(5, stats.getTotalDislikes());
    }
} 