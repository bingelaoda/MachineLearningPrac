/*   1:    */ package weka.classifiers.meta.nestedDichotomies;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Hashtable;
/*   5:    */ import java.util.Random;
/*   6:    */ import weka.classifiers.Classifier;
/*   7:    */ import weka.classifiers.RandomizableSingleClassifierEnhancer;
/*   8:    */ import weka.classifiers.meta.FilteredClassifier;
/*   9:    */ import weka.classifiers.rules.ZeroR;
/*  10:    */ import weka.classifiers.trees.J48;
/*  11:    */ import weka.core.Capabilities;
/*  12:    */ import weka.core.Capabilities.Capability;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Range;
/*  16:    */ import weka.core.RevisionUtils;
/*  17:    */ import weka.core.TechnicalInformation;
/*  18:    */ import weka.core.TechnicalInformation.Field;
/*  19:    */ import weka.core.TechnicalInformation.Type;
/*  20:    */ import weka.core.TechnicalInformationHandler;
/*  21:    */ import weka.core.Utils;
/*  22:    */ import weka.filters.Filter;
/*  23:    */ import weka.filters.unsupervised.attribute.MakeIndicator;
/*  24:    */ import weka.filters.unsupervised.instance.RemoveWithValues;
/*  25:    */ 
/*  26:    */ public class ClassBalancedND
/*  27:    */   extends RandomizableSingleClassifierEnhancer
/*  28:    */   implements TechnicalInformationHandler
/*  29:    */ {
/*  30:    */   static final long serialVersionUID = 5944063630650811903L;
/*  31:    */   protected FilteredClassifier m_FilteredClassifier;
/*  32:    */   protected Hashtable<String, Classifier> m_classifiers;
/*  33:181 */   protected ClassBalancedND m_FirstSuccessor = null;
/*  34:184 */   protected ClassBalancedND m_SecondSuccessor = null;
/*  35:187 */   protected Range m_Range = null;
/*  36:190 */   protected boolean m_hashtablegiven = false;
/*  37:    */   
/*  38:    */   public ClassBalancedND()
/*  39:    */   {
/*  40:197 */     this.m_Classifier = new J48();
/*  41:    */   }
/*  42:    */   
/*  43:    */   protected String defaultClassifierString()
/*  44:    */   {
/*  45:208 */     return "weka.classifiers.trees.J48";
/*  46:    */   }
/*  47:    */   
/*  48:    */   public TechnicalInformation getTechnicalInformation()
/*  49:    */   {
/*  50:223 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  51:224 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Lin Dong and Eibe Frank and Stefan Kramer");
/*  52:225 */     result.setValue(TechnicalInformation.Field.TITLE, "Ensembles of Balanced Nested Dichotomies for Multi-class Problems");
/*  53:    */     
/*  54:227 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "PKDD");
/*  55:228 */     result.setValue(TechnicalInformation.Field.YEAR, "2005");
/*  56:229 */     result.setValue(TechnicalInformation.Field.PAGES, "84-95");
/*  57:230 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*  58:    */     
/*  59:232 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.INPROCEEDINGS);
/*  60:233 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Eibe Frank and Stefan Kramer");
/*  61:234 */     additional.setValue(TechnicalInformation.Field.TITLE, "Ensembles of nested dichotomies for multi-class problems");
/*  62:    */     
/*  63:236 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "Twenty-first International Conference on Machine Learning");
/*  64:    */     
/*  65:238 */     additional.setValue(TechnicalInformation.Field.YEAR, "2004");
/*  66:239 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "ACM");
/*  67:    */     
/*  68:241 */     return result;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setHashtable(Hashtable<String, Classifier> table)
/*  72:    */   {
/*  73:251 */     this.m_hashtablegiven = true;
/*  74:252 */     this.m_classifiers = table;
/*  75:    */   }
/*  76:    */   
/*  77:    */   private void generateClassifierForNode(Instances data, Range classes, Random rand, Classifier classifier, Hashtable<String, Classifier> table)
/*  78:    */     throws Exception
/*  79:    */   {
/*  80:270 */     int[] indices = classes.getSelection();
/*  81:273 */     for (int j = indices.length - 1; j > 0; j--)
/*  82:    */     {
/*  83:274 */       int randPos = rand.nextInt(j + 1);
/*  84:275 */       int temp = indices[randPos];
/*  85:276 */       indices[randPos] = indices[j];
/*  86:277 */       indices[j] = temp;
/*  87:    */     }
/*  88:281 */     int first = indices.length / 2;
/*  89:282 */     int second = indices.length - first;
/*  90:283 */     int[] firstInds = new int[first];
/*  91:284 */     int[] secondInds = new int[second];
/*  92:285 */     System.arraycopy(indices, 0, firstInds, 0, first);
/*  93:286 */     System.arraycopy(indices, first, secondInds, 0, second);
/*  94:    */     
/*  95:    */ 
/*  96:289 */     int[] sortedFirst = Utils.sort(firstInds);
/*  97:290 */     int[] sortedSecond = Utils.sort(secondInds);
/*  98:291 */     int[] firstCopy = new int[first];
/*  99:292 */     int[] secondCopy = new int[second];
/* 100:293 */     for (int i = 0; i < sortedFirst.length; i++) {
/* 101:294 */       firstCopy[i] = firstInds[sortedFirst[i]];
/* 102:    */     }
/* 103:296 */     firstInds = firstCopy;
/* 104:297 */     for (int i = 0; i < sortedSecond.length; i++) {
/* 105:298 */       secondCopy[i] = secondInds[sortedSecond[i]];
/* 106:    */     }
/* 107:300 */     secondInds = secondCopy;
/* 108:303 */     if (firstInds[0] > secondInds[0])
/* 109:    */     {
/* 110:304 */       int[] help = secondInds;
/* 111:305 */       secondInds = firstInds;
/* 112:306 */       firstInds = help;
/* 113:307 */       int help2 = second;
/* 114:308 */       second = first;
/* 115:309 */       first = help2;
/* 116:    */     }
/* 117:312 */     this.m_Range = new Range(Range.indicesToRangeList(firstInds));
/* 118:313 */     this.m_Range.setUpper(data.numClasses() - 1);
/* 119:    */     
/* 120:315 */     Range secondRange = new Range(Range.indicesToRangeList(secondInds));
/* 121:316 */     secondRange.setUpper(data.numClasses() - 1);
/* 122:    */     
/* 123:    */ 
/* 124:319 */     MakeIndicator filter = new MakeIndicator();
/* 125:320 */     filter.setAttributeIndex("" + (data.classIndex() + 1));
/* 126:321 */     filter.setValueIndices(this.m_Range.getRanges());
/* 127:322 */     filter.setNumeric(false);
/* 128:323 */     filter.setInputFormat(data);
/* 129:324 */     this.m_FilteredClassifier = new FilteredClassifier();
/* 130:325 */     this.m_FilteredClassifier.setDoNotCheckForModifiedClassAttribute(true);
/* 131:326 */     if (data.numInstances() > 0) {
/* 132:327 */       this.m_FilteredClassifier.setClassifier(weka.classifiers.AbstractClassifier.makeCopies(classifier, 1)[0]);
/* 133:    */     } else {
/* 134:330 */       this.m_FilteredClassifier.setClassifier(new ZeroR());
/* 135:    */     }
/* 136:332 */     this.m_FilteredClassifier.setFilter(filter);
/* 137:    */     
/* 138:    */ 
/* 139:335 */     this.m_classifiers = table;
/* 140:337 */     if (!this.m_classifiers.containsKey(getString(firstInds) + "|" + getString(secondInds)))
/* 141:    */     {
/* 142:339 */       this.m_FilteredClassifier.buildClassifier(data);
/* 143:340 */       this.m_classifiers.put(getString(firstInds) + "|" + getString(secondInds), this.m_FilteredClassifier);
/* 144:    */     }
/* 145:    */     else
/* 146:    */     {
/* 147:343 */       this.m_FilteredClassifier = ((FilteredClassifier)this.m_classifiers.get(getString(firstInds) + "|" + getString(secondInds)));
/* 148:    */     }
/* 149:348 */     this.m_FirstSuccessor = new ClassBalancedND();
/* 150:349 */     if (first == 1)
/* 151:    */     {
/* 152:350 */       this.m_FirstSuccessor.m_Range = this.m_Range;
/* 153:    */     }
/* 154:    */     else
/* 155:    */     {
/* 156:352 */       RemoveWithValues rwv = new RemoveWithValues();
/* 157:353 */       rwv.setInvertSelection(true);
/* 158:354 */       rwv.setNominalIndices(this.m_Range.getRanges());
/* 159:355 */       rwv.setAttributeIndex("" + (data.classIndex() + 1));
/* 160:356 */       rwv.setInputFormat(data);
/* 161:357 */       Instances firstSubset = Filter.useFilter(data, rwv);
/* 162:358 */       this.m_FirstSuccessor.generateClassifierForNode(firstSubset, this.m_Range, rand, classifier, this.m_classifiers);
/* 163:    */     }
/* 164:361 */     this.m_SecondSuccessor = new ClassBalancedND();
/* 165:362 */     if (second == 1)
/* 166:    */     {
/* 167:363 */       this.m_SecondSuccessor.m_Range = secondRange;
/* 168:    */     }
/* 169:    */     else
/* 170:    */     {
/* 171:365 */       RemoveWithValues rwv = new RemoveWithValues();
/* 172:366 */       rwv.setInvertSelection(true);
/* 173:367 */       rwv.setNominalIndices(secondRange.getRanges());
/* 174:368 */       rwv.setAttributeIndex("" + (data.classIndex() + 1));
/* 175:369 */       rwv.setInputFormat(data);
/* 176:370 */       Instances secondSubset = Filter.useFilter(data, rwv);
/* 177:371 */       this.m_SecondSuccessor = new ClassBalancedND();
/* 178:    */       
/* 179:373 */       this.m_SecondSuccessor.generateClassifierForNode(secondSubset, secondRange, rand, classifier, this.m_classifiers);
/* 180:    */     }
/* 181:    */   }
/* 182:    */   
/* 183:    */   public Capabilities getCapabilities()
/* 184:    */   {
/* 185:385 */     Capabilities result = super.getCapabilities();
/* 186:    */     
/* 187:    */ 
/* 188:388 */     result.disableAllClasses();
/* 189:389 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 190:390 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 191:    */     
/* 192:    */ 
/* 193:393 */     result.setMinimumNumberInstances(1);
/* 194:    */     
/* 195:395 */     return result;
/* 196:    */   }
/* 197:    */   
/* 198:    */   public void buildClassifier(Instances data)
/* 199:    */     throws Exception
/* 200:    */   {
/* 201:408 */     getCapabilities().testWithFail(data);
/* 202:    */     
/* 203:    */ 
/* 204:411 */     data = new Instances(data);
/* 205:412 */     data.deleteWithMissingClass();
/* 206:    */     
/* 207:414 */     Random random = data.getRandomNumberGenerator(this.m_Seed);
/* 208:416 */     if (!this.m_hashtablegiven) {
/* 209:417 */       this.m_classifiers = new Hashtable();
/* 210:    */     }
/* 211:422 */     boolean[] present = new boolean[data.numClasses()];
/* 212:423 */     for (int i = 0; i < data.numInstances(); i++) {
/* 213:424 */       present[((int)data.instance(i).classValue())] = true;
/* 214:    */     }
/* 215:426 */     StringBuffer list = new StringBuffer();
/* 216:427 */     for (int i = 0; i < present.length; i++) {
/* 217:428 */       if (present[i] != 0)
/* 218:    */       {
/* 219:429 */         if (list.length() > 0) {
/* 220:430 */           list.append(",");
/* 221:    */         }
/* 222:432 */         list.append(i + 1);
/* 223:    */       }
/* 224:    */     }
/* 225:436 */     Range newRange = new Range(list.toString());
/* 226:437 */     newRange.setUpper(data.numClasses() - 1);
/* 227:    */     
/* 228:439 */     generateClassifierForNode(data, newRange, random, this.m_Classifier, this.m_classifiers);
/* 229:    */   }
/* 230:    */   
/* 231:    */   public double[] distributionForInstance(Instance inst)
/* 232:    */     throws Exception
/* 233:    */   {
/* 234:453 */     double[] newDist = new double[inst.numClasses()];
/* 235:454 */     if (this.m_FirstSuccessor == null)
/* 236:    */     {
/* 237:455 */       for (int i = 0; i < inst.numClasses(); i++) {
/* 238:456 */         if (this.m_Range.isInRange(i)) {
/* 239:457 */           newDist[i] = 1.0D;
/* 240:    */         }
/* 241:    */       }
/* 242:460 */       return newDist;
/* 243:    */     }
/* 244:462 */     double[] firstDist = this.m_FirstSuccessor.distributionForInstance(inst);
/* 245:463 */     double[] secondDist = this.m_SecondSuccessor.distributionForInstance(inst);
/* 246:464 */     double[] dist = this.m_FilteredClassifier.distributionForInstance(inst);
/* 247:465 */     for (int i = 0; i < inst.numClasses(); i++)
/* 248:    */     {
/* 249:466 */       if ((firstDist[i] > 0.0D) && (secondDist[i] > 0.0D)) {
/* 250:467 */         System.err.println("Panik!!");
/* 251:    */       }
/* 252:469 */       if (this.m_Range.isInRange(i)) {
/* 253:470 */         newDist[i] = (dist[1] * firstDist[i]);
/* 254:    */       } else {
/* 255:472 */         newDist[i] = (dist[0] * secondDist[i]);
/* 256:    */       }
/* 257:    */     }
/* 258:475 */     return newDist;
/* 259:    */   }
/* 260:    */   
/* 261:    */   public String getString(int[] indices)
/* 262:    */   {
/* 263:487 */     StringBuffer string = new StringBuffer();
/* 264:488 */     for (int i = 0; i < indices.length; i++)
/* 265:    */     {
/* 266:489 */       if (i > 0) {
/* 267:490 */         string.append(',');
/* 268:    */       }
/* 269:492 */       string.append(indices[i]);
/* 270:    */     }
/* 271:494 */     return string.toString();
/* 272:    */   }
/* 273:    */   
/* 274:    */   public String globalInfo()
/* 275:    */   {
/* 276:503 */     return "A meta classifier for handling multi-class datasets with 2-class classifiers by building a random class-balanced tree structure.\n\nFor more info, check\n\n" + getTechnicalInformation().toString();
/* 277:    */   }
/* 278:    */   
/* 279:    */   public String toString()
/* 280:    */   {
/* 281:516 */     if (this.m_classifiers == null) {
/* 282:517 */       return "ClassBalancedND: No model built yet.";
/* 283:    */     }
/* 284:519 */     StringBuffer text = new StringBuffer();
/* 285:520 */     text.append("ClassBalancedND");
/* 286:521 */     treeToString(text, 0);
/* 287:    */     
/* 288:523 */     return text.toString();
/* 289:    */   }
/* 290:    */   
/* 291:    */   private int treeToString(StringBuffer text, int nn)
/* 292:    */   {
/* 293:535 */     nn++;
/* 294:536 */     text.append("\n\nNode number: " + nn + "\n\n");
/* 295:537 */     if (this.m_FilteredClassifier != null) {
/* 296:538 */       text.append(this.m_FilteredClassifier);
/* 297:    */     } else {
/* 298:540 */       text.append("null");
/* 299:    */     }
/* 300:542 */     if (this.m_FirstSuccessor != null)
/* 301:    */     {
/* 302:543 */       nn = this.m_FirstSuccessor.treeToString(text, nn);
/* 303:544 */       nn = this.m_SecondSuccessor.treeToString(text, nn);
/* 304:    */     }
/* 305:546 */     return nn;
/* 306:    */   }
/* 307:    */   
/* 308:    */   public String getRevision()
/* 309:    */   {
/* 310:556 */     return RevisionUtils.extract("$Revision: 12648 $");
/* 311:    */   }
/* 312:    */   
/* 313:    */   public static void main(String[] argv)
/* 314:    */   {
/* 315:565 */     runClassifier(new ClassBalancedND(), argv);
/* 316:    */   }
/* 317:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.nestedDichotomies.ClassBalancedND
 * JD-Core Version:    0.7.0.1
 */