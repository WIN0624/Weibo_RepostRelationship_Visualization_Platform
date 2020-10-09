# -*- coding: utf-8 -*-
"""
Created on Sun May 17 2020
@author: Ying, Le_C
"""
import re
import time
import json
import requests
from retrying import retry
from bs4 import BeautifulSoup
from utils.agent import get_header, get_proxy
from datetime import datetime
from jsonpath import jsonpath
from urllib.parse import quote
from utils.logger import Logger
from utils.standarize_date import standardize_date


def word_get_query_info(wd, writer):
    log = Logger(f'getQuery_{wd}')
    logger = log.getLogger()
    logger.info(f'Keyword: {wd}. Start crawling ...')
    get_query_info(wd, writer, logger)


def getTopic(text):
    regex = re.compile('#.+?#')
    topic = ''
    for r in regex.findall(text):
        topic += r + ' '
    return topic


def getText(mblog):
    if mblog['isLongText']:
        text = mblog['longText']['longTextContent']
    else:
        soup = BeautifulSoup(mblog['text'], 'html.parser')
        text = ''
        for cstr in soup.strings:
            if len(cstr) > 1:
                text += cstr.strip() + ' '
    return getTopic(text), text


# 获取返回微博总页数
@retry(stop_max_attempt_number=5, wait_fixed=3000)
def get_Page(wd, base_url, logger):
    r = requests.get(base_url, headers=get_header(), proxies=get_proxy())
    r.raise_for_status()
    page = json.loads(r.text)['data']['cardlistInfo']['total']/10 + 1
    logger.info(f'Keyword: {wd}. Get {page} pages of returned weibo.')
    return page


# 输入检索词得到wbid，用户id及用户名
def get_query_info(wd, writer, logger, since_date=None):
    if_crawl = True
    page_count = 0
    error = {}
    # 将检索词编码，嵌入url得到不同词的url字典
    # 爬取检索页面下热门栏的页面
    base_url = 'https://m.weibo.cn/api/container/getIndex?containerid=100103type%3D60%26q%3D' + quote(wd) + '%26t%3D0&page_type=searchall'
    # 计算可获取的总页数
    page = get_Page(wd, base_url, logger)
    # 获取包含检索词的相关微博
    while (page_count <= page):
        result_list = []
        page_count += 1
        this_url = base_url + '&page=' + str(page_count)
        # logger.info(f'Page {page_count}: {this_url}')
        try:
            time.sleep(3)
            r = requests.get(this_url, headers=get_header(), proxies=get_proxy())
            logger.info(f'Crawling Query. Page {page_count} of keyword {wd}')
            r.raise_for_status()
            r.encoding = r.apparent_encoding
            content = json.loads(r.text)
            if content.get('ok') == 1:
                mblogs = jsonpath(content, '$.data.cards..mblog')
                for mblog in mblogs:
                    # 含有该键的mblog表示该条微博不是原创微博
                    if mblog.get('retweeted_status'):
                        continue
                    mblog['created_at'] = standardize_date(mblog['created_at'])
                    this_topic, this_text = getText(mblog)
                    this_dict = {
                                'keyword': str(wd),
                                'user_id': mblog['user']['id'],
                                'screen_name': mblog['user']['screen_name'],
                                'bw_id': mblog['id'],
                                'repost_count': mblog['reposts_count'],
                                'topic': this_topic,
                                'content': this_text,
                                'created_at': mblog['created_at']
                            }
                    if since_date:
                        since_date = datetime.strptime(since_date, '%Y-%m-%d')
                        created_at = datetime.strptime(mblog['created_at'], '%Y-%m-%d')
                        if (created_at > since_date):
                            if_crawl = False
                    else:
                        if_crawl = False
                    if not if_crawl:
                        result_list.append(this_dict)
                # 将该页面符合规定时间的内容写入csv
                writer.write_csv(result_list)
            else:
                continue
        except Exception as e:
            # 若第一次错误，则将url加入error，并休息60s
            if error.get(this_url) is None:
                error[this_url] = 1
                page_count -= 1
                time.sleep(60)
            # 若第二次错误，则报错
            else:
                logger.error(f'Page {page_count} failed. {e}')
