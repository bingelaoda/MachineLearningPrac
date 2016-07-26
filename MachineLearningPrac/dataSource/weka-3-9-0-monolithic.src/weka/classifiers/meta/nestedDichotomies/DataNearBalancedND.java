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
/*  26:    */ public class DataNearBalancedND
/*  27:    */   extends RandomizableSingleClassifierEnhancer
/*  28:    */   implements TechnicalInformationHandler
/*  29:    */ {
/*  30:    */   static final long serialVersionUID = 5117477294209496368L;
/*  31:    */   protected FilteredClassifier m_FilteredClassifier;
/*  32:177 */   protected Hashtable<String, Classifier> m_classifiers = new Hashtable();
/*  33:180 */   protected DataNearBalancedND m_FirstSuccessor = null;
/*  34:183 */   protected DataNearBalancedND m_SecondSuccessor = null;
/*  35:186 */   protected Range m_Range = null;
/*  36:189 */   protected boolean m_hashtablegiven = false;
/*  37:    */   
/*  38:    */   public DataNearBalancedND()
/*  39:    */   {
/*  40:196 */     this.m_Classifier = new J48();
/*  41:    */   }
/*  42:    */   
/*  43:    */   protected String defaultClassifierString()
/*  44:    */   {
/*  45:207 */     return "weka.classifiers.trees.J48";
/*  46:    */   }
/*  47:    */   
/*  48:    */   public TechnicalInformation getTechnicalInformation()
/*  49:    */   {
/*  50:222 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  51:223 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Lin Dong and Eibe Frank and Stefan Kramer");
/*  52:224 */     result.setValue(TechnicalInformation.Field.TITLE, "Ensembles of Balanced Nested Dichotomies for Multi-class Problems");
/*  53:    */     
/*  54:226 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "PKDD");
/*  55:227 */     result.setValue(TechnicalInformation.Field.YEAR, "2005");
/*  56:228 */     result.setValue(TechnicalInformation.Field.PAGES, "84-95");
/*  57:229 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*  58:    */     
/*  59:231 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.INPROCEEDINGS);
/*  60:232 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Eibe Frank and Stefan Kramer");
/*  61:233 */     additional.setValue(TechnicalInformation.Field.TITLE, "Ensembles of nested dichotomies for multi-class problems");
/*  62:    */     
/*  63:235 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "Twenty-first International Conference on Machine Learning");
/*  64:    */     
/*  65:237 */     additional.setValue(TechnicalInformation.Field.YEAR, "2004");
/*  66:238 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "ACM");
/*  67:    */     
/*  68:240 */     return result;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setHashtable(Hashtable<String, Classifier> table)
/*  72:    */   {
/*  73:250 */     this.m_hashtablegiven = true;
/*  74:251 */     this.m_classifiers = table;
/*  75:    */   }
/*  76:    */   
/*  77:    */   private void generateClassifierForNode(Instances data, Range classes, Random rand, Classifier classifier, Hashtable<String, Classifier> table, double[] instsNumAllClasses)
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
/*  88:281 */     double total = 0.0D;
/*  89:282 */     for (int indice : indices) {
/*  90:283 */       total += instsNumAllClasses[indice];
/*  91:    */     }
/*  92:285 */     double halfOfTotal = total / 2.0D;
/*  93:    */     
/*  94:    */ 
/*  95:    */ 
/*  96:289 */     double sumLeft = 0.0D;double sumRight = 0.0D;
/*  97:290 */     int i = 0;int j = indices.length - 1;
/*  98:    */     do
/*  99:    */     {
/* 100:292 */       if (i == j)
/* 101:    */       {
/* 102:293 */         if (rand.nextBoolean()) {
/* 103:294 */           sumLeft += instsNumAllClasses[indices[(i++)]];
/* 104:    */         } else {
/* 105:296 */           sumRight += instsNumAllClasses[indices[(j--)]];
/* 106:    */         }
/* 107:    */       }
/* 108:    */       else
/* 109:    */       {
/* 110:299 */         sumLeft += instsNumAllClasses[indices[(i++)]];
/* 111:300 */         sumRight += instsNumAllClasses[indices[(j--)]];
/* 112:    */       }
/* 113:302 */     } while ((Utils.sm(sumLeft, halfOfTotal)) && (Utils.sm(sumRight, halfOfTotal)));
/* 114:304 */     int first = 0;int second = 0;
/* 115:305 */     if (!Utils.sm(sumLeft, halfOfTotal)) {
/* 116:306 */       first = i;
/* 117:    */     } else {
/* 118:308 */       first = j + 1;
/* 119:    */     }
/* 120:310 */     second = indices.length - first;
/* 121:    */     
/* 122:312 */     int[] firstInds = new int[first];
/* 123:313 */     int[] secondInds = new int[second];
/* 124:314 */     System.arraycopy(indices, 0, firstInds, 0, first);
/* 125:315 */     System.arraycopy(indices, first, secondInds, 0, second);
/* 126:    */     
/* 127:    */ 
/* 128:318 */     int[] sortedFirst = Utils.sort(firstInds);
/* 129:319 */     int[] sortedSecond = Utils.sort(secondInds);
/* 130:320 */     int[] firstCopy = new int[first];
/* 131:321 */     int[] secondCopy = new int[second];
/* 132:322 */     for (int k = 0; k < sortedFirst.length; k++) {
/* 133:323 */       firstCopy[k] = firstInds[sortedFirst[k]];
/* 134:    */     }
/* 135:325 */     firstInds = firstCopy;
/* 136:326 */     for (int k = 0; k < sortedSecond.length; k++) {
/* 137:327 */       secondCopy[k] = secondInds[sortedSecond[k]];
/* 138:    */     }
/* 139:329 */     secondInds = secondCopy;
/* 140:332 */     if (firstInds[0] > secondInds[0])
/* 141:    */     {
/* 142:333 */       int[] help = secondInds;
/* 143:334 */       secondInds = firstInds;
/* 144:335 */       firstInds = help;
/* 145:336 */       int help2 = second;
/* 146:337 */       second = first;
/* 147:338 */       first = help2;
/* 148:    */     }
/* 149:341 */     this.m_Range = new Range(Range.indicesToRangeList(firstInds));
/* 150:342 */     this.m_Range.setUpper(data.numClasses() - 1);
/* 151:    */     
/* 152:344 */     Range secondRange = new Range(Range.indicesToRangeList(secondInds));
/* 153:345 */     secondRange.setUpper(data.numClasses() - 1);
/* 154:    */     
/* 155:    */ 
/* 156:348 */     MakeIndicator filter = new MakeIndicator();
/* 157:349 */     filter.setAttributeIndex("" + (data.classIndex() + 1));
/* 158:350 */     filter.setValueIndices(this.m_Range.getRanges());
/* 159:351 */     filter.setNumeric(false);
/* 160:352 */     filter.setInputFormat(data);
/* 161:353 */     this.m_FilteredClassifier = new FilteredClassifier();
/* 162:354 */     this.m_FilteredClassifier.setDoNotCheckForModifiedClassAttribute(true);
/* 163:355 */     if (data.numInstances() > 0) {
/* 164:356 */       this.m_FilteredClassifier.setClassifier(weka.classifiers.AbstractClassifier.makeCopies(classifier, 1)[0]);
/* 165:    */     } else {
/* 166:359 */       this.m_FilteredClassifier.setClassifier(new ZeroR());
/* 167:    */     }
/* 168:361 */     this.m_FilteredClassifier.setFilter(filter);
/* 169:    */     
/* 170:    */ 
/* 171:364 */     this.m_classifiers = table;
/* 172:366 */     if (!this.m_classifiers.containsKey(getString(firstInds) + "|" + getString(secondInds)))
/* 173:    */     {
/* 174:368 */       this.m_FilteredClassifier.buildClassifier(data);
/* 175:369 */       this.m_classifiers.put(getString(firstInds) + "|" + getString(secondInds), this.m_FilteredClassifier);
/* 176:    */     }
/* 177:    */     else
/* 178:    */     {
/* 179:372 */       this.m_FilteredClassifier = ((FilteredClassifier)this.m_classifiers.get(getString(firstInds) + "|" + getString(secondInds)));
/* 180:    */     }
/* 181:377 */     this.m_FirstSuccessor = new DataNearBalancedND();
/* 182:378 */     if (first == 1)
/* 183:    */     {
/* 184:379 */       this.m_FirstSuccessor.m_Range = this.m_Range;
/* 185:    */     }
/* 186:    */     else
/* 187:    */     {
/* 188:381 */       RemoveWithValues rwv = new RemoveWithValues();
/* 189:382 */       rwv.setInvertSelection(true);
/* 190:383 */       rwv.setNominalIndices(this.m_Range.getRanges());
/* 191:384 */       rwv.setAttributeIndex("" + (data.classIndex() + 1));
/* 192:385 */       rwv.setInputFormat(data);
/* 193:386 */       Instances firstSubset = Filter.useFilter(data, rwv);
/* 194:387 */       this.m_FirstSuccessor.generateClassifierForNode(firstSubset, this.m_Range, rand, classifier, this.m_classifiers, instsNumAllClasses);
/* 195:    */     }
/* 196:390 */     this.m_SecondSuccessor = new DataNearBalancedND();
/* 197:391 */     if (second == 1)
/* 198:    */     {
/* 199:392 */       this.m_SecondSuccessor.m_Range = secondRange;
/* 200:    */     }
/* 201:    */     else
/* 202:    */     {
/* 203:394 */       RemoveWithValues rwv = new RemoveWithValues();
/* 204:395 */       rwv.setInvertSelection(true);
/* 205:396 */       rwv.setNominalIndices(secondRange.getRanges());
/* 206:397 */       rwv.setAttributeIndex("" + (data.classIndex() + 1));
/* 207:398 */       rwv.setInputFormat(data);
/* 208:399 */       Instances secondSubset = Filter.useFilter(data, rwv);
/* 209:400 */       this.m_SecondSuccessor = new DataNearBalancedND();
/* 210:    */       
/* 211:402 */       this.m_SecondSuccessor.generateClassifierForNode(secondSubset, secondRange, rand, classifier, this.m_classifiers, instsNumAllClasses);
/* 212:    */     }
/* 213:    */   }
/* 214:    */   
/* 215:    */   public Capabilities getCapabilities()
/* 216:    */   {
/* 217:414 */     Capabilities result = super.getCapabilities();
/* 218:    */     
/* 219:    */ 
/* 220:417 */     result.disableAllClasses();
/* 221:418 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 222:419 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 223:    */     
/* 224:    */ 
/* 225:422 */     result.setMinimumNumberInstances(1);
/* 226:    */     
/* 227:424 */     return result;
/* 228:    */   }
/* 229:    */   
/* 230:    */   public void buildClassifier(Instances data)
/* 231:    */     throws Exception
/* 232:    */   {
/* 233:437 */     getCapabilities().testWithFail(data);
/* 234:    */     
/* 235:    */ 
/* 236:440 */     data = new Instances(data);
/* 237:441 */     data.deleteWithMissingClass();
/* 238:    */     
/* 239:443 */     Random random = data.getRandomNumberGenerator(this.m_Seed);
/* 240:445 */     if (!this.m_hashtablegiven) {
/* 241:446 */       this.m_classifiers = new Hashtable();
/* 242:    */     }
/* 243:451 */     boolean[] present = new boolean[data.numClasses()];
/* 244:452 */     for (int i = 0; i < data.numInstances(); i++) {
/* 245:453 */       present[((int)data.instance(i).classValue())] = true;
/* 246:    */     }
/* 247:455 */     StringBuffer list = new StringBuffer();
/* 248:456 */     for (int i = 0; i < present.length; i++) {
/* 249:457 */       if (present[i] != 0)
/* 250:    */       {
/* 251:458 */         if (list.length() > 0) {
/* 252:459 */           list.append(",");
/* 253:    */         }
/* 254:461 */         list.append(i + 1);
/* 255:    */       }
/* 256:    */     }
/* 257:466 */     double[] instsNum = new double[data.numClasses()];
/* 258:467 */     for (int i = 0; i < data.numInstances(); i++) {
/* 259:468 */       instsNum[((int)data.instance(i).classValue())] += data.instance(i).weight();
/* 260:    */     }
/* 261:472 */     Range newRange = new Range(list.toString());
/* 262:473 */     newRange.setUpper(data.numClasses() - 1);
/* 263:    */     
/* 264:475 */     generateClassifierForNode(data, newRange, random, this.m_Classifier, this.m_classifiers, instsNum);
/* 265:    */   }
/* 266:    */   
/* 267:    */   public double[] distributionForInstance(Instance inst)
/* 268:    */     throws Exception
/* 269:    */   {
/* 270:489 */     double[] newDist = new double[inst.numClasses()];
/* 271:490 */     if (this.m_FirstSuccessor == null)
/* 272:    */     {
/* 273:491 */       for (int i = 0; i < inst.numClasses(); i++) {
/* 274:492 */         if (this.m_Range.isInRange(i)) {
/* 275:493 */           newDist[i] = 1.0D;
/* 276:    */         }
/* 277:    */       }
/* 278:496 */       return newDist;
/* 279:    */     }
/* 280:498 */     double[] firstDist = this.m_FirstSuccessor.distributionForInstance(inst);
/* 281:499 */     double[] secondDist = this.m_SecondSuccessor.distributionForInstance(inst);
/* 282:500 */     double[] dist = this.m_FilteredClassifier.distributionForInstance(inst);
/* 283:501 */     for (int i = 0; i < inst.numClasses(); i++)
/* 284:    */     {
/* 285:502 */       if ((firstDist[i] > 0.0D) && (secondDist[i] > 0.0D)) {
/* 286:503 */         System.err.println("Panik!!");
/* 287:    */       }
/* 288:505 */       if (this.m_Range.isInRange(i)) {
/* 289:506 */         newDist[i] = (dist[1] * firstDist[i]);
/* 290:    */       } else {
/* 291:508 */         newDist[i] = (dist[0] * secondDist[i]);
/* 292:    */       }
/* 293:    */     }
/* 294:511 */     if (!Utils.eq(Utils.sum(newDist), 1.0D))
/* 295:    */     {
/* 296:512 */       System.err.println(Utils.sum(newDist));
/* 297:513 */       for (double element : dist) {
/* 298:514 */         System.err.print(element + " ");
/* 299:    */       }
/* 300:516 */       System.err.println();
/* 301:517 */       for (double element : newDist) {
/* 302:518 */         System.err.print(element + " ");
/* 303:    */       }
/* 304:520 */       System.err.println();
/* 305:521 */       System.err.println(inst);
/* 306:522 */       System.err.println(this.m_FilteredClassifier);
/* 307:    */       
/* 308:524 */       System.err.println("bad");
/* 309:    */     }
/* 310:526 */     return newDist;
/* 311:    */   }
/* 312:    */   
/* 313:    */   public String getString(int[] indices)
/* 314:    */   {
/* 315:538 */     StringBuffer string = new StringBuffer();
/* 316:539 */     for (int i = 0; i < indices.length; i++)
/* 317:    */     {
/* 318:540 */       if (i > 0) {
/* 319:541 */         string.append(',');
/* 320:    */       }
/* 321:543 */       string.append(indices[i]);
/* 322:    */     }
/* 323:545 */     return string.toString();
/* 324:    */   }
/* 325:    */   
/* 326:    */   public String globalInfo()
/* 327:    */   {
/* 328:554 */     return "A meta classifier for handling multi-class datasets with 2-class classifiers by building a random data-balanced tree structure.\n\nFor more info, check\n\n" + getTechnicalInformation().toString();
/* 329:    */   }
/* 330:    */   
/* 331:    */   public String toString()
/* 332:    */   {
/* 333:567 */     if (this.m_classifiers == null) {
/* 334:568 */       return "DataNearBalancedND: No model built yet.";
/* 335:    */     }
/* 336:570 */     StringBuffer text = new StringBuffer();
/* 337:571 */     text.append("DataNearBalancedND");
/* 338:572 */     treeToString(text, 0);
/* 339:    */     
/* 340:574 */     return text.toString();
/* 341:    */   }
/* 342:    */   
/* 343:    */   private int treeToString(StringBuffer text, int nn)
/* 344:    */   {
/* 345:586 */     nn++;
/* 346:587 */     text.append("\n\nNode number: " + nn + "\n\n");
/* 347:588 */     if (this.m_FilteredClassifier != null) {
/* 348:589 */       text.append(this.m_FilteredClassifier);
/* 349:    */     } else {
/* 350:591 */       text.append("null");
/* 351:    */     }
/* 352:593 */     if (this.m_FirstSuccessor != null)
/* 353:    */     {
/* 354:594 */       nn = this.m_FirstSuccessor.treeToString(text, nn);
/* 355:595 */       nn = this.m_SecondSuccessor.treeToString(text, nn);
/* 356:    */     }
/* 357:597 */     return nn;
/* 358:    */   }
/* 359:    */   
/* 360:    */   public String getRevision()
/* 361:    */   {
/* 362:607 */     return RevisionUtils.extract("$Revision: 12648 $");
/* 363:    */   }
/* 364:    */   
/* 365:    */   public static void main(String[] argv)
/* 366:    */   {
/* 367:616 */     runClassifier(new DataNearBalancedND(), argv);
/* 368:    */   }
/* 369:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.nestedDichotomies.DataNearBalancedND
 * JD-Core Version:    0.7.0.1
 */