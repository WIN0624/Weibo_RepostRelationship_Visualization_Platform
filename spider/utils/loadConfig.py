import csv
import json


def load_config(dir=False, log=False, temp=False):
    # 获取数据输出路径和检索词
    config = json.load(open('config.json', 'r', encoding='utf-8'))
    log_dir = config['log_dir']
    temp_dir = config['temp_dir']
    topic_dir = config['topic_dir']
    hot_data_dir = config['hot_data_dir']
    repost_data_dir = config['repost_data_dir']
    # 当被logger调用时，仅返回目录路径
    if (log):
        return log_dir
    # 当被word_spider调用时，返回数据路径和话题存储路径
    if (dir):
        return topic_dir, hot_data_dir, repost_data_dir
    if (temp):
        return temp_dir

    searchlist = config['search_list']
    # 只能传入csv类型
    if type(searchlist) is str:
        with open(searchlist, 'r', encoding='utf-8-sig') as f:
            if ('.csv' in searchlist):
                rows = csv.reader(f)
                searchlist = [row[0].strip() for row in rows]
            if ('.txt' in searchlist):
                rows = f.readlines()
                searchlist = [row.strip() for row in rows]

    # 否则为pool_spider调用，返回检索词列表
    return searchlist
