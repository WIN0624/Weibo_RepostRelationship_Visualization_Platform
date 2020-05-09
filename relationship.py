# 通过微博博文id建立转发关系
# 输入用户id和微博博文id

import csv
import json
import requests
import time

# 爬取单个微博
def get_fs_info(u_id,bw_id):
    b = True
    n = 0
    error = {}
    while b:
        try:
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
                        fsbw_id = i.get('id')
                        write_csv([u_id,bw_id,fs_id,fsbw_id])
            else:
                b = False
        except Exception as e :
            if str(e) == 'Expecting value: line 1 column 1 (char 0)' and error.get(url, -1) == -1:
                error[url] = 1
                n -= 1
                time.sleep(5)
            elif str(e) == 'Expecting value: line 1 column 1 (char 0)' and error.get(url, -1) == 1:
                time.sleep(5)
            else:
                b = False
                print('Error:\n',e)
        time.sleep(1)

def write_csv(result_data): # result_data四元数组
    """将爬取的信息写入csv文件"""
    try:
        with open('relationship.csv','a',encoding='utf-8-sig',newline='') as f:
            writer = csv.writer(f)
            writer.writerow(result_data)
    except Exception as e:
        print('Error: ', e)

if __name__ == '__main__':
    result_headers = [
        '博主id',
        '博文id',
        '转发博主id',
        '转发博文id',
    ]
    with open('relationship.csv', 'w', encoding='utf-8-sig', newline='') as f:
        writer = csv.writer(f)
        writer.writerows([result_headers])
    # 在此处读取 'user+bw.csv' 并进行遍历
    u_id = 5984601062
    bw_id = 4502405230434173
    get_fs_info(u_id,bw_id)
