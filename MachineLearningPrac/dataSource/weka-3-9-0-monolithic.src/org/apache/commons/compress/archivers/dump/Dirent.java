/*  1:   */ package org.apache.commons.compress.archivers.dump;
/*  2:   */ 
/*  3:   */ class Dirent
/*  4:   */ {
/*  5:   */   private final int ino;
/*  6:   */   private final int parentIno;
/*  7:   */   private final int type;
/*  8:   */   private final String name;
/*  9:   */   
/* 10:   */   Dirent(int ino, int parentIno, int type, String name)
/* 11:   */   {
/* 12:39 */     this.ino = ino;
/* 13:40 */     this.parentIno = parentIno;
/* 14:41 */     this.type = type;
/* 15:42 */     this.name = name;
/* 16:   */   }
/* 17:   */   
/* 18:   */   int getIno()
/* 19:   */   {
/* 20:50 */     return this.ino;
/* 21:   */   }
/* 22:   */   
/* 23:   */   int getParentIno()
/* 24:   */   {
/* 25:58 */     return this.parentIno;
/* 26:   */   }
/* 27:   */   
/* 28:   */   int getType()
/* 29:   */   {
/* 30:66 */     return this.type;
/* 31:   */   }
/* 32:   */   
/* 33:   */   String getName()
/* 34:   */   {
/* 35:74 */     return this.name;
/* 36:   */   }
/* 37:   */   
/* 38:   */   public String toString()
/* 39:   */   {
/* 40:82 */     return String.format("[%d]: %s", new Object[] { Integer.valueOf(this.ino), this.name });
/* 41:   */   }
/* 42:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.dump.Dirent
 * JD-Core Version:    0.7.0.1
 */