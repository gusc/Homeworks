//
//  Graph.cpp
//  PD
//
//  Created by Gusts Kaksis on 22/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

#include "Graph.h"
#include "GraphFile.h"
#include <fstream>

Graph::Graph(ifstream& fs) :
    numNodes(0),
    numEdges(0)
{
    // Read node count
    numNodes = 0;
    if (!ReadNumber(fs, numNodes))
    {
        printf("Can't read the number of nodes in the data file");
        exit(1);
    }
    
    // Read edges into an adjacency matrix
    Edge e;
    numEdges = 0;
    while (ReadEdgeFromStream(fs, e))
    {
        // We use zero-based indexing
        e.nodeA --;
        e.nodeB --;
        edges.push_back(e);
        numEdges ++;
    }
}

void Graph::DumpGraph()
{
    printf("Edges (total: %llu)\n", numEdges);
    for (size_t i = 0; i < edges.size(); i++)
    {
        printf("%llu - (%llu) - %llu\n", edges[i].nodeA, edges[i].weight, edges[i].nodeB);
    }
    printf("\n");
}
