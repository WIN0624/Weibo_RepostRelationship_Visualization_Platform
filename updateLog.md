# Update Details.
> **What does the Update Log do?**
<br><br>
This is a document for our development, which contains the updates for Modeuls and Fornt-End development.

## Logs
### MAY 15, 2020
1. add memebers responsible for front-end display.
2. add moduels of `get_usr_id.py` and `get_topic.py`, which provide accesses of **hot-spot topic** and **fans information of users**.


## DEVELOPMENT

## 问题：
1. 去重（可以使用数据库存储并去重，方便读取和修改，技术含量比较高）
2. 训练周期：当运行完`relationship.py`，需要读取`relationship.csv`的粉丝id，继续运行`relationship.py`（时间间隔，重复读取问题）
3. 大v用户id获取需要有代表性（基于转发关系获得的用户id容易圈子化）

## 远期目标
1. 后端提供的数据库接口
2. 前端的可视化模型
3. 对爬取的微博博文进行分析