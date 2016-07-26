/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.beans.BeanDescriptor;
/*  4:   */ import java.beans.EventSetDescriptor;
/*  5:   */ import java.beans.PropertyDescriptor;
/*  6:   */ import java.beans.SimpleBeanInfo;
/*  7:   */ 
/*  8:   */ public class IncrementalClassifierEvaluatorBeanInfo
/*  9:   */   extends SimpleBeanInfo
/* 10:   */ {
/* 11:   */   public PropertyDescriptor[] getPropertyDescriptors()
/* 12:   */   {
/* 13:   */     try
/* 14:   */     {
/* 15:47 */       PropertyDescriptor p1 = new PropertyDescriptor("statusFrequency", IncrementalClassifierEvaluator.class);
/* 16:48 */       PropertyDescriptor p2 = new PropertyDescriptor("outputPerClassInfoRetrievalStats", IncrementalClassifierEvaluator.class);
/* 17:   */       
/* 18:50 */       PropertyDescriptor p3 = new PropertyDescriptor("chartingEvalWindowSize", IncrementalClassifierEvaluator.class);
/* 19:   */       
/* 20:52 */       return new PropertyDescriptor[] { p1, p2, p3 };
/* 21:   */     }
/* 22:   */     catch (Exception ex)
/* 23:   */     {
/* 24:55 */       ex.printStackTrace();
/* 25:   */     }
/* 26:57 */     return null;
/* 27:   */   }
/* 28:   */   
/* 29:   */   public EventSetDescriptor[] getEventSetDescriptors()
/* 30:   */   {
/* 31:   */     try
/* 32:   */     {
/* 33:67 */       return new EventSetDescriptor[] { new EventSetDescriptor(IncrementalClassifierEvaluator.class, "chart", ChartListener.class, "acceptDataPoint"), new EventSetDescriptor(IncrementalClassifierEvaluator.class, "text", TextListener.class, "acceptText") };
/* 34:   */     }
/* 35:   */     catch (Exception ex)
/* 36:   */     {
/* 37:79 */       ex.printStackTrace();
/* 38:   */     }
/* 39:81 */     return null;
/* 40:   */   }
/* 41:   */   
/* 42:   */   public BeanDescriptor getBeanDescriptor()
/* 43:   */   {
/* 44:90 */     return new BeanDescriptor(IncrementalClassifierEvaluator.class, IncrementalClassifierEvaluatorCustomizer.class);
/* 45:   */   }
/* 46:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.IncrementalClassifierEvaluatorBeanInfo
 * JD-Core Version:    0.7.0.1
 */