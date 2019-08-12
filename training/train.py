# -*- coding:utf-8 -*-
import numpy as np
import sys
from sklearn import svm
from sklearn.externals import joblib
from sklearn import preprocessing
from sklearn import linear_model
from sklearn.multiclass import OneVsRestClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn.tree import ExtraTreeClassifier
from sklearn.neural_network import MLPClassifier
from sklearn.ensemble import GradientBoostingClassifier
from xgboost import XGBClassifier
from sklearn.impute import SimpleImputer
from sklearn.feature_selection import SelectKBest, chi2

# sys.argv[1] = 训练集X，文件名
# sys.argv[2] = 训练集Y，文件名
# sys.argv[3] = 模型保存，文件名
x_data = np.loadtxt(sys.argv[1]).astype(np.float32)
y_data = np.loadtxt(sys.argv[2]).astype(np.float32)

# support = SelectKBest(chi2, k=2).fit(x_data, [0, 3])
# print(support.get_support())

# We evaluate the x and y by sklearn to get a sense of the coefficients.
# reg = OneVsRestClassifier(svm.SVC(C=1.0, kernel='rbf', gamma='scale', probability=True))
# reg=DecisionTreeClassifier()
# reg=XGBClassifier()
# reg=GradientBoostingClassifier()
# reg=ExtraTreeClassifier()
# reg=MLPClassifier()
# reg = RandomForestClassifier(n_estimators=100)
# reg = linear_model.LinearRegression(fit_intercept=False)
reg = linear_model.LogisticRegression(solver='sag', multi_class='auto', class_weight='balanced')

x_data=SimpleImputer(missing_values=999.00, strategy='mean').fit_transform(x_data)
x_data=preprocessing.MinMaxScaler().fit_transform(x_data)
reg.fit(x_data, y_data)

joblib.dump(reg, sys.argv[3])
