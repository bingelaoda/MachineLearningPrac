/*  1:   */ package weka.filters.unsupervised.attribute;
/*  2:   */ 
/*  3:   */ import java.beans.BeanDescriptor;
/*  4:   */ import java.beans.SimpleBeanInfo;
/*  5:   */ import weka.gui.filters.AddUserFieldsCustomizer;
/*  6:   */ 
/*  7:   */ public class AddUserFieldsBeanInfo
/*  8:   */   extends SimpleBeanInfo
/*  9:   */ {
/* 10:   */   public BeanDescriptor getBeanDescriptor()
/* 11:   */   {
/* 12:42 */     return new BeanDescriptor(AddUserFields.class, AddUserFieldsCustomizer.class);
/* 13:   */   }
/* 14:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.AddUserFieldsBeanInfo
 * JD-Core Version:    0.7.0.1
 */