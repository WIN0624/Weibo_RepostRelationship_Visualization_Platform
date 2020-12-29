from utils.loadConfig import load_config


def splitList(raw_searchlist, group_num, breakpos=False):
    # 为进程池预处理
    # searchlist：共分为group_num组对应group_num个进程，每个元素为词典，sublist为检索词列表，breakpos为断点
    searchlist = []
    temp = []
    count = 1
    num = int(len(raw_searchlist) / group_num)
    if num == 1:
        raise Exception('Please reduce the group number or add more words')
    for ele in raw_searchlist:
        temp.append(ele)
        if count % num == 0 and not len(searchlist) == (group_num - 1):
            searchlist.append({'sublist': temp})
            temp = []
        count += 1
    if temp:
        searchlist.append({'sublist': temp})

    # 断点处理
    if breakpos:
        newList = []
        breakList = load_config()['breakList']
        for item in breakList:
            this_dict = {}
            this_dict['breakpos'] = item
            temp_list = searchlist[item['batch_num']]['sublist']
            pos = temp_list.index(item['center_bw_id'])
            this_dict['sublist'] = temp_list[pos:pos+10]
            newList.append(this_dict)
            # 若某个batch剩余id很多，非断点id处理成其它list
            pos += 10
            while pos < len(temp_list):
                newList.append({'sublist': temp_list[pos:pos+10]})
                pos += 10
        return newList
    else:
        return searchlist
