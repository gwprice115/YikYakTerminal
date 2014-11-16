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
		# If location already set in past, read file
		f = open("locationsetting", "r")
		fileinput = f.read()
		f.close()
		
		# Extract location coordinates and name from file
		coords = fileinput.split('\n')
		
		currentlatitude = coords[0]
		currentlongitude = coords[1]
		# print("Location is set to: ", coords[2])
		
		# Set up coordinate object
		coordlocation = pk.Location(currentlatitude, currentlongitude)
		
	except FileNotFoundError:
		# If first time using app, ask for preferred location
		coordlocation = newLocation(geocoder, "Claremont Colleges")
		
		# If location retrieval fails, ask user for coordinates
		if coordlocation == 0:
			# print("Please enter coordinates manually: ")
			
			currentlatitude = input("Latitude: ")
			currentlongitude = input("Longitude: ")
			coordlocation = pk.Location(currentlatitude, currentlongitude)
	
	try:
		# If user already has ID, read file
		f = open("userID", "r")
		userID = f.read()
		f.close()
		
		# start API with saved user ID
		remoteyakker = pk.Yakker(userID, coordlocation, False)
		
	except FileNotFoundError:
		# start API and create new user ID
		remoteyakker = pk.Yakker(None, coordlocation, True)
		
		try:
			# Create file if it does not exist and write user ID
			f = open("userID", 'w+')
			f.write(remoteyakker.id)
			f.close()
			
		except:
			pass
			
	# Print User Info Text
	# print("User ID: ", remoteyakker.id, "\n")
	# print("Connecting to Yik Yak server...\n")
	# print("Yakarma Level:",remoteyakker.get_yakarma(), "\n")
	# print("Type one of the one-letter commands below or use the command in conjunction with a parameter.")

	# now = datetime.datetime.now()
	# print(now.year,"-",now.month,"-",now.day," ",now.hour,":",now.minute,":",now.second)

	currentlist = []
	
	# When actions are completed, user can execute another action or quit the app
	# colleges = ["Columbia University","Claremont Colleges","Georgia Southern University","Texas A&M","Clemson University","Wake Forest University","Stanford University","Colgate University","University of Utah"]
	collegeFiles = {"Columbia University":"columbiaFile.txt","Claremont Colleges":"claremontFile.txt","Georgia Southern University":"georgiaFile.txt","Texas A&M":"texasFile.txt","Clemson University":"clemsonFile.txt","Wake Forest University":"wakeFile.txt","Stanford University":"stanfordFile.txt","Colgate University":"colgateFile.txt","University of Utah":"utahFile.txt"}
	timeRadius = 0#5
	timesToCollect = {"1hour":(60-timeRadius,60+timeRadius),"2hour":(120-timeRadius,120+timeRadius),"3hour":(180-timeRadius,180+timeRadius),"4hour":(240-timeRadius,240+timeRadius)}
	collegeLocations = {}
	for college in collegeFiles.keys():
		try:
			collegeLocations[college] = newLocation(geocoder, college)
			print("setting"+college)
		except: 
			print("Google Geolocation API isn't working for "+college+" for some reason.")

	start_moment = datetime.now()-timedelta(minutes = timeRadius * 2)
	while True:
		end_moment = datetime.now()
		time_to_sleep = start_moment + timedelta(minutes = timeRadius * 2) - end_moment
		if (time_to_sleep.total_seconds() >0):
			sleep(time_to_sleep.total_seconds())
		start_moment = datetime.now()
		
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

		for (schoolName,schoolFile) in collegeFiles.items() :

			# coordlocation = changeLocation(geocoder, schoolName)
			coordlocation = collegeLocations[schoolName]
			remoteyakker.update_location(coordlocation)
			currentlist = remoteyakker.get_yaks()

			for (folderName,times) in timesToCollect.items():

				outFile = open("data/"+folderName+"/"+schoolFile,"a")
				# print(schoolName,schoolFile)
				read(currentlist,outFile, times)
				outFile.close()
		
		# Insert line gap
		# print()
		
		# Show all action choices
		# choice = input("*Read Latest Yaks\t\t(R)\n*Read Top Local Yaks\t\t(T)\n\n*Read Best Yaks of All Time\t(B)\n\n*Show User Yaks\t\t\t(S)\n*Show User Comments\t\t(O)\n\n*Post Yak\t\t\t(P) or (P <message>)\n*Post Comment\t\t\t(C) or (C <yak#>)\n\n*Upvote Yak\t\t\t(U) or (U <yak#>)\n*Downvote Yak\t\t\t(D) or (D <yak#>)\n*Report Yak\t\t\t(E) or (E <yak#>)\n\n*Upvote Comment\t\t\t(V) or (V <yak# comment#>)\n*Downvote Comment\t\t(H) or (H <yak# comment#>)\n*Report Comment\t\t\t(M) or (M <yak# comment#>)\n\n*Yakarma Level\t\t\t(Y)\n\n*Choose New User ID\t\t(I) or (I <userID>)\n*Choose New Location\t\t(L) or (L <location>)\n\n*Contact Yik Yak\t\t(F)\n\n*Quit App\t\t\t(Q)\n\n-> ")
		
		# Read Yaks
		# if choice.upper() == 'R':
		# 	currentlist = remoteyakker.get_yaks()
		# 	read(currentlist)
		
		# Read Local Top Yaks
		# elif choice.upper() == 'T':
		# 	currentlist = remoteyakker.get_area_tops()
		# 	read(currentlist)
			
		# Read Best of All Time
		# elif choice.upper() == 'B':
		# 	currentlist = remoteyakker.get_greatest()
		# 	read(currentlist)
				
		# Change Location

		# elif choice[0].upper() == 'L':
		# 	# set location from parameter or input
		# 	if len(choice) > 2:
		# 		coordlocation = changeLocation(geocoder, choice[2:])
		# 	else:
		# 		coordlocation = changeLocation(geocoder)
				
		# 	remoteyakker.update_location(coordlocation)
			
		# 	yaklist = remoteyakker.get_yaks()
		# 	currentlist = yaklist
			
def newLocation(geocoder, address=""):
	# figure out location latitude and longitude based on address
	print("new location"+address)
	if len(address) == 0:
		address = input("Enter college name or address: ")
	try:
		currentlocation = geocoder.geocode(address)
	except:
		print("\nGoogle Geocoding API is offline or has reached the limit of queries.\n")
		return 0
		
	coordlocation = 0
	try:
		coordlocation = pk.Location(currentlocation.latitude, currentlocation.longitude)
		
		# Create file if it does not exist and write
		f = open("locationsetting", 'w+')
		coordoutput = str(currentlocation.latitude) + '\n' + str(currentlocation.longitude)
		f.write(coordoutput)
		f.write("\n")
		f.write(address)
		f.close()
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
	
def changeLocation(geocoder, address=""):
	coordlocation = newLocation(geocoder, address)
	
	# If location retrieval fails, ask user for coordinates
	if coordlocation == 0:
		print("\nPlease enter coordinates manually: ")
		currentlatitude = input("Latitude: ")
		currentlongitude = input("Longitude: ")
		coordlocation = pk.Location(currentlatitude, currentlongitude)
		
	return coordlocation
	
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