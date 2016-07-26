/*   1:    */ package org.apache.commons.compress.utils;
/*   2:    */ 
/*   3:    */ import java.io.Closeable;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import java.nio.ByteOrder;
/*   7:    */ 
/*   8:    */ public class BitInputStream
/*   9:    */   implements Closeable
/*  10:    */ {
/*  11:    */   private static final int MAXIMUM_CACHE_SIZE = 63;
/*  12: 33 */   private static final long[] MASKS = new long[64];
/*  13:    */   private final InputStream in;
/*  14:    */   private final ByteOrder byteOrder;
/*  15:    */   
/*  16:    */   static
/*  17:    */   {
/*  18: 36 */     for (int i = 1; i <= 63; i++) {
/*  19: 37 */       MASKS[i] = ((MASKS[(i - 1)] << 1) + 1L);
/*  20:    */     }
/*  21:    */   }
/*  22:    */   
/*  23: 43 */   private long bitsCached = 0L;
/*  24: 44 */   private int bitsCachedSize = 0;
/*  25:    */   
/*  26:    */   public BitInputStream(InputStream in, ByteOrder byteOrder)
/*  27:    */   {
/*  28: 53 */     this.in = in;
/*  29: 54 */     this.byteOrder = byteOrder;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void close()
/*  33:    */     throws IOException
/*  34:    */   {
/*  35: 58 */     this.in.close();
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void clearBitCache()
/*  39:    */   {
/*  40: 66 */     this.bitsCached = 0L;
/*  41: 67 */     this.bitsCachedSize = 0;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public long readBits(int count)
/*  45:    */     throws IOException
/*  46:    */   {
/*  47: 81 */     if ((count < 0) || (count > 63)) {
/*  48: 82 */       throw new IllegalArgumentException("count must not be negative or greater than 63");
/*  49:    */     }
/*  50: 84 */     while (this.bitsCachedSize < count)
/*  51:    */     {
/*  52: 85 */       long nextByte = this.in.read();
/*  53: 86 */       if (nextByte < 0L) {
/*  54: 87 */         return nextByte;
/*  55:    */       }
/*  56: 89 */       if (this.byteOrder == ByteOrder.LITTLE_ENDIAN)
/*  57:    */       {
/*  58: 90 */         this.bitsCached |= nextByte << this.bitsCachedSize;
/*  59:    */       }
/*  60:    */       else
/*  61:    */       {
/*  62: 92 */         this.bitsCached <<= 8;
/*  63: 93 */         this.bitsCached |= nextByte;
/*  64:    */       }
/*  65: 95 */       this.bitsCachedSize += 8;
/*  66:    */     }
/*  67:    */     long bitsOut;
/*  68: 99 */     if (this.byteOrder == ByteOrder.LITTLE_ENDIAN)
/*  69:    */     {
/*  70:100 */       long bitsOut = this.bitsCached & MASKS[count];
/*  71:101 */       this.bitsCached >>>= count;
/*  72:    */     }
/*  73:    */     else
/*  74:    */     {
/*  75:103 */       bitsOut = this.bitsCached >> this.bitsCachedSize - count & MASKS[count];
/*  76:    */     }
/*  77:105 */     this.bitsCachedSize -= count;
/*  78:106 */     return bitsOut;
/*  79:    */   }
/*  80:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.utils.BitInputStream
 * JD-Core Version:    0.7.0.1
 */