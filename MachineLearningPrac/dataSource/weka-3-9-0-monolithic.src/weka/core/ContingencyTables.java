/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ 
/*   5:    */ public class ContingencyTables
/*   6:    */   implements RevisionHandler
/*   7:    */ {
/*   8: 34 */   public static final double log2 = Math.log(2.0D);
/*   9:    */   private static final double MAX_INT_FOR_CACHE_PLUS_ONE = 10000.0D;
/*  10: 38 */   private static final double[] INT_N_LOG_N_CACHE = new double[10000];
/*  11:    */   
/*  12:    */   static
/*  13:    */   {
/*  14: 42 */     for (int i = 1; i < 10000.0D; i++)
/*  15:    */     {
/*  16: 43 */       double d = i;
/*  17: 44 */       INT_N_LOG_N_CACHE[i] = (d * Math.log(d));
/*  18:    */     }
/*  19:    */   }
/*  20:    */   
/*  21:    */   public static double chiSquared(double[][] matrix, boolean yates)
/*  22:    */   {
/*  23: 59 */     int df = (matrix.length - 1) * (matrix[0].length - 1);
/*  24:    */     
/*  25: 61 */     return Statistics.chiSquaredProbability(chiVal(matrix, yates), df);
/*  26:    */   }
/*  27:    */   
/*  28:    */   public static double chiVal(double[][] matrix, boolean useYates)
/*  29:    */   {
/*  30: 75 */     double expect = 0.0D;double chival = 0.0D;double n = 0.0D;
/*  31: 76 */     boolean yates = true;
/*  32:    */     
/*  33: 78 */     int nrows = matrix.length;
/*  34: 79 */     int ncols = matrix[0].length;
/*  35: 80 */     double[] rtotal = new double[nrows];
/*  36: 81 */     double[] ctotal = new double[ncols];
/*  37: 82 */     for (int row = 0; row < nrows; row++) {
/*  38: 83 */       for (int col = 0; col < ncols; col++)
/*  39:    */       {
/*  40: 84 */         rtotal[row] += matrix[row][col];
/*  41: 85 */         ctotal[col] += matrix[row][col];
/*  42: 86 */         n += matrix[row][col];
/*  43:    */       }
/*  44:    */     }
/*  45: 89 */     int df = (nrows - 1) * (ncols - 1);
/*  46: 90 */     if ((df > 1) || (!useYates)) {
/*  47: 91 */       yates = false;
/*  48: 92 */     } else if (df <= 0) {
/*  49: 93 */       return 0.0D;
/*  50:    */     }
/*  51: 95 */     chival = 0.0D;
/*  52: 96 */     for (row = 0; row < nrows; row++) {
/*  53: 97 */       if (Utils.gr(rtotal[row], 0.0D)) {
/*  54: 98 */         for (int col = 0; col < ncols; col++) {
/*  55: 99 */           if (Utils.gr(ctotal[col], 0.0D))
/*  56:    */           {
/*  57:100 */             expect = ctotal[col] * rtotal[row] / n;
/*  58:101 */             chival += chiCell(matrix[row][col], expect, yates);
/*  59:    */           }
/*  60:    */         }
/*  61:    */       }
/*  62:    */     }
/*  63:106 */     return chival;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public static boolean cochransCriterion(double[][] matrix)
/*  67:    */   {
/*  68:120 */     double n = 0.0D;double smallfreq = 5.0D;
/*  69:121 */     int smallcount = 0;int nonZeroRows = 0;int nonZeroColumns = 0;
/*  70:    */     
/*  71:    */ 
/*  72:124 */     int nrows = matrix.length;
/*  73:125 */     int ncols = matrix[0].length;
/*  74:    */     
/*  75:127 */     double[] rtotal = new double[nrows];
/*  76:128 */     double[] ctotal = new double[ncols];
/*  77:129 */     for (int row = 0; row < nrows; row++) {
/*  78:130 */       for (int col = 0; col < ncols; col++)
/*  79:    */       {
/*  80:131 */         rtotal[row] += matrix[row][col];
/*  81:132 */         ctotal[col] += matrix[row][col];
/*  82:133 */         n += matrix[row][col];
/*  83:    */       }
/*  84:    */     }
/*  85:136 */     for (row = 0; row < nrows; row++) {
/*  86:137 */       if (Utils.gr(rtotal[row], 0.0D)) {
/*  87:138 */         nonZeroRows++;
/*  88:    */       }
/*  89:    */     }
/*  90:141 */     for (int col = 0; col < ncols; col++) {
/*  91:142 */       if (Utils.gr(ctotal[col], 0.0D)) {
/*  92:143 */         nonZeroColumns++;
/*  93:    */       }
/*  94:    */     }
/*  95:146 */     for (row = 0; row < nrows; row++) {
/*  96:147 */       if (Utils.gr(rtotal[row], 0.0D)) {
/*  97:148 */         for (col = 0; col < ncols; col++) {
/*  98:149 */           if (Utils.gr(ctotal[col], 0.0D))
/*  99:    */           {
/* 100:150 */             double expect = ctotal[col] * rtotal[row] / n;
/* 101:151 */             if (Utils.sm(expect, smallfreq))
/* 102:    */             {
/* 103:152 */               if (Utils.sm(expect, 1.0D)) {
/* 104:153 */                 return false;
/* 105:    */               }
/* 106:155 */               smallcount++;
/* 107:156 */               if (smallcount > nonZeroRows * nonZeroColumns / smallfreq) {
/* 108:157 */                 return false;
/* 109:    */               }
/* 110:    */             }
/* 111:    */           }
/* 112:    */         }
/* 113:    */       }
/* 114:    */     }
/* 115:165 */     return true;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public static double CramersV(double[][] matrix)
/* 119:    */   {
/* 120:177 */     double n = 0.0D;
/* 121:    */     
/* 122:179 */     int nrows = matrix.length;
/* 123:180 */     int ncols = matrix[0].length;
/* 124:181 */     for (int row = 0; row < nrows; row++) {
/* 125:182 */       for (int col = 0; col < ncols; col++) {
/* 126:183 */         n += matrix[row][col];
/* 127:    */       }
/* 128:    */     }
/* 129:186 */     int min = nrows < ncols ? nrows - 1 : ncols - 1;
/* 130:187 */     if ((min == 0) || (Utils.eq(n, 0.0D))) {
/* 131:188 */       return 0.0D;
/* 132:    */     }
/* 133:189 */     return Math.sqrt(chiVal(matrix, false) / (n * min));
/* 134:    */   }
/* 135:    */   
/* 136:    */   public static double entropy(double[] array)
/* 137:    */   {
/* 138:200 */     double returnValue = 0.0D;double sum = 0.0D;
/* 139:202 */     for (int i = 0; i < array.length; i++)
/* 140:    */     {
/* 141:203 */       returnValue -= lnFunc(array[i]);
/* 142:204 */       sum += array[i];
/* 143:    */     }
/* 144:206 */     if (Utils.eq(sum, 0.0D)) {
/* 145:207 */       return 0.0D;
/* 146:    */     }
/* 147:209 */     return (returnValue + lnFunc(sum)) / (sum * log2);
/* 148:    */   }
/* 149:    */   
/* 150:    */   public static double entropyConditionedOnColumns(double[][] matrix)
/* 151:    */   {
/* 152:222 */     double returnValue = 0.0D;double total = 0.0D;
/* 153:224 */     for (int j = 0; j < matrix[0].length; j++)
/* 154:    */     {
/* 155:225 */       double sumForColumn = 0.0D;
/* 156:226 */       for (int i = 0; i < matrix.length; i++)
/* 157:    */       {
/* 158:227 */         returnValue += lnFunc(matrix[i][j]);
/* 159:228 */         sumForColumn += matrix[i][j];
/* 160:    */       }
/* 161:230 */       returnValue -= lnFunc(sumForColumn);
/* 162:231 */       total += sumForColumn;
/* 163:    */     }
/* 164:233 */     if (Utils.eq(total, 0.0D)) {
/* 165:234 */       return 0.0D;
/* 166:    */     }
/* 167:236 */     return -returnValue / (total * log2);
/* 168:    */   }
/* 169:    */   
/* 170:    */   public static double entropyConditionedOnRows(double[][] matrix)
/* 171:    */   {
/* 172:248 */     double returnValue = 0.0D;double total = 0.0D;
/* 173:250 */     for (int i = 0; i < matrix.length; i++)
/* 174:    */     {
/* 175:251 */       double sumForRow = 0.0D;
/* 176:252 */       for (int j = 0; j < matrix[0].length; j++)
/* 177:    */       {
/* 178:253 */         returnValue += lnFunc(matrix[i][j]);
/* 179:254 */         sumForRow += matrix[i][j];
/* 180:    */       }
/* 181:256 */       returnValue -= lnFunc(sumForRow);
/* 182:257 */       total += sumForRow;
/* 183:    */     }
/* 184:259 */     if (Utils.eq(total, 0.0D)) {
/* 185:260 */       return 0.0D;
/* 186:    */     }
/* 187:262 */     return -returnValue / (total * log2);
/* 188:    */   }
/* 189:    */   
/* 190:    */   public static double entropyConditionedOnRows(double[][] train, double[][] test, double numClasses)
/* 191:    */   {
/* 192:279 */     double returnValue = 0.0D;double testSum = 0.0D;
/* 193:281 */     for (int i = 0; i < test.length; i++)
/* 194:    */     {
/* 195:282 */       double trainSumForRow = 0.0D;
/* 196:283 */       double testSumForRow = 0.0D;
/* 197:284 */       for (int j = 0; j < test[0].length; j++)
/* 198:    */       {
/* 199:285 */         returnValue -= test[i][j] * Math.log(train[i][j] + 1.0D);
/* 200:286 */         trainSumForRow += train[i][j];
/* 201:287 */         testSumForRow += test[i][j];
/* 202:    */       }
/* 203:289 */       testSum = testSumForRow;
/* 204:290 */       returnValue += testSumForRow * Math.log(trainSumForRow + numClasses);
/* 205:    */     }
/* 206:293 */     return returnValue / (testSum * log2);
/* 207:    */   }
/* 208:    */   
/* 209:    */   public static double entropyOverRows(double[][] matrix)
/* 210:    */   {
/* 211:304 */     double returnValue = 0.0D;double total = 0.0D;
/* 212:306 */     for (int i = 0; i < matrix.length; i++)
/* 213:    */     {
/* 214:307 */       double sumForRow = 0.0D;
/* 215:308 */       for (int j = 0; j < matrix[0].length; j++) {
/* 216:309 */         sumForRow += matrix[i][j];
/* 217:    */       }
/* 218:311 */       returnValue -= lnFunc(sumForRow);
/* 219:312 */       total += sumForRow;
/* 220:    */     }
/* 221:314 */     if (Utils.eq(total, 0.0D)) {
/* 222:315 */       return 0.0D;
/* 223:    */     }
/* 224:317 */     return (returnValue + lnFunc(total)) / (total * log2);
/* 225:    */   }
/* 226:    */   
/* 227:    */   public static double entropyOverColumns(double[][] matrix)
/* 228:    */   {
/* 229:328 */     double returnValue = 0.0D;double total = 0.0D;
/* 230:330 */     for (int j = 0; j < matrix[0].length; j++)
/* 231:    */     {
/* 232:331 */       double sumForColumn = 0.0D;
/* 233:332 */       for (int i = 0; i < matrix.length; i++) {
/* 234:333 */         sumForColumn += matrix[i][j];
/* 235:    */       }
/* 236:335 */       returnValue -= lnFunc(sumForColumn);
/* 237:336 */       total += sumForColumn;
/* 238:    */     }
/* 239:338 */     if (Utils.eq(total, 0.0D)) {
/* 240:339 */       return 0.0D;
/* 241:    */     }
/* 242:341 */     return (returnValue + lnFunc(total)) / (total * log2);
/* 243:    */   }
/* 244:    */   
/* 245:    */   public static double gainRatio(double[][] matrix)
/* 246:    */   {
/* 247:353 */     double preSplit = 0.0D;double postSplit = 0.0D;double splitEnt = 0.0D;
/* 248:354 */     double total = 0.0D;
/* 249:357 */     for (int i = 0; i < matrix[0].length; i++)
/* 250:    */     {
/* 251:358 */       double sumForColumn = 0.0D;
/* 252:359 */       for (int j = 0; j < matrix.length; j++) {
/* 253:360 */         sumForColumn += matrix[j][i];
/* 254:    */       }
/* 255:361 */       preSplit += lnFunc(sumForColumn);
/* 256:362 */       total += sumForColumn;
/* 257:    */     }
/* 258:364 */     preSplit -= lnFunc(total);
/* 259:367 */     for (int i = 0; i < matrix.length; i++)
/* 260:    */     {
/* 261:368 */       double sumForRow = 0.0D;
/* 262:369 */       for (int j = 0; j < matrix[0].length; j++)
/* 263:    */       {
/* 264:370 */         postSplit += lnFunc(matrix[i][j]);
/* 265:371 */         sumForRow += matrix[i][j];
/* 266:    */       }
/* 267:373 */       splitEnt += lnFunc(sumForRow);
/* 268:    */     }
/* 269:375 */     postSplit -= splitEnt;
/* 270:376 */     splitEnt -= lnFunc(total);
/* 271:    */     
/* 272:378 */     double infoGain = preSplit - postSplit;
/* 273:379 */     if (Utils.eq(splitEnt, 0.0D)) {
/* 274:380 */       return 0.0D;
/* 275:    */     }
/* 276:381 */     return infoGain / splitEnt;
/* 277:    */   }
/* 278:    */   
/* 279:    */   public static double log2MultipleHypergeometric(double[][] matrix)
/* 280:    */   {
/* 281:393 */     double sum = 0.0D;double total = 0.0D;
/* 282:395 */     for (int i = 0; i < matrix.length; i++)
/* 283:    */     {
/* 284:396 */       double sumForRow = 0.0D;
/* 285:397 */       for (int j = 0; j < matrix[i].length; j++) {
/* 286:398 */         sumForRow += matrix[i][j];
/* 287:    */       }
/* 288:400 */       sum += SpecialFunctions.lnFactorial(sumForRow);
/* 289:401 */       total += sumForRow;
/* 290:    */     }
/* 291:403 */     for (int j = 0; j < matrix[0].length; j++)
/* 292:    */     {
/* 293:404 */       double sumForColumn = 0.0D;
/* 294:405 */       for (int i = 0; i < matrix.length; i++) {
/* 295:406 */         sumForColumn += matrix[i][j];
/* 296:    */       }
/* 297:408 */       sum += SpecialFunctions.lnFactorial(sumForColumn);
/* 298:    */     }
/* 299:410 */     for (int i = 0; i < matrix.length; i++) {
/* 300:411 */       for (int j = 0; j < matrix[i].length; j++) {
/* 301:412 */         sum -= SpecialFunctions.lnFactorial(matrix[i][j]);
/* 302:    */       }
/* 303:    */     }
/* 304:415 */     sum -= SpecialFunctions.lnFactorial(total);
/* 305:416 */     return -sum / log2;
/* 306:    */   }
/* 307:    */   
/* 308:    */   public static double[][] reduceMatrix(double[][] matrix)
/* 309:    */   {
/* 310:428 */     int nonZeroRows = 0;int nonZeroColumns = 0;
/* 311:    */     
/* 312:    */ 
/* 313:    */ 
/* 314:432 */     int nrows = matrix.length;
/* 315:433 */     int ncols = matrix[0].length;
/* 316:434 */     double[] rtotal = new double[nrows];
/* 317:435 */     double[] ctotal = new double[ncols];
/* 318:436 */     for (int row = 0; row < nrows; row++) {
/* 319:437 */       for (int col = 0; col < ncols; col++)
/* 320:    */       {
/* 321:438 */         rtotal[row] += matrix[row][col];
/* 322:439 */         ctotal[col] += matrix[row][col];
/* 323:    */       }
/* 324:    */     }
/* 325:442 */     for (row = 0; row < nrows; row++) {
/* 326:443 */       if (Utils.gr(rtotal[row], 0.0D)) {
/* 327:444 */         nonZeroRows++;
/* 328:    */       }
/* 329:    */     }
/* 330:447 */     for (int col = 0; col < ncols; col++) {
/* 331:448 */       if (Utils.gr(ctotal[col], 0.0D)) {
/* 332:449 */         nonZeroColumns++;
/* 333:    */       }
/* 334:    */     }
/* 335:452 */     double[][] newMatrix = new double[nonZeroRows][nonZeroColumns];
/* 336:453 */     int currRow = 0;
/* 337:454 */     for (row = 0; row < nrows; row++) {
/* 338:455 */       if (Utils.gr(rtotal[row], 0.0D))
/* 339:    */       {
/* 340:456 */         int currCol = 0;
/* 341:457 */         for (col = 0; col < ncols; col++) {
/* 342:458 */           if (Utils.gr(ctotal[col], 0.0D))
/* 343:    */           {
/* 344:459 */             newMatrix[currRow][currCol] = matrix[row][col];
/* 345:460 */             currCol++;
/* 346:    */           }
/* 347:    */         }
/* 348:463 */         currRow++;
/* 349:    */       }
/* 350:    */     }
/* 351:466 */     return newMatrix;
/* 352:    */   }
/* 353:    */   
/* 354:    */   public static double symmetricalUncertainty(double[][] matrix)
/* 355:    */   {
/* 356:478 */     double total = 0.0D;double columnEntropy = 0.0D;
/* 357:479 */     double rowEntropy = 0.0D;double entropyConditionedOnRows = 0.0D;double infoGain = 0.0D;
/* 358:482 */     for (int i = 0; i < matrix[0].length; i++)
/* 359:    */     {
/* 360:483 */       double sumForColumn = 0.0D;
/* 361:484 */       for (int j = 0; j < matrix.length; j++) {
/* 362:485 */         sumForColumn += matrix[j][i];
/* 363:    */       }
/* 364:487 */       columnEntropy += lnFunc(sumForColumn);
/* 365:488 */       total += sumForColumn;
/* 366:    */     }
/* 367:490 */     columnEntropy -= lnFunc(total);
/* 368:493 */     for (int i = 0; i < matrix.length; i++)
/* 369:    */     {
/* 370:494 */       double sumForRow = 0.0D;
/* 371:495 */       for (int j = 0; j < matrix[0].length; j++)
/* 372:    */       {
/* 373:496 */         sumForRow += matrix[i][j];
/* 374:497 */         entropyConditionedOnRows += lnFunc(matrix[i][j]);
/* 375:    */       }
/* 376:499 */       rowEntropy += lnFunc(sumForRow);
/* 377:    */     }
/* 378:501 */     entropyConditionedOnRows -= rowEntropy;
/* 379:502 */     rowEntropy -= lnFunc(total);
/* 380:503 */     infoGain = columnEntropy - entropyConditionedOnRows;
/* 381:504 */     if ((Utils.eq(columnEntropy, 0.0D)) || (Utils.eq(rowEntropy, 0.0D))) {
/* 382:505 */       return 0.0D;
/* 383:    */     }
/* 384:506 */     return 2.0D * (infoGain / (columnEntropy + rowEntropy));
/* 385:    */   }
/* 386:    */   
/* 387:    */   public static double tauVal(double[][] matrix)
/* 388:    */   {
/* 389:519 */     double maxcol = 0.0D;double maxtotal = 0.0D;double n = 0.0D;
/* 390:    */     
/* 391:521 */     int nrows = matrix.length;
/* 392:522 */     int ncols = matrix[0].length;
/* 393:523 */     double[] ctotal = new double[ncols];
/* 394:524 */     for (int row = 0; row < nrows; row++)
/* 395:    */     {
/* 396:525 */       double max = 0.0D;
/* 397:526 */       for (int col = 0; col < ncols; col++)
/* 398:    */       {
/* 399:527 */         if (Utils.gr(matrix[row][col], max)) {
/* 400:528 */           max = matrix[row][col];
/* 401:    */         }
/* 402:529 */         ctotal[col] += matrix[row][col];
/* 403:530 */         n += matrix[row][col];
/* 404:    */       }
/* 405:532 */       maxtotal += max;
/* 406:    */     }
/* 407:534 */     if (Utils.eq(n, 0.0D)) {
/* 408:535 */       return 0.0D;
/* 409:    */     }
/* 410:537 */     maxcol = ctotal[Utils.maxIndex(ctotal)];
/* 411:538 */     return (maxtotal - maxcol) / (n - maxcol);
/* 412:    */   }
/* 413:    */   
/* 414:    */   public static double lnFunc(double num)
/* 415:    */   {
/* 416:546 */     if (num <= 0.0D) {
/* 417:547 */       return 0.0D;
/* 418:    */     }
/* 419:551 */     if (num < 10000.0D)
/* 420:    */     {
/* 421:552 */       int n = (int)num;
/* 422:553 */       if (n == num) {
/* 423:554 */         return INT_N_LOG_N_CACHE[n];
/* 424:    */       }
/* 425:    */     }
/* 426:557 */     return num * Math.log(num);
/* 427:    */   }
/* 428:    */   
/* 429:    */   private static double chiCell(double freq, double expected, boolean yates)
/* 430:    */   {
/* 431:573 */     if (Utils.smOrEq(expected, 0.0D)) {
/* 432:574 */       return 0.0D;
/* 433:    */     }
/* 434:578 */     double diff = Math.abs(freq - expected);
/* 435:579 */     if (yates)
/* 436:    */     {
/* 437:582 */       diff -= 0.5D;
/* 438:585 */       if (diff < 0.0D) {
/* 439:586 */         diff = 0.0D;
/* 440:    */       }
/* 441:    */     }
/* 442:591 */     return diff * diff / expected;
/* 443:    */   }
/* 444:    */   
/* 445:    */   public String getRevision()
/* 446:    */   {
/* 447:600 */     return RevisionUtils.extract("$Revision: 10057 $");
/* 448:    */   }
/* 449:    */   
/* 450:    */   public static void main(String[] ops)
/* 451:    */   {
/* 452:608 */     double[] firstRow = { 10.0D, 5.0D, 20.0D };
/* 453:609 */     double[] secondRow = { 2.0D, 10.0D, 6.0D };
/* 454:610 */     double[] thirdRow = { 5.0D, 10.0D, 10.0D };
/* 455:611 */     double[][] matrix = new double[3][0];
/* 456:    */     
/* 457:613 */     matrix[0] = firstRow;matrix[1] = secondRow;matrix[2] = thirdRow;
/* 458:614 */     for (int i = 0; i < matrix.length; i++)
/* 459:    */     {
/* 460:615 */       for (int j = 0; j < matrix[i].length; j++) {
/* 461:616 */         System.out.print(matrix[i][j] + " ");
/* 462:    */       }
/* 463:618 */       System.out.println();
/* 464:    */     }
/* 465:620 */     System.out.println("Chi-squared probability: " + chiSquared(matrix, false));
/* 466:    */     
/* 467:622 */     System.out.println("Chi-squared value: " + chiVal(matrix, false));
/* 468:    */     
/* 469:624 */     System.out.println("Cochran's criterion fullfilled: " + cochransCriterion(matrix));
/* 470:    */     
/* 471:626 */     System.out.println("Cramer's V: " + CramersV(matrix));
/* 472:    */     
/* 473:628 */     System.out.println("Entropy of first row: " + entropy(firstRow));
/* 474:    */     
/* 475:630 */     System.out.println("Entropy conditioned on columns: " + entropyConditionedOnColumns(matrix));
/* 476:    */     
/* 477:632 */     System.out.println("Entropy conditioned on rows: " + entropyConditionedOnRows(matrix));
/* 478:    */     
/* 479:634 */     System.out.println("Entropy conditioned on rows (with Laplace): " + entropyConditionedOnRows(matrix, matrix, 3.0D));
/* 480:    */     
/* 481:636 */     System.out.println("Entropy of rows: " + entropyOverRows(matrix));
/* 482:    */     
/* 483:638 */     System.out.println("Entropy of columns: " + entropyOverColumns(matrix));
/* 484:    */     
/* 485:640 */     System.out.println("Gain ratio: " + gainRatio(matrix));
/* 486:    */     
/* 487:642 */     System.out.println("Negative log2 of multiple hypergeometric probability: " + log2MultipleHypergeometric(matrix));
/* 488:    */     
/* 489:644 */     System.out.println("Symmetrical uncertainty: " + symmetricalUncertainty(matrix));
/* 490:    */     
/* 491:646 */     System.out.println("Tau value: " + tauVal(matrix));
/* 492:    */     
/* 493:648 */     double[][] newMatrix = new double[3][3];
/* 494:649 */     newMatrix[0][0] = 1.0D;newMatrix[0][1] = 0.0D;newMatrix[0][2] = 1.0D;
/* 495:650 */     newMatrix[1][0] = 0.0D;newMatrix[1][1] = 0.0D;newMatrix[1][2] = 0.0D;
/* 496:651 */     newMatrix[2][0] = 1.0D;newMatrix[2][1] = 0.0D;newMatrix[2][2] = 1.0D;
/* 497:652 */     System.out.println("Matrix with empty row and column: ");
/* 498:653 */     for (int i = 0; i < newMatrix.length; i++)
/* 499:    */     {
/* 500:654 */       for (int j = 0; j < newMatrix[i].length; j++) {
/* 501:655 */         System.out.print(newMatrix[i][j] + " ");
/* 502:    */       }
/* 503:657 */       System.out.println();
/* 504:    */     }
/* 505:659 */     System.out.println("Reduced matrix: ");
/* 506:660 */     newMatrix = reduceMatrix(newMatrix);
/* 507:661 */     for (int i = 0; i < newMatrix.length; i++)
/* 508:    */     {
/* 509:662 */       for (int j = 0; j < newMatrix[i].length; j++) {
/* 510:663 */         System.out.print(newMatrix[i][j] + " ");
/* 511:    */       }
/* 512:665 */       System.out.println();
/* 513:    */     }
/* 514:    */   }
/* 515:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.ContingencyTables
 * JD-Core Version:    0.7.0.1
 */