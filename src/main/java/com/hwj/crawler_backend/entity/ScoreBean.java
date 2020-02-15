package com.hwj.crawler_backend.entity;

public class ScoreBean {
    private String xn; // 学年
    private String xq; // 学期
    private String code; // 课程代码
    private String name; // 课程名称
    private String type; // 课程性质
    private String credit; //学分数
    private String score; // 期末总成绩
    private String retestScore; // 补考成绩
    private String isRebuild; // 是否重修
    private String college; // 开课学院
    private String remark; // 备注
    private String retestRemark; // 补考备注

    public String getXn() {
        return xn;
    }

    public void setXn(String xn) {
        this.xn = xn;
    }

    public String getXq() {
        return xq;
    }

    public void setXq(String xq) {
        this.xq = xq;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getRetestScore() {
        return retestScore;
    }

    public void setRetestScore(String retestScore) {
        this.retestScore = retestScore;
    }

    public String getIsRebuild() {
        return isRebuild;
    }

    public void setIsRebuild(String isRebuild) {
        this.isRebuild = isRebuild;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRetestRemark() {
        return retestRemark;
    }

    public void setRetestRemark(String retestRemark) {
        this.retestRemark = retestRemark;
    }
}
