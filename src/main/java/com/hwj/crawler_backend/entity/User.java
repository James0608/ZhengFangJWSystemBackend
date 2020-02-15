package com.hwj.crawler_backend.entity;

public class User {
    private String userName; // 学生姓名
    private String userNumber; // 学生学号
    private String userPassword; // 登录密码

    public User(String userName, String userNumble, String userPassword, String checkCode) {
        this.userName = userName;
        this.userNumber = userNumble;
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", userNumber='" + userNumber + '\'' +
                ", userPassword='" + userPassword + '\'' +
                '}';
    }
}
