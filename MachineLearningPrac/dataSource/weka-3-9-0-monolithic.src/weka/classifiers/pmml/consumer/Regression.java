/*   1:    */ package weka.classifiers.pmml.consumer;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import org.w3c.dom.Element;
/*   7:    */ import org.w3c.dom.Node;
/*   8:    */ import org.w3c.dom.NodeList;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.Utils;
/*  14:    */ import weka.core.matrix.Maths;
/*  15:    */ import weka.core.pmml.MappingInfo;
/*  16:    */ import weka.core.pmml.MiningSchema;
/*  17:    */ import weka.core.pmml.TargetMetaInfo;
/*  18:    */ import weka.gui.Logger;
/*  19:    */ 
/*  20:    */ public class Regression
/*  21:    */   extends PMMLClassifier
/*  22:    */   implements Serializable
/*  23:    */ {
/*  24:    */   private static final long serialVersionUID = -5551125528409488634L;
/*  25:    */   protected String m_algorithmName;
/*  26:    */   protected RegressionTable[] m_regressionTables;
/*  27:    */   
/*  28:    */   static class RegressionTable
/*  29:    */     implements Serializable
/*  30:    */   {
/*  31:    */     private static final long serialVersionUID = -5259866093996338995L;
/*  32:    */     public static final int REGRESSION = 0;
/*  33:    */     public static final int CLASSIFICATION = 1;
/*  34:    */     
/*  35:    */     static abstract class Predictor
/*  36:    */       implements Serializable
/*  37:    */     {
/*  38:    */       private static final long serialVersionUID = 7043831847273383618L;
/*  39:    */       protected String m_name;
/*  40: 76 */       protected int m_miningSchemaAttIndex = -1;
/*  41: 79 */       protected double m_coefficient = 1.0D;
/*  42:    */       
/*  43:    */       protected Predictor(Element predictor, Instances miningSchema)
/*  44:    */         throws Exception
/*  45:    */       {
/*  46: 89 */         this.m_name = predictor.getAttribute("name");
/*  47: 90 */         for (int i = 0; i < miningSchema.numAttributes(); i++)
/*  48:    */         {
/*  49: 91 */           Attribute temp = miningSchema.attribute(i);
/*  50: 92 */           if (temp.name().equals(this.m_name)) {
/*  51: 93 */             this.m_miningSchemaAttIndex = i;
/*  52:    */           }
/*  53:    */         }
/*  54: 97 */         if (this.m_miningSchemaAttIndex == -1) {
/*  55: 98 */           throw new Exception("[Predictor] unable to find matching attribute for predictor " + this.m_name);
/*  56:    */         }
/*  57:102 */         String coeff = predictor.getAttribute("coefficient");
/*  58:103 */         if (coeff.length() > 0) {
/*  59:104 */           this.m_coefficient = Double.parseDouble(coeff);
/*  60:    */         }
/*  61:    */       }
/*  62:    */       
/*  63:    */       public String toString()
/*  64:    */       {
/*  65:113 */         return Utils.doubleToString(this.m_coefficient, 12, 4) + " * ";
/*  66:    */       }
/*  67:    */       
/*  68:    */       public abstract void add(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2);
/*  69:    */     }
/*  70:    */     
/*  71:    */     protected class NumericPredictor
/*  72:    */       extends Regression.RegressionTable.Predictor
/*  73:    */     {
/*  74:    */       private static final long serialVersionUID = -4335075205696648273L;
/*  75:137 */       protected double m_exponent = 1.0D;
/*  76:    */       
/*  77:    */       protected NumericPredictor(Element predictor, Instances miningSchema)
/*  78:    */         throws Exception
/*  79:    */       {
/*  80:149 */         super(miningSchema);
/*  81:    */         
/*  82:151 */         String exponent = predictor.getAttribute("exponent");
/*  83:152 */         if (exponent.length() > 0) {
/*  84:153 */           this.m_exponent = Double.parseDouble(exponent);
/*  85:    */         }
/*  86:    */       }
/*  87:    */       
/*  88:    */       public String toString()
/*  89:    */       {
/*  90:161 */         String output = super.toString();
/*  91:162 */         output = output + this.m_name;
/*  92:163 */         if ((this.m_exponent > 1.0D) || (this.m_exponent < 1.0D)) {
/*  93:164 */           output = output + "^" + Utils.doubleToString(this.m_exponent, 4);
/*  94:    */         }
/*  95:166 */         return output;
/*  96:    */       }
/*  97:    */       
/*  98:    */       public void add(double[] preds, double[] input)
/*  99:    */       {
/* 100:178 */         if (Regression.RegressionTable.this.m_targetCategory == -1) {
/* 101:179 */           preds[0] += this.m_coefficient * Math.pow(input[this.m_miningSchemaAttIndex], this.m_exponent);
/* 102:    */         } else {
/* 103:181 */           preds[Regression.RegressionTable.this.m_targetCategory] += this.m_coefficient * Math.pow(input[this.m_miningSchemaAttIndex], this.m_exponent);
/* 104:    */         }
/* 105:    */       }
/* 106:    */     }
/* 107:    */     
/* 108:    */     protected class CategoricalPredictor
/* 109:    */       extends Regression.RegressionTable.Predictor
/* 110:    */     {
/* 111:    */       private static final long serialVersionUID = 3077920125549906819L;
/* 112:    */       protected String m_valueName;
/* 113:199 */       protected int m_valueIndex = -1;
/* 114:    */       
/* 115:    */       protected CategoricalPredictor(Element predictor, Instances miningSchema)
/* 116:    */         throws Exception
/* 117:    */       {
/* 118:211 */         super(miningSchema);
/* 119:    */         
/* 120:213 */         String valName = predictor.getAttribute("value");
/* 121:214 */         if (valName.length() == 0) {
/* 122:215 */           throw new Exception("[CategoricalPredictor] attribute value not specified!");
/* 123:    */         }
/* 124:218 */         this.m_valueName = valName;
/* 125:    */         
/* 126:220 */         Attribute att = miningSchema.attribute(this.m_miningSchemaAttIndex);
/* 127:221 */         if (att.isString()) {
/* 128:225 */           att.addStringValue(this.m_valueName);
/* 129:    */         }
/* 130:227 */         this.m_valueIndex = att.indexOfValue(this.m_valueName);
/* 131:234 */         if (this.m_valueIndex == -1) {
/* 132:235 */           throw new Exception("[CategoricalPredictor] unable to find value " + this.m_valueName + " in mining schema attribute " + att.name());
/* 133:    */         }
/* 134:    */       }
/* 135:    */       
/* 136:    */       public String toString()
/* 137:    */       {
/* 138:245 */         String output = super.toString();
/* 139:246 */         output = output + this.m_name + "=" + this.m_valueName;
/* 140:247 */         return output;
/* 141:    */       }
/* 142:    */       
/* 143:    */       public void add(double[] preds, double[] input)
/* 144:    */       {
/* 145:261 */         if (this.m_valueIndex == (int)input[this.m_miningSchemaAttIndex]) {
/* 146:262 */           if (Regression.RegressionTable.this.m_targetCategory == -1) {
/* 147:263 */             preds[0] += this.m_coefficient;
/* 148:    */           } else {
/* 149:265 */             preds[Regression.RegressionTable.this.m_targetCategory] += this.m_coefficient;
/* 150:    */           }
/* 151:    */         }
/* 152:    */       }
/* 153:    */     }
/* 154:    */     
/* 155:    */     protected class PredictorTerm
/* 156:    */       implements Serializable
/* 157:    */     {
/* 158:    */       private static final long serialVersionUID = 5493100145890252757L;
/* 159:280 */       protected double m_coefficient = 1.0D;
/* 160:    */       protected int[] m_indexes;
/* 161:    */       protected String[] m_fieldNames;
/* 162:    */       
/* 163:    */       protected PredictorTerm(Element predictorTerm, Instances miningSchema)
/* 164:    */         throws Exception
/* 165:    */       {
/* 166:299 */         String coeff = predictorTerm.getAttribute("coefficient");
/* 167:300 */         if ((coeff != null) && (coeff.length() > 0)) {
/* 168:    */           try
/* 169:    */           {
/* 170:302 */             this.m_coefficient = Double.parseDouble(coeff);
/* 171:    */           }
/* 172:    */           catch (IllegalArgumentException ex)
/* 173:    */           {
/* 174:304 */             throw new Exception("[PredictorTerm] unable to parse coefficient");
/* 175:    */           }
/* 176:    */         }
/* 177:308 */         NodeList fields = predictorTerm.getElementsByTagName("FieldRef");
/* 178:309 */         if (fields.getLength() > 0)
/* 179:    */         {
/* 180:310 */           this.m_indexes = new int[fields.getLength()];
/* 181:311 */           this.m_fieldNames = new String[fields.getLength()];
/* 182:313 */           for (int i = 0; i < fields.getLength(); i++)
/* 183:    */           {
/* 184:314 */             Node fieldRef = fields.item(i);
/* 185:315 */             if (fieldRef.getNodeType() == 1)
/* 186:    */             {
/* 187:316 */               String fieldName = ((Element)fieldRef).getAttribute("field");
/* 188:317 */               if ((fieldName != null) && (fieldName.length() > 0))
/* 189:    */               {
/* 190:318 */                 boolean found = false;
/* 191:320 */                 for (int j = 0; j < miningSchema.numAttributes(); j++) {
/* 192:321 */                   if (miningSchema.attribute(j).name().equals(fieldName))
/* 193:    */                   {
/* 194:324 */                     if (!miningSchema.attribute(j).isNumeric()) {
/* 195:325 */                       throw new Exception("[PredictorTerm] field is not continuous: " + fieldName);
/* 196:    */                     }
/* 197:328 */                     found = true;
/* 198:329 */                     this.m_indexes[i] = j;
/* 199:330 */                     this.m_fieldNames[i] = fieldName;
/* 200:331 */                     break;
/* 201:    */                   }
/* 202:    */                 }
/* 203:334 */                 if (!found) {
/* 204:335 */                   throw new Exception("[PredictorTerm] Unable to find field " + fieldName + " in mining schema!");
/* 205:    */                 }
/* 206:    */               }
/* 207:    */             }
/* 208:    */           }
/* 209:    */         }
/* 210:    */       }
/* 211:    */       
/* 212:    */       public String toString()
/* 213:    */       {
/* 214:348 */         StringBuffer result = new StringBuffer();
/* 215:349 */         result.append("(" + Utils.doubleToString(this.m_coefficient, 12, 4));
/* 216:350 */         for (int i = 0; i < this.m_fieldNames.length; i++) {
/* 217:351 */           result.append(" * " + this.m_fieldNames[i]);
/* 218:    */         }
/* 219:353 */         result.append(")");
/* 220:354 */         return result.toString();
/* 221:    */       }
/* 222:    */       
/* 223:    */       public void add(double[] preds, double[] input)
/* 224:    */       {
/* 225:366 */         int indx = 0;
/* 226:367 */         if (Regression.RegressionTable.this.m_targetCategory != -1) {
/* 227:368 */           indx = Regression.RegressionTable.this.m_targetCategory;
/* 228:    */         }
/* 229:371 */         double result = this.m_coefficient;
/* 230:372 */         for (int i = 0; i < this.m_indexes.length; i++) {
/* 231:373 */           result *= input[this.m_indexes[i]];
/* 232:    */         }
/* 233:375 */         preds[indx] += result;
/* 234:    */       }
/* 235:    */     }
/* 236:    */     
/* 237:386 */     protected int m_functionType = 0;
/* 238:    */     protected MiningSchema m_miningSchema;
/* 239:392 */     protected double m_intercept = 0.0D;
/* 240:395 */     protected int m_targetCategory = -1;
/* 241:398 */     protected ArrayList<Predictor> m_predictors = new ArrayList();
/* 242:402 */     protected ArrayList<PredictorTerm> m_predictorTerms = new ArrayList();
/* 243:    */     
/* 244:    */     public String toString()
/* 245:    */     {
/* 246:409 */       Instances miningSchema = this.m_miningSchema.getFieldsAsInstances();
/* 247:410 */       StringBuffer temp = new StringBuffer();
/* 248:411 */       temp.append("Regression table:\n");
/* 249:412 */       temp.append(miningSchema.classAttribute().name());
/* 250:413 */       if (this.m_functionType == 1) {
/* 251:414 */         temp.append("=" + miningSchema.classAttribute().value(this.m_targetCategory));
/* 252:    */       }
/* 253:418 */       temp.append(" =\n\n");
/* 254:421 */       for (int i = 0; i < this.m_predictors.size(); i++) {
/* 255:422 */         temp.append(((Predictor)this.m_predictors.get(i)).toString() + " +\n");
/* 256:    */       }
/* 257:426 */       for (int i = 0; i < this.m_predictorTerms.size(); i++) {
/* 258:427 */         temp.append(((PredictorTerm)this.m_predictorTerms.get(i)).toString() + " +\n");
/* 259:    */       }
/* 260:430 */       temp.append(Utils.doubleToString(this.m_intercept, 12, 4));
/* 261:431 */       temp.append("\n\n");
/* 262:    */       
/* 263:433 */       return temp.toString();
/* 264:    */     }
/* 265:    */     
/* 266:    */     protected RegressionTable(Element table, int functionType, MiningSchema mSchema)
/* 267:    */       throws Exception
/* 268:    */     {
/* 269:451 */       this.m_miningSchema = mSchema;
/* 270:452 */       this.m_functionType = functionType;
/* 271:    */       
/* 272:454 */       Instances miningSchema = this.m_miningSchema.getFieldsAsInstances();
/* 273:    */       
/* 274:    */ 
/* 275:457 */       String intercept = table.getAttribute("intercept");
/* 276:458 */       if (intercept.length() > 0) {
/* 277:459 */         this.m_intercept = Double.parseDouble(intercept);
/* 278:    */       }
/* 279:463 */       if (this.m_functionType == 1)
/* 280:    */       {
/* 281:465 */         String targetCat = table.getAttribute("targetCategory");
/* 282:466 */         if (targetCat.length() > 0)
/* 283:    */         {
/* 284:467 */           Attribute classA = miningSchema.classAttribute();
/* 285:468 */           for (int i = 0; i < classA.numValues(); i++) {
/* 286:469 */             if (classA.value(i).equals(targetCat)) {
/* 287:470 */               this.m_targetCategory = i;
/* 288:    */             }
/* 289:    */           }
/* 290:    */         }
/* 291:474 */         if (this.m_targetCategory == -1) {
/* 292:475 */           throw new Exception("[RegressionTable] No target categories defined for classification");
/* 293:    */         }
/* 294:    */       }
/* 295:480 */       NodeList numericPs = table.getElementsByTagName("NumericPredictor");
/* 296:481 */       for (int i = 0; i < numericPs.getLength(); i++)
/* 297:    */       {
/* 298:482 */         Node nP = numericPs.item(i);
/* 299:483 */         if (nP.getNodeType() == 1)
/* 300:    */         {
/* 301:484 */           NumericPredictor numP = new NumericPredictor((Element)nP, miningSchema);
/* 302:485 */           this.m_predictors.add(numP);
/* 303:    */         }
/* 304:    */       }
/* 305:490 */       NodeList categoricalPs = table.getElementsByTagName("CategoricalPredictor");
/* 306:491 */       for (int i = 0; i < categoricalPs.getLength(); i++)
/* 307:    */       {
/* 308:492 */         Node cP = categoricalPs.item(i);
/* 309:493 */         if (cP.getNodeType() == 1)
/* 310:    */         {
/* 311:494 */           CategoricalPredictor catP = new CategoricalPredictor((Element)cP, miningSchema);
/* 312:495 */           this.m_predictors.add(catP);
/* 313:    */         }
/* 314:    */       }
/* 315:500 */       NodeList predictorTerms = table.getElementsByTagName("PredictorTerm");
/* 316:501 */       for (int i = 0; i < predictorTerms.getLength(); i++)
/* 317:    */       {
/* 318:502 */         Node pT = predictorTerms.item(i);
/* 319:503 */         PredictorTerm predT = new PredictorTerm((Element)pT, miningSchema);
/* 320:504 */         this.m_predictorTerms.add(predT);
/* 321:    */       }
/* 322:    */     }
/* 323:    */     
/* 324:    */     public void predict(double[] preds, double[] input)
/* 325:    */     {
/* 326:509 */       if (this.m_targetCategory == -1) {
/* 327:510 */         preds[0] = this.m_intercept;
/* 328:    */       } else {
/* 329:512 */         preds[this.m_targetCategory] = this.m_intercept;
/* 330:    */       }
/* 331:516 */       for (int i = 0; i < this.m_predictors.size(); i++)
/* 332:    */       {
/* 333:517 */         Predictor p = (Predictor)this.m_predictors.get(i);
/* 334:518 */         p.add(preds, input);
/* 335:    */       }
/* 336:522 */       for (int i = 0; i < this.m_predictorTerms.size(); i++)
/* 337:    */       {
/* 338:523 */         PredictorTerm pt = (PredictorTerm)this.m_predictorTerms.get(i);
/* 339:524 */         pt.add(preds, input);
/* 340:    */       }
/* 341:    */     }
/* 342:    */   }
/* 343:    */   
/* 344:    */   static enum Normalization
/* 345:    */   {
/* 346:539 */     NONE,  SIMPLEMAX,  SOFTMAX,  LOGIT,  PROBIT,  CLOGLOG,  EXP,  LOGLOG,  CAUCHIT;
/* 347:    */     
/* 348:    */     private Normalization() {}
/* 349:    */   }
/* 350:    */   
/* 351:543 */   protected Normalization m_normalizationMethod = Normalization.NONE;
/* 352:    */   
/* 353:    */   public Regression(Element model, Instances dataDictionary, MiningSchema miningSchema)
/* 354:    */     throws Exception
/* 355:    */   {
/* 356:555 */     super(dataDictionary, miningSchema);
/* 357:    */     
/* 358:557 */     int functionType = 0;
/* 359:    */     
/* 360:    */ 
/* 361:560 */     String fName = model.getAttribute("functionName");
/* 362:562 */     if (fName.equals("regression")) {
/* 363:563 */       functionType = 0;
/* 364:564 */     } else if (fName.equals("classification")) {
/* 365:565 */       functionType = 1;
/* 366:    */     } else {
/* 367:567 */       throw new Exception("[PMML Regression] Function name not defined in pmml!");
/* 368:    */     }
/* 369:571 */     String algName = model.getAttribute("algorithmName");
/* 370:572 */     if ((algName != null) && (algName.length() > 0)) {
/* 371:573 */       this.m_algorithmName = algName;
/* 372:    */     }
/* 373:577 */     this.m_normalizationMethod = determineNormalization(model);
/* 374:    */     
/* 375:579 */     setUpRegressionTables(model, functionType);
/* 376:    */   }
/* 377:    */   
/* 378:    */   private void setUpRegressionTables(Element model, int functionType)
/* 379:    */     throws Exception
/* 380:    */   {
/* 381:596 */     NodeList tableList = model.getElementsByTagName("RegressionTable");
/* 382:598 */     if (tableList.getLength() == 0) {
/* 383:599 */       throw new Exception("[Regression] no regression tables defined!");
/* 384:    */     }
/* 385:602 */     this.m_regressionTables = new RegressionTable[tableList.getLength()];
/* 386:604 */     for (int i = 0; i < tableList.getLength(); i++)
/* 387:    */     {
/* 388:605 */       Node table = tableList.item(i);
/* 389:606 */       if (table.getNodeType() == 1)
/* 390:    */       {
/* 391:607 */         RegressionTable tempRTable = new RegressionTable((Element)table, functionType, this.m_miningSchema);
/* 392:    */         
/* 393:    */ 
/* 394:    */ 
/* 395:611 */         this.m_regressionTables[i] = tempRTable;
/* 396:    */       }
/* 397:    */     }
/* 398:    */   }
/* 399:    */   
/* 400:    */   private static Normalization determineNormalization(Element model)
/* 401:    */   {
/* 402:624 */     Normalization normMethod = Normalization.NONE;
/* 403:    */     
/* 404:626 */     String normName = model.getAttribute("normalizationMethod");
/* 405:627 */     if (normName.equals("simplemax")) {
/* 406:628 */       normMethod = Normalization.SIMPLEMAX;
/* 407:629 */     } else if (normName.equals("softmax")) {
/* 408:630 */       normMethod = Normalization.SOFTMAX;
/* 409:631 */     } else if (normName.equals("logit")) {
/* 410:632 */       normMethod = Normalization.LOGIT;
/* 411:633 */     } else if (normName.equals("probit")) {
/* 412:634 */       normMethod = Normalization.PROBIT;
/* 413:635 */     } else if (normName.equals("cloglog")) {
/* 414:636 */       normMethod = Normalization.CLOGLOG;
/* 415:637 */     } else if (normName.equals("exp")) {
/* 416:638 */       normMethod = Normalization.EXP;
/* 417:639 */     } else if (normName.equals("loglog")) {
/* 418:640 */       normMethod = Normalization.LOGLOG;
/* 419:641 */     } else if (normName.equals("cauchit")) {
/* 420:642 */       normMethod = Normalization.CAUCHIT;
/* 421:    */     }
/* 422:644 */     return normMethod;
/* 423:    */   }
/* 424:    */   
/* 425:    */   public String toString()
/* 426:    */   {
/* 427:651 */     StringBuffer temp = new StringBuffer();
/* 428:652 */     temp.append("PMML version " + getPMMLVersion());
/* 429:653 */     if (!getCreatorApplication().equals("?")) {
/* 430:654 */       temp.append("\nApplication: " + getCreatorApplication());
/* 431:    */     }
/* 432:656 */     if (this.m_algorithmName != null) {
/* 433:657 */       temp.append("\nPMML Model: " + this.m_algorithmName);
/* 434:    */     }
/* 435:659 */     temp.append("\n\n");
/* 436:660 */     temp.append(this.m_miningSchema);
/* 437:662 */     for (RegressionTable table : this.m_regressionTables) {
/* 438:663 */       temp.append(table);
/* 439:    */     }
/* 440:666 */     if (this.m_normalizationMethod != Normalization.NONE) {
/* 441:667 */       temp.append("Normalization: " + this.m_normalizationMethod);
/* 442:    */     }
/* 443:669 */     temp.append("\n");
/* 444:    */     
/* 445:671 */     return temp.toString();
/* 446:    */   }
/* 447:    */   
/* 448:    */   public double[] distributionForInstance(Instance inst)
/* 449:    */     throws Exception
/* 450:    */   {
/* 451:684 */     if (!this.m_initialized) {
/* 452:685 */       mapToMiningSchema(inst.dataset());
/* 453:    */     }
/* 454:687 */     double[] preds = null;
/* 455:688 */     if (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNumeric()) {
/* 456:689 */       preds = new double[1];
/* 457:    */     } else {
/* 458:691 */       preds = new double[this.m_miningSchema.getFieldsAsInstances().classAttribute().numValues()];
/* 459:    */     }
/* 460:698 */     double[] incoming = this.m_fieldsMap.instanceToSchema(inst, this.m_miningSchema);
/* 461:    */     
/* 462:    */ 
/* 463:    */ 
/* 464:    */ 
/* 465:    */ 
/* 466:    */ 
/* 467:    */ 
/* 468:    */ 
/* 469:    */ 
/* 470:    */ 
/* 471:    */ 
/* 472:    */ 
/* 473:    */ 
/* 474:    */ 
/* 475:    */ 
/* 476:714 */     boolean hasMissing = false;
/* 477:715 */     for (int i = 0; i < incoming.length; i++) {
/* 478:716 */       if ((i != this.m_miningSchema.getFieldsAsInstances().classIndex()) && (Utils.isMissingValue(incoming[i])))
/* 479:    */       {
/* 480:718 */         hasMissing = true;
/* 481:719 */         break;
/* 482:    */       }
/* 483:    */     }
/* 484:723 */     if (hasMissing)
/* 485:    */     {
/* 486:724 */       if (!this.m_miningSchema.hasTargetMetaData())
/* 487:    */       {
/* 488:725 */         String message = "[Regression] WARNING: Instance to predict has missing value(s) but there is no missing value handling meta data and no prior probabilities/default value to fall back to. No prediction will be made (" + ((this.m_miningSchema.getFieldsAsInstances().classAttribute().isNominal()) || (this.m_miningSchema.getFieldsAsInstances().classAttribute().isString()) ? "zero probabilities output)." : "NaN output).");
/* 489:733 */         if (this.m_log == null) {
/* 490:734 */           System.err.println(message);
/* 491:    */         } else {
/* 492:736 */           this.m_log.logMessage(message);
/* 493:    */         }
/* 494:738 */         if (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNumeric()) {
/* 495:739 */           preds[0] = Utils.missingValue();
/* 496:    */         }
/* 497:741 */         return preds;
/* 498:    */       }
/* 499:744 */       TargetMetaInfo targetData = this.m_miningSchema.getTargetMetaData();
/* 500:745 */       if (this.m_miningSchema.getFieldsAsInstances().classAttribute().isNumeric())
/* 501:    */       {
/* 502:746 */         preds[0] = targetData.getDefaultValue();
/* 503:    */       }
/* 504:    */       else
/* 505:    */       {
/* 506:748 */         Instances miningSchemaI = this.m_miningSchema.getFieldsAsInstances();
/* 507:749 */         for (int i = 0; i < miningSchemaI.classAttribute().numValues(); i++) {
/* 508:750 */           preds[i] = targetData.getPriorProbability(miningSchemaI.classAttribute().value(i));
/* 509:    */         }
/* 510:    */       }
/* 511:753 */       return preds;
/* 512:    */     }
/* 513:757 */     for (int i = 0; i < this.m_regressionTables.length; i++) {
/* 514:758 */       this.m_regressionTables[i].predict(preds, incoming);
/* 515:    */     }
/* 516:762 */     switch (1.$SwitchMap$weka$classifiers$pmml$consumer$Regression$Normalization[this.m_normalizationMethod.ordinal()])
/* 517:    */     {
/* 518:    */     case 1: 
/* 519:    */       break;
/* 520:    */     case 2: 
/* 521:767 */       Utils.normalize(preds);
/* 522:768 */       break;
/* 523:    */     case 3: 
/* 524:770 */       for (int i = 0; i < preds.length; i++) {
/* 525:771 */         preds[i] = Math.exp(preds[i]);
/* 526:    */       }
/* 527:773 */       if (preds.length == 1) {
/* 528:776 */         preds[0] /= (preds[0] + 1.0D);
/* 529:    */       } else {
/* 530:778 */         Utils.normalize(preds);
/* 531:    */       }
/* 532:780 */       break;
/* 533:    */     case 4: 
/* 534:782 */       for (int i = 0; i < preds.length; i++) {
/* 535:783 */         preds[i] = (1.0D / (1.0D + Math.exp(-preds[i])));
/* 536:    */       }
/* 537:785 */       Utils.normalize(preds);
/* 538:786 */       break;
/* 539:    */     case 5: 
/* 540:788 */       for (int i = 0; i < preds.length; i++) {
/* 541:789 */         preds[i] = Maths.pnorm(preds[i]);
/* 542:    */       }
/* 543:791 */       Utils.normalize(preds);
/* 544:792 */       break;
/* 545:    */     case 6: 
/* 546:795 */       for (int i = 0; i < preds.length; i++) {
/* 547:796 */         preds[i] = (1.0D - Math.exp(-Math.exp(-preds[i])));
/* 548:    */       }
/* 549:798 */       Utils.normalize(preds);
/* 550:799 */       break;
/* 551:    */     case 7: 
/* 552:801 */       for (int i = 0; i < preds.length; i++) {
/* 553:802 */         preds[i] = Math.exp(preds[i]);
/* 554:    */       }
/* 555:804 */       Utils.normalize(preds);
/* 556:805 */       break;
/* 557:    */     case 8: 
/* 558:808 */       for (int i = 0; i < preds.length; i++) {
/* 559:809 */         preds[i] = Math.exp(-Math.exp(-preds[i]));
/* 560:    */       }
/* 561:811 */       Utils.normalize(preds);
/* 562:812 */       break;
/* 563:    */     case 9: 
/* 564:814 */       for (int i = 0; i < preds.length; i++) {
/* 565:815 */         preds[i] = (0.5D + 0.3183098861837907D * Math.atan(preds[i]));
/* 566:    */       }
/* 567:817 */       Utils.normalize(preds);
/* 568:818 */       break;
/* 569:    */     default: 
/* 570:820 */       throw new Exception("[Regression] unknown normalization method");
/* 571:    */     }
/* 572:825 */     if ((this.m_miningSchema.getFieldsAsInstances().classAttribute().isNumeric()) && (this.m_miningSchema.hasTargetMetaData()))
/* 573:    */     {
/* 574:827 */       TargetMetaInfo targetData = this.m_miningSchema.getTargetMetaData();
/* 575:828 */       preds[0] = targetData.applyMinMaxRescaleCast(preds[0]);
/* 576:    */     }
/* 577:832 */     return preds;
/* 578:    */   }
/* 579:    */   
/* 580:    */   public String getRevision()
/* 581:    */   {
/* 582:839 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 583:    */   }
/* 584:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.pmml.consumer.Regression
 * JD-Core Version:    0.7.0.1
 */