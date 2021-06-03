
import random
import math
import networkx
import matplotlib.pyplot as plt
import time
import numpy as np
from pprint import pprint
import copy

TREE_CREATION_BENCH = {}
NR_SAMPLES = 20

def main():

    broadcastRes = {}

    for testSize in np.arange(10, 1010, 10).tolist():
        
        TREE_CREATION_BENCH[testSize] = []
        broadcastRes[testSize] = []

        for sampleNumber in range(0, NR_SAMPLES):
            #ELAPSED, GRAPH = create_points_based_tree(testSize)
            ELAPSED, GRAPH = create_random_tree(testSize)
            TREE_CREATION_BENCH[testSize].append(ELAPSED)
            broadcastRes[testSize] = broadcast_graph(testSize, GRAPH)

    plottedData = { "test": [], "avg": [] }

    for test in TREE_CREATION_BENCH:
        testAvg   = sum(TREE_CREATION_BENCH[test]) / len(TREE_CREATION_BENCH[test])
        plottedData["test"].append(test)
        plottedData["avg"].append(testAvg)

    plot_results(plottedData, "size of tree (nr. of nodes)", "average time to create tree (ms)", "Benchmarks (samples={})".format(NR_SAMPLES), "test", "avg")

    plottedData = { "test": [], "avg": [] }

    for test in broadcastRes:
        testAvg   = float(sum(broadcastRes[test])) / float(len(broadcastRes[test]))
        plottedData["test"].append(test)
        plottedData["avg"].append(testAvg)

    pprint(plottedData)
    plot_results(plottedData, "size of tree (nr. of nodes)", "avg rounds", "Benchmarks (samples={})".format(NR_SAMPLES), "test", "avg")

    #networkx.draw(GRAPH, with_labels=True)
    #plt.show()

def broadcast_graph(size, GRAPH):

    results = []

    for sampleNumber in range(0, NR_SAMPLES):

        selectedNode = rootName()
        foundNodes = {selectedNode}
        currentRound = 0
        currentNeighbours = {selectedNode}

        print("\ttest size = {} [sample#{}] broadcasting from node 0".format(size, sampleNumber))

        while len(foundNodes) < size:

            for nb in currentNeighbours:
                myFriends = GRAPH.neighbors(nb)
                for friend in myFriends:
                    foundNodes.add(friend)

            currentNeighbours = foundNodes.difference(currentNeighbours)

            currentRound = currentRound + 1                
        
        results.append(currentRound)

    # networkx.draw(GRAPH, with_labels=True)
    # plt.show()

    return results

def create_random_tree(NR_ELEMTS_ATTACH):
    
    GRAPH = networkx.Graph()
    GRAPH.add_node(rootName())

    NODES = [{"id": idx, "points": -1, "avgSession": round(random.uniform(0, 100), 2), "location": random.randint(1, 1000)} for idx in range(1, NR_ELEMTS_ATTACH+1)]
 
    ST_TIME = time.time()
   
    GRAPH.add_nodes_from([nodeName(NODES[nodeIdx]) for nodeIdx in range(0, len(NODES))])

    added = [rootName()]
    for node in NODES:
        choosed = random.choice(added)
        added.append(nodeName(node))
        GRAPH.add_edge(choosed, nodeName(node))

    ELAPSED = round((time.time()-ST_TIME)*1000, 3)

    return ELAPSED, GRAPH

def create_points_based_tree(NR_ELEMTS_ATTACH):
    
    GRAPH = networkx.Graph()
    GRAPH.add_node(rootName())

    NODES = [{"id": idx, "points": -1, "avgSession": round(random.uniform(0, 100), 2), "location": random.randint(1, 1000)} for idx in range(1, NR_ELEMTS_ATTACH+1)]

    targetNodeLocation = 0
    nodePoints = []

    ST_TIME = time.time()

    for nodeIdx in range(0, len(NODES)):
        NODES[nodeIdx]["points"] = points(NODES[nodeIdx]["avgSession"], NODES[nodeIdx]["location"], targetNodeLocation)

    NODES = sorted(NODES, key=lambda x: x["points"], reverse = True)

    GRAPH.add_nodes_from([nodeName(NODES[nodeIdx]) for nodeIdx in range(0, len(NODES))])

    highIdx = math.ceil(len(NODES)/2)

    # connect the first half to root node
    for node in NODES[:highIdx]:
        GRAPH.add_edge(rootName(), nodeName(node))

    # connect the second half to a random node
    choosedNodes = copy.deepcopy(NODES[:highIdx])
    for node in NODES[highIdx:]:
        randomNode = random.choice(choosedNodes)
        choosedNodes.append(node)
        GRAPH.add_edge(nodeName(node), nodeName(randomNode))

    ELAPSED = round((time.time()-ST_TIME)*1000, 3)

    return ELAPSED, GRAPH

def plot_results(plottedData, xlabel, ylabel, title, xname, yname):
    plt.plot(plottedData[xname], plottedData[yname], marker="o", linestyle="--")
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.title(title)
    plt.grid(True)
    plt.show()

def points(sessionAvgTime, location, target):
    return round(sessionAvgTime / abs(location-target), 2)

def nodeName(node):
    return "node{}\n({})".format(node["id"], node["points"])

def rootName():
    return nodeName({"id": 0, "points": "root"})

if __name__ == '__main__':
    main()