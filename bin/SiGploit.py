#!/usr/bin/env python

import sys
import os
import signal
import time
import tracking



def banner(word):
	letterforms = '''\
       |       |       |       |       |       |       | |
  XXX  |  XXX  |  XXX  |   X   |       |  XXX  |  XXX  |!|
  X  X |  X  X |  X  X |       |       |       |       |"|
  X X  |  X X  |XXXXXXX|  X X  |XXXXXXX|  X X  |  X X  |#|
 XXXXX |X  X  X|X  X   | XXXXX |   X  X|X  X  X| XXXXX |$|
XXX   X|X X  X |XXX X  |   X   |  X XXX| X  X X|X   XXX|%|
  XX   | X  X  |  XX   | XXX   |X   X X|X    X | XXX  X|&|
  XXX  |  XXX  |   X   |  X    |       |       |       |'|
   XX  |  X    | X     | X     | X     |  X    |   XX  |(|
  XX   |    X  |     X |     X |     X |    X  |  XX   |)|
       | X   X |  X X  |XXXXXXX|  X X  | X   X |       |*|
       |   X   |   X   | XXXXX |   X   |   X   |       |+|
       |       |       |  XXX  |  XXX  |   X   |  X    |,|
       |       |       | XXXXX |       |       |       |-|
       |       |       |       |  XXX  |  XXX  |  XXX  |.|
      X|     X |    X  |   X   |  X    | X     |X      |/|
  XXX  | X   X |X     X|X     X|X     X| X   X |  XXX  |0|
   X   |  XX   | X X   |   X   |   X   |   X   | XXXXX |1|
 XXXXX |X     X|      X| XXXXX |X      |X      |XXXXXXX|2|
 XXXXX |X     X|      X| XXXXX |      X|X     X| XXXXX |3|
X      |X    X |X    X |X    X |XXXXXXX|     X |     X |4|
XXXXXXX|X      |X      |XXXXXX |      X|X     X| XXXXX |5|
 XXXXX |X     X|X      |XXXXXX |X     X|X     X| XXXXX |6|
XXXXXX |X    X |    X  |   X   |  X    |  X    |  X    |7|
 XXXXX |X     X|X     X| XXXXX |X     X|X     X| XXXXX |8|
 XXXXX |X     X|X     X| XXXXXX|      X|X     X| XXXXX |9|
   X   |  XXX  |   X   |       |   X   |  XXX  |   X   |:|
  XXX  |  XXX  |       |  XXX  |  XXX  |   X   |  X    |;|
    X  |   X   |  X    | X     |  X    |   X   |    X  |<|
       |       |XXXXXXX|       |XXXXXXX|       |       |=|
  X    |   X   |    X  |     X |    X  |   X   |  X    |>|
 XXXXX |X     X|      X|   XXX |   X   |       |   X   |?|
 XXXXX |X     X|X XXX X|X XXX X|X XXXX |X      | XXXXX |@|
   X   |  X X  | X   X |X     X|XXXXXXX|X     X|X     X|A|
XXXXXX |X     X|X     X|XXXXXX |X     X|X     X|XXXXXX |B|
 XXXXX |X     X|X      |X      |X      |X     X| XXXXX |C|
XXXXXX |X     X|X     X|X     X|X     X|X     X|XXXXXX |D|
XXXXXXX|X      |X      |XXXXX  |X      |X      |XXXXXXX|E|
XXXXXXX|X      |X      |XXXXX  |X      |X      |X      |F|
 21.45 |6     8|7      |2   lat|x1    x5|9     4| 31.74 |G|
X     X|X     X|X     X|XXXXXXX|X     X|X     X|X     X|H|
  XXX  |   X   |   X   |   X   |   X   |   X   |  XXX  |I|
      X|      X|      X|      X|X     X|X     X| XXXXX |J|
X    X |X   X  |X  X   |XXX    |X  X   |X   X  |X    X |K|
X      |X      |X      |X      |X      |X      |XXXXXXX|L|
X     X|XX   XX|X X X X|X  X  X|X     X|X     X|X     X|M|
X     X|XX    X|X X   X|X  X  X|X   X X|X    XX|X     X|N|
XXXXXXX|X     X|X     X|X     X|X     X|X     X|XXXXXXX|O|
XXXXXX |X     X|X     X|XXXXXX |X      |X      |X      |P|
 XXXXX |X     X|X     X|X     X|X   X X|X    X | XXXX X|Q|
XXXXXX |X     X|X     X|XXXXXX |X   X  |X    X |X     X|R|
 _IMSI |0x1  GT|PC     | _IMEI |     CI|Kc  421| _HLR_ |S|
XXXXXXX|   X   |   X   |   X   |   X   |   X   |   X   |T|
X     X|X     X|X     X|X     X|X     X|X     X| XXXXX |U|
X     X|X     X|X     X|X     X| X   X |  X X  |   X   |V|
X     X|X  X  X|X  X  X|X  X  X|X  X  X|X  X  X| XX XX |W|
X     X| X   X |  X X  |   X   |  X X  | X   X |X     X|X|
X     X| X   X |  X X  |   X   |   X   |   X   |   X   |Y|
XXXXXXX|     X |    X  |   X   |  X    | X     |XXXXXXX|Z|
 XXXXX | X     | X     | X     | X     | X     | XXXXX |[|
X      | X     |  X    |   X   |    X  |     X |      X|\|
 XXXXX |     X |     X |     X |     X |     X | XXXXX |]|
   X   |  X X  | X   X |       |       |       |       |^|
       |       |       |       |       |       |XXXXXXX|_|
       |  XXX  |  XXX  |   X   |    X  |       |       |`|
       |   XX  |  X  X | X    X| XXXXXX| X    X| X    X|a|
       | XXXXX | X    X| XXXXX | X    X| X    X| XXXXX |b|
       |  XXXX | X    X| X     | X     | X    X|  XXXX |c|
       | XXXXX | X    X| X    X| X    X| X    X| XXXXX |d|
       | XXXXXX| X     | XXXXX | X     | X     | XXXXXX|e|
       | XXXXXX| X     | XXXXX | X     | X     | X     |f|
       |  XXXX | X    X| X     | X  XXX| X    X|  XXXX |g|
       | X    X| X    X| XXXXXX| X    X| X    X| X    X|h|
       |  E    |  n    |  C    |  r    |  P    |  T    |i|
       |      X|      X|      X|      X| X    X|  XXXX |j|
       | X    X| X   X | XXXX  | X  X  | X   X | X    X|k|
       | GT    | PC    | x7    |x6    | x8    | Fraud |l|
       | X    X| XX  XX| X XX X| X    X| X    X| X    X|m|
       | X    X| XX   X| X X  X| X  X X| X   XX| X    X|n|
       |  SGSN | X    X| X    X| X    X| X    X|  gGsN |o|
       | Track | 6    8| s    i|credit | Kc    | G     |p|
       |  XXXX | X    X| X    X| X  X X| X   X |  XXX X|q|
       | XXXXX | X    X| X    X| XXXXX | X   X | X    X|r|
       |  XXXX | X     |  XXXX |      X| X    X|  XXXX |s|
       |--USIM-- |   x0  |   x2  |   x3  |   x8  |   x6  |t|
       | X    X| X    X| X    X| X    X| X    X|  XXXX |u|
       | X    X| X    X| X    X| X    X|  X  X |   XX  |v|
       | X    X| X    X| X    X| X XX X| XX  XX| X    X|w|
       | X    X|  X  X |   XX  |   XX  |  X  X | X    X|x|
       |  X   X|   X X |    X  |    X  |    X  |    X  |y|
       | XXXXXX|     X |    X  |   X   |  X    | XXXXXX|z|
  XXX  | X     | X     |XX     | X     | X     |  XXX  |{|
   X   |   X   |   X   |       |   X   |   X   |   X   |||
  XXX  |     X |     X |     XX|     X |     X |  XXX  |}|
 XX    |X  X  X|    XX |       |       |       |       |~|
'''.splitlines()

	table = {}
	for form in letterforms:
  		if '|' in form:
        		table[form[-2]] = form[:-3].split('|')

	ROWS = len(table.values()[0])

   	for row in range(ROWS):
        	for c in word:
            		print table[c][row],
        	print
    	print


