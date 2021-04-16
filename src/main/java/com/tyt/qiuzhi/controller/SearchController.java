package com.tyt.qiuzhi.controller;

import com.tyt.qiuzhi.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Map;

@Controller
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);


    private static final String SEARCH_HLPRE = "<span style=\"color: red\">";
    private static final String SEARCH_HLPOS = "</span>";

    @Autowired
    SearchService searchService;

    @RequestMapping(path = {"/search"}, method = {RequestMethod.GET})
    public String search(Model model, @RequestParam("q") String keyword,
                         @RequestParam(value = "offset", defaultValue = "0") int offset,
                         @RequestParam(value = "count", defaultValue = "20") int count){

        if ("".equals(keyword)){
            return "redirect:/";
        }
        Map<String, Object> search = null;
        try {
            search = searchService.search(keyword, offset, count, SEARCH_HLPRE, SEARCH_HLPOS);
            model.addAttribute("searchResult",search.get("searchEntities"));
            model.addAttribute("totalHits",search.get("totalHits"));
        } catch (IOException e) {
            logger.error("ES查询异常："+e.getMessage());
        }

        return "search";
    }

}
