/*   1:    */ package weka.classifiers.functions;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.AbstractClassifier;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.TechnicalInformation;
/*  16:    */ import weka.core.TechnicalInformation.Field;
/*  17:    */ import weka.core.TechnicalInformation.Type;
/*  18:    */ import weka.core.TechnicalInformationHandler;
/*  19:    */ import weka.core.Utils;
/*  20:    */ import weka.filters.Filter;
/*  21:    */ import weka.filters.unsupervised.attribute.NominalToBinary;
/*  22:    */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*  23:    */ 
/*  24:    */ public class VotedPerceptron
/*  25:    */   extends AbstractClassifier
/*  26:    */   implements OptionHandler, TechnicalInformationHandler
/*  27:    */ {
/*  28:    */   static final long serialVersionUID = -1072429260104568698L;
/*  29:105 */   private int m_MaxK = 10000;
/*  30:108 */   private int m_NumIterations = 1;
/*  31:111 */   private double m_Exponent = 1.0D;
/*  32:114 */   private int m_K = 0;
/*  33:117 */   private int[] m_Additions = null;
/*  34:120 */   private boolean[] m_IsAddition = null;
/*  35:123 */   private int[] m_Weights = null;
/*  36:126 */   private Instances m_Train = null;
/*  37:129 */   private int m_Seed = 1;
/*  38:    */   private NominalToBinary m_NominalToBinary;
/*  39:    */   private ReplaceMissingValues m_ReplaceMissingValues;
/*  40:    */   
/*  41:    */   public String globalInfo()
/*  42:    */   {
/*  43:143 */     return "Implementation of the voted perceptron algorithm by Freund and Schapire. Globally replaces all missing values, and transforms nominal attributes into binary ones.\n\nFor more information, see:\n\n" + getTechnicalInformation().toString();
/*  44:    */   }
/*  45:    */   
/*  46:    */   public TechnicalInformation getTechnicalInformation()
/*  47:    */   {
/*  48:161 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  49:162 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Y. Freund and R. E. Schapire");
/*  50:163 */     result.setValue(TechnicalInformation.Field.TITLE, "Large margin classification using the perceptron algorithm");
/*  51:164 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "11th Annual Conference on Computational Learning Theory");
/*  52:165 */     result.setValue(TechnicalInformation.Field.YEAR, "1998");
/*  53:166 */     result.setValue(TechnicalInformation.Field.PAGES, "209-217");
/*  54:167 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "ACM Press");
/*  55:168 */     result.setValue(TechnicalInformation.Field.ADDRESS, "New York, NY");
/*  56:    */     
/*  57:170 */     return result;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public Enumeration<Option> listOptions()
/*  61:    */   {
/*  62:180 */     Vector<Option> newVector = new Vector(4);
/*  63:    */     
/*  64:182 */     newVector.addElement(new Option("\tThe number of iterations to be performed.\n\t(default 1)", "I", 1, "-I <int>"));
/*  65:    */     
/*  66:    */ 
/*  67:185 */     newVector.addElement(new Option("\tThe exponent for the polynomial kernel.\n\t(default 1)", "E", 1, "-E <double>"));
/*  68:    */     
/*  69:    */ 
/*  70:188 */     newVector.addElement(new Option("\tThe seed for the random number generation.\n\t(default 1)", "S", 1, "-S <int>"));
/*  71:    */     
/*  72:    */ 
/*  73:191 */     newVector.addElement(new Option("\tThe maximum number of alterations allowed.\n\t(default 10000)", "M", 1, "-M <int>"));
/*  74:    */     
/*  75:    */ 
/*  76:    */ 
/*  77:195 */     newVector.addAll(Collections.list(super.listOptions()));
/*  78:    */     
/*  79:197 */     return newVector.elements();
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void setOptions(String[] options)
/*  83:    */     throws Exception
/*  84:    */   {
/*  85:229 */     String iterationsString = Utils.getOption('I', options);
/*  86:230 */     if (iterationsString.length() != 0) {
/*  87:231 */       this.m_NumIterations = Integer.parseInt(iterationsString);
/*  88:    */     } else {
/*  89:233 */       this.m_NumIterations = 1;
/*  90:    */     }
/*  91:235 */     String exponentsString = Utils.getOption('E', options);
/*  92:236 */     if (exponentsString.length() != 0) {
/*  93:237 */       this.m_Exponent = new Double(exponentsString).doubleValue();
/*  94:    */     } else {
/*  95:239 */       this.m_Exponent = 1.0D;
/*  96:    */     }
/*  97:241 */     String seedString = Utils.getOption('S', options);
/*  98:242 */     if (seedString.length() != 0) {
/*  99:243 */       this.m_Seed = Integer.parseInt(seedString);
/* 100:    */     } else {
/* 101:245 */       this.m_Seed = 1;
/* 102:    */     }
/* 103:247 */     String alterationsString = Utils.getOption('M', options);
/* 104:248 */     if (alterationsString.length() != 0) {
/* 105:249 */       this.m_MaxK = Integer.parseInt(alterationsString);
/* 106:    */     } else {
/* 107:251 */       this.m_MaxK = 10000;
/* 108:    */     }
/* 109:254 */     super.setOptions(options);
/* 110:    */     
/* 111:256 */     Utils.checkForRemainingOptions(options);
/* 112:    */   }
/* 113:    */   
/* 114:    */   public String[] getOptions()
/* 115:    */   {
/* 116:266 */     Vector<String> options = new Vector();
/* 117:    */     
/* 118:268 */     options.add("-I");options.add("" + this.m_NumIterations);
/* 119:269 */     options.add("-E");options.add("" + this.m_Exponent);
/* 120:270 */     options.add("-S");options.add("" + this.m_Seed);
/* 121:271 */     options.add("-M");options.add("" + this.m_MaxK);
/* 122:    */     
/* 123:273 */     Collections.addAll(options, super.getOptions());
/* 124:    */     
/* 125:275 */     return (String[])options.toArray(new String[0]);
/* 126:    */   }
/* 127:    */   
/* 128:    */   public Capabilities getCapabilities()
/* 129:    */   {
/* 130:284 */     Capabilities result = super.getCapabilities();
/* 131:285 */     result.disableAll();
/* 132:    */     
/* 133:    */ 
/* 134:288 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 135:289 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 136:290 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 137:291 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 138:    */     
/* 139:    */ 
/* 140:294 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/* 141:295 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 142:    */     
/* 143:    */ 
/* 144:298 */     result.setMinimumNumberInstances(0);
/* 145:    */     
/* 146:300 */     return result;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public void buildClassifier(Instances insts)
/* 150:    */     throws Exception
/* 151:    */   {
/* 152:312 */     getCapabilities().testWithFail(insts);
/* 153:    */     
/* 154:    */ 
/* 155:315 */     insts = new Instances(insts);
/* 156:316 */     insts.deleteWithMissingClass();
/* 157:    */     
/* 158:    */ 
/* 159:319 */     this.m_Train = new Instances(insts);
/* 160:320 */     this.m_ReplaceMissingValues = new ReplaceMissingValues();
/* 161:321 */     this.m_ReplaceMissingValues.setInputFormat(this.m_Train);
/* 162:322 */     this.m_Train = Filter.useFilter(this.m_Train, this.m_ReplaceMissingValues);
/* 163:    */     
/* 164:324 */     this.m_NominalToBinary = new NominalToBinary();
/* 165:325 */     this.m_NominalToBinary.setInputFormat(this.m_Train);
/* 166:326 */     this.m_Train = Filter.useFilter(this.m_Train, this.m_NominalToBinary);
/* 167:    */     
/* 168:    */ 
/* 169:329 */     this.m_Train.randomize(new Random(this.m_Seed));
/* 170:    */     
/* 171:    */ 
/* 172:332 */     this.m_Additions = new int[this.m_MaxK + 1];
/* 173:333 */     this.m_IsAddition = new boolean[this.m_MaxK + 1];
/* 174:334 */     this.m_Weights = new int[this.m_MaxK + 1];
/* 175:    */     
/* 176:    */ 
/* 177:337 */     this.m_K = 0;
/* 178:339 */     for (int it = 0; it < this.m_NumIterations; it++) {
/* 179:340 */       for (int i = 0; i < this.m_Train.numInstances(); i++)
/* 180:    */       {
/* 181:341 */         Instance inst = this.m_Train.instance(i);
/* 182:342 */         if (!inst.classIsMissing())
/* 183:    */         {
/* 184:343 */           int prediction = makePrediction(this.m_K, inst);
/* 185:344 */           int classValue = (int)inst.classValue();
/* 186:345 */           if (prediction == classValue)
/* 187:    */           {
/* 188:346 */             this.m_Weights[this.m_K] += 1;
/* 189:    */           }
/* 190:    */           else
/* 191:    */           {
/* 192:348 */             this.m_IsAddition[this.m_K] = (classValue == 1 ? 1 : false);
/* 193:349 */             this.m_Additions[this.m_K] = i;
/* 194:350 */             this.m_K += 1;
/* 195:351 */             this.m_Weights[this.m_K] += 1;
/* 196:    */           }
/* 197:353 */           if (this.m_K == this.m_MaxK) {
/* 198:    */             return;
/* 199:    */           }
/* 200:    */         }
/* 201:    */       }
/* 202:    */     }
/* 203:    */   }
/* 204:    */   
/* 205:    */   public double[] distributionForInstance(Instance inst)
/* 206:    */     throws Exception
/* 207:    */   {
/* 208:372 */     this.m_ReplaceMissingValues.input(inst);
/* 209:373 */     this.m_ReplaceMissingValues.batchFinished();
/* 210:374 */     inst = this.m_ReplaceMissingValues.output();
/* 211:    */     
/* 212:376 */     this.m_NominalToBinary.input(inst);
/* 213:377 */     this.m_NominalToBinary.batchFinished();
/* 214:378 */     inst = this.m_NominalToBinary.output();
/* 215:    */     
/* 216:    */ 
/* 217:381 */     double output = 0.0D;double sumSoFar = 0.0D;
/* 218:382 */     if (this.m_K > 0) {
/* 219:383 */       for (int i = 0; i <= this.m_K; i++)
/* 220:    */       {
/* 221:384 */         if (sumSoFar < 0.0D) {
/* 222:385 */           output -= this.m_Weights[i];
/* 223:    */         } else {
/* 224:387 */           output += this.m_Weights[i];
/* 225:    */         }
/* 226:389 */         if (this.m_IsAddition[i] != 0) {
/* 227:390 */           sumSoFar += innerProduct(this.m_Train.instance(this.m_Additions[i]), inst);
/* 228:    */         } else {
/* 229:392 */           sumSoFar -= innerProduct(this.m_Train.instance(this.m_Additions[i]), inst);
/* 230:    */         }
/* 231:    */       }
/* 232:    */     }
/* 233:396 */     double[] result = new double[2];
/* 234:397 */     result[1] = (1.0D / (1.0D + Math.exp(-output)));
/* 235:398 */     result[0] = (1.0D - result[1]);
/* 236:    */     
/* 237:400 */     return result;
/* 238:    */   }
/* 239:    */   
/* 240:    */   public String toString()
/* 241:    */   {
/* 242:410 */     return "VotedPerceptron: Number of perceptrons=" + this.m_K;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public String maxKTipText()
/* 246:    */   {
/* 247:419 */     return "The maximum number of alterations to the perceptron.";
/* 248:    */   }
/* 249:    */   
/* 250:    */   public int getMaxK()
/* 251:    */   {
/* 252:429 */     return this.m_MaxK;
/* 253:    */   }
/* 254:    */   
/* 255:    */   public void setMaxK(int v)
/* 256:    */   {
/* 257:439 */     this.m_MaxK = v;
/* 258:    */   }
/* 259:    */   
/* 260:    */   public String numIterationsTipText()
/* 261:    */   {
/* 262:448 */     return "Number of iterations to be performed.";
/* 263:    */   }
/* 264:    */   
/* 265:    */   public int getNumIterations()
/* 266:    */   {
/* 267:458 */     return this.m_NumIterations;
/* 268:    */   }
/* 269:    */   
/* 270:    */   public void setNumIterations(int v)
/* 271:    */   {
/* 272:468 */     this.m_NumIterations = v;
/* 273:    */   }
/* 274:    */   
/* 275:    */   public String exponentTipText()
/* 276:    */   {
/* 277:477 */     return "Exponent for the polynomial kernel.";
/* 278:    */   }
/* 279:    */   
/* 280:    */   public double getExponent()
/* 281:    */   {
/* 282:487 */     return this.m_Exponent;
/* 283:    */   }
/* 284:    */   
/* 285:    */   public void setExponent(double v)
/* 286:    */   {
/* 287:497 */     this.m_Exponent = v;
/* 288:    */   }
/* 289:    */   
/* 290:    */   public String seedTipText()
/* 291:    */   {
/* 292:506 */     return "Seed for the random number generator.";
/* 293:    */   }
/* 294:    */   
/* 295:    */   public int getSeed()
/* 296:    */   {
/* 297:516 */     return this.m_Seed;
/* 298:    */   }
/* 299:    */   
/* 300:    */   public void setSeed(int v)
/* 301:    */   {
/* 302:526 */     this.m_Seed = v;
/* 303:    */   }
/* 304:    */   
/* 305:    */   private double innerProduct(Instance i1, Instance i2)
/* 306:    */     throws Exception
/* 307:    */   {
/* 308:540 */     double result = 0.0D;
/* 309:541 */     int n1 = i1.numValues();int n2 = i2.numValues();
/* 310:542 */     int classIndex = this.m_Train.classIndex();
/* 311:543 */     int p1 = 0;
/* 312:543 */     for (int p2 = 0; (p1 < n1) && (p2 < n2);)
/* 313:    */     {
/* 314:544 */       int ind1 = i1.index(p1);
/* 315:545 */       int ind2 = i2.index(p2);
/* 316:546 */       if (ind1 == ind2)
/* 317:    */       {
/* 318:547 */         if (ind1 != classIndex) {
/* 319:548 */           result += i1.valueSparse(p1) * i2.valueSparse(p2);
/* 320:    */         }
/* 321:551 */         p1++;p2++;
/* 322:    */       }
/* 323:552 */       else if (ind1 > ind2)
/* 324:    */       {
/* 325:553 */         p2++;
/* 326:    */       }
/* 327:    */       else
/* 328:    */       {
/* 329:555 */         p1++;
/* 330:    */       }
/* 331:    */     }
/* 332:558 */     result += 1.0D;
/* 333:560 */     if (this.m_Exponent != 1.0D) {
/* 334:561 */       return Math.pow(result, this.m_Exponent);
/* 335:    */     }
/* 336:563 */     return result;
/* 337:    */   }
/* 338:    */   
/* 339:    */   private int makePrediction(int k, Instance inst)
/* 340:    */     throws Exception
/* 341:    */   {
/* 342:577 */     double result = 0.0D;
/* 343:578 */     for (int i = 0; i < k; i++) {
/* 344:579 */       if (this.m_IsAddition[i] != 0) {
/* 345:580 */         result += innerProduct(this.m_Train.instance(this.m_Additions[i]), inst);
/* 346:    */       } else {
/* 347:582 */         result -= innerProduct(this.m_Train.instance(this.m_Additions[i]), inst);
/* 348:    */       }
/* 349:    */     }
/* 350:585 */     if (result < 0.0D) {
/* 351:586 */       return 0;
/* 352:    */     }
/* 353:588 */     return 1;
/* 354:    */   }
/* 355:    */   
/* 356:    */   public String getRevision()
/* 357:    */   {
/* 358:598 */     return RevisionUtils.extract("$Revision: 10141 $");
/* 359:    */   }
/* 360:    */   
/* 361:    */   public static void main(String[] argv)
/* 362:    */   {
/* 363:607 */     runClassifier(new VotedPerceptron(), argv);
/* 364:    */   }
/* 365:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.VotedPerceptron
 * JD-Core Version:    0.7.0.1
 */