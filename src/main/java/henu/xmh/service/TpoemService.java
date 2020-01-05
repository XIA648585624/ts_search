package henu.xmh.service;

import henu.xmh.pojo.HotSearch;
import henu.xmh.pojo.TPoem;

import java.util.List;
import java.util.Map;

public interface TpoemService {
    List<TPoem> findAllForAge(Integer currentPageNum, Integer pageSize);

    Integer getCountForAll(Integer pageSize);

    //根据条件查询
    List<TPoem> findBySerachFiledString(String searchField, String searchString, String searchOper, Integer currentPageNum, Integer pageSize);

    Integer getCountForSerachFiledString(String searchField, String searchString, String searchOper);

    void add(TPoem tPoem);

    void alter(TPoem tPoem);

    void drop(String id);

    TPoem findOne(String id);

    void alterForJg(TPoem tPoem, String oper);

    void savrEs();

    void delEs();

    //分页根据索引的String分词查询
    Map<String, Object> findTPoemBySearchStringForPage(String searchString, Integer pageNum, Integer pageSize);

    void addSearchStringToRedis(String searchString);

    List<HotSearch> findHotSearch();
}
