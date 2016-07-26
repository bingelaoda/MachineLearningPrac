/*   1:    */ package weka.core.matrix;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.RevisionHandler;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ 
/*   7:    */ public class SingularValueDecomposition
/*   8:    */   implements Serializable, RevisionHandler
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -8738089610999867951L;
/*  11:    */   private double[][] U;
/*  12:    */   private double[][] V;
/*  13:    */   private double[] s;
/*  14:    */   private int m;
/*  15:    */   private int n;
/*  16:    */   
/*  17:    */   public SingularValueDecomposition(Matrix Arg)
/*  18:    */   {
/*  19: 77 */     double[][] A = (double[][])null;
/*  20: 78 */     this.m = Arg.getRowDimension();
/*  21: 79 */     this.n = Arg.getColumnDimension();
/*  22:    */     
/*  23:    */ 
/*  24:    */ 
/*  25:    */ 
/*  26:    */ 
/*  27:    */ 
/*  28:    */ 
/*  29: 87 */     boolean usingTranspose = false;
/*  30: 88 */     if (this.m < this.n)
/*  31:    */     {
/*  32: 92 */       A = Arg.transpose().getArrayCopy();
/*  33: 93 */       usingTranspose = true;
/*  34: 94 */       int temp = this.m;
/*  35: 95 */       this.m = this.n;
/*  36: 96 */       this.n = temp;
/*  37:    */     }
/*  38:    */     else
/*  39:    */     {
/*  40: 98 */       A = Arg.getArrayCopy();
/*  41:    */     }
/*  42:101 */     int nu = Math.min(this.m, this.n);
/*  43:102 */     this.s = new double[Math.min(this.m + 1, this.n)];
/*  44:103 */     this.U = new double[this.m][this.m];
/*  45:104 */     this.V = new double[this.n][this.n];
/*  46:105 */     double[] e = new double[this.n];
/*  47:106 */     double[] work = new double[this.m];
/*  48:107 */     boolean wantu = true;
/*  49:108 */     boolean wantv = true;
/*  50:    */     
/*  51:    */ 
/*  52:    */ 
/*  53:    */ 
/*  54:113 */     int nct = Math.min(this.m - 1, this.n);
/*  55:114 */     int nrt = Math.max(0, Math.min(this.n - 2, this.m));
/*  56:115 */     for (int k = 0; k < Math.max(nct, nrt); k++)
/*  57:    */     {
/*  58:116 */       if (k < nct)
/*  59:    */       {
/*  60:121 */         this.s[k] = 0.0D;
/*  61:122 */         for (int i = k; i < this.m; i++) {
/*  62:123 */           this.s[k] = Maths.hypot(this.s[k], A[i][k]);
/*  63:    */         }
/*  64:125 */         if (this.s[k] != 0.0D)
/*  65:    */         {
/*  66:126 */           if (A[k][k] < 0.0D) {
/*  67:127 */             this.s[k] = (-this.s[k]);
/*  68:    */           }
/*  69:129 */           for (int i = k; i < this.m; i++) {
/*  70:130 */             A[i][k] /= this.s[k];
/*  71:    */           }
/*  72:132 */           A[k][k] += 1.0D;
/*  73:    */         }
/*  74:134 */         this.s[k] = (-this.s[k]);
/*  75:    */       }
/*  76:136 */       for (int j = k + 1; j < this.n; j++)
/*  77:    */       {
/*  78:137 */         if (((k < nct ? 1 : 0) & (this.s[k] != 0.0D ? 1 : 0)) != 0)
/*  79:    */         {
/*  80:141 */           double t = 0.0D;
/*  81:142 */           for (int i = k; i < this.m; i++) {
/*  82:143 */             t += A[i][k] * A[i][j];
/*  83:    */           }
/*  84:145 */           t = -t / A[k][k];
/*  85:146 */           for (int i = k; i < this.m; i++) {
/*  86:147 */             A[i][j] += t * A[i][k];
/*  87:    */           }
/*  88:    */         }
/*  89:154 */         e[j] = A[k][j];
/*  90:    */       }
/*  91:156 */       if ((wantu & k < nct)) {
/*  92:161 */         for (int i = k; i < this.m; i++) {
/*  93:162 */           this.U[i][k] = A[i][k];
/*  94:    */         }
/*  95:    */       }
/*  96:165 */       if (k < nrt)
/*  97:    */       {
/*  98:170 */         e[k] = 0.0D;
/*  99:171 */         for (int i = k + 1; i < this.n; i++) {
/* 100:172 */           e[k] = Maths.hypot(e[k], e[i]);
/* 101:    */         }
/* 102:174 */         if (e[k] != 0.0D)
/* 103:    */         {
/* 104:175 */           if (e[(k + 1)] < 0.0D) {
/* 105:176 */             e[k] = (-e[k]);
/* 106:    */           }
/* 107:178 */           for (int i = k + 1; i < this.n; i++) {
/* 108:179 */             e[i] /= e[k];
/* 109:    */           }
/* 110:181 */           e[(k + 1)] += 1.0D;
/* 111:    */         }
/* 112:183 */         e[k] = (-e[k]);
/* 113:184 */         if (((k + 1 < this.m ? 1 : 0) & (e[k] != 0.0D ? 1 : 0)) != 0)
/* 114:    */         {
/* 115:188 */           for (int i = k + 1; i < this.m; i++) {
/* 116:189 */             work[i] = 0.0D;
/* 117:    */           }
/* 118:191 */           for (int j = k + 1; j < this.n; j++) {
/* 119:192 */             for (int i = k + 1; i < this.m; i++) {
/* 120:193 */               work[i] += e[j] * A[i][j];
/* 121:    */             }
/* 122:    */           }
/* 123:196 */           for (int j = k + 1; j < this.n; j++)
/* 124:    */           {
/* 125:197 */             double t = -e[j] / e[(k + 1)];
/* 126:198 */             for (int i = k + 1; i < this.m; i++) {
/* 127:199 */               A[i][j] += t * work[i];
/* 128:    */             }
/* 129:    */           }
/* 130:    */         }
/* 131:203 */         if (wantv) {
/* 132:208 */           for (int i = k + 1; i < this.n; i++) {
/* 133:209 */             this.V[i][k] = e[i];
/* 134:    */           }
/* 135:    */         }
/* 136:    */       }
/* 137:    */     }
/* 138:217 */     int p = Math.min(this.n, this.m + 1);
/* 139:218 */     if (nct < this.n) {
/* 140:219 */       this.s[nct] = A[nct][nct];
/* 141:    */     }
/* 142:221 */     if (this.m < p) {
/* 143:222 */       this.s[(p - 1)] = 0.0D;
/* 144:    */     }
/* 145:224 */     if (nrt + 1 < p) {
/* 146:225 */       e[nrt] = A[nrt][(p - 1)];
/* 147:    */     }
/* 148:227 */     e[(p - 1)] = 0.0D;
/* 149:231 */     if (wantu)
/* 150:    */     {
/* 151:232 */       for (int j = nct; j < nu; j++)
/* 152:    */       {
/* 153:233 */         for (int i = 0; i < this.m; i++) {
/* 154:234 */           this.U[i][j] = 0.0D;
/* 155:    */         }
/* 156:236 */         this.U[j][j] = 1.0D;
/* 157:    */       }
/* 158:238 */       for (int k = nct - 1; k >= 0; k--) {
/* 159:239 */         if (this.s[k] != 0.0D)
/* 160:    */         {
/* 161:240 */           for (int j = k + 1; j < nu; j++)
/* 162:    */           {
/* 163:241 */             double t = 0.0D;
/* 164:242 */             for (int i = k; i < this.m; i++) {
/* 165:243 */               t += this.U[i][k] * this.U[i][j];
/* 166:    */             }
/* 167:245 */             t = -t / this.U[k][k];
/* 168:246 */             for (int i = k; i < this.m; i++) {
/* 169:247 */               this.U[i][j] += t * this.U[i][k];
/* 170:    */             }
/* 171:    */           }
/* 172:250 */           for (int i = k; i < this.m; i++) {
/* 173:251 */             this.U[i][k] = (-this.U[i][k]);
/* 174:    */           }
/* 175:253 */           this.U[k][k] = (1.0D + this.U[k][k]);
/* 176:254 */           for (int i = 0; i < k - 1; i++) {
/* 177:255 */             this.U[i][k] = 0.0D;
/* 178:    */           }
/* 179:    */         }
/* 180:    */         else
/* 181:    */         {
/* 182:258 */           for (int i = 0; i < this.m; i++) {
/* 183:259 */             this.U[i][k] = 0.0D;
/* 184:    */           }
/* 185:261 */           this.U[k][k] = 1.0D;
/* 186:    */         }
/* 187:    */       }
/* 188:    */     }
/* 189:268 */     if (wantv) {
/* 190:269 */       for (int k = this.n - 1; k >= 0; k--)
/* 191:    */       {
/* 192:270 */         if (((k < nrt ? 1 : 0) & (e[k] != 0.0D ? 1 : 0)) != 0) {
/* 193:271 */           for (int j = k + 1; j < nu; j++)
/* 194:    */           {
/* 195:272 */             double t = 0.0D;
/* 196:273 */             for (int i = k + 1; i < this.n; i++) {
/* 197:274 */               t += this.V[i][k] * this.V[i][j];
/* 198:    */             }
/* 199:276 */             t = -t / this.V[(k + 1)][k];
/* 200:277 */             for (int i = k + 1; i < this.n; i++) {
/* 201:278 */               this.V[i][j] += t * this.V[i][k];
/* 202:    */             }
/* 203:    */           }
/* 204:    */         }
/* 205:282 */         for (int i = 0; i < this.n; i++) {
/* 206:283 */           this.V[i][k] = 0.0D;
/* 207:    */         }
/* 208:285 */         this.V[k][k] = 1.0D;
/* 209:    */       }
/* 210:    */     }
/* 211:291 */     int pp = p - 1;
/* 212:292 */     int iter = 0;
/* 213:293 */     double eps = Math.pow(2.0D, -52.0D);
/* 214:294 */     double tiny = Math.pow(2.0D, -966.0D);
/* 215:295 */     while (p > 0)
/* 216:    */     {
/* 217:310 */       for (int k = p - 2; k >= -1; k--)
/* 218:    */       {
/* 219:311 */         if (k == -1) {
/* 220:    */           break;
/* 221:    */         }
/* 222:314 */         if (Math.abs(e[k]) <= tiny + eps * (Math.abs(this.s[k]) + Math.abs(this.s[(k + 1)])))
/* 223:    */         {
/* 224:316 */           e[k] = 0.0D;
/* 225:317 */           break;
/* 226:    */         }
/* 227:    */       }
/* 228:    */       int kase;
/* 229:    */       int kase;
/* 230:320 */       if (k == p - 2)
/* 231:    */       {
/* 232:321 */         kase = 4;
/* 233:    */       }
/* 234:    */       else
/* 235:    */       {
/* 236:324 */         for (int ks = p - 1; ks >= k; ks--)
/* 237:    */         {
/* 238:325 */           if (ks == k) {
/* 239:    */             break;
/* 240:    */           }
/* 241:328 */           double t = (ks != p ? Math.abs(e[ks]) : 0.0D) + (ks != k + 1 ? Math.abs(e[(ks - 1)]) : 0.0D);
/* 242:330 */           if (Math.abs(this.s[ks]) <= tiny + eps * t)
/* 243:    */           {
/* 244:331 */             this.s[ks] = 0.0D;
/* 245:332 */             break;
/* 246:    */           }
/* 247:    */         }
/* 248:    */         int kase;
/* 249:335 */         if (ks == k)
/* 250:    */         {
/* 251:336 */           kase = 3;
/* 252:    */         }
/* 253:    */         else
/* 254:    */         {
/* 255:    */           int kase;
/* 256:337 */           if (ks == p - 1)
/* 257:    */           {
/* 258:338 */             kase = 1;
/* 259:    */           }
/* 260:    */           else
/* 261:    */           {
/* 262:340 */             kase = 2;
/* 263:341 */             k = ks;
/* 264:    */           }
/* 265:    */         }
/* 266:    */       }
/* 267:344 */       k++;
/* 268:348 */       switch (kase)
/* 269:    */       {
/* 270:    */       case 1: 
/* 271:353 */         double f = e[(p - 2)];
/* 272:354 */         e[(p - 2)] = 0.0D;
/* 273:355 */         for (int j = p - 2; j >= k; j--)
/* 274:    */         {
/* 275:356 */           double t = Maths.hypot(this.s[j], f);
/* 276:357 */           double cs = this.s[j] / t;
/* 277:358 */           double sn = f / t;
/* 278:359 */           this.s[j] = t;
/* 279:360 */           if (j != k)
/* 280:    */           {
/* 281:361 */             f = -sn * e[(j - 1)];
/* 282:362 */             e[(j - 1)] = (cs * e[(j - 1)]);
/* 283:    */           }
/* 284:364 */           if (wantv) {
/* 285:365 */             for (int i = 0; i < this.n; i++)
/* 286:    */             {
/* 287:366 */               t = cs * this.V[i][j] + sn * this.V[i][(p - 1)];
/* 288:367 */               this.V[i][(p - 1)] = (-sn * this.V[i][j] + cs * this.V[i][(p - 1)]);
/* 289:368 */               this.V[i][j] = t;
/* 290:    */             }
/* 291:    */           }
/* 292:    */         }
/* 293:373 */         break;
/* 294:    */       case 2: 
/* 295:378 */         double f = e[(k - 1)];
/* 296:379 */         e[(k - 1)] = 0.0D;
/* 297:380 */         for (int j = k; j < p; j++)
/* 298:    */         {
/* 299:381 */           double t = Maths.hypot(this.s[j], f);
/* 300:382 */           double cs = this.s[j] / t;
/* 301:383 */           double sn = f / t;
/* 302:384 */           this.s[j] = t;
/* 303:385 */           f = -sn * e[j];
/* 304:386 */           e[j] = (cs * e[j]);
/* 305:387 */           if (wantu) {
/* 306:388 */             for (int i = 0; i < this.m; i++)
/* 307:    */             {
/* 308:389 */               t = cs * this.U[i][j] + sn * this.U[i][(k - 1)];
/* 309:390 */               this.U[i][(k - 1)] = (-sn * this.U[i][j] + cs * this.U[i][(k - 1)]);
/* 310:391 */               this.U[i][j] = t;
/* 311:    */             }
/* 312:    */           }
/* 313:    */         }
/* 314:396 */         break;
/* 315:    */       case 3: 
/* 316:404 */         double scale = Math.max(Math.max(Math.max(Math.max(Math.abs(this.s[(p - 1)]), Math.abs(this.s[(p - 2)])), Math.abs(e[(p - 2)])), Math.abs(this.s[k])), Math.abs(e[k]));
/* 317:    */         
/* 318:    */ 
/* 319:407 */         double sp = this.s[(p - 1)] / scale;
/* 320:408 */         double spm1 = this.s[(p - 2)] / scale;
/* 321:409 */         double epm1 = e[(p - 2)] / scale;
/* 322:410 */         double sk = this.s[k] / scale;
/* 323:411 */         double ek = e[k] / scale;
/* 324:412 */         double b = ((spm1 + sp) * (spm1 - sp) + epm1 * epm1) / 2.0D;
/* 325:413 */         double c = sp * epm1 * (sp * epm1);
/* 326:414 */         double shift = 0.0D;
/* 327:415 */         if (((b != 0.0D ? 1 : 0) | (c != 0.0D ? 1 : 0)) != 0)
/* 328:    */         {
/* 329:416 */           shift = Math.sqrt(b * b + c);
/* 330:417 */           if (b < 0.0D) {
/* 331:418 */             shift = -shift;
/* 332:    */           }
/* 333:420 */           shift = c / (b + shift);
/* 334:    */         }
/* 335:422 */         double f = (sk + sp) * (sk - sp) + shift;
/* 336:423 */         double g = sk * ek;
/* 337:427 */         for (int j = k; j < p - 1; j++)
/* 338:    */         {
/* 339:428 */           double t = Maths.hypot(f, g);
/* 340:429 */           double cs = f / t;
/* 341:430 */           double sn = g / t;
/* 342:431 */           if (j != k) {
/* 343:432 */             e[(j - 1)] = t;
/* 344:    */           }
/* 345:434 */           f = cs * this.s[j] + sn * e[j];
/* 346:435 */           e[j] = (cs * e[j] - sn * this.s[j]);
/* 347:436 */           g = sn * this.s[(j + 1)];
/* 348:437 */           this.s[(j + 1)] = (cs * this.s[(j + 1)]);
/* 349:438 */           if (wantv) {
/* 350:439 */             for (int i = 0; i < this.n; i++)
/* 351:    */             {
/* 352:440 */               t = cs * this.V[i][j] + sn * this.V[i][(j + 1)];
/* 353:441 */               this.V[i][(j + 1)] = (-sn * this.V[i][j] + cs * this.V[i][(j + 1)]);
/* 354:442 */               this.V[i][j] = t;
/* 355:    */             }
/* 356:    */           }
/* 357:445 */           t = Maths.hypot(f, g);
/* 358:446 */           cs = f / t;
/* 359:447 */           sn = g / t;
/* 360:448 */           this.s[j] = t;
/* 361:449 */           f = cs * e[j] + sn * this.s[(j + 1)];
/* 362:450 */           this.s[(j + 1)] = (-sn * e[j] + cs * this.s[(j + 1)]);
/* 363:451 */           g = sn * e[(j + 1)];
/* 364:452 */           e[(j + 1)] = (cs * e[(j + 1)]);
/* 365:453 */           if ((wantu) && (j < this.m - 1)) {
/* 366:454 */             for (int i = 0; i < this.m; i++)
/* 367:    */             {
/* 368:455 */               t = cs * this.U[i][j] + sn * this.U[i][(j + 1)];
/* 369:456 */               this.U[i][(j + 1)] = (-sn * this.U[i][j] + cs * this.U[i][(j + 1)]);
/* 370:457 */               this.U[i][j] = t;
/* 371:    */             }
/* 372:    */           }
/* 373:    */         }
/* 374:461 */         e[(p - 2)] = f;
/* 375:462 */         iter += 1;
/* 376:    */         
/* 377:464 */         break;
/* 378:    */       case 4: 
/* 379:472 */         if (this.s[k] <= 0.0D)
/* 380:    */         {
/* 381:473 */           this.s[k] = (this.s[k] < 0.0D ? -this.s[k] : 0.0D);
/* 382:474 */           if (wantv) {
/* 383:475 */             for (int i = 0; i <= pp; i++) {
/* 384:476 */               this.V[i][k] = (-this.V[i][k]);
/* 385:    */             }
/* 386:    */           }
/* 387:    */         }
/* 388:483 */         while ((k < pp) && 
/* 389:484 */           (this.s[k] < this.s[(k + 1)]))
/* 390:    */         {
/* 391:487 */           double t = this.s[k];
/* 392:488 */           this.s[k] = this.s[(k + 1)];
/* 393:489 */           this.s[(k + 1)] = t;
/* 394:490 */           if ((wantv) && (k < this.n - 1)) {
/* 395:491 */             for (int i = 0; i < this.n; i++)
/* 396:    */             {
/* 397:492 */               t = this.V[i][(k + 1)];this.V[i][(k + 1)] = this.V[i][k];this.V[i][k] = t;
/* 398:    */             }
/* 399:    */           }
/* 400:495 */           if ((wantu) && (k < this.m - 1)) {
/* 401:496 */             for (int i = 0; i < this.m; i++)
/* 402:    */             {
/* 403:497 */               t = this.U[i][(k + 1)];this.U[i][(k + 1)] = this.U[i][k];this.U[i][k] = t;
/* 404:    */             }
/* 405:    */           }
/* 406:500 */           k++;
/* 407:    */         }
/* 408:502 */         iter = 0;
/* 409:503 */         p--;
/* 410:    */       }
/* 411:    */     }
/* 412:509 */     if (usingTranspose)
/* 413:    */     {
/* 414:510 */       int temp = this.m;
/* 415:511 */       this.m = this.n;
/* 416:512 */       this.n = temp;
/* 417:513 */       double[][] tempA = this.U;
/* 418:514 */       this.U = this.V;
/* 419:515 */       this.V = tempA;
/* 420:    */     }
/* 421:    */   }
/* 422:    */   
/* 423:    */   public Matrix getU()
/* 424:    */   {
/* 425:524 */     return new Matrix(this.U, this.m, this.m);
/* 426:    */   }
/* 427:    */   
/* 428:    */   public Matrix getV()
/* 429:    */   {
/* 430:532 */     return new Matrix(this.V, this.n, this.n);
/* 431:    */   }
/* 432:    */   
/* 433:    */   public double[] getSingularValues()
/* 434:    */   {
/* 435:540 */     return this.s;
/* 436:    */   }
/* 437:    */   
/* 438:    */   public Matrix getS()
/* 439:    */   {
/* 440:548 */     Matrix X = new Matrix(this.m, this.n);
/* 441:549 */     double[][] S = X.getArray();
/* 442:550 */     for (int i = 0; i < Math.min(this.m, this.n); i++) {
/* 443:551 */       S[i][i] = this.s[i];
/* 444:    */     }
/* 445:553 */     return X;
/* 446:    */   }
/* 447:    */   
/* 448:    */   public double norm2()
/* 449:    */   {
/* 450:561 */     return this.s[0];
/* 451:    */   }
/* 452:    */   
/* 453:    */   public double cond()
/* 454:    */   {
/* 455:569 */     return this.s[0] / this.s[(Math.min(this.m, this.n) - 1)];
/* 456:    */   }
/* 457:    */   
/* 458:    */   public int rank()
/* 459:    */   {
/* 460:577 */     double eps = Math.pow(2.0D, -52.0D);
/* 461:578 */     double tol = Math.max(this.m, this.n) * this.s[0] * eps;
/* 462:579 */     int r = 0;
/* 463:580 */     for (int i = 0; i < this.s.length; i++) {
/* 464:581 */       if (this.s[i] > tol) {
/* 465:582 */         r++;
/* 466:    */       }
/* 467:    */     }
/* 468:585 */     return r;
/* 469:    */   }
/* 470:    */   
/* 471:    */   public String getRevision()
/* 472:    */   {
/* 473:594 */     return RevisionUtils.extract("$Revision: 11815 $");
/* 474:    */   }
/* 475:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.matrix.SingularValueDecomposition
 * JD-Core Version:    0.7.0.1
 */