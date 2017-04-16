package com.frontend;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;

@RestController
public class DateService {

    @RequestMapping("/date")
    public DateServiceResponse getDate(
            @RequestParam(value="name", defaultValue="World") String name) {
        DateServiceResponse response = new DateServiceResponse();
        response.setGreeting("Greetings, " + name);
        response.setResult(Calendar.getInstance().getTime().toString());
        return response;
    }
}
