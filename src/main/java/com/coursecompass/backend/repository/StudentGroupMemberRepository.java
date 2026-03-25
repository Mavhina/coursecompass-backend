package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.StudentGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentGroupMemberRepository extends JpaRepository<StudentGroupMember, Long> {
    void deleteByGroupIdAndId(Long groupId, Long memberId);
}