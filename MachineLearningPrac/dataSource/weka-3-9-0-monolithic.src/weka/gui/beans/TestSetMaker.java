/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.core.Instances;
/*   6:    */ import weka.gui.Logger;
/*   7:    */ 
/*   8:    */ public class TestSetMaker
/*   9:    */   extends AbstractTestSetProducer
/*  10:    */   implements DataSourceListener, TrainingSetListener, EventConstraints, Serializable, StructureProducer
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = -8473882857628061841L;
/*  13: 42 */   protected boolean m_receivedStopNotification = false;
/*  14:    */   
/*  15:    */   public Instances getStructure(String eventName)
/*  16:    */   {
/*  17: 59 */     if (!eventName.equals("dataSet")) {
/*  18: 60 */       return null;
/*  19:    */     }
/*  20: 62 */     if (this.m_listenee == null) {
/*  21: 63 */       return null;
/*  22:    */     }
/*  23: 65 */     if ((this.m_listenee != null) && ((this.m_listenee instanceof StructureProducer))) {
/*  24: 66 */       return ((StructureProducer)this.m_listenee).getStructure("dataSet");
/*  25:    */     }
/*  26: 68 */     return null;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public TestSetMaker()
/*  30:    */   {
/*  31: 72 */     this.m_visual.loadIcons("weka/gui/beans/icons/TestSetMaker.gif", "weka/gui/beans/icons/TestSetMaker_animated.gif");
/*  32:    */     
/*  33: 74 */     this.m_visual.setText("TestSetMaker");
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void setCustomName(String name)
/*  37:    */   {
/*  38: 84 */     this.m_visual.setText(name);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public String getCustomName()
/*  42:    */   {
/*  43: 94 */     return this.m_visual.getText();
/*  44:    */   }
/*  45:    */   
/*  46:    */   public String globalInfo()
/*  47:    */   {
/*  48:103 */     return "Designate an incoming data set as a test set.";
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void acceptDataSet(DataSetEvent e)
/*  52:    */   {
/*  53:113 */     this.m_receivedStopNotification = false;
/*  54:114 */     TestSetEvent tse = new TestSetEvent(this, e.getDataSet());
/*  55:115 */     tse.m_setNumber = 1;
/*  56:116 */     tse.m_maxSetNumber = 1;
/*  57:117 */     notifyTestSetProduced(tse);
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void acceptTrainingSet(TrainingSetEvent e)
/*  61:    */   {
/*  62:122 */     this.m_receivedStopNotification = false;
/*  63:123 */     TestSetEvent tse = new TestSetEvent(this, e.getTrainingSet());
/*  64:124 */     tse.m_setNumber = 1;
/*  65:125 */     tse.m_maxSetNumber = 1;
/*  66:126 */     notifyTestSetProduced(tse);
/*  67:    */   }
/*  68:    */   
/*  69:    */   protected void notifyTestSetProduced(TestSetEvent tse)
/*  70:    */   {
/*  71:    */     Vector<TestSetListener> l;
/*  72:137 */     synchronized (this)
/*  73:    */     {
/*  74:138 */       l = (Vector)this.m_listeners.clone();
/*  75:    */     }
/*  76:140 */     if (l.size() > 0) {
/*  77:141 */       for (int i = 0; i < l.size(); i++)
/*  78:    */       {
/*  79:142 */         if (this.m_receivedStopNotification)
/*  80:    */         {
/*  81:143 */           if (this.m_logger != null)
/*  82:    */           {
/*  83:144 */             this.m_logger.logMessage("[TestSetMaker] " + statusMessagePrefix() + " stopping.");
/*  84:    */             
/*  85:146 */             this.m_logger.statusMessage(statusMessagePrefix() + "INTERRUPTED");
/*  86:    */           }
/*  87:148 */           this.m_receivedStopNotification = false;
/*  88:149 */           break;
/*  89:    */         }
/*  90:151 */         ((TestSetListener)l.elementAt(i)).acceptTestSet(tse);
/*  91:    */       }
/*  92:    */     }
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void stop()
/*  96:    */   {
/*  97:159 */     this.m_receivedStopNotification = true;
/*  98:162 */     if ((this.m_listenee instanceof BeanCommon)) {
/*  99:163 */       ((BeanCommon)this.m_listenee).stop();
/* 100:    */     }
/* 101:    */   }
/* 102:    */   
/* 103:    */   public boolean isBusy()
/* 104:    */   {
/* 105:175 */     return false;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public boolean eventGeneratable(String eventName)
/* 109:    */   {
/* 110:188 */     if (this.m_listenee == null) {
/* 111:189 */       return false;
/* 112:    */     }
/* 113:192 */     if (((this.m_listenee instanceof EventConstraints)) && 
/* 114:193 */       (!((EventConstraints)this.m_listenee).eventGeneratable("dataSet"))) {
/* 115:194 */       return false;
/* 116:    */     }
/* 117:197 */     return true;
/* 118:    */   }
/* 119:    */   
/* 120:    */   private String statusMessagePrefix()
/* 121:    */   {
/* 122:201 */     return getCustomName() + "$" + hashCode() + "|";
/* 123:    */   }
/* 124:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.TestSetMaker
 * JD-Core Version:    0.7.0.1
 */