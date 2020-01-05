package henu.xmh.dao;

import henu.xmh.pojo.TPoem;
import henu.xmh.pojo.TPoemExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TPoemMapper {


    int countByExample(TPoemExample example);

    int deleteByExample(TPoemExample example);

    int deleteByPrimaryKey(String id);

    int insert(TPoem record);

    int insertSelective(TPoem record);

    List<TPoem> selectByExample(TPoemExample example);

    TPoem selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") TPoem record, @Param("example") TPoemExample example);

    int updateByExample(@Param("record") TPoem record, @Param("example") TPoemExample example);

    int updateByPrimaryKeySelective(TPoem record);

    int updateByPrimaryKey(TPoem record);
}