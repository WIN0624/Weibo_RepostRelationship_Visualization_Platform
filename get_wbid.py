# 尝试用用户id获得用户所有的微博博文id
# 输入用户id
# 输出微博博文id

import csv
import requests
import re
import time
import json

def get_user_containerid(user_id):   #containerid和usid不一致，查看用户的关注列表需要他的containerid，usid用于获取用户主页信息
    url = 'http://m.weibo.cn/api/container/getIndex?type=uid&value={user_id}'.format(user_id=user_id)
    resp = requests.get(url)
    jsondata = resp.json()
    jsondata = jsondata['data']
    fans_id=jsondata.get('follow_scheme')
    items = re.findall(r"&lfid=(\w+)*", fans_id, re.M)
    for i in items:
        return i

def get_luicode_lfid(sheader):
    url = sheader
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
    return [luicode,lfid,containerid]

# 获取微博博文bw_id
def get_bw_id(user_id,sheader): # 用户id和主页前缀
    b = True
    n = 0
    sid = ''
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
                        content = [user_id,i['mblog'].get('id')]
                        write_file(content)
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



def write_file(content):
    with open('user+bw.csv','a') as f:
        writer = csv.writer(f)
        writer.writerow(content)

if __name__ == '__main__':

    result_headers = [
        '博主id',
        '博文id',
    ]
    with open('user+bw.csv', 'w') as f:
        writer = csv.writer(f)
        writer.writerow(result_headers)
    # 此处读取 '各界大v用户id.txt' 并进行遍历
    user_id = 3537176965
    containerid = get_user_containerid(str(user_id))

    sheader = 'https://m.weibo.cn/api/container/getIndex?uid=' \
              ''+str(user_id)+'&type=uid&value='+str(user_id)+\
              '&containerid='+str(containerid)
    l = get_luicode_lfid(sheader)
    sheader = 'https://m.weibo.cn/api/container/getIndex?uid=' \
              ''+str(user_id)+'&luicode='+str(l[0])+'&lfid='+str(l[1])+\
              '&type=uid&value='+str(user_id)+'&containerid='+str(l[2])
    get_bw_id(user_id,sheader)
# https://m.weibo.cn/api/container/getIndex?uid=1623886424&luicode=10000011&lfid=100103type%3D1%26t%3D10%26q%3D%23%E5%AF%BC%E6%BC%94%E7%BD%97%E6%96%87%E5%8E%BB%E4%B8%96%23&type=uid&value=1623886424&containerid=1076031623886424