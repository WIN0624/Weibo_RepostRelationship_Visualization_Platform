# -*- coding:utf-8 -*-

# 判断是否为原创微博
def judge_origin(bw_id):
    url = 'https://m.weibo.cn/detail/' + str(bw_id)
    response = requests.get(url)
    b = True # 原创
    if 'retweeted_status' in response.text:
        b = False # 转发
    return b