# -*- coding: utf-8 -*-
"""
Created on Sun May 17 2020
@author: Ying
"""
import csv
import time
import json
import random
import requests
from jsonpath import jsonpath
from get_topic import get_hot
from datetime import datetime
from urllib.parse import quote


# 相当于主函数
def get_query_wb(topic=False, json=False, csv=False):
    search_list = ['新型冠状病毒', 'AI', '经济学', '管理学']
    # 添加50个热搜入检索词
    if topic:
        addTopic(search_list)
    # 获得爬取结果
    results_list, results_dict = get_info(search_list)
    # 按需要输出
    if json:
        printJson(results_dict)
    if csv:
        printCSV(results_list)
    return results_list, results_dict


def addTopic(search_list):
    topic_list = get_hot()[3]
    search_list += topic_list


def printJson(results_dict):
    with open('query.json', 'w') as f:
        json.dump(results_dict, f, ensure_ascii=False)


def printCSV(results_list):
    headers = ['用户id', '用户名', '微博id']
    with open('query.csv', 'w', newline='') as f:
        f_csv = csv.DictWriter(f, headers)
        f_csv.writeheader()
        f_csv.writerows(results_list)


def get_baseurl(wd):
    base_url = 'http://m.weibo.cn/container/getIndex?containerid=100103type%3D1%26q%3D' + quote(wd) + "&page_type=searchall&page="
    return base_url


# 输入检索词得到wbid，用户id及用户名
def get_info(search_list):
    print('Start Time: ' + str(datetime.now()))
    headers = {'User-Agent': 'Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6'}
    results_list = []
    results_dict = {}
    for wd in search_list:
        wd_list = []
        # 将检索词编码，嵌入url得到不同词的url字典
        base_url = get_baseurl(wd)
        count = 0
        # 获取多页该检索词的结果页面
        for page in range(1, 20):
            this_url = base_url + str(page)
            try:
                r = requests.get(this_url, headers=headers)
                r.raise_for_status()
                r.encoding = r.apparent_encoding
                content = json.loads(r.text)
                if content.get('ok') == 1:
                    mblogs = jsonpath(content, '$.data.cards..mblog')
                    for mblog in mblogs:
                        this_dict = {'用户id': mblog['user']['id'], '用户名': mblog['user']['screen_name'], '微博id': mblog['id']}
                        wd_list.append(this_dict)
                        count += 1
                if count % 10 == 0:
                    time.sleep(random.randint(2, 8))
            except IndexError:
                print('There is no more data for this word! To the next word!')
                break
            except requests.HTTPError:
                print('I have met the HTTPError! I got to stop 3 minutes!')
                time.sleep(180)
            except Exception:
                continue

        # Store the results of all reachable pages to the list and dict
        results_list += wd_list + []
        results_dict[wd] = wd_list
        endtime = str(datetime.now()).split(' ')[1]
        print('Get %d weibo of %s at %s' % (len(wd_list), wd, endtime))
        time.sleep(5)
    return results_list, results_dict


if __name__ == '__main__':
    get_query_wb(json=True, csv=True)