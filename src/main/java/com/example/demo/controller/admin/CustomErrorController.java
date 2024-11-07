package com.example.demo.controller.admin;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError() {
        // You can return a view name here or an error page.
        return "error"; // Make sure to have an error.html or error.jsp in your templates
    }
}
