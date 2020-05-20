# WeiboSpider
**@authors: members of the 2018 class scientific research team from School of Information Management of SYSU，lead by Prof. Li.**

<br>

## 前言：
> 微博用户拥有唯一的用户id，每一条微博也有唯一的博文id。本项目旨在利用[移动端微博网页](http://m.weibo.cn)，获取公开微博的相关数据。并进行相关的数据分析和可视化工作。

<br>

## 目录
0. [前言](##前言)
1. [模块介绍](##模块介绍)
    + [Spider Moduels](###SpiderModuels)
    + [Front-End](###FrontEnd)
2. [项目更新日志](##更新日志)
3. [如何安装python依赖？](##如何安装python依赖)
4. [关于我们](##关于我们)

<br>

## 模块介绍：

+ ### SpiderModuels

    1. `search_uid.py` , 用于获取指定query的用户id，返回用户相关信息的字典
    2. `get_wbid.py` ,用于获得用户id下的微博id（通过访问用户主页实现）
    3. `IDRelationship.py` ,用于互相获取微博中复杂的内容识别ID关系，例如您可以通过输入用户微博ID获得其所有的微博博文ID。
    4. `get_topic.py` 用于获取实时热搜主题以及相应热度，提供两种方式存储：csv, json. *具体格式`[index, topic, score]`* 
    生成文件命名为 **爬取热搜时间.csv /.json** 
    5. `get_query_wb`用于根据检索词获取相关微博的id、用户id和用户名，提供存储格式：csv格式和json格式.
    6. `get_usr_fans.py`用于获取用户的粉丝id以及粉丝名，返回id列表以及名字列表，可生成三个文本文件，分别存储了id、用户名、id与用户名的对应关系
    7. `additionalFeatures.py` 用于存储一些额外的微博特征的识别方法。现可提供对微博博文的原创性识别功能。
    8. `fileFormatConversion.py` 为部分csv输出文件提供转换为json格式的功能，在相关内容爬取模块的输出部分会被自动调用。

+ ### FrontEnd

    1. 待更新

<br><br>

## 更新日志
更新日志记录了科研小组在开发过程中的所有会议记录和相关更新的详细日志。如果有兴趣，可以从中根据我们从原始版本开始的迭代开发过程，了解我们的开发历程，并且强烈欢迎您提出建议。

<br><br>

## 如何安装python依赖？
我们提供了专门的依赖文档供您一键安装所有python依赖。

使用 `pip install -r requirements.txt` 命令完成python第三方库依赖的安装。

<br><br><br>

## 关于我们
> 该项目用于构建微博通用搜索和信息分析平台，贡献者主要分为爬虫、前端展示和后端存储。

**Spider Contributor**: Zeng Ying, Zhong Shanshan, Pan Ziyang, Chen Lehua, Huang Zhishan, Yu Yixia.
