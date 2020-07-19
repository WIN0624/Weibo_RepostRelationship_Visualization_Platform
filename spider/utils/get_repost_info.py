import os
import time
import json
import requests
from retrying import retry
from jsonpath import jsonpath
from datetime import datetime
from utils.logger import getLogger
from utils.csvWriter import csvWriter
from utils.loadConfig import load_config
from utils.agent import get_header, get_proxy
from utils.standarize_date import standardize_date


# 获取转发关系的主函数
def get_repost_relationship(bw_id, repost_writer, logger):
    # 初始化层数为1，仍可以获取转发关系
    level = 1
    # center_bw_id记录最原始的bw_id
    center_bw_id = bw_id
    # 类层次遍历处理转发关系
    # 为了节省内存，将每一层的层级关系写入文件
    temp_dir = load_config(temp=True)
    temp_file = temp_dir + f'Level_{level+1}_{center_bw_id}.csv'
    temp_writer = csvWriter(temp_file, temp=True)
    # 写入该id一级转发信息并将转发bw_id放入队列
    get_repost_info(center_bw_id, bw_id, level, repost_writer, logger, temp_writer)
    # 获取一级转发的微博id
    idList = temp_writer.get_idList()

    if len(idList) == 0:
        logger.error(f'No repost of center_bw {center_bw_id}.')
    while len(idList) > 0:
        level += 1
        # 删除存储本层idList的文件
        os.remove(temp_file)
        # 创建下一层的原博文件，即该层的转发微博id
        temp_file = temp_dir + f'Level_{level+1}_{center_bw_id}.csv'
        temp_writer = csvWriter(temp_file, temp=True)
        # 获得该层所有bw_id的直接转发关系
        for bw_id in idList:
            get_repost_info(center_bw_id, bw_id, level, repost_writer, logger, temp_writer)
        idList = temp_writer.get_idList()


# 获取原博相关信息
@retry(stop_max_attempt_number=5, wait_fixed=3000)
def get_origin_info(bw_id, logger):
    try:
        time.sleep(3)
        url = 'https://m.weibo.cn/statuses/show?id=' + str(bw_id)
        r = requests.get(url, headers=get_header(), proxies=get_proxy())
        r.raise_for_status()
        r.encoding = r.apparent_encoding
        content = json.loads(r.text)
        if content.get('ok') == 1:
            # 默认为原创
            origin = True
            # 若包含被转发微博信息，判断为转发
            if 'retweeted_status' in r.text:
                origin = False
            # 获取转发页数
            rp_count = jsonpath(content, '$.data.reposts_count')[0]
            if rp_count > 0:
                rp_page = int(rp_count) / 10 + 1
            else:
                rp_page = 0
            # 获取被转发用户信息
            origin_user = jsonpath(content, '$.data.user')[0]
            info_dict = {
                'bw_id': bw_id,
                'origin': origin,
                'rp_count': rp_count,
                'rp_page': rp_page,
                'origin_user': origin_user
            }
            return info_dict
        else:
            return False
    except Exception as e:
        logger.error(f"Cannot get details of weibo {bw_id}. {e}")


def get_repost_info(center_bw_id, bw_id, level, writer, logger, temp_writer, since_date=None):
    if_crawl = True
    error = {}
    idList = []
    # 获取原博主信息
    origin_info = get_origin_info(bw_id, logger)
    # 获取成功时：
    if origin_info:
        # 原创信息
        origin = origin_info['origin']
        # 用户信息
        origin_user = origin_info['origin_user']
        # 转发数和转发总页数
        rp_count = origin_info['rp_count']
        page = origin_info['rp_page']
    # 可能出现微博删除或无法获取的情况，则不再获取该bw_id
    else:
        return None
    if page == 0:
        logger.info(f'Center bw : {center_bw_id}. level: {level}. No repost of this bw {bw_id}.')
        writer.write_csv(None, END=True, center_bw_id=center_bw_id, origin_info=origin_info, level=level)
    else:
        logger.info(f'Center bw : {center_bw_id}. Get {page} pages of bw {bw_id}.')
        base_url = 'https://m.weibo.cn/api/statuses/repostTimeline?id=' + str(bw_id) + '&page='
        page_count = 0
        while (page_count <= page):
            page_count += 1
            result_list = []
            try:
                time.sleep(3)
                this_url = base_url + str(page_count)
                logger.info(f'Center bw : {center_bw_id}. level: {level}. Crawling page {page_count} of bw {bw_id}.')
                r = requests.get(this_url, headers=get_header(), proxies=get_proxy())
                r.raise_for_status()
                r.encoding = r.apparent_encoding
                content = json.loads(r.text)
                if content.get('ok') == 1:
                    datas = jsonpath(content, '$.data.data.*')
                    for data in datas:
                        data['created_at'] = standardize_date(data['created_at'])
                        this_dict = {
                            'center_bw_id': center_bw_id,
                            'user_id': origin_user['id'],
                            'screen_name': origin_user['screen_name'],
                            'bw_id': bw_id,
                            'origin': origin,
                            'repost_count': rp_count,
                            'fs_count': origin_user['followers_count'],
                            'fs_user_id': data['user']['id'],
                            'fs_screen_name': data['user']['screen_name'],
                            'fs_bw_id': data['id'],
                            'fs_fans_count': data['user']['followers_count'],
                            'level': level,
                            'raw_text': data['raw_text'],
                            'created_at': data['created_at']
                        }
                        # 将待爬取id放入下一轮爬取的id列表，记录将其作为原博时所处level：为当前level+1
                        idList.append({'bw_id': data['id']})
                        # 判断是否是规定时间之后产生的微博
                        if since_date:
                            since_date = datetime.strptime(since_date, '%Y-%m-%d')
                            created_at = datetime.strptime(data['created_at'], '%Y-%m-%d')
                            if (created_at > since_date):
                                if_crawl = False
                        else:
                            if_crawl = False
                        if not if_crawl:
                            result_list.append(this_dict)
                    # 将符合规定时间的内容写入csv
                    writer.write_csv(result_list)
                else:
                    continue
            except Exception as e:
                if error.get(this_url) is None:
                    error[this_url] = 1
                    page_count -= 1
                    time.sleep(60)
                else:
                    logger.error(f"Cannot get page {page_count} of bw {bw_id}. {e}")
        # 爬取完所有页数，将idList写入对应的level文件
        if idList:
            temp_writer.write_csv(idList)
