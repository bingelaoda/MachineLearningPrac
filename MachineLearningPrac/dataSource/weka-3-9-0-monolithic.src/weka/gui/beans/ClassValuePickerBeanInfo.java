/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.beans.BeanDescriptor;
/*  4:   */ import java.beans.EventSetDescriptor;
/*  5:   */ import java.beans.PropertyDescriptor;
/*  6:   */ import java.beans.SimpleBeanInfo;
/*  7:   */ 
/*  8:   */ public class ClassValuePickerBeanInfo
/*  9:   */   extends SimpleBeanInfo
/* 10:   */ {
/* 11:   */   public EventSetDescriptor[] getEventSetDescriptors()
/* 12:   */   {
/* 13:   */     try
/* 14:   */     {
/* 15:45 */       return new EventSetDescriptor[] { new EventSetDescriptor(ClassValuePicker.class, "dataSet", DataSourceListener.class, "acceptDataSet") };
/* 16:   */     }
/* 17:   */     catch (Exception ex)
/* 18:   */     {
/* 19:52 */       ex.printStackTrace();
/* 20:   */     }
/* 21:54 */     return null;
/* 22:   */   }
/* 23:   */   
/* 24:   */   public PropertyDescriptor[] getPropertyDescriptors()
/* 25:   */   {
/* 26:   */     try
/* 27:   */     {
/* 28:65 */       PropertyDescriptor p1 = new PropertyDescriptor("classValue", ClassValuePicker.class);
/* 29:66 */       return new PropertyDescriptor[] { p1 };
/* 30:   */     }
/* 31:   */     catch (Exception ex)
/* 32:   */     {
/* 33:69 */       ex.printStackTrace();
/* 34:   */     }
/* 35:71 */     return null;
/* 36:   */   }
/* 37:   */   
/* 38:   */   public BeanDescriptor getBeanDescriptor()
/* 39:   */   {
/* 40:75 */     return new BeanDescriptor(ClassValuePicker.class, ClassValuePickerCustomizer.class);
/* 41:   */   }
/* 42:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ClassValuePickerBeanInfo
 * JD-Core Version:    0.7.0.1
 */