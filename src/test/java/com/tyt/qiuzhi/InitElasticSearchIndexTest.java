package com.tyt.qiuzhi;

import com.tyt.qiuzhi.dao.QuestionDAO;
import com.tyt.qiuzhi.dao.UserDAO;
import com.tyt.qiuzhi.elasticsearch.dao.SearchDAO;
import com.tyt.qiuzhi.elasticsearch.entity.SearchEntity;
import com.tyt.qiuzhi.model.Question;
import com.tyt.qiuzhi.model.User;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class InitElasticSearchIndexTest {

    @Autowired
    SearchDAO searchDAO;

    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Test
    public void initIndex(){

        List<Question> questions = questionDAO.selectAll();
        User user = null;
        for (Question question : questions) {
            user = userDAO.selectById(question.getUserId());

            SearchEntity entity = new SearchEntity();

            entity.setId(question.getId());
            entity.setNickName(user.getNickName());
            entity.setLabel(question.getLabel());
            entity.setCommentCount(question.getCommentCount());
            entity.setDescription(question.getDescription());
            entity.setHeadUrl(user.getHeadUrl());
            entity.setCreatedDate(question.getCreatedDate());
            entity.setReward(question.getReward());
            entity.setTitle(question.getTitle());
            entity.setUserId(question.getUserId());

            searchDAO.save(entity);
        }

    }

    @Test
    public void testSearch() throws IOException {

        List<SearchEntity> searchEntities = new ArrayList<>();

        //构建查询请求
        SearchRequest searchRequest = new SearchRequest("qiuzhi");
        //构建搜索构建对象
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //构建bool查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("title","测试提问重点本科"))
                .should(QueryBuilders.matchQuery("description","测试提问重点本科"))
                .should(QueryBuilders.matchQuery("label","测试提问重点本科"))
                .should(QueryBuilders.matchQuery("nickName","测试提问重点本科"));

        sourceBuilder.query(boolQuery)
                .from(0)//起始条数，即（当前页-1）*size的值
                .size(6)//每页展示条数
                //.postFilter(QueryBuilders.matchAllQuery())//过滤
                .sort("createdDate", SortOrder.DESC)//排序 按时间降序
                .highlighter(new HighlightBuilder().field("*").requireFieldMatch(false).preTags("<span style='color:red'>").postTags("</span>"));

        //设置查询请求对象
        searchRequest.types("question").source(sourceBuilder);
        //执行查询
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //处理查询响应
        System.out.println("符合条件的文档总数："+searchResponse.getHits().getTotalHits());
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
                entity.setTitle(highlightFields.get("description").getFragments()[0].toString());
            }
            if (highlightFields.containsKey("label")){
                entity.setTitle(highlightFields.get("label").getFragments()[0].toString());
            }
            if (highlightFields.containsKey("nickName")) {
                entity.setTitle(highlightFields.get("nickName").getFragments()[0].toString());
            }


            System.out.println(entity);

            searchEntities.add(entity);
        }

    }


    @Test
    public void testSave(){
        /*SearchEntity entity = new SearchEntity();
        entity.setId(10086);
        entity.setNickName("张三丰");
        entity.setLabel("分享");
        entity.setCommentCount(12);
        entity.setDescription("太极创始人");
        entity.setHeadUrl("http://localhost/ahdhgwjdg.jpg");
        entity.setCreatedDate(new Date());
        entity.setReward(20);
        entity.setTitle("一代武学大师");
        entity.setUserId(112);
        searchDAO.save(entity);*/
        System.out.println("创建索引成功");
    }

}
