/*   1:    */ package org.apache.commons.compress.archivers.sevenz;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ 
/*   5:    */ public enum SevenZMethod
/*   6:    */ {
/*   7: 38 */   COPY(new byte[] { 0 }),  LZMA(new byte[] { 3, 1, 1 }),  LZMA2(new byte[] { 33 }),  DEFLATE(new byte[] { 4, 1, 8 }),  BZIP2(new byte[] { 4, 2, 2 }),  AES256SHA256(new byte[] { 6, -15, 7, 1 }),  BCJ_X86_FILTER(new byte[] { 3, 3, 1, 3 }),  BCJ_PPC_FILTER(new byte[] { 3, 3, 2, 5 }),  BCJ_IA64_FILTER(new byte[] { 3, 3, 4, 1 }),  BCJ_ARM_FILTER(new byte[] { 3, 3, 5, 1 }),  BCJ_ARM_THUMB_FILTER(new byte[] { 3, 3, 7, 1 }),  BCJ_SPARC_FILTER(new byte[] { 3, 3, 8, 5 }),  DELTA_FILTER(new byte[] { 3 });
/*   8:    */   
/*   9:    */   private final byte[] id;
/*  10:    */   
/*  11:    */   private SevenZMethod(byte[] id)
/*  12:    */   {
/*  13: 91 */     this.id = id;
/*  14:    */   }
/*  15:    */   
/*  16:    */   byte[] getId()
/*  17:    */   {
/*  18: 95 */     byte[] copy = new byte[this.id.length];
/*  19: 96 */     System.arraycopy(this.id, 0, copy, 0, this.id.length);
/*  20: 97 */     return copy;
/*  21:    */   }
/*  22:    */   
/*  23:    */   static SevenZMethod byId(byte[] id)
/*  24:    */   {
/*  25:101 */     for (SevenZMethod m : (SevenZMethod[])SevenZMethod.class.getEnumConstants()) {
/*  26:102 */       if (Arrays.equals(m.id, id)) {
/*  27:103 */         return m;
/*  28:    */       }
/*  29:    */     }
/*  30:106 */     return null;
/*  31:    */   }
/*  32:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.sevenz.SevenZMethod
 * JD-Core Version:    0.7.0.1
 */