import csv
import json
import time
import requests
from jsonpath import jsonpath
from utils.agent import get_header, get_proxy


def get_more_topic(query, epoch, topic_dir):
    topic_list = []
    page_count = 0
    # 获取返回的总页数
    base_url = 'https://m.weibo.cn/api/container/getIndex?containerid=100103type%3D38%26q%3D' + str(query) + '%26t%3D0&page_type=searchall'
    try:
        r = requests.get(base_url, headers=get_header(), proxies=get_proxy())
        r.raise_for_status()
        page = json.loads(r.text)['data']['cardlistInfo']['total'] / 10
        print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]  EPOCH: {epoch}. Keyword: {query}. Get {page} pages of new topics.')
    except Exception:
        time.sleep(60)
        get_more_topic(query, epoch, topic_dir)
    while(page_count <= page):
        time.sleep(3)
        page_count += 1
        this_url = base_url + '&page=' + str(page_count)
        print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]  Crawling Topic. Page {page_count} of keyword {query}')
        try:
            r = requests.get(this_url, headers=get_header(), proxies=get_proxy())
            r.raise_for_status()
            r.encoding = r.apparent_encoding
            content = json.loads(r.text)
            if content['ok'] == 1:
                items = jsonpath(content, '$..card_group..title_sub')
                for item in items:
                    temp = item.strip('#')
                    if temp != query.strip():
                        topic_list.append([temp])
            else:
                continue
        except Exception:
            print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]  Error happen in page --->" + str(page_count)')

    # 结果写入文件
    with open(topic_dir + 'Topics_' + str(epoch) + '.csv', 'a', encoding='utf-8', newline='') as f:
        writer = csv.writer(f)
        writer.writerows(topic_list)

    # 获取元素输出
    print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]  Finished Crawling Topic. Get {len(topic_list)} new topic for keyword {query}')
