package com.hwj.crawler_backend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JavaOCR {
    @Value("${crawler.javaOCR.trainSetDir}")
    private String trainSetDir;
    @Value("${crawler.javaOCR.trainResultDir}")
    private String trainResultDir;
    @Value("${crawler.javaOCR.trainTestDir}")
    private String trainTestDir;

    public  int whiteOrBlack(int colorRgb) {
        Color color = new Color(colorRgb);
        if (color.getRed() + color.getBlue() + color.getGreen() > 153) {
            return 1;//white
        } else
            return 0;//black
    }

    public  int isBlack(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() <= 100) {
            return 1;
        }
        return 0;
    }

    public  int isWhite(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() > 600) {
            return 1;
        }
        return 0;
    }

    public  int isBlue(int colorInt) {
        Color color = new Color(colorInt);
        int rgb = color.getRed() + color.getGreen() + color.getBlue();
        if (rgb == 153) {
            return 1;
        }
        return 0;
    }

    /**
     * 对验证码图片进行预处理，提高识别度,BufferedImage对象的左上角坐标为 (0, 0)，单位为px
     *
     * @param bi 原始验证码图片
     * @return 预处理过的验证码图片
     */
    public  BufferedImage getImgBinary(BufferedImage bi) throws IOException {
        // 先裁剪掉验证码左边的空白
        bi = bi.getSubimage(3, 1, bi.getWidth() - 3, bi.getHeight() - 5);
        // 直接裁剪左边50px的验证码，相当于裁剪验证码右边的空白
        bi = bi.getSubimage(0, 0, 50, bi.getHeight());
        int width = bi.getWidth();
        int height = bi.getHeight();
        // 图片黑白处理
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // 如果不是纯色的验证码，可以用大于、小于某一范围之类的判断，而不是用等于
                int rgb = bi.getRGB(i, j);
                if (isBlue(rgb) == 1) {
                    bi.setRGB(i, j, Color.BLACK.getRGB());
                } else {
                    bi.setRGB(i, j, Color.WHITE.getRGB());
                }
            }
        }
        return bi;
    }

    /**
     * 进行图片裁剪，将每张验证码进行横向四等分
     *
     * @param image 要裁剪的验证码图片
     * @return 裁剪后的验证码图片子集
     */
    public  List<BufferedImage> getCharSplit(BufferedImage image) {
        List<BufferedImage> subImageList = new ArrayList<>();
        int width = image.getWidth() / 4;
        int height = image.getHeight();
        subImageList.add(image.getSubimage(0, 0, width, height));
        subImageList.add(image.getSubimage(width, 0, width, height));
        subImageList.add(image.getSubimage(width * 2, 0, width, height));
        subImageList.add(image.getSubimage(width * 3, 0, width, height));
        return subImageList;
    }

    /**
     * 进行验证码识别训练
     *
     * @throws IOException 抛IO异常
     */
    public  void trainOcr() throws IOException {
        int index = 0;
        File dir = new File(trainSetDir);
        for (File f : dir.listFiles()) {
            List<BufferedImage> list = getCharSplit(getImgBinary(ImageIO.read(f)));
            if (list.size() == 4) {
                for (int i = 0; i < list.size(); i++) {
                    ImageIO.write(list.get(i), "png", new File(trainResultDir
                            + f.getName().charAt(i) + "-" + (index++) + ".png"));
                }
            }
        }
        System.out.println("训练成功，共载入训练集" + dir.listFiles().length + "张图片");
    }

    /**
     * 读取训练集结果目录下的所有训练图片
     *
     * @return 训练集结果目录下的所有训练图片
     * @throws IOException
     */
    public Map<BufferedImage, String> loadTrainOcr() {
        Map<BufferedImage, String> map = new HashMap<>();
        // 读取训练集结果目录
         File trainResultFileDir = new File(trainResultDir);
        // 获取目录中的所有图片
        for (File file : trainResultFileDir.listFiles()) {
            // 读取到内存中的BufferedImage为key,文件名的第一个字符为value
            try {
                map.put(ImageIO.read(file), file.getName().charAt(0) + "");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * @param image 裁剪后的验证码图片子集
     * @param map   训练集
     * @return 验证码图片子集的识别结果
     */
    private String charOcr(BufferedImage image, Map<BufferedImage, String> map) {
        String string = "";
        int width = image.getWidth();
        int height = image.getHeight();
        int min = width * height;

        for (BufferedImage subImage : map.keySet()) {
            int count = 0;
            if (Math.abs(subImage.getWidth() - width) > 2)
                continue;
            int widthmin = width < subImage.getWidth() ? width : subImage.getWidth();
            int heightmin = height < subImage.getHeight() ? height : subImage.getHeight();
            loop:
            for (int i = 0; i < widthmin; i++) {
                for (int j = 0; j < heightmin; j++) {
                    if (isBlack(subImage.getRGB(i, j)) != isBlack(image.getRGB(i, j))) {
                        count++;
                    }
                    if (count >= min)
                        break loop;
                }
            }
            if (count < min) {
                min = count;
                string = map.get(subImage);
            }
        }
        return string;
    }

    /**
     * @param imgBinary 待识别的验证码
     * @param map       训练集
     * @return 验证码的识别结果
     */
    public String getOcrResult(BufferedImage imgBinary, Map<BufferedImage, String> map) {
        StringBuilder result = new StringBuilder();
        List<BufferedImage> splitList = getCharSplit(imgBinary);
        for (BufferedImage img : splitList) {
            result.append(charOcr(img, map));
        }
        return result.toString();
    }

    public String getTrainTestDir() {
        return trainTestDir;
    }
}
