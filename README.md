# CafeManagmentTool
Cafe Management System : Java Appllication Based on client-server topology, using the RMI technologie, and the SERVER in our case is the PC because it provoides all the information for the MANAGER.
# Build It 

Prerequisit
------------
* jdk 1.8
* maven 
* linux system OR cygwin with make package

Step by Step
------------
you need to install utils package : 
```
cd utils
mvn clean install 
```
after that we need to package setup for both side :
```
cd setupManager
mvn clean package
cd ../setupPc
mvn clean package
```
then we need to generate stubs for the REMOTE_OBJECT :
```
cd setupManager/target/classes/
rmic me.elhoussam.implementation.REMOTE_OBJECT
#the same commands for the setupPc
```
before packaging the distributable version, we must copy the stubs:
```
cd setupManager/
cp -v target/classes/me/elhoussam/implementation/*Stub.class ../setupPC/src/
#the same commands for the setupPc
```
and finally we are ready to create FatJar :
```
cd setupManager/
mvn clean package
#the same commands for the setupPc
```
OneCommand
----
to build the entire app with one command
```
make -s all
```

Enjoy.
