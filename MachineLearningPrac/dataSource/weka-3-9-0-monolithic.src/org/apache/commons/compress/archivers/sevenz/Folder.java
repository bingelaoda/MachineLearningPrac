/*   1:    */ package org.apache.commons.compress.archivers.sevenz;
/*   2:    */ 
/*   3:    */ import java.util.LinkedList;
/*   4:    */ 
/*   5:    */ class Folder
/*   6:    */ {
/*   7:    */   Coder[] coders;
/*   8:    */   long totalInputStreams;
/*   9:    */   long totalOutputStreams;
/*  10:    */   BindPair[] bindPairs;
/*  11:    */   long[] packedStreams;
/*  12:    */   long[] unpackSizes;
/*  13:    */   boolean hasCrc;
/*  14:    */   long crc;
/*  15:    */   int numUnpackSubStreams;
/*  16:    */   
/*  17:    */   Iterable<Coder> getOrderedCoders()
/*  18:    */   {
/*  19: 55 */     LinkedList<Coder> l = new LinkedList();
/*  20: 56 */     int current = (int)this.packedStreams[0];
/*  21: 57 */     while (current != -1)
/*  22:    */     {
/*  23: 58 */       l.addLast(this.coders[current]);
/*  24: 59 */       int pair = findBindPairForOutStream(current);
/*  25: 60 */       current = pair != -1 ? (int)this.bindPairs[pair].inIndex : -1;
/*  26:    */     }
/*  27: 62 */     return l;
/*  28:    */   }
/*  29:    */   
/*  30:    */   int findBindPairForInStream(int index)
/*  31:    */   {
/*  32: 66 */     for (int i = 0; i < this.bindPairs.length; i++) {
/*  33: 67 */       if (this.bindPairs[i].inIndex == index) {
/*  34: 68 */         return i;
/*  35:    */       }
/*  36:    */     }
/*  37: 71 */     return -1;
/*  38:    */   }
/*  39:    */   
/*  40:    */   int findBindPairForOutStream(int index)
/*  41:    */   {
/*  42: 75 */     for (int i = 0; i < this.bindPairs.length; i++) {
/*  43: 76 */       if (this.bindPairs[i].outIndex == index) {
/*  44: 77 */         return i;
/*  45:    */       }
/*  46:    */     }
/*  47: 80 */     return -1;
/*  48:    */   }
/*  49:    */   
/*  50:    */   long getUnpackSize()
/*  51:    */   {
/*  52: 84 */     if (this.totalOutputStreams == 0L) {
/*  53: 85 */       return 0L;
/*  54:    */     }
/*  55: 87 */     for (int i = (int)this.totalOutputStreams - 1; i >= 0; i--) {
/*  56: 88 */       if (findBindPairForOutStream(i) < 0) {
/*  57: 89 */         return this.unpackSizes[i];
/*  58:    */       }
/*  59:    */     }
/*  60: 92 */     return 0L;
/*  61:    */   }
/*  62:    */   
/*  63:    */   long getUnpackSizeForCoder(Coder coder)
/*  64:    */   {
/*  65: 96 */     if (this.coders != null) {
/*  66: 97 */       for (int i = 0; i < this.coders.length; i++) {
/*  67: 98 */         if (this.coders[i] == coder) {
/*  68: 99 */           return this.unpackSizes[i];
/*  69:    */         }
/*  70:    */       }
/*  71:    */     }
/*  72:103 */     return 0L;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public String toString()
/*  76:    */   {
/*  77:108 */     return "Folder with " + this.coders.length + " coders, " + this.totalInputStreams + " input streams, " + this.totalOutputStreams + " output streams, " + this.bindPairs.length + " bind pairs, " + this.packedStreams.length + " packed streams, " + this.unpackSizes.length + " unpack sizes, " + (this.hasCrc ? "with CRC " + this.crc : "without CRC") + " and " + this.numUnpackSubStreams + " unpack streams";
/*  78:    */   }
/*  79:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.sevenz.Folder
 * JD-Core Version:    0.7.0.1
 */