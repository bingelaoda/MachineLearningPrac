/*   1:    */ package weka.attributeSelection;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.BitSet;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.OptionHandler;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.TechnicalInformation;
/*  14:    */ import weka.core.TechnicalInformation.Field;
/*  15:    */ import weka.core.TechnicalInformation.Type;
/*  16:    */ import weka.core.TechnicalInformationHandler;
/*  17:    */ import weka.core.Utils;
/*  18:    */ 
/*  19:    */ public class RankSearch
/*  20:    */   extends ASSearch
/*  21:    */   implements OptionHandler, TechnicalInformationHandler
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = -7992268736874353755L;
/*  24:    */   private int m_numAttribs;
/*  25:    */   private ASEvaluation m_ASEval;
/*  26:    */   private ASEvaluation m_SubsetEval;
/*  27:    */   private Instances m_Instances;
/*  28:    */   private double m_bestMerit;
/*  29:    */   private int[] m_Ranking;
/*  30:131 */   protected double m_improvementThreshold = 0.0D;
/*  31:137 */   protected boolean m_excludeNonImproving = false;
/*  32:140 */   protected int m_add = 1;
/*  33:143 */   protected int m_startPoint = 0;
/*  34:    */   protected boolean m_debug;
/*  35:152 */   protected int m_nonImprovingAdditions = 0;
/*  36:    */   
/*  37:    */   public String globalInfo()
/*  38:    */   {
/*  39:161 */     return "RankSearch : \n\nUses an attribute/subset evaluator to rank all attributes. If a subset evaluator is specified, then a forward selection search is used to generate a ranked list. From the ranked list of attributes, subsets of increasing size are evaluated, ie. The best attribute, the best attribute plus the next best attribute, etc.... The best attribute set is reported. RankSearch is linear in the number of attributes if a simple attribute evaluator is used such as GainRatioAttributeEval. For more information see:\n\n" + getTechnicalInformation().toString();
/*  40:    */   }
/*  41:    */   
/*  42:    */   public TechnicalInformation getTechnicalInformation()
/*  43:    */   {
/*  44:184 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  45:185 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Mark Hall and Geoffrey Holmes");
/*  46:186 */     result.setValue(TechnicalInformation.Field.YEAR, "2003");
/*  47:187 */     result.setValue(TechnicalInformation.Field.TITLE, "Benchmarking attribute selection techniques for discrete class data mining");
/*  48:    */     
/*  49:    */ 
/*  50:190 */     result.setValue(TechnicalInformation.Field.JOURNAL, "IEEE Transactions on Knowledge and Data Engineering");
/*  51:    */     
/*  52:192 */     result.setValue(TechnicalInformation.Field.VOLUME, "15");
/*  53:193 */     result.setValue(TechnicalInformation.Field.NUMBER, "6");
/*  54:194 */     result.setValue(TechnicalInformation.Field.PAGES, "1437-1447");
/*  55:195 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "IEEE Computer Society");
/*  56:    */     
/*  57:197 */     return result;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public RankSearch()
/*  61:    */   {
/*  62:204 */     resetOptions();
/*  63:    */   }
/*  64:    */   
/*  65:    */   public String attributeEvaluatorTipText()
/*  66:    */   {
/*  67:214 */     return "Attribute evaluator to use for generating a ranking.";
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void setAttributeEvaluator(ASEvaluation newEvaluator)
/*  71:    */   {
/*  72:223 */     this.m_ASEval = newEvaluator;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public ASEvaluation getAttributeEvaluator()
/*  76:    */   {
/*  77:232 */     return this.m_ASEval;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public String stepSizeTipText()
/*  81:    */   {
/*  82:242 */     return "Add this many attributes from the ranking in each iteration.";
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setStepSize(int ss)
/*  86:    */   {
/*  87:251 */     if (ss > 0) {
/*  88:252 */       this.m_add = ss;
/*  89:    */     }
/*  90:    */   }
/*  91:    */   
/*  92:    */   public int getStepSize()
/*  93:    */   {
/*  94:262 */     return this.m_add;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public String startPointTipText()
/*  98:    */   {
/*  99:272 */     return "Start evaluating from this point in the ranking.";
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void setStartPoint(int sp)
/* 103:    */   {
/* 104:281 */     if (sp >= 0) {
/* 105:282 */       this.m_startPoint = sp;
/* 106:    */     }
/* 107:    */   }
/* 108:    */   
/* 109:    */   public int getStartPoint()
/* 110:    */   {
/* 111:292 */     return this.m_startPoint;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public String debuggingOutputTipText()
/* 115:    */   {
/* 116:302 */     return "Output debugging information to the console";
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void setDebuggingOutput(boolean d)
/* 120:    */   {
/* 121:311 */     this.m_debug = d;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public boolean getDebuggingOutput()
/* 125:    */   {
/* 126:320 */     return this.m_debug;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public String improvementThresholdTipText()
/* 130:    */   {
/* 131:330 */     return "Threshold on improvement in merit by which to accept additional attributes from the ranked list";
/* 132:    */   }
/* 133:    */   
/* 134:    */   public void setImprovementThreshold(double t)
/* 135:    */   {
/* 136:340 */     this.m_improvementThreshold = t;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public double getImprovementThreshold()
/* 140:    */   {
/* 141:349 */     return this.m_improvementThreshold;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public String excludeNonImprovingAttributesTipText()
/* 145:    */   {
/* 146:359 */     return "As more attributes are considered from the ranked list, don't include any prior ones that did not improve merit";
/* 147:    */   }
/* 148:    */   
/* 149:    */   public void setExcludeNonImprovingAttributes(boolean b)
/* 150:    */   {
/* 151:370 */     this.m_excludeNonImproving = b;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public boolean getExcludeNonImprovingAttributes()
/* 155:    */   {
/* 156:380 */     return this.m_excludeNonImproving;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public String nonImprovingAdditionsTipText()
/* 160:    */   {
/* 161:390 */     return "Terminate the evaluation of the ranking after this many non-improving additions to the best subset seen (0 = don't terminate early)";
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void setNonImprovingAdditions(int t)
/* 165:    */   {
/* 166:402 */     this.m_nonImprovingAdditions = t;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public int getNonImprovingAdditions()
/* 170:    */   {
/* 171:412 */     return this.m_nonImprovingAdditions;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public Enumeration<Option> listOptions()
/* 175:    */   {
/* 176:422 */     Vector<Option> newVector = new Vector(7);
/* 177:    */     
/* 178:424 */     newVector.addElement(new Option("\tclass name of attribute evaluator to use for ranking. Place any\n\tevaluator options LAST on the command line following a \"--\".\n\teg.:\n\t\t-A weka.attributeSelection.GainRatioAttributeEval ... -- -M\n\t(default: weka.attributeSelection.GainRatioAttributeEval)", "A", 1, "-A <attribute evaluator>"));
/* 179:    */     
/* 180:    */ 
/* 181:    */ 
/* 182:    */ 
/* 183:    */ 
/* 184:    */ 
/* 185:    */ 
/* 186:432 */     newVector.addElement(new Option("\tnumber of attributes to be added from the\n\tranking in each iteration (default = 1).", "S", 1, "-S <step size>"));
/* 187:    */     
/* 188:    */ 
/* 189:    */ 
/* 190:    */ 
/* 191:437 */     newVector.addElement(new Option("\tpoint in the ranking to start evaluating from. \n\t(default = 0, ie. the head of the ranking).", "R", 1, "-R <start point>"));
/* 192:    */     
/* 193:    */ 
/* 194:    */ 
/* 195:    */ 
/* 196:442 */     newVector.addElement(new Option("\tThreshold on improvement in merit by which to accept\n\tadditional attributes from the ranked list \n\t(default = 0).", "I", 1, "-I <threshold>"));
/* 197:    */     
/* 198:    */ 
/* 199:    */ 
/* 200:    */ 
/* 201:447 */     newVector.addElement(new Option("\tNumber of non-improving additions to the best subset seen\n\tto tolerate before terminating the search (default = 0, i.e.\n\tdon't terminate early).", "N", 1, "-N <number of non-improving additions>"));
/* 202:    */     
/* 203:    */ 
/* 204:    */ 
/* 205:    */ 
/* 206:    */ 
/* 207:453 */     newVector.addElement(new Option("\tExclude non improving attributes when\n\tconsidering more attributes from the ranked list", "X", 0, "-X"));
/* 208:    */     
/* 209:    */ 
/* 210:    */ 
/* 211:457 */     newVector.addElement(new Option("\tPrint debugging output", "D", 0, "-D"));
/* 212:459 */     if ((this.m_ASEval != null) && ((this.m_ASEval instanceof OptionHandler)))
/* 213:    */     {
/* 214:460 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to evaluator " + this.m_ASEval.getClass().getName() + ":"));
/* 215:    */       
/* 216:    */ 
/* 217:463 */       newVector.addAll(Collections.list(((OptionHandler)this.m_ASEval).listOptions()));
/* 218:    */     }
/* 219:467 */     return newVector.elements();
/* 220:    */   }
/* 221:    */   
/* 222:    */   public void setOptions(String[] options)
/* 223:    */     throws Exception
/* 224:    */   {
/* 225:515 */     resetOptions();
/* 226:    */     
/* 227:517 */     String optionString = Utils.getOption('S', options);
/* 228:518 */     if (optionString.length() != 0) {
/* 229:519 */       setStepSize(Integer.parseInt(optionString));
/* 230:    */     }
/* 231:522 */     optionString = Utils.getOption('R', options);
/* 232:523 */     if (optionString.length() != 0) {
/* 233:524 */       setStartPoint(Integer.parseInt(optionString));
/* 234:    */     }
/* 235:527 */     optionString = Utils.getOption('I', options);
/* 236:528 */     if (optionString.length() > 0) {
/* 237:529 */       setImprovementThreshold(Double.parseDouble(optionString));
/* 238:    */     }
/* 239:532 */     optionString = Utils.getOption('N', options);
/* 240:533 */     if (optionString.length() > 0) {
/* 241:534 */       setNonImprovingAdditions(Integer.parseInt(optionString));
/* 242:    */     }
/* 243:537 */     optionString = Utils.getOption('A', options);
/* 244:538 */     if (optionString.length() == 0) {
/* 245:539 */       optionString = GainRatioAttributeEval.class.getName();
/* 246:    */     }
/* 247:541 */     setAttributeEvaluator(ASEvaluation.forName(optionString, Utils.partitionOptions(options)));
/* 248:    */     
/* 249:    */ 
/* 250:544 */     setExcludeNonImprovingAttributes(Utils.getFlag('X', options));
/* 251:    */     
/* 252:546 */     setDebuggingOutput(Utils.getFlag('D', options));
/* 253:    */     
/* 254:548 */     Utils.checkForRemainingOptions(options);
/* 255:    */   }
/* 256:    */   
/* 257:    */   public String[] getOptions()
/* 258:    */   {
/* 259:559 */     Vector<String> options = new Vector();
/* 260:    */     
/* 261:561 */     options.add("-S");
/* 262:562 */     options.add("" + getStepSize());
/* 263:    */     
/* 264:564 */     options.add("-R");
/* 265:565 */     options.add("" + getStartPoint());
/* 266:    */     
/* 267:567 */     options.add("-N");
/* 268:568 */     options.add("" + getNonImprovingAdditions());
/* 269:    */     
/* 270:570 */     options.add("-I");
/* 271:571 */     options.add("" + getImprovementThreshold());
/* 272:573 */     if (getExcludeNonImprovingAttributes()) {
/* 273:574 */       options.add("-X");
/* 274:    */     }
/* 275:577 */     if (getDebuggingOutput()) {
/* 276:578 */       options.add("-D");
/* 277:    */     }
/* 278:581 */     if (getAttributeEvaluator() != null)
/* 279:    */     {
/* 280:582 */       options.add("-A");
/* 281:583 */       options.add(getAttributeEvaluator().getClass().getName());
/* 282:    */     }
/* 283:586 */     if ((this.m_ASEval != null) && ((this.m_ASEval instanceof OptionHandler)))
/* 284:    */     {
/* 285:587 */       String[] evaluatorOptions = ((OptionHandler)this.m_ASEval).getOptions();
/* 286:589 */       if (evaluatorOptions.length > 0)
/* 287:    */       {
/* 288:590 */         options.add("--");
/* 289:591 */         Collections.addAll(options, evaluatorOptions);
/* 290:    */       }
/* 291:    */     }
/* 292:595 */     return (String[])options.toArray(new String[0]);
/* 293:    */   }
/* 294:    */   
/* 295:    */   protected void resetOptions()
/* 296:    */   {
/* 297:602 */     this.m_ASEval = new GainRatioAttributeEval();
/* 298:603 */     this.m_Ranking = null;
/* 299:    */   }
/* 300:    */   
/* 301:    */   public int[] search(ASEvaluation ASEval, Instances data)
/* 302:    */     throws Exception
/* 303:    */   {
/* 304:618 */     double best_merit = -1.797693134862316E+308D;
/* 305:    */     
/* 306:620 */     BitSet best_group = null;
/* 307:622 */     if (!(ASEval instanceof SubsetEvaluator)) {
/* 308:623 */       throw new Exception(ASEval.getClass().getName() + " is not a " + "Subset evaluator!");
/* 309:    */     }
/* 310:627 */     this.m_SubsetEval = ASEval;
/* 311:628 */     this.m_Instances = data;
/* 312:629 */     this.m_numAttribs = this.m_Instances.numAttributes();
/* 313:648 */     if (this.m_debug) {
/* 314:649 */       System.err.println("Ranking...");
/* 315:    */     }
/* 316:651 */     if ((this.m_ASEval instanceof AttributeEvaluator))
/* 317:    */     {
/* 318:653 */       Ranker ranker = new Ranker();
/* 319:654 */       this.m_ASEval.buildEvaluator(this.m_Instances);
/* 320:655 */       if ((this.m_ASEval instanceof AttributeTransformer))
/* 321:    */       {
/* 322:657 */         this.m_Instances = ((AttributeTransformer)this.m_ASEval).transformedData(this.m_Instances);
/* 323:    */         
/* 324:659 */         this.m_SubsetEval.buildEvaluator(this.m_Instances);
/* 325:    */       }
/* 326:661 */       this.m_Ranking = ranker.search(this.m_ASEval, this.m_Instances);
/* 327:    */     }
/* 328:    */     else
/* 329:    */     {
/* 330:663 */       GreedyStepwise fs = new GreedyStepwise();
/* 331:    */       
/* 332:665 */       fs.setGenerateRanking(true);
/* 333:666 */       this.m_ASEval.buildEvaluator(this.m_Instances);
/* 334:667 */       fs.search(this.m_ASEval, this.m_Instances);
/* 335:668 */       double[][] rankres = fs.rankedAttributes();
/* 336:669 */       this.m_Ranking = new int[rankres.length];
/* 337:670 */       for (int i = 0; i < rankres.length; i++) {
/* 338:671 */         this.m_Ranking[i] = ((int)rankres[i][0]);
/* 339:    */       }
/* 340:    */     }
/* 341:675 */     boolean[] dontAdd = null;
/* 342:676 */     if (this.m_excludeNonImproving) {
/* 343:677 */       dontAdd = new boolean[this.m_Ranking.length];
/* 344:    */     }
/* 345:680 */     if (this.m_debug) {
/* 346:681 */       System.err.println("Done ranking. Evaluating ranking...");
/* 347:    */     }
/* 348:684 */     int additions = 0;
/* 349:    */     
/* 350:686 */     int tenPercent = (this.m_Ranking.length - this.m_startPoint) / 10;
/* 351:687 */     int count = 0;
/* 352:688 */     for (int i = this.m_startPoint; i < this.m_Ranking.length; goto 852)
/* 353:    */     {
/* 354:    */       label328:
/* 355:689 */       i += this.m_add;
/* 356:690 */       if (i > this.m_Ranking.length) {
/* 357:691 */         i = this.m_Ranking.length;
/* 358:    */       }
/* 359:693 */       BitSet temp_group = new BitSet(this.m_numAttribs);
/* 360:694 */       for (int j = 0; j < i; j++) {
/* 361:695 */         if (this.m_excludeNonImproving)
/* 362:    */         {
/* 363:696 */           if (dontAdd[j] == 0) {
/* 364:697 */             temp_group.set(this.m_Ranking[j]);
/* 365:    */           }
/* 366:    */         }
/* 367:    */         else {
/* 368:700 */           temp_group.set(this.m_Ranking[j]);
/* 369:    */         }
/* 370:    */       }
/* 371:704 */       additions++;
/* 372:705 */       count += this.m_add;
/* 373:706 */       double temp_merit = ((SubsetEvaluator)this.m_SubsetEval).evaluateSubset(temp_group);
/* 374:708 */       if ((this.m_debug) && (tenPercent > 0) && (count >= tenPercent))
/* 375:    */       {
/* 376:709 */         System.err.println("" + (i - this.m_startPoint) / (this.m_Ranking.length - this.m_startPoint) * 100.0D + " percent complete");
/* 377:    */         
/* 378:    */ 
/* 379:    */ 
/* 380:713 */         count = 0;
/* 381:    */       }
/* 382:716 */       if (temp_merit - best_merit > this.m_improvementThreshold)
/* 383:    */       {
/* 384:717 */         best_merit = temp_merit;
/* 385:    */         
/* 386:719 */         best_group = temp_group;
/* 387:720 */         additions = 0;
/* 388:722 */         if (this.m_debug)
/* 389:    */         {
/* 390:723 */           int[] atts = attributeList(best_group);
/* 391:724 */           System.err.print("Best subset found so far (" + atts.length + " features): ");
/* 392:726 */           for (int a : atts) {
/* 393:727 */             System.err.print("" + (a + 1) + " ");
/* 394:    */           }
/* 395:729 */           System.err.println("\nMerit: " + best_merit);
/* 396:    */         }
/* 397:    */       }
/* 398:731 */       else if ((this.m_excludeNonImproving) && (i > 0))
/* 399:    */       {
/* 400:732 */         if (this.m_debug) {
/* 401:733 */           System.err.println("Skipping atts ranked " + (i - this.m_add) + " to " + i + " as there is no improvement");
/* 402:    */         }
/* 403:736 */         for (int j = i - this.m_add; j < i; j++) {
/* 404:737 */           dontAdd[j] = true;
/* 405:    */         }
/* 406:    */       }
/* 407:741 */       if ((this.m_nonImprovingAdditions <= 0) || (additions <= this.m_nonImprovingAdditions)) {
/* 408:    */         break label328;
/* 409:    */       }
/* 410:742 */       if (this.m_debug) {
/* 411:743 */         System.err.println("Terminating the search after " + this.m_nonImprovingAdditions + " non-improving additions");
/* 412:    */       }
/* 413:    */     }
/* 414:750 */     this.m_bestMerit = best_merit;
/* 415:751 */     return attributeList(best_group);
/* 416:    */   }
/* 417:    */   
/* 418:    */   private int[] attributeList(BitSet group)
/* 419:    */   {
/* 420:761 */     int count = 0;
/* 421:764 */     for (int i = 0; i < this.m_numAttribs; i++) {
/* 422:765 */       if (group.get(i)) {
/* 423:766 */         count++;
/* 424:    */       }
/* 425:    */     }
/* 426:770 */     int[] list = new int[count];
/* 427:771 */     count = 0;
/* 428:773 */     for (int i = 0; i < this.m_numAttribs; i++) {
/* 429:774 */       if (group.get(i)) {
/* 430:775 */         list[(count++)] = i;
/* 431:    */       }
/* 432:    */     }
/* 433:779 */     return list;
/* 434:    */   }
/* 435:    */   
/* 436:    */   public String toString()
/* 437:    */   {
/* 438:789 */     StringBuffer text = new StringBuffer();
/* 439:790 */     text.append("\tRankSearch :\n");
/* 440:791 */     text.append("\tAttribute evaluator : " + getAttributeEvaluator().getClass().getName() + " ");
/* 441:793 */     if ((this.m_ASEval instanceof OptionHandler))
/* 442:    */     {
/* 443:794 */       String[] evaluatorOptions = new String[0];
/* 444:795 */       evaluatorOptions = ((OptionHandler)this.m_ASEval).getOptions();
/* 445:796 */       for (String evaluatorOption : evaluatorOptions) {
/* 446:797 */         text.append(evaluatorOption + ' ');
/* 447:    */       }
/* 448:    */     }
/* 449:800 */     text.append("\n");
/* 450:801 */     text.append("\tAttribute ranking : \n");
/* 451:802 */     int rlength = (int)(Math.log(this.m_Ranking.length) / Math.log(10.0D) + 1.0D);
/* 452:803 */     for (int element : this.m_Ranking) {
/* 453:804 */       text.append("\t " + Utils.doubleToString(element + 1, rlength, 0) + " " + this.m_Instances.attribute(element).name() + '\n');
/* 454:    */     }
/* 455:807 */     text.append("\tMerit of best subset found : ");
/* 456:808 */     int fieldwidth = 3;
/* 457:809 */     double precision = this.m_bestMerit - (int)this.m_bestMerit;
/* 458:810 */     if (Math.abs(this.m_bestMerit) > 0.0D) {
/* 459:811 */       fieldwidth = (int)Math.abs(Math.log(Math.abs(this.m_bestMerit)) / Math.log(10.0D)) + 2;
/* 460:    */     }
/* 461:814 */     if (Math.abs(precision) > 0.0D) {
/* 462:815 */       precision = Math.abs(Math.log(Math.abs(precision)) / Math.log(10.0D)) + 3.0D;
/* 463:    */     } else {
/* 464:817 */       precision = 2.0D;
/* 465:    */     }
/* 466:820 */     text.append(Utils.doubleToString(Math.abs(this.m_bestMerit), fieldwidth + (int)precision, (int)precision) + "\n");
/* 467:    */     
/* 468:    */ 
/* 469:823 */     return text.toString();
/* 470:    */   }
/* 471:    */   
/* 472:    */   public String getRevision()
/* 473:    */   {
/* 474:833 */     return RevisionUtils.extract("$Revision: 10325 $");
/* 475:    */   }
/* 476:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.RankSearch
 * JD-Core Version:    0.7.0.1
 */