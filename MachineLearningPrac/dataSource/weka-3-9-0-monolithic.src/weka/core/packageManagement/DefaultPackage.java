/*   1:    */ package weka.core.packageManagement;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.io.Serializable;
/*   6:    */ import java.net.URL;
/*   7:    */ import java.util.ArrayList;
/*   8:    */ import java.util.HashMap;
/*   9:    */ import java.util.Iterator;
/*  10:    */ import java.util.List;
/*  11:    */ import java.util.Map;
/*  12:    */ import java.util.Set;
/*  13:    */ import java.util.StringTokenizer;
/*  14:    */ 
/*  15:    */ public class DefaultPackage
/*  16:    */   extends Package
/*  17:    */   implements Serializable
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = 3643121886457892125L;
/*  20:    */   protected File m_packageHome;
/*  21:    */   protected transient PackageManager m_packageManager;
/*  22:    */   
/*  23:    */   public Object clone()
/*  24:    */   {
/*  25: 69 */     DefaultPackage newP = null;
/*  26: 71 */     if (this.m_packageHome != null) {
/*  27: 72 */       newP = new DefaultPackage(new File(this.m_packageHome.toString()), this.m_packageManager);
/*  28:    */     } else {
/*  29: 75 */       newP = new DefaultPackage(null, this.m_packageManager);
/*  30:    */     }
/*  31: 78 */     HashMap<Object, Object> metaData = new HashMap();
/*  32: 79 */     Set<?> keys = this.m_packageMetaData.keySet();
/*  33: 80 */     Iterator<?> i = keys.iterator();
/*  34: 82 */     while (i.hasNext())
/*  35:    */     {
/*  36: 83 */       Object key = i.next();
/*  37: 84 */       Object value = this.m_packageMetaData.get(key);
/*  38: 85 */       metaData.put(key, value);
/*  39:    */     }
/*  40: 88 */     newP.setPackageMetaData(metaData);
/*  41:    */     
/*  42: 90 */     return newP;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setPackageManager(PackageManager p)
/*  46:    */   {
/*  47: 99 */     this.m_packageManager = p;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public DefaultPackage(File packageHome, PackageManager manager, Map<?, ?> packageMetaData)
/*  51:    */   {
/*  52:111 */     this(packageHome, manager);
/*  53:    */     
/*  54:113 */     setPackageMetaData(packageMetaData);
/*  55:    */   }
/*  56:    */   
/*  57:    */   public DefaultPackage(File packageHome, PackageManager manager)
/*  58:    */   {
/*  59:123 */     this.m_packageHome = packageHome;
/*  60:124 */     this.m_packageManager = manager;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public URL getPackageURL()
/*  64:    */     throws Exception
/*  65:    */   {
/*  66:137 */     String url = getPackageMetaDataElement("PackageURL").toString().trim();
/*  67:    */     
/*  68:139 */     URL packageURL = new URL(url);
/*  69:    */     
/*  70:141 */     return packageURL;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public String getName()
/*  74:    */   {
/*  75:151 */     return getPackageMetaDataElement("PackageName").toString();
/*  76:    */   }
/*  77:    */   
/*  78:    */   protected static String[] splitNameVersion(String nameAndVersion)
/*  79:    */   {
/*  80:155 */     String[] result = new String[3];
/*  81:    */     
/*  82:157 */     nameAndVersion = nameAndVersion.trim();
/*  83:158 */     if (nameAndVersion.indexOf('(') < 0)
/*  84:    */     {
/*  85:159 */       result[0] = nameAndVersion;
/*  86:    */     }
/*  87:160 */     else if (nameAndVersion.indexOf(')') >= 0)
/*  88:    */     {
/*  89:161 */       boolean ok = true;
/*  90:162 */       result[0] = nameAndVersion.substring(0, nameAndVersion.indexOf(40));
/*  91:163 */       result[0] = result[0].trim();
/*  92:    */       
/*  93:165 */       String secondInequality = null;
/*  94:166 */       int delimiterIndex = nameAndVersion.indexOf('|');
/*  95:167 */       if (delimiterIndex >= 0)
/*  96:    */       {
/*  97:168 */         secondInequality = nameAndVersion.substring(delimiterIndex + 1, nameAndVersion.length());
/*  98:    */         
/*  99:170 */         secondInequality = secondInequality.trim();
/* 100:171 */         String[] result2 = new String[5];
/* 101:172 */         result2[0] = result[0];
/* 102:173 */         result = result2;
/* 103:    */       }
/* 104:    */       else
/* 105:    */       {
/* 106:175 */         delimiterIndex = nameAndVersion.length();
/* 107:    */       }
/* 108:178 */       nameAndVersion = nameAndVersion.substring(nameAndVersion.indexOf('(') + 1, delimiterIndex);
/* 109:    */       
/* 110:180 */       nameAndVersion = nameAndVersion.trim();
/* 111:181 */       int pos = 1;
/* 112:182 */       if (nameAndVersion.charAt(0) == '=')
/* 113:    */       {
/* 114:183 */         result[1] = "=";
/* 115:    */       }
/* 116:184 */       else if (nameAndVersion.charAt(1) == '=')
/* 117:    */       {
/* 118:185 */         pos++;
/* 119:186 */         if (nameAndVersion.charAt(0) == '<') {
/* 120:187 */           result[1] = "<=";
/* 121:    */         } else {
/* 122:189 */           result[1] = ">=";
/* 123:    */         }
/* 124:    */       }
/* 125:191 */       else if (nameAndVersion.charAt(0) == '<')
/* 126:    */       {
/* 127:192 */         result[1] = "<";
/* 128:    */       }
/* 129:193 */       else if (nameAndVersion.charAt(0) == '>')
/* 130:    */       {
/* 131:194 */         result[1] = ">";
/* 132:    */       }
/* 133:    */       else
/* 134:    */       {
/* 135:196 */         ok = false;
/* 136:    */       }
/* 137:199 */       if (ok)
/* 138:    */       {
/* 139:200 */         if (secondInequality != null) {
/* 140:201 */           delimiterIndex = nameAndVersion.length();
/* 141:    */         } else {
/* 142:203 */           delimiterIndex = nameAndVersion.indexOf(')');
/* 143:    */         }
/* 144:205 */         nameAndVersion = nameAndVersion.substring(pos, delimiterIndex);
/* 145:206 */         result[2] = nameAndVersion.trim();
/* 146:    */       }
/* 147:210 */       if (secondInequality != null)
/* 148:    */       {
/* 149:211 */         ok = true;
/* 150:212 */         pos = 1;
/* 151:213 */         if (secondInequality.charAt(0) == '=')
/* 152:    */         {
/* 153:214 */           result[3] = "=";
/* 154:    */         }
/* 155:215 */         else if (secondInequality.charAt(1) == '=')
/* 156:    */         {
/* 157:216 */           pos++;
/* 158:217 */           if (secondInequality.charAt(0) == '<') {
/* 159:218 */             result[3] = "<=";
/* 160:    */           } else {
/* 161:220 */             result[3] = ">=";
/* 162:    */           }
/* 163:    */         }
/* 164:222 */         else if (secondInequality.charAt(0) == '<')
/* 165:    */         {
/* 166:223 */           result[3] = "<";
/* 167:    */         }
/* 168:224 */         else if (secondInequality.charAt(0) == '>')
/* 169:    */         {
/* 170:225 */           result[3] = ">";
/* 171:    */         }
/* 172:    */         else
/* 173:    */         {
/* 174:227 */           ok = false;
/* 175:    */         }
/* 176:230 */         if (ok)
/* 177:    */         {
/* 178:231 */           secondInequality = secondInequality.substring(pos, secondInequality.indexOf(')'));
/* 179:    */           
/* 180:233 */           result[4] = secondInequality.trim();
/* 181:    */         }
/* 182:    */       }
/* 183:    */     }
/* 184:238 */     return result;
/* 185:    */   }
/* 186:    */   
/* 187:    */   public List<Dependency> getDependencies()
/* 188:    */     throws Exception
/* 189:    */   {
/* 190:250 */     List<Dependency> dependencies = new ArrayList();
/* 191:251 */     String dependenciesS = getPackageMetaDataElement("Depends").toString();
/* 192:253 */     if (dependenciesS != null)
/* 193:    */     {
/* 194:254 */       StringTokenizer tok = new StringTokenizer(dependenciesS, ",");
/* 195:255 */       while (tok.hasMoreTokens())
/* 196:    */       {
/* 197:256 */         String nextT = tok.nextToken().trim();
/* 198:257 */         String[] split = splitNameVersion(nextT);
/* 199:258 */         Package toAdd = null;
/* 200:261 */         if (!split[0].equalsIgnoreCase(this.m_packageManager.getBaseSystemName()))
/* 201:    */         {
/* 202:264 */           toAdd = this.m_packageManager.getRepositoryPackageInfo(split[0], split[2]);
/* 203:266 */           if (split.length == 3)
/* 204:    */           {
/* 205:267 */             VersionPackageConstraint versionConstraint = new VersionPackageConstraint(toAdd);
/* 206:269 */             if (split[2] == null) {
/* 207:272 */               versionConstraint.setVersionConstraint(VersionPackageConstraint.VersionComparison.LESSTHANOREQUAL);
/* 208:    */             } else {
/* 209:275 */               versionConstraint.setVersionConstraint(split[1]);
/* 210:    */             }
/* 211:278 */             Dependency dep = new Dependency(this, versionConstraint);
/* 212:279 */             dependencies.add(dep);
/* 213:    */           }
/* 214:    */           else
/* 215:    */           {
/* 216:282 */             VersionRangePackageConstraint versionConstraint = new VersionRangePackageConstraint(toAdd);
/* 217:    */             
/* 218:284 */             VersionPackageConstraint.VersionComparison comp1 = VersionPackageConstraint.getVersionComparison(split[1]);
/* 219:    */             
/* 220:286 */             VersionPackageConstraint.VersionComparison comp2 = VersionPackageConstraint.getVersionComparison(split[3]);
/* 221:    */             
/* 222:288 */             versionConstraint.setRangeConstraint(split[2], comp1, split[4], comp2);
/* 223:    */             
/* 224:    */ 
/* 225:291 */             Dependency dep = new Dependency(this, versionConstraint);
/* 226:292 */             dependencies.add(dep);
/* 227:    */           }
/* 228:    */         }
/* 229:    */       }
/* 230:    */     }
/* 231:298 */     return dependencies;
/* 232:    */   }
/* 233:    */   
/* 234:    */   public List<Dependency> getBaseSystemDependency()
/* 235:    */     throws Exception
/* 236:    */   {
/* 237:310 */     String dependenciesS = getPackageMetaDataElement("Depends").toString();
/* 238:311 */     Dependency baseDep = null;
/* 239:312 */     List<Dependency> baseDeps = new ArrayList();
/* 240:314 */     if (dependenciesS != null)
/* 241:    */     {
/* 242:315 */       StringTokenizer tok = new StringTokenizer(dependenciesS, ",");
/* 243:316 */       while (tok.hasMoreTokens())
/* 244:    */       {
/* 245:317 */         String nextT = tok.nextToken().trim();
/* 246:318 */         String[] split = splitNameVersion(nextT);
/* 247:320 */         if (split[0].equalsIgnoreCase(this.m_packageManager.getBaseSystemName()))
/* 248:    */         {
/* 249:323 */           Map<String, String> baseMap = new HashMap();
/* 250:324 */           baseMap.put("PackageName", "weka");
/* 251:    */           
/* 252:    */ 
/* 253:    */ 
/* 254:    */ 
/* 255:329 */           split[2] = (split[2] == null ? "1000.1000.1000" : split[2]);
/* 256:330 */           baseMap.put("Version", split[2]);
/* 257:331 */           Package basePackage = new DefaultPackage(null, this.m_packageManager, baseMap);
/* 258:334 */           if (split.length == 3)
/* 259:    */           {
/* 260:335 */             VersionPackageConstraint baseConstraint = new VersionPackageConstraint(basePackage);
/* 261:    */             
/* 262:337 */             VersionPackageConstraint.VersionComparison baseComp = VersionPackageConstraint.VersionComparison.LESSTHANOREQUAL;
/* 263:338 */             if (split[1] != null) {
/* 264:339 */               baseComp = VersionPackageConstraint.getVersionComparison(split[1]);
/* 265:    */             }
/* 266:342 */             baseConstraint.setVersionConstraint(baseComp);
/* 267:    */             
/* 268:344 */             baseDep = new Dependency(this, baseConstraint);
/* 269:345 */             baseDeps.add(baseDep);
/* 270:    */           }
/* 271:    */           else
/* 272:    */           {
/* 273:348 */             VersionRangePackageConstraint baseConstraint = new VersionRangePackageConstraint(basePackage);
/* 274:    */             
/* 275:    */ 
/* 276:351 */             VersionPackageConstraint.VersionComparison comp1 = VersionPackageConstraint.getVersionComparison(split[1]);
/* 277:    */             
/* 278:353 */             VersionPackageConstraint.VersionComparison comp2 = VersionPackageConstraint.getVersionComparison(split[3]);
/* 279:    */             
/* 280:355 */             baseConstraint.setRangeConstraint(split[2], comp1, split[4], comp2);
/* 281:    */             
/* 282:357 */             baseDep = new Dependency(this, baseConstraint);
/* 283:    */           }
/* 284:    */         }
/* 285:    */       }
/* 286:    */     }
/* 287:363 */     if (baseDeps.size() == 0) {
/* 288:364 */       throw new Exception("[Package] " + getPackageMetaDataElement("PackageName").toString() + " can't determine what version of the base system is required!!");
/* 289:    */     }
/* 290:369 */     return baseDeps;
/* 291:    */   }
/* 292:    */   
/* 293:    */   private boolean findPackage(String packageName, List<Package> packageList)
/* 294:    */   {
/* 295:373 */     boolean found = false;
/* 296:    */     
/* 297:375 */     Iterator<Package> i = packageList.iterator();
/* 298:377 */     while (i.hasNext())
/* 299:    */     {
/* 300:378 */       Package p = (Package)i.next();
/* 301:379 */       String pName = p.getPackageMetaDataElement("PackageName").toString();
/* 302:380 */       if (packageName.equals(pName))
/* 303:    */       {
/* 304:381 */         found = true;
/* 305:382 */         break;
/* 306:    */       }
/* 307:    */     }
/* 308:386 */     return found;
/* 309:    */   }
/* 310:    */   
/* 311:    */   public List<Dependency> getMissingDependencies(List<Package> packages)
/* 312:    */     throws Exception
/* 313:    */   {
/* 314:403 */     List<Dependency> missing = new ArrayList();
/* 315:404 */     String dependencies = getPackageMetaDataElement("Depends").toString();
/* 316:406 */     if (dependencies != null)
/* 317:    */     {
/* 318:407 */       StringTokenizer tok = new StringTokenizer(dependencies, ",");
/* 319:408 */       while (tok.hasMoreTokens())
/* 320:    */       {
/* 321:409 */         String nextT = tok.nextToken().trim();
/* 322:410 */         String[] split = splitNameVersion(nextT);
/* 323:413 */         if (!split[0].equalsIgnoreCase(this.m_packageManager.getBaseSystemName()))
/* 324:    */         {
/* 325:416 */           Package tempDep = this.m_packageManager.getRepositoryPackageInfo(split[0], split[2]);
/* 326:418 */           if (!findPackage(split[0], packages))
/* 327:    */           {
/* 328:419 */             VersionPackageConstraint versionConstraint = new VersionPackageConstraint(tempDep);
/* 329:421 */             if (split[2] == null)
/* 330:    */             {
/* 331:424 */               versionConstraint.setVersionConstraint(VersionPackageConstraint.VersionComparison.LESSTHANOREQUAL);
/* 332:    */               
/* 333:426 */               missing.add(new Dependency(this, versionConstraint));
/* 334:    */             }
/* 335:428 */             else if (split.length == 3)
/* 336:    */             {
/* 337:429 */               versionConstraint.setVersionConstraint(split[1]);
/* 338:430 */               missing.add(new Dependency(this, versionConstraint));
/* 339:    */             }
/* 340:    */             else
/* 341:    */             {
/* 342:432 */               VersionRangePackageConstraint versionRConstraint = new VersionRangePackageConstraint(tempDep);
/* 343:    */               
/* 344:434 */               VersionPackageConstraint.VersionComparison comp1 = VersionPackageConstraint.getVersionComparison(split[1]);
/* 345:    */               
/* 346:436 */               VersionPackageConstraint.VersionComparison comp2 = VersionPackageConstraint.getVersionComparison(split[3]);
/* 347:    */               
/* 348:    */ 
/* 349:439 */               versionRConstraint.setRangeConstraint(split[2], comp1, split[4], comp2);
/* 350:    */               
/* 351:441 */               missing.add(new Dependency(this, versionRConstraint));
/* 352:    */             }
/* 353:    */           }
/* 354:    */         }
/* 355:    */       }
/* 356:    */     }
/* 357:449 */     return missing;
/* 358:    */   }
/* 359:    */   
/* 360:    */   public List<Dependency> getMissingDependencies()
/* 361:    */     throws Exception
/* 362:    */   {
/* 363:460 */     List<Package> installedPackages = this.m_packageManager.getInstalledPackages();
/* 364:461 */     String dependencies = getPackageMetaDataElement("Depends").toString();
/* 365:    */     
/* 366:463 */     return getMissingDependencies(installedPackages);
/* 367:    */   }
/* 368:    */   
/* 369:    */   public List<Dependency> getIncompatibleDependencies(List<Package> packages)
/* 370:    */     throws Exception
/* 371:    */   {
/* 372:503 */     List<Dependency> incompatible = new ArrayList();
/* 373:504 */     String dependencies = getPackageMetaDataElement("Depends").toString();
/* 374:506 */     if (dependencies != null)
/* 375:    */     {
/* 376:507 */       StringTokenizer tok = new StringTokenizer(dependencies, ",");
/* 377:    */       String[] splitD;
/* 378:508 */       while (tok.hasMoreTokens())
/* 379:    */       {
/* 380:509 */         String nextT = tok.nextToken().trim();
/* 381:510 */         splitD = splitNameVersion(nextT);
/* 382:513 */         if ((splitD[1] != null) && (splitD[2] != null)) {
/* 383:514 */           for (Package p : packages)
/* 384:    */           {
/* 385:515 */             String packageNameI = p.getPackageMetaDataElement("PackageName").toString();
/* 386:517 */             if (packageNameI.trim().equalsIgnoreCase(splitD[0].trim()))
/* 387:    */             {
/* 388:519 */               String versionI = p.getPackageMetaDataElement("Version").toString().trim();
/* 389:523 */               if (splitD.length == 3)
/* 390:    */               {
/* 391:524 */                 VersionPackageConstraint.VersionComparison constraint = VersionPackageConstraint.getVersionComparison(splitD[1]);
/* 392:526 */                 if (!VersionPackageConstraint.checkConstraint(versionI, constraint, splitD[2]))
/* 393:    */                 {
/* 394:528 */                   VersionPackageConstraint vpc = new VersionPackageConstraint(p);
/* 395:529 */                   vpc.setVersionConstraint(constraint);
/* 396:530 */                   incompatible.add(new Dependency(this, vpc));
/* 397:    */                 }
/* 398:    */               }
/* 399:    */               else
/* 400:    */               {
/* 401:533 */                 VersionRangePackageConstraint versionRConstraint = new VersionRangePackageConstraint(p);
/* 402:    */                 
/* 403:535 */                 VersionPackageConstraint.VersionComparison comp1 = VersionPackageConstraint.getVersionComparison(splitD[1]);
/* 404:    */                 
/* 405:537 */                 VersionPackageConstraint.VersionComparison comp2 = VersionPackageConstraint.getVersionComparison(splitD[3]);
/* 406:    */                 
/* 407:    */ 
/* 408:540 */                 versionRConstraint.setRangeConstraint(splitD[2], comp1, splitD[4], comp2);
/* 409:    */                 
/* 410:542 */                 incompatible.add(new Dependency(this, versionRConstraint));
/* 411:    */               }
/* 412:    */             }
/* 413:    */           }
/* 414:    */         }
/* 415:    */       }
/* 416:    */     }
/* 417:557 */     return incompatible;
/* 418:    */   }
/* 419:    */   
/* 420:    */   public List<Dependency> getIncompatibleDependencies()
/* 421:    */     throws Exception
/* 422:    */   {
/* 423:569 */     List<Package> installedP = this.m_packageManager.getInstalledPackages();
/* 424:570 */     String dependencies = getPackageMetaDataElement("Depends").toString();
/* 425:    */     
/* 426:572 */     return getIncompatibleDependencies(installedP);
/* 427:    */   }
/* 428:    */   
/* 429:    */   public boolean isCompatibleBaseSystem()
/* 430:    */     throws Exception
/* 431:    */   {
/* 432:624 */     String baseSystemName = this.m_packageManager.getBaseSystemName();
/* 433:625 */     String systemVersion = this.m_packageManager.getBaseSystemVersion().toString();
/* 434:    */     
/* 435:    */ 
/* 436:628 */     String dependencies = getPackageMetaDataElement("Depends").toString();
/* 437:629 */     if (dependencies == null) {
/* 438:630 */       return true;
/* 439:    */     }
/* 440:633 */     boolean ok = true;
/* 441:634 */     StringTokenizer tok = new StringTokenizer(dependencies, ",");
/* 442:635 */     while (tok.hasMoreTokens())
/* 443:    */     {
/* 444:636 */       String nextT = tok.nextToken().trim();
/* 445:637 */       String[] split = splitNameVersion(nextT);
/* 446:638 */       if (split[0].startsWith(baseSystemName.toLowerCase())) {
/* 447:640 */         if (split[1] != null) {
/* 448:641 */           if (split.length == 3)
/* 449:    */           {
/* 450:642 */             VersionPackageConstraint.VersionComparison constraint = VersionPackageConstraint.getVersionComparison(split[1]);
/* 451:644 */             if (!VersionPackageConstraint.checkConstraint(systemVersion, constraint, split[2]))
/* 452:    */             {
/* 453:646 */               ok = false;
/* 454:647 */               break;
/* 455:    */             }
/* 456:    */           }
/* 457:    */           else
/* 458:    */           {
/* 459:651 */             Map<String, String> baseMap = new HashMap();
/* 460:652 */             baseMap.put("PackageName", "weka");
/* 461:    */             
/* 462:654 */             baseMap.put("Version", systemVersion);
/* 463:655 */             Package basePackage = new DefaultPackage(null, this.m_packageManager, baseMap);
/* 464:    */             
/* 465:    */ 
/* 466:658 */             VersionRangePackageConstraint versionRConstraint = new VersionRangePackageConstraint(basePackage);
/* 467:    */             
/* 468:660 */             VersionPackageConstraint.VersionComparison comp1 = VersionPackageConstraint.getVersionComparison(split[1]);
/* 469:    */             
/* 470:662 */             VersionPackageConstraint.VersionComparison comp2 = VersionPackageConstraint.getVersionComparison(split[3]);
/* 471:    */             
/* 472:    */ 
/* 473:665 */             versionRConstraint.setRangeConstraint(split[2], comp1, split[4], comp2);
/* 474:668 */             if (!versionRConstraint.checkConstraint(basePackage))
/* 475:    */             {
/* 476:669 */               ok = false;
/* 477:670 */               break;
/* 478:    */             }
/* 479:    */           }
/* 480:    */         }
/* 481:    */       }
/* 482:    */     }
/* 483:682 */     return ok;
/* 484:    */   }
/* 485:    */   
/* 486:    */   public void install()
/* 487:    */     throws Exception
/* 488:    */   {
/* 489:731 */     URL packageURL = getPackageURL();
/* 490:    */     
/* 491:733 */     this.m_packageManager.installPackageFromURL(packageURL, new PrintStream[0]);
/* 492:    */   }
/* 493:    */   
/* 494:    */   public boolean isInstalled()
/* 495:    */   {
/* 496:743 */     File packageDir = new File(this.m_packageHome.getAbsoluteFile() + File.separator + this.m_packageMetaData.get("PackageName") + File.separator + "Description.props");
/* 497:    */     
/* 498:    */ 
/* 499:746 */     return packageDir.exists();
/* 500:    */   }
/* 501:    */   
/* 502:    */   public static void main(String[] args)
/* 503:    */   {
/* 504:750 */     String installed = args[0];
/* 505:751 */     String toCheckAgainst = args[1];
/* 506:752 */     String[] splitI = splitNameVersion(installed);
/* 507:753 */     String[] splitA = splitNameVersion(toCheckAgainst);
/* 508:    */     try
/* 509:    */     {
/* 510:757 */       if (splitA.length == 3)
/* 511:    */       {
/* 512:759 */         System.out.println("Checking first version number against second constraint");
/* 513:    */         
/* 514:761 */         VersionPackageConstraint.VersionComparison constraint = VersionPackageConstraint.getVersionComparison(splitA[1]);
/* 515:764 */         if (VersionPackageConstraint.checkConstraint(splitI[2], constraint, splitA[2])) {
/* 516:766 */           System.out.println(splitI[2] + " is compatible with " + args[1]);
/* 517:    */         } else {
/* 518:768 */           System.out.println(splitI[2] + " is not compatible with " + args[1]);
/* 519:    */         }
/* 520:771 */         Map<String, String> baseMap = new HashMap();
/* 521:772 */         baseMap.put("PackageName", splitA[0]);
/* 522:773 */         baseMap.put("Version", splitA[2]);
/* 523:774 */         Package packageA = new DefaultPackage(null, null, baseMap);
/* 524:775 */         packageA.setPackageMetaData(baseMap);
/* 525:776 */         VersionPackageConstraint constrA = new VersionPackageConstraint(packageA);
/* 526:    */         
/* 527:778 */         constrA.setVersionConstraint(constraint);
/* 528:780 */         if (splitI.length == 3)
/* 529:    */         {
/* 530:782 */           VersionPackageConstraint.VersionComparison constraintI = VersionPackageConstraint.getVersionComparison(splitI[1]);
/* 531:    */           
/* 532:    */ 
/* 533:785 */           Package packageI = (Package)packageA.clone();
/* 534:786 */           packageI.setPackageMetaDataElement(VersionPackageConstraint.VERSION_KEY, splitI[2]);
/* 535:    */           
/* 536:788 */           VersionPackageConstraint constrI = new VersionPackageConstraint(packageI);
/* 537:    */           
/* 538:790 */           constrI.setVersionConstraint(constraintI);
/* 539:    */           
/* 540:792 */           PackageConstraint pc = null;
/* 541:793 */           if ((pc = constrI.checkConstraint(constrA)) != null) {
/* 542:794 */             System.out.println(constrI + " and " + constrA + " are compatible\n\n" + "compatible constraint " + pc);
/* 543:    */           } else {
/* 544:797 */             System.out.println(constrI + " and " + constrA + " are not compatible");
/* 545:    */           }
/* 546:    */         }
/* 547:    */       }
/* 548:    */       else
/* 549:    */       {
/* 550:805 */         System.out.println("Checking first version number against second constraint");
/* 551:    */         
/* 552:807 */         Map<String, String> baseMap = new HashMap();
/* 553:808 */         baseMap.put("PackageName", splitI[0]);
/* 554:    */         
/* 555:810 */         baseMap.put("Version", splitI[2]);
/* 556:811 */         Package p = new DefaultPackage(null, null, baseMap);
/* 557:    */         
/* 558:813 */         VersionRangePackageConstraint c = new VersionRangePackageConstraint(p);
/* 559:    */         
/* 560:815 */         VersionPackageConstraint.VersionComparison comp1 = VersionPackageConstraint.getVersionComparison(splitA[1]);
/* 561:    */         
/* 562:817 */         VersionPackageConstraint.VersionComparison comp2 = VersionPackageConstraint.getVersionComparison(splitA[3]);
/* 563:    */         
/* 564:819 */         c.setRangeConstraint(splitA[2], comp1, splitA[4], comp2);
/* 565:821 */         if (c.checkConstraint(p)) {
/* 566:822 */           System.out.println(splitI[2] + " is compatible with " + args[1]);
/* 567:    */         } else {
/* 568:824 */           System.out.println(splitI[2] + " is not compatible with " + args[1]);
/* 569:    */         }
/* 570:    */       }
/* 571:    */     }
/* 572:    */     catch (Exception ex)
/* 573:    */     {
/* 574:829 */       ex.printStackTrace();
/* 575:    */     }
/* 576:    */   }
/* 577:    */   
/* 578:    */   public void setPackageMetaDataElement(Object key, Object value)
/* 579:    */     throws Exception
/* 580:    */   {
/* 581:849 */     if (this.m_packageMetaData == null) {
/* 582:850 */       throw new Exception("[DefaultPackage] no meta data map has been set!");
/* 583:    */     }
/* 584:854 */     Map<Object, Object> meta = this.m_packageMetaData;
/* 585:    */     
/* 586:856 */     meta.put(key, value);
/* 587:    */   }
/* 588:    */   
/* 589:    */   public String toString()
/* 590:    */   {
/* 591:861 */     String packageName = getPackageMetaDataElement("PackageName").toString();
/* 592:862 */     String version = getPackageMetaDataElement("Version").toString();
/* 593:863 */     return packageName + " (" + version + ")";
/* 594:    */   }
/* 595:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.packageManagement.DefaultPackage
 * JD-Core Version:    0.7.0.1
 */