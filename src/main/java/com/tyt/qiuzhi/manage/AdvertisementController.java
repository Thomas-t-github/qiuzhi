package com.tyt.qiuzhi.manage;

import cn.hutool.core.date.DateUnit;
import com.alibaba.fastjson.JSONObject;
import com.tyt.qiuzhi.model.Advertisement;
import com.tyt.qiuzhi.service.QiniuService;
import com.tyt.qiuzhi.util.JedisAdapter;
import com.tyt.qiuzhi.util.QiuzhiUtils;
import com.tyt.qiuzhi.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/manage")
public class AdvertisementController {
    private static final Logger logger = LoggerFactory.getLogger(AdvertisementController.class);

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    QiniuService qiniuService;

    @RequestMapping("/toSetAdv")
    public String toSetAdv(){
        return "manage/setAdv";
    }

    @RequestMapping(value = "/setAdv",method = RequestMethod.POST,produces={"application/json;charset=UTF-8"})
    @ResponseBody
    public String setAdv(@RequestParam("title") String title,
                         @RequestParam("siteLink") String siteLink,
                         @RequestParam("startDate") String startDate,
                         @RequestParam("endDate") String endDate,
                         @RequestParam(value = "file",defaultValue = "") String file){

        final List<String> images = jedisAdapter.lrange(RedisKeyUtil.getBizAdvertisementImageKey(), 0, -1);
        if (images == null && images.size() == 0){
            return QiuzhiUtils.getJSONString(1,"未上传图片");
        }

        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Advertisement advertisement = new Advertisement();

            advertisement.setTitle(title);
            advertisement.setSiteLink(siteLink);
            advertisement.setStartDate(dateFormat.parse(endDate));
            advertisement.setEndDate(dateFormat.parse(startDate));
            advertisement.setImageUrl(images);

            int time = (int) (dateFormat.parse(endDate).getTime()-dateFormat.parse(startDate).getTime());
            jedisAdapter.setex(RedisKeyUtil.getBizAdvertisementKey(),
                    time < 0 ? Integer.MAX_VALUE : time,
                    JSONObject.toJSONString(advertisement));

            jedisAdapter.del(RedisKeyUtil.getBizAdvertisementImageKey());
            return QiuzhiUtils.getJSONString(0,"设置广告成功");
        } catch (ParseException e) {
            logger.error("日期转换异常："+e.getMessage());
        }

        return QiuzhiUtils.getJSONString(1,"设置广告失败");
    }

    @RequestMapping("/uploadImage")
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile[] file){

        for (MultipartFile multipartFile : file) {
            try {
                String imageUrl = qiniuService.saveImage(multipartFile);
                if (imageUrl == null){
                    jedisAdapter.del(RedisKeyUtil.getBizAdvertisementImageKey());
                    return QiuzhiUtils.getJSONString(1,multipartFile.getOriginalFilename()+"图片上传失败");
                }else {
                    jedisAdapter.lpush(RedisKeyUtil.getBizAdvertisementImageKey(),imageUrl);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return QiuzhiUtils.getJSONString(0,"图片上传成功");
    }


}
