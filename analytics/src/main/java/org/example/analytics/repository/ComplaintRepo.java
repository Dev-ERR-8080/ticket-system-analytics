package org.example.analytics.repository;


import org.example.analytics.model.Category;
import org.example.analytics.model.Complaint;
import org.example.analytics.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ComplaintRepo extends JpaRepository<Complaint, Long> {

    long countByStatus(Status status);

    long countByCategory(Category category);

    long countByRaisedBy(Long userId);

    @Query("SELECT c.status, COUNT(c) FROM Complaint c GROUP BY c.status")
    List<Object[]> countGroupByStatus();

    @Query("SELECT DATE(c.createdAt), COUNT(c) FROM Complaint c GROUP BY DATE(c.createdAt)")
    List<Object[]> countDailyTrend();

    @Query(value = """
    SELECT AVG(EXTRACT(EPOCH FROM (resolved_at - created_at)) / 3600)
    FROM complaints
    WHERE status = 'RESOLVED'
""", nativeQuery = true)
    Double averageResolutionTime();

    @Query("SELECT c.raisedBy, COUNT(c) as total FROM Complaint c GROUP BY c.raisedBy ORDER BY total DESC")
    List<Object[]> findTopUsers();

    @Query("SELECT c.category, COUNT(c) FROM Complaint c GROUP BY c.category")
    List<Object[]> countByCategoryGroup();

    @Query("SELECT c.block, COUNT(c) FROM Complaint c GROUP BY c.block")
    List<Object[]> countByBlock();

    @Query("SELECT c FROM Complaint c WHERE c.status != 'RESOLVED' AND c.createdAt < :threshold")
    List<Complaint> findSlaViolations(LocalDateTime threshold);

    @Query(value = """
        SELECT DATE(created_at) as day, COUNT(*)
        FROM complaints
        GROUP BY day
        ORDER BY day
    """, nativeQuery = true)
    List<Object[]> dailyTrend();

    @Query(value = """
        SELECT DATE_TRUNC('week', created_at) as week, COUNT(*)
        FROM complaints
        GROUP BY week
        ORDER BY week
    """, nativeQuery = true)
    List<Object[]> weeklyTrend();

    @Query(value = """
    SELECT DATE_TRUNC('month', created_at) as month, COUNT(*)
    FROM complaints
    GROUP BY month
    ORDER BY month
""", nativeQuery = true)
    List<Object[]> monthlyTrend();

    @Query(value = """
    SELECT EXTRACT(HOUR FROM created_at) as hour, COUNT(*)
    FROM complaints
    GROUP BY hour
    ORDER BY hour
""", nativeQuery = true)
    List<Object[]> peakHours();
}
