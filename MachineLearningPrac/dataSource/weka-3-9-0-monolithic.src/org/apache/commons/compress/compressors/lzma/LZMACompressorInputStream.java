/*   1:    */ package org.apache.commons.compress.compressors.lzma;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*   6:    */ import org.tukaani.xz.LZMAInputStream;
/*   7:    */ 
/*   8:    */ public class LZMACompressorInputStream
/*   9:    */   extends CompressorInputStream
/*  10:    */ {
/*  11:    */   private final InputStream in;
/*  12:    */   
/*  13:    */   public LZMACompressorInputStream(InputStream inputStream)
/*  14:    */     throws IOException
/*  15:    */   {
/*  16: 48 */     this.in = new LZMAInputStream(inputStream);
/*  17:    */   }
/*  18:    */   
/*  19:    */   public int read()
/*  20:    */     throws IOException
/*  21:    */   {
/*  22: 54 */     int ret = this.in.read();
/*  23: 55 */     count(ret == -1 ? 0 : 1);
/*  24: 56 */     return ret;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public int read(byte[] buf, int off, int len)
/*  28:    */     throws IOException
/*  29:    */   {
/*  30: 62 */     int ret = this.in.read(buf, off, len);
/*  31: 63 */     count(ret);
/*  32: 64 */     return ret;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public long skip(long n)
/*  36:    */     throws IOException
/*  37:    */   {
/*  38: 70 */     return this.in.skip(n);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public int available()
/*  42:    */     throws IOException
/*  43:    */   {
/*  44: 76 */     return this.in.available();
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void close()
/*  48:    */     throws IOException
/*  49:    */   {
/*  50: 82 */     this.in.close();
/*  51:    */   }
/*  52:    */   
/*  53:    */   public static boolean matches(byte[] signature, int length)
/*  54:    */   {
/*  55: 98 */     if ((signature == null) || (length < 3)) {
/*  56: 99 */       return false;
/*  57:    */     }
/*  58:102 */     if (signature[0] != 93) {
/*  59:103 */       return false;
/*  60:    */     }
/*  61:106 */     if (signature[1] != 0) {
/*  62:107 */       return false;
/*  63:    */     }
/*  64:110 */     if (signature[2] != 0) {
/*  65:111 */       return false;
/*  66:    */     }
/*  67:114 */     return true;
/*  68:    */   }
/*  69:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream
 * JD-Core Version:    0.7.0.1
 */