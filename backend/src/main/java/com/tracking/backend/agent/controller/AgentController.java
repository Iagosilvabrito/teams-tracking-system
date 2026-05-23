package com.tracking.backend.agent.controller;

import com.tracking.backend.agent.dto.AgentRequest;
import com.tracking.backend.agent.dto.AgentResponse;
import com.tracking.backend.agent.service.AgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @GetMapping
    public List<AgentResponse> findAll() {
        return agentService.findAll();
    }

    @GetMapping("/{id}")
    public AgentResponse findById(@PathVariable Long id) {
        return agentService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgentResponse create(@RequestBody @Valid AgentRequest request) {
        return agentService.create(request);
    }

    @PutMapping("/{id}")
    public AgentResponse update(@PathVariable Long id, @RequestBody @Valid AgentRequest request) {
        return agentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        agentService.delete(id);
    }
}