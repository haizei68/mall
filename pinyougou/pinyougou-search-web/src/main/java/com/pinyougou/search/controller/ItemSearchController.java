package com.pinyougou.search.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/item")
public class ItemSearchController {
    @Reference
    private ItemSearchService itemSearchService;

    /**
     * 搜索实现
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search")
    public Map<String,Object> search(@RequestBody(required = false) Map searchMap){
        return itemSearchService.search(searchMap);
    }


}
