/*   1:    */ package weka.classifiers.functions.neural;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Graphics;
/*   5:    */ import java.io.Serializable;
/*   6:    */ import weka.core.RevisionHandler;
/*   7:    */ 
/*   8:    */ public abstract class NeuralConnection
/*   9:    */   implements Serializable, RevisionHandler
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = -286208828571059163L;
/*  12:    */   public static final int UNCONNECTED = 0;
/*  13:    */   public static final int PURE_INPUT = 1;
/*  14:    */   public static final int PURE_OUTPUT = 2;
/*  15:    */   public static final int INPUT = 4;
/*  16:    */   public static final int OUTPUT = 8;
/*  17:    */   public static final int CONNECTED = 16;
/*  18:    */   protected NeuralConnection[] m_inputList;
/*  19:    */   protected NeuralConnection[] m_outputList;
/*  20:    */   protected int[] m_inputNums;
/*  21:    */   protected int[] m_outputNums;
/*  22:    */   protected int m_numInputs;
/*  23:    */   protected int m_numOutputs;
/*  24:    */   protected double m_unitValue;
/*  25:    */   protected double m_unitError;
/*  26:    */   protected boolean m_weightsUpdated;
/*  27:    */   protected String m_id;
/*  28:    */   protected int m_type;
/*  29:    */   protected double m_x;
/*  30:    */   protected double m_y;
/*  31:    */   
/*  32:    */   public NeuralConnection(String id)
/*  33:    */   {
/*  34:121 */     this.m_id = id;
/*  35:122 */     this.m_inputList = new NeuralConnection[0];
/*  36:123 */     this.m_outputList = new NeuralConnection[0];
/*  37:124 */     this.m_inputNums = new int[0];
/*  38:125 */     this.m_outputNums = new int[0];
/*  39:    */     
/*  40:127 */     this.m_numInputs = 0;
/*  41:128 */     this.m_numOutputs = 0;
/*  42:    */     
/*  43:130 */     this.m_unitValue = (0.0D / 0.0D);
/*  44:131 */     this.m_unitError = (0.0D / 0.0D);
/*  45:    */     
/*  46:133 */     this.m_weightsUpdated = false;
/*  47:134 */     this.m_x = 0.0D;
/*  48:135 */     this.m_y = 0.0D;
/*  49:136 */     this.m_type = 0;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public String getId()
/*  53:    */   {
/*  54:144 */     return this.m_id;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public int getType()
/*  58:    */   {
/*  59:151 */     return this.m_type;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setType(int t)
/*  63:    */   {
/*  64:158 */     this.m_type = t;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public abstract void reset();
/*  68:    */   
/*  69:    */   public abstract double outputValue(boolean paramBoolean);
/*  70:    */   
/*  71:    */   public abstract double errorValue(boolean paramBoolean);
/*  72:    */   
/*  73:    */   public abstract void saveWeights();
/*  74:    */   
/*  75:    */   public abstract void restoreWeights();
/*  76:    */   
/*  77:    */   public double weightValue(int n)
/*  78:    */   {
/*  79:207 */     return 1.0D;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void updateWeights(double l, double m)
/*  83:    */   {
/*  84:224 */     if (!this.m_weightsUpdated)
/*  85:    */     {
/*  86:225 */       for (int noa = 0; noa < this.m_numInputs; noa++) {
/*  87:226 */         this.m_inputList[noa].updateWeights(l, m);
/*  88:    */       }
/*  89:228 */       this.m_weightsUpdated = true;
/*  90:    */     }
/*  91:    */   }
/*  92:    */   
/*  93:    */   public NeuralConnection[] getInputs()
/*  94:    */   {
/*  95:240 */     return this.m_inputList;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public NeuralConnection[] getOutputs()
/*  99:    */   {
/* 100:250 */     return this.m_outputList;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public int[] getInputNums()
/* 104:    */   {
/* 105:260 */     return this.m_inputNums;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public int[] getOutputNums()
/* 109:    */   {
/* 110:270 */     return this.m_outputNums;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public double getX()
/* 114:    */   {
/* 115:277 */     return this.m_x;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public double getY()
/* 119:    */   {
/* 120:284 */     return this.m_y;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void setX(double x)
/* 124:    */   {
/* 125:291 */     this.m_x = x;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public void setY(double y)
/* 129:    */   {
/* 130:298 */     this.m_y = y;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public boolean onUnit(Graphics g, int x, int y, int w, int h)
/* 134:    */   {
/* 135:313 */     int m = (int)(this.m_x * w);
/* 136:314 */     int c = (int)(this.m_y * h);
/* 137:315 */     if ((x > m + 10) || (x < m - 10) || (y > c + 10) || (y < c - 10)) {
/* 138:316 */       return false;
/* 139:    */     }
/* 140:318 */     return true;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void drawNode(Graphics g, int w, int h)
/* 144:    */   {
/* 145:330 */     if ((this.m_type & 0x8) == 8) {
/* 146:331 */       g.setColor(Color.orange);
/* 147:    */     } else {
/* 148:334 */       g.setColor(Color.red);
/* 149:    */     }
/* 150:336 */     g.fillOval((int)(this.m_x * w) - 9, (int)(this.m_y * h) - 9, 19, 19);
/* 151:337 */     g.setColor(Color.gray);
/* 152:338 */     g.fillOval((int)(this.m_x * w) - 5, (int)(this.m_y * h) - 5, 11, 11);
/* 153:    */   }
/* 154:    */   
/* 155:    */   public void drawHighlight(Graphics g, int w, int h)
/* 156:    */   {
/* 157:349 */     drawNode(g, w, h);
/* 158:350 */     g.setColor(Color.yellow);
/* 159:351 */     g.fillOval((int)(this.m_x * w) - 5, (int)(this.m_y * h) - 5, 11, 11);
/* 160:    */   }
/* 161:    */   
/* 162:    */   public void drawInputLines(Graphics g, int w, int h)
/* 163:    */   {
/* 164:362 */     g.setColor(Color.black);
/* 165:    */     
/* 166:364 */     int px = (int)(this.m_x * w);
/* 167:365 */     int py = (int)(this.m_y * h);
/* 168:366 */     for (int noa = 0; noa < this.m_numInputs; noa++) {
/* 169:367 */       g.drawLine((int)(this.m_inputList[noa].getX() * w), (int)(this.m_inputList[noa].getY() * h), px, py);
/* 170:    */     }
/* 171:    */   }
/* 172:    */   
/* 173:    */   public void drawOutputLines(Graphics g, int w, int h)
/* 174:    */   {
/* 175:381 */     g.setColor(Color.black);
/* 176:    */     
/* 177:383 */     int px = (int)(this.m_x * w);
/* 178:384 */     int py = (int)(this.m_y * h);
/* 179:385 */     for (int noa = 0; noa < this.m_numOutputs; noa++) {
/* 180:386 */       g.drawLine(px, py, (int)(this.m_outputList[noa].getX() * w), (int)(this.m_outputList[noa].getY() * h));
/* 181:    */     }
/* 182:    */   }
/* 183:    */   
/* 184:    */   protected boolean connectInput(NeuralConnection i, int n)
/* 185:    */   {
/* 186:401 */     for (int noa = 0; noa < this.m_numInputs; noa++) {
/* 187:402 */       if (i == this.m_inputList[noa]) {
/* 188:403 */         return false;
/* 189:    */       }
/* 190:    */     }
/* 191:406 */     if (this.m_numInputs >= this.m_inputList.length) {
/* 192:408 */       allocateInputs();
/* 193:    */     }
/* 194:410 */     this.m_inputList[this.m_numInputs] = i;
/* 195:411 */     this.m_inputNums[this.m_numInputs] = n;
/* 196:412 */     this.m_numInputs += 1;
/* 197:413 */     return true;
/* 198:    */   }
/* 199:    */   
/* 200:    */   protected void allocateInputs()
/* 201:    */   {
/* 202:422 */     NeuralConnection[] temp1 = new NeuralConnection[this.m_inputList.length + 15];
/* 203:423 */     int[] temp2 = new int[this.m_inputNums.length + 15];
/* 204:425 */     for (int noa = 0; noa < this.m_numInputs; noa++)
/* 205:    */     {
/* 206:426 */       temp1[noa] = this.m_inputList[noa];
/* 207:427 */       temp2[noa] = this.m_inputNums[noa];
/* 208:    */     }
/* 209:429 */     this.m_inputList = temp1;
/* 210:430 */     this.m_inputNums = temp2;
/* 211:    */   }
/* 212:    */   
/* 213:    */   protected boolean connectOutput(NeuralConnection o, int n)
/* 214:    */   {
/* 215:441 */     for (int noa = 0; noa < this.m_numOutputs; noa++) {
/* 216:442 */       if (o == this.m_outputList[noa]) {
/* 217:443 */         return false;
/* 218:    */       }
/* 219:    */     }
/* 220:446 */     if (this.m_numOutputs >= this.m_outputList.length) {
/* 221:448 */       allocateOutputs();
/* 222:    */     }
/* 223:450 */     this.m_outputList[this.m_numOutputs] = o;
/* 224:451 */     this.m_outputNums[this.m_numOutputs] = n;
/* 225:452 */     this.m_numOutputs += 1;
/* 226:453 */     return true;
/* 227:    */   }
/* 228:    */   
/* 229:    */   protected void allocateOutputs()
/* 230:    */   {
/* 231:462 */     NeuralConnection[] temp1 = new NeuralConnection[this.m_outputList.length + 15];
/* 232:    */     
/* 233:    */ 
/* 234:465 */     int[] temp2 = new int[this.m_outputNums.length + 15];
/* 235:467 */     for (int noa = 0; noa < this.m_numOutputs; noa++)
/* 236:    */     {
/* 237:468 */       temp1[noa] = this.m_outputList[noa];
/* 238:469 */       temp2[noa] = this.m_outputNums[noa];
/* 239:    */     }
/* 240:471 */     this.m_outputList = temp1;
/* 241:472 */     this.m_outputNums = temp2;
/* 242:    */   }
/* 243:    */   
/* 244:    */   protected boolean disconnectInput(NeuralConnection i, int n)
/* 245:    */   {
/* 246:486 */     int loc = -1;
/* 247:487 */     boolean removed = false;
/* 248:    */     do
/* 249:    */     {
/* 250:489 */       loc = -1;
/* 251:490 */       for (int noa = 0; noa < this.m_numInputs; noa++) {
/* 252:491 */         if ((i == this.m_inputList[noa]) && ((n == -1) || (n == this.m_inputNums[noa])))
/* 253:    */         {
/* 254:492 */           loc = noa;
/* 255:493 */           break;
/* 256:    */         }
/* 257:    */       }
/* 258:497 */       if (loc >= 0)
/* 259:    */       {
/* 260:498 */         for (int noa = loc + 1; noa < this.m_numInputs; noa++)
/* 261:    */         {
/* 262:499 */           this.m_inputList[(noa - 1)] = this.m_inputList[noa];
/* 263:500 */           this.m_inputNums[(noa - 1)] = this.m_inputNums[noa];
/* 264:    */           
/* 265:502 */           this.m_inputList[(noa - 1)].changeOutputNum(this.m_inputNums[(noa - 1)], noa - 1);
/* 266:    */         }
/* 267:504 */         this.m_numInputs -= 1;
/* 268:505 */         removed = true;
/* 269:    */       }
/* 270:507 */     } while ((n == -1) && (loc != -1));
/* 271:509 */     return removed;
/* 272:    */   }
/* 273:    */   
/* 274:    */   public void removeAllInputs()
/* 275:    */   {
/* 276:518 */     for (int noa = 0; noa < this.m_numInputs; noa++) {
/* 277:521 */       this.m_inputList[noa].disconnectOutput(this, -1);
/* 278:    */     }
/* 279:525 */     this.m_inputList = new NeuralConnection[0];
/* 280:526 */     setType(getType() & 0xFFFFFFFB);
/* 281:527 */     if (getNumOutputs() == 0) {
/* 282:528 */       setType(getType() & 0xFFFFFFEF);
/* 283:    */     }
/* 284:530 */     this.m_inputNums = new int[0];
/* 285:531 */     this.m_numInputs = 0;
/* 286:    */   }
/* 287:    */   
/* 288:    */   protected void changeInputNum(int n, int v)
/* 289:    */   {
/* 290:544 */     if ((n >= this.m_numInputs) || (n < 0)) {
/* 291:545 */       return;
/* 292:    */     }
/* 293:548 */     this.m_inputNums[n] = v;
/* 294:    */   }
/* 295:    */   
/* 296:    */   protected boolean disconnectOutput(NeuralConnection o, int n)
/* 297:    */   {
/* 298:562 */     int loc = -1;
/* 299:563 */     boolean removed = false;
/* 300:    */     do
/* 301:    */     {
/* 302:565 */       loc = -1;
/* 303:566 */       for (int noa = 0; noa < this.m_numOutputs; noa++) {
/* 304:567 */         if ((o == this.m_outputList[noa]) && ((n == -1) || (n == this.m_outputNums[noa])))
/* 305:    */         {
/* 306:568 */           loc = noa;
/* 307:569 */           break;
/* 308:    */         }
/* 309:    */       }
/* 310:573 */       if (loc >= 0)
/* 311:    */       {
/* 312:574 */         for (int noa = loc + 1; noa < this.m_numOutputs; noa++)
/* 313:    */         {
/* 314:575 */           this.m_outputList[(noa - 1)] = this.m_outputList[noa];
/* 315:576 */           this.m_outputNums[(noa - 1)] = this.m_outputNums[noa];
/* 316:    */           
/* 317:    */ 
/* 318:579 */           this.m_outputList[(noa - 1)].changeInputNum(this.m_outputNums[(noa - 1)], noa - 1);
/* 319:    */         }
/* 320:581 */         this.m_numOutputs -= 1;
/* 321:582 */         removed = true;
/* 322:    */       }
/* 323:584 */     } while ((n == -1) && (loc != -1));
/* 324:586 */     return removed;
/* 325:    */   }
/* 326:    */   
/* 327:    */   public void removeAllOutputs()
/* 328:    */   {
/* 329:595 */     for (int noa = 0; noa < this.m_numOutputs; noa++) {
/* 330:598 */       this.m_outputList[noa].disconnectInput(this, -1);
/* 331:    */     }
/* 332:602 */     this.m_outputList = new NeuralConnection[0];
/* 333:603 */     this.m_outputNums = new int[0];
/* 334:604 */     setType(getType() & 0xFFFFFFF7);
/* 335:605 */     if (getNumInputs() == 0) {
/* 336:606 */       setType(getType() & 0xFFFFFFEF);
/* 337:    */     }
/* 338:608 */     this.m_numOutputs = 0;
/* 339:    */   }
/* 340:    */   
/* 341:    */   protected void changeOutputNum(int n, int v)
/* 342:    */   {
/* 343:619 */     if ((n >= this.m_numOutputs) || (n < 0)) {
/* 344:620 */       return;
/* 345:    */     }
/* 346:623 */     this.m_outputNums[n] = v;
/* 347:    */   }
/* 348:    */   
/* 349:    */   public int getNumInputs()
/* 350:    */   {
/* 351:630 */     return this.m_numInputs;
/* 352:    */   }
/* 353:    */   
/* 354:    */   public int getNumOutputs()
/* 355:    */   {
/* 356:637 */     return this.m_numOutputs;
/* 357:    */   }
/* 358:    */   
/* 359:    */   public static boolean connect(NeuralConnection s, NeuralConnection t)
/* 360:    */   {
/* 361:649 */     if ((s == null) || (t == null)) {
/* 362:650 */       return false;
/* 363:    */     }
/* 364:656 */     disconnect(s, t);
/* 365:657 */     if (s == t) {
/* 366:658 */       return false;
/* 367:    */     }
/* 368:660 */     if ((t.getType() & 0x1) == 1) {
/* 369:661 */       return false;
/* 370:    */     }
/* 371:663 */     if ((s.getType() & 0x2) == 2) {
/* 372:664 */       return false;
/* 373:    */     }
/* 374:666 */     if (((s.getType() & 0x1) == 1) && ((t.getType() & 0x2) == 2)) {
/* 375:668 */       return false;
/* 376:    */     }
/* 377:670 */     if (((t.getType() & 0x2) == 2) && (t.getNumInputs() > 0)) {
/* 378:671 */       return false;
/* 379:    */     }
/* 380:674 */     if (((t.getType() & 0x2) == 2) && ((s.getType() & 0x8) == 8)) {
/* 381:676 */       return false;
/* 382:    */     }
/* 383:679 */     if (!s.connectOutput(t, t.getNumInputs())) {
/* 384:680 */       return false;
/* 385:    */     }
/* 386:682 */     if (!t.connectInput(s, s.getNumOutputs() - 1))
/* 387:    */     {
/* 388:684 */       s.disconnectOutput(t, t.getNumInputs());
/* 389:685 */       return false;
/* 390:    */     }
/* 391:690 */     if ((s.getType() & 0x1) == 1) {
/* 392:691 */       t.setType(t.getType() | 0x4);
/* 393:693 */     } else if ((t.getType() & 0x2) == 2) {
/* 394:694 */       s.setType(s.getType() | 0x8);
/* 395:    */     }
/* 396:696 */     t.setType(t.getType() | 0x10);
/* 397:697 */     s.setType(s.getType() | 0x10);
/* 398:698 */     return true;
/* 399:    */   }
/* 400:    */   
/* 401:    */   public static boolean disconnect(NeuralConnection s, NeuralConnection t)
/* 402:    */   {
/* 403:710 */     if ((s == null) || (t == null)) {
/* 404:711 */       return false;
/* 405:    */     }
/* 406:714 */     boolean stat1 = s.disconnectOutput(t, -1);
/* 407:715 */     boolean stat2 = t.disconnectInput(s, -1);
/* 408:716 */     if ((stat1) && (stat2))
/* 409:    */     {
/* 410:717 */       if ((s.getType() & 0x1) == 1) {
/* 411:718 */         t.setType(t.getType() & 0xFFFFFFFB);
/* 412:720 */       } else if ((t.getType() & 0x2) == 2) {
/* 413:721 */         s.setType(s.getType() & 0xFFFFFFF7);
/* 414:    */       }
/* 415:723 */       if ((s.getNumInputs() == 0) && (s.getNumOutputs() == 0)) {
/* 416:724 */         s.setType(s.getType() & 0xFFFFFFEF);
/* 417:    */       }
/* 418:726 */       if ((t.getNumInputs() == 0) && (t.getNumOutputs() == 0)) {
/* 419:727 */         t.setType(t.getType() & 0xFFFFFFEF);
/* 420:    */       }
/* 421:    */     }
/* 422:730 */     return (stat1) && (stat2);
/* 423:    */   }
/* 424:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.neural.NeuralConnection
 * JD-Core Version:    0.7.0.1
 */