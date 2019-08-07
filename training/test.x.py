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



# We evaluate the x and y by sklearn to get a sense of the coefficients.
# reg = linear_model.LogisticRegression(solver='liblinear')
reg = joblib.load(sys.argv[2])
y_data = reg.predict_proba(SimpleImputer(missing_values=999, strategy='mean').fit_transform(x_data))
# for y in y_data:
#     print y

print "拟合算法:"
print reg.score(SimpleImputer(missing_values=999.00, strategy='mean').fit_transform(x_data), np.loadtxt('temp/odd75_y_test.dat.x').astype(np.float32))
# print reg.score(x_data, np.loadtxt('temp/odd75_y_test.dat.x').astype(np.float32))
print "\n"
# y_data_test = np.loadtxt(sys.argv[3]).astype(np.float32)
# print reg.score(x_data_std, y_data_test)
