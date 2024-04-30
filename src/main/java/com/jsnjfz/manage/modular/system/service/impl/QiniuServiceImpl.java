package com.jsnjfz.manage.modular.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jsnjfz.manage.modular.system.config.QiniuConfig;
import com.jsnjfz.manage.modular.system.enums.AddressEnum;
import com.jsnjfz.manage.modular.system.service.IQiniuService;
import com.jsnjfz.manage.modular.system.utils.StringUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * @Author: quanyuhang
 * @Date: 2022-06-08-10:51
 * @Description:
 */
@Service
public class QiniuServiceImpl implements IQiniuService {

    @Autowired
    private QiniuConfig qiniuConfig;
    private Zone zone = Zone.autoZone();

    @Override
    public String uploadFile(MultipartFile file,Integer fileType) {
        return this.updloadFile(file,fileType);
    }
    public String updloadFile(MultipartFile file,Integer fileType){
        String url = null;
        // 获取文件的名称
        String fileName = file.getOriginalFilename();
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(zone);
        cfg.useHttpsDomains=false;
        UploadManager uploadManager = new UploadManager(cfg);
        String token = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey()).uploadToken(qiniuConfig.getBucketName());
        // 使用工具类根据上传文件生成唯一图片名称
        String imgName = StringUtil.getRandomImgName(fileName);
        //根据前端传值获取上传地址
        AddressEnum addressEnum = AddressEnum.getMessageEnum(fileType);
        imgName=addressEnum.getFileAddress()+imgName;
        if (!file.isEmpty()) {
          InputStream inputStream =null;
            try {
                inputStream=(InputStream) file.getInputStream();
                Response response = uploadManager.put(inputStream, imgName, token,null,null);
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                url=qiniuConfig.getUrl()+putRet.key;
                //System.out.println(putRet.hash);
                inputStream.close();
            } catch (QiniuException ex) {
                ex.printStackTrace();
                return "error";
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                return "error";
            }catch (IOException ioe){
                ioe.printStackTrace();
                return "error";
            }
            return url;
        }
        return "error";
    }


    @Override
    public String checkImageContent(String imageUrl) {
        //基础参数拼接
        String url = qiniuConfig.getQiniuCheckImgUrl();
        String host = qiniuConfig.getQiniuHostUrl();
        String body = "{ \"data\": { \"uri\": \""+imageUrl+"\" }, \"params\": { \"scenes\": [ \"pulp\", \"terror\", \"politician\" ] } }";
        String contentType = "application/json";
        String method = "POST";
        String accessKey= qiniuConfig.getAccessKey();
        String secretKey= qiniuConfig.getSecretKey();
        Auth auth = Auth.create(accessKey, secretKey);
        String qiniuToken = "Qiniu " + auth.signRequestV2(url, method, body.getBytes(), contentType);
        //头部部分
        StringMap header = new StringMap();
        header.put("Host",host);
        header.put("Authorization",qiniuToken);
        header.put("Content-Type", contentType);
        Configuration c = new Configuration(zone);
        Client client = new Client(c);
        try {
            Response response = client.post(url, body.getBytes(), header, contentType);
            JSONObject checkResult = JSON.parseObject(response.bodyString());
            return JSON.parseObject(checkResult.get("result").toString()).get("suggestion").toString();
        } catch (QiniuException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 检查图片的格式
     * @param multipartFiles
     * @return 格式正确的文件
     */
    @Override
    public  ArrayList<MultipartFile> checkImageFormat(ArrayList<MultipartFile> multipartFiles){
        ArrayList<MultipartFile> afterCheckFiles = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            ArrayList<String> imageSuffixList = new ArrayList<>();
            String suffix = getSuffix(multipartFile);
            //图片后缀
            imageSuffixList.add("jpg");
            imageSuffixList.add("png");
            imageSuffixList.add("avi");
            imageSuffixList.add("flv");
            imageSuffixList.add("mpg");
            imageSuffixList.add("mpeg");
            imageSuffixList.add("mpe");
            imageSuffixList.add("m1v");
            imageSuffixList.add("m2v");
            imageSuffixList.add("mpv2");
            imageSuffixList.add("mp2v");
            imageSuffixList.add("dat");
            imageSuffixList.add("ts");
            imageSuffixList.add("tp");
            imageSuffixList.add("tpr");
            imageSuffixList.add("pva");
            imageSuffixList.add("pss");
            imageSuffixList.add("mp4");
            imageSuffixList.add("m4v");
            imageSuffixList.add("m4p");
            imageSuffixList.add("m4b");
            imageSuffixList.add("3gp");
            imageSuffixList.add("3gpp");
            imageSuffixList.add("3g2");
            imageSuffixList.add("3gp2");
            imageSuffixList.add("ogg");
            imageSuffixList.add("mov");
            imageSuffixList.add("qt");
            imageSuffixList.add("amr");
            imageSuffixList.add("rm");
            imageSuffixList.add("ram");
            imageSuffixList.add("rmvb");
            imageSuffixList.add("rpm");
            //判断视频的后缀   MP4、MOV、WMV
            ArrayList<String> videoSuffixList = new ArrayList<>();
            videoSuffixList.add("mp4");
            videoSuffixList.add("mov");
            videoSuffixList.add("wmv");
            long size = multipartFile.getSize();
            if (suffix != null&&imageSuffixList.contains(suffix)&&multipartFile.getSize() / 1024 < 3072&&multipartFile.getSize() / 1024 > 0) {
                afterCheckFiles.add(multipartFile);
            }
        }
        return afterCheckFiles;
    }

    public static String getSuffix(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        //System.out.println(originalFilename);
        int dot = originalFilename.lastIndexOf('.');
        int fileNameLength = originalFilename.length();
        if ((dot > -1) && (dot < (originalFilename.length()))) {
            String suffix = originalFilename.substring(dot + 1, fileNameLength);
            return suffix.toLowerCase();
        }
        return null;
    }
    //删除文件
    public  String deleteFile(String filepath){
        String[] split = filepath.split("//");
        //构造一个带指定Region对象的配置类
        Configuration cfg = new Configuration(zone);
        System.out.println(split[2]);
        String key = split[2];
        Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            return bucketManager.delete(qiniuConfig.getBucketName(), key).toString();
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
        return "";
    }
    public static void main(String[] args) {
        String url="http://test.png";
        for (String s : url.split("//")) {
            System.out.println(s);
        }

//        String imageString = ImgUtil.getImageString("C:\\Users\\ASUS\\Documents\\WeChat Files\\wxid_vzk0z5ghy6q922\\FileStorage\\File\\2020-09\\20220608134731.png");
//        JSONObject jsonObject = checkImageContent("data:application/octet-stream;base64," + imageString);
//        System.out.println(JSON.parseObject(jsonObject.get("result").toString()).get("suggestion").toString());
//        System.out.println(jsonObject);
    }
}
