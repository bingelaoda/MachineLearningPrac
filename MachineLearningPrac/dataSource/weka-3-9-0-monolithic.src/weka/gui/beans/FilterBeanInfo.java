/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.beans.BeanDescriptor;
/*  4:   */ import java.beans.EventSetDescriptor;
/*  5:   */ import java.beans.SimpleBeanInfo;
/*  6:   */ 
/*  7:   */ public class FilterBeanInfo
/*  8:   */   extends SimpleBeanInfo
/*  9:   */ {
/* 10:   */   public EventSetDescriptor[] getEventSetDescriptors()
/* 11:   */   {
/* 12:   */     try
/* 13:   */     {
/* 14:43 */       return new EventSetDescriptor[] { new EventSetDescriptor(TrainingSetProducer.class, "trainingSet", TrainingSetListener.class, "acceptTrainingSet"), new EventSetDescriptor(TestSetProducer.class, "testSet", TestSetListener.class, "acceptTestSet"), new EventSetDescriptor(DataSource.class, "dataSet", DataSourceListener.class, "acceptDataSet"), new EventSetDescriptor(DataSource.class, "instance", InstanceListener.class, "acceptInstance"), new EventSetDescriptor(Filter.class, "configuration", ConfigurationListener.class, "acceptConfiguration") };
/* 15:   */     }
/* 16:   */     catch (Exception ex)
/* 17:   */     {
/* 18:67 */       ex.printStackTrace();
/* 19:   */     }
/* 20:69 */     return null;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public BeanDescriptor getBeanDescriptor()
/* 24:   */   {
/* 25:78 */     return new BeanDescriptor(Filter.class, FilterCustomizer.class);
/* 26:   */   }
/* 27:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.FilterBeanInfo
 * JD-Core Version:    0.7.0.1
 */