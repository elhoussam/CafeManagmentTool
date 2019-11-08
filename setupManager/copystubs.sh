#!/bin/bash
DIR=target/classes/me/elhoussam/implementation/
PC_DIR=setupPc
cd $DIR
#echo `pwd`
#ls -l *.class | awk '{print $9}' | cut -d'.' -f1
mapfile -t my_array < <(  ls -l *_Stub.* | awk '{print $9}')
cd ../../../../../
#echo `pwd` 
for i in "${my_array[@]}"; do 
	echo "================================== copy ($i)"
	cp -v "$DIR$i" "../$PC_DIR/src/"
	#echo "rmic me.elhoussam.implementation.$i : "
	#rmic me.elhoussam.implementation.$i; 
done