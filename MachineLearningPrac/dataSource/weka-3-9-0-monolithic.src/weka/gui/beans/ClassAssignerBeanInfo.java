/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.beans.BeanDescriptor;
/*  4:   */ import java.beans.EventSetDescriptor;
/*  5:   */ import java.beans.PropertyDescriptor;
/*  6:   */ import java.beans.SimpleBeanInfo;
/*  7:   */ 
/*  8:   */ public class ClassAssignerBeanInfo
/*  9:   */   extends SimpleBeanInfo
/* 10:   */ {
/* 11:   */   public EventSetDescriptor[] getEventSetDescriptors()
/* 12:   */   {
/* 13:   */     try
/* 14:   */     {
/* 15:45 */       return new EventSetDescriptor[] { new EventSetDescriptor(DataSource.class, "dataSet", DataSourceListener.class, "acceptDataSet"), new EventSetDescriptor(DataSource.class, "instance", InstanceListener.class, "acceptInstance"), new EventSetDescriptor(TrainingSetProducer.class, "trainingSet", TrainingSetListener.class, "acceptTrainingSet"), new EventSetDescriptor(TestSetProducer.class, "testSet", TestSetListener.class, "acceptTestSet") };
/* 16:   */     }
/* 17:   */     catch (Exception ex)
/* 18:   */     {
/* 19:64 */       ex.printStackTrace();
/* 20:   */     }
/* 21:66 */     return null;
/* 22:   */   }
/* 23:   */   
/* 24:   */   public PropertyDescriptor[] getPropertyDescriptors()
/* 25:   */   {
/* 26:   */     try
/* 27:   */     {
/* 28:77 */       PropertyDescriptor p1 = new PropertyDescriptor("classColumn", ClassAssigner.class);
/* 29:78 */       return new PropertyDescriptor[] { p1 };
/* 30:   */     }
/* 31:   */     catch (Exception ex)
/* 32:   */     {
/* 33:81 */       ex.printStackTrace();
/* 34:   */     }
/* 35:83 */     return null;
/* 36:   */   }
/* 37:   */   
/* 38:   */   public BeanDescriptor getBeanDescriptor()
/* 39:   */   {
/* 40:87 */     return new BeanDescriptor(ClassAssigner.class, ClassAssignerCustomizer.class);
/* 41:   */   }
/* 42:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ClassAssignerBeanInfo
 * JD-Core Version:    0.7.0.1
 */