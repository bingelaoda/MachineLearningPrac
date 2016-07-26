/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.sql.PreparedStatement;
/*   6:    */ import java.sql.ResultSet;
/*   7:    */ import java.sql.ResultSetMetaData;
/*   8:    */ import java.sql.Time;
/*   9:    */ import java.sql.Timestamp;
/*  10:    */ import java.util.ArrayList;
/*  11:    */ import java.util.Date;
/*  12:    */ import java.util.Enumeration;
/*  13:    */ import java.util.Hashtable;
/*  14:    */ import java.util.Vector;
/*  15:    */ import weka.core.Attribute;
/*  16:    */ import weka.core.DenseInstance;
/*  17:    */ import weka.core.Instance;
/*  18:    */ import weka.core.Instances;
/*  19:    */ import weka.core.Option;
/*  20:    */ import weka.core.OptionHandler;
/*  21:    */ import weka.core.RevisionUtils;
/*  22:    */ import weka.core.SparseInstance;
/*  23:    */ import weka.core.Utils;
/*  24:    */ 
/*  25:    */ public class InstanceQuery
/*  26:    */   extends DatabaseUtils
/*  27:    */   implements OptionHandler, InstanceQueryAdapter
/*  28:    */ {
/*  29:    */   static final long serialVersionUID = 718158370917782584L;
/*  30:102 */   protected boolean m_CreateSparseData = false;
/*  31:105 */   protected String m_Query = "SELECT * from ?";
/*  32:108 */   protected File m_CustomPropsFile = null;
/*  33:    */   
/*  34:    */   public InstanceQuery()
/*  35:    */     throws Exception
/*  36:    */   {}
/*  37:    */   
/*  38:    */   public Enumeration<Option> listOptions()
/*  39:    */   {
/*  40:128 */     Vector<Option> result = new Vector();
/*  41:    */     
/*  42:130 */     result.addElement(new Option("\tSQL query to execute.", "Q", 1, "-Q <query>"));
/*  43:    */     
/*  44:    */ 
/*  45:133 */     result.addElement(new Option("\tReturn sparse rather than normal instances.", "S", 0, "-S"));
/*  46:    */     
/*  47:    */ 
/*  48:136 */     result.addElement(new Option("\tThe username to use for connecting.", "U", 1, "-U <username>"));
/*  49:    */     
/*  50:    */ 
/*  51:139 */     result.addElement(new Option("\tThe password to use for connecting.", "P", 1, "-P <password>"));
/*  52:    */     
/*  53:    */ 
/*  54:142 */     result.add(new Option("\tThe custom properties file to use instead of default ones,\n\tcontaining the database parameters.\n\t(default: none)", "custom-props", 1, "-custom-props <file>"));
/*  55:    */     
/*  56:    */ 
/*  57:    */ 
/*  58:    */ 
/*  59:147 */     result.addElement(new Option("\tEnables debug output.", "D", 0, "-D"));
/*  60:    */     
/*  61:149 */     return result.elements();
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setOptions(String[] options)
/*  65:    */     throws Exception
/*  66:    */   {
/*  67:193 */     setSparseData(Utils.getFlag('S', options));
/*  68:    */     
/*  69:195 */     String tmpStr = Utils.getOption('Q', options);
/*  70:196 */     if (tmpStr.length() != 0) {
/*  71:197 */       setQuery(tmpStr);
/*  72:    */     }
/*  73:200 */     tmpStr = Utils.getOption('U', options);
/*  74:201 */     if (tmpStr.length() != 0) {
/*  75:202 */       setUsername(tmpStr);
/*  76:    */     }
/*  77:205 */     tmpStr = Utils.getOption('P', options);
/*  78:206 */     if (tmpStr.length() != 0) {
/*  79:207 */       setPassword(tmpStr);
/*  80:    */     }
/*  81:210 */     tmpStr = Utils.getOption("custom-props", options);
/*  82:211 */     if (tmpStr.length() == 0) {
/*  83:212 */       setCustomPropsFile(null);
/*  84:    */     } else {
/*  85:214 */       setCustomPropsFile(new File(tmpStr));
/*  86:    */     }
/*  87:217 */     setDebug(Utils.getFlag('D', options));
/*  88:    */   }
/*  89:    */   
/*  90:    */   public String queryTipText()
/*  91:    */   {
/*  92:227 */     return "The SQL query to execute against the database.";
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void setQuery(String q)
/*  96:    */   {
/*  97:236 */     this.m_Query = q;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public String getQuery()
/* 101:    */   {
/* 102:245 */     return this.m_Query;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public String sparseDataTipText()
/* 106:    */   {
/* 107:255 */     return "Encode data as sparse instances.";
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setSparseData(boolean s)
/* 111:    */   {
/* 112:264 */     this.m_CreateSparseData = s;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public boolean getSparseData()
/* 116:    */   {
/* 117:274 */     return this.m_CreateSparseData;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void setCustomPropsFile(File value)
/* 121:    */   {
/* 122:285 */     this.m_CustomPropsFile = value;
/* 123:286 */     initialize(this.m_CustomPropsFile);
/* 124:    */   }
/* 125:    */   
/* 126:    */   public File getCustomPropsFile()
/* 127:    */   {
/* 128:295 */     return this.m_CustomPropsFile;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public String customPropsFileTipText()
/* 132:    */   {
/* 133:304 */     return "The custom properties that the user can use to override the default ones.";
/* 134:    */   }
/* 135:    */   
/* 136:    */   public String[] getOptions()
/* 137:    */   {
/* 138:315 */     Vector<String> options = new Vector();
/* 139:    */     
/* 140:317 */     options.add("-Q");
/* 141:318 */     options.add(getQuery());
/* 142:320 */     if (getSparseData()) {
/* 143:321 */       options.add("-S");
/* 144:    */     }
/* 145:324 */     if (!getUsername().equals(""))
/* 146:    */     {
/* 147:325 */       options.add("-U");
/* 148:326 */       options.add(getUsername());
/* 149:    */     }
/* 150:329 */     if (!getPassword().equals(""))
/* 151:    */     {
/* 152:330 */       options.add("-P");
/* 153:331 */       options.add(getPassword());
/* 154:    */     }
/* 155:334 */     if ((this.m_CustomPropsFile != null) && (!this.m_CustomPropsFile.isDirectory()))
/* 156:    */     {
/* 157:335 */       options.add("-custom-props");
/* 158:336 */       options.add(this.m_CustomPropsFile.toString());
/* 159:    */     }
/* 160:339 */     if (getDebug()) {
/* 161:340 */       options.add("-D");
/* 162:    */     }
/* 163:343 */     return (String[])options.toArray(new String[options.size()]);
/* 164:    */   }
/* 165:    */   
/* 166:    */   public Instances retrieveInstances()
/* 167:    */     throws Exception
/* 168:    */   {
/* 169:354 */     return retrieveInstances(this.m_Query);
/* 170:    */   }
/* 171:    */   
/* 172:    */   public static Instances retrieveInstances(InstanceQueryAdapter adapter, ResultSet rs)
/* 173:    */     throws Exception
/* 174:    */   {
/* 175:359 */     if (adapter.getDebug()) {
/* 176:360 */       System.err.println("Getting metadata...");
/* 177:    */     }
/* 178:362 */     ResultSetMetaData md = rs.getMetaData();
/* 179:363 */     if (adapter.getDebug()) {
/* 180:364 */       System.err.println("Completed getting metadata...");
/* 181:    */     }
/* 182:368 */     int numAttributes = md.getColumnCount();
/* 183:369 */     int[] attributeTypes = new int[numAttributes];
/* 184:    */     
/* 185:371 */     Hashtable<String, Double>[] nominalIndexes = new Hashtable[numAttributes];
/* 186:    */     
/* 187:373 */     ArrayList<String>[] nominalStrings = new ArrayList[numAttributes];
/* 188:374 */     for (int i = 1; i <= numAttributes; i++) {
/* 189:381 */       switch (adapter.translateDBColumnType(md.getColumnTypeName(i)))
/* 190:    */       {
/* 191:    */       case 0: 
/* 192:385 */         attributeTypes[(i - 1)] = 1;
/* 193:386 */         nominalIndexes[(i - 1)] = new Hashtable();
/* 194:387 */         nominalStrings[(i - 1)] = new ArrayList();
/* 195:388 */         break;
/* 196:    */       case 9: 
/* 197:391 */         attributeTypes[(i - 1)] = 2;
/* 198:392 */         nominalIndexes[(i - 1)] = new Hashtable();
/* 199:393 */         nominalStrings[(i - 1)] = new ArrayList();
/* 200:394 */         break;
/* 201:    */       case 1: 
/* 202:397 */         attributeTypes[(i - 1)] = 1;
/* 203:398 */         nominalIndexes[(i - 1)] = new Hashtable();
/* 204:399 */         nominalIndexes[(i - 1)].put("false", new Double(0.0D));
/* 205:400 */         nominalIndexes[(i - 1)].put("true", new Double(1.0D));
/* 206:401 */         nominalStrings[(i - 1)] = new ArrayList();
/* 207:402 */         nominalStrings[(i - 1)].add("false");
/* 208:403 */         nominalStrings[(i - 1)].add("true");
/* 209:404 */         break;
/* 210:    */       case 2: 
/* 211:407 */         attributeTypes[(i - 1)] = 0;
/* 212:408 */         break;
/* 213:    */       case 3: 
/* 214:411 */         attributeTypes[(i - 1)] = 0;
/* 215:412 */         break;
/* 216:    */       case 4: 
/* 217:415 */         attributeTypes[(i - 1)] = 0;
/* 218:416 */         break;
/* 219:    */       case 5: 
/* 220:419 */         attributeTypes[(i - 1)] = 0;
/* 221:420 */         break;
/* 222:    */       case 6: 
/* 223:423 */         attributeTypes[(i - 1)] = 0;
/* 224:424 */         break;
/* 225:    */       case 7: 
/* 226:427 */         attributeTypes[(i - 1)] = 0;
/* 227:428 */         break;
/* 228:    */       case 8: 
/* 229:430 */         attributeTypes[(i - 1)] = 3;
/* 230:431 */         break;
/* 231:    */       case 10: 
/* 232:433 */         attributeTypes[(i - 1)] = 3;
/* 233:434 */         break;
/* 234:    */       case 11: 
/* 235:436 */         attributeTypes[(i - 1)] = 3;
/* 236:437 */         break;
/* 237:    */       default: 
/* 238:440 */         attributeTypes[(i - 1)] = 2;
/* 239:    */       }
/* 240:    */     }
/* 241:447 */     Vector<String> columnNames = new Vector();
/* 242:448 */     for (int i = 0; i < numAttributes; i++) {
/* 243:449 */       columnNames.add(md.getColumnLabel(i + 1));
/* 244:    */     }
/* 245:453 */     if (adapter.getDebug()) {
/* 246:454 */       System.err.println("Creating instances...");
/* 247:    */     }
/* 248:456 */     ArrayList<Instance> instances = new ArrayList();
/* 249:457 */     int rowCount = 0;
/* 250:458 */     while (rs.next())
/* 251:    */     {
/* 252:459 */       if ((rowCount % 100 == 0) && 
/* 253:460 */         (adapter.getDebug()))
/* 254:    */       {
/* 255:461 */         System.err.print("read " + rowCount + " instances \r");
/* 256:462 */         System.err.flush();
/* 257:    */       }
/* 258:465 */       double[] vals = new double[numAttributes];
/* 259:466 */       for (int i = 1; i <= numAttributes; i++) {
/* 260:472 */         switch (adapter.translateDBColumnType(md.getColumnTypeName(i)))
/* 261:    */         {
/* 262:    */         case 0: 
/* 263:474 */           String str = rs.getString(i);
/* 264:476 */           if (rs.wasNull())
/* 265:    */           {
/* 266:477 */             vals[(i - 1)] = Utils.missingValue();
/* 267:    */           }
/* 268:    */           else
/* 269:    */           {
/* 270:479 */             Double index = (Double)nominalIndexes[(i - 1)].get(str);
/* 271:480 */             if (index == null)
/* 272:    */             {
/* 273:481 */               index = new Double(nominalStrings[(i - 1)].size());
/* 274:482 */               nominalIndexes[(i - 1)].put(str, index);
/* 275:483 */               nominalStrings[(i - 1)].add(str);
/* 276:    */             }
/* 277:485 */             vals[(i - 1)] = index.doubleValue();
/* 278:    */           }
/* 279:487 */           break;
/* 280:    */         case 9: 
/* 281:489 */           String txt = rs.getString(i);
/* 282:491 */           if (rs.wasNull())
/* 283:    */           {
/* 284:492 */             vals[(i - 1)] = Utils.missingValue();
/* 285:    */           }
/* 286:    */           else
/* 287:    */           {
/* 288:494 */             Double index = (Double)nominalIndexes[(i - 1)].get(txt);
/* 289:495 */             if (index == null)
/* 290:    */             {
/* 291:499 */               index = Double.valueOf(new Double(nominalStrings[(i - 1)].size()).doubleValue() + 1.0D);
/* 292:500 */               nominalIndexes[(i - 1)].put(txt, index);
/* 293:501 */               nominalStrings[(i - 1)].add(txt);
/* 294:    */             }
/* 295:503 */             vals[(i - 1)] = index.doubleValue();
/* 296:    */           }
/* 297:505 */           break;
/* 298:    */         case 1: 
/* 299:507 */           boolean boo = rs.getBoolean(i);
/* 300:508 */           if (rs.wasNull()) {
/* 301:509 */             vals[(i - 1)] = Utils.missingValue();
/* 302:    */           } else {
/* 303:511 */             vals[(i - 1)] = (boo ? 1.0D : 0.0D);
/* 304:    */           }
/* 305:513 */           break;
/* 306:    */         case 2: 
/* 307:516 */           double dd = rs.getDouble(i);
/* 308:518 */           if (rs.wasNull()) {
/* 309:519 */             vals[(i - 1)] = Utils.missingValue();
/* 310:    */           } else {
/* 311:522 */             vals[(i - 1)] = dd;
/* 312:    */           }
/* 313:524 */           break;
/* 314:    */         case 3: 
/* 315:526 */           byte by = rs.getByte(i);
/* 316:527 */           if (rs.wasNull()) {
/* 317:528 */             vals[(i - 1)] = Utils.missingValue();
/* 318:    */           } else {
/* 319:530 */             vals[(i - 1)] = by;
/* 320:    */           }
/* 321:532 */           break;
/* 322:    */         case 4: 
/* 323:534 */           short sh = rs.getShort(i);
/* 324:535 */           if (rs.wasNull()) {
/* 325:536 */             vals[(i - 1)] = Utils.missingValue();
/* 326:    */           } else {
/* 327:538 */             vals[(i - 1)] = sh;
/* 328:    */           }
/* 329:540 */           break;
/* 330:    */         case 5: 
/* 331:542 */           int in = rs.getInt(i);
/* 332:543 */           if (rs.wasNull()) {
/* 333:544 */             vals[(i - 1)] = Utils.missingValue();
/* 334:    */           } else {
/* 335:546 */             vals[(i - 1)] = in;
/* 336:    */           }
/* 337:548 */           break;
/* 338:    */         case 6: 
/* 339:550 */           long lo = rs.getLong(i);
/* 340:551 */           if (rs.wasNull()) {
/* 341:552 */             vals[(i - 1)] = Utils.missingValue();
/* 342:    */           } else {
/* 343:554 */             vals[(i - 1)] = lo;
/* 344:    */           }
/* 345:556 */           break;
/* 346:    */         case 7: 
/* 347:558 */           float fl = rs.getFloat(i);
/* 348:559 */           if (rs.wasNull()) {
/* 349:560 */             vals[(i - 1)] = Utils.missingValue();
/* 350:    */           } else {
/* 351:562 */             vals[(i - 1)] = fl;
/* 352:    */           }
/* 353:564 */           break;
/* 354:    */         case 8: 
/* 355:566 */           Date date = rs.getDate(i);
/* 356:567 */           if (rs.wasNull()) {
/* 357:568 */             vals[(i - 1)] = Utils.missingValue();
/* 358:    */           } else {
/* 359:571 */             vals[(i - 1)] = date.getTime();
/* 360:    */           }
/* 361:573 */           break;
/* 362:    */         case 10: 
/* 363:575 */           Time time = rs.getTime(i);
/* 364:576 */           if (rs.wasNull()) {
/* 365:577 */             vals[(i - 1)] = Utils.missingValue();
/* 366:    */           } else {
/* 367:580 */             vals[(i - 1)] = time.getTime();
/* 368:    */           }
/* 369:582 */           break;
/* 370:    */         case 11: 
/* 371:584 */           Timestamp ts = rs.getTimestamp(i);
/* 372:585 */           if (rs.wasNull()) {
/* 373:586 */             vals[(i - 1)] = Utils.missingValue();
/* 374:    */           } else {
/* 375:588 */             vals[(i - 1)] = ts.getTime();
/* 376:    */           }
/* 377:590 */           break;
/* 378:    */         default: 
/* 379:592 */           vals[(i - 1)] = Utils.missingValue();
/* 380:    */         }
/* 381:    */       }
/* 382:    */       Instance newInst;
/* 383:    */       Instance newInst;
/* 384:596 */       if (adapter.getSparseData()) {
/* 385:597 */         newInst = new SparseInstance(1.0D, vals);
/* 386:    */       } else {
/* 387:599 */         newInst = new DenseInstance(1.0D, vals);
/* 388:    */       }
/* 389:601 */       instances.add(newInst);
/* 390:602 */       rowCount++;
/* 391:    */     }
/* 392:607 */     if (adapter.getDebug()) {
/* 393:608 */       System.err.println("Creating header...");
/* 394:    */     }
/* 395:610 */     ArrayList<Attribute> attribInfo = new ArrayList();
/* 396:611 */     for (int i = 0; i < numAttributes; i++)
/* 397:    */     {
/* 398:614 */       String attribName = adapter.attributeCaseFix((String)columnNames.get(i));
/* 399:615 */       switch (attributeTypes[i])
/* 400:    */       {
/* 401:    */       case 1: 
/* 402:617 */         attribInfo.add(new Attribute(attribName, nominalStrings[i]));
/* 403:618 */         break;
/* 404:    */       case 0: 
/* 405:620 */         attribInfo.add(new Attribute(attribName));
/* 406:621 */         break;
/* 407:    */       case 2: 
/* 408:623 */         Attribute att = new Attribute(attribName, (ArrayList)null);
/* 409:624 */         attribInfo.add(att);
/* 410:625 */         for (int n = 0; n < nominalStrings[i].size(); n++) {
/* 411:626 */           att.addStringValue((String)nominalStrings[i].get(n));
/* 412:    */         }
/* 413:628 */         break;
/* 414:    */       case 3: 
/* 415:630 */         attribInfo.add(new Attribute(attribName, (String)null));
/* 416:631 */         break;
/* 417:    */       default: 
/* 418:633 */         throw new Exception("Unknown attribute type");
/* 419:    */       }
/* 420:    */     }
/* 421:636 */     Instances result = new Instances("QueryResult", attribInfo, instances.size());
/* 422:638 */     for (int i = 0; i < instances.size(); i++) {
/* 423:639 */       result.add((Instance)instances.get(i));
/* 424:    */     }
/* 425:642 */     return result;
/* 426:    */   }
/* 427:    */   
/* 428:    */   public Instances retrieveInstances(String query)
/* 429:    */     throws Exception
/* 430:    */   {
/* 431:655 */     if (this.m_Debug) {
/* 432:656 */       System.err.println("Executing query: " + query);
/* 433:    */     }
/* 434:658 */     connectToDatabase();
/* 435:659 */     if (!execute(query))
/* 436:    */     {
/* 437:660 */       if (this.m_PreparedStatement.getUpdateCount() == -1) {
/* 438:661 */         throw new Exception("Query didn't produce results");
/* 439:    */       }
/* 440:663 */       if (this.m_Debug) {
/* 441:664 */         System.err.println(this.m_PreparedStatement.getUpdateCount() + " rows affected.");
/* 442:    */       }
/* 443:667 */       close();
/* 444:668 */       return null;
/* 445:    */     }
/* 446:671 */     ResultSet rs = getResultSet();
/* 447:672 */     if (this.m_Debug) {
/* 448:673 */       System.err.println("Getting metadata...");
/* 449:    */     }
/* 450:676 */     Instances result = retrieveInstances(this, rs);
/* 451:677 */     close(rs);
/* 452:    */     
/* 453:679 */     return result;
/* 454:    */   }
/* 455:    */   
/* 456:    */   public static void main(String[] args)
/* 457:    */   {
/* 458:    */     try
/* 459:    */     {
/* 460:691 */       InstanceQuery iq = new InstanceQuery();
/* 461:692 */       String query = Utils.getOption('Q', args);
/* 462:693 */       if (query.length() == 0) {
/* 463:694 */         iq.setQuery("select * from Experiment_index");
/* 464:    */       } else {
/* 465:696 */         iq.setQuery(query);
/* 466:    */       }
/* 467:698 */       iq.setOptions(args);
/* 468:    */       try
/* 469:    */       {
/* 470:700 */         Utils.checkForRemainingOptions(args);
/* 471:    */       }
/* 472:    */       catch (Exception e)
/* 473:    */       {
/* 474:702 */         System.err.println("Options for weka.experiment.InstanceQuery:\n");
/* 475:703 */         Enumeration<Option> en = iq.listOptions();
/* 476:704 */         while (en.hasMoreElements())
/* 477:    */         {
/* 478:705 */           Option o = (Option)en.nextElement();
/* 479:706 */           System.err.println(o.synopsis() + "\n" + o.description());
/* 480:    */         }
/* 481:708 */         System.exit(1);
/* 482:    */       }
/* 483:711 */       Instances aha = iq.retrieveInstances();
/* 484:712 */       iq.disconnectFromDatabase();
/* 485:714 */       if (aha == null) {
/* 486:715 */         return;
/* 487:    */       }
/* 488:720 */       System.out.println(new Instances(aha, 0));
/* 489:721 */       for (int i = 0; i < aha.numInstances(); i++) {
/* 490:722 */         System.out.println(aha.instance(i));
/* 491:    */       }
/* 492:    */     }
/* 493:    */     catch (Exception e)
/* 494:    */     {
/* 495:725 */       e.printStackTrace();
/* 496:726 */       System.err.println(e.getMessage());
/* 497:    */     }
/* 498:    */   }
/* 499:    */   
/* 500:    */   public String getRevision()
/* 501:    */   {
/* 502:737 */     return RevisionUtils.extract("$Revision: 11885 $");
/* 503:    */   }
/* 504:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.InstanceQuery
 * JD-Core Version:    0.7.0.1
 */