/*   1:    */ package weka.attributeSelection;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.BitSet;
/*   7:    */ import java.util.Enumeration;
/*   8:    */ import java.util.Hashtable;
/*   9:    */ import java.util.Vector;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.OptionHandler;
/*  13:    */ import weka.core.Range;
/*  14:    */ import weka.core.RevisionHandler;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SelectedTag;
/*  17:    */ import weka.core.Tag;
/*  18:    */ import weka.core.Utils;
/*  19:    */ 
/*  20:    */ public class BestFirst
/*  21:    */   extends ASSearch
/*  22:    */   implements OptionHandler, StartSetHandler
/*  23:    */ {
/*  24:    */   static final long serialVersionUID = 7841338689536821867L;
/*  25:    */   protected int m_maxStale;
/*  26:    */   protected int m_searchDirection;
/*  27:    */   protected static final int SELECTION_BACKWARD = 0;
/*  28:    */   protected static final int SELECTION_FORWARD = 1;
/*  29:    */   protected static final int SELECTION_BIDIRECTIONAL = 2;
/*  30:    */   
/*  31:    */   public class Link2
/*  32:    */     implements Serializable, RevisionHandler
/*  33:    */   {
/*  34:    */     static final long serialVersionUID = -8236598311516351420L;
/*  35:    */     Object[] m_data;
/*  36:    */     double m_merit;
/*  37:    */     
/*  38:    */     public Link2(Object[] data, double mer)
/*  39:    */     {
/*  40:114 */       this.m_data = data;
/*  41:115 */       this.m_merit = mer;
/*  42:    */     }
/*  43:    */     
/*  44:    */     public Object[] getData()
/*  45:    */     {
/*  46:120 */       return this.m_data;
/*  47:    */     }
/*  48:    */     
/*  49:    */     public String toString()
/*  50:    */     {
/*  51:125 */       return "Node: " + this.m_data.toString() + "  " + this.m_merit;
/*  52:    */     }
/*  53:    */     
/*  54:    */     public String getRevision()
/*  55:    */     {
/*  56:135 */       return RevisionUtils.extract("$Revision: 10396 $");
/*  57:    */     }
/*  58:    */   }
/*  59:    */   
/*  60:    */   public class LinkedList2
/*  61:    */     extends ArrayList<BestFirst.Link2>
/*  62:    */   {
/*  63:    */     static final long serialVersionUID = 3250538292330398929L;
/*  64:    */     int m_MaxSize;
/*  65:    */     
/*  66:    */     public LinkedList2(int sz)
/*  67:    */     {
/*  68:158 */       this.m_MaxSize = sz;
/*  69:    */     }
/*  70:    */     
/*  71:    */     public void removeLinkAt(int index)
/*  72:    */       throws Exception
/*  73:    */     {
/*  74:168 */       if ((index >= 0) && (index < size())) {
/*  75:169 */         remove(index);
/*  76:    */       } else {
/*  77:171 */         throw new Exception("index out of range (removeLinkAt)");
/*  78:    */       }
/*  79:    */     }
/*  80:    */     
/*  81:    */     public BestFirst.Link2 getLinkAt(int index)
/*  82:    */       throws Exception
/*  83:    */     {
/*  84:182 */       if (size() == 0) {
/*  85:183 */         throw new Exception("List is empty (getLinkAt)");
/*  86:    */       }
/*  87:185 */       if ((index >= 0) && (index < size())) {
/*  88:186 */         return (BestFirst.Link2)get(index);
/*  89:    */       }
/*  90:188 */       throw new Exception("index out of range (getLinkAt)");
/*  91:    */     }
/*  92:    */     
/*  93:    */     public void addToList(Object[] data, double mer)
/*  94:    */       throws Exception
/*  95:    */     {
/*  96:200 */       BestFirst.Link2 newL = new BestFirst.Link2(BestFirst.this, data, mer);
/*  97:202 */       if (size() == 0)
/*  98:    */       {
/*  99:203 */         add(newL);
/* 100:    */       }
/* 101:205 */       else if (mer > ((BestFirst.Link2)get(0)).m_merit)
/* 102:    */       {
/* 103:206 */         if (size() == this.m_MaxSize) {
/* 104:207 */           removeLinkAt(this.m_MaxSize - 1);
/* 105:    */         }
/* 106:211 */         add(0, newL);
/* 107:    */       }
/* 108:    */       else
/* 109:    */       {
/* 110:213 */         int i = 0;
/* 111:214 */         int size = size();
/* 112:215 */         boolean done = false;
/* 113:220 */         if ((size != this.m_MaxSize) || (mer > ((BestFirst.Link2)get(size() - 1)).m_merit)) {
/* 114:225 */           while ((!done) && (i < size)) {
/* 115:226 */             if (mer > ((BestFirst.Link2)get(i)).m_merit)
/* 116:    */             {
/* 117:227 */               if (size == this.m_MaxSize) {
/* 118:228 */                 removeLinkAt(this.m_MaxSize - 1);
/* 119:    */               }
/* 120:232 */               add(i, newL);
/* 121:233 */               done = true;
/* 122:    */             }
/* 123:235 */             else if (i == size - 1)
/* 124:    */             {
/* 125:236 */               add(newL);
/* 126:237 */               done = true;
/* 127:    */             }
/* 128:    */             else
/* 129:    */             {
/* 130:239 */               i++;
/* 131:    */             }
/* 132:    */           }
/* 133:    */         }
/* 134:    */       }
/* 135:    */     }
/* 136:    */     
/* 137:    */     public String getRevision()
/* 138:    */     {
/* 139:254 */       return RevisionUtils.extract("$Revision: 10396 $");
/* 140:    */     }
/* 141:    */   }
/* 142:    */   
/* 143:272 */   public static final Tag[] TAGS_SELECTION = { new Tag(0, "Backward"), new Tag(1, "Forward"), new Tag(2, "Bi-directional") };
/* 144:    */   protected int[] m_starting;
/* 145:    */   protected Range m_startRange;
/* 146:    */   protected boolean m_hasClass;
/* 147:    */   protected int m_classIndex;
/* 148:    */   protected int m_numAttribs;
/* 149:    */   protected int m_totalEvals;
/* 150:    */   protected boolean m_debug;
/* 151:    */   protected double m_bestMerit;
/* 152:    */   protected int m_cacheSize;
/* 153:    */   
/* 154:    */   public String globalInfo()
/* 155:    */   {
/* 156:311 */     return "BestFirst:\n\nSearches the space of attribute subsets by greedy hillclimbing augmented with a backtracking facility. Setting the number of consecutive non-improving nodes allowed controls the level of backtracking done. Best first may start with the empty set of attributes and search forward, or start with the full set of attributes and search backward, or start at any point and search in both directions (by considering all possible single attribute additions and deletions at a given point).\n";
/* 157:    */   }
/* 158:    */   
/* 159:    */   public BestFirst()
/* 160:    */   {
/* 161:326 */     resetOptions();
/* 162:    */   }
/* 163:    */   
/* 164:    */   public Enumeration<Option> listOptions()
/* 165:    */   {
/* 166:337 */     Vector<Option> newVector = new Vector(4);
/* 167:    */     
/* 168:339 */     newVector.addElement(new Option("\tSpecify a starting set of attributes.\n\tEg. 1,3,5-7.", "P", 1, "-P <start set>"));
/* 169:    */     
/* 170:341 */     newVector.addElement(new Option("\tDirection of search. (default = 1).", "D", 1, "-D <0 = backward | 1 = forward | 2 = bi-directional>"));
/* 171:    */     
/* 172:343 */     newVector.addElement(new Option("\tNumber of non-improving nodes to\n\tconsider before terminating search.", "N", 1, "-N <num>"));
/* 173:    */     
/* 174:345 */     newVector.addElement(new Option("\tSize of lookup cache for evaluated subsets.\n\tExpressed as a multiple of the number of\n\tattributes in the data set. (default = 1)", "S", 1, "-S <num>"));
/* 175:    */     
/* 176:    */ 
/* 177:    */ 
/* 178:    */ 
/* 179:350 */     return newVector.elements();
/* 180:    */   }
/* 181:    */   
/* 182:    */   public void setOptions(String[] options)
/* 183:    */     throws Exception
/* 184:    */   {
/* 185:394 */     resetOptions();
/* 186:    */     
/* 187:396 */     String optionString = Utils.getOption('P', options);
/* 188:397 */     if (optionString.length() != 0) {
/* 189:398 */       setStartSet(optionString);
/* 190:    */     }
/* 191:401 */     optionString = Utils.getOption('D', options);
/* 192:403 */     if (optionString.length() != 0) {
/* 193:404 */       setDirection(new SelectedTag(Integer.parseInt(optionString), TAGS_SELECTION));
/* 194:    */     } else {
/* 195:407 */       setDirection(new SelectedTag(1, TAGS_SELECTION));
/* 196:    */     }
/* 197:410 */     optionString = Utils.getOption('N', options);
/* 198:412 */     if (optionString.length() != 0) {
/* 199:413 */       setSearchTermination(Integer.parseInt(optionString));
/* 200:    */     }
/* 201:416 */     optionString = Utils.getOption('S', options);
/* 202:417 */     if (optionString.length() != 0) {
/* 203:418 */       setLookupCacheSize(Integer.parseInt(optionString));
/* 204:    */     }
/* 205:421 */     this.m_debug = Utils.getFlag('Z', options);
/* 206:    */   }
/* 207:    */   
/* 208:    */   public void setLookupCacheSize(int size)
/* 209:    */   {
/* 210:432 */     if (size >= 0) {
/* 211:433 */       this.m_cacheSize = size;
/* 212:    */     }
/* 213:    */   }
/* 214:    */   
/* 215:    */   public int getLookupCacheSize()
/* 216:    */   {
/* 217:444 */     return this.m_cacheSize;
/* 218:    */   }
/* 219:    */   
/* 220:    */   public String lookupCacheSizeTipText()
/* 221:    */   {
/* 222:454 */     return "Set the maximum size of the lookup cache of evaluated subsets. This is expressed as a multiplier of the number of attributes in the data set. (default = 1).";
/* 223:    */   }
/* 224:    */   
/* 225:    */   public String startSetTipText()
/* 226:    */   {
/* 227:466 */     return "Set the start point for the search. This is specified as a comma seperated list off attribute indexes starting at 1. It can include ranges. Eg. 1,2,5-9,17.";
/* 228:    */   }
/* 229:    */   
/* 230:    */   public void setStartSet(String startSet)
/* 231:    */     throws Exception
/* 232:    */   {
/* 233:481 */     this.m_startRange.setRanges(startSet);
/* 234:    */   }
/* 235:    */   
/* 236:    */   public String getStartSet()
/* 237:    */   {
/* 238:491 */     return this.m_startRange.getRanges();
/* 239:    */   }
/* 240:    */   
/* 241:    */   public String searchTerminationTipText()
/* 242:    */   {
/* 243:501 */     return "Specify the number of consecutive non-improving nodes to allow before terminating the search.";
/* 244:    */   }
/* 245:    */   
/* 246:    */   public void setSearchTermination(int t)
/* 247:    */     throws Exception
/* 248:    */   {
/* 249:513 */     if (t < 1) {
/* 250:514 */       throw new Exception("Value of -N must be > 0.");
/* 251:    */     }
/* 252:517 */     this.m_maxStale = t;
/* 253:    */   }
/* 254:    */   
/* 255:    */   public int getSearchTermination()
/* 256:    */   {
/* 257:526 */     return this.m_maxStale;
/* 258:    */   }
/* 259:    */   
/* 260:    */   public String directionTipText()
/* 261:    */   {
/* 262:536 */     return "Set the direction of the search.";
/* 263:    */   }
/* 264:    */   
/* 265:    */   public void setDirection(SelectedTag d)
/* 266:    */   {
/* 267:546 */     if (d.getTags() == TAGS_SELECTION) {
/* 268:547 */       this.m_searchDirection = d.getSelectedTag().getID();
/* 269:    */     }
/* 270:    */   }
/* 271:    */   
/* 272:    */   public SelectedTag getDirection()
/* 273:    */   {
/* 274:558 */     return new SelectedTag(this.m_searchDirection, TAGS_SELECTION);
/* 275:    */   }
/* 276:    */   
/* 277:    */   public String[] getOptions()
/* 278:    */   {
/* 279:569 */     Vector<String> options = new Vector();
/* 280:571 */     if (!getStartSet().equals(""))
/* 281:    */     {
/* 282:572 */       options.add("-P");
/* 283:573 */       options.add("" + startSetToString());
/* 284:    */     }
/* 285:575 */     options.add("-D");
/* 286:576 */     options.add("" + this.m_searchDirection);
/* 287:577 */     options.add("-N");
/* 288:578 */     options.add("" + this.m_maxStale);
/* 289:    */     
/* 290:580 */     return (String[])options.toArray(new String[0]);
/* 291:    */   }
/* 292:    */   
/* 293:    */   private String startSetToString()
/* 294:    */   {
/* 295:593 */     StringBuffer FString = new StringBuffer();
/* 296:596 */     if (this.m_starting == null) {
/* 297:597 */       return getStartSet();
/* 298:    */     }
/* 299:599 */     for (int i = 0; i < this.m_starting.length; i++)
/* 300:    */     {
/* 301:600 */       boolean didPrint = false;
/* 302:602 */       if ((!this.m_hasClass) || ((this.m_hasClass == true) && (i != this.m_classIndex)))
/* 303:    */       {
/* 304:603 */         FString.append(this.m_starting[i] + 1);
/* 305:604 */         didPrint = true;
/* 306:    */       }
/* 307:607 */       if (i == this.m_starting.length - 1) {
/* 308:608 */         FString.append("");
/* 309:610 */       } else if (didPrint) {
/* 310:611 */         FString.append(",");
/* 311:    */       }
/* 312:    */     }
/* 313:616 */     return FString.toString();
/* 314:    */   }
/* 315:    */   
/* 316:    */   public String toString()
/* 317:    */   {
/* 318:626 */     StringBuffer BfString = new StringBuffer();
/* 319:627 */     BfString.append("\tBest first.\n\tStart set: ");
/* 320:629 */     if (this.m_starting == null) {
/* 321:630 */       BfString.append("no attributes\n");
/* 322:    */     } else {
/* 323:632 */       BfString.append(startSetToString() + "\n");
/* 324:    */     }
/* 325:635 */     BfString.append("\tSearch direction: ");
/* 326:637 */     if (this.m_searchDirection == 0) {
/* 327:638 */       BfString.append("backward\n");
/* 328:640 */     } else if (this.m_searchDirection == 1) {
/* 329:641 */       BfString.append("forward\n");
/* 330:    */     } else {
/* 331:643 */       BfString.append("bi-directional\n");
/* 332:    */     }
/* 333:647 */     BfString.append("\tStale search after " + this.m_maxStale + " node expansions\n");
/* 334:    */     
/* 335:649 */     BfString.append("\tTotal number of subsets evaluated: " + this.m_totalEvals + "\n");
/* 336:    */     
/* 337:651 */     BfString.append("\tMerit of best subset found: " + Utils.doubleToString(Math.abs(this.m_bestMerit), 8, 3) + "\n");
/* 338:    */     
/* 339:653 */     return BfString.toString();
/* 340:    */   }
/* 341:    */   
/* 342:    */   protected void printGroup(BitSet tt, int numAttribs)
/* 343:    */   {
/* 344:659 */     for (int i = 0; i < numAttribs; i++) {
/* 345:660 */       if (tt.get(i) == true) {
/* 346:661 */         System.out.print(i + 1 + " ");
/* 347:    */       }
/* 348:    */     }
/* 349:665 */     System.out.println();
/* 350:    */   }
/* 351:    */   
/* 352:    */   public int[] search(ASEvaluation ASEval, Instances data)
/* 353:    */     throws Exception
/* 354:    */   {
/* 355:678 */     this.m_totalEvals = 0;
/* 356:679 */     if (!(ASEval instanceof SubsetEvaluator)) {
/* 357:680 */       throw new Exception(ASEval.getClass().getName() + " is not a " + "Subset evaluator!");
/* 358:    */     }
/* 359:684 */     if ((ASEval instanceof UnsupervisedSubsetEvaluator))
/* 360:    */     {
/* 361:685 */       this.m_hasClass = false;
/* 362:    */     }
/* 363:    */     else
/* 364:    */     {
/* 365:687 */       this.m_hasClass = true;
/* 366:688 */       this.m_classIndex = data.classIndex();
/* 367:    */     }
/* 368:691 */     SubsetEvaluator ASEvaluator = (SubsetEvaluator)ASEval;
/* 369:692 */     this.m_numAttribs = data.numAttributes();
/* 370:    */     
/* 371:694 */     int best_size = 0;
/* 372:695 */     int size = 0;
/* 373:    */     
/* 374:697 */     int sd = this.m_searchDirection;
/* 375:    */     
/* 376:    */ 
/* 377:    */ 
/* 378:    */ 
/* 379:    */ 
/* 380:    */ 
/* 381:    */ 
/* 382:705 */     Hashtable<String, Double> lookup = new Hashtable(this.m_cacheSize * this.m_numAttribs);
/* 383:    */     
/* 384:707 */     int insertCount = 0;
/* 385:708 */     LinkedList2 bfList = new LinkedList2(this.m_maxStale);
/* 386:709 */     double best_merit = -1.797693134862316E+308D;
/* 387:710 */     int stale = 0;
/* 388:711 */     BitSet best_group = new BitSet(this.m_numAttribs);
/* 389:    */     
/* 390:713 */     this.m_startRange.setUpper(this.m_numAttribs - 1);
/* 391:714 */     if (!getStartSet().equals("")) {
/* 392:715 */       this.m_starting = this.m_startRange.getSelection();
/* 393:    */     }
/* 394:718 */     if (this.m_starting != null)
/* 395:    */     {
/* 396:719 */       for (int i = 0; i < this.m_starting.length; i++) {
/* 397:720 */         if (this.m_starting[i] != this.m_classIndex) {
/* 398:721 */           best_group.set(this.m_starting[i]);
/* 399:    */         }
/* 400:    */       }
/* 401:725 */       best_size = this.m_starting.length;
/* 402:726 */       this.m_totalEvals += 1;
/* 403:    */     }
/* 404:728 */     else if (this.m_searchDirection == 0)
/* 405:    */     {
/* 406:729 */       setStartSet("1-last");
/* 407:730 */       this.m_starting = new int[this.m_numAttribs];
/* 408:    */       
/* 409:    */ 
/* 410:733 */       int i = 0;
/* 411:733 */       for (int j = 0; i < this.m_numAttribs; i++) {
/* 412:734 */         if (i != this.m_classIndex)
/* 413:    */         {
/* 414:735 */           best_group.set(i);
/* 415:736 */           this.m_starting[(j++)] = i;
/* 416:    */         }
/* 417:    */       }
/* 418:740 */       best_size = this.m_numAttribs - 1;
/* 419:741 */       this.m_totalEvals += 1;
/* 420:    */     }
/* 421:746 */     best_merit = ASEvaluator.evaluateSubset(best_group);
/* 422:    */     
/* 423:748 */     Object[] best = new Object[1];
/* 424:749 */     best[0] = best_group.clone();
/* 425:750 */     bfList.addToList(best, best_merit);
/* 426:751 */     BitSet tt = (BitSet)best_group.clone();
/* 427:752 */     String hashC = tt.toString();
/* 428:753 */     lookup.put(hashC, new Double(best_merit));
/* 429:755 */     while (stale < this.m_maxStale)
/* 430:    */     {
/* 431:756 */       boolean added = false;
/* 432:    */       int done;
/* 433:758 */       if (this.m_searchDirection == 2)
/* 434:    */       {
/* 435:760 */         int done = 2;
/* 436:761 */         sd = 1;
/* 437:    */       }
/* 438:    */       else
/* 439:    */       {
/* 440:763 */         done = 1;
/* 441:    */       }
/* 442:767 */       if (bfList.size() == 0)
/* 443:    */       {
/* 444:768 */         stale = this.m_maxStale;
/* 445:769 */         break;
/* 446:    */       }
/* 447:773 */       Link2 tl = bfList.getLinkAt(0);
/* 448:774 */       BitSet temp_group = (BitSet)tl.getData()[0];
/* 449:775 */       temp_group = (BitSet)temp_group.clone();
/* 450:    */       
/* 451:777 */       bfList.removeLinkAt(0);
/* 452:    */       
/* 453:    */ 
/* 454:    */ 
/* 455:781 */       int kk = 0;
/* 456:781 */       for (size = 0; kk < this.m_numAttribs; kk++) {
/* 457:782 */         if (temp_group.get(kk)) {
/* 458:783 */           size++;
/* 459:    */         }
/* 460:    */       }
/* 461:    */       do
/* 462:    */       {
/* 463:788 */         for (int i = 0; i < this.m_numAttribs; i++)
/* 464:    */         {
/* 465:    */           boolean z;
/* 466:    */           boolean z;
/* 467:789 */           if (sd == 1) {
/* 468:790 */             z = (i != this.m_classIndex) && (!temp_group.get(i));
/* 469:    */           } else {
/* 470:792 */             z = (i != this.m_classIndex) && (temp_group.get(i));
/* 471:    */           }
/* 472:795 */           if (z)
/* 473:    */           {
/* 474:797 */             if (sd == 1)
/* 475:    */             {
/* 476:798 */               temp_group.set(i);
/* 477:799 */               size++;
/* 478:    */             }
/* 479:    */             else
/* 480:    */             {
/* 481:801 */               temp_group.clear(i);
/* 482:802 */               size--;
/* 483:    */             }
/* 484:809 */             tt = (BitSet)temp_group.clone();
/* 485:810 */             hashC = tt.toString();
/* 486:    */             double merit;
/* 487:812 */             if (!lookup.containsKey(hashC))
/* 488:    */             {
/* 489:813 */               double merit = ASEvaluator.evaluateSubset(temp_group);
/* 490:814 */               this.m_totalEvals += 1;
/* 491:817 */               if (insertCount > this.m_cacheSize * this.m_numAttribs)
/* 492:    */               {
/* 493:818 */                 lookup = new Hashtable(this.m_cacheSize * this.m_numAttribs);
/* 494:    */                 
/* 495:820 */                 insertCount = 0;
/* 496:    */               }
/* 497:822 */               hashC = tt.toString();
/* 498:823 */               lookup.put(hashC, new Double(merit));
/* 499:824 */               insertCount++;
/* 500:    */             }
/* 501:    */             else
/* 502:    */             {
/* 503:826 */               merit = ((Double)lookup.get(hashC)).doubleValue();
/* 504:    */             }
/* 505:830 */             Object[] add = new Object[1];
/* 506:831 */             add[0] = tt.clone();
/* 507:832 */             bfList.addToList(add, merit);
/* 508:834 */             if (this.m_debug)
/* 509:    */             {
/* 510:835 */               System.out.print("Group: ");
/* 511:836 */               printGroup(tt, this.m_numAttribs);
/* 512:837 */               System.out.println("Merit: " + merit);
/* 513:    */             }
/* 514:841 */             if (sd == 1) {
/* 515:842 */               z = merit - best_merit > 1.E-005D;
/* 516:844 */             } else if (merit == best_merit) {
/* 517:845 */               z = size < best_size;
/* 518:    */             } else {
/* 519:847 */               z = merit > best_merit;
/* 520:    */             }
/* 521:851 */             if (z)
/* 522:    */             {
/* 523:852 */               added = true;
/* 524:853 */               stale = 0;
/* 525:854 */               best_merit = merit;
/* 526:    */               
/* 527:856 */               best_size = size;
/* 528:857 */               best_group = (BitSet)temp_group.clone();
/* 529:    */             }
/* 530:861 */             if (sd == 1)
/* 531:    */             {
/* 532:862 */               temp_group.clear(i);
/* 533:863 */               size--;
/* 534:    */             }
/* 535:    */             else
/* 536:    */             {
/* 537:865 */               temp_group.set(i);
/* 538:866 */               size++;
/* 539:    */             }
/* 540:    */           }
/* 541:    */         }
/* 542:871 */         if (done == 2) {
/* 543:872 */           sd = 0;
/* 544:    */         }
/* 545:875 */         done--;
/* 546:876 */       } while (done > 0);
/* 547:882 */       if (!added) {
/* 548:883 */         stale++;
/* 549:    */       }
/* 550:    */     }
/* 551:887 */     this.m_bestMerit = best_merit;
/* 552:888 */     return attributeList(best_group);
/* 553:    */   }
/* 554:    */   
/* 555:    */   protected void resetOptions()
/* 556:    */   {
/* 557:895 */     this.m_maxStale = 5;
/* 558:896 */     this.m_searchDirection = 1;
/* 559:897 */     this.m_starting = null;
/* 560:898 */     this.m_startRange = new Range();
/* 561:899 */     this.m_classIndex = -1;
/* 562:900 */     this.m_totalEvals = 0;
/* 563:901 */     this.m_cacheSize = 1;
/* 564:902 */     this.m_debug = false;
/* 565:    */   }
/* 566:    */   
/* 567:    */   protected int[] attributeList(BitSet group)
/* 568:    */   {
/* 569:912 */     int count = 0;
/* 570:915 */     for (int i = 0; i < this.m_numAttribs; i++) {
/* 571:916 */       if (group.get(i)) {
/* 572:917 */         count++;
/* 573:    */       }
/* 574:    */     }
/* 575:921 */     int[] list = new int[count];
/* 576:922 */     count = 0;
/* 577:924 */     for (int i = 0; i < this.m_numAttribs; i++) {
/* 578:925 */       if (group.get(i)) {
/* 579:926 */         list[(count++)] = i;
/* 580:    */       }
/* 581:    */     }
/* 582:930 */     return list;
/* 583:    */   }
/* 584:    */   
/* 585:    */   public String getRevision()
/* 586:    */   {
/* 587:940 */     return RevisionUtils.extract("$Revision: 10396 $");
/* 588:    */   }
/* 589:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.BestFirst
 * JD-Core Version:    0.7.0.1
 */