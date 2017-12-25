#!/usr/bin/env python
import sys
import os
import SiGploit
import time

from subprocess import *


cwd = os.path.dirname(os.getcwd())

sri_path = os.path.join(cwd,'SS7/Tracking/SRI')
srism_path = os.path.join(cwd,'SS7/Tracking/SRISM')
psi_path = os.path.join(cwd,'SS7/Tracking/PSI')
ati_path = os.path.join(cwd,'SS7/Tracking/ATI')
srigprs_path = os.path.join(cwd,'SS7/Tracking/SRIGPRS')


def sri():
	
	jar_file = 'SendRoutingInfo.jar'

	try:
		sendRoutingInfo = check_call(['java','-jar', os.path.join(sri_path,jar_file)])
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
		print "\033[31m[-]SendRoutingInfo Failed to Launch, Error:\033[0m " + str(e)
		time.sleep(2)
		SiGploit.attacksMenu()
	

def psi():
	
	jar_file = 'ProvideSubscriberInfo.jar'
	
	try:
		psi = check_call(['java','-jar', os.path.join(psi_path,jar_file)])
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
		print "\033[31m[-]ProvideSubscriberInfo Failed to Launch, Error:\033[0m " + str(e)
		time.sleep(2)
		SiGploit.attacksMenu()

def srism():
	jar_file = 'SendRoutingInfoForSM.jar'

	try:
		srism = check_call(['java','-jar', os.path.join(srism_path,jar_file)])
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
		print "\033[31m[-]SendRoutingInfoForSM Failed to Launch, Error:\033[0m " + str(e)
		time.sleep(2)
		SiGploit.attacksMenu()

def ati():
	jar_file = 'AnyTimeInterrogation.jar'

	try:
		ati = check_call(['java','-jar', os.path.join(ati_path,jar_file)])
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
		print "\033[31m[-]AnyTimeInterrogation Failed to Launch, Error:\033[0m " + str(e)
		time.sleep(2)
		SiGploit.attacksMenu()

def srigprs():
	jar_file = 'SendRoutingInfoForGPRS.jar'
	
	try:
		srigprs = check_call(['java','-jar', os.path.join(srigprs_path,jar_file)])
		if srigprs == 0:
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
		print "\033[31m[-]SendRoutingInfoForGPRS Failed to Launch, Error:\033[0m " + str(e)
		time.sleep(2)
		SiGploit.attacksMenu()
