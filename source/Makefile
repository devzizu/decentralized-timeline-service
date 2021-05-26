
compile:
	mvn compile
	mvn clean install
	
package:
	mvn package

clean:
	mvn clean

run:
	mvn exec:java -Dexec.mainClass=$(main) -Dexec.args="$(args)"

# run node REGISTER REQUEST
# make run main="app.node.Node" args="--register -node mirs1 -pub 10000 -pull 10001 -reply 10002"

# run node LOGIN REQUEST
# make run main="app.node.Node" args="--login -node mirs1 -pub 10000 -pull 10001 -reply 10002"

# run central example
# make run main="app.central.Central" args="-central central1"