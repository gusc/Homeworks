//
//  GrpahFile.h
//  PD
//
//  Created by Gusts Kaksis on 22/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

#ifndef GrpahFile_h
#define GrpahFile_h

#include <fstream>
#include "Graph.h"

using namespace std;

/// Read a single number from the data file
bool ReadNumber(ifstream& fs, int64_t& num);

/// Read an edge representation (a tripplet of integers) from the data file
bool ReadEdgeFromStream(ifstream& fs, Edge& e);

#endif /* GrpahFile_h */
