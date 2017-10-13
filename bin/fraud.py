#!/usr/bin/env python
import sys
import os
import SiGploit
import time

from subprocess import *

cwd = os.path.dirname(os.getcwd())

simsi_path = os.path.join(cwd,'SS7/Fraud/SIMSI')
mtsms_path = os.path.join(cwd,'SS7/Fraud/MTSMS')


def simsi():
	
	jar_file = 'SendIMSI.jar'

	try:
		sendIMSI = check_call(['java','-jar', os.path.join(simsi_path,jar_file)])
		if sendIMSI == 0:
			fr = raw_input('\nWould you like to go back to Fraud Menu? (y/n): ')
			if fr == 'y' or fr == 'yes':
				SiGploit.Fraud()
			elif fr == 'n' or fr == 'no':
				attack_menu = raw_input('Would you like to choose another attacks category? (y/n): ')
				if attack_menu == 'y'or attack_menu =='yes':
					SiGploit.attacksMenu()
				elif attack_menu == 'n' or attack_menu =='no':
					main_menu = raw_input('Would you like to go back to the main menu? (y/exit): ')
					if main_menu == 'y' or main_menu =='yes':
						SiGploit.mainMenu()
					elif main_menu =='exit':
						print 'TCAP End...'
						sys.exit(0)
			
	
	except CalledProcessError as e:
		print "\033[31mSendIMSI Failed to Launch, Error: " + e.message


def mtsms():
	
	jar_file = 'MTForwardSMS.jar'

	try:
		mtForwardSMS = check_call(['java','-jar', os.path.join(mtsms_path,jar_file)])
		if mtForwardSMS == 0:
			fr = raw_input('\nWould you like to go back to Fraud Menu? (y/n): ')
			if fr == 'y' or fr == 'yes':
				SiGploit.Fraud()
			elif fr == 'n' or fr == 'no':
				attack_menu = raw_input('Would you like to choose another attacks category? (y/n): ')
				if attack_menu == 'y'or attack_menu =='yes':
					SiGploit.attacksMenu()
				elif attack_menu == 'n' or attack_menu =='no':
					main_menu = raw_input('Would you like to go back to the main menu? (y/exit): ')
					if main_menu == 'y' or main_menu =='yes':
						SiGploit.mainMenu()
					elif main_menu =='exit':
						print 'TCAP End...'
						sys.exit(0)
			
	
	except CalledProcessError as e:
		print "\033[31mMTForwardSMS Failed to Launch, Error: " + e.message
	
