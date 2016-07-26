/*  1:   */ package org.apache.commons.compress.archivers.zip;
/*  2:   */ 
/*  3:   */ class CircularBuffer
/*  4:   */ {
/*  5:   */   private final int size;
/*  6:   */   private final byte[] buffer;
/*  7:   */   private int readIndex;
/*  8:   */   private int writeIndex;
/*  9:   */   
/* 10:   */   CircularBuffer(int size)
/* 11:   */   {
/* 12:43 */     this.size = size;
/* 13:44 */     this.buffer = new byte[size];
/* 14:   */   }
/* 15:   */   
/* 16:   */   public boolean available()
/* 17:   */   {
/* 18:51 */     return this.readIndex != this.writeIndex;
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void put(int value)
/* 22:   */   {
/* 23:58 */     this.buffer[this.writeIndex] = ((byte)value);
/* 24:59 */     this.writeIndex = ((this.writeIndex + 1) % this.size);
/* 25:   */   }
/* 26:   */   
/* 27:   */   public int get()
/* 28:   */   {
/* 29:66 */     if (available())
/* 30:   */     {
/* 31:67 */       int value = this.buffer[this.readIndex];
/* 32:68 */       this.readIndex = ((this.readIndex + 1) % this.size);
/* 33:69 */       return value & 0xFF;
/* 34:   */     }
/* 35:71 */     return -1;
/* 36:   */   }
/* 37:   */   
/* 38:   */   public void copy(int distance, int length)
/* 39:   */   {
/* 40:82 */     int pos1 = this.writeIndex - distance;
/* 41:83 */     int pos2 = pos1 + length;
/* 42:84 */     for (int i = pos1; i < pos2; i++)
/* 43:   */     {
/* 44:85 */       this.buffer[this.writeIndex] = this.buffer[((i + this.size) % this.size)];
/* 45:86 */       this.writeIndex = ((this.writeIndex + 1) % this.size);
/* 46:   */     }
/* 47:   */   }
/* 48:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.CircularBuffer
 * JD-Core Version:    0.7.0.1
 */