package com.tyt.qiuzhi.service;

import com.tyt.qiuzhi.elasticsearch.dao.SearchDAO;
import com.tyt.qiuzhi.elasticsearch.entity.SearchEntity;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SearchService {

    private static final String SEARCH_INDEX = "qiuzhi";
    private static final String SEARCH_TYPE = "question";

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    SearchDAO searchDAO;

    public Map<String, Object> search(String keyword,int offset,int count,String hlPre,String hlPos) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("keyword",keyword);
        List<SearchEntity> searchEntities = new ArrayList<>();

        //构建查询请求
        SearchRequest searchRequest = new SearchRequest(SEARCH_INDEX);
        //构建搜索构建对象
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //构建bool查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("title",keyword))
                .should(QueryBuilders.matchQuery("description",keyword))
                .should(QueryBuilders.matchQuery("label",keyword))
                .should(QueryBuilders.matchQuery("nickName",keyword));

        sourceBuilder.query(boolQuery)
                .from(offset)//起始条数，即（当前页-1）*size的值
                .size(count)//每页展示条数
                //.postFilter(QueryBuilders.matchAllQuery())//过滤
                .sort("createdDate", SortOrder.DESC)//排序 按时间降序
                .highlighter(new HighlightBuilder().field("*").requireFieldMatch(false).preTags(hlPre).postTags(hlPos));

        //设置查询请求对象
        searchRequest.types(SEARCH_TYPE).source(sourceBuilder);
        //执行查询
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //处理查询响应
        result.put("totalHits",searchResponse.getHits().getTotalHits());
        SearchHit[] hits = searchResponse.getHits().getHits();

        for (SearchHit hit : hits) {
            SearchEntity entity = new SearchEntity();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            entity.setId(Integer.parseInt(sourceAsMap.get("id").toString()));
            entity.setTitle(sourceAsMap.get("title").toString());
            entity.setCreatedDate(new Date(Long.parseLong(sourceAsMap.get("createdDate").toString())));
            entity.setHeadUrl(sourceAsMap.get("headUrl").toString());
            entity.setDescription(sourceAsMap.get("description").toString());
            entity.setLabel(sourceAsMap.get("label").toString());
            entity.setNickName(sourceAsMap.get("nickName").toString());
            entity.setCommentCount(Integer.parseInt(sourceAsMap.get("commentCount").toString()));
            entity.setReward(Integer.parseInt(sourceAsMap.get("reward").toString()));
            entity.setUserId(Integer.parseInt(sourceAsMap.get("userId").toString()));

            Map<String, HighlightField> highlightFields = hit.getHighlightFields();

            if (highlightFields.containsKey("title")){
                entity.setTitle(highlightFields.get("title").getFragments()[0].toString());
            }
            if (highlightFields.containsKey("description")){
                entity.setDescription(highlightFields.get("description").getFragments()[0].toString());
            }
            if (highlightFields.containsKey("label")){
                entity.setLabel(highlightFields.get("label").getFragments()[0].toString());
            }
            if (highlightFields.containsKey("nickName")) {
                entity.setNickName(highlightFields.get("nickName").getFragments()[0].toString());
            }
            searchEntities.add(entity);
        }

        result.put("searchEntities",searchEntities);

        return result;
    }

    public SearchEntity index(SearchEntity searchEntity){
        return searchDAO.save(searchEntity);
    }


}
