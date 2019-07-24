#!/usr/bin/env python
# -*- coding: utf-8 -*-
# 导入模块
from wxpy import *
# 初始化机器人，扫码登陆
bot = Bot()

# 搜索名称含有 "游否" 的男性深圳好友
my_friend = bot.friends().search('焦功进')[0]

# 发送文本给好友
my_friend.send('Python Call: Hello WeChat!')
