package com.hwj.crawler_backend.entity;

public class HttpBean {
    private User user; // 已登陆用户的信息
    private String cookie;// 记录cookie
    private String viewState;// 记录正方教务系统页面表单的__VIEWSTATE的值
    private String queryStuCourseListUrl; // 查询课表的URL
    private String queryStuScoreListUrl; // 查询成绩URL
    private String queryStuScoreListUrl2;// 查询成绩URL2(速度较慢，涵盖成绩统计等)

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getViewState() {
        return viewState;
    }

    public void setViewState(String viewState) {
        this.viewState = viewState;
    }

    public String getQueryStuCourseListUrl() {
        return queryStuCourseListUrl;
    }

    public void setQueryStuCourseListUrl(String queryStuCourseListUrl) {
        this.queryStuCourseListUrl = queryStuCourseListUrl;
    }

    public String getQueryStuScoreListUrl() {
        return queryStuScoreListUrl;
    }

    public void setQueryStuScoreListUrl(String queryStuScoreListUrl) {
        this.queryStuScoreListUrl = queryStuScoreListUrl;
    }

    public String getQueryStuScoreListUrl2() {
        return queryStuScoreListUrl2;
    }

    public void setQueryStuScoreListUrl2(String queryStuScoreListUrl2) {
        this.queryStuScoreListUrl2 = queryStuScoreListUrl2;
    }

    public HttpBean() {
    }

    public HttpBean(User user) {
        this.user = user;
    }
}
