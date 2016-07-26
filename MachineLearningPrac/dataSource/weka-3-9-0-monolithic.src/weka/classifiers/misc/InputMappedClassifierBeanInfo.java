/*  1:   */ package weka.classifiers.misc;
/*  2:   */ 
/*  3:   */ import java.beans.PropertyDescriptor;
/*  4:   */ import java.beans.SimpleBeanInfo;
/*  5:   */ import java.util.ArrayList;
/*  6:   */ import weka.gui.beans.FileEnvironmentField;
/*  7:   */ 
/*  8:   */ public class InputMappedClassifierBeanInfo
/*  9:   */   extends SimpleBeanInfo
/* 10:   */ {
/* 11:   */   public PropertyDescriptor[] getPropertyDescriptors()
/* 12:   */   {
/* 13:   */     try
/* 14:   */     {
/* 15:47 */       ArrayList<PropertyDescriptor> pds = new ArrayList();
/* 16:   */       
/* 17:49 */       PropertyDescriptor p1 = new PropertyDescriptor("modelPath", InputMappedClassifier.class);
/* 18:50 */       p1.setPropertyEditorClass(FileEnvironmentField.class);
/* 19:51 */       pds.add(p1);
/* 20:   */       
/* 21:53 */       pds.add(new PropertyDescriptor("ignoreCaseForNames", InputMappedClassifier.class));
/* 22:54 */       pds.add(new PropertyDescriptor("suppressMappingReport", InputMappedClassifier.class));
/* 23:55 */       pds.add(new PropertyDescriptor("trim", InputMappedClassifier.class));
/* 24:56 */       pds.add(new PropertyDescriptor("classifier", InputMappedClassifier.class));
/* 25:   */       
/* 26:   */ 
/* 27:59 */       pds.add(new PropertyDescriptor("options", InputMappedClassifier.class));
/* 28:   */       
/* 29:61 */       return (PropertyDescriptor[])pds.toArray(new PropertyDescriptor[1]);
/* 30:   */     }
/* 31:   */     catch (Exception ex)
/* 32:   */     {
/* 33:63 */       ex.printStackTrace();
/* 34:   */     }
/* 35:65 */     return null;
/* 36:   */   }
/* 37:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.misc.InputMappedClassifierBeanInfo
 * JD-Core Version:    0.7.0.1
 */