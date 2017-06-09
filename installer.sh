#!/bin/bash



#Download SS7 Stack from JSS7
download()
{
	url=$1
	wget --progress=dot $url 2>&1 | grep --line-buffered "%" | sed -u -e "s,\.,,g" | awk '{printf("\b\b\b\b%4s", $2)}' 
	echo -ne "\b\b\b\b"
	
}

FILE="restcomm-jss7-7.3.1398.zip"
REST_DIR="restcomm-jss7-7.3.1398"

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
download "https://github.com/RestComm/jss7/releases/download/7.3.1398/$FILE"

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


if [ $? == 0 ]
then
	echo -e '\n\033[32m[+]\033[0mSiGploit is Ready for use..run SiGploit.py'
	exit 0
else
	echo -e '\033[31m[-]\033[0mError Installing SiGploit..'
	exit 1
fi
