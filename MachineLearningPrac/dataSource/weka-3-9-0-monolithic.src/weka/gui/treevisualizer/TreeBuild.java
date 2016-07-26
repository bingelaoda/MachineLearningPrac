/*   1:    */ package weka.gui.treevisualizer;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.io.BufferedReader;
/*   5:    */ import java.io.IOException;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.io.Reader;
/*   8:    */ import java.io.StreamTokenizer;
/*   9:    */ import java.util.Hashtable;
/*  10:    */ import java.util.Vector;
/*  11:    */ 
/*  12:    */ public class TreeBuild
/*  13:    */ {
/*  14:    */   private Vector<Node> m_aNodes;
/*  15:    */   private Vector<Edge> m_aEdges;
/*  16:    */   private Vector<InfoObject> m_nodes;
/*  17:    */   private Vector<InfoObject> m_edges;
/*  18:    */   private InfoObject m_grObj;
/*  19:    */   private InfoObject m_noObj;
/*  20:    */   private InfoObject m_edObj;
/*  21:    */   private StreamTokenizer m_st;
/*  22:    */   private final Hashtable<String, Color> m_colorTable;
/*  23:    */   
/*  24:    */   public TreeBuild()
/*  25:    */   {
/*  26: 88 */     this.m_colorTable = new Hashtable();
/*  27:    */     
/*  28: 90 */     Colors ab = new Colors();
/*  29: 91 */     for (NamedColor m_col : ab.m_cols) {
/*  30: 92 */       this.m_colorTable.put(m_col.m_name, m_col.m_col);
/*  31:    */     }
/*  32:    */   }
/*  33:    */   
/*  34:    */   public Node create(Reader t)
/*  35:    */   {
/*  36:105 */     this.m_nodes = new Vector(50, 50);
/*  37:106 */     this.m_edges = new Vector(50, 50);
/*  38:107 */     this.m_grObj = new InfoObject("graph");
/*  39:108 */     this.m_noObj = new InfoObject("node");
/*  40:109 */     this.m_edObj = new InfoObject("edge");
/*  41:    */     
/*  42:    */ 
/*  43:112 */     this.m_st = new StreamTokenizer(new BufferedReader(t));
/*  44:113 */     setSyntax();
/*  45:    */     
/*  46:115 */     graph();
/*  47:    */     
/*  48:117 */     Node top = generateStructures();
/*  49:    */     
/*  50:119 */     return top;
/*  51:    */   }
/*  52:    */   
/*  53:    */   private Node generateStructures()
/*  54:    */   {
/*  55:135 */     this.m_aNodes = new Vector(50, 50);
/*  56:136 */     this.m_aEdges = new Vector(50, 50);
/*  57:137 */     for (int noa = 0; noa < this.m_nodes.size(); noa++)
/*  58:    */     {
/*  59:138 */       InfoObject t = (InfoObject)this.m_nodes.elementAt(noa);
/*  60:139 */       String id = t.m_id;
/*  61:    */       String label;
/*  62:    */       String label;
/*  63:141 */       if (t.m_label == null)
/*  64:    */       {
/*  65:    */         String label;
/*  66:142 */         if (this.m_noObj.m_label == null) {
/*  67:143 */           label = "";
/*  68:    */         } else {
/*  69:145 */           label = this.m_noObj.m_label;
/*  70:    */         }
/*  71:    */       }
/*  72:    */       else
/*  73:    */       {
/*  74:148 */         label = t.m_label;
/*  75:    */       }
/*  76:    */       Integer shape;
/*  77:    */       Integer shape;
/*  78:151 */       if (t.m_shape == null)
/*  79:    */       {
/*  80:    */         Integer shape;
/*  81:152 */         if (this.m_noObj.m_shape == null) {
/*  82:153 */           shape = new Integer(2);
/*  83:    */         } else {
/*  84:155 */           shape = getShape(this.m_noObj.m_shape);
/*  85:    */         }
/*  86:    */       }
/*  87:    */       else
/*  88:    */       {
/*  89:158 */         shape = getShape(t.m_shape);
/*  90:    */       }
/*  91:160 */       if (shape == null) {
/*  92:161 */         shape = new Integer(2);
/*  93:    */       }
/*  94:    */       Integer style;
/*  95:    */       Integer style;
/*  96:164 */       if (t.m_style == null)
/*  97:    */       {
/*  98:    */         Integer style;
/*  99:165 */         if (this.m_noObj.m_style == null) {
/* 100:166 */           style = new Integer(1);
/* 101:    */         } else {
/* 102:168 */           style = getStyle(this.m_noObj.m_style);
/* 103:    */         }
/* 104:    */       }
/* 105:    */       else
/* 106:    */       {
/* 107:171 */         style = getStyle(t.m_style);
/* 108:    */       }
/* 109:173 */       if (style == null) {
/* 110:174 */         style = new Integer(1);
/* 111:    */       }
/* 112:    */       Color fontcolor;
/* 113:    */       Color fontcolor;
/* 114:184 */       if (t.m_fontColor == null)
/* 115:    */       {
/* 116:    */         Color fontcolor;
/* 117:185 */         if (this.m_noObj.m_fontColor == null) {
/* 118:186 */           fontcolor = Color.black;
/* 119:    */         } else {
/* 120:188 */           fontcolor = (Color)this.m_colorTable.get(this.m_noObj.m_fontColor.toLowerCase());
/* 121:    */         }
/* 122:    */       }
/* 123:    */       else
/* 124:    */       {
/* 125:191 */         fontcolor = (Color)this.m_colorTable.get(t.m_fontColor.toLowerCase());
/* 126:    */       }
/* 127:193 */       if (fontcolor == null) {
/* 128:194 */         fontcolor = Color.black;
/* 129:    */       }
/* 130:    */       Color color;
/* 131:    */       Color color;
/* 132:197 */       if (t.m_color == null)
/* 133:    */       {
/* 134:    */         Color color;
/* 135:198 */         if (this.m_noObj.m_color == null) {
/* 136:199 */           color = Color.gray;
/* 137:    */         } else {
/* 138:201 */           color = (Color)this.m_colorTable.get(this.m_noObj.m_color.toLowerCase());
/* 139:    */         }
/* 140:    */       }
/* 141:    */       else
/* 142:    */       {
/* 143:204 */         color = (Color)this.m_colorTable.get(t.m_color.toLowerCase());
/* 144:    */       }
/* 145:206 */       if (color == null) {
/* 146:207 */         color = Color.gray;
/* 147:    */       }
/* 148:210 */       this.m_aNodes.addElement(new Node(label, id, style.intValue(), shape.intValue(), color, t.m_data));
/* 149:    */     }
/* 150:214 */     for (int noa = 0; noa < this.m_edges.size(); noa++)
/* 151:    */     {
/* 152:215 */       InfoObject t = (InfoObject)this.m_edges.elementAt(noa);
/* 153:216 */       String id = t.m_id;
/* 154:    */       String label;
/* 155:    */       String label;
/* 156:218 */       if (t.m_label == null)
/* 157:    */       {
/* 158:    */         String label;
/* 159:219 */         if (this.m_noObj.m_label == null) {
/* 160:220 */           label = "";
/* 161:    */         } else {
/* 162:222 */           label = this.m_noObj.m_label;
/* 163:    */         }
/* 164:    */       }
/* 165:    */       else
/* 166:    */       {
/* 167:225 */         label = t.m_label;
/* 168:    */       }
/* 169:    */       Integer shape;
/* 170:    */       Integer shape;
/* 171:228 */       if (t.m_shape == null)
/* 172:    */       {
/* 173:    */         Integer shape;
/* 174:229 */         if (this.m_noObj.m_shape == null) {
/* 175:230 */           shape = new Integer(2);
/* 176:    */         } else {
/* 177:232 */           shape = getShape(this.m_noObj.m_shape);
/* 178:    */         }
/* 179:    */       }
/* 180:    */       else
/* 181:    */       {
/* 182:235 */         shape = getShape(t.m_shape);
/* 183:    */       }
/* 184:237 */       if (shape == null) {
/* 185:238 */         shape = new Integer(2);
/* 186:    */       }
/* 187:    */       Integer style;
/* 188:    */       Integer style;
/* 189:241 */       if (t.m_style == null)
/* 190:    */       {
/* 191:    */         Integer style;
/* 192:242 */         if (this.m_noObj.m_style == null) {
/* 193:243 */           style = new Integer(1);
/* 194:    */         } else {
/* 195:245 */           style = getStyle(this.m_noObj.m_style);
/* 196:    */         }
/* 197:    */       }
/* 198:    */       else
/* 199:    */       {
/* 200:248 */         style = getStyle(t.m_style);
/* 201:    */       }
/* 202:250 */       if (style == null) {
/* 203:251 */         style = new Integer(1);
/* 204:    */       }
/* 205:    */       Color fontcolor;
/* 206:    */       Color fontcolor;
/* 207:261 */       if (t.m_fontColor == null)
/* 208:    */       {
/* 209:    */         Color fontcolor;
/* 210:262 */         if (this.m_noObj.m_fontColor == null) {
/* 211:263 */           fontcolor = Color.black;
/* 212:    */         } else {
/* 213:265 */           fontcolor = (Color)this.m_colorTable.get(this.m_noObj.m_fontColor.toLowerCase());
/* 214:    */         }
/* 215:    */       }
/* 216:    */       else
/* 217:    */       {
/* 218:268 */         fontcolor = (Color)this.m_colorTable.get(t.m_fontColor.toLowerCase());
/* 219:    */       }
/* 220:270 */       if (fontcolor == null) {
/* 221:271 */         fontcolor = Color.black;
/* 222:    */       }
/* 223:    */       Color color;
/* 224:    */       Color color;
/* 225:274 */       if (t.m_color == null)
/* 226:    */       {
/* 227:    */         Color color;
/* 228:275 */         if (this.m_noObj.m_color == null) {
/* 229:276 */           color = Color.white;
/* 230:    */         } else {
/* 231:278 */           color = (Color)this.m_colorTable.get(this.m_noObj.m_color.toLowerCase());
/* 232:    */         }
/* 233:    */       }
/* 234:    */       else
/* 235:    */       {
/* 236:281 */         color = (Color)this.m_colorTable.get(t.m_color.toLowerCase());
/* 237:    */       }
/* 238:283 */       if (color == null) {
/* 239:284 */         color = Color.white;
/* 240:    */       }
/* 241:287 */       this.m_aEdges.addElement(new Edge(label, t.m_source, t.m_target));
/* 242:    */     }
/* 243:291 */     Node sour = null;Node targ = null;
/* 244:293 */     for (int noa = 0; noa < this.m_aEdges.size(); noa++)
/* 245:    */     {
/* 246:294 */       boolean f_set = false;
/* 247:295 */       boolean s_set = false;
/* 248:296 */       Edge y = (Edge)this.m_aEdges.elementAt(noa);
/* 249:297 */       for (int nob = 0; nob < this.m_aNodes.size(); nob++)
/* 250:    */       {
/* 251:298 */         Node x = (Node)this.m_aNodes.elementAt(nob);
/* 252:299 */         if (x.getRefer().equals(y.getRtarget()))
/* 253:    */         {
/* 254:300 */           f_set = true;
/* 255:301 */           targ = x;
/* 256:    */         }
/* 257:303 */         if (x.getRefer().equals(y.getRsource()))
/* 258:    */         {
/* 259:304 */           s_set = true;
/* 260:305 */           sour = x;
/* 261:    */         }
/* 262:307 */         if ((f_set == true) && (s_set == true)) {
/* 263:    */           break;
/* 264:    */         }
/* 265:    */       }
/* 266:311 */       if (targ != sour)
/* 267:    */       {
/* 268:312 */         y.setTarget(targ);
/* 269:313 */         y.setSource(sour);
/* 270:    */       }
/* 271:    */       else
/* 272:    */       {
/* 273:315 */         System.out.println("logic error");
/* 274:    */       }
/* 275:    */     }
/* 276:319 */     for (int noa = 0; noa < this.m_aNodes.size(); noa++) {
/* 277:320 */       if (((Node)this.m_aNodes.elementAt(noa)).getParent(0) == null) {
/* 278:321 */         sour = (Node)this.m_aNodes.elementAt(noa);
/* 279:    */       }
/* 280:    */     }
/* 281:325 */     return sour;
/* 282:    */   }
/* 283:    */   
/* 284:    */   private Integer getShape(String sh)
/* 285:    */   {
/* 286:335 */     if ((sh.equalsIgnoreCase("box")) || (sh.equalsIgnoreCase("rectangle"))) {
/* 287:336 */       return new Integer(1);
/* 288:    */     }
/* 289:337 */     if (sh.equalsIgnoreCase("oval")) {
/* 290:338 */       return new Integer(2);
/* 291:    */     }
/* 292:339 */     if (sh.equalsIgnoreCase("diamond")) {
/* 293:340 */       return new Integer(3);
/* 294:    */     }
/* 295:342 */     return null;
/* 296:    */   }
/* 297:    */   
/* 298:    */   private Integer getStyle(String sty)
/* 299:    */   {
/* 300:354 */     if (sty.equalsIgnoreCase("filled")) {
/* 301:355 */       return new Integer(1);
/* 302:    */     }
/* 303:357 */     return null;
/* 304:    */   }
/* 305:    */   
/* 306:    */   private void setSyntax()
/* 307:    */   {
/* 308:367 */     this.m_st.resetSyntax();
/* 309:368 */     this.m_st.eolIsSignificant(false);
/* 310:369 */     this.m_st.slashStarComments(true);
/* 311:370 */     this.m_st.slashSlashComments(true);
/* 312:    */     
/* 313:372 */     this.m_st.whitespaceChars(0, 32);
/* 314:373 */     this.m_st.wordChars(33, 255);
/* 315:374 */     this.m_st.ordinaryChar(91);
/* 316:375 */     this.m_st.ordinaryChar(93);
/* 317:376 */     this.m_st.ordinaryChar(123);
/* 318:377 */     this.m_st.ordinaryChar(125);
/* 319:378 */     this.m_st.ordinaryChar(45);
/* 320:379 */     this.m_st.ordinaryChar(62);
/* 321:380 */     this.m_st.ordinaryChar(47);
/* 322:381 */     this.m_st.ordinaryChar(42);
/* 323:382 */     this.m_st.quoteChar(34);
/* 324:383 */     this.m_st.whitespaceChars(59, 59);
/* 325:384 */     this.m_st.ordinaryChar(61);
/* 326:    */   }
/* 327:    */   
/* 328:    */   private void alterSyntax()
/* 329:    */   {
/* 330:391 */     this.m_st.resetSyntax();
/* 331:392 */     this.m_st.wordChars(0, 255);
/* 332:393 */     this.m_st.slashStarComments(false);
/* 333:394 */     this.m_st.slashSlashComments(false);
/* 334:395 */     this.m_st.ordinaryChar(10);
/* 335:396 */     this.m_st.ordinaryChar(13);
/* 336:    */   }
/* 337:    */   
/* 338:    */   private void nextToken(String r)
/* 339:    */   {
/* 340:406 */     int t = 0;
/* 341:    */     try
/* 342:    */     {
/* 343:408 */       t = this.m_st.nextToken();
/* 344:    */     }
/* 345:    */     catch (IOException e) {}
/* 346:412 */     if (t == -1) {
/* 347:413 */       System.out.println("eof , " + r);
/* 348:414 */     } else if (t == -2) {
/* 349:415 */       System.out.println("got a number , " + r);
/* 350:    */     }
/* 351:    */   }
/* 352:    */   
/* 353:    */   private void graph()
/* 354:    */   {
/* 355:424 */     nextToken("expected 'digraph'");
/* 356:426 */     if (!this.m_st.sval.equalsIgnoreCase("digraph")) {
/* 357:429 */       System.out.println("expected 'digraph'");
/* 358:    */     }
/* 359:432 */     nextToken("expected a Graph Name");
/* 360:433 */     if (this.m_st.sval == null) {
/* 361:436 */       System.out.println("expected a Graph Name");
/* 362:    */     }
/* 363:439 */     nextToken("expected '{'");
/* 364:441 */     if (this.m_st.ttype == 123) {
/* 365:442 */       stmtList();
/* 366:    */     } else {
/* 367:444 */       System.out.println("expected '{'");
/* 368:    */     }
/* 369:    */   }
/* 370:    */   
/* 371:    */   private void stmtList()
/* 372:    */   {
/* 373:454 */     boolean flag = true;
/* 374:455 */     while (flag)
/* 375:    */     {
/* 376:456 */       nextToken("expects a STMT_LIST item or '}'");
/* 377:457 */       if (this.m_st.ttype == 125)
/* 378:    */       {
/* 379:458 */         flag = false;
/* 380:    */       }
/* 381:459 */       else if ((this.m_st.sval.equalsIgnoreCase("graph")) || (this.m_st.sval.equalsIgnoreCase("node")) || (this.m_st.sval.equalsIgnoreCase("edge")))
/* 382:    */       {
/* 383:462 */         this.m_st.pushBack();
/* 384:463 */         attrStmt();
/* 385:    */       }
/* 386:    */       else
/* 387:    */       {
/* 388:465 */         nodeId(this.m_st.sval, 0);
/* 389:    */       }
/* 390:    */     }
/* 391:    */   }
/* 392:    */   
/* 393:    */   private void attrStmt()
/* 394:    */   {
/* 395:476 */     nextToken("expected 'graph' or 'node' or 'edge'");
/* 396:478 */     if (this.m_st.sval.equalsIgnoreCase("graph"))
/* 397:    */     {
/* 398:479 */       nextToken("expected a '['");
/* 399:480 */       if (this.m_st.ttype == 91) {
/* 400:481 */         attrList(this.m_grObj);
/* 401:    */       } else {
/* 402:483 */         System.out.println("expected a '['");
/* 403:    */       }
/* 404:    */     }
/* 405:485 */     else if (this.m_st.sval.equalsIgnoreCase("node"))
/* 406:    */     {
/* 407:486 */       nextToken("expected a '['");
/* 408:487 */       if (this.m_st.ttype == 91) {
/* 409:488 */         attrList(this.m_noObj);
/* 410:    */       } else {
/* 411:490 */         System.out.println("expected a '['");
/* 412:    */       }
/* 413:    */     }
/* 414:492 */     else if (this.m_st.sval.equalsIgnoreCase("edge"))
/* 415:    */     {
/* 416:493 */       nextToken("expected a '['");
/* 417:494 */       if (this.m_st.ttype == 91) {
/* 418:495 */         attrList(this.m_edObj);
/* 419:    */       } else {
/* 420:497 */         System.out.println("expected a '['");
/* 421:    */       }
/* 422:    */     }
/* 423:    */     else
/* 424:    */     {
/* 425:501 */       System.out.println("expected 'graph' or 'node' or 'edge'");
/* 426:    */     }
/* 427:    */   }
/* 428:    */   
/* 429:    */   private void nodeId(String s, int t)
/* 430:    */   {
/* 431:514 */     nextToken("error occurred in node_id");
/* 432:516 */     if (this.m_st.ttype == 125)
/* 433:    */     {
/* 434:518 */       if (t == 0) {
/* 435:519 */         this.m_nodes.addElement(new InfoObject(s));
/* 436:    */       }
/* 437:521 */       this.m_st.pushBack();
/* 438:    */     }
/* 439:522 */     else if (this.m_st.ttype == 45)
/* 440:    */     {
/* 441:523 */       nextToken("error occurred checking for an edge");
/* 442:524 */       if (this.m_st.ttype == 62) {
/* 443:525 */         edgeStmt(s);
/* 444:    */       } else {
/* 445:527 */         System.out.println("error occurred checking for an edge");
/* 446:    */       }
/* 447:    */     }
/* 448:529 */     else if (this.m_st.ttype == 91)
/* 449:    */     {
/* 450:531 */       if (t == 0)
/* 451:    */       {
/* 452:532 */         this.m_nodes.addElement(new InfoObject(s));
/* 453:533 */         attrList((InfoObject)this.m_nodes.lastElement());
/* 454:    */       }
/* 455:    */       else
/* 456:    */       {
/* 457:535 */         attrList((InfoObject)this.m_edges.lastElement());
/* 458:    */       }
/* 459:    */     }
/* 460:537 */     else if (this.m_st.sval != null)
/* 461:    */     {
/* 462:539 */       if (t == 0) {
/* 463:540 */         this.m_nodes.addElement(new InfoObject(s));
/* 464:    */       }
/* 465:542 */       this.m_st.pushBack();
/* 466:    */     }
/* 467:    */     else
/* 468:    */     {
/* 469:544 */       System.out.println("error occurred in node_id");
/* 470:    */     }
/* 471:    */   }
/* 472:    */   
/* 473:    */   private void edgeStmt(String i)
/* 474:    */   {
/* 475:554 */     nextToken("error getting target of edge");
/* 476:556 */     if (this.m_st.sval != null)
/* 477:    */     {
/* 478:557 */       this.m_edges.addElement(new InfoObject("an edge ,no id"));
/* 479:558 */       ((InfoObject)this.m_edges.lastElement()).m_source = i;
/* 480:559 */       ((InfoObject)this.m_edges.lastElement()).m_target = this.m_st.sval;
/* 481:560 */       nodeId(this.m_st.sval, 1);
/* 482:    */     }
/* 483:    */     else
/* 484:    */     {
/* 485:562 */       System.out.println("error getting target of edge");
/* 486:    */     }
/* 487:    */   }
/* 488:    */   
/* 489:    */   private void attrList(InfoObject a)
/* 490:    */   {
/* 491:572 */     boolean flag = true;
/* 492:574 */     while (flag)
/* 493:    */     {
/* 494:575 */       nextToken("error in attr_list");
/* 495:577 */       if (this.m_st.ttype == 93)
/* 496:    */       {
/* 497:578 */         flag = false;
/* 498:    */       }
/* 499:579 */       else if (this.m_st.sval.equalsIgnoreCase("color"))
/* 500:    */       {
/* 501:580 */         nextToken("error getting color");
/* 502:581 */         if (this.m_st.ttype == 61)
/* 503:    */         {
/* 504:582 */           nextToken("error getting color");
/* 505:583 */           if (this.m_st.sval != null) {
/* 506:584 */             a.m_color = this.m_st.sval;
/* 507:    */           } else {
/* 508:586 */             System.out.println("error getting color");
/* 509:    */           }
/* 510:    */         }
/* 511:    */         else
/* 512:    */         {
/* 513:589 */           System.out.println("error getting color");
/* 514:    */         }
/* 515:    */       }
/* 516:591 */       else if (this.m_st.sval.equalsIgnoreCase("fontcolor"))
/* 517:    */       {
/* 518:592 */         nextToken("error getting font color");
/* 519:593 */         if (this.m_st.ttype == 61)
/* 520:    */         {
/* 521:594 */           nextToken("error getting font color");
/* 522:595 */           if (this.m_st.sval != null) {
/* 523:596 */             a.m_fontColor = this.m_st.sval;
/* 524:    */           } else {
/* 525:598 */             System.out.println("error getting font color");
/* 526:    */           }
/* 527:    */         }
/* 528:    */         else
/* 529:    */         {
/* 530:601 */           System.out.println("error getting font color");
/* 531:    */         }
/* 532:    */       }
/* 533:603 */       else if (this.m_st.sval.equalsIgnoreCase("fontsize"))
/* 534:    */       {
/* 535:604 */         nextToken("error getting font size");
/* 536:605 */         if (this.m_st.ttype == 61)
/* 537:    */         {
/* 538:606 */           nextToken("error getting font size");
/* 539:607 */           if (this.m_st.sval == null) {
/* 540:609 */             System.out.println("error getting font size");
/* 541:    */           }
/* 542:    */         }
/* 543:    */         else
/* 544:    */         {
/* 545:612 */           System.out.println("error getting font size");
/* 546:    */         }
/* 547:    */       }
/* 548:614 */       else if (this.m_st.sval.equalsIgnoreCase("label"))
/* 549:    */       {
/* 550:615 */         nextToken("error getting label");
/* 551:616 */         if (this.m_st.ttype == 61)
/* 552:    */         {
/* 553:617 */           nextToken("error getting label");
/* 554:618 */           if (this.m_st.sval != null) {
/* 555:619 */             a.m_label = this.m_st.sval;
/* 556:    */           } else {
/* 557:621 */             System.out.println("error getting label");
/* 558:    */           }
/* 559:    */         }
/* 560:    */         else
/* 561:    */         {
/* 562:624 */           System.out.println("error getting label");
/* 563:    */         }
/* 564:    */       }
/* 565:626 */       else if (this.m_st.sval.equalsIgnoreCase("shape"))
/* 566:    */       {
/* 567:627 */         nextToken("error getting shape");
/* 568:628 */         if (this.m_st.ttype == 61)
/* 569:    */         {
/* 570:629 */           nextToken("error getting shape");
/* 571:630 */           if (this.m_st.sval != null) {
/* 572:631 */             a.m_shape = this.m_st.sval;
/* 573:    */           } else {
/* 574:633 */             System.out.println("error getting shape");
/* 575:    */           }
/* 576:    */         }
/* 577:    */         else
/* 578:    */         {
/* 579:636 */           System.out.println("error getting shape");
/* 580:    */         }
/* 581:    */       }
/* 582:638 */       else if (this.m_st.sval.equalsIgnoreCase("style"))
/* 583:    */       {
/* 584:639 */         nextToken("error getting style");
/* 585:640 */         if (this.m_st.ttype == 61)
/* 586:    */         {
/* 587:641 */           nextToken("error getting style");
/* 588:642 */           if (this.m_st.sval != null) {
/* 589:643 */             a.m_style = this.m_st.sval;
/* 590:    */           } else {
/* 591:645 */             System.out.println("error getting style");
/* 592:    */           }
/* 593:    */         }
/* 594:    */         else
/* 595:    */         {
/* 596:648 */           System.out.println("error getting style");
/* 597:    */         }
/* 598:    */       }
/* 599:650 */       else if (this.m_st.sval.equalsIgnoreCase("data"))
/* 600:    */       {
/* 601:651 */         nextToken("error getting data");
/* 602:652 */         if (this.m_st.ttype == 61)
/* 603:    */         {
/* 604:655 */           alterSyntax();
/* 605:656 */           a.m_data = new String("");
/* 606:    */           for (;;)
/* 607:    */           {
/* 608:659 */             nextToken("error getting data");
/* 609:660 */             if ((this.m_st.sval != null) && (a.m_data != null) && (this.m_st.sval.equals(","))) {
/* 610:    */               break;
/* 611:    */             }
/* 612:662 */             if (this.m_st.sval != null) {
/* 613:663 */               a.m_data = a.m_data.concat(this.m_st.sval);
/* 614:664 */             } else if (this.m_st.ttype == 13) {
/* 615:665 */               a.m_data = a.m_data.concat("\r");
/* 616:666 */             } else if (this.m_st.ttype == 10) {
/* 617:667 */               a.m_data = a.m_data.concat("\n");
/* 618:    */             } else {
/* 619:669 */               System.out.println("error getting data");
/* 620:    */             }
/* 621:    */           }
/* 622:672 */           setSyntax();
/* 623:    */         }
/* 624:    */         else
/* 625:    */         {
/* 626:674 */           System.out.println("error getting data");
/* 627:    */         }
/* 628:    */       }
/* 629:    */     }
/* 630:    */   }
/* 631:    */   
/* 632:    */   private class InfoObject
/* 633:    */   {
/* 634:    */     public String m_id;
/* 635:    */     public String m_color;
/* 636:    */     public String m_fontColor;
/* 637:    */     public String m_label;
/* 638:    */     public String m_shape;
/* 639:    */     public String m_style;
/* 640:    */     public String m_source;
/* 641:    */     public String m_target;
/* 642:    */     public String m_data;
/* 643:    */     
/* 644:    */     public InfoObject(String i)
/* 645:    */     {
/* 646:718 */       this.m_id = i;
/* 647:719 */       this.m_color = null;
/* 648:720 */       this.m_fontColor = null;
/* 649:721 */       this.m_label = null;
/* 650:722 */       this.m_shape = null;
/* 651:723 */       this.m_style = null;
/* 652:724 */       this.m_source = null;
/* 653:725 */       this.m_target = null;
/* 654:726 */       this.m_data = null;
/* 655:    */     }
/* 656:    */   }
/* 657:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.treevisualizer.TreeBuild
 * JD-Core Version:    0.7.0.1
 */