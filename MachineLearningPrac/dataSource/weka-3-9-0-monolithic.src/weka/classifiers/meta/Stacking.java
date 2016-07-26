/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Random;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.classifiers.AbstractClassifier;
/*   9:    */ import weka.classifiers.Classifier;
/*  10:    */ import weka.classifiers.RandomizableParallelMultipleClassifiersCombiner;
/*  11:    */ import weka.classifiers.rules.ZeroR;
/*  12:    */ import weka.core.Attribute;
/*  13:    */ import weka.core.Capabilities;
/*  14:    */ import weka.core.CommandlineRunnable;
/*  15:    */ import weka.core.DenseInstance;
/*  16:    */ import weka.core.Instance;
/*  17:    */ import weka.core.Instances;
/*  18:    */ import weka.core.Option;
/*  19:    */ import weka.core.OptionHandler;
/*  20:    */ import weka.core.RevisionUtils;
/*  21:    */ import weka.core.TechnicalInformation;
/*  22:    */ import weka.core.TechnicalInformation.Field;
/*  23:    */ import weka.core.TechnicalInformation.Type;
/*  24:    */ import weka.core.TechnicalInformationHandler;
/*  25:    */ import weka.core.Utils;
/*  26:    */ 
/*  27:    */ public class Stacking
/*  28:    */   extends RandomizableParallelMultipleClassifiersCombiner
/*  29:    */   implements TechnicalInformationHandler
/*  30:    */ {
/*  31:    */   static final long serialVersionUID = 5134738557155845452L;
/*  32:100 */   protected Classifier m_MetaClassifier = new ZeroR();
/*  33:103 */   protected Instances m_MetaFormat = null;
/*  34:106 */   protected Instances m_BaseFormat = null;
/*  35:109 */   protected int m_NumFolds = 10;
/*  36:    */   
/*  37:    */   public String globalInfo()
/*  38:    */   {
/*  39:118 */     return "Combines several classifiers using the stacking method. Can do classification or regression.\n\nFor more information, see\n\n" + getTechnicalInformation().toString();
/*  40:    */   }
/*  41:    */   
/*  42:    */   public TechnicalInformation getTechnicalInformation()
/*  43:    */   {
/*  44:134 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  45:135 */     result.setValue(TechnicalInformation.Field.AUTHOR, "David H. Wolpert");
/*  46:136 */     result.setValue(TechnicalInformation.Field.YEAR, "1992");
/*  47:137 */     result.setValue(TechnicalInformation.Field.TITLE, "Stacked generalization");
/*  48:138 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Neural Networks");
/*  49:139 */     result.setValue(TechnicalInformation.Field.VOLUME, "5");
/*  50:140 */     result.setValue(TechnicalInformation.Field.PAGES, "241-259");
/*  51:141 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Pergamon Press");
/*  52:    */     
/*  53:143 */     return result;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public Enumeration<Option> listOptions()
/*  57:    */   {
/*  58:153 */     Vector<Option> newVector = new Vector(2);
/*  59:154 */     newVector.addElement(new Option(metaOption(), "M", 0, "-M <scheme specification>"));
/*  60:    */     
/*  61:    */ 
/*  62:157 */     newVector.addElement(new Option("\tSets the number of cross-validation folds.", "X", 1, "-X <number of folds>"));
/*  63:    */     
/*  64:    */ 
/*  65:    */ 
/*  66:161 */     newVector.addAll(Collections.list(super.listOptions()));
/*  67:163 */     if ((getMetaClassifier() instanceof OptionHandler))
/*  68:    */     {
/*  69:164 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to meta classifier " + getMetaClassifier().getClass().getName() + ":"));
/*  70:    */       
/*  71:    */ 
/*  72:    */ 
/*  73:168 */       newVector.addAll(Collections.list(((OptionHandler)getMetaClassifier()).listOptions()));
/*  74:    */     }
/*  75:170 */     return newVector.elements();
/*  76:    */   }
/*  77:    */   
/*  78:    */   protected String metaOption()
/*  79:    */   {
/*  80:180 */     return "\tFull name of meta classifier, followed by options.\n\t(default: \"weka.classifiers.rules.Zero\")";
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void setOptions(String[] options)
/*  84:    */     throws Exception
/*  85:    */   {
/*  86:217 */     String numFoldsString = Utils.getOption('X', options);
/*  87:218 */     if (numFoldsString.length() != 0) {
/*  88:219 */       setNumFolds(Integer.parseInt(numFoldsString));
/*  89:    */     } else {
/*  90:221 */       setNumFolds(10);
/*  91:    */     }
/*  92:223 */     processMetaOptions(options);
/*  93:224 */     super.setOptions(options);
/*  94:    */   }
/*  95:    */   
/*  96:    */   protected void processMetaOptions(String[] options)
/*  97:    */     throws Exception
/*  98:    */   {
/*  99:235 */     String classifierString = Utils.getOption('M', options);
/* 100:236 */     String[] classifierSpec = Utils.splitOptions(classifierString);
/* 101:    */     String classifierName;
/* 102:    */     String classifierName;
/* 103:238 */     if (classifierSpec.length == 0)
/* 104:    */     {
/* 105:239 */       classifierName = "weka.classifiers.rules.ZeroR";
/* 106:    */     }
/* 107:    */     else
/* 108:    */     {
/* 109:241 */       classifierName = classifierSpec[0];
/* 110:242 */       classifierSpec[0] = "";
/* 111:    */     }
/* 112:244 */     setMetaClassifier(AbstractClassifier.forName(classifierName, classifierSpec));
/* 113:    */   }
/* 114:    */   
/* 115:    */   public String[] getOptions()
/* 116:    */   {
/* 117:254 */     String[] superOptions = super.getOptions();
/* 118:255 */     String[] options = new String[superOptions.length + 4];
/* 119:    */     
/* 120:257 */     int current = 0;
/* 121:258 */     options[(current++)] = "-X";options[(current++)] = ("" + getNumFolds());
/* 122:259 */     options[(current++)] = "-M";
/* 123:260 */     options[(current++)] = (getMetaClassifier().getClass().getName() + " " + Utils.joinOptions(((OptionHandler)getMetaClassifier()).getOptions()));
/* 124:    */     
/* 125:    */ 
/* 126:263 */     System.arraycopy(superOptions, 0, options, current, superOptions.length);
/* 127:    */     
/* 128:265 */     return options;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public String numFoldsTipText()
/* 132:    */   {
/* 133:274 */     return "The number of folds used for cross-validation.";
/* 134:    */   }
/* 135:    */   
/* 136:    */   public int getNumFolds()
/* 137:    */   {
/* 138:284 */     return this.m_NumFolds;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public void setNumFolds(int numFolds)
/* 142:    */     throws Exception
/* 143:    */   {
/* 144:295 */     if (numFolds < 0) {
/* 145:296 */       throw new IllegalArgumentException("Stacking: Number of cross-validation folds must be positive.");
/* 146:    */     }
/* 147:299 */     this.m_NumFolds = numFolds;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public String metaClassifierTipText()
/* 151:    */   {
/* 152:308 */     return "The meta classifiers to be used.";
/* 153:    */   }
/* 154:    */   
/* 155:    */   public void setMetaClassifier(Classifier classifier)
/* 156:    */   {
/* 157:318 */     this.m_MetaClassifier = classifier;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public Classifier getMetaClassifier()
/* 161:    */   {
/* 162:328 */     return this.m_MetaClassifier;
/* 163:    */   }
/* 164:    */   
/* 165:    */   public Capabilities getCapabilities()
/* 166:    */   {
/* 167:340 */     Capabilities result = super.getCapabilities();
/* 168:341 */     result.setMinimumNumberInstances(getNumFolds());
/* 169:    */     
/* 170:343 */     return result;
/* 171:    */   }
/* 172:    */   
/* 173:    */   public void buildClassifier(Instances data)
/* 174:    */     throws Exception
/* 175:    */   {
/* 176:356 */     if (this.m_MetaClassifier == null) {
/* 177:357 */       throw new IllegalArgumentException("No meta classifier has been set");
/* 178:    */     }
/* 179:361 */     getCapabilities().testWithFail(data);
/* 180:    */     
/* 181:    */ 
/* 182:364 */     Instances newData = new Instances(data);
/* 183:365 */     this.m_BaseFormat = new Instances(data, 0);
/* 184:366 */     newData.deleteWithMissingClass();
/* 185:    */     
/* 186:368 */     Random random = new Random(this.m_Seed);
/* 187:369 */     newData.randomize(random);
/* 188:370 */     if (newData.classAttribute().isNominal()) {
/* 189:371 */       newData.stratify(this.m_NumFolds);
/* 190:    */     }
/* 191:375 */     generateMetaLevel(newData, random);
/* 192:    */     
/* 193:    */ 
/* 194:    */ 
/* 195:    */ 
/* 196:380 */     super.buildClassifier(newData);
/* 197:    */     
/* 198:    */ 
/* 199:383 */     buildClassifiers(newData);
/* 200:    */   }
/* 201:    */   
/* 202:    */   protected void generateMetaLevel(Instances newData, Random random)
/* 203:    */     throws Exception
/* 204:    */   {
/* 205:396 */     Instances metaData = metaFormat(newData);
/* 206:397 */     this.m_MetaFormat = new Instances(metaData, 0);
/* 207:398 */     for (int j = 0; j < this.m_NumFolds; j++)
/* 208:    */     {
/* 209:399 */       Instances train = newData.trainCV(this.m_NumFolds, j, random);
/* 210:    */       
/* 211:    */ 
/* 212:    */ 
/* 213:    */ 
/* 214:    */ 
/* 215:    */ 
/* 216:406 */       super.buildClassifier(train);
/* 217:    */       
/* 218:    */ 
/* 219:409 */       buildClassifiers(train);
/* 220:    */       
/* 221:    */ 
/* 222:412 */       Instances test = newData.testCV(this.m_NumFolds, j);
/* 223:413 */       for (int i = 0; i < test.numInstances(); i++) {
/* 224:414 */         metaData.add(metaInstance(test.instance(i)));
/* 225:    */       }
/* 226:    */     }
/* 227:418 */     this.m_MetaClassifier.buildClassifier(metaData);
/* 228:    */   }
/* 229:    */   
/* 230:    */   public double[] distributionForInstance(Instance instance)
/* 231:    */     throws Exception
/* 232:    */   {
/* 233:431 */     return this.m_MetaClassifier.distributionForInstance(metaInstance(instance));
/* 234:    */   }
/* 235:    */   
/* 236:    */   public String toString()
/* 237:    */   {
/* 238:441 */     if (this.m_Classifiers.length == 0) {
/* 239:442 */       return "Stacking: No base schemes entered.";
/* 240:    */     }
/* 241:444 */     if (this.m_MetaClassifier == null) {
/* 242:445 */       return "Stacking: No meta scheme selected.";
/* 243:    */     }
/* 244:447 */     if (this.m_MetaFormat == null) {
/* 245:448 */       return "Stacking: No model built yet.";
/* 246:    */     }
/* 247:450 */     String result = "Stacking\n\nBase classifiers\n\n";
/* 248:451 */     for (int i = 0; i < this.m_Classifiers.length; i++) {
/* 249:452 */       result = result + getClassifier(i).toString() + "\n\n";
/* 250:    */     }
/* 251:455 */     result = result + "\n\nMeta classifier\n\n";
/* 252:456 */     result = result + this.m_MetaClassifier.toString();
/* 253:    */     
/* 254:458 */     return result;
/* 255:    */   }
/* 256:    */   
/* 257:    */   protected Instances metaFormat(Instances instances)
/* 258:    */     throws Exception
/* 259:    */   {
/* 260:469 */     ArrayList<Attribute> attributes = new ArrayList();
/* 261:472 */     for (int k = 0; k < this.m_Classifiers.length; k++)
/* 262:    */     {
/* 263:473 */       Classifier classifier = getClassifier(k);
/* 264:474 */       String name = classifier.getClass().getName() + "-" + (k + 1);
/* 265:475 */       if (this.m_BaseFormat.classAttribute().isNumeric()) {
/* 266:476 */         attributes.add(new Attribute(name));
/* 267:    */       } else {
/* 268:478 */         for (int j = 0; j < this.m_BaseFormat.classAttribute().numValues(); j++) {
/* 269:479 */           attributes.add(new Attribute(name + ":" + this.m_BaseFormat.classAttribute().value(j)));
/* 270:    */         }
/* 271:    */       }
/* 272:    */     }
/* 273:485 */     attributes.add((Attribute)this.m_BaseFormat.classAttribute().copy());
/* 274:486 */     Instances metaFormat = new Instances("Meta format", attributes, 0);
/* 275:487 */     metaFormat.setClassIndex(metaFormat.numAttributes() - 1);
/* 276:488 */     return metaFormat;
/* 277:    */   }
/* 278:    */   
/* 279:    */   protected Instance metaInstance(Instance instance)
/* 280:    */     throws Exception
/* 281:    */   {
/* 282:500 */     double[] values = new double[this.m_MetaFormat.numAttributes()];
/* 283:    */     
/* 284:502 */     int i = 0;
/* 285:503 */     for (int k = 0; k < this.m_Classifiers.length; k++)
/* 286:    */     {
/* 287:504 */       Classifier classifier = getClassifier(k);
/* 288:505 */       if (this.m_BaseFormat.classAttribute().isNumeric())
/* 289:    */       {
/* 290:506 */         values[(i++)] = classifier.classifyInstance(instance);
/* 291:    */       }
/* 292:    */       else
/* 293:    */       {
/* 294:508 */         double[] dist = classifier.distributionForInstance(instance);
/* 295:509 */         for (int j = 0; j < dist.length; j++) {
/* 296:510 */           values[(i++)] = dist[j];
/* 297:    */         }
/* 298:    */       }
/* 299:    */     }
/* 300:514 */     values[i] = instance.classValue();
/* 301:515 */     Instance metaInstance = new DenseInstance(1.0D, values);
/* 302:516 */     metaInstance.setDataset(this.m_MetaFormat);
/* 303:517 */     return metaInstance;
/* 304:    */   }
/* 305:    */   
/* 306:    */   public void preExecution()
/* 307:    */     throws Exception
/* 308:    */   {
/* 309:522 */     super.preExecution();
/* 310:523 */     if ((getMetaClassifier() instanceof CommandlineRunnable)) {
/* 311:524 */       ((CommandlineRunnable)getMetaClassifier()).preExecution();
/* 312:    */     }
/* 313:    */   }
/* 314:    */   
/* 315:    */   public void postExecution()
/* 316:    */     throws Exception
/* 317:    */   {
/* 318:530 */     super.postExecution();
/* 319:531 */     if ((getMetaClassifier() instanceof CommandlineRunnable)) {
/* 320:532 */       ((CommandlineRunnable)getMetaClassifier()).postExecution();
/* 321:    */     }
/* 322:    */   }
/* 323:    */   
/* 324:    */   public String getRevision()
/* 325:    */   {
/* 326:542 */     return RevisionUtils.extract("$Revision: 12205 $");
/* 327:    */   }
/* 328:    */   
/* 329:    */   public static void main(String[] argv)
/* 330:    */   {
/* 331:552 */     runClassifier(new Stacking(), argv);
/* 332:    */   }
/* 333:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.Stacking
 * JD-Core Version:    0.7.0.1
 */