// test for printNodes()

#include "Graph.h"

int main() {

	// make a graph
	gdwg::Graph<unsigned int,int> g;

    // std::cout << "Adding Nodes" << std::endl;
	for (int i = 1; i <= 15; ++i){
		g.addNode(i);
	}

    // std::cout << "Adding Edges" << std::endl;
	for (int i = 1; i <= 15; ++i) {
		g.addEdge(1, i, 100);
		g.addEdge(i, 1, 100);
	}

    // std::cout << "mergeReplace Nodes" << std::endl;
	for (int i = 2;i <= 15; ++i){
		g.mergeReplace(i,1);
	}

    // std::cout << "Print Edges" << std::endl;
	g.printEdges(1);

    // std::cout << "-------------------" << std::endl;
}

