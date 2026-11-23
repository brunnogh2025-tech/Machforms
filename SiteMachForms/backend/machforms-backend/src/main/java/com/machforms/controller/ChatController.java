package com.machforms.controller;

import com.machforms.dto.request.ChatRequest;
import com.machforms.dto.response.ChatResponse;
import com.machforms.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /** Tela de chat (exige login do organizador). */
    @PostMapping
    public ChatResponse enviar(@Valid @RequestBody ChatRequest request) {
        return new ChatResponse(chatService.responder(request.message()));
    }
}
