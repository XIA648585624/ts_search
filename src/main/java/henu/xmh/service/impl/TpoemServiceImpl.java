package henu.xmh.service.impl;

import henu.xmh.dao.TPoemMapper;
import henu.xmh.dao.TpoemRepository;
import henu.xmh.pojo.HotSearch;
import henu.xmh.pojo.TPoem;
import henu.xmh.pojo.TPoemExample;
import henu.xmh.service.TpoemService;
import henu.xmh.util.ApplicationContextUtil;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class TpoemServiceImpl implements TpoemService {
    @Autowired
    private TPoemMapper tPoemMapper;
    @Autowired
    private TpoemRepository tpoemRepository;//es简单操作对象
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;//复杂操作的es操作对象

    /**
     * 分页查询的业务方法
     *
     * @param currentPageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<TPoem> findAllForAge(Integer currentPageNum, Integer pageSize) {
        Integer beginValue = (currentPageNum - 1) * pageSize;
        TPoemExample example = new TPoemExample();
        example.setBeginValue(beginValue);
        example.setPageSize(pageSize);
        return tPoemMapper.selectByExample(example);
    }

    /**
     * 获取总的诗词记录数
     *
     * @param pageSize
     * @return
     */
    @Override
    public Integer getCountForAll(Integer pageSize) {
        TPoemExample example = new TPoemExample();
        return tPoemMapper.countByExample(example);
    }

    /**
     * 根据条件查询（为jqgird定制的查询），并且分页
     *
     * @param searchField
     * @param searchString
     * @param searchOper
     * @param currentPageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<TPoem> findBySerachFiledString(String searchField, String searchString, String searchOper, Integer currentPageNum, Integer pageSize) {
        Integer beginValue = (currentPageNum - 1) * pageSize;
        TPoemExample example = new TPoemExample();
        example.setBeginValue(beginValue);
        example.setPageSize(pageSize);
        TPoemExample.Criteria criteria = example.createCriteria();
        doCriteria(criteria, searchField, searchString, searchOper);//设置条件相关判断
        return tPoemMapper.selectByExample(example);
    }

    /**
     * 获取根据条件查询的记录数
     *
     * @param searchField
     * @param searchString
     * @param searchOper
     * @return
     */
    @Override
    public Integer getCountForSerachFiledString(String searchField, String searchString, String searchOper) {
        TPoemExample example = new TPoemExample();
        TPoemExample.Criteria criteria = example.createCriteria();
        doCriteria(criteria, searchField, searchString, searchOper);//设置条件相关判断
        return tPoemMapper.countByExample(example);
    }

    /**
     * 添加一条诗词记录
     *
     * @param tPoem
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void add(TPoem tPoem) {
        tPoem.setId(UUID.randomUUID().toString());
        tPoemMapper.insertSelective(tPoem);
    }

    /**
     * 修改一条诗词
     *
     * @param tPoem
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void alter(TPoem tPoem) {
        tPoemMapper.updateByPrimaryKeySelective(tPoem);
    }

    /**
     * 删除一条诗词记录
     *
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void drop(String id) {
        tPoemMapper.deleteByPrimaryKey(id);
    }

    /**
     * 查找一条诗词记录
     *
     * @param id
     * @return
     */
    @Override
    public TPoem findOne(String id) {
        return tPoemMapper.selectByPrimaryKey(id);
    }

    /**
     * jgird的前台的修改相关的业务方法
     *
     * @param tPoem 诗歌的详细信息
     * @param oper  操作
     */
    @Override
    public void alterForJg(TPoem tPoem, String oper) {
        System.out.println(oper + ":" + tPoem);
        if ("add".equals(oper)) {
            add(tPoem);
        } else if ("del".equals(oper)) {
            drop(tPoem.getId());
        } else {//修改
            alter(tPoem);
        }
    }

    /**
     * 判断条件查询的搜索条件
     *
     * @param criteria     条件尺
     * @param searchField  属性名
     * @param searchString 属性值
     * @param searchOper   条件
     */
    private void doCriteria(TPoemExample.Criteria criteria, String searchField, String searchString, String searchOper) {
        //条件判断
        if ("name".equals(searchField)) {//诗词名
            criteria.andNameLike("%" + searchString + "%");
        } else if ("author".equals(searchField)) {//作者
            criteria.andAuthorLike("%" + searchString + "%");
        } else if ("type".equals(searchField)) {//类型
            criteria.andTypeLike("%" + searchString + "%");
        } else if ("content".equals(searchField)) {//内容
            criteria.andContentLike("%" + searchString + "%");
        } else if ("origin".equals(searchField)) {//来源
            criteria.andOriginLike("%" + searchString + "%");
        } else if ("authordes".equals(searchField)) {//作者简介
            criteria.andAuthordesLike("%" + searchString + "%");
        }
    }

    /**
     * 做一次索引，利用分页查询，分批操作（一次50条）
     */
    //全部做一次索引
    @Override
    public void savrEs() {
        Integer countForAll = getCountForAll(50);//总的记录数
        countForAll = (countForAll % 50 == 0) ? (countForAll / 50) : (countForAll / 50 + 1);
        System.out.println("=======================一共" + countForAll + "页索引：========================");
        for (int i = 0; i < countForAll; i++) {//建立次数50个记录数的页数,一次建立50条索引
            TPoemExample example = new TPoemExample();
            example.setBeginValue(i * 50);
            example.setPageSize(50);
            List<TPoem> tPoems = tPoemMapper.selectByExample(example);//返回第i+1页的50条数据
            tpoemRepository.saveAll(tPoems);
            System.out.println((i * 50 + 1) + "---->" + (i + 1) * 50 + "记录创建索引成功!共" + countForAll + "条数据需要建立索引");
        }
        System.out.println("========================索引建立完成！=======================================");
       /* List<TPoem> tPoems = tPoemMapper.selectByExample(new TPoemExample());
        tPoems.forEach(p->{
            tpoemRepository.save(p);
            System.out.println(p.getName()+"创建索引成功！");
        });*/
    }

    /**
     * 删除所有查询
     */
    //删除所有索引
    @Override
    public void delEs() {
        tpoemRepository.deleteAll();
    }

    //添加热词到redis的zset结构中
    @Override
    public void addSearchStringToRedis(String searchString) {
        RedisTemplate redisTemplate = (RedisTemplate) ApplicationContextUtil.getBean("redisTemplate", RedisTemplate.class);
        ZSetOperations<String, Object> stringObjectZSetOperations = redisTemplate.opsForZSet();
        //查询ts下所有的热词
        Set<Object> ts = stringObjectZSetOperations.range("ts", 0, -1);
        Boolean exit = false;
        for (Object s : ts) {
            if (s.toString().equals(searchString)) {//如果存在
                stringObjectZSetOperations.incrementScore("ts", searchString, 1);
                exit = true;
            }
        }
        if (!exit) stringObjectZSetOperations.add("ts", searchString, 1);

        /*stringObjectZSetOperations.add("ts",searchString,);*/
    }

    /**
     * 查找热词胖排行榜
     *
     * @return
     */
    @Override
    public List<HotSearch> findHotSearch() {
        System.out.println("==================热搜榜=========================");
        List<HotSearch> hotSearches = new ArrayList<>();
        RedisTemplate redisTemplate = (RedisTemplate) ApplicationContextUtil.getBean("redisTemplate", RedisTemplate.class);//获取redis对象
        System.out.println(redisTemplate);
        ZSetOperations<String, Object> stringObjectZSetOperations = redisTemplate.opsForZSet();
        //查询分数在15-999999之间的热搜词
        Set<ZSetOperations.TypedTuple<Object>> ts = stringObjectZSetOperations.reverseRangeByScoreWithScores("ts", 15, 999999);
        ts.forEach(t -> {
            HotSearch hotSearch = new HotSearch();
            hotSearch.setValue(t.getValue().toString()).setScore(t.getScore());
            hotSearches.add(hotSearch);
        });
        System.out.println(hotSearches);
        return hotSearches;//返回所有结果集
    }

    /**
     * 分页，多字段分词，高亮，查询
     *
     * @param searchString
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Map<String, Object> findTPoemBySearchStringForPage(String searchString, Integer pageNum, Integer pageSize) {
        addSearchStringToRedis(searchString);//添加到热搜榜并累加权重
        Map<String, Object> maps = new HashMap<>();
        List<TPoem> result = new ArrayList<>();

        //高亮字段
        HighlightBuilder.Field nameField = new HighlightBuilder.Field("name")
                .requireFieldMatch(false)//关闭检索字段匹配
                .preTags("<span style='color:red'>")
                .postTags("</span>");
        HighlightBuilder.Field contentField = new HighlightBuilder.Field("content")
                .requireFieldMatch(false)//关闭检索字段匹配
                .preTags("<span style='color:red'>")
                .postTags("</span>");
        HighlightBuilder.Field authordesField = new HighlightBuilder.Field("authordes")
                .requireFieldMatch(false)//关闭检索字段匹配
                .preTags("<span style='color:red'>")
                .postTags("</span>");

        //创建searchQuery
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices("ts")
                .withTypes("poem")
                //设置查询的条件为多字段分词查询
                .withQuery(QueryBuilders.queryStringQuery(searchString).field("name").field("content").field("authordes"))
                .withPageable(PageRequest.of(pageNum - 1, pageSize))//分页
                .withHighlightFields(nameField, contentField, authordesField)//设置高亮字段
                .build();
        AggregatedPage<TPoem> tPoems = elasticsearchTemplate.queryForPage(searchQuery, TPoem.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                List<TPoem> tPoems = new ArrayList<>();
                SearchHit[] hits = response.getHits().getHits();//获取结果集
                long totalHits = response.getHits().getTotalHits();
                maps.put("count", totalHits);//总记录数
                //遍历结果集
                for (SearchHit hit : hits) {
                    TPoem tPoem = new TPoem();
                    //获取原始记录
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    tPoem.setAuthor(sourceAsMap.get("author").toString())
                            .setId(sourceAsMap.get("id").toString())
                            .setAuthordes(sourceAsMap.get("authordes").toString())
                            .setCategoryid(sourceAsMap.get("categoryid").toString())
                            .setCategoryname(sourceAsMap.get("categoryname").toString())
                            .setContent(sourceAsMap.get("content").toString())
                            .setHref(sourceAsMap.get("href") + "")
                            .setName(sourceAsMap.get("name").toString())
                            .setOrigin(sourceAsMap.get("origin").toString())
                            .setType(sourceAsMap.get("type").toString());
                    //获取高亮的记录
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                    if (highlightFields.containsKey("name"))
                        tPoem.setName(highlightFields.get("name").fragments()[0].toString());
                    if (highlightFields.containsKey("content"))
                        tPoem.setContent(highlightFields.get("content").fragments()[0].toString());
                    if (highlightFields.containsKey("authordes"))
                        tPoem.setAuthordes(highlightFields.get("authordes").fragments()[0].toString());

                    tPoems.add(tPoem);
                }//hits foreach结束
                return new AggregatedPageImpl<T>((List<T>) tPoems);
            }
        });
        tPoems.forEach(p -> {
            result.add(p);
        });
        maps.put("tpomes", result);//查询到的所有记录信息添加到maps集合中
        return maps;
    }


}
