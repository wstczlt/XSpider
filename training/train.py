# -*- coding:utf-8 -*-
import numpy as np
import sys
from sklearn import linear_model
from sklearn.externals import joblib

# sys.argv[1] = 训练集X，文件名
# sys.argv[2] = 训练集Y，文件名
# sys.argv[3] = 模型保存，文件名
# Read x and y
x_data = np.loadtxt(sys.argv[1]).astype(np.float32)
y_data = np.loadtxt(sys.argv[2]).astype(np.float32)

# We evaluate the x and y by sklearn to get a sense of the coefficients.
reg = linear_model.LogisticRegression(solver='liblinear')
# reg = linear_model.LinearRegression()
reg.fit(x_data, y_data)
joblib.dump(reg, sys.argv[3])
