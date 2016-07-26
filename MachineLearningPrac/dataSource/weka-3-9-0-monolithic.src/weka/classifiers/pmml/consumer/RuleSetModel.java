/*   1:    */ package weka.classifiers.pmml.consumer;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import org.w3c.dom.Element;
/*   6:    */ import org.w3c.dom.Node;
/*   7:    */ import org.w3c.dom.NodeList;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.Utils;
/*  13:    */ import weka.core.pmml.MappingInfo;
/*  14:    */ import weka.core.pmml.MiningSchema;
/*  15:    */ 
/*  16:    */ public class RuleSetModel
/*  17:    */   extends PMMLClassifier
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = 1993161168811020547L;
/*  20:    */   
/*  21:    */   static abstract class Rule
/*  22:    */     implements Serializable
/*  23:    */   {
/*  24:    */     private static final long serialVersionUID = 6236231263477446102L;
/*  25:    */     protected TreeModel.Predicate m_predicate;
/*  26:    */     
/*  27:    */     public Rule(Element ruleE, MiningSchema miningSchema)
/*  28:    */       throws Exception
/*  29:    */     {
/*  30: 64 */       this.m_predicate = TreeModel.Predicate.getPredicate(ruleE, miningSchema);
/*  31:    */     }
/*  32:    */     
/*  33:    */     public abstract void fires(double[] paramArrayOfDouble, ArrayList<RuleSetModel.SimpleRule> paramArrayList);
/*  34:    */     
/*  35:    */     public abstract String toString(String paramString, int paramInt);
/*  36:    */   }
/*  37:    */   
/*  38:    */   static class SimpleRule
/*  39:    */     extends RuleSetModel.Rule
/*  40:    */   {
/*  41:    */     private static final long serialVersionUID = -2612893679476049682L;
/*  42:    */     protected String m_ID;
/*  43:    */     protected String m_scoreString;
/*  44:104 */     protected double m_score = Utils.missingValue();
/*  45:107 */     protected double m_recordCount = Utils.missingValue();
/*  46:113 */     protected double m_nbCorrect = Utils.missingValue();
/*  47:116 */     protected double m_confidence = Utils.missingValue();
/*  48:119 */     protected ArrayList<TreeModel.ScoreDistribution> m_scoreDistributions = new ArrayList();
/*  49:126 */     protected double m_weight = Utils.missingValue();
/*  50:    */     
/*  51:    */     public String toString(String prefix, int indent)
/*  52:    */     {
/*  53:129 */       StringBuffer temp = new StringBuffer();
/*  54:131 */       for (int i = 0; i < indent; i++) {
/*  55:132 */         prefix = prefix + " ";
/*  56:    */       }
/*  57:135 */       temp.append(prefix + "Simple rule: " + this.m_predicate + "\n");
/*  58:136 */       temp.append(prefix + " => " + this.m_scoreString + "\n");
/*  59:137 */       if (!Utils.isMissingValue(this.m_recordCount)) {
/*  60:138 */         temp.append(prefix + " recordCount: " + this.m_recordCount + "\n");
/*  61:    */       }
/*  62:140 */       if (!Utils.isMissingValue(this.m_nbCorrect)) {
/*  63:141 */         temp.append(prefix + "   nbCorrect: " + this.m_nbCorrect + "\n");
/*  64:    */       }
/*  65:143 */       if (!Utils.isMissingValue(this.m_confidence)) {
/*  66:144 */         temp.append(prefix + "  confidence: " + this.m_confidence + "\n");
/*  67:    */       }
/*  68:146 */       if (!Utils.isMissingValue(this.m_weight)) {
/*  69:147 */         temp.append(prefix + "      weight: " + this.m_weight + "\n");
/*  70:    */       }
/*  71:150 */       return temp.toString();
/*  72:    */     }
/*  73:    */     
/*  74:    */     public String toString()
/*  75:    */     {
/*  76:154 */       return toString("", 0);
/*  77:    */     }
/*  78:    */     
/*  79:    */     public SimpleRule(Element ruleE, MiningSchema miningSchema)
/*  80:    */       throws Exception
/*  81:    */     {
/*  82:165 */       super(miningSchema);
/*  83:    */       
/*  84:167 */       String id = ruleE.getAttribute("id");
/*  85:168 */       if ((id != null) && (id.length() > 0)) {
/*  86:169 */         this.m_ID = id;
/*  87:    */       }
/*  88:172 */       this.m_scoreString = ruleE.getAttribute("score");
/*  89:173 */       Attribute classAtt = miningSchema.getFieldsAsInstances().classAttribute();
/*  90:174 */       if (classAtt.isNumeric())
/*  91:    */       {
/*  92:175 */         this.m_score = Double.parseDouble(this.m_scoreString);
/*  93:    */       }
/*  94:    */       else
/*  95:    */       {
/*  96:177 */         if (classAtt.indexOfValue(this.m_scoreString) < 0) {
/*  97:178 */           throw new Exception("[SimpleRule] class value " + this.m_scoreString + "does not exist in class attribute " + classAtt.name());
/*  98:    */         }
/*  99:181 */         this.m_score = classAtt.indexOfValue(this.m_scoreString);
/* 100:    */       }
/* 101:184 */       String recordCount = ruleE.getAttribute("recordCount");
/* 102:185 */       if ((recordCount != null) && (recordCount.length() > 0)) {
/* 103:186 */         this.m_recordCount = Double.parseDouble(recordCount);
/* 104:    */       }
/* 105:189 */       String nbCorrect = ruleE.getAttribute("nbCorrect");
/* 106:190 */       if ((nbCorrect != null) && (nbCorrect.length() > 0)) {
/* 107:191 */         this.m_nbCorrect = Double.parseDouble(nbCorrect);
/* 108:    */       }
/* 109:194 */       String confidence = ruleE.getAttribute("confidence");
/* 110:195 */       if ((confidence != null) && (confidence.length() > 0)) {
/* 111:196 */         this.m_confidence = Double.parseDouble(confidence);
/* 112:    */       }
/* 113:199 */       String weight = ruleE.getAttribute("weight");
/* 114:200 */       if ((weight != null) && (weight.length() > 0)) {
/* 115:201 */         this.m_weight = Double.parseDouble(weight);
/* 116:    */       }
/* 117:    */       double baseCount;
/* 118:205 */       if (miningSchema.getFieldsAsInstances().classAttribute().isNominal())
/* 119:    */       {
/* 120:207 */         NodeList scoreChildren = ruleE.getChildNodes();
/* 121:209 */         for (int i = 0; i < scoreChildren.getLength(); i++)
/* 122:    */         {
/* 123:210 */           Node child = scoreChildren.item(i);
/* 124:211 */           if (child.getNodeType() == 1)
/* 125:    */           {
/* 126:212 */             String tagName = ((Element)child).getTagName();
/* 127:213 */             if (tagName.equals("ScoreDistribution"))
/* 128:    */             {
/* 129:214 */               TreeModel.ScoreDistribution newDist = new TreeModel.ScoreDistribution((Element)child, miningSchema, this.m_recordCount);
/* 130:    */               
/* 131:    */ 
/* 132:217 */               this.m_scoreDistributions.add(newDist);
/* 133:    */             }
/* 134:    */           }
/* 135:    */         }
/* 136:224 */         if ((this.m_scoreDistributions.size() > 0) && (this.m_scoreDistributions.size() != miningSchema.getFieldsAsInstances().classAttribute().numValues())) {
/* 137:227 */           throw new Exception("[SimpleRule] Number of score distribution elements is  different than the number of class labels!");
/* 138:    */         }
/* 139:232 */         if (Utils.isMissingValue(this.m_recordCount))
/* 140:    */         {
/* 141:233 */           baseCount = 0.0D;
/* 142:234 */           for (TreeModel.ScoreDistribution s : this.m_scoreDistributions) {
/* 143:235 */             baseCount += s.getRecordCount();
/* 144:    */           }
/* 145:238 */           for (TreeModel.ScoreDistribution s : this.m_scoreDistributions) {
/* 146:239 */             s.deriveConfidenceValue(baseCount);
/* 147:    */           }
/* 148:    */         }
/* 149:    */       }
/* 150:    */     }
/* 151:    */     
/* 152:    */     public void fires(double[] input, ArrayList<SimpleRule> ruleCollection)
/* 153:    */     {
/* 154:252 */       if (this.m_predicate.evaluate(input) == TreeModel.Predicate.Eval.TRUE) {
/* 155:253 */         ruleCollection.add(this);
/* 156:    */       }
/* 157:    */     }
/* 158:    */     
/* 159:    */     public double[] score(double[] instance, Attribute classAtt)
/* 160:    */       throws Exception
/* 161:    */     {
/* 162:    */       double[] preds;
/* 163:272 */       if (classAtt.isNumeric())
/* 164:    */       {
/* 165:273 */         double[] preds = new double[1];
/* 166:274 */         preds[0] = this.m_score;
/* 167:    */       }
/* 168:    */       else
/* 169:    */       {
/* 170:276 */         preds = new double[classAtt.numValues()];
/* 171:277 */         if (this.m_scoreDistributions.size() > 0) {
/* 172:278 */           for (TreeModel.ScoreDistribution s : this.m_scoreDistributions) {
/* 173:279 */             preds[s.getClassLabelIndex()] = s.getConfidence();
/* 174:    */           }
/* 175:281 */         } else if (!Utils.isMissingValue(this.m_confidence)) {
/* 176:282 */           preds[classAtt.indexOfValue(this.m_scoreString)] = this.m_confidence;
/* 177:    */         } else {
/* 178:284 */           preds[classAtt.indexOfValue(this.m_scoreString)] = 1.0D;
/* 179:    */         }
/* 180:    */       }
/* 181:288 */       return preds;
/* 182:    */     }
/* 183:    */     
/* 184:    */     public double getWeight()
/* 185:    */     {
/* 186:297 */       return this.m_weight;
/* 187:    */     }
/* 188:    */     
/* 189:    */     public String getID()
/* 190:    */     {
/* 191:306 */       return this.m_ID;
/* 192:    */     }
/* 193:    */     
/* 194:    */     public double getScore()
/* 195:    */     {
/* 196:317 */       return this.m_score;
/* 197:    */     }
/* 198:    */   }
/* 199:    */   
/* 200:    */   static class CompoundRule
/* 201:    */     extends RuleSetModel.Rule
/* 202:    */   {
/* 203:    */     private static final long serialVersionUID = -2853658811459970718L;
/* 204:330 */     ArrayList<RuleSetModel.Rule> m_childRules = new ArrayList();
/* 205:    */     
/* 206:    */     public String toString(String prefix, int indent)
/* 207:    */     {
/* 208:333 */       StringBuffer temp = new StringBuffer();
/* 209:335 */       for (int i = 0; i < indent; i++) {
/* 210:336 */         prefix = prefix + " ";
/* 211:    */       }
/* 212:339 */       temp.append(prefix + "Compound rule: " + this.m_predicate + "\n");
/* 213:341 */       for (RuleSetModel.Rule r : this.m_childRules) {
/* 214:342 */         temp.append(r.toString(prefix, indent + 1));
/* 215:    */       }
/* 216:345 */       return temp.toString();
/* 217:    */     }
/* 218:    */     
/* 219:    */     public String toString()
/* 220:    */     {
/* 221:349 */       return toString("", 0);
/* 222:    */     }
/* 223:    */     
/* 224:    */     public CompoundRule(Element ruleE, MiningSchema miningSchema)
/* 225:    */       throws Exception
/* 226:    */     {
/* 227:362 */       super(miningSchema);
/* 228:    */       
/* 229:    */ 
/* 230:365 */       NodeList ruleChildren = ruleE.getChildNodes();
/* 231:366 */       for (int i = 0; i < ruleChildren.getLength(); i++)
/* 232:    */       {
/* 233:367 */         Node child = ruleChildren.item(i);
/* 234:368 */         if (child.getNodeType() == 1)
/* 235:    */         {
/* 236:369 */           String tagName = ((Element)child).getTagName();
/* 237:370 */           if (tagName.equals("SimpleRule"))
/* 238:    */           {
/* 239:371 */             RuleSetModel.Rule childRule = new RuleSetModel.SimpleRule((Element)child, miningSchema);
/* 240:372 */             this.m_childRules.add(childRule);
/* 241:    */           }
/* 242:373 */           else if (tagName.equals("CompoundRule"))
/* 243:    */           {
/* 244:374 */             RuleSetModel.Rule childRule = new CompoundRule((Element)child, miningSchema);
/* 245:375 */             this.m_childRules.add(childRule);
/* 246:    */           }
/* 247:    */         }
/* 248:    */       }
/* 249:    */     }
/* 250:    */     
/* 251:    */     public void fires(double[] input, ArrayList<RuleSetModel.SimpleRule> ruleCollection)
/* 252:    */     {
/* 253:390 */       if (this.m_predicate.evaluate(input) == TreeModel.Predicate.Eval.TRUE) {
/* 254:392 */         for (RuleSetModel.Rule r : this.m_childRules) {
/* 255:393 */           r.fires(input, ruleCollection);
/* 256:    */         }
/* 257:    */       }
/* 258:    */     }
/* 259:    */   }
/* 260:    */   
/* 261:    */   static class RuleSet
/* 262:    */     implements Serializable
/* 263:    */   {
/* 264:    */     private static final long serialVersionUID = -8718126887943074376L;
/* 265:    */     
/* 266:    */     static enum RuleSelectionMethod
/* 267:    */     {
/* 268:408 */       WEIGHTEDSUM("weightedSum"),  WEIGHTEDMAX("weightedMax"),  FIRSTHIT("firstHit");
/* 269:    */       
/* 270:    */       private final String m_stringVal;
/* 271:    */       
/* 272:    */       private RuleSelectionMethod(String name)
/* 273:    */       {
/* 274:415 */         this.m_stringVal = name;
/* 275:    */       }
/* 276:    */       
/* 277:    */       public String toString()
/* 278:    */       {
/* 279:419 */         return this.m_stringVal;
/* 280:    */       }
/* 281:    */     }
/* 282:    */     
/* 283:428 */     private double m_recordCount = Utils.missingValue();
/* 284:434 */     private double m_nbCorrect = Utils.missingValue();
/* 285:    */     private String m_defaultScore;
/* 286:446 */     private double m_defaultPrediction = Utils.missingValue();
/* 287:452 */     private ArrayList<TreeModel.ScoreDistribution> m_scoreDistributions = new ArrayList();
/* 288:459 */     private double m_defaultConfidence = Utils.missingValue();
/* 289:    */     private RuleSelectionMethod m_currentMethod;
/* 290:465 */     private ArrayList<RuleSelectionMethod> m_availableRuleSelectionMethods = new ArrayList();
/* 291:469 */     private ArrayList<RuleSetModel.Rule> m_rules = new ArrayList();
/* 292:    */     
/* 293:    */     public String toString()
/* 294:    */     {
/* 295:475 */       StringBuffer temp = new StringBuffer();
/* 296:    */       
/* 297:477 */       temp.append("Rule selection method: " + this.m_currentMethod + "\n");
/* 298:478 */       if (this.m_defaultScore != null)
/* 299:    */       {
/* 300:479 */         temp.append("Default prediction: " + this.m_defaultScore + "\n");
/* 301:481 */         if (!Utils.isMissingValue(this.m_recordCount)) {
/* 302:482 */           temp.append("       recordCount: " + this.m_recordCount + "\n");
/* 303:    */         }
/* 304:484 */         if (!Utils.isMissingValue(this.m_nbCorrect)) {
/* 305:485 */           temp.append("         nbCorrect: " + this.m_nbCorrect + "\n");
/* 306:    */         }
/* 307:487 */         if (!Utils.isMissingValue(this.m_defaultConfidence)) {
/* 308:488 */           temp.append(" defaultConfidence: " + this.m_defaultConfidence + "\n");
/* 309:    */         }
/* 310:491 */         temp.append("\n");
/* 311:    */       }
/* 312:494 */       for (RuleSetModel.Rule r : this.m_rules) {
/* 313:495 */         temp.append(r + "\n");
/* 314:    */       }
/* 315:498 */       return temp.toString();
/* 316:    */     }
/* 317:    */     
/* 318:    */     public RuleSet(Element ruleSetNode, MiningSchema miningSchema)
/* 319:    */       throws Exception
/* 320:    */     {
/* 321:511 */       String recordCount = ruleSetNode.getAttribute("recordCount");
/* 322:512 */       if ((recordCount != null) && (recordCount.length() > 0)) {
/* 323:513 */         this.m_recordCount = Double.parseDouble(recordCount);
/* 324:    */       }
/* 325:516 */       String nbCorrect = ruleSetNode.getAttribute("nbCorrect");
/* 326:517 */       if (((nbCorrect != null ? 1 : 0) & (nbCorrect.length() > 0 ? 1 : 0)) != 0) {
/* 327:518 */         this.m_nbCorrect = Double.parseDouble(nbCorrect);
/* 328:    */       }
/* 329:521 */       String defaultScore = ruleSetNode.getAttribute("defaultScore");
/* 330:522 */       if ((defaultScore != null) && (defaultScore.length() > 0))
/* 331:    */       {
/* 332:523 */         this.m_defaultScore = defaultScore;
/* 333:    */         
/* 334:525 */         Attribute classAtt = miningSchema.getFieldsAsInstances().classAttribute();
/* 335:526 */         if (classAtt == null) {
/* 336:527 */           throw new Exception("[RuleSet] class attribute not set!");
/* 337:    */         }
/* 338:530 */         if (classAtt.isNumeric())
/* 339:    */         {
/* 340:531 */           this.m_defaultPrediction = Double.parseDouble(defaultScore);
/* 341:    */         }
/* 342:    */         else
/* 343:    */         {
/* 344:533 */           if (classAtt.indexOfValue(defaultScore) < 0) {
/* 345:534 */             throw new Exception("[RuleSet] class value " + defaultScore + " not found!");
/* 346:    */           }
/* 347:537 */           this.m_defaultPrediction = classAtt.indexOfValue(defaultScore);
/* 348:    */         }
/* 349:    */       }
/* 350:541 */       String defaultConfidence = ruleSetNode.getAttribute("defaultConfidence");
/* 351:542 */       if ((defaultConfidence != null) && (defaultConfidence.length() > 0)) {
/* 352:543 */         this.m_defaultConfidence = Double.parseDouble(defaultConfidence);
/* 353:    */       }
/* 354:547 */       NodeList selectionNL = ruleSetNode.getElementsByTagName("RuleSelectionMethod");
/* 355:548 */       for (int i = 0; i < selectionNL.getLength(); i++)
/* 356:    */       {
/* 357:549 */         Node selectN = selectionNL.item(i);
/* 358:550 */         if (selectN.getNodeType() == 1)
/* 359:    */         {
/* 360:551 */           Element sN = (Element)selectN;
/* 361:552 */           String criterion = sN.getAttribute("criterion");
/* 362:553 */           for (RuleSelectionMethod m : RuleSelectionMethod.values()) {
/* 363:554 */             if (m.toString().equals(criterion))
/* 364:    */             {
/* 365:555 */               this.m_availableRuleSelectionMethods.add(m);
/* 366:556 */               if (i == 0) {
/* 367:558 */                 this.m_currentMethod = m;
/* 368:    */               }
/* 369:    */             }
/* 370:    */           }
/* 371:    */         }
/* 372:    */       }
/* 373:    */       double baseCount;
/* 374:565 */       if (miningSchema.getFieldsAsInstances().classAttribute().isNominal())
/* 375:    */       {
/* 376:567 */         NodeList scoreChildren = ruleSetNode.getChildNodes();
/* 377:568 */         for (int i = 0; i < scoreChildren.getLength(); i++)
/* 378:    */         {
/* 379:569 */           Node child = scoreChildren.item(i);
/* 380:570 */           if (child.getNodeType() == 1)
/* 381:    */           {
/* 382:571 */             String tagName = ((Element)child).getTagName();
/* 383:572 */             if (tagName.equals("ScoreDistribution"))
/* 384:    */             {
/* 385:573 */               TreeModel.ScoreDistribution newDist = new TreeModel.ScoreDistribution((Element)child, miningSchema, this.m_recordCount);
/* 386:    */               
/* 387:    */ 
/* 388:576 */               this.m_scoreDistributions.add(newDist);
/* 389:    */             }
/* 390:    */           }
/* 391:    */         }
/* 392:582 */         if (Utils.isMissingValue(this.m_recordCount))
/* 393:    */         {
/* 394:583 */           baseCount = 0.0D;
/* 395:584 */           for (TreeModel.ScoreDistribution s : this.m_scoreDistributions) {
/* 396:585 */             baseCount += s.getRecordCount();
/* 397:    */           }
/* 398:588 */           for (TreeModel.ScoreDistribution s : this.m_scoreDistributions) {
/* 399:589 */             s.deriveConfidenceValue(baseCount);
/* 400:    */           }
/* 401:    */         }
/* 402:    */       }
/* 403:595 */       NodeList ruleChildren = ruleSetNode.getChildNodes();
/* 404:596 */       for (int i = 0; i < ruleChildren.getLength(); i++)
/* 405:    */       {
/* 406:597 */         Node child = ruleChildren.item(i);
/* 407:598 */         if (child.getNodeType() == 1)
/* 408:    */         {
/* 409:599 */           String tagName = ((Element)child).getTagName();
/* 410:600 */           if (tagName.equals("SimpleRule"))
/* 411:    */           {
/* 412:601 */             RuleSetModel.Rule tempRule = new RuleSetModel.SimpleRule((Element)child, miningSchema);
/* 413:602 */             this.m_rules.add(tempRule);
/* 414:    */           }
/* 415:603 */           else if (tagName.equals("CompoundRule"))
/* 416:    */           {
/* 417:604 */             RuleSetModel.Rule tempRule = new RuleSetModel.CompoundRule((Element)child, miningSchema);
/* 418:605 */             this.m_rules.add(tempRule);
/* 419:    */           }
/* 420:    */         }
/* 421:    */       }
/* 422:    */     }
/* 423:    */     
/* 424:    */     protected double[] score(double[] instance, Attribute classAtt)
/* 425:    */       throws Exception
/* 426:    */     {
/* 427:622 */       double[] preds = null;
/* 428:623 */       if (classAtt.isNumeric()) {
/* 429:624 */         preds = new double[1];
/* 430:    */       } else {
/* 431:626 */         preds = new double[classAtt.numValues()];
/* 432:    */       }
/* 433:630 */       ArrayList<RuleSetModel.SimpleRule> firingRules = new ArrayList();
/* 434:632 */       for (RuleSetModel.Rule r : this.m_rules) {
/* 435:633 */         r.fires(instance, firingRules);
/* 436:    */       }
/* 437:636 */       if (firingRules.size() > 0)
/* 438:    */       {
/* 439:637 */         if (this.m_currentMethod == RuleSelectionMethod.FIRSTHIT)
/* 440:    */         {
/* 441:638 */           preds = ((RuleSetModel.SimpleRule)firingRules.get(0)).score(instance, classAtt);
/* 442:    */         }
/* 443:639 */         else if (this.m_currentMethod == RuleSelectionMethod.WEIGHTEDMAX)
/* 444:    */         {
/* 445:640 */           double wMax = (-1.0D / 0.0D);
/* 446:641 */           RuleSetModel.SimpleRule best = null;
/* 447:642 */           for (RuleSetModel.SimpleRule s : firingRules)
/* 448:    */           {
/* 449:643 */             if (Utils.isMissingValue(s.getWeight())) {
/* 450:644 */               throw new Exception("[RuleSet] Scoring criterion is WEIGHTEDMAX, but rule " + s.getID() + " does not have a weight defined!");
/* 451:    */             }
/* 452:647 */             if (s.getWeight() > wMax)
/* 453:    */             {
/* 454:648 */               wMax = s.getWeight();
/* 455:649 */               best = s;
/* 456:    */             }
/* 457:    */           }
/* 458:652 */           if (best == null) {
/* 459:653 */             throw new Exception("[RuleSet] Unable to determine the best rule under the WEIGHTEDMAX criterion!");
/* 460:    */           }
/* 461:656 */           preds = best.score(instance, classAtt);
/* 462:    */         }
/* 463:657 */         else if (this.m_currentMethod == RuleSelectionMethod.WEIGHTEDSUM)
/* 464:    */         {
/* 465:658 */           double sumOfWeights = 0.0D;
/* 466:659 */           for (RuleSetModel.SimpleRule s : firingRules)
/* 467:    */           {
/* 468:660 */             if (Utils.isMissingValue(s.getWeight())) {
/* 469:661 */               throw new Exception("[RuleSet] Scoring criterion is WEIGHTEDSUM, but rule " + s.getID() + " does not have a weight defined!");
/* 470:    */             }
/* 471:664 */             if (classAtt.isNumeric())
/* 472:    */             {
/* 473:665 */               sumOfWeights += s.getWeight();
/* 474:666 */               preds[0] += s.getScore() * s.getWeight();
/* 475:    */             }
/* 476:    */             else
/* 477:    */             {
/* 478:668 */               preds[((int)s.getScore())] += s.getWeight();
/* 479:    */             }
/* 480:    */           }
/* 481:671 */           if (classAtt.isNumeric())
/* 482:    */           {
/* 483:672 */             if (sumOfWeights == 0.0D) {
/* 484:673 */               throw new Exception("[RuleSet] Sum of weights is zero!");
/* 485:    */             }
/* 486:675 */             preds[0] /= sumOfWeights;
/* 487:    */           }
/* 488:    */         }
/* 489:    */       }
/* 490:682 */       else if (classAtt.isNumeric()) {
/* 491:683 */         preds[0] = this.m_defaultPrediction;
/* 492:685 */       } else if (this.m_scoreDistributions.size() > 0) {
/* 493:686 */         for (TreeModel.ScoreDistribution s : this.m_scoreDistributions) {
/* 494:687 */           preds[s.getClassLabelIndex()] = s.getConfidence();
/* 495:    */         }
/* 496:689 */       } else if (!Utils.isMissingValue(this.m_defaultConfidence)) {
/* 497:690 */         preds[((int)this.m_defaultPrediction)] = this.m_defaultConfidence;
/* 498:    */       } else {
/* 499:692 */         preds[((int)this.m_defaultPrediction)] = 1.0D;
/* 500:    */       }
/* 501:697 */       return preds;
/* 502:    */     }
/* 503:    */   }
/* 504:    */   
/* 505:702 */   protected TreeModel.MiningFunction m_functionType = TreeModel.MiningFunction.CLASSIFICATION;
/* 506:    */   protected String m_modelName;
/* 507:    */   protected String m_algorithmName;
/* 508:    */   protected RuleSet m_ruleSet;
/* 509:    */   
/* 510:    */   public RuleSetModel(Element model, Instances dataDictionary, MiningSchema miningSchema)
/* 511:    */     throws Exception
/* 512:    */   {
/* 513:724 */     super(dataDictionary, miningSchema);
/* 514:726 */     if (!getPMMLVersion().equals("3.2")) {}
/* 515:730 */     String fn = model.getAttribute("functionName");
/* 516:731 */     if (fn.equals("regression")) {
/* 517:732 */       this.m_functionType = TreeModel.MiningFunction.REGRESSION;
/* 518:    */     }
/* 519:735 */     String modelName = model.getAttribute("modelName");
/* 520:736 */     if ((modelName != null) && (modelName.length() > 0)) {
/* 521:737 */       this.m_modelName = modelName;
/* 522:    */     }
/* 523:740 */     String algoName = model.getAttribute("algorithmName");
/* 524:741 */     if ((algoName != null) && (algoName.length() > 0)) {
/* 525:742 */       this.m_algorithmName = algoName;
/* 526:    */     }
/* 527:745 */     NodeList ruleset = model.getElementsByTagName("RuleSet");
/* 528:746 */     if (ruleset.getLength() == 1)
/* 529:    */     {
/* 530:747 */       Node ruleSetNode = ruleset.item(0);
/* 531:748 */       if (ruleSetNode.getNodeType() == 1) {
/* 532:749 */         this.m_ruleSet = new RuleSet((Element)ruleSetNode, miningSchema);
/* 533:    */       }
/* 534:    */     }
/* 535:    */     else
/* 536:    */     {
/* 537:752 */       throw new Exception("[RuleSetModel] Should only have a single RuleSet!");
/* 538:    */     }
/* 539:    */   }
/* 540:    */   
/* 541:    */   public double[] distributionForInstance(Instance inst)
/* 542:    */     throws Exception
/* 543:    */   {
/* 544:766 */     if (!this.m_initialized) {
/* 545:767 */       mapToMiningSchema(inst.dataset());
/* 546:    */     }
/* 547:769 */     double[] preds = null;
/* 548:771 */     if (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNumeric()) {
/* 549:772 */       preds = new double[1];
/* 550:    */     } else {
/* 551:774 */       preds = new double[this.m_miningSchema.getFieldsAsInstances().classAttribute().numValues()];
/* 552:    */     }
/* 553:777 */     double[] incoming = this.m_fieldsMap.instanceToSchema(inst, this.m_miningSchema);
/* 554:    */     
/* 555:779 */     preds = this.m_ruleSet.score(incoming, this.m_miningSchema.getFieldsAsInstances().classAttribute());
/* 556:782 */     if (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNominal()) {
/* 557:783 */       Utils.normalize(preds);
/* 558:    */     }
/* 559:786 */     return preds;
/* 560:    */   }
/* 561:    */   
/* 562:    */   public String toString()
/* 563:    */   {
/* 564:795 */     StringBuffer temp = new StringBuffer();
/* 565:    */     
/* 566:797 */     temp.append("PMML version " + getPMMLVersion());
/* 567:798 */     if (!getCreatorApplication().equals("?")) {
/* 568:799 */       temp.append("\nApplication: " + getCreatorApplication());
/* 569:    */     }
/* 570:801 */     temp.append("\nPMML Model: RuleSetModel");
/* 571:802 */     temp.append("\n\n");
/* 572:803 */     temp.append(this.m_miningSchema);
/* 573:805 */     if (this.m_algorithmName != null) {
/* 574:806 */       temp.append("\nAlgorithm: " + this.m_algorithmName + "\n");
/* 575:    */     }
/* 576:809 */     temp.append(this.m_ruleSet);
/* 577:    */     
/* 578:811 */     return temp.toString();
/* 579:    */   }
/* 580:    */   
/* 581:    */   public String getRevision()
/* 582:    */   {
/* 583:820 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 584:    */   }
/* 585:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.pmml.consumer.RuleSetModel
 * JD-Core Version:    0.7.0.1
 */