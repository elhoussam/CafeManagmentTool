# CafeManagmentTool
Cafe Management System : Java Appllication Based on client-server topology, using the RMI technologie, and the SERVER in our case is the PC because it provoides all the information for the MANAGER.
# How to use
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
Build
----
to build the entire app with one command
```
make all
```

Enjoy.
