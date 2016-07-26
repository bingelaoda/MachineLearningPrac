/*  1:   */ package weka.experiment;
/*  2:   */ 
/*  3:   */ import java.io.Serializable;
/*  4:   */ 
/*  5:   */ public class RemoteExperimentEvent
/*  6:   */   implements Serializable
/*  7:   */ {
/*  8:   */   private static final long serialVersionUID = 7000867987391866451L;
/*  9:   */   public boolean m_statusMessage;
/* 10:   */   public boolean m_logMessage;
/* 11:   */   public String m_messageString;
/* 12:   */   public boolean m_experimentFinished;
/* 13:   */   
/* 14:   */   public RemoteExperimentEvent(boolean status, boolean log, boolean finished, String message)
/* 15:   */   {
/* 16:59 */     this.m_statusMessage = status;
/* 17:60 */     this.m_logMessage = log;
/* 18:61 */     this.m_experimentFinished = finished;
/* 19:62 */     this.m_messageString = message;
/* 20:   */   }
/* 21:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.RemoteExperimentEvent
 * JD-Core Version:    0.7.0.1
 */