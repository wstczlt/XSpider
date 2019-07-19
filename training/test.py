# -*- coding:utf-8 -*-
import sys
import numpy as np
from sklearn.externals import joblib
# sys.argv[1] = 训练集X，文件名
# sys.argv[2] = 模型名称，文件名
# Read x and y
x_data = np.loadtxt(sys.argv[1]).astype(np.float32)

# We evaluate the x and y by sklearn to get a sense of the coefficients.
# reg = linear_model.LogisticRegression(solver='liblinear')
reg = joblib.load(sys.argv[2])
y_data = reg.predict(x_data)
for y in y_data:
    print y