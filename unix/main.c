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
#include <stdbool.h>
#include <math.h>
#include <time.h>
#ifdef WITH_MPI
#   include <mpi.h>
#elif WITH_PTHREAD
#   define USE_PTHREAD 1
#   include <pthread.h>
#   include <unistd.h>
#endif
#include "md5.h"

#ifdef OPEN_MPI
#   define TAG_DONE 0
#endif

#ifdef USE_PTHREAD
#   define NUM_THREADS 4
#endif

// Reference hash to find
const uint8_t hash_ref[16] = {0x66,0xd9,0x97,0x89,0x35,0x15,0x0b,0x34,0xb9,0xdc,0x07,0x41,0xbc,0x64,0x2b,0xe2};
// Alphabet consists only of A-Za-z characters + \0 byte
const uint64_t alphabet_size = 51;
// Whether we've found a match
bool found = false;

// Decode 64-bit integer to 5 char string
int decode(uint64_t idx, char* str)
{
    memset(str, 0, 6);
    int j = 0;
    for (; j < 6; j++)
    {
        unsigned char c = idx % alphabet_size;
        idx /= alphabet_size;
        if (c == 0)
        {
            break;
        }
        else
        {
            c += 64; // @
            if (c > 90) // Z
            {
                c += 7; // a - Z
            }
        }
        str[j] = c;
    }
    return j;
}

// Process the word and compare it to the reference hash
int process(uint64_t idx)
{
    char str[6];
    uint8_t hash[16];

    // Decode next string
    decode(idx, str);

    //printf("%d: %llu, %s\n", rank, idx, str);

    // Get md5 hash
    if (strlen(str))
    {
        md5(hash, str, strlen(str));
        // Test the hash against the reference
        if (memcmp(hash, hash_ref, 16) == 0)
        {
            return idx;
        }
    }

    return 0;
}

// Process a single job
int run_job(int rank, int size)
{
#ifdef OPEN_MPI
    MPI_Status status;
#endif
    uint64_t idx_hit = 0;

    // 6 ASCII letters A-Za-z + 0-char can be compressed into a 64-bit integer
    uint64_t max = pow(alphabet_size, 6);

    // Word iterator loop
    for (uint64_t idx = rank; idx < max && idx_hit == 0 && !found; idx += size)
    {
        // Process the word
        idx_hit = process(idx);

#ifdef OPEN_MPI
        // Inform master about the succesful find
        if (rank != 0 && idx_hit > 0)
        {
            MPI_Send(&idx_hit, 1, MPI_LONG_LONG, 0, TAG_DONE, MPI_COMM_WORLD);
        }

        // Gather info from other nodes
        int flag = 0;
        MPI_Iprobe(MPI_ANY_SOURCE, TAG_DONE, MPI_COMM_WORLD, &flag, &status);
        if (flag)
        {
            uint64_t idx_response = 0;
            MPI_Recv(&idx_response, 1, MPI_LONG_LONG, status.MPI_SOURCE, TAG_DONE, MPI_COMM_WORLD, &status);
            if (rank == 0)
            {
                // Master node gathers a correct answer from other nodes
                idx_hit = idx_response;
            }
            // Anyway we're done
            found = true;
        }
#endif

    }

    return idx_hit;
}

#ifdef USE_PTHREAD
uint64_t idx_hit_global = 0;
// The thread - runs the job in the thread
void* run_thread(void* argument)
{
    int size = NUM_THREADS;
    int rank = *((int*)argument);
    uint64_t idx_hit = run_job(rank, size);
    if (idx_hit > 0)
    {
        // Signal other threads to stop
        found = true;
        idx_hit_global = idx_hit;
    }
    return NULL;
}
#endif

int main(int argc, char **argv)
{
    int rank = 0; // Node index
    int size = 1; // Total number of MPI nodes
    uint64_t nil = 0; // Reference nill
    uint64_t idx_hit = 0;

#ifdef OPEN_MPI
    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Status status;
    MPI_Request req;
    printf("Running MPI as %d of %d\n", rank, size);
    double start = MPI_Wtime();
#else
    #ifdef USE_PTHREAD
    printf("Running pthread\n");
    #else
    printf("Running single-threaded\n");
    #endif
    time_t start;
    start = clock();
#endif

#ifdef USE_PTHREAD
    // Create threads and launch them
    pthread_t threads[NUM_THREADS];
    int thread_args[NUM_THREADS];
    for (int i = 0; i < NUM_THREADS; i++)
    {
        thread_args[i] = i;
        if (!pthread_create(&threads[i], NULL, run_thread, &thread_args[i]))
        {
            printf("Spawning thread %d\n", i);
        }
    }
    // Wait for the threads to complete
    while (!found)
    {
        usleep(10);
    }
    // Read from the global variable
    idx_hit = idx_hit_global;
#else
    // Perform a single job directly
    idx_hit = run_job(rank, size);
#endif
    
    // Calculate the time taken
    float diff = 0.f;
#ifdef OPEN_MPI
    double end = MPI_Wtime();
    diff = (float)(end - start);
    // Kill all the child nodes
    if (rank == 0)
    {
        for (int j = 1; j < size; j ++)
        {
            MPI_Send(&nil, 1, MPI_LONG_LONG, j, TAG_DONE, MPI_COMM_WORLD);
        }
    }
    // Finalize MPI session
    MPI_Finalize();
#else
    time_t end;
    end = clock();
    diff = (float)(end - start) / 1000000.0;
#endif
    
    // Print the results
    if (rank == 0 && idx_hit > 0)
    {
        char str[6];
        memset(str, 0, 6);
        decode(idx_hit, str);
        printf("The answer is: '%s'\n", str);
        printf("Took %f seconds\n", diff);
    }
    
    return 0;
}
