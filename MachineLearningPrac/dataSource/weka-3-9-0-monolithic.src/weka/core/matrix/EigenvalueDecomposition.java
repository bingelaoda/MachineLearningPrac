/*   1:    */ package weka.core.matrix;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.RevisionHandler;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ 
/*   7:    */ public class EigenvalueDecomposition
/*   8:    */   implements Serializable, RevisionHandler
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = 4011654467211422319L;
/*  11:    */   private int n;
/*  12:    */   private boolean issymmetric;
/*  13:    */   private double[] d;
/*  14:    */   private double[] e;
/*  15:    */   private double[][] V;
/*  16:    */   private double[][] H;
/*  17:    */   private double[] ort;
/*  18:    */   private transient double cdivr;
/*  19:    */   private transient double cdivi;
/*  20:    */   
/*  21:    */   private void tred2()
/*  22:    */   {
/*  23:100 */     for (int j = 0; j < this.n; j++) {
/*  24:101 */       this.d[j] = this.V[(this.n - 1)][j];
/*  25:    */     }
/*  26:106 */     for (int i = this.n - 1; i > 0; i--)
/*  27:    */     {
/*  28:110 */       double scale = 0.0D;
/*  29:111 */       double h = 0.0D;
/*  30:112 */       for (int k = 0; k < i; k++) {
/*  31:113 */         scale += Math.abs(this.d[k]);
/*  32:    */       }
/*  33:115 */       if (scale == 0.0D)
/*  34:    */       {
/*  35:116 */         this.e[i] = this.d[(i - 1)];
/*  36:117 */         for (int j = 0; j < i; j++)
/*  37:    */         {
/*  38:118 */           this.d[j] = this.V[(i - 1)][j];
/*  39:119 */           this.V[i][j] = 0.0D;
/*  40:120 */           this.V[j][i] = 0.0D;
/*  41:    */         }
/*  42:    */       }
/*  43:    */       else
/*  44:    */       {
/*  45:126 */         for (int k = 0; k < i; k++)
/*  46:    */         {
/*  47:127 */           this.d[k] /= scale;
/*  48:128 */           h += this.d[k] * this.d[k];
/*  49:    */         }
/*  50:130 */         double f = this.d[(i - 1)];
/*  51:131 */         double g = Math.sqrt(h);
/*  52:132 */         if (f > 0.0D) {
/*  53:133 */           g = -g;
/*  54:    */         }
/*  55:135 */         this.e[i] = (scale * g);
/*  56:136 */         h -= f * g;
/*  57:137 */         this.d[(i - 1)] = (f - g);
/*  58:138 */         for (int j = 0; j < i; j++) {
/*  59:139 */           this.e[j] = 0.0D;
/*  60:    */         }
/*  61:144 */         for (int j = 0; j < i; j++)
/*  62:    */         {
/*  63:145 */           f = this.d[j];
/*  64:146 */           this.V[j][i] = f;
/*  65:147 */           g = this.e[j] + this.V[j][j] * f;
/*  66:148 */           for (int k = j + 1; k <= i - 1; k++)
/*  67:    */           {
/*  68:149 */             g += this.V[k][j] * this.d[k];
/*  69:150 */             this.e[k] += this.V[k][j] * f;
/*  70:    */           }
/*  71:152 */           this.e[j] = g;
/*  72:    */         }
/*  73:154 */         f = 0.0D;
/*  74:155 */         for (int j = 0; j < i; j++)
/*  75:    */         {
/*  76:156 */           this.e[j] /= h;
/*  77:157 */           f += this.e[j] * this.d[j];
/*  78:    */         }
/*  79:159 */         double hh = f / (h + h);
/*  80:160 */         for (int j = 0; j < i; j++) {
/*  81:161 */           this.e[j] -= hh * this.d[j];
/*  82:    */         }
/*  83:163 */         for (int j = 0; j < i; j++)
/*  84:    */         {
/*  85:164 */           f = this.d[j];
/*  86:165 */           g = this.e[j];
/*  87:166 */           for (int k = j; k <= i - 1; k++) {
/*  88:167 */             this.V[k][j] -= f * this.e[k] + g * this.d[k];
/*  89:    */           }
/*  90:169 */           this.d[j] = this.V[(i - 1)][j];
/*  91:170 */           this.V[i][j] = 0.0D;
/*  92:    */         }
/*  93:    */       }
/*  94:173 */       this.d[i] = h;
/*  95:    */     }
/*  96:178 */     for (int i = 0; i < this.n - 1; i++)
/*  97:    */     {
/*  98:179 */       this.V[(this.n - 1)][i] = this.V[i][i];
/*  99:180 */       this.V[i][i] = 1.0D;
/* 100:181 */       double h = this.d[(i + 1)];
/* 101:182 */       if (h != 0.0D)
/* 102:    */       {
/* 103:183 */         for (int k = 0; k <= i; k++) {
/* 104:184 */           this.d[k] = (this.V[k][(i + 1)] / h);
/* 105:    */         }
/* 106:186 */         for (int j = 0; j <= i; j++)
/* 107:    */         {
/* 108:187 */           double g = 0.0D;
/* 109:188 */           for (int k = 0; k <= i; k++) {
/* 110:189 */             g += this.V[k][(i + 1)] * this.V[k][j];
/* 111:    */           }
/* 112:191 */           for (int k = 0; k <= i; k++) {
/* 113:192 */             this.V[k][j] -= g * this.d[k];
/* 114:    */           }
/* 115:    */         }
/* 116:    */       }
/* 117:196 */       for (int k = 0; k <= i; k++) {
/* 118:197 */         this.V[k][(i + 1)] = 0.0D;
/* 119:    */       }
/* 120:    */     }
/* 121:200 */     for (int j = 0; j < this.n; j++)
/* 122:    */     {
/* 123:201 */       this.d[j] = this.V[(this.n - 1)][j];
/* 124:202 */       this.V[(this.n - 1)][j] = 0.0D;
/* 125:    */     }
/* 126:204 */     this.V[(this.n - 1)][(this.n - 1)] = 1.0D;
/* 127:205 */     this.e[0] = 0.0D;
/* 128:    */   }
/* 129:    */   
/* 130:    */   private void tql2()
/* 131:    */   {
/* 132:217 */     for (int i = 1; i < this.n; i++) {
/* 133:218 */       this.e[(i - 1)] = this.e[i];
/* 134:    */     }
/* 135:220 */     this.e[(this.n - 1)] = 0.0D;
/* 136:    */     
/* 137:222 */     double f = 0.0D;
/* 138:223 */     double tst1 = 0.0D;
/* 139:224 */     double eps = Math.pow(2.0D, -52.0D);
/* 140:225 */     for (int l = 0; l < this.n; l++)
/* 141:    */     {
/* 142:229 */       tst1 = Math.max(tst1, Math.abs(this.d[l]) + Math.abs(this.e[l]));
/* 143:230 */       int m = l;
/* 144:231 */       while ((m < this.n) && 
/* 145:232 */         (Math.abs(this.e[m]) > eps * tst1)) {
/* 146:235 */         m++;
/* 147:    */       }
/* 148:241 */       if (m > l)
/* 149:    */       {
/* 150:242 */         int iter = 0;
/* 151:    */         do
/* 152:    */         {
/* 153:244 */           iter += 1;
/* 154:    */           
/* 155:    */ 
/* 156:    */ 
/* 157:248 */           double g = this.d[l];
/* 158:249 */           double p = (this.d[(l + 1)] - g) / (2.0D * this.e[l]);
/* 159:250 */           double r = Maths.hypot(p, 1.0D);
/* 160:251 */           if (p < 0.0D) {
/* 161:252 */             r = -r;
/* 162:    */           }
/* 163:254 */           this.d[l] = (this.e[l] / (p + r));
/* 164:255 */           this.d[(l + 1)] = (this.e[l] * (p + r));
/* 165:256 */           double dl1 = this.d[(l + 1)];
/* 166:257 */           double h = g - this.d[l];
/* 167:258 */           for (int i = l + 2; i < this.n; i++) {
/* 168:259 */             this.d[i] -= h;
/* 169:    */           }
/* 170:261 */           f += h;
/* 171:    */           
/* 172:    */ 
/* 173:    */ 
/* 174:265 */           p = this.d[m];
/* 175:266 */           double c = 1.0D;
/* 176:267 */           double c2 = c;
/* 177:268 */           double c3 = c;
/* 178:269 */           double el1 = this.e[(l + 1)];
/* 179:270 */           double s = 0.0D;
/* 180:271 */           double s2 = 0.0D;
/* 181:272 */           for (int i = m - 1; i >= l; i--)
/* 182:    */           {
/* 183:273 */             c3 = c2;
/* 184:274 */             c2 = c;
/* 185:275 */             s2 = s;
/* 186:276 */             g = c * this.e[i];
/* 187:277 */             h = c * p;
/* 188:278 */             r = Maths.hypot(p, this.e[i]);
/* 189:279 */             this.e[(i + 1)] = (s * r);
/* 190:280 */             s = this.e[i] / r;
/* 191:281 */             c = p / r;
/* 192:282 */             p = c * this.d[i] - s * g;
/* 193:283 */             this.d[(i + 1)] = (h + s * (c * g + s * this.d[i]));
/* 194:287 */             for (int k = 0; k < this.n; k++)
/* 195:    */             {
/* 196:288 */               h = this.V[k][(i + 1)];
/* 197:289 */               this.V[k][(i + 1)] = (s * this.V[k][i] + c * h);
/* 198:290 */               this.V[k][i] = (c * this.V[k][i] - s * h);
/* 199:    */             }
/* 200:    */           }
/* 201:293 */           p = -s * s2 * c3 * el1 * this.e[l] / dl1;
/* 202:294 */           this.e[l] = (s * p);
/* 203:295 */           this.d[l] = (c * p);
/* 204:299 */         } while (Math.abs(this.e[l]) > eps * tst1);
/* 205:    */       }
/* 206:301 */       this.d[l] += f;
/* 207:302 */       this.e[l] = 0.0D;
/* 208:    */     }
/* 209:307 */     for (int i = 0; i < this.n - 1; i++)
/* 210:    */     {
/* 211:308 */       int k = i;
/* 212:309 */       double p = this.d[i];
/* 213:310 */       for (int j = i + 1; j < this.n; j++) {
/* 214:311 */         if (this.d[j] < p)
/* 215:    */         {
/* 216:312 */           k = j;
/* 217:313 */           p = this.d[j];
/* 218:    */         }
/* 219:    */       }
/* 220:316 */       if (k != i)
/* 221:    */       {
/* 222:317 */         this.d[k] = this.d[i];
/* 223:318 */         this.d[i] = p;
/* 224:319 */         for (int j = 0; j < this.n; j++)
/* 225:    */         {
/* 226:320 */           p = this.V[j][i];
/* 227:321 */           this.V[j][i] = this.V[j][k];
/* 228:322 */           this.V[j][k] = p;
/* 229:    */         }
/* 230:    */       }
/* 231:    */     }
/* 232:    */   }
/* 233:    */   
/* 234:    */   private void orthes()
/* 235:    */   {
/* 236:337 */     int low = 0;
/* 237:338 */     int high = this.n - 1;
/* 238:340 */     for (int m = low + 1; m <= high - 1; m++)
/* 239:    */     {
/* 240:344 */       double scale = 0.0D;
/* 241:345 */       for (int i = m; i <= high; i++) {
/* 242:346 */         scale += Math.abs(this.H[i][(m - 1)]);
/* 243:    */       }
/* 244:348 */       if (scale != 0.0D)
/* 245:    */       {
/* 246:352 */         double h = 0.0D;
/* 247:353 */         for (int i = high; i >= m; i--)
/* 248:    */         {
/* 249:354 */           this.ort[i] = (this.H[i][(m - 1)] / scale);
/* 250:355 */           h += this.ort[i] * this.ort[i];
/* 251:    */         }
/* 252:357 */         double g = Math.sqrt(h);
/* 253:358 */         if (this.ort[m] > 0.0D) {
/* 254:359 */           g = -g;
/* 255:    */         }
/* 256:361 */         h -= this.ort[m] * g;
/* 257:362 */         this.ort[m] -= g;
/* 258:367 */         for (int j = m; j < this.n; j++)
/* 259:    */         {
/* 260:368 */           double f = 0.0D;
/* 261:369 */           for (int i = high; i >= m; i--) {
/* 262:370 */             f += this.ort[i] * this.H[i][j];
/* 263:    */           }
/* 264:372 */           f /= h;
/* 265:373 */           for (int i = m; i <= high; i++) {
/* 266:374 */             this.H[i][j] -= f * this.ort[i];
/* 267:    */           }
/* 268:    */         }
/* 269:378 */         for (int i = 0; i <= high; i++)
/* 270:    */         {
/* 271:379 */           double f = 0.0D;
/* 272:380 */           for (int j = high; j >= m; j--) {
/* 273:381 */             f += this.ort[j] * this.H[i][j];
/* 274:    */           }
/* 275:383 */           f /= h;
/* 276:384 */           for (int j = m; j <= high; j++) {
/* 277:385 */             this.H[i][j] -= f * this.ort[j];
/* 278:    */           }
/* 279:    */         }
/* 280:388 */         this.ort[m] = (scale * this.ort[m]);
/* 281:389 */         this.H[m][(m - 1)] = (scale * g);
/* 282:    */       }
/* 283:    */     }
/* 284:395 */     for (int i = 0; i < this.n; i++) {
/* 285:396 */       for (int j = 0; j < this.n; j++) {
/* 286:397 */         this.V[i][j] = (i == j ? 1.0D : 0.0D);
/* 287:    */       }
/* 288:    */     }
/* 289:401 */     for (int m = high - 1; m >= low + 1; m--) {
/* 290:402 */       if (this.H[m][(m - 1)] != 0.0D)
/* 291:    */       {
/* 292:403 */         for (int i = m + 1; i <= high; i++) {
/* 293:404 */           this.ort[i] = this.H[i][(m - 1)];
/* 294:    */         }
/* 295:406 */         for (int j = m; j <= high; j++)
/* 296:    */         {
/* 297:407 */           double g = 0.0D;
/* 298:408 */           for (int i = m; i <= high; i++) {
/* 299:409 */             g += this.ort[i] * this.V[i][j];
/* 300:    */           }
/* 301:412 */           g = g / this.ort[m] / this.H[m][(m - 1)];
/* 302:413 */           for (int i = m; i <= high; i++) {
/* 303:414 */             this.V[i][j] += g * this.ort[i];
/* 304:    */           }
/* 305:    */         }
/* 306:    */       }
/* 307:    */     }
/* 308:    */   }
/* 309:    */   
/* 310:    */   private void cdiv(double xr, double xi, double yr, double yi)
/* 311:    */   {
/* 312:427 */     if (Math.abs(yr) > Math.abs(yi))
/* 313:    */     {
/* 314:428 */       double r = yi / yr;
/* 315:429 */       double d = yr + r * yi;
/* 316:430 */       this.cdivr = ((xr + r * xi) / d);
/* 317:431 */       this.cdivi = ((xi - r * xr) / d);
/* 318:    */     }
/* 319:    */     else
/* 320:    */     {
/* 321:433 */       double r = yr / yi;
/* 322:434 */       double d = yi + r * yr;
/* 323:435 */       this.cdivr = ((r * xr + xi) / d);
/* 324:436 */       this.cdivi = ((r * xi - xr) / d);
/* 325:    */     }
/* 326:    */   }
/* 327:    */   
/* 328:    */   private void hqr2()
/* 329:    */   {
/* 330:452 */     int nn = this.n;
/* 331:453 */     int n = nn - 1;
/* 332:454 */     int low = 0;
/* 333:455 */     int high = nn - 1;
/* 334:456 */     double eps = Math.pow(2.0D, -52.0D);
/* 335:457 */     double exshift = 0.0D;
/* 336:458 */     double p = 0.0D;double q = 0.0D;double r = 0.0D;double s = 0.0D;double z = 0.0D;
/* 337:    */     
/* 338:    */ 
/* 339:    */ 
/* 340:462 */     double norm = 0.0D;
/* 341:463 */     for (int i = 0; i < nn; i++)
/* 342:    */     {
/* 343:464 */       if (((i < low ? 1 : 0) | (i > high ? 1 : 0)) != 0)
/* 344:    */       {
/* 345:465 */         this.d[i] = this.H[i][i];
/* 346:466 */         this.e[i] = 0.0D;
/* 347:    */       }
/* 348:468 */       for (int j = Math.max(i - 1, 0); j < nn; j++) {
/* 349:469 */         norm += Math.abs(this.H[i][j]);
/* 350:    */       }
/* 351:    */     }
/* 352:475 */     int iter = 0;
/* 353:476 */     while (n >= low)
/* 354:    */     {
/* 355:480 */       int l = n;
/* 356:481 */       while (l > low)
/* 357:    */       {
/* 358:482 */         s = Math.abs(this.H[(l - 1)][(l - 1)]) + Math.abs(this.H[l][l]);
/* 359:483 */         if (s == 0.0D) {
/* 360:484 */           s = norm;
/* 361:    */         }
/* 362:486 */         if (Math.abs(this.H[l][(l - 1)]) < eps * s) {
/* 363:    */           break;
/* 364:    */         }
/* 365:489 */         l--;
/* 366:    */       }
/* 367:495 */       if (l == n)
/* 368:    */       {
/* 369:496 */         this.H[n][n] += exshift;
/* 370:497 */         this.d[n] = this.H[n][n];
/* 371:498 */         this.e[n] = 0.0D;
/* 372:499 */         n--;
/* 373:500 */         iter = 0;
/* 374:    */       }
/* 375:504 */       else if (l == n - 1)
/* 376:    */       {
/* 377:505 */         double w = this.H[n][(n - 1)] * this.H[(n - 1)][n];
/* 378:506 */         p = (this.H[(n - 1)][(n - 1)] - this.H[n][n]) / 2.0D;
/* 379:507 */         q = p * p + w;
/* 380:508 */         z = Math.sqrt(Math.abs(q));
/* 381:509 */         this.H[n][n] += exshift;
/* 382:510 */         this.H[(n - 1)][(n - 1)] += exshift;
/* 383:511 */         double x = this.H[n][n];
/* 384:515 */         if (q >= 0.0D)
/* 385:    */         {
/* 386:516 */           if (p >= 0.0D) {
/* 387:517 */             z = p + z;
/* 388:    */           } else {
/* 389:519 */             z = p - z;
/* 390:    */           }
/* 391:521 */           this.d[(n - 1)] = (x + z);
/* 392:522 */           this.d[n] = this.d[(n - 1)];
/* 393:523 */           if (z != 0.0D) {
/* 394:524 */             this.d[n] = (x - w / z);
/* 395:    */           }
/* 396:526 */           this.e[(n - 1)] = 0.0D;
/* 397:527 */           this.e[n] = 0.0D;
/* 398:528 */           x = this.H[n][(n - 1)];
/* 399:529 */           s = Math.abs(x) + Math.abs(z);
/* 400:530 */           p = x / s;
/* 401:531 */           q = z / s;
/* 402:532 */           r = Math.sqrt(p * p + q * q);
/* 403:533 */           p /= r;
/* 404:534 */           q /= r;
/* 405:538 */           for (int j = n - 1; j < nn; j++)
/* 406:    */           {
/* 407:539 */             z = this.H[(n - 1)][j];
/* 408:540 */             this.H[(n - 1)][j] = (q * z + p * this.H[n][j]);
/* 409:541 */             this.H[n][j] = (q * this.H[n][j] - p * z);
/* 410:    */           }
/* 411:546 */           for (int i = 0; i <= n; i++)
/* 412:    */           {
/* 413:547 */             z = this.H[i][(n - 1)];
/* 414:548 */             this.H[i][(n - 1)] = (q * z + p * this.H[i][n]);
/* 415:549 */             this.H[i][n] = (q * this.H[i][n] - p * z);
/* 416:    */           }
/* 417:554 */           for (int i = low; i <= high; i++)
/* 418:    */           {
/* 419:555 */             z = this.V[i][(n - 1)];
/* 420:556 */             this.V[i][(n - 1)] = (q * z + p * this.V[i][n]);
/* 421:557 */             this.V[i][n] = (q * this.V[i][n] - p * z);
/* 422:    */           }
/* 423:    */         }
/* 424:    */         else
/* 425:    */         {
/* 426:563 */           this.d[(n - 1)] = (x + p);
/* 427:564 */           this.d[n] = (x + p);
/* 428:565 */           this.e[(n - 1)] = z;
/* 429:566 */           this.e[n] = (-z);
/* 430:    */         }
/* 431:568 */         n -= 2;
/* 432:569 */         iter = 0;
/* 433:    */       }
/* 434:    */       else
/* 435:    */       {
/* 436:577 */         double x = this.H[n][n];
/* 437:578 */         double y = 0.0D;
/* 438:579 */         double w = 0.0D;
/* 439:580 */         if (l < n)
/* 440:    */         {
/* 441:581 */           y = this.H[(n - 1)][(n - 1)];
/* 442:582 */           w = this.H[n][(n - 1)] * this.H[(n - 1)][n];
/* 443:    */         }
/* 444:587 */         if (iter == 10)
/* 445:    */         {
/* 446:588 */           exshift += x;
/* 447:589 */           for (int i = low; i <= n; i++) {
/* 448:590 */             this.H[i][i] -= x;
/* 449:    */           }
/* 450:592 */           s = Math.abs(this.H[n][(n - 1)]) + Math.abs(this.H[(n - 1)][(n - 2)]);
/* 451:593 */           x = y = 0.75D * s;
/* 452:594 */           w = -0.4375D * s * s;
/* 453:    */         }
/* 454:599 */         if (iter == 30)
/* 455:    */         {
/* 456:600 */           s = (y - x) / 2.0D;
/* 457:601 */           s = s * s + w;
/* 458:602 */           if (s > 0.0D)
/* 459:    */           {
/* 460:603 */             s = Math.sqrt(s);
/* 461:604 */             if (y < x) {
/* 462:605 */               s = -s;
/* 463:    */             }
/* 464:607 */             s = x - w / ((y - x) / 2.0D + s);
/* 465:608 */             for (int i = low; i <= n; i++) {
/* 466:609 */               this.H[i][i] -= s;
/* 467:    */             }
/* 468:611 */             exshift += s;
/* 469:612 */             x = y = w = 0.964D;
/* 470:    */           }
/* 471:    */         }
/* 472:616 */         iter += 1;
/* 473:    */         
/* 474:    */ 
/* 475:    */ 
/* 476:620 */         int m = n - 2;
/* 477:621 */         while (m >= l)
/* 478:    */         {
/* 479:622 */           z = this.H[m][m];
/* 480:623 */           r = x - z;
/* 481:624 */           s = y - z;
/* 482:625 */           p = (r * s - w) / this.H[(m + 1)][m] + this.H[m][(m + 1)];
/* 483:626 */           q = this.H[(m + 1)][(m + 1)] - z - r - s;
/* 484:627 */           r = this.H[(m + 2)][(m + 1)];
/* 485:628 */           s = Math.abs(p) + Math.abs(q) + Math.abs(r);
/* 486:629 */           p /= s;
/* 487:630 */           q /= s;
/* 488:631 */           r /= s;
/* 489:632 */           if (m == l) {
/* 490:    */             break;
/* 491:    */           }
/* 492:635 */           if (Math.abs(this.H[m][(m - 1)]) * (Math.abs(q) + Math.abs(r)) < eps * (Math.abs(p) * (Math.abs(this.H[(m - 1)][(m - 1)]) + Math.abs(z) + Math.abs(this.H[(m + 1)][(m + 1)])))) {
/* 493:    */             break;
/* 494:    */           }
/* 495:640 */           m--;
/* 496:    */         }
/* 497:643 */         for (int i = m + 2; i <= n; i++)
/* 498:    */         {
/* 499:644 */           this.H[i][(i - 2)] = 0.0D;
/* 500:645 */           if (i > m + 2) {
/* 501:646 */             this.H[i][(i - 3)] = 0.0D;
/* 502:    */           }
/* 503:    */         }
/* 504:652 */         for (int k = m; k <= n - 1; k++)
/* 505:    */         {
/* 506:653 */           boolean notlast = k != n - 1;
/* 507:654 */           if (k != m)
/* 508:    */           {
/* 509:655 */             p = this.H[k][(k - 1)];
/* 510:656 */             q = this.H[(k + 1)][(k - 1)];
/* 511:657 */             r = notlast ? this.H[(k + 2)][(k - 1)] : 0.0D;
/* 512:658 */             x = Math.abs(p) + Math.abs(q) + Math.abs(r);
/* 513:659 */             if (x != 0.0D)
/* 514:    */             {
/* 515:660 */               p /= x;
/* 516:661 */               q /= x;
/* 517:662 */               r /= x;
/* 518:    */             }
/* 519:    */           }
/* 520:665 */           if (x == 0.0D) {
/* 521:    */             break;
/* 522:    */           }
/* 523:668 */           s = Math.sqrt(p * p + q * q + r * r);
/* 524:669 */           if (p < 0.0D) {
/* 525:670 */             s = -s;
/* 526:    */           }
/* 527:672 */           if (s != 0.0D)
/* 528:    */           {
/* 529:673 */             if (k != m) {
/* 530:674 */               this.H[k][(k - 1)] = (-s * x);
/* 531:675 */             } else if (l != m) {
/* 532:676 */               this.H[k][(k - 1)] = (-this.H[k][(k - 1)]);
/* 533:    */             }
/* 534:678 */             p += s;
/* 535:679 */             x = p / s;
/* 536:680 */             y = q / s;
/* 537:681 */             z = r / s;
/* 538:682 */             q /= p;
/* 539:683 */             r /= p;
/* 540:687 */             for (int j = k; j < nn; j++)
/* 541:    */             {
/* 542:688 */               p = this.H[k][j] + q * this.H[(k + 1)][j];
/* 543:689 */               if (notlast)
/* 544:    */               {
/* 545:690 */                 p += r * this.H[(k + 2)][j];
/* 546:691 */                 this.H[(k + 2)][j] -= p * z;
/* 547:    */               }
/* 548:693 */               this.H[k][j] -= p * x;
/* 549:694 */               this.H[(k + 1)][j] -= p * y;
/* 550:    */             }
/* 551:699 */             for (int i = 0; i <= Math.min(n, k + 3); i++)
/* 552:    */             {
/* 553:700 */               p = x * this.H[i][k] + y * this.H[i][(k + 1)];
/* 554:701 */               if (notlast)
/* 555:    */               {
/* 556:702 */                 p += z * this.H[i][(k + 2)];
/* 557:703 */                 this.H[i][(k + 2)] -= p * r;
/* 558:    */               }
/* 559:705 */               this.H[i][k] -= p;
/* 560:706 */               this.H[i][(k + 1)] -= p * q;
/* 561:    */             }
/* 562:711 */             for (int i = low; i <= high; i++)
/* 563:    */             {
/* 564:712 */               p = x * this.V[i][k] + y * this.V[i][(k + 1)];
/* 565:713 */               if (notlast)
/* 566:    */               {
/* 567:714 */                 p += z * this.V[i][(k + 2)];
/* 568:715 */                 this.V[i][(k + 2)] -= p * r;
/* 569:    */               }
/* 570:717 */               this.V[i][k] -= p;
/* 571:718 */               this.V[i][(k + 1)] -= p * q;
/* 572:    */             }
/* 573:    */           }
/* 574:    */         }
/* 575:    */       }
/* 576:    */     }
/* 577:727 */     if (norm == 0.0D) {
/* 578:728 */       return;
/* 579:    */     }
/* 580:731 */     for (n = nn - 1; n >= 0; n--)
/* 581:    */     {
/* 582:732 */       p = this.d[n];
/* 583:733 */       q = this.e[n];
/* 584:737 */       if (q == 0.0D)
/* 585:    */       {
/* 586:738 */         int l = n;
/* 587:739 */         this.H[n][n] = 1.0D;
/* 588:740 */         for (int i = n - 1; i >= 0; i--)
/* 589:    */         {
/* 590:741 */           double w = this.H[i][i] - p;
/* 591:742 */           r = 0.0D;
/* 592:743 */           for (int j = l; j <= n; j++) {
/* 593:744 */             r += this.H[i][j] * this.H[j][n];
/* 594:    */           }
/* 595:746 */           if (this.e[i] < 0.0D)
/* 596:    */           {
/* 597:747 */             z = w;
/* 598:748 */             s = r;
/* 599:    */           }
/* 600:    */           else
/* 601:    */           {
/* 602:750 */             l = i;
/* 603:751 */             if (this.e[i] == 0.0D)
/* 604:    */             {
/* 605:752 */               if (w != 0.0D) {
/* 606:753 */                 this.H[i][n] = (-r / w);
/* 607:    */               } else {
/* 608:755 */                 this.H[i][n] = (-r / (eps * norm));
/* 609:    */               }
/* 610:    */             }
/* 611:    */             else
/* 612:    */             {
/* 613:761 */               double x = this.H[i][(i + 1)];
/* 614:762 */               double y = this.H[(i + 1)][i];
/* 615:763 */               q = (this.d[i] - p) * (this.d[i] - p) + this.e[i] * this.e[i];
/* 616:764 */               double t = (x * s - z * r) / q;
/* 617:765 */               this.H[i][n] = t;
/* 618:766 */               if (Math.abs(x) > Math.abs(z)) {
/* 619:767 */                 this.H[(i + 1)][n] = ((-r - w * t) / x);
/* 620:    */               } else {
/* 621:769 */                 this.H[(i + 1)][n] = ((-s - y * t) / z);
/* 622:    */               }
/* 623:    */             }
/* 624:775 */             double t = Math.abs(this.H[i][n]);
/* 625:776 */             if (eps * t * t > 1.0D) {
/* 626:777 */               for (int j = i; j <= n; j++) {
/* 627:778 */                 this.H[j][n] /= t;
/* 628:    */               }
/* 629:    */             }
/* 630:    */           }
/* 631:    */         }
/* 632:    */       }
/* 633:786 */       else if (q < 0.0D)
/* 634:    */       {
/* 635:787 */         int l = n - 1;
/* 636:791 */         if (Math.abs(this.H[n][(n - 1)]) > Math.abs(this.H[(n - 1)][n]))
/* 637:    */         {
/* 638:792 */           this.H[(n - 1)][(n - 1)] = (q / this.H[n][(n - 1)]);
/* 639:793 */           this.H[(n - 1)][n] = (-(this.H[n][n] - p) / this.H[n][(n - 1)]);
/* 640:    */         }
/* 641:    */         else
/* 642:    */         {
/* 643:795 */           cdiv(0.0D, -this.H[(n - 1)][n], this.H[(n - 1)][(n - 1)] - p, q);
/* 644:796 */           this.H[(n - 1)][(n - 1)] = this.cdivr;
/* 645:797 */           this.H[(n - 1)][n] = this.cdivi;
/* 646:    */         }
/* 647:799 */         this.H[n][(n - 1)] = 0.0D;
/* 648:800 */         this.H[n][n] = 1.0D;
/* 649:801 */         for (int i = n - 2; i >= 0; i--)
/* 650:    */         {
/* 651:803 */           double ra = 0.0D;
/* 652:804 */           double sa = 0.0D;
/* 653:805 */           for (int j = l; j <= n; j++)
/* 654:    */           {
/* 655:806 */             ra += this.H[i][j] * this.H[j][(n - 1)];
/* 656:807 */             sa += this.H[i][j] * this.H[j][n];
/* 657:    */           }
/* 658:809 */           double w = this.H[i][i] - p;
/* 659:811 */           if (this.e[i] < 0.0D)
/* 660:    */           {
/* 661:812 */             z = w;
/* 662:813 */             r = ra;
/* 663:814 */             s = sa;
/* 664:    */           }
/* 665:    */           else
/* 666:    */           {
/* 667:816 */             l = i;
/* 668:817 */             if (this.e[i] == 0.0D)
/* 669:    */             {
/* 670:818 */               cdiv(-ra, -sa, w, q);
/* 671:819 */               this.H[i][(n - 1)] = this.cdivr;
/* 672:820 */               this.H[i][n] = this.cdivi;
/* 673:    */             }
/* 674:    */             else
/* 675:    */             {
/* 676:825 */               double x = this.H[i][(i + 1)];
/* 677:826 */               double y = this.H[(i + 1)][i];
/* 678:827 */               double vr = (this.d[i] - p) * (this.d[i] - p) + this.e[i] * this.e[i] - q * q;
/* 679:828 */               double vi = (this.d[i] - p) * 2.0D * q;
/* 680:829 */               if (((vr == 0.0D ? 1 : 0) & (vi == 0.0D ? 1 : 0)) != 0) {
/* 681:830 */                 vr = eps * norm * (Math.abs(w) + Math.abs(q) + Math.abs(x) + Math.abs(y) + Math.abs(z));
/* 682:    */               }
/* 683:833 */               cdiv(x * r - z * ra + q * sa, x * s - z * sa - q * ra, vr, vi);
/* 684:834 */               this.H[i][(n - 1)] = this.cdivr;
/* 685:835 */               this.H[i][n] = this.cdivi;
/* 686:836 */               if (Math.abs(x) > Math.abs(z) + Math.abs(q))
/* 687:    */               {
/* 688:837 */                 this.H[(i + 1)][(n - 1)] = ((-ra - w * this.H[i][(n - 1)] + q * this.H[i][n]) / x);
/* 689:838 */                 this.H[(i + 1)][n] = ((-sa - w * this.H[i][n] - q * this.H[i][(n - 1)]) / x);
/* 690:    */               }
/* 691:    */               else
/* 692:    */               {
/* 693:840 */                 cdiv(-r - y * this.H[i][(n - 1)], -s - y * this.H[i][n], z, q);
/* 694:841 */                 this.H[(i + 1)][(n - 1)] = this.cdivr;
/* 695:842 */                 this.H[(i + 1)][n] = this.cdivi;
/* 696:    */               }
/* 697:    */             }
/* 698:848 */             double t = Math.max(Math.abs(this.H[i][(n - 1)]), Math.abs(this.H[i][n]));
/* 699:849 */             if (eps * t * t > 1.0D) {
/* 700:850 */               for (int j = i; j <= n; j++)
/* 701:    */               {
/* 702:851 */                 this.H[j][(n - 1)] /= t;
/* 703:852 */                 this.H[j][n] /= t;
/* 704:    */               }
/* 705:    */             }
/* 706:    */           }
/* 707:    */         }
/* 708:    */       }
/* 709:    */     }
/* 710:862 */     for (int i = 0; i < nn; i++) {
/* 711:863 */       if (((i < low ? 1 : 0) | (i > high ? 1 : 0)) != 0) {
/* 712:864 */         for (int j = i; j < nn; j++) {
/* 713:865 */           this.V[i][j] = this.H[i][j];
/* 714:    */         }
/* 715:    */       }
/* 716:    */     }
/* 717:872 */     for (int j = nn - 1; j >= low; j--) {
/* 718:873 */       for (int i = low; i <= high; i++)
/* 719:    */       {
/* 720:874 */         z = 0.0D;
/* 721:875 */         for (int k = low; k <= Math.min(j, high); k++) {
/* 722:876 */           z += this.V[i][k] * this.H[k][j];
/* 723:    */         }
/* 724:878 */         this.V[i][j] = z;
/* 725:    */       }
/* 726:    */     }
/* 727:    */   }
/* 728:    */   
/* 729:    */   public EigenvalueDecomposition(Matrix Arg)
/* 730:    */   {
/* 731:890 */     double[][] A = Arg.getArray();
/* 732:891 */     this.n = Arg.getColumnDimension();
/* 733:892 */     this.V = new double[this.n][this.n];
/* 734:893 */     this.d = new double[this.n];
/* 735:894 */     this.e = new double[this.n];
/* 736:    */     
/* 737:896 */     this.issymmetric = true;
/* 738:897 */     for (int j = 0; (j < this.n & this.issymmetric); j++) {
/* 739:898 */       for (int i = 0; (i < this.n & this.issymmetric); i++) {
/* 740:899 */         this.issymmetric = (A[i][j] == A[j][i]);
/* 741:    */       }
/* 742:    */     }
/* 743:903 */     if (this.issymmetric)
/* 744:    */     {
/* 745:904 */       for (int i = 0; i < this.n; i++) {
/* 746:905 */         for (int j = 0; j < this.n; j++) {
/* 747:906 */           this.V[i][j] = A[i][j];
/* 748:    */         }
/* 749:    */       }
/* 750:911 */       tred2();
/* 751:    */       
/* 752:    */ 
/* 753:914 */       tql2();
/* 754:    */     }
/* 755:    */     else
/* 756:    */     {
/* 757:917 */       this.H = new double[this.n][this.n];
/* 758:918 */       this.ort = new double[this.n];
/* 759:920 */       for (int j = 0; j < this.n; j++) {
/* 760:921 */         for (int i = 0; i < this.n; i++) {
/* 761:922 */           this.H[i][j] = A[i][j];
/* 762:    */         }
/* 763:    */       }
/* 764:927 */       orthes();
/* 765:    */       
/* 766:    */ 
/* 767:930 */       hqr2();
/* 768:    */     }
/* 769:    */   }
/* 770:    */   
/* 771:    */   public Matrix getV()
/* 772:    */   {
/* 773:939 */     return new Matrix(this.V, this.n, this.n);
/* 774:    */   }
/* 775:    */   
/* 776:    */   public double[] getRealEigenvalues()
/* 777:    */   {
/* 778:947 */     return this.d;
/* 779:    */   }
/* 780:    */   
/* 781:    */   public double[] getImagEigenvalues()
/* 782:    */   {
/* 783:955 */     return this.e;
/* 784:    */   }
/* 785:    */   
/* 786:    */   public Matrix getD()
/* 787:    */   {
/* 788:963 */     Matrix X = new Matrix(this.n, this.n);
/* 789:964 */     double[][] D = X.getArray();
/* 790:965 */     for (int i = 0; i < this.n; i++)
/* 791:    */     {
/* 792:966 */       for (int j = 0; j < this.n; j++) {
/* 793:967 */         D[i][j] = 0.0D;
/* 794:    */       }
/* 795:969 */       D[i][i] = this.d[i];
/* 796:970 */       if (this.e[i] > 0.0D) {
/* 797:971 */         D[i][(i + 1)] = this.e[i];
/* 798:972 */       } else if (this.e[i] < 0.0D) {
/* 799:973 */         D[i][(i - 1)] = this.e[i];
/* 800:    */       }
/* 801:    */     }
/* 802:976 */     return X;
/* 803:    */   }
/* 804:    */   
/* 805:    */   public String getRevision()
/* 806:    */   {
/* 807:985 */     return RevisionUtils.extract("$Revision: 5953 $");
/* 808:    */   }
/* 809:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.matrix.EigenvalueDecomposition
 * JD-Core Version:    0.7.0.1
 */