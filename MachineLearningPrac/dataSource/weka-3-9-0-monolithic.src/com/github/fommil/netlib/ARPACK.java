/*  1:   */ package com.github.fommil.netlib;
/*  2:   */ 
/*  3:   */ import java.util.logging.Logger;
/*  4:   */ import org.netlib.util.doubleW;
/*  5:   */ import org.netlib.util.floatW;
/*  6:   */ import org.netlib.util.intW;
/*  7:   */ 
/*  8:   */ public abstract class ARPACK
/*  9:   */ {
/* 10:45 */   private static final Logger log = Logger.getLogger(ARPACK.class.getName());
/* 11:   */   private static final String FALLBACK = "com.github.fommil.netlib.F2jARPACK";
/* 12:   */   private static final String IMPLS = "com.github.fommil.netlib.NativeSystemARPACK,com.github.fommil.netlib.NativeRefARPACK,com.github.fommil.netlib.F2jARPACK";
/* 13:   */   private static final String PROPERTY_KEY = "com.github.fommil.netlib.ARPACK";
/* 14:   */   private static final ARPACK INSTANCE;
/* 15:   */   
/* 16:   */   static
/* 17:   */   {
/* 18:   */     try
/* 19:   */     {
/* 20:54 */       String[] classNames = System.getProperty("com.github.fommil.netlib.ARPACK", "com.github.fommil.netlib.NativeSystemARPACK,com.github.fommil.netlib.NativeRefARPACK,com.github.fommil.netlib.F2jARPACK").split(",");
/* 21:55 */       ARPACK impl = null;
/* 22:56 */       for (String className : classNames) {
/* 23:   */         try
/* 24:   */         {
/* 25:58 */           impl = load(className);
/* 26:   */         }
/* 27:   */         catch (Throwable e)
/* 28:   */         {
/* 29:61 */           log.warning("Failed to load implementation from: " + className);
/* 30:   */         }
/* 31:   */       }
/* 32:64 */       if (impl == null)
/* 33:   */       {
/* 34:65 */         log.warning("Using the fallback implementation.");
/* 35:66 */         impl = load("com.github.fommil.netlib.F2jARPACK");
/* 36:   */       }
/* 37:68 */       INSTANCE = impl;
/* 38:69 */       log.config("Implementation provided by " + INSTANCE.getClass());
/* 39:   */     }
/* 40:   */     catch (Exception e)
/* 41:   */     {
/* 42:71 */       throw new ExceptionInInitializerError(e);
/* 43:   */     }
/* 44:   */   }
/* 45:   */   
/* 46:   */   private static ARPACK load(String className)
/* 47:   */     throws Exception
/* 48:   */   {
/* 49:76 */     Class klass = Class.forName(className);
/* 50:77 */     return (ARPACK)klass.newInstance();
/* 51:   */   }
/* 52:   */   
/* 53:   */   public static ARPACK getInstance()
/* 54:   */   {
/* 55:84 */     return INSTANCE;
/* 56:   */   }
/* 57:   */   
/* 58:   */   public abstract void dmout(int paramInt1, int paramInt2, int paramInt3, double[] paramArrayOfDouble, int paramInt4, int paramInt5, String paramString);
/* 59:   */   
/* 60:   */   public abstract void dmout(int paramInt1, int paramInt2, int paramInt3, double[] paramArrayOfDouble, int paramInt4, int paramInt5, int paramInt6, String paramString);
/* 61:   */   
/* 62:   */   public abstract void dvout(int paramInt1, int paramInt2, double[] paramArrayOfDouble, int paramInt3, String paramString);
/* 63:   */   
/* 64:   */   public abstract void dvout(int paramInt1, int paramInt2, double[] paramArrayOfDouble, int paramInt3, int paramInt4, String paramString);
/* 65:   */   
/* 66:   */   public abstract int icnteq(int paramInt1, int[] paramArrayOfInt, int paramInt2);
/* 67:   */   
/* 68:   */   public abstract int icnteq(int paramInt1, int[] paramArrayOfInt, int paramInt2, int paramInt3);
/* 69:   */   
/* 70:   */   public abstract void icopy(int paramInt1, int[] paramArrayOfInt1, int paramInt2, int[] paramArrayOfInt2, int paramInt3);
/* 71:   */   
/* 72:   */   public abstract void icopy(int paramInt1, int[] paramArrayOfInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt2, int paramInt4, int paramInt5);
/* 73:   */   
/* 74:   */   public abstract void iset(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3);
/* 75:   */   
/* 76:   */   public abstract void iset(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3, int paramInt4);
/* 77:   */   
/* 78:   */   public abstract void iswap(int paramInt1, int[] paramArrayOfInt1, int paramInt2, int[] paramArrayOfInt2, int paramInt3);
/* 79:   */   
/* 80:   */   public abstract void iswap(int paramInt1, int[] paramArrayOfInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt2, int paramInt4, int paramInt5);
/* 81:   */   
/* 82:   */   public abstract void ivout(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3, String paramString);
/* 83:   */   
/* 84:   */   public abstract void ivout(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3, int paramInt4, String paramString);
/* 85:   */   
/* 86:   */   public abstract void second(floatW paramfloatW);
/* 87:   */   
/* 88:   */   public abstract void smout(int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat, int paramInt4, int paramInt5, String paramString);
/* 89:   */   
/* 90:   */   public abstract void smout(int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat, int paramInt4, int paramInt5, int paramInt6, String paramString);
/* 91:   */   
/* 92:   */   public abstract void svout(int paramInt1, int paramInt2, float[] paramArrayOfFloat, int paramInt3, String paramString);
/* 93:   */   
/* 94:   */   public abstract void svout(int paramInt1, int paramInt2, float[] paramArrayOfFloat, int paramInt3, int paramInt4, String paramString);
/* 95:   */   
/* 96:   */   public abstract void dgetv0(intW paramintW1, String paramString, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, double[] paramArrayOfDouble1, int paramInt4, double[] paramArrayOfDouble2, doubleW paramdoubleW, int[] paramArrayOfInt, double[] paramArrayOfDouble3, intW paramintW2);
/* 97:   */   
/* 98:   */   public abstract void dgetv0(intW paramintW1, String paramString, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, double[] paramArrayOfDouble1, int paramInt4, int paramInt5, double[] paramArrayOfDouble2, int paramInt6, doubleW paramdoubleW, int[] paramArrayOfInt, int paramInt7, double[] paramArrayOfDouble3, int paramInt8, intW paramintW2);
/* 99:   */   
/* :0:   */   public abstract void dlaqrb(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, double[] paramArrayOfDouble1, int paramInt4, double[] paramArrayOfDouble2, double[] paramArrayOfDouble3, double[] paramArrayOfDouble4, intW paramintW);
/* :1:   */   
/* :2:   */   public abstract void dlaqrb(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, double[] paramArrayOfDouble1, int paramInt4, int paramInt5, double[] paramArrayOfDouble2, int paramInt6, double[] paramArrayOfDouble3, int paramInt7, double[] paramArrayOfDouble4, int paramInt8, intW paramintW);
/* :3:   */   
/* :4:   */   public abstract void dnaitr(intW paramintW1, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[] paramArrayOfDouble1, doubleW paramdoubleW, double[] paramArrayOfDouble2, int paramInt5, double[] paramArrayOfDouble3, int paramInt6, int[] paramArrayOfInt, double[] paramArrayOfDouble4, intW paramintW2);
/* :5:   */   
/* :6:   */   public abstract void dnaitr(intW paramintW1, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[] paramArrayOfDouble1, int paramInt5, doubleW paramdoubleW, double[] paramArrayOfDouble2, int paramInt6, int paramInt7, double[] paramArrayOfDouble3, int paramInt8, int paramInt9, int[] paramArrayOfInt, int paramInt10, double[] paramArrayOfDouble4, int paramInt11, intW paramintW2);
/* :7:   */   
/* :8:   */   public abstract void dnapps(int paramInt1, intW paramintW, int paramInt2, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double[] paramArrayOfDouble3, int paramInt3, double[] paramArrayOfDouble4, int paramInt4, double[] paramArrayOfDouble5, double[] paramArrayOfDouble6, int paramInt5, double[] paramArrayOfDouble7, double[] paramArrayOfDouble8);
/* :9:   */   
/* ;0:   */   public abstract void dnapps(int paramInt1, intW paramintW, int paramInt2, double[] paramArrayOfDouble1, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, double[] paramArrayOfDouble3, int paramInt5, int paramInt6, double[] paramArrayOfDouble4, int paramInt7, int paramInt8, double[] paramArrayOfDouble5, int paramInt9, double[] paramArrayOfDouble6, int paramInt10, int paramInt11, double[] paramArrayOfDouble7, int paramInt12, double[] paramArrayOfDouble8, int paramInt13);
/* ;1:   */   
/* ;2:   */   public abstract void dnaup2(intW paramintW1, String paramString1, int paramInt1, String paramString2, intW paramintW2, intW paramintW3, double paramDouble, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, int paramInt4, intW paramintW4, double[] paramArrayOfDouble2, int paramInt5, double[] paramArrayOfDouble3, int paramInt6, double[] paramArrayOfDouble4, double[] paramArrayOfDouble5, double[] paramArrayOfDouble6, double[] paramArrayOfDouble7, int paramInt7, double[] paramArrayOfDouble8, int[] paramArrayOfInt, double[] paramArrayOfDouble9, intW paramintW5);
/* ;3:   */   
/* ;4:   */   public abstract void dnaup2(intW paramintW1, String paramString1, int paramInt1, String paramString2, intW paramintW2, intW paramintW3, double paramDouble, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, intW paramintW4, double[] paramArrayOfDouble2, int paramInt6, int paramInt7, double[] paramArrayOfDouble3, int paramInt8, int paramInt9, double[] paramArrayOfDouble4, int paramInt10, double[] paramArrayOfDouble5, int paramInt11, double[] paramArrayOfDouble6, int paramInt12, double[] paramArrayOfDouble7, int paramInt13, int paramInt14, double[] paramArrayOfDouble8, int paramInt15, int[] paramArrayOfInt, int paramInt16, double[] paramArrayOfDouble9, int paramInt17, intW paramintW5);
/* ;5:   */   
/* ;6:   */   public abstract void dnaupd(intW paramintW1, String paramString1, int paramInt1, String paramString2, int paramInt2, doubleW paramdoubleW, double[] paramArrayOfDouble1, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, int[] paramArrayOfInt1, int[] paramArrayOfInt2, double[] paramArrayOfDouble3, double[] paramArrayOfDouble4, int paramInt5, intW paramintW2);
/* ;7:   */   
/* ;8:   */   public abstract void dnaupd(intW paramintW1, String paramString1, int paramInt1, String paramString2, int paramInt2, doubleW paramdoubleW, double[] paramArrayOfDouble1, int paramInt3, int paramInt4, double[] paramArrayOfDouble2, int paramInt5, int paramInt6, int[] paramArrayOfInt1, int paramInt7, int[] paramArrayOfInt2, int paramInt8, double[] paramArrayOfDouble3, int paramInt9, double[] paramArrayOfDouble4, int paramInt10, int paramInt11, intW paramintW2);
/* ;9:   */   
/* <0:   */   public abstract void dnconv(int paramInt, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double[] paramArrayOfDouble3, double paramDouble, intW paramintW);
/* <1:   */   
/* <2:   */   public abstract void dnconv(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, double[] paramArrayOfDouble3, int paramInt4, double paramDouble, intW paramintW);
/* <3:   */   
/* <4:   */   public abstract void dneigh(double paramDouble, intW paramintW1, double[] paramArrayOfDouble1, int paramInt1, double[] paramArrayOfDouble2, double[] paramArrayOfDouble3, double[] paramArrayOfDouble4, double[] paramArrayOfDouble5, int paramInt2, double[] paramArrayOfDouble6, intW paramintW2);
/* <5:   */   
/* <6:   */   public abstract void dneigh(double paramDouble, intW paramintW1, double[] paramArrayOfDouble1, int paramInt1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, double[] paramArrayOfDouble3, int paramInt4, double[] paramArrayOfDouble4, int paramInt5, double[] paramArrayOfDouble5, int paramInt6, int paramInt7, double[] paramArrayOfDouble6, int paramInt8, intW paramintW2);
/* <7:   */   
/* <8:   */   public abstract void dneupd(boolean paramBoolean, String paramString1, boolean[] paramArrayOfBoolean, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double[] paramArrayOfDouble3, int paramInt1, double paramDouble1, double paramDouble2, double[] paramArrayOfDouble4, String paramString2, int paramInt2, String paramString3, intW paramintW1, double paramDouble3, double[] paramArrayOfDouble5, int paramInt3, double[] paramArrayOfDouble6, int paramInt4, int[] paramArrayOfInt1, int[] paramArrayOfInt2, double[] paramArrayOfDouble7, double[] paramArrayOfDouble8, int paramInt5, intW paramintW2);
/* <9:   */   
/* =0:   */   public abstract void dneupd(boolean paramBoolean, String paramString1, boolean[] paramArrayOfBoolean, int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, double[] paramArrayOfDouble3, int paramInt4, int paramInt5, double paramDouble1, double paramDouble2, double[] paramArrayOfDouble4, int paramInt6, String paramString2, int paramInt7, String paramString3, intW paramintW1, double paramDouble3, double[] paramArrayOfDouble5, int paramInt8, int paramInt9, double[] paramArrayOfDouble6, int paramInt10, int paramInt11, int[] paramArrayOfInt1, int paramInt12, int[] paramArrayOfInt2, int paramInt13, double[] paramArrayOfDouble7, int paramInt14, double[] paramArrayOfDouble8, int paramInt15, int paramInt16, intW paramintW2);
/* =1:   */   
/* =2:   */   public abstract void dngets(int paramInt, String paramString, intW paramintW1, intW paramintW2, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double[] paramArrayOfDouble3, double[] paramArrayOfDouble4, double[] paramArrayOfDouble5);
/* =3:   */   
/* =4:   */   public abstract void dngets(int paramInt1, String paramString, intW paramintW1, intW paramintW2, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, double[] paramArrayOfDouble3, int paramInt4, double[] paramArrayOfDouble4, int paramInt5, double[] paramArrayOfDouble5, int paramInt6);
/* =5:   */   
/* =6:   */   public abstract void dsaitr(intW paramintW1, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[] paramArrayOfDouble1, doubleW paramdoubleW, double[] paramArrayOfDouble2, int paramInt5, double[] paramArrayOfDouble3, int paramInt6, int[] paramArrayOfInt, double[] paramArrayOfDouble4, intW paramintW2);
/* =7:   */   
/* =8:   */   public abstract void dsaitr(intW paramintW1, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[] paramArrayOfDouble1, int paramInt5, doubleW paramdoubleW, double[] paramArrayOfDouble2, int paramInt6, int paramInt7, double[] paramArrayOfDouble3, int paramInt8, int paramInt9, int[] paramArrayOfInt, int paramInt10, double[] paramArrayOfDouble4, int paramInt11, intW paramintW2);
/* =9:   */   
/* >0:   */   public abstract void dsapps(int paramInt1, int paramInt2, int paramInt3, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int paramInt4, double[] paramArrayOfDouble3, int paramInt5, double[] paramArrayOfDouble4, double[] paramArrayOfDouble5, int paramInt6, double[] paramArrayOfDouble6);
/* >1:   */   
/* >2:   */   public abstract void dsapps(int paramInt1, int paramInt2, int paramInt3, double[] paramArrayOfDouble1, int paramInt4, double[] paramArrayOfDouble2, int paramInt5, int paramInt6, double[] paramArrayOfDouble3, int paramInt7, int paramInt8, double[] paramArrayOfDouble4, int paramInt9, double[] paramArrayOfDouble5, int paramInt10, int paramInt11, double[] paramArrayOfDouble6, int paramInt12);
/* >3:   */   
/* >4:   */   public abstract void dsaup2(intW paramintW1, String paramString1, int paramInt1, String paramString2, intW paramintW2, intW paramintW3, double paramDouble, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, int paramInt4, intW paramintW4, double[] paramArrayOfDouble2, int paramInt5, double[] paramArrayOfDouble3, int paramInt6, double[] paramArrayOfDouble4, double[] paramArrayOfDouble5, double[] paramArrayOfDouble6, int paramInt7, double[] paramArrayOfDouble7, int[] paramArrayOfInt, double[] paramArrayOfDouble8, intW paramintW5);
/* >5:   */   
/* >6:   */   public abstract void dsaup2(intW paramintW1, String paramString1, int paramInt1, String paramString2, intW paramintW2, intW paramintW3, double paramDouble, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, intW paramintW4, double[] paramArrayOfDouble2, int paramInt6, int paramInt7, double[] paramArrayOfDouble3, int paramInt8, int paramInt9, double[] paramArrayOfDouble4, int paramInt10, double[] paramArrayOfDouble5, int paramInt11, double[] paramArrayOfDouble6, int paramInt12, int paramInt13, double[] paramArrayOfDouble7, int paramInt14, int[] paramArrayOfInt, int paramInt15, double[] paramArrayOfDouble8, int paramInt16, intW paramintW5);
/* >7:   */   
/* >8:   */   public abstract void dsaupd(intW paramintW1, String paramString1, int paramInt1, String paramString2, int paramInt2, doubleW paramdoubleW, double[] paramArrayOfDouble1, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, int[] paramArrayOfInt1, int[] paramArrayOfInt2, double[] paramArrayOfDouble3, double[] paramArrayOfDouble4, int paramInt5, intW paramintW2);
/* >9:   */   
/* ?0:   */   public abstract void dsaupd(intW paramintW1, String paramString1, int paramInt1, String paramString2, int paramInt2, doubleW paramdoubleW, double[] paramArrayOfDouble1, int paramInt3, int paramInt4, double[] paramArrayOfDouble2, int paramInt5, int paramInt6, int[] paramArrayOfInt1, int paramInt7, int[] paramArrayOfInt2, int paramInt8, double[] paramArrayOfDouble3, int paramInt9, double[] paramArrayOfDouble4, int paramInt10, int paramInt11, intW paramintW2);
/* ?1:   */   
/* ?2:   */   public abstract void dsconv(int paramInt, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double paramDouble, intW paramintW);
/* ?3:   */   
/* ?4:   */   public abstract void dsconv(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, double paramDouble, intW paramintW);
/* ?5:   */   
/* ?6:   */   public abstract void dseigt(double paramDouble, int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, double[] paramArrayOfDouble3, double[] paramArrayOfDouble4, intW paramintW);
/* ?7:   */   
/* ?8:   */   public abstract void dseigt(double paramDouble, int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, double[] paramArrayOfDouble3, int paramInt5, double[] paramArrayOfDouble4, int paramInt6, intW paramintW);
/* ?9:   */   
/* @0:   */   public abstract void dsesrt(String paramString, boolean paramBoolean, int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3);
/* @1:   */   
/* @2:   */   public abstract void dsesrt(String paramString, boolean paramBoolean, int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, int paramInt5);
/* @3:   */   
/* @4:   */   public abstract void dseupd(boolean paramBoolean, String paramString1, boolean[] paramArrayOfBoolean, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int paramInt1, double paramDouble1, String paramString2, int paramInt2, String paramString3, intW paramintW1, double paramDouble2, double[] paramArrayOfDouble3, int paramInt3, double[] paramArrayOfDouble4, int paramInt4, int[] paramArrayOfInt1, int[] paramArrayOfInt2, double[] paramArrayOfDouble5, double[] paramArrayOfDouble6, int paramInt5, intW paramintW2);
/* @5:   */   
/* @6:   */   public abstract void dseupd(boolean paramBoolean, String paramString1, boolean[] paramArrayOfBoolean, int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, int paramInt4, double paramDouble1, String paramString2, int paramInt5, String paramString3, intW paramintW1, double paramDouble2, double[] paramArrayOfDouble3, int paramInt6, int paramInt7, double[] paramArrayOfDouble4, int paramInt8, int paramInt9, int[] paramArrayOfInt1, int paramInt10, int[] paramArrayOfInt2, int paramInt11, double[] paramArrayOfDouble5, int paramInt12, double[] paramArrayOfDouble6, int paramInt13, int paramInt14, intW paramintW2);
/* @7:   */   
/* @8:   */   public abstract void dsgets(int paramInt, String paramString, intW paramintW1, intW paramintW2, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double[] paramArrayOfDouble3);
/* @9:   */   
/* A0:   */   public abstract void dsgets(int paramInt1, String paramString, intW paramintW1, intW paramintW2, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, double[] paramArrayOfDouble3, int paramInt4);
/* A1:   */   
/* A2:   */   public abstract void dsortc(String paramString, boolean paramBoolean, int paramInt, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double[] paramArrayOfDouble3);
/* A3:   */   
/* A4:   */   public abstract void dsortc(String paramString, boolean paramBoolean, int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, double[] paramArrayOfDouble3, int paramInt4);
/* A5:   */   
/* A6:   */   public abstract void dsortr(String paramString, boolean paramBoolean, int paramInt, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2);
/* A7:   */   
/* A8:   */   public abstract void dsortr(String paramString, boolean paramBoolean, int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3);
/* A9:   */   
/* B0:   */   public abstract void dstatn();
/* B1:   */   
/* B2:   */   public abstract void dstats();
/* B3:   */   
/* B4:   */   public abstract void dstqrb(int paramInt, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double[] paramArrayOfDouble3, double[] paramArrayOfDouble4, intW paramintW);
/* B5:   */   
/* B6:   */   public abstract void dstqrb(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, double[] paramArrayOfDouble3, int paramInt4, double[] paramArrayOfDouble4, int paramInt5, intW paramintW);
/* B7:   */   
/* B8:   */   public abstract void sgetv0(intW paramintW1, String paramString, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, float[] paramArrayOfFloat1, int paramInt4, float[] paramArrayOfFloat2, floatW paramfloatW, int[] paramArrayOfInt, float[] paramArrayOfFloat3, intW paramintW2);
/* B9:   */   
/* C0:   */   public abstract void sgetv0(intW paramintW1, String paramString, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, float[] paramArrayOfFloat1, int paramInt4, int paramInt5, float[] paramArrayOfFloat2, int paramInt6, floatW paramfloatW, int[] paramArrayOfInt, int paramInt7, float[] paramArrayOfFloat3, int paramInt8, intW paramintW2);
/* C1:   */   
/* C2:   */   public abstract void slaqrb(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat1, int paramInt4, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3, float[] paramArrayOfFloat4, intW paramintW);
/* C3:   */   
/* C4:   */   public abstract void slaqrb(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat1, int paramInt4, int paramInt5, float[] paramArrayOfFloat2, int paramInt6, float[] paramArrayOfFloat3, int paramInt7, float[] paramArrayOfFloat4, int paramInt8, intW paramintW);
/* C5:   */   
/* C6:   */   public abstract void snaitr(intW paramintW1, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOfFloat1, floatW paramfloatW, float[] paramArrayOfFloat2, int paramInt5, float[] paramArrayOfFloat3, int paramInt6, int[] paramArrayOfInt, float[] paramArrayOfFloat4, intW paramintW2);
/* C7:   */   
/* C8:   */   public abstract void snaitr(intW paramintW1, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOfFloat1, int paramInt5, floatW paramfloatW, float[] paramArrayOfFloat2, int paramInt6, int paramInt7, float[] paramArrayOfFloat3, int paramInt8, int paramInt9, int[] paramArrayOfInt, int paramInt10, float[] paramArrayOfFloat4, int paramInt11, intW paramintW2);
/* C9:   */   
/* D0:   */   public abstract void snapps(int paramInt1, intW paramintW, int paramInt2, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3, int paramInt3, float[] paramArrayOfFloat4, int paramInt4, float[] paramArrayOfFloat5, float[] paramArrayOfFloat6, int paramInt5, float[] paramArrayOfFloat7, float[] paramArrayOfFloat8);
/* D1:   */   
/* D2:   */   public abstract void snapps(int paramInt1, intW paramintW, int paramInt2, float[] paramArrayOfFloat1, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, float[] paramArrayOfFloat3, int paramInt5, int paramInt6, float[] paramArrayOfFloat4, int paramInt7, int paramInt8, float[] paramArrayOfFloat5, int paramInt9, float[] paramArrayOfFloat6, int paramInt10, int paramInt11, float[] paramArrayOfFloat7, int paramInt12, float[] paramArrayOfFloat8, int paramInt13);
/* D3:   */   
/* D4:   */   public abstract void snaup2(intW paramintW1, String paramString1, int paramInt1, String paramString2, intW paramintW2, intW paramintW3, float paramFloat, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, int paramInt4, intW paramintW4, float[] paramArrayOfFloat2, int paramInt5, float[] paramArrayOfFloat3, int paramInt6, float[] paramArrayOfFloat4, float[] paramArrayOfFloat5, float[] paramArrayOfFloat6, float[] paramArrayOfFloat7, int paramInt7, float[] paramArrayOfFloat8, int[] paramArrayOfInt, float[] paramArrayOfFloat9, intW paramintW5);
/* D5:   */   
/* D6:   */   public abstract void snaup2(intW paramintW1, String paramString1, int paramInt1, String paramString2, intW paramintW2, intW paramintW3, float paramFloat, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, intW paramintW4, float[] paramArrayOfFloat2, int paramInt6, int paramInt7, float[] paramArrayOfFloat3, int paramInt8, int paramInt9, float[] paramArrayOfFloat4, int paramInt10, float[] paramArrayOfFloat5, int paramInt11, float[] paramArrayOfFloat6, int paramInt12, float[] paramArrayOfFloat7, int paramInt13, int paramInt14, float[] paramArrayOfFloat8, int paramInt15, int[] paramArrayOfInt, int paramInt16, float[] paramArrayOfFloat9, int paramInt17, intW paramintW5);
/* D7:   */   
/* D8:   */   public abstract void snaupd(intW paramintW1, String paramString1, int paramInt1, String paramString2, int paramInt2, floatW paramfloatW, float[] paramArrayOfFloat1, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, int[] paramArrayOfInt1, int[] paramArrayOfInt2, float[] paramArrayOfFloat3, float[] paramArrayOfFloat4, int paramInt5, intW paramintW2);
/* D9:   */   
/* E0:   */   public abstract void snaupd(intW paramintW1, String paramString1, int paramInt1, String paramString2, int paramInt2, floatW paramfloatW, float[] paramArrayOfFloat1, int paramInt3, int paramInt4, float[] paramArrayOfFloat2, int paramInt5, int paramInt6, int[] paramArrayOfInt1, int paramInt7, int[] paramArrayOfInt2, int paramInt8, float[] paramArrayOfFloat3, int paramInt9, float[] paramArrayOfFloat4, int paramInt10, int paramInt11, intW paramintW2);
/* E1:   */   
/* E2:   */   public abstract void snconv(int paramInt, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3, float paramFloat, intW paramintW);
/* E3:   */   
/* E4:   */   public abstract void snconv(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, float[] paramArrayOfFloat3, int paramInt4, float paramFloat, intW paramintW);
/* E5:   */   
/* E6:   */   public abstract void sneigh(float paramFloat, intW paramintW1, float[] paramArrayOfFloat1, int paramInt1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3, float[] paramArrayOfFloat4, float[] paramArrayOfFloat5, int paramInt2, float[] paramArrayOfFloat6, intW paramintW2);
/* E7:   */   
/* E8:   */   public abstract void sneigh(float paramFloat, intW paramintW1, float[] paramArrayOfFloat1, int paramInt1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, float[] paramArrayOfFloat3, int paramInt4, float[] paramArrayOfFloat4, int paramInt5, float[] paramArrayOfFloat5, int paramInt6, int paramInt7, float[] paramArrayOfFloat6, int paramInt8, intW paramintW2);
/* E9:   */   
/* F0:   */   public abstract void sneupd(boolean paramBoolean, String paramString1, boolean[] paramArrayOfBoolean, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3, int paramInt1, float paramFloat1, float paramFloat2, float[] paramArrayOfFloat4, String paramString2, int paramInt2, String paramString3, intW paramintW1, float paramFloat3, float[] paramArrayOfFloat5, int paramInt3, float[] paramArrayOfFloat6, int paramInt4, int[] paramArrayOfInt1, int[] paramArrayOfInt2, float[] paramArrayOfFloat7, float[] paramArrayOfFloat8, int paramInt5, intW paramintW2);
/* F1:   */   
/* F2:   */   public abstract void sneupd(boolean paramBoolean, String paramString1, boolean[] paramArrayOfBoolean, int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, float[] paramArrayOfFloat3, int paramInt4, int paramInt5, float paramFloat1, float paramFloat2, float[] paramArrayOfFloat4, int paramInt6, String paramString2, int paramInt7, String paramString3, intW paramintW1, float paramFloat3, float[] paramArrayOfFloat5, int paramInt8, int paramInt9, float[] paramArrayOfFloat6, int paramInt10, int paramInt11, int[] paramArrayOfInt1, int paramInt12, int[] paramArrayOfInt2, int paramInt13, float[] paramArrayOfFloat7, int paramInt14, float[] paramArrayOfFloat8, int paramInt15, int paramInt16, intW paramintW2);
/* F3:   */   
/* F4:   */   public abstract void sngets(int paramInt, String paramString, intW paramintW1, intW paramintW2, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3, float[] paramArrayOfFloat4, float[] paramArrayOfFloat5);
/* F5:   */   
/* F6:   */   public abstract void sngets(int paramInt1, String paramString, intW paramintW1, intW paramintW2, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, float[] paramArrayOfFloat3, int paramInt4, float[] paramArrayOfFloat4, int paramInt5, float[] paramArrayOfFloat5, int paramInt6);
/* F7:   */   
/* F8:   */   public abstract void ssaitr(intW paramintW1, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOfFloat1, floatW paramfloatW, float[] paramArrayOfFloat2, int paramInt5, float[] paramArrayOfFloat3, int paramInt6, int[] paramArrayOfInt, float[] paramArrayOfFloat4, intW paramintW2);
/* F9:   */   
/* G0:   */   public abstract void ssaitr(intW paramintW1, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOfFloat1, int paramInt5, floatW paramfloatW, float[] paramArrayOfFloat2, int paramInt6, int paramInt7, float[] paramArrayOfFloat3, int paramInt8, int paramInt9, int[] paramArrayOfInt, int paramInt10, float[] paramArrayOfFloat4, int paramInt11, intW paramintW2);
/* G1:   */   
/* G2:   */   public abstract void ssapps(int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int paramInt4, float[] paramArrayOfFloat3, int paramInt5, float[] paramArrayOfFloat4, float[] paramArrayOfFloat5, int paramInt6, float[] paramArrayOfFloat6);
/* G3:   */   
/* G4:   */   public abstract void ssapps(int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat1, int paramInt4, float[] paramArrayOfFloat2, int paramInt5, int paramInt6, float[] paramArrayOfFloat3, int paramInt7, int paramInt8, float[] paramArrayOfFloat4, int paramInt9, float[] paramArrayOfFloat5, int paramInt10, int paramInt11, float[] paramArrayOfFloat6, int paramInt12);
/* G5:   */   
/* G6:   */   public abstract void ssaup2(intW paramintW1, String paramString1, int paramInt1, String paramString2, intW paramintW2, intW paramintW3, float paramFloat, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, int paramInt4, intW paramintW4, float[] paramArrayOfFloat2, int paramInt5, float[] paramArrayOfFloat3, int paramInt6, float[] paramArrayOfFloat4, float[] paramArrayOfFloat5, float[] paramArrayOfFloat6, int paramInt7, float[] paramArrayOfFloat7, int[] paramArrayOfInt, float[] paramArrayOfFloat8, intW paramintW5);
/* G7:   */   
/* G8:   */   public abstract void ssaup2(intW paramintW1, String paramString1, int paramInt1, String paramString2, intW paramintW2, intW paramintW3, float paramFloat, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, intW paramintW4, float[] paramArrayOfFloat2, int paramInt6, int paramInt7, float[] paramArrayOfFloat3, int paramInt8, int paramInt9, float[] paramArrayOfFloat4, int paramInt10, float[] paramArrayOfFloat5, int paramInt11, float[] paramArrayOfFloat6, int paramInt12, int paramInt13, float[] paramArrayOfFloat7, int paramInt14, int[] paramArrayOfInt, int paramInt15, float[] paramArrayOfFloat8, int paramInt16, intW paramintW5);
/* G9:   */   
/* H0:   */   public abstract void ssaupd(intW paramintW1, String paramString1, int paramInt1, String paramString2, int paramInt2, floatW paramfloatW, float[] paramArrayOfFloat1, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, int[] paramArrayOfInt1, int[] paramArrayOfInt2, float[] paramArrayOfFloat3, float[] paramArrayOfFloat4, int paramInt5, intW paramintW2);
/* H1:   */   
/* H2:   */   public abstract void ssaupd(intW paramintW1, String paramString1, int paramInt1, String paramString2, int paramInt2, floatW paramfloatW, float[] paramArrayOfFloat1, int paramInt3, int paramInt4, float[] paramArrayOfFloat2, int paramInt5, int paramInt6, int[] paramArrayOfInt1, int paramInt7, int[] paramArrayOfInt2, int paramInt8, float[] paramArrayOfFloat3, int paramInt9, float[] paramArrayOfFloat4, int paramInt10, int paramInt11, intW paramintW2);
/* H3:   */   
/* H4:   */   public abstract void ssconv(int paramInt, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float paramFloat, intW paramintW);
/* H5:   */   
/* H6:   */   public abstract void ssconv(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, float paramFloat, intW paramintW);
/* H7:   */   
/* H8:   */   public abstract void sseigt(float paramFloat, int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3, float[] paramArrayOfFloat4, intW paramintW);
/* H9:   */   
/* I0:   */   public abstract void sseigt(float paramFloat, int paramInt1, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, float[] paramArrayOfFloat3, int paramInt5, float[] paramArrayOfFloat4, int paramInt6, intW paramintW);
/* I1:   */   
/* I2:   */   public abstract void ssesrt(String paramString, boolean paramBoolean, int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3);
/* I3:   */   
/* I4:   */   public abstract void ssesrt(String paramString, boolean paramBoolean, int paramInt1, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, int paramInt5);
/* I5:   */   
/* I6:   */   public abstract void sseupd(boolean paramBoolean, String paramString1, boolean[] paramArrayOfBoolean, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int paramInt1, float paramFloat1, String paramString2, int paramInt2, String paramString3, intW paramintW1, float paramFloat2, float[] paramArrayOfFloat3, int paramInt3, float[] paramArrayOfFloat4, int paramInt4, int[] paramArrayOfInt1, int[] paramArrayOfInt2, float[] paramArrayOfFloat5, float[] paramArrayOfFloat6, int paramInt5, intW paramintW2);
/* I7:   */   
/* I8:   */   public abstract void sseupd(boolean paramBoolean, String paramString1, boolean[] paramArrayOfBoolean, int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, int paramInt4, float paramFloat1, String paramString2, int paramInt5, String paramString3, intW paramintW1, float paramFloat2, float[] paramArrayOfFloat3, int paramInt6, int paramInt7, float[] paramArrayOfFloat4, int paramInt8, int paramInt9, int[] paramArrayOfInt1, int paramInt10, int[] paramArrayOfInt2, int paramInt11, float[] paramArrayOfFloat5, int paramInt12, float[] paramArrayOfFloat6, int paramInt13, int paramInt14, intW paramintW2);
/* I9:   */   
/* J0:   */   public abstract void ssgets(int paramInt, String paramString, intW paramintW1, intW paramintW2, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3);
/* J1:   */   
/* J2:   */   public abstract void ssgets(int paramInt1, String paramString, intW paramintW1, intW paramintW2, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, float[] paramArrayOfFloat3, int paramInt4);
/* J3:   */   
/* J4:   */   public abstract void ssortc(String paramString, boolean paramBoolean, int paramInt, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3);
/* J5:   */   
/* J6:   */   public abstract void ssortc(String paramString, boolean paramBoolean, int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, float[] paramArrayOfFloat3, int paramInt4);
/* J7:   */   
/* J8:   */   public abstract void ssortr(String paramString, boolean paramBoolean, int paramInt, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2);
/* J9:   */   
/* K0:   */   public abstract void ssortr(String paramString, boolean paramBoolean, int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3);
/* K1:   */   
/* K2:   */   public abstract void sstatn();
/* K3:   */   
/* K4:   */   public abstract void sstats();
/* K5:   */   
/* K6:   */   public abstract void sstqrb(int paramInt, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, float[] paramArrayOfFloat3, float[] paramArrayOfFloat4, intW paramintW);
/* K7:   */   
/* K8:   */   public abstract void sstqrb(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, float[] paramArrayOfFloat3, int paramInt4, float[] paramArrayOfFloat4, int paramInt5, intW paramintW);
/* K9:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     com.github.fommil.netlib.ARPACK
 * JD-Core Version:    0.7.0.1
 */