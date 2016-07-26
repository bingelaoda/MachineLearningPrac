/*   1:    */ package org.netlib.util;
/*   2:    */ 
/*   3:    */ public class MatConv
/*   4:    */ {
/*   5:    */   public static double[] doubleTwoDtoOneD(double[][] paramArrayOfDouble)
/*   6:    */   {
/*   7: 35 */     int i = paramArrayOfDouble.length;
/*   8: 36 */     double[] arrayOfDouble = new double[i * paramArrayOfDouble[0].length];
/*   9: 38 */     for (int j = 0; j < i; j++) {
/*  10: 39 */       for (int k = 0; k < paramArrayOfDouble[0].length; k++) {
/*  11: 40 */         arrayOfDouble[(j + k * i)] = paramArrayOfDouble[j][k];
/*  12:    */       }
/*  13:    */     }
/*  14: 42 */     return arrayOfDouble;
/*  15:    */   }
/*  16:    */   
/*  17:    */   public static double[][] doubleOneDtoTwoD(double[] paramArrayOfDouble, int paramInt)
/*  18:    */   {
/*  19: 57 */     double[][] arrayOfDouble = new double[paramInt][paramArrayOfDouble.length / paramInt];
/*  20: 60 */     for (int i = 0; i < paramInt; i++) {
/*  21: 61 */       for (int j = 0; j < arrayOfDouble[0].length; j++) {
/*  22: 62 */         arrayOfDouble[i][j] = paramArrayOfDouble[(i + j * paramInt)];
/*  23:    */       }
/*  24:    */     }
/*  25: 64 */     return arrayOfDouble;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public static float[] floatTwoDtoOneD(float[][] paramArrayOfFloat)
/*  29:    */   {
/*  30: 82 */     int i = paramArrayOfFloat.length;
/*  31: 83 */     float[] arrayOfFloat = new float[i * paramArrayOfFloat[0].length];
/*  32: 85 */     for (int j = 0; j < i; j++) {
/*  33: 86 */       for (int k = 0; k < paramArrayOfFloat[0].length; k++) {
/*  34: 87 */         arrayOfFloat[(j + k * i)] = paramArrayOfFloat[j][k];
/*  35:    */       }
/*  36:    */     }
/*  37: 89 */     return arrayOfFloat;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public static float[][] floatOneDtoTwoD(float[] paramArrayOfFloat, int paramInt)
/*  41:    */   {
/*  42:104 */     float[][] arrayOfFloat = new float[paramInt][paramArrayOfFloat.length / paramInt];
/*  43:106 */     for (int i = 0; i < paramInt; i++) {
/*  44:107 */       for (int j = 0; j < arrayOfFloat[0].length; j++) {
/*  45:108 */         arrayOfFloat[i][j] = paramArrayOfFloat[(i + j * paramInt)];
/*  46:    */       }
/*  47:    */     }
/*  48:110 */     return arrayOfFloat;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static int[] intTwoDtoOneD(int[][] paramArrayOfInt)
/*  52:    */   {
/*  53:128 */     int i = paramArrayOfInt.length;
/*  54:129 */     int[] arrayOfInt = new int[i * paramArrayOfInt[0].length];
/*  55:131 */     for (int j = 0; j < i; j++) {
/*  56:132 */       for (int k = 0; k < paramArrayOfInt[0].length; k++) {
/*  57:133 */         arrayOfInt[(j + k * i)] = paramArrayOfInt[j][k];
/*  58:    */       }
/*  59:    */     }
/*  60:135 */     return arrayOfInt;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public static int[][] intOneDtoTwoD(int[] paramArrayOfInt, int paramInt)
/*  64:    */   {
/*  65:150 */     int[][] arrayOfInt = new int[paramInt][paramArrayOfInt.length / paramInt];
/*  66:153 */     for (int i = 0; i < paramInt; i++) {
/*  67:154 */       for (int j = 0; j < arrayOfInt[0].length; j++) {
/*  68:155 */         arrayOfInt[i][j] = paramArrayOfInt[(i + j * paramInt)];
/*  69:    */       }
/*  70:    */     }
/*  71:157 */     return arrayOfInt;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public static void copyOneDintoTwoD(double[][] paramArrayOfDouble, double[] paramArrayOfDouble1)
/*  75:    */   {
/*  76:172 */     int k = paramArrayOfDouble.length;
/*  77:174 */     for (int i = 0; i < k; i++) {
/*  78:175 */       for (int j = 0; j < paramArrayOfDouble[0].length; j++) {
/*  79:176 */         paramArrayOfDouble[i][j] = paramArrayOfDouble1[(i + j * k)];
/*  80:    */       }
/*  81:    */     }
/*  82:    */   }
/*  83:    */   
/*  84:    */   public static void copyOneDintoTwoD(float[][] paramArrayOfFloat, float[] paramArrayOfFloat1)
/*  85:    */   {
/*  86:191 */     int k = paramArrayOfFloat.length;
/*  87:193 */     for (int i = 0; i < k; i++) {
/*  88:194 */       for (int j = 0; j < paramArrayOfFloat[0].length; j++) {
/*  89:195 */         paramArrayOfFloat[i][j] = paramArrayOfFloat1[(i + j * k)];
/*  90:    */       }
/*  91:    */     }
/*  92:    */   }
/*  93:    */   
/*  94:    */   public static void copyOneDintoTwoD(int[][] paramArrayOfInt, int[] paramArrayOfInt1)
/*  95:    */   {
/*  96:210 */     int k = paramArrayOfInt.length;
/*  97:212 */     for (int i = 0; i < k; i++) {
/*  98:213 */       for (int j = 0; j < paramArrayOfInt[0].length; j++) {
/*  99:214 */         paramArrayOfInt[i][j] = paramArrayOfInt1[(i + j * k)];
/* 100:    */       }
/* 101:    */     }
/* 102:    */   }
/* 103:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.util.MatConv
 * JD-Core Version:    0.7.0.1
 */