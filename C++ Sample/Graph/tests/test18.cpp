
#include "Graph.h"

int main() {
	
	// create 3 graphs
	auto g = new gdwg::Graph<int,int>{};

	std::cout << "Adding 15000 Nodes" << std::endl;	
	for (int i = 1; i <= 15000; ++i) {
		g->addNode(i);
	}

	std::cout << "Try inserting 100 duplicate edges to Nodes 1" << std::endl;
	for (int i = 1; i <= 100; ++i) {
		g->addEdge(1, 1, 100);
	}

	g->printEdges(1);

	std::cout << "Copying graph" << std::endl;	
	auto g1 = *g;
	
	std::cout << "Clearing the new graph" << std::endl;	
	g1.clear();

	std::cout << "Nodes in g:" << std::endl;	
	g->printNodes();


	std::cout << "Nodes in g1:" << std::endl;	
	g1.printNodes();

	std::cout << "Add some nodes to g1:" << std::endl;	

	for (int i = 1; i <= 20; ++i) {
		g1.addNode(i);
	}

	std::cout << "Add some edges to g1." << std::endl;

	for (int i = 1; i <= 10; ++i) {
		g1.addEdge(1,i,66);
	}

	for (int i = 1; i <= 10; ++i) {
		g1.addEdge(1,i,66);
	}

	for (int i = 1; i <= 10; ++i) {
		g1.addEdge(1,i,50);
	}

	for (int i = 1; i <= 5; ++i) {
		g1.addEdge(2,i,50);
	}

	for (int i = 3; i <= 20; ++i) {
		g1.addEdge(i,1,1999);
	}

	std::cout << "Print edges in g1:" << std::endl;

	for (int i = 1; i <= 20; ++i) {
		g1.printEdges(i);
	}

	std::cout << "mergeReplace 1 to 2:" << std::endl;
	g1.mergeReplace(1,2);

	std::cout << "Print edges in g1 after merging:" << std::endl;

	for (int i = 2; i <= 20; ++i) {
		g1.printEdges(i);
	}

	//for (g.begin(); !g.end(); g.next()){
	//	std::cout << g.value() << std::endl;
	//}

}