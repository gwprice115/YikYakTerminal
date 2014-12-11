#authors: sarah jundt, george price, shannon lubetich
#gets the accuracy of test results 
#args: paths for predicted_values, actual_values 
import sys
from itertools import izip

predPath = sys.argv[1]
dataPath = sys.argv[2]

total = 0
correct = 0
with open(predPath) as predFile, open(dataPath) as dataFile: 
    for p, d in izip(predFile, dataFile):
        if (float(p)<0 and float(d)<0) or (float(p)>0 and float(d)>0):
        	correct=correct+1
        total = total+1
print "correct", correct
print "total", total
print "accuracy:",float(correct)/total
