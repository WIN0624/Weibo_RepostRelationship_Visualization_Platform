# 微博121端项目

### 项目结构

```yml
-src
	-java
		-com.weibo.weibo
			-config # 配置类
			-controller # 接收ajax请求并发起grpc请求
	-proto # grpc-proto文件
	-resources
		application.yml # 全局配置文件
```



### 接口文档

##### 1. 根据检索词获取前十条微博

url：http://121.46.19.26:8288/getWeibo/{keyword}

请求方式：http get

请求参数：

| 参数    | 说明   |
| ------- | ------ |
| keyword | 检索词 |

返回参数：

| 参数      | 说明     |
| --------- | -------- |
| bw_id     | 微博id   |
| user_id   | 用户id   |
| user_name | 用户名   |
| wd        | 检索词   |
| tag       | 标签     |
| bw_text   | 微博正文 |



##### 2. 根据微博id获取其转发关系

url：http://121.46.19.26:8288/getRelationship/{weiboId}

请求方式：http get

请求参数：

| 参数    | 说明   |
| ------- | ------ |
| weiboId | 微博id |

返回参数：

| 参数           | 说明               |
| -------------- | ------------------ |
| bw_id          | 微博id             |
| user_id        | 用户id             |
| user_name      | 用户名             |
| origin         | 是否原创           |
| fans_count     | 用户粉丝数         |
| reposts_count  | 微博转发数         |
| fs_user_id     | 转发用户id         |
| fs_screen_name | 转发用户名         |
| fs_bw_id       | 转发微博id         |
| fs_fans_count  | 转发用户粉丝数     |
| level          | 转发层数           |
| raw_text       | 转发微博时带的内容 |



##### 3. 根据检索词获取前十条微博

url：http://121.46.19.26:8288/getRelationshipBody/{weiboId}

请求方式：http get

请求参数：

| 参数    | 说明   |
| ------- | ------ |
| keyword | 检索词 |

返回参数：

| 参数      | 说明     |
| --------- | -------- |
| bw_id     | 微博id   |
| user_id   | 用户id   |
| user_name | 用户名   |
| wd        | 检索词   |
| tag       | 标签     |
| bw_text   | 微博正文 |







