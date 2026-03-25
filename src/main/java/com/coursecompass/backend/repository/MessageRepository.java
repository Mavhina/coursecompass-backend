package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // 1:1 conversation between tutor and student
    @Query("""
        SELECT m FROM Message m
        WHERE (m.senderId = :tutorId AND m.receiverId = :studentId)
           OR (m.senderId = :studentId AND m.receiverId = :tutorId)
        ORDER BY m.sentAt ASC
    """)
    List<Message> findConversation(@Param("tutorId") Long tutorId,
                                   @Param("studentId") Long studentId);

    // Group conversation
    @Query("""
        SELECT m FROM Message m
        WHERE m.receiverId = :groupId
          AND m.senderRole = 'tutor-group'
        ORDER BY m.sentAt ASC
    """)
    List<Message> findGroupConversation(@Param("groupId") Long groupId);

    // Broadcast messages from a specific tutor
    @Query("""
        SELECT m FROM Message m
        WHERE m.senderId = :tutorId
          AND m.senderRole = 'broadcast'
        ORDER BY m.sentAt ASC
    """)
    List<Message> findBroadcastMessages(@Param("tutorId") Long tutorId);

    // Get distinct tutor IDs who have messaged this student
    @Query("""
        SELECT DISTINCT m.senderId FROM Message m
        WHERE m.receiverId = :studentId
          AND m.senderRole = 'tutor'
    """)
    List<Long> findTutorIdsThatMessagedStudent(@Param("studentId") Long studentId);

    // Get latest message between tutor and student
    @Query("""
        SELECT m FROM Message m
        WHERE (m.senderId = :tutorId AND m.receiverId = :studentId)
           OR (m.senderId = :studentId AND m.receiverId = :tutorId)
        ORDER BY m.sentAt DESC
        LIMIT 1
    """)
    Optional<Message> findLatestMessage(@Param("tutorId") Long tutorId,
                                        @Param("studentId") Long studentId);

    // Get all broadcast messages from a list of tutors
    @Query("""
        SELECT m FROM Message m
        WHERE m.senderId IN :tutorIds
          AND m.senderRole = 'broadcast'
        ORDER BY m.sentAt ASC
    """)
    List<Message> findBroadcastMessagesFromTutors(@Param("tutorIds") List<Long> tutorIds);

    // ── Unread counts ─────────────────────────────────────────────────────

    // Count unread messages sent by a specific sender to a specific receiver
    @Query("""
        SELECT COUNT(m) FROM Message m
        WHERE m.senderId = :senderId
          AND m.receiverId = :receiverId
          AND m.read = false
          AND m.senderRole = :senderRole
    """)
    long countUnread(@Param("senderId") Long senderId,
                     @Param("receiverId") Long receiverId,
                     @Param("senderRole") String senderRole);

    // Count unread messages for a student from a specific tutor (tutor → student)
    @Query("""
        SELECT COUNT(m) FROM Message m
        WHERE m.senderId = :tutorId
          AND m.receiverId = :studentId
          AND m.read = false
          AND m.senderRole = 'tutor'
    """)
    long countUnreadFromTutor(@Param("tutorId") Long tutorId,
                              @Param("studentId") Long studentId);

    // Count unread messages for a tutor from a specific student (student → tutor)
    @Query("""
        SELECT COUNT(m) FROM Message m
        WHERE m.senderId = :studentId
          AND m.receiverId = :tutorId
          AND m.read = false
          AND m.senderRole = 'student'
    """)
    long countUnreadFromStudent(@Param("studentId") Long studentId,
                                @Param("tutorId") Long tutorId);

    // ── Mark as read ──────────────────────────────────────────────────────

    // Mark all messages from tutor to student as read (student opens chat)
    @Modifying
    @Query("""
        UPDATE Message m SET m.read = true
        WHERE m.senderId = :tutorId
          AND m.receiverId = :studentId
          AND m.read = false
          AND m.senderRole = 'tutor'
    """)
    void markTutorMessagesAsRead(@Param("tutorId") Long tutorId,
                                 @Param("studentId") Long studentId);

    // Mark all messages from student to tutor as read (tutor opens chat)
    @Modifying
    @Query("""
        UPDATE Message m SET m.read = true
        WHERE m.senderId = :studentId
          AND m.receiverId = :tutorId
          AND m.read = false
          AND m.senderRole = 'student'
    """)
    void markStudentMessagesAsRead(@Param("studentId") Long studentId,
                                   @Param("tutorId") Long tutorId);

    // Get distinct student IDs who have sent messages to this tutor
    @Query("""
        SELECT DISTINCT m.senderId FROM Message m
        WHERE m.receiverId = :tutorId
          AND m.senderRole = 'student'
    """)
    List<Long> findStudentIdsThatMessagedTutor(@Param("tutorId") Long tutorId);
}