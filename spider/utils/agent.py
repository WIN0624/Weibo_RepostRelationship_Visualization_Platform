import requests
from fake_useragent import UserAgent

ua = UserAgent(verify_ssl=False)


def get_header():
    return {'User-Agent': ua.random, 'Cookie': 'xq_a_token=3242a6863ac15761c18a8469b89065b03bd5e164'}


def get_proxy():
    proxypool_url = 'http://127.0.0.1:5555/random'
    proxies = {'http': 'http://' + requests.get(proxypool_url).text.strip()}
    return proxies
