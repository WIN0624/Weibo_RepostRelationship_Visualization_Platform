def get_bw_info(user_dict,filename):
    b = True
    n = 0
    error = {}
    bw_id = user_dict['bw_id']
    rp_count = get_rca_count(bw_id)['reposts_count'] # 转发次数
    while b:
        try:
            o = judge_origin(bw_id)  # 判断是否为原创
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
                        fs_bw_id = i.get('id')
                        fs_screen_name = i.get('user').get('screen_name')
                        write_csv([user_dict['user_id'],user_dict['screen_name'],bw_id,o,
                                   rp_count,fs_id,fs_screen_name,fs_bw_id],'rp_relationship.csv')
                        #[用户id，用户名，微博id，是否原创，被转发的次数，转发的粉丝id，粉丝名，转发的微博id]
            else:
                b = False
        except Exception as e :
            if str(e) == 'Expecting value: line 1 column 1 (char 0)' and error.get(url, -1) == -1:
                error[url] = 1
                n -= 1
                time.sleep(5)
                print('重新请求-->', url)
            elif str(e) == 'Expecting value: line 1 column 1 (char 0)' and error.get(url, -1) == 1:
                time.sleep(5)
            else:
                b = False
                print('Error:\n',e)
        time.sleep(1)

def get_info(search_list, since_date=None):
    print('Start Time: ' + str(datetime.now()))
    headers = {'User-Agent': 'Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6'}
    results_list = []
    results_dict = {}
    if_crawl = True
    for wd in search_list:
        wd_list = []
        # 将检索词编码，嵌入url得到不同词的url字典
        base_url = get_baseurl(wd)
        count = 0
        # 获取多页该检索词的结果页面
        for page in range(1, 250):
            print('This is page ' + str(page))
            this_url = base_url + str(page)
            try:
                # proxypool_url = 'http://127.0.0.1:5555/random'
                # proxies = {'http': 'http://' + requests.get(proxypool_url).text.strip()}
                r = requests.get(this_url, headers=headers)
                r.raise_for_status()
                r.encoding = r.apparent_encoding
                content = json.loads(r.text)
                if content.get('ok') == 1:
                    mblogs = jsonpath(content, '$.data.cards..mblog')
                    for mblog in mblogs:
                        mblog['created_at'] = standardize_date(mblog['created_at'])
                        this_topic, this_text = getText(mblog)
                        this_dict = {
                                    '检索词': str(wd),
                                    '用户id': mblog['user']['id'], 
                                    '用户名': mblog['user']['screen_name'], 
                                    '微博id': mblog['id'],
                                    '话题': this_topic,
                                    '微博正文': this_text,
                                    '发表时间': mblog['created_at']
                                }
                content2 = json.loads(response.content.decode('utf-8'))
                if content2.get('ok') == 1:
                    fslogs = jsonpath(content2, '$.data.cards..fslog')
                    for fslog in fslogs:
                         fslog['created_at'] = standardize_date(fslog['created_at'])
                        if since_date:             
                            since_date = datetime.strptime(since_date, '%Y-%m-%d') 
                            created_at1 = datetime.strptime(mblog['created_at'], '%Y-%m-%d')
                            created_at2= datetime.strptime(fslog['created_at'], '%Y-%m-%d')
                            if (created_at1 > since_date):  #对时间进行判断，如果检索词微博时间较大，爬取下来
                                if_crawl = False
                            if(created_at1<since_date):     #如果较小，再对转发微博的时间进行判断，较大则爬取
                                if(created_at2>since_date):
                                    if_crawl = False
                                if(created_at2<since_date):
                                    if_crawl = True
                            if not if_crawl:
                                wd_list.append(this_dict)
                                count += 1
                if count % 10 == 0:
                    time.sleep(random.randint(2, 8))
            except IndexError:
                print('There is no more data for this word! To the next word!')
                break
            except requests.HTTPError:
                print('I have met the HTTPError! I got to stop 3 minutes!')
                time.sleep(180)
            except Exception:
                traceback.print_exc()
                continue

        # Store the results of all reachable pages to the list and dict
        results_list += wd_list + []
        results_dict[wd] = wd_list
        endtime = str(datetime.now()).split(' ')[1]
        print('Get %d weibo of %s at %s' % (len(wd_list), wd, endtime))
        time.sleep(5)
    return results_list, results_dict
                
            
