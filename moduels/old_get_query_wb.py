# -*- coding: utf-8 -*-
"""
Created on Sun May 17 2020
@author: Ying, Le_C
"""
import re
import csv
import time
import json
import random
import requests
import traceback
from bs4 import BeautifulSoup
from jsonpath import jsonpath
from get_topic import get_hot
from datetime import datetime, timedelta
from urllib.parse import quote


# 相当于主函数
def get_query_wb(topic=False, json=False, csv=False, since_date=None):
    search_list = ['新型冠状病毒']
    # 添加50个热搜入检索词
    if topic:
        addTopic(search_list)
    # 获得爬取结果
    results_list, results_dict = get_info(search_list, since_date=since_date)
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
    with open('query.json', 'w', errors='ignore') as f:
        json.dump(results_dict, f, ensure_ascii=False)


def printCSV(results_list):
    headers = ['检索词', '用户id', '用户名', '微博id', '话题', '微博正文', '发表时间']
    with open('query.csv', 'w', newline='', errors='ignore') as f:
        f_csv = csv.DictWriter(f, headers)
        f_csv.writeheader()
        f_csv.writerows(results_list)


def get_baseurl(wd):
    base_url = 'https://m.weibo.cn/api/container/getIndex?containerid=100103type%3D1%26q%3D' + quote(wd) + "&page_type=searchall&page="
    return base_url


def getTopic(text):
    regex = re.compile('#.+?#')
    topic = ''
    for r in regex.findall(text):
        topic += r + '\n'
    return topic


def getText(mblog):
    if mblog['isLongText']:
        text = mblog['longText']['longTextContent'] 
    else:
        soup = BeautifulSoup(mblog['text'], 'html.parser')
        text = ''
        for cstr in soup.strings:
            if len(cstr) > 1:
                text += cstr.strip() + '\t'
    return getTopic(text), text


# 输入检索词得到wbid，用户id及用户名
def get_info(search_list, since_date=None):
    print('Start Time: ' + str(datetime.now()))
    headers = {'User-Agent': 'Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6'}
    results_list = []
    results_dict = {}
    if_crawl = True
    for wd in search_list:
        wd_list = []
        # 将检索词编码，嵌入url得到不同词的url字典
        base_url = get_baseurl(wd)
        count = 0
        # 获取多页该检索词的结果页面
        for page in range(1, 250):
            print('This is page ' + str(page))
            this_url = base_url + str(page)
            try:
                # proxypool_url = 'http://127.0.0.1:5555/random'
                # proxies = {'http': 'http://' + requests.get(proxypool_url).text.strip()}
                r = requests.get(this_url, headers=headers)
                r.raise_for_status()
                r.encoding = r.apparent_encoding
                content = json.loads(r.text)
                if content.get('ok') == 1:
                    mblogs = jsonpath(content, '$.data.cards..mblog')
                    for mblog in mblogs:
                        mblog['created_at'] = standardize_date(mblog['created_at'])
                        this_topic, this_text = getText(mblog)
                        this_dict = {
                                    '检索词': str(wd),
                                    '用户id': mblog['user']['id'], 
                                    '用户名': mblog['user']['screen_name'], 
                                    '微博id': mblog['id'],
                                    '话题': this_topic,
                                    '微博正文': this_text,
                                    '发表时间': mblog['created_at']
                                }
                        if since_date:
                            since_date = datetime.strptime(since_date, '%Y-%m-%d')
                            created_at = datetime.strptime(mblog['created_at'], '%Y-%m-%d')
                            if (created_at > since_date):
                                if_crawl = False
                        else:
                            if_crawl = False
                        if not if_crawl:
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
                traceback.print_exc()
                continue

        # Store the results of all reachable pages to the list and dict
        results_list += wd_list + []
        results_dict[wd] = wd_list
        endtime = str(datetime.now()).split(' ')[1]
        print('Get %d weibo of %s at %s' % (len(wd_list), wd, endtime))
        time.sleep(5)
    return results_list, results_dict


def standardize_date(created_at):
    """标准化微博发布时间"""
    if u"刚刚" in created_at:
        created_at = datetime.now().strftime("%Y-%m-%d")
    elif u"分钟" in created_at:
        minute = created_at[:created_at.find(u"分钟")]
        minute = timedelta(minutes=int(minute))
        created_at = (datetime.now() - minute).strftime("%Y-%m-%d")
    elif u"小时" in created_at:
        hour = created_at[:created_at.find(u"小时")]
        hour = timedelta(hours=int(hour))
        created_at = (datetime.now() - hour).strftime("%Y-%m-%d")
    elif u"昨天" in created_at:
        day = timedelta(days=1)
        created_at = (datetime.now() - day).strftime("%Y-%m-%d")
    elif created_at.count('-') == 1:
        year = datetime.now().strftime("%Y")
        created_at = year + "-" + created_at
    return created_at


if __name__ == '__main__':
    te11 = '2020-05-31'
    get_query_wb(json=True, csv=True)
