/*   1:    */ package weka.classifiers.misc;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.AbstractClassifier;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.CapabilitiesHandler;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.Option;
/*  15:    */ import weka.core.OptionHandler;
/*  16:    */ import weka.core.RevisionHandler;
/*  17:    */ import weka.core.RevisionUtils;
/*  18:    */ import weka.core.TechnicalInformation;
/*  19:    */ import weka.core.TechnicalInformation.Field;
/*  20:    */ import weka.core.TechnicalInformation.Type;
/*  21:    */ import weka.core.TechnicalInformationHandler;
/*  22:    */ import weka.core.Utils;
/*  23:    */ import weka.core.neighboursearch.LinearNNSearch;
/*  24:    */ import weka.core.neighboursearch.NearestNeighbourSearch;
/*  25:    */ import weka.filters.Filter;
/*  26:    */ 
/*  27:    */ public class LOF
/*  28:    */   extends AbstractClassifier
/*  29:    */   implements Serializable, CapabilitiesHandler, OptionHandler, TechnicalInformationHandler, RevisionHandler
/*  30:    */ {
/*  31:    */   private static final long serialVersionUID = -2736613569494944202L;
/*  32:    */   protected weka.filters.unsupervised.attribute.LOF m_lof;
/*  33:125 */   protected String m_minPtsLB = "10";
/*  34:128 */   protected String m_minPtsUB = "40";
/*  35:131 */   protected NearestNeighbourSearch m_nnTemplate = new LinearNNSearch();
/*  36:134 */   protected String m_numSlots = "1";
/*  37:    */   protected double m_minScore;
/*  38:    */   protected double m_maxScore;
/*  39:    */   protected static final double m_tol = 1.0E-006D;
/*  40:    */   
/*  41:    */   public String globalInfo()
/*  42:    */   {
/*  43:152 */     return "A Classifier that applies the LOF (Local Outlier Factor) algorithm to compute an \"outlier\" score for each instance in the data. The data is expected to have a unary or binary class attribute, which is ignored at training time. The distributionForInstance() method returns 1 - normalized outlier score in the first element of the distribution. If the class attribute is binary, then the second element holds the normalized outlier score. To evaluate performance of this method for a dataset where outliers/anomalies are known, simply code the outliers using the class attribute: normal cases should correspond to the first value of the class attribute; outliers to the second one.\n\nCan use multiple cores/cpus to speed up the LOF computation for large datasets. Nearest neighbor search methods and distance functions are pluggable.\n\nFor more information, see:\n\n" + getTechnicalInformation().toString();
/*  44:    */   }
/*  45:    */   
/*  46:    */   public TechnicalInformation getTechnicalInformation()
/*  47:    */   {
/*  48:179 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  49:180 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Markus M. Breunig and Hans-Peter Kriegel and Raymond T. Ng and Jorg Sander");
/*  50:    */     
/*  51:182 */     result.setValue(TechnicalInformation.Field.TITLE, "LOF: Identifying Density-Based Local Outliers");
/*  52:    */     
/*  53:184 */     result.setValue(TechnicalInformation.Field.JOURNAL, "ACM SIGMOD Record");
/*  54:185 */     result.setValue(TechnicalInformation.Field.YEAR, "2000");
/*  55:186 */     result.setValue(TechnicalInformation.Field.VOLUME, "29");
/*  56:187 */     result.setValue(TechnicalInformation.Field.NUMBER, "2");
/*  57:188 */     result.setValue(TechnicalInformation.Field.PAGES, "93-104");
/*  58:189 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "ACM New York");
/*  59:190 */     return result;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public Capabilities getCapabilities()
/*  63:    */   {
/*  64:201 */     Capabilities result = super.getCapabilities();
/*  65:202 */     result.disableAll();
/*  66:    */     
/*  67:    */ 
/*  68:205 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  69:206 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  70:207 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  71:    */     
/*  72:    */ 
/*  73:210 */     result.enable(Capabilities.Capability.UNARY_CLASS);
/*  74:211 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/*  75:212 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  76:    */     
/*  77:    */ 
/*  78:215 */     result.setMinimumNumberInstances(1);
/*  79:    */     
/*  80:217 */     return result;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public Enumeration<Option> listOptions()
/*  84:    */   {
/*  85:228 */     Vector<Option> newVector = new Vector();
/*  86:229 */     newVector.add(new Option("\tLower bound on the k nearest neighbors for finding max LOF (minPtsLB)\n\t(default = 10)", "min", 1, "-min <num>"));
/*  87:    */     
/*  88:    */ 
/*  89:    */ 
/*  90:233 */     newVector.add(new Option("\tUpper bound on the k nearest neighbors for finding max LOF (minPtsUB)\n\t(default = 40)", "max", 1, "-max <num>"));
/*  91:    */     
/*  92:    */ 
/*  93:    */ 
/*  94:237 */     newVector.addElement(new Option("\tThe nearest neighbour search algorithm to use (default: weka.core.neighboursearch.LinearNNSearch).\n", "A", 0, "-A"));
/*  95:    */     
/*  96:    */ 
/*  97:    */ 
/*  98:241 */     newVector.addElement(new Option("\tNumber of execution slots.\n\t(default 1 - i.e. no parallelism)", "num-slots", 1, "-num-slots <num>"));
/*  99:    */     
/* 100:    */ 
/* 101:    */ 
/* 102:245 */     newVector.addAll(Collections.list(super.listOptions()));
/* 103:    */     
/* 104:247 */     return newVector.elements();
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void setOptions(String[] options)
/* 108:    */     throws Exception
/* 109:    */   {
/* 110:292 */     String minP = Utils.getOption("min", options);
/* 111:293 */     if (minP.length() > 0) {
/* 112:294 */       setMinPointsLowerBound(minP);
/* 113:    */     }
/* 114:297 */     String maxP = Utils.getOption("max", options);
/* 115:298 */     if (maxP.length() > 0) {
/* 116:299 */       setMinPointsUpperBound(maxP);
/* 117:    */     }
/* 118:302 */     String nnSearchClass = Utils.getOption('A', options);
/* 119:303 */     if (nnSearchClass.length() != 0)
/* 120:    */     {
/* 121:304 */       String[] nnSearchClassSpec = Utils.splitOptions(nnSearchClass);
/* 122:305 */       if (nnSearchClassSpec.length == 0) {
/* 123:306 */         throw new Exception("Invalid NearestNeighbourSearch algorithm specification string.");
/* 124:    */       }
/* 125:309 */       String className = nnSearchClassSpec[0];
/* 126:310 */       nnSearchClassSpec[0] = "";
/* 127:    */       
/* 128:312 */       setNNSearch((NearestNeighbourSearch)Utils.forName(NearestNeighbourSearch.class, className, nnSearchClassSpec));
/* 129:    */     }
/* 130:    */     else
/* 131:    */     {
/* 132:315 */       setNNSearch(new LinearNNSearch());
/* 133:    */     }
/* 134:318 */     String slotsS = Utils.getOption("num-slots", options);
/* 135:319 */     if (slotsS.length() > 0) {
/* 136:320 */       setNumExecutionSlots(slotsS);
/* 137:    */     }
/* 138:323 */     super.setOptions(options);
/* 139:    */     
/* 140:325 */     Utils.checkForRemainingOptions(options);
/* 141:    */   }
/* 142:    */   
/* 143:    */   public String[] getOptions()
/* 144:    */   {
/* 145:336 */     Vector<String> options = new Vector();
/* 146:    */     
/* 147:338 */     options.add("-min");
/* 148:339 */     options.add(getMinPointsLowerBound());
/* 149:340 */     options.add("-max");
/* 150:341 */     options.add(getMinPointsUpperBound());
/* 151:    */     
/* 152:343 */     options.add("-A");
/* 153:344 */     options.add(this.m_nnTemplate.getClass().getName() + " " + Utils.joinOptions(this.m_nnTemplate.getOptions()));
/* 154:    */     
/* 155:346 */     options.add("-num-slots");
/* 156:347 */     options.add(getNumExecutionSlots());
/* 157:    */     
/* 158:349 */     Collections.addAll(options, super.getOptions());
/* 159:    */     
/* 160:351 */     return (String[])options.toArray(new String[0]);
/* 161:    */   }
/* 162:    */   
/* 163:    */   public String minPointsLowerBoundTipText()
/* 164:    */   {
/* 165:361 */     return "The lower bound (minPtsLB) to use on the range for k when determining the maximum LOF value";
/* 166:    */   }
/* 167:    */   
/* 168:    */   public void setMinPointsLowerBound(String pts)
/* 169:    */   {
/* 170:372 */     this.m_minPtsLB = pts;
/* 171:    */   }
/* 172:    */   
/* 173:    */   public String getMinPointsLowerBound()
/* 174:    */   {
/* 175:382 */     return this.m_minPtsLB;
/* 176:    */   }
/* 177:    */   
/* 178:    */   public String minPointsUpperBoundTipText()
/* 179:    */   {
/* 180:392 */     return "The upper bound (minPtsUB) to use on the range for k when determining the maximum LOF value";
/* 181:    */   }
/* 182:    */   
/* 183:    */   public void setMinPointsUpperBound(String pts)
/* 184:    */   {
/* 185:403 */     this.m_minPtsUB = pts;
/* 186:    */   }
/* 187:    */   
/* 188:    */   public String getMinPointsUpperBound()
/* 189:    */   {
/* 190:413 */     return this.m_minPtsUB;
/* 191:    */   }
/* 192:    */   
/* 193:    */   public String NNSearchTipText()
/* 194:    */   {
/* 195:423 */     return "The nearest neighbour search algorithm to use (Default: weka.core.neighboursearch.LinearNNSearch).";
/* 196:    */   }
/* 197:    */   
/* 198:    */   public void setNNSearch(NearestNeighbourSearch s)
/* 199:    */   {
/* 200:433 */     this.m_nnTemplate = s;
/* 201:    */   }
/* 202:    */   
/* 203:    */   public NearestNeighbourSearch getNNSearch()
/* 204:    */   {
/* 205:442 */     return this.m_nnTemplate;
/* 206:    */   }
/* 207:    */   
/* 208:    */   public String numExecutionSlotsTipText()
/* 209:    */   {
/* 210:452 */     return "The number of execution slots (threads) to use for finding LOF values.";
/* 211:    */   }
/* 212:    */   
/* 213:    */   public void setNumExecutionSlots(String slots)
/* 214:    */   {
/* 215:464 */     this.m_numSlots = slots;
/* 216:    */   }
/* 217:    */   
/* 218:    */   public String getNumExecutionSlots()
/* 219:    */   {
/* 220:475 */     return this.m_numSlots;
/* 221:    */   }
/* 222:    */   
/* 223:    */   public void buildClassifier(Instances data)
/* 224:    */     throws Exception
/* 225:    */   {
/* 226:481 */     getCapabilities().testWithFail(data);
/* 227:    */     
/* 228:483 */     this.m_maxScore = 4.9E-324D;
/* 229:484 */     this.m_minScore = 1.7976931348623157E+308D;
/* 230:485 */     this.m_lof = new weka.filters.unsupervised.attribute.LOF();
/* 231:486 */     this.m_lof.setInputFormat(data);
/* 232:487 */     this.m_lof.setMinPointsLowerBound(this.m_minPtsLB);
/* 233:488 */     this.m_lof.setMinPointsUpperBound(this.m_minPtsUB);
/* 234:489 */     this.m_lof.setNNSearch(this.m_nnTemplate);
/* 235:490 */     this.m_lof.setNumExecutionSlots(this.m_numSlots);
/* 236:    */     
/* 237:492 */     Instances temp = Filter.useFilter(data, this.m_lof);
/* 238:494 */     for (int i = 0; i < temp.numInstances(); i++)
/* 239:    */     {
/* 240:495 */       double current = temp.instance(i).value(temp.numAttributes() - 1);
/* 241:496 */       if (!Double.isNaN(current))
/* 242:    */       {
/* 243:497 */         if (current > this.m_maxScore) {
/* 244:498 */           this.m_maxScore = current;
/* 245:    */         }
/* 246:500 */         if (current < this.m_minScore) {
/* 247:501 */           this.m_minScore = current;
/* 248:    */         }
/* 249:    */       }
/* 250:    */     }
/* 251:    */   }
/* 252:    */   
/* 253:    */   public double[] distributionForInstance(Instance inst)
/* 254:    */     throws Exception
/* 255:    */   {
/* 256:509 */     double[] scores = new double[inst.classAttribute().numValues()];
/* 257:    */     double lofScore;
/* 258:    */     double lofScore;
/* 259:512 */     if (this.m_maxScore == this.m_minScore)
/* 260:    */     {
/* 261:513 */       lofScore = 0.0D;
/* 262:    */     }
/* 263:    */     else
/* 264:    */     {
/* 265:515 */       this.m_lof.input(inst);
/* 266:516 */       Instance scored = this.m_lof.output();
/* 267:517 */       lofScore = scored.value(scored.numAttributes() - 1);
/* 268:    */       
/* 269:519 */       lofScore -= this.m_minScore;
/* 270:520 */       if (lofScore <= 0.0D) {
/* 271:521 */         lofScore = 1.0E-006D;
/* 272:    */       }
/* 273:524 */       lofScore /= (this.m_maxScore - this.m_minScore);
/* 274:525 */       if (lofScore >= 1.0D) {
/* 275:526 */         lofScore = 0.999999D;
/* 276:    */       }
/* 277:    */     }
/* 278:530 */     scores[0] = (1.0D - lofScore);
/* 279:531 */     if (scores.length > 1) {
/* 280:532 */       scores[1] = lofScore;
/* 281:    */     }
/* 282:535 */     return scores;
/* 283:    */   }
/* 284:    */   
/* 285:    */   public String toString()
/* 286:    */   {
/* 287:540 */     if (this.m_lof == null) {
/* 288:541 */       return "No model built yet!";
/* 289:    */     }
/* 290:544 */     return "Local Outlier Factor classifier\n\n";
/* 291:    */   }
/* 292:    */   
/* 293:    */   public String getRevision()
/* 294:    */   {
/* 295:554 */     return RevisionUtils.extract("$Revision: 9723 $");
/* 296:    */   }
/* 297:    */   
/* 298:    */   public static void main(String[] args)
/* 299:    */   {
/* 300:564 */     runClassifier(new LOF(), args);
/* 301:    */   }
/* 302:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.misc.LOF
 * JD-Core Version:    0.7.0.1
 */