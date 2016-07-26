/*   1:    */ package org.apache.commons.compress.compressors.xz;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*   6:    */ import org.tukaani.xz.SingleXZInputStream;
/*   7:    */ import org.tukaani.xz.XZ;
/*   8:    */ import org.tukaani.xz.XZInputStream;
/*   9:    */ 
/*  10:    */ public class XZCompressorInputStream
/*  11:    */   extends CompressorInputStream
/*  12:    */ {
/*  13:    */   private final InputStream in;
/*  14:    */   
/*  15:    */   public static boolean matches(byte[] signature, int length)
/*  16:    */   {
/*  17: 44 */     if (length < XZ.HEADER_MAGIC.length) {
/*  18: 45 */       return false;
/*  19:    */     }
/*  20: 48 */     for (int i = 0; i < XZ.HEADER_MAGIC.length; i++) {
/*  21: 49 */       if (signature[i] != XZ.HEADER_MAGIC[i]) {
/*  22: 50 */         return false;
/*  23:    */       }
/*  24:    */     }
/*  25: 54 */     return true;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public XZCompressorInputStream(InputStream inputStream)
/*  29:    */     throws IOException
/*  30:    */   {
/*  31: 72 */     this(inputStream, false);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public XZCompressorInputStream(InputStream inputStream, boolean decompressConcatenated)
/*  35:    */     throws IOException
/*  36:    */   {
/*  37: 95 */     if (decompressConcatenated) {
/*  38: 96 */       this.in = new XZInputStream(inputStream);
/*  39:    */     } else {
/*  40: 98 */       this.in = new SingleXZInputStream(inputStream);
/*  41:    */     }
/*  42:    */   }
/*  43:    */   
/*  44:    */   public int read()
/*  45:    */     throws IOException
/*  46:    */   {
/*  47:104 */     int ret = this.in.read();
/*  48:105 */     count(ret == -1 ? -1 : 1);
/*  49:106 */     return ret;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public int read(byte[] buf, int off, int len)
/*  53:    */     throws IOException
/*  54:    */   {
/*  55:111 */     int ret = this.in.read(buf, off, len);
/*  56:112 */     count(ret);
/*  57:113 */     return ret;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public long skip(long n)
/*  61:    */     throws IOException
/*  62:    */   {
/*  63:118 */     return this.in.skip(n);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public int available()
/*  67:    */     throws IOException
/*  68:    */   {
/*  69:123 */     return this.in.available();
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void close()
/*  73:    */     throws IOException
/*  74:    */   {
/*  75:128 */     this.in.close();
/*  76:    */   }
/*  77:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.xz.XZCompressorInputStream
 * JD-Core Version:    0.7.0.1
 */