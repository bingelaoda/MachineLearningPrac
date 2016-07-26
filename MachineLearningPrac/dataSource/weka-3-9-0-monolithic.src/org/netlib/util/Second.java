/*  1:   */ package org.netlib.util;
/*  2:   */ 
/*  3:   */ public class Second
/*  4:   */ {
/*  5:   */   public static float second()
/*  6:   */   {
/*  7:40 */     float[] arrayOfFloat = new float[2];
/*  8:   */     
/*  9:42 */     Etime.etime();
/* 10:43 */     Etime.etime(arrayOfFloat, 0);
/* 11:   */     
/* 12:45 */     return arrayOfFloat[0];
/* 13:   */   }
/* 14:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.util.Second
 * JD-Core Version:    0.7.0.1
 */