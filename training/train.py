# -*- coding:utf-8 -*-
import numpy as np
import sys
from sklearn.externals import joblib
from sklearn.preprocessing import StandardScaler
from sklearn.svm import SVC

# sys.argv[1] = 训练集X，文件名
# sys.argv[2] = 训练集Y，文件名
# sys.argv[3] = 模型保存，文件名
x_data = np.loadtxt(sys.argv[1]).astype(np.float32)
y_data = np.loadtxt(sys.argv[2]).astype(np.float32)

scaler = StandardScaler()
x_data_std = scaler.fit_transform(x_data)

# We evaluate the x and y by sklearn to get a sense of the coefficients.
# reg = linear_model.LogisticRegression(solver='sag')
reg = SVC(C=10, kernel='rbf', probability=True)
reg.fit(x_data_std, y_data)
joblib.dump(reg, sys.argv[3])
