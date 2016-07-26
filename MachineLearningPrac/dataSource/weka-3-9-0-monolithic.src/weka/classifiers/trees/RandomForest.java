/*   1:    */ package weka.classifiers.trees;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.AbstractClassifier;
/*   8:    */ import weka.classifiers.Classifier;
/*   9:    */ import weka.classifiers.meta.Bagging;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.OptionHandler;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.TechnicalInformation;
/*  15:    */ import weka.core.TechnicalInformation.Field;
/*  16:    */ import weka.core.TechnicalInformation.Type;
/*  17:    */ import weka.core.Utils;
/*  18:    */ import weka.gui.ProgrammaticProperty;
/*  19:    */ 
/*  20:    */ public class RandomForest
/*  21:    */   extends Bagging
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = 1116839470751428698L;
/*  24:    */   
/*  25:    */   protected int defaultNumberOfIterations()
/*  26:    */   {
/*  27:158 */     return 100;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public RandomForest()
/*  31:    */   {
/*  32:166 */     RandomTree rTree = new RandomTree();
/*  33:167 */     rTree.setDoNotCheckCapabilities(true);
/*  34:168 */     super.setClassifier(rTree);
/*  35:169 */     super.setRepresentCopiesUsingWeights(true);
/*  36:170 */     setNumIterations(defaultNumberOfIterations());
/*  37:    */   }
/*  38:    */   
/*  39:    */   public Capabilities getCapabilities()
/*  40:    */   {
/*  41:182 */     return new RandomTree().getCapabilities();
/*  42:    */   }
/*  43:    */   
/*  44:    */   protected String defaultClassifierString()
/*  45:    */   {
/*  46:193 */     return "weka.classifiers.trees.RandomTree";
/*  47:    */   }
/*  48:    */   
/*  49:    */   protected String[] defaultClassifierOptions()
/*  50:    */   {
/*  51:204 */     String[] args = { "-do-not-check-capabilities" };
/*  52:205 */     return args;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public String globalInfo()
/*  56:    */   {
/*  57:216 */     return "Class for constructing a forest of random trees.\n\nFor more information see: \n\n" + getTechnicalInformation().toString();
/*  58:    */   }
/*  59:    */   
/*  60:    */   public TechnicalInformation getTechnicalInformation()
/*  61:    */   {
/*  62:231 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  63:232 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Leo Breiman");
/*  64:233 */     result.setValue(TechnicalInformation.Field.YEAR, "2001");
/*  65:234 */     result.setValue(TechnicalInformation.Field.TITLE, "Random Forests");
/*  66:235 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Machine Learning");
/*  67:236 */     result.setValue(TechnicalInformation.Field.VOLUME, "45");
/*  68:237 */     result.setValue(TechnicalInformation.Field.NUMBER, "1");
/*  69:238 */     result.setValue(TechnicalInformation.Field.PAGES, "5-32");
/*  70:    */     
/*  71:240 */     return result;
/*  72:    */   }
/*  73:    */   
/*  74:    */   @ProgrammaticProperty
/*  75:    */   public void setClassifier(Classifier newClassifier)
/*  76:    */   {
/*  77:251 */     if (!(newClassifier instanceof RandomTree)) {
/*  78:252 */       throw new IllegalArgumentException("RandomForest: Argument of setClassifier() must be a RandomTree.");
/*  79:    */     }
/*  80:254 */     super.setClassifier(newClassifier);
/*  81:    */   }
/*  82:    */   
/*  83:    */   @ProgrammaticProperty
/*  84:    */   public void setRepresentCopiesUsingWeights(boolean representUsingWeights)
/*  85:    */   {
/*  86:265 */     if (!representUsingWeights) {
/*  87:266 */       throw new IllegalArgumentException("RandomForest: Argument of setRepresentCopiesUsingWeights() must be true.");
/*  88:    */     }
/*  89:268 */     super.setRepresentCopiesUsingWeights(representUsingWeights);
/*  90:    */   }
/*  91:    */   
/*  92:    */   public String numFeaturesTipText()
/*  93:    */   {
/*  94:278 */     return ((RandomTree)getClassifier()).KValueTipText();
/*  95:    */   }
/*  96:    */   
/*  97:    */   public int getNumFeatures()
/*  98:    */   {
/*  99:288 */     return ((RandomTree)getClassifier()).getKValue();
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void setNumFeatures(int newNumFeatures)
/* 103:    */   {
/* 104:298 */     ((RandomTree)getClassifier()).setKValue(newNumFeatures);
/* 105:    */   }
/* 106:    */   
/* 107:    */   public String maxDepthTipText()
/* 108:    */   {
/* 109:308 */     return ((RandomTree)getClassifier()).maxDepthTipText();
/* 110:    */   }
/* 111:    */   
/* 112:    */   public int getMaxDepth()
/* 113:    */   {
/* 114:317 */     return ((RandomTree)getClassifier()).getMaxDepth();
/* 115:    */   }
/* 116:    */   
/* 117:    */   public void setMaxDepth(int value)
/* 118:    */   {
/* 119:326 */     ((RandomTree)getClassifier()).setMaxDepth(value);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public String breakTiesRandomlyTipText()
/* 123:    */   {
/* 124:336 */     return ((RandomTree)getClassifier()).breakTiesRandomlyTipText();
/* 125:    */   }
/* 126:    */   
/* 127:    */   public boolean getBreakTiesRandomly()
/* 128:    */   {
/* 129:346 */     return ((RandomTree)getClassifier()).getBreakTiesRandomly();
/* 130:    */   }
/* 131:    */   
/* 132:    */   public void setBreakTiesRandomly(boolean newBreakTiesRandomly)
/* 133:    */   {
/* 134:356 */     ((RandomTree)getClassifier()).setBreakTiesRandomly(newBreakTiesRandomly);
/* 135:    */   }
/* 136:    */   
/* 137:    */   public String toString()
/* 138:    */   {
/* 139:367 */     if (this.m_Classifiers == null) {
/* 140:368 */       return "RandomForest: No model built yet.";
/* 141:    */     }
/* 142:370 */     StringBuffer buffer = new StringBuffer("RandomForest\n\n");
/* 143:371 */     buffer.append(super.toString());
/* 144:372 */     return buffer.toString();
/* 145:    */   }
/* 146:    */   
/* 147:    */   public Enumeration<Option> listOptions()
/* 148:    */   {
/* 149:383 */     Vector<Option> newVector = new Vector();
/* 150:    */     
/* 151:385 */     newVector.addElement(new Option("\tSize of each bag, as a percentage of the\n\ttraining set size. (default 100)", "P", 1, "-P"));
/* 152:    */     
/* 153:    */ 
/* 154:    */ 
/* 155:    */ 
/* 156:390 */     newVector.addElement(new Option("\tCalculate the out of bag error.", "O", 0, "-O"));
/* 157:    */     
/* 158:    */ 
/* 159:    */ 
/* 160:394 */     newVector.addElement(new Option("\tWhether to store out of bag predictions in internal evaluation object.", "store-out-of-bag-predictions", 0, "-store-out-of-bag-predictions"));
/* 161:    */     
/* 162:    */ 
/* 163:    */ 
/* 164:398 */     newVector.addElement(new Option("\tWhether to output complexity-based statistics when out-of-bag evaluation is performed.", "output-out-of-bag-complexity-statistics", 0, "-output-out-of-bag-complexity-statistics"));
/* 165:    */     
/* 166:    */ 
/* 167:    */ 
/* 168:402 */     newVector.addElement(new Option("\tPrint the individual classifiers in the output", "print", 0, "-print"));
/* 169:    */     
/* 170:    */ 
/* 171:405 */     newVector.addElement(new Option("\tNumber of iterations.\n\t(current value " + getNumIterations() + ")", "I", 1, "-I <num>"));
/* 172:    */     
/* 173:    */ 
/* 174:    */ 
/* 175:    */ 
/* 176:410 */     newVector.addElement(new Option("\tNumber of execution slots.\n\t(default 1 - i.e. no parallelism)\n\t(use 0 to auto-detect number of cores)", "num-slots", 1, "-num-slots <num>"));
/* 177:    */     
/* 178:    */ 
/* 179:    */ 
/* 180:    */ 
/* 181:    */ 
/* 182:416 */     List<Option> list = Collections.list(((OptionHandler)getClassifier()).listOptions());
/* 183:417 */     newVector.addAll(list);
/* 184:    */     
/* 185:419 */     return newVector.elements();
/* 186:    */   }
/* 187:    */   
/* 188:    */   public String[] getOptions()
/* 189:    */   {
/* 190:429 */     Vector<String> result = new Vector();
/* 191:    */     
/* 192:431 */     result.add("-P");
/* 193:432 */     result.add("" + getBagSizePercent());
/* 194:434 */     if (getCalcOutOfBag()) {
/* 195:435 */       result.add("-O");
/* 196:    */     }
/* 197:438 */     if (getStoreOutOfBagPredictions()) {
/* 198:439 */       result.add("-store-out-of-bag-predictions");
/* 199:    */     }
/* 200:442 */     if (getOutputOutOfBagComplexityStatistics()) {
/* 201:443 */       result.add("-output-out-of-bag-complexity-statistics");
/* 202:    */     }
/* 203:446 */     if (getPrintClassifiers()) {
/* 204:447 */       result.add("-print");
/* 205:    */     }
/* 206:450 */     result.add("-I");
/* 207:451 */     result.add("" + getNumIterations());
/* 208:    */     
/* 209:453 */     result.add("-num-slots");
/* 210:454 */     result.add("" + getNumExecutionSlots());
/* 211:456 */     if (getDoNotCheckCapabilities()) {
/* 212:457 */       result.add("-do-not-check-capabilities");
/* 213:    */     }
/* 214:461 */     Vector<String> classifierOptions = new Vector();
/* 215:462 */     Collections.addAll(classifierOptions, ((OptionHandler)getClassifier()).getOptions());
/* 216:463 */     Option.deleteFlagString(classifierOptions, "-do-not-check-capabilities");
/* 217:464 */     result.addAll(classifierOptions);
/* 218:    */     
/* 219:466 */     return (String[])result.toArray(new String[result.size()]);
/* 220:    */   }
/* 221:    */   
/* 222:    */   public void setOptions(String[] options)
/* 223:    */     throws Exception
/* 224:    */   {
/* 225:549 */     String bagSize = Utils.getOption('P', options);
/* 226:550 */     if (bagSize.length() != 0) {
/* 227:551 */       setBagSizePercent(Integer.parseInt(bagSize));
/* 228:    */     } else {
/* 229:553 */       setBagSizePercent(100);
/* 230:    */     }
/* 231:556 */     setCalcOutOfBag(Utils.getFlag('O', options));
/* 232:    */     
/* 233:558 */     setStoreOutOfBagPredictions(Utils.getFlag("store-out-of-bag-predictions", options));
/* 234:    */     
/* 235:560 */     setOutputOutOfBagComplexityStatistics(Utils.getFlag("output-out-of-bag-complexity-statistics", options));
/* 236:    */     
/* 237:562 */     setPrintClassifiers(Utils.getFlag("print", options));
/* 238:    */     
/* 239:564 */     String iterations = Utils.getOption('I', options);
/* 240:565 */     if (iterations.length() != 0) {
/* 241:566 */       setNumIterations(Integer.parseInt(iterations));
/* 242:    */     } else {
/* 243:568 */       setNumIterations(defaultNumberOfIterations());
/* 244:    */     }
/* 245:571 */     String numSlots = Utils.getOption("num-slots", options);
/* 246:572 */     if (numSlots.length() != 0) {
/* 247:573 */       setNumExecutionSlots(Integer.parseInt(numSlots));
/* 248:    */     } else {
/* 249:575 */       setNumExecutionSlots(1);
/* 250:    */     }
/* 251:578 */     RandomTree classifier = (RandomTree)AbstractClassifier.forName(defaultClassifierString(), options);
/* 252:579 */     setDoNotCheckCapabilities(classifier.getDoNotCheckCapabilities());
/* 253:580 */     setSeed(classifier.getSeed());
/* 254:581 */     setDebug(classifier.getDebug());
/* 255:582 */     setNumFeatures(classifier.getNumDecimalPlaces());
/* 256:583 */     classifier.setDoNotCheckCapabilities(true);
/* 257:    */     
/* 258:    */ 
/* 259:586 */     setClassifier(classifier);
/* 260:    */     
/* 261:588 */     Utils.checkForRemainingOptions(options);
/* 262:    */   }
/* 263:    */   
/* 264:    */   public String getRevision()
/* 265:    */   {
/* 266:598 */     return RevisionUtils.extract("$Revision: 12645 $");
/* 267:    */   }
/* 268:    */   
/* 269:    */   public static void main(String[] argv)
/* 270:    */   {
/* 271:607 */     runClassifier(new RandomForest(), argv);
/* 272:    */   }
/* 273:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.RandomForest
 * JD-Core Version:    0.7.0.1
 */