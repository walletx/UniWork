// test for printNodes()

#include "Graph.h"

int main() {
	
	// make a graph
	gdwg::Graph<unsigned int,int> g;
	
	g.addNode(1);
	g.addNode(2);
	g.addNode(3);
	g.addNode(4);

	g.addEdge(1,2,12);
	g.addEdge(1,3,13);
	g.addEdge(1,4,14);
	g.addEdge(2,1,21);
	g.addEdge(2,3,23);
	g.addEdge(3,1,31);
	g.addEdge(3,4,34);

	std::cout << "before move (g)" << std::endl;

	for (g.begin(); !g.end(); g.next()){
	   std::cout << g.value() << std::endl;
	}

    const auto &cg = std::move(g);

    std::cout << "after move (g)" << std::endl;

	for (g.begin(); !g.end(); g.next()){
	   std::cout << g.value() << std::endl;
	}

	std::cout << "after move (cg)" << std::endl;

	for (cg.begin(); !cg.end(); cg.next()){
	   std::cout << cg.value() << std::endl;
	}

	std::cout << "delete g Node 1 and 2" << std::endl;

    g.deleteNode(1);
    g.deleteNode(2);

    std::cout << "after delete (g)" << std::endl;

	for (g.begin(); !g.end(); g.next()){
	   std::cout << g.value() << std::endl;
	}

	std::cout << "after delete (cg)" << std::endl;

	for (cg.begin(); !cg.end(); cg.next()){
	   std::cout << cg.value() << std::endl;
	}

	std::cout << "after delete cg Node 1 and 2" << std::endl;
}

