package com.hwj.crawler_backend.entity;

public class CourseBean {
    public static final int ALL_WEEK = 0;// 全部周数

    public static final int SINGLE_WEEK = 1;// 单周

    public static final int DOUBLE_WEEK = 2;// 双周

    private String name;// 课程名称

    private String type;// 课程类型

    private String classRoom;// 上课教室

    private String teacher;// 任课老师

    private int startWeek;// 课程开始周

    private int endWeek;// 课程结束周

    private int weekState;// 记录该课程是双周还是单周上课

    private SchoolTime schoolTime;// 课程在课表中的位置

    /**
     * 记录课程在课表中位置的内部类
     */
    class SchoolTime {
        private String number; //  第几节课

        private String day; // 星期几

        public SchoolTime() {
        }

        public SchoolTime(String number, String day) {
            this.number = number;
            this.day = day;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        @Override
        public String toString() {
            return "SchoolTime{" +
                    "number='" + number + '\'' +
                    ", day='" + day + '\'' +
                    '}';
        }
    }

    /**
     * 构造函数
     */
    public CourseBean() {
    }

    /**
     * 获取课程名称
     * @return 返回课程名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置课程名称
     * @param name 课程名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取课程类型
     * @return 返回课程类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置课程类型
     * @param type 课程类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取教室
     * @return 返回教室
     */
    public String getClassRoom() {
        return classRoom;
    }

    /**
     * 设置教室
     * @param classRoom 返回教室
     */
    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    /**
     * 获取教师
     * @return 返回教师
     */
    public String getTeacher() {
        return teacher;
    }

    /**
     * 设置教师
     * @param teacher 教师
     */
    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    /**
     * 设置课程开始周
     * @param startWeek 课程开始周
     */
    public void setStartWeek(int startWeek) {
        this.startWeek = startWeek;
    }

    /**
     * 获取课程结束周
     * @return 课程结束周
     */
    public int getEndWeek() {
        return endWeek;
    }

    /**
     * 设置课程结束周
     * @param endWeek 课程结束周
     */
    public void setEndWeek(int endWeek) {
        this.endWeek = endWeek;
    }

    /**
     * 获取课程上课是全部周、双周还是单周
     * @return 返回值为ALL_WEEK、SINGLE_WEEK或DOUBLE_WEEK
     */
    public int getWeekState() {
        return weekState;
    }

    /**
     * 设置课程的上课周
     * @param weekState 可取值为ALL_WEEK、SINGLE_WEEK或DOUBLE_WEEK
     */
    public void setWeekState(int weekState) {
        this.weekState = weekState;
    }

    /**
     * 获取课程在课表的位置
     * @return 课表的位置
     */
    public SchoolTime getSchoolTime() {
        return schoolTime;
    }

    /**
     * 设置课表位置
     * @param schoolTime 课表位置
     */
    public void setSchoolTime(SchoolTime schoolTime) {
        this.schoolTime = schoolTime;
    }

    /**
     * 设置课表位置
     * @param x 节数
     * @param y 星期几
     */
    public void setSchoolTime(String x,String y) {
        this.schoolTime = new SchoolTime(x,y);
    }

    /**
     * 获取第几节课上课
     * @return 返回值表示第几节课上课
     */
    public String getNumber() {
        return schoolTime.getNumber();
    }

    /**
     * 获取星期几上课
     * @return 返回星期几
     */
    public String getDay() {
        return schoolTime.getDay();
    }

    /**
     * 获取课程开始周
     * @return 课程开始周
     */
    public int getStartWeek() {
        return startWeek;
    }

    @Override
    public String toString() {
        return "CourseBean{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", classRoom='" + classRoom + '\'' +
                ", teacher='" + teacher + '\'' +
                ", startWeek=" + startWeek +
                ", endWeek=" + endWeek +
                ", weekState=" + weekState +
                ", schoolTime=" + schoolTime +
                '}';
    }
}
