import os
import csv
import pandas as pd
from utils.merge_csv import mergeCSV


class csvWriter(object):

    def __init__(self, filename, search=False, repost=False, temp=False, breakpos=False):
        self.filename = filename
        self.search = search
        self.repost = repost
        self.temp = temp
        if self.search:
            self.header = ['keyword', 'user_id', 'screen_name', 'bw_id', 'repost_count', 'topic', 'content', 'created_at']
        if self.repost:
            self.header = ['center_bw_id', 'user_id', 'screen_name', 'bw_id', 'origin', 'repost_count', 'fs_count', 'fs_user_id', 'fs_screen_name', 'fs_bw_id', 'fs_fans_count', 'level', 'raw_text', 'created_at']
        if self.temp:
            self.header = ['bw_id']
        if not breakpos:
            self.create_csv()

    # 创建初始空文件
    def create_csv(self):
        if not os.path.exists(self.filename):
            with open(self.filename, 'w', encoding='utf-8', newline='') as f:
                csv_writer = csv.DictWriter(f, self.header)
                csv_writer.writeheader()

    # 写入每个页面的字典列表
    # 当爬取到最后一层时，才会用到关键字参数
    def write_csv(self, result_list, END=False, center_bw_id=None, origin_info=None, level=None):
        # 打开待写入文件
        with open(self.filename, 'a', encoding='utf-8', newline='') as f:
            csv_writer = csv.DictWriter(f, self.header)
            # 判断是否最后一层
            if END:
                # 若已是最后一层转发，许多字段需要设为空
                this_dict = {
                    'center_bw_id': center_bw_id,
                    'user_id': origin_info['origin_user']['id'],
                    'screen_name': origin_info['origin_user']['screen_name'],
                    'bw_id': origin_info['bw_id'],
                    'origin': origin_info['origin'],
                    'repost_count': 0,
                    'fs_count': origin_info['origin_user']['followers_count'],
                    'fs_user_id': 'Null',
                    'fs_screen_name': 'Null',
                    'fs_bw_id': 'Null',
                    'fs_fans_count': 'Null',
                    'level': level,
                    'raw_text': 'Null',
                    'created_at': 'Null'
                }
                csv_writer.writerow(this_dict)
            else:
                csv_writer.writerows(result_list)

    # 获取要爬取转发关系的列表
    def get_idList(self, bw_id=None):
        df = pd.read_csv(self.filename, header=0)
        df = df.drop_duplicates('bw_id', keep='last')
        idList = df['bw_id'].tolist()
        if self.search:
            df.to_csv(self.filename, index=False)
        if self.temp and bw_id:
            pos = idList.index(bw_id)  # 数字形式
            # 断点 or 断点加1
            idList = idList[pos:]
        return idList

    def merge_csv(self, temp_dir):
        if self.repost:
            mergeCSV(temp_dir, self.filename)
