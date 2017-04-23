// test for printNodes()

#include "../Graph.h"

int main() {
	
	// make a graph
	gdwg::Graph<unsigned int,int> g;
	
	g.addNode(1);
	g.addNode(2);
	g.addNode(3);
	g.addNode(4);
	g.addNode(5);
	
	g.addEdge(1,2,1);
	g.addEdge(1,3,1);
	g.addEdge(1,4,1);
	g.addEdge(1,5,1);

	std::cout << "printing edges" << std::endl;
	g.printEdges(1);

	g.replace(5, 6);
	g.replace(4, 7);
	g.replace(3, 8);
	g.replace(2, 9);

	std::cout << "printing edges after replace" << std::endl;
	g.printEdges(1);
}

