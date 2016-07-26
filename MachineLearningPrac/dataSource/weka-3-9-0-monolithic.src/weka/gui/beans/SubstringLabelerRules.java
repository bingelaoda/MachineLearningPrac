/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.HashSet;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Set;
/*   8:    */ import java.util.Vector;
/*   9:    */ import java.util.regex.Matcher;
/*  10:    */ import java.util.regex.Pattern;
/*  11:    */ import weka.core.Attribute;
/*  12:    */ import weka.core.DenseInstance;
/*  13:    */ import weka.core.Environment;
/*  14:    */ import weka.core.EnvironmentHandler;
/*  15:    */ import weka.core.Instance;
/*  16:    */ import weka.core.Instances;
/*  17:    */ import weka.core.Range;
/*  18:    */ import weka.core.SerializedObject;
/*  19:    */ import weka.core.Utils;
/*  20:    */ import weka.gui.Logger;
/*  21:    */ 
/*  22:    */ public class SubstringLabelerRules
/*  23:    */   implements EnvironmentHandler, Serializable
/*  24:    */ {
/*  25:    */   public static final String MATCH_RULE_SEPARATOR = "@@match-rule@@";
/*  26:    */   private static final long serialVersionUID = 1392983905562573599L;
/*  27:    */   protected List<SubstringLabelerMatchRule> m_matchRules;
/*  28:    */   protected boolean m_hasLabels;
/*  29:    */   protected boolean m_consumeNonMatching;
/*  30:    */   protected Instances m_inputStructure;
/*  31:    */   protected Instances m_outputStructure;
/*  32: 77 */   protected String m_attName = "newAtt";
/*  33: 80 */   protected String m_statusMessagePrefix = "";
/*  34:    */   protected boolean m_nominalBinary;
/*  35: 88 */   protected boolean m_voteLabels = true;
/*  36: 91 */   protected transient Environment m_env = Environment.getSystemWide();
/*  37:    */   
/*  38:    */   public SubstringLabelerRules(String matchDetails, String newAttName, boolean consumeNonMatching, boolean nominalBinary, Instances inputStructure, String statusMessagePrefix, Logger log, Environment env)
/*  39:    */     throws Exception
/*  40:    */   {
/*  41:112 */     this.m_matchRules = matchRulesFromInternal(matchDetails, inputStructure, statusMessagePrefix, log, env);
/*  42:    */     
/*  43:    */ 
/*  44:    */ 
/*  45:116 */     this.m_inputStructure = new Instances(inputStructure, 0);
/*  46:117 */     this.m_attName = newAttName;
/*  47:118 */     this.m_statusMessagePrefix = statusMessagePrefix;
/*  48:119 */     this.m_consumeNonMatching = consumeNonMatching;
/*  49:120 */     this.m_nominalBinary = nominalBinary;
/*  50:121 */     this.m_env = env;
/*  51:    */     
/*  52:123 */     makeOutputStructure();
/*  53:    */   }
/*  54:    */   
/*  55:    */   public SubstringLabelerRules(String matchDetails, String newAttName, Instances inputStructure)
/*  56:    */     throws Exception
/*  57:    */   {
/*  58:137 */     this(matchDetails, newAttName, false, false, inputStructure, "", null, Environment.getSystemWide());
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setConsumeNonMatching(boolean n)
/*  62:    */   {
/*  63:149 */     this.m_consumeNonMatching = n;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public boolean getConsumeNonMatching()
/*  67:    */   {
/*  68:160 */     return this.m_consumeNonMatching;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setNominalBinary(boolean n)
/*  72:    */   {
/*  73:174 */     this.m_nominalBinary = n;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public boolean getNominalBinary()
/*  77:    */   {
/*  78:188 */     return this.m_nominalBinary;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public Instances getOutputStructure()
/*  82:    */   {
/*  83:197 */     return this.m_outputStructure;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public Instances getInputStructure()
/*  87:    */   {
/*  88:206 */     return this.m_inputStructure;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setNewAttributeName(String newName)
/*  92:    */   {
/*  93:215 */     this.m_attName = newName;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public String getNewAttributeName()
/*  97:    */   {
/*  98:224 */     return this.m_attName;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void setEnvironment(Environment env)
/* 102:    */   {
/* 103:229 */     this.m_env = env;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public static List<SubstringLabelerMatchRule> matchRulesFromInternal(String matchDetails, Instances inputStructure, String statusMessagePrefix, Logger log, Environment env)
/* 107:    */   {
/* 108:246 */     List<SubstringLabelerMatchRule> matchRules = new ArrayList();
/* 109:    */     
/* 110:    */ 
/* 111:249 */     String[] matchParts = matchDetails.split("@@match-rule@@");
/* 112:250 */     for (String p : matchParts)
/* 113:    */     {
/* 114:251 */       SubstringLabelerMatchRule m = new SubstringLabelerMatchRule(p.trim());
/* 115:252 */       m.m_statusMessagePrefix = (statusMessagePrefix == null ? "" : statusMessagePrefix);
/* 116:    */       
/* 117:254 */       m.m_logger = log;
/* 118:255 */       m.init(env, inputStructure);
/* 119:256 */       matchRules.add(m);
/* 120:    */     }
/* 121:259 */     return matchRules;
/* 122:    */   }
/* 123:    */   
/* 124:    */   protected void makeOutputStructure()
/* 125:    */     throws Exception
/* 126:    */   {
/* 127:270 */     if (this.m_matchRules.size() > 0)
/* 128:    */     {
/* 129:272 */       int labelCount = 0;
/* 130:    */       
/* 131:274 */       HashSet<String> uniqueLabels = new HashSet();
/* 132:275 */       Vector<String> labelVec = new Vector();
/* 133:276 */       for (SubstringLabelerMatchRule m : this.m_matchRules) {
/* 134:277 */         if ((m.getLabel() != null) && (m.getLabel().length() > 0))
/* 135:    */         {
/* 136:278 */           if (!uniqueLabels.contains(m.getLabel()))
/* 137:    */           {
/* 138:283 */             uniqueLabels.add(m.getLabel());
/* 139:284 */             labelVec.addElement(m.getLabel());
/* 140:    */           }
/* 141:286 */           labelCount++;
/* 142:    */         }
/* 143:    */       }
/* 144:290 */       if (labelCount > 0) {
/* 145:291 */         if (labelCount == this.m_matchRules.size()) {
/* 146:292 */           this.m_hasLabels = true;
/* 147:    */         } else {
/* 148:294 */           throw new Exception("Can't have only some rules with a label!");
/* 149:    */         }
/* 150:    */       }
/* 151:298 */       this.m_outputStructure = ((Instances)new SerializedObject(this.m_inputStructure).getObject());
/* 152:    */       
/* 153:300 */       Attribute newAtt = null;
/* 154:301 */       if (this.m_hasLabels)
/* 155:    */       {
/* 156:302 */         newAtt = new Attribute(this.m_attName, labelVec);
/* 157:    */       }
/* 158:303 */       else if (this.m_nominalBinary)
/* 159:    */       {
/* 160:304 */         labelVec.addElement("0");
/* 161:305 */         labelVec.addElement("1");
/* 162:306 */         newAtt = new Attribute(this.m_attName, labelVec);
/* 163:    */       }
/* 164:    */       else
/* 165:    */       {
/* 166:308 */         newAtt = new Attribute(this.m_attName);
/* 167:    */       }
/* 168:311 */       this.m_outputStructure.insertAttributeAt(newAtt, this.m_outputStructure.numAttributes());
/* 169:    */       
/* 170:    */ 
/* 171:    */ 
/* 172:    */ 
/* 173:    */ 
/* 174:    */ 
/* 175:    */ 
/* 176:    */ 
/* 177:    */ 
/* 178:    */ 
/* 179:    */ 
/* 180:323 */       return;
/* 181:    */     }
/* 182:326 */     this.m_outputStructure = new Instances(this.m_inputStructure);
/* 183:    */   }
/* 184:    */   
/* 185:    */   public Instance makeOutputInstance(Instance inputI, boolean batch)
/* 186:    */     throws Exception
/* 187:    */   {
/* 188:342 */     if (this.m_outputStructure == null) {
/* 189:343 */       throw new Exception("OutputStructure has not been determined!");
/* 190:    */     }
/* 191:346 */     int newAttIndex = this.m_outputStructure.numAttributes() - 1;
/* 192:    */     
/* 193:348 */     Instance result = inputI;
/* 194:349 */     if (this.m_matchRules.size() > 0)
/* 195:    */     {
/* 196:350 */       String label = null;
/* 197:351 */       int[] labelVotes = new int[this.m_matchRules.size()];
/* 198:352 */       int index = 0;
/* 199:353 */       for (SubstringLabelerMatchRule m : this.m_matchRules)
/* 200:    */       {
/* 201:354 */         label = m.apply(inputI);
/* 202:356 */         if (label != null)
/* 203:    */         {
/* 204:357 */           if (!this.m_voteLabels) {
/* 205:    */             break;
/* 206:    */           }
/* 207:358 */           labelVotes[index] += 1;
/* 208:    */         }
/* 209:363 */         index++;
/* 210:    */       }
/* 211:366 */       if ((this.m_voteLabels) && (Utils.sum(labelVotes) > 0))
/* 212:    */       {
/* 213:367 */         int maxIndex = Utils.maxIndex(labelVotes);
/* 214:368 */         label = ((SubstringLabelerMatchRule)this.m_matchRules.get(maxIndex)).getLabel();
/* 215:    */       }
/* 216:371 */       double[] vals = new double[this.m_outputStructure.numAttributes()];
/* 217:372 */       for (int i = 0; i < inputI.numAttributes(); i++) {
/* 218:373 */         if (!inputI.attribute(i).isString())
/* 219:    */         {
/* 220:374 */           vals[i] = inputI.value(i);
/* 221:    */         }
/* 222:376 */         else if (!batch)
/* 223:    */         {
/* 224:377 */           vals[i] = 0.0D;
/* 225:378 */           String v = inputI.stringValue(i);
/* 226:379 */           this.m_outputStructure.attribute(i).setStringValue(v);
/* 227:    */         }
/* 228:    */         else
/* 229:    */         {
/* 230:381 */           String v = inputI.stringValue(i);
/* 231:382 */           vals[i] = this.m_outputStructure.attribute(i).addStringValue(v);
/* 232:    */         }
/* 233:    */       }
/* 234:387 */       if (label != null)
/* 235:    */       {
/* 236:388 */         if (this.m_hasLabels) {
/* 237:389 */           vals[newAttIndex] = this.m_outputStructure.attribute(this.m_attName).indexOfValue(label);
/* 238:    */         } else {
/* 239:392 */           vals[newAttIndex] = 1.0D;
/* 240:    */         }
/* 241:    */       }
/* 242:395 */       else if (this.m_hasLabels)
/* 243:    */       {
/* 244:396 */         if (!getConsumeNonMatching()) {
/* 245:397 */           vals[newAttIndex] = Utils.missingValue();
/* 246:    */         } else {
/* 247:399 */           return null;
/* 248:    */         }
/* 249:    */       }
/* 250:    */       else {
/* 251:402 */         vals[newAttIndex] = 0.0D;
/* 252:    */       }
/* 253:406 */       result = new DenseInstance(1.0D, vals);
/* 254:407 */       result.setDataset(this.m_outputStructure);
/* 255:    */     }
/* 256:410 */     return result;
/* 257:    */   }
/* 258:    */   
/* 259:    */   public static class SubstringLabelerMatchRule
/* 260:    */     implements Serializable
/* 261:    */   {
/* 262:    */     public static final String MATCH_PART_SEPARATOR = "@@MR@@";
/* 263:    */     private static final long serialVersionUID = 6518104085439241523L;
/* 264:426 */     protected String m_match = "";
/* 265:429 */     protected String m_label = "";
/* 266:    */     protected boolean m_regex;
/* 267:    */     protected boolean m_ignoreCase;
/* 268:    */     protected Pattern m_regexPattern;
/* 269:441 */     protected String m_attsToApplyTo = "";
/* 270:    */     protected String m_matchS;
/* 271:    */     protected String m_labelS;
/* 272:    */     protected int[] m_selectedAtts;
/* 273:    */     protected String m_statusMessagePrefix;
/* 274:    */     protected Logger m_logger;
/* 275:    */     
/* 276:    */     public SubstringLabelerMatchRule() {}
/* 277:    */     
/* 278:    */     public SubstringLabelerMatchRule(String setup)
/* 279:    */     {
/* 280:471 */       parseFromInternal(setup);
/* 281:    */     }
/* 282:    */     
/* 283:    */     public SubstringLabelerMatchRule(String match, boolean regex, boolean ignoreCase, String selectedAtts)
/* 284:    */     {
/* 285:484 */       this.m_match = match;
/* 286:485 */       this.m_regex = regex;
/* 287:486 */       this.m_ignoreCase = ignoreCase;
/* 288:487 */       this.m_attsToApplyTo = selectedAtts;
/* 289:    */     }
/* 290:    */     
/* 291:    */     protected void parseFromInternal(String setup)
/* 292:    */     {
/* 293:496 */       String[] parts = setup.split("@@MR@@");
/* 294:497 */       if ((parts.length < 4) || (parts.length > 5)) {
/* 295:498 */         throw new IllegalArgumentException("Malformed match definition: " + setup);
/* 296:    */       }
/* 297:502 */       this.m_attsToApplyTo = parts[0].trim();
/* 298:503 */       this.m_regex = parts[1].trim().toLowerCase().equals("t");
/* 299:504 */       this.m_ignoreCase = parts[2].trim().toLowerCase().equals("t");
/* 300:505 */       this.m_match = parts[3].trim();
/* 301:507 */       if ((this.m_match == null) || (this.m_match.length() == 0)) {
/* 302:508 */         throw new IllegalArgumentException("Must provide something to match!");
/* 303:    */       }
/* 304:511 */       if (parts.length == 5) {
/* 305:512 */         this.m_label = parts[4].trim();
/* 306:    */       }
/* 307:    */     }
/* 308:    */     
/* 309:    */     public void setMatch(String match)
/* 310:    */     {
/* 311:522 */       this.m_match = match;
/* 312:    */     }
/* 313:    */     
/* 314:    */     public String getMatch()
/* 315:    */     {
/* 316:531 */       return this.m_match;
/* 317:    */     }
/* 318:    */     
/* 319:    */     public void setLabel(String label)
/* 320:    */     {
/* 321:541 */       this.m_label = label;
/* 322:    */     }
/* 323:    */     
/* 324:    */     public String getLabel()
/* 325:    */     {
/* 326:551 */       return this.m_label;
/* 327:    */     }
/* 328:    */     
/* 329:    */     public void setRegex(boolean regex)
/* 330:    */     {
/* 331:560 */       this.m_regex = regex;
/* 332:    */     }
/* 333:    */     
/* 334:    */     public boolean getRegex()
/* 335:    */     {
/* 336:569 */       return this.m_regex;
/* 337:    */     }
/* 338:    */     
/* 339:    */     public void setIgnoreCase(boolean ignore)
/* 340:    */     {
/* 341:578 */       this.m_ignoreCase = ignore;
/* 342:    */     }
/* 343:    */     
/* 344:    */     public boolean getIgnoreCase()
/* 345:    */     {
/* 346:587 */       return this.m_ignoreCase;
/* 347:    */     }
/* 348:    */     
/* 349:    */     public void setAttsToApplyTo(String a)
/* 350:    */     {
/* 351:596 */       this.m_attsToApplyTo = a;
/* 352:    */     }
/* 353:    */     
/* 354:    */     public String getAttsToApplyTo()
/* 355:    */     {
/* 356:605 */       return this.m_attsToApplyTo;
/* 357:    */     }
/* 358:    */     
/* 359:    */     public void init(Environment env, Instances structure)
/* 360:    */     {
/* 361:618 */       this.m_matchS = this.m_match;
/* 362:619 */       this.m_labelS = this.m_label;
/* 363:620 */       String attsToApplyToS = this.m_attsToApplyTo;
/* 364:    */       try
/* 365:    */       {
/* 366:623 */         this.m_matchS = env.substitute(this.m_matchS);
/* 367:624 */         this.m_labelS = env.substitute(this.m_labelS);
/* 368:625 */         attsToApplyToS = env.substitute(attsToApplyToS);
/* 369:    */       }
/* 370:    */       catch (Exception ex) {}
/* 371:629 */       if (this.m_regex)
/* 372:    */       {
/* 373:630 */         String match = this.m_matchS;
/* 374:631 */         if (this.m_ignoreCase) {
/* 375:632 */           match = match.toLowerCase();
/* 376:    */         }
/* 377:636 */         this.m_regexPattern = Pattern.compile(match);
/* 378:    */       }
/* 379:640 */       String tempRangeS = attsToApplyToS;
/* 380:641 */       tempRangeS = tempRangeS.replace("/first", "first").replace("/last", "last");
/* 381:    */       
/* 382:643 */       Range tempR = new Range();
/* 383:644 */       tempR.setRanges(attsToApplyToS);
/* 384:    */       try
/* 385:    */       {
/* 386:646 */         tempR.setUpper(structure.numAttributes() - 1);
/* 387:647 */         this.m_selectedAtts = tempR.getSelection();
/* 388:    */       }
/* 389:    */       catch (IllegalArgumentException ex)
/* 390:    */       {
/* 391:650 */         this.m_selectedAtts = null;
/* 392:    */       }
/* 393:    */       int c;
/* 394:653 */       if (this.m_selectedAtts == null)
/* 395:    */       {
/* 396:655 */         Set<Integer> indexes = new HashSet();
/* 397:656 */         String[] attParts = this.m_attsToApplyTo.split(",");
/* 398:657 */         for (String att : attParts)
/* 399:    */         {
/* 400:658 */           att = att.trim();
/* 401:659 */           if (att.toLowerCase().equals("/first"))
/* 402:    */           {
/* 403:660 */             indexes.add(Integer.valueOf(0));
/* 404:    */           }
/* 405:661 */           else if (att.toLowerCase().equals("/last"))
/* 406:    */           {
/* 407:662 */             indexes.add(Integer.valueOf(structure.numAttributes() - 1));
/* 408:    */           }
/* 409:665 */           else if (structure.attribute(att) != null)
/* 410:    */           {
/* 411:666 */             indexes.add(new Integer(structure.attribute(att).index()));
/* 412:    */           }
/* 413:668 */           else if (this.m_logger != null)
/* 414:    */           {
/* 415:669 */             String msg = this.m_statusMessagePrefix + "Can't find attribute '" + att + "in the incoming instances - ignoring";
/* 416:    */             
/* 417:    */ 
/* 418:672 */             this.m_logger.logMessage(msg);
/* 419:    */           }
/* 420:    */         }
/* 421:678 */         this.m_selectedAtts = new int[indexes.size()];
/* 422:679 */         c = 0;
/* 423:680 */         for (Integer i : indexes) {
/* 424:681 */           this.m_selectedAtts[(c++)] = i.intValue();
/* 425:    */         }
/* 426:    */       }
/* 427:686 */       Set<Integer> indexes = new HashSet();
/* 428:687 */       for (int m_selectedAtt : this.m_selectedAtts) {
/* 429:688 */         if (structure.attribute(m_selectedAtt).isString())
/* 430:    */         {
/* 431:689 */           indexes.add(Integer.valueOf(m_selectedAtt));
/* 432:    */         }
/* 433:691 */         else if (this.m_logger != null)
/* 434:    */         {
/* 435:692 */           String msg = this.m_statusMessagePrefix + "Attribute '" + structure.attribute(m_selectedAtt).name() + "is not a string attribute - " + "ignoring";
/* 436:    */           
/* 437:    */ 
/* 438:    */ 
/* 439:696 */           this.m_logger.logMessage(msg);
/* 440:    */         }
/* 441:    */       }
/* 442:702 */       this.m_selectedAtts = new int[indexes.size()];
/* 443:703 */       int c = 0;
/* 444:704 */       for (Integer i : indexes) {
/* 445:705 */         this.m_selectedAtts[(c++)] = i.intValue();
/* 446:    */       }
/* 447:    */     }
/* 448:    */     
/* 449:    */     public String apply(Instance inst)
/* 450:    */     {
/* 451:719 */       for (int i = 0; i < this.m_selectedAtts.length; i++) {
/* 452:720 */         if (!inst.isMissing(this.m_selectedAtts[i]))
/* 453:    */         {
/* 454:721 */           String value = inst.stringValue(this.m_selectedAtts[i]);
/* 455:    */           
/* 456:723 */           String result = apply(value);
/* 457:724 */           if (result != null) {
/* 458:726 */             return result;
/* 459:    */           }
/* 460:    */         }
/* 461:    */       }
/* 462:731 */       return null;
/* 463:    */     }
/* 464:    */     
/* 465:    */     protected String apply(String source)
/* 466:    */     {
/* 467:743 */       String result = source;
/* 468:744 */       String match = this.m_matchS;
/* 469:745 */       boolean ruleMatches = false;
/* 470:746 */       if (this.m_ignoreCase)
/* 471:    */       {
/* 472:747 */         result = result.toLowerCase();
/* 473:748 */         match = match.toLowerCase();
/* 474:    */       }
/* 475:750 */       if ((result != null) && (result.length() > 0)) {
/* 476:751 */         if (this.m_regex)
/* 477:    */         {
/* 478:752 */           if (this.m_regexPattern.matcher(result).matches()) {
/* 479:754 */             ruleMatches = true;
/* 480:    */           }
/* 481:    */         }
/* 482:    */         else {
/* 483:757 */           ruleMatches = result.indexOf(match) >= 0;
/* 484:    */         }
/* 485:    */       }
/* 486:761 */       return ruleMatches ? this.m_label : null;
/* 487:    */     }
/* 488:    */     
/* 489:    */     public String toString()
/* 490:    */     {
/* 491:774 */       StringBuffer buff = new StringBuffer();
/* 492:775 */       buff.append(this.m_regex ? "Regex: " : "Substring: ");
/* 493:776 */       buff.append(this.m_match).append("  ");
/* 494:777 */       buff.append(this.m_ignoreCase ? "[ignore case]" : "").append("  ");
/* 495:778 */       if ((this.m_label != null) && (this.m_label.length() > 0)) {
/* 496:779 */         buff.append("Label: ").append(this.m_label).append("  ");
/* 497:    */       }
/* 498:781 */       buff.append("[Atts: " + this.m_attsToApplyTo + "]");
/* 499:    */       
/* 500:783 */       return buff.toString();
/* 501:    */     }
/* 502:    */     
/* 503:    */     public String toStringInternal()
/* 504:    */     {
/* 505:795 */       StringBuffer buff = new StringBuffer();
/* 506:796 */       buff.append(this.m_attsToApplyTo).append("@@MR@@");
/* 507:797 */       buff.append(this.m_regex ? "t" : "f").append("@@MR@@");
/* 508:798 */       buff.append(this.m_ignoreCase ? "t" : "f").append("@@MR@@");
/* 509:799 */       buff.append(this.m_match).append("@@MR@@");
/* 510:800 */       buff.append(this.m_label);
/* 511:    */       
/* 512:802 */       return buff.toString();
/* 513:    */     }
/* 514:    */   }
/* 515:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.SubstringLabelerRules
 * JD-Core Version:    0.7.0.1
 */