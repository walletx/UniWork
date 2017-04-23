// test for printNodes()

#include "../Graph.h"

int main() {
	
	// make a graph
	gdwg::Graph<unsigned int,int> g;
	
	g.addNode(1);
	g.addNode(2);
	g.addNode(3);
	g.addNode(4);

	for (g.begin(); !g.end(); g.next())
	   std::cout << g.value() << std::endl;

    const auto cg = g;
    g.replace(1, 8);
    g.replace(2, 7);
    g.replace(3, 6);
    g.replace(4, 5);
    std::cout << "original after replacement" << std::endl;
    for (g.begin(); !g.end(); g.next())
	   std::cout << g.value() << std::endl;

	g.deleteNode(8);
    g.deleteNode(6);
    std::cout << "deleted nodes from original" << std::endl;
    for (g.begin(); !g.end(); g.next())
	   std::cout << g.value() << std::endl;

	std::cout << "call mergeReplace() on original" << std::endl;
	g.mergeReplace(7, 5);

	for (g.begin(); !g.end(); g.next())
	   std::cout << g.value() << std::endl;

	std::cout << "copy of original" << std::endl;
	for (cg.begin(); !cg.end(); cg.next())
	   std::cout << cg.value() << std::endl;
}

