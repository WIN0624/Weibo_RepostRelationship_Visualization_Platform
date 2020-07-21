import logging
from utils.loadConfig import load_config


def getLogger(name):
    # load_config
    dir = load_config()['log_dir']
    logging.basicConfig(filename=dir+name+'_spider.log',
                        level=logging.INFO,
                        format='[%(asctime)s]  %(levelname)-12s | %(message)s',
                        datefmt='%Y-%m-%d %H:%M:%S')
    logger = logging.getLogger()
    return logger
