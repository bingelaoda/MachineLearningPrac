/*   1:    */ package weka.classifiers.misc;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Random;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.classifiers.RandomizableClassifier;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Capabilities.Capability;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.Option;
/*  15:    */ import weka.core.TechnicalInformation;
/*  16:    */ import weka.core.TechnicalInformation.Field;
/*  17:    */ import weka.core.TechnicalInformation.Type;
/*  18:    */ import weka.core.TechnicalInformationHandler;
/*  19:    */ import weka.core.Utils;
/*  20:    */ 
/*  21:    */ public class IsolationForest
/*  22:    */   extends RandomizableClassifier
/*  23:    */   implements TechnicalInformationHandler, Serializable
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = 5586674623147772788L;
/*  26:    */   protected Tree[] m_trees;
/*  27:    */   protected int m_numTrees;
/*  28:    */   protected int m_subsampleSize;
/*  29:    */   
/*  30:    */   public IsolationForest()
/*  31:    */   {
/*  32:109 */     this.m_trees = null;
/*  33:    */     
/*  34:    */ 
/*  35:112 */     this.m_numTrees = 100;
/*  36:    */     
/*  37:    */ 
/*  38:115 */     this.m_subsampleSize = 256;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public String globalInfo()
/*  42:    */   {
/*  43:122 */     return "Implements the isolation forest method for anomaly detection.\n\nNote that this classifier is designed for anomaly detection, it is not designed for solving two-class or multi-class classification problems!\n\nThe data is expected to have have a class attribute with one or two values, which is ignored at training time. The distributionForInstance() method returns (1 - anomaly score) as the first element in the distribution, the second element (in the case of two classes) is the anomaly score.\n\nTo evaluate performance of this method for a dataset where anomalies are known, simply code the anomalies using the class attribute: normal cases should correspond to the first value of the class attribute, anomalies to the second one.\n\nFor more information, see:\n\n" + getTechnicalInformation().toString();
/*  44:    */   }
/*  45:    */   
/*  46:    */   public TechnicalInformation getTechnicalInformation()
/*  47:    */   {
/*  48:147 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  49:148 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Fei Tony Liu and Kai Ming Ting and Zhi-Hua Zhou");
/*  50:    */     
/*  51:150 */     result.setValue(TechnicalInformation.Field.TITLE, "Isolation Forest");
/*  52:151 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "ICDM");
/*  53:152 */     result.setValue(TechnicalInformation.Field.YEAR, "2008");
/*  54:153 */     result.setValue(TechnicalInformation.Field.PAGES, "413-422");
/*  55:154 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "IEEE Computer Society");
/*  56:    */     
/*  57:156 */     return result;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public Capabilities getCapabilities()
/*  61:    */   {
/*  62:164 */     Capabilities result = super.getCapabilities();
/*  63:165 */     result.disableAll();
/*  64:    */     
/*  65:    */ 
/*  66:168 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  67:169 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  68:    */     
/*  69:    */ 
/*  70:172 */     result.enable(Capabilities.Capability.UNARY_CLASS);
/*  71:173 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/*  72:174 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  73:    */     
/*  74:    */ 
/*  75:177 */     result.setMinimumNumberInstances(0);
/*  76:    */     
/*  77:179 */     return result;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public String toString()
/*  81:    */   {
/*  82:188 */     if (this.m_trees == null) {
/*  83:189 */       return "No model built yet.";
/*  84:    */     }
/*  85:191 */     return "Isolation forest for anomaly detection (" + this.m_numTrees + ", " + this.m_subsampleSize + ")";
/*  86:    */   }
/*  87:    */   
/*  88:    */   public String numTreesTipText()
/*  89:    */   {
/*  90:204 */     return "The number of trees to use in the forest.";
/*  91:    */   }
/*  92:    */   
/*  93:    */   public int getNumTrees()
/*  94:    */   {
/*  95:214 */     return this.m_numTrees;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void setNumTrees(int k)
/*  99:    */   {
/* 100:224 */     this.m_numTrees = k;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public String subsampleSizeTipText()
/* 104:    */   {
/* 105:235 */     return "The size of the subsample used to build each tree.";
/* 106:    */   }
/* 107:    */   
/* 108:    */   public int getSubsampleSize()
/* 109:    */   {
/* 110:245 */     return this.m_subsampleSize;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void setSubsampleSize(int n)
/* 114:    */   {
/* 115:255 */     this.m_subsampleSize = n;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public Enumeration<Option> listOptions()
/* 119:    */   {
/* 120:266 */     Vector<Option> newVector = new Vector();
/* 121:    */     
/* 122:268 */     newVector.addElement(new Option("\tThe number of trees in the forest (default 100).", "I", 1, "-I <number of trees>"));
/* 123:    */     
/* 124:    */ 
/* 125:    */ 
/* 126:272 */     newVector.addElement(new Option("\tThe subsample size for each tree (default 256).", "N", 1, "-N <the size of the subsample for each tree>"));
/* 127:    */     
/* 128:    */ 
/* 129:    */ 
/* 130:276 */     newVector.addAll(Collections.list(super.listOptions()));
/* 131:    */     
/* 132:278 */     return newVector.elements();
/* 133:    */   }
/* 134:    */   
/* 135:    */   public String[] getOptions()
/* 136:    */   {
/* 137:289 */     Vector<String> result = new Vector();
/* 138:    */     
/* 139:291 */     result.add("-I");
/* 140:292 */     result.add("" + getNumTrees());
/* 141:    */     
/* 142:294 */     result.add("-N");
/* 143:295 */     result.add("" + getSubsampleSize());
/* 144:    */     
/* 145:297 */     Collections.addAll(result, super.getOptions());
/* 146:    */     
/* 147:299 */     return (String[])result.toArray(new String[result.size()]);
/* 148:    */   }
/* 149:    */   
/* 150:    */   public void setOptions(String[] options)
/* 151:    */     throws Exception
/* 152:    */   {
/* 153:339 */     String tmpStr = Utils.getOption('I', options);
/* 154:340 */     if (tmpStr.length() != 0) {
/* 155:341 */       this.m_numTrees = Integer.parseInt(tmpStr);
/* 156:    */     } else {
/* 157:343 */       this.m_numTrees = 100;
/* 158:    */     }
/* 159:346 */     tmpStr = Utils.getOption('N', options);
/* 160:347 */     if (tmpStr.length() != 0) {
/* 161:348 */       this.m_subsampleSize = Integer.parseInt(tmpStr);
/* 162:    */     } else {
/* 163:350 */       this.m_subsampleSize = 256;
/* 164:    */     }
/* 165:353 */     super.setOptions(options);
/* 166:    */     
/* 167:355 */     Utils.checkForRemainingOptions(options);
/* 168:    */   }
/* 169:    */   
/* 170:    */   public void buildClassifier(Instances data)
/* 171:    */     throws Exception
/* 172:    */   {
/* 173:365 */     getCapabilities().testWithFail(data);
/* 174:368 */     if (data.numInstances() < this.m_subsampleSize) {
/* 175:369 */       this.m_subsampleSize = data.numInstances();
/* 176:    */     }
/* 177:373 */     this.m_trees = new Tree[this.m_numTrees];
/* 178:374 */     data = new Instances(data);
/* 179:375 */     Random r = data.numInstances() > 0 ? data.getRandomNumberGenerator(this.m_Seed) : new Random(this.m_Seed);
/* 180:377 */     for (int i = 0; i < this.m_numTrees; i++)
/* 181:    */     {
/* 182:378 */       data.randomize(r);
/* 183:379 */       this.m_trees[i] = new Tree(new Instances(data, 0, this.m_subsampleSize), r, 0, (int)Math.ceil(Utils.log2(data.numInstances())));
/* 184:    */     }
/* 185:    */   }
/* 186:    */   
/* 187:    */   public static double c(double n)
/* 188:    */   {
/* 189:390 */     if (n <= 1.0D) {
/* 190:391 */       return 0.0D;
/* 191:    */     }
/* 192:393 */     return 2.0D * (Math.log(n - 1.0D) + 0.5772156649D) - 2.0D * (n - 1.0D) / n;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public double[] distributionForInstance(Instance inst)
/* 196:    */   {
/* 197:402 */     double avgPathLength = 0.0D;
/* 198:403 */     for (Tree m_tree : this.m_trees) {
/* 199:404 */       avgPathLength += m_tree.pathLength(inst);
/* 200:    */     }
/* 201:406 */     avgPathLength /= this.m_trees.length;
/* 202:    */     
/* 203:408 */     double[] scores = new double[inst.numClasses()];
/* 204:409 */     scores[0] = (1.0D - Math.pow(2.0D, -avgPathLength / c(this.m_subsampleSize)));
/* 205:410 */     if (scores.length > 1) {
/* 206:411 */       scores[1] = (1.0D - scores[0]);
/* 207:    */     }
/* 208:414 */     return scores;
/* 209:    */   }
/* 210:    */   
/* 211:    */   public static void main(String[] args)
/* 212:    */   {
/* 213:422 */     runClassifier(new IsolationForest(), args);
/* 214:    */   }
/* 215:    */   
/* 216:    */   protected class Tree
/* 217:    */     implements Serializable
/* 218:    */   {
/* 219:    */     private static final long serialVersionUID = 7786674623147772711L;
/* 220:    */     protected int m_size;
/* 221:    */     protected int m_a;
/* 222:    */     protected double m_splitPoint;
/* 223:    */     protected Tree[] m_successors;
/* 224:    */     
/* 225:    */     protected Tree(Instances data, Random r, int height, int maxHeight)
/* 226:    */     {
/* 227:451 */       this.m_size = data.numInstances();
/* 228:454 */       if ((this.m_size <= 1) || (height == maxHeight)) {
/* 229:455 */         return;
/* 230:    */       }
/* 231:459 */       ArrayList<Integer> al = new ArrayList();
/* 232:460 */       double[][] minmax = new double[2][data.numAttributes()];
/* 233:461 */       for (int j = 0; j < data.numAttributes(); j++)
/* 234:    */       {
/* 235:462 */         minmax[0][j] = data.instance(0).value(j);
/* 236:463 */         minmax[1][j] = minmax[0][j];
/* 237:    */       }
/* 238:465 */       for (int i = 1; i < data.numInstances(); i++)
/* 239:    */       {
/* 240:466 */         Instance inst = data.instance(i);
/* 241:467 */         for (int j = 0; j < data.numAttributes(); j++)
/* 242:    */         {
/* 243:468 */           if (inst.value(j) < minmax[0][j]) {
/* 244:469 */             minmax[0][j] = inst.value(j);
/* 245:    */           }
/* 246:471 */           if (inst.value(j) > minmax[1][j]) {
/* 247:472 */             minmax[1][j] = inst.value(j);
/* 248:    */           }
/* 249:    */         }
/* 250:    */       }
/* 251:476 */       for (int j = 0; j < data.numAttributes(); j++) {
/* 252:477 */         if ((j != data.classIndex()) && 
/* 253:478 */           (minmax[0][j] < minmax[1][j])) {
/* 254:479 */           al.add(Integer.valueOf(j));
/* 255:    */         }
/* 256:    */       }
/* 257:485 */       if (al.size() == 0) {
/* 258:486 */         return;
/* 259:    */       }
/* 260:490 */       this.m_a = ((Integer)al.get(r.nextInt(al.size()))).intValue();
/* 261:491 */       this.m_splitPoint = (r.nextDouble() * (minmax[1][this.m_a] - minmax[0][this.m_a]) + minmax[0][this.m_a]);
/* 262:    */       
/* 263:    */ 
/* 264:    */ 
/* 265:495 */       this.m_successors = new Tree[2];
/* 266:496 */       for (int i = 0; i < 2; i++)
/* 267:    */       {
/* 268:497 */         Instances tempData = new Instances(data, data.numInstances());
/* 269:498 */         for (int j = 0; j < data.numInstances(); j++)
/* 270:    */         {
/* 271:499 */           if ((i == 0) && (data.instance(j).value(this.m_a) < this.m_splitPoint)) {
/* 272:500 */             tempData.add(data.instance(j));
/* 273:    */           }
/* 274:502 */           if ((i == 1) && (data.instance(j).value(this.m_a) >= this.m_splitPoint)) {
/* 275:503 */             tempData.add(data.instance(j));
/* 276:    */           }
/* 277:    */         }
/* 278:506 */         tempData.compactify();
/* 279:507 */         this.m_successors[i] = new Tree(IsolationForest.this, tempData, r, height + 1, maxHeight);
/* 280:    */       }
/* 281:    */     }
/* 282:    */     
/* 283:    */     protected double pathLength(Instance inst)
/* 284:    */     {
/* 285:517 */       if (this.m_successors == null) {
/* 286:518 */         return IsolationForest.c(this.m_size);
/* 287:    */       }
/* 288:520 */       if (inst.value(this.m_a) < this.m_splitPoint) {
/* 289:521 */         return this.m_successors[0].pathLength(inst) + 1.0D;
/* 290:    */       }
/* 291:523 */       return this.m_successors[1].pathLength(inst) + 1.0D;
/* 292:    */     }
/* 293:    */   }
/* 294:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.misc.IsolationForest
 * JD-Core Version:    0.7.0.1
 */