LOCAL_SRC_FILES = EstimateNoiseW.c EstimateNoiseW_data.c EstimateNoiseW_emxutil.c EstimateNoiseW_initialize.c EstimateNoiseW_rtwutil.c EstimateNoiseW_terminate.c any.c power.c rdivide.c rtGetInf.c rtGetNaN.c rt_nonfinite.c sum.c EstimateNoise_wrap.c
LCOAL_SRC_FILES2 = test.c
OBJECT_FILES	= $(LOCAL_SRC_FILES:%.c=%.o)       
OBJECT_FILES2   = $(LOCAL_SRC_FILES2:%.c=%.o)       
CFLAGS=-I/System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers/ -g

# all is the default rule
all	: libcsense-native.dylib 

clean:
	./clean_swig.sh
	rm -f *.class
	rm -f  *.o libcsense-native.dylib test 
	
test: test.c 
	gcc test.c -o test -lcsense-native -L.
	
java:
	swig -java EstimateNoise.i
	javac *.java
	
# remove the old tapestry library and remake the new one
libcsense-native.dylib:	$(OBJECT_FILES)
	rm -f $@
	#ld -bundle -flat_namespace -undefined suppress -o liba_normalizec.dylib $(OBJECT_FILES)
	gcc -shared -o libcsense-native.dylib $(OBJECT_FILES)
