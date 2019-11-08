M=mvn clean
MANAGER_DIR=setupManager
PC_DIR=setupPc
UTILS_DIR=utils
CLASSES_DIR=target/classes/me/elhoussam/implementation/
# && echo `pwd`
.SILENT all:package
	@echo "Build project phase is done"
package: copystubs managerpackage pcpackage
	@echo "pacakge phase is done"
managerpackage:
	cd $(MANAGER_DIR)  && $(M) package > /dev/null
	@echo $(MANAGER_DIR)" packaging succeed " 
pcpackage:
	cd $(PC_DIR)  && $(M) package > /dev/null
	@echo $(PC_DIR)" packaging succeed " 	
copystubs: rmicc
	cd $(MANAGER_DIR)    && ./copystubs.sh
	cd $(PC_DIR)    && ./copystubs.sh
rmicc: compile
	cd $(MANAGER_DIR)    && ./rmic.sh
	cd $(PC_DIR)    && ./rmic.sh
compile: installutils pccompile managercompile
	@echo "compile phase is done"
managercompile:
	cd $(MANAGER_DIR)  && $(M) compile > /dev/null
	@echo $(MANAGER_DIR)" compilation succeed " 
pccompile:
	cd $(PC_DIR)  && $(M) compile > /dev/null
	@echo $(PC_DIR)" compilation succeed "
installutils: cleanall
	cd $(UTILS_DIR)  && $(M) install > /dev/null
	@echo $(UTILS_DIR)" installation succeed " 
cleanall:
	cd $(MANAGER_DIR)    && ./cleanall.sh
	@echo $(MANAGER_DIR)" Cleaning done "
	cd $(PC_DIR)    && ./cleanall.sh
	@echo $(PC_DIR)" Cleaning done "
	

	



	