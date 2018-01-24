MACOS_SDK_PATH = $(shell xcrun --sdk macosx --show-sdk-path)

CXXFLAGS = -isysroot $(MACOS_SDK_PATH) -std=gnu++14 -stdlib=libc++
LDFLAGS = -isysroot $(MACOS_SDK_PATH) -framework Foundation

OBJS = main.o MST.o Graph.o GraphFile.o

all: PD

PD: $(OBJS)
	$(CXX) $(OBJS) $(LDFLAGS) -o PD

%.o: %.cpp
	$(CXX) $(CXXFLAGS) -O2 -c -o $@ $<

clean:
	rm -f *.o PD
