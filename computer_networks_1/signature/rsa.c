//
//  rsa.c
//  MD3
//
//  Created by Gusts Kaksis on 13/01/2018.
//  Copyright © 2018 Gusts Kaksis. All rights reserved.
//

#include "rsa.h"
#include <math.h>
#include <stdbool.h>
#include <stdlib.h>
#include <time.h>
#include <gmp.h>

static uint64_t gcd(uint64_t a, uint64_t b)
{
    uint64_t temp;
    while (1)
    {
        temp = a % b;
        if (temp == 0)
        {
            return b;
        }
        a = b;
        b = temp;
    }
}

static uint64_t exponent(uint64_t z)
{
    int e = rand() % 256;
    while (1)
    {
        if (gcd(e, z) == 1)
        {
            return e;
        }
        e = (e + 1) % 256;
        if (e <= 2)
        {
            e = 3;
        }
    }
}

static bool is_prime(uint64_t num){
    for (int i = 2; i * i <= num; i++)
    {
        if (num % i == 0)
        {
            return false;
        }
    }
    return true;
}
static uint64_t rnd_prime()
{
    uint64_t prime = rand() % 256;
    while (!is_prime(prime) && prime <= 16)
    {
        prime = rand() % 256;
    }
    return prime;
}

void rsa_init()
{
    srand((unsigned int)time(NULL));
}

void rsa_keygen(rsa_keypair_t* keypair_out)
{
    uint64_t p = rnd_prime();
    uint64_t q = rnd_prime();
    uint64_t n = p * q;
    uint64_t z = (p - 1) * (q - 1);
    uint64_t e = exponent(z);
    
    mpz_t dm, em, zm;
    mpz_inits(dm, em, zm, NULL);
    mpz_set_ui(em, e);
    mpz_set_ui(zm, z);
    mpz_invert(dm, em, zm);
    uint64_t d = mpz_get_ui(dm);
    mpz_clears(dm, em, zm, NULL);
    
    keypair_out->pub.exp = e;
    keypair_out->pub.mod = n;
    keypair_out->priv.exp = d;
    keypair_out->priv.mod = n;
}

void rsa_encrypt(uint64_t* cyphertext, const uint8_t* message, size_t len, rsa_key_t* key)
{
    mpz_t m, c, e, n;
    mpz_inits(m, c, e, n, NULL);
    
    // Import message buffer into big-int buffer
    mpz_set_ui(e, key->exp);
    mpz_set_ui(n, key->mod);
    
    const uint8_t* mb = message;
    uint64_t* cb = cyphertext;
    while (len --)
    {
        mpz_set_ui(m, *(mb++));
        // m ^ exp % mod
        mpz_powm(c, m, e, n);
        *(cb++) = mpz_get_ui(c);
    }
    
    mpz_clears(m, c, e, n, NULL);
}

void rsa_decrypt(uint8_t* message, const uint64_t* cyphertext, size_t len, rsa_key_t* key)
{
    mpz_t m, c, e, n;
    mpz_inits(m, c, e, n, NULL);
    
    mpz_set_ui(e, key->exp);
    mpz_set_ui(n, key->mod);
    
    uint8_t* mb = message;
    const uint64_t* cb = cyphertext;
    while (len --)
    {
        mpz_set_ui(c, *(cb++));
        // c ^ exp % mod
        mpz_powm(m, c, e, n);
        *(mb++) = (uint8_t)mpz_get_ui(m);
    }
    
    mpz_clears(m, c, e, n, NULL);
}
