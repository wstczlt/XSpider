# -*- coding:utf-8 -*-
import numpy as np
import sys
from sklearn.externals import joblib
from sklearn.impute import SimpleImputer

# sys.argv[1] = 训练集X，文件名
# sys.argv[2] = 模型名称，文件名
# sys.argv[3] = 训练集Y，文件名
# Read x and y
x_data = np.loadtxt(sys.argv[1]).astype(np.float32)

reg = joblib.load(sys.argv[2])
x_data = SimpleImputer(missing_values=999.00, strategy='mean').fit_transform(x_data)
y_data = reg.predict(x_data)
for y in y_data:
    print y

if sys.argv[3]:
    print "分类算法:"
    print reg.score(x_data, np.loadtxt(sys.argv[3]).astype(np.float32))
    print(reg.feature_importances_)
    print "\n"

