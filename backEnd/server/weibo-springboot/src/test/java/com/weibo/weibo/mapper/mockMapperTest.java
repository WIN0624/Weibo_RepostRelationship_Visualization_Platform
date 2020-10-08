package com.weibo.weibo.mapper;

import com.weibo.weibo.entity.RepoRelationship;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class mockMapperTest {
    @Mock
    private RepoRelationshipMapper repoRelationshipMapper;
    @Before
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
    }
    @After
    public void tearDown() throws Exception{}
    @Test
    public void selectRepoRelationshipByCenter_bw_id() throws Exception{
        String center_bw_id="4523566437432908";
        List<RepoRelationship> repoRelationships=new ArrayList<>();
        when(repoRelationshipMapper.selectRepoRelationshipByCenter_bw_id(center_bw_id)).thenReturn(repoRelationships);
        List<RepoRelationship> repoRelationships1=repoRelationshipMapper.selectRepoRelationshipByCenter_bw_id(center_bw_id);
        assertTrue(repoRelationships1.isEmpty());
    }
}
