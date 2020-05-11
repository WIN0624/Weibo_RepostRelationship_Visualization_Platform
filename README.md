# WeiboSpider
**@ Zeng Ying, Pan Ziyang, Zhong Shanshan, Huang Zhishan, Yu Yixia, Chen Lehua**
<br>
>  mass表示批量，不带mass主要用于调试
<br>

## 使用：
1. 人工获取一些大v的用户id
2. 运行get_wbid.py,获得大v用户的微博id
3. 运行relationship.py,得到用户之间的转发关系，以[用户id，用户微博id，粉丝id，粉丝微博id]存储

## 训练：
1. 读取relationship.csv的粉丝id（可以另存为 普通用户id.txt）
2. 运行relationship.py,处理粉丝id,得到用户之间的转发关系，以[用户id，用户微博id，粉丝id，粉丝微博id]存储

## 问题：
1. 去重（可以使用数据库存储并去重，方便读取和修改，技术含量比较高）
2. 训练周期：当运行完relationship.py，需要读取relationship.csv的粉丝id，继续运行relationship.py（时间间隔，重复读取问题）
3. 大v用户id获取需要有代表性（基于转发关系获得的用户id容易圈子化）