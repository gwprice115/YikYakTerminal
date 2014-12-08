

#takes in (1) a prediction file and (2) a data file

import sys
from itertools import izip

predPath = sys.argv[1]
dataPath = sys.argv[2]

dfms = 0.0 # distance from the mean squared
dfps = 0.0 # distance from the prediction squared

dataValSum = 0
dataPointCount = 0
with open(dataPath) as dataFile :
	for d in dataFile :
		dataPointCount = dataPointCount + 1
		dVal = int(d)
		dataValSum = dataValSum + dVal
dataValMean = dataValSum / dataPointCount

with open(predPath) as predFile, open(dataPath) as dataFile: 
    for p, d in izip(predFile, dataFile):
        dVal = int(d)
        pVal = int(p.split(" ")[0])
        dfms = dfms + (dVal - dataValMean)*(dVal - dataValMean)
        dfps = dfps + (dVal - pVal)*(dVal - pVal)
        
print str(1 - (dfps / dfms) ) #r-squared!!!       
