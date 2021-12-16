package com.tyt.qiuzhi.service;

import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.tyt.qiuzhi.util.QiuzhiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class QiniuService {

    private static final Logger logger = LoggerFactory.getLogger(QiniuService.class);

    @Value("${qiniu.server.accessKey}")
    private String accessKey;

    @Value("${qiniu.server.secretKey}")
    private String secretKey;

    @Value("${qiniu.server.bucket}")
    private String bucket;

    public String saveImage(MultipartFile file) throws IOException {

        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region2());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传

        /*
        //采用配置文件的方式进行配置
        String accessKey = "******";
        String secretKey = "******";
        String bucket = "******";
        */

        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
        //String localFilePath = "/home/qiniu/test.png";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        //String key = null;
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {

            int dotPos = file.getOriginalFilename().lastIndexOf(".");
            if (dotPos < 0){
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();

            if (!QiuzhiUtils.isFileAllowed(fileExt)){
                return null;
            }

            String fileName = UUID.randomUUID().toString().replaceAll("-","")+"."+fileExt;


            Response response = uploadManager.put(file.getBytes(),fileName,upToken);
            //解析上传成功的结果
            //DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            //System.out.println(putRet.key);
            //System.out.println(putRet.hash);
            //System.out.println(response.bodyString());
            if (response.isOK() && response.isJson()){
                String key = JSONObject.parseObject(response.bodyString()).get("key").toString();
                return QiuzhiUtils.QINIU_DOMAIN_PREFIX+key;
            }else {
                logger.error("七牛存储异常："+response.bodyString());
                return null;
            }

        } catch (QiniuException ex) {
            logger.error("七牛存储异常"+ex.getMessage());
            return null;
        }

    }

}
