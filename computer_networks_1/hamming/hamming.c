//
//  main.c
//  Hamming code
//
//  Created by Gusts Kaksis on 27/11/2017.
//  Copyright © Gusts Kaksis. All rights reserved.
//

#include <stdio.h>
#include <stdbool.h>
#include <stdint.h>

void printbin(uint8_t val)
{
    for (int i = 7; i >= 0; i --)
    {
        uint8_t bit = (val >> i) & 1;
        printf("%d", bit);
    }
}

// Standard Hamming 7-4 encoding using bit matrix multiplication
uint8_t hamming7_4_encode(uint8_t input_nib)
{
    // Generator matrix
    static uint8_t gmatrix[4] = {
        0b0000111,
        0b0011001,
        0b0101010,
        0b1001011
    };
    
    uint8_t output = 0;
    uint8_t input = input_nib & 0xF; // Only lowest 4 bits
    
    // Do bit-matrix multiplication
    for (int obit = 0; obit < 7; obit++)
    {
        uint8_t im = 0;
        for (int ibit = 0; ibit < 4; ibit++)
        {
            // Modulo 2
            // multiplication = AND
            // addition       = XOR
            // im = im + (input(ibit) x gmatrix(ibit))
            // a.k.a.
            // im = im ^ (input(ibit) & gmatrix(ibit))
            im ^= (((input >> ibit) & (gmatrix[ibit] >> obit)) & 1);
        }
        // output(obit) = im
        output |= (im << obit);
    }
    
    return output;
}

// Encode whole 8-bit byte (each 4-bit nibble separately) and concatenate into a 16-bit word
uint16_t hamming8bit_encode(uint8_t input)
{
    uint16_t output = hamming7_4_encode(input) & 0xFF;
    output |= (hamming7_4_encode(input >> 4) & 0xFF) << 8;
    return output;
}

// Calculate codeword syndrome (3-bit)
uint8_t hamming7_4_syndrome(uint8_t input)
{
    // Check mask
    static uint8_t hmatrix[3] = {
        0b1010101,
        0b1100110,
        0b1111000
    };
    
    uint8_t output = 0;
    
    // Do bit-matrix multiplication
    for (int obit = 0; obit < 3; obit++)
    {
        uint8_t im = 0;
        for (int ibit = 0; ibit < 7; ibit++)
        {
            // Modulo 2
            // multiplication = AND
            // addition       = XOR
            // im = im + (input(ibit) x gmatrix(ibit))
            // a.k.a.
            // im = im ^ (input(ibit) & gmatrix(ibit))
            im ^= (((input >> ibit) & (hmatrix[obit] >> ibit)) & 1);
        }
        // output(obit) = im
        output |= (im << obit);
    }
    
    return output;
}

// Check if any bit is reported erroneous by the syndrome
bool hamming8bit_errcheck(uint8_t input)
{
    uint8_t syndrome = hamming7_4_syndrome(input);
    
    printf("Syndrome: 0b");
    printbin(syndrome);
    printf("\n");
    
    if (syndrome > 0)
    {
        return true;
    }
    
    return false;
}

// Do a bit correction
uint16_t hamming8bit_correct(uint16_t input)
{
    uint8_t syndrome = hamming7_4_syndrome(input);
    if (syndrome > 0)
    {
        printf("Correcting bit: %d\n", syndrome);
        return input ^ (1 << (syndrome - 1));
    }
    return input;
}

// Decode an 8-bit byte
uint8_t hamming8bit_decode(uint16_t input)
{
    uint8_t output = 0;
    
    // Brute force decode
    uint8_t in[] = {0, 0};
    in[0] = input & 0xFF;
    in[1] = (input >> 8) & 0xFF;
    
    for (int nib = 0; nib < 2; nib++)
    {
        uint8_t im = (in[nib] & 0b0100) >> 2;
        im |= (in[nib] & 0b1110000) >> 3;
        output |= (im << (4 * nib));
    }
    
    return output;
}

static uint8_t inputs[] = {
    0x00,
    0x01,
    0b00001110,
    0b10101010,
    0x0F,
    0xFF
};

static uint16_t corrupt[] = {
    0x0, // No corruption
    0x02,
    0b00000101,
    0b00001010,
    0b01010101,
    0xFF
};

int main(int argc, const char * argv[])
{    
    for (int i = 0; i < 6; i ++)
    {
        printf("===============================\n");
        printf("Input: 0x%x => 0b", inputs[i]);
        printbin(inputs[i]);
        printf("\n");
        
        uint16_t send_parts = hamming8bit_encode(inputs[i]);
        uint8_t send_words[] = {0, 0};
        send_words[0] = send_parts & 0xFF;
        send_words[1] = (send_parts >> 8) & 0xFF;
        
        printf("Sending: 0x%x => 0b", send_parts);
        printbin(send_words[1]);
        printbin(send_words[0]);
        printf("\n\n");
        
        for (int j = 0; j < 6; j ++)
        {
            uint8_t output = 0;
            if (corrupt[j] != 0)
            {
                printf("Corruption mask => 0b");
                printbin(corrupt[j]);
                printf("\n");
            }
            else
            {
                printf("Without corruption\n");
            }
            printf("\n");
            
            for (int b = 0; b < 2; b++)
            {
                printf("Byte %d\n", b + 1);
                
                uint8_t send = send_words[b];
                printf("Send: 0x%x => 0b", send);
                printbin(send);
                printf("\n");
                
                uint8_t recieve = send ^ corrupt[j];
                
                printf("Received: 0x%x => 0b", recieve);
                printbin(recieve);
                printf("\n");
                
                if (hamming8bit_errcheck(recieve))
                {
                    recieve = hamming8bit_correct(recieve);
                    printf("Result: 0x%x => 0b", recieve);
                    printbin(recieve);
                    printf("\n");
                }
                else
                {
                    printf("No errors found\n");
                }
                
                output |= (hamming8bit_decode(recieve) << (4 * b));
            }

            printf("Output: 0x%x => 0b", output);
            printbin(output);
            printf("\n");

            if (output == inputs[i])
            {
                printf("OK - output is the same as input\n");
            }
            else
            {
                printf("FAIL - input/output mismatch\n");
            }
            printf("\n");
        }
        
        printf("\n");
    }
    return 0;
}

