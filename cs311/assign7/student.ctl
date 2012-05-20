LOAD DATA
INFILE *
INTO TABLE Student
FIELDS TERMINATED BY '|' TRAILING NULLCOLS
(Tnumber, firstname, lastname, phone, OCMR, year, email, dorm, room)
BEGINDATA
T00032696|Charles|Treatman|6106622991|2817|Senior|ctreatma@oberlin.edu||
T00041279|Nicole|Middaugh|4406105305|2005|Senior|nmiddaug@oberlin.edu||
T00041423|Kimberly|Meinert|4407766127|0016|Freshman|kmeinert@oberlin.edu|Fairchild|106
T00052271|Craig|Betchart|4407762636|2112|Sophomore|cbetchar@oberlin.edu|Burton|210
T00031143|Jennifer|Ni|4407743822|2073|Senior|jni@oberlin.edu||
T00034342|Kathryn|Powers|4407742092|0010|5th Year|kat4342@earthlink.net||
T00066241|Shannon|Davis|4407762514|1705|Freshman|sdavis@oberlin.edu|Burton|206
T00053796|Kimberly|Davis|4407743174|1819|Junior|kdavis@oberlin.edu||
T00067344|Burton|Betchart|4407743282|1865|Senior|bbetchar@oberlin.edu||