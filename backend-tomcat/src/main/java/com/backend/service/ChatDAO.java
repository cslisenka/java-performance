package com.backend.service;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

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

    public boolean add(String name, String message) {
        // Simulating errors
        if ("error".equals(name)) {
            throw new RuntimeException("backend error");
        }

        int rows = jdbcTemplate.update("INSERT INTO chat (message, timestamp) VALUES (?, NOW())", message);
        return rows > 0;
    }

    @Transactional(readOnly = true)
    public List<Message> getAll() {
        return sessionFactory.getCurrentSession()
                .createQuery("from Message msg order by msg.timestamp DESC")
                .setMaxResults(2).list();
    }
}