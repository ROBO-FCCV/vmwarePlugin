# coding=utf-8
from sys import argv
import com.huawei.kmc.com.huawei.kmc
A = com.huawei.kmc.com.huawei.kmc.API()
num1 = argv[1]
print(A.encrypt(0, num1))
