/*   1:    */ package weka.classifiers.functions.supportVector;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.Option;
/*  10:    */ import weka.core.RevisionUtils;
/*  11:    */ import weka.core.TechnicalInformation;
/*  12:    */ import weka.core.TechnicalInformation.Field;
/*  13:    */ import weka.core.TechnicalInformation.Type;
/*  14:    */ import weka.core.TechnicalInformationHandler;
/*  15:    */ import weka.core.Utils;
/*  16:    */ 
/*  17:    */ public class RegSMO
/*  18:    */   extends RegOptimizer
/*  19:    */   implements TechnicalInformationHandler
/*  20:    */ {
/*  21:    */   private static final long serialVersionUID = -7504070793279598638L;
/*  22: 92 */   protected double m_eps = 1.0E-012D;
/*  23:    */   protected static final double m_Del = 1.0E-010D;
/*  24:    */   double[] m_error;
/*  25:    */   protected double m_alpha1;
/*  26:    */   protected double m_alpha1Star;
/*  27:    */   protected double m_alpha2;
/*  28:    */   protected double m_alpha2Star;
/*  29:    */   
/*  30:    */   public String globalInfo()
/*  31:    */   {
/*  32:130 */     return "Implementation of SMO for support vector regression as described in :\n\n" + getTechnicalInformation().toString();
/*  33:    */   }
/*  34:    */   
/*  35:    */   public TechnicalInformation getTechnicalInformation()
/*  36:    */   {
/*  37:145 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.MISC);
/*  38:146 */     result.setValue(TechnicalInformation.Field.AUTHOR, "A.J. Smola and B. Schoelkopf");
/*  39:147 */     result.setValue(TechnicalInformation.Field.TITLE, "A tutorial on support vector regression");
/*  40:148 */     result.setValue(TechnicalInformation.Field.NOTE, "NeuroCOLT2 Technical Report NC2-TR-1998-030");
/*  41:149 */     result.setValue(TechnicalInformation.Field.YEAR, "1998");
/*  42:    */     
/*  43:151 */     return result;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public Enumeration<Option> listOptions()
/*  47:    */   {
/*  48:161 */     Vector<Option> result = new Vector();
/*  49:    */     
/*  50:163 */     result.addElement(new Option("\tThe epsilon for round-off error.\n\t(default 1.0e-12)", "P", 1, "-P <double>"));
/*  51:    */     
/*  52:    */ 
/*  53:166 */     result.addAll(Collections.list(super.listOptions()));
/*  54:    */     
/*  55:168 */     return result.elements();
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setOptions(String[] options)
/*  59:    */     throws Exception
/*  60:    */   {
/*  61:205 */     String tmpStr = Utils.getOption('P', options);
/*  62:206 */     if (tmpStr.length() != 0) {
/*  63:207 */       setEpsilon(Double.parseDouble(tmpStr));
/*  64:    */     } else {
/*  65:209 */       setEpsilon(1.0E-012D);
/*  66:    */     }
/*  67:212 */     super.setOptions(options);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public String[] getOptions()
/*  71:    */   {
/*  72:223 */     Vector<String> result = new Vector();
/*  73:    */     
/*  74:225 */     result.add("-P");
/*  75:226 */     result.add("" + getEpsilon());
/*  76:    */     
/*  77:228 */     Collections.addAll(result, super.getOptions());
/*  78:    */     
/*  79:230 */     return (String[])result.toArray(new String[result.size()]);
/*  80:    */   }
/*  81:    */   
/*  82:    */   public String epsilonTipText()
/*  83:    */   {
/*  84:240 */     return "The epsilon for round-off error (shouldn't be changed).";
/*  85:    */   }
/*  86:    */   
/*  87:    */   public double getEpsilon()
/*  88:    */   {
/*  89:249 */     return this.m_eps;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void setEpsilon(double v)
/*  93:    */   {
/*  94:258 */     this.m_eps = v;
/*  95:    */   }
/*  96:    */   
/*  97:    */   protected void init(Instances data)
/*  98:    */     throws Exception
/*  99:    */   {
/* 100:269 */     super.init(data);
/* 101:    */     
/* 102:    */ 
/* 103:272 */     this.m_error = new double[this.m_nInstances];
/* 104:273 */     for (int i = 0; i < this.m_nInstances; i++) {
/* 105:274 */       this.m_error[i] = (-this.m_target[i]);
/* 106:    */     }
/* 107:    */   }
/* 108:    */   
/* 109:    */   protected void wrapUp()
/* 110:    */     throws Exception
/* 111:    */   {
/* 112:286 */     this.m_error = null;
/* 113:287 */     super.wrapUp();
/* 114:    */   }
/* 115:    */   
/* 116:    */   protected boolean findOptimalPointOnLine(int i1, double alpha1, double alpha1Star, double C1, int i2, double alpha2, double alpha2Star, double C2, double gamma, double eta, double deltaPhi)
/* 117:    */   {
/* 118:310 */     if (eta <= 0.0D) {
/* 119:313 */       return false;
/* 120:    */     }
/* 121:316 */     boolean case1 = false;
/* 122:317 */     boolean case2 = false;
/* 123:318 */     boolean case3 = false;
/* 124:319 */     boolean case4 = false;
/* 125:320 */     boolean finished = false;
/* 126:325 */     while (!finished) {
/* 127:341 */       if ((!case1) && ((alpha1 > 0.0D) || ((alpha1Star == 0.0D) && (deltaPhi > 0.0D))) && ((alpha2 > 0.0D) || ((alpha2Star == 0.0D) && (deltaPhi < 0.0D))))
/* 128:    */       {
/* 129:344 */         double L = Math.max(0.0D, gamma - C1);
/* 130:345 */         double H = Math.min(C2, gamma);
/* 131:346 */         if (L < H)
/* 132:    */         {
/* 133:347 */           double a2 = alpha2 - deltaPhi / eta;
/* 134:348 */           a2 = Math.min(a2, H);
/* 135:349 */           a2 = Math.max(L, a2);
/* 136:351 */           if (a2 > C2 - 1.0E-010D * C2) {
/* 137:352 */             a2 = C2;
/* 138:353 */           } else if (a2 <= 1.0E-010D * C2) {
/* 139:354 */             a2 = 0.0D;
/* 140:    */           }
/* 141:356 */           double a1 = alpha1 - (a2 - alpha2);
/* 142:357 */           if (a1 > C1 - 1.0E-010D * C1) {
/* 143:358 */             a1 = C1;
/* 144:359 */           } else if (a1 <= 1.0E-010D * C1) {
/* 145:360 */             a1 = 0.0D;
/* 146:    */           }
/* 147:363 */           if (Math.abs(alpha1 - a1) > this.m_eps)
/* 148:    */           {
/* 149:364 */             deltaPhi += eta * (a2 - alpha2);
/* 150:365 */             alpha1 = a1;
/* 151:366 */             alpha2 = a2;
/* 152:    */           }
/* 153:    */         }
/* 154:    */         else
/* 155:    */         {
/* 156:369 */           finished = true;
/* 157:    */         }
/* 158:371 */         case1 = true;
/* 159:    */       }
/* 160:389 */       else if ((!case2) && ((alpha1 > 0.0D) || ((alpha1Star == 0.0D) && (deltaPhi > 2.0D * this.m_epsilon))) && ((alpha2Star > 0.0D) || ((alpha2 == 0.0D) && (deltaPhi > 2.0D * this.m_epsilon))))
/* 161:    */       {
/* 162:393 */         double L = Math.max(0.0D, -gamma);
/* 163:394 */         double H = Math.min(C2, -gamma + C1);
/* 164:395 */         if (L < H)
/* 165:    */         {
/* 166:396 */           double a2 = alpha2Star + (deltaPhi - 2.0D * this.m_epsilon) / eta;
/* 167:397 */           a2 = Math.min(a2, H);
/* 168:398 */           a2 = Math.max(L, a2);
/* 169:400 */           if (a2 > C2 - 1.0E-010D * C2) {
/* 170:401 */             a2 = C2;
/* 171:402 */           } else if (a2 <= 1.0E-010D * C2) {
/* 172:403 */             a2 = 0.0D;
/* 173:    */           }
/* 174:405 */           double a1 = alpha1 + (a2 - alpha2Star);
/* 175:406 */           if (a1 > C1 - 1.0E-010D * C1) {
/* 176:407 */             a1 = C1;
/* 177:408 */           } else if (a1 <= 1.0E-010D * C1) {
/* 178:409 */             a1 = 0.0D;
/* 179:    */           }
/* 180:412 */           if (Math.abs(alpha1 - a1) > this.m_eps)
/* 181:    */           {
/* 182:413 */             deltaPhi += eta * (-a2 + alpha2Star);
/* 183:414 */             alpha1 = a1;
/* 184:415 */             alpha2Star = a2;
/* 185:    */           }
/* 186:    */         }
/* 187:    */         else
/* 188:    */         {
/* 189:418 */           finished = true;
/* 190:    */         }
/* 191:420 */         case2 = true;
/* 192:    */       }
/* 193:438 */       else if ((!case3) && ((alpha1Star > 0.0D) || ((alpha1 == 0.0D) && (deltaPhi < -2.0D * this.m_epsilon))) && ((alpha2 > 0.0D) || ((alpha2Star == 0.0D) && (deltaPhi < -2.0D * this.m_epsilon))))
/* 194:    */       {
/* 195:442 */         double L = Math.max(0.0D, gamma);
/* 196:443 */         double H = Math.min(C2, C1 + gamma);
/* 197:444 */         if (L < H)
/* 198:    */         {
/* 199:447 */           double a2 = alpha2 - (deltaPhi + 2.0D * this.m_epsilon) / eta;
/* 200:448 */           a2 = Math.min(a2, H);
/* 201:449 */           a2 = Math.max(L, a2);
/* 202:451 */           if (a2 > C2 - 1.0E-010D * C2) {
/* 203:452 */             a2 = C2;
/* 204:453 */           } else if (a2 <= 1.0E-010D * C2) {
/* 205:454 */             a2 = 0.0D;
/* 206:    */           }
/* 207:456 */           double a1 = alpha1Star + (a2 - alpha2);
/* 208:457 */           if (a1 > C1 - 1.0E-010D * C1) {
/* 209:458 */             a1 = C1;
/* 210:459 */           } else if (a1 <= 1.0E-010D * C1) {
/* 211:460 */             a1 = 0.0D;
/* 212:    */           }
/* 213:463 */           if (Math.abs(alpha1Star - a1) > this.m_eps)
/* 214:    */           {
/* 215:464 */             deltaPhi += eta * (a2 - alpha2);
/* 216:465 */             alpha1Star = a1;
/* 217:466 */             alpha2 = a2;
/* 218:    */           }
/* 219:    */         }
/* 220:    */         else
/* 221:    */         {
/* 222:469 */           finished = true;
/* 223:    */         }
/* 224:471 */         case3 = true;
/* 225:    */       }
/* 226:492 */       else if ((!case4) && ((alpha1Star > 0.0D) || ((alpha1 == 0.0D) && (deltaPhi < 0.0D))) && ((alpha2Star > 0.0D) || ((alpha2 == 0.0D) && (deltaPhi > 0.0D))))
/* 227:    */       {
/* 228:496 */         double L = Math.max(0.0D, -gamma - C1);
/* 229:497 */         double H = Math.min(C2, -gamma);
/* 230:498 */         if (L < H)
/* 231:    */         {
/* 232:499 */           double a2 = alpha2Star + deltaPhi / eta;
/* 233:500 */           a2 = Math.min(a2, H);
/* 234:501 */           a2 = Math.max(L, a2);
/* 235:503 */           if (a2 > C2 - 1.0E-010D * C2) {
/* 236:504 */             a2 = C2;
/* 237:505 */           } else if (a2 <= 1.0E-010D * C2) {
/* 238:506 */             a2 = 0.0D;
/* 239:    */           }
/* 240:508 */           double a1 = alpha1Star - (a2 - alpha2Star);
/* 241:509 */           if (a1 > C1 - 1.0E-010D * C1) {
/* 242:510 */             a1 = C1;
/* 243:511 */           } else if (a1 <= 1.0E-010D * C1) {
/* 244:512 */             a1 = 0.0D;
/* 245:    */           }
/* 246:515 */           if (Math.abs(alpha1Star - a1) > this.m_eps)
/* 247:    */           {
/* 248:516 */             deltaPhi += eta * (-a2 + alpha2Star);
/* 249:    */             
/* 250:518 */             alpha1Star = a1;
/* 251:519 */             alpha2Star = a2;
/* 252:    */           }
/* 253:    */         }
/* 254:    */         else
/* 255:    */         {
/* 256:522 */           finished = true;
/* 257:    */         }
/* 258:524 */         case4 = true;
/* 259:    */       }
/* 260:    */       else
/* 261:    */       {
/* 262:526 */         finished = true;
/* 263:    */       }
/* 264:    */     }
/* 265:542 */     if ((Math.abs(alpha1 - this.m_alpha[i1]) > this.m_eps) || (Math.abs(alpha1Star - this.m_alphaStar[i1]) > this.m_eps) || (Math.abs(alpha2 - this.m_alpha[i2]) > this.m_eps) || (Math.abs(alpha2Star - this.m_alphaStar[i2]) > this.m_eps))
/* 266:    */     {
/* 267:547 */       if (alpha1 > C1 - 1.0E-010D * C1) {
/* 268:548 */         alpha1 = C1;
/* 269:549 */       } else if (alpha1 <= 1.0E-010D * C1) {
/* 270:550 */         alpha1 = 0.0D;
/* 271:    */       }
/* 272:552 */       if (alpha1Star > C1 - 1.0E-010D * C1) {
/* 273:553 */         alpha1Star = C1;
/* 274:554 */       } else if (alpha1Star <= 1.0E-010D * C1) {
/* 275:555 */         alpha1Star = 0.0D;
/* 276:    */       }
/* 277:557 */       if (alpha2 > C2 - 1.0E-010D * C2) {
/* 278:558 */         alpha2 = C2;
/* 279:559 */       } else if (alpha2 <= 1.0E-010D * C2) {
/* 280:560 */         alpha2 = 0.0D;
/* 281:    */       }
/* 282:562 */       if (alpha2Star > C2 - 1.0E-010D * C2) {
/* 283:563 */         alpha2Star = C2;
/* 284:564 */       } else if (alpha2Star <= 1.0E-010D * C2) {
/* 285:565 */         alpha2Star = 0.0D;
/* 286:    */       }
/* 287:569 */       this.m_alpha[i1] = alpha1;
/* 288:570 */       this.m_alphaStar[i1] = alpha1Star;
/* 289:571 */       this.m_alpha[i2] = alpha2;
/* 290:572 */       this.m_alphaStar[i2] = alpha2Star;
/* 291:575 */       if ((alpha1 != 0.0D) || (alpha1Star != 0.0D))
/* 292:    */       {
/* 293:576 */         if (!this.m_supportVectors.contains(i1)) {
/* 294:577 */           this.m_supportVectors.insert(i1);
/* 295:    */         }
/* 296:    */       }
/* 297:    */       else {
/* 298:580 */         this.m_supportVectors.delete(i1);
/* 299:    */       }
/* 300:582 */       if ((alpha2 != 0.0D) || (alpha2Star != 0.0D))
/* 301:    */       {
/* 302:583 */         if (!this.m_supportVectors.contains(i2)) {
/* 303:584 */           this.m_supportVectors.insert(i2);
/* 304:    */         }
/* 305:    */       }
/* 306:    */       else {
/* 307:587 */         this.m_supportVectors.delete(i2);
/* 308:    */       }
/* 309:589 */       return true;
/* 310:    */     }
/* 311:592 */     return false;
/* 312:    */   }
/* 313:    */   
/* 314:    */   protected int takeStep(int i1, int i2, double alpha2, double alpha2Star, double phi2)
/* 315:    */     throws Exception
/* 316:    */   {
/* 317:610 */     if (i1 == i2) {
/* 318:611 */       return 0;
/* 319:    */     }
/* 320:613 */     double C1 = this.m_C * this.m_data.instance(i1).weight();
/* 321:614 */     double C2 = this.m_C * this.m_data.instance(i2).weight();
/* 322:    */     
/* 323:    */ 
/* 324:    */ 
/* 325:618 */     double alpha1 = this.m_alpha[i1];
/* 326:619 */     double alpha1Star = this.m_alphaStar[i1];
/* 327:620 */     double phi1 = this.m_error[i1];
/* 328:    */     
/* 329:    */ 
/* 330:    */ 
/* 331:    */ 
/* 332:    */ 
/* 333:    */ 
/* 334:    */ 
/* 335:628 */     double k11 = this.m_kernel.eval(i1, i1, this.m_data.instance(i1));
/* 336:629 */     double k12 = this.m_kernel.eval(i1, i2, this.m_data.instance(i1));
/* 337:630 */     double k22 = this.m_kernel.eval(i2, i2, this.m_data.instance(i2));
/* 338:631 */     double eta = -2.0D * k12 + k11 + k22;
/* 339:633 */     if (eta < 0.0D) {
/* 340:636 */       return 0;
/* 341:    */     }
/* 342:638 */     double gamma = alpha1 - alpha1Star + alpha2 - alpha2Star;
/* 343:    */     
/* 344:    */ 
/* 345:    */ 
/* 346:    */ 
/* 347:    */ 
/* 348:    */ 
/* 349:    */ 
/* 350:    */ 
/* 351:    */ 
/* 352:648 */     double alpha1old = alpha1;
/* 353:649 */     double alpha1Starold = alpha1Star;
/* 354:650 */     double alpha2old = alpha2;
/* 355:651 */     double alpha2Starold = alpha2Star;
/* 356:652 */     double deltaPhi = phi2 - phi1;
/* 357:654 */     if (findOptimalPointOnLine(i1, alpha1, alpha1Star, C1, i2, alpha2, alpha2Star, C2, gamma, eta, deltaPhi))
/* 358:    */     {
/* 359:656 */       alpha1 = this.m_alpha[i1];
/* 360:657 */       alpha1Star = this.m_alphaStar[i1];
/* 361:658 */       alpha2 = this.m_alpha[i2];
/* 362:659 */       alpha2Star = this.m_alphaStar[i2];
/* 363:    */       
/* 364:    */ 
/* 365:662 */       double dAlpha1 = alpha1 - alpha1old - (alpha1Star - alpha1Starold);
/* 366:663 */       double dAlpha2 = alpha2 - alpha2old - (alpha2Star - alpha2Starold);
/* 367:664 */       for (int j = 0; j < this.m_nInstances; j++) {
/* 368:665 */         if ((j != i1) && (j != i2)) {
/* 369:666 */           this.m_error[j] += dAlpha1 * this.m_kernel.eval(i1, j, this.m_data.instance(i1)) + dAlpha2 * this.m_kernel.eval(i2, j, this.m_data.instance(i2));
/* 370:    */         }
/* 371:    */       }
/* 372:670 */       this.m_error[i1] += dAlpha1 * k11 + dAlpha2 * k12;
/* 373:671 */       this.m_error[i2] += dAlpha1 * k12 + dAlpha2 * k22;
/* 374:    */       
/* 375:    */ 
/* 376:674 */       double b1 = 1.7976931348623157E+308D;
/* 377:675 */       double b2 = 1.7976931348623157E+308D;
/* 378:676 */       if (((0.0D < alpha1) && (alpha1 < C1)) || ((0.0D < alpha1Star) && (alpha1Star < C1)) || ((0.0D < alpha2) && (alpha2 < C2)) || ((0.0D < alpha2Star) && (alpha2Star < C2)))
/* 379:    */       {
/* 380:678 */         if ((0.0D < alpha1) && (alpha1 < C1)) {
/* 381:679 */           b1 = this.m_error[i1] - this.m_epsilon;
/* 382:680 */         } else if ((0.0D < alpha1Star) && (alpha1Star < C1)) {
/* 383:681 */           b1 = this.m_error[i1] + this.m_epsilon;
/* 384:    */         }
/* 385:683 */         if ((0.0D < alpha2) && (alpha2 < C2)) {
/* 386:684 */           b2 = this.m_error[i2] - this.m_epsilon;
/* 387:685 */         } else if ((0.0D < alpha2Star) && (alpha2Star < C2)) {
/* 388:686 */           b2 = this.m_error[i2] + this.m_epsilon;
/* 389:    */         }
/* 390:688 */         if (b1 < 1.7976931348623157E+308D)
/* 391:    */         {
/* 392:689 */           this.m_b = b1;
/* 393:690 */           if (b2 < 1.7976931348623157E+308D) {
/* 394:691 */             this.m_b = ((b1 + b2) / 2.0D);
/* 395:    */           }
/* 396:    */         }
/* 397:693 */         else if (b2 < 1.7976931348623157E+308D)
/* 398:    */         {
/* 399:694 */           this.m_b = b2;
/* 400:    */         }
/* 401:    */       }
/* 402:696 */       else if (this.m_b == 0.0D)
/* 403:    */       {
/* 404:698 */         this.m_b = ((this.m_error[i1] + this.m_error[i2]) / 2.0D);
/* 405:    */       }
/* 406:706 */       return 1;
/* 407:    */     }
/* 408:708 */     return 0;
/* 409:    */   }
/* 410:    */   
/* 411:    */   protected int examineExample(int i2)
/* 412:    */     throws Exception
/* 413:    */   {
/* 414:723 */     double alpha2 = this.m_alpha[i2];
/* 415:724 */     double alpha2Star = this.m_alphaStar[i2];
/* 416:    */     
/* 417:726 */     double C2 = this.m_C;
/* 418:727 */     double C2Star = this.m_C;
/* 419:    */     
/* 420:729 */     double phi2 = this.m_error[i2];
/* 421:    */     
/* 422:731 */     double phi2b = phi2 - this.m_b;
/* 423:736 */     if (((phi2b > this.m_epsilon) && (alpha2Star < C2Star)) || ((phi2b < this.m_epsilon) && (alpha2Star > 0.0D)) || ((-phi2b > this.m_epsilon) && (alpha2 < C2)) || ((-phi2b > this.m_epsilon) && (alpha2 > 0.0D)))
/* 424:    */     {
/* 425:745 */       int i1 = secondChoiceHeuristic(i2);
/* 426:746 */       if ((i1 >= 0) && (takeStep(i1, i2, alpha2, alpha2Star, phi2) > 0)) {
/* 427:747 */         return 1;
/* 428:    */       }
/* 429:753 */       for (i1 = 0; i1 < this.m_target.length; i1++) {
/* 430:754 */         if (((this.m_alpha[i1] > 0.0D) && (this.m_alpha[i1] < this.m_C)) || ((this.m_alphaStar[i1] > 0.0D) && (this.m_alphaStar[i1] < this.m_C))) {
/* 431:756 */           if (takeStep(i1, i2, alpha2, alpha2Star, phi2) > 0) {
/* 432:757 */             return 1;
/* 433:    */           }
/* 434:    */         }
/* 435:    */       }
/* 436:765 */       for (i1 = 0; i1 < this.m_target.length; i1++) {
/* 437:766 */         if (takeStep(i1, i2, alpha2, alpha2Star, phi2) > 0) {
/* 438:767 */           return 1;
/* 439:    */         }
/* 440:    */       }
/* 441:    */     }
/* 442:773 */     return 0;
/* 443:    */   }
/* 444:    */   
/* 445:    */   protected int secondChoiceHeuristic(int i2)
/* 446:    */   {
/* 447:787 */     for (int i = 0; i < 59; i++)
/* 448:    */     {
/* 449:788 */       int i1 = this.m_random.nextInt(this.m_nInstances);
/* 450:789 */       if (((i1 != i2) && (this.m_alpha[i1] > 0.0D) && (this.m_alpha[i1] < this.m_C)) || ((this.m_alphaStar[i1] > 0.0D) && (this.m_alphaStar[i1] < this.m_C))) {
/* 451:791 */         return i1;
/* 452:    */       }
/* 453:    */     }
/* 454:794 */     return -1;
/* 455:    */   }
/* 456:    */   
/* 457:    */   public void optimize()
/* 458:    */     throws Exception
/* 459:    */   {
/* 460:810 */     int numChanged = 0;
/* 461:811 */     int examineAll = 1;
/* 462:812 */     int sigFig = -100;
/* 463:813 */     int loopCounter = 0;
/* 464:815 */     while ((((numChanged > 0) || (examineAll > 0) ? 1 : 0) | (sigFig < 3 ? 1 : 0)) != 0)
/* 465:    */     {
/* 466:818 */       loopCounter++;
/* 467:819 */       numChanged = 0;
/* 468:    */       
/* 469:    */ 
/* 470:    */ 
/* 471:    */ 
/* 472:    */ 
/* 473:    */ 
/* 474:    */ 
/* 475:827 */       int numSamples = 0;
/* 476:828 */       if (examineAll > 0) {
/* 477:829 */         for (int i = 0; i < this.m_nInstances; i++) {
/* 478:830 */           numChanged += examineExample(i);
/* 479:    */         }
/* 480:    */       } else {
/* 481:833 */         for (int i = 0; i < this.m_target.length; i++) {
/* 482:834 */           if (((this.m_alpha[i] > 0.0D) && (this.m_alpha[i] < this.m_C * this.m_data.instance(i).weight())) || ((this.m_alphaStar[i] > 0.0D) && (this.m_alphaStar[i] < this.m_C * this.m_data.instance(i).weight())))
/* 483:    */           {
/* 484:837 */             numSamples++;
/* 485:838 */             numChanged += examineExample(i);
/* 486:    */           }
/* 487:    */         }
/* 488:    */       }
/* 489:848 */       int minimumNumChanged = 1;
/* 490:849 */       if (loopCounter % 2 == 0) {
/* 491:850 */         minimumNumChanged = (int)Math.max(1.0D, 0.1D * numSamples);
/* 492:    */       }
/* 493:858 */       if (examineAll == 1) {
/* 494:859 */         examineAll = 0;
/* 495:860 */       } else if (numChanged < minimumNumChanged) {
/* 496:861 */         examineAll = 1;
/* 497:    */       }
/* 498:865 */       if (loopCounter == 2500) {
/* 499:    */         break;
/* 500:    */       }
/* 501:    */     }
/* 502:    */   }
/* 503:    */   
/* 504:    */   public void buildClassifier(Instances instances)
/* 505:    */     throws Exception
/* 506:    */   {
/* 507:882 */     init(instances);
/* 508:    */     
/* 509:884 */     optimize();
/* 510:    */     
/* 511:886 */     wrapUp();
/* 512:    */   }
/* 513:    */   
/* 514:    */   public String getRevision()
/* 515:    */   {
/* 516:896 */     return RevisionUtils.extract("$Revision: 10169 $");
/* 517:    */   }
/* 518:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.supportVector.RegSMO
 * JD-Core Version:    0.7.0.1
 */