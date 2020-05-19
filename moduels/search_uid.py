# ***************************************************************************
# 利用微博移动版的搜索界面查询指定用户的相关信息
# input: searchQuery，精确的用户信息
# output: user_info字典，相关参数有：关注数量，粉丝数量，性别，用户id，微博名称，认证信息
# ***************************************************************************

# -*- coding:UTF-8 -*-

'''
@author: panzy
'''

import requests
import json
import sys
from imp import reload

def pageSearchFor_uid(searchQuery):
    try:
        info = {}
        response = requests.get("https://m.weibo.cn/api/container/getIndex?containerid=100103type%3D3%26q%3D" + searchQuery + "%26t%3D0&page_type=searchall")
        print(response.url)
        response.raise_for_status()
        jsonscript = json.loads(response.content.decode('utf-8'))
        if jsonscript.get('ok') == 1:
            # length = len(json.get('data').get('cards')[1].get('card_group'))
            for userCard in jsonscript.get('data').get('cards')[1].get('card_group'):
                if userCard.get('user').get('screen_name') == searchQuery:
                    info['followers_count'] = userCard.get('user').get('followers_count')
                    print(info['followers_count'])
                    info['follow_count'] = userCard.get('user').get('follow_count')
                    info['gender'] = userCard.get('user').get('gender')
                    info['uid'] = userCard.get('user').get('id')
                    info['screen_name'] = userCard.get('user').get('screen_name')
                    info['verified'] = userCard.get('user').get('verified')
                    info['profile_url'] = userCard.get('user').get('profile_url')
                    print("successfully find the user!")
                    break
            if info == {}:
                print("Warning: 无精确匹配用户")
        else:
            print("Warning: 该用户不存在")
    except Exception as e: 
        print('Error:\n',e)

    return info

# *************************************
# 测试用
# if __name__ == "__main__":
#     reload(sys)
#     sys.setdefaultencoding('utf8') 
#     info = pageSearchFor_uid('')
#     print(info['uid'])
#     print(info['followers_count'])
#     print(info['follow_count'])
#     print(info['gender'])
#     print(info['uid'])
#     print(info['screen_name'])
#     print(info['verified'])                
#     print(info['profile_url'])
# *************************************