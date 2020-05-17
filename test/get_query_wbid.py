# -*- coding: utf-8 -*-
"""
Created on Sun May 17 2020
@author: Le_C
"""

import requests
import json
from jsonpath import jsonpath
import traceback
from datetime import datetime
from urllib.parse import quote


def get_url_list(search_list):
    url_dict = {}
    for wd in search_list:
        # 将中文编码为ascii
        url = 'http://m.weibo.cn/container/getIndex?containerid=100103type%3D1%26q%3D' + quote(wd) + "&page_type=searchall&page="
        # 加入每个词对应的查询页（未指定页码）
        url_dict[wd] = url
    return url_dict


def get_info(search_list, url_list):
    print('Start Time: ' + str(datetime.now()))
    results = {}
    for wd in search_list:
        id_list = []
        for page in range(1, 10):
            this_url = url_list[wd] + str(page)
            try:
                # add header for the crawler
                headers = {'User-Agent': 'Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6'}
                r = requests.get(this_url, headers=headers)
                r.raise_for_status()
                r.encoding = r.apparent_encoding
                content = json.loads(r.text)
                if content.get('ok') == 1:
                    id_list += jsonpath(content, '$.data.cards..mblog.id]')
            except Exception:
                traceback.print_exc()
        results[wd] = id_list
        results_json = json.dumps(results, ensure_ascii=False)
    return results_json


if __name__ == '__main__':
    # add search list
    search_list = ['新型冠状病毒', 'AI', '经济学', '管理学']
    url_dict = get_url_list(search_list)
    results_json = get_info(search_list, url_dict)
    with open('search.json', 'w') as f:
        f.write(results_json)