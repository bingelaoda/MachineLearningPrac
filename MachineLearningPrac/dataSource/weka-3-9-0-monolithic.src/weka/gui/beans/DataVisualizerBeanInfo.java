/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.beans.BeanDescriptor;
/*  4:   */ import java.beans.EventSetDescriptor;
/*  5:   */ import java.beans.SimpleBeanInfo;
/*  6:   */ 
/*  7:   */ public class DataVisualizerBeanInfo
/*  8:   */   extends SimpleBeanInfo
/*  9:   */ {
/* 10:   */   public EventSetDescriptor[] getEventSetDescriptors()
/* 11:   */   {
/* 12:   */     try
/* 13:   */     {
/* 14:43 */       return new EventSetDescriptor[] { new EventSetDescriptor(DataVisualizer.class, "dataSet", DataSourceListener.class, "acceptDataSet"), new EventSetDescriptor(DataVisualizer.class, "image", ImageListener.class, "acceptImage") };
/* 15:   */     }
/* 16:   */     catch (Exception ex)
/* 17:   */     {
/* 18:55 */       ex.printStackTrace();
/* 19:   */     }
/* 20:57 */     return null;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public BeanDescriptor getBeanDescriptor()
/* 24:   */   {
/* 25:66 */     return new BeanDescriptor(DataVisualizer.class, DataVisualizerCustomizer.class);
/* 26:   */   }
/* 27:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.DataVisualizerBeanInfo
 * JD-Core Version:    0.7.0.1
 */