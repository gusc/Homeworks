//
//  rsa.h
//  MD3
//
//  Created by Gusts Kaksis on 13/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

#ifndef rsa_h
#define rsa_h

#include <stdio.h>
#include <stdint.h>

typedef struct {
    uint64_t exp;
    uint64_t mod;
} rsa_key_t;

typedef struct {
    rsa_key_t priv;
    rsa_key_t pub;
} rsa_keypair_t;

/**
 * Initialize RSA keygen engine
 */
void rsa_init();

/**
 * Generate a new RSA key pair from two primes p and q
 * @param keypair_out - pointer to a key-pair structure
 */
void rsa_keygen(rsa_keypair_t* keypair_out);

/**
 * Encrypt a byte bufer using an RSA key
 * @param cyphertext - the output buffer pointer
 * @param message - message to encrypt
 * @param len - the length of the message (also the number of element in output)
 * @param key - RSA key
 */
void rsa_encrypt(uint64_t* cyphertext, const uint8_t* message, size_t len, rsa_key_t* key);

/**
 * Decrypt a 64-bit bufer using an RSA key
 * @param message - the output buffer pointer
 * @param cyphertext - the encrypted cyphertext to decrypt
 * @param len - the length of the message (also the number of element in output)
 * @param key - RSA key
 */
void rsa_decrypt(uint8_t* message, const uint64_t* cyphertext, size_t len, rsa_key_t* key);

#endif /* rsa_h */
