/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.beans.BeanDescriptor;
/*  4:   */ import java.beans.EventSetDescriptor;
/*  5:   */ import java.beans.PropertyDescriptor;
/*  6:   */ import java.beans.SimpleBeanInfo;
/*  7:   */ 
/*  8:   */ public class ClassifierPerformanceEvaluatorBeanInfo
/*  9:   */   extends SimpleBeanInfo
/* 10:   */ {
/* 11:   */   public EventSetDescriptor[] getEventSetDescriptors()
/* 12:   */   {
/* 13:   */     try
/* 14:   */     {
/* 15:40 */       return new EventSetDescriptor[] { new EventSetDescriptor(ClassifierPerformanceEvaluator.class, "text", TextListener.class, "acceptText"), new EventSetDescriptor(ClassifierPerformanceEvaluator.class, "thresholdData", ThresholdDataListener.class, "acceptDataSet"), new EventSetDescriptor(ClassifierPerformanceEvaluator.class, "visualizableError", VisualizableErrorListener.class, "acceptDataSet") };
/* 16:   */     }
/* 17:   */     catch (Exception ex)
/* 18:   */     {
/* 19:50 */       ex.printStackTrace();
/* 20:   */     }
/* 21:52 */     return null;
/* 22:   */   }
/* 23:   */   
/* 24:   */   public PropertyDescriptor[] getPropertyDescriptors()
/* 25:   */   {
/* 26:   */     try
/* 27:   */     {
/* 28:65 */       PropertyDescriptor p1 = new PropertyDescriptor("executionSlots", ClassifierPerformanceEvaluator.class);
/* 29:   */       
/* 30:67 */       PropertyDescriptor p2 = new PropertyDescriptor("errorPlotPointSizeProportionalToMargin", ClassifierPerformanceEvaluator.class);
/* 31:   */       
/* 32:69 */       return new PropertyDescriptor[] { p1, p2 };
/* 33:   */     }
/* 34:   */     catch (Exception ex)
/* 35:   */     {
/* 36:72 */       ex.printStackTrace();
/* 37:   */     }
/* 38:74 */     return null;
/* 39:   */   }
/* 40:   */   
/* 41:   */   public BeanDescriptor getBeanDescriptor()
/* 42:   */   {
/* 43:84 */     return new BeanDescriptor(ClassifierPerformanceEvaluator.class, ClassifierPerformanceEvaluatorCustomizer.class);
/* 44:   */   }
/* 45:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ClassifierPerformanceEvaluatorBeanInfo
 * JD-Core Version:    0.7.0.1
 */