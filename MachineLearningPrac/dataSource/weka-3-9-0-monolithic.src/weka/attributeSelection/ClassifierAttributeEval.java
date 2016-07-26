/*   1:    */ package weka.attributeSelection;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.BitSet;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.HashSet;
/*   7:    */ import java.util.Set;
/*   8:    */ import java.util.Vector;
/*   9:    */ import java.util.concurrent.Callable;
/*  10:    */ import java.util.concurrent.ExecutorService;
/*  11:    */ import java.util.concurrent.Executors;
/*  12:    */ import java.util.concurrent.Future;
/*  13:    */ import weka.classifiers.Classifier;
/*  14:    */ import weka.core.Capabilities;
/*  15:    */ import weka.core.Instances;
/*  16:    */ import weka.core.Option;
/*  17:    */ import weka.core.OptionHandler;
/*  18:    */ import weka.core.RevisionUtils;
/*  19:    */ import weka.core.SelectedTag;
/*  20:    */ import weka.core.Utils;
/*  21:    */ 
/*  22:    */ public class ClassifierAttributeEval
/*  23:    */   extends ASEvaluation
/*  24:    */   implements AttributeEvaluator, OptionHandler
/*  25:    */ {
/*  26:    */   private static final long serialVersionUID = 2442390690522602284L;
/*  27:    */   protected Instances m_trainInstances;
/*  28:    */   protected double[] m_merit;
/*  29:123 */   protected WrapperSubsetEval m_wrapperTemplate = new WrapperSubsetEval();
/*  30:126 */   protected String m_wrapperSetup = "";
/*  31:    */   protected boolean m_leaveOneOut;
/*  32:    */   protected transient ExecutorService m_pool;
/*  33:138 */   protected int m_executionSlots = 1;
/*  34:    */   
/*  35:    */   public ClassifierAttributeEval()
/*  36:    */   {
/*  37:144 */     resetOptions();
/*  38:    */   }
/*  39:    */   
/*  40:    */   public String globalInfo()
/*  41:    */   {
/*  42:154 */     return "ClassifierAttributeEval :\n\nEvaluates the worth of an attribute by using a user-specified classifier.\n";
/*  43:    */   }
/*  44:    */   
/*  45:    */   public Enumeration<Option> listOptions()
/*  46:    */   {
/*  47:165 */     Vector<Option> result = new Vector();
/*  48:    */     
/*  49:167 */     Enumeration<Option> wrapperOpts = this.m_wrapperTemplate.listOptions();
/*  50:168 */     while (wrapperOpts.hasMoreElements()) {
/*  51:169 */       result.addElement(wrapperOpts.nextElement());
/*  52:    */     }
/*  53:172 */     result.addElement(new Option("\tEvaluate an attribute by measuring the impact of leaving it out\n\tfrom the full set instead of considering its worth in isolation", "L", 0, "-L"));
/*  54:    */     
/*  55:    */ 
/*  56:    */ 
/*  57:    */ 
/*  58:177 */     result.addElement(new Option("\tNumber of attributes to evaluate in parallel.\n\tDefault = 1 (i.e. no parallelism)", "execution-slots", 1, "-execution-slots <integer>"));
/*  59:    */     
/*  60:    */ 
/*  61:    */ 
/*  62:    */ 
/*  63:182 */     return result.elements();
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void setOptions(String[] options)
/*  67:    */     throws Exception
/*  68:    */   {
/*  69:248 */     resetOptions();
/*  70:    */     
/*  71:250 */     this.m_leaveOneOut = Utils.getFlag('L', options);
/*  72:251 */     String slots = Utils.getOption("execution-slots", options);
/*  73:252 */     if (slots.length() > 0) {
/*  74:253 */       this.m_executionSlots = Integer.parseInt(slots);
/*  75:    */     }
/*  76:255 */     this.m_wrapperTemplate.setOptions(options);
/*  77:    */     
/*  78:257 */     Utils.checkForRemainingOptions(options);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public String[] getOptions()
/*  82:    */   {
/*  83:269 */     ArrayList<String> result = new ArrayList();
/*  84:271 */     if (this.m_leaveOneOut) {
/*  85:272 */       result.add("-L");
/*  86:    */     }
/*  87:275 */     result.add("-execution-slots");
/*  88:276 */     result.add("" + this.m_executionSlots);
/*  89:278 */     for (String o : this.m_wrapperTemplate.getOptions()) {
/*  90:279 */       result.add(o);
/*  91:    */     }
/*  92:282 */     return (String[])result.toArray(new String[result.size()]);
/*  93:    */   }
/*  94:    */   
/*  95:    */   public String leaveOneAttributeOutTipText()
/*  96:    */   {
/*  97:291 */     return "Evaluate an attribute by measuring the impact of leaving it out from the full set instead of considering its worth in isolation.";
/*  98:    */   }
/*  99:    */   
/* 100:    */   public void setLeaveOneAttributeOut(boolean l)
/* 101:    */   {
/* 102:304 */     this.m_leaveOneOut = l;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public boolean getLeaveOneAttributeOut()
/* 106:    */   {
/* 107:316 */     return this.m_leaveOneOut;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public String numToEvaluateInParallelTipText()
/* 111:    */   {
/* 112:325 */     return "The number of attributes to evaluate in parallel";
/* 113:    */   }
/* 114:    */   
/* 115:    */   public void setNumToEvaluateInParallel(int n)
/* 116:    */   {
/* 117:334 */     this.m_executionSlots = n;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public int getNumToEvaluateInParallel()
/* 121:    */   {
/* 122:343 */     return this.m_executionSlots;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public void setIRClassValue(String val)
/* 126:    */   {
/* 127:355 */     this.m_wrapperTemplate.setIRClassValue(val);
/* 128:    */   }
/* 129:    */   
/* 130:    */   public String getIRClassValue()
/* 131:    */   {
/* 132:367 */     return this.m_wrapperTemplate.getIRClassValue();
/* 133:    */   }
/* 134:    */   
/* 135:    */   public String IRClassValueTipText()
/* 136:    */   {
/* 137:377 */     return "The class label, or 1-based index of the class label, to use when evaluating subsets with an IR metric (such as f-measure or AUC. Leaving this unset will result in the class frequency weighted average of the metric being used.";
/* 138:    */   }
/* 139:    */   
/* 140:    */   public String evaluationMeasureTipText()
/* 141:    */   {
/* 142:390 */     return "The measure used to evaluate the performance of attribute combinations.";
/* 143:    */   }
/* 144:    */   
/* 145:    */   public SelectedTag getEvaluationMeasure()
/* 146:    */   {
/* 147:400 */     return this.m_wrapperTemplate.getEvaluationMeasure();
/* 148:    */   }
/* 149:    */   
/* 150:    */   public void setEvaluationMeasure(SelectedTag newMethod)
/* 151:    */   {
/* 152:410 */     this.m_wrapperTemplate.setEvaluationMeasure(newMethod);
/* 153:    */   }
/* 154:    */   
/* 155:    */   public String thresholdTipText()
/* 156:    */   {
/* 157:420 */     return this.m_wrapperTemplate.thresholdTipText();
/* 158:    */   }
/* 159:    */   
/* 160:    */   public void setThreshold(double t)
/* 161:    */   {
/* 162:429 */     this.m_wrapperTemplate.setThreshold(t);
/* 163:    */   }
/* 164:    */   
/* 165:    */   public double getThreshold()
/* 166:    */   {
/* 167:438 */     return this.m_wrapperTemplate.getThreshold();
/* 168:    */   }
/* 169:    */   
/* 170:    */   public String foldsTipText()
/* 171:    */   {
/* 172:448 */     return this.m_wrapperTemplate.foldsTipText();
/* 173:    */   }
/* 174:    */   
/* 175:    */   public void setFolds(int f)
/* 176:    */   {
/* 177:457 */     this.m_wrapperTemplate.setFolds(f);
/* 178:    */   }
/* 179:    */   
/* 180:    */   public int getFolds()
/* 181:    */   {
/* 182:466 */     return this.m_wrapperTemplate.getFolds();
/* 183:    */   }
/* 184:    */   
/* 185:    */   public String seedTipText()
/* 186:    */   {
/* 187:476 */     return this.m_wrapperTemplate.seedTipText();
/* 188:    */   }
/* 189:    */   
/* 190:    */   public void setSeed(int s)
/* 191:    */   {
/* 192:485 */     this.m_wrapperTemplate.setSeed(s);
/* 193:    */   }
/* 194:    */   
/* 195:    */   public int getSeed()
/* 196:    */   {
/* 197:494 */     return this.m_wrapperTemplate.getSeed();
/* 198:    */   }
/* 199:    */   
/* 200:    */   public String classifierTipText()
/* 201:    */   {
/* 202:504 */     return this.m_wrapperTemplate.classifierTipText();
/* 203:    */   }
/* 204:    */   
/* 205:    */   public void setClassifier(Classifier newClassifier)
/* 206:    */   {
/* 207:513 */     this.m_wrapperTemplate.setClassifier(newClassifier);
/* 208:    */   }
/* 209:    */   
/* 210:    */   public Classifier getClassifier()
/* 211:    */   {
/* 212:522 */     return this.m_wrapperTemplate.getClassifier();
/* 213:    */   }
/* 214:    */   
/* 215:    */   public Capabilities getCapabilities()
/* 216:    */   {
/* 217:535 */     Capabilities result = this.m_wrapperTemplate.getClassifier().getCapabilities();
/* 218:536 */     result.setOwner(this);
/* 219:    */     
/* 220:538 */     return result;
/* 221:    */   }
/* 222:    */   
/* 223:    */   public void buildEvaluator(final Instances data)
/* 224:    */     throws Exception
/* 225:    */   {
/* 226:550 */     getCapabilities().testWithFail(data);
/* 227:    */     
/* 228:552 */     this.m_trainInstances = new Instances(data, 0);
/* 229:    */     
/* 230:554 */     double baseMerit = 0.0D;
/* 231:555 */     this.m_merit = new double[data.numAttributes()];
/* 232:556 */     this.m_pool = Executors.newFixedThreadPool(this.m_executionSlots);
/* 233:    */     
/* 234:558 */     Set<Future<double[]>> results = new HashSet();
/* 235:560 */     for (int i = -1; i < data.numAttributes(); i++) {
/* 236:561 */       if (i != data.classIndex())
/* 237:    */       {
/* 238:563 */         final int attIndex = i;
/* 239:564 */         Future<double[]> futureEval = this.m_pool.submit(new Callable()
/* 240:    */         {
/* 241:    */           public double[] call()
/* 242:    */             throws Exception
/* 243:    */           {
/* 244:567 */             double[] eval = new double[2];
/* 245:568 */             eval[0] = attIndex;
/* 246:569 */             WrapperSubsetEval evaluator = new WrapperSubsetEval();
/* 247:570 */             evaluator.setOptions(ClassifierAttributeEval.this.m_wrapperTemplate.getOptions());
/* 248:571 */             evaluator.buildEvaluator(data);
/* 249:572 */             if (ClassifierAttributeEval.this.m_wrapperSetup.length() == 0) {
/* 250:573 */               ClassifierAttributeEval.this.m_wrapperSetup = evaluator.toString();
/* 251:    */             }
/* 252:575 */             BitSet b = new BitSet(data.numAttributes());
/* 253:576 */             if (ClassifierAttributeEval.this.m_leaveOneOut)
/* 254:    */             {
/* 255:577 */               b.set(0, data.numAttributes());
/* 256:578 */               b.set(data.classIndex(), false);
/* 257:    */             }
/* 258:580 */             if (attIndex >= 0) {
/* 259:581 */               b.set(attIndex, !ClassifierAttributeEval.this.m_leaveOneOut);
/* 260:    */             }
/* 261:583 */             eval[1] = evaluator.evaluateSubset(b);
/* 262:    */             
/* 263:585 */             return eval;
/* 264:    */           }
/* 265:588 */         });
/* 266:589 */         results.add(futureEval);
/* 267:    */       }
/* 268:    */     }
/* 269:593 */     for (Future<double[]> f : results) {
/* 270:594 */       if (((double[])f.get())[0] != -1.0D) {
/* 271:595 */         this.m_merit[((int)((double[])f.get())[0])] = ((double[])f.get())[1];
/* 272:    */       } else {
/* 273:597 */         baseMerit = ((double[])f.get())[1];
/* 274:    */       }
/* 275:    */     }
/* 276:601 */     for (int i = 0; i < data.numAttributes(); i++) {
/* 277:602 */       this.m_merit[i] = (this.m_leaveOneOut ? baseMerit - this.m_merit[i] : this.m_merit[i] - baseMerit);
/* 278:    */     }
/* 279:606 */     this.m_pool.shutdown();
/* 280:607 */     this.m_trainInstances = new Instances(this.m_trainInstances, 0);
/* 281:    */   }
/* 282:    */   
/* 283:    */   protected void resetOptions()
/* 284:    */   {
/* 285:614 */     this.m_trainInstances = null;
/* 286:615 */     this.m_wrapperTemplate = new WrapperSubsetEval();
/* 287:616 */     this.m_wrapperSetup = "";
/* 288:    */   }
/* 289:    */   
/* 290:    */   public double evaluateAttribute(int attribute)
/* 291:    */     throws Exception
/* 292:    */   {
/* 293:629 */     return this.m_merit[attribute];
/* 294:    */   }
/* 295:    */   
/* 296:    */   public String toString()
/* 297:    */   {
/* 298:639 */     StringBuffer text = new StringBuffer();
/* 299:641 */     if (this.m_trainInstances == null)
/* 300:    */     {
/* 301:642 */       text.append("\tClassifier feature evaluator has not been built yet");
/* 302:    */     }
/* 303:    */     else
/* 304:    */     {
/* 305:644 */       text.append("\tClassifier feature evaluator " + (this.m_leaveOneOut ? "(leave one out)" : "") + "\n\n");
/* 306:    */       
/* 307:646 */       text.append("\tUsing ");
/* 308:    */       
/* 309:648 */       text.append(this.m_wrapperSetup);
/* 310:    */     }
/* 311:650 */     text.append("\n");
/* 312:    */     
/* 313:652 */     return text.toString();
/* 314:    */   }
/* 315:    */   
/* 316:    */   public String getRevision()
/* 317:    */   {
/* 318:662 */     return RevisionUtils.extract("$Revision: 11220 $");
/* 319:    */   }
/* 320:    */   
/* 321:    */   public static void main(String[] args)
/* 322:    */   {
/* 323:671 */     runEvaluator(new ClassifierAttributeEval(), args);
/* 324:    */   }
/* 325:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.ClassifierAttributeEval
 * JD-Core Version:    0.7.0.1
 */