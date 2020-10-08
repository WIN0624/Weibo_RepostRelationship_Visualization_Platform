package com.weibo.weibo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepoRelationship {
    private String user_id;
    private String screen_name;
    private String bw_id;
    private String origin;
    private String reposts_count;
    private String fs_user_id;
    private String fs_screen_name;
    private String fs_bw_id;
    private String fans_count; // fs_count
    private String fs_fans_count;
    private String level;
    private String raw_text;
    private String created_at;
}