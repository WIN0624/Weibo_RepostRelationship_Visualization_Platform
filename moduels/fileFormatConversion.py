# -*- coding:utf-8 -*-

import csv
import json
import pandas as pd
import os

# 'rp_relationship.csv'
# 以加的方式进行存储，使用前需要先调用origin—file
# 用于将re_relationship.csv转成需要的json
def csv_to_json(csv_filename,json_filename):
    data=pd.read_csv(csv_filename, encoding='utf-8')
    data = data.T
    data_user = data[0:5]
    data_wb = data[2:3].T
    data_fs = data[5:]
    data_user.to_json('data_user.json')
    data_fs.to_json('data_fs.json')
    with open('data_user.json','r') as f:
        data_user = f.read()
    with open('data_fs.json','r') as f:
        data_fs = f.read()
    bwid = []
    data_user = json.loads(data_user)
    data_fs = json.loads(data_fs)
    s = ''
    for i in data_wb['bw_id']:
        bwid.append(i)
    count = 0
    for i in range(len(bwid)):
        if i==0:
            s_add = str(count) + ':' +str(data_user[str(i)])[:-1] + ',"repost_info":[' + str(data_fs[str(i)])
            s += s_add
        elif bwid[i] == bwid[i-1]:
            s_add = ','+str(data_fs[str(i)])
            s += s_add
        else:
            count += 1
            s_add = ']},'+str(count)+':' +str(data_user[str(i)])[:-1] + ',"repost_info":[' + str(data_fs[str(i)])
            s += s_add
    s = ('{'+s+']}}')
    s = eval(s)
    s = json.dumps(s,ensure_ascii=False)
    with open(json_filename, 'a', encoding='utf-8') as f:
        f.write(s)
    os.remove('data_fs.json')
    os.remove('data_user.json')

def hot2json(csv_file):
#    time_name = time.strftime('%Y%m%d%H',time.localtime())
    json_file =csv_file[:-4] + '.json'
    csv_file = open(csv_file,'r')
    json_file = open(json_file,'w')
    fieldnames=('index','topic','score')
    reader = csv.DictReader(csv_file,fieldnames)
    cnt=0
    json_file.write('[')
    for row in reader:
        if cnt == 0:
            pass
        else:
            json.dump(row,json_file,ensure_ascii=False)
            json_file.write(','+'\n')
        cnt += 1
    json_file.write(']')
    print("finish")