package com.hwj.crawler_backend.ocr;

import com.hwj.crawler_backend.CrawlerBackendApplication;
import com.hwj.crawler_backend.utils.JavaOCR;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrawlerBackendApplication.class)
public class TrainOcr {
    @Autowired
    JavaOCR javaOCR;

    @Test
    public void trainOCR() throws IOException {
        javaOCR.trainOcr();
    }

    @Test
    public void testLoadTrainResultSpeed() {
        long start = System.currentTimeMillis();
        Map<BufferedImage, String> map = javaOCR.loadTrainOcr();
        long end = System.currentTimeMillis();
        System.out.println("用时:" + (end - start) + "ms");
    }

    @Test
    public void testOCRAccuracy() throws IOException {
        // 读取测试集目录
        File testFileDir = new File(javaOCR.getTrainTestDir());
        // 加载训练集
        Map<BufferedImage, String> map = javaOCR.loadTrainOcr();
        // 遍历测试集
        File[] testImages = testFileDir.listFiles();
        int testSetSize = testImages.length;
        int i = 0;
        long startTime = System.currentTimeMillis(); //获取开始时间
        for (File file : testImages) {
            String fileName = file.getName().replaceAll("[.][^.]+$", "");
            BufferedImage imgBinary = javaOCR.getImgBinary(ImageIO.read(file));
            String result = javaOCR.getOcrResult(imgBinary, map);
            if (fileName.equals(result)) {
                i++;
            } else {
                System.out.println("识别失败," + file.getName() + "的识别结果为：" + result);
            }
        }
        System.out.println("共识别" + testSetSize + "张验证码，其中成功识别" + i + "张，识别率为:"
                + String.format("%.2f", (i / (double) testSetSize) * 100) + "%");
        long endTime = System.currentTimeMillis(); //获取结束时间
        System.out.println("测试程序运行时间： " + (endTime - startTime) + "ms");
    }
}
