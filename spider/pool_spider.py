import os
from multiprocessing import Pool
from word_spider import word_spider
from utils.loadConfig import load_config


def split_searchList(raw_searchlist):
    # 为进程池预处理
    # searchlist：共十个元素，每个元素为数组，数组中为检索词
    searchlist = []
    temp = []
    count = 1
    num = int(len(raw_searchlist) / 5)
    if num == 1:
        raise Exception('Please reduce the group number or add more words')
    for ele in raw_searchlist:
        temp.append(ele)
        count += 1
        if count % num == 0 and not len(searchlist) == 4:
            searchlist.append(temp)
            temp = []
    if temp:
        searchlist.append(temp)
    return searchlist


def pool_spider():
    # 加载设置文件，获取处理好的检索词列表
    raw_searchlist = load_config()
    searchlist = split_searchList(raw_searchlist)
    print('Link parent process %s.' % os.getpid())
    p = Pool(5)
    for list in searchlist:
        p.apply_async(word_spider, args=(list,))
    p.close()   # 关闭进程池
    p.join()    # 阻塞父进程直至所有子进程运行完毕


if __name__ == '__main__':
    pool_spider()
