package com.weibo.weibo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lijy3
 * @version 1.0
 * @ClassName Query 微博内容实体类
 * @Description TODO
 * @date 2020/5/24 16:11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Query implements Serializable {
    private static final long serialVersionUID = -1840831686851699943L;
    private String id;
    private String wd;
    private String user_id;
    private String screen_name;
    private String bw_id;
    private String topic;
    private String content;
    private String created_at;
    private String reposts_count;

}
