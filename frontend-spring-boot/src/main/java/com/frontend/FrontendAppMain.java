package com.frontend;

import com.frontend.ws.MessageAddRequest;
import com.frontend.ws.MessageAddResponse;
import com.frontend.ws.TextMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@SpringBootApplication
public class FrontendAppMain {

    public static final String ADD_MESSAGE_URL = "http://localhost:8988/addMessage";
    public static final String GET_MESSAGES_URL = "http://localhost:8988/getMessages";

    public static void main(String[] args) {
		SpringApplication.run(FrontendAppMain.class, args);
	}

	@RequestMapping("/chat")
	public TextMessage[] chat(@RequestParam(value="name", defaultValue="noname") String name,
                              @RequestParam(value="message", defaultValue="nomessage") String message) {
		RestTemplate template = new RestTemplate();

		MessageAddResponse response = template.postForObject(ADD_MESSAGE_URL,
				new MessageAddRequest(name, message),
				MessageAddResponse.class);

        if (response.isSuccess()) {
            ResponseEntity<TextMessage[]> messages = template.getForEntity(GET_MESSAGES_URL, TextMessage[].class);
            return messages.getBody();
        } else {
            throw new RuntimeException("Backend error");
        }
	}
}