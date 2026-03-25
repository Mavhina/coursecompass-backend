package com.coursecompass.backend.service;

import com.coursecompass.backend.domain.GroupMessageRead;
import com.coursecompass.backend.domain.StudentGroupMember;
import com.coursecompass.backend.repository.GroupMessageReadRepository;
import com.coursecompass.backend.repository.StudentGroupMemberRepository;
import com.coursecompass.backend.repository.StudentGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class GroupMessageReadService {

    private final GroupMessageReadRepository readRepository;
    private final StudentGroupRepository     groupRepository;
    private final StudentGroupMemberRepository memberRepository;

    /**
     * Called when a group message is saved.
     * Creates an unread record for every platform-user member of the group EXCEPT the sender.
     */
    @Transactional
    public void createUnreadRecordsForGroupMessage(Long messageId, Long groupId, Long senderId) {
        groupRepository.findById(groupId).ifPresent(group -> {
            for (StudentGroupMember member : group.getMembers()) {
                Long studentId = member.getStudentId();
                // Only platform users (studentId != null), not the sender, no duplicates
                if (studentId == null) continue;
                if (studentId.equals(senderId)) continue;
                if (readRepository.existsByMessageIdAndStudentId(messageId, studentId)) continue;

                readRepository.save(GroupMessageRead.builder()
                        .messageId(messageId)
                        .studentId(studentId)
                        .read(false)
                        .build());
            }
        });
    }

    /**
     * Called when a student opens a group chat.
     * Marks all their unread messages in that group as read.
     */
    @Transactional
    public void markGroupAsRead(Long studentId, Long groupId) {
        readRepository.markGroupMessagesAsRead(studentId, groupId, LocalDateTime.now());
    }

    /**
     * Total unread group messages for a student across all groups.
     */
    public long countAllUnreadForStudent(Long studentId) {
        return readRepository.countAllUnreadForStudent(studentId);
    }

    /**
     * Unread count per group for a student.
     * Returns map of groupId -> unreadCount (only groups with unread > 0).
     */
    public Map<String, Long> getUnreadCountsPerGroup(Long studentId) {
        List<Long> groupIds = readRepository.findGroupIdsWithUnreadForStudent(studentId);
        Map<String, Long> result = new HashMap<>();
        for (Long groupId : groupIds) {
            long count = readRepository.countUnreadForStudentInGroup(studentId, groupId);
            if (count > 0) result.put(String.valueOf(groupId), count);
        }
        return result;
    }
}