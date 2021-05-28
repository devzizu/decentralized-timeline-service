
compile:
	mvn compile
	mvn clean install
	
package:
	mvn package

clean:
	mvn clean

run:
	mvn exec:java -Dexec.mainClass=$(main) -Dexec.args="$(args)"

ifeq (0,1)
NODE 1: run node REGISTER REQUEST
make run main="app.node.Node" args="--register -node mirs1 -pub 10000 -pull 10001 -reply 10002"
NODE 2: run node REGISTER REQUEST
make run main="app.node.Node" args="--register -node mirs2 -pub 10010 -pull 10011 -reply 10012"
NODE 3: run node REGISTER REQUEST
make run main="app.node.Node" args="--register -node mirs3 -pub 10020 -pull 10021 -reply 10022"

NODE 1: run node LOGIN REQUEST
make run main="app.node.Node" args="--login -node mirs1 -pub 10000 -pull 10001 -reply 10002"
NODE 2: run node LOGIN REQUEST
make run main="app.node.Node" args="--login -node mirs2 -pub 10010 -pull 10011 -reply 10012"

run central example
make run main="app.central.Central" args="-central central1"
endif