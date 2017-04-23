//COMP6771 Assignment 3 - Pongpol Trisuwan (z3471079)

#ifndef _graph_h
#define _graph_h

#include <iostream>
#include <vector>
#include <list>
#include <string>
#include <memory>
#include <algorithm>
#include <stdexcept>

// Spec location at: https://www.cse.unsw.edu.au/~cs6771/16s2/assignments/gdwg.html

/* 1. Assuming the existence of == operator for N and E classes according to forum post (otherwise can also use !< && !< idiom) */

/* 2. Using std::shared_ptr<N> nodeVal and std::shared_ptr<E> edgeVal - would prefer to just do N nodeVal and E edgeVal
But out of paranoia I will follow the "You must use smart pointers to represent the nodes and edges in your implementation." to the letter */

namespace gdwg {
    
    // Graph class - N is the node type, E is the edge type
    template <typename N, typename E> class Graph {

        public:
            
            //Constructors and Assignment
            
             Graph();
             
             //Copy Constructor
             Graph(const Graph &graph);
             
             //Move Constructor
             Graph(Graph &&graph);
             
            //Copy Assignment
             Graph& operator= (const Graph &graph);
             
            //Move Assignment
             Graph& operator= (Graph &&graph);
            //~Graph();
            
            //Member functions
            
             bool addNode(const N& val);
             bool addEdge(const N& src, const N& dst, const E& w);
             bool replace(const N& oldData, const N& newData);
             
             // Merges node with oldData and node with newData
             // Any edges from other nodes which pointed to the old node now points to the new node
             void mergeReplace(const N& oldData, const N& newData);
             
             //Deletes a node as well as prune edges in other nodes
             void deleteNode(const N&) noexcept;
             
             void deleteEdge(const N& src, const N& dst, const E& w) noexcept;
             
             // Clears the graph and leaves it empty
             void clear() noexcept;
             
             bool isNode(const N& val) const;
             bool isConnected(const N& src, const N& dst) const;
             void printNodes() const;
             void printEdges(const N& val) const;            
            
            //Iterator functions
            
             void begin() const;
             bool end() const;
             void next() const;
             const N& value() const;
                        
        private:
            
            // All members in Node and Edge are public
            // However since they are private classes they can only be accessed by Graph
            class Node;
            class Edge;
            
            std::vector<std::shared_ptr<Node>> nodeList;
            
            // Iterator object for the 'iterator' implementation
            typedef typename std::vector<std::shared_ptr<Node>>::const_iterator cIter;
            mutable cIter iter;
            
    };
    
    // Nested private class Node
    template <typename N, typename E> 
    class Graph<N, E>::Node {
        
        public:
            
            Node();
            Node(const N &label);
            
            // Print operator overload prints out node value
            friend std::ostream& operator<< (std::ostream &os, const Node &node) {
                os << *(node.nodeVal);
                return os;
            };
            
            friend bool operator< (const Node &node1, const Node &node2) {
                
                if (node1.edgeList.size() < node2.edgeList.size()) {
                    return true;
                } else {
                    if (node1.edgeList.size() == node2.edgeList.size() && *(node1.nodeVal) < *(node2.nodeVal)) {
                        return true;
                    }
                }
                
                return false;
            };
            
            std::shared_ptr<N> nodeVal;
            std::vector<std::shared_ptr<Edge>> edgeList;
            
    };
    
    // Nested private class Edge
    template <typename N, typename E> 
    class Graph<N, E>::Edge {
        
        public:
            
            Edge();
            Edge(const E &weight, std::shared_ptr<Node> &dest);
            
            // Print operator overload prints out destination node value and edge weight
            friend std::ostream& operator<< (std::ostream &os, const Edge &edge) {
                auto dest = (edge.destNode).lock();
                os << *(dest->nodeVal) << " " << *(edge.edgeVal);
                return os;
            };
            
            friend bool operator< (const Edge &edge1, const Edge &edge2) {
                
                if (*(edge1.edgeVal) < *(edge2.edgeVal)) {
                    return true;
                } else {
                    if (*(edge1.edgeVal) == *(edge2.edgeVal)) {
                        
                        auto dest1 = (edge1.destNode).lock();
                        auto dest2 = (edge2.destNode).lock();
                        
                        if (*(dest1->nodeVal) < *(dest2->nodeVal)) {
                            return true;
                        }
                        
                    }
                }
                
                return false;
            };
            
            std::shared_ptr<E> edgeVal;
            std::weak_ptr<Node> destNode;
    
    };
    
    #include "Graph.tem"
    
}

#endif