#!/bin/bash
DIR=target/classes/me/elhoussam/implementation/
cd $DIR
#echo `pwd`
#ls -l *.class | awk '{print $9}' | cut -d'.' -f1
mapfile -t my_array < <(  ls -l *.class | awk '{print $9}' | cut -d'.' -f1 )
cd ../../../
#echo `pwd` 
for i in "${my_array[@]}"; do 
	echo "============================ rmic $i" 
	rmic me.elhoussam.implementation.$i ; 
done