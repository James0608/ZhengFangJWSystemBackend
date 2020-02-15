package com.hwj.crawler_backend.checkcode;

import com.hwj.crawler_backend.CrawlerBackendApplication;
import com.hwj.crawler_backend.service.HttpService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrawlerBackendApplication.class)
public class DownloadCheckCode {
    @Autowired
    HttpService httpService;

    @Test
    public void downloadCheckcode() {
        BufferedImage image = null;
        for (int i = 0; i < 100 ; i++) {
            try {
                image = ImageIO.read(new ByteArrayInputStream(httpService.getCheckImg()));
                ImageIO.write(image,"png",new File("D:\\image\\"+new Date().getTime()+".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
