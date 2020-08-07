import csv
import glob
import time
import pandas as pd
from multiprocessing import Pool
from pool_spider import split_searchList
from utils.csvWriter import csvWriter
from utils.loadConfig import load_config
from utils.get_query_info import one_word_get_query_info
from utils.get_repost_info import one_word_repost_relationship


def one_word_spider():
    # 加载设置文件，获取数据输出路径和检索词
    config = load_config()
    hot_dir = config['hot_dir']
    repost_dir = config['repost_dir']
    one_repost_dir = config['one_repost_dir']
    searchlist = config['searchlist']
    if type(searchlist) is str or len(searchlist) == 1:
        wd = searchlist
    else:
        raise ValueError('one_word_spider() can only accept one search word!')

    # 写文件的文件名
    search_file = hot_dir + 'search_result_' + str(wd) + '.csv'
    # 创建写的对象,同时创建文件
    search_writer = csvWriter(search_file, search=True)
    # 获取该检索词的所有相关微博，至多能获取1000条
    # 为了日志正常输出（python的logging非进程安全），需要用到进程池
    print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]  Keyword: {wd}. Start crawling ...')
    p = Pool(1)
    p.apply_async(one_word_get_query_info, args=(wd, search_writer))
    p.close()
    p.join()

    # 获取相关微博id组成的列表
    # 将其分为10个列表，用10个进程进行转发关系爬取
    raw_searchlist = search_writer.get_idList()
    searchList = split_searchList(raw_searchlist, 10)

    # 生成10进程的进程池
    print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]  Generating Process Pool Containing 10 Process...')
    p = Pool(10)
    for group in searchList:
        p.apply_async(one_word_repost_relationship, args=(group,))
    p.close()
    p.join()

    # 将10个进程的csv文件进行合并
    print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]  Start Merging csv Files...')
    merge_csv(wd, one_repost_dir, repost_dir)


def merge_csv(wd, one_repost_dir, repost_dir):
    csv_list = glob.glob(one_repost_dir + '*.csv')
    print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]  Find {str(len(csv_list))} csv files in total.')
    print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]  Start Merging csv Files...')
    filename = repost_dir + 'repost_Relationship_' + wd + '.csv'
    with open(filename, 'w', encoding='utf-8-sig', newline='') as f:
        f_csv = csv.writer(f)
        count = 1
        for file in csv_list:
            with open(file, 'r', encoding='utf-8') as f2:
                f2_csv = csv.reader(f2)
                if count == 1:
                    f_csv.writerows(f2_csv)
                else:
                    rows = list(f2_csv)
                    f_csv.writerows(rows[1:])
                count += 1
    # 去重
    drop_duplicates(filename)
    print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]  Finish Merging!')


def drop_duplicates(filename):
    df = pd.read_csv(filename, header=0)
    df1 = df.loc[df['fs_bw_id'] == 'Null']
    df2 = df.loc[df['fs_bw_id'] != 'Null']
    df1 = df1.drop_duplicates('user_id', keep='last')
    df2 = df2.drop_duplicates('fs_bw_id', keep='last')
    df = pd.concat([df1, df2], axis=0)
    df = df.sort_index(axis=0, ascending=True)
    df.to_csv(filename, index=False)


# 针对断点
def getBreakList(raw_searchlist, breakpos):
    # 将待爬列表分成10份(与原始的分法对应），对每一份，只取断点之后的微博id
    # 将子列表作为输入，爬取转发关系
    searchList = split_searchList(raw_searchlist, 10)
    num = len(searchList)
    if len(breakpos) != num:
        print('You need to Modify this code OR Check if you miss some wb_id in breakpos!')
    # 适当调整爬取列表长度
    allList = []
    for i in range(10):
        temp = searchList[i]
        try:
            pos = temp.index(breakpos[i]['center_bw_id'])
        except Exception:
            print(f"Break bw_id can't be found in sublist {i} of searchList")
            continue
        if len(temp) - pos < 15:
            thisList = temp[pos:]   # 取当前断点之后的id列表，对断点特别处理
            this_dict = {'thisList': thisList, "breakpos": breakpos[i]}
            allList.append(this_dict)
        else:
            num = 1
            while pos < len(temp):
                thisList = temp[pos:pos+15]
                if num == 1:
                    this_dict = {'thisList': thisList, "breakpos": breakpos[i]}
                    allList.append(this_dict)
                else:
                    this_dict = {'thisList': thisList, "breakpos": None}
                    allList.append(this_dict)
                pos += 15
                num += 1
    return allList


def one_word_continue():
    # 加载设置文件，获取数据输出路径和检索词
    config = load_config()
    repost_dir = config['repost_dir']
    one_repost_dir = config['one_repost_dir']
    searchlist = config['searchlist']
    if type(searchlist) is str or len(searchlist) == 1:
        wd = searchlist
    else:
        raise ValueError('one_word_spider() can only accept one search word!')
    # 读取已爬取好的检索词相关微博
    filename = config['hot_dir'] + 'search_result_' + str(wd) + '.csv'
    reader = csvWriter(filename, search=True, breakpos=True)
    raw_searchlist = reader.get_idList()

    # 根据每个进程此前中断的center_bw_id重新进行爬取
    breakpos = config['breakpos']
    allList = getBreakList(raw_searchlist, breakpos)

    # 启动进程池
    pool_size = len(allList)
    p = Pool(pool_size)
    print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]  Generating Process Pool Containing {str(pool_size)} Process...')
    print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]  Start crawling repost relationship...')
    
    for sublist in allList:
        if sublist.get('breakpos'):
            p.apply_async(one_word_repost_relationship, args=(sublist['thisList'], sublist['breakpos']))
        else:
            p.apply_async(one_word_repost_relationship, args=(sublist['thisList'],))

    p.close()
    p.join()
    print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]  Finish crawling repost relationship!')

    # 将所有csv文件进行合并
    merge_csv(wd, one_repost_dir, repost_dir)


if __name__ == '__main__':
    one_word_continue()
