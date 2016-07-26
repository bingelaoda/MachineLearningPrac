/*  1:   */ package com.github.fommil.netlib;
/*  2:   */ 
/*  3:   */ import java.util.logging.Logger;
/*  4:   */ import org.netlib.util.doubleW;
/*  5:   */ import org.netlib.util.floatW;
/*  6:   */ 
/*  7:   */ public abstract class BLAS
/*  8:   */ {
/*  9:45 */   private static final Logger log = Logger.getLogger(BLAS.class.getName());
/* 10:   */   private static final String FALLBACK = "com.github.fommil.netlib.F2jBLAS";
/* 11:   */   private static final String IMPLS = "com.github.fommil.netlib.NativeSystemBLAS,com.github.fommil.netlib.NativeRefBLAS,com.github.fommil.netlib.F2jBLAS";
/* 12:   */   private static final String PROPERTY_KEY = "com.github.fommil.netlib.BLAS";
/* 13:   */   private static final BLAS INSTANCE;
/* 14:   */   
/* 15:   */   static
/* 16:   */   {
/* 17:   */     try
/* 18:   */     {
/* 19:54 */       String[] classNames = System.getProperty("com.github.fommil.netlib.BLAS", "com.github.fommil.netlib.NativeSystemBLAS,com.github.fommil.netlib.NativeRefBLAS,com.github.fommil.netlib.F2jBLAS").split(",");
/* 20:55 */       BLAS impl = null;
/* 21:56 */       for (String className : classNames) {
/* 22:   */         try
/* 23:   */         {
/* 24:58 */           impl = load(className);
/* 25:   */         }
/* 26:   */         catch (Throwable e)
/* 27:   */         {
/* 28:61 */           log.warning("Failed to load implementation from: " + className);
/* 29:   */         }
/* 30:   */       }
/* 31:64 */       if (impl == null)
/* 32:   */       {
/* 33:65 */         log.warning("Using the fallback implementation.");
/* 34:66 */         impl = load("com.github.fommil.netlib.F2jBLAS");
/* 35:   */       }
/* 36:68 */       INSTANCE = impl;
/* 37:69 */       log.config("Implementation provided by " + INSTANCE.getClass());
/* 38:   */     }
/* 39:   */     catch (Exception e)
/* 40:   */     {
/* 41:71 */       throw new ExceptionInInitializerError(e);
/* 42:   */     }
/* 43:   */   }
/* 44:   */   
/* 45:   */   private static BLAS load(String className)
/* 46:   */     throws Exception
/* 47:   */   {
/* 48:76 */     Class klass = Class.forName(className);
/* 49:77 */     return (BLAS)klass.newInstance();
/* 50:   */   }
/* 51:   */   
/* 52:   */   public static BLAS getInstance()
/* 53:   */   {
/* 54:84 */     return INSTANCE;
/* 55:   */   }
/* 56:   */   
/* 57:   */   public abstract double dasum(int paramInt1, double[] paramArrayOfDouble, int paramInt2);
/* 58:   */   
/* 59:   */   public abstract double dasum(int paramInt1, double[] paramArrayOfDouble, int paramInt2, int paramInt3);
/* 60:   */   
/* 61:   */   public abstract void daxpy(int paramInt1, double paramDouble, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3);
/* 62:   */   
/* 63:   */   public abstract void daxpy(int paramInt1, double paramDouble, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, int paramInt5);
/* 64:   */   
/* 65:   */   public abstract void dcopy(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3);
/* 66:   */   
/* 67:   */   public abstract void dcopy(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, int paramInt5);
/* 68:   */   
/* 69:   */   public abstract double ddot(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3);
/* 70:   */   
/* 71:   */   public abstract double ddot(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, int paramInt5);
/* 72:   */   
/* 73:   */   public abstract void dgbmv(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double[] paramArrayOfDouble1, int paramInt5, double[] paramArrayOfDouble2, int paramInt6, double paramDouble2, double[] paramArrayOfDouble3, int paramInt7);
/* 74:   */   
/* 75:   */   public abstract void dgbmv(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, double paramDouble1, double[] paramArrayOfDouble1, int paramInt5, int paramInt6, double[] paramArrayOfDouble2, int paramInt7, int paramInt8, double paramDouble2, double[] paramArrayOfDouble3, int paramInt9, int paramInt10);
/* 76:   */   
/* 77:   */   public abstract void dgemm(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, double paramDouble1, double[] paramArrayOfDouble1, int paramInt4, double[] paramArrayOfDouble2, int paramInt5, double paramDouble2, double[] paramArrayOfDouble3, int paramInt6);
/* 78:   */   
/* 79:   */   public abstract void dgemm(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, double paramDouble1, double[] paramArrayOfDouble1, int paramInt4, int paramInt5, double[] paramArrayOfDouble2, int paramInt6, int paramInt7, double paramDouble2, double[] paramArrayOfDouble3, int paramInt8, int paramInt9);
/* 80:   */   
/* 81:   */   public abstract void dgemv(String paramString, int paramInt1, int paramInt2, double paramDouble1, double[] paramArrayOfDouble1, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, double paramDouble2, double[] paramArrayOfDouble3, int paramInt5);
/* 82:   */   
/* 83:   */   public abstract void dgemv(String paramString, int paramInt1, int paramInt2, double paramDouble1, double[] paramArrayOfDouble1, int paramInt3, int paramInt4, double[] paramArrayOfDouble2, int paramInt5, int paramInt6, double paramDouble2, double[] paramArrayOfDouble3, int paramInt7, int paramInt8);
/* 84:   */   
/* 85:   */   public abstract void dger(int paramInt1, int paramInt2, double paramDouble, double[] paramArrayOfDouble1, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, double[] paramArrayOfDouble3, int paramInt5);
/* 86:   */   
/* 87:   */   public abstract void dger(int paramInt1, int paramInt2, double paramDouble, double[] paramArrayOfDouble1, int paramInt3, int paramInt4, double[] paramArrayOfDouble2, int paramInt5, int paramInt6, double[] paramArrayOfDouble3, int paramInt7, int paramInt8);
/* 88:   */   
/* 89:   */   public abstract double dnrm2(int paramInt1, double[] paramArrayOfDouble, int paramInt2);
/* 90:   */   
/* 91:   */   public abstract double dnrm2(int paramInt1, double[] paramArrayOfDouble, int paramInt2, int paramInt3);
/* 92:   */   
/* 93:   */   public abstract void drot(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, double paramDouble1, double paramDouble2);
/* 94:   */   
/* 95:   */   public abstract void drot(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, int paramInt5, double paramDouble1, double paramDouble2);
/* 96:   */   
/* 97:   */   public abstract void drotg(doubleW paramdoubleW1, doubleW paramdoubleW2, doubleW paramdoubleW3, doubleW paramdoubleW4);
/* 98:   */   
/* 99:   */   public abstract void drotm(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, double[] paramArrayOfDouble3);
/* :0:   */   
/* :1:   */   public abstract void drotm(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, int paramInt5, double[] paramArrayOfDouble3, int paramInt6);
/* :2:   */   
/* :3:   */   public abstract void drotmg(doubleW paramdoubleW1, doubleW paramdoubleW2, doubleW paramdoubleW3, double paramDouble, double[] paramArrayOfDouble);
/* :4:   */   
/* :5:   */   public abstract void drotmg(doubleW paramdoubleW1, doubleW paramdoubleW2, doubleW paramdoubleW3, double paramDouble, double[] paramArrayOfDouble, int paramInt);
/* :6:   */   
/* :7:   */   public abstract void dsbmv(String paramString, int paramInt1, int paramInt2, double paramDouble1, double[] paramArrayOfDouble1, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, double paramDouble2, double[] paramArrayOfDouble3, int paramInt5);
/* :8:   */   
/* :9:   */   public abstract void dsbmv(String paramString, int paramInt1, int paramInt2, double paramDouble1, double[] paramArrayOfDouble1, int paramInt3, int paramInt4, double[] paramArrayOfDouble2, int paramInt5, int paramInt6, double paramDouble2, double[] paramArrayOfDouble3, int paramInt7, int paramInt8);
/* ;0:   */   
/* ;1:   */   public abstract void dscal(int paramInt1, double paramDouble, double[] paramArrayOfDouble, int paramInt2);
/* ;2:   */   
/* ;3:   */   public abstract void dscal(int paramInt1, double paramDouble, double[] paramArrayOfDouble, int paramInt2, int paramInt3);
/* ;4:   */   
/* ;5:   */   public abstract void dspmv(String paramString, int paramInt1, double paramDouble1, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int paramInt2, double paramDouble2, double[] paramArrayOfDouble3, int paramInt3);
/* ;6:   */   
/* ;7:   */   public abstract void dspmv(String paramString, int paramInt1, double paramDouble1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, int paramInt4, double paramDouble2, double[] paramArrayOfDouble3, int paramInt5, int paramInt6);
/* ;8:   */   
/* ;9:   */   public abstract void dspr(String paramString, int paramInt1, double paramDouble, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2);
/* <0:   */   
/* <1:   */   public abstract void dspr(String paramString, int paramInt1, double paramDouble, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4);
/* <2:   */   
/* <3:   */   public abstract void dspr2(String paramString, int paramInt1, double paramDouble, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, double[] paramArrayOfDouble3);
/* <4:   */   
/* <5:   */   public abstract void dspr2(String paramString, int paramInt1, double paramDouble, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, int paramInt5, double[] paramArrayOfDouble3, int paramInt6);
/* <6:   */   
/* <7:   */   public abstract void dswap(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3);
/* <8:   */   
/* <9:   */   public abstract void dswap(int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, int paramInt5);
/* =0:   */   
/* =1:   */   public abstract void dsymm(String paramString1, String paramString2, int paramInt1, int paramInt2, double paramDouble1, double[] paramArrayOfDouble1, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, double paramDouble2, double[] paramArrayOfDouble3, int paramInt5);
/* =2:   */   
/* =3:   */   public abstract void dsymm(String paramString1, String paramString2, int paramInt1, int paramInt2, double paramDouble1, double[] paramArrayOfDouble1, int paramInt3, int paramInt4, double[] paramArrayOfDouble2, int paramInt5, int paramInt6, double paramDouble2, double[] paramArrayOfDouble3, int paramInt7, int paramInt8);
/* =4:   */   
/* =5:   */   public abstract void dsymv(String paramString, int paramInt1, double paramDouble1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, double paramDouble2, double[] paramArrayOfDouble3, int paramInt4);
/* =6:   */   
/* =7:   */   public abstract void dsymv(String paramString, int paramInt1, double paramDouble1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, int paramInt5, double paramDouble2, double[] paramArrayOfDouble3, int paramInt6, int paramInt7);
/* =8:   */   
/* =9:   */   public abstract void dsyr(String paramString, int paramInt1, double paramDouble, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3);
/* >0:   */   
/* >1:   */   public abstract void dsyr(String paramString, int paramInt1, double paramDouble, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, int paramInt5);
/* >2:   */   
/* >3:   */   public abstract void dsyr2(String paramString, int paramInt1, double paramDouble, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, double[] paramArrayOfDouble3, int paramInt4);
/* >4:   */   
/* >5:   */   public abstract void dsyr2(String paramString, int paramInt1, double paramDouble, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, int paramInt5, double[] paramArrayOfDouble3, int paramInt6, int paramInt7);
/* >6:   */   
/* >7:   */   public abstract void dsyr2k(String paramString1, String paramString2, int paramInt1, int paramInt2, double paramDouble1, double[] paramArrayOfDouble1, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, double paramDouble2, double[] paramArrayOfDouble3, int paramInt5);
/* >8:   */   
/* >9:   */   public abstract void dsyr2k(String paramString1, String paramString2, int paramInt1, int paramInt2, double paramDouble1, double[] paramArrayOfDouble1, int paramInt3, int paramInt4, double[] paramArrayOfDouble2, int paramInt5, int paramInt6, double paramDouble2, double[] paramArrayOfDouble3, int paramInt7, int paramInt8);
/* ?0:   */   
/* ?1:   */   public abstract void dsyrk(String paramString1, String paramString2, int paramInt1, int paramInt2, double paramDouble1, double[] paramArrayOfDouble1, int paramInt3, double paramDouble2, double[] paramArrayOfDouble2, int paramInt4);
/* ?2:   */   
/* ?3:   */   public abstract void dsyrk(String paramString1, String paramString2, int paramInt1, int paramInt2, double paramDouble1, double[] paramArrayOfDouble1, int paramInt3, int paramInt4, double paramDouble2, double[] paramArrayOfDouble2, int paramInt5, int paramInt6);
/* ?4:   */   
/* ?5:   */   public abstract void dtbmv(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, double[] paramArrayOfDouble1, int paramInt3, double[] paramArrayOfDouble2, int paramInt4);
/* ?6:   */   
/* ?7:   */   public abstract void dtbmv(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, double[] paramArrayOfDouble1, int paramInt3, int paramInt4, double[] paramArrayOfDouble2, int paramInt5, int paramInt6);
/* ?8:   */   
/* ?9:   */   public abstract void dtbsv(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, double[] paramArrayOfDouble1, int paramInt3, double[] paramArrayOfDouble2, int paramInt4);
/* @0:   */   
/* @1:   */   public abstract void dtbsv(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, double[] paramArrayOfDouble1, int paramInt3, int paramInt4, double[] paramArrayOfDouble2, int paramInt5, int paramInt6);
/* @2:   */   
/* @3:   */   public abstract void dtpmv(String paramString1, String paramString2, String paramString3, int paramInt1, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int paramInt2);
/* @4:   */   
/* @5:   */   public abstract void dtpmv(String paramString1, String paramString2, String paramString3, int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, int paramInt4);
/* @6:   */   
/* @7:   */   public abstract void dtpsv(String paramString1, String paramString2, String paramString3, int paramInt1, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int paramInt2);
/* @8:   */   
/* @9:   */   public abstract void dtpsv(String paramString1, String paramString2, String paramString3, int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3, int paramInt4);
/* A0:   */   
/* A1:   */   public abstract void dtrmm(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, double paramDouble, double[] paramArrayOfDouble1, int paramInt3, double[] paramArrayOfDouble2, int paramInt4);
/* A2:   */   
/* A3:   */   public abstract void dtrmm(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, double paramDouble, double[] paramArrayOfDouble1, int paramInt3, int paramInt4, double[] paramArrayOfDouble2, int paramInt5, int paramInt6);
/* A4:   */   
/* A5:   */   public abstract void dtrmv(String paramString1, String paramString2, String paramString3, int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3);
/* A6:   */   
/* A7:   */   public abstract void dtrmv(String paramString1, String paramString2, String paramString3, int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, int paramInt5);
/* A8:   */   
/* A9:   */   public abstract void dtrsm(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, double paramDouble, double[] paramArrayOfDouble1, int paramInt3, double[] paramArrayOfDouble2, int paramInt4);
/* B0:   */   
/* B1:   */   public abstract void dtrsm(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, double paramDouble, double[] paramArrayOfDouble1, int paramInt3, int paramInt4, double[] paramArrayOfDouble2, int paramInt5, int paramInt6);
/* B2:   */   
/* B3:   */   public abstract void dtrsv(String paramString1, String paramString2, String paramString3, int paramInt1, double[] paramArrayOfDouble1, int paramInt2, double[] paramArrayOfDouble2, int paramInt3);
/* B4:   */   
/* B5:   */   public abstract void dtrsv(String paramString1, String paramString2, String paramString3, int paramInt1, double[] paramArrayOfDouble1, int paramInt2, int paramInt3, double[] paramArrayOfDouble2, int paramInt4, int paramInt5);
/* B6:   */   
/* B7:   */   public abstract int idamax(int paramInt1, double[] paramArrayOfDouble, int paramInt2);
/* B8:   */   
/* B9:   */   public abstract int idamax(int paramInt1, double[] paramArrayOfDouble, int paramInt2, int paramInt3);
/* C0:   */   
/* C1:   */   public abstract int isamax(int paramInt1, float[] paramArrayOfFloat, int paramInt2);
/* C2:   */   
/* C3:   */   public abstract int isamax(int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3);
/* C4:   */   
/* C5:   */   public abstract boolean lsame(String paramString1, String paramString2);
/* C6:   */   
/* C7:   */   public abstract float sasum(int paramInt1, float[] paramArrayOfFloat, int paramInt2);
/* C8:   */   
/* C9:   */   public abstract float sasum(int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3);
/* D0:   */   
/* D1:   */   public abstract void saxpy(int paramInt1, float paramFloat, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3);
/* D2:   */   
/* D3:   */   public abstract void saxpy(int paramInt1, float paramFloat, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, int paramInt5);
/* D4:   */   
/* D5:   */   public abstract void scopy(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3);
/* D6:   */   
/* D7:   */   public abstract void scopy(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, int paramInt5);
/* D8:   */   
/* D9:   */   public abstract float sdot(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3);
/* E0:   */   
/* E1:   */   public abstract float sdot(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, int paramInt5);
/* E2:   */   
/* E3:   */   public abstract float sdsdot(int paramInt1, float paramFloat, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3);
/* E4:   */   
/* E5:   */   public abstract float sdsdot(int paramInt1, float paramFloat, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, int paramInt5);
/* E6:   */   
/* E7:   */   public abstract void sgbmv(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, float[] paramArrayOfFloat1, int paramInt5, float[] paramArrayOfFloat2, int paramInt6, float paramFloat2, float[] paramArrayOfFloat3, int paramInt7);
/* E8:   */   
/* E9:   */   public abstract void sgbmv(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, float[] paramArrayOfFloat1, int paramInt5, int paramInt6, float[] paramArrayOfFloat2, int paramInt7, int paramInt8, float paramFloat2, float[] paramArrayOfFloat3, int paramInt9, int paramInt10);
/* F0:   */   
/* F1:   */   public abstract void sgemm(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, float paramFloat1, float[] paramArrayOfFloat1, int paramInt4, float[] paramArrayOfFloat2, int paramInt5, float paramFloat2, float[] paramArrayOfFloat3, int paramInt6);
/* F2:   */   
/* F3:   */   public abstract void sgemm(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, float paramFloat1, float[] paramArrayOfFloat1, int paramInt4, int paramInt5, float[] paramArrayOfFloat2, int paramInt6, int paramInt7, float paramFloat2, float[] paramArrayOfFloat3, int paramInt8, int paramInt9);
/* F4:   */   
/* F5:   */   public abstract void sgemv(String paramString, int paramInt1, int paramInt2, float paramFloat1, float[] paramArrayOfFloat1, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, float paramFloat2, float[] paramArrayOfFloat3, int paramInt5);
/* F6:   */   
/* F7:   */   public abstract void sgemv(String paramString, int paramInt1, int paramInt2, float paramFloat1, float[] paramArrayOfFloat1, int paramInt3, int paramInt4, float[] paramArrayOfFloat2, int paramInt5, int paramInt6, float paramFloat2, float[] paramArrayOfFloat3, int paramInt7, int paramInt8);
/* F8:   */   
/* F9:   */   public abstract void sger(int paramInt1, int paramInt2, float paramFloat, float[] paramArrayOfFloat1, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, float[] paramArrayOfFloat3, int paramInt5);
/* G0:   */   
/* G1:   */   public abstract void sger(int paramInt1, int paramInt2, float paramFloat, float[] paramArrayOfFloat1, int paramInt3, int paramInt4, float[] paramArrayOfFloat2, int paramInt5, int paramInt6, float[] paramArrayOfFloat3, int paramInt7, int paramInt8);
/* G2:   */   
/* G3:   */   public abstract float snrm2(int paramInt1, float[] paramArrayOfFloat, int paramInt2);
/* G4:   */   
/* G5:   */   public abstract float snrm2(int paramInt1, float[] paramArrayOfFloat, int paramInt2, int paramInt3);
/* G6:   */   
/* G7:   */   public abstract void srot(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, float paramFloat1, float paramFloat2);
/* G8:   */   
/* G9:   */   public abstract void srot(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, int paramInt5, float paramFloat1, float paramFloat2);
/* H0:   */   
/* H1:   */   public abstract void srotg(floatW paramfloatW1, floatW paramfloatW2, floatW paramfloatW3, floatW paramfloatW4);
/* H2:   */   
/* H3:   */   public abstract void srotm(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, float[] paramArrayOfFloat3);
/* H4:   */   
/* H5:   */   public abstract void srotm(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, int paramInt5, float[] paramArrayOfFloat3, int paramInt6);
/* H6:   */   
/* H7:   */   public abstract void srotmg(floatW paramfloatW1, floatW paramfloatW2, floatW paramfloatW3, float paramFloat, float[] paramArrayOfFloat);
/* H8:   */   
/* H9:   */   public abstract void srotmg(floatW paramfloatW1, floatW paramfloatW2, floatW paramfloatW3, float paramFloat, float[] paramArrayOfFloat, int paramInt);
/* I0:   */   
/* I1:   */   public abstract void ssbmv(String paramString, int paramInt1, int paramInt2, float paramFloat1, float[] paramArrayOfFloat1, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, float paramFloat2, float[] paramArrayOfFloat3, int paramInt5);
/* I2:   */   
/* I3:   */   public abstract void ssbmv(String paramString, int paramInt1, int paramInt2, float paramFloat1, float[] paramArrayOfFloat1, int paramInt3, int paramInt4, float[] paramArrayOfFloat2, int paramInt5, int paramInt6, float paramFloat2, float[] paramArrayOfFloat3, int paramInt7, int paramInt8);
/* I4:   */   
/* I5:   */   public abstract void sscal(int paramInt1, float paramFloat, float[] paramArrayOfFloat, int paramInt2);
/* I6:   */   
/* I7:   */   public abstract void sscal(int paramInt1, float paramFloat, float[] paramArrayOfFloat, int paramInt2, int paramInt3);
/* I8:   */   
/* I9:   */   public abstract void sspmv(String paramString, int paramInt1, float paramFloat1, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int paramInt2, float paramFloat2, float[] paramArrayOfFloat3, int paramInt3);
/* J0:   */   
/* J1:   */   public abstract void sspmv(String paramString, int paramInt1, float paramFloat1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, int paramInt4, float paramFloat2, float[] paramArrayOfFloat3, int paramInt5, int paramInt6);
/* J2:   */   
/* J3:   */   public abstract void sspr(String paramString, int paramInt1, float paramFloat, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2);
/* J4:   */   
/* J5:   */   public abstract void sspr(String paramString, int paramInt1, float paramFloat, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2, int paramInt4);
/* J6:   */   
/* J7:   */   public abstract void sspr2(String paramString, int paramInt1, float paramFloat, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, float[] paramArrayOfFloat3);
/* J8:   */   
/* J9:   */   public abstract void sspr2(String paramString, int paramInt1, float paramFloat, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, int paramInt5, float[] paramArrayOfFloat3, int paramInt6);
/* K0:   */   
/* K1:   */   public abstract void sswap(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3);
/* K2:   */   
/* K3:   */   public abstract void sswap(int paramInt1, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, int paramInt5);
/* K4:   */   
/* K5:   */   public abstract void ssymm(String paramString1, String paramString2, int paramInt1, int paramInt2, float paramFloat1, float[] paramArrayOfFloat1, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, float paramFloat2, float[] paramArrayOfFloat3, int paramInt5);
/* K6:   */   
/* K7:   */   public abstract void ssymm(String paramString1, String paramString2, int paramInt1, int paramInt2, float paramFloat1, float[] paramArrayOfFloat1, int paramInt3, int paramInt4, float[] paramArrayOfFloat2, int paramInt5, int paramInt6, float paramFloat2, float[] paramArrayOfFloat3, int paramInt7, int paramInt8);
/* K8:   */   
/* K9:   */   public abstract void ssymv(String paramString, int paramInt1, float paramFloat1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, float paramFloat2, float[] paramArrayOfFloat3, int paramInt4);
/* L0:   */   
/* L1:   */   public abstract void ssymv(String paramString, int paramInt1, float paramFloat1, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, int paramInt5, float paramFloat2, float[] paramArrayOfFloat3, int paramInt6, int paramInt7);
/* L2:   */   
/* L3:   */   public abstract void ssyr(String paramString, int paramInt1, float paramFloat, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3);
/* L4:   */   
/* L5:   */   public abstract void ssyr(String paramString, int paramInt1, float paramFloat, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, int paramInt5);
/* L6:   */   
/* L7:   */   public abstract void ssyr2(String paramString, int paramInt1, float paramFloat, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, float[] paramArrayOfFloat3, int paramInt4);
/* L8:   */   
/* L9:   */   public abstract void ssyr2(String paramString, int paramInt1, float paramFloat, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, int paramInt5, float[] paramArrayOfFloat3, int paramInt6, int paramInt7);
/* M0:   */   
/* M1:   */   public abstract void ssyr2k(String paramString1, String paramString2, int paramInt1, int paramInt2, float paramFloat1, float[] paramArrayOfFloat1, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, float paramFloat2, float[] paramArrayOfFloat3, int paramInt5);
/* M2:   */   
/* M3:   */   public abstract void ssyr2k(String paramString1, String paramString2, int paramInt1, int paramInt2, float paramFloat1, float[] paramArrayOfFloat1, int paramInt3, int paramInt4, float[] paramArrayOfFloat2, int paramInt5, int paramInt6, float paramFloat2, float[] paramArrayOfFloat3, int paramInt7, int paramInt8);
/* M4:   */   
/* M5:   */   public abstract void ssyrk(String paramString1, String paramString2, int paramInt1, int paramInt2, float paramFloat1, float[] paramArrayOfFloat1, int paramInt3, float paramFloat2, float[] paramArrayOfFloat2, int paramInt4);
/* M6:   */   
/* M7:   */   public abstract void ssyrk(String paramString1, String paramString2, int paramInt1, int paramInt2, float paramFloat1, float[] paramArrayOfFloat1, int paramInt3, int paramInt4, float paramFloat2, float[] paramArrayOfFloat2, int paramInt5, int paramInt6);
/* M8:   */   
/* M9:   */   public abstract void stbmv(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, float[] paramArrayOfFloat1, int paramInt3, float[] paramArrayOfFloat2, int paramInt4);
/* N0:   */   
/* N1:   */   public abstract void stbmv(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, float[] paramArrayOfFloat1, int paramInt3, int paramInt4, float[] paramArrayOfFloat2, int paramInt5, int paramInt6);
/* N2:   */   
/* N3:   */   public abstract void stbsv(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, float[] paramArrayOfFloat1, int paramInt3, float[] paramArrayOfFloat2, int paramInt4);
/* N4:   */   
/* N5:   */   public abstract void stbsv(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, float[] paramArrayOfFloat1, int paramInt3, int paramInt4, float[] paramArrayOfFloat2, int paramInt5, int paramInt6);
/* N6:   */   
/* N7:   */   public abstract void stpmv(String paramString1, String paramString2, String paramString3, int paramInt1, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int paramInt2);
/* N8:   */   
/* N9:   */   public abstract void stpmv(String paramString1, String paramString2, String paramString3, int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, int paramInt4);
/* O0:   */   
/* O1:   */   public abstract void stpsv(String paramString1, String paramString2, String paramString3, int paramInt1, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int paramInt2);
/* O2:   */   
/* O3:   */   public abstract void stpsv(String paramString1, String paramString2, String paramString3, int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3, int paramInt4);
/* O4:   */   
/* O5:   */   public abstract void strmm(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, float paramFloat, float[] paramArrayOfFloat1, int paramInt3, float[] paramArrayOfFloat2, int paramInt4);
/* O6:   */   
/* O7:   */   public abstract void strmm(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, float paramFloat, float[] paramArrayOfFloat1, int paramInt3, int paramInt4, float[] paramArrayOfFloat2, int paramInt5, int paramInt6);
/* O8:   */   
/* O9:   */   public abstract void strmv(String paramString1, String paramString2, String paramString3, int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3);
/* P0:   */   
/* P1:   */   public abstract void strmv(String paramString1, String paramString2, String paramString3, int paramInt1, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, int paramInt5);
/* P2:   */   
/* P3:   */   public abstract void strsm(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, float paramFloat, float[] paramArrayOfFloat1, int paramInt3, float[] paramArrayOfFloat2, int paramInt4);
/* P4:   */   
/* P5:   */   public abstract void strsm(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, float paramFloat, float[] paramArrayOfFloat1, int paramInt3, int paramInt4, float[] paramArrayOfFloat2, int paramInt5, int paramInt6);
/* P6:   */   
/* P7:   */   public abstract void strsv(String paramString1, String paramString2, String paramString3, int paramInt1, float[] paramArrayOfFloat1, int paramInt2, float[] paramArrayOfFloat2, int paramInt3);
/* P8:   */   
/* P9:   */   public abstract void strsv(String paramString1, String paramString2, String paramString3, int paramInt1, float[] paramArrayOfFloat1, int paramInt2, int paramInt3, float[] paramArrayOfFloat2, int paramInt4, int paramInt5);
/* Q0:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     com.github.fommil.netlib.BLAS
 * JD-Core Version:    0.7.0.1
 */