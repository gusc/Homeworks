//
//  MST.cpp
//  PD
//
//  Created by Gusts Kaksis on 23/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

#include "MST.h"
#include <algorithm>

int64_t MST::FindRoot(MST::SubSet* subsets, int64_t i)
{
    if (subsets[i].parent != i)
    {
        // Perform a path compression
        subsets[i].parent = FindRoot(subsets, subsets[i].parent);
    }
    return subsets[i].parent;
}

void MST::Union(SubSet* subsets, int64_t x, int64_t y)
{
    int64_t xroot = FindRoot(subsets, x);
    int64_t yroot = FindRoot(subsets, y);
    if (subsets[xroot].rank < subsets[yroot].rank)
    {
        // X is smaller than Y, put X under Y
        subsets[xroot].parent = yroot;
    }
    else if (subsets[xroot].rank > subsets[yroot].rank)
    {
        // X is larger than Y, put Y under X
        subsets[yroot].parent = xroot;
    }
    else
    {
        // Put Y under X and increase X's rank by one
        subsets[yroot].parent = xroot;
        subsets[xroot].rank++;
    }
}

MST::MST(Graph& g) :
    graph(g)
{
    // Sort all edges in descending order - we need to start with the heaviest ones
    sort(graph.edges.begin(), graph.edges.end(), [](Edge a, Edge b) {
        return a.weight > b.weight;
    });
    
    // Create subset for each node
    SubSet* subsets = new SubSet[graph.numNodes];
    for (int64_t i = 0; i < graph.numNodes; i++)
    {
        subsets[i].parent = i;
        subsets[i].rank = 0;
    }
    
    // Iterate the sorted edges
    for (int64_t j = 0, i = 0; j < graph.numNodes - 1;)
    {
        Edge next_edge = graph.edges[i];
        int64_t x = FindRoot(subsets, next_edge.nodeA);
        int64_t y = FindRoot(subsets, next_edge.nodeB);
        
        if (x != y)
        {
            // Include the edge only if it does not cause cycle
            mst.push_back(next_edge);
            Union(subsets, x, y);
            j++;
            graph.edges[i].weight = 0;
        }
        i++;
    }
    
    delete[] subsets;
}

int64_t MST::GetLeftoverWeight()
{
    int64_t sum = 0;
    for (int64_t i = 0; i < graph.numEdges; i++)
    {
        sum += graph.edges[i].weight;
    }
    return sum;
}

void MST::DumpTree()
{
    printf("MST:\n");
    for (size_t i = 1; i < mst.size(); i++)
    {
        printf("%lli - (%lli) - %lli\n", mst[i].nodeA, mst[i].weight, mst[i].nodeB);
    }
    printf("\n");
}
