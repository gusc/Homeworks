MPI_COMPILE_FLAGS = $(shell mpicc --showme:compile)
MPI_LINK_FLAGS = $(shell mpicc --showme:link)
MACOS_SDK_PATH = $(shell xcrun --sdk macosx --show-sdk-path)

CCFLAGS = -isysroot $(MACOS_SDK_PATH)
LDFLAGS = -isysroot $(MACOS_SDK_PATH) -framework Foundation

LC_OBJS = md5.lc.o main.lc.o
MPI_OBJS = md5.mpi.o main.mpi.o
PT_OBJS = md5.pt.o main.pt.o

all: mpi-md

mpi-md: $(MPI_OBJS)
	$(CC) $(MPI_OBJS) $(MPI_LINK_FLAGS) -o mpi-md

%.mpi.o: %.c
	$(CC) $(MPI_COMPILE_FLAGS) -O2 -DWITH_MPI=1 -c -o $@ $<

pt-md: $(PT_OBJS)
	$(CC) $(PT_OBJS) $(LDFLAGS) -o pt-md

%.pt.o: %.c
	$(CC) $(CCFLAGS) -O2 -DWITH_PTHREAD=1 -c -o $@ $<

local-md: $(LC_OBJS)
	$(CC) $(LC_OBJS) $(LDFLAGS) -o local-md

%.lc.o: %.c
	$(CC) $(CCFLAGS) -O2 -c -o $@ $<

clean:
	rm -f *.o local-md mpi-md pt-md
