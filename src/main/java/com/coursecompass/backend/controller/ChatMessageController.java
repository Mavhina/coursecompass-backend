package com.coursecompass.backend.controller;

import com.coursecompass.backend.domain.User;
import com.coursecompass.backend.dto.ChatDtos;
import com.coursecompass.backend.service.ChatMessageService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chats/messages")
public class ChatMessageController {

    private final ChatMessageService service;

    public ChatMessageController(ChatMessageService service) {
        this.service = service;
    }

    // ✅ POST /api/chats/messages
    @PostMapping
    public ChatDtos.MessageResponse create(Authentication auth,
                                           @RequestBody ChatDtos.CreateMessageRequest req) {
        User user = (User) auth.getPrincipal(); // user comes from JWT
        return service.create(user, req);
    }

    // ✅ GET /api/chats/messages?room=community&page=0&size=50
    @GetMapping
    public ChatDtos.ListResponse list(@RequestParam String room,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "50") int size) {
        return service.list(room, page, size);
    }

    // ✅ DELETE /api/chats/messages/{id}  (soft delete)
    @DeleteMapping("/{id}")
    public ChatDtos.DeleteResponse delete(Authentication auth,
                                          @PathVariable Long id) {
        User user = (User) auth.getPrincipal();
        return service.softDelete(id, user);
    }
}
