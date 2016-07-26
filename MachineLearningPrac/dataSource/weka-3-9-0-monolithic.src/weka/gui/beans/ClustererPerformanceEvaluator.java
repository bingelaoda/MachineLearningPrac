/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.clusterers.ClusterEvaluation;
/*   7:    */ import weka.clusterers.Clusterer;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.gui.Logger;
/*  11:    */ 
/*  12:    */ public class ClustererPerformanceEvaluator
/*  13:    */   extends AbstractEvaluator
/*  14:    */   implements BatchClustererListener, Serializable, UserRequestAcceptor, EventConstraints
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = 8041163601333978584L;
/*  17:    */   private transient ClusterEvaluation m_eval;
/*  18:    */   private transient Clusterer m_clusterer;
/*  19: 53 */   private transient Thread m_evaluateThread = null;
/*  20: 55 */   private final Vector<TextListener> m_textListeners = new Vector();
/*  21:    */   
/*  22:    */   public ClustererPerformanceEvaluator()
/*  23:    */   {
/*  24: 58 */     this.m_visual.loadIcons("weka/gui/beans/icons/ClustererPerformanceEvaluator.gif", "weka/gui/beans/icons/ClustererPerformanceEvaluator_animated.gif");
/*  25:    */     
/*  26:    */ 
/*  27: 61 */     this.m_visual.setText("ClustererPerformanceEvaluator");
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void setCustomName(String name)
/*  31:    */   {
/*  32: 71 */     this.m_visual.setText(name);
/*  33:    */   }
/*  34:    */   
/*  35:    */   public String getCustomName()
/*  36:    */   {
/*  37: 81 */     return this.m_visual.getText();
/*  38:    */   }
/*  39:    */   
/*  40:    */   public String globalInfo()
/*  41:    */   {
/*  42: 90 */     return "Evaluate the performance of batch trained clusterers.";
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void acceptClusterer(final BatchClustererEvent ce)
/*  46:    */   {
/*  47:101 */     if (ce.getTestSet().isStructureOnly()) {
/*  48:102 */       return;
/*  49:    */     }
/*  50:    */     try
/*  51:    */     {
/*  52:105 */       if (this.m_evaluateThread == null)
/*  53:    */       {
/*  54:106 */         this.m_evaluateThread = new Thread()
/*  55:    */         {
/*  56:    */           public void run()
/*  57:    */           {
/*  58:110 */             boolean numericClass = false;
/*  59:    */             try
/*  60:    */             {
/*  61:113 */               if (ce.getSetNumber() == 1)
/*  62:    */               {
/*  63:116 */                 ClustererPerformanceEvaluator.this.m_eval = new ClusterEvaluation();
/*  64:117 */                 ClustererPerformanceEvaluator.this.m_clusterer = ce.getClusterer();
/*  65:118 */                 ClustererPerformanceEvaluator.this.m_eval.setClusterer(ClustererPerformanceEvaluator.this.m_clusterer);
/*  66:    */               }
/*  67:121 */               if (ce.getSetNumber() <= ce.getMaxSetNumber())
/*  68:    */               {
/*  69:123 */                 if (ClustererPerformanceEvaluator.this.m_logger != null) {
/*  70:124 */                   ClustererPerformanceEvaluator.this.m_logger.statusMessage(ClustererPerformanceEvaluator.this.statusMessagePrefix() + "Evaluating (" + ce.getSetNumber() + ")...");
/*  71:    */                 }
/*  72:127 */                 ClustererPerformanceEvaluator.this.m_visual.setAnimated();
/*  73:128 */                 if ((ce.getTestSet().getDataSet().classIndex() != -1) && (ce.getTestSet().getDataSet().classAttribute().isNumeric()))
/*  74:    */                 {
/*  75:130 */                   numericClass = true;
/*  76:131 */                   ce.getTestSet().getDataSet().setClassIndex(-1);
/*  77:    */                 }
/*  78:133 */                 ClustererPerformanceEvaluator.this.m_eval.evaluateClusterer(ce.getTestSet().getDataSet());
/*  79:    */               }
/*  80:136 */               if (ce.getSetNumber() == ce.getMaxSetNumber())
/*  81:    */               {
/*  82:137 */                 String textTitle = ClustererPerformanceEvaluator.this.m_clusterer.getClass().getName();
/*  83:138 */                 textTitle = textTitle.substring(textTitle.lastIndexOf('.') + 1, textTitle.length());
/*  84:    */                 String test;
/*  85:    */                 String test;
/*  86:141 */                 if (ce.getTestOrTrain() == 0) {
/*  87:142 */                   test = "test";
/*  88:    */                 } else {
/*  89:144 */                   test = "training";
/*  90:    */                 }
/*  91:146 */                 String resultT = "=== Evaluation result for " + test + " instances ===\n\n" + "Scheme: " + textTitle + "\n" + "Relation: " + ce.getTestSet().getDataSet().relationName() + "\n\n" + ClustererPerformanceEvaluator.this.m_eval.clusterResultsToString();
/*  92:150 */                 if (numericClass) {
/*  93:151 */                   resultT = resultT + "\n\nNo class based evaluation possible. Class attribute has to be nominal.";
/*  94:    */                 }
/*  95:154 */                 TextEvent te = new TextEvent(ClustererPerformanceEvaluator.this, resultT, textTitle);
/*  96:    */                 
/*  97:156 */                 ClustererPerformanceEvaluator.this.notifyTextListeners(te);
/*  98:157 */                 if (ClustererPerformanceEvaluator.this.m_logger != null) {
/*  99:158 */                   ClustererPerformanceEvaluator.this.m_logger.statusMessage(ClustererPerformanceEvaluator.this.statusMessagePrefix() + "Finished.");
/* 100:    */                 }
/* 101:    */               }
/* 102:    */             }
/* 103:    */             catch (Exception ex)
/* 104:    */             {
/* 105:163 */               ClustererPerformanceEvaluator.this.stop();
/* 106:164 */               if (ClustererPerformanceEvaluator.this.m_logger != null)
/* 107:    */               {
/* 108:165 */                 ClustererPerformanceEvaluator.this.m_logger.statusMessage(ClustererPerformanceEvaluator.this.statusMessagePrefix() + "ERROR (see log for details");
/* 109:    */                 
/* 110:167 */                 ClustererPerformanceEvaluator.this.m_logger.logMessage("[ClustererPerformanceEvaluator] " + ClustererPerformanceEvaluator.this.statusMessagePrefix() + " problem while evaluating clusterer. " + ex.getMessage());
/* 111:    */               }
/* 112:171 */               ex.printStackTrace();
/* 113:    */             }
/* 114:    */             finally
/* 115:    */             {
/* 116:174 */               ClustererPerformanceEvaluator.this.m_visual.setStatic();
/* 117:175 */               ClustererPerformanceEvaluator.this.m_evaluateThread = null;
/* 118:176 */               if ((isInterrupted()) && 
/* 119:177 */                 (ClustererPerformanceEvaluator.this.m_logger != null))
/* 120:    */               {
/* 121:178 */                 ClustererPerformanceEvaluator.this.m_logger.logMessage("[" + ClustererPerformanceEvaluator.this.getCustomName() + "] Evaluation interrupted!");
/* 122:    */                 
/* 123:180 */                 ClustererPerformanceEvaluator.this.m_logger.statusMessage(ClustererPerformanceEvaluator.this.statusMessagePrefix() + "INTERRUPTED");
/* 124:    */               }
/* 125:183 */               ClustererPerformanceEvaluator.this.block(false);
/* 126:    */             }
/* 127:    */           }
/* 128:186 */         };
/* 129:187 */         this.m_evaluateThread.setPriority(1);
/* 130:188 */         this.m_evaluateThread.start();
/* 131:    */         
/* 132:    */ 
/* 133:    */ 
/* 134:192 */         block(true);
/* 135:    */         
/* 136:194 */         this.m_evaluateThread = null;
/* 137:    */       }
/* 138:    */     }
/* 139:    */     catch (Exception ex)
/* 140:    */     {
/* 141:197 */       ex.printStackTrace();
/* 142:    */     }
/* 143:    */   }
/* 144:    */   
/* 145:    */   public boolean isBusy()
/* 146:    */   {
/* 147:209 */     return this.m_evaluateThread != null;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public void stop()
/* 151:    */   {
/* 152:219 */     if ((this.m_listenee instanceof BeanCommon)) {
/* 153:221 */       ((BeanCommon)this.m_listenee).stop();
/* 154:    */     }
/* 155:225 */     if (this.m_evaluateThread != null)
/* 156:    */     {
/* 157:226 */       this.m_evaluateThread.interrupt();
/* 158:227 */       this.m_evaluateThread.stop();
/* 159:228 */       this.m_evaluateThread = null;
/* 160:229 */       this.m_visual.setStatic();
/* 161:    */     }
/* 162:    */   }
/* 163:    */   
/* 164:    */   private synchronized void block(boolean tf)
/* 165:    */   {
/* 166:240 */     if (tf) {
/* 167:    */       try
/* 168:    */       {
/* 169:243 */         if ((this.m_evaluateThread != null) && (this.m_evaluateThread.isAlive())) {
/* 170:244 */           wait();
/* 171:    */         }
/* 172:    */       }
/* 173:    */       catch (InterruptedException ex) {}
/* 174:    */     } else {
/* 175:249 */       notifyAll();
/* 176:    */     }
/* 177:    */   }
/* 178:    */   
/* 179:    */   public Enumeration<String> enumerateRequests()
/* 180:    */   {
/* 181:260 */     Vector<String> newVector = new Vector(0);
/* 182:261 */     if (this.m_evaluateThread != null) {
/* 183:262 */       newVector.addElement("Stop");
/* 184:    */     }
/* 185:264 */     return newVector.elements();
/* 186:    */   }
/* 187:    */   
/* 188:    */   public void performRequest(String request)
/* 189:    */   {
/* 190:275 */     if (request.compareTo("Stop") == 0) {
/* 191:276 */       stop();
/* 192:    */     } else {
/* 193:278 */       throw new IllegalArgumentException(request + " not supported (ClustererPerformanceEvaluator)");
/* 194:    */     }
/* 195:    */   }
/* 196:    */   
/* 197:    */   public synchronized void addTextListener(TextListener cl)
/* 198:    */   {
/* 199:290 */     this.m_textListeners.addElement(cl);
/* 200:    */   }
/* 201:    */   
/* 202:    */   public synchronized void removeTextListener(TextListener cl)
/* 203:    */   {
/* 204:299 */     this.m_textListeners.remove(cl);
/* 205:    */   }
/* 206:    */   
/* 207:    */   private void notifyTextListeners(TextEvent te)
/* 208:    */   {
/* 209:    */     Vector<TextListener> l;
/* 210:310 */     synchronized (this)
/* 211:    */     {
/* 212:311 */       l = (Vector)this.m_textListeners.clone();
/* 213:    */     }
/* 214:313 */     if (l.size() > 0) {
/* 215:314 */       for (int i = 0; i < l.size(); i++) {
/* 216:317 */         ((TextListener)l.elementAt(i)).acceptText(te);
/* 217:    */       }
/* 218:    */     }
/* 219:    */   }
/* 220:    */   
/* 221:    */   public boolean eventGeneratable(String eventName)
/* 222:    */   {
/* 223:332 */     if (this.m_listenee == null) {
/* 224:333 */       return false;
/* 225:    */     }
/* 226:336 */     if (((this.m_listenee instanceof EventConstraints)) && 
/* 227:337 */       (!((EventConstraints)this.m_listenee).eventGeneratable("batchClusterer"))) {
/* 228:338 */       return false;
/* 229:    */     }
/* 230:341 */     return true;
/* 231:    */   }
/* 232:    */   
/* 233:    */   private String statusMessagePrefix()
/* 234:    */   {
/* 235:345 */     return getCustomName() + "$" + hashCode() + "|";
/* 236:    */   }
/* 237:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ClustererPerformanceEvaluator
 * JD-Core Version:    0.7.0.1
 */