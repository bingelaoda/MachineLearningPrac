/*   1:    */ package org.apache.commons.compress.utils;
/*   2:    */ 
/*   3:    */ import java.io.ByteArrayOutputStream;
/*   4:    */ import java.io.Closeable;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.InputStream;
/*   7:    */ import java.io.OutputStream;
/*   8:    */ 
/*   9:    */ public final class IOUtils
/*  10:    */ {
/*  11:    */   private static final int COPY_BUF_SIZE = 8024;
/*  12:    */   private static final int SKIP_BUF_SIZE = 4096;
/*  13: 38 */   private static final byte[] SKIP_BUF = new byte[4096];
/*  14:    */   
/*  15:    */   public static long copy(InputStream input, OutputStream output)
/*  16:    */     throws IOException
/*  17:    */   {
/*  18: 57 */     return copy(input, output, 8024);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public static long copy(InputStream input, OutputStream output, int buffersize)
/*  22:    */     throws IOException
/*  23:    */   {
/*  24: 74 */     byte[] buffer = new byte[buffersize];
/*  25: 75 */     int n = 0;
/*  26: 76 */     long count = 0L;
/*  27: 77 */     while (-1 != (n = input.read(buffer)))
/*  28:    */     {
/*  29: 78 */       output.write(buffer, 0, n);
/*  30: 79 */       count += n;
/*  31:    */     }
/*  32: 81 */     return count;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public static long skip(InputStream input, long numToSkip)
/*  36:    */     throws IOException
/*  37:    */   {
/*  38:101 */     long available = numToSkip;
/*  39:102 */     while (numToSkip > 0L)
/*  40:    */     {
/*  41:103 */       long skipped = input.skip(numToSkip);
/*  42:104 */       if (skipped == 0L) {
/*  43:    */         break;
/*  44:    */       }
/*  45:107 */       numToSkip -= skipped;
/*  46:    */     }
/*  47:110 */     while (numToSkip > 0L)
/*  48:    */     {
/*  49:111 */       int read = readFully(input, SKIP_BUF, 0, (int)Math.min(numToSkip, 4096L));
/*  50:113 */       if (read < 1) {
/*  51:    */         break;
/*  52:    */       }
/*  53:116 */       numToSkip -= read;
/*  54:    */     }
/*  55:118 */     return available - numToSkip;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public static int readFully(InputStream input, byte[] b)
/*  59:    */     throws IOException
/*  60:    */   {
/*  61:134 */     return readFully(input, b, 0, b.length);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public static int readFully(InputStream input, byte[] b, int offset, int len)
/*  65:    */     throws IOException
/*  66:    */   {
/*  67:155 */     if ((len < 0) || (offset < 0) || (len + offset > b.length)) {
/*  68:156 */       throw new IndexOutOfBoundsException();
/*  69:    */     }
/*  70:158 */     int count = 0;int x = 0;
/*  71:159 */     while (count != len)
/*  72:    */     {
/*  73:160 */       x = input.read(b, offset + count, len - count);
/*  74:161 */       if (x == -1) {
/*  75:    */         break;
/*  76:    */       }
/*  77:164 */       count += x;
/*  78:    */     }
/*  79:166 */     return count;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public static byte[] toByteArray(InputStream input)
/*  83:    */     throws IOException
/*  84:    */   {
/*  85:188 */     ByteArrayOutputStream output = new ByteArrayOutputStream();
/*  86:189 */     copy(input, output);
/*  87:190 */     return output.toByteArray();
/*  88:    */   }
/*  89:    */   
/*  90:    */   public static void closeQuietly(Closeable c)
/*  91:    */   {
/*  92:199 */     if (c != null) {
/*  93:    */       try
/*  94:    */       {
/*  95:201 */         c.close();
/*  96:    */       }
/*  97:    */       catch (IOException ignored) {}
/*  98:    */     }
/*  99:    */   }
/* 100:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.utils.IOUtils
 * JD-Core Version:    0.7.0.1
 */