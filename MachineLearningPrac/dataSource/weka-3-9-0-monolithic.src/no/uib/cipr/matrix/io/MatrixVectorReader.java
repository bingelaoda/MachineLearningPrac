/*   1:    */ package no.uib.cipr.matrix.io;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.EOFException;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.Reader;
/*   7:    */ import java.io.StreamTokenizer;
/*   8:    */ import java.util.LinkedList;
/*   9:    */ import java.util.List;
/*  10:    */ 
/*  11:    */ public class MatrixVectorReader
/*  12:    */   extends BufferedReader
/*  13:    */ {
/*  14:    */   private StreamTokenizer st;
/*  15:    */   
/*  16:    */   public MatrixVectorReader(Reader in)
/*  17:    */   {
/*  18: 48 */     super(in);
/*  19: 49 */     setup();
/*  20:    */   }
/*  21:    */   
/*  22:    */   public MatrixVectorReader(Reader in, int sz)
/*  23:    */   {
/*  24: 61 */     super(in, sz);
/*  25: 62 */     setup();
/*  26:    */   }
/*  27:    */   
/*  28:    */   private void setup()
/*  29:    */   {
/*  30: 69 */     this.st = new StreamTokenizer(this);
/*  31: 70 */     this.st.resetSyntax();
/*  32: 71 */     this.st.eolIsSignificant(false);
/*  33: 72 */     this.st.lowerCaseMode(true);
/*  34:    */     
/*  35:    */ 
/*  36: 75 */     this.st.wordChars(48, 57);
/*  37: 76 */     this.st.wordChars(45, 46);
/*  38:    */     
/*  39:    */ 
/*  40: 79 */     this.st.wordChars(0, 255);
/*  41:    */     
/*  42:    */ 
/*  43: 82 */     this.st.commentChar(37);
/*  44:    */     
/*  45:    */ 
/*  46: 85 */     this.st.whitespaceChars(32, 32);
/*  47: 86 */     this.st.whitespaceChars(9, 14);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void add(int num, int[] indices)
/*  51:    */   {
/*  52: 99 */     for (int i = 0; i < indices.length; i++) {
/*  53:100 */       indices[i] += num;
/*  54:    */     }
/*  55:    */   }
/*  56:    */   
/*  57:    */   private String readTrimmedLine()
/*  58:    */     throws IOException
/*  59:    */   {
/*  60:110 */     String line = readLine();
/*  61:111 */     if (line != null) {
/*  62:112 */       return line.trim();
/*  63:    */     }
/*  64:114 */     throw new EOFException();
/*  65:    */   }
/*  66:    */   
/*  67:    */   public MatrixInfo readMatrixInfo()
/*  68:    */     throws IOException
/*  69:    */   {
/*  70:123 */     String[] component = readTrimmedLine().split(" +");
/*  71:124 */     if (component.length != 5) {
/*  72:125 */       throw new IOException("Current line unparsable. It must consist of 5 tokens");
/*  73:    */     }
/*  74:129 */     if (!component[0].equalsIgnoreCase("%%MatrixMarket")) {
/*  75:130 */       throw new IOException("Not in Matrix Market exchange format");
/*  76:    */     }
/*  77:133 */     if (!component[1].equalsIgnoreCase("matrix")) {
/*  78:134 */       throw new IOException("Expected \"matrix\", got " + component[1]);
/*  79:    */     }
/*  80:137 */     boolean sparse = false;
/*  81:138 */     if (component[2].equalsIgnoreCase("coordinate")) {
/*  82:139 */       sparse = true;
/*  83:140 */     } else if (component[2].equalsIgnoreCase("array")) {
/*  84:141 */       sparse = false;
/*  85:    */     } else {
/*  86:143 */       throw new IOException("Unknown layout " + component[2]);
/*  87:    */     }
/*  88:146 */     MatrixInfo.MatrixField field = null;
/*  89:147 */     if (component[3].equalsIgnoreCase("real")) {
/*  90:148 */       field = MatrixInfo.MatrixField.Real;
/*  91:149 */     } else if (component[3].equalsIgnoreCase("integer")) {
/*  92:150 */       field = MatrixInfo.MatrixField.Integer;
/*  93:151 */     } else if (component[3].equalsIgnoreCase("complex")) {
/*  94:152 */       field = MatrixInfo.MatrixField.Complex;
/*  95:153 */     } else if (component[3].equalsIgnoreCase("pattern")) {
/*  96:154 */       field = MatrixInfo.MatrixField.Pattern;
/*  97:    */     } else {
/*  98:156 */       throw new IOException("Unknown field specification " + component[3]);
/*  99:    */     }
/* 100:159 */     MatrixInfo.MatrixSymmetry symmetry = null;
/* 101:160 */     if (component[4].equalsIgnoreCase("general")) {
/* 102:161 */       symmetry = MatrixInfo.MatrixSymmetry.General;
/* 103:162 */     } else if (component[4].equalsIgnoreCase("symmetric")) {
/* 104:163 */       symmetry = MatrixInfo.MatrixSymmetry.Symmetric;
/* 105:164 */     } else if (component[4].equalsIgnoreCase("skew-symmetric")) {
/* 106:165 */       symmetry = MatrixInfo.MatrixSymmetry.SkewSymmetric;
/* 107:166 */     } else if (component[4].equalsIgnoreCase("Hermitian")) {
/* 108:167 */       symmetry = MatrixInfo.MatrixSymmetry.Hermitian;
/* 109:    */     } else {
/* 110:169 */       throw new IOException("Unknown symmetry specification " + component[4]);
/* 111:    */     }
/* 112:173 */     return new MatrixInfo(sparse, field, symmetry);
/* 113:    */   }
/* 114:    */   
/* 115:    */   public VectorInfo readVectorInfo()
/* 116:    */     throws IOException
/* 117:    */   {
/* 118:182 */     String[] component = readTrimmedLine().split(" +");
/* 119:183 */     if (component.length != 4) {
/* 120:184 */       throw new IOException("Current line unparsable. It must consist of 4 tokens");
/* 121:    */     }
/* 122:188 */     if (!component[0].equalsIgnoreCase("%%MatrixMarket")) {
/* 123:189 */       throw new IOException("Not in Matrix Market exchange format");
/* 124:    */     }
/* 125:192 */     if (!component[1].equalsIgnoreCase("vector")) {
/* 126:193 */       throw new IOException("Expected \"vector\", got " + component[1]);
/* 127:    */     }
/* 128:196 */     boolean sparse = false;
/* 129:197 */     if (component[2].equalsIgnoreCase("coordinate")) {
/* 130:198 */       sparse = true;
/* 131:199 */     } else if (component[2].equalsIgnoreCase("array")) {
/* 132:200 */       sparse = false;
/* 133:    */     } else {
/* 134:202 */       throw new IOException("Unknown layout " + component[2]);
/* 135:    */     }
/* 136:205 */     VectorInfo.VectorField field = null;
/* 137:206 */     if (component[3].equalsIgnoreCase("real")) {
/* 138:207 */       field = VectorInfo.VectorField.Real;
/* 139:208 */     } else if (component[3].equalsIgnoreCase("integer")) {
/* 140:209 */       field = VectorInfo.VectorField.Integer;
/* 141:210 */     } else if (component[3].equalsIgnoreCase("complex")) {
/* 142:211 */       field = VectorInfo.VectorField.Complex;
/* 143:212 */     } else if (component[3].equalsIgnoreCase("pattern")) {
/* 144:213 */       field = VectorInfo.VectorField.Pattern;
/* 145:    */     } else {
/* 146:215 */       throw new IOException("Unknown field specification " + component[3]);
/* 147:    */     }
/* 148:218 */     return new VectorInfo(sparse, field);
/* 149:    */   }
/* 150:    */   
/* 151:    */   public boolean hasInfo()
/* 152:    */     throws IOException
/* 153:    */   {
/* 154:229 */     mark(1024);
/* 155:230 */     String[] component = readTrimmedLine().split(" +");
/* 156:231 */     reset();
/* 157:    */     
/* 158:233 */     return component[0].equalsIgnoreCase("%%MatrixMarket");
/* 159:    */   }
/* 160:    */   
/* 161:    */   public String[] readComments()
/* 162:    */     throws IOException
/* 163:    */   {
/* 164:242 */     List<String> list = new LinkedList();
/* 165:    */     for (;;)
/* 166:    */     {
/* 167:244 */       mark(1024);
/* 168:245 */       String line = readTrimmedLine();
/* 169:246 */       if (line.length() > 0)
/* 170:    */       {
/* 171:247 */         if (line.charAt(0) != '%')
/* 172:    */         {
/* 173:248 */           reset();
/* 174:249 */           break;
/* 175:    */         }
/* 176:251 */         list.add(line.substring(1));
/* 177:    */       }
/* 178:    */     }
/* 179:253 */     return (String[])list.toArray(new String[list.size()]);
/* 180:    */   }
/* 181:    */   
/* 182:    */   public MatrixSize readMatrixSize(MatrixInfo info)
/* 183:    */     throws IOException
/* 184:    */   {
/* 185:261 */     int numRows = getInt();int numColumns = getInt();
/* 186:264 */     if (info.isDense()) {
/* 187:265 */       return new MatrixSize(numRows, numColumns, info);
/* 188:    */     }
/* 189:267 */     int numEntries = getInt();
/* 190:268 */     return new MatrixSize(numRows, numColumns, numEntries);
/* 191:    */   }
/* 192:    */   
/* 193:    */   public MatrixSize readArraySize()
/* 194:    */     throws IOException
/* 195:    */   {
/* 196:276 */     int numRows = getInt();int numColumns = getInt();
/* 197:    */     
/* 198:278 */     return new MatrixSize(numRows, numColumns, numRows * numColumns);
/* 199:    */   }
/* 200:    */   
/* 201:    */   public MatrixSize readCoordinateSize()
/* 202:    */     throws IOException
/* 203:    */   {
/* 204:285 */     int numRows = getInt();int numColumns = getInt();int numEntries = getInt();
/* 205:    */     
/* 206:287 */     return new MatrixSize(numRows, numColumns, numEntries);
/* 207:    */   }
/* 208:    */   
/* 209:    */   public VectorSize readVectorSize(VectorInfo info)
/* 210:    */     throws IOException
/* 211:    */   {
/* 212:295 */     int size = getInt();
/* 213:298 */     if (info.isDense()) {
/* 214:299 */       return new VectorSize(size);
/* 215:    */     }
/* 216:301 */     int numEntries = getInt();
/* 217:302 */     return new VectorSize(size, numEntries);
/* 218:    */   }
/* 219:    */   
/* 220:    */   public VectorSize readVectorArraySize()
/* 221:    */     throws IOException
/* 222:    */   {
/* 223:310 */     int size = getInt();
/* 224:    */     
/* 225:312 */     return new VectorSize(size);
/* 226:    */   }
/* 227:    */   
/* 228:    */   public VectorSize readVectorCoordinateSize()
/* 229:    */     throws IOException
/* 230:    */   {
/* 231:319 */     int size = getInt();int numEntries = getInt();
/* 232:    */     
/* 233:321 */     return new VectorSize(size, numEntries);
/* 234:    */   }
/* 235:    */   
/* 236:    */   public void readArray(double[] data)
/* 237:    */     throws IOException
/* 238:    */   {
/* 239:328 */     int size = data.length;
/* 240:329 */     for (int i = 0; i < size; i++) {
/* 241:330 */       data[i] = getDouble();
/* 242:    */     }
/* 243:    */   }
/* 244:    */   
/* 245:    */   public void readArray(float[] data)
/* 246:    */     throws IOException
/* 247:    */   {
/* 248:337 */     int size = data.length;
/* 249:338 */     for (int i = 0; i < size; i++) {
/* 250:339 */       data[i] = getFloat();
/* 251:    */     }
/* 252:    */   }
/* 253:    */   
/* 254:    */   public void readArray(int[] data)
/* 255:    */     throws IOException
/* 256:    */   {
/* 257:346 */     int size = data.length;
/* 258:347 */     for (int i = 0; i < size; i++) {
/* 259:348 */       data[i] = getInt();
/* 260:    */     }
/* 261:    */   }
/* 262:    */   
/* 263:    */   public void readArray(long[] data)
/* 264:    */     throws IOException
/* 265:    */   {
/* 266:355 */     int size = data.length;
/* 267:356 */     for (int i = 0; i < size; i++) {
/* 268:357 */       data[i] = getLong();
/* 269:    */     }
/* 270:    */   }
/* 271:    */   
/* 272:    */   public void readArray(double[] dataR, double[] dataI)
/* 273:    */     throws IOException
/* 274:    */   {
/* 275:365 */     int size = dataR.length;
/* 276:366 */     if (size != dataI.length) {
/* 277:367 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 278:    */     }
/* 279:369 */     for (int i = 0; i < size; i++)
/* 280:    */     {
/* 281:370 */       dataR[i] = getDouble();
/* 282:371 */       dataI[i] = getDouble();
/* 283:    */     }
/* 284:    */   }
/* 285:    */   
/* 286:    */   public void readArray(float[] dataR, float[] dataI)
/* 287:    */     throws IOException
/* 288:    */   {
/* 289:380 */     int size = dataR.length;
/* 290:381 */     if (size != dataI.length) {
/* 291:382 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 292:    */     }
/* 293:384 */     for (int i = 0; i < size; i++)
/* 294:    */     {
/* 295:385 */       dataR[i] = getFloat();
/* 296:386 */       dataI[i] = getFloat();
/* 297:    */     }
/* 298:    */   }
/* 299:    */   
/* 300:    */   public void readCoordinate(int[] index, double[] data)
/* 301:    */     throws IOException
/* 302:    */   {
/* 303:394 */     int size = index.length;
/* 304:395 */     if (size != data.length) {
/* 305:396 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 306:    */     }
/* 307:398 */     for (int i = 0; i < size; i++)
/* 308:    */     {
/* 309:399 */       index[i] = getInt();
/* 310:400 */       data[i] = getDouble();
/* 311:    */     }
/* 312:    */   }
/* 313:    */   
/* 314:    */   public void readCoordinate(int[] index, float[] data)
/* 315:    */     throws IOException
/* 316:    */   {
/* 317:408 */     int size = index.length;
/* 318:409 */     if (size != data.length) {
/* 319:410 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 320:    */     }
/* 321:412 */     for (int i = 0; i < size; i++)
/* 322:    */     {
/* 323:413 */       index[i] = getInt();
/* 324:414 */       data[i] = getFloat();
/* 325:    */     }
/* 326:    */   }
/* 327:    */   
/* 328:    */   public void readCoordinate(int[] index, int[] data)
/* 329:    */     throws IOException
/* 330:    */   {
/* 331:422 */     int size = index.length;
/* 332:423 */     if (size != data.length) {
/* 333:424 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 334:    */     }
/* 335:426 */     for (int i = 0; i < size; i++)
/* 336:    */     {
/* 337:427 */       index[i] = getInt();
/* 338:428 */       data[i] = getInt();
/* 339:    */     }
/* 340:    */   }
/* 341:    */   
/* 342:    */   public void readCoordinate(int[] index, long[] data)
/* 343:    */     throws IOException
/* 344:    */   {
/* 345:436 */     int size = index.length;
/* 346:437 */     if (size != data.length) {
/* 347:438 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 348:    */     }
/* 349:440 */     for (int i = 0; i < size; i++)
/* 350:    */     {
/* 351:441 */       index[i] = getInt();
/* 352:442 */       data[i] = getLong();
/* 353:    */     }
/* 354:    */   }
/* 355:    */   
/* 356:    */   public void readCoordinate(int[] index, float[] dataR, float[] dataI)
/* 357:    */     throws IOException
/* 358:    */   {
/* 359:452 */     int size = index.length;
/* 360:453 */     if ((size != dataR.length) || (size != dataI.length)) {
/* 361:454 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 362:    */     }
/* 363:456 */     for (int i = 0; i < size; i++)
/* 364:    */     {
/* 365:457 */       index[i] = getInt();
/* 366:458 */       dataR[i] = getFloat();
/* 367:459 */       dataI[i] = getFloat();
/* 368:    */     }
/* 369:    */   }
/* 370:    */   
/* 371:    */   public void readCoordinate(int[] index, double[] dataR, double[] dataI)
/* 372:    */     throws IOException
/* 373:    */   {
/* 374:469 */     int size = index.length;
/* 375:470 */     if ((size != dataR.length) || (size != dataI.length)) {
/* 376:471 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 377:    */     }
/* 378:473 */     for (int i = 0; i < size; i++)
/* 379:    */     {
/* 380:474 */       index[i] = getInt();
/* 381:475 */       dataR[i] = getDouble();
/* 382:476 */       dataI[i] = getDouble();
/* 383:    */     }
/* 384:    */   }
/* 385:    */   
/* 386:    */   public void readPattern(int[] index)
/* 387:    */     throws IOException
/* 388:    */   {
/* 389:484 */     int size = index.length;
/* 390:485 */     for (int i = 0; i < size; i++) {
/* 391:486 */       index[i] = getInt();
/* 392:    */     }
/* 393:    */   }
/* 394:    */   
/* 395:    */   public void readCoordinate(int[] row, int[] column, double[] data)
/* 396:    */     throws IOException
/* 397:    */   {
/* 398:494 */     int size = row.length;
/* 399:495 */     if ((size != column.length) || (size != data.length)) {
/* 400:496 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 401:    */     }
/* 402:498 */     for (int i = 0; i < size; i++)
/* 403:    */     {
/* 404:499 */       row[i] = getInt();
/* 405:500 */       column[i] = getInt();
/* 406:501 */       data[i] = getDouble();
/* 407:    */     }
/* 408:    */   }
/* 409:    */   
/* 410:    */   public void readCoordinate(int[] row, int[] column, float[] data)
/* 411:    */     throws IOException
/* 412:    */   {
/* 413:510 */     int size = row.length;
/* 414:511 */     if ((size != column.length) || (size != data.length)) {
/* 415:512 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 416:    */     }
/* 417:514 */     for (int i = 0; i < size; i++)
/* 418:    */     {
/* 419:515 */       row[i] = getInt();
/* 420:516 */       column[i] = getInt();
/* 421:517 */       data[i] = getFloat();
/* 422:    */     }
/* 423:    */   }
/* 424:    */   
/* 425:    */   public void readCoordinate(int[] row, int[] column, int[] data)
/* 426:    */     throws IOException
/* 427:    */   {
/* 428:526 */     int size = row.length;
/* 429:527 */     if ((size != column.length) || (size != data.length)) {
/* 430:528 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 431:    */     }
/* 432:530 */     for (int i = 0; i < size; i++)
/* 433:    */     {
/* 434:531 */       row[i] = getInt();
/* 435:532 */       column[i] = getInt();
/* 436:533 */       data[i] = getInt();
/* 437:    */     }
/* 438:    */   }
/* 439:    */   
/* 440:    */   public void readCoordinate(int[] row, int[] column, long[] data)
/* 441:    */     throws IOException
/* 442:    */   {
/* 443:542 */     int size = row.length;
/* 444:543 */     if ((size != column.length) || (size != data.length)) {
/* 445:544 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 446:    */     }
/* 447:546 */     for (int i = 0; i < size; i++)
/* 448:    */     {
/* 449:547 */       row[i] = getInt();
/* 450:548 */       column[i] = getInt();
/* 451:549 */       data[i] = getLong();
/* 452:    */     }
/* 453:    */   }
/* 454:    */   
/* 455:    */   public void readPattern(int[] row, int[] column)
/* 456:    */     throws IOException
/* 457:    */   {
/* 458:557 */     int size = row.length;
/* 459:558 */     if (size != column.length) {
/* 460:559 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 461:    */     }
/* 462:561 */     for (int i = 0; i < size; i++)
/* 463:    */     {
/* 464:562 */       row[i] = getInt();
/* 465:563 */       column[i] = getInt();
/* 466:    */     }
/* 467:    */   }
/* 468:    */   
/* 469:    */   public void readCoordinate(int[] row, int[] column, double[] dataR, double[] dataI)
/* 470:    */     throws IOException
/* 471:    */   {
/* 472:573 */     int size = row.length;
/* 473:574 */     if ((size != column.length) || (size != dataR.length) || (size != dataI.length)) {
/* 474:576 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 475:    */     }
/* 476:578 */     for (int i = 0; i < size; i++)
/* 477:    */     {
/* 478:579 */       row[i] = getInt();
/* 479:580 */       column[i] = getInt();
/* 480:581 */       dataR[i] = getDouble();
/* 481:582 */       dataI[i] = getDouble();
/* 482:    */     }
/* 483:    */   }
/* 484:    */   
/* 485:    */   public void readCoordinate(int[] row, int[] column, float[] dataR, float[] dataI)
/* 486:    */     throws IOException
/* 487:    */   {
/* 488:592 */     int size = row.length;
/* 489:593 */     if ((size != column.length) || (size != dataR.length) || (size != dataI.length)) {
/* 490:595 */       throw new IllegalArgumentException("All arrays must be of the same size");
/* 491:    */     }
/* 492:597 */     for (int i = 0; i < size; i++)
/* 493:    */     {
/* 494:598 */       row[i] = getInt();
/* 495:599 */       column[i] = getInt();
/* 496:600 */       dataR[i] = getFloat();
/* 497:601 */       dataI[i] = getFloat();
/* 498:    */     }
/* 499:    */   }
/* 500:    */   
/* 501:    */   private int getInt()
/* 502:    */     throws IOException
/* 503:    */   {
/* 504:609 */     this.st.nextToken();
/* 505:610 */     if (this.st.ttype == -3) {
/* 506:611 */       return Double.valueOf(this.st.sval).intValue();
/* 507:    */     }
/* 508:612 */     if (this.st.ttype == -1) {
/* 509:613 */       throw new EOFException("End-of-File encountered during parsing");
/* 510:    */     }
/* 511:615 */     throw new IOException("Unknown token found during parsing");
/* 512:    */   }
/* 513:    */   
/* 514:    */   private long getLong()
/* 515:    */     throws IOException
/* 516:    */   {
/* 517:622 */     this.st.nextToken();
/* 518:623 */     if (this.st.ttype == -3) {
/* 519:624 */       return Long.parseLong(this.st.sval);
/* 520:    */     }
/* 521:625 */     if (this.st.ttype == -1) {
/* 522:626 */       throw new EOFException("End-of-File encountered during parsing");
/* 523:    */     }
/* 524:628 */     throw new IOException("Unknown token found during parsing");
/* 525:    */   }
/* 526:    */   
/* 527:    */   private double getDouble()
/* 528:    */     throws IOException
/* 529:    */   {
/* 530:635 */     this.st.nextToken();
/* 531:636 */     if (this.st.ttype == -3) {
/* 532:637 */       return Double.parseDouble(this.st.sval);
/* 533:    */     }
/* 534:638 */     if (this.st.ttype == -1) {
/* 535:639 */       throw new EOFException("End-of-File encountered during parsing");
/* 536:    */     }
/* 537:641 */     throw new IOException("Unknown token found during parsing");
/* 538:    */   }
/* 539:    */   
/* 540:    */   private float getFloat()
/* 541:    */     throws IOException
/* 542:    */   {
/* 543:648 */     this.st.nextToken();
/* 544:649 */     if (this.st.ttype == -3) {
/* 545:650 */       return Float.parseFloat(this.st.sval);
/* 546:    */     }
/* 547:651 */     if (this.st.ttype == -1) {
/* 548:652 */       throw new EOFException("End-of-File encountered during parsing");
/* 549:    */     }
/* 550:654 */     throw new IOException("Unknown token found during parsing");
/* 551:    */   }
/* 552:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.io.MatrixVectorReader
 * JD-Core Version:    0.7.0.1
 */