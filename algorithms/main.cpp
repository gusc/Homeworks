//
//  main.cpp
//  PD
//
//  Created by Gusts Kaksis on 22/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

#include "Graph.h"
#include "MST.h"

int main(int argc, const char* argv[])
{
    if (argc > 1)
    {
        // Open the data file
        ifstream fs(argv[1], ios::binary);
        
        // Read a graph from file
        Graph g(fs);
        
        // Dump the Graph
        if (argc == 3 && strcmp(argv[2], "-dump") == 0)
        {
            g.DumpGraph();
        }
        
        // Run a MST (maximum spanning tree) algorithm on this graph
        MST mst(g);
        
        // Dump the MST
        if (argc == 3 && strcmp(argv[2], "-dump") == 0)
        {
            mst.DumpTree();
        }
        
        // Get the sum of all edges that were left over
        int64_t weight = mst.GetLeftoverWeight();
        printf("The total weight (the sum of difficulty levels) is: %lld\n", weight);
    }
    return 0;
}
