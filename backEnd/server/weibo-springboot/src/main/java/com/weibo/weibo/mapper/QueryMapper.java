package com.weibo.weibo.mapper;

import com.weibo.weibo.entity.Query;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface QueryMapper {
    List<Query> selectQueryByBw_ids(@Param("bw_ids")List<String> bw_ids);

    /**
     *
     * @param bw_id 用户id
     * @return Query
     */
    @Select("SELECT * FROM load_test WHERE bw_id = #{bw_id}")
    List<Query> selectQueryByBw_id(@Param("bw_id") String bw_id);

}
