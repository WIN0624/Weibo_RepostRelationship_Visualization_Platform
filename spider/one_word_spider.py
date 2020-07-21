import csv
import glob
import time
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

    # 创建写文件
    search_file = hot_dir + 'search_result_' + str(wd) + '.csv'
    # 创建写的对象,同时创建文件
    search_writer = csvWriter(search_file, search=True)
    # 获取该检索词的所有相关微博，至多能获取1000条
    # 为了日志正常输出（python的logging非进程安全），需要用到进程池
    print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]. Keyword: {wd}. Start crawling ...')
    p = Pool(1)
    p.apply_async(one_word_get_query_info, args=(wd, search_writer))
    p.close()
    p.join()

    # 获取相关微博id组成的列表
    # 将其分为10个列表，用10个进程进行转发关系爬取
    raw_searchlist = search_writer.get_idList()
    searchList = split_searchList(raw_searchlist, 10)

    # 生成10进程的进程池
    print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]. Generating Process Pool Containing 10 Process...')
    p = Pool(10)
    for group in searchList:
        p.apply_async(one_word_repost_relationship, args=(group,))
    p.close()
    p.join()

    # 将10个进程的csv文件进行合并
    print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]. Start Merging csv Files...')
    merge_csv(wd, one_repost_dir, repost_dir)


def merge_csv(wd, one_repost_dir, repost_dir):
    csv_list = glob.glob(one_repost_dir + '*.csv')
    print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]. Find {str(len(csv_list))} csv files in total.')
    print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]. Start processing...')
    with open(repost_dir + 'repost_Relationship_' + wd + '.csv', 'w', encoding='utf-8-sig', newline='') as f:
        f_csv = csv.writer(f)
        for file in csv_list:
            with open(file, 'r', encoding='utf-8') as f2:
                f2_csv = csv.reader(f2)
                if '1_spider.log' in file:
                    f_csv.writerows(f2_csv)
                else:
                    rows = list(f2_csv)
                    f_csv.writerows(rows[1:])
    print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]. Finish Merging.')


if __name__ == '__main__':
    one_word_spider()
