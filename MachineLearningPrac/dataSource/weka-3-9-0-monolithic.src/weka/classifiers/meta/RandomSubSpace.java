/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Random;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.classifiers.Classifier;
/*  10:    */ import weka.classifiers.RandomizableParallelIteratedSingleClassifierEnhancer;
/*  11:    */ import weka.classifiers.rules.ZeroR;
/*  12:    */ import weka.classifiers.trees.REPTree;
/*  13:    */ import weka.core.Attribute;
/*  14:    */ import weka.core.Capabilities;
/*  15:    */ import weka.core.Instance;
/*  16:    */ import weka.core.Instances;
/*  17:    */ import weka.core.Option;
/*  18:    */ import weka.core.Randomizable;
/*  19:    */ import weka.core.RevisionUtils;
/*  20:    */ import weka.core.TechnicalInformation;
/*  21:    */ import weka.core.TechnicalInformation.Field;
/*  22:    */ import weka.core.TechnicalInformation.Type;
/*  23:    */ import weka.core.TechnicalInformationHandler;
/*  24:    */ import weka.core.Utils;
/*  25:    */ import weka.core.WeightedInstancesHandler;
/*  26:    */ import weka.filters.unsupervised.attribute.Remove;
/*  27:    */ 
/*  28:    */ public class RandomSubSpace
/*  29:    */   extends RandomizableParallelIteratedSingleClassifierEnhancer
/*  30:    */   implements WeightedInstancesHandler, TechnicalInformationHandler
/*  31:    */ {
/*  32:    */   private static final long serialVersionUID = 1278172513912424947L;
/*  33:137 */   protected double m_SubSpaceSize = 0.5D;
/*  34:    */   protected Classifier m_ZeroR;
/*  35:    */   protected Instances m_data;
/*  36:    */   
/*  37:    */   public RandomSubSpace()
/*  38:    */   {
/*  39:151 */     this.m_Classifier = new REPTree();
/*  40:    */   }
/*  41:    */   
/*  42:    */   public String globalInfo()
/*  43:    */   {
/*  44:161 */     return "This method constructs a decision tree based classifier that maintains highest accuracy on training data and improves on generalization accuracy as it grows in complexity. The classifier consists of multiple trees constructed systematically by pseudorandomly selecting subsets of components of the feature vector, that is, trees constructed in randomly chosen subspaces.\n\nFor more information, see\n\n" + getTechnicalInformation().toString();
/*  45:    */   }
/*  46:    */   
/*  47:    */   public TechnicalInformation getTechnicalInformation()
/*  48:    */   {
/*  49:182 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  50:183 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Tin Kam Ho");
/*  51:184 */     result.setValue(TechnicalInformation.Field.YEAR, "1998");
/*  52:185 */     result.setValue(TechnicalInformation.Field.TITLE, "The Random Subspace Method for Constructing Decision Forests");
/*  53:186 */     result.setValue(TechnicalInformation.Field.JOURNAL, "IEEE Transactions on Pattern Analysis and Machine Intelligence");
/*  54:187 */     result.setValue(TechnicalInformation.Field.VOLUME, "20");
/*  55:188 */     result.setValue(TechnicalInformation.Field.NUMBER, "8");
/*  56:189 */     result.setValue(TechnicalInformation.Field.PAGES, "832-844");
/*  57:190 */     result.setValue(TechnicalInformation.Field.URL, "http://citeseer.ist.psu.edu/ho98random.html");
/*  58:191 */     result.setValue(TechnicalInformation.Field.ISSN, "0162-8828");
/*  59:    */     
/*  60:193 */     return result;
/*  61:    */   }
/*  62:    */   
/*  63:    */   protected String defaultClassifierString()
/*  64:    */   {
/*  65:202 */     return "weka.classifiers.trees.REPTree";
/*  66:    */   }
/*  67:    */   
/*  68:    */   public Enumeration<Option> listOptions()
/*  69:    */   {
/*  70:211 */     Vector<Option> result = new Vector();
/*  71:    */     
/*  72:213 */     result.addElement(new Option("\tSize of each subspace:\n\t\t< 1: percentage of the number of attributes\n\t\t>=1: absolute number of attributes\n", "P", 1, "-P"));
/*  73:    */     
/*  74:    */ 
/*  75:    */ 
/*  76:    */ 
/*  77:    */ 
/*  78:219 */     result.addAll(Collections.list(super.listOptions()));
/*  79:    */     
/*  80:221 */     return result.elements();
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void setOptions(String[] options)
/*  84:    */     throws Exception
/*  85:    */   {
/*  86:285 */     String tmpStr = Utils.getOption('P', options);
/*  87:286 */     if (tmpStr.length() != 0) {
/*  88:287 */       setSubSpaceSize(Double.parseDouble(tmpStr));
/*  89:    */     } else {
/*  90:289 */       setSubSpaceSize(0.5D);
/*  91:    */     }
/*  92:291 */     super.setOptions(options);
/*  93:    */     
/*  94:293 */     Utils.checkForRemainingOptions(options);
/*  95:    */   }
/*  96:    */   
/*  97:    */   public String[] getOptions()
/*  98:    */   {
/*  99:302 */     Vector<String> result = new Vector();
/* 100:    */     
/* 101:304 */     result.add("-P");
/* 102:305 */     result.add("" + getSubSpaceSize());
/* 103:    */     
/* 104:307 */     Collections.addAll(result, super.getOptions());
/* 105:    */     
/* 106:309 */     return (String[])result.toArray(new String[result.size()]);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public String subSpaceSizeTipText()
/* 110:    */   {
/* 111:319 */     return "Size of each subSpace: if less than 1 as a percentage of the number of attributes, otherwise the absolute number of attributes.";
/* 112:    */   }
/* 113:    */   
/* 114:    */   public double getSubSpaceSize()
/* 115:    */   {
/* 116:330 */     return this.m_SubSpaceSize;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void setSubSpaceSize(double value)
/* 120:    */   {
/* 121:339 */     this.m_SubSpaceSize = value;
/* 122:    */   }
/* 123:    */   
/* 124:    */   protected int numberOfAttributes(int total, double fraction)
/* 125:    */   {
/* 126:351 */     int k = (int)Math.round(fraction < 1.0D ? total * fraction : fraction);
/* 127:353 */     if (k > total) {
/* 128:354 */       k = total;
/* 129:    */     }
/* 130:355 */     if (k < 1) {
/* 131:356 */       k = 1;
/* 132:    */     }
/* 133:358 */     return k;
/* 134:    */   }
/* 135:    */   
/* 136:    */   protected String randomSubSpace(Integer[] indices, int subSpaceSize, int classIndex, Random random)
/* 137:    */   {
/* 138:372 */     Collections.shuffle(Arrays.asList(indices), random);
/* 139:373 */     StringBuffer sb = new StringBuffer("");
/* 140:374 */     for (int i = 0; i < subSpaceSize; i++) {
/* 141:375 */       sb.append(indices[i] + ",");
/* 142:    */     }
/* 143:377 */     sb.append(classIndex);
/* 144:379 */     if (getDebug()) {
/* 145:380 */       System.out.println("subSPACE = " + sb);
/* 146:    */     }
/* 147:382 */     return sb.toString();
/* 148:    */   }
/* 149:    */   
/* 150:    */   public void buildClassifier(Instances data)
/* 151:    */     throws Exception
/* 152:    */   {
/* 153:395 */     getCapabilities().testWithFail(data);
/* 154:    */     
/* 155:    */ 
/* 156:398 */     this.m_data = new Instances(data);
/* 157:401 */     if (this.m_data.numAttributes() == 1)
/* 158:    */     {
/* 159:402 */       System.err.println("Cannot build model (only class attribute present in data!), using ZeroR model instead!");
/* 160:    */       
/* 161:    */ 
/* 162:405 */       this.m_ZeroR = new ZeroR();
/* 163:406 */       this.m_ZeroR.buildClassifier(this.m_data);
/* 164:407 */       return;
/* 165:    */     }
/* 166:410 */     this.m_ZeroR = null;
/* 167:    */     
/* 168:    */ 
/* 169:413 */     super.buildClassifier(data);
/* 170:    */     
/* 171:415 */     Integer[] indices = new Integer[data.numAttributes() - 1];
/* 172:416 */     int classIndex = data.classIndex();
/* 173:417 */     int offset = 0;
/* 174:418 */     for (int i = 0; i < indices.length + 1; i++) {
/* 175:419 */       if (i != classIndex) {
/* 176:420 */         indices[(offset++)] = Integer.valueOf(i + 1);
/* 177:    */       }
/* 178:    */     }
/* 179:423 */     int subSpaceSize = numberOfAttributes(indices.length, getSubSpaceSize());
/* 180:424 */     Random random = data.getRandomNumberGenerator(this.m_Seed);
/* 181:426 */     for (int j = 0; j < this.m_Classifiers.length; j++)
/* 182:    */     {
/* 183:427 */       if ((this.m_Classifier instanceof Randomizable)) {
/* 184:428 */         ((Randomizable)this.m_Classifiers[j]).setSeed(random.nextInt());
/* 185:    */       }
/* 186:430 */       FilteredClassifier fc = new FilteredClassifier();
/* 187:431 */       fc.setClassifier(this.m_Classifiers[j]);
/* 188:432 */       this.m_Classifiers[j] = fc;
/* 189:433 */       Remove rm = new Remove();
/* 190:434 */       rm.setOptions(new String[] { "-V", "-R", randomSubSpace(indices, subSpaceSize, classIndex + 1, random) });
/* 191:435 */       fc.setFilter(rm);
/* 192:    */     }
/* 193:441 */     buildClassifiers();
/* 194:    */     
/* 195:    */ 
/* 196:444 */     this.m_data = null;
/* 197:    */   }
/* 198:    */   
/* 199:    */   protected synchronized Instances getTrainingSet(int iteration)
/* 200:    */     throws Exception
/* 201:    */   {
/* 202:458 */     return this.m_data;
/* 203:    */   }
/* 204:    */   
/* 205:    */   public double[] distributionForInstance(Instance instance)
/* 206:    */     throws Exception
/* 207:    */   {
/* 208:472 */     if (this.m_ZeroR != null) {
/* 209:473 */       return this.m_ZeroR.distributionForInstance(instance);
/* 210:    */     }
/* 211:476 */     double[] sums = new double[instance.numClasses()];
/* 212:    */     
/* 213:478 */     double numPreds = 0.0D;
/* 214:479 */     for (int i = 0; i < this.m_NumIterations; i++) {
/* 215:480 */       if (instance.classAttribute().isNumeric() == true)
/* 216:    */       {
/* 217:481 */         double pred = this.m_Classifiers[i].classifyInstance(instance);
/* 218:482 */         if (!Utils.isMissingValue(pred))
/* 219:    */         {
/* 220:483 */           sums[0] += pred;
/* 221:484 */           numPreds += 1.0D;
/* 222:    */         }
/* 223:    */       }
/* 224:    */       else
/* 225:    */       {
/* 226:487 */         double[] newProbs = this.m_Classifiers[i].distributionForInstance(instance);
/* 227:488 */         for (int j = 0; j < newProbs.length; j++) {
/* 228:489 */           sums[j] += newProbs[j];
/* 229:    */         }
/* 230:    */       }
/* 231:    */     }
/* 232:492 */     if (instance.classAttribute().isNumeric() == true)
/* 233:    */     {
/* 234:493 */       if (numPreds == 0.0D) {
/* 235:494 */         sums[0] = Utils.missingValue();
/* 236:    */       } else {
/* 237:496 */         sums[0] /= numPreds;
/* 238:    */       }
/* 239:498 */       return sums;
/* 240:    */     }
/* 241:499 */     if (Utils.eq(Utils.sum(sums), 0.0D)) {
/* 242:500 */       return sums;
/* 243:    */     }
/* 244:502 */     Utils.normalize(sums);
/* 245:503 */     return sums;
/* 246:    */   }
/* 247:    */   
/* 248:    */   public String toString()
/* 249:    */   {
/* 250:515 */     if (this.m_ZeroR != null)
/* 251:    */     {
/* 252:516 */       StringBuffer buf = new StringBuffer();
/* 253:517 */       buf.append(getClass().getName().replaceAll(".*\\.", "") + "\n");
/* 254:518 */       buf.append(getClass().getName().replaceAll(".*\\.", "").replaceAll(".", "=") + "\n\n");
/* 255:519 */       buf.append("Warning: No model could be built, hence ZeroR model is used:\n\n");
/* 256:520 */       buf.append(this.m_ZeroR.toString());
/* 257:521 */       return buf.toString();
/* 258:    */     }
/* 259:524 */     if (this.m_Classifiers == null) {
/* 260:525 */       return "RandomSubSpace: No model built yet.";
/* 261:    */     }
/* 262:527 */     StringBuffer text = new StringBuffer();
/* 263:528 */     text.append("All the base classifiers: \n\n");
/* 264:529 */     for (int i = 0; i < this.m_Classifiers.length; i++) {
/* 265:530 */       text.append(this.m_Classifiers[i].toString() + "\n\n");
/* 266:    */     }
/* 267:532 */     return text.toString();
/* 268:    */   }
/* 269:    */   
/* 270:    */   public String getRevision()
/* 271:    */   {
/* 272:541 */     return RevisionUtils.extract("$Revision: 11461 $");
/* 273:    */   }
/* 274:    */   
/* 275:    */   public static void main(String[] args)
/* 276:    */   {
/* 277:550 */     runClassifier(new RandomSubSpace(), args);
/* 278:    */   }
/* 279:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.RandomSubSpace
 * JD-Core Version:    0.7.0.1
 */