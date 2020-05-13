# -*- coding:utf-8 -*-

from moduels import fileFormatConversion
from moduels import IDRelationship

u_id = 1288739185
# screen_name = '关晓彤'
# bw_id = 4503001732052045
# get_fs_info(u_id, screen_name, bw_id)


# # 获取用户id下的所有微博id,保存在 uid+sn+bwid.csv
# containerid = IDRelationship.get_user_containerid(u_id)
# d = IDRelationship.get_luicode_lfid(u_id,containerid)
# IDRelationship.origin_csv_file(['user_id','screen_name','bw_id'],'uid+sn+bwid.csv') # 初始化文件
# IDRelationship.get_bw_id(u_id,d) # 获取到一个csv文件 uid+sn+bwid.csv


# 获取微博转发关系,保存在re_relationship.csv
with open('uid+sn+bwid.csv','r',encoding='utf-8') as f:
    info = f.readlines()
IDRelationship.origin_csv_file(['user_id','screen_name','bw_id','origin','reposts_count','fs_user_id','fs_screen_name','fs_bw_id'],'rp_relationship.csv')
for i in info:
    i = i.replace('\n','').split(',')
    if i != [''] and i[2] != 'bw_id':
        user_dict = {
            'user_id': i[0],
            'screen_name':i[1],
            'bw_id': i[2],
        }
        IDRelationship.get_bw_info(user_dict,'rp_relationship.csv')

# # 把 rp_relationship.csv 存储为合适的json格式,保存在rp_relationship.json
# with open('rp_relationship.json','w') as f:
#     f.write('')
# fileFoematConversion.csv_to_json('rp_relationship.csv','rp_relationship.json')