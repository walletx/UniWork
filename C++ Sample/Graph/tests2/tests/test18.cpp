// Print nodes after each edge addition
// Should reorder as outdegree changes

#include "../Graph.h"

int main() {
	
	// make a graph
	gdwg::Graph<unsigned int,int> g;
	
	g.addNode(1);
	g.addNode(2);
	g.addNode(3);
	g.addNode(4);

	// Equal number of outgoing so sorted on node data
	g.addEdge(1,4,1);
	g.addEdge(1,4,2);
	g.addEdge(1,4,3);
	g.addEdge(1,4,4);

	g.addEdge(2,1,1);
	g.addEdge(2,4,2);
	g.addEdge(2,4,3);

	g.addEdge(3,1,1);
	g.addEdge(3,2,2);
	
	std::cout << "printing initial order" << std::endl;
	g.printNodes();

	g.deleteNode(4);
	
	std::cout << "order after node deletion" << std::endl;
	g.printNodes();

}

