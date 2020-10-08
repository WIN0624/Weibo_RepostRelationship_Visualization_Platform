import csv
import time
import glob
import shutil
import pandas as pd


def mergeCSV(repost_temp_dir, filename):
    csv_list = glob.glob(repost_temp_dir + '*.csv')
    print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]  Find {str(len(csv_list))} csv files in total.')
    print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]  Start Merging csv Files...')
    with open(filename, 'w', encoding='utf-8-sig', newline='') as f:
        f_csv = csv.writer(f)
        for file in csv_list:
            with open(file, 'r', encoding='utf-8') as f2:
                f2_csv = csv.reader(f2)
                rows = list(f2_csv)
                f_csv.writerows(rows[1:])
    # 去重
    drop_duplicates(filename)
    print(f'[{time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())}]  Finish Merging!')
    # 删除中间目录
    shutil.rmtree(repost_temp_dir)


# 去重
def drop_duplicates(filename):
    df = pd.read_csv(filename, header=0)
    df = df.drop_duplicates()
    df = df.sort_index(axis=0, ascending=True)
    df.to_csv(filename, index=False)
