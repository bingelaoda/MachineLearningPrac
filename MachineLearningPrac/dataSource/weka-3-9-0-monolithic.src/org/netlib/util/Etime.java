/*  1:   */ package org.netlib.util;
/*  2:   */ 
/*  3:   */ public class Etime
/*  4:   */ {
/*  5:31 */   private static int call_num = 0;
/*  6:32 */   private static long start_time = 0L;
/*  7:   */   
/*  8:   */   public static void etime()
/*  9:   */   {
/* 10:39 */     float[] arrayOfFloat = new float[2];
/* 11:40 */     etime(arrayOfFloat, 0);
/* 12:   */   }
/* 13:   */   
/* 14:   */   public static float etime(float[] paramArrayOfFloat, int paramInt)
/* 15:   */   {
/* 16:58 */     if (call_num++ == 0)
/* 17:   */     {
/* 18:60 */       start_time = System.currentTimeMillis();
/* 19:61 */       paramArrayOfFloat[(0 + paramInt)] = 0.0F;
/* 20:62 */       paramArrayOfFloat[(1 + paramInt)] = 0.0F;
/* 21:63 */       return 0.0F;
/* 22:   */     }
/* 23:66 */     paramArrayOfFloat[(0 + paramInt)] = ((float)(System.currentTimeMillis() - start_time) / 1000.0F);
/* 24:67 */     paramArrayOfFloat[(1 + paramInt)] = paramArrayOfFloat[(0 + paramInt)];
/* 25:68 */     return paramArrayOfFloat[(0 + paramInt)];
/* 26:   */   }
/* 27:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.util.Etime
 * JD-Core Version:    0.7.0.1
 */