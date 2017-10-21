package com.backend.api;

import com.backend.service.ChatMessage;
import com.backend.service.ChatDAO;
import com.backend.dto.AddMessageRequest;
import com.backend.dto.AddMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@RestController
public class HTTPWebService {

    private static final Logger log = LoggerFactory.getLogger(HTTPWebService.class);

    @Autowired
    private ChatDAO dao;

    @ResponseBody
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public AddMessageResponse add(@RequestBody AddMessageRequest message, HttpServletRequest request) {
        log("/add", request);

        boolean result = dao.add(message.getName(), message.getMessage());
        return new AddMessageResponse(result);
    }

    @ResponseBody
    @RequestMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ChatMessage> getAll(HttpServletRequest request) {
        log("/get", request);
        return dao.getAll();
    }

    private void log(String methodName, HttpServletRequest request) {
        log.info("{} Dynatrace [{}], Zipkin [{}]", methodName,
                getHeaders("x-dynatrace", request), getHeaders("x-b3", request));
    }

    private String getHeaders(String prefix, HttpServletRequest request) {
        return Collections.list(request.getHeaderNames()).stream()
                .filter((headerName) -> headerName.startsWith(prefix))
                .map((headerName) -> headerName + "=" + request.getHeader(headerName))
                .reduce((header1, header2) -> header1 + ", " + header2).orElse("");
    }
}