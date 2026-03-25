package com.coursecompass.backend.repository;

import com.coursecompass.backend.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByRoomAndIsVisibleTrueOrderByCreatedAtAsc(String room, Pageable pageable);
}
