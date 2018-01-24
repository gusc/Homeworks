//
//  Graph.h
//  PD
//
//  Created by Gusts Kaksis on 22/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

#ifndef Graph_h
#define Graph_h

#include <cstdint>
#include <vector>
#include <fstream>

using namespace std;

/// Representation of an edge in the data file
struct Edge
{
    int64_t nodeA;
    int64_t nodeB;
    int64_t weight;
};

/// Representation of a graph
class Graph
{
public:
    /// Create a graph from a file stream
    Graph(ifstream& fs);
    
    /// Debug the tree
    void DumpGraph();
    
    int64_t numNodes;
    int64_t numEdges;
    vector<Edge> edges;
};

#endif /* Graph_h */
