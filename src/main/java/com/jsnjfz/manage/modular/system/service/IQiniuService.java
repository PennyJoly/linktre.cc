package com.jsnjfz.manage.modular.system.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

public interface IQiniuService {
    /**
     * 上传文件到七牛云
     * @param file 文件
     * @param fileType 文件类型
     * @return -
     */
    String uploadFile(MultipartFile file,Integer fileType);

    /**
     * 检查图片内容是否符合规定
     * @param imageUrl 图片的url地址或者图片Base64编码（Base64编码请求时应在开头加上data:application/octet-stream;base64,）
     * @return JSONObject
     */
    String checkImageContent(String imageUrl);

    /**
     * 检查图片格式
     * @param multipartFiles
     * @return
     */
    ArrayList<MultipartFile> checkImageFormat(ArrayList<MultipartFile> multipartFiles);

    /**
     * 文件删除
     * @param filepath 文件路径
     * @return
     */
    String deleteFile(String filepath);
}
