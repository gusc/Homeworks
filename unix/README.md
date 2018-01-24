Message Passing Interface
=========================

Implementation of brute-force password cracker using Open-MPI interface.

Requirements
------------

Open-MPI, which can be installed using Homebrew:

```
brew install open-mpi
```

Xcode 8+ or at least Xcode Command Line Tools

Build
-----

MPI version

```
make clean && make mpi-md
```

PThreads version

```
make clean && make pt-md
```

Single threaded version

```
make clean && make local-md
```

Run
---

PThreads and single threaded versions are pretty straight forward:

```
local-md
```

Open-MPI version is run using "mpirun":

```
mpirun -n 4 mpi-pd
```
