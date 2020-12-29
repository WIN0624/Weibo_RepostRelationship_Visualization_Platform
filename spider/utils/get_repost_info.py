import os
import re
import time
import json
import requests
from retrying import retry
from jsonpath import jsonpath
from datetime import datetime
from utils.logger import Logger
from utils.csvWriter import csvWriter
from utils.agent import get_header, get_proxy
from utils.standarize_date import standardize_date


def word_repost_relationship(batch_num, temp_dir, searchList, breakpos=None):
    # 据进程名生成日志
    name = f'getRepost_batchNum{str(batch_num)}'
    log = Logger(name)
    logger = log.getLogger()

    # 断点处理
    if not breakpos:
        # 每个进程维护一个sublist转发关系的层级目录（临时）
        level_dir = temp_dir + f'temp_{name}/'
        if not os.path.exists(level_dir):
            os.mkdir(level_dir)
        # 生成写文件
        repost_file = temp_dir + name + '.csv'
        repost_writer = csvWriter(repost_file, repost=True)
    else:
        level_dir = temp_dir + breakpos['level_dir']
        repost_file = temp_dir + breakpos['repost_file']
        repost_writer = csvWriter(repost_file, repost=True, breakpos=True)
        # 先爬取完断点id，再对余下id按常规爬取
        get_repost_relationship(breakpos['center_bw_id'], repost_writer, level_dir, logger, breakpos)
        searchList = searchList[1:]

    # 常规爬取
    logger.info('Strat getting repost...')
    for id in searchList:
        get_repost_relationship(id, repost_writer, level_dir, logger)
    logger.info('Finish!')
    # 爬取完后，删除日志
    # 日志主要用于处理断点，若爬取完成则不再需要
    log.remove()


# 获取转发关系的主函数
def get_repost_relationship(bw_id, repost_writer, level_dir, logger, breakpos=None):
    # center_bw_id记录最原始的bw_id
    center_bw_id = bw_id
    # 类层次遍历处理转发关系
    # 为了节省内存，将每一层的层级关系写入level_dir

    # 断点处理
    if not breakpos:
        # 初始化层数为0，仍可以获取转发关系
        level = 1
        idList = [bw_id]
    else:
        level = breakpos['level']
        break_file = level_dir + f'Level_{level}_{center_bw_id}.csv'
        temp_writer = csvWriter(break_file, temp=True, breakpos=True)
        idList = temp_writer.get_idList(breakpos.get('break_id'))

    # 爬取转发
    while len(idList) > 0:
        # 创建下一层的原博文件，即该层的转发微博的微博id
        temp_file = level_dir + f'Level_{level+1}_{center_bw_id}.csv'
        if level == breakpos and breakpos.get('break_id'):
            temp_writer = csvWriter(temp_file, temp=True, breakpos=True)   # 断点为本层的中间，所以其下一层文件早已创建，直接往后添加
        else:
            temp_writer = csvWriter(temp_file, temp=True)                   # 非断点，则照常创建新文件

        # 获得该层所有bw_id的直接转发关系
        for bw_id in idList:
            get_repost_info(center_bw_id, bw_id, level, repost_writer, logger, temp_writer)
        # 获取下一level的原博id
        idList = temp_writer.get_idList()
        # 删除存储本层idList的文件
        if not level == 1:
            os.remove(level_dir + f'Level_{level}_{center_bw_id}.csv')
        level += 1
    # 爬取结束后，删除最后一次的temp_file
    os.remove(temp_file)


# 获取原博相关信息
@retry(stop_max_attempt_number=5, wait_fixed=3000)
def get_origin_info(bw_id, logger):
    try:
        time.sleep(5)
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
    # 转发信息爬取
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
                time.sleep(7)
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
                        flag = checkLevel(level, origin_user['screen_name'], data['raw_text'])
                        if flag:
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
                            # 将待爬取id放入下一轮爬取的id列表(即其作为原博时)
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
                        else:
                            continue
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


def checkLevel(level, origin_name, text):
    flag = False
    regex = re.compile(r'//@(\w+?)(\s?):')
    text = transfer(text)
    origin_name = transfer(text)
    try:
        fromer = regex.findall(text)[0][0]
    except IndexError:
        if level == 1:    # 没查找到相关内容
            flag = True
    if level > 1 and fromer == origin_name:   # 转发博文内容的前一级转发与当前原博昵称相同，则层级未紊乱
        flag = True
    return flag


def transfer(text):
    if '-' in text:  # '-'在正则表达式中有其他用处
        text = text.replace('-', '_')
    if '·' in text:
        text = text.replace('·', '_')
    return text
