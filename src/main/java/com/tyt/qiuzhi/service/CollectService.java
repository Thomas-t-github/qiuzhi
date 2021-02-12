package com.tyt.qiuzhi.service;


import com.tyt.qiuzhi.dao.CollectDAO;
import com.tyt.qiuzhi.model.Collect;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectService {

    @Autowired
    CollectDAO collectDAO;

    public int addCollect(Collect collect){
        return collectDAO.addCollect(collect);
    }

    public List<Collect> selectByEntity(int entityId, int entityType){
        return collectDAO.selectByEntity(entityId,entityType);
    }

    public List<Collect> selectByUserId(int userId){
        return collectDAO.selectByUserId(userId);
    }

    public Collect selectById(int id){
        return collectDAO.selectById(id);
    }

    public int updateStatusById(int id, int status){
        return collectDAO.updateStatusById(id,status);
    }

    public List<Collect> getUserCollectStatus(int userId, int entityId, int entityType){
        return collectDAO.getUserCollectStatus(userId,entityId,entityType);
    }

    public int updateStatus(int entityId, int entityType, int status){
        return collectDAO.updateStatus(entityId,entityType,status);
    }

    public int getUserCollectsCount(int userId){
        return collectDAO.getUserCollectsCount(userId);
    }

}
