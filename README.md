Yik Yak Terminal
==============

Contributed to by gwprice115, sjundt, and snl017. 
Used for our final project in NLP, Fall 2014.

Based on implementation of YikYak using the pyak API by joseph346.

This project includes a script to obtain data from nine universities on YikYak, in the main folder called "YikYak.py."

We then used this script to obtain data over a multiweek period on an Amazon EC2 instance. That data can be found in the "data" folder.

For two hours since posting, we converted this data into one line per Yak, then shuffled it to separate for training and testing purposes. These processed files can be found in the "data shuffled" folder.


All our code for analysis can be found in the "Java_code."
To analyze our data, we created a script to extract features, format them into input for SVMLight, and then run SVMLight for classification.

SVMLight can be found at http://svmlight.joachims.org

"regressionModels" contains our trained classification models from SVMLight. 

"results" contains the output of the classification approach on our test sets. 


--------------------------------------------------------------------------------------
From the original project: "Python implementation of Yik Yak using the pyak API by joseph346. More features to come in the future."
    		
## API and Licensing

This app is licensed under the GPL license. Feel free to contribute to it.

This software utilizes PyGeoCoder to convert addresses to coordinates (licensed under BSD): http://code.xster.net/pygeocoder/wiki/Home

API url was http://github.com/joseph346/pyak/, but the repo seems to be deleted now.

I modified the original to create specific output for this app.
