def splitList(raw_searchlist, group_num):
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
