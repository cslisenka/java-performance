package com.app2;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class App2Controller {

    private static final Logger log = LoggerFactory.getLogger(App2Controller.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @RequestMapping(value = "/message", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void add(@RequestBody MessageDTO dto, HttpServletRequest request) {
        if (request != null) {
            log.info("POST /message Dynatrace [{}], Zipkin [{}]", getHeaders("x-dynatrace", request), getHeaders("x-b3", request));
        }

        // Simulating errors
        if (dto.getMessage().contains("error")) {
            throw new RuntimeException("app2 error");
        }

        jdbcTemplate.update("INSERT INTO chat (message, timestamp) VALUES (?, NOW())", dto.getMessage());
    }

    @ResponseBody
    @RequestMapping(value = "/message", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public List<MessageDTO> getAll(HttpServletRequest request) {
        log.info("GET /message Dynatrace [{}], Zipkin [{}]", getHeaders("x-dynatrace", request), getHeaders("x-b3", request));

        List<MessageEntity> result = sessionFactory.getCurrentSession()
                .createQuery("from MessageEntity msg order by msg.timestamp DESC")
                .setMaxResults(5).list();

        return result.stream().map(msg -> new MessageDTO(msg.getMessage())).collect(Collectors.toList());
    }

    @PostConstruct
    public void init() {
        jdbcTemplate.execute("DELETE FROM chat");
    }

    private String getHeaders(String prefix, HttpServletRequest request) {
        return Collections.list(request.getHeaderNames()).stream()
                .filter((headerName) -> headerName.startsWith(prefix))
                .map((headerName) -> headerName + "=" + request.getHeader(headerName))
                .reduce((header1, header2) -> header1 + ", " + header2).orElse("");
    }
}