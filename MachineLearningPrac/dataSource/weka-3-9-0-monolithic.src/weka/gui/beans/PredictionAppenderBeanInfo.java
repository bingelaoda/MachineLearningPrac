/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.beans.BeanDescriptor;
/*  4:   */ import java.beans.EventSetDescriptor;
/*  5:   */ import java.beans.PropertyDescriptor;
/*  6:   */ import java.beans.SimpleBeanInfo;
/*  7:   */ 
/*  8:   */ public class PredictionAppenderBeanInfo
/*  9:   */   extends SimpleBeanInfo
/* 10:   */ {
/* 11:   */   public EventSetDescriptor[] getEventSetDescriptors()
/* 12:   */   {
/* 13:   */     try
/* 14:   */     {
/* 15:46 */       return new EventSetDescriptor[] { new EventSetDescriptor(PredictionAppender.class, "dataSet", DataSourceListener.class, "acceptDataSet"), new EventSetDescriptor(PredictionAppender.class, "instance", InstanceListener.class, "acceptInstance"), new EventSetDescriptor(PredictionAppender.class, "trainingSet", TrainingSetListener.class, "acceptTrainingSet"), new EventSetDescriptor(PredictionAppender.class, "testSet", TestSetListener.class, "acceptTestSet") };
/* 16:   */     }
/* 17:   */     catch (Exception ex)
/* 18:   */     {
/* 19:66 */       ex.printStackTrace();
/* 20:   */     }
/* 21:68 */     return null;
/* 22:   */   }
/* 23:   */   
/* 24:   */   public PropertyDescriptor[] getPropertyDescriptors()
/* 25:   */   {
/* 26:   */     try
/* 27:   */     {
/* 28:79 */       PropertyDescriptor p1 = new PropertyDescriptor("appendPredictedProbabilities", PredictionAppender.class);
/* 29:   */       
/* 30:81 */       return new PropertyDescriptor[] { p1 };
/* 31:   */     }
/* 32:   */     catch (Exception ex)
/* 33:   */     {
/* 34:84 */       ex.printStackTrace();
/* 35:   */     }
/* 36:86 */     return null;
/* 37:   */   }
/* 38:   */   
/* 39:   */   public BeanDescriptor getBeanDescriptor()
/* 40:   */   {
/* 41:95 */     return new BeanDescriptor(PredictionAppender.class, PredictionAppenderCustomizer.class);
/* 42:   */   }
/* 43:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.PredictionAppenderBeanInfo
 * JD-Core Version:    0.7.0.1
 */