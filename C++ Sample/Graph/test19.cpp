
#include "Graph.h"

int main() {
	
	// create 3 graphs
	gdwg::Graph<std::string,int> g;

	g.addNode("a");
	g.addNode("b");
	g.addNode("c");

	g.addEdge("a", "b", 10);
	g.addEdge("a", "c", 10);

	g.printNodes();

	g.printEdges("a");

	//for (g.begin(); !g.end(); g.next()){
	//	std::cout << g.value() << std::endl;
	//}

}