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
	
	for (g.begin(); !g.end(); g.next()){
	   	std::cout << g.value() << std::endl;
	   	
		if (g.value() == 1) {
	   		std::cout << "edgy: ";
	   		g.printEdges(g.value());
	   	}
	}
}