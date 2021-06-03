
# CENTRAL : ./run.sh central
# NODE    : ./run.sh node 1/2/... register/login

compile:
	mvn compile
	mvn clean install
	
package:
	mvn package

clean:
	mvn clean

remove_saves:
	rm -rf ../nodes/node*

run:
	mvn exec:java -Dexec.mainClass=$(main) -Dexec.args="$(args)"