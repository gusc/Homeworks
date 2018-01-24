//
//  crc.c
//  CRC
//
//  Created by Gusts Kaksis on 30/12/2017.
//  Copyright © Gusts Kaksis. All rights reserved.
//

#include <stdio.h>
#include <stdint.h>

const uint32_t ieee802_generator = 0b10000010011000001000111011011011; // a.k.a. 0x82608EDB

static uint32_t reverse(uint32_t data, size_t bits)
{
    uint32_t rev_data = 0;
    for (size_t b = 0; b < bits; b++)
    {
        rev_data |= ((data & 1) << ((bits - 1) - b));
        data = (data >> 1);
    }
    return rev_data;
}

uint32_t crc32rev(const char* buff, size_t len)
{
    uint32_t remainder = 0xFFFFFFFF;
    uint32_t generator = reverse(ieee802_generator, 32);
    for (size_t byte = 0; byte < len; byte++)
    {
        remainder ^= (reverse(buff[byte], 8) << 24);
        for (size_t bit = 8; bit > 0; bit--)
        {
            if (remainder & 0x80000000)
            {
                remainder = (remainder << 1) ^ generator;
            }
            else
            {
                remainder = (remainder << 1);
            }
        }
    }
    return ~reverse(remainder, 32);
}

int main(int argc, const char * argv[])
{
    char text[] = "hello internet";
    
    uint32_t crc = crc32rev(text, sizeof(text));
    uint32_t crc_good = crc;
    printf("CRC32 of '%s': 0x%x\n\n", text, crc);
    
    printf("Flip first bit of 4th byte\n");
    text[3] = text[3] ^ 1;
    crc = crc32rev(text, sizeof(text));
    printf("CRC32: 0x%x\n", crc);
    if (crc == crc_good)
    {
        printf("OK\n\n");
    }
    else
    {
        printf("FAIL!\n\n");
    }
    text[3] = text[3] ^ 1; // Flip it back
    
    printf("Inverse first 4 bytes\n");
    for (size_t i = 0; i < 4; i ++)
    {
        text[i] = ~text[i];
    }
    crc = crc32rev(text, sizeof(text));
    printf("CRC32: 0x%x\n", crc);
    if (crc == crc_good)
    {
        printf("OK\n\n");
    }
    else
    {
        printf("FAIL!\n\n");
    }
    
    printf("Inverse the rest of the bytes\n");
    for (size_t i = 4; i < sizeof(text); i ++)
    {
        text[i] = ~text[i];
    }
    crc = crc32rev(text, sizeof(text));
    printf("CRC32: 0x%x\n", crc);
    
    if (crc == ~crc_good)
    {
        printf("Inverse match\n");
    }
    else if (crc == crc_good)
    {
        printf("OK\n\n");
    }
    else
    {
        printf("FAIL!\n\n");
    }
    
    return 0;
}
