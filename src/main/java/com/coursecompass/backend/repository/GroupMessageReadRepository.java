package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.GroupMessageRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GroupMessageReadRepository extends JpaRepository<GroupMessageRead, Long> {

    // Count unread group messages for a student in a specific group
    @Query("""
        SELECT COUNT(r) FROM GroupMessageRead r
        WHERE r.studentId = :studentId
          AND r.messageId IN (
              SELECT m.id FROM Message m
              WHERE m.receiverId = :groupId
                AND m.senderRole = 'tutor-group'
          )
          AND r.read = false
    """)
    long countUnreadForStudentInGroup(@Param("studentId") Long studentId,
                                      @Param("groupId") Long groupId);

    // Count total unread group messages across ALL groups for a student
    @Query("""
        SELECT COUNT(r) FROM GroupMessageRead r
        WHERE r.studentId = :studentId
          AND r.read = false
    """)
    long countAllUnreadForStudent(@Param("studentId") Long studentId);

    // Mark all unread as read for a student in a group
    @Modifying
    @Query("""
        UPDATE GroupMessageRead r SET r.read = true, r.readAt = :now
        WHERE r.studentId = :studentId
          AND r.read = false
          AND r.messageId IN (
              SELECT m.id FROM Message m
              WHERE m.receiverId = :groupId
                AND m.senderRole = 'tutor-group'
          )
    """)
    void markGroupMessagesAsRead(@Param("studentId") Long studentId,
                                 @Param("groupId") Long groupId,
                                 @Param("now") LocalDateTime now);

    // Check if a read record already exists (to avoid duplicates)
    boolean existsByMessageIdAndStudentId(Long messageId, Long studentId);

    // Get all unread group IDs for a student (for counting per group)
    @Query("""
        SELECT DISTINCT m.receiverId FROM GroupMessageRead r
        JOIN Message m ON m.id = r.messageId
        WHERE r.studentId = :studentId
          AND r.read = false
          AND m.senderRole = 'tutor-group'
    """)
    List<Long> findGroupIdsWithUnreadForStudent(@Param("studentId") Long studentId);
}