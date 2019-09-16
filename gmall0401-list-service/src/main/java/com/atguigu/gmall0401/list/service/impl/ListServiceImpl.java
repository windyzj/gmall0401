package com.atguigu.gmall0401.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall0401.bean.SkuLsInfo;
import com.atguigu.gmall0401.bean.SkuLsParams;
import com.atguigu.gmall0401.bean.SkuLsResult;
import com.atguigu.gmall0401.service.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;


@Service
public class ListServiceImpl implements ListService {

    @Autowired
    JestClient jestClient;

    public void saveSkuLsInfo(SkuLsInfo skuLsInfo){

        Index.Builder indexBuilder = new Index.Builder(skuLsInfo);
        indexBuilder.index("gmall0401_sku_info").type("_doc").id(skuLsInfo.getId());
        Index index = indexBuilder.build();
        try {
            jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SkuLsResult getSkuLsInfoList(SkuLsParams skuLsParams) {
        String query="{\n" +
                "  \"query\": {\n" +
                "    \"bool\": {\n" +
                "      \"must\": [\n" +
                "        {\"match\": {\n" +
                "          \"skuName\": \""+skuLsParams.getKeyword()+"\"\n" +
                "        }}\n" +
                "      ],\n" +
                "      \"filter\": [ \n" +
                "          {\"term\": {\n" +
                "            \"catalog3Id\": \"61\"\n" +
                "          }},\n" +
                "          {\"term\": {\n" +
                "            \"skuAttrValueList.valueId\": \"83\"\n" +
                "          }},\n" +
                "          {\"term\": {\n" +
                "            \"skuAttrValueList.valueId\": \"154\"\n" +
                "          }},\n" +
                "          \n" +
                "           {\"range\": {\n" +
                "            \"price\": {\"gte\": 3200}\n" +
                "           }}\n" +
                "\n" +
                "        ]\n" +
                "      }\n" +
                "    },\n" +
                "    \"from\": 0\n" +
                "    , \"size\": 2\n" +
                "    , \"highlight\": {\"fields\": {\"skuName\": {\"pre_tags\": \"<span style='color:red' >\",\"post_tags\": \"</span>\"}}  }\n" +
                "  \n" +
                "    ,\n" +
                "    \"aggs\": {\n" +
                "      \"groupby_valueid\": {\n" +
                "        \"terms\": {\n" +
                "          \"field\": \"skuAttrValueList.valueId\",\n" +
                "          \"size\": 1000\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"sort\": [\n" +
                "      {\n" +
                "        \"hotScore\": {\n" +
                "          \"order\": \"desc\"\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "}";

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //商品名称查询搜索
        boolQueryBuilder.must(new MatchQueryBuilder("skuName",skuLsParams.getKeyword()));

        //三级分类过滤
        boolQueryBuilder.filter(new TermQueryBuilder("catalog3Id",skuLsParams.getCatalog3Id()));
        //平台属性过滤
        String[] valueIds = skuLsParams.getValueId();
        for (int i = 0; i < valueIds.length; i++) {
            String valueid = valueIds[i];
            boolQueryBuilder.filter(new TermQueryBuilder("skuAttrValueList.valueId",valueid));
        }
       //  价格
      //    boolQueryBuilder.filter(new RangeQueryBuilder("price").gte("3200"));

    searchSourceBuilder.query(boolQueryBuilder);
        // 起始行
    searchSourceBuilder.from((skuLsParams.getPageNo()-1)*skuLsParams.getPageSize());
    // 页行数
        searchSourceBuilder.size(skuLsParams.getPageSize());
     //高亮
        searchSourceBuilder.highlight(new HighlightBuilder().field("skuName").preTags("<span style='color:red'>" ).postTags("</span>"));
    //聚合
        TermsBuilder aggsBuilder = AggregationBuilders.terms("groupby_value_id").field("skuAttrValueList.valueId").size(1000);
        searchSourceBuilder.aggregation(aggsBuilder);
        //排序
        searchSourceBuilder.sort("hotScore", SortOrder.DESC);

        System.out.println(searchSourceBuilder.toString());

        Search.Builder searchBuilder = new Search.Builder(searchSourceBuilder.toString());
        Search search = searchBuilder.addIndex("gmall0401_sku_info").addType("_doc").build();
        try {
            jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
