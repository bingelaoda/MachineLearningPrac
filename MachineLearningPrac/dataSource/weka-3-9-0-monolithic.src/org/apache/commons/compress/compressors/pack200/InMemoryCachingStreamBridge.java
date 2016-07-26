/*  1:   */ package org.apache.commons.compress.compressors.pack200;
/*  2:   */ 
/*  3:   */ import java.io.ByteArrayInputStream;
/*  4:   */ import java.io.ByteArrayOutputStream;
/*  5:   */ import java.io.IOException;
/*  6:   */ import java.io.InputStream;
/*  7:   */ 
/*  8:   */ class InMemoryCachingStreamBridge
/*  9:   */   extends StreamBridge
/* 10:   */ {
/* 11:   */   InMemoryCachingStreamBridge()
/* 12:   */   {
/* 13:34 */     super(new ByteArrayOutputStream());
/* 14:   */   }
/* 15:   */   
/* 16:   */   InputStream getInputView()
/* 17:   */     throws IOException
/* 18:   */   {
/* 19:39 */     return new ByteArrayInputStream(((ByteArrayOutputStream)this.out).toByteArray());
/* 20:   */   }
/* 21:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.pack200.InMemoryCachingStreamBridge
 * JD-Core Version:    0.7.0.1
 */