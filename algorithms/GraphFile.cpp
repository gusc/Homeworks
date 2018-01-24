//
//  GraphFile.cpp
//  PD
//
//  Created by Gusts Kaksis on 22/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

#include "GraphFile.h"

bool ReadNumber(ifstream& fs, int64_t& num)
{
    char number_buff[32] = {0}; // 32 bytes is enough to represent a 64-bit integer
    char c = 0; // Temporary character store
    char* n = number_buff;
    bool read_num = false;
    size_t l = 0;
    while (fs.get(c))
    {
        if (c >= '0' && c <= '9' && l < 21)
        {
            // We only accept valid
            *(n++) = c;
            // We're currently reading a number
            read_num = true;
            // Keep track of number length
            l ++;
        }
        else if (read_num)
        {
            // We were reading a number and now it has ended
            break;
        }
    }
    if (read_num)
    {
        // There was a number, so we convert it from string to int and return
        num = atoll(number_buff);
        return true;
    }
    // No numbers were found in the stream
    return false;
}

bool ReadEdgeFromStream(ifstream& fs, Edge& e)
{
    return ReadNumber(fs, e.nodeA) &&
           ReadNumber(fs, e.nodeB) &&
           ReadNumber(fs, e.weight);
}
