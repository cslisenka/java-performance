package com.backend.service;

import com.backend.dto.MessageDTO;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChatDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SessionFactory sessionFactory;

    @PostConstruct
    public void init() {
        jdbcTemplate.execute("DELETE FROM chat");
    }

    public boolean add(String message) {
        // Simulating errors
        if ("error".equals(message)) {
            throw new RuntimeException("backend error");
        }

        int rows = jdbcTemplate.update("INSERT INTO chat (message, timestamp) VALUES (?, NOW())", message);
        return rows > 0;
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getAll() {
        List<Message> result = sessionFactory.getCurrentSession()
                .createQuery("from Message msg order by msg.timestamp DESC")
                .setMaxResults(2).list();

        return result.stream().map(msg -> new MessageDTO(msg.getMessage())).collect(Collectors.toList());
    }
}