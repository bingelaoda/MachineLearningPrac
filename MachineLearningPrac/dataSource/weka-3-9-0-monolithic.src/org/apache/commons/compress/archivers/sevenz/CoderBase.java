/*  1:   */ package org.apache.commons.compress.archivers.sevenz;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ import java.io.InputStream;
/*  5:   */ import java.io.OutputStream;
/*  6:   */ 
/*  7:   */ abstract class CoderBase
/*  8:   */ {
/*  9:   */   private final Class<?>[] acceptableOptions;
/* 10:29 */   private static final byte[] NONE = new byte[0];
/* 11:   */   
/* 12:   */   protected CoderBase(Class<?>... acceptableOptions)
/* 13:   */   {
/* 14:35 */     this.acceptableOptions = acceptableOptions;
/* 15:   */   }
/* 16:   */   
/* 17:   */   boolean canAcceptOptions(Object opts)
/* 18:   */   {
/* 19:42 */     for (Class<?> c : this.acceptableOptions) {
/* 20:43 */       if (c.isInstance(opts)) {
/* 21:44 */         return true;
/* 22:   */       }
/* 23:   */     }
/* 24:47 */     return false;
/* 25:   */   }
/* 26:   */   
/* 27:   */   byte[] getOptionsAsProperties(Object options)
/* 28:   */   {
/* 29:54 */     return NONE;
/* 30:   */   }
/* 31:   */   
/* 32:   */   Object getOptionsFromCoder(Coder coder, InputStream in)
/* 33:   */   {
/* 34:61 */     return null;
/* 35:   */   }
/* 36:   */   
/* 37:   */   abstract InputStream decode(String paramString, InputStream paramInputStream, long paramLong, Coder paramCoder, byte[] paramArrayOfByte)
/* 38:   */     throws IOException;
/* 39:   */   
/* 40:   */   OutputStream encode(OutputStream out, Object options)
/* 41:   */     throws IOException
/* 42:   */   {
/* 43:75 */     throw new UnsupportedOperationException("method doesn't support writing");
/* 44:   */   }
/* 45:   */   
/* 46:   */   protected static int numberOptionOrDefault(Object options, int defaultValue)
/* 47:   */   {
/* 48:83 */     return (options instanceof Number) ? ((Number)options).intValue() : defaultValue;
/* 49:   */   }
/* 50:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.sevenz.CoderBase
 * JD-Core Version:    0.7.0.1
 */