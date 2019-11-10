declare -a arr=("setupManager" "setupPc" "utils")
for i in "${arr[@]}"; do 
	cd $i && echo "Clean $i progressig" &&
	rm -frv target &&
	rm -frv *_logs &&
	rm -frv src/*Stub.class && echo "clean $i done" &&
	cd ..
done