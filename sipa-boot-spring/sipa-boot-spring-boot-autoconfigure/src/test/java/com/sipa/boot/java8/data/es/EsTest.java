package com.sipa.boot.java8.data.es;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.io.IOException;
import java.time.LocalDateTime;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.utils.TimeUtils;
import com.sipa.boot.java8.common.utils.UuidUtils;
import com.sipa.boot.java8.data.es.client.ElasticRestClient;
import com.sipa.boot.java8.data.es.config.ElasticSearchAutoConfiguration;
import com.sipa.boot.java8.data.es.entity.Demo;
import com.sipa.boot.java8.data.es.entity.DkLog;
import com.sipa.boot.java8.data.es.entity.DkLogContext;
import com.sipa.boot.java8.data.es.entity.TimeSeriesDemo;

/**
 * @author caszhou
 * @date 2021/10/16
 */
@ActiveProfiles("es")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ElasticSearchAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class})
public class EsTest {
    private static final Log LOGGER = LogFactory.get(EsTest.class);

    private static final String INDEX = "demo";

    private static final String TIME_SERIES_INDEX = "time-series-demo";

    private static final String ID = "1";

    private static final String TIME_SERIES_ID = "ts_id_";

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    @Ignore
    public void testClient() {
        Assert.assertNotNull(ElasticRestClient.getQueryClient());
        Assert.assertNotNull(ElasticRestClient.getWriteClient());

        Assert.assertNotNull(restHighLevelClient);
        Assert.assertNotNull(elasticsearchOperations);
        Assert.assertNotNull(elasticsearchRestTemplate);

        Assert.assertEquals(elasticsearchRestTemplate, elasticsearchOperations);
    }

    @Before
    @Ignore
    public void prepare() {
        this.elasticsearchRestTemplate.save(
            DkLog.DkLogBuilder.aDkLog()
                .withId(UuidUtils.generator())
                .withSource("DK")
                .withType("钥匙")
                .withContext(DkLogContext.DkLogContextBuilder.aDkLogContext()
                    .withVin("12345678901234567")
                    .withVid("vid")
                    .withIccid("12345678901234567890")
                    .withDeviceId("deviceId")
                    .build())
                .withContent("删除钥匙")
                .withCreateTime(TimeUtils.fdt2ldt("2021-10-03 10:00:00"))
                .build(),
            DkLog.DkLogBuilder.aDkLog()
                .withId(UuidUtils.generator())
                .withSource("APP")
                .withType("钥匙")
                .withContext(DkLogContext.DkLogContextBuilder.aDkLogContext()
                    .withVin("12345678901234567")
                    .withVid("vid")
                    .withMobile("13587756256")
                    .withUserId("userId")
                    .build())
                .withContent("同步钥匙")
                .withCreateTime(TimeUtils.fdt2ldt("2021-10-04 10:00:00"))
                .build(),
            DkLog.DkLogBuilder.aDkLog()
                .withId(UuidUtils.generator())
                .withSource("DK")
                .withType("钥匙")
                .withContext(DkLogContext.DkLogContextBuilder.aDkLogContext()
                    .withVin("12345678901234567")
                    .withVid("vid")
                    .withMobile("13862123242")
                    .withUserId("platformUserId")
                    .build())
                .withContent("预销毁")
                .withCreateTime(TimeUtils.fdt2ldt("2021-10-05 10:00:00"))
                .build(),
            DkLog.DkLogBuilder.aDkLog()
                .withId(UuidUtils.generator())
                .withSource("控制器")
                .withType("请求")
                .withContext(DkLogContext.DkLogContextBuilder.aDkLogContext()
                    .withVin("12345678901234567")
                    .withVid("vid")
                    .withIccid("12345678901234567890")
                    .withDeviceId("deviceId")
                    .build())
                .withContent("注册")
                .withCreateTime(TimeUtils.fdt2ldt("2021-10-02 10:00:00"))
                .build(),
            DkLog.DkLogBuilder.aDkLog()
                .withId(UuidUtils.generator())
                .withSource("APP")
                .withType("钥匙")
                .withContext(DkLogContext.DkLogContextBuilder.aDkLogContext()
                    .withVin("12345678901234567")
                    .withVid("vid")
                    .withMobile("13587756256")
                    .withUserId("userId")
                    .build())
                .withContent("创建钥匙")
                .withCreateTime(TimeUtils.fdt2ldt("2021-10-01 10:00:00"))
                .build());
    }

    @Test
    @Ignore
    public void testFullTextSearch() {
        SearchHits<DkLog> searchHits = this.elasticsearchOperations
            .search(new NativeSearchQueryBuilder().withQuery(matchQuery("content", "注册钥匙")).build(), DkLog.class);
        Assert.assertEquals(40, searchHits.getSearchHits().size());
    }

