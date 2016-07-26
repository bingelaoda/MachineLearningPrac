/*   1:    */ package weka.classifiers.bayes.net;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.io.StringReader;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.StringTokenizer;
/*   8:    */ import javax.xml.parsers.DocumentBuilder;
/*   9:    */ import javax.xml.parsers.DocumentBuilderFactory;
/*  10:    */ import org.w3c.dom.CharacterData;
/*  11:    */ import org.w3c.dom.Document;
/*  12:    */ import org.w3c.dom.Element;
/*  13:    */ import org.w3c.dom.Node;
/*  14:    */ import org.w3c.dom.NodeList;
/*  15:    */ import org.xml.sax.InputSource;
/*  16:    */ import weka.classifiers.bayes.BayesNet;
/*  17:    */ import weka.classifiers.bayes.net.estimate.DiscreteEstimatorBayes;
/*  18:    */ import weka.core.Attribute;
/*  19:    */ import weka.core.Instances;
/*  20:    */ import weka.core.RevisionUtils;
/*  21:    */ import weka.core.TechnicalInformation;
/*  22:    */ import weka.core.TechnicalInformation.Field;
/*  23:    */ import weka.core.TechnicalInformation.Type;
/*  24:    */ import weka.core.TechnicalInformationHandler;
/*  25:    */ import weka.estimators.Estimator;
/*  26:    */ 
/*  27:    */ public class BIFReader
/*  28:    */   extends BayesNet
/*  29:    */   implements TechnicalInformationHandler
/*  30:    */ {
/*  31:    */   protected int[] m_nPositionX;
/*  32:    */   protected int[] m_nPositionY;
/*  33:    */   private int[] m_order;
/*  34:    */   static final long serialVersionUID = -8358864680379881429L;
/*  35:    */   String m_sFile;
/*  36:    */   
/*  37:    */   public String globalInfo()
/*  38:    */   {
/*  39:116 */     return "Builds a description of a Bayes Net classifier stored in XML BIF 0.3 format.\n\nFor more details on XML BIF see:\n\n" + getTechnicalInformation().toString();
/*  40:    */   }
/*  41:    */   
/*  42:    */   public BIFReader processFile(String sFile)
/*  43:    */     throws Exception
/*  44:    */   {
/*  45:129 */     this.m_sFile = sFile;
/*  46:130 */     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
/*  47:131 */     factory.setValidating(true);
/*  48:132 */     Document doc = factory.newDocumentBuilder().parse(new File(sFile));
/*  49:133 */     doc.normalize();
/*  50:    */     
/*  51:135 */     buildInstances(doc, sFile);
/*  52:136 */     buildStructure(doc);
/*  53:137 */     return this;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public BIFReader processString(String sStr)
/*  57:    */     throws Exception
/*  58:    */   {
/*  59:141 */     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
/*  60:142 */     factory.setValidating(true);
/*  61:143 */     Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(sStr)));
/*  62:    */     
/*  63:145 */     doc.normalize();
/*  64:146 */     buildInstances(doc, "from-string");
/*  65:147 */     buildStructure(doc);
/*  66:148 */     return this;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public String getFileName()
/*  70:    */   {
/*  71:160 */     return this.m_sFile;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public TechnicalInformation getTechnicalInformation()
/*  75:    */   {
/*  76:174 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.MISC);
/*  77:175 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Fabio Cozman and Marek Druzdzel and Daniel Garcia");
/*  78:    */     
/*  79:177 */     result.setValue(TechnicalInformation.Field.YEAR, "1998");
/*  80:178 */     result.setValue(TechnicalInformation.Field.TITLE, "XML BIF version 0.3");
/*  81:179 */     result.setValue(TechnicalInformation.Field.URL, "http://www-2.cs.cmu.edu/~fgcozman/Research/InterchangeFormat/");
/*  82:    */     
/*  83:    */ 
/*  84:182 */     return result;
/*  85:    */   }
/*  86:    */   
/*  87:    */   void buildStructure(Document doc)
/*  88:    */     throws Exception
/*  89:    */   {
/*  90:196 */     this.m_Distributions = new Estimator[this.m_Instances.numAttributes()][];
/*  91:197 */     for (int iNode = 0; iNode < this.m_Instances.numAttributes(); iNode++)
/*  92:    */     {
/*  93:199 */       String sName = this.m_Instances.attribute(iNode).name();
/*  94:200 */       Element definition = getDefinition(doc, sName);
/*  95:    */       
/*  96:    */ 
/*  97:    */ 
/*  98:    */ 
/*  99:    */ 
/* 100:    */ 
/* 101:    */ 
/* 102:    */ 
/* 103:    */ 
/* 104:    */ 
/* 105:    */ 
/* 106:212 */       ArrayList<Node> nodelist = getParentNodes(definition);
/* 107:213 */       for (int iParent = 0; iParent < nodelist.size(); iParent++)
/* 108:    */       {
/* 109:214 */         Node parentName = ((Node)nodelist.get(iParent)).getFirstChild();
/* 110:215 */         String sParentName = ((CharacterData)parentName).getData();
/* 111:216 */         int nParent = getNode(sParentName);
/* 112:217 */         this.m_ParentSets[iNode].addParent(nParent, this.m_Instances);
/* 113:    */       }
/* 114:220 */       int nCardinality = this.m_ParentSets[iNode].getCardinalityOfParents();
/* 115:221 */       int nValues = this.m_Instances.attribute(iNode).numValues();
/* 116:222 */       this.m_Distributions[iNode] = new Estimator[nCardinality];
/* 117:223 */       for (int i = 0; i < nCardinality; i++) {
/* 118:224 */         this.m_Distributions[iNode][i] = new DiscreteEstimatorBayes(nValues, 0.0D);
/* 119:    */       }
/* 120:233 */       String sTable = getTable(definition);
/* 121:234 */       StringTokenizer st = new StringTokenizer(sTable.toString());
/* 122:236 */       for (int i = 0; i < nCardinality; i++)
/* 123:    */       {
/* 124:237 */         DiscreteEstimatorBayes d = (DiscreteEstimatorBayes)this.m_Distributions[iNode][i];
/* 125:238 */         for (int iValue = 0; iValue < nValues; iValue++)
/* 126:    */         {
/* 127:239 */           String sWeight = st.nextToken();
/* 128:240 */           d.addValue(iValue, new Double(sWeight).doubleValue());
/* 129:    */         }
/* 130:    */       }
/* 131:    */     }
/* 132:    */   }
/* 133:    */   
/* 134:    */   public void Sync(BayesNet other)
/* 135:    */     throws Exception
/* 136:    */   {
/* 137:255 */     int nAtts = this.m_Instances.numAttributes();
/* 138:256 */     if (nAtts != other.m_Instances.numAttributes()) {
/* 139:257 */       throw new Exception("Cannot synchronize networks: different number of attributes.");
/* 140:    */     }
/* 141:260 */     this.m_order = new int[nAtts];
/* 142:261 */     for (int iNode = 0; iNode < nAtts; iNode++)
/* 143:    */     {
/* 144:262 */       String sName = other.getNodeName(iNode);
/* 145:263 */       this.m_order[getNode(sName)] = iNode;
/* 146:    */     }
/* 147:    */   }
/* 148:    */   
/* 149:    */   public String getContent(Element node)
/* 150:    */   {
/* 151:280 */     String result = "";
/* 152:281 */     NodeList list = node.getChildNodes();
/* 153:283 */     for (int i = 0; i < list.getLength(); i++)
/* 154:    */     {
/* 155:284 */       Node item = list.item(i);
/* 156:285 */       if (item.getNodeType() == 3) {
/* 157:286 */         result = result + "\n" + item.getNodeValue();
/* 158:    */       }
/* 159:    */     }
/* 160:290 */     return result;
/* 161:    */   }
/* 162:    */   
/* 163:    */   void buildInstances(Document doc, String sName)
/* 164:    */     throws Exception
/* 165:    */   {
/* 166:306 */     NodeList nodelist = selectAllNames(doc);
/* 167:307 */     if (nodelist.getLength() > 0) {
/* 168:308 */       sName = ((CharacterData)nodelist.item(0).getFirstChild()).getData();
/* 169:    */     }
/* 170:312 */     nodelist = selectAllVariables(doc);
/* 171:313 */     int nNodes = nodelist.getLength();
/* 172:    */     
/* 173:315 */     ArrayList<Attribute> attInfo = new ArrayList(nNodes);
/* 174:    */     
/* 175:    */ 
/* 176:318 */     this.m_nPositionX = new int[nodelist.getLength()];
/* 177:319 */     this.m_nPositionY = new int[nodelist.getLength()];
/* 178:322 */     for (int iNode = 0; iNode < nodelist.getLength(); iNode++)
/* 179:    */     {
/* 180:326 */       ArrayList<Node> valueslist = selectOutCome(nodelist.item(iNode));
/* 181:    */       
/* 182:328 */       int nValues = valueslist.size();
/* 183:    */       
/* 184:330 */       ArrayList<String> nomStrings = new ArrayList(nValues + 1);
/* 185:331 */       for (int iValue = 0; iValue < nValues; iValue++)
/* 186:    */       {
/* 187:332 */         Node node = ((Node)valueslist.get(iValue)).getFirstChild();
/* 188:333 */         String sValue = ((CharacterData)node).getData();
/* 189:334 */         if (sValue == null) {
/* 190:335 */           sValue = "Value" + (iValue + 1);
/* 191:    */         }
/* 192:337 */         nomStrings.add(sValue);
/* 193:    */       }
/* 194:341 */       ArrayList<Node> nodelist2 = selectName(nodelist.item(iNode));
/* 195:342 */       if (nodelist2.size() == 0) {
/* 196:343 */         throw new Exception("No name specified for variable");
/* 197:    */       }
/* 198:345 */       String sNodeName = ((CharacterData)((Node)nodelist2.get(0)).getFirstChild()).getData();
/* 199:    */       
/* 200:    */ 
/* 201:348 */       Attribute att = new Attribute(sNodeName, nomStrings);
/* 202:349 */       attInfo.add(att);
/* 203:    */       
/* 204:351 */       valueslist = selectProperty(nodelist.item(iNode));
/* 205:352 */       nValues = valueslist.size();
/* 206:354 */       for (int iValue = 0; iValue < nValues; iValue++)
/* 207:    */       {
/* 208:356 */         Node node = ((Node)valueslist.get(iValue)).getFirstChild();
/* 209:357 */         String sValue = ((CharacterData)node).getData();
/* 210:358 */         if (sValue.startsWith("position"))
/* 211:    */         {
/* 212:359 */           int i0 = sValue.indexOf('(');
/* 213:360 */           int i1 = sValue.indexOf(',');
/* 214:361 */           int i2 = sValue.indexOf(')');
/* 215:362 */           String sX = sValue.substring(i0 + 1, i1).trim();
/* 216:363 */           String sY = sValue.substring(i1 + 1, i2).trim();
/* 217:    */           try
/* 218:    */           {
/* 219:365 */             this.m_nPositionX[iNode] = Integer.parseInt(sX);
/* 220:366 */             this.m_nPositionY[iNode] = Integer.parseInt(sY);
/* 221:    */           }
/* 222:    */           catch (NumberFormatException e)
/* 223:    */           {
/* 224:368 */             System.err.println("Wrong number format in position :(" + sX + "," + sY + ")");
/* 225:    */             
/* 226:370 */             this.m_nPositionX[iNode] = 0;
/* 227:371 */             this.m_nPositionY[iNode] = 0;
/* 228:    */           }
/* 229:    */         }
/* 230:    */       }
/* 231:    */     }
/* 232:378 */     this.m_Instances = new Instances(sName, attInfo, 100);
/* 233:379 */     this.m_Instances.setClassIndex(nNodes - 1);
/* 234:380 */     setUseADTree(false);
/* 235:381 */     initStructure();
/* 236:    */   }
/* 237:    */   
/* 238:    */   NodeList selectAllNames(Document doc)
/* 239:    */     throws Exception
/* 240:    */   {
/* 241:398 */     NodeList nodelist = doc.getElementsByTagName("NAME");
/* 242:399 */     return nodelist;
/* 243:    */   }
/* 244:    */   
/* 245:    */   NodeList selectAllVariables(Document doc)
/* 246:    */     throws Exception
/* 247:    */   {
/* 248:404 */     NodeList nodelist = doc.getElementsByTagName("VARIABLE");
/* 249:405 */     return nodelist;
/* 250:    */   }
/* 251:    */   
/* 252:    */   Element getDefinition(Document doc, String sName)
/* 253:    */     throws Exception
/* 254:    */   {
/* 255:412 */     NodeList nodelist = doc.getElementsByTagName("DEFINITION");
/* 256:413 */     for (int iNode = 0; iNode < nodelist.getLength(); iNode++)
/* 257:    */     {
/* 258:414 */       Node node = nodelist.item(iNode);
/* 259:415 */       ArrayList<Node> list = selectElements(node, "FOR");
/* 260:416 */       if (list.size() > 0)
/* 261:    */       {
/* 262:417 */         Node forNode = (Node)list.get(0);
/* 263:418 */         if (getContent((Element)forNode).trim().equals(sName)) {
/* 264:419 */           return (Element)node;
/* 265:    */         }
/* 266:    */       }
/* 267:    */     }
/* 268:423 */     throw new Exception("Could not find definition for ((" + sName + "))");
/* 269:    */   }
/* 270:    */   
/* 271:    */   ArrayList<Node> getParentNodes(Node definition)
/* 272:    */     throws Exception
/* 273:    */   {
/* 274:428 */     ArrayList<Node> nodelist = selectElements(definition, "GIVEN");
/* 275:429 */     return nodelist;
/* 276:    */   }
/* 277:    */   
/* 278:    */   String getTable(Node definition)
/* 279:    */     throws Exception
/* 280:    */   {
/* 281:434 */     ArrayList<Node> nodelist = selectElements(definition, "TABLE");
/* 282:435 */     String sTable = getContent((Element)nodelist.get(0));
/* 283:436 */     sTable = sTable.replaceAll("\\n", " ");
/* 284:437 */     return sTable;
/* 285:    */   }
/* 286:    */   
/* 287:    */   ArrayList<Node> selectOutCome(Node item)
/* 288:    */     throws Exception
/* 289:    */   {
/* 290:442 */     ArrayList<Node> nodelist = selectElements(item, "OUTCOME");
/* 291:443 */     return nodelist;
/* 292:    */   }
/* 293:    */   
/* 294:    */   ArrayList<Node> selectName(Node item)
/* 295:    */     throws Exception
/* 296:    */   {
/* 297:448 */     ArrayList<Node> nodelist = selectElements(item, "NAME");
/* 298:449 */     return nodelist;
/* 299:    */   }
/* 300:    */   
/* 301:    */   ArrayList<Node> selectProperty(Node item)
/* 302:    */     throws Exception
/* 303:    */   {
/* 304:454 */     ArrayList<Node> nodelist = selectElements(item, "PROPERTY");
/* 305:455 */     return nodelist;
/* 306:    */   }
/* 307:    */   
/* 308:    */   ArrayList<Node> selectElements(Node item, String sElement)
/* 309:    */     throws Exception
/* 310:    */   {
/* 311:459 */     NodeList children = item.getChildNodes();
/* 312:460 */     ArrayList<Node> nodelist = new ArrayList();
/* 313:461 */     for (int iNode = 0; iNode < children.getLength(); iNode++)
/* 314:    */     {
/* 315:462 */       Node node = children.item(iNode);
/* 316:463 */       if ((node.getNodeType() == 1) && (node.getNodeName().equals(sElement))) {
/* 317:465 */         nodelist.add(node);
/* 318:    */       }
/* 319:    */     }
/* 320:468 */     return nodelist;
/* 321:    */   }
/* 322:    */   
/* 323:    */   public int missingArcs(BayesNet other)
/* 324:    */   {
/* 325:    */     try
/* 326:    */     {
/* 327:480 */       Sync(other);
/* 328:481 */       int nMissing = 0;
/* 329:482 */       for (int iAttribute = 0; iAttribute < this.m_Instances.numAttributes(); iAttribute++) {
/* 330:483 */         for (int iParent = 0; iParent < this.m_ParentSets[iAttribute].getNrOfParents(); iParent++)
/* 331:    */         {
/* 332:485 */           int nParent = this.m_ParentSets[iAttribute].getParent(iParent);
/* 333:486 */           if ((!other.getParentSet(this.m_order[iAttribute]).contains(this.m_order[nParent])) && (!other.getParentSet(this.m_order[nParent]).contains(this.m_order[iAttribute]))) {
/* 334:490 */             nMissing++;
/* 335:    */           }
/* 336:    */         }
/* 337:    */       }
/* 338:494 */       return nMissing;
/* 339:    */     }
/* 340:    */     catch (Exception e)
/* 341:    */     {
/* 342:496 */       System.err.println(e.getMessage());
/* 343:    */     }
/* 344:497 */     return 0;
/* 345:    */   }
/* 346:    */   
/* 347:    */   public int extraArcs(BayesNet other)
/* 348:    */   {
/* 349:    */     try
/* 350:    */     {
/* 351:510 */       Sync(other);
/* 352:511 */       int nExtra = 0;
/* 353:512 */       for (int iAttribute = 0; iAttribute < this.m_Instances.numAttributes(); iAttribute++) {
/* 354:513 */         for (int iParent = 0; iParent < other.getParentSet(this.m_order[iAttribute]).getNrOfParents(); iParent++)
/* 355:    */         {
/* 356:515 */           int nParent = this.m_order[other.getParentSet(this.m_order[iAttribute]).getParent(iParent)];
/* 357:517 */           if ((!this.m_ParentSets[iAttribute].contains(nParent)) && (!this.m_ParentSets[nParent].contains(iAttribute))) {
/* 358:519 */             nExtra++;
/* 359:    */           }
/* 360:    */         }
/* 361:    */       }
/* 362:523 */       return nExtra;
/* 363:    */     }
/* 364:    */     catch (Exception e)
/* 365:    */     {
/* 366:525 */       System.err.println(e.getMessage());
/* 367:    */     }
/* 368:526 */     return 0;
/* 369:    */   }
/* 370:    */   
/* 371:    */   public double divergence(BayesNet other)
/* 372:    */   {
/* 373:    */     try
/* 374:    */     {
/* 375:542 */       Sync(other);
/* 376:    */       
/* 377:544 */       double D = 0.0D;
/* 378:545 */       int nNodes = this.m_Instances.numAttributes();
/* 379:546 */       int[] nCard = new int[nNodes];
/* 380:547 */       for (int iNode = 0; iNode < nNodes; iNode++) {
/* 381:548 */         nCard[iNode] = this.m_Instances.attribute(iNode).numValues();
/* 382:    */       }
/* 383:551 */       int[] x = new int[nNodes];
/* 384:    */       
/* 385:553 */       int i = 0;
/* 386:554 */       while (i < nNodes)
/* 387:    */       {
/* 388:556 */         x[i] += 1;
/* 389:557 */         while ((i < nNodes) && (x[i] == this.m_Instances.attribute(i).numValues()))
/* 390:    */         {
/* 391:558 */           x[i] = 0;
/* 392:559 */           i++;
/* 393:560 */           if (i < nNodes) {
/* 394:561 */             x[i] += 1;
/* 395:    */           }
/* 396:    */         }
/* 397:564 */         if (i < nNodes)
/* 398:    */         {
/* 399:565 */           i = 0;
/* 400:    */           
/* 401:567 */           double P = 1.0D;
/* 402:568 */           for (int iNode = 0; iNode < nNodes; iNode++)
/* 403:    */           {
/* 404:569 */             int iCPT = 0;
/* 405:570 */             for (int iParent = 0; iParent < this.m_ParentSets[iNode].getNrOfParents(); iParent++)
/* 406:    */             {
/* 407:572 */               int nParent = this.m_ParentSets[iNode].getParent(iParent);
/* 408:573 */               iCPT = iCPT * nCard[nParent] + x[nParent];
/* 409:    */             }
/* 410:575 */             P *= this.m_Distributions[iNode][iCPT].getProbability(x[iNode]);
/* 411:    */           }
/* 412:578 */           double Q = 1.0D;
/* 413:579 */           for (int iNode = 0; iNode < nNodes; iNode++)
/* 414:    */           {
/* 415:580 */             int iCPT = 0;
/* 416:581 */             for (int iParent = 0; iParent < other.getParentSet(this.m_order[iNode]).getNrOfParents(); iParent++)
/* 417:    */             {
/* 418:583 */               int nParent = this.m_order[other.getParentSet(this.m_order[iNode]).getParent(iParent)];
/* 419:    */               
/* 420:585 */               iCPT = iCPT * nCard[nParent] + x[nParent];
/* 421:    */             }
/* 422:587 */             Q *= other.m_Distributions[this.m_order[iNode]][iCPT].getProbability(x[iNode]);
/* 423:    */           }
/* 424:593 */           if ((P > 0.0D) && (Q > 0.0D)) {
/* 425:594 */             D += P * Math.log(Q / P);
/* 426:    */           }
/* 427:    */         }
/* 428:    */       }
/* 429:598 */       return D;
/* 430:    */     }
/* 431:    */     catch (Exception e)
/* 432:    */     {
/* 433:600 */       System.err.println(e.getMessage());
/* 434:    */     }
/* 435:601 */     return 0.0D;
/* 436:    */   }
/* 437:    */   
/* 438:    */   public int reversedArcs(BayesNet other)
/* 439:    */   {
/* 440:    */     try
/* 441:    */     {
/* 442:613 */       Sync(other);
/* 443:614 */       int nReversed = 0;
/* 444:615 */       for (int iAttribute = 0; iAttribute < this.m_Instances.numAttributes(); iAttribute++) {
/* 445:616 */         for (int iParent = 0; iParent < this.m_ParentSets[iAttribute].getNrOfParents(); iParent++)
/* 446:    */         {
/* 447:618 */           int nParent = this.m_ParentSets[iAttribute].getParent(iParent);
/* 448:619 */           if ((!other.getParentSet(this.m_order[iAttribute]).contains(this.m_order[nParent])) && (other.getParentSet(this.m_order[nParent]).contains(this.m_order[iAttribute]))) {
/* 449:623 */             nReversed++;
/* 450:    */           }
/* 451:    */         }
/* 452:    */       }
/* 453:627 */       return nReversed;
/* 454:    */     }
/* 455:    */     catch (Exception e)
/* 456:    */     {
/* 457:629 */       System.err.println(e.getMessage());
/* 458:    */     }
/* 459:630 */     return 0;
/* 460:    */   }
/* 461:    */   
/* 462:    */   public int getNode(String sNodeName)
/* 463:    */     throws Exception
/* 464:    */   {
/* 465:643 */     int iNode = 0;
/* 466:644 */     while (iNode < this.m_Instances.numAttributes())
/* 467:    */     {
/* 468:645 */       if (this.m_Instances.attribute(iNode).name().equals(sNodeName)) {
/* 469:646 */         return iNode;
/* 470:    */       }
/* 471:648 */       iNode++;
/* 472:    */     }
/* 473:650 */     throw new Exception("Could not find node [[" + sNodeName + "]]");
/* 474:    */   }
/* 475:    */   
/* 476:    */   public String getRevision()
/* 477:    */   {
/* 478:666 */     return RevisionUtils.extract("$Revision: 10153 $");
/* 479:    */   }
/* 480:    */   
/* 481:    */   public static void main(String[] args)
/* 482:    */   {
/* 483:    */     try
/* 484:    */     {
/* 485:676 */       BIFReader br = new BIFReader();
/* 486:677 */       br.processFile(args[0]);
/* 487:678 */       System.out.println(br.toString());
/* 488:    */     }
/* 489:    */     catch (Throwable t)
/* 490:    */     {
/* 491:681 */       t.printStackTrace();
/* 492:    */     }
/* 493:    */   }
/* 494:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.BIFReader
 * JD-Core Version:    0.7.0.1
 */