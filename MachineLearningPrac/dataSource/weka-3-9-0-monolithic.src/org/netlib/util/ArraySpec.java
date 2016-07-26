/*   1:    */ package org.netlib.util;
/*   2:    */ 
/*   3:    */ import java.util.Vector;
/*   4:    */ 
/*   5:    */ public class ArraySpec
/*   6:    */ {
/*   7:    */   private Vector vec;
/*   8:    */   
/*   9:    */   public ArraySpec(int[] paramArrayOfInt, int paramInt1, int paramInt2)
/*  10:    */   {
/*  11: 30 */     this.vec = new Vector();
/*  12: 32 */     for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
/*  13: 33 */       this.vec.addElement(new Integer(paramArrayOfInt[i]));
/*  14:    */     }
/*  15:    */   }
/*  16:    */   
/*  17:    */   public ArraySpec(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
/*  18:    */   {
/*  19: 45 */     this.vec = new Vector();
/*  20: 47 */     for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
/*  21: 48 */       this.vec.addElement(new Double(paramArrayOfDouble[i]));
/*  22:    */     }
/*  23:    */   }
/*  24:    */   
/*  25:    */   public ArraySpec(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
/*  26:    */   {
/*  27: 60 */     this.vec = new Vector();
/*  28: 62 */     for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
/*  29: 63 */       this.vec.addElement(new Float(paramArrayOfFloat[i]));
/*  30:    */     }
/*  31:    */   }
/*  32:    */   
/*  33:    */   public ArraySpec(String[] paramArrayOfString, int paramInt1, int paramInt2)
/*  34:    */   {
/*  35: 75 */     this.vec = new Vector();
/*  36: 77 */     for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
/*  37: 78 */       this.vec.addElement(new String(paramArrayOfString[i]));
/*  38:    */     }
/*  39:    */   }
/*  40:    */   
/*  41:    */   public ArraySpec(String paramString)
/*  42:    */   {
/*  43: 89 */     char[] arrayOfChar = paramString.toCharArray();
/*  44: 90 */     this.vec = new Vector();
/*  45: 92 */     for (int i = 0; i < arrayOfChar.length; i++) {
/*  46: 93 */       this.vec.addElement(new String(String.valueOf(arrayOfChar[i])));
/*  47:    */     }
/*  48:    */   }
/*  49:    */   
/*  50:    */   public Vector get_vec()
/*  51:    */   {
/*  52:102 */     return this.vec;
/*  53:    */   }
/*  54:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.netlib.util.ArraySpec
 * JD-Core Version:    0.7.0.1
 */