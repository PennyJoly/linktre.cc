package com.jsnjfz.manage.modular.system.utils;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;

/**
 * 图片处理工具类
 * @author chengkun
 * @date 2022/4/22 18:05
 */
@Slf4j
public class ImgUtil {

    /**
     * 将MultipartFile类转为BufferedImage对象
     * @param file 文件
     * @return -
     */
    public static BufferedImage toBufferedImage(MultipartFile file) {
        BufferedImage srcImage = null;
        try {
            FileInputStream in = (FileInputStream) file.getInputStream();
            srcImage = ImageIO.read(in);
        } catch (IOException e) {

        }
        return srcImage;
    }

    /**
     * 将BufferedImage转为InputStream
     * @param image 图片
     * @return -
     */
    public static InputStream bufferedImageToInputStream(BufferedImage image){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
            InputStream input = new ByteArrayInputStream(os.toByteArray());
            return input;
        } catch (IOException e) {
            log.error("提示:",e);
        }
        return null;
    }

    /**
     * 获取封装得MultipartFile
     *
     * @param inputStream inputStream
     * @param fileName    fileName
     * @return MultipartFile
     */
    public static MultipartFile getMultipartFile(InputStream inputStream, String fileName) {
        FileItem fileItem = createFileItem(inputStream, fileName);
        return new CommonsMultipartFile(fileItem);
    }

    /**
     * FileItem类对象创建
     *
     * @param inputStream inputStream
     * @param fileName    fileName
     * @return FileItem
     */
    public static FileItem createFileItem(InputStream inputStream, String fileName) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        String textFieldName = "file";
        FileItem item = factory.createItem(textFieldName, MediaType.MULTIPART_FORM_DATA_VALUE, true, fileName);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        OutputStream os = null;
        //使用输出流输出输入流的字节
        try {
            os = item.getOutputStream();
            while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            inputStream.close();
        } catch (IOException e) {
            log.error("Stream copy exception", e);
            throw new IllegalArgumentException("文件上传失败");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("Stream close exception", e);

                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("Stream close exception", e);
                }
            }
        }

        return item;
    }

    /**
     * 判断文件是否为图片
     */
    public static boolean isPicture(String imgName) {
        boolean flag = false;
        if (StringUtils.isBlank(imgName)) {
            return false;
        }
        String[] split = imgName.split("\\.");
        String subName = split[1];
        String[] arr = {"bmp", "dib", "gif", "jfif", "jpe", "jpeg", "jpg", "png", "tif", "tiff", "ico"};
        for (String item : arr) {
            if (item.equals(subName)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 将图片转为base64字符串
     * @param imageFile
     * @return
     */
    public static String getImageString(String imageFile){
        InputStream is = null;
        try {
            byte[] data = null;
            is = new FileInputStream(new File(imageFile));
            data = new byte[is.available()];
            is.read(data);
            return new String(Base64.encodeBase64(data));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                    is = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * 将图片转为base64字符串
     * @param imageFile
     * @return
     */
    public static String getImageString(MultipartFile imageFile){

        InputStream is = null;
        try {
            byte[] data = null;
            byte [] byteArr=imageFile.getBytes();
            is = new ByteArrayInputStream(byteArr);
            data = new byte[is.available()];
            is.read(data);
            return new String(Base64.encodeBase64(data));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                    is = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * 功能描述:将文件压缩到指定大小并上传
     *
     *
     * @Param desFileSize: 指定文件大小（单位为kb）
     * @Param accuracy:精度（一般为0.5f）
     * @Param mutipartfile：要压缩得图片
     * @return: File
     * @auther: qyh
     * @date: 2022/7/3 19:17
     */
    public static MultipartFile commpressPicCycle( long desFileSize, double accuracy,MultipartFile mutipartfile) throws IOException{
        String originalFilename = mutipartfile.getOriginalFilename();
        String[] filename = originalFilename.split("\\.");
        File fileConver = convertFile(mutipartfile);
        long fileSize = FileUtils.sizeOf(fileConver);
        InputStream fileInputStream = new FileInputStream(fileConver);
        // 判断图片大小是否小于指定图片大小
        if(fileSize <= desFileSize * 1024){

            return new MockMultipartFile("mutilConver."+filename[1],mutipartfile.getOriginalFilename(),"text/plain",fileInputStream);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        byte[] b=new byte[1024];
        int n;
        while ((n=fileInputStream.read(b))!=-1){
            bos.write(b,0,n);
        }
        byte[] bytes = bos.toByteArray();
        //计算宽高
        BufferedImage bim = ImageIO.read(new ByteArrayInputStream(bytes));
        int imgWidth = bim.getWidth();
        int imgHeight = bim.getHeight();
        int desWidth = new BigDecimal(imgWidth).multiply( new BigDecimal(accuracy)).intValue();
        int desHeight = new BigDecimal(imgHeight).multiply( new BigDecimal(accuracy)).intValue();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); //字节输出流（写入到内存）
        //Thumbnails.of(new ByteArrayInputStream(bytes)).size(desWidth, desHeight).outputQuality(accuracy).toOutputStream(baos);
        Thumbnails.of(fileConver).size(desWidth, desHeight).outputQuality(accuracy).toFile(fileConver);
        System.out.println(fileConver.length()+"==========================");
        InputStream inputStream = new FileInputStream(fileConver);
        mutipartfile=new MockMultipartFile("mutilConver."+filename[1],mutipartfile.getOriginalFilename(),"text/plain",inputStream);
        System.out.println(mutipartfile.getSize()+"=====================");
        fileInputStream.close();
        bos.close();
        //如果不满足要求,递归直至满足要求
        return commpressPicCycle(desFileSize,accuracy, mutipartfile);
    }


    /**
     * 將MutipartFile轉file
     * @param multipartFile
     * @return
     */
    public static File convertFile(MultipartFile multipartFile) {
        File file = null;
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String[] filename = originalFilename.split("\\.");
            if (filename[0].length()<3){
                filename[0]=filename[0]+"plus";
            }
            file = File.createTempFile(filename[0], "."+filename[1]);
            multipartFile.transferTo(file);
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
