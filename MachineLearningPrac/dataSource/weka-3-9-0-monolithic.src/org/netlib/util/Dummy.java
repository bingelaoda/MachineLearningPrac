/*  1:   */ package org.netlib.util;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ 
/*  5:   */ public class Dummy
/*  6:   */ {
/*  7:   */   public static void go_to(String paramString, int paramInt)
/*  8:   */   {
/*  9:32 */     System.err.println("Warning: Untransformed goto remaining in program! (" + paramString + ", " + paramInt + ")");
/* 10:   */   }
/* 11:   */   
/* 12:   */   public static void label(String paramString, int paramInt)
/* 13:   */   {
/* 14:43 */     System.err.println("Warning: Untransformed label remaining in program! (" + paramString + ", " + paramInt + ")");
/* 15:   */   }
/* 16:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.util.Dummy
 * JD-Core Version:    0.7.0.1
 */