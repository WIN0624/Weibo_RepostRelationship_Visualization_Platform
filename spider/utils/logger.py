import os
import logging
from utils.loadConfig import load_config


class Logger:
    def __init__(self, name):
        dir = load_config()['log_dir']
        self.name = dir + name + '_spider.log'

    def getLogger(self):
        logging.basicConfig(filename=self.name,
                            level=logging.INFO,
                            format='[%(asctime)s]  %(levelname)-12s | %(message)s',
                            datefmt='%Y-%m-%d %H:%M:%S')
        logger = logging.getLogger()
        return logger

    def remove(self):
        os.remove(self.name)
