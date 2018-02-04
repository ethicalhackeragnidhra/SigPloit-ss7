#!/usr/bin/env python
'''
Created on 1 Feb 2018

@author: loay
'''

import sys
import os
import time


from ss7main import *
from subprocess import *



sri_path = os.path.join(os.getcwd(),'ss7/attacks/tracking/sri')
srism_path = os.path.join(os.getcwd(),'ss7/attacks/tracking/srism')
psi_path = os.path.join(os.getcwd(),'ss7/attacks/tracking/psi')
ati_path = os.path.join(os.getcwd(),'ss7/attacks/tracking/ati')
#srigprs_path = os.path.join(os.getcwd(),'ss7/attacks/tracking/srigprs')



def sri():
	
	jar_file = 'SendRoutingInfo.jar'

	try:
		sendRoutingInfo = check_call(['java','-jar', os.path.join(sri_path,jar_file)])
		if sendRoutingInfo == 0:
			lt = raw_input('\nWould you like to go back to LocationTracking Menu? (y/n): ')
			if lt == 'y' or lt == 'yes':
				LocationTracking()
			elif lt == 'n' or lt == 'no':
				attack_menu = raw_input('Would you like to choose another attacks category? (y/n): ')
				if attack_menu == 'y'or attack_menu =='yes':
					attacksMenu()
				elif attack_menu == 'n' or attack_menu =='no':
					main_menu = raw_input('Would you like to go back to the main menu? (y/exit): ')
					if main_menu == 'y' or main_menu =='yes':
						mainMenu()
					elif main_menu =='exit':
						print 'TCAP End...'
						sys.exit(0)
			
	
	except CalledProcessError as e:
		print "\033[31m[-]Error:\033[0mSendRoutingInfo Failed to Launch, " + e.message
		time.sleep(2)
	

def psi():
	
	jar_file = 'ProvideSubscriberInfo.jar'
	
	try:
		psi = check_call(['java','-jar', os.path.join(psi_path,jar_file)])
		if psi == 0:
			lt = raw_input('\nWould you like to go back to LocationTracking Menu? (y/n): ')
			if lt == 'y' or lt == 'yes':
				LocationTracking()
			elif lt == 'n' or lt == 'no':
				attack_menu = raw_input('Would you like to choose another attacks category? (y/n): ')
				if attack_menu == 'y'or attack_menu =='yes':
					attacksMenu()
				elif attack_menu == 'n' or attack_menu =='no':
					main_menu = raw_input('Would you like to go back to the main menu? (y/exit): ')
					if main_menu == 'y' or main_menu =='yes':
						mainMenu()
					elif main_menu =='exit':
						print 'TCAP End...'
						sys.exit(0)
			
	
	except CalledProcessError as e:
		print "\033[31m[-]Error:\033[0m"+jar_file+" Failed to Launch, " + e.message
		time.sleep(2)

def srism():
	jar_file = 'SendRoutingInfoForSM.jar'

	try:
		srism = check_call(['java','-jar', os.path.join(srism_path,jar_file)])
		if srism == 0:
			lt = raw_input('\nWould you like to go back to LocationTracking Menu? (y/n): ')
			if lt == 'y' or lt == 'yes':
				LocationTracking()
			elif lt == 'n' or lt == 'no':
				attack_menu = raw_input('Would you like to choose another attacks category? (y/n): ')
				if attack_menu == 'y'or attack_menu =='yes':
					attacksMenu()
				elif attack_menu == 'n' or attack_menu =='no':
					main_menu = raw_input('Would you like to go back to the main menu? (y/exit): ')
					if main_menu == 'y' or main_menu =='yes':
						mainMenu()
					elif main_menu =='exit':
						print 'TCAP End...'
						sys.exit(0)
			
	
	except CalledProcessError as e:
		print "\033[31m[-]Error:\033[0m"+jar_file+" Failed to Launch, " + e.message
		time.sleep(2)

def ati():
	jar_file = 'AnyTimeInterrogation.jar'

	try:
		ati = check_call(['java','-jar', os.path.join(ati_path,jar_file)])
		if ati == 0:
			lt = raw_input('\nWould you like to go back to LocationTracking Menu? (y/n): ')
			if lt == 'y' or lt == 'yes':
				LocationTracking()
			elif lt == 'n' or lt == 'no':
				attack_menu = raw_input('Would you like to choose another attacks category? (y/n): ')
				if attack_menu == 'y'or attack_menu =='yes':
					attacksMenu()
				elif attack_menu == 'n' or attack_menu =='no':
					main_menu = raw_input('Would you like to go back to the main menu? (y/exit): ')
					if main_menu == 'y' or main_menu =='yes':
						mainMenu()
					elif main_menu =='exit':
						print 'TCAP End...'
						sys.exit(0)
			
	
	except CalledProcessError as e:
		print "\033[31m[-]Error:\033[0m"+jar_file+" Failed to Launch, " + str(e)
		time.sleep(2)

def srigprs():
	jar_file = 'SendRoutingInfoForGPRS.jar'
	
	try:
		srigprs = check_call(['java','-jar', os.path.join(srigprs_path,jar_file)])
		if srigprs == 0:
			lt = raw_input('\nWould you like to go back to LocationTracking Menu? (y/n): ')
			if lt == 'y' or lt == 'yes':
				LocationTracking()
			elif lt == 'n' or lt == 'no':
				attack_menu = raw_input('Would you like to choose another attacks category? (y/n): ')
				if attack_menu == 'y'or attack_menu =='yes':
					attacksMenu()
				elif attack_menu == 'n' or attack_menu =='no':
					main_menu = raw_input('Would you like to go back to the main menu? (y/exit): ')
					if main_menu == 'y' or main_menu =='yes':
						mainMenu()
					elif main_menu =='exit':
						print 'TCAP End...'
						sys.exit(0)
			
	
	except CalledProcessError as e:
		print "\033[31m[-]Error:\033[0m"+jar_file+" Failed to Launch,  " + e.message
		time.sleep(2)
