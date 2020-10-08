# 微博192端项目

### 项目结构

```yml
-src
	-mian
		-java
			-com.weibo.weibo
				-config # 配置类
				-entity # 实体类
				-mapper # mybatis dao层接口
				-MyRedis # 不清楚redis放在哪，先保留
				-service # service层，现用于接收grpc请求
				-util # 工具类
					createJson.java # 生成json
		-proto # proto文件
		-resource
			-mybatis
				-mapper # 放mybatis映射文件
				mybatis-config.xml # mybatis配置文件
           	application.yml # 项目全局配置文件
	
-target # 生成的各种东西，包括class和protobuf

pom.xml # 依赖
start.sh # 前台运行脚本
nohupstart.sh # 后台运行脚本
stop.sh # 关闭后台运行脚本

```



### Memo

1. 该项目文件使用了Lombok，具体使用方式自行百度，这里稍微介绍一下已经使用了的东西

   注意：在idea中要下载插件**Lombok**才不会报红

   @Data：用在了实体类上，使用后会自动包含setter、getter、toString方法

   @Slf4j：用于生成日志，使用方法是写在类上，调用时直接用 log.info()、log.error() 等

2. 本次项目文件中**已使用Druid数据连接池**，直接使用mybatis就可以用了，是隐形的，相关配置可以在application.yml中调整
3. proto相关操作需要在idea中下载插件 **Protobuf Support**