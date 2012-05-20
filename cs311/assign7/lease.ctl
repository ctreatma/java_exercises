LOAD DATA
INFILE *
INTO TABLE Lease
FIELDS TERMINATED BY '|'
(street, months, rent, gasyn, seweryn)
BEGINDATA
261 N Main Street|10|260|N|N
216 N Main Street|9|280|N|Y
88 Groveland Street|12|260|Y|Y
37 Locust Street|10|225|Y|Y
119 N Pleasant Street|10|260|Y|N
27 Union Street|12|315|N|N