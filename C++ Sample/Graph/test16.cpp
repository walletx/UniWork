// test for printNodes()

#include "Graph.h"

int main() {
	
	// make a graph
	gdwg::Graph<unsigned int,int> g;
	
	for (int i = 1; i <= 15000; i++){
		g.addNode(i);
	}

	for (int i = 1; i <= 15000; i++){
		g.addEdge(1, i, 100);
	}

	g.clear();
	
}

