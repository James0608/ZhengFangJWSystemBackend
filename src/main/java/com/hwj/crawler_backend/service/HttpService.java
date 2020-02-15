package com.hwj.crawler_backend.service;

import com.hwj.crawler_backend.contant.GlobalConstant;
import com.hwj.crawler_backend.utils.JavaOCR;
import com.hwj.crawler_backend.utils.ParseUtil;
import com.hwj.crawler_backend.entity.CourseBean;
import com.hwj.crawler_backend.entity.HttpBean;
import com.hwj.crawler_backend.entity.ScoreBean;
import com.hwj.crawler_backend.entity.User;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("httpService")
public class HttpService {
    private CloseableHttpClient httpClient;// Http客户端
    private Map<BufferedImage, String> map;// 验证码识别训练结果集
    private JavaOCR javaOCR;
    private HttpServletRequest request;
    @Value("${crawler.javaOCR.trainRecordDir}")
    private String trainRecordDir; // 验证码识别结果保存路径

    @Autowired
    public HttpService(JavaOCR javaOCR, HttpServletRequest request) {
        httpClient = HttpClients.createDefault();
        this.javaOCR = javaOCR;
        this.request = request;
    }

    @PostConstruct
    public void postConstruct() {
        map = javaOCR.loadTrainOcr();
    }