def attacksMenu():
	os.system('clear')
	
	print " \033[34mChoose From the Below Attack Categories\033[0m ".center(105,"#")
	print
	print "0) Location Tracking".rjust(23)
	print "1) Call and SMS Interception".rjust(31)
	print "2) Fraud".rjust(11)
	print 
	print "or type back to return to the main menu".rjust(42)
	print
	
	choice = raw_input("\033[37m(\033[0m\033[2;31mAttacks\033[0m\033[37m)>\033[0m ")
	
	if choice == "0":
		LocationTracking()
		
	elif choice == "1":
		#Interception()
		print "\n\033[34m[*]\033[0mInterception not updated in this release.."
		print "\033[34m[*]\033[0mGoing back to Attacks Menu"
		time.sleep(1.5)
		attacksMenu()
	elif choice == "2":
		#Fraud()
		print "\n\033[34m[*]\033[0mFraud not updated in this release.."
		print "\033[34m[*]\033[0mGoing back to Attacks Menu"
		time.sleep(1.5)
		attacksMenu()
	elif choice == "back":
		mainMenu()
	else:
		print '\n\033[31m[-]Error:\033[0m Please Enter a Valid Choice (0 - 2)'
		time.sleep(1.5)
		attacksMenu()


def LocationTracking():
	os.system('clear')

	print " \033[31mLocation Tracking Module\033[0m ".center(105,"#")
	print " \033[34mSelect a Message from the below\033[0m ".center(105,"#")
	print
	print "   Message".rjust(10)+"\t\t\t Category"
	print "   ---------------------------------------"
	print "0) SendRoutingInfo".rjust(21) +"\t\t CAT1"
	print "1) ProvideSubsriberInfo".rjust(26) + "\t CAT2"
	print "2) SendRoutingInfoForSM".rjust(26) + "\t CAT3"
	print "3) AnyTimeInterrogation".rjust(26) + "\t CAT1"
	print
	print "or type back to go back to Attacks Menu".rjust(42)

	choice = raw_input("\033[37m(\033[0m\033[2;31mLocationTracking\033[0m\033[37m)>\033[0m ")

	if choice == "0":
		tracking.sri()
	elif choice == "1":
		tracking.psi()
	elif choice == "2":
		tracking.srism()
	elif choice == "3":
		tracking.ati()
	elif choice == "back":
		attacksMenu()	
	else:
		print '\n\033[31m[-]Error:\033[0m Please Enter a Valid Choice (0 - 3)'
		time.sleep(1.5)
		LocationTracking()


