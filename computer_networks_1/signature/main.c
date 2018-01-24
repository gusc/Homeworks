//
//  main.c
//  MD3
//
//  Created by Gusts Kaksis on 09/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <math.h>

#include "md5.h"
#include "rsa.h"

int main(int argc, char **argv)
{
    uint8_t hash[16];
    uint64_t signature[16];
    
    rsa_init();
    
    if (argc == 1)
    {
        printf("Commands:\n"
               "signature key-gen\n  generate a new 64-bit RSA key pair.\n"
               "  This writes two files key.priv, key.pub to working directory\n"
               "signature sign file key.priv\n  signs a message file with the private key and writes the signature to signature.dat file\n"
               "signature verify file key.pub signature.dat\n  verifies a message file signature with a public key\n");
        return 1;
    }
    
    if (argc >= 2)
    {
        if (strcmp(argv[1], "key-gen") == 0)
        {
            rsa_keypair_t pair;
            rsa_keygen(&pair);
            
            printf("Private key:\n  exponent: 0x%llx\n    modulo: 0x%llx\n", pair.priv.exp, pair.priv.mod);
            printf("Public key:\n  exponent: 0x%llx\n    modulo: 0x%llx\n", pair.pub.exp, pair.pub.mod);
            
            FILE* fh = fopen("key.priv", "w");
            fwrite(&pair.priv, sizeof(rsa_key_t), 1, fh);
            fclose(fh);
            
            fh = fopen("key.pub", "w");
            fwrite(&pair.pub, sizeof(rsa_key_t), 1, fh);
            fclose(fh);
        }
        else if (strcmp(argv[1], "sign") == 0)
        {
            if (argc < 4)
            {
                printf("Insufficient number of arguments");
                return 1;
            }
            
            // Read the file contents
            FILE* fh = fopen(argv[2], "r");
            fseek(fh, 0, SEEK_END);
            long fsize = ftell(fh);
            fseek(fh, 0, SEEK_SET);
            char* msg = malloc(fsize);
            fread(msg, sizeof(char), fsize, fh);
            fclose(fh);
            
            // Get the MD5 hash of the message
            md5(hash, msg, fsize);
            
            free(msg);
            
            // Read the private key file
            fh = fopen(argv[3], "r");
            rsa_key_t key;
            fread(&key, sizeof(rsa_key_t), 1, fh);
            fclose(fh);
            
            // Sign the hash
            rsa_encrypt(signature, hash, 16, &key);
            
            // Write the signature to data file
            fh = fopen("signature.dat", "w");
            fwrite(signature, sizeof(uint64_t), 16, fh);
            fclose(fh);
        }
        else if (strcmp(argv[1], "verify") == 0)
        {
            if (argc < 5)
            {
                printf("Insufficient number of arguments");
                return 1;
            }
            
            // Read the file contents
            FILE* fh = fopen(argv[2], "r");
            fseek(fh, 0, SEEK_END);
            long fsize = ftell(fh);
            fseek(fh, 0, SEEK_SET);
            char* msg = malloc(fsize);
            fread(msg, sizeof(char), fsize, fh);
            fclose(fh);
            
            // Get the MD5 hash of the message
            md5(hash, msg, fsize);
            
            free(msg);
            
            // Read the public key file
            fh = fopen(argv[3], "r");
            rsa_key_t key;
            fread(&key, sizeof(rsa_key_t), 1, fh);
            fclose(fh);
            
            // Read the signature file
            fh = fopen(argv[4], "r");
            fseek(fh, 0, SEEK_END);
            fsize = ftell(fh);
            fseek(fh, 0, SEEK_SET);
            if (fsize / sizeof(uint64_t) != 16)
            {
                printf("Something is wrong, the signature should be exactly 128 bytes long");
                return 1;
            }
            fread(signature, sizeof(uint64_t), 16, fh);
            fclose(fh);
            
            // Decrypt the signature
            uint8_t hash2[16];
            rsa_decrypt(hash2, signature, 16, &key);
            
            // Compare the hashes
            if (memcmp(hash2, hash, 16) == 0)
            {
                printf("Signature is valid.\n");
            }
            else
            {
                printf("WARNING! Signatures do not match!\n");
            }
        }
    }
    
    return 0;
}
