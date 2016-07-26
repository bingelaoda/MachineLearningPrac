/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.beans.BeanDescriptor;
/*  4:   */ import java.beans.EventSetDescriptor;
/*  5:   */ import java.beans.PropertyDescriptor;
/*  6:   */ import java.beans.SimpleBeanInfo;
/*  7:   */ 
/*  8:   */ public class StripChartBeanInfo
/*  9:   */   extends SimpleBeanInfo
/* 10:   */ {
/* 11:   */   public EventSetDescriptor[] getEventSetDescriptors()
/* 12:   */   {
/* 13:44 */     EventSetDescriptor[] esds = new EventSetDescriptor[0];
/* 14:45 */     return esds;
/* 15:   */   }
/* 16:   */   
/* 17:   */   public PropertyDescriptor[] getPropertyDescriptors()
/* 18:   */   {
/* 19:   */     try
/* 20:   */     {
/* 21:58 */       PropertyDescriptor p1 = new PropertyDescriptor("xLabelFreq", StripChart.class);
/* 22:59 */       PropertyDescriptor p2 = new PropertyDescriptor("refreshFreq", StripChart.class);
/* 23:60 */       PropertyDescriptor p3 = new PropertyDescriptor("refreshWidth", StripChart.class);
/* 24:61 */       return new PropertyDescriptor[] { p1, p2, p3 };
/* 25:   */     }
/* 26:   */     catch (Exception ex)
/* 27:   */     {
/* 28:64 */       ex.printStackTrace();
/* 29:   */     }
/* 30:66 */     return null;
/* 31:   */   }
/* 32:   */   
/* 33:   */   public BeanDescriptor getBeanDescriptor()
/* 34:   */   {
/* 35:75 */     return new BeanDescriptor(StripChart.class, StripChartCustomizer.class);
/* 36:   */   }
/* 37:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.StripChartBeanInfo
 * JD-Core Version:    0.7.0.1
 */