    @Test
    @Ignore
    public void testBoolSearch() {
        QueryBuilder queryBuilder = boolQuery().must(matchQuery("type", "钥匙"))
            .must(rangeQuery("createTime").from(TimeUtils.fdt2ldt("2021-10-02 10:00:00"))
                .to(TimeUtils.fdt2ldt("2021-10-05 10:00:00")));
        FieldSortBuilder sortBuilder = new FieldSortBuilder("createTime").order(SortOrder.DESC);
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(queryBuilder)
            .withSort(sortBuilder)
            .withPageable(PageRequest.of(1, 10))
            .build();
        SearchHits<DkLog> searchHits = this.elasticsearchOperations.search(nativeSearchQuery, DkLog.class);
        Assert.assertEquals(10, searchHits.getSearchHits().size());
    }

    @Test
    @Ignore
    public void testDataStreamBySpringData() {
        long ts = System.currentTimeMillis();
        long ts2 = ts + 1;

        Iterable<TimeSeriesDemo> demos = this.elasticsearchRestTemplate.save(
            TimeSeriesDemo.TimeSeriesDemoBuilder.aTimeSeriesDemo()
                .withId(TIME_SERIES_ID + ts)
                .withTimestamp(ts)
                .withSpeed(100)
                .build(),
            TimeSeriesDemo.TimeSeriesDemoBuilder.aTimeSeriesDemo()
                .withId(TIME_SERIES_ID + ts2)
                .withTimestamp(ts2)
                .withSpeed(50)
                .build());
        Assert.assertNotNull(demos);
    }

    @Test
    @Ignore
    public void testDataStream() {
        long ts = System.currentTimeMillis();
        long ts2 = ts + 1;

        IndexRequest request1 =
            new IndexRequest(TIME_SERIES_INDEX).id(TIME_SERIES_ID + ts).source("@timestamp", ts, "speed", 100);

        IndexRequest request2 =
            new IndexRequest(TIME_SERIES_INDEX).id(TIME_SERIES_ID + ts2).source("@timestamp", ts2, "speed", 50);

        BulkRequest request = new BulkRequest();
        request.add(request1, request2);
        try {
            BulkResponse bulkResponse = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            Assert.assertFalse(bulkResponse.hasFailures());
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    @Test
    @Ignore
    public void testDocBySpringData() {
        testDocIndexBySpringData();
        testDocGetBySpringData();
        testDocUpdateBySpringData();
        testDocDeleteBySpringData();
    }

    public void testDocDeleteBySpringData() {
        String result = this.elasticsearchRestTemplate.delete(ID, IndexCoordinates.of(INDEX));
        LOGGER.info(result);
    }

    public void testDocUpdateBySpringData() {
        Demo demo = this.elasticsearchRestTemplate.save(Demo.DemoBuilder.aDemo()
            .withId(ID)
            .withUser("xiajiezhou2")
            .withPostDate(LocalDateTime.now())
            .withMessage("Test for es2")
            .build());
        LOGGER.info(demo.toString());
    }

    public void testDocGetBySpringData() {
        Demo demo = this.elasticsearchRestTemplate.get(ID, Demo.class);
        Assert.assertNotNull(demo);
        LOGGER.info(demo.toString());
    }

    public void testDocIndexBySpringData() {
        Demo demo = this.elasticsearchRestTemplate.save(Demo.DemoBuilder.aDemo()
            .withId(ID)
            .withUser("xiajiezhou")
            .withPostDate(LocalDateTime.now())
            .withMessage("Test for es")
            .build());
        LOGGER.info(demo.toString());
    }

    @Test
    @Ignore
    public void testDoc() {
        testDocIndex();
        testDocGet();
        testDocUpdate();
        testDDelete();
    }

    public void testDDelete() {
        DeleteRequest request = new DeleteRequest(INDEX, ID);
        try {
            DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
            LOGGER.info(response.toString());
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    public void testDocUpdate() {
        UpdateRequest request = new UpdateRequest(INDEX, ID).doc("user", "caszhou");
        try {
            UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
            LOGGER.info(response.toString());

            GetResult result = response.getGetResult();
            if (null != result && result.isExists()) {
                String sourceAsString = result.sourceAsString();
                LOGGER.info(sourceAsString);
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    public void testDocGet() {
        GetRequest request = new GetRequest(INDEX, ID);
        try {
            GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
            LOGGER.info(response.toString());
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    public void testDocIndex() {
        IndexRequest request = new IndexRequest(INDEX).id(ID)
            .source("user", "kimchy", "postDate", LocalDateTime.now(), "message", "trying out Elasticsearch")
            .opType(DocWriteRequest.OpType.CREATE);
        try {
            IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            LOGGER.info(response.toString());
        } catch (ElasticsearchException e) {
            LOGGER.error(e);
            Assert.assertSame(e.status(), RestStatus.CONFLICT);
        } catch (IOException e) {
            LOGGER.error(e);
            Assert.fail();
        }
    }
}
