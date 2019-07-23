#!/usr/bin/env python
# -*- coding: utf-8 -*-
# @Author: Daijingbo
# @Date  : 2019/6/16
# @Desc  :FBP ML XGBClassifier
# http://www.captainbed.net/blog-acredjb
import matplotlib.pyplot as plt
import pandas as pd
# from sklearn import preprocessing
# from sklearn.preprocessing import Imputer
from sklearn.feature_extraction import DictVectorizer
# from sklearn.cross_validation import train_test_split
from sklearn.model_selection import train_test_split
from xgboost import XGBClassifier
from xgboost import plot_importance


def trainandTest(X, y, X_t):
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.25, random_state=33)
    ### feature_extraction
    vec = DictVectorizer(sparse=False)
    X_train_std = vec.fit_transform(X_train.to_dict(orient='record'))
    X_test_std = vec.fit_transform(X_test.to_dict(orient='record'))
    ############第三处调参：选择全参数和无参数（默认）################################
    # model=xgb.XGBClassifier(learning_rate =0.1,n_estimators=1000,max_depth=4,min_child_weight=6,gamma=0,subsample=0.8,colsample_bytree=0.8,reg_alpha=0.005,objective='binary:logistic',nthread=4,scale_pos_weight=1,seed=27)
    model = XGBClassifier()  # ok无参数

    model.fit(X_train_std, y_train)
    # 对测试集进行预测
    ans = model.predict(X_test_std)

    cnt1 = 0
    cnt2 = 0
    # print list(X_test.keys())
    # print list(y_test.keys())
    # print y_test.to_dict()
    ylist = y_test.values.tolist()
    for i in range(0, len(ans)):
        if ans[i] == ylist[i]:
            cnt1 += 1
        else:
            cnt2 += 1
    print("Accuracy: %.2f %% " % (100 * cnt1 / (cnt1 + cnt2)))
    # cnt1 = 0
    # cnt2 = 0
    # print y_test
    # print y_test['5476']
    # for i in range(len(y_test)):
    #     if ans[i] == y_test[0][i]:
    #         cnt1 += 1
    #     else:
    #         cnt2 += 1

    # print("Accuracy: %.2f %% " % (100 * cnt1 / (cnt1 + cnt2)))
    plot_importance(model)
    plt.show()


if __name__ == '__main__':
    f0 = 'ysb';  # bet365
    f1 = 'li';  # 8jbb
    f2 = 'bet365';  # wl
    f3 = 'hg';  # 10ysb
    f4 = 'wl';
    f5 = 'ms';
    f6 = 'ao';  # li
    f7 = 'interw';
    f8 = 'w';
    f9 = '10bet';  # interw
    f10 = 'SNAI';  # 9ms

    ################第一处调参：选择训练集数据的行数1000-4000-all##################
    trainFilePath = 'FBP_train.csv'
    # trainFilePath='E:/PythonLearn/pc_ex/AdaBoost_PeiLv/FBP_train-3000.csv'
    # trainFilePath = 'E:/PythonLearn/pc_ex/AdaBoost_PeiLv/FBP_train-all.csv'
    testFilePath = 'FBP_predict.csv'
    data = pd.read_csv(trainFilePath)
    X_test = pd.read_csv(testFilePath)
    ###############第二处调参：选择全部特征还是部分特征###########################
    X_train = data[[f0, f1, f2, f3, f4, f5, f6, f7, f8, f9, f10]]  # 全特征
    # X_train=data[[f10, f7, f5,f6]]

    y_train = data['y']
    trainandTest(X_train, y_train, X_test)
