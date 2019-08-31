#coding=utf-8

# 导入模块
import os
import time
from wxpy import *

# 初始化机器人，扫码登陆
bot = Bot()

my_friend = ensure_one(bot.search('李维民'))
path = sys.path[0] + '/wechat.dat'


def read():
	result = ""
	try:
		f = open(path, 'r')    #以读方式打开文件
		for line in f.readlines():        
		    line = line.strip()                             
		    result += line + "\n"
		f.close()
		os.remove(path)                   
	except IOError:
		pass                               
	return result

def sendMsg(text):
	my_friend.send(text)

while True:
	text = read()
	if text:
		sendMsg(text)
		print(text)
	time.sleep(1)

