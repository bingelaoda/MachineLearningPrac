/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.beans.BeanDescriptor;
/*  4:   */ 
/*  5:   */ public class LoaderBeanInfo
/*  6:   */   extends AbstractDataSourceBeanInfo
/*  7:   */ {
/*  8:   */   public BeanDescriptor getBeanDescriptor()
/*  9:   */   {
/* 10:40 */     return new BeanDescriptor(Loader.class, LoaderCustomizer.class);
/* 11:   */   }
/* 12:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.LoaderBeanInfo
 * JD-Core Version:    0.7.0.1
 */