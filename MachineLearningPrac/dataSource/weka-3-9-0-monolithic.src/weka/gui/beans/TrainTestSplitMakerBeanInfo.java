/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.beans.BeanDescriptor;
/*  4:   */ import java.beans.PropertyDescriptor;
/*  5:   */ 
/*  6:   */ public class TrainTestSplitMakerBeanInfo
/*  7:   */   extends AbstractTrainAndTestSetProducerBeanInfo
/*  8:   */ {
/*  9:   */   public PropertyDescriptor[] getPropertyDescriptors()
/* 10:   */   {
/* 11:   */     try
/* 12:   */     {
/* 13:47 */       PropertyDescriptor p1 = new PropertyDescriptor("trainPercent", TrainTestSplitMaker.class);
/* 14:48 */       PropertyDescriptor p2 = new PropertyDescriptor("seed", TrainTestSplitMaker.class);
/* 15:49 */       return new PropertyDescriptor[] { p1, p2 };
/* 16:   */     }
/* 17:   */     catch (Exception ex)
/* 18:   */     {
/* 19:52 */       ex.printStackTrace();
/* 20:   */     }
/* 21:54 */     return null;
/* 22:   */   }
/* 23:   */   
/* 24:   */   public BeanDescriptor getBeanDescriptor()
/* 25:   */   {
/* 26:63 */     return new BeanDescriptor(TrainTestSplitMaker.class, TrainTestSplitMakerCustomizer.class);
/* 27:   */   }
/* 28:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.TrainTestSplitMakerBeanInfo
 * JD-Core Version:    0.7.0.1
 */