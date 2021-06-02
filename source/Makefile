
ifeq (run,$(firstword $(MAKECMDGOALS)))
  # use the rest as arguments for "run"
  RUN_ARGS := $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS))
  # ...and turn them into do-nothing targets
  $(eval $(RUN_ARGS):;@:)
endif

compile:
	mvn compile
	mvn clean install
	
package:
	mvn package

clean:
	mvn clean

run:
	mvn exec:java -Dexec.mainClass=$(main) -Dexec.args="$(args)"

central:
	make run main="app.central.Central" args="-central central1"

node1_register:
	make run main="app.node.Node" args="--register -node node1 -pub 10000 -pull 10001 -reply 10002"

node1_login:
	make run main="app.node.Node" args="--login -node node1 -pub 10000 -pull 10001 -reply 10002"

node2_register:
	make run main="app.node.Node" args="--register -node node2 -pub 10010 -pull 10011 -reply 10012"

node2_login:
	make run main="app.node.Node" args="--login -node node2 -pub 10010 -pull 10011 -reply 10012"

node3_register:
	make run main="app.node.Node" args="--register -node node3 -pub 10020 -pull 10021 -reply 10022"

node3_login:
	make run main="app.node.Node" args="--login -node node3 -pub 10020 -pull 10021 -reply 10022"

node4_register:
	make run main="app.node.Node" args="--register -node node4 -pub 10030 -pull 10031 -reply 10032"

node4_login:
	make run main="app.node.Node" args="--login -node node4 -pub 10030 -pull 10031 -reply 10032"

node5_register:
	make run main="app.node.Node" args="--register -node node5 -pub 10040 -pull 10041 -reply 10042"

node5_login:
	make run main="app.node.Node" args="--login -node node5 -pub 10040 -pull 10041 -reply 10042"