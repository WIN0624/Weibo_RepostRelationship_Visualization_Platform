# -*- coding: utf-8 -*-
"""
Created on Sun May 17 2020
@author: Ying
"""
import time
import random
import requests
import json
from jsonpath import jsonpath
from get_topic import get_hot
from datetime import datetime
from urllib.parse import quote


# 相当于主函数
def get_query_wbid(topic=False):
    search_list = ['新型冠状病毒', 'AI', '经济学', '管理学']
    if topic:
        topic_list = get_hot()[3]
        search_list += topic_list
    results_list, results_dict = get_info(search_list)
    return results_list, results_dict


# 输入检索词得到wbid列表
def get_info(search_list):
    print('Start Time: ' + str(datetime.now()))
    results_list = []
    results_dict = {}
    for wd in search_list:
        id_list = []
        count = 0
        exception_count = 0
        # 将检索词编码，嵌入url得到不同词的url字典
        base_url = get_baseurl(wd)
        # 获取多页该检索词的结果页面
        for page in range(1, 100):
            this_url = base_url + str(page)
            try:
                # add header for the crawler
                headers = {'User-Agent': 'Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6'}
                r = requests.get(this_url, headers=headers)
                r.raise_for_status()
                r.encoding = r.apparent_encoding
                content = json.loads(r.text)
                if content.get('ok') == 1:
                    id_list += jsonpath(content, '$.data.cards..mblog.id')
                    count += len(id_list)
                time.sleep(random.randint(2, 8))
            except IndexError:
                print('There is no more data for this word! To the next word!')
                break
            except Exception:
                exception_count += 1
                if exception_count > 6:
                    print('I have failed 5 times for this word! I got to stop 10 minutes!')
                    time.sleep(600)
                print("request has been rejected or failed! Sleep 1 minutes and try next page!")
                time.sleep(60)
                continue

        # Store the results of all reachable pages to the list and dict
        results_list.append(id_list)
        results_dict[wd] = id_list
        print('Get %d weibo of %s' % (count, wd))
        time.sleep(10)
    return results_list, results_dict


def get_baseurl(wd):
    base_url = 'http://m.weibo.cn/container/getIndex?containerid=100103type%3D1%26q%3D' + quote(wd) + "&page_type=searchall&page="
    return base_url


if __name__ == '__main__':
    results_dict = get_query_wbid()[1]
    with open('search.json', 'w') as f:
        json.dump(results_dict, f, ensure_ascii=False)

