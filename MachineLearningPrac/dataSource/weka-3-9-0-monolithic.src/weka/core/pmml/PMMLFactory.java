/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.io.BufferedInputStream;
/*   4:    */ import java.io.BufferedOutputStream;
/*   5:    */ import java.io.File;
/*   6:    */ import java.io.FileInputStream;
/*   7:    */ import java.io.FileOutputStream;
/*   8:    */ import java.io.InputStream;
/*   9:    */ import java.io.ObjectOutputStream;
/*  10:    */ import java.io.OutputStream;
/*  11:    */ import java.util.ArrayList;
/*  12:    */ import javax.xml.parsers.DocumentBuilder;
/*  13:    */ import javax.xml.parsers.DocumentBuilderFactory;
/*  14:    */ import org.w3c.dom.Document;
/*  15:    */ import org.w3c.dom.Element;
/*  16:    */ import org.w3c.dom.Node;
/*  17:    */ import org.w3c.dom.NodeList;
/*  18:    */ import weka.classifiers.AbstractClassifier;
/*  19:    */ import weka.classifiers.pmml.consumer.GeneralRegression;
/*  20:    */ import weka.classifiers.pmml.consumer.NeuralNetwork;
/*  21:    */ import weka.classifiers.pmml.consumer.PMMLClassifier;
/*  22:    */ import weka.classifiers.pmml.consumer.Regression;
/*  23:    */ import weka.classifiers.pmml.consumer.RuleSetModel;
/*  24:    */ import weka.classifiers.pmml.consumer.SupportVectorMachineModel;
/*  25:    */ import weka.classifiers.pmml.consumer.TreeModel;
/*  26:    */ import weka.core.Attribute;
/*  27:    */ import weka.core.Instance;
/*  28:    */ import weka.core.Instances;
/*  29:    */ import weka.core.RevisionUtils;
/*  30:    */ import weka.core.Utils;
/*  31:    */ import weka.gui.Logger;
/*  32:    */ 
/*  33:    */ public class PMMLFactory
/*  34:    */ {
/*  35:    */   protected static enum ModelType
/*  36:    */   {
/*  37: 67 */     UNKNOWN_MODEL("unknown"),  REGRESSION_MODEL("Regression"),  GENERAL_REGRESSION_MODEL("GeneralRegression"),  NEURAL_NETWORK_MODEL("NeuralNetwork"),  TREE_MODEL("TreeModel"),  RULESET_MODEL("RuleSetModel"),  SVM_MODEL("SupportVectorMachineModel");
/*  38:    */     
/*  39:    */     private final String m_stringVal;
/*  40:    */     
/*  41:    */     private ModelType(String name)
/*  42:    */     {
/*  43: 75 */       this.m_stringVal = name;
/*  44:    */     }
/*  45:    */     
/*  46:    */     public String toString()
/*  47:    */     {
/*  48: 80 */       return this.m_stringVal;
/*  49:    */     }
/*  50:    */   }
/*  51:    */   
/*  52:    */   public static PMMLModel getPMMLModel(String filename)
/*  53:    */     throws Exception
/*  54:    */   {
/*  55: 92 */     return getPMMLModel(filename, null);
/*  56:    */   }
/*  57:    */   
/*  58:    */   public static PMMLModel getPMMLModel(File file)
/*  59:    */     throws Exception
/*  60:    */   {
/*  61:103 */     return getPMMLModel(file, null);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public static PMMLModel getPMMLModel(InputStream stream)
/*  65:    */     throws Exception
/*  66:    */   {
/*  67:114 */     return getPMMLModel(stream, null);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public static PMMLModel getPMMLModel(String filename, Logger log)
/*  71:    */     throws Exception
/*  72:    */   {
/*  73:127 */     return getPMMLModel(new File(filename), log);
/*  74:    */   }
/*  75:    */   
/*  76:    */   public static PMMLModel getPMMLModel(File file, Logger log)
/*  77:    */     throws Exception
/*  78:    */   {
/*  79:139 */     return getPMMLModel(new BufferedInputStream(new FileInputStream(file)), log);
/*  80:    */   }
/*  81:    */   
/*  82:    */   private static boolean isPMML(Document doc)
/*  83:    */   {
/*  84:143 */     NodeList tempL = doc.getElementsByTagName("PMML");
/*  85:144 */     if (tempL.getLength() == 0) {
/*  86:145 */       return false;
/*  87:    */     }
/*  88:148 */     return true;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public static PMMLModel getPMMLModel(InputStream stream, Logger log)
/*  92:    */     throws Exception
/*  93:    */   {
/*  94:161 */     DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
/*  95:162 */     DocumentBuilder db = dbf.newDocumentBuilder();
/*  96:163 */     Document doc = db.parse(stream);
/*  97:164 */     stream.close();
/*  98:165 */     doc.getDocumentElement().normalize();
/*  99:166 */     if (!isPMML(doc)) {
/* 100:167 */       throw new IllegalArgumentException("[PMMLFactory] Source is not a PMML file!!");
/* 101:    */     }
/* 102:174 */     Instances dataDictionary = getDataDictionaryAsInstances(doc);
/* 103:175 */     TransformationDictionary transDict = getTransformationDictionary(doc, dataDictionary);
/* 104:    */     
/* 105:    */ 
/* 106:178 */     ModelType modelType = getModelType(doc);
/* 107:179 */     if (modelType == ModelType.UNKNOWN_MODEL) {
/* 108:180 */       throw new Exception("Unsupported PMML model type");
/* 109:    */     }
/* 110:182 */     Element model = getModelElement(doc, modelType);
/* 111:    */     
/* 112:    */ 
/* 113:185 */     MiningSchema ms = new MiningSchema(model, dataDictionary, transDict);
/* 114:    */     
/* 115:    */ 
/* 116:    */ 
/* 117:    */ 
/* 118:    */ 
/* 119:191 */     PMMLModel theModel = getModelInstance(doc, modelType, model, dataDictionary, ms);
/* 120:193 */     if (log != null) {
/* 121:194 */       theModel.setLog(log);
/* 122:    */     }
/* 123:196 */     return theModel;
/* 124:    */   }
/* 125:    */   
/* 126:    */   protected static TransformationDictionary getTransformationDictionary(Document doc, Instances dataDictionary)
/* 127:    */     throws Exception
/* 128:    */   {
/* 129:211 */     TransformationDictionary transDict = null;
/* 130:    */     
/* 131:213 */     NodeList transL = doc.getElementsByTagName("TransformationDictionary");
/* 132:215 */     if (transL.getLength() > 0)
/* 133:    */     {
/* 134:216 */       Node transNode = transL.item(0);
/* 135:217 */       if (transNode.getNodeType() == 1) {
/* 136:218 */         transDict = new TransformationDictionary((Element)transNode, dataDictionary);
/* 137:    */       }
/* 138:    */     }
/* 139:223 */     return transDict;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public static void serializePMMLModel(PMMLModel model, String filename)
/* 143:    */     throws Exception
/* 144:    */   {
/* 145:235 */     serializePMMLModel(model, new File(filename));
/* 146:    */   }
/* 147:    */   
/* 148:    */   public static void serializePMMLModel(PMMLModel model, File file)
/* 149:    */     throws Exception
/* 150:    */   {
/* 151:247 */     serializePMMLModel(model, new BufferedOutputStream(new FileOutputStream(file)));
/* 152:    */   }
/* 153:    */   
/* 154:    */   public static void serializePMMLModel(PMMLModel model, OutputStream stream)
/* 155:    */     throws Exception
/* 156:    */   {
/* 157:260 */     ObjectOutputStream oo = new ObjectOutputStream(stream);
/* 158:261 */     Instances header = model.getMiningSchema().getFieldsAsInstances();
/* 159:262 */     oo.writeObject(header);
/* 160:263 */     oo.writeObject(model);
/* 161:264 */     oo.flush();
/* 162:265 */     oo.close();
/* 163:    */   }
/* 164:    */   
/* 165:    */   protected static PMMLModel getModelInstance(Document doc, ModelType modelType, Element model, Instances dataDictionary, MiningSchema miningSchema)
/* 166:    */     throws Exception
/* 167:    */   {
/* 168:283 */     PMMLModel pmmlM = null;
/* 169:284 */     switch (1.$SwitchMap$weka$core$pmml$PMMLFactory$ModelType[modelType.ordinal()])
/* 170:    */     {
/* 171:    */     case 1: 
/* 172:286 */       pmmlM = new Regression(model, dataDictionary, miningSchema);
/* 173:    */       
/* 174:288 */       break;
/* 175:    */     case 2: 
/* 176:290 */       pmmlM = new GeneralRegression(model, dataDictionary, miningSchema);
/* 177:    */       
/* 178:292 */       break;
/* 179:    */     case 3: 
/* 180:294 */       pmmlM = new NeuralNetwork(model, dataDictionary, miningSchema);
/* 181:295 */       break;
/* 182:    */     case 4: 
/* 183:297 */       pmmlM = new TreeModel(model, dataDictionary, miningSchema);
/* 184:298 */       break;
/* 185:    */     case 5: 
/* 186:300 */       pmmlM = new RuleSetModel(model, dataDictionary, miningSchema);
/* 187:301 */       break;
/* 188:    */     case 6: 
/* 189:303 */       pmmlM = new SupportVectorMachineModel(model, dataDictionary, miningSchema);
/* 190:304 */       break;
/* 191:    */     default: 
/* 192:306 */       throw new Exception("[PMMLFactory] Unknown model type!!");
/* 193:    */     }
/* 194:308 */     pmmlM.setPMMLVersion(doc);
/* 195:309 */     pmmlM.setCreatorApplication(doc);
/* 196:310 */     return pmmlM;
/* 197:    */   }
/* 198:    */   
/* 199:    */   protected static ModelType getModelType(Document doc)
/* 200:    */   {
/* 201:320 */     NodeList temp = doc.getElementsByTagName("RegressionModel");
/* 202:321 */     if (temp.getLength() > 0) {
/* 203:322 */       return ModelType.REGRESSION_MODEL;
/* 204:    */     }
/* 205:325 */     temp = doc.getElementsByTagName("GeneralRegressionModel");
/* 206:326 */     if (temp.getLength() > 0) {
/* 207:327 */       return ModelType.GENERAL_REGRESSION_MODEL;
/* 208:    */     }
/* 209:330 */     temp = doc.getElementsByTagName("NeuralNetwork");
/* 210:331 */     if (temp.getLength() > 0) {
/* 211:332 */       return ModelType.NEURAL_NETWORK_MODEL;
/* 212:    */     }
/* 213:335 */     temp = doc.getElementsByTagName("TreeModel");
/* 214:336 */     if (temp.getLength() > 0) {
/* 215:337 */       return ModelType.TREE_MODEL;
/* 216:    */     }
/* 217:340 */     temp = doc.getElementsByTagName("RuleSetModel");
/* 218:341 */     if (temp.getLength() > 0) {
/* 219:342 */       return ModelType.RULESET_MODEL;
/* 220:    */     }
/* 221:345 */     temp = doc.getElementsByTagName("SupportVectorMachineModel");
/* 222:346 */     if (temp.getLength() > 0) {
/* 223:347 */       return ModelType.SVM_MODEL;
/* 224:    */     }
/* 225:350 */     return ModelType.UNKNOWN_MODEL;
/* 226:    */   }
/* 227:    */   
/* 228:    */   protected static Element getModelElement(Document doc, ModelType modelType)
/* 229:    */     throws Exception
/* 230:    */   {
/* 231:362 */     NodeList temp = null;
/* 232:363 */     Element model = null;
/* 233:364 */     switch (1.$SwitchMap$weka$core$pmml$PMMLFactory$ModelType[modelType.ordinal()])
/* 234:    */     {
/* 235:    */     case 1: 
/* 236:366 */       temp = doc.getElementsByTagName("RegressionModel");
/* 237:367 */       break;
/* 238:    */     case 2: 
/* 239:369 */       temp = doc.getElementsByTagName("GeneralRegressionModel");
/* 240:370 */       break;
/* 241:    */     case 3: 
/* 242:372 */       temp = doc.getElementsByTagName("NeuralNetwork");
/* 243:373 */       break;
/* 244:    */     case 4: 
/* 245:375 */       temp = doc.getElementsByTagName("TreeModel");
/* 246:376 */       break;
/* 247:    */     case 5: 
/* 248:378 */       temp = doc.getElementsByTagName("RuleSetModel");
/* 249:379 */       break;
/* 250:    */     case 6: 
/* 251:381 */       temp = doc.getElementsByTagName("SupportVectorMachineModel");
/* 252:382 */       break;
/* 253:    */     default: 
/* 254:384 */       throw new Exception("[PMMLFactory] unknown/unsupported model type.");
/* 255:    */     }
/* 256:387 */     if ((temp != null) && (temp.getLength() > 0))
/* 257:    */     {
/* 258:388 */       Node modelNode = temp.item(0);
/* 259:389 */       if (modelNode.getNodeType() == 1) {
/* 260:390 */         model = (Element)modelNode;
/* 261:    */       }
/* 262:    */     }
/* 263:394 */     return model;
/* 264:    */   }
/* 265:    */   
/* 266:    */   @Deprecated
/* 267:    */   protected static Instances getMiningSchemaAsInstances(Element model, Instances dataDictionary)
/* 268:    */     throws Exception
/* 269:    */   {
/* 270:409 */     ArrayList<Attribute> attInfo = new ArrayList();
/* 271:410 */     NodeList fieldList = model.getElementsByTagName("MiningField");
/* 272:411 */     int classIndex = -1;
/* 273:412 */     int addedCount = 0;
/* 274:413 */     for (int i = 0; i < fieldList.getLength(); i++)
/* 275:    */     {
/* 276:414 */       Node miningField = fieldList.item(i);
/* 277:415 */       if (miningField.getNodeType() == 1)
/* 278:    */       {
/* 279:416 */         Element miningFieldEl = (Element)miningField;
/* 280:417 */         String name = miningFieldEl.getAttribute("name");
/* 281:418 */         String usage = miningFieldEl.getAttribute("usageType");
/* 282:    */         
/* 283:    */ 
/* 284:    */ 
/* 285:422 */         Attribute miningAtt = dataDictionary.attribute(name);
/* 286:423 */         if (miningAtt != null)
/* 287:    */         {
/* 288:424 */           if ((usage.length() == 0) || (usage.equals("active")) || (usage.equals("predicted")))
/* 289:    */           {
/* 290:426 */             attInfo.add(miningAtt);
/* 291:427 */             addedCount++;
/* 292:    */           }
/* 293:429 */           if (usage.equals("predicted")) {
/* 294:430 */             classIndex = addedCount - 1;
/* 295:    */           }
/* 296:    */         }
/* 297:    */         else
/* 298:    */         {
/* 299:433 */           throw new Exception("Can't find mining field: " + name + " in the data dictionary.");
/* 300:    */         }
/* 301:    */       }
/* 302:    */     }
/* 303:439 */     Instances insts = new Instances("miningSchema", attInfo, 0);
/* 304:441 */     if (classIndex != -1) {
/* 305:442 */       insts.setClassIndex(classIndex);
/* 306:    */     }
/* 307:445 */     return insts;
/* 308:    */   }
/* 309:    */   
/* 310:    */   protected static Instances getDataDictionaryAsInstances(Document doc)
/* 311:    */     throws Exception
/* 312:    */   {
/* 313:461 */     ArrayList<Attribute> attInfo = new ArrayList();
/* 314:462 */     NodeList dataDictionary = doc.getElementsByTagName("DataField");
/* 315:463 */     for (int i = 0; i < dataDictionary.getLength(); i++)
/* 316:    */     {
/* 317:464 */       Node dataField = dataDictionary.item(i);
/* 318:465 */       if (dataField.getNodeType() == 1)
/* 319:    */       {
/* 320:466 */         Element dataFieldEl = (Element)dataField;
/* 321:467 */         String name = dataFieldEl.getAttribute("name");
/* 322:468 */         String type = dataFieldEl.getAttribute("optype");
/* 323:469 */         Attribute tempAtt = null;
/* 324:470 */         if ((name != null) && (type != null))
/* 325:    */         {
/* 326:471 */           if (type.equals("continuous"))
/* 327:    */           {
/* 328:472 */             tempAtt = new Attribute(name);
/* 329:    */           }
/* 330:473 */           else if ((type.equals("categorical")) || (type.equals("ordinal")))
/* 331:    */           {
/* 332:474 */             NodeList valueList = dataFieldEl.getElementsByTagName("Value");
/* 333:475 */             if ((valueList == null) || (valueList.getLength() == 0))
/* 334:    */             {
/* 335:479 */               ArrayList<String> nullV = null;
/* 336:480 */               tempAtt = new Attribute(name, nullV);
/* 337:    */             }
/* 338:    */             else
/* 339:    */             {
/* 340:483 */               ArrayList<String> valueVector = new ArrayList();
/* 341:484 */               for (int j = 0; j < valueList.getLength(); j++)
/* 342:    */               {
/* 343:485 */                 Node val = valueList.item(j);
/* 344:486 */                 if (val.getNodeType() == 1)
/* 345:    */                 {
/* 346:488 */                   String property = ((Element)val).getAttribute("property");
/* 347:489 */                   if ((property == null) || (property.length() == 0) || (property.equals("valid")))
/* 348:    */                   {
/* 349:491 */                     String value = ((Element)val).getAttribute("value");
/* 350:492 */                     valueVector.add(value);
/* 351:    */                   }
/* 352:    */                 }
/* 353:    */               }
/* 354:501 */               tempAtt = new Attribute(name, valueVector);
/* 355:    */             }
/* 356:    */           }
/* 357:    */           else
/* 358:    */           {
/* 359:504 */             throw new Exception("[PMMLFactory] can't handle " + type + "attributes.");
/* 360:    */           }
/* 361:507 */           attInfo.add(tempAtt);
/* 362:    */         }
/* 363:    */       }
/* 364:    */     }
/* 365:517 */     Instances insts = new Instances("dataDictionary", attInfo, 0);
/* 366:    */     
/* 367:    */ 
/* 368:520 */     return insts;
/* 369:    */   }
/* 370:    */   
/* 371:    */   public static String applyClassifier(PMMLModel model, Instances test)
/* 372:    */     throws Exception
/* 373:    */   {
/* 374:525 */     StringBuffer buff = new StringBuffer();
/* 375:526 */     if (!(model instanceof PMMLClassifier)) {
/* 376:527 */       throw new Exception("PMML model is not a classifier!");
/* 377:    */     }
/* 378:530 */     double[] preds = null;
/* 379:531 */     PMMLClassifier classifier = (PMMLClassifier)model;
/* 380:532 */     for (int i = 0; i < test.numInstances(); i++)
/* 381:    */     {
/* 382:533 */       buff.append("Actual: ");
/* 383:534 */       Instance temp = test.instance(i);
/* 384:535 */       if (temp.classAttribute().isNumeric()) {
/* 385:536 */         buff.append(temp.value(temp.classIndex()) + " ");
/* 386:    */       } else {
/* 387:538 */         buff.append(temp.classAttribute().value((int)temp.value(temp.classIndex())) + " ");
/* 388:    */       }
/* 389:542 */       preds = classifier.distributionForInstance(temp);
/* 390:543 */       buff.append(" Predicted: ");
/* 391:544 */       for (double pred : preds) {
/* 392:545 */         buff.append("" + pred + " ");
/* 393:    */       }
/* 394:547 */       buff.append("\n");
/* 395:    */     }
/* 396:549 */     return buff.toString();
/* 397:    */   }
/* 398:    */   
/* 399:    */   private static class PMMLClassifierRunner
/* 400:    */     extends AbstractClassifier
/* 401:    */   {
/* 402:    */     private static final long serialVersionUID = -3742334356788083347L;
/* 403:    */     
/* 404:    */     public double[] distributionForInstance(Instance test)
/* 405:    */       throws Exception
/* 406:    */     {
/* 407:559 */       throw new Exception("Don't call this method!!");
/* 408:    */     }
/* 409:    */     
/* 410:    */     public void buildClassifier(Instances instances)
/* 411:    */       throws Exception
/* 412:    */     {
/* 413:564 */       throw new Exception("Don't call this method!!");
/* 414:    */     }
/* 415:    */     
/* 416:    */     public String getRevision()
/* 417:    */     {
/* 418:569 */       return RevisionUtils.extract("$Revision: 10203 $");
/* 419:    */     }
/* 420:    */     
/* 421:    */     public void evaluatePMMLClassifier(String[] options)
/* 422:    */     {
/* 423:573 */       runClassifier(this, options);
/* 424:    */     }
/* 425:    */   }
/* 426:    */   
/* 427:    */   public static void main(String[] args)
/* 428:    */   {
/* 429:    */     try
/* 430:    */     {
/* 431:579 */       String[] optionsTmp = new String[args.length];
/* 432:580 */       for (int i = 0; i < args.length; i++) {
/* 433:581 */         optionsTmp[i] = args[i];
/* 434:    */       }
/* 435:583 */       String pmmlFile = Utils.getOption('l', optionsTmp);
/* 436:584 */       if (pmmlFile.length() == 0) {
/* 437:585 */         throw new Exception("[PMMLFactory] must specify a PMML file using the -l option.");
/* 438:    */       }
/* 439:589 */       getPMMLModel(pmmlFile, null);
/* 440:    */       
/* 441:591 */       PMMLClassifierRunner pcr = new PMMLClassifierRunner(null);
/* 442:592 */       pcr.evaluatePMMLClassifier(args);
/* 443:    */     }
/* 444:    */     catch (Exception ex)
/* 445:    */     {
/* 446:609 */       ex.printStackTrace();
/* 447:    */     }
/* 448:    */   }
/* 449:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.PMMLFactory
 * JD-Core Version:    0.7.0.1
 */