error id: file:///C:/Users/abduk/Documents/etude/efrei/S9/integration_cloud/cloud2/lab_mcp_ai_agent_springboot/agent/src/test/java/com/example/agent/web/AgentControllerIT.java:_empty_/WebTestClient#post#
file:///C:/Users/abduk/Documents/etude/efrei/S9/integration_cloud/cloud2/lab_mcp_ai_agent_springboot/agent/src/test/java/com/example/agent/web/AgentControllerIT.java
empty definition using pc, found symbol in pc: _empty_/WebTestClient#post#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 959
uri: file:///C:/Users/abduk/Documents/etude/efrei/S9/integration_cloud/cloud2/lab_mcp_ai_agent_springboot/agent/src/test/java/com/example/agent/web/AgentControllerIT.java
text:
```scala
package com.example.agent.web;

import com.example.mcp.McpHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AgentControllerIT {

  @Autowired
  WebTestClient web;

  @MockBean
  McpHttpClient mcp;

  @Test
  void should_call_endpoint() {
    when(mcp.callTool(eq("create_issue"), anyMap()))
        .thenReturn(Mono.just(Map.of("number", 1, "html_url", "https://github.com/o/r/issues/1")));

    web.@@post().uri("/api/run")
        .bodyValue(Map.of("prompt", "Create a task to add OpenTelemetry"))
        .exchange()
        .expectStatus().isOk();
  }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/WebTestClient#post#