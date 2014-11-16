#! /usr/bin/env python3
import API as pk
import pygeocoder
import requests
import datetime

from time import strftime, gmtime, sleep
from datetime import datetime, timedelta

def main():
	
	# Initialize Google Geocoder API
	geocoder = pygeocoder.Geocoder("AIzaSyB3X2bU_oHFuiYUpcHog_jQhaR-zN-3cmE")

	try:
		# If user already has ID, read file
		f = open("userID", "r")
		userID = f.read()
		f.close()
		
		# start API with saved user ID
		remoteyakker = pk.Yakker(userID, None, False)
		
	except FileNotFoundError:
		# start API and create new user ID
		remoteyakker = pk.Yakker(None, None, True)
		
		try:
			# Create file if it does not exist and write user ID
			f = open("userID", 'w+')
			f.write(remoteyakker.id)
			f.close()
			
		except:
			pass

	currentlist = []
	
	# When actions are completed, user can execute another action or quit the app

	# Locations to query
		# Columbia University
		# Claremont Colleges
		# Georgia Southern University
		# Texas A&M
		# Clemson University
		# Wake Forest University
		# Stanford University
		# Colgate University
		# University of Utah
	# colleges = ["Columbia University","Claremont Colleges","Georgia Southern University","Texas A&M","Clemson University","Wake Forest University","Stanford University","Colgate University","University of Utah"]
	collegeFiles = {"Columbia University":"columbiaFile.txt","Claremont Colleges":"claremontFile.txt","Georgia Southern University":"georgiaFile.txt","Texas A&M":"texasFile.txt","Clemson University":"clemsonFile.txt","Wake Forest University":"wakeFile.txt","Stanford University":"stanfordFile.txt","Colgate University":"colgateFile.txt","University of Utah":"utahFile.txt"}
	timeRadius = 5
	timesToCollect = {"1hour":(60-timeRadius,60+timeRadius),"2hour":(120-timeRadius,120+timeRadius),"3hour":(180-timeRadius,180+timeRadius),"4hour":(240-timeRadius,240+timeRadius)}
	#get the locations for all of the colleges we care about
	collegeLocations = {}
	for college in collegeFiles.keys():
		try:
			collegeLocations[college] = newLocation(geocoder, college)
		except: 
			print("Google Geolocation API isn't working for "+college+" for some reason.")

	start_moment = datetime.now()-timedelta(minutes = timeRadius * 2)
	while True:
		end_moment = datetime.now()
		time_to_sleep = start_moment + timedelta(minutes = timeRadius * 2) - end_moment
		if (time_to_sleep.total_seconds() >0):
			sleep(time_to_sleep.total_seconds())
		start_moment = datetime.now()

		for (schoolName,schoolFile) in collegeFiles.items() :
			coordlocation = collegeLocations[schoolName] #keep track of the locations & don't use the API
			remoteyakker.update_location(coordlocation)
			currentlist = remoteyakker.get_yaks()

			for (folderName,times) in timesToCollect.items():

				outFile = open("data/"+folderName+"/"+schoolFile,"a")
				# print(schoolName,schoolFile)
				read(currentlist,outFile, times)
				outFile.close()
		
def newLocation(geocoder, address=""):
	# figure out location latitude and longitude based on address
	if len(address) == 0:
		print ("\nInvalid address\n")
		return 0
	try:
		currentlocation = geocoder.geocode(address)
	except:
		print("\nGoogle Geocoding API is offline or has reached the limit of queries.\n")
		return 0
		
	coordlocation = 0
	try:
		coordlocation = pk.Location(currentlocation.latitude, currentlocation.longitude)
	except:
		print("Unable to get location.")
		
	return coordlocation
	
def setUserID(location, userID=""):
	if userID == "":
		userID = input("Enter userID or leave blank to generate random ID: ")
		
	if userID == "":
		# Create new userID
		remoteyakker = pk.Yakker(None, location, True)
	else:
		# Use existing userID
		remoteyakker = pk.Yakker(userID, location, False)
	try:
		# Create file if it does not exist and write user ID
		f = open("userID", 'w+')
		f.write(remoteyakker.id)
		f.close()
		
	except:
		pass
	
	return remoteyakker
	
def read(yaklist,outFile, times):
	(start_time, end_time) = times
	yakNum = 1
	for yak in yaklist:
		
		now = datetime.now() + timedelta(hours=3)
		yakTime = datetime.strptime(yak.time, "%Y-%m-%d %H:%M:%S")
		timeDiff = now - yakTime
		leastTime = timedelta(minutes=start_time)
		mostTime = timedelta(minutes = end_time)
		# if (timeDiff.month ==0 and timeDiff.day ==0 and
		# 	((timeDiff.hour <1 and timeDiff.minute >=55) or (timeDiff.hour ==1 and timeDiff.minute <=5))):
		if (timeDiff < mostTime and timeDiff > leastTime):
			print (outFile)
			# line between yaks
			outFile.write("_" * 93)
			# show yak
			outFile.write("\n" + str(yakNum) + "\n")
			
			outFile.write(str(now.year)+"-"+str(now.month)+"-"+str(now.day)+" "+str(now.hour)+":"+str(now.minute)+":"+str(now.second))

			yak.print_yak(outFile)
			
			commentNum = 1
			# comments header
			comments = yak.get_comments()
			# number of comments
			outFile.write("\n\tComments: "+str(len(comments))+"\n")
			
			# # print all comments separated by dashes
			# for comment in comments:
			# 	outFile.write("\t   {0:>4}".format(commentNum), end=' ')
			# 	print ("-" * 77)
			# 	comment.print_comment()
			# 	commentNum += 1
				
			yakNum += 1
		
main()