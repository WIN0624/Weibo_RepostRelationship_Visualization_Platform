# Update Details.
> **What does the Update Log do?**
> <br><br>
> This is a document for our development, which contains the updates for Modeuls and Fornt-End development.

<br>

## CONTENT
1. [Logs-EN](##Logs)
2. [Logs-ZH](##日志)
3. [Development](##DEVELOPMENT)

<br>

## Logs
### MAY 15, 2020
1. Add memebers responsible for front-end display.
2. Add moduels of `get_usr_id.py` and `get_topic.py`.

### MAY 17, 2020

1. Add return topic list in `get_topic.py`.
2. Add first edition of `get_query_wbid.py`.

### MAY 18, 2020
1. some troubles in `get_user_fans.py` that some WeiBo users cannot be crawled are fixed.
2. add two params named *`page`* and *`write_in_a_file`* in `get_usr_fans.py`.

### MAY 19, 2020

1. change hot2json format in `get_topic.py`.
1. change hot2json format in `get_topic.py`  
2. move hot2json  into `fileFormatConversion.py`
3. add input parameter in  `get_topic.py`  and remove path in return.

### MAY 20, 2020
1. add `echarts.min.js` in `frontEnd`
2. add `jquery.min.js` in `frontEnd`

<br><br><br>


## 日志
### 2020年5月15日
1. 加入前端组成员。
2. 增加 `get_usr_id.py` 和 `get_topic.py` 模块。

### 2020年5月17日

1. 增加了主题列表作为 `get_topic.py` 返回值。
2. 增加 `get_query_wbid.py` 初版。

### 2020年5月18日

1. 修复了 `get_usr_fans.py` 存在的某些用户无法爬取的问题。
2. 增加了 `get_usr_fans.py` 的两项参数 `page` 和 `write_in_a_file`。

### 2020年5月19日

1. 修改 `get_topic.py` 中hot2json的输出格式。
1. 修改`get_topic.py`中hot2json的输出格式。
2. 把`get_topic.py`中hot2json移到`fileFormatConversion.py`
3. 添加输入boolean型参数conv2jsonadd作为`get_topic.py`  输入参数，移除path作为return。

### 2020年5月20日

1. 上传文件 `echarts.min.js` 
2. 上传文件 `jquery.min.js` 

<br><br><br>

## DEVELOPMENT

### 2020.5.11
#### 问题：
1. 去重（可以使用数据库存储并去重，方便读取和修改，技术含量比较高）
2. 训练周期：当运行完`relationship.py`，需要读取`relationship.csv`的粉丝id，继续运行`relationship.py`（时间间隔，重复读取问题）
3. 大v用户id获取需要有代表性（基于转发关系获得的用户id容易圈子化）

#### 远期目标
1. 后端提供的数据库接口
2. 前端的可视化模型
3. 对爬取的微博博文进行分析
