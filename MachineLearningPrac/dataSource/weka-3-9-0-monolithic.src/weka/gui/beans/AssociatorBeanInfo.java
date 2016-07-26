/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.beans.BeanDescriptor;
/*  4:   */ import java.beans.EventSetDescriptor;
/*  5:   */ import java.beans.SimpleBeanInfo;
/*  6:   */ 
/*  7:   */ public class AssociatorBeanInfo
/*  8:   */   extends SimpleBeanInfo
/*  9:   */ {
/* 10:   */   public EventSetDescriptor[] getEventSetDescriptors()
/* 11:   */   {
/* 12:   */     try
/* 13:   */     {
/* 14:38 */       return new EventSetDescriptor[] { new EventSetDescriptor(Associator.class, "text", TextListener.class, "acceptText"), new EventSetDescriptor(Associator.class, "graph", GraphListener.class, "acceptGraph"), new EventSetDescriptor(Associator.class, "configuration", ConfigurationListener.class, "acceptConfiguration"), new EventSetDescriptor(Associator.class, "batchAssociationRules", BatchAssociationRulesListener.class, "acceptAssociationRules") };
/* 15:   */     }
/* 16:   */     catch (Exception ex)
/* 17:   */     {
/* 18:58 */       ex.printStackTrace();
/* 19:   */     }
/* 20:60 */     return null;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public BeanDescriptor getBeanDescriptor()
/* 24:   */   {
/* 25:69 */     return new BeanDescriptor(Associator.class, AssociatorCustomizer.class);
/* 26:   */   }
/* 27:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.AssociatorBeanInfo
 * JD-Core Version:    0.7.0.1
 */