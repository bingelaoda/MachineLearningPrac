/*  1:   */ package weka.classifiers.evaluation;
/*  2:   */ 
/*  3:   */ import weka.core.Utils;
/*  4:   */ 
/*  5:   */ public abstract interface Prediction
/*  6:   */ {
/*  7:37 */   public static final double MISSING_VALUE = ;
/*  8:   */   
/*  9:   */   public abstract double weight();
/* 10:   */   
/* 11:   */   public abstract double actual();
/* 12:   */   
/* 13:   */   public abstract double predicted();
/* 14:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.Prediction
 * JD-Core Version:    0.7.0.1
 */