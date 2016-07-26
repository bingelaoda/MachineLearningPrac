/*   1:    */ package org.apache.commons.compress.utils;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.util.zip.Checksum;
/*   6:    */ 
/*   7:    */ public class ChecksumVerifyingInputStream
/*   8:    */   extends InputStream
/*   9:    */ {
/*  10:    */   private final InputStream in;
/*  11:    */   private long bytesRemaining;
/*  12:    */   private final long expectedChecksum;
/*  13:    */   private final Checksum checksum;
/*  14:    */   
/*  15:    */   public ChecksumVerifyingInputStream(Checksum checksum, InputStream in, long size, long expectedChecksum)
/*  16:    */   {
/*  17: 38 */     this.checksum = checksum;
/*  18: 39 */     this.in = in;
/*  19: 40 */     this.expectedChecksum = expectedChecksum;
/*  20: 41 */     this.bytesRemaining = size;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public int read()
/*  24:    */     throws IOException
/*  25:    */   {
/*  26: 52 */     if (this.bytesRemaining <= 0L) {
/*  27: 53 */       return -1;
/*  28:    */     }
/*  29: 55 */     int ret = this.in.read();
/*  30: 56 */     if (ret >= 0)
/*  31:    */     {
/*  32: 57 */       this.checksum.update(ret);
/*  33: 58 */       this.bytesRemaining -= 1L;
/*  34:    */     }
/*  35: 60 */     if ((this.bytesRemaining == 0L) && (this.expectedChecksum != this.checksum.getValue())) {
/*  36: 61 */       throw new IOException("Checksum verification failed");
/*  37:    */     }
/*  38: 63 */     return ret;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public int read(byte[] b)
/*  42:    */     throws IOException
/*  43:    */   {
/*  44: 74 */     return read(b, 0, b.length);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public int read(byte[] b, int off, int len)
/*  48:    */     throws IOException
/*  49:    */   {
/*  50: 85 */     int ret = this.in.read(b, off, len);
/*  51: 86 */     if (ret >= 0)
/*  52:    */     {
/*  53: 87 */       this.checksum.update(b, off, ret);
/*  54: 88 */       this.bytesRemaining -= ret;
/*  55:    */     }
/*  56: 90 */     if ((this.bytesRemaining <= 0L) && (this.expectedChecksum != this.checksum.getValue())) {
/*  57: 91 */       throw new IOException("Checksum verification failed");
/*  58:    */     }
/*  59: 93 */     return ret;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public long skip(long n)
/*  63:    */     throws IOException
/*  64:    */   {
/*  65: 99 */     if (read() >= 0) {
/*  66:100 */       return 1L;
/*  67:    */     }
/*  68:102 */     return 0L;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void close()
/*  72:    */     throws IOException
/*  73:    */   {
/*  74:108 */     this.in.close();
/*  75:    */   }
/*  76:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.utils.ChecksumVerifyingInputStream
 * JD-Core Version:    0.7.0.1
 */