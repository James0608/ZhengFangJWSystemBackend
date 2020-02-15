# 项目开源

# 1.Github项目地址

- 项目地址：<https://github.com/James0608/ZhengFangJWSystemBackend> 
- 我的博客地址：https://blog.heyhwj.cn/

# 2.如何启动项目？

- 请先阅读[代码实现的总体思路](<https://blog.heyhwj.cn/2020/02/14/%E6%88%91%E7%9A%84%E7%AC%AC%E4%B8%80%E4%B8%AA%E5%BC%80%E6%BA%90%E9%A1%B9%E7%9B%AE%EF%BC%9AJava%E7%88%AC%E8%99%AB%E7%88%AC%E5%8F%96%E6%97%A7%E7%89%88%E6%AD%A3%E6%96%B9%E6%95%99%E5%8A%A1%E7%B3%BB%E7%BB%9F%E8%AF%BE%E7%A8%8B%E8%A1%A8%E3%80%81%E6%88%90%E7%BB%A9%E8%A1%A8/#2-%E4%BB%A3%E7%A0%81%E5%AE%9E%E7%8E%B0%E7%9A%84%E6%80%BB%E4%BD%93%E6%80%9D%E8%B7%AF> )
- windows用户，在D盘创建image目录，然后把项目`src/resource/ocr/`目录下的train_set(数据集)、train_test(测试集)、train_result(结果集)、record(每次登录记录验证码识别结果)这四个文件夹复制到image下，这样子就不用修改`application-dev.yml`。反过来，也可以通过修改配置文件来自定义加载路径。Linux用户请参考`application-prod.yml`配置文件的路径来创建。
- 更改`GlobalConstant`类下的URL为自己学校正方教务管理系统的地址，**一般是只需要更改域名部分，后面的子路径即使是不同学校也不会有变化。**

# 3.项目无法启动怎么办？

- 请检查是否是路径错误，是否已经正确的按要求创建了所需要的目录

- 请检查`GlobalConstant`类下的URL与教务系统上的请求URL是否一致

- 请检查正方教务管理系统FormData（post请求的body）的key是否与项目代码中的一致

  不同学校的系统，可能在表单参数的名称上有所差异，请根据自己的实际情况更改`HttpService`类里对应的代码。

- 可以在Github上提issiue，也可以直接到[博客文章](<https://blog.heyhwj.cn/2020/02/14/%E6%88%91%E7%9A%84%E7%AC%AC%E4%B8%80%E4%B8%AA%E5%BC%80%E6%BA%90%E9%A1%B9%E7%9B%AE%EF%BC%9AJava%E7%88%AC%E8%99%AB%E7%88%AC%E5%8F%96%E6%97%A7%E7%89%88%E6%AD%A3%E6%96%B9%E6%95%99%E5%8A%A1%E7%B3%BB%E7%BB%9F%E8%AF%BE%E7%A8%8B%E8%A1%A8%E3%80%81%E6%88%90%E7%BB%A9%E8%A1%A8/>  )下进行评论，详细描述错误现象，错误是否可重现等。

# 4.特别鸣谢

- 本项目的验证码识别部分是在[Allenhua的自动识别验证码项目](<https://github.com/Allenhua/HzauStudy> )的基础上完成的，特此鸣谢。

- 参考文章：

  [1]：[用java模拟登录正方教务系统，抓取课表和个人成绩等数据](<https://blog.csdn.net/esaulu/article/details/65661133> )

  [2]：[爬取正方教务管理系统获取学生信息](<https://blog.csdn.net/ldx19980108/article/details/81217899> )

感谢为开源工作做出奉献的每一个开发者，开源意味着更多的交流机会和学习机会，同样希望自己这个项目能帮到有需要的人。