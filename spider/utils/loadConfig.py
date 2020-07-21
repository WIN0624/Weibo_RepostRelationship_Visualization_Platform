import csv
import json


def load_config():
    # 获取数据输出路径和检索词
    raw_config = json.load(open('config.json', 'r', encoding='utf-8'))
    config = {}
    config['log_dir'] = raw_config['log_dir']
    config['hot_dir'] = raw_config['hot_dir']
    config['topic_dir'] = raw_config['topic_dir']
    config['repost_dir'] = raw_config['repost_dir']
    config['repost_temp_dir'] = raw_config['repost_temp_dir']
    config['one_repost_dir'] = raw_config['one_word_repost_dir']

    # 处理检索词列表
    searchlist = raw_config['searchlist']
    # 只能传入csv或txt类型
    if type(searchlist) is str and '.csv' in searchlist and '.txt' in searchlist:
        with open(searchlist, 'r', encoding='utf-8-sig') as f:
            if ('.csv' in searchlist):
                rows = csv.reader(f)
                searchlist = [row[0].strip() for row in rows]
            if ('.txt' in searchlist):
                rows = f.readlines()
                searchlist = [row.strip() for row in rows]
    config['searchlist'] = searchlist

    # 否则为pool_spider调用，返回检索词列表
    return config
