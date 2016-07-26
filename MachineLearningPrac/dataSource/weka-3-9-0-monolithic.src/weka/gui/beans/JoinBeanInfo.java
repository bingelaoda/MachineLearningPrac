/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.beans.BeanDescriptor;
/*  4:   */ import java.beans.EventSetDescriptor;
/*  5:   */ import java.beans.SimpleBeanInfo;
/*  6:   */ 
/*  7:   */ public class JoinBeanInfo
/*  8:   */   extends SimpleBeanInfo
/*  9:   */ {
/* 10:   */   public EventSetDescriptor[] getEventSetDescriptors()
/* 11:   */   {
/* 12:   */     try
/* 13:   */     {
/* 14:44 */       return new EventSetDescriptor[] { new EventSetDescriptor(DataSource.class, "instance", InstanceListener.class, "acceptInstance"), new EventSetDescriptor(DataSource.class, "dataSet", DataSourceListener.class, "acceptDataSet") };
/* 15:   */     }
/* 16:   */     catch (Exception ex)
/* 17:   */     {
/* 18:52 */       ex.printStackTrace();
/* 19:   */     }
/* 20:54 */     return null;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public BeanDescriptor getBeanDescriptor()
/* 24:   */   {
/* 25:64 */     return new BeanDescriptor(Join.class, JoinCustomizer.class);
/* 26:   */   }
/* 27:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.JoinBeanInfo
 * JD-Core Version:    0.7.0.1
 */