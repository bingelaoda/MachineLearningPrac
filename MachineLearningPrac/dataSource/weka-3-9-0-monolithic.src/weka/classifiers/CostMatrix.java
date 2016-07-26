/*   1:    */ package weka.classifiers;
/*   2:    */ 
/*   3:    */ import java.io.LineNumberReader;
/*   4:    */ import java.io.Reader;
/*   5:    */ import java.io.Serializable;
/*   6:    */ import java.io.StreamTokenizer;
/*   7:    */ import java.io.Writer;
/*   8:    */ import java.util.Random;
/*   9:    */ import java.util.StringTokenizer;
/*  10:    */ import weka.core.Attribute;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.RevisionHandler;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.Utils;
/*  16:    */ import weka.core.expressionlanguage.common.IfElseMacro;
/*  17:    */ import weka.core.expressionlanguage.common.JavaMacro;
/*  18:    */ import weka.core.expressionlanguage.common.MacroDeclarationsCompositor;
/*  19:    */ import weka.core.expressionlanguage.common.MathFunctions;
/*  20:    */ import weka.core.expressionlanguage.common.Primitives.DoubleExpression;
/*  21:    */ import weka.core.expressionlanguage.core.MacroDeclarations;
/*  22:    */ import weka.core.expressionlanguage.core.Node;
/*  23:    */ import weka.core.expressionlanguage.parser.Parser;
/*  24:    */ import weka.core.expressionlanguage.weka.InstancesHelper;
/*  25:    */ 
/*  26:    */ public class CostMatrix
/*  27:    */   implements Serializable, RevisionHandler
/*  28:    */ {
/*  29:    */   private static final long serialVersionUID = -1973792250544554965L;
/*  30:    */   private int m_size;
/*  31:    */   protected Object[][] m_matrix;
/*  32: 82 */   public static String FILE_EXTENSION = ".cost";
/*  33:    */   
/*  34:    */   public CostMatrix(int numOfClasses)
/*  35:    */   {
/*  36: 91 */     this.m_size = numOfClasses;
/*  37: 92 */     initialize();
/*  38:    */   }
/*  39:    */   
/*  40:    */   public CostMatrix(CostMatrix toCopy)
/*  41:    */   {
/*  42:101 */     this(toCopy.size());
/*  43:103 */     for (int i = 0; i < this.m_size; i++) {
/*  44:104 */       for (int j = 0; j < this.m_size; j++) {
/*  45:105 */         setCell(i, j, toCopy.getCell(i, j));
/*  46:    */       }
/*  47:    */     }
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void initialize()
/*  51:    */   {
/*  52:114 */     this.m_matrix = new Object[this.m_size][this.m_size];
/*  53:115 */     for (int i = 0; i < this.m_size; i++) {
/*  54:116 */       for (int j = 0; j < this.m_size; j++) {
/*  55:117 */         setCell(i, j, i == j ? new Double(0.0D) : new Double(1.0D));
/*  56:    */       }
/*  57:    */     }
/*  58:    */   }
/*  59:    */   
/*  60:    */   public int size()
/*  61:    */   {
/*  62:128 */     return this.m_size;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public int numColumns()
/*  66:    */   {
/*  67:137 */     return size();
/*  68:    */   }
/*  69:    */   
/*  70:    */   public int numRows()
/*  71:    */   {
/*  72:146 */     return size();
/*  73:    */   }
/*  74:    */   
/*  75:    */   private boolean replaceStrings(Instances dataset)
/*  76:    */     throws Exception
/*  77:    */   {
/*  78:150 */     boolean nonDouble = false;
/*  79:152 */     for (int i = 0; i < this.m_size; i++) {
/*  80:153 */       for (int j = 0; j < this.m_size; j++) {
/*  81:154 */         if ((getCell(i, j) instanceof String))
/*  82:    */         {
/*  83:155 */           setCell(i, j, new InstanceExpression((String)getCell(i, j), dataset));
/*  84:156 */           nonDouble = true;
/*  85:    */         }
/*  86:157 */         else if ((getCell(i, j) instanceof InstanceExpression))
/*  87:    */         {
/*  88:158 */           nonDouble = true;
/*  89:    */         }
/*  90:    */       }
/*  91:    */     }
/*  92:163 */     return nonDouble;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public Instances applyCostMatrix(Instances data, Random random)
/*  96:    */     throws Exception
/*  97:    */   {
/*  98:181 */     double sumOfWeightFactors = 0.0D;
/*  99:184 */     if (data.classIndex() < 0) {
/* 100:185 */       throw new Exception("Class index is not set!");
/* 101:    */     }
/* 102:188 */     if (size() != data.numClasses()) {
/* 103:189 */       throw new Exception("Misclassification cost matrix has wrong format!");
/* 104:    */     }
/* 105:193 */     if (replaceStrings(data))
/* 106:    */     {
/* 107:195 */       if (data.classAttribute().numValues() > 2) {
/* 108:196 */         throw new Exception("Can't resample/reweight instances using non-fixed cost values when there are more than two classes!");
/* 109:    */       }
/* 110:200 */       double[] weightOfInstances = new double[data.numInstances()];
/* 111:201 */       for (int i = 0; i < data.numInstances(); i++)
/* 112:    */       {
/* 113:202 */         Instance inst = data.instance(i);
/* 114:203 */         int classValIndex = (int)inst.classValue();
/* 115:204 */         double factor = 1.0D;
/* 116:205 */         Object element = classValIndex == 0 ? getCell(classValIndex, 1) : getCell(classValIndex, 0);
/* 117:208 */         if ((element instanceof Double)) {
/* 118:209 */           factor = ((Double)element).doubleValue();
/* 119:    */         } else {
/* 120:211 */           factor = ((InstanceExpression)element).evaluate(inst);
/* 121:    */         }
/* 122:213 */         weightOfInstances[i] = (inst.weight() * factor);
/* 123:    */       }
/* 124:222 */       if (random != null) {
/* 125:223 */         return data.resampleWithWeights(random, weightOfInstances);
/* 126:    */       }
/* 127:225 */       Instances instances = new Instances(data);
/* 128:226 */       for (int i = 0; i < data.numInstances(); i++) {
/* 129:227 */         instances.instance(i).setWeight(weightOfInstances[i]);
/* 130:    */       }
/* 131:229 */       return instances;
/* 132:    */     }
/* 133:234 */     double[] weightFactor = new double[data.numClasses()];
/* 134:235 */     double[] weightOfInstancesInClass = new double[data.numClasses()];
/* 135:236 */     for (int j = 0; j < data.numInstances(); j++) {
/* 136:237 */       weightOfInstancesInClass[((int)data.instance(j).classValue())] += data.instance(j).weight();
/* 137:    */     }
/* 138:240 */     double sumOfWeights = Utils.sum(weightOfInstancesInClass);
/* 139:243 */     for (int i = 0; i < this.m_size; i++) {
/* 140:244 */       if (!Utils.eq(((Double)getCell(i, i)).doubleValue(), 0.0D))
/* 141:    */       {
/* 142:245 */         CostMatrix normMatrix = new CostMatrix(this);
/* 143:246 */         normMatrix.normalize();
/* 144:247 */         return normMatrix.applyCostMatrix(data, random);
/* 145:    */       }
/* 146:    */     }
/* 147:251 */     for (int i = 0; i < data.numClasses(); i++)
/* 148:    */     {
/* 149:256 */       double sumOfMissClassWeights = 0.0D;
/* 150:257 */       for (int j = 0; j < data.numClasses(); j++)
/* 151:    */       {
/* 152:258 */         if (Utils.sm(((Double)getCell(i, j)).doubleValue(), 0.0D)) {
/* 153:259 */           throw new Exception("Neg. weights in misclassification cost matrix!");
/* 154:    */         }
/* 155:262 */         sumOfMissClassWeights += ((Double)getCell(i, j)).doubleValue();
/* 156:    */       }
/* 157:264 */       weightFactor[i] = (sumOfMissClassWeights * sumOfWeights);
/* 158:265 */       sumOfWeightFactors += sumOfMissClassWeights * weightOfInstancesInClass[i];
/* 159:    */     }
/* 160:267 */     for (int i = 0; i < data.numClasses(); i++) {
/* 161:268 */       weightFactor[i] /= sumOfWeightFactors;
/* 162:    */     }
/* 163:272 */     double[] weightOfInstances = new double[data.numInstances()];
/* 164:273 */     for (int i = 0; i < data.numInstances(); i++) {
/* 165:274 */       weightOfInstances[i] = (data.instance(i).weight() * weightFactor[((int)data.instance(i).classValue())]);
/* 166:    */     }
/* 167:280 */     if (random != null) {
/* 168:281 */       return data.resampleWithWeights(random, weightOfInstances);
/* 169:    */     }
/* 170:283 */     Instances instances = new Instances(data);
/* 171:284 */     for (int i = 0; i < data.numInstances(); i++) {
/* 172:285 */       instances.instance(i).setWeight(weightOfInstances[i]);
/* 173:    */     }
/* 174:287 */     return instances;
/* 175:    */   }
/* 176:    */   
/* 177:    */   public double[] expectedCosts(double[] classProbs)
/* 178:    */     throws Exception
/* 179:    */   {
/* 180:302 */     if (classProbs.length != this.m_size) {
/* 181:303 */       throw new Exception("Length of probability estimates don't match cost matrix");
/* 182:    */     }
/* 183:307 */     double[] costs = new double[this.m_size];
/* 184:309 */     for (int x = 0; x < this.m_size; x++) {
/* 185:310 */       for (int y = 0; y < this.m_size; y++)
/* 186:    */       {
/* 187:311 */         Object element = getCell(y, x);
/* 188:312 */         if (!(element instanceof Double)) {
/* 189:313 */           throw new Exception("Can't use non-fixed costs in computing expected costs.");
/* 190:    */         }
/* 191:316 */         costs[x] += classProbs[y] * ((Double)element).doubleValue();
/* 192:    */       }
/* 193:    */     }
/* 194:320 */     return costs;
/* 195:    */   }
/* 196:    */   
/* 197:    */   public double[] expectedCosts(double[] classProbs, Instance inst)
/* 198:    */     throws Exception
/* 199:    */   {
/* 200:336 */     if (classProbs.length != this.m_size) {
/* 201:337 */       throw new Exception("Length of probability estimates don't match cost matrix");
/* 202:    */     }
/* 203:341 */     if (!replaceStrings(inst.dataset())) {
/* 204:342 */       return expectedCosts(classProbs);
/* 205:    */     }
/* 206:345 */     double[] costs = new double[this.m_size];
/* 207:347 */     for (int x = 0; x < this.m_size; x++) {
/* 208:348 */       for (int y = 0; y < this.m_size; y++)
/* 209:    */       {
/* 210:349 */         Object element = getCell(y, x);
/* 211:    */         double costVal;
/* 212:    */         double costVal;
/* 213:351 */         if (!(element instanceof Double)) {
/* 214:352 */           costVal = ((InstanceExpression)element).evaluate(inst);
/* 215:    */         } else {
/* 216:354 */           costVal = ((Double)element).doubleValue();
/* 217:    */         }
/* 218:356 */         costs[x] += classProbs[y] * costVal;
/* 219:    */       }
/* 220:    */     }
/* 221:360 */     return costs;
/* 222:    */   }
/* 223:    */   
/* 224:    */   public double getMaxCost(int classVal)
/* 225:    */     throws Exception
/* 226:    */   {
/* 227:372 */     double maxCost = (-1.0D / 0.0D);
/* 228:374 */     for (int i = 0; i < this.m_size; i++)
/* 229:    */     {
/* 230:375 */       Object element = getCell(classVal, i);
/* 231:376 */       if (!(element instanceof Double)) {
/* 232:377 */         throw new Exception("Can't use non-fixed costs when getting max cost.");
/* 233:    */       }
/* 234:380 */       double cost = ((Double)element).doubleValue();
/* 235:381 */       if (cost > maxCost) {
/* 236:382 */         maxCost = cost;
/* 237:    */       }
/* 238:    */     }
/* 239:385 */     return maxCost;
/* 240:    */   }
/* 241:    */   
/* 242:    */   public double getMaxCost(int classVal, Instance inst)
/* 243:    */     throws Exception
/* 244:    */   {
/* 245:397 */     if (!replaceStrings(inst.dataset())) {
/* 246:398 */       return getMaxCost(classVal);
/* 247:    */     }
/* 248:401 */     double maxCost = (-1.0D / 0.0D);
/* 249:403 */     for (int i = 0; i < this.m_size; i++)
/* 250:    */     {
/* 251:404 */       Object element = getCell(classVal, i);
/* 252:    */       double cost;
/* 253:    */       double cost;
/* 254:405 */       if (!(element instanceof Double)) {
/* 255:406 */         cost = ((InstanceExpression)element).evaluate(inst);
/* 256:    */       } else {
/* 257:408 */         cost = ((Double)element).doubleValue();
/* 258:    */       }
/* 259:410 */       if (cost > maxCost) {
/* 260:411 */         maxCost = cost;
/* 261:    */       }
/* 262:    */     }
/* 263:414 */     return maxCost;
/* 264:    */   }
/* 265:    */   
/* 266:    */   public void normalize()
/* 267:    */   {
/* 268:423 */     for (int y = 0; y < this.m_size; y++)
/* 269:    */     {
/* 270:424 */       double diag = ((Double)getCell(y, y)).doubleValue();
/* 271:425 */       for (int x = 0; x < this.m_size; x++) {
/* 272:426 */         setCell(x, y, new Double(((Double)getCell(x, y)).doubleValue() - diag));
/* 273:    */       }
/* 274:    */     }
/* 275:    */   }
/* 276:    */   
/* 277:    */   public void readOldFormat(Reader reader)
/* 278:    */     throws Exception
/* 279:    */   {
/* 280:444 */     StreamTokenizer tokenizer = new StreamTokenizer(reader);
/* 281:    */     
/* 282:446 */     initialize();
/* 283:    */     
/* 284:448 */     tokenizer.commentChar(37);
/* 285:449 */     tokenizer.eolIsSignificant(true);
/* 286:    */     int currentToken;
/* 287:450 */     while (-1 != (currentToken = tokenizer.nextToken())) {
/* 288:453 */       if (currentToken != 10)
/* 289:    */       {
/* 290:458 */         if (currentToken != -2) {
/* 291:459 */           throw new Exception("Only numbers and comments allowed in cost file!");
/* 292:    */         }
/* 293:462 */         double firstIndex = tokenizer.nval;
/* 294:463 */         if (!Utils.eq((int)firstIndex, firstIndex)) {
/* 295:464 */           throw new Exception("First number in line has to be index of a class!");
/* 296:    */         }
/* 297:467 */         if ((int)firstIndex >= size()) {
/* 298:468 */           throw new Exception("Class index out of range!");
/* 299:    */         }
/* 300:472 */         if (-1 == (currentToken = tokenizer.nextToken())) {
/* 301:473 */           throw new Exception("Premature end of file!");
/* 302:    */         }
/* 303:475 */         if (currentToken == 10) {
/* 304:476 */           throw new Exception("Premature end of line!");
/* 305:    */         }
/* 306:478 */         if (currentToken != -2) {
/* 307:479 */           throw new Exception("Only numbers and comments allowed in cost file!");
/* 308:    */         }
/* 309:482 */         double secondIndex = tokenizer.nval;
/* 310:483 */         if (!Utils.eq((int)secondIndex, secondIndex)) {
/* 311:484 */           throw new Exception("Second number in line has to be index of a class!");
/* 312:    */         }
/* 313:487 */         if ((int)secondIndex >= size()) {
/* 314:488 */           throw new Exception("Class index out of range!");
/* 315:    */         }
/* 316:490 */         if ((int)secondIndex == (int)firstIndex) {
/* 317:491 */           throw new Exception("Diagonal of cost matrix non-zero!");
/* 318:    */         }
/* 319:495 */         if (-1 == (currentToken = tokenizer.nextToken())) {
/* 320:496 */           throw new Exception("Premature end of file!");
/* 321:    */         }
/* 322:498 */         if (currentToken == 10) {
/* 323:499 */           throw new Exception("Premature end of line!");
/* 324:    */         }
/* 325:501 */         if (currentToken != -2) {
/* 326:502 */           throw new Exception("Only numbers and comments allowed in cost file!");
/* 327:    */         }
/* 328:505 */         double weight = tokenizer.nval;
/* 329:506 */         if (!Utils.gr(weight, 0.0D)) {
/* 330:507 */           throw new Exception("Only positive weights allowed!");
/* 331:    */         }
/* 332:509 */         setCell((int)firstIndex, (int)secondIndex, new Double(weight));
/* 333:    */       }
/* 334:    */     }
/* 335:    */   }
/* 336:    */   
/* 337:    */   public CostMatrix(Reader reader)
/* 338:    */     throws Exception
/* 339:    */   {
/* 340:523 */     LineNumberReader lnr = new LineNumberReader(reader);
/* 341:    */     
/* 342:525 */     int currentRow = -1;
/* 343:    */     String line;
/* 344:527 */     while ((line = lnr.readLine()) != null) {
/* 345:530 */       if (!line.startsWith("%"))
/* 346:    */       {
/* 347:534 */         StringTokenizer st = new StringTokenizer(line);
/* 348:536 */         if (st.hasMoreTokens()) {
/* 349:540 */           if (currentRow < 0)
/* 350:    */           {
/* 351:541 */             int rows = Integer.parseInt(st.nextToken());
/* 352:542 */             if (!st.hasMoreTokens()) {
/* 353:543 */               throw new Exception("Line " + lnr.getLineNumber() + ": expected number of columns");
/* 354:    */             }
/* 355:547 */             int cols = Integer.parseInt(st.nextToken());
/* 356:548 */             if (rows != cols) {
/* 357:549 */               throw new Exception("Trying to create a non-square cost matrix");
/* 358:    */             }
/* 359:552 */             this.m_size = rows;
/* 360:553 */             initialize();
/* 361:554 */             currentRow++;
/* 362:    */           }
/* 363:    */           else
/* 364:    */           {
/* 365:558 */             if (currentRow == this.m_size) {
/* 366:559 */               throw new Exception("Line " + lnr.getLineNumber() + ": too many rows provided");
/* 367:    */             }
/* 368:563 */             for (int i = 0; i < this.m_size; i++)
/* 369:    */             {
/* 370:564 */               if (!st.hasMoreTokens()) {
/* 371:565 */                 throw new Exception("Line " + lnr.getLineNumber() + ": too few matrix elements provided");
/* 372:    */               }
/* 373:569 */               String nextTok = st.nextToken();
/* 374:    */               
/* 375:571 */               Double val = null;
/* 376:    */               try
/* 377:    */               {
/* 378:573 */                 val = new Double(nextTok);
/* 379:    */               }
/* 380:    */               catch (Exception ex)
/* 381:    */               {
/* 382:575 */                 val = null;
/* 383:    */               }
/* 384:577 */               if (val == null) {
/* 385:578 */                 setCell(currentRow, i, nextTok);
/* 386:    */               } else {
/* 387:580 */                 setCell(currentRow, i, val);
/* 388:    */               }
/* 389:    */             }
/* 390:583 */             currentRow++;
/* 391:    */           }
/* 392:    */         }
/* 393:    */       }
/* 394:    */     }
/* 395:587 */     if (currentRow == -1) {
/* 396:588 */       throw new Exception("Line " + lnr.getLineNumber() + ": expected number of rows");
/* 397:    */     }
/* 398:590 */     if (currentRow != this.m_size) {
/* 399:591 */       throw new Exception("Line " + lnr.getLineNumber() + ": too few rows provided");
/* 400:    */     }
/* 401:    */   }
/* 402:    */   
/* 403:    */   public void write(Writer w)
/* 404:    */     throws Exception
/* 405:    */   {
/* 406:604 */     w.write("% Rows\tColumns\n");
/* 407:605 */     w.write("" + this.m_size + "\t" + this.m_size + "\n");
/* 408:606 */     w.write("% Matrix elements\n");
/* 409:607 */     for (int i = 0; i < this.m_size; i++)
/* 410:    */     {
/* 411:608 */       for (int j = 0; j < this.m_size; j++) {
/* 412:609 */         w.write("" + getCell(i, j) + "\t");
/* 413:    */       }
/* 414:611 */       w.write("\n");
/* 415:    */     }
/* 416:613 */     w.flush();
/* 417:    */   }
/* 418:    */   
/* 419:    */   public String toMatlab()
/* 420:    */   {
/* 421:628 */     StringBuffer result = new StringBuffer();
/* 422:    */     
/* 423:630 */     result.append("[");
/* 424:632 */     for (int i = 0; i < this.m_size; i++)
/* 425:    */     {
/* 426:633 */       if (i > 0) {
/* 427:634 */         result.append("; ");
/* 428:    */       }
/* 429:637 */       for (int n = 0; n < this.m_size; n++)
/* 430:    */       {
/* 431:638 */         if (n > 0) {
/* 432:639 */           result.append(" ");
/* 433:    */         }
/* 434:641 */         result.append(getCell(i, n));
/* 435:    */       }
/* 436:    */     }
/* 437:645 */     result.append("]");
/* 438:    */     
/* 439:647 */     return result.toString();
/* 440:    */   }
/* 441:    */   
/* 442:    */   public static CostMatrix parseMatlab(String matlab)
/* 443:    */     throws Exception
/* 444:    */   {
/* 445:666 */     String cells = matlab.substring(matlab.indexOf("[") + 1, matlab.indexOf("]")).trim();
/* 446:    */     
/* 447:    */ 
/* 448:    */ 
/* 449:670 */     StringTokenizer tokRow = new StringTokenizer(cells, ";");
/* 450:671 */     int rows = tokRow.countTokens();
/* 451:672 */     StringTokenizer tokCol = new StringTokenizer(tokRow.nextToken(), " ");
/* 452:673 */     int cols = tokCol.countTokens();
/* 453:    */     
/* 454:    */ 
/* 455:676 */     CostMatrix result = new CostMatrix(rows);
/* 456:677 */     tokRow = new StringTokenizer(cells, ";");
/* 457:678 */     rows = 0;
/* 458:679 */     while (tokRow.hasMoreTokens())
/* 459:    */     {
/* 460:680 */       tokCol = new StringTokenizer(tokRow.nextToken(), " ");
/* 461:681 */       cols = 0;
/* 462:682 */       while (tokCol.hasMoreTokens())
/* 463:    */       {
/* 464:684 */         String current = tokCol.nextToken();
/* 465:    */         try
/* 466:    */         {
/* 467:686 */           double val = Double.parseDouble(current);
/* 468:687 */           result.setCell(rows, cols, new Double(val));
/* 469:    */         }
/* 470:    */         catch (NumberFormatException e)
/* 471:    */         {
/* 472:690 */           result.setCell(rows, cols, current);
/* 473:    */         }
/* 474:692 */         cols++;
/* 475:    */       }
/* 476:694 */       rows++;
/* 477:    */     }
/* 478:697 */     return result;
/* 479:    */   }
/* 480:    */   
/* 481:    */   public final void setCell(int rowIndex, int columnIndex, Object value)
/* 482:    */   {
/* 483:708 */     this.m_matrix[rowIndex][columnIndex] = value;
/* 484:    */   }
/* 485:    */   
/* 486:    */   public final Object getCell(int rowIndex, int columnIndex)
/* 487:    */   {
/* 488:720 */     return this.m_matrix[rowIndex][columnIndex];
/* 489:    */   }
/* 490:    */   
/* 491:    */   public final double getElement(int rowIndex, int columnIndex)
/* 492:    */     throws Exception
/* 493:    */   {
/* 494:733 */     if (!(this.m_matrix[rowIndex][columnIndex] instanceof Double)) {
/* 495:734 */       throw new Exception("Cost matrix contains non-fixed costs!");
/* 496:    */     }
/* 497:736 */     return ((Double)this.m_matrix[rowIndex][columnIndex]).doubleValue();
/* 498:    */   }
/* 499:    */   
/* 500:    */   public final double getElement(int rowIndex, int columnIndex, Instance inst)
/* 501:    */     throws Exception
/* 502:    */   {
/* 503:751 */     if ((this.m_matrix[rowIndex][columnIndex] instanceof Double)) {
/* 504:752 */       return ((Double)this.m_matrix[rowIndex][columnIndex]).doubleValue();
/* 505:    */     }
/* 506:753 */     if ((this.m_matrix[rowIndex][columnIndex] instanceof String)) {
/* 507:754 */       replaceStrings(inst.dataset());
/* 508:    */     }
/* 509:757 */     return ((InstanceExpression)this.m_matrix[rowIndex][columnIndex]).evaluate(inst);
/* 510:    */   }
/* 511:    */   
/* 512:    */   public final void setElement(int rowIndex, int columnIndex, double value)
/* 513:    */   {
/* 514:769 */     this.m_matrix[rowIndex][columnIndex] = new Double(value);
/* 515:    */   }
/* 516:    */   
/* 517:    */   public String toString()
/* 518:    */   {
/* 519:782 */     double maxval = 0.0D;
/* 520:783 */     boolean fractional = false;
/* 521:784 */     Object element = null;
/* 522:785 */     int widthNumber = 0;
/* 523:786 */     int widthExpression = 0;
/* 524:787 */     for (int i = 0; i < size(); i++) {
/* 525:788 */       for (int j = 0; j < size(); j++)
/* 526:    */       {
/* 527:789 */         element = getCell(i, j);
/* 528:790 */         if ((element instanceof Double))
/* 529:    */         {
/* 530:791 */           double current = ((Double)element).doubleValue();
/* 531:793 */           if (current < 0.0D) {
/* 532:794 */             current *= -11.0D;
/* 533:    */           }
/* 534:795 */           if (current > maxval) {
/* 535:796 */             maxval = current;
/* 536:    */           }
/* 537:797 */           double fract = Math.abs(current - Math.rint(current));
/* 538:798 */           if ((!fractional) && (Math.log(fract) / Math.log(10.0D) >= -2.0D)) {
/* 539:799 */             fractional = true;
/* 540:    */           }
/* 541:    */         }
/* 542:802 */         else if (element.toString().length() > widthExpression)
/* 543:    */         {
/* 544:803 */           widthExpression = element.toString().length();
/* 545:    */         }
/* 546:    */       }
/* 547:    */     }
/* 548:808 */     if (maxval > 0.0D) {
/* 549:809 */       widthNumber = (int)(Math.log(maxval) / Math.log(10.0D) + (fractional ? 4 : 1));
/* 550:    */     }
/* 551:813 */     int width = widthNumber > widthExpression ? widthNumber : widthExpression;
/* 552:    */     
/* 553:815 */     StringBuffer text = new StringBuffer();
/* 554:816 */     for (int i = 0; i < size(); i++)
/* 555:    */     {
/* 556:817 */       for (int j = 0; j < size(); j++)
/* 557:    */       {
/* 558:818 */         element = getCell(i, j);
/* 559:819 */         if ((element instanceof Double))
/* 560:    */         {
/* 561:820 */           text.append(" ").append(Utils.doubleToString(((Double)element).doubleValue(), width, fractional ? 2 : 0));
/* 562:    */         }
/* 563:    */         else
/* 564:    */         {
/* 565:824 */           int diff = width - element.toString().length();
/* 566:825 */           if (diff > 0)
/* 567:    */           {
/* 568:826 */             int left = diff % 2;
/* 569:827 */             left += diff / 2;
/* 570:828 */             String temp = Utils.padLeft(element.toString(), element.toString().length() + left);
/* 571:    */             
/* 572:    */ 
/* 573:831 */             temp = Utils.padRight(temp, width);
/* 574:832 */             text.append(" ").append(temp);
/* 575:    */           }
/* 576:    */           else
/* 577:    */           {
/* 578:834 */             text.append(" ").append(element.toString());
/* 579:    */           }
/* 580:    */         }
/* 581:    */       }
/* 582:838 */       text.append("\n");
/* 583:    */     }
/* 584:841 */     return text.toString();
/* 585:    */   }
/* 586:    */   
/* 587:    */   public String getRevision()
/* 588:    */   {
/* 589:851 */     return RevisionUtils.extract("$Revision: 11868 $");
/* 590:    */   }
/* 591:    */   
/* 592:    */   private static class InstanceExpression
/* 593:    */   {
/* 594:    */     private final Primitives.DoubleExpression m_compiledExpression;
/* 595:    */     private final String m_expression;
/* 596:    */     private final InstancesHelper m_instancesHelper;
/* 597:    */     
/* 598:    */     public InstanceExpression(String expression, Instances dataset)
/* 599:    */       throws Exception
/* 600:    */     {
/* 601:861 */       this.m_expression = expression;
/* 602:862 */       this.m_instancesHelper = new InstancesHelper(dataset);
/* 603:    */       
/* 604:864 */       Node node = Parser.parse(expression, this.m_instancesHelper, new MacroDeclarationsCompositor(new MacroDeclarations[] { this.m_instancesHelper, new MathFunctions(), new IfElseMacro(), new JavaMacro() }));
/* 605:874 */       if (!(node instanceof Primitives.DoubleExpression)) {
/* 606:875 */         throw new Exception("Expression must be of double type!");
/* 607:    */       }
/* 608:877 */       this.m_compiledExpression = ((Primitives.DoubleExpression)node);
/* 609:    */     }
/* 610:    */     
/* 611:    */     public double evaluate(Instance inst)
/* 612:    */     {
/* 613:881 */       this.m_instancesHelper.setInstance(inst);
/* 614:882 */       return this.m_compiledExpression.evaluate();
/* 615:    */     }
/* 616:    */     
/* 617:    */     public String toString()
/* 618:    */     {
/* 619:887 */       return this.m_expression;
/* 620:    */     }
/* 621:    */   }
/* 622:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.CostMatrix
 * JD-Core Version:    0.7.0.1
 */