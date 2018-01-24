//
//  md5.h
//  Source: https://gist.github.com/creationix/4710780
//
//  Created by Gusts Kaksis on 09/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

#ifndef md5_h
#define md5_h

#include <stdint.h>
#include <stdlib.h>

/**
 * Calculate MD5 hash of a string
 * @param [out] hash - output hash buffer, must be 16 bytes long
 * @param [in] msg - the message to hash
 * @param len - the message length
 */
void md5(uint8_t* hash, const char* initial_msg, size_t initial_len);

#endif /* md5_h */
