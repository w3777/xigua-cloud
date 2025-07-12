package com.xigua.client.utils;

import com.xigua.domain.dto.MultipartFileDTO;
import com.xigua.service.FileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

@Component
public class NineCellImageUtil {

    private int bitmapSize = 300;  // 画布大小
    private int paddingSize = 10;  // 内间距
    private int itemMargin = 15;   // 图片之间的间距
    private Color backgroundColor = Color.WHITE;  // 背景色
    @Autowired
    private FileService fileService;

    /**
     * 生成九宫格图片
     * @author wangjinfei
     * @date 2025/7/12 11:33
     * @param imageList
     * @return String
    */
    public String generateNineCellImage(List<BufferedImage> imageList){
        MultipartFile multipartFile = null;

        // 1.合并九宫格图片
        BufferedImage image = mergeNineCellImages(imageList);

        try {
            // 2.将 BufferedImage 转换为 InputStream
            InputStream inputStream = FileUtil.convertToInputStream(image, "png");
            // 3.将 InputStream 转换为 MultipartFile
            multipartFile = FileUtil.convertToMultipartFile(inputStream, "group_avatar.png");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 4.MultipartFile dubbo不能直接序列化 用对象包装一下
        MultipartFileDTO multipartFileDTO = new MultipartFileDTO();
        BeanUtils.copyProperties(multipartFile,multipartFileDTO);
        // 5.上传图片
        String url = fileService.upload(multipartFileDTO);

        return url;
    }

    // 合并九宫格图片
    private BufferedImage mergeNineCellImages(List<BufferedImage> imageList) {
        if (imageList == null || imageList.size() == 0) {
            return null;
        }

        // 限制最多显示9张图片
        int length = Math.min(imageList.size(), 9);
        BufferedImage outImage = new BufferedImage(bitmapSize, bitmapSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = outImage.createGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, bitmapSize, bitmapSize);  // 填充背景

        // 处理不同的图片数量
        switch (length) {
            case 1: return handleOneImage(outImage, imageList.get(0));
            case 2: return handleTwoImages(outImage, imageList);
            case 3: return handleThreeImages(outImage, imageList);
            case 4: return handleFourImages(outImage, imageList);
            case 5: return handleFiveImages(outImage, imageList);
            case 6: return handleSixImages(outImage, imageList);
            case 7: return handleSevenImages(outImage, imageList);
            case 8: return handleEightImages(outImage, imageList);
            case 9: return handleNineImages(outImage, imageList);
            default: return outImage;  // 默认返回空图像
        }
    }

    // 处理1张图片的情况
    private BufferedImage handleOneImage(BufferedImage outImage, BufferedImage image) {
        /**
         * 一张图片时，将图片缩放到合适的大小并居中
        */

        Graphics2D g = outImage.createGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, bitmapSize, bitmapSize);
        image = scaleAndCenterInsideImage(image, bitmapSize - paddingSize * 2);
        g.drawImage(image, paddingSize, paddingSize, null);
        g.dispose();
        return outImage;
    }

    // 处理2张图片的情况
    private BufferedImage handleTwoImages(BufferedImage outImage, List<BufferedImage> imageList) {
        /**
         * 二张图片时，一行显示两张图片，居中
         */

        Graphics2D g = outImage.createGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, bitmapSize, bitmapSize);

        int cellSize = (bitmapSize - paddingSize * 2 - itemMargin) / 2;
        imageList.get(0);
        imageList.get(1);
        BufferedImage image1 = scaleAndCenterInsideImage(imageList.get(0), cellSize);
        BufferedImage image2 = scaleAndCenterInsideImage(imageList.get(1), cellSize);

