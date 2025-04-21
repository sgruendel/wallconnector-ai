package com.sgruendel.wallconnector_ai;

import org.slf4j.Logger;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.AllArgsConstructor;


@Controller
@AllArgsConstructor
public class WebController {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(WebController.class);

    private final OpenAiChatModel chatModel;

    private final ChargingTools chargingTools;

    @GetMapping("/")
    public String index() {

        return "index";
    }

    @PostMapping("/chat")
    public String generate(@RequestBody final String message, final Model model) {

        final String response = ChatClient.create(chatModel)
                .prompt(message)
                .tools(chargingTools)
                .call()
                .content();
        LOGGER.info("Response: {}", response);

        model.addAttribute("response", response);

        return "index :: response";
    }

}
