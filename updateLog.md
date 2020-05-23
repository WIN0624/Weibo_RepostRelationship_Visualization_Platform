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

---
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
4. replace `get_query_wbid.py` with `get_query_wb.py`. The latter provides user id, user screen_name and a method to store info with the csv format.

### MAY 19, 2020
1. add `echarts.min.js` in `frontEnd`
2. add `jquery.min.js` in `frontEnd`
3. add `repostRelation.js` in `frontEnd`,some problems are not solved.

### MAY 20, 2020
1. add 'hottopic_bar.html' in `frontEnd`, visualize hot topic in a bar chart.
2. add 'hottopic_table.html' in `frontEnd`, visualize hot topic in a table.
3. add `repostRelation.html` in `frontEnd`
4. modify`repostRelation.js`in `frontEnd`, add a method to add the source blogger node and solve the problem of duplicate name
5. add proxypool in `get_topic.py`

### MAY 21, 2020

1. add longText in `get_query_wb.py`
2. add `Fans List.html` in `frontEnd`, display fans'names in a list
3. Add `Fans_List1.html` in `frontEnd`
4. delete `test.md` from `frontEnd`

### MAY 22, 2020

1. Add crawl time feature  in `get_query_wb.py`

<br><br>

---

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
2. 把`get_topic.py`中hot2json移到`fileFormatConversion.py`。
3. 添加输入boolean型参数conv2jsonadd作为`get_topic.py`  输入参数，移除path作为return。
4. 用`get_query_wb.py` 替换`get_query_wbid.py`。新增了用户id、用户名两个信息，提供存储为csv格式的方法。

### 2020年5月19日

1. 上传文件 `echarts.min.js` 
2. 上传文件 `jquery.min.js` 
3. 上传`repostRelation.js`，部分问题待解决

### 2020年5月20日

1. 上传文件 `hottopic_bar.html`，将热搜可视化为柱状图
2. 上传文件 `hottopic_table.html`，将热搜可视化为一个表格
3. 上传文件 `repostRelation.html`
4. 修改`repostRelation.js`，新增添加源博主信息节点的方法，解决name重复的问题
5. `get_topic.py`增加代理池

### 2020年5月21日

1. 增加 `get_query_wb.py` 爬取微博正文的功能
2. 上传文件 `Fans_List.html` ，展示用列表展示粉丝姓名
3. 上传文件 `Fans_List1.html`
4. 删除测试文档`test.md`

### 2020年5月22日

1. 增加`get_query_wb.py` 爬取指定时间段的功能

<br><br>

---
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

<br>

### 2020.5.20
#### 问题:
1. 使用MINA框架完成不同服务器之间的联系
2. 后台大批量的数据爬取，如果规避重复爬取和断点续爬？
3. 数据库和索引库的设计
4. 前端的网页设计样式，用户操作逻辑框架

#### 近期目标：
1. **Spider Group**: 
    + 完善目前基于检索词获取微博正文的代码 
    + 大规模爬取数据
    + 研究分布式爬取和后台部署
    + 为其他组提供需要的数据
2. **Web Group**:
    + 前后端调通
    + 整理好json数据格式
    + Force Layout图完毕
    + 制作网站展示页
3. **DataBase Group**:
    + 建立设计索引表和数据库，使用 MySql 和 Redis
    + 前后端调通
    + 编写脚本导入爬虫爬取到的少量数据