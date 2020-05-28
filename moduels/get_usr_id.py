# -*- coding:utf-8 -*-

import requests
import json

#输入用户id
#返回用户粉丝id列表以及粉丝名字列表
def get_fans_info(uid):
    n=0
    fans_id_list, fans_name_list = [], []
    while True:

        if(n==0):
            url = "https://m.weibo.cn/api/container/getIndex?containerid=231051_-_fans_-_" + str(uid)
            n+=1
        else:
            url = "https://m.weibo.cn/api/container/getIndex?containerid=231051_-_fans_-_"+ str(uid)+"&since_id=" + str(n)
            n+=1

        html = requests.get(url)
        jsontext = json.loads(html.text)
        if jsontext['ok'] == 1:
            print("正在处理第"+str(n-1)+"页---------->")
            fans_list = jsontext['data']['cards'][0]['card_group']
            for fan in fans_list:
                fans_id_list.append(fan['user']['id'])
                fans_name_list.append(fan['user']['screen_name'])
        else:
            print("处理完毕")
            break

    return fans_id_list, fans_name_list


    






