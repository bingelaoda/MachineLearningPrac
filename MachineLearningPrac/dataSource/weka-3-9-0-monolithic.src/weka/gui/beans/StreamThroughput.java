/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.gui.Logger;
/*   5:    */ 
/*   6:    */ public class StreamThroughput
/*   7:    */   implements Serializable
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = 2820675210555581676L;
/*  10: 52 */   protected transient int m_avInstsPerSec = 0;
/*  11:    */   protected transient double m_startTime;
/*  12:    */   protected transient int m_instanceCount;
/*  13:    */   protected transient int m_sampleCount;
/*  14: 56 */   protected transient String m_statusMessagePrefix = "";
/*  15: 62 */   protected transient int m_sampleTime = 2000;
/*  16:    */   protected transient double m_cumulativeTime;
/*  17:    */   protected transient int m_numSamples;
/*  18:    */   protected transient double m_updateStart;
/*  19:    */   
/*  20:    */   public StreamThroughput(String statusMessagePrefix)
/*  21:    */   {
/*  22: 76 */     this.m_instanceCount = 0;
/*  23: 77 */     this.m_sampleCount = 0;
/*  24: 78 */     this.m_numSamples = 0;
/*  25: 79 */     this.m_cumulativeTime = 0.0D;
/*  26: 80 */     this.m_startTime = System.currentTimeMillis();
/*  27: 81 */     this.m_statusMessagePrefix = statusMessagePrefix;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public StreamThroughput(String statusMessagePrefix, String initialMessage, Logger log)
/*  31:    */   {
/*  32: 97 */     this(statusMessagePrefix);
/*  33: 98 */     if (log != null) {
/*  34: 99 */       log.statusMessage(this.m_statusMessagePrefix + initialMessage);
/*  35:    */     }
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void setSamplePeriod(int period)
/*  39:    */   {
/*  40:109 */     this.m_sampleTime = period;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void updateStart()
/*  44:    */   {
/*  45:118 */     this.m_updateStart = System.currentTimeMillis();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void updateEnd(Logger log)
/*  49:    */   {
/*  50:129 */     this.m_instanceCount += 1;
/*  51:130 */     this.m_sampleCount += 1;
/*  52:131 */     double end = System.currentTimeMillis();
/*  53:132 */     double temp = end - this.m_updateStart;
/*  54:133 */     this.m_cumulativeTime += temp;
/*  55:134 */     boolean toFastToMeasure = false;
/*  56:136 */     if (end - this.m_startTime >= this.m_sampleTime)
/*  57:    */     {
/*  58:137 */       computeUpdate(end);
/*  59:139 */       if (log != null) {
/*  60:140 */         log.statusMessage(this.m_statusMessagePrefix + "Processed " + this.m_instanceCount + " insts @ " + this.m_avInstsPerSec / this.m_numSamples + " insts/sec" + (toFastToMeasure ? "*" : ""));
/*  61:    */       }
/*  62:144 */       this.m_sampleCount = 0;
/*  63:145 */       this.m_cumulativeTime = 0.0D;
/*  64:146 */       this.m_startTime = System.currentTimeMillis();
/*  65:    */     }
/*  66:    */   }
/*  67:    */   
/*  68:    */   protected boolean computeUpdate(double end)
/*  69:    */   {
/*  70:151 */     boolean toFastToMeasure = false;
/*  71:152 */     int instsPerSec = 0;
/*  72:154 */     if (this.m_cumulativeTime == 0.0D)
/*  73:    */     {
/*  74:162 */       double sampleTime = end - this.m_startTime;
/*  75:163 */       instsPerSec = (int)(this.m_sampleCount / (sampleTime / 1000.0D));
/*  76:164 */       toFastToMeasure = true;
/*  77:    */     }
/*  78:    */     else
/*  79:    */     {
/*  80:166 */       instsPerSec = (int)(this.m_sampleCount / (this.m_cumulativeTime / 1000.0D));
/*  81:    */     }
/*  82:168 */     this.m_numSamples += 1;
/*  83:169 */     this.m_avInstsPerSec += instsPerSec;
/*  84:    */     
/*  85:171 */     return toFastToMeasure;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public int getAverageInstancesPerSecond()
/*  89:    */   {
/*  90:180 */     int nS = this.m_numSamples > 0 ? this.m_numSamples : 1;
/*  91:181 */     return this.m_avInstsPerSec / nS;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public String finished(Logger log)
/*  95:    */   {
/*  96:192 */     if (this.m_avInstsPerSec == 0) {
/*  97:193 */       computeUpdate(System.currentTimeMillis());
/*  98:    */     }
/*  99:196 */     int nS = this.m_numSamples > 0 ? this.m_numSamples : 1;
/* 100:197 */     String msg = "Finished - " + this.m_instanceCount + " insts @ " + this.m_avInstsPerSec / nS + " insts/sec";
/* 101:199 */     if (log != null) {
/* 102:200 */       log.statusMessage(this.m_statusMessagePrefix + msg);
/* 103:    */     }
/* 104:203 */     return msg;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public String finished()
/* 108:    */   {
/* 109:213 */     if (this.m_avInstsPerSec == 0) {
/* 110:214 */       computeUpdate(System.currentTimeMillis());
/* 111:    */     }
/* 112:217 */     int nS = this.m_numSamples > 0 ? this.m_numSamples : 1;
/* 113:218 */     String msg = "Finished - " + this.m_instanceCount + " insts @ " + this.m_avInstsPerSec / nS + " insts/sec";
/* 114:    */     
/* 115:    */ 
/* 116:221 */     return msg;
/* 117:    */   }
/* 118:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.StreamThroughput
 * JD-Core Version:    0.7.0.1
 */