import csv
import pandas as pd
import numpy as np

header = ['center_bw_id', 'user_id', 'screen_name', 'bw_id', 'origin', 'repost_count', 'fs_count', 'fs_user_id', 'fs_screen_name', 'fs_bw_id', 'fs_fans_count', 'level', 'raw_text', 'created_at']


def sampling():
    with open('search_result_新冠.csv', 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        raw_list = list(set([row['bw_id'] for row in reader]))

    df = pd.read_csv('repost_Relationship_新冠.csv', header=0)
    df = df.loc[df['fs_bw_id'] != 'Null']
    result = pd.DataFrame(columns=header)
    for id in raw_list:
        sub = df.loc[df['center_bw_id'] == int(id)]
        if sub.shape[0] > 1000:
            print(id)
            sub = subSampling(sub)
        result = result.append(sub)
    result = result.sort_index(axis=0, ascending=True)
    result.to_csv('sample.csv', index=False)


def subSampling(df):
    max_level = df.iloc[-1]['level']
    if max_level > 6:
        max_level = 6
    # 最终提取共1000个结点
    result = reduceNodes(max_level, df, 1000)
    result = result.sort_values(by=['center_bw_id', 'level'])
    return result


def reduceNodes(level, df, remain):
    '''
    level：当前层级
    remain：剩余位置
    '''
    # 计算在当前层数下，可取的转发链总数
    # 此处假设每个最外层节点对应一个父节点，实际可能多个节点对应一个父节点
    if level == 0:
        result = pd.DataFrame(columns=header)
        return result
    else:
        cnt = int(remain / level)
        df1 = df.loc[df['level'] == level]
        # 循环过程中，可能某一层全为0，故空时，直接进入下一循环
        # 因为会不断去除df中已被爬取的转发链，可能某层所有数据都在此前爬过的转发链中
        if df1.empty:
            chain = pd.DataFrame(columns=header)
        else:
            num = df1.shape[0]
            if num <= cnt:
                cnt = num
                df2 = df1
            else:
                sampler = np.random.choice(num, cnt, replace=False)
                df2 = df1.iloc[sampler]
            idList = df2['fs_bw_id'].tolist()
            # 取第1层到第level层的转发数据
            chain = getChain(level, idList, df)
            # 取差集，去除已爬取的数据，以免在下一轮循环中重复获取
            df = diff(df, chain)
            remain -= len(chain)
        return reduceNodes(level-1, df, remain).append(chain)


def diff(df, sub):
    df = df.append(sub)
    df = df.drop_duplicates(keep=False)
    return df


def getChain(level, idList, df):
    '''
    df：某一center_bw_id的所有转发数据
    level: 表示待爬取'bw_id'的层级
    idList：所有待爬取'bw_id'的对应的转发博文id'fs_bw_id'
    '''
    if len(idList) == 0 or level == 0:
        # level1对应的bw_id即中心博文id，无需继续爬取
        return pd.DataFrame(columns=header)
    else:
        this = pd.DataFrame(columns=header)
        next_idList = []
        for id in idList:
            temp = df.loc[df['fs_bw_id'] == str(id)]
            this = this.append(temp)
            next_idList += temp['bw_id'].tolist()
        return getChain(level-1, set(next_idList), df).append(this)


if __name__ == '__main__':
    sampling()
    # df = pd.read_csv('repost_Relationship_新冠.csv', header=0)
    # df = df.loc[df['center_bw_id'] == 4527239532382708]
    # df = df.loc[df['fs_bw_id'] != 'Null']
    # print(df.shape[0])
    # df1 = subSampling(df)
    # print(df1.shape[0])
    # df1.to_csv('test.csv')