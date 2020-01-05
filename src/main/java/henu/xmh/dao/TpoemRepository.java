package henu.xmh.dao;

import henu.xmh.pojo.TPoem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

//对es相关操作
public interface TpoemRepository extends ElasticsearchRepository<TPoem, String> {

}
