import csv
import json


def load_config():
    '''
    parameter:
        - log_dir：日志路径
        - hot_dir: 检索结果的存储路径
        - repost_dir：转发数据的存储路径
        - topic_dir：扩充话题的存储路径
        - process_num：进程数目
        - searchlist：检索词列表（数组、字符串或文件类型）
        - expand_topic：是否需要扩充话题以增大查全率
        - break_word：断点处的爬取词汇
        - breakList：数组
            - batch_num：断点的批号
            - center_bw_id：中心微博id
            - level：断点的层级数
            - break_id：断点id
            - repost_file：待写入的文件
        - breakPoint(无需用户创建，内部增加字段用于逻辑处理)
            - 若json中有断点相关字段，为True
            - 没有，为False
    '''
    # 获取数据输出路径和检索词
    config = json.load(open('config.json', 'r', encoding='utf-8'))

    # 处理检索词列表
    searchlist = config['searchlist']
    # 只能传入csv或txt类型
    if type(searchlist) is str:
        if '.csv' in searchlist or '.txt' in searchlist:
            with open(searchlist, 'r', encoding='utf-8-sig') as f:
                if ('.csv' in searchlist):
                    rows = csv.reader(f)
                    searchlist = [row[0].strip() for row in rows]
                if ('.txt' in searchlist):
                    rows = f.readlines()
                    searchlist = [row.strip() for row in rows]
        else:
            searchlist = [searchlist]   # 看作此时仅传入一个词，如'新冠'
    # 断点处理
    if config.get('break_word'):
        pos = searchlist.index(config['break_word'])
        searchlist = searchlist[pos:]
    config['searchlist'] = searchlist

    # 处理one_word(字符串转布尔)
    if config['expand_topic'] == 'True':
        config['expand_topic'] = True
    elif config['expand_topic'] == 'False':
        config['expand_topic'] = False

    return config
