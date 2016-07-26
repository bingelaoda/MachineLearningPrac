/*   1:    */ package weka.datagenerators.clusterers;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Random;
/*   5:    */ import java.util.StringTokenizer;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Option;
/*   8:    */ import weka.core.Range;
/*   9:    */ import weka.core.RevisionUtils;
/*  10:    */ import weka.core.SelectedTag;
/*  11:    */ import weka.core.Tag;
/*  12:    */ import weka.core.Utils;
/*  13:    */ import weka.datagenerators.ClusterDefinition;
/*  14:    */ import weka.datagenerators.ClusterGenerator;
/*  15:    */ 
/*  16:    */ public class SubspaceClusterDefinition
/*  17:    */   extends ClusterDefinition
/*  18:    */ {
/*  19:    */   static final long serialVersionUID = 3135678125044007231L;
/*  20:    */   protected int m_clustertype;
/*  21:    */   protected int m_clustersubtype;
/*  22:    */   protected int m_numClusterAttributes;
/*  23:    */   protected int m_numInstances;
/*  24:    */   protected int m_MinInstNum;
/*  25:    */   protected int m_MaxInstNum;
/*  26:    */   protected Range m_AttrIndexRange;
/*  27:    */   protected boolean[] m_attributes;
/*  28:    */   protected int[] m_attrIndices;
/*  29:    */   protected double[] m_valueA;
/*  30:    */   protected double[] m_valueB;
/*  31:    */   
/*  32:    */   public SubspaceClusterDefinition() {}
/*  33:    */   
/*  34:    */   public SubspaceClusterDefinition(ClusterGenerator parent)
/*  35:    */   {
/*  36:135 */     super(parent);
/*  37:    */   }
/*  38:    */   
/*  39:    */   protected void setDefaults()
/*  40:    */     throws Exception
/*  41:    */   {
/*  42:145 */     setClusterType(defaultClusterType());
/*  43:146 */     setClusterSubType(defaultClusterSubType());
/*  44:147 */     setMinInstNum(defaultMinInstNum());
/*  45:148 */     setMaxInstNum(defaultMaxInstNum());
/*  46:149 */     setAttrIndexRange(defaultAttrIndexRange());
/*  47:150 */     this.m_numClusterAttributes = 1;
/*  48:151 */     setValuesList(defaultValuesList());
/*  49:    */   }
/*  50:    */   
/*  51:    */   public String globalInfo()
/*  52:    */   {
/*  53:162 */     return "A single cluster for the SubspaceCluster datagenerator";
/*  54:    */   }
/*  55:    */   
/*  56:    */   public Enumeration<Option> listOptions()
/*  57:    */   {
/*  58:172 */     Vector<Option> result = new Vector();
/*  59:    */     
/*  60:174 */     result.addElement(new Option("\tGenerates randomly distributed instances in the cluster.", "A", 1, "-A <range>"));
/*  61:    */     
/*  62:    */ 
/*  63:    */ 
/*  64:178 */     result.addElement(new Option("\tGenerates uniformly distributed instances in the cluster.", "U", 1, "-U <range>"));
/*  65:    */     
/*  66:    */ 
/*  67:    */ 
/*  68:182 */     result.addElement(new Option("\tGenerates gaussian distributed instances in the cluster.", "G", 1, "-G <range>"));
/*  69:    */     
/*  70:    */ 
/*  71:    */ 
/*  72:186 */     result.addElement(new Option("\tThe attribute min/max (-A and -U) or mean/stddev (-G) for\n\tthe cluster.", "D", 1, "-D <num>,<num>"));
/*  73:    */     
/*  74:    */ 
/*  75:    */ 
/*  76:190 */     result.addElement(new Option("\tThe range of number of instances per cluster (default " + defaultMinInstNum() + ".." + defaultMaxInstNum() + ").", "N", 1, "-N <num>..<num>"));
/*  77:    */     
/*  78:    */ 
/*  79:    */ 
/*  80:    */ 
/*  81:195 */     result.addElement(new Option("\tUses integer instead of continuous values (default continuous).", "I", 0, "-I"));
/*  82:    */     
/*  83:    */ 
/*  84:    */ 
/*  85:199 */     return result.elements();
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void setOptions(String[] options)
/*  89:    */     throws Exception
/*  90:    */   {
/*  91:251 */     int typeCount = 0;
/*  92:252 */     String fromToStr = "";
/*  93:    */     
/*  94:254 */     String tmpStr = Utils.getOption('A', options);
/*  95:255 */     if (tmpStr.length() != 0)
/*  96:    */     {
/*  97:256 */       fromToStr = tmpStr;
/*  98:257 */       setClusterType(new SelectedTag(0, SubspaceCluster.TAGS_CLUSTERTYPE));
/*  99:    */       
/* 100:259 */       typeCount++;
/* 101:    */     }
/* 102:262 */     tmpStr = Utils.getOption('U', options);
/* 103:263 */     if (tmpStr.length() != 0)
/* 104:    */     {
/* 105:264 */       fromToStr = tmpStr;
/* 106:265 */       setClusterType(new SelectedTag(1, SubspaceCluster.TAGS_CLUSTERTYPE));
/* 107:    */       
/* 108:267 */       typeCount++;
/* 109:    */     }
/* 110:270 */     tmpStr = Utils.getOption('G', options);
/* 111:271 */     if (tmpStr.length() != 0)
/* 112:    */     {
/* 113:272 */       fromToStr = tmpStr;
/* 114:273 */       setClusterType(new SelectedTag(2, SubspaceCluster.TAGS_CLUSTERTYPE));
/* 115:    */       
/* 116:275 */       typeCount++;
/* 117:    */     }
/* 118:279 */     if (typeCount == 0) {
/* 119:280 */       setClusterType(new SelectedTag(0, SubspaceCluster.TAGS_CLUSTERTYPE));
/* 120:282 */     } else if (typeCount > 1) {
/* 121:283 */       throw new Exception("Only one cluster type can be specified!");
/* 122:    */     }
/* 123:286 */     setAttrIndexRange(fromToStr);
/* 124:    */     
/* 125:288 */     tmpStr = Utils.getOption('D', options);
/* 126:289 */     if (tmpStr.length() != 0) {
/* 127:290 */       setValuesList(tmpStr);
/* 128:    */     } else {
/* 129:292 */       setValuesList(defaultValuesList());
/* 130:    */     }
/* 131:296 */     tmpStr = Utils.getOption('N', options);
/* 132:297 */     if (tmpStr.length() != 0) {
/* 133:298 */       setInstNums(tmpStr);
/* 134:    */     } else {
/* 135:300 */       setInstNums(defaultMinInstNum() + ".." + defaultMaxInstNum());
/* 136:    */     }
/* 137:303 */     if (Utils.getFlag('I', options)) {
/* 138:304 */       setClusterSubType(new SelectedTag(1, SubspaceCluster.TAGS_CLUSTERSUBTYPE));
/* 139:    */     } else {
/* 140:307 */       setClusterSubType(new SelectedTag(0, SubspaceCluster.TAGS_CLUSTERSUBTYPE));
/* 141:    */     }
/* 142:    */   }
/* 143:    */   
/* 144:    */   public String[] getOptions()
/* 145:    */   {
/* 146:321 */     Vector<String> result = new Vector();
/* 147:323 */     if (isRandom())
/* 148:    */     {
/* 149:324 */       result.add("-A");
/* 150:325 */       result.add("" + getAttrIndexRange());
/* 151:    */     }
/* 152:326 */     else if (isUniform())
/* 153:    */     {
/* 154:327 */       result.add("-U");
/* 155:328 */       result.add("" + getAttrIndexRange());
/* 156:    */     }
/* 157:329 */     else if (isGaussian())
/* 158:    */     {
/* 159:330 */       result.add("-G");
/* 160:331 */       result.add("" + getAttrIndexRange());
/* 161:    */     }
/* 162:334 */     result.add("-D");
/* 163:335 */     result.add("" + getValuesList());
/* 164:    */     
/* 165:337 */     result.add("-N");
/* 166:338 */     result.add("" + getInstNums());
/* 167:340 */     if (this.m_clustersubtype == 1) {
/* 168:341 */       result.add("-I");
/* 169:    */     }
/* 170:344 */     return (String[])result.toArray(new String[result.size()]);
/* 171:    */   }
/* 172:    */   
/* 173:    */   public String attributesToString()
/* 174:    */   {
/* 175:353 */     StringBuffer text = new StringBuffer();
/* 176:354 */     int j = 0;
/* 177:355 */     for (int i = 0; i < this.m_attributes.length; i++) {
/* 178:356 */       if (this.m_attributes[i] != 0)
/* 179:    */       {
/* 180:357 */         if (isGaussian())
/* 181:    */         {
/* 182:358 */           text.append(" Attribute: " + i);
/* 183:359 */           text.append(" Mean: " + this.m_valueA[j]);
/* 184:360 */           text.append(" StdDev: " + this.m_valueB[j] + "\n%");
/* 185:    */         }
/* 186:    */         else
/* 187:    */         {
/* 188:362 */           text.append(" Attribute: " + i);
/* 189:363 */           text.append(" Range: " + this.m_valueA[j]);
/* 190:364 */           text.append(" - " + this.m_valueB[j] + "\n%");
/* 191:    */         }
/* 192:366 */         j++;
/* 193:    */       }
/* 194:    */     }
/* 195:369 */     return text.toString();
/* 196:    */   }
/* 197:    */   
/* 198:    */   public String toString()
/* 199:    */   {
/* 200:379 */     StringBuffer text = new StringBuffer();
/* 201:380 */     text.append("attributes " + attributesToString() + "\n");
/* 202:381 */     text.append("number of instances " + getInstNums());
/* 203:382 */     return text.toString();
/* 204:    */   }
/* 205:    */   
/* 206:    */   public void setParent(SubspaceCluster parent)
/* 207:    */   {
/* 208:391 */     super.setParent(parent);
/* 209:392 */     this.m_AttrIndexRange.setUpper(getParent().getNumAttributes());
/* 210:    */   }
/* 211:    */   
/* 212:    */   protected String defaultAttrIndexRange()
/* 213:    */   {
/* 214:401 */     return "1";
/* 215:    */   }
/* 216:    */   
/* 217:    */   public void setAttrIndexRange(String rangeList)
/* 218:    */   {
/* 219:414 */     this.m_numClusterAttributes = 0;
/* 220:415 */     if (this.m_AttrIndexRange == null) {
/* 221:416 */       this.m_AttrIndexRange = new Range();
/* 222:    */     }
/* 223:418 */     this.m_AttrIndexRange.setRanges(rangeList);
/* 224:420 */     if (getParent() != null)
/* 225:    */     {
/* 226:421 */       this.m_AttrIndexRange.setUpper(getParent().getNumAttributes());
/* 227:422 */       this.m_attributes = new boolean[getParent().getNumAttributes()];
/* 228:423 */       for (int i = 0; i < this.m_attributes.length; i++) {
/* 229:424 */         if (this.m_AttrIndexRange.isInRange(i))
/* 230:    */         {
/* 231:425 */           this.m_numClusterAttributes += 1;
/* 232:426 */           this.m_attributes[i] = true;
/* 233:    */         }
/* 234:    */         else
/* 235:    */         {
/* 236:428 */           this.m_attributes[i] = false;
/* 237:    */         }
/* 238:    */       }
/* 239:433 */       this.m_attrIndices = new int[this.m_numClusterAttributes];
/* 240:434 */       int clusterI = -1;
/* 241:435 */       for (int i = 0; i < this.m_attributes.length; i++) {
/* 242:436 */         if (this.m_AttrIndexRange.isInRange(i))
/* 243:    */         {
/* 244:437 */           clusterI++;
/* 245:438 */           this.m_attrIndices[clusterI] = i;
/* 246:    */         }
/* 247:    */       }
/* 248:    */     }
/* 249:    */   }
/* 250:    */   
/* 251:    */   public String getAttrIndexRange()
/* 252:    */   {
/* 253:450 */     return this.m_AttrIndexRange.getRanges();
/* 254:    */   }
/* 255:    */   
/* 256:    */   public String attrIndexRangeTipText()
/* 257:    */   {
/* 258:460 */     return "The attribute range(s).";
/* 259:    */   }
/* 260:    */   
/* 261:    */   public boolean[] getAttributes()
/* 262:    */   {
/* 263:464 */     return this.m_attributes;
/* 264:    */   }
/* 265:    */   
/* 266:    */   public double[] getMinValue()
/* 267:    */   {
/* 268:468 */     return this.m_valueA;
/* 269:    */   }
/* 270:    */   
/* 271:    */   public double[] getMaxValue()
/* 272:    */   {
/* 273:472 */     return this.m_valueB;
/* 274:    */   }
/* 275:    */   
/* 276:    */   public double[] getMeanValue()
/* 277:    */   {
/* 278:476 */     return this.m_valueA;
/* 279:    */   }
/* 280:    */   
/* 281:    */   public double[] getStddevValue()
/* 282:    */   {
/* 283:480 */     return this.m_valueB;
/* 284:    */   }
/* 285:    */   
/* 286:    */   public int getNumInstances()
/* 287:    */   {
/* 288:484 */     return this.m_numInstances;
/* 289:    */   }
/* 290:    */   
/* 291:    */   protected SelectedTag defaultClusterType()
/* 292:    */   {
/* 293:493 */     return new SelectedTag(0, SubspaceCluster.TAGS_CLUSTERTYPE);
/* 294:    */   }
/* 295:    */   
/* 296:    */   public SelectedTag getClusterType()
/* 297:    */   {
/* 298:504 */     return new SelectedTag(this.m_clustertype, SubspaceCluster.TAGS_CLUSTERTYPE);
/* 299:    */   }
/* 300:    */   
/* 301:    */   public void setClusterType(SelectedTag value)
/* 302:    */   {
/* 303:514 */     if (value.getTags() == SubspaceCluster.TAGS_CLUSTERTYPE) {
/* 304:515 */       this.m_clustertype = value.getSelectedTag().getID();
/* 305:    */     }
/* 306:    */   }
/* 307:    */   
/* 308:    */   public String clusterTypeTipText()
/* 309:    */   {
/* 310:526 */     return "The type of cluster to use.";
/* 311:    */   }
/* 312:    */   
/* 313:    */   protected SelectedTag defaultClusterSubType()
/* 314:    */   {
/* 315:535 */     return new SelectedTag(0, SubspaceCluster.TAGS_CLUSTERSUBTYPE);
/* 316:    */   }
/* 317:    */   
/* 318:    */   public SelectedTag getClusterSubType()
/* 319:    */   {
/* 320:546 */     return new SelectedTag(this.m_clustersubtype, SubspaceCluster.TAGS_CLUSTERSUBTYPE);
/* 321:    */   }
/* 322:    */   
/* 323:    */   public void setClusterSubType(SelectedTag value)
/* 324:    */   {
/* 325:557 */     if (value.getTags() == SubspaceCluster.TAGS_CLUSTERSUBTYPE) {
/* 326:558 */       this.m_clustersubtype = value.getSelectedTag().getID();
/* 327:    */     }
/* 328:    */   }
/* 329:    */   
/* 330:    */   public String clusterSubTypeTipText()
/* 331:    */   {
/* 332:569 */     return "The sub-type of cluster to use.";
/* 333:    */   }
/* 334:    */   
/* 335:    */   public boolean isRandom()
/* 336:    */   {
/* 337:578 */     return this.m_clustertype == 0;
/* 338:    */   }
/* 339:    */   
/* 340:    */   public boolean isUniform()
/* 341:    */   {
/* 342:587 */     return this.m_clustertype == 1;
/* 343:    */   }
/* 344:    */   
/* 345:    */   public boolean isGaussian()
/* 346:    */   {
/* 347:596 */     return this.m_clustertype == 2;
/* 348:    */   }
/* 349:    */   
/* 350:    */   public boolean isContinuous()
/* 351:    */   {
/* 352:605 */     return this.m_clustertype == 0;
/* 353:    */   }
/* 354:    */   
/* 355:    */   public boolean isInteger()
/* 356:    */   {
/* 357:614 */     return this.m_clustersubtype == 1;
/* 358:    */   }
/* 359:    */   
/* 360:    */   protected void setInstNums(String fromTo)
/* 361:    */   {
/* 362:624 */     int i = fromTo.indexOf("..");
/* 363:625 */     if (i == -1) {
/* 364:626 */       i = fromTo.length();
/* 365:    */     }
/* 366:628 */     String from = fromTo.substring(0, i);
/* 367:629 */     this.m_MinInstNum = Integer.parseInt(from);
/* 368:630 */     if (i < fromTo.length())
/* 369:    */     {
/* 370:631 */       String to = fromTo.substring(i + 2, fromTo.length());
/* 371:632 */       this.m_MaxInstNum = Integer.parseInt(to);
/* 372:    */     }
/* 373:    */     else
/* 374:    */     {
/* 375:634 */       this.m_MaxInstNum = this.m_MinInstNum;
/* 376:    */     }
/* 377:    */   }
/* 378:    */   
/* 379:    */   protected String getInstNums()
/* 380:    */   {
/* 381:646 */     String text = new String("" + this.m_MinInstNum + ".." + this.m_MaxInstNum);
/* 382:647 */     return text;
/* 383:    */   }
/* 384:    */   
/* 385:    */   protected String instNumsTipText()
/* 386:    */   {
/* 387:657 */     return "The lower and upper boundary for the number of instances in this cluster.";
/* 388:    */   }
/* 389:    */   
/* 390:    */   protected int defaultMinInstNum()
/* 391:    */   {
/* 392:666 */     return 1;
/* 393:    */   }
/* 394:    */   
/* 395:    */   public int getMinInstNum()
/* 396:    */   {
/* 397:675 */     return this.m_MinInstNum;
/* 398:    */   }
/* 399:    */   
/* 400:    */   public void setMinInstNum(int newMinInstNum)
/* 401:    */   {
/* 402:684 */     this.m_MinInstNum = newMinInstNum;
/* 403:    */   }
/* 404:    */   
/* 405:    */   public String minInstNumTipText()
/* 406:    */   {
/* 407:694 */     return "The lower boundary for instances per cluster.";
/* 408:    */   }
/* 409:    */   
/* 410:    */   protected int defaultMaxInstNum()
/* 411:    */   {
/* 412:703 */     return 50;
/* 413:    */   }
/* 414:    */   
/* 415:    */   public int getMaxInstNum()
/* 416:    */   {
/* 417:712 */     return this.m_MaxInstNum;
/* 418:    */   }
/* 419:    */   
/* 420:    */   public void setMaxInstNum(int newMaxInstNum)
/* 421:    */   {
/* 422:721 */     this.m_MaxInstNum = newMaxInstNum;
/* 423:    */   }
/* 424:    */   
/* 425:    */   public String maxInstNumTipText()
/* 426:    */   {
/* 427:731 */     return "The upper boundary for instances per cluster.";
/* 428:    */   }
/* 429:    */   
/* 430:    */   public void setNumInstances(Random r)
/* 431:    */   {
/* 432:740 */     if (this.m_MaxInstNum > this.m_MinInstNum) {
/* 433:741 */       this.m_numInstances = ((int)(r.nextDouble() * (this.m_MaxInstNum - this.m_MinInstNum) + this.m_MinInstNum));
/* 434:    */     } else {
/* 435:743 */       this.m_numInstances = this.m_MinInstNum;
/* 436:    */     }
/* 437:    */   }
/* 438:    */   
/* 439:    */   protected String defaultValuesList()
/* 440:    */   {
/* 441:753 */     return "1,10";
/* 442:    */   }
/* 443:    */   
/* 444:    */   public void setValuesList(String fromToList)
/* 445:    */     throws Exception
/* 446:    */   {
/* 447:764 */     this.m_valueA = new double[this.m_numClusterAttributes];
/* 448:765 */     this.m_valueB = new double[this.m_numClusterAttributes];
/* 449:766 */     setValuesList(fromToList, this.m_valueA, this.m_valueB, "D");
/* 450:767 */     SubspaceCluster parent = (SubspaceCluster)getParent();
/* 451:769 */     for (int i = 0; i < this.m_numClusterAttributes; i++)
/* 452:    */     {
/* 453:770 */       if ((!isGaussian()) && (this.m_valueA[i] > this.m_valueB[i])) {
/* 454:771 */         throw new Exception("Min must be smaller than max.");
/* 455:    */       }
/* 456:774 */       if (getParent() != null)
/* 457:    */       {
/* 458:776 */         if (parent.isBoolean(this.m_attrIndices[i]))
/* 459:    */         {
/* 460:777 */           parent.getNumValues()[this.m_attrIndices[i]] = 2;
/* 461:778 */           if (((this.m_valueA[i] != 0.0D) && (this.m_valueA[i] != 1.0D)) || ((this.m_valueB[i] != 0.0D) && (this.m_valueB[i] != 1.0D))) {
/* 462:780 */             throw new Exception("Ranges for boolean must be 0 or 1 only.");
/* 463:    */           }
/* 464:    */         }
/* 465:784 */         if (parent.isNominal(this.m_attrIndices[i]))
/* 466:    */         {
/* 467:786 */           double rest = this.m_valueA[i] - Math.rint(this.m_valueA[i]);
/* 468:787 */           if (rest != 0.0D) {
/* 469:788 */             throw new Exception(" Ranges for nominal must be integer");
/* 470:    */           }
/* 471:790 */           rest = this.m_valueB[i] - Math.rint(this.m_valueB[i]);
/* 472:791 */           if (rest != 0.0D) {
/* 473:792 */             throw new Exception("Ranges for nominal must be integer");
/* 474:    */           }
/* 475:794 */           if (this.m_valueA[i] < 0.0D) {
/* 476:795 */             throw new Exception("Range for nominal must start with number 0.0 or higher");
/* 477:    */           }
/* 478:798 */           if (this.m_valueB[i] + 1.0D > parent.getNumValues()[this.m_attrIndices[i]]) {
/* 479:801 */             parent.getNumValues()[this.m_attrIndices[i]] = ((int)this.m_valueB[i] + 1);
/* 480:    */           }
/* 481:    */         }
/* 482:    */       }
/* 483:    */     }
/* 484:    */   }
/* 485:    */   
/* 486:    */   public String getValuesList()
/* 487:    */   {
/* 488:815 */     String result = "";
/* 489:817 */     if (this.m_valueA != null) {
/* 490:818 */       for (int i = 0; i < this.m_valueA.length; i++)
/* 491:    */       {
/* 492:819 */         if (i > 0) {
/* 493:820 */           result = result + ",";
/* 494:    */         }
/* 495:822 */         result = result + "" + this.m_valueA[i] + "," + this.m_valueB[i];
/* 496:    */       }
/* 497:    */     }
/* 498:826 */     return result;
/* 499:    */   }
/* 500:    */   
/* 501:    */   public String valuesListTipText()
/* 502:    */   {
/* 503:836 */     return "The min (mean) and max (standard deviation) for each attribute as a comma-separated string.";
/* 504:    */   }
/* 505:    */   
/* 506:    */   public void setValuesList(String fromToList, double[] first, double[] second, String optionLetter)
/* 507:    */     throws Exception
/* 508:    */   {
/* 509:855 */     StringTokenizer tok = new StringTokenizer(fromToList, ",");
/* 510:856 */     if (tok.countTokens() != first.length + second.length) {
/* 511:857 */       throw new Exception("Wrong number of values for option '-" + optionLetter + "'.");
/* 512:    */     }
/* 513:861 */     int index = 0;
/* 514:862 */     while (tok.hasMoreTokens())
/* 515:    */     {
/* 516:863 */       first[index] = Double.parseDouble(tok.nextToken());
/* 517:864 */       second[index] = Double.parseDouble(tok.nextToken());
/* 518:865 */       index++;
/* 519:    */     }
/* 520:    */   }
/* 521:    */   
/* 522:    */   public String getRevision()
/* 523:    */   {
/* 524:876 */     return RevisionUtils.extract("$Revision: 12478 $");
/* 525:    */   }
/* 526:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.datagenerators.clusterers.SubspaceClusterDefinition
 * JD-Core Version:    0.7.0.1
 */