def mainMenu():
	os.system('clear')
	banner('SiGploit')
	print "\033[33m[-][-]\033[0m\t\tSignaling Exploitation Framework\t\t\033[33m[-][-]\033[0m"
	print "\033[33m[-][-]\033[0m\t\t\tVersion:\033[31mBETA 0.1\033[0m\t\t\t\033[33m[-][-]\033[0m"
	print "\033[33m[-][-]\033[0m\t\tCodedBy:\033[32mLoay AbdelRazek(@sigploit)\033[0m\t\t\033[33m[-][-]\033[0m\n"
	print
	print "0) SS7".rjust(8)
	print "1) Documentation".rjust(18)
	print
	print "or quit to exit SiGploit\n".rjust(28)
	
	choice = raw_input("\033[34msig\033[0m\033[37m>\033[0m ")
	
	if choice == "0":
		os.system('clear')
		attacksMenu()
	if choice == "1":
		print "\n\033[34m[*]\033[0mDocumentation will be updated in the next release.."
		print "\033[34m[*]\033[0mGoing back to Main Menu"
		time.sleep(1.5)
		mainMenu()
	elif choice == "quit":
		print '\nYou are now exiting SiGploit...'
		time.sleep(1)
		sys.exit(0)
	else:
		print '\n\033[31m[-]Error:\033[0m Please Enter a Valid Choice (0 - 2)'
		time.sleep(1.5)
		mainMenu()

def signal_handler(signal,frame):
	print '\nYou are now exiting SiGploit...'
	time.sleep(1)
	sys.exit(0)

signal.signal(signal.SIGINT, signal_handler)	
	
if __name__ =='__main__':
	mainMenu()

if __name__=='__SiGploit__':
	LocationTracking()
	attacksMenu()
	mainMenu()
