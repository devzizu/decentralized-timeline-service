
NODE_MAIN=app.node.Node

compile:
	mvn compile
	mvn clean install
	
package:
	mvn package

run:
	mvn exec:java -q -Dexec.mainClass=$(NODE_MAIN) -Dexec.args="$(ARGS)"

# program run example:
# make run ARGS="-node 1 -pub 10000 -pull 10001 -reply 10002"