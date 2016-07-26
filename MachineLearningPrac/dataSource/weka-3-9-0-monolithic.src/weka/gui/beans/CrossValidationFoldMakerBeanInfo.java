/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.beans.BeanDescriptor;
/*  4:   */ import java.beans.PropertyDescriptor;
/*  5:   */ 
/*  6:   */ public class CrossValidationFoldMakerBeanInfo
/*  7:   */   extends AbstractTrainAndTestSetProducerBeanInfo
/*  8:   */ {
/*  9:   */   public PropertyDescriptor[] getPropertyDescriptors()
/* 10:   */   {
/* 11:   */     try
/* 12:   */     {
/* 13:46 */       PropertyDescriptor p1 = new PropertyDescriptor("folds", CrossValidationFoldMaker.class);
/* 14:47 */       PropertyDescriptor p2 = new PropertyDescriptor("seed", CrossValidationFoldMaker.class);
/* 15:48 */       PropertyDescriptor p3 = new PropertyDescriptor("preserveOrder", CrossValidationFoldMaker.class);
/* 16:49 */       return new PropertyDescriptor[] { p1, p2, p3 };
/* 17:   */     }
/* 18:   */     catch (Exception ex)
/* 19:   */     {
/* 20:52 */       ex.printStackTrace();
/* 21:   */     }
/* 22:54 */     return null;
/* 23:   */   }
/* 24:   */   
/* 25:   */   public BeanDescriptor getBeanDescriptor()
/* 26:   */   {
/* 27:63 */     return new BeanDescriptor(CrossValidationFoldMaker.class, CrossValidationFoldMakerCustomizer.class);
/* 28:   */   }
/* 29:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.CrossValidationFoldMakerBeanInfo
 * JD-Core Version:    0.7.0.1
 */