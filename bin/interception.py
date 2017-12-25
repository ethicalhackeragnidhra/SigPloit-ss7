#!/usr/bin/env python
import sys
import os
import SiGploit
import time

from subprocess import *


cwd = os.path.dirname(os.getcwd())

ul_path = os.path.join(cwd,'SS7/Interception/UL')



def ul():
	
	jar_file = 'UpdateLocation.jar'

	try:
		updateLocation = check_call(['java','-jar', os.path.join(ul_path,jar_file)])
		if updateLocation == 0:
			it = raw_input('\nWould you like to go back to Interception Menu? (y/n): ')
			if it == 'y' or it == 'yes':
				SiGploit.Interception()
			elif it == 'n' or it == 'no':
				attack_menu = raw_input('Would you like to choose another attacks category? (y/n): ')
				if attack_menu == 'y'or attack_menu =='yes':
					SiGploit.attacksMenu()
				elif attack_menu == 'n' or attack_menu =='no':
					main_menu = raw_input('Would you like to go back to the main menu? (y/exit): ')
					if main_menu == 'y' or main_menu =='yes':
						SiGploit.mainMenu()
					elif main_menu =='exit':
						print 'TCAP End...'
						time.sleep(1)
						sys.exit(0)
			
	
	except CalledProcessError as e:
		print "\033[31m[-]UpdateLocation Failed to Launch, Error: " + e.message
		time.sleep(2)
		SiGploit.attacksMenu()
	
