package com.sgruendel.wallconnector_ai;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/ai")
@AllArgsConstructor
public class ChatController {

    private final OpenAiChatModel chatModel;

    private final ChargingTools chargingTools;

    @PostMapping("/generate")
    public Map<String,String> generate(@RequestBody final String message) {

        final String response = ChatClient.create(chatModel)
                .prompt(message)
                .tools(chargingTools)
                .call()
                .content();

        return Map.of("generation", response);
    }

}