        // 第一张图片（左）
        g.drawImage(image1, paddingSize, (bitmapSize - cellSize) / 2, null);
        // 第二张图片（右）
        g.drawImage(image2, paddingSize + cellSize + itemMargin, (bitmapSize - cellSize) / 2, null);
        g.dispose();
        return outImage;
    }

    // 处理3张图片的情况
    private BufferedImage handleThreeImages(BufferedImage outImage, List<BufferedImage> imageList) {
        /**
         * 三张图片时，第一行显示一张图片，第二行显示两张图片
         */

        Graphics2D g = outImage.createGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, bitmapSize, bitmapSize);

        int cellSize = (bitmapSize - paddingSize * 2 - itemMargin) / 2;
        // 第一张图片放第一行
        BufferedImage image1 = scaleAndCenterInsideImage(imageList.get(0), cellSize);
        g.drawImage(image1, (bitmapSize - cellSize) / 2, paddingSize, null);

        // 第二张和第三张图片放第二行
        BufferedImage image2 = scaleAndCenterInsideImage(imageList.get(1), cellSize);
        BufferedImage image3 = scaleAndCenterInsideImage(imageList.get(2), cellSize);
        g.drawImage(image2, paddingSize, paddingSize + cellSize + itemMargin, null);
        g.drawImage(image3, paddingSize + cellSize + itemMargin, paddingSize + cellSize + itemMargin, null);

        g.dispose();
        return outImage;
    }

    // 处理4张图片的情况
    private BufferedImage handleFourImages(BufferedImage outImage, List<BufferedImage> imageList) {
        /**
         * 四张图片时，两行显示两张图片
         */

        Graphics2D g = outImage.createGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, bitmapSize, bitmapSize);

        int cellSize = (bitmapSize - paddingSize * 2 - itemMargin) / 2;
        BufferedImage image1 = scaleAndCenterInsideImage(imageList.get(0), cellSize);
        BufferedImage image2 = scaleAndCenterInsideImage(imageList.get(1), cellSize);
        BufferedImage image3 = scaleAndCenterInsideImage(imageList.get(2), cellSize);
        BufferedImage image4 = scaleAndCenterInsideImage(imageList.get(3), cellSize);

        // 第一行放两张图片
        g.drawImage(image1, paddingSize, paddingSize, null);
        g.drawImage(image2, paddingSize + cellSize + itemMargin, paddingSize, null);

        // 第二行放两张图片
        g.drawImage(image3, paddingSize, paddingSize + cellSize + itemMargin, null);
        g.drawImage(image4, paddingSize + cellSize + itemMargin, paddingSize + cellSize + itemMargin, null);

        g.dispose();
        return outImage;
    }

    // 处理5张图片的情况
    private BufferedImage handleFiveImages(BufferedImage outImage, List<BufferedImage> imageList) {
        /**
         * 五张图片时，第一行显示两张图片，第二行显示三张图片
         */

        Graphics2D g = outImage.createGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, bitmapSize, bitmapSize);

        int cellSize = (bitmapSize - paddingSize * 2 - itemMargin * 2) / 3;

        // 缩放并居中处理每张图片
        BufferedImage image1 = scaleAndCenterInsideImage(imageList.get(0), cellSize);
        BufferedImage image2 = scaleAndCenterInsideImage(imageList.get(1), cellSize);
        BufferedImage image3 = scaleAndCenterInsideImage(imageList.get(2), cellSize);
        BufferedImage image4 = scaleAndCenterInsideImage(imageList.get(3), cellSize);
        BufferedImage image5 = scaleAndCenterInsideImage(imageList.get(4), cellSize);

        // 计算三列的居中位置
        int rowLeft = (bitmapSize - cellSize * 3 - itemMargin * 2) / 2;

        // 计算三行的垂直居中位置
        int totalHeight = cellSize * 2 + itemMargin; // 第一行和第二行合并的高度
        int verticalTop = (bitmapSize - totalHeight) / 2;

        // 第一行：2张图片居中
        int firstRowLeft = (bitmapSize - (cellSize * 2 + itemMargin)) / 2; // 计算两张图片的居中位置
        g.drawImage(image1, firstRowLeft, verticalTop, null);
        g.drawImage(image2, firstRowLeft + cellSize + itemMargin, verticalTop, null);

        // 第二行：3张图片均匀分布
        g.drawImage(image3, rowLeft, verticalTop + cellSize + itemMargin, null);
        g.drawImage(image4, rowLeft + cellSize + itemMargin, verticalTop + cellSize + itemMargin, null);
        g.drawImage(image5, rowLeft + cellSize * 2 + itemMargin * 2, verticalTop + cellSize + itemMargin, null);

        g.dispose();
        return outImage;
    }

    // 处理6张图片的情况
    private BufferedImage handleSixImages(BufferedImage outImage, List<BufferedImage> imageList) {
        /**
         * 六张图片时，两行显示三张图片
         */

        Graphics2D g = outImage.createGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, bitmapSize, bitmapSize);

        int cellSize = (bitmapSize - paddingSize * 2 - itemMargin * 2) / 3;

        // 缩放并居中处理每张图片
        BufferedImage image1 = scaleAndCenterInsideImage(imageList.get(0), cellSize);
        BufferedImage image2 = scaleAndCenterInsideImage(imageList.get(1), cellSize);
        BufferedImage image3 = scaleAndCenterInsideImage(imageList.get(2), cellSize);
        BufferedImage image4 = scaleAndCenterInsideImage(imageList.get(3), cellSize);
        BufferedImage image5 = scaleAndCenterInsideImage(imageList.get(4), cellSize);
        BufferedImage image6 = scaleAndCenterInsideImage(imageList.get(5), cellSize);

        // 计算三列的居中位置
        int rowLeft = (bitmapSize - cellSize * 3 - itemMargin * 2) / 2;  // 计算每行的图片居中位置

        // 计算两行的垂直居中位置
        int totalHeight = cellSize * 2 + itemMargin * 2;  // 两行的总高度
        int verticalTop = (bitmapSize - totalHeight) / 2; // 计算上下的偏移量以居中两行

        // 第一行：3张图片居中显示
        g.drawImage(image1, rowLeft, verticalTop, null);  // 第一张
        g.drawImage(image2, rowLeft + cellSize + itemMargin, verticalTop, null);  // 第二张
        g.drawImage(image3, rowLeft + cellSize * 2 + itemMargin * 2, verticalTop, null);  // 第三张

        // 第二行：3张图片居中显示
        g.drawImage(image4, rowLeft, verticalTop + cellSize + itemMargin, null);  // 第一张
        g.drawImage(image5, rowLeft + cellSize + itemMargin, verticalTop + cellSize + itemMargin, null);  // 第二张
        g.drawImage(image6, rowLeft + cellSize * 2 + itemMargin * 2, verticalTop + cellSize + itemMargin, null);  // 第三张

        g.dispose();
        return outImage;
    }

    // 处理7张图片的情况
    private BufferedImage handleSevenImages(BufferedImage outImage, List<BufferedImage> imageList) {
        /**
         * 七张图片时，第一行显示一张图片，第二行和第三行显示三张图片
         */

        Graphics2D g = outImage.createGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, bitmapSize, bitmapSize);

        int cellSize = (bitmapSize - paddingSize * 2 - itemMargin * 2) / 3;

        // 缩放并居中处理每张图片
        BufferedImage image1 = scaleAndCenterInsideImage(imageList.get(0), cellSize);
        BufferedImage image2 = scaleAndCenterInsideImage(imageList.get(1), cellSize);
        BufferedImage image3 = scaleAndCenterInsideImage(imageList.get(2), cellSize);
        BufferedImage image4 = scaleAndCenterInsideImage(imageList.get(3), cellSize);
        BufferedImage image5 = scaleAndCenterInsideImage(imageList.get(4), cellSize);
        BufferedImage image6 = scaleAndCenterInsideImage(imageList.get(5), cellSize);
        BufferedImage image7 = scaleAndCenterInsideImage(imageList.get(6), cellSize);

        // 计算三列的居中位置
        int rowLeft = (bitmapSize - cellSize * 3 - itemMargin * 2) / 2;

        // 计算三行的垂直居中位置
        int totalHeight = cellSize * 3 + itemMargin * 2;
        int verticalTop = (bitmapSize - totalHeight) / 2;

        // 第一行：1张图片居中
        g.drawImage(image1, rowLeft + cellSize + itemMargin, verticalTop, null); // 调整图片在第一行的位置，居中显示

        // 第二行：3张图片均匀分布
        g.drawImage(image2, rowLeft, verticalTop + cellSize + itemMargin, null);
        g.drawImage(image3, rowLeft + cellSize + itemMargin, verticalTop + cellSize + itemMargin, null);
        g.drawImage(image4, rowLeft + cellSize * 2 + itemMargin * 2, verticalTop + cellSize + itemMargin, null);

        // 第三行：3张图片均匀分布
        g.drawImage(image5, rowLeft, verticalTop + cellSize * 2 + itemMargin * 2, null);
        g.drawImage(image6, rowLeft + cellSize + itemMargin, verticalTop + cellSize * 2 + itemMargin * 2, null);
        g.drawImage(image7, rowLeft + cellSize * 2 + itemMargin * 2, verticalTop + cellSize * 2 + itemMargin * 2, null);

        g.dispose();
        return outImage;
    }


    // 处理8张图片的情况
    private BufferedImage handleEightImages(BufferedImage outImage, List<BufferedImage> imageList) {
        /**
         * 八张图片时，第一行显示两张图片，第二行和第三行显示三张图片
         */

        Graphics2D g = outImage.createGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, bitmapSize, bitmapSize);

        int cellSize = (bitmapSize - paddingSize * 2 - itemMargin * 2) / 3;

        // 缩放并居中处理每张图片
        BufferedImage image1 = scaleAndCenterInsideImage(imageList.get(0), cellSize);
        BufferedImage image2 = scaleAndCenterInsideImage(imageList.get(1), cellSize);
        BufferedImage image3 = scaleAndCenterInsideImage(imageList.get(2), cellSize);
        BufferedImage image4 = scaleAndCenterInsideImage(imageList.get(3), cellSize);
        BufferedImage image5 = scaleAndCenterInsideImage(imageList.get(4), cellSize);
        BufferedImage image6 = scaleAndCenterInsideImage(imageList.get(5), cellSize);
        BufferedImage image7 = scaleAndCenterInsideImage(imageList.get(6), cellSize);
        BufferedImage image8 = scaleAndCenterInsideImage(imageList.get(7), cellSize);

        // 计算三列的居中位置
        int rowLeft = (bitmapSize - cellSize * 3 - itemMargin * 2) / 2;

        // 计算三行的垂直居中位置
        int totalHeight = cellSize * 3 + itemMargin * 2;
        int verticalTop = (bitmapSize - totalHeight) / 2;

        // 第一行：2张图片居中
        int firstRowLeft = (bitmapSize - (cellSize * 2 + itemMargin)) / 2; // 计算两张图片的居中位置
        g.drawImage(image1, firstRowLeft, verticalTop, null);
        g.drawImage(image2, firstRowLeft + cellSize + itemMargin, verticalTop, null);

        // 第二行：3张图片均匀分布
        g.drawImage(image3, rowLeft, verticalTop + cellSize + itemMargin, null);
        g.drawImage(image4, rowLeft + cellSize + itemMargin, verticalTop + cellSize + itemMargin, null);
        g.drawImage(image5, rowLeft + cellSize * 2 + itemMargin * 2, verticalTop + cellSize + itemMargin, null);

        // 第三行：3张图片均匀分布
        g.drawImage(image6, rowLeft, verticalTop + cellSize * 2 + itemMargin * 2, null);
        g.drawImage(image7, rowLeft + cellSize + itemMargin, verticalTop + cellSize * 2 + itemMargin * 2, null);
        g.drawImage(image8, rowLeft + cellSize * 2 + itemMargin * 2, verticalTop + cellSize * 2 + itemMargin * 2, null);

        g.dispose();
        return outImage;
    }

    // 处理9张图片的情况
    private BufferedImage handleNineImages(BufferedImage outImage, List<BufferedImage> imageList) {
        /**
         * 九张图片时，三行三列
         */

        Graphics2D g = outImage.createGraphics();
        g.setColor(backgroundColor);
        g.fillRect(0, 0, bitmapSize, bitmapSize);

        int cellSize = (bitmapSize - paddingSize * 2 - itemMargin * 2) / 3;
        BufferedImage image1 = scaleAndCenterInsideImage(imageList.get(0), cellSize);
        BufferedImage image2 = scaleAndCenterInsideImage(imageList.get(1), cellSize);
        BufferedImage image3 = scaleAndCenterInsideImage(imageList.get(2), cellSize);
        BufferedImage image4 = scaleAndCenterInsideImage(imageList.get(3), cellSize);
        BufferedImage image5 = scaleAndCenterInsideImage(imageList.get(4), cellSize);
        BufferedImage image6 = scaleAndCenterInsideImage(imageList.get(5), cellSize);
        BufferedImage image7 = scaleAndCenterInsideImage(imageList.get(6), cellSize);
        BufferedImage image8 = scaleAndCenterInsideImage(imageList.get(7), cellSize);
        BufferedImage image9 = scaleAndCenterInsideImage(imageList.get(8), cellSize);

        // 第一行：3张图片均匀分布
        g.drawImage(image1, paddingSize, paddingSize, null);
        g.drawImage(image2, paddingSize + cellSize + itemMargin, paddingSize, null);
        g.drawImage(image3, paddingSize + cellSize * 2 + itemMargin * 2, paddingSize, null);

        // 第二行：3张图片均匀分布
        g.drawImage(image4, paddingSize, paddingSize + cellSize + itemMargin, null);
        g.drawImage(image5, paddingSize + cellSize + itemMargin, paddingSize + cellSize + itemMargin, null);
        g.drawImage(image6, paddingSize + cellSize * 2 + itemMargin * 2, paddingSize + cellSize + itemMargin, null);

        // 第三行：3张图片均匀分布
        g.drawImage(image7, paddingSize, paddingSize + cellSize * 2 + itemMargin * 2, null);
        g.drawImage(image8, paddingSize + cellSize + itemMargin, paddingSize + cellSize * 2 + itemMargin * 2, null);
        g.drawImage(image9, paddingSize + cellSize * 2 + itemMargin * 2, paddingSize + cellSize * 2 + itemMargin * 2, null);

        g.dispose();
        return outImage;
    }

    // 图片缩放并居中
    private BufferedImage scaleAndCenterInsideImage(BufferedImage image, int size) {
        int originalWidth = image.getWidth();
        int originalHeight = image.getHeight();

        float scale = Math.min((float) size / originalWidth, (float) size / originalHeight);
        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);

        BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaledImage.createGraphics();
        g.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();

        // 创建新的图片，大小为 size x size，背景为白色
        BufferedImage outImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        g = outImage.createGraphics();
        g.setColor(Color.WHITE);  // Background color
        g.fillRect(0, 0, size, size);
        int offsetX = (size - scaledWidth) / 2;
        int offsetY = (size - scaledHeight) / 2;
        g.drawImage(scaledImage, offsetX, offsetY, null);
        g.dispose();

        return outImage;
    }
}
