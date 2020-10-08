package com.weibo.weibo.mapper;

import com.weibo.weibo.entity.RepoRelationship;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface RepoRelationshipMapper {
    /**
     * @param bw_id
     * @return RepoRelationship
     */
    @Select("select * from load_test2 where bw_id=#{bw_id}")
    RepoRelationship selectRepoRelationshipByBw_idString(@Param("bw_id")String bw_id);

    List<RepoRelationship> selectRepoRelationshipByCenter_bw_id(@Param("center_bw_id")String center_bw_id);

}
