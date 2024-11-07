package com.example.demo.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/user")
public class DangNhapController {
    @GetMapping(value = "/dangnhap")
    public String DangNhap() {
        return "user/dang-nhap/dangnhap";
    }
}
