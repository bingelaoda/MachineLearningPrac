/*   1:    */ package weka.classifiers;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.BatchPredictor;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.CapabilitiesHandler;
/*  11:    */ import weka.core.CapabilitiesIgnorer;
/*  12:    */ import weka.core.CommandlineRunnable;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.OptionHandler;
/*  17:    */ import weka.core.RevisionHandler;
/*  18:    */ import weka.core.RevisionUtils;
/*  19:    */ import weka.core.SerializedObject;
/*  20:    */ import weka.core.Utils;
/*  21:    */ 
/*  22:    */ public abstract class AbstractClassifier
/*  23:    */   implements Classifier, BatchPredictor, Cloneable, Serializable, OptionHandler, CapabilitiesHandler, RevisionHandler, CapabilitiesIgnorer, CommandlineRunnable
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = 6502780192411755341L;
/*  26: 60 */   protected boolean m_Debug = false;
/*  27: 63 */   protected boolean m_DoNotCheckCapabilities = false;
/*  28: 68 */   public static int NUM_DECIMAL_PLACES_DEFAULT = 2;
/*  29: 69 */   protected int m_numDecimalPlaces = NUM_DECIMAL_PLACES_DEFAULT;
/*  30: 72 */   public static String BATCH_SIZE_DEFAULT = "100";
/*  31: 73 */   protected String m_BatchSize = BATCH_SIZE_DEFAULT;
/*  32:    */   
/*  33:    */   public static Classifier forName(String classifierName, String[] options)
/*  34:    */     throws Exception
/*  35:    */   {
/*  36: 91 */     return (AbstractClassifier)Utils.forName(Classifier.class, classifierName, options);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public static Classifier makeCopy(Classifier model)
/*  40:    */     throws Exception
/*  41:    */   {
/*  42:104 */     return (Classifier)new SerializedObject(model).getObject();
/*  43:    */   }
/*  44:    */   
/*  45:    */   public static Classifier[] makeCopies(Classifier model, int num)
/*  46:    */     throws Exception
/*  47:    */   {
/*  48:119 */     if (model == null) {
/*  49:120 */       throw new Exception("No model classifier set");
/*  50:    */     }
/*  51:122 */     Classifier[] classifiers = new Classifier[num];
/*  52:123 */     SerializedObject so = new SerializedObject(model);
/*  53:124 */     for (int i = 0; i < classifiers.length; i++) {
/*  54:125 */       classifiers[i] = ((Classifier)so.getObject());
/*  55:    */     }
/*  56:127 */     return classifiers;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public static void runClassifier(Classifier classifier, String[] options)
/*  60:    */   {
/*  61:    */     try
/*  62:    */     {
/*  63:138 */       if ((classifier instanceof CommandlineRunnable)) {
/*  64:139 */         ((CommandlineRunnable)classifier).preExecution();
/*  65:    */       }
/*  66:141 */       System.out.println(Evaluation.evaluateModel(classifier, options));
/*  67:    */     }
/*  68:    */     catch (Exception e)
/*  69:    */     {
/*  70:143 */       if (((e.getMessage() != null) && (e.getMessage().indexOf("General options") == -1)) || (e.getMessage() == null)) {
/*  71:146 */         e.printStackTrace();
/*  72:    */       } else {
/*  73:148 */         System.err.println(e.getMessage());
/*  74:    */       }
/*  75:    */     }
/*  76:151 */     if ((classifier instanceof CommandlineRunnable)) {
/*  77:    */       try
/*  78:    */       {
/*  79:153 */         ((CommandlineRunnable)classifier).postExecution();
/*  80:    */       }
/*  81:    */       catch (Exception ex)
/*  82:    */       {
/*  83:155 */         ex.printStackTrace();
/*  84:    */       }
/*  85:    */     }
/*  86:    */   }
/*  87:    */   
/*  88:    */   public double classifyInstance(Instance instance)
/*  89:    */     throws Exception
/*  90:    */   {
/*  91:173 */     double[] dist = distributionForInstance(instance);
/*  92:174 */     if (dist == null) {
/*  93:175 */       throw new Exception("Null distribution predicted");
/*  94:    */     }
/*  95:177 */     switch (instance.classAttribute().type())
/*  96:    */     {
/*  97:    */     case 1: 
/*  98:179 */       double max = 0.0D;
/*  99:180 */       int maxIndex = 0;
/* 100:182 */       for (int i = 0; i < dist.length; i++) {
/* 101:183 */         if (dist[i] > max)
/* 102:    */         {
/* 103:184 */           maxIndex = i;
/* 104:185 */           max = dist[i];
/* 105:    */         }
/* 106:    */       }
/* 107:188 */       if (max > 0.0D) {
/* 108:189 */         return maxIndex;
/* 109:    */       }
/* 110:191 */       return Utils.missingValue();
/* 111:    */     case 0: 
/* 112:    */     case 3: 
/* 113:195 */       return dist[0];
/* 114:    */     }
/* 115:197 */     return Utils.missingValue();
/* 116:    */   }
/* 117:    */   
/* 118:    */   public double[] distributionForInstance(Instance instance)
/* 119:    */     throws Exception
/* 120:    */   {
/* 121:216 */     double[] dist = new double[instance.numClasses()];
/* 122:217 */     switch (instance.classAttribute().type())
/* 123:    */     {
/* 124:    */     case 1: 
/* 125:219 */       double classification = classifyInstance(instance);
/* 126:220 */       if (Utils.isMissingValue(classification)) {
/* 127:221 */         return dist;
/* 128:    */       }
/* 129:223 */       dist[((int)classification)] = 1.0D;
/* 130:    */       
/* 131:225 */       return dist;
/* 132:    */     case 0: 
/* 133:    */     case 3: 
/* 134:228 */       dist[0] = classifyInstance(instance);
/* 135:229 */       return dist;
/* 136:    */     }
/* 137:231 */     return dist;
/* 138:    */   }
/* 139:    */   
/* 140:    */   public Enumeration<Option> listOptions()
/* 141:    */   {
/* 142:243 */     Vector<Option> newVector = Option.listOptionsForClassHierarchy(getClass(), AbstractClassifier.class);
/* 143:    */     
/* 144:    */ 
/* 145:246 */     newVector.addElement(new Option("\tIf set, classifier is run in debug mode and\n\tmay output additional info to the console", "output-debug-info", 0, "-output-debug-info"));
/* 146:    */     
/* 147:    */ 
/* 148:    */ 
/* 149:250 */     newVector.addElement(new Option("\tIf set, classifier capabilities are not checked before classifier is built\n\t(use with caution).", "-do-not-check-capabilities", 0, "-do-not-check-capabilities"));
/* 150:    */     
/* 151:    */ 
/* 152:    */ 
/* 153:254 */     newVector.addElement(new Option("\tThe number of decimal places for the output of numbers in the model (default " + this.m_numDecimalPlaces + ").", "num-decimal-places", 1, "-num-decimal-places"));
/* 154:    */     
/* 155:    */ 
/* 156:    */ 
/* 157:258 */     newVector.addElement(new Option("\tThe desired batch size for batch prediction  (default " + this.m_BatchSize + ").", "batch-size", 1, "-batch-size"));
/* 158:    */     
/* 159:    */ 
/* 160:    */ 
/* 161:    */ 
/* 162:263 */     return newVector.elements();
/* 163:    */   }
/* 164:    */   
/* 165:    */   public String[] getOptions()
/* 166:    */   {
/* 167:274 */     Vector<String> options = new Vector();
/* 168:275 */     for (String s : Option.getOptionsForHierarchy(this, AbstractClassifier.class)) {
/* 169:277 */       options.add(s);
/* 170:    */     }
/* 171:280 */     if (getDebug()) {
/* 172:281 */       options.add("-output-debug-info");
/* 173:    */     }
/* 174:283 */     if (getDoNotCheckCapabilities()) {
/* 175:284 */       options.add("-do-not-check-capabilities");
/* 176:    */     }
/* 177:286 */     if (getNumDecimalPlaces() != NUM_DECIMAL_PLACES_DEFAULT)
/* 178:    */     {
/* 179:287 */       options.add("-num-decimal-places");
/* 180:288 */       options.add("" + getNumDecimalPlaces());
/* 181:    */     }
/* 182:290 */     if (!getBatchSize().equals(BATCH_SIZE_DEFAULT))
/* 183:    */     {
/* 184:291 */       options.add("-batch-size");
/* 185:292 */       options.add("" + getBatchSize());
/* 186:    */     }
/* 187:294 */     return (String[])options.toArray(new String[0]);
/* 188:    */   }
/* 189:    */   
/* 190:    */   public void setOptions(String[] options)
/* 191:    */     throws Exception
/* 192:    */   {
/* 193:325 */     Option.setOptionsForHierarchy(options, this, AbstractClassifier.class);
/* 194:326 */     setDebug(Utils.getFlag("output-debug-info", options));
/* 195:327 */     setDoNotCheckCapabilities(Utils.getFlag("do-not-check-capabilities", options));
/* 196:    */     
/* 197:    */ 
/* 198:330 */     String optionString = Utils.getOption("num-decimal-places", options);
/* 199:331 */     if (optionString.length() != 0) {
/* 200:332 */       setNumDecimalPlaces(new Integer(optionString).intValue());
/* 201:    */     }
/* 202:334 */     optionString = Utils.getOption("batch-size", options);
/* 203:335 */     if (optionString.length() != 0) {
/* 204:336 */       setBatchSize(optionString);
/* 205:    */     }
/* 206:    */   }
/* 207:    */   
/* 208:    */   public boolean getDebug()
/* 209:    */   {
/* 210:347 */     return this.m_Debug;
/* 211:    */   }
/* 212:    */   
/* 213:    */   public void setDebug(boolean debug)
/* 214:    */   {
/* 215:357 */     this.m_Debug = debug;
/* 216:    */   }
/* 217:    */   
/* 218:    */   public String debugTipText()
/* 219:    */   {
/* 220:367 */     return "If set to true, classifier may output additional info to the console.";
/* 221:    */   }
/* 222:    */   
/* 223:    */   public boolean getDoNotCheckCapabilities()
/* 224:    */   {
/* 225:379 */     return this.m_DoNotCheckCapabilities;
/* 226:    */   }
/* 227:    */   
/* 228:    */   public void setDoNotCheckCapabilities(boolean doNotCheckCapabilities)
/* 229:    */   {
/* 230:390 */     this.m_DoNotCheckCapabilities = doNotCheckCapabilities;
/* 231:    */   }
/* 232:    */   
/* 233:    */   public String doNotCheckCapabilitiesTipText()
/* 234:    */   {
/* 235:400 */     return "If set, classifier capabilities are not checked before classifier is built (Use with caution to reduce runtime).";
/* 236:    */   }
/* 237:    */   
/* 238:    */   public String numDecimalPlacesTipText()
/* 239:    */   {
/* 240:411 */     return "The number of decimal places to be used for the output of numbers in the model.";
/* 241:    */   }
/* 242:    */   
/* 243:    */   public int getNumDecimalPlaces()
/* 244:    */   {
/* 245:419 */     return this.m_numDecimalPlaces;
/* 246:    */   }
/* 247:    */   
/* 248:    */   public void setNumDecimalPlaces(int num)
/* 249:    */   {
/* 250:426 */     this.m_numDecimalPlaces = num;
/* 251:    */   }
/* 252:    */   
/* 253:    */   public String batchSizeTipText()
/* 254:    */   {
/* 255:436 */     return "The preferred number of instances to process if batch prediction is being performed. More or fewer instances may be provided, but this gives implementations a chance to specify a preferred batch size.";
/* 256:    */   }
/* 257:    */   
/* 258:    */   public void setBatchSize(String size)
/* 259:    */   {
/* 260:448 */     this.m_BatchSize = size;
/* 261:    */   }
/* 262:    */   
/* 263:    */   public String getBatchSize()
/* 264:    */   {
/* 265:458 */     return this.m_BatchSize;
/* 266:    */   }
/* 267:    */   
/* 268:    */   public boolean implementsMoreEfficientBatchPrediction()
/* 269:    */   {
/* 270:471 */     return false;
/* 271:    */   }
/* 272:    */   
/* 273:    */   public double[][] distributionsForInstances(Instances batch)
/* 274:    */     throws Exception
/* 275:    */   {
/* 276:489 */     double[][] batchPreds = new double[batch.numInstances()][];
/* 277:490 */     for (int i = 0; i < batch.numInstances(); i++) {
/* 278:491 */       batchPreds[i] = distributionForInstance(batch.instance(i));
/* 279:    */     }
/* 280:494 */     return batchPreds;
/* 281:    */   }
/* 282:    */   
/* 283:    */   public Capabilities getCapabilities()
/* 284:    */   {
/* 285:508 */     Capabilities result = new Capabilities(this);
/* 286:509 */     result.enableAll();
/* 287:    */     
/* 288:511 */     return result;
/* 289:    */   }
/* 290:    */   
/* 291:    */   public String getRevision()
/* 292:    */   {
/* 293:521 */     return RevisionUtils.extract("$Revision: 12701 $");
/* 294:    */   }
/* 295:    */   
/* 296:    */   public void preExecution()
/* 297:    */     throws Exception
/* 298:    */   {}
/* 299:    */   
/* 300:    */   public void run(Object toRun, String[] options)
/* 301:    */     throws Exception
/* 302:    */   {
/* 303:543 */     if (!(toRun instanceof Classifier)) {
/* 304:544 */       throw new IllegalArgumentException("Object to run is not a Classifier!");
/* 305:    */     }
/* 306:546 */     runClassifier((Classifier)toRun, options);
/* 307:    */   }
/* 308:    */   
/* 309:    */   public void postExecution()
/* 310:    */     throws Exception
/* 311:    */   {}
/* 312:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.AbstractClassifier
 * JD-Core Version:    0.7.0.1
 */