# -*- coding:utf-8 -*-

import requests
import json

#input:
#usr_id
#number_of_pages(default=10, if page=0 means get all pages)
#write_in_a_file(default:False, if write_in_a_file=True will return 3 files)

#output:fans_id_list, fans_name_list

def get_fans_info(uid, page=10,write_in_a_file=False):
    n=0
    fans_id_list, fans_name_list = [], []

    if(page==0):

        while(True):

            if (n == 0):
                url = "https://m.weibo.cn/api/container/getIndex?containerid=231051_-_fans_-_" + str(uid)
                n += 1
            else:
                url = "https://m.weibo.cn/api/container/getIndex?containerid=231051_-_fans_-_" + str(
                    uid) + "&since_id=" + str(n)
                n += 1

            html = requests.get(url)
            jsontext = json.loads(html.text)

            if jsontext['ok'] == 1:

                print("Processing Page " + str(n) + "---------->")
                fans_list = jsontext['data']['cards'][0]['card_group']
                if "user" in fans_list[0].keys():
                    for fan in fans_list:
                        fans_id_list.append(fan['user']['id'])
                        fans_name_list.append(fan['user']['screen_name'])
            else:
                print("Finished")
                break
    else:

        for i in range(page):

            if(n==0):
                url = "https://m.weibo.cn/api/container/getIndex?containerid=231051_-_fans_-_" + str(uid)
                n+=1
            else:
                url = "https://m.weibo.cn/api/container/getIndex?containerid=231051_-_fans_-_"+ str(uid)+"&since_id=" + str(n)
                n+=1

            html = requests.get(url)
            jsontext = json.loads(html.text)

            if jsontext['ok'] == 1:

                print("Processing Page "+str(n)+"---------->")
                fans_list = jsontext['data']['cards'][0]['card_group']
                if "user" in fans_list[0].keys():
                    for fan in fans_list:
                        fans_id_list.append(fan['user']['id'])
                        fans_name_list.append(fan['user']['screen_name'])
            else:
                print("Finished")
                break


    if(write_in_a_file):
        with open("fans_id.txt", "w") as f1:
            for id in fans_id_list:
                f1.write(str(id)+"\n")
        with open("fans_name.txt", "w") as f2:
            for name in fans_name_list:
                f2.write(str(name)+"\n")
        with open("id2name.txt", "w") as f3:
            for id,name in zip(fans_id_list,fans_name_list):
                f3.write(str(id)+":"+str(name) + "\n")

    return fans_id_list, fans_name_list










