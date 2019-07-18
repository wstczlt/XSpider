# -*- coding:utf-8 -*-
import numpy as np
import tensorflow as tf
from sklearn import linear_model

# Read x and y
x_data = np.loadtxt("x.dat").astype(np.float32)
y_data = np.loadtxt("y.dat").astype(np.float32)

# We evaluate the x and y by sklearn to get a sense of the coefficients.
reg = linear_model.LinearRegression()
reg.fit(x_data, y_data)
print ("Coefficients of sklearn: K=%s, b=%f" % (reg.coef_, reg.intercept_))
#
# # Now we use tensorflow to get similar results.
# # Before we put the x_data into tensorflow, we need to standardize it
# # in order to achieve better performance in gradient descent;
# # If not standardized, the convergency speed could not be tolearated.
# # Reason:  If a feature has a variance that is orders of magnitude larger than others,
# # it might dominate the objective function
# # and make the estimator unable to learn from other features correctly as expected.
# # scaler = preprocessing.StandardScaler().fit(x_data)
# # print (scaler.mean_, scaler.scale_)
# # x_data_standard = scaler.transform(x_data)
# x_data_standard = x_data
#
# n = 8  # n为特征数量
#
# W = tf.Variable(tf.zeros([n, 1]))
# b = tf.Variable(tf.zeros([1]))
# y = tf.matmul(x_data_standard, W) + b
#
#
# loss = tf.reduce_mean(tf.square(y - y_data))
# optimizer = tf.train.GradientDescentOptimizer(0.1)
# train = optimizer.minimize(loss)
#
# sess = tf.Session()
# sess.run(tf.global_variables_initializer())
# for step in range(1000):
#     sess.run(train)
#     if step % 100 == 0:
#         print (step, sess.run(W).flatten(), sess.run(b).flatten(), sess.run(loss))
#
# print ("Coefficients of tensorflow: K=%s, b=%s" % (
# sess.run(W).flatten(), sess.run(b).flatten()))
# # print ("Coefficients of tensorflow (raw input): K=%s, b=%s" % (sess.run(W).flatten() / scaler.scale_, sess.run(b).flatten() - np.dot(scaler.mean_ / scaler.scale_, sess.run(W))))
