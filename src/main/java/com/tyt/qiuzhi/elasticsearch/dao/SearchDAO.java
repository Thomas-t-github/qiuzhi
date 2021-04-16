package com.tyt.qiuzhi.elasticsearch.dao;

import com.tyt.qiuzhi.elasticsearch.entity.SearchEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchDAO extends ElasticsearchRepository<SearchEntity,Integer>{

}
