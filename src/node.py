
import argparse
import logging
import asyncio
import threading
import sys

from kademlia.network import Server

# Setting up logger for kademlia
LOGGER = None

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
    parser.add_argument('-m', '--node_mode', dest='node_mode', type=str, required=True)
    return parser.parse_args()

def init_node(ARGS):

    loop = asyncio.get_event_loop()
    loop.set_debug(True)

    server = Server()
    asyncio.run(server.listen(ARGS.server_port))

    if ARGS.node_mode == "bootstrap":
        asyncio.run(server.bootstrap([(ARGS.boostrap_host, ARGS.boostrap_port)]))

    return (server, loop)

async def user_input(server):
    while True:
        key = input("key: ")
        print("entered key:", key)
        val = input("val: ")
        print("entered val:", val)
        await server.set(key, val)
        print("getting val from DHT:")
        print(await server.get(key))

def callback_user_input(server):
    loop = asyncio.new_event_loop()
    asyncio.set_event_loop(loop)
    loop.run_until_complete(user_input(server))
    loop.close()

def main():

    # program arguments
    ARGS = setup_arguments_parser()

    # kademlia logger
    setup_logger()

    print("[init] starting peer ", ARGS)

    (server, loop) = init_node(ARGS)

    # if ARGS.node_mode == "start":
    #    threading.Thread(target=callback_user_input, args=(server,)).start()

    try:

        loop.run_forever()
    
    except KeyboardInterrupt:
        pass
    finally:
        server.stop()
        loop.close()

if __name__ == "__main__":
    main()

# sudo lsof -i -P -n | grep LISTEN