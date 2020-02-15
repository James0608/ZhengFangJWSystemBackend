package com.hwj.crawler_backend.controller;

import com.hwj.crawler_backend.entity.CourseBean;
import com.hwj.crawler_backend.service.HttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CourseController {
    private HttpService httpService;

    @Autowired
    public CourseController(HttpService httpService) {
        this.httpService = httpService;
    }

    @GetMapping("DefaultCourseList")
    public List<CourseBean> getDefaultCourseList() {
        return httpService.queryStuCourseList(null, null);
    }

    @GetMapping("CourseList")
    public List<CourseBean> getScoreList(@RequestParam("xn") String xn, @RequestParam("xq") String xq) {
        return httpService.queryStuCourseList(xn, xq);
    }
}
