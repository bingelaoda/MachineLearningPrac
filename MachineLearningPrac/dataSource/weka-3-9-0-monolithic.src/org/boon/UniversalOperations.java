/*  1:   */ package org.boon;
/*  2:   */ 
/*  3:   */ public abstract interface UniversalOperations<ITEM, INDEX>
/*  4:   */ {
/*  5:   */   public abstract ITEM idx(INDEX paramINDEX);
/*  6:   */   
/*  7:   */   public abstract void idx(INDEX paramINDEX, ITEM paramITEM);
/*  8:   */   
/*  9:   */   public abstract int len();
/* 10:   */   
/* 11:   */   public abstract Returns add(ITEM paramITEM);
/* 12:   */   
/* 13:   */   public abstract UniversalOperations copy(UniversalOperations paramUniversalOperations);
/* 14:   */   
/* 15:   */   public abstract UniversalOperations clone(UniversalOperations paramUniversalOperations);
/* 16:   */   
/* 17:   */   public abstract void insert(ITEM paramITEM);
/* 18:   */   
/* 19:   */   public abstract void slc(INDEX paramINDEX1, INDEX paramINDEX2);
/* 20:   */   
/* 21:   */   public abstract void slc(INDEX paramINDEX);
/* 22:   */   
/* 23:   */   public abstract void slcEnd(INDEX paramINDEX);
/* 24:   */   
/* 25:   */   public static enum Returns
/* 26:   */   {
/* 27:88 */     VARIES;
/* 28:   */     
/* 29:   */     private Returns() {}
/* 30:   */   }
/* 31:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.UniversalOperations
 * JD-Core Version:    0.7.0.1
 */