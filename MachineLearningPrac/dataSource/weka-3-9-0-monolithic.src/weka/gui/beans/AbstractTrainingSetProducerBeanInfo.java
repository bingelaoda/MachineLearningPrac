/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.beans.EventSetDescriptor;
/*  4:   */ import java.beans.SimpleBeanInfo;
/*  5:   */ 
/*  6:   */ public class AbstractTrainingSetProducerBeanInfo
/*  7:   */   extends SimpleBeanInfo
/*  8:   */ {
/*  9:   */   public EventSetDescriptor[] getEventSetDescriptors()
/* 10:   */   {
/* 11:   */     try
/* 12:   */     {
/* 13:42 */       return new EventSetDescriptor[] { new EventSetDescriptor(TrainingSetProducer.class, "trainingSet", TrainingSetListener.class, "acceptTrainingSet") };
/* 14:   */     }
/* 15:   */     catch (Exception ex)
/* 16:   */     {
/* 17:50 */       ex.printStackTrace();
/* 18:   */     }
/* 19:52 */     return null;
/* 20:   */   }
/* 21:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.AbstractTrainingSetProducerBeanInfo
 * JD-Core Version:    0.7.0.1
 */