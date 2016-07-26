/*   1:    */ package weka.attributeSelection;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.BitSet;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Random;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.Option;
/*  10:    */ import weka.core.OptionHandler;
/*  11:    */ import weka.core.Range;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.TechnicalInformation;
/*  14:    */ import weka.core.TechnicalInformation.Field;
/*  15:    */ import weka.core.TechnicalInformation.Type;
/*  16:    */ import weka.core.TechnicalInformationHandler;
/*  17:    */ import weka.core.Utils;
/*  18:    */ 
/*  19:    */ public class RandomSearch
/*  20:    */   extends ASSearch
/*  21:    */   implements StartSetHandler, OptionHandler, TechnicalInformationHandler
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = 7479392617377425484L;
/*  24:    */   private int[] m_starting;
/*  25:    */   private Range m_startRange;
/*  26:    */   private BitSet m_bestGroup;
/*  27:    */   private double m_bestMerit;
/*  28:    */   private boolean m_onlyConsiderBetterAndSmaller;
/*  29:    */   private boolean m_hasClass;
/*  30:    */   private int m_classIndex;
/*  31:    */   private int m_numAttribs;
/*  32:    */   private int m_seed;
/*  33:    */   private double m_searchSize;
/*  34:    */   private int m_iterations;
/*  35:    */   private Random m_random;
/*  36:    */   private boolean m_verbose;
/*  37:    */   
/*  38:    */   public String globalInfo()
/*  39:    */   {
/*  40:169 */     return "RandomSearch : \n\nPerforms a Random search in the space of attribute subsets. If no start set is supplied, Random search starts from a random point and reports the best subset found. If a start set is supplied, Random searches randomly for subsets that are as good or better than the start point with the same or or fewer attributes. Using RandomSearch in conjunction with a start set containing all attributes equates to the LVF algorithm of Liu and Setiono (ICML-96).\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  41:    */   }
/*  42:    */   
/*  43:    */   public TechnicalInformation getTechnicalInformation()
/*  44:    */   {
/*  45:191 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  46:192 */     result.setValue(TechnicalInformation.Field.AUTHOR, "H. Liu and R. Setiono");
/*  47:193 */     result.setValue(TechnicalInformation.Field.TITLE, "A probabilistic approach to feature selection - A filter solution");
/*  48:    */     
/*  49:195 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "13th International Conference on Machine Learning");
/*  50:    */     
/*  51:197 */     result.setValue(TechnicalInformation.Field.YEAR, "1996");
/*  52:198 */     result.setValue(TechnicalInformation.Field.PAGES, "319-327");
/*  53:    */     
/*  54:200 */     return result;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public RandomSearch()
/*  58:    */   {
/*  59:207 */     resetOptions();
/*  60:    */   }
/*  61:    */   
/*  62:    */   public Enumeration<Option> listOptions()
/*  63:    */   {
/*  64:217 */     Vector<Option> newVector = new Vector(4);
/*  65:    */     
/*  66:219 */     newVector.addElement(new Option("\tSpecify a starting set of attributes.\n\tEg. 1,3,5-7.\n\tIf a start point is supplied,\n\trandom search evaluates the start\n\tpoint and then randomly looks for\n\tsubsets that are as good as or better\n\tthan the start point with the same\n\tor lower cardinality.", "P", 1, "-P <start set>"));
/*  67:    */     
/*  68:    */ 
/*  69:    */ 
/*  70:    */ 
/*  71:    */ 
/*  72:    */ 
/*  73:    */ 
/*  74:227 */     newVector.addElement(new Option("\tPercent of search space to consider.\n\t(default = 25%).", "F", 1, "-F <percent> "));
/*  75:    */     
/*  76:229 */     newVector.addElement(new Option("\tOutput subsets as the search progresses.\n\t(default = false).", "V", 0, "-V"));
/*  77:    */     
/*  78:    */ 
/*  79:    */ 
/*  80:233 */     newVector.addElement(new Option("\tRandom seed\n\t(default = 1)", "seed", 1, "-seed <num>"));
/*  81:    */     
/*  82:235 */     return newVector.elements();
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setOptions(String[] options)
/*  86:    */     throws Exception
/*  87:    */   {
/*  88:278 */     resetOptions();
/*  89:    */     
/*  90:280 */     String optionString = Utils.getOption('P', options);
/*  91:281 */     if (optionString.length() != 0) {
/*  92:282 */       setStartSet(optionString);
/*  93:    */     }
/*  94:285 */     optionString = Utils.getOption('F', options);
/*  95:286 */     if (optionString.length() != 0) {
/*  96:287 */       setSearchPercent(new Double(optionString).doubleValue());
/*  97:    */     }
/*  98:290 */     optionString = Utils.getOption("seed", options);
/*  99:291 */     if (optionString.length() > 0) {
/* 100:292 */       setSeed(Integer.parseInt(optionString));
/* 101:    */     }
/* 102:295 */     setVerbose(Utils.getFlag('V', options));
/* 103:    */     
/* 104:297 */     Utils.checkForRemainingOptions(options);
/* 105:    */   }
/* 106:    */   
/* 107:    */   public String[] getOptions()
/* 108:    */   {
/* 109:308 */     Vector<String> options = new Vector();
/* 110:310 */     if (this.m_verbose) {
/* 111:311 */       options.add("-V");
/* 112:    */     }
/* 113:314 */     if (!getStartSet().equals(""))
/* 114:    */     {
/* 115:315 */       options.add("-P");
/* 116:316 */       options.add("" + startSetToString());
/* 117:    */     }
/* 118:319 */     options.add("-F");
/* 119:320 */     options.add("" + getSearchPercent());
/* 120:    */     
/* 121:322 */     options.add("-seed");
/* 122:323 */     options.add("" + getSeed());
/* 123:    */     
/* 124:325 */     return (String[])options.toArray(new String[0]);
/* 125:    */   }
/* 126:    */   
/* 127:    */   public String startSetTipText()
/* 128:    */   {
/* 129:335 */     return "Set the start point for the search. This is specified as a comma seperated list off attribute indexes starting at 1. It can include ranges. Eg. 1,2,5-9,17. If specified, Random searches for subsets of attributes that are as good as or better than the start set with the same or lower cardinality.";
/* 130:    */   }
/* 131:    */   
/* 132:    */   public void setStartSet(String startSet)
/* 133:    */     throws Exception
/* 134:    */   {
/* 135:355 */     this.m_startRange.setRanges(startSet);
/* 136:    */   }
/* 137:    */   
/* 138:    */   public String getStartSet()
/* 139:    */   {
/* 140:365 */     return this.m_startRange.getRanges();
/* 141:    */   }
/* 142:    */   
/* 143:    */   public String verboseTipText()
/* 144:    */   {
/* 145:375 */     return "Print progress information. Sends progress info to the terminal as the search progresses.";
/* 146:    */   }
/* 147:    */   
/* 148:    */   public void setVerbose(boolean v)
/* 149:    */   {
/* 150:385 */     this.m_verbose = v;
/* 151:    */   }
/* 152:    */   
/* 153:    */   public boolean getVerbose()
/* 154:    */   {
/* 155:394 */     return this.m_verbose;
/* 156:    */   }
/* 157:    */   
/* 158:    */   public String searchPercentTipText()
/* 159:    */   {
/* 160:404 */     return "Percentage of the search space to explore.";
/* 161:    */   }
/* 162:    */   
/* 163:    */   public void setSearchPercent(double p)
/* 164:    */   {
/* 165:413 */     p = Math.abs(p);
/* 166:414 */     if (p == 0.0D) {
/* 167:415 */       p = 25.0D;
/* 168:    */     }
/* 169:418 */     if (p > 100.0D) {
/* 170:419 */       p = 100.0D;
/* 171:    */     }
/* 172:422 */     this.m_searchSize = (p / 100.0D);
/* 173:    */   }
/* 174:    */   
/* 175:    */   public double getSearchPercent()
/* 176:    */   {
/* 177:431 */     return this.m_searchSize * 100.0D;
/* 178:    */   }
/* 179:    */   
/* 180:    */   public String seedTipText()
/* 181:    */   {
/* 182:441 */     return "Seed for the random number generator";
/* 183:    */   }
/* 184:    */   
/* 185:    */   public void setSeed(int seed)
/* 186:    */   {
/* 187:445 */     this.m_seed = seed;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public int getSeed()
/* 191:    */   {
/* 192:449 */     return this.m_seed;
/* 193:    */   }
/* 194:    */   
/* 195:    */   private String startSetToString()
/* 196:    */   {
/* 197:462 */     StringBuffer FString = new StringBuffer();
/* 198:465 */     if (this.m_starting == null) {
/* 199:466 */       return getStartSet();
/* 200:    */     }
/* 201:469 */     for (int i = 0; i < this.m_starting.length; i++)
/* 202:    */     {
/* 203:470 */       boolean didPrint = false;
/* 204:472 */       if ((!this.m_hasClass) || ((this.m_hasClass == true) && (i != this.m_classIndex)))
/* 205:    */       {
/* 206:473 */         FString.append(this.m_starting[i] + 1);
/* 207:474 */         didPrint = true;
/* 208:    */       }
/* 209:477 */       if (i == this.m_starting.length - 1) {
/* 210:478 */         FString.append("");
/* 211:480 */       } else if (didPrint) {
/* 212:481 */         FString.append(",");
/* 213:    */       }
/* 214:    */     }
/* 215:486 */     return FString.toString();
/* 216:    */   }
/* 217:    */   
/* 218:    */   public String toString()
/* 219:    */   {
/* 220:496 */     StringBuffer text = new StringBuffer();
/* 221:    */     
/* 222:498 */     text.append("\tRandom search.\n\tStart set: ");
/* 223:499 */     if (this.m_starting == null) {
/* 224:500 */       text.append("no attributes\n");
/* 225:    */     } else {
/* 226:502 */       text.append(startSetToString() + "\n");
/* 227:    */     }
/* 228:504 */     text.append("\tNumber of iterations: " + this.m_iterations + " (" + this.m_searchSize * 100.0D + "% of the search space)\n");
/* 229:    */     
/* 230:506 */     text.append("\tMerit of best subset found: " + Utils.doubleToString(Math.abs(this.m_bestMerit), 8, 3) + "\n");
/* 231:    */     
/* 232:    */ 
/* 233:509 */     return text.toString();
/* 234:    */   }
/* 235:    */   
/* 236:    */   public int[] search(ASEvaluation ASEval, Instances data)
/* 237:    */     throws Exception
/* 238:    */   {
/* 239:523 */     int sizeOfBest = this.m_numAttribs;
/* 240:    */     
/* 241:525 */     this.m_bestGroup = new BitSet(this.m_numAttribs);
/* 242:    */     
/* 243:527 */     this.m_onlyConsiderBetterAndSmaller = false;
/* 244:528 */     if (!(ASEval instanceof SubsetEvaluator)) {
/* 245:529 */       throw new Exception(ASEval.getClass().getName() + " is not a " + "Subset evaluator!");
/* 246:    */     }
/* 247:533 */     this.m_random = new Random(this.m_seed);
/* 248:535 */     if ((ASEval instanceof UnsupervisedSubsetEvaluator))
/* 249:    */     {
/* 250:536 */       this.m_hasClass = false;
/* 251:    */     }
/* 252:    */     else
/* 253:    */     {
/* 254:538 */       this.m_hasClass = true;
/* 255:539 */       this.m_classIndex = data.classIndex();
/* 256:    */     }
/* 257:542 */     SubsetEvaluator ASEvaluator = (SubsetEvaluator)ASEval;
/* 258:543 */     this.m_numAttribs = data.numAttributes();
/* 259:    */     
/* 260:545 */     this.m_startRange.setUpper(this.m_numAttribs - 1);
/* 261:546 */     if (!getStartSet().equals("")) {
/* 262:547 */       this.m_starting = this.m_startRange.getSelection();
/* 263:    */     }
/* 264:    */     double best_merit;
/* 265:551 */     if (this.m_starting != null)
/* 266:    */     {
/* 267:552 */       for (int element : this.m_starting) {
/* 268:553 */         if (element != this.m_classIndex) {
/* 269:554 */           this.m_bestGroup.set(element);
/* 270:    */         }
/* 271:    */       }
/* 272:557 */       this.m_onlyConsiderBetterAndSmaller = true;
/* 273:558 */       double best_merit = ASEvaluator.evaluateSubset(this.m_bestGroup);
/* 274:559 */       sizeOfBest = countFeatures(this.m_bestGroup);
/* 275:    */     }
/* 276:    */     else
/* 277:    */     {
/* 278:562 */       this.m_bestGroup = generateRandomSubset();
/* 279:563 */       best_merit = ASEvaluator.evaluateSubset(this.m_bestGroup);
/* 280:    */     }
/* 281:566 */     if (this.m_verbose) {
/* 282:567 */       System.out.println("Initial subset (" + Utils.doubleToString(Math.abs(best_merit), 8, 5) + "): " + printSubset(this.m_bestGroup));
/* 283:    */     }
/* 284:    */     int i;
/* 285:573 */     if (this.m_hasClass) {
/* 286:574 */       i = this.m_numAttribs - 1;
/* 287:    */     } else {
/* 288:576 */       i = this.m_numAttribs;
/* 289:    */     }
/* 290:578 */     this.m_iterations = ((int)(this.m_searchSize * Math.pow(2.0D, i)));
/* 291:583 */     for (int i = 0; i < this.m_iterations; i++)
/* 292:    */     {
/* 293:584 */       BitSet temp = generateRandomSubset();
/* 294:585 */       if (this.m_onlyConsiderBetterAndSmaller)
/* 295:    */       {
/* 296:586 */         int tempSize = countFeatures(temp);
/* 297:587 */         if (tempSize <= sizeOfBest)
/* 298:    */         {
/* 299:588 */           double tempMerit = ASEvaluator.evaluateSubset(temp);
/* 300:589 */           if (tempMerit >= best_merit)
/* 301:    */           {
/* 302:590 */             sizeOfBest = tempSize;
/* 303:591 */             this.m_bestGroup = temp;
/* 304:592 */             best_merit = tempMerit;
/* 305:593 */             if (this.m_verbose)
/* 306:    */             {
/* 307:594 */               System.out.print("New best subset (" + Utils.doubleToString(Math.abs(best_merit), 8, 5) + "): " + printSubset(this.m_bestGroup) + " :");
/* 308:    */               
/* 309:    */ 
/* 310:597 */               System.out.println(Utils.doubleToString(i / this.m_iterations * 100.0D, 5, 1) + "% done");
/* 311:    */             }
/* 312:    */           }
/* 313:    */         }
/* 314:    */       }
/* 315:    */       else
/* 316:    */       {
/* 317:604 */         double tempMerit = ASEvaluator.evaluateSubset(temp);
/* 318:605 */         if (tempMerit > best_merit)
/* 319:    */         {
/* 320:606 */           this.m_bestGroup = temp;
/* 321:607 */           best_merit = tempMerit;
/* 322:608 */           if (this.m_verbose)
/* 323:    */           {
/* 324:609 */             System.out.print("New best subset (" + Utils.doubleToString(Math.abs(best_merit), 8, 5) + "): " + printSubset(this.m_bestGroup) + " :");
/* 325:    */             
/* 326:    */ 
/* 327:612 */             System.out.println(Utils.doubleToString(i / this.m_iterations * 100.0D, 5, 1) + "% done");
/* 328:    */           }
/* 329:    */         }
/* 330:    */       }
/* 331:    */     }
/* 332:619 */     this.m_bestMerit = best_merit;
/* 333:620 */     return attributeList(this.m_bestGroup);
/* 334:    */   }
/* 335:    */   
/* 336:    */   private String printSubset(BitSet temp)
/* 337:    */   {
/* 338:630 */     StringBuffer text = new StringBuffer();
/* 339:632 */     for (int j = 0; j < this.m_numAttribs; j++) {
/* 340:633 */       if (temp.get(j)) {
/* 341:634 */         text.append(j + 1 + " ");
/* 342:    */       }
/* 343:    */     }
/* 344:637 */     return text.toString();
/* 345:    */   }
/* 346:    */   
/* 347:    */   private int[] attributeList(BitSet group)
/* 348:    */   {
/* 349:647 */     int count = 0;
/* 350:650 */     for (int i = 0; i < this.m_numAttribs; i++) {
/* 351:651 */       if (group.get(i)) {
/* 352:652 */         count++;
/* 353:    */       }
/* 354:    */     }
/* 355:656 */     int[] list = new int[count];
/* 356:657 */     count = 0;
/* 357:659 */     for (int i = 0; i < this.m_numAttribs; i++) {
/* 358:660 */       if (group.get(i)) {
/* 359:661 */         list[(count++)] = i;
/* 360:    */       }
/* 361:    */     }
/* 362:665 */     return list;
/* 363:    */   }
/* 364:    */   
/* 365:    */   private BitSet generateRandomSubset()
/* 366:    */   {
/* 367:674 */     BitSet temp = new BitSet(this.m_numAttribs);
/* 368:677 */     for (int i = 0; i < this.m_numAttribs; i++)
/* 369:    */     {
/* 370:678 */       double r = this.m_random.nextDouble();
/* 371:679 */       if ((r <= 0.5D) && (
/* 372:680 */         (!this.m_hasClass) || (i != this.m_classIndex))) {
/* 373:682 */         temp.set(i);
/* 374:    */       }
/* 375:    */     }
/* 376:686 */     return temp;
/* 377:    */   }
/* 378:    */   
/* 379:    */   private int countFeatures(BitSet featureSet)
/* 380:    */   {
/* 381:696 */     int count = 0;
/* 382:697 */     for (int i = 0; i < this.m_numAttribs; i++) {
/* 383:698 */       if (featureSet.get(i)) {
/* 384:699 */         count++;
/* 385:    */       }
/* 386:    */     }
/* 387:702 */     return count;
/* 388:    */   }
/* 389:    */   
/* 390:    */   private void resetOptions()
/* 391:    */   {
/* 392:709 */     this.m_starting = null;
/* 393:710 */     this.m_startRange = new Range();
/* 394:711 */     this.m_searchSize = 0.25D;
/* 395:712 */     this.m_seed = 1;
/* 396:713 */     this.m_onlyConsiderBetterAndSmaller = false;
/* 397:714 */     this.m_verbose = false;
/* 398:    */   }
/* 399:    */   
/* 400:    */   public String getRevision()
/* 401:    */   {
/* 402:724 */     return RevisionUtils.extract("$Revision: 10325 $");
/* 403:    */   }
/* 404:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.RandomSearch
 * JD-Core Version:    0.7.0.1
 */