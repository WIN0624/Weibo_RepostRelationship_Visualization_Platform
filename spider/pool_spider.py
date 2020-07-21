import os
from multiprocessing import Pool
from word_spider import word_spider
from utils.loadConfig import load_config


def split_searchList(raw_searchlist, group_num):
    # 为进程池预处理
    # searchlist：共分为group_num组对应group_num个进程，每个元素为数组，数组中为检索词
    searchlist = []
    temp = []
    count = 1
    num = int(len(raw_searchlist) / group_num)
    if num == 1:
        raise Exception('Please reduce the group number or add more words')
    for ele in raw_searchlist:
        temp.append(ele)
        if count % num == 0 and not len(searchlist) == (group_num - 1):
            searchlist.append(temp)
            temp = []
        count += 1
    if temp:
        searchlist.append(temp)
    return searchlist


def pool_spider(group_num):
    # 加载设置文件，获取处理好的检索词列表
    raw_searchlist = load_config()['searchlist']
    searchlist = split_searchList(raw_searchlist, 5)
    print('Link parent process %s.' % os.getpid())
    p = Pool(group_num)
    for list in searchlist:
        p.apply_async(word_spider, args=(list,))
    p.close()   # 关闭进程池
    p.join()    # 阻塞父进程直至所有子进程运行完毕


if __name__ == '__main__':
    group_num = 5
    pool_spider(group_num)
