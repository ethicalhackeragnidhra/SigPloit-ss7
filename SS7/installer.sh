#!/bin/bash



#Download SS7 Stack from JSS7
download()
{
	url=$1
	wget --progress=dot $url 2>&1 | grep --line-buffered "%" | sed -u -e "s,\.,,g" | awk '{printf("\b\b\b\b%4s", $2)}' 
	echo -ne "\b\b\b\b"
	
}

FILE="restcomm-jss7-7.2.1390.zip"
REST_DIR="restcomm-jss7-7.2.1390"

#Cleaning if any previous download occurred that may be corrupted or interrupted
if [ -f "$FILE" ]
then
        echo -e '\033[34m[*]\033[0mDetected SS7 Stack File..Removing it'
        rm -f $FILE
        echo -e '\033[34m[*]\033[0mDownloading a fresh version of '"$FILE"'..'
fi



if [ -d "$REST_DIR" ]
then
	echo -e '\033[34m[*]\033[0mDetected SS7 Stack Directory..Removing it'
	rm -rf $REST_DIR
	
fi

#Downloading the Stack
echo -e '\033[34m[*]\033[0mRetrieving SS7 Stack'
download "https://github.com/RestComm/jss7/releases/download/7.2.1390/$FILE"

if [ $? == 0 ]
then
	echo -e '\n\033[32m[+]\033[0mSS7 Stack Downloaded..'
else
	echo -e '\033[31m[-]\033[0mError Downloading the Stack..'
	exit 1
fi

#Unzipping
unzip $FILE
rm -f $FILE


#Setting CLASSPATH Variable
export CLASSPATH=$PWD/restcomm-jss7-7.2.1390/ss7/restcomm-ss7-service/lib/*
echo 'export CLASSPATH=$PWD/restcomm-jss7-7.2.1390/ss7/restcomm-ss7-service/lib/*' >> ~/.bashrc
echo 'export CLASSPATH=$PWD/restcomm-jss7-7.2.1390/ss7/restcomm-ss7-service/lib/*' >> ~/.bash_profile

source ~/.bashrc
source ~/.bash_profile

#Creating LIB directory
mkdir Tracking/ati/lib
mkdir Tracking/psi/lib
mkdir Tracking/sri/lib
mkdir Tracking/sri/lib
mkdir Tracking/srism/lib

cp restcomm-jss7-7.2.1390/ss7/restcomm-ss7-service/lib/* Tracking/ati/lib/
cp restcomm-jss7-7.2.1390/ss7/restcomm-ss7-service/lib/* Tracking/psi/lib/
cp restcomm-jss7-7.2.1390/ss7/restcomm-ss7-service/lib/* Tracking/sri/lib/
cp restcomm-jss7-7.2.1390/ss7/restcomm-ss7-service/lib/* Tracking/srism/lib/

if [ $? == 0 ]
then
	echo -e '\n\033[32m[+]\033[0mDirectories set..'

if [ "$CLASSPATH" == "$PWD/restcomm-jss7-7.2.1390/ss7/restcomm-ss7-service/lib/*" ]
then
	echo -e '\n\033[32m[+]\033[0mSiGploit is Ready for use..run SiGploit.py'
	exit 0
else
	echo -e '\033[31m[-]\033[0mError Setting CLASSPATH..'
	exit 1
fi


