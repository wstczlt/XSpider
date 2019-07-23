# -*- coding:utf-8 -*-
import numpy as np
import sys
from sklearn.externals import joblib
from sklearn.preprocessing import StandardScaler

# sys.argv[1] = 训练集X，文件名
# sys.argv[2] = 模型名称，文件名
# sys.argv[3] = 训练集Y，文件名
# Read x and y
x_data = np.loadtxt(sys.argv[1]).astype(np.float32)


scaler = StandardScaler()
x_data_std = scaler.fit_transform(x_data)

# We evaluate the x and y by sklearn to get a sense of the coefficients.
# reg = linear_model.LogisticRegression(solver='liblinear')
reg = joblib.load(sys.argv[2])
y_data = reg.predict_proba(x_data_std)
for y in y_data:
    print y

# y_data_test = np.loadtxt(sys.argv[3]).astype(np.float32)
# print reg.score(x_data_std, y_data_test)