    /**
     * 初始化，主要用于收集cookie和viewState
     */
    public HttpBean init() {
        CloseableHttpResponse requestResponse = sendGetRequest(GlobalConstant.INDEX_URL, "");
        String cookie = requestResponse.getFirstHeader("Set-Cookie").getValue();//  获取cookie
        HttpBean httpBean = new HttpBean();
        try {
            String html = EntityUtils.toString(requestResponse.getEntity(), "utf-8");
            httpBean.setViewState(getViewState(html));//提取页面表单中的__VIEWSTATE的值
            httpBean.setCookie(cookie);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("完成初始化，获取到的cookie为" + httpBean.getCookie()
                + ",获取到的viewState为" + httpBean.getViewState());
        return httpBean;
    }

    /**
     * 根据指定url发送get请求
     *
     * @param url     请求url
     * @param referer 引用地址
     * @return 响应页面的response对象
     */
    public CloseableHttpResponse sendGetRequest(String url, String referer) {
        HttpSession session = request.getSession();
        HttpBean httpBean = (HttpBean) session.getAttribute("httpBean");
        String cookie = StringUtils.isEmpty(httpBean) ? "" : httpBean.getCookie();
        System.out.println("发送get请求：" + url);
        CloseableHttpResponse requestResponse = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Cookie", cookie);//设置cookie
            if (referer != null && !referer.equals("")) {
                httpGet.setHeader("Referer", referer);//如果有地址引用则设置
            }
            requestResponse = httpClient.execute(httpGet);//提交请求获得响应
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestResponse;
    }

    /**
     * 根据指定url和参数值发送post请求
     *
     * @param url     请求url
     * @param referer 引用
     * @param entity  参数列表
     * @return 响应页面的response对象
     */
    public CloseableHttpResponse sendPostRequest(String url, String referer, UrlEncodedFormEntity entity) {
        HttpSession session = request.getSession();
        HttpBean httpBean = (HttpBean) session.getAttribute("httpBean");
        String cookie = StringUtils.isEmpty(httpBean) ? "" : httpBean.getCookie();
        System.out.println("发送post请求：" + url);
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse requestResponse = null;
        try {
            httpPost.setHeader("Cookie", cookie);    //设置cookie
            if (referer != null && !referer.equals("")) {
                httpPost.setHeader("Referer", referer);//如果有地址引用则设置
            }
            httpPost.setEntity(entity);//设置请求参数
            requestResponse = httpClient.execute(httpPost);//提交请求
        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestResponse;
    }

    /**
     * 登陆
     *
     * @param user 用户信息
     * @return 返回登陆是否成功
     */
    public String login(User user) {
        HttpSession session = request.getSession();
        // 初始化
        HttpBean httpBean = init();
        // 将信息保存进新创建的session中
        session.setAttribute("httpBean", httpBean);
        //组织登陆请求参数（表单参数名称可能不一样，请自行核对）
        ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair("__VIEWSTATE", httpBean.getViewState()));//__VIEWSTATE，不可缺少这个参数
        params.add(new BasicNameValuePair("txtUserName", user.getUserNumber()));//学号
        params.add(new BasicNameValuePair("TextBox1", ""));
        params.add(new BasicNameValuePair("TextBox2", user.getUserPassword()));//密码
        params.add(new BasicNameValuePair("txtSecretCode", getCheckImgText()));//验证码
        params.add(new BasicNameValuePair("RadioButtonList1", "学生"));//登陆用户类型
        params.add(new BasicNameValuePair("Button1", ""));
        params.add(new BasicNameValuePair("lbLanguage", ""));
        params.add(new BasicNameValuePair("hidPdrs", ""));
        params.add(new BasicNameValuePair("hidsc", ""));
        String loginErrorMsg = "no error";
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "GB2312");    //封装成参数对象
            CloseableHttpResponse requestResponse = sendPostRequest(GlobalConstant.LOGIN_URL, null, entity);//发送请求
            String html = EntityUtils.toString(requestResponse.getEntity(), "utf-8");
            // 检测是否有登陆错误的信息，有则记录信息，若返回的状态码是302则登陆成功
            if (html.contains(GlobalConstant.CHECKCODE_ERROR)) {
                loginErrorMsg = GlobalConstant.CHECKCODE_ERROR;
            } else if (html.contains(GlobalConstant.CHECKCODE_NULL)) {
                loginErrorMsg = GlobalConstant.CHECKCODE_NULL;
            } else if (html.contains(GlobalConstant.PASSWORD_ERROR)) {
                loginErrorMsg = GlobalConstant.PASSWORD_ERROR;
            } else if (html.contains(GlobalConstant.USERNUMBER_NULL)) {
                loginErrorMsg = GlobalConstant.USERNUMBER_NULL;
            } else if (html.contains(GlobalConstant.USERNUMBER_ERROR)) {
                loginErrorMsg = GlobalConstant.USERNUMBER_ERROR;
            } else if (requestResponse.getStatusLine().getStatusCode() == 302) {
                // 登陆成功,保存已登录的用户的信息
                httpBean.setUser(user);
                // 保存主页面的查询链接
                httpBean = saveQueryURL(httpBean);
                // 更新session中的信息
                session.setAttribute("httpBean", httpBean);
                return "登录成功";// 返回登陆成功信息
            } else {
                loginErrorMsg = "未知错误";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loginErrorMsg;
    }

    /**
     * __VIEWSTATE字段不能和查询的学期相同
     * 查询非本学期的课程时，用post方法
     * 查询本学期的课程时，用get方法
     *
     * @param xn
     * @param xq
     * @throws IOException
     */
    public ArrayList<CourseBean> queryStuCourseList(String xn, String xq) {
        HttpSession session = request.getSession();
        HttpBean httpBean = (HttpBean) session.getAttribute("httpBean");
        String queryCourseUrl = GlobalConstant.INDEX_URL + httpBean.getQueryStuCourseListUrl();
        CloseableHttpResponse requestResponse = null;
        //没有学年度和学期的的信息，则发送get请求，否则发送post请求
        if (xn == null || xq == null) {
            requestResponse = sendGetRequest(queryCourseUrl, GlobalConstant.MAIN_URL + httpBean.getUser().getUserNumber());
        } else {
            List<NameValuePair> courseForms = new ArrayList<>();
            courseForms.add(new BasicNameValuePair("__EVENTTARGET", ""));
            courseForms.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
            courseForms.add(new BasicNameValuePair("__VIEWSTATE", httpBean.getViewState()));
            courseForms.add(new BasicNameValuePair("xnd", xn));
            courseForms.add(new BasicNameValuePair("xqd", xq));
            try {
                requestResponse = sendPostRequest(queryCourseUrl, queryCourseUrl, new UrlEncodedFormEntity(courseForms, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String courseListSourceCode = null;
        try {
            courseListSourceCode = EntityUtils.toString(requestResponse.getEntity(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 更新__VIEWSTATE值
        httpBean.setViewState(getViewState(courseListSourceCode));
        // 更新session中的信息
        session.setAttribute("httpBean", httpBean);
        // 解析HTML
        return ParseUtil.parseCourseTableHtml(courseListSourceCode);
    }

    /**
     * 查询个人成绩,以集合的形式返回结果
     *
     * @param xn 学年格式为201x-201x
     * @param xq 学期分为1和2
     * @throws IOException
     */
    public List<ScoreBean> queryStuGrade(String xn, String xq) {
        HttpSession session = request.getSession();
        HttpBean httpBean = (HttpBean) session.getAttribute("httpBean");
        CloseableHttpResponse requestResponse = null;
        String queryGradeUrl = GlobalConstant.INDEX_URL + httpBean.getQueryStuScoreListUrl();
        if (xn == null || xq == null) {
            requestResponse = sendGetRequest(queryGradeUrl, GlobalConstant.MAIN_URL + httpBean.getUser().getUserNumber());
        } else {
            List<NameValuePair> scoreForms = new ArrayList<>();
            scoreForms.add(new BasicNameValuePair("__EVENTTARGET", ""));
            scoreForms.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
            scoreForms.add(new BasicNameValuePair("__VIEWSTATE", httpBean.getViewState()));
            scoreForms.add(new BasicNameValuePair("ddlxn", xn));
            scoreForms.add(new BasicNameValuePair("ddlxq", xq));
            try {
                scoreForms.add(new BasicNameValuePair("btnCx", URLEncoder.encode("查询", "GB2312")));
                requestResponse = sendPostRequest(queryGradeUrl, queryGradeUrl, new UrlEncodedFormEntity(scoreForms, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String gradeSourceCode = null;
        try {
            gradeSourceCode = EntityUtils.toString(requestResponse.getEntity(), "utf-8");
            System.out.println(gradeSourceCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 更新__VIEWSTATE值
        httpBean.setViewState(getViewState(gradeSourceCode));
        // 更新session中的信息
        session.setAttribute("httpBean", httpBean);
        return ParseUtil.parseScoreHtml(gradeSourceCode);
    }

    public List<ScoreBean> queryStuGrade2(String xn, String xq) {
        HttpSession session = request.getSession();
        HttpBean httpBean = (HttpBean) session.getAttribute("httpBean");
        // 更新VIEWSTATE
        CloseableHttpResponse httpResponse = sendGetRequest(GlobalConstant.INDEX_URL + httpBean.getQueryStuScoreListUrl2(), GlobalConstant.MAIN_URL + httpBean.getUser().getUserNumber());
        String string = null;
        try {
            string = EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        httpBean.setViewState(getViewState(string));

        CloseableHttpResponse requestResponse = null;
        String queryGradeUrl = GlobalConstant.INDEX_URL + httpBean.getQueryStuScoreListUrl2();
        List<NameValuePair> scoreForms = new ArrayList<>();
        scoreForms.add(new BasicNameValuePair("__EVENTTARGET", ""));
        scoreForms.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
        scoreForms.add(new BasicNameValuePair("__VIEWSTATE", httpBean.getViewState()));
        scoreForms.add(new BasicNameValuePair("ddlXN", xn));
        scoreForms.add(new BasicNameValuePair("ddlXQ", xq));
        scoreForms.add(new BasicNameValuePair("ddl_kcxz", ""));
        scoreForms.add(new BasicNameValuePair("btn_xq", "%D1%A7%C6%DA%B3%C9%BC%A8"));
        try {
            requestResponse = sendPostRequest(queryGradeUrl, queryGradeUrl, new UrlEncodedFormEntity(scoreForms, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String gradeSourceCode = null;
        try {
            gradeSourceCode = EntityUtils.toString(requestResponse.getEntity(), "utf-8");
            // System.out.println(gradeSourceCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 更新__VIEWSTATE值
        httpBean.setViewState(getViewState(gradeSourceCode));
        // 更新session中的信息
        session.setAttribute("httpBean", httpBean);
        return ParseUtil.parseScoreHtml2(gradeSourceCode);
    }

    /**
     * 获取验证码
     *
     * @return 验证码图片
     */
    public byte[] getCheckImg() {
        String url = GlobalConstant.SECRETCODE_URL;
        byte[] imgByte = null;
        try {
            CloseableHttpResponse requestResponse = sendGetRequest(url, "");
            imgByte = EntityUtils.toByteArray(requestResponse.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgByte;
    }

    /**
     *
     * @return 验证码识别结果
     */
    public String getCheckImgText() {
        String ocrResult = "";
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(getCheckImg()));
            BufferedImage imageBinary = javaOCR.getImgBinary(image);
            ocrResult = javaOCR.getOcrResult(imageBinary, map);
            ImageIO.write(image, "png", new File(trainRecordDir + ocrResult + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ocrResult;
    }

    /**
     * @param html 登录页面源码
     * @return 登录页的__VIEWSTATE
     */
    public String getViewState(String html) {
        return Jsoup.parse(html).select("input[name=__VIEWSTATE]").val();
    }

    /**
     * 访问系统首页，查找并保存查询各种信息的URL
     *
     * @param httpBean
     */
    public HttpBean saveQueryURL(HttpBean httpBean) throws IOException {
        CloseableHttpResponse response = sendGetRequest(GlobalConstant.MAIN_URL + httpBean.getUser().getUserNumber(),GlobalConstant.LOGIN_URL);
        String html = EntityUtils.toString(response.getEntity(), "utf-8");
        // 信息查询的URL
        String regex_url = "<a href=\"(\\w+)\\.aspx\\?xh=(\\d+)&xm=(.+?)&gnmkdm=N(\\d+)\" target='zhuti' onclick=\"GetMc\\('(.+?)'\\);\">(.+?)</a>";
        // 提取URL中的姓名
        String regex_name = "&xm=(\\S+)&";
        Pattern pattern1 = Pattern.compile(regex_url);
        Pattern pattern2 = Pattern.compile(regex_name);
        Matcher matcher = pattern1.matcher(html);
        while (matcher.find()) {
            // <a href="xskbcx.aspx?xh=xxxxxxxxx&xm=某某某&gnmkdm=N121603" target='zhuti' onclick="GetMc('学生个人课表');">学生个人课表</a>
            String res = matcher.group();
            // xskbcx.aspx?xh=xxxxxxxxx&xm=某某某&gnmkdm=N121603" target='zhuti' onclick="GetMc('学生个人课表');">学生个人课表</a>
            String url = res.substring(res.indexOf("href=\"") + 6);
            // xskbcx.aspx?xh=xxxxxxxxx&xm=某某某&gnmkdm=N121603
            url = url.substring(0, url.indexOf("\""));
            // 姓名为中文,需要进行编码 URLEncoder.encode(userName, "GB2312")
            Matcher matcher2 = pattern2.matcher(url);
            if (matcher2.find()) {
                url = url.replaceAll(regex_name, "&xm=" + URLEncoder.encode(matcher2.group(1)) + "&");
                if (StringUtils.isEmpty(httpBean.getUser().getUserName()))
                    httpBean.getUser().setUserName(matcher2.group(1));
            }
            if (res.contains("学生个人课表")) {
                httpBean.setQueryStuCourseListUrl(url);
                continue;
            }
            /*  有两种成绩查询,名称相同,但实际URL不同
                xscjcx_dq.aspx?xh=xxxxxxxxx&xm=%DD%B6%CE%B0%BD%DC&gnmkdm=N121617
                xscjcx.aspx?xh=xxxxxxxxx&xm=%DD%B6%CE%B0%BD%DC&gnmkdm=N121618
            */
            if (res.contains("成绩查询") && res.contains("N121617")) {
                httpBean.setQueryStuScoreListUrl(url);
            }
            if (res.contains("成绩查询") && res.contains("N121618")) {
                httpBean.setQueryStuScoreListUrl2(url);
            }
        }
        return httpBean;
    }
}

















































