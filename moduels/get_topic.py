# -*- coding: utf-8 -*-
"""
Created on Mon May 11 20:34:41 2020

@author: Le_C
"""

import os
import time
import requests
from lxml import etree
import csv
import pandas as pd
from fileFormatConversion import hot2json 

def get_hot(conver2json):
    proxypool_url = 'http://127.0.0.1:5555/random'
    proxies = {'http': 'http://' + requests.get(proxypool_url).text.strip()}
    url = "https://s.weibo.com/top/summary?cate=realtimehot"
    headers={
        'Host': 's.weibo.com',
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3',
        'Accept-Encoding': 'gzip, deflate, br',
        'Accept-Language': 'zh-CN,zh;q=0.9',
        'Connection': 'keep-alive',
        'Referer': 'https://weibo.com/',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36'
    }

    r = requests.get(url,headers=headers,proxies=proxies)
    # print(r.status_code)
    
    html_xpath = etree.HTML(r.text)
    data = html_xpath.xpath('//*[@id="pl_top_realtimehot"]/table/tbody/tr/td[2]')
    num = -1
    
    # 解决存储路径
#    time_name = time.strftime('%Y{y}%m{m}%d{d}%H{h}',time.localtime()).format(y='年', m='月', d='日',h='点')
    time_name = time.strftime('%Y%m%d%H%M',time.localtime())
    # time_path = time.strftime('%Y{y}%m{m}%d{d}',time.localtime()).format(y='年', m='月', d='日')
    # time_name = time.strftime('%Y{y}%m{m}%d{d}%H{h}',time.localtime()).format(y='年', m='月', d='日',h='点')
    # root = "./" + time_path + "/"
    # path = root + time_name + '.md'
    # if not os.path.exists(root):
    #     os.mkdir(root)
    
    # 最终文件存储位置
    #root = all_path  + "/"
    path =time_name + '.csv'
    
    #print(path)
    # 文件头部信息
    with open(path,'a',newline='') as f:
        wt = csv.writer(f)
        wt.writerow(['index','topic','score'])
    f.close()
    
    for tr in (data):
        title = tr.xpath('./a/text()')
        hot_score = tr.xpath('./span/text()')
        
        num += 1
    
        # 过滤第 0 条
        if num == 0:
            pass
        else:
            with open(path,'a') as f:
                wt = csv.writer(f)
                wt.writerows([[num,title[0],hot_score[0]]])
                
    csv_data = pd.read_csv(path,encoding='gbk')
    csv_df = pd.DataFrame(csv_data)
    topic_list = csv_df['topic'].tolist()
    csv_dict = csv_df.to_dict(orient="dict")
    if(conver2json):
        hot2json(path)
    return csv_df,csv_dict,topic_list
    
# if __name__ == '__main__':
#     conver2json = True
#     csv_df, csv_dict, topic_list = get_hot(conver2json)