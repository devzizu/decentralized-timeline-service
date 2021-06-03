
import argparse
import logging
import asyncio
import threading
import sys

from kademlia.network import Server

# Setting up logger for kademlia
LOGGER = None
# Program arguments
ARGS = None
# Server node
NODE_SERVER = None 

def setup_logger():
    global LOGGER
    handler = logging.StreamHandler()
    formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
    handler.setFormatter(formatter)
    LOGGER = logging.getLogger('kademlia')
    LOGGER.addHandler(handler)
    LOGGER.setLevel(logging.DEBUG)

def setup_arguments_parser():
    parser = argparse.ArgumentParser()
    parser.add_argument('-bhost', '--boostrap_host', dest='boostrap_host', type=str, required=False)
    parser.add_argument('-bport', '--boostrap_port', dest='boostrap_port', type=int, required=False)
    parser.add_argument('-server', '--server_port', dest='server_port', type=int, required=True)
    parser.add_argument('-m', '--node_mode', choices=["start", "bootstrap"], dest='node_mode', type=str, required=True)
    return parser.parse_args()

def init_node(ARGS):

    loop = asyncio.new_event_loop()
    loop.set_debug(True)

    server = Server()
    loop.run_until_complete(server.listen(ARGS.server_port))

    if ARGS.node_mode == "bootstrap":
        loop.run_until_complete(server.bootstrap([(ARGS.boostrap_host, ARGS.boostrap_port)]))

    return (server, loop)

async def read_user_input():
    while True:
        print("key: ")
        key = sys.stdin.readline()
        print("val: ")
        val = sys.stdin.readline()
        await NODE_SERVER.set(key, val)
        print("answer: " + await NODE_SERVER.get(key))

def callback_node_server():
    
    global NODE_SERVER
    (NODE_SERVER, loop) = init_node(ARGS)

    try:

        loop.run_forever()

    except KeyboardInterrupt:
        pass
    finally:
        NODE_SERVER.stop()
        loop.close()

def main():

    # program arguments
    global ARGS
    ARGS = setup_arguments_parser()

    # kademlia logger
    setup_logger()

    print("[init] starting peer ", ARGS)

    # run node server
    threading.Thread(target=callback_node_server).start()
    threading.Thread(target=asyncio.run, args=(read_user_input(),)).start()
    
if __name__ == "__main__":
    main()

# sudo lsof -i -P -n | grep LISTEN