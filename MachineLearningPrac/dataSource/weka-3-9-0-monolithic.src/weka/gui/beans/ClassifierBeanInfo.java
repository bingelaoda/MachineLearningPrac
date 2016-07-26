/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.beans.BeanDescriptor;
/*  4:   */ import java.beans.EventSetDescriptor;
/*  5:   */ import java.beans.SimpleBeanInfo;
/*  6:   */ 
/*  7:   */ public class ClassifierBeanInfo
/*  8:   */   extends SimpleBeanInfo
/*  9:   */ {
/* 10:   */   public EventSetDescriptor[] getEventSetDescriptors()
/* 11:   */   {
/* 12:   */     try
/* 13:   */     {
/* 14:38 */       return new EventSetDescriptor[] { new EventSetDescriptor(Classifier.class, "batchClassifier", BatchClassifierListener.class, "acceptClassifier"), new EventSetDescriptor(Classifier.class, "graph", GraphListener.class, "acceptGraph"), new EventSetDescriptor(Classifier.class, "text", TextListener.class, "acceptText"), new EventSetDescriptor(Classifier.class, "incrementalClassifier", IncrementalClassifierListener.class, "acceptClassifier"), new EventSetDescriptor(Classifier.class, "configuration", ConfigurationListener.class, "acceptConfiguration") };
/* 15:   */     }
/* 16:   */     catch (Exception ex)
/* 17:   */     {
/* 18:63 */       ex.printStackTrace();
/* 19:   */     }
/* 20:65 */     return null;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public BeanDescriptor getBeanDescriptor()
/* 24:   */   {
/* 25:74 */     return new BeanDescriptor(Classifier.class, ClassifierCustomizer.class);
/* 26:   */   }
/* 27:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ClassifierBeanInfo
 * JD-Core Version:    0.7.0.1
 */