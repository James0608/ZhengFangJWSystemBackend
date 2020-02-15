package com.hwj.crawler_backend.controller;

import com.hwj.crawler_backend.entity.User;
import com.hwj.crawler_backend.service.HttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class LoginController {
    private HttpService httpService;

    @Autowired
    public LoginController(HttpService httpService) {
        this.httpService = httpService;
    }

    @PostMapping("login")
    public String login(@RequestBody User user, HttpServletRequest requeset) {
        String loginResult = httpService.login(user);
        while (loginResult.equals("验证码不正确！！")) {
            loginResult = httpService.login(user);
        }
        return loginResult;
    }
}
