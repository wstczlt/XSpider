# -*- coding:utf-8 -*-
import numpy as np
import sys
from sklearn.externals import joblib
from sklearn import linear_model

# sys.argv[1] = 训练集X，文件名
# sys.argv[2] = 训练集Y，文件名
# sys.argv[3] = 模型保存，文件名
x_data = np.loadtxt(sys.argv[1]).astype(np.float32)
y_data = np.loadtxt(sys.argv[2]).astype(np.float32)


# We evaluate the x and y by sklearn to get a sense of the coefficients.
reg = linear_model.LogisticRegression(solver='sag')
# reg = linear_model.LinearRegression(fit_intercept=False)
reg.fit(x_data, y_data)

# print ("Coefficients of sklearn: K=%s, b=%f" % (reg.coef_, reg.intercept_))
joblib.dump(reg, sys.argv[3])
