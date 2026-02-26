package com.example.agent.web;

import com.example.agent.service.AgentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AgentControllerIT {

  @Autowired
  WebTestClient web;

  @MockBean
  AgentService agentService;

  @Test
  void should_call_endpoint() {
    when(agentService.run(anyString()))
        .thenReturn("Issue #1 created: https://github.com/o/r/issues/1");

    web.post().uri("/api/run")
        .contentType(MediaType.TEXT_PLAIN)
        .bodyValue("Create a task to add OpenTelemetry")
        .exchange()
        .expectStatus().isOk();
  }
}
