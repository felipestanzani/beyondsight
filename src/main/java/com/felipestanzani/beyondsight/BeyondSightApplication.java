package com.felipestanzani.beyondsight;

import com.felipestanzani.beyondsight.controller.ImpactMcpController;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BeyondSightApplication {

    static void main(String[] args) {
        SpringApplication.run(BeyondSightApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider toolCallbackProvider(ImpactMcpController mcpImpactService) {
        return MethodToolCallbackProvider.builder().toolObjects(mcpImpactService).build();
    }
}
