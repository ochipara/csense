LOCAL_SRC_FILES =a_melbankm.c a_melcepst.c a_melcepst_data.c a_melcepst_emxAPI.c a_melcepst_emxutil.c a_melcepst_initialize.c a_melcepst_rtwutil.c a_melcepst_terminate.c a_rdct.c a_rfft.c abs.c fft.c floor.c frq2mel.c mel2frq.c rtGetInf.c rtGetNaN.c rt_nonfinite.c sqrt.c sum.c a_melcepst_wrap.c melcepst_wrap.c #test.c 
LCOAL_SRC_FILES2 = test.c
OBJECT_FILES	= $(LOCAL_SRC_FILES:%.c=%.o)       
OBJECT_FILES2   = $(LOCAL_SRC_FILES2:%.c=%.o)       
CFLAGS=-I/System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers/ -g

# all is the default rule
all	: liba_melcepst.dylib test

clean:
	mv test.java ..
	rm -f *.java *.class
	rm -f  *.o liba_melcepst.dylib test 
	mv ../test.java .
	
test: test.o
	gcc test.c -o test -la_melcepst -L.
	
java:
	swig -java melcepst.i
	javac *.java
	
# remove the old tapestry library and remake the new one
liba_melcepst.dylib:	$(OBJECT_FILES)
	rm -f $@
	#ld -bundle -flat_namespace -undefined suppress -o liba_melcepst.dylib $(OBJECT_FILES)
	gcc -shared -o liba_melcepst.dylib $(OBJECT_FILES)
