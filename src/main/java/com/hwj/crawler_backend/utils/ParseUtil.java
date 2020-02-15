package com.hwj.crawler_backend.utils;

import com.hwj.crawler_backend.entity.CourseBean;
import com.hwj.crawler_backend.entity.ScoreBean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtil {
    public static List<ScoreBean> parseScoreHtml(String html) {
        List<ScoreBean> scoreList = new ArrayList<>();
        // 解析HTML
        Document document = Jsoup.parse(html);
        Elements dateListRows = document.getElementsByClass("datelist").select("tr");
        for (Element dateListRow : dateListRows) {
            if (dateListRow.className().equals("datelisthead")) {
                continue;
            }
            Elements dateListTds = dateListRow.select("td");
            ScoreBean scoreBean = new ScoreBean();
            for (int j = 0; j < dateListTds.size(); j++) {
                String text = dateListTds.get(j).text();
                switch (j) {
                    case 0:
                        scoreBean.setXn(text);
                        break;
                    case 1:
                        scoreBean.setXq(text);
                        break;
                    case 2:
                        scoreBean.setCode(text);
                        break;
                    case 3:
                        scoreBean.setName(text);
                        break;
                    case 4:
                        scoreBean.setType(text);
                        break;
                    case 6:
                        scoreBean.setCredit(text);
                        break;
                    case 10:
                        scoreBean.setScore(text);
                        break;
                    case 11:
                        scoreBean.setRetestScore(text);
                        break;
                    case 12:
                        scoreBean.setIsRebuild(text);
                        break;
                    case 13:
                        scoreBean.setCollege(text);
                        break;
                    case 14:
                        scoreBean.setRemark(text);
                        break;
                    case 15:
                        scoreBean.setRetestRemark(text);
                        break;
                }
            }
            scoreList.add(scoreBean);
        }
        return scoreList;
    }

    public static List<ScoreBean> parseScoreHtml2(String html) {
        List<ScoreBean> scoreList = new ArrayList<>();
        // 解析HTML
        Document document = Jsoup.parse(html);
        Elements dateListRows = document.getElementsByClass("datelist").select("tr");
        for (Element dateListRow : dateListRows) {
            if (dateListRow.className().equals("datelisthead")) {
                continue;
            }
            Elements dateListTds = dateListRow.select("td");
            ScoreBean scoreBean = new ScoreBean();
            for (int j = 0; j < dateListTds.size(); j++) {
                String text = dateListTds.get(j).text();
                switch (j) {
                    case 0:
                        scoreBean.setXn(text);
                        break;
                    case 1:
                        scoreBean.setXq(text);
                        break;
                    case 2:
                        scoreBean.setCode(text);
                        break;
                    case 3:
                        scoreBean.setName(text);
                        break;
                    case 4:
                        scoreBean.setType(text);
                        break;
                    case 6:
                        scoreBean.setCredit(text);
                        break;
                    case 12:
                        scoreBean.setScore(text);
                        break;
                    case 14:
                        text = !text.equals(" ") ? text : "无";
                        scoreBean.setRetestScore(text);
                        break;
                    case 16:
                        text = !text.equals(" ") ? text : "无";
                        scoreBean.setCollege(text);
                        break;
                    case 17:
                        text = !text.equals(" ") ? text : "无";
                        scoreBean.setRemark(text);
                        break;
                    case 18:
                        text = !text.isEmpty() ? text : "否";
                        scoreBean.setIsRebuild(text);
                        break;
                }
            }
            scoreList.add(scoreBean);
        }
        return scoreList;
    }

    public static ArrayList<CourseBean> parseCourseTableHtml(String html) {
        ArrayList<String> courseListHtml = getCourseListHtml(html);
        ArrayList<String> courseList = getCourseList(courseListHtml);
        ArrayList<CourseBean> cours = parseCourseList(courseList);
        return cours;
    }

    public static ArrayList<String> getCourseListHtml(String html) {
        // 解析HTML
        Document document = Jsoup.parse(html);
        Elements courseRows = document.getElementById("Table1").select("tr:nth-child(2n-1)");
        ArrayList<String> courseHtmlList = new ArrayList<>();
        for (int i = 1; i < courseRows.size(); i++) {
            Elements dateListTdsString = courseRows.get(i).select("td[rowspan=2]");
            for (int j = 0; j < dateListTdsString.size(); j++) {
                String courseHtml = dateListTdsString.get(j).toString();
                courseHtmlList.add(courseHtml);
            }
        }
        return courseHtmlList;
    }

    public static ArrayList<String> getCourseList(ArrayList<String> courseListHtml) {
        // 删除调课信息
        String regex_font = "\\s<font.*?>(.*?)</font>";
        // 删除所有html标签，但保留标签内内容
        String regex_html = "<[^>]+(>*)";
        // 删除期末考试时间信息
        String regex_exam = "\\s(\\d{4})年(\\d{2})月(\\d{2})日(\\S+)\\s*(\\S*)";
        ArrayList<String> courseList = new ArrayList<>();
        for (String courseHtml : courseListHtml) {
            String courses = courseHtml.replaceAll("<br><br>", "  ").replaceAll("<br>", " ")
                    .replaceAll(regex_font, "").replaceAll(regex_html, "").replaceAll(regex_exam, "");
            courseList.addAll(Arrays.asList(courses.split("\\s{2}")));
        }
        return courseList;
    }

    public static ArrayList<CourseBean> parseCourseList(ArrayList<String> courseList) {
        String regex_course = "(\\S+)\\s+(\\S+)\\s+(\\S{2})(\\S+)\\{第(\\d+)-(\\d+)周(\\S*)}\\s+(\\S+)\\s*(\\S*)";
        Pattern pattern1 = Pattern.compile(regex_course);
        ArrayList<CourseBean> cours = new ArrayList<>();
        String number = null;
        String day = null;
        for (String str : courseList) {
            CourseBean courseBean = new CourseBean();
            str = str.replaceAll("\\|", "");
            Matcher matcher = pattern1.matcher(str);
            if (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    String text = matcher.group(i);
                    switch (i) {
                        case 1:
                            courseBean.setName(text); // 课程名称
                            break;
                        case 2:
                            courseBean.setType(text);// 课程性质
                            break;
                        case 3:
                            day = text;// 星期几
                            break;
                        case 4:
                            number = text;// 第几节
                            break;
                        case 5:
                            courseBean.setStartWeek(Integer.parseInt(text)); //开课周
                            break;
                        case 6:
                            courseBean.setEndWeek(Integer.parseInt(text)); // 结课周
                            break;
                        case 7:
                            if (StringUtils.isEmpty(text)) {
                                courseBean.setWeekState(CourseBean.ALL_WEEK);
                            } else if (text.equals("单周")) {
                                courseBean.setWeekState(CourseBean.SINGLE_WEEK);
                            } else if (text.equals("双周")) {
                                courseBean.setWeekState(CourseBean.DOUBLE_WEEK);
                            }
                        case 8:
                            courseBean.setTeacher(text); // 任课老师
                            break;
                        case 9:
                            text = StringUtils.isEmpty(text) ? "暂无安排" : text;// 教室地点
                            courseBean.setClassRoom(text);
                            break;
                    }
                }
                courseBean.setSchoolTime(number, day);

                cours.add(courseBean);
                System.out.println(courseBean.toString());
            }
        }
        return cours;
    }
}
