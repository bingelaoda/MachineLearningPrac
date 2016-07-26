/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.gui.Logger;
/*   8:    */ 
/*   9:    */ public class TrainingSetMaker
/*  10:    */   extends AbstractTrainingSetProducer
/*  11:    */   implements DataSourceListener, TestSetListener, EventConstraints, Serializable, StructureProducer
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = -6152577265471535786L;
/*  14: 42 */   protected boolean m_receivedStopNotification = false;
/*  15:    */   
/*  16:    */   public TrainingSetMaker()
/*  17:    */   {
/*  18: 45 */     this.m_visual.loadIcons("weka/gui/beans/icons/TrainingSetMaker.gif", "weka/gui/beans/icons/TrainingSetMaker_animated.gif");
/*  19:    */     
/*  20: 47 */     this.m_visual.setText("TrainingSetMaker");
/*  21:    */   }
/*  22:    */   
/*  23:    */   public Instances getStructure(String eventName)
/*  24:    */   {
/*  25: 65 */     if (!eventName.equals("dataSet")) {
/*  26: 66 */       return null;
/*  27:    */     }
/*  28: 68 */     if (this.m_listenee == null) {
/*  29: 69 */       return null;
/*  30:    */     }
/*  31: 71 */     if ((this.m_listenee != null) && ((this.m_listenee instanceof StructureProducer))) {
/*  32: 72 */       return ((StructureProducer)this.m_listenee).getStructure("dataSet");
/*  33:    */     }
/*  34: 74 */     return null;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void setCustomName(String name)
/*  38:    */   {
/*  39: 84 */     this.m_visual.setText(name);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public String getCustomName()
/*  43:    */   {
/*  44: 94 */     return this.m_visual.getText();
/*  45:    */   }
/*  46:    */   
/*  47:    */   public String globalInfo()
/*  48:    */   {
/*  49:103 */     return "Designate an incoming data set as a training set.";
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void acceptDataSet(DataSetEvent e)
/*  53:    */   {
/*  54:113 */     this.m_receivedStopNotification = false;
/*  55:114 */     TrainingSetEvent tse = new TrainingSetEvent(this, e.getDataSet());
/*  56:115 */     tse.m_setNumber = 1;
/*  57:116 */     tse.m_maxSetNumber = 1;
/*  58:117 */     notifyTrainingSetProduced(tse);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void acceptTestSet(TestSetEvent e)
/*  62:    */   {
/*  63:122 */     this.m_receivedStopNotification = false;
/*  64:123 */     TrainingSetEvent tse = new TrainingSetEvent(this, e.getTestSet());
/*  65:124 */     tse.m_setNumber = 1;
/*  66:125 */     tse.m_maxSetNumber = 1;
/*  67:126 */     notifyTrainingSetProduced(tse);
/*  68:    */   }
/*  69:    */   
/*  70:    */   protected void notifyTrainingSetProduced(TrainingSetEvent tse)
/*  71:    */   {
/*  72:    */     Vector<TrainingSetListener> l;
/*  73:137 */     synchronized (this)
/*  74:    */     {
/*  75:138 */       l = (Vector)this.m_listeners.clone();
/*  76:    */     }
/*  77:140 */     if (l.size() > 0) {
/*  78:141 */       for (int i = 0; i < l.size(); i++)
/*  79:    */       {
/*  80:142 */         if (this.m_receivedStopNotification)
/*  81:    */         {
/*  82:143 */           if (this.m_logger != null)
/*  83:    */           {
/*  84:144 */             this.m_logger.logMessage("T[rainingSetMaker] " + statusMessagePrefix() + " stopping.");
/*  85:    */             
/*  86:146 */             this.m_logger.statusMessage(statusMessagePrefix() + "INTERRUPTED");
/*  87:    */           }
/*  88:148 */           this.m_receivedStopNotification = false;
/*  89:149 */           break;
/*  90:    */         }
/*  91:151 */         System.err.println("Notifying listeners (training set maker)");
/*  92:152 */         ((TrainingSetListener)l.elementAt(i)).acceptTrainingSet(tse);
/*  93:    */       }
/*  94:    */     }
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void stop()
/*  98:    */   {
/*  99:162 */     this.m_receivedStopNotification = true;
/* 100:165 */     if ((this.m_listenee instanceof BeanCommon)) {
/* 101:167 */       ((BeanCommon)this.m_listenee).stop();
/* 102:    */     }
/* 103:    */   }
/* 104:    */   
/* 105:    */   public boolean isBusy()
/* 106:    */   {
/* 107:179 */     return false;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public boolean eventGeneratable(String eventName)
/* 111:    */   {
/* 112:192 */     if (this.m_listenee == null) {
/* 113:193 */       return false;
/* 114:    */     }
/* 115:196 */     if (((this.m_listenee instanceof EventConstraints)) && 
/* 116:197 */       (!((EventConstraints)this.m_listenee).eventGeneratable("dataSet"))) {
/* 117:198 */       return false;
/* 118:    */     }
/* 119:201 */     return true;
/* 120:    */   }
/* 121:    */   
/* 122:    */   private String statusMessagePrefix()
/* 123:    */   {
/* 124:205 */     return getCustomName() + "$" + hashCode() + "|";
/* 125:    */   }
/* 126:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.TrainingSetMaker
 * JD-Core Version:    0.7.0.1
 */