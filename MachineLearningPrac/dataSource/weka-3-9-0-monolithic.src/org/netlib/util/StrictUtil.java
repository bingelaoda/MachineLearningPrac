/*   1:    */ package org.netlib.util;
/*   2:    */ 
/*   3:    */ public class StrictUtil
/*   4:    */   extends Util
/*   5:    */ {
/*   6:    */   public static strictfp int max(int paramInt1, int paramInt2, int paramInt3)
/*   7:    */   {
/*   8: 32 */     return StrictMath.max(paramInt1 > paramInt2 ? paramInt1 : paramInt2, StrictMath.max(paramInt2, paramInt3));
/*   9:    */   }
/*  10:    */   
/*  11:    */   public static strictfp float max(float paramFloat1, float paramFloat2, float paramFloat3)
/*  12:    */   {
/*  13: 47 */     return StrictMath.max(paramFloat1 > paramFloat2 ? paramFloat1 : paramFloat2, StrictMath.max(paramFloat2, paramFloat3));
/*  14:    */   }
/*  15:    */   
/*  16:    */   public static strictfp double max(double paramDouble1, double paramDouble2, double paramDouble3)
/*  17:    */   {
/*  18: 62 */     return StrictMath.max(paramDouble1 > paramDouble2 ? paramDouble1 : paramDouble2, StrictMath.max(paramDouble2, paramDouble3));
/*  19:    */   }
/*  20:    */   
/*  21:    */   public static strictfp int min(int paramInt1, int paramInt2, int paramInt3)
/*  22:    */   {
/*  23: 77 */     return StrictMath.min(paramInt1 < paramInt2 ? paramInt1 : paramInt2, StrictMath.min(paramInt2, paramInt3));
/*  24:    */   }
/*  25:    */   
/*  26:    */   public static strictfp float min(float paramFloat1, float paramFloat2, float paramFloat3)
/*  27:    */   {
/*  28: 92 */     return StrictMath.min(paramFloat1 < paramFloat2 ? paramFloat1 : paramFloat2, StrictMath.min(paramFloat2, paramFloat3));
/*  29:    */   }
/*  30:    */   
/*  31:    */   public static strictfp double min(double paramDouble1, double paramDouble2, double paramDouble3)
/*  32:    */   {
/*  33:107 */     return StrictMath.min(paramDouble1 < paramDouble2 ? paramDouble1 : paramDouble2, StrictMath.min(paramDouble2, paramDouble3));
/*  34:    */   }
/*  35:    */   
/*  36:    */   public static strictfp double log10(double paramDouble)
/*  37:    */   {
/*  38:120 */     return StrictMath.log(paramDouble) / 2.30258509D;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public static strictfp float log10(float paramFloat)
/*  42:    */   {
/*  43:133 */     return (float)(StrictMath.log(paramFloat) / 2.30258509D);
/*  44:    */   }
/*  45:    */   
/*  46:    */   public static strictfp int nint(float paramFloat)
/*  47:    */   {
/*  48:152 */     return (int)(paramFloat >= 0.0F ? paramFloat + 0.5D : paramFloat - 0.5D);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static strictfp int idnint(double paramDouble)
/*  52:    */   {
/*  53:171 */     return (int)(paramDouble >= 0.0D ? paramDouble + 0.5D : paramDouble - 0.5D);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public static strictfp float sign(float paramFloat1, float paramFloat2)
/*  57:    */   {
/*  58:191 */     return paramFloat2 >= 0.0F ? StrictMath.abs(paramFloat1) : -StrictMath.abs(paramFloat1);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public static strictfp int isign(int paramInt1, int paramInt2)
/*  62:    */   {
/*  63:211 */     return paramInt2 >= 0 ? StrictMath.abs(paramInt1) : -StrictMath.abs(paramInt1);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public static strictfp double dsign(double paramDouble1, double paramDouble2)
/*  67:    */   {
/*  68:231 */     return paramDouble2 >= 0.0D ? StrictMath.abs(paramDouble1) : -StrictMath.abs(paramDouble1);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public static strictfp float dim(float paramFloat1, float paramFloat2)
/*  72:    */   {
/*  73:251 */     return paramFloat1 > paramFloat2 ? paramFloat1 - paramFloat2 : 0.0F;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public static strictfp int idim(int paramInt1, int paramInt2)
/*  77:    */   {
/*  78:271 */     return paramInt1 > paramInt2 ? paramInt1 - paramInt2 : 0;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public static strictfp double ddim(double paramDouble1, double paramDouble2)
/*  82:    */   {
/*  83:291 */     return paramDouble1 > paramDouble2 ? paramDouble1 - paramDouble2 : 0.0D;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public static strictfp double sinh(double paramDouble)
/*  87:    */   {
/*  88:304 */     return (StrictMath.exp(paramDouble) - StrictMath.exp(-paramDouble)) * 0.5D;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public static strictfp double cosh(double paramDouble)
/*  92:    */   {
/*  93:317 */     return (StrictMath.exp(paramDouble) + StrictMath.exp(-paramDouble)) * 0.5D;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public static strictfp double tanh(double paramDouble)
/*  97:    */   {
/*  98:330 */     return sinh(paramDouble) / cosh(paramDouble);
/*  99:    */   }
/* 100:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.util.StrictUtil
 * JD-Core Version:    0.7.0.1
 */