#include <android/log.h>  
#include "a_melcepst.h"
#include "a_melcepst_emxutil.h"
#define APP "Melcepst"  

void a_melcepst_wrap(const real_T s[512], int32_T offset1, 
                        real_T fs, int32_T nc, emxArray_real_T *c, 
                        real_T r[32], int32_T offset2) {
    int i = 0;
    s = &s[offset1];
    r = &r[offset2];
    //__android_log_print(ANDROID_LOG_DEBUG, APP, "1st, c->size: %dx%d, c->allocatedSize: %d, c->data: %p\n", c->size[0], c->size[1], c->allocatedSize, c->data);
    if(c->data != r) {
        if(c->allocatedSize == 0) free(c->data);
        c->size[0] = 1;
        c->size[1] = 32;
        c->data = r;
        c->allocatedSize = 64;
        c->canFreeData = FALSE;
    }
    //__android_log_print(ANDROID_LOG_DEBUG, APP, "2nd, c->size: %dx%d, c->allocatedSize: %d, c->data: %p\n", c->size[0], c->size[1], c->allocatedSize, c->data);
    //for(i = 0; i < 33; i++) {
    //    printf("r[%d]: %f, c->data[%d] %f\n", i, r[i], i, c->data[i]);
    //}
    a_melcepst(s, fs, nc, c);
    //__android_log_print(ANDROID_LOG_DEBUG, APP, "3rd, c->size: %dx%d, c->allocatedSize: %d, c->data: %p\n", c->size[0], c->size[1], c->allocatedSize, c->data);
    //for(i = 0; i < 33; i++) {
    //    printf("r[%d]: %f, c->data[%d] %f\n", i, r[i], i, c->data[i]);
    //}
}
