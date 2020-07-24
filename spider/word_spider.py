import os
import csv
import multiprocessing
from utils.logger import getLogger
from utils.csvWriter import csvWriter
from utils.loadConfig import load_config
from utils.get_query_info import get_query_info
from utils.get_more_topic import get_more_topic
from utils.get_repost_info import get_repost_relationship


# 参数为初始检索词列表，进程名
def word_spider(searchlist):
    # 加载设置文件，获取数据输出路径和检索词
    config = load_config()
    hot_dir = config['hot_dir']
    topic_dir = config['topic_dir']
    repost_dir = config['repost_dir']
    # 根据规定日志目录创建目录实例
    name = multiprocessing.current_process().name
    logger = getLogger(name)
    topic_dir += name + '_'
    # 记录载入检索词列表的次数
    epoch = 1

    while True:
        # 对每一个词爬取相关微博和各微博的转发关系
        for wd in searchlist:
            logger.info(f'EPOCH: {epoch}. Keyword: {wd}. Start crawling ...')
            search_file = hot_dir + 'search_result_' + str(wd) + '.csv'
            repost_file = repost_dir + 'repost_Relationship_' + str(wd) + '.csv'
            # 创建两个写的对象,同时创建文件
            search_writer = csvWriter(search_file, search=True)
            repost_writer = csvWriter(repost_file, repost=True)

            # 获取该检索词的所有相关微博，至多能获取1000条
            get_query_info(wd, search_writer, logger)

            # 获取相关微博id组成的列表
            idList = search_writer.get_idList()
            # 获取各相关微博的转发关系
            for bw_id in idList:
                get_repost_relationship(bw_id, repost_writer, logger)

            repost_writer.drop_duplicates()

            # 获取该词相关所有话题作为之后的检索词
            get_more_topic(wd, epoch, topic_dir, logger)

        # 结束一轮检索爬取
        # 获取新检索词列表
        filename = topic_dir + 'Topics_' + str(epoch) + '.csv'
        with open(filename, 'r', encoding='utf-8-sig') as f:
            rows = csv.reader(f)
            searchlist = [row[0].strip() for row in rows]

        # 删除中间文件
        os.remove(filename)

        epoch += 1
