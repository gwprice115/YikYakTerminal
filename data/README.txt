Yik Yak data collected by George Price, Sarah Jundt, and Shannon Lubetich.

This data was collected every ten minutes using the YikYakTerminal API.
Yaks that were between 55 and 65 minutes old were added to a file in the 1hour directory.
Yaks that were between 115 and 125 minutes old were added to a file in the 2hour directory.
Yaks that were between 175 and 185 minutes old were added to a file in the 3hour directory.
Yaks that were between 235 and 245 minutes old were added to a file in the 4hour directory.

Each school has its own file in these directories:
The Claremont Colleges -> claremontFile.txt
Clemson University —> clemsonFile.txt
Colgate University —> colgateFile.txt
Columbia University -> columbiaFile.txt
Georgia Southern University -> georgiaFile.txt
Stanford University —> stanfordFile.txt
Texas A&M University -> texasFile.txt
The University of Utah -> utahFile.txt
Wake Forest University -> wakeFile.txt

Data was collected between November 16th, 2014 at 5:45 PM and December 7, 2014 at 7:10 PM Eastern Standard Time.

The format of a single Yak is:
[index for Yak, unique within a given collection time]
[collection time][optional header]
[text of Yak]
	[number of likes] likes  |  Posted  [date and time posted]  at  [latitude and longitude of post: scattered by the company for anonymity]
	Comments: [number of comments]
