# -*- coding:utf-8 -*-
import numpy as np
# from sklearn import preprocessing
# from sklearn.preprocessing import Imputer
from sklearn.feature_extraction import DictVectorizer
from sklearn.svm import SVC
from xgboost import XGBClassifier
from sklearn.model_selection import train_test_split

# sys.argv[1] = 训练集X，文件名
# sys.argv[2] = 训练集Y，文件名
# sys.argv[3] = 模型保存，文件名
data = np.loadtxt('oddX.dat', delimiter=',').astype(np.float32)
x_data = data[0:5000, 1:15]
y_data = data[0:5000, 15:16]

test_x_data = data[5000:5500, 1:15]
test_y_data = data[5000:5500, 15:16]

# print  x_data

vec = DictVectorizer(sparse=False)
x_data_std = vec.fit_transform(x_data.to_dict(orient='record'))
test_x_data_std = vec.transform(test_x_data.to_dict(orient='record'))
reg = XGBClassifier()  # ok无参数

# x_data_std = vec.fit_transform(x_data)
# test_x_data_std = scaler.fit_transform(test_x_data)
# We evaluate the x and y by sklearn to get a sense of the coefficients.
# reg = linear_model.LogisticRegression(solver='sag')
# reg = SVC(C=10, kernel='rbf', gamma=0.01, probability=True)
reg.fit(x_data_std, y_data)

print reg.score(test_x_data_std, test_y_data)
