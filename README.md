# Weibo_RepostRelationship_Visualization_Platform
**@authors: members of the 2018 class scientific research team from School of Information Management of SYSU，lead by Prof. Li.**

<br>

## <span id="qianyan">前言：</span>
> 本项目旨在利用[移动端微博网页](http://m.weibo.cn)，获取公开微博的相关数据，并进行相关的数据分析和可视化工作。

<br>

## 目录
0. [前言](#qianyan)
1. [模块介绍](#moduel)
    + [Spider Moduels](#spider)
    + [Front End](#frontend)
    + [Back End](#backend)
2. [项目更新日志](#updatedocument)
3. [如何安装python依赖？](#pythondependance)
4. [关于我们](#aboutus)

<br>

## <span id="moduel">1. 模块介绍</span>

### <span id="spider"> 1.1 Spider Moduels </span>

```
    Weibo_RepostRelationship_Visualization_Platform/spider/:
        │  pool_spider.py
        │  README.md
        │  word_spider.py
        │  one_word_spider.py
        |
        └─utils
            │  agent.py
            │  csvWriter.py
            │  get_more_topic.py
            │  get_query_info.py
            │  get_repost_info.py
            │  loadConfig.py
            │  logger.py
            │  standarize_date.py
            │  __init__.py
            │
            └─__pycache__
```

#### 1.1.1 模块方法
---
- 代理模块 `agent.py`

    - 代理模块通过使用代理池模拟多用户请求，并随机生成请求伪装头部
- 写入模块 `csvWriter.py`

    - 将此前多个写方法，封装为**csvWriter**类。根据需求生成csv文件或对已有文件进行追加写入
- 获取检索词相关微博模块 `get_query_info.py`

    - 根据请求对微博进行爬取，解决了多页爬取，能够获取话题并对微博内容进行格式处理
- 获取转发关系模块 `get_repost_info.py`

    - 获取源微博的转发关系，采用读取转发列表的方式，对转发列表的微博id进行遍历爬取
- 扩充话题模块 `get_more_topic()`

    - 根据输入的检索词，到微博话题页面检索所有相关话题，将得到的话题列表写入话题文件。
- 时间格式化模块 `standarize_date().py`

    - 为之后断点续存问题作准备。
- 日志模块 `logger.py`

    - 负责根据进程名生成每个进程对应的目录。
- 加载配置模块 `loadConfig`

    - 负责获取用户设置：可设置日志存储路径、话题存储路径、检索词相关微博存储路径、转发关系存储路径、待爬取微博id列表存储路径以及检索次列表。

<br>

#### 1.1.2 主功能函数
---
- `one_word_spider.py`
    - 获取一个检索词在微博检索页面中所有相关微博
    - 对每条相关微博获取多层转发关系（10个进程同时处理）

- `word_spider.py`
    - 获取微博检索页面中所有相关微博
    - 对每条相关微博获取多层转发关系
    - 对 searchList 中每一个词进行话题扩充。用 EPOCH 记录迭代次数。将每一轮迭代的到的扩充话题用 EPOCH 次数标记的文件，下一轮迭代的searchList从此文件中读出。

- `pool_spider.py`

    - 生成进程，调用 `word_spider.py` 作为进程内容


<br>

了解更多，点击[这里](spider/README.md)

<br>

### <span id="frontend">1.2 Front End</span>
---
    1. 待更新

<br>

### <span id="backend">1.3 Back End</span>
---
    1. 待更新
<br><br>

## <span id="updatedocument">2. 更新日志</span>
[开发日志](updateLog.md)将会实时更新。

日志记录了科研小组在开发过程中的所有会议记录和相关更新的详细日志。如果对此感兴趣，可以从中根据我们的开发迭代过程，了解我们的开发历程，并强烈欢迎您提出建议。

<br><br>

## <span id="pythondependance">3. 如何安装python依赖？</span>
您可能需要安装python以来以来才能实现以上工作。我们提供了专门的依赖文档供您一键安装所有python依赖。

使用 `pip install -r requirements.txt` 命令即可完成python第三方库依赖的安装。

<br><br>

## <span id="aboutus">4. 关于我们</span>
> 该项目用于构建微博通用搜索和信息分析平台，贡献者主要分为爬虫、前端展示和后端存储三组。

**Spider Contributors**: Zeng Ying, Zhong Shanshan, Pan Ziyang, Chen Lehua, Huang Zhishan, Yu Yixia.

**Web Contributors**: Feng Yanxia, Chen Jinying, Peng Jiahui, Gong ZhiLin, Li Yi.

**Sql & Redis Contributors**: Li Jiayi, Han Yuxuan, Kuang Qianyin, Jia Chang, Du Chongwen, Xiao Jingbo, Li Ziqian, Lin Jie, Zeng Linrong.
