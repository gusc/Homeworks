//
//  MST.h
//  PD
//
//  Created by Gusts Kaksis on 23/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

#ifndef MST_h
#define MST_h

#include <cstdint>
#include <vector>
#include "Graph.h"

using namespace std;

class MST
{
private:
    // A private structure to represent a subset (up-tree)
    struct SubSet
    {
        int64_t parent;
        int64_t rank;
    };
    
    /// The graph
    Graph& graph;
    /// Resulting MST
    vector<Edge> mst;
    
    // Find a root of the set of nodes with node i in it
    int64_t FindRoot(MST::SubSet* subsets, int64_t i);
    // Unify two subsets (up-trees)
    void Union(SubSet* subsets, int64_t x, int64_t y);
    
public:
    /// Perform a maximum-spanning-tree
    MST(Graph& g);
    
    /// Calculate left-over weight of the graph edges
    /// (i.e. the total weight of the roads where Winnie the Pooh has to place his pot of honey)
    int64_t GetLeftoverWeight();
    
    /// Debug the tree
    void DumpTree();
};

#endif /* MST_h */
