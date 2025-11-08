package com.felipestanzani.beyondsight;

import com.felipestanzani.beyondsight.controller.ElementReferenceController;
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
    public ToolCallbackProvider toolCallbackProvider(ElementReferenceController impactMcpController) {
        return MethodToolCallbackProvider.builder().toolObjects(impactMcpController).build();
    }
}
