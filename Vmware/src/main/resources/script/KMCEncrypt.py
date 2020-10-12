# coding=utf-8
from sys import argv
import kmc.kmc
A = kmc.kmc.API()
num1 = argv[1]
print(A.encrypt(0, num1))
