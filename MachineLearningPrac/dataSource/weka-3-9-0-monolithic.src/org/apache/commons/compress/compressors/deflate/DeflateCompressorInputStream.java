/*   1:    */ package org.apache.commons.compress.compressors.deflate;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.util.zip.Inflater;
/*   6:    */ import java.util.zip.InflaterInputStream;
/*   7:    */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*   8:    */ 
/*   9:    */ public class DeflateCompressorInputStream
/*  10:    */   extends CompressorInputStream
/*  11:    */ {
/*  12:    */   private static final int MAGIC_1 = 120;
/*  13:    */   private static final int MAGIC_2a = 1;
/*  14:    */   private static final int MAGIC_2b = 94;
/*  15:    */   private static final int MAGIC_2c = 156;
/*  16:    */   private static final int MAGIC_2d = 218;
/*  17:    */   private final InputStream in;
/*  18:    */   
/*  19:    */   public DeflateCompressorInputStream(InputStream inputStream)
/*  20:    */   {
/*  21: 49 */     this(inputStream, new DeflateParameters());
/*  22:    */   }
/*  23:    */   
/*  24:    */   public DeflateCompressorInputStream(InputStream inputStream, DeflateParameters parameters)
/*  25:    */   {
/*  26: 61 */     this.in = new InflaterInputStream(inputStream, new Inflater(!parameters.withZlibHeader()));
/*  27:    */   }
/*  28:    */   
/*  29:    */   public int read()
/*  30:    */     throws IOException
/*  31:    */   {
/*  32: 67 */     int ret = this.in.read();
/*  33: 68 */     count(ret == -1 ? 0 : 1);
/*  34: 69 */     return ret;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public int read(byte[] buf, int off, int len)
/*  38:    */     throws IOException
/*  39:    */   {
/*  40: 75 */     int ret = this.in.read(buf, off, len);
/*  41: 76 */     count(ret);
/*  42: 77 */     return ret;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public long skip(long n)
/*  46:    */     throws IOException
/*  47:    */   {
/*  48: 83 */     return this.in.skip(n);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public int available()
/*  52:    */     throws IOException
/*  53:    */   {
/*  54: 89 */     return this.in.available();
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void close()
/*  58:    */     throws IOException
/*  59:    */   {
/*  60: 95 */     this.in.close();
/*  61:    */   }
/*  62:    */   
/*  63:    */   public static boolean matches(byte[] signature, int length)
/*  64:    */   {
/*  65:112 */     return (length > 3) && (signature[0] == 120) && ((signature[1] == 1) || (signature[1] == 94) || (signature[1] == -100) || (signature[1] == -38));
/*  66:    */   }
/*  67:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream
 * JD-Core Version:    0.7.0.1
 */