## Document for spider

## 1. 基本架构
### 1.1	代理模块`agent.py`
其中:
- `get_header()`负责随机产生请求头部。
- `get_proxy()`负责获取代理。
### 1.2 写入模块`csvWriter.py`
将此前多个写方法，封装为**csvWriter**类。其中:
- `__init__()`可以根据检索或获取转发关系，生成不同的csv头部字段，并调用create_csv。
- `create_csv()`负责产生文件并写入头部。
- `write_csv()`负责将爬取到的数据，不断追加入文件。
- `drop_duplicates()`负责处理[转发关系紊乱](#4-转发关系紊乱问题说明)的问题。
- `get_idList()`负责获取将要爬取转发关系的下一组微博的id列表。
### 1.3	获取检索词相关微博模块`get_query_info.py`
其中：
- `get_Topic()`。解析页面返回的每条微博的text，获取话题。
- `get_Text()`。解析页面返回微博，对text进行格式处理。
- `get_Page`。获取该检索词相关的微博页数，利于之后爬取。
- `get_query_info()`。基于检索词发送请求，解析检索页面。
### 1.4 获取转发关系模块`get_repost_info.py`
其中：
- `get_repost_relationship()`。进行多层爬取，每层需要：
    - 读取将要爬取的微博id列表
    - 对于其中每个id，调用`get_repost_info()`
    - 处理该层得到的下一组要爬取微博id的写入。
- `get_origin_info()`。获取原博的相关信息，辅助`get_repost_info()`。
- `get_repost_info()`。基于每条相关微博id发送请求，解析该微博页面获取转发信息。
### 1.5 扩充话题模块`get_more_topic.py`
根据输入的检索词，到微博话题页面检索所有相关话题，将得到的话题列表写入话题文件。
### 1.6 时间格式化模块`standarize_date.py`
为之后断点续存问题作准备。
### 1.7 日志模块`logger.py`
负责根据进程名生成每个进程对应的目录。
### 1.8 加载配置模块`loadConfig`
负责获取用户设置：
- 日志存储路径`log_dir`
- 话题存储路径`topic_dir`。此处话题指根据检索词扩充获得的话题，以csv文件存储。
- 检索词相关微博存储路径`hot__dir`。
- 转发关系信息存储路径`repost_dir`。
- 所有待爬取微博id列表的存储路径`repost_temp_dir`。
- one_word_spider中多进程爬取的转发关系暂存路径`one_word_repost_dir`。
- 检索词列表`searchlist`。可以列表形式传入，若为文件则设为文件名，将会进行相应的读取操作。
## 2. 主功能函数

### 2.1 `word_spider.py`
- 对 searchList 中每一个词
    - 获取微博检索页面中所有相关微博
    - 对每条相关微博获取多层转发关系
- 对 searchList 中每一个词进行话题扩充。用 EPOCH 记录迭代次数。将每一轮迭代的到的扩充话题用 EPOCH 次数标记的文件，下一轮迭代的searchList从此文件中读出。

### 2.2 `pool_spider.py`
- `split_searchList()`。负责将获取的检索词列表分成较为均匀的5份，作为5个子进程的输入。
- `pool_spider()`。产生5个子进程，执行`word_spider.py`中的`word_spider()`函数。

### 2.3 `one_word_spider.py` | 补充函数 | 加快转发关系爬取
- 获取一个检索词在微博检索页面中所有相关微博
- 将相关微博的总id列表切分成10份
- 建立进程池（10个进程），传入每个相关微博列表，对其中每个id爬取转发关系

[针对`one_word_spider`增加的相关函数]
- get_query_info.py：`one_word_get_query_info()`。建立one_word_spider的getQuery日志，获取检索信息。
- get_repost_info.py：`one_word_get_repost_relationship()`。
    - 根据进程名生成爬取日志
    - 获取对应的存储路径，生成csvWriter
    - 接收id列表，对于其中每个id，调用`get_repost_relationship()`
## 3. 待完成内容
### 3.1 `user_spider.py`
- [ ] 根据用户名获取所有微博，并对每条微博获取多层转发关系。可用于之后研究特定大V的热门微博转发情况。

## 4. 转发关系紊乱问题说明

### 4.1 紊乱在微博页面的显示

  在爬取微博的直接转发关系时，原博的转发会混入间接转发的内容：

  <img src="https://raw.githubusercontent.com/WIN0624/IMAGE/master/img/20200724121139.png" width="50%" height="50%">

  而按照当前的爬取逻辑，爬虫会将这些间接转发的内容当做直接转发的微博处理。

### 4.2 紊乱在爬取数据中的体现
* **问题：同一条微博，同时属于多个转发层级** <br>
  对于显示紊乱的微博，其在整一条转发链的每级爬取都会出现。<br>
  以A为原创微博为例，转发链为"A <-B <-C <-D <-E"。<br>
  若E为紊乱微博，则在爬取A、B、C、D时都会出现该紊乱微博，则爬虫会将E分别处理为第1、2、3、4层转发，即其与B（直接转发A）、C、D同层，最后才将其记录为D的直接转发。
> 注：整条转发链上的其它微博也会重复记录（即以上例子中C、D两条微博也是紊乱微博，会被多次记录）

* 体现1：爬取A、B、C直接转发时，对应字段会反复出现这条微博<br>
  [例子]<br>
  <img src="https://raw.githubusercontent.com/WIN0624/IMAGE/master/img/20200724151530.png" width="50%" height="50%"><br>
  其转发数据显示如下（实际上应取最后一层）：<br>
  <img src="https://raw.githubusercontent.com/WIN0624/IMAGE/master/img/20200724151705.png" width="70%" height="70%">

* 体现2：将爬取紊乱微博的转发关系时，level各不相同。实际上，仅最高level为正确的层数。<br>
  <img src="https://raw.githubusercontent.com/WIN0624/IMAGE/master/img/20200724152940.png" width="60%" height="60%">

  ### 4.3 紊乱的特殊情况
  对于“无为李爷”发布于7月19日的微博：

  <img src="https://gitee.com/WIN0624/document/raw/markdown-picture/img/image-20200813234313255.png" width="65%" height="65%">
  
  

  爬取到“三观不正向前冲”为一级转发，但浏览其微博发现其是间接转发：
  
  <img src="https://gitee.com/WIN0624/document/raw/markdown-picture/img/image-20200813234509604.png" width="60%" height="60%">
  
  

  这似乎是典型的转发紊乱问题，则在爬取数据中应当存在“帝吧小帅帅”的转发微博，但却没有找到相关数据，而在“无为李爷”微博的评论区域却发现了这条内容：

  <img src="https://gitee.com/WIN0624/document/raw/markdown-picture/img/image-20200813234630566.png" width="60%" height="60%">
  
  
  
  可见，对于转发紊乱问题的判断，不能仅仅通过转发微博内容中所含“//@”个数来判断层级，而仍是需要根据最高爬取层级来进行判断。
  
  
  
