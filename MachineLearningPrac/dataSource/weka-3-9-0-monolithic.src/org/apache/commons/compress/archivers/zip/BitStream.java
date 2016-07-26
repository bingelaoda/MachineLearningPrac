/*  1:   */ package org.apache.commons.compress.archivers.zip;
/*  2:   */ 
/*  3:   */ import java.io.IOException;
/*  4:   */ import java.io.InputStream;
/*  5:   */ import java.nio.ByteOrder;
/*  6:   */ import org.apache.commons.compress.utils.BitInputStream;
/*  7:   */ 
/*  8:   */ class BitStream
/*  9:   */   extends BitInputStream
/* 10:   */ {
/* 11:   */   BitStream(InputStream in)
/* 12:   */   {
/* 13:36 */     super(in, ByteOrder.LITTLE_ENDIAN);
/* 14:   */   }
/* 15:   */   
/* 16:   */   int nextBit()
/* 17:   */     throws IOException
/* 18:   */   {
/* 19:45 */     return (int)readBits(1);
/* 20:   */   }
/* 21:   */   
/* 22:   */   long nextBits(int n)
/* 23:   */     throws IOException
/* 24:   */   {
/* 25:55 */     return readBits(n);
/* 26:   */   }
/* 27:   */   
/* 28:   */   int nextByte()
/* 29:   */     throws IOException
/* 30:   */   {
/* 31:59 */     return (int)readBits(8);
/* 32:   */   }
/* 33:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.BitStream
 * JD-Core Version:    0.7.0.1
 */