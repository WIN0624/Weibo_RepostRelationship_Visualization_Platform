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
### 1.5 扩充话题模块`get_more_topic()`
根据输入的检索词，到微博话题页面检索所有相关话题，将得到的话题列表写入话题文件。
### 1.6 时间格式化模块`standarize_date().py`
为之后断点续存问题作准备。
### 1.7 日志模块`logger.py`
负责根据进程名生成每个进程对应的目录。
### 1.8 加载配置模块`loadConfig`
负责获取用户设置：
- 日志存储路径`log_dir`
- 话题存储路径`topic_dir`。此处话题指根据检索词扩充获得的话题，以csv文件存储。
- 检索词相关微博存储路径`hot_data_dir`。
- 转发关系信息存储路径`repost_data_dir`。
- 所有待爬取微博id列表的存储路径`temp_dir`。
- 检索词列表。若为文件进行相应的读取操作。
## 2. 主功能函数
### 2.1 `word_spider.py`
- 对 searchList 中每一个词
    - 获取微博检索页面中所有相关微博
    - 对每条相关微博获取多层转发关系
- 对 searchList 中每一个词进行话题扩充。用 EPOCH 记录迭代次数。将每一轮迭代的到的扩充话题用 EPOCH 次数标记的文件，下一轮迭代的searchList从此文件中读出。

### 2.2 `pool_spider.py`
- `split_searchList()`。负责将获取的检索词列表分成较为均匀的5份，作为5个子进程的输入。
- `pool_spider()`。产生5个子进程，执行`word_spider.py`中的`word_spider()`函数。
## 3. 待完成内容
### 3.1 写入模块中的去重方法`drop_duplicate()`
- [ ] 去重且针对层次错乱问题，需要去除转发关系中出现在多个层次的同一条微博，只取最高层。
### 3.2 `user_spider.py`
- [ ] 根据用户名获取所有微博，并对每条微博获取多层转发关系。可用于之后研究特定大V的热门微博转发情况。
