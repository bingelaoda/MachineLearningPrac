/*   1:    */ package weka.core.packageManagement;
/*   2:    */ 
/*   3:    */ public class VersionRangePackageConstraint
/*   4:    */   extends PackageConstraint
/*   5:    */ {
/*   6:    */   protected String m_lowerBound;
/*   7:    */   protected VersionPackageConstraint.VersionComparison m_lowerConstraint;
/*   8:    */   protected String m_upperBound;
/*   9:    */   protected VersionPackageConstraint.VersionComparison m_upperConstraint;
/*  10:    */   protected boolean m_boundOr;
/*  11:    */   
/*  12:    */   public VersionRangePackageConstraint(Package p)
/*  13:    */   {
/*  14: 55 */     setPackage(p);
/*  15:    */   }
/*  16:    */   
/*  17:    */   public void setRangeConstraint(String bound1, VersionPackageConstraint.VersionComparison comp1, String bound2, VersionPackageConstraint.VersionComparison comp2)
/*  18:    */     throws Exception
/*  19:    */   {
/*  20: 73 */     if ((comp1 == VersionPackageConstraint.VersionComparison.EQUAL) || (comp2 == VersionPackageConstraint.VersionComparison.EQUAL)) {
/*  21: 75 */       throw new Exception("[VersionRangePackageConstraint] malformed version range constraint (= not allowed)!");
/*  22:    */     }
/*  23: 81 */     if (comp1.compatibleWith(comp2)) {
/*  24: 82 */       throw new Exception("[VersionRangePackageConstraint] malformed version range constraint!");
/*  25:    */     }
/*  26: 87 */     VersionPackageConstraint.VersionComparison boundsComp = VersionPackageConstraint.compare(bound1, bound2);
/*  27: 90 */     if (boundsComp == VersionPackageConstraint.VersionComparison.EQUAL) {
/*  28: 91 */       throw new Exception("[VersionRangePackageConstraint] malformed version range - both bounds are equal!");
/*  29:    */     }
/*  30: 95 */     if ((comp1 == VersionPackageConstraint.VersionComparison.GREATERTHAN) || (comp1 == VersionPackageConstraint.VersionComparison.GREATERTHANOREQUAL))
/*  31:    */     {
/*  32: 98 */       if (boundsComp == VersionPackageConstraint.VersionComparison.GREATERTHAN) {
/*  33: 99 */         this.m_boundOr = true;
/*  34:    */       }
/*  35:    */     }
/*  36:102 */     else if (boundsComp == VersionPackageConstraint.VersionComparison.LESSTHAN) {
/*  37:103 */       this.m_boundOr = true;
/*  38:    */     }
/*  39:108 */     if (boundsComp == VersionPackageConstraint.VersionComparison.LESSTHAN)
/*  40:    */     {
/*  41:109 */       this.m_lowerBound = bound1;
/*  42:110 */       this.m_lowerConstraint = comp1;
/*  43:111 */       this.m_upperBound = bound2;
/*  44:112 */       this.m_upperConstraint = comp2;
/*  45:    */     }
/*  46:    */     else
/*  47:    */     {
/*  48:114 */       this.m_lowerBound = bound2;
/*  49:115 */       this.m_lowerConstraint = comp2;
/*  50:116 */       this.m_upperBound = bound1;
/*  51:117 */       this.m_upperConstraint = comp1;
/*  52:    */     }
/*  53:    */   }
/*  54:    */   
/*  55:    */   public String getLowerBound()
/*  56:    */   {
/*  57:127 */     return this.m_lowerBound;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public String getUpperBound()
/*  61:    */   {
/*  62:136 */     return this.m_upperBound;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public VersionPackageConstraint.VersionComparison getLowerComparison()
/*  66:    */   {
/*  67:145 */     return this.m_lowerConstraint;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public VersionPackageConstraint.VersionComparison getUpperComparison()
/*  71:    */   {
/*  72:154 */     return this.m_upperConstraint;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public boolean isBoundOR()
/*  76:    */   {
/*  77:163 */     return this.m_boundOr;
/*  78:    */   }
/*  79:    */   
/*  80:    */   protected static boolean checkConstraint(String toCheck, VersionPackageConstraint.VersionComparison comp1, String bound1, VersionPackageConstraint.VersionComparison comp2, String bound2, boolean boundOr)
/*  81:    */   {
/*  82:173 */     boolean result1 = VersionPackageConstraint.checkConstraint(toCheck, comp1, bound1);
/*  83:    */     
/*  84:175 */     boolean result2 = VersionPackageConstraint.checkConstraint(toCheck, comp2, bound2);
/*  85:178 */     if (boundOr) {
/*  86:179 */       return (result1) || (result2);
/*  87:    */     }
/*  88:181 */     return (result1) && (result2);
/*  89:    */   }
/*  90:    */   
/*  91:    */   public boolean checkConstraint(Package target)
/*  92:    */     throws Exception
/*  93:    */   {
/*  94:198 */     if ((this.m_lowerConstraint == null) || (this.m_upperConstraint == null)) {
/*  95:199 */       throw new Exception("[VersionRangePackageConstraint] No constraint has been set!");
/*  96:    */     }
/*  97:203 */     String targetVersion = target.getPackageMetaDataElement(VersionPackageConstraint.VERSION_KEY).toString();
/*  98:    */     
/*  99:    */ 
/* 100:    */ 
/* 101:207 */     return checkConstraint(targetVersion, this.m_lowerConstraint, this.m_lowerBound, this.m_upperConstraint, this.m_upperBound, this.m_boundOr);
/* 102:    */   }
/* 103:    */   
/* 104:    */   protected PackageConstraint checkTargetVersionRangePackageConstraint(VersionRangePackageConstraint target)
/* 105:    */     throws Exception
/* 106:    */   {
/* 107:216 */     String targetLowerBound = target.getLowerBound();
/* 108:217 */     String targetUpperBound = target.getUpperBound();
/* 109:    */     
/* 110:219 */     VersionPackageConstraint.VersionComparison targetLowerComp = target.getLowerComparison();
/* 111:    */     
/* 112:221 */     VersionPackageConstraint.VersionComparison targetUpperComp = target.getUpperComparison();
/* 113:224 */     if (!this.m_boundOr)
/* 114:    */     {
/* 115:225 */       if (target.isBoundOR())
/* 116:    */       {
/* 117:228 */         Package p = (Package)target.getPackage().clone();
/* 118:229 */         p.setPackageMetaDataElement(VersionPackageConstraint.VERSION_KEY, target.getLowerBound());
/* 119:    */         
/* 120:231 */         VersionPackageConstraint lowerC = new VersionPackageConstraint(p);
/* 121:232 */         lowerC.setVersionConstraint(target.getLowerComparison());
/* 122:    */         
/* 123:234 */         p = (Package)p.clone();
/* 124:235 */         p.setPackageMetaDataElement(VersionPackageConstraint.VERSION_KEY, target.getUpperBound());
/* 125:    */         
/* 126:237 */         VersionPackageConstraint upperC = new VersionPackageConstraint(p);
/* 127:238 */         upperC.setVersionConstraint(target.getUpperComparison());
/* 128:    */         
/* 129:240 */         PackageConstraint coveringLower = checkTargetVersionPackageConstraint(lowerC);
/* 130:241 */         if (coveringLower != null) {
/* 131:245 */           return coveringLower;
/* 132:    */         }
/* 133:248 */         PackageConstraint coveringUpper = checkTargetVersionPackageConstraint(upperC);
/* 134:249 */         return coveringUpper;
/* 135:    */       }
/* 136:252 */       String resultLowerBound = null;
/* 137:253 */       String resultUpperBound = null;
/* 138:254 */       VersionPackageConstraint.VersionComparison resultLowerComp = null;
/* 139:255 */       VersionPackageConstraint.VersionComparison resultUpperComp = null;
/* 140:    */       
/* 141:257 */       VersionPackageConstraint.VersionComparison lowerComp = VersionPackageConstraint.compare(this.m_lowerBound, targetLowerBound);
/* 142:259 */       if (lowerComp == VersionPackageConstraint.VersionComparison.EQUAL)
/* 143:    */       {
/* 144:260 */         resultLowerBound = this.m_lowerBound;
/* 145:261 */         resultLowerComp = VersionPackageConstraint.VersionComparison.GREATERTHANOREQUAL;
/* 146:263 */         if ((targetLowerComp == VersionPackageConstraint.VersionComparison.GREATERTHAN) || (this.m_lowerConstraint == VersionPackageConstraint.VersionComparison.GREATERTHAN)) {
/* 147:265 */           resultLowerComp = VersionPackageConstraint.VersionComparison.GREATERTHAN;
/* 148:    */         }
/* 149:    */       }
/* 150:267 */       else if (lowerComp == VersionPackageConstraint.VersionComparison.GREATERTHAN)
/* 151:    */       {
/* 152:268 */         resultLowerBound = this.m_lowerBound;
/* 153:269 */         resultLowerComp = this.m_lowerConstraint;
/* 154:    */       }
/* 155:    */       else
/* 156:    */       {
/* 157:271 */         resultLowerBound = targetLowerBound;
/* 158:272 */         resultLowerComp = targetLowerComp;
/* 159:    */       }
/* 160:275 */       VersionPackageConstraint.VersionComparison upperComp = VersionPackageConstraint.compare(this.m_upperBound, targetUpperBound);
/* 161:277 */       if (upperComp == VersionPackageConstraint.VersionComparison.EQUAL)
/* 162:    */       {
/* 163:278 */         resultUpperBound = this.m_upperBound;
/* 164:279 */         resultUpperComp = VersionPackageConstraint.VersionComparison.LESSTHANOREQUAL;
/* 165:281 */         if ((targetUpperComp == VersionPackageConstraint.VersionComparison.LESSTHAN) || (this.m_upperConstraint == VersionPackageConstraint.VersionComparison.LESSTHAN)) {
/* 166:283 */           resultUpperComp = VersionPackageConstraint.VersionComparison.LESSTHAN;
/* 167:    */         }
/* 168:    */       }
/* 169:285 */       else if (upperComp == VersionPackageConstraint.VersionComparison.LESSTHAN)
/* 170:    */       {
/* 171:286 */         resultUpperBound = this.m_upperBound;
/* 172:287 */         resultUpperComp = this.m_upperConstraint;
/* 173:    */       }
/* 174:    */       else
/* 175:    */       {
/* 176:289 */         resultUpperBound = targetUpperBound;
/* 177:290 */         resultUpperComp = targetUpperComp;
/* 178:    */       }
/* 179:295 */       VersionPackageConstraint.VersionComparison disjointCheck = VersionPackageConstraint.compare(resultUpperBound, resultLowerBound);
/* 180:297 */       if ((disjointCheck == VersionPackageConstraint.VersionComparison.LESSTHAN) || (disjointCheck == VersionPackageConstraint.VersionComparison.EQUAL)) {
/* 181:303 */         return null;
/* 182:    */       }
/* 183:307 */       VersionRangePackageConstraint result = new VersionRangePackageConstraint(getPackage());
/* 184:308 */       result.setRangeConstraint(resultLowerBound, resultLowerComp, resultUpperBound, resultUpperComp);
/* 185:    */       
/* 186:    */ 
/* 187:311 */       return result;
/* 188:    */     }
/* 189:315 */     if (!target.isBoundOR())
/* 190:    */     {
/* 191:318 */       Package p = (Package)getPackage().clone();
/* 192:319 */       p.setPackageMetaDataElement(VersionPackageConstraint.VERSION_KEY, this.m_lowerBound);
/* 193:320 */       VersionPackageConstraint lowerC = new VersionPackageConstraint(p);
/* 194:321 */       lowerC.setVersionConstraint(this.m_lowerConstraint);
/* 195:    */       
/* 196:323 */       p = (Package)p.clone();
/* 197:324 */       p.setPackageMetaDataElement(VersionPackageConstraint.VERSION_KEY, this.m_upperBound);
/* 198:    */       
/* 199:326 */       VersionPackageConstraint upperC = new VersionPackageConstraint(p);
/* 200:327 */       upperC.setVersionConstraint(this.m_upperConstraint);
/* 201:    */       
/* 202:329 */       PackageConstraint coveringLower = target.checkTargetVersionPackageConstraint(lowerC);
/* 203:330 */       if (coveringLower != null) {
/* 204:334 */         return coveringLower;
/* 205:    */       }
/* 206:337 */       PackageConstraint coveringUpper = checkTargetVersionPackageConstraint(upperC);
/* 207:338 */       return coveringUpper;
/* 208:    */     }
/* 209:342 */     String resultLowerBound = null;
/* 210:343 */     String resultUpperBound = null;
/* 211:344 */     VersionPackageConstraint.VersionComparison resultLowerComp = null;
/* 212:345 */     VersionPackageConstraint.VersionComparison resultUpperComp = null;
/* 213:    */     
/* 214:347 */     VersionPackageConstraint.VersionComparison lowerComp = VersionPackageConstraint.compare(this.m_lowerBound, targetLowerBound);
/* 215:349 */     if (lowerComp == VersionPackageConstraint.VersionComparison.EQUAL)
/* 216:    */     {
/* 217:350 */       resultLowerBound = this.m_lowerBound;
/* 218:351 */       resultLowerComp = VersionPackageConstraint.VersionComparison.LESSTHANOREQUAL;
/* 219:353 */       if ((targetLowerComp == VersionPackageConstraint.VersionComparison.LESSTHAN) || (this.m_lowerConstraint == VersionPackageConstraint.VersionComparison.LESSTHAN)) {
/* 220:355 */         resultLowerComp = VersionPackageConstraint.VersionComparison.LESSTHAN;
/* 221:    */       }
/* 222:    */     }
/* 223:357 */     else if (lowerComp == VersionPackageConstraint.VersionComparison.LESSTHAN)
/* 224:    */     {
/* 225:358 */       resultLowerBound = this.m_lowerBound;
/* 226:359 */       resultLowerComp = this.m_lowerConstraint;
/* 227:    */     }
/* 228:    */     else
/* 229:    */     {
/* 230:361 */       resultLowerBound = targetLowerBound;
/* 231:362 */       resultLowerComp = targetLowerComp;
/* 232:    */     }
/* 233:365 */     VersionPackageConstraint.VersionComparison upperComp = VersionPackageConstraint.compare(this.m_upperBound, targetUpperBound);
/* 234:367 */     if (upperComp == VersionPackageConstraint.VersionComparison.EQUAL)
/* 235:    */     {
/* 236:368 */       resultUpperBound = this.m_upperBound;
/* 237:369 */       resultUpperComp = VersionPackageConstraint.VersionComparison.GREATERTHANOREQUAL;
/* 238:371 */       if ((targetUpperComp == VersionPackageConstraint.VersionComparison.GREATERTHAN) || (this.m_upperConstraint == VersionPackageConstraint.VersionComparison.GREATERTHAN)) {
/* 239:373 */         resultUpperComp = VersionPackageConstraint.VersionComparison.GREATERTHAN;
/* 240:    */       }
/* 241:    */     }
/* 242:375 */     else if (upperComp == VersionPackageConstraint.VersionComparison.GREATERTHAN)
/* 243:    */     {
/* 244:376 */       resultUpperBound = this.m_upperBound;
/* 245:377 */       resultUpperComp = this.m_upperConstraint;
/* 246:    */     }
/* 247:    */     else
/* 248:    */     {
/* 249:379 */       resultUpperBound = targetUpperBound;
/* 250:380 */       resultUpperComp = targetUpperComp;
/* 251:    */     }
/* 252:383 */     VersionRangePackageConstraint result = new VersionRangePackageConstraint(getPackage());
/* 253:384 */     result.setRangeConstraint(resultLowerBound, resultLowerComp, resultUpperBound, resultUpperComp);
/* 254:    */     
/* 255:    */ 
/* 256:387 */     return result;
/* 257:    */   }
/* 258:    */   
/* 259:    */   protected PackageConstraint checkTargetVersionPackageConstraint(VersionPackageConstraint target)
/* 260:    */     throws Exception
/* 261:    */   {
/* 262:395 */     VersionPackageConstraint.VersionComparison targetComp = target.getVersionComparison();
/* 263:    */     
/* 264:    */ 
/* 265:398 */     String targetVersion = target.getPackage().getPackageMetaDataElement(VersionPackageConstraint.VERSION_KEY).toString();
/* 266:    */     
/* 267:    */ 
/* 268:    */ 
/* 269:402 */     VersionPackageConstraint.VersionComparison lowerComp = VersionPackageConstraint.compare(targetVersion, this.m_lowerBound);
/* 270:    */     
/* 271:404 */     VersionPackageConstraint.VersionComparison upperComp = VersionPackageConstraint.compare(targetVersion, this.m_upperBound);
/* 272:    */     
/* 273:    */ 
/* 274:407 */     boolean lowerCheck = false;
/* 275:408 */     boolean upperCheck = false;
/* 276:409 */     String coveringLowerBound = null;
/* 277:410 */     String coveringUpperBound = null;
/* 278:411 */     VersionPackageConstraint.VersionComparison coveringLowerConstraint = null;
/* 279:412 */     VersionPackageConstraint.VersionComparison coveringUpperConstraint = null;
/* 280:416 */     if (targetComp == VersionPackageConstraint.VersionComparison.EQUAL)
/* 281:    */     {
/* 282:420 */       if (checkConstraint(target.getPackage())) {
/* 283:421 */         return target;
/* 284:    */       }
/* 285:423 */       return null;
/* 286:    */     }
/* 287:426 */     if (this.m_boundOr)
/* 288:    */     {
/* 289:429 */       if (this.m_lowerConstraint == VersionPackageConstraint.VersionComparison.LESSTHAN)
/* 290:    */       {
/* 291:430 */         if ((lowerComp == VersionPackageConstraint.VersionComparison.EQUAL) || (lowerComp == VersionPackageConstraint.VersionComparison.GREATERTHAN))
/* 292:    */         {
/* 293:432 */           if ((targetComp == VersionPackageConstraint.VersionComparison.GREATERTHAN) || (targetComp == VersionPackageConstraint.VersionComparison.GREATERTHANOREQUAL))
/* 294:    */           {
/* 295:435 */             lowerCheck = false;
/* 296:    */           }
/* 297:    */           else
/* 298:    */           {
/* 299:437 */             lowerCheck = true;
/* 300:    */             
/* 301:439 */             coveringLowerBound = this.m_lowerBound;
/* 302:440 */             coveringLowerConstraint = this.m_lowerConstraint;
/* 303:    */           }
/* 304:    */         }
/* 305:442 */         else if (lowerComp == VersionPackageConstraint.VersionComparison.LESSTHAN)
/* 306:    */         {
/* 307:443 */           lowerCheck = true;
/* 308:444 */           coveringLowerBound = targetVersion;
/* 309:445 */           coveringLowerConstraint = targetComp;
/* 310:446 */           if ((targetComp == VersionPackageConstraint.VersionComparison.LESSTHAN) || (targetComp == VersionPackageConstraint.VersionComparison.LESSTHANOREQUAL))
/* 311:    */           {
/* 312:448 */             coveringUpperBound = null;
/* 313:    */           }
/* 314:    */           else
/* 315:    */           {
/* 316:451 */             coveringUpperBound = this.m_lowerBound;
/* 317:452 */             coveringUpperConstraint = this.m_lowerConstraint;
/* 318:    */           }
/* 319:    */         }
/* 320:    */       }
/* 321:457 */       else if (lowerComp == VersionPackageConstraint.VersionComparison.GREATERTHAN)
/* 322:    */       {
/* 323:458 */         if ((targetComp == VersionPackageConstraint.VersionComparison.GREATERTHAN) || (targetComp == VersionPackageConstraint.VersionComparison.GREATERTHANOREQUAL))
/* 324:    */         {
/* 325:461 */           lowerCheck = false;
/* 326:    */         }
/* 327:    */         else
/* 328:    */         {
/* 329:463 */           lowerCheck = true;
/* 330:    */           
/* 331:465 */           coveringLowerBound = this.m_lowerBound;
/* 332:466 */           coveringLowerConstraint = this.m_lowerConstraint;
/* 333:    */         }
/* 334:    */       }
/* 335:468 */       else if (lowerComp == VersionPackageConstraint.VersionComparison.EQUAL)
/* 336:    */       {
/* 337:471 */         if (targetComp == VersionPackageConstraint.VersionComparison.GREATERTHAN)
/* 338:    */         {
/* 339:472 */           lowerCheck = false;
/* 340:    */         }
/* 341:    */         else
/* 342:    */         {
/* 343:475 */           lowerCheck = true;
/* 344:476 */           coveringLowerBound = targetVersion;
/* 345:477 */           coveringLowerConstraint = targetComp;
/* 346:    */           
/* 347:479 */           coveringUpperBound = null;
/* 348:    */         }
/* 349:    */       }
/* 350:    */       else
/* 351:    */       {
/* 352:483 */         lowerCheck = true;
/* 353:484 */         coveringLowerBound = targetVersion;
/* 354:485 */         coveringLowerConstraint = targetComp;
/* 355:486 */         if ((targetComp == VersionPackageConstraint.VersionComparison.LESSTHAN) || (targetComp == VersionPackageConstraint.VersionComparison.LESSTHANOREQUAL))
/* 356:    */         {
/* 357:488 */           coveringUpperBound = null;
/* 358:    */         }
/* 359:    */         else
/* 360:    */         {
/* 361:491 */           coveringUpperBound = this.m_lowerBound;
/* 362:492 */           coveringUpperConstraint = this.m_lowerConstraint;
/* 363:    */         }
/* 364:    */       }
/* 365:499 */       if (!lowerCheck) {
/* 366:502 */         if (this.m_upperConstraint == VersionPackageConstraint.VersionComparison.GREATERTHAN)
/* 367:    */         {
/* 368:503 */           if ((upperComp == VersionPackageConstraint.VersionComparison.EQUAL) || (upperComp == VersionPackageConstraint.VersionComparison.LESSTHAN))
/* 369:    */           {
/* 370:505 */             if ((targetComp == VersionPackageConstraint.VersionComparison.LESSTHAN) || (targetComp == VersionPackageConstraint.VersionComparison.LESSTHANOREQUAL))
/* 371:    */             {
/* 372:508 */               upperCheck = false;
/* 373:    */             }
/* 374:    */             else
/* 375:    */             {
/* 376:510 */               lowerCheck = true;
/* 377:    */               
/* 378:512 */               coveringUpperBound = this.m_upperBound;
/* 379:513 */               coveringUpperConstraint = this.m_upperConstraint;
/* 380:    */             }
/* 381:    */           }
/* 382:515 */           else if (upperComp == VersionPackageConstraint.VersionComparison.GREATERTHAN)
/* 383:    */           {
/* 384:516 */             upperCheck = true;
/* 385:517 */             coveringUpperBound = targetVersion;
/* 386:518 */             coveringUpperConstraint = targetComp;
/* 387:519 */             if ((targetComp == VersionPackageConstraint.VersionComparison.GREATERTHAN) || (targetComp == VersionPackageConstraint.VersionComparison.GREATERTHANOREQUAL))
/* 388:    */             {
/* 389:521 */               coveringLowerBound = null;
/* 390:    */             }
/* 391:    */             else
/* 392:    */             {
/* 393:524 */               coveringLowerBound = this.m_upperBound;
/* 394:525 */               coveringLowerConstraint = this.m_upperConstraint;
/* 395:    */             }
/* 396:    */           }
/* 397:    */         }
/* 398:530 */         else if (upperComp == VersionPackageConstraint.VersionComparison.LESSTHAN)
/* 399:    */         {
/* 400:531 */           if ((targetComp == VersionPackageConstraint.VersionComparison.LESSTHAN) || (targetComp == VersionPackageConstraint.VersionComparison.LESSTHANOREQUAL))
/* 401:    */           {
/* 402:534 */             upperCheck = false;
/* 403:    */           }
/* 404:    */           else
/* 405:    */           {
/* 406:536 */             upperCheck = true;
/* 407:    */             
/* 408:538 */             coveringUpperBound = this.m_upperBound;
/* 409:539 */             coveringUpperConstraint = this.m_upperConstraint;
/* 410:    */           }
/* 411:    */         }
/* 412:541 */         else if (upperComp == VersionPackageConstraint.VersionComparison.EQUAL)
/* 413:    */         {
/* 414:544 */           if (targetComp == VersionPackageConstraint.VersionComparison.LESSTHAN)
/* 415:    */           {
/* 416:545 */             upperCheck = false;
/* 417:    */           }
/* 418:    */           else
/* 419:    */           {
/* 420:548 */             upperCheck = true;
/* 421:549 */             coveringUpperBound = targetVersion;
/* 422:550 */             coveringUpperConstraint = targetComp;
/* 423:    */             
/* 424:552 */             coveringLowerBound = null;
/* 425:    */           }
/* 426:    */         }
/* 427:    */         else
/* 428:    */         {
/* 429:556 */           upperCheck = true;
/* 430:557 */           coveringUpperBound = targetVersion;
/* 431:558 */           coveringUpperConstraint = targetComp;
/* 432:559 */           if ((targetComp == VersionPackageConstraint.VersionComparison.GREATERTHAN) || (targetComp == VersionPackageConstraint.VersionComparison.GREATERTHANOREQUAL))
/* 433:    */           {
/* 434:561 */             coveringLowerBound = null;
/* 435:    */           }
/* 436:    */           else
/* 437:    */           {
/* 438:564 */             coveringLowerBound = this.m_upperBound;
/* 439:565 */             coveringLowerConstraint = this.m_upperConstraint;
/* 440:    */           }
/* 441:    */         }
/* 442:    */       }
/* 443:574 */       if ((!lowerCheck) && (!upperCheck)) {
/* 444:576 */         throw new Exception("[VersionRangePackageConstraint] This shouldn't be possible!!");
/* 445:    */       }
/* 446:579 */       if ((coveringLowerBound != null) && (coveringUpperBound != null))
/* 447:    */       {
/* 448:580 */         VersionRangePackageConstraint result = new VersionRangePackageConstraint(getPackage());
/* 449:581 */         result.setRangeConstraint(coveringLowerBound, coveringLowerConstraint, coveringUpperBound, coveringUpperConstraint);
/* 450:    */         
/* 451:583 */         return result;
/* 452:    */       }
/* 453:586 */       String newVersionNumber = coveringLowerBound != null ? coveringLowerBound : coveringUpperBound;
/* 454:    */       
/* 455:    */ 
/* 456:589 */       VersionPackageConstraint.VersionComparison newConstraint = coveringLowerConstraint != null ? coveringLowerConstraint : coveringUpperConstraint;
/* 457:    */       
/* 458:    */ 
/* 459:    */ 
/* 460:593 */       Package p = (Package)getPackage().clone();
/* 461:    */       
/* 462:595 */       p.setPackageMetaDataElement(VersionPackageConstraint.VERSION_KEY, newVersionNumber);
/* 463:    */       
/* 464:597 */       VersionPackageConstraint result = new VersionPackageConstraint(p);
/* 465:598 */       result.setVersionConstraint(newConstraint);
/* 466:    */       
/* 467:600 */       return result;
/* 468:    */     }
/* 469:604 */     if (lowerComp == VersionPackageConstraint.VersionComparison.LESSTHAN)
/* 470:    */     {
/* 471:605 */       if ((targetComp == VersionPackageConstraint.VersionComparison.LESSTHAN) || (targetComp == VersionPackageConstraint.VersionComparison.LESSTHANOREQUAL) || (targetComp == VersionPackageConstraint.VersionComparison.EQUAL))
/* 472:    */       {
/* 473:609 */         lowerCheck = false;
/* 474:    */       }
/* 475:    */       else
/* 476:    */       {
/* 477:612 */         lowerCheck = true;
/* 478:613 */         coveringLowerBound = this.m_lowerBound;
/* 479:614 */         coveringLowerConstraint = this.m_lowerConstraint;
/* 480:615 */         coveringUpperBound = this.m_upperBound;
/* 481:616 */         coveringUpperConstraint = this.m_upperConstraint;
/* 482:    */       }
/* 483:    */     }
/* 484:618 */     else if (lowerComp == VersionPackageConstraint.VersionComparison.EQUAL)
/* 485:    */     {
/* 486:620 */       if ((targetComp == VersionPackageConstraint.VersionComparison.GREATERTHANOREQUAL) || (targetComp == VersionPackageConstraint.VersionComparison.GREATERTHAN))
/* 487:    */       {
/* 488:622 */         lowerCheck = true;
/* 489:623 */         coveringLowerBound = this.m_lowerBound;
/* 490:624 */         coveringLowerConstraint = (this.m_lowerConstraint == VersionPackageConstraint.VersionComparison.GREATERTHAN) || (targetComp == VersionPackageConstraint.VersionComparison.GREATERTHAN) ? VersionPackageConstraint.VersionComparison.GREATERTHAN : VersionPackageConstraint.VersionComparison.GREATERTHANOREQUAL;
/* 491:    */         
/* 492:    */ 
/* 493:    */ 
/* 494:    */ 
/* 495:629 */         coveringUpperBound = this.m_upperBound;
/* 496:630 */         coveringUpperConstraint = this.m_upperConstraint;
/* 497:    */       }
/* 498:633 */       else if ((targetComp == VersionPackageConstraint.VersionComparison.LESSTHANOREQUAL) && (this.m_lowerConstraint == VersionPackageConstraint.VersionComparison.GREATERTHANOREQUAL))
/* 499:    */       {
/* 500:635 */         VersionPackageConstraint.VersionComparison newComp = VersionPackageConstraint.VersionComparison.EQUAL;
/* 501:    */         
/* 502:637 */         VersionPackageConstraint result = new VersionPackageConstraint(target.getPackage());
/* 503:    */         
/* 504:639 */         result.setVersionConstraint(newComp);
/* 505:    */         
/* 506:641 */         return result;
/* 507:    */       }
/* 508:    */     }
/* 509:644 */     else if (lowerComp == VersionPackageConstraint.VersionComparison.GREATERTHAN) {
/* 510:646 */       if (upperComp == VersionPackageConstraint.VersionComparison.LESSTHAN) {
/* 511:647 */         if ((targetComp == VersionPackageConstraint.VersionComparison.LESSTHAN) || (targetComp == VersionPackageConstraint.VersionComparison.LESSTHANOREQUAL))
/* 512:    */         {
/* 513:649 */           lowerCheck = true;upperCheck = true;
/* 514:650 */           coveringLowerBound = this.m_lowerBound;
/* 515:651 */           coveringLowerConstraint = this.m_lowerConstraint;
/* 516:652 */           coveringUpperBound = targetVersion;
/* 517:653 */           coveringUpperConstraint = targetComp;
/* 518:    */         }
/* 519:    */         else
/* 520:    */         {
/* 521:655 */           coveringLowerBound = targetVersion;
/* 522:656 */           coveringLowerConstraint = targetComp;
/* 523:657 */           coveringUpperBound = this.m_upperBound;
/* 524:658 */           coveringUpperConstraint = this.m_upperConstraint;
/* 525:    */         }
/* 526:    */       }
/* 527:    */     }
/* 528:663 */     if ((coveringLowerBound == null) || (coveringUpperBound == null)) {
/* 529:665 */       if (upperComp == VersionPackageConstraint.VersionComparison.EQUAL)
/* 530:    */       {
/* 531:667 */         if ((targetComp == VersionPackageConstraint.VersionComparison.LESSTHANOREQUAL) || (targetComp == VersionPackageConstraint.VersionComparison.LESSTHAN))
/* 532:    */         {
/* 533:669 */           upperCheck = true;
/* 534:670 */           coveringUpperBound = this.m_upperBound;
/* 535:671 */           coveringUpperConstraint = (this.m_upperConstraint == VersionPackageConstraint.VersionComparison.LESSTHAN) || (targetComp == VersionPackageConstraint.VersionComparison.LESSTHAN) ? VersionPackageConstraint.VersionComparison.LESSTHAN : VersionPackageConstraint.VersionComparison.LESSTHANOREQUAL;
/* 536:    */           
/* 537:    */ 
/* 538:    */ 
/* 539:    */ 
/* 540:676 */           coveringLowerBound = this.m_lowerBound;
/* 541:677 */           coveringLowerConstraint = this.m_lowerConstraint;
/* 542:    */         }
/* 543:680 */         else if ((targetComp == VersionPackageConstraint.VersionComparison.GREATERTHANOREQUAL) && (this.m_upperConstraint == VersionPackageConstraint.VersionComparison.LESSTHANOREQUAL))
/* 544:    */         {
/* 545:682 */           VersionPackageConstraint.VersionComparison newComp = VersionPackageConstraint.VersionComparison.EQUAL;
/* 546:    */           
/* 547:684 */           VersionPackageConstraint result = new VersionPackageConstraint(target.getPackage());
/* 548:    */           
/* 549:686 */           result.setVersionConstraint(newComp);
/* 550:    */           
/* 551:688 */           return result;
/* 552:    */         }
/* 553:    */       }
/* 554:691 */       else if (upperComp == VersionPackageConstraint.VersionComparison.GREATERTHAN) {
/* 555:692 */         if ((targetComp == VersionPackageConstraint.VersionComparison.GREATERTHAN) || (targetComp == VersionPackageConstraint.VersionComparison.GREATERTHANOREQUAL) || (targetComp == VersionPackageConstraint.VersionComparison.EQUAL))
/* 556:    */         {
/* 557:696 */           upperCheck = false;
/* 558:    */         }
/* 559:    */         else
/* 560:    */         {
/* 561:699 */           upperCheck = true;
/* 562:700 */           coveringUpperBound = this.m_upperBound;
/* 563:701 */           coveringUpperConstraint = this.m_upperConstraint;
/* 564:702 */           coveringLowerBound = this.m_lowerBound;
/* 565:703 */           coveringLowerConstraint = this.m_lowerConstraint;
/* 566:    */         }
/* 567:    */       }
/* 568:    */     }
/* 569:708 */     if ((coveringUpperBound == null) && (coveringLowerBound == null)) {
/* 570:710 */       return null;
/* 571:    */     }
/* 572:713 */     if ((coveringUpperBound == null) || (coveringLowerBound == null)) {
/* 573:716 */       throw new Exception("[VersionRangePackageConstraint] This shouldn't be possible!!");
/* 574:    */     }
/* 575:719 */     VersionRangePackageConstraint result = new VersionRangePackageConstraint(getPackage());
/* 576:    */     
/* 577:    */ 
/* 578:722 */     result.setRangeConstraint(coveringLowerBound, coveringLowerConstraint, coveringUpperBound, coveringUpperConstraint);
/* 579:    */     
/* 580:724 */     return result;
/* 581:    */   }
/* 582:    */   
/* 583:    */   public PackageConstraint checkConstraint(PackageConstraint target)
/* 584:    */     throws Exception
/* 585:    */   {
/* 586:742 */     if ((this.m_lowerConstraint == null) || (this.m_upperConstraint == null)) {
/* 587:743 */       throw new Exception("[VersionRangePackageConstraint] No constraint has been set!");
/* 588:    */     }
/* 589:749 */     if ((!(target instanceof VersionRangePackageConstraint)) && (!(target instanceof VersionPackageConstraint))) {
/* 590:751 */       throw new Exception("[VersionRangePackageConstraint] incompatible target constraint!");
/* 591:    */     }
/* 592:757 */     if ((target instanceof VersionPackageConstraint))
/* 593:    */     {
/* 594:758 */       PackageConstraint result = checkTargetVersionPackageConstraint((VersionPackageConstraint)target);
/* 595:    */       
/* 596:760 */       return result;
/* 597:    */     }
/* 598:761 */     if ((target instanceof VersionRangePackageConstraint))
/* 599:    */     {
/* 600:763 */       PackageConstraint result = checkTargetVersionRangePackageConstraint((VersionRangePackageConstraint)target);
/* 601:    */       
/* 602:765 */       return result;
/* 603:    */     }
/* 604:768 */     return null;
/* 605:    */   }
/* 606:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.packageManagement.VersionRangePackageConstraint
 * JD-Core Version:    0.7.0.1
 */