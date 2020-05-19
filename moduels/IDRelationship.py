# -*- coding:utf-8 -*-

import csv
import json
import requests
import time
import re
from moduels.additionalFeatures import judge_origin

# 输入用户id，获取containerid
def get_user_containerid(user_id):
    url = 'http://m.weibo.cn/api/container/getIndex?type=uid&value={user_id}'.format(user_id=user_id)
    resp = requests.get(url)
    jsondata = resp.json()
    jsondata = jsondata['data']
    fans_id=jsondata.get('follow_scheme')
    items = re.findall(r"&lfid=(\w+)*", fans_id, re.M)
    for i in items:
        return i

# 输入用户id和containerid，获取luicode、lfid，更新containerid
def get_luicode_lfid(user_id,containerid):
    url = 'https://m.weibo.cn/api/container/getIndex?uid=' \
          ''+str(user_id)+'&type=uid&value='+str(user_id)+\
          '&containerid='+str(containerid)
    proxypool_url = 'http://127.0.0.1:5555/random'
    proxies = {'http': 'http://' + requests.get(proxypool_url).text.strip()}
    response = requests.get(url, proxies=proxies)
    html = json.loads(response.content.decode('utf-8'))
    s = html.get('data').get('scheme')
    luicode = s[s.find('luicode=')+8:s.find('&lfid=')]
    lfid = s[s.find('&lfid=')+6:]
    for i in html.get('data').get('tabsInfo').get('tabs'):
        if i.get('tabKey') == 'weibo':
            containerid = i.get('containerid')
    return {'luicode':luicode,
            'lfid':lfid,
            'containerid':containerid
            }

# 输入user_id,luicode,lfid,containerid,获取微博博文bw_id
def get_bw_id(user_id,d): # 用户id和主页前缀
    b = True
    n = 0
    sid = '1'
    luicode = d['luicode']
    lfid = d['lfid']
    containerid = d['containerid']
    sheader =  'https://m.weibo.cn/api/container/getIndex?uid=' \
          ''+str(user_id)+'&luicode='+str(luicode)+'&lfid='+str(lfid)+\
          '&type=uid&value='+str(user_id)+'&containerid='+str(containerid)
    url = sheader
    error = {}
    while b:
        try:

            n += 1
            print('正在处理主页--->', url)
            proxypool_url = 'http://127.0.0.1:5555/random'
            proxies = {'http': 'http://' + requests.get(proxypool_url).text.strip()}
            response = requests.get(url,proxies=proxies)
            html = json.loads(response.content.decode('utf-8'))

            if 'data' in html.keys():
                if 'since_id' in html.get('data').get('cardlistInfo'):
                    if  html.get('data').get('cardlistInfo').get('since_id') == sid:
                        break
                    elif sid == '':
                        break
                    else:
                        sid = html.get('data').get('cardlistInfo').get('since_id')

                else:
                    break

                if 'cards' in html.get('data'):
                    for i in html.get('data').get('cards'):
                        if i.get('mblog',-1) != -1:
                            screen_name = i['mblog'].get('user').get('screen_name')
                            # reposts_count = i['mblog'].get('reposts_count')
                            # reposts_count: 11
                            # comments_count: 20
                            # attitudes_count: 73
                            content = [user_id,screen_name,i['mblog'].get('id')]
                            write_csv(content,'uid+sn+bwid.csv')  # 保存
                else:
                    break

            time.sleep(1)
        except Exception as e:
            print('请求主页出错--->', url)
            if str(e) == 'Expecting value: line 1 column 1 (char 0)' and error.get(url, -1) == -1:
                error[url] = 1
                n -= 1
                print('重新请求主页--->', url)
                time.sleep(5)
            elif str(e) == 'Expecting value: line 1 column 1 (char 0)' and error.get(url, -1) == 1:
                time.sleep(5)
            else:
                b = False
                print('错误信息：\n',e)
        url = sheader + '&since_id=' + str(sid)
    print('共处理主页 ',n)

# 输入bw_id ，filename为存储文件名，得到微博转发信息
def get_bw_info(user_dict,filename):
    b = True
    n = 0
    error = {}
    bw_id = user_dict['bw_id']
    rp_count = get_rca_count(bw_id)['reposts_count'] # 转发次数
    while b:
        try:
            o = judge_origin(bw_id)  # 判断是否为原创
            n += 1
            url = 'https://m.weibo.cn/api/statuses/repostTimeline?id=' + str(bw_id) + '&page=' + str(n)
            print('正在处理-->',url)
            proxypool_url = 'http://127.0.0.1:5555/random'
            proxies = {'http': 'http://' + requests.get(proxypool_url).text.strip()}
            response = requests.get(url, proxies=proxies)
            html = json.loads(response.content.decode('utf-8'))
            if 'data' in html.keys():
                if 'data' in html.get('data').keys():
                    for i in html.get('data').get('data'):
                        fs_id = i.get('user').get('id')
                        fs_bw_id = i.get('id')
                        fs_screen_name = i.get('user').get('screen_name')
                        write_csv([user_dict['user_id'],user_dict['screen_name'],bw_id,o,
                                   rp_count,fs_id,fs_screen_name,fs_bw_id],'rp_relationship.csv')
                        #[用户id，用户名，微博id，是否原创，被转发的次数，转发的粉丝id，粉丝名，转发的微博id]
            else:
                b = False
        except Exception as e :
            if str(e) == 'Expecting value: line 1 column 1 (char 0)' and error.get(url, -1) == -1:
                error[url] = 1
                n -= 1
                time.sleep(5)
                print('重新请求-->', url)
            elif str(e) == 'Expecting value: line 1 column 1 (char 0)' and error.get(url, -1) == 1:
                time.sleep(5)
            else:
                b = False
                print('Error:\n',e)
        time.sleep(1)

# 输入微博id，获取转发/评论/点赞数
def get_rca_count(bw_id):
    error = {}
    try:
        url = 'https://m.weibo.cn/statuses/extend?id=' + str(bw_id)
        print('正在处理-->',url)
        proxypool_url = 'http://127.0.0.1:5555/random'
        proxies = {'http': 'http://' + requests.get(proxypool_url).text.strip()}
        response = requests.get(url, proxies=proxies)
        html = json.loads(response.content.decode('utf-8'))
        if 'data' in html.keys():
            i = html.get('data')
            reposts_count = i.get('reposts_count')
            comments_count = i.get('comments_count')
            attitudes_count = i.get('attitudes_count')
            return {'reposts_count':reposts_count,
                    'comments_count':comments_count,
                    'attitudes_count':attitudes_count
                    }  # 获取
    except Exception as e :
        if str(e) == 'Expecting value: line 1 column 1 (char 0)' and error.get(url, -1) == -1:
            error[url] = 1
            time.sleep(5)
            get_rca_count(bw_id)
            print('重新请求-->', url)
        else:
            print('Error:\n',e)
            return None
        time.sleep(1)

# 初始化文件,result_headers为列名, filename为文件名
def origin_file(result_headers,filename):
    with open(filename, 'w', encoding='utf-8-sig', newline='') as f:
        writer = csv.writer(f)
        writer.writerows([result_headers])

# 将爬取的信息写入csv文件
def write_csv(result_data,filename): # result_data数组
    try:
        with open(filename,'a',encoding='utf-8-sig',newline='') as f:
            writer = csv.writer(f)
            writer.writerow(result_data)
    except Exception as e:
        print('Error: ', e)