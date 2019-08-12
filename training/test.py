# -*- coding:utf-8 -*-
import numpy as np
import sys
from sklearn.externals import joblib
from sklearn import preprocessing
from sklearn.impute import SimpleImputer

# sys.argv[1] = 训练集X，文件名
# sys.argv[2] = 模型名称，文件名
# sys.argv[3] = 训练集Y，文件名
# Read x and y
x_data = np.loadtxt(sys.argv[1]).astype(np.float32)

reg = joblib.load(sys.argv[2])
x_data = SimpleImputer(missing_values=999.00, strategy='mean').fit_transform(x_data)
x_data=preprocessing.MinMaxScaler().fit_transform(x_data)

if len(sys.argv) <= 3:
    y_data = reg.predict_proba(x_data)
    for y in y_data:
        print y
else:
    print "拟合算法:"
    print reg.score(x_data, np.loadtxt(sys.argv[3]).astype(np.float32))
    print reg.coef_
    print "\n"

