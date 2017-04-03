#!/usr/bin/env python
import sys
import os
import SiGploit
import time

from subprocess import *



home_dir = os.path.expanduser('~')
sri_path = os.path.join(home_dir,'TelcoSploit/out/artifacts/Tracking/sri')
srism_path = os.path.join(home_dir,'TelcoSploit/out/artifacts/Tracking/srism')
psi_path = os.path.join(home_dir,'TelcoSploit/out/artifacts/Tracking/psi')
ati_path = os.path.join(home_dir,'TelcoSploit/out/artifacts/Tracking/ati')


"""
sri_path = os.path.join(home_dir,'/SiGploit/SS7/Tracking/sri')
srism_path = os.path.join(home_dir,'/SiGploit/SS7/Tracking/srism')
psi_path = os.path.join(home_dir,'/SiGploit/SS7/Tracking/psi')
ati_path = os.path.join(home_dir,'/SiGploit/SS7/Tracking/ati')
"""
def sri():
	
	jar_file = 'SendRoutingInfo.jar'
	if jar_file not in os.listdir(sri_path):
		print '\033[31m[-]Error:\033[0m This attack is not found..Fork for Updates'
		time.sleep(1)
		print '\033[34m[*]\033[0mGoing back to Location Tracking Attacks Menu'
		time.sleep(1.5)
		SiGploit.LocationTracking()	
	else:
		try:
			sendRoutingInfo = check_call(['java','-cp','$SIGPLOIT','-jar', os.path.join(sri_path,jar_file)])
			if sendRoutingInfo == 0:
				lt = raw_input('\nWould you like to go back to LocationTracking Menu? (y/n): ')
				if lt == 'y' or lt == 'yes':
					SiGploit.LocationTracking()
				elif lt == 'n' or lt == 'no':
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
			print "\033[31mSendRoutingInfo Failed to Launch, Error: " + e.message
	

def psi():
	
	jar_file = 'ProvideSubscriberInfo.jar'
	if jar_file not in os.listdir(psi_path):
		print '\033[31m[-]Error:\033[0m This attack is not found..Fork for Updates'
		time.sleep(1)
		print '\033[34m[*]\033[0mGoing back to Location Tracking Attacks Menu'
		time.sleep(1.5)
		SiGploit.LocationTracking()
	else:
		try:
			psi = check_call(['java','-cp','$SIGPLOIT','-jar', os.path.join(psi_path,jar_file)])
			if psi == 0:
				lt = raw_input('\nWould you like to go back to LocationTracking Menu? (y/n): ')
				if lt == 'y' or lt == 'yes':
					SiGploit.LocationTracking()
				elif lt == 'n' or lt == 'no':
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
			print "\033[31m"+jar_file+" Failed to Launch, Error: " + e.message

def srism():
	jar_file = 'SendRoutingInfoForSM.jar'
	if jar_file not in os.listdir(srism_path):
		print '\033[31m[-]Error:\033[0m This attack is not found..Fork for Updates'
		time.sleep(1)
		print '\033[34m[*]\033[0mGoing back to Location Tracking Attacks Menu'
		time.sleep(1.5)
		SiGploit.LocationTracking()
	else:
		try:
			srism = check_call(['java','-cp','$SIGPLOIT','-jar', os.path.join(srism_path,jar_file)])
			if srism == 0:
				lt = raw_input('\nWould you like to go back to LocationTracking Menu? (y/n): ')
				if lt == 'y' or lt == 'yes':
					SiGploit.LocationTracking()
				elif lt == 'n' or lt == 'no':
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
			print "\033[31m"+jar_file+" Failed to Launch, Error: " + e.message

def ati():
	jar_file = 'AnyTimeInterrogation.jar'
	if jar_file not in os.listdir(ati_path):
		print '\033[31m[-]Error:\033[0m This attack is not found..Fork for Updates'
		time.sleep(1)
		print '\033[34m[*]\033[0mGoing back to Location Tracking Attacks Menu'
		time.sleep(1.5)
		SiGploit.LocationTracking()
	else:
		try:
			ati = check_call(['java','-cp','$SIGPLOIT','-jar', os.path.join(ati_path,jar_file)])
			if ati == 0:
				lt = raw_input('\nWould you like to go back to LocationTracking Menu? (y/n): ')
				if lt == 'y' or lt == 'yes':
					SiGploit.LocationTracking()
				elif lt == 'n' or lt == 'no':
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
			print "\033[31m"+jar_file+" Failed to Launch, Error: " + e.message
