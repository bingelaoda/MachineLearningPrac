/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.BufferedInputStream;
/*   4:    */ import java.io.BufferedReader;
/*   5:    */ import java.io.BufferedWriter;
/*   6:    */ import java.io.File;
/*   7:    */ import java.io.FileInputStream;
/*   8:    */ import java.io.FileOutputStream;
/*   9:    */ import java.io.FileReader;
/*  10:    */ import java.io.FileWriter;
/*  11:    */ import java.io.PrintStream;
/*  12:    */ import java.io.StringReader;
/*  13:    */ import java.util.ArrayList;
/*  14:    */ import java.util.Arrays;
/*  15:    */ import java.util.Comparator;
/*  16:    */ import java.util.Iterator;
/*  17:    */ import java.util.Map;
/*  18:    */ import java.util.Properties;
/*  19:    */ import java.util.Set;
/*  20:    */ import java.util.TreeMap;
/*  21:    */ import java.util.zip.ZipEntry;
/*  22:    */ import java.util.zip.ZipOutputStream;
/*  23:    */ 
/*  24:    */ public class RepositoryIndexGenerator
/*  25:    */ {
/*  26: 60 */   public static String HEADER = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n<html>\n<head>\n<title>Waikato Environment for Knowledge Analysis (WEKA)</title>\n<!-- CSS Stylesheet -->\n<style>body\n{\nbackground: #ededed;\ncolor: #666666;\nfont: 14px Tahoma, Helvetica, sans-serif;;\nmargin: 5px 10px 5px 10px;\npadding: 0px;\n}\n</style>\n\n</head>\n<body bgcolor=\"#ededed\" text=\"#666666\">\n";
/*  27: 67 */   public static String BIRD_IMAGE1 = "<img src=\"Title-Bird-Header.gif\">\n";
/*  28: 68 */   public static String BIRD_IMAGE2 = "<img src=\"../Title-Bird-Header.gif\">\n";
/*  29: 69 */   public static String PENTAHO_IMAGE1 = "<img src=\"pentaho_logo_rgb_sm.png\">\n\n";
/*  30: 71 */   public static String PENTAHO_IMAGE2 = "<img src=\"../pentaho_logo_rgb_sm.png\">\n\n";
/*  31:    */   
/*  32:    */   private static int[] parseVersion(String version)
/*  33:    */   {
/*  34: 75 */     int major = 0;
/*  35: 76 */     int minor = 0;
/*  36: 77 */     int revision = 0;
/*  37: 78 */     int[] majMinRev = new int[3];
/*  38:    */     try
/*  39:    */     {
/*  40: 81 */       String tmpStr = version;
/*  41: 82 */       tmpStr = tmpStr.replace('-', '.');
/*  42: 83 */       if (tmpStr.indexOf(".") > -1)
/*  43:    */       {
/*  44: 84 */         major = Integer.parseInt(tmpStr.substring(0, tmpStr.indexOf(".")));
/*  45: 85 */         tmpStr = tmpStr.substring(tmpStr.indexOf(".") + 1);
/*  46: 86 */         if (tmpStr.indexOf(".") > -1)
/*  47:    */         {
/*  48: 87 */           minor = Integer.parseInt(tmpStr.substring(0, tmpStr.indexOf(".")));
/*  49: 88 */           tmpStr = tmpStr.substring(tmpStr.indexOf(".") + 1);
/*  50: 89 */           if (!tmpStr.equals("")) {
/*  51: 90 */             revision = Integer.parseInt(tmpStr);
/*  52:    */           } else {
/*  53: 92 */             revision = 0;
/*  54:    */           }
/*  55:    */         }
/*  56: 95 */         else if (!tmpStr.equals(""))
/*  57:    */         {
/*  58: 96 */           minor = Integer.parseInt(tmpStr);
/*  59:    */         }
/*  60:    */         else
/*  61:    */         {
/*  62: 98 */           minor = 0;
/*  63:    */         }
/*  64:    */       }
/*  65:102 */       else if (!tmpStr.equals(""))
/*  66:    */       {
/*  67:103 */         major = Integer.parseInt(tmpStr);
/*  68:    */       }
/*  69:    */       else
/*  70:    */       {
/*  71:105 */         major = 0;
/*  72:    */       }
/*  73:    */     }
/*  74:    */     catch (Exception e)
/*  75:    */     {
/*  76:109 */       e.printStackTrace();
/*  77:110 */       major = -1;
/*  78:111 */       minor = -1;
/*  79:112 */       revision = -1;
/*  80:    */     }
/*  81:    */     finally
/*  82:    */     {
/*  83:114 */       majMinRev[0] = major;
/*  84:115 */       majMinRev[1] = minor;
/*  85:116 */       majMinRev[2] = revision;
/*  86:    */     }
/*  87:119 */     return majMinRev;
/*  88:    */   }
/*  89:    */   
/*  90:    */   private static String cleansePropertyValue(String propVal)
/*  91:    */   {
/*  92:123 */     propVal = propVal.replace("<", "&#60;");
/*  93:124 */     propVal = propVal.replace(">", "&#62;");
/*  94:125 */     propVal = propVal.replace("@", "{[at]}");
/*  95:126 */     propVal = propVal.replace("\n", "<br/>");
/*  96:    */     
/*  97:128 */     return propVal;
/*  98:    */   }
/*  99:    */   
/* 100:    */   protected static int compare(String version1, String version2)
/* 101:    */   {
/* 102:134 */     int[] majMinRev1 = parseVersion(version1);
/* 103:135 */     int[] majMinRev2 = parseVersion(version2);
/* 104:    */     int result;
/* 105:    */     int result;
/* 106:139 */     if (majMinRev1[0] < majMinRev2[0])
/* 107:    */     {
/* 108:140 */       result = -1;
/* 109:    */     }
/* 110:    */     else
/* 111:    */     {
/* 112:    */       int result;
/* 113:141 */       if (majMinRev1[0] == majMinRev2[0])
/* 114:    */       {
/* 115:    */         int result;
/* 116:142 */         if (majMinRev1[1] < majMinRev2[1])
/* 117:    */         {
/* 118:143 */           result = -1;
/* 119:    */         }
/* 120:    */         else
/* 121:    */         {
/* 122:    */           int result;
/* 123:144 */           if (majMinRev1[1] == majMinRev2[1])
/* 124:    */           {
/* 125:    */             int result;
/* 126:145 */             if (majMinRev1[2] < majMinRev2[2])
/* 127:    */             {
/* 128:146 */               result = -1;
/* 129:    */             }
/* 130:    */             else
/* 131:    */             {
/* 132:    */               int result;
/* 133:147 */               if (majMinRev1[2] == majMinRev2[2]) {
/* 134:148 */                 result = 0;
/* 135:    */               } else {
/* 136:150 */                 result = 1;
/* 137:    */               }
/* 138:    */             }
/* 139:    */           }
/* 140:    */           else
/* 141:    */           {
/* 142:153 */             result = 1;
/* 143:    */           }
/* 144:    */         }
/* 145:    */       }
/* 146:    */       else
/* 147:    */       {
/* 148:156 */         result = 1;
/* 149:    */       }
/* 150:    */     }
/* 151:159 */     return result;
/* 152:    */   }
/* 153:    */   
/* 154:    */   private static String[] processPackage(File packageDirectory)
/* 155:    */     throws Exception
/* 156:    */   {
/* 157:164 */     System.err.println("Processing " + packageDirectory);
/* 158:165 */     File[] contents = packageDirectory.listFiles();
/* 159:166 */     File latest = null;
/* 160:167 */     ArrayList<File> propsFiles = new ArrayList();
/* 161:168 */     StringBuffer versionsTextBuffer = new StringBuffer();
/* 162:170 */     for (File content : contents) {
/* 163:171 */       if ((content.isFile()) && (content.getName().endsWith(".props")))
/* 164:    */       {
/* 165:172 */         propsFiles.add(content);
/* 166:173 */         if (content.getName().equals("Latest.props")) {
/* 167:174 */           latest = content;
/* 168:    */         }
/* 169:    */       }
/* 170:    */     }
/* 171:183 */     File[] sortedPropsFiles = (File[])propsFiles.toArray(new File[0]);
/* 172:184 */     Arrays.sort(sortedPropsFiles, new Comparator()
/* 173:    */     {
/* 174:    */       public int compare(File first, File second)
/* 175:    */       {
/* 176:187 */         String firstV = first.getName().substring(0, first.getName().indexOf(".props"));
/* 177:    */         
/* 178:189 */         String secondV = second.getName().substring(0, second.getName().indexOf(".props"));
/* 179:191 */         if (firstV.equalsIgnoreCase("Latest")) {
/* 180:192 */           return -1;
/* 181:    */         }
/* 182:193 */         if (secondV.equalsIgnoreCase("Latest")) {
/* 183:194 */           return 1;
/* 184:    */         }
/* 185:196 */         return -RepositoryIndexGenerator.compare(firstV, secondV);
/* 186:    */       }
/* 187:199 */     });
/* 188:200 */     StringBuffer indexBuff = new StringBuffer();
/* 189:201 */     indexBuff.append(HEADER + "\n\n");
/* 190:    */     
/* 191:    */ 
/* 192:204 */     Properties latestProps = new Properties();
/* 193:205 */     latestProps.load(new BufferedReader(new FileReader(latest)));
/* 194:206 */     String packageName = latestProps.getProperty("PackageName") + ": ";
/* 195:207 */     String packageTitle = latestProps.getProperty("Title");
/* 196:208 */     String packageCategory = latestProps.getProperty("Category");
/* 197:209 */     String latestVersion = latestProps.getProperty("Version");
/* 198:210 */     if (packageCategory == null) {
/* 199:211 */       packageCategory = "";
/* 200:    */     }
/* 201:213 */     indexBuff.append("<h2>" + packageName + packageTitle + "</h2>\n\n");
/* 202:    */     
/* 203:215 */     String author = latestProps.getProperty("Author");
/* 204:216 */     author = cleansePropertyValue(author);
/* 205:217 */     String URL = latestProps.getProperty("URL");
/* 206:218 */     if (URL != null) {
/* 207:219 */       URL = cleansePropertyValue(URL);
/* 208:    */     }
/* 209:221 */     String maintainer = latestProps.getProperty("Maintainer");
/* 210:222 */     maintainer = cleansePropertyValue(maintainer);
/* 211:    */     
/* 212:224 */     indexBuff.append("\n<table>\n");
/* 213:225 */     if ((URL != null) && (URL.length() > 0))
/* 214:    */     {
/* 215:226 */       indexBuff.append("<tr><td valign=top>URL:</td><td width=50></td>");
/* 216:    */       
/* 217:228 */       URL = "<a href=\"" + URL + "\">" + URL + "</a>";
/* 218:229 */       indexBuff.append("<td>" + URL + "</td></tr>\n");
/* 219:    */     }
/* 220:231 */     indexBuff.append("<tr><td valign=top>Author:</td><td width=50></td>");
/* 221:    */     
/* 222:233 */     indexBuff.append("<td>" + author + "</td></tr>\n");
/* 223:234 */     indexBuff.append("<tr><td valign=top>Maintainer:</td><td width=50></td>");
/* 224:    */     
/* 225:236 */     indexBuff.append("<td>" + maintainer + "</td></tr>\n");
/* 226:237 */     indexBuff.append("</table>\n<p>\n");
/* 227:    */     
/* 228:239 */     String description = latestProps.getProperty("Description");
/* 229:240 */     indexBuff.append("<p>" + description.replace("\n", "<br/>") + "</p>\n\n");
/* 230:    */     
/* 231:242 */     indexBuff.append("<p>All available versions:<br>\n");
/* 232:243 */     for (int i = 0; i < sortedPropsFiles.length; i++)
/* 233:    */     {
/* 234:244 */       if (i > 0)
/* 235:    */       {
/* 236:245 */         String versionNumber = sortedPropsFiles[i].getName().substring(0, sortedPropsFiles[i].getName().indexOf(".props"));
/* 237:    */         
/* 238:    */ 
/* 239:248 */         versionsTextBuffer.append(versionNumber + "\n");
/* 240:249 */         System.err.println(versionNumber);
/* 241:    */       }
/* 242:251 */       String name = sortedPropsFiles[i].getName();
/* 243:252 */       name = name.substring(0, name.indexOf(".props"));
/* 244:253 */       indexBuff.append("<a href=\"" + name + ".html" + "\">" + name + "</a><br>\n");
/* 245:    */       
/* 246:    */ 
/* 247:256 */       StringBuffer version = new StringBuffer();
/* 248:257 */       version.append(HEADER + "\n\n");
/* 249:    */       
/* 250:    */ 
/* 251:260 */       version.append("<table summary=\"Package " + packageName + " summary\">\n");
/* 252:    */       
/* 253:262 */       Properties versionProps = new Properties();
/* 254:263 */       versionProps.load(new BufferedReader(new FileReader(sortedPropsFiles[i])));
/* 255:    */       
/* 256:    */ 
/* 257:266 */       Set<Object> keys = versionProps.keySet();
/* 258:267 */       String[] sortedKeys = (String[])keys.toArray(new String[0]);
/* 259:268 */       Arrays.sort(sortedKeys);
/* 260:271 */       for (String key : sortedKeys) {
/* 261:273 */         if ((!key.equalsIgnoreCase("PackageName")) && (!key.equalsIgnoreCase("Title")) && (!key.equalsIgnoreCase("DoNotLoadIfFileNotPresentMessage")) && (!key.equalsIgnoreCase("DoNotLoadIfClassNotPresentMessage")) && (!key.equalsIgnoreCase("DoNotLoadIfEnvVarNotSetMessage")))
/* 262:    */         {
/* 263:281 */           version.append("<tr><td valign=top>" + key + ":</td><td width=50></td>");
/* 264:    */           
/* 265:283 */           String propValue = versionProps.getProperty(key);
/* 266:285 */           if (!key.equalsIgnoreCase("Description"))
/* 267:    */           {
/* 268:286 */             propValue = propValue.replace("<", "&#60;");
/* 269:287 */             propValue = propValue.replace(">", "&#62;");
/* 270:288 */             propValue = propValue.replace("@", "{[at]}");
/* 271:289 */             propValue = propValue.replace("\n", "<br/>");
/* 272:    */           }
/* 273:297 */           if ((key.equals("PackageURL")) || (key.equals("URL"))) {
/* 274:298 */             propValue = "<a href=\"" + propValue + "\">" + propValue + "</a>";
/* 275:    */           }
/* 276:301 */           version.append("<td>" + propValue + "</td></tr>\n");
/* 277:    */         }
/* 278:    */       }
/* 279:305 */       version.append("</table>\n</body>\n</html>\n");
/* 280:306 */       String versionHTMLFileName = packageDirectory.getAbsolutePath() + File.separator + name + ".html";
/* 281:    */       
/* 282:308 */       BufferedWriter br = new BufferedWriter(new FileWriter(versionHTMLFileName));
/* 283:    */       
/* 284:310 */       br.write(version.toString());
/* 285:311 */       br.flush();
/* 286:312 */       br.close();
/* 287:    */     }
/* 288:315 */     indexBuff.append("</body>\n</html>\n");
/* 289:316 */     String packageIndexName = packageDirectory.getAbsolutePath() + File.separator + "index.html";
/* 290:    */     
/* 291:318 */     BufferedWriter br = new BufferedWriter(new FileWriter(packageIndexName));
/* 292:319 */     br.write(indexBuff.toString());
/* 293:320 */     br.flush();
/* 294:321 */     br.close();
/* 295:    */     
/* 296:    */ 
/* 297:324 */     String packageVersionsName = packageDirectory.getAbsolutePath() + File.separator + "versions.txt";
/* 298:    */     
/* 299:326 */     br = new BufferedWriter(new FileWriter(packageVersionsName));
/* 300:327 */     br.write(versionsTextBuffer.toString());
/* 301:328 */     br.flush();
/* 302:329 */     br.close();
/* 303:    */     
/* 304:    */ 
/* 305:332 */     String[] returnInfo = new String[3];
/* 306:333 */     returnInfo[0] = packageTitle;
/* 307:334 */     returnInfo[1] = packageCategory;
/* 308:335 */     returnInfo[2] = latestVersion;
/* 309:336 */     return returnInfo;
/* 310:    */   }
/* 311:    */   
/* 312:    */   private static void writeMainIndex(Map<String, String[]> packages, File repositoryHome)
/* 313:    */     throws Exception
/* 314:    */   {
/* 315:341 */     StringBuffer indexBuf = new StringBuffer();
/* 316:342 */     StringBuffer packageList = new StringBuffer();
/* 317:    */     
/* 318:    */ 
/* 319:    */ 
/* 320:346 */     StringBuffer packageListPlusVersion = new StringBuffer();
/* 321:    */     
/* 322:348 */     indexBuf.append(HEADER + BIRD_IMAGE1);
/* 323:349 */     indexBuf.append(PENTAHO_IMAGE1);
/* 324:350 */     indexBuf.append("<h1>WEKA Packages </h1>\n\n");
/* 325:351 */     indexBuf.append("<p><b>IMPORTANT: make sure there are no old versions of Weka (<3.7.2) in your CLASSPATH before starting Weka</b>\n\n");
/* 326:    */     
/* 327:    */ 
/* 328:    */ 
/* 329:    */ 
/* 330:    */ 
/* 331:    */ 
/* 332:358 */     indexBuf.append("<h3>Installation of Packages</h3>\n");
/* 333:359 */     indexBuf.append("A GUI package manager is available from the \"Tools\" menu of the GUIChooser<br><br><code>java -jar weka.jar</code><p>\n\n");
/* 334:    */     
/* 335:    */ 
/* 336:    */ 
/* 337:363 */     indexBuf.append("For a command line package manager type:<br><br<code>java weka.core.WekaPackageManager -h</code><br><br>\n");
/* 338:    */     
/* 339:    */ 
/* 340:366 */     indexBuf.append("<hr/>\n");
/* 341:    */     
/* 342:368 */     indexBuf.append("<h3>Running packaged algorithms from the command line</h3><code>java weka.Run [algorithm name]</code><p>Substring matching is also supported. E.g. try:<br><br><code>java weka.Run Bayes</code><hr/>");
/* 343:    */     
/* 344:    */ 
/* 345:    */ 
/* 346:    */ 
/* 347:    */ 
/* 348:374 */     Set<String> names = packages.keySet();
/* 349:375 */     indexBuf.append("<h3>Available Packages (" + packages.keySet().size() + ")</h3>\n\n");
/* 350:    */     
/* 351:    */ 
/* 352:378 */     indexBuf.append("<table>\n");
/* 353:379 */     Iterator<String> i = names.iterator();
/* 354:380 */     while (i.hasNext())
/* 355:    */     {
/* 356:381 */       String packageName = (String)i.next();
/* 357:382 */       String[] info = (String[])packages.get(packageName);
/* 358:383 */       String packageTitle = info[0];
/* 359:384 */       String packageCategory = info[1];
/* 360:385 */       String latestVersion = info[2];
/* 361:386 */       String href = "<a href=\"./" + packageName + "/index.html\">" + packageName + "</a>";
/* 362:    */       
/* 363:    */ 
/* 364:389 */       indexBuf.append("<tr valign=\"top\">\n");
/* 365:390 */       indexBuf.append("<td>" + href + "</td><td width=50></td><td>" + packageCategory + "</td><td width=50></td><td>" + packageTitle + "</td></tr>\n");
/* 366:    */       
/* 367:    */ 
/* 368:    */ 
/* 369:    */ 
/* 370:395 */       packageList.append(packageName + "\n");
/* 371:396 */       packageListPlusVersion.append(packageName).append(":").append(latestVersion).append("\n");
/* 372:    */     }
/* 373:400 */     indexBuf.append("</table>\n<hr/>\n</body></html>\n");
/* 374:401 */     String indexName = repositoryHome.getAbsolutePath() + File.separator + "index.html";
/* 375:    */     
/* 376:403 */     BufferedWriter br = new BufferedWriter(new FileWriter(indexName));
/* 377:404 */     br.write(indexBuf.toString());
/* 378:405 */     br.flush();
/* 379:406 */     br.close();
/* 380:    */     
/* 381:408 */     String packageListName = repositoryHome.getAbsolutePath() + File.separator + "packageList.txt";
/* 382:    */     
/* 383:410 */     br = new BufferedWriter(new FileWriter(packageListName));
/* 384:411 */     br.write(packageList.toString());
/* 385:412 */     br.flush();
/* 386:413 */     br.close();
/* 387:    */     
/* 388:415 */     packageListName = repositoryHome.getAbsolutePath() + File.separator + "packageListWithVersion.txt";
/* 389:    */     
/* 390:    */ 
/* 391:418 */     br = new BufferedWriter(new FileWriter(packageListName));
/* 392:419 */     br.write(packageListPlusVersion.toString());
/* 393:420 */     br.flush();
/* 394:421 */     br.close();
/* 395:    */     
/* 396:423 */     String numPackagesName = repositoryHome.getAbsolutePath() + File.separator + "numPackages.txt";
/* 397:    */     
/* 398:425 */     br = new BufferedWriter(new FileWriter(numPackagesName));
/* 399:426 */     br.write(packages.keySet().size() + "\n");
/* 400:427 */     br.flush();
/* 401:428 */     br.close();
/* 402:    */     
/* 403:    */ 
/* 404:431 */     writeRepoZipFile(repositoryHome, packageList);
/* 405:    */   }
/* 406:    */   
/* 407:    */   private static void transBytes(BufferedInputStream bi, ZipOutputStream z)
/* 408:    */     throws Exception
/* 409:    */   {
/* 410:    */     int b;
/* 411:437 */     while ((b = bi.read()) != -1) {
/* 412:438 */       z.write(b);
/* 413:    */     }
/* 414:    */   }
/* 415:    */   
/* 416:    */   protected static void writeZipEntryForPackage(File repositoryHome, String packageName, ZipOutputStream zos)
/* 417:    */     throws Exception
/* 418:    */   {
/* 419:445 */     ZipEntry packageDir = new ZipEntry(packageName + "/");
/* 420:446 */     zos.putNextEntry(packageDir);
/* 421:    */     
/* 422:448 */     ZipEntry z = new ZipEntry(packageName + "/Latest.props");
/* 423:449 */     ZipEntry z2 = new ZipEntry(packageName + "/Latest.html");
/* 424:    */     
/* 425:451 */     FileInputStream fi = new FileInputStream(repositoryHome.getAbsolutePath() + File.separator + packageName + File.separator + "Latest.props");
/* 426:    */     
/* 427:    */ 
/* 428:454 */     BufferedInputStream bi = new BufferedInputStream(fi);
/* 429:455 */     zos.putNextEntry(z);
/* 430:456 */     transBytes(bi, zos);
/* 431:457 */     bi.close();
/* 432:    */     
/* 433:459 */     fi = new FileInputStream(repositoryHome.getAbsolutePath() + File.separator + packageName + File.separator + "Latest.html");
/* 434:    */     
/* 435:    */ 
/* 436:462 */     bi = new BufferedInputStream(fi);
/* 437:463 */     zos.putNextEntry(z2);
/* 438:464 */     transBytes(bi, zos);
/* 439:465 */     bi.close();
/* 440:    */     
/* 441:    */ 
/* 442:468 */     z = new ZipEntry(packageName + "/versions.txt");
/* 443:469 */     fi = new FileInputStream(packageName + File.separator + "versions.txt");
/* 444:470 */     bi = new BufferedInputStream(fi);
/* 445:471 */     zos.putNextEntry(z);
/* 446:472 */     transBytes(bi, zos);
/* 447:473 */     bi.close();
/* 448:    */     
/* 449:    */ 
/* 450:476 */     z = new ZipEntry(packageName + "/index.html");
/* 451:477 */     fi = new FileInputStream(repositoryHome.getAbsolutePath() + File.separator + packageName + File.separator + "index.html");
/* 452:    */     
/* 453:    */ 
/* 454:480 */     bi = new BufferedInputStream(fi);
/* 455:481 */     zos.putNextEntry(z);
/* 456:482 */     transBytes(bi, zos);
/* 457:483 */     bi.close();
/* 458:    */     
/* 459:    */ 
/* 460:486 */     FileReader vi = new FileReader(repositoryHome.getAbsolutePath() + File.separator + packageName + File.separator + "versions.txt");
/* 461:    */     
/* 462:    */ 
/* 463:489 */     BufferedReader bvi = new BufferedReader(vi);
/* 464:    */     String version;
/* 465:491 */     while ((version = bvi.readLine()) != null)
/* 466:    */     {
/* 467:492 */       z = new ZipEntry(packageName + "/" + version + ".props");
/* 468:493 */       fi = new FileInputStream(repositoryHome.getAbsolutePath() + File.separator + packageName + File.separator + version + ".props");
/* 469:    */       
/* 470:    */ 
/* 471:496 */       bi = new BufferedInputStream(fi);
/* 472:497 */       zos.putNextEntry(z);
/* 473:498 */       transBytes(bi, zos);
/* 474:499 */       bi.close();
/* 475:    */       
/* 476:501 */       z = new ZipEntry(packageName + "/" + version + ".html");
/* 477:502 */       fi = new FileInputStream(repositoryHome.getAbsolutePath() + File.separator + packageName + File.separator + version + ".html");
/* 478:    */       
/* 479:    */ 
/* 480:505 */       bi = new BufferedInputStream(fi);
/* 481:506 */       zos.putNextEntry(z);
/* 482:507 */       transBytes(bi, zos);
/* 483:508 */       bi.close();
/* 484:    */     }
/* 485:510 */     bvi.close();
/* 486:    */   }
/* 487:    */   
/* 488:    */   protected static void writeRepoZipFile(File repositoryHome, StringBuffer packagesList)
/* 489:    */   {
/* 490:516 */     System.out.print("Writing repo archive");
/* 491:517 */     StringReader r = new StringReader(packagesList.toString());
/* 492:518 */     BufferedReader br = new BufferedReader(r);
/* 493:    */     try
/* 494:    */     {
/* 495:522 */       FileOutputStream fo = new FileOutputStream(repositoryHome.getAbsolutePath() + File.separator + "repo.zip");
/* 496:    */       
/* 497:    */ 
/* 498:525 */       ZipOutputStream zos = new ZipOutputStream(fo);
/* 499:    */       String packageName;
/* 500:527 */       while ((packageName = br.readLine()) != null)
/* 501:    */       {
/* 502:528 */         writeZipEntryForPackage(repositoryHome, packageName, zos);
/* 503:529 */         System.out.print(".");
/* 504:    */       }
/* 505:531 */       System.out.println();
/* 506:    */       
/* 507:    */ 
/* 508:534 */       ZipEntry z = new ZipEntry("packageList.txt");
/* 509:535 */       FileInputStream fi = new FileInputStream(repositoryHome.getAbsolutePath() + File.separator + "packageList.txt");
/* 510:    */       
/* 511:    */ 
/* 512:538 */       BufferedInputStream bi = new BufferedInputStream(fi);
/* 513:539 */       zos.putNextEntry(z);
/* 514:540 */       transBytes(bi, zos);
/* 515:541 */       bi.close();
/* 516:    */       
/* 517:    */ 
/* 518:544 */       z = new ZipEntry("packageListWithVersion.txt");
/* 519:545 */       fi = new FileInputStream(repositoryHome.getAbsolutePath() + File.separator + "packageListWithVersion.txt");
/* 520:    */       
/* 521:    */ 
/* 522:548 */       bi = new BufferedInputStream(fi);
/* 523:549 */       zos.putNextEntry(z);
/* 524:550 */       transBytes(bi, zos);
/* 525:551 */       bi.close();
/* 526:    */       
/* 527:    */ 
/* 528:554 */       File forced = new File(repositoryHome.getAbsolutePath() + File.separator + "forcedRefreshCount.txt");
/* 529:557 */       if (forced.exists())
/* 530:    */       {
/* 531:558 */         z = new ZipEntry("forcedRefreshCount.txt");
/* 532:559 */         fi = new FileInputStream(forced);
/* 533:560 */         bi = new BufferedInputStream(fi);
/* 534:561 */         zos.putNextEntry(z);
/* 535:562 */         transBytes(bi, zos);
/* 536:563 */         bi.close();
/* 537:    */       }
/* 538:567 */       FileReader fr = new FileReader(repositoryHome.getAbsolutePath() + File.separator + "images.txt");
/* 539:    */       
/* 540:    */ 
/* 541:570 */       br = new BufferedReader(fr);
/* 542:    */       String imageName;
/* 543:572 */       while ((imageName = br.readLine()) != null)
/* 544:    */       {
/* 545:573 */         z = new ZipEntry(imageName);
/* 546:574 */         fi = new FileInputStream(repositoryHome.getAbsolutePath() + File.separator + imageName);
/* 547:    */         
/* 548:    */ 
/* 549:577 */         bi = new BufferedInputStream(fi);
/* 550:578 */         zos.putNextEntry(z);
/* 551:579 */         transBytes(bi, zos);
/* 552:580 */         bi.close();
/* 553:    */       }
/* 554:584 */       z = new ZipEntry("images.txt");
/* 555:585 */       fi = new FileInputStream(repositoryHome.getAbsolutePath() + File.separator + "images.txt");
/* 556:    */       
/* 557:    */ 
/* 558:588 */       bi = new BufferedInputStream(fi);
/* 559:589 */       zos.putNextEntry(z);
/* 560:590 */       transBytes(bi, zos);
/* 561:591 */       bi.close();
/* 562:    */       
/* 563:593 */       zos.close();
/* 564:    */       
/* 565:595 */       File f = new File(repositoryHome.getAbsolutePath() + File.separator + "repo.zip");
/* 566:    */       
/* 567:597 */       long size = f.length();
/* 568:    */       
/* 569:    */ 
/* 570:600 */       FileWriter fw = new FileWriter(repositoryHome.getAbsolutePath() + File.separator + "repoSize.txt");
/* 571:603 */       if (size > 1024L) {
/* 572:604 */         size /= 1024L;
/* 573:    */       }
/* 574:606 */       fw.write("" + size);
/* 575:607 */       fw.flush();
/* 576:608 */       fw.close();
/* 577:    */     }
/* 578:    */     catch (Exception ex)
/* 579:    */     {
/* 580:610 */       ex.printStackTrace();
/* 581:    */     }
/* 582:    */   }
/* 583:    */   
/* 584:    */   public static void main(String[] args)
/* 585:    */   {
/* 586:    */     try
/* 587:    */     {
/* 588:622 */       if (args.length < 1)
/* 589:    */       {
/* 590:623 */         System.err.println("Usage:\n\n\tRepositoryIndexGenerator <path to repository>");
/* 591:    */         
/* 592:625 */         System.exit(1);
/* 593:    */       }
/* 594:628 */       File repositoryHome = new File(args[0]);
/* 595:629 */       TreeMap<String, String[]> packages = new TreeMap();
/* 596:    */       
/* 597:    */ 
/* 598:632 */       File[] contents = repositoryHome.listFiles();
/* 599:634 */       for (File content : contents) {
/* 600:635 */         if (content.isDirectory())
/* 601:    */         {
/* 602:639 */           String[] packageInfo = processPackage(content);
/* 603:640 */           packages.put(content.getName(), packageInfo);
/* 604:    */         }
/* 605:    */       }
/* 606:645 */       writeMainIndex(packages, repositoryHome);
/* 607:    */     }
/* 608:    */     catch (Exception ex)
/* 609:    */     {
/* 610:647 */       ex.printStackTrace();
/* 611:    */     }
/* 612:    */   }
/* 613:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.RepositoryIndexGenerator
 * JD-Core Version:    0.7.0.1
 */