MACOS_SDK_PATH = $(shell xcrun --sdk macosx --show-sdk-path)

CCFLAGS = -isysroot $(MACOS_SDK_PATH) -I/usr/local/include
LDFLAGS = -isysroot $(MACOS_SDK_PATH) -framework Foundation -L/usr/local/lib -lgmp

OBJS = md5.o rsa.o main.o

all: signature

signature: $(OBJS)
	$(CC) $(OBJS) $(LDFLAGS) -o signature

%.o: %.c
	$(CC) $(CCFLAGS) -O2 -c -o $@ $<

clean:
	rm -f *.o signature
