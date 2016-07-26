/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.beans.BeanDescriptor;
/*  4:   */ import java.beans.EventSetDescriptor;
/*  5:   */ import java.beans.SimpleBeanInfo;
/*  6:   */ 
/*  7:   */ public class TextSaverBeanInfo
/*  8:   */   extends SimpleBeanInfo
/*  9:   */ {
/* 10:   */   public EventSetDescriptor[] getEventSetDescriptors()
/* 11:   */   {
/* 12:44 */     EventSetDescriptor[] esds = new EventSetDescriptor[0];
/* 13:45 */     return esds;
/* 14:   */   }
/* 15:   */   
/* 16:   */   public BeanDescriptor getBeanDescriptor()
/* 17:   */   {
/* 18:55 */     return new BeanDescriptor(TextSaver.class, TextSaverCustomizer.class);
/* 19:   */   }
/* 20:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.TextSaverBeanInfo
 * JD-Core Version:    0.7.0.1
 */