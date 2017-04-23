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
	
	std::cout << "printing nodes" << std::endl;
	g.printNodes();

	g.addEdge(1,2,1);
	g.addEdge(2,3,1);
	g.addEdge(3,1,1);
	
	std::cout << "printing nodes after adding edges" << std::endl;
	g.printNodes();

	g.addEdge(1,2,2);
	g.addEdge(2,1,2);
	
	std::cout << "printing nodes after adding more edges" << std::endl;
	g.printNodes();

	g.addEdge(1,2,3);

	std::cout << "printing nodes after last edge addition" << std::endl;
	g.printNodes();
}

