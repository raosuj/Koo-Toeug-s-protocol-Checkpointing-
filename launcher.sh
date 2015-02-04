#!/bin/bash


# Change this to your netid
netid=nxg121330

#
# Root directory of your project
PROJDIR=$HOME/AOS/Project3

#
# This assumes your config file is named "config.txt"
# and is located in your project directory
#
CONFIG=$PROJDIR/config.txt

#
# Directory your java classes are in
#
BINDIR=$PROJDIR

#
# Your main project class
#
PROG=Project3

n=1

cat $CONFIG | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
    read i
    while read line 

    do
        host=$( echo $line | awk '{ print $1 }' )
	 echo $host
	 ssh $netid@$host "cd $PROJDIR; java Application $n $1; exit;" &
        n=$(( n + 1 ))
    done
   
)

