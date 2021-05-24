
compile:
	mvn compile
	mvn clean install
	
package:
	mvn package

clean:
	mvn clean

run:
	mvn exec:java -q -Dexec.mainClass=$(main) -Dexec.args="$(args)"

# program run example:
# make run main="app.node.Node" args="--register -node 1 -pub 10001 -pull 10002 -reply 10002"