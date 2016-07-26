/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.BufferedWriter;
/*   5:    */ import java.io.File;
/*   6:    */ import java.io.FileReader;
/*   7:    */ import java.io.FileWriter;
/*   8:    */ import java.io.PrintStream;
/*   9:    */ import java.util.Collections;
/*  10:    */ import java.util.Date;
/*  11:    */ import java.util.Enumeration;
/*  12:    */ import java.util.HashSet;
/*  13:    */ import java.util.Vector;
/*  14:    */ 
/*  15:    */ public class Stopwords
/*  16:    */   implements RevisionHandler
/*  17:    */ {
/*  18: 71 */   protected HashSet<String> m_Words = null;
/*  19:    */   protected static Stopwords m_Stopwords;
/*  20:    */   
/*  21:    */   static
/*  22:    */   {
/*  23: 77 */     if (m_Stopwords == null) {
/*  24: 78 */       m_Stopwords = new Stopwords();
/*  25:    */     }
/*  26:    */   }
/*  27:    */   
/*  28:    */   public Stopwords()
/*  29:    */   {
/*  30: 88 */     this.m_Words = new HashSet();
/*  31:    */     
/*  32:    */ 
/*  33: 91 */     add("a");
/*  34: 92 */     add("able");
/*  35: 93 */     add("about");
/*  36: 94 */     add("above");
/*  37: 95 */     add("according");
/*  38: 96 */     add("accordingly");
/*  39: 97 */     add("across");
/*  40: 98 */     add("actually");
/*  41: 99 */     add("after");
/*  42:100 */     add("afterwards");
/*  43:101 */     add("again");
/*  44:102 */     add("against");
/*  45:103 */     add("all");
/*  46:104 */     add("allow");
/*  47:105 */     add("allows");
/*  48:106 */     add("almost");
/*  49:107 */     add("alone");
/*  50:108 */     add("along");
/*  51:109 */     add("already");
/*  52:110 */     add("also");
/*  53:111 */     add("although");
/*  54:112 */     add("always");
/*  55:113 */     add("am");
/*  56:114 */     add("among");
/*  57:115 */     add("amongst");
/*  58:116 */     add("an");
/*  59:117 */     add("and");
/*  60:118 */     add("another");
/*  61:119 */     add("any");
/*  62:120 */     add("anybody");
/*  63:121 */     add("anyhow");
/*  64:122 */     add("anyone");
/*  65:123 */     add("anything");
/*  66:124 */     add("anyway");
/*  67:125 */     add("anyways");
/*  68:126 */     add("anywhere");
/*  69:127 */     add("apart");
/*  70:128 */     add("appear");
/*  71:129 */     add("appreciate");
/*  72:130 */     add("appropriate");
/*  73:131 */     add("are");
/*  74:132 */     add("around");
/*  75:133 */     add("as");
/*  76:134 */     add("aside");
/*  77:135 */     add("ask");
/*  78:136 */     add("asking");
/*  79:137 */     add("associated");
/*  80:138 */     add("at");
/*  81:139 */     add("available");
/*  82:140 */     add("away");
/*  83:141 */     add("awfully");
/*  84:142 */     add("b");
/*  85:143 */     add("be");
/*  86:144 */     add("became");
/*  87:145 */     add("because");
/*  88:146 */     add("become");
/*  89:147 */     add("becomes");
/*  90:148 */     add("becoming");
/*  91:149 */     add("been");
/*  92:150 */     add("before");
/*  93:151 */     add("beforehand");
/*  94:152 */     add("behind");
/*  95:153 */     add("being");
/*  96:154 */     add("believe");
/*  97:155 */     add("below");
/*  98:156 */     add("beside");
/*  99:157 */     add("besides");
/* 100:158 */     add("best");
/* 101:159 */     add("better");
/* 102:160 */     add("between");
/* 103:161 */     add("beyond");
/* 104:162 */     add("both");
/* 105:163 */     add("brief");
/* 106:164 */     add("but");
/* 107:165 */     add("by");
/* 108:166 */     add("c");
/* 109:167 */     add("came");
/* 110:168 */     add("can");
/* 111:169 */     add("cannot");
/* 112:170 */     add("cant");
/* 113:171 */     add("cause");
/* 114:172 */     add("causes");
/* 115:173 */     add("certain");
/* 116:174 */     add("certainly");
/* 117:175 */     add("changes");
/* 118:176 */     add("clearly");
/* 119:177 */     add("co");
/* 120:178 */     add("com");
/* 121:179 */     add("come");
/* 122:180 */     add("comes");
/* 123:181 */     add("concerning");
/* 124:182 */     add("consequently");
/* 125:183 */     add("consider");
/* 126:184 */     add("considering");
/* 127:185 */     add("contain");
/* 128:186 */     add("containing");
/* 129:187 */     add("contains");
/* 130:188 */     add("corresponding");
/* 131:189 */     add("could");
/* 132:190 */     add("course");
/* 133:191 */     add("currently");
/* 134:192 */     add("d");
/* 135:193 */     add("definitely");
/* 136:194 */     add("described");
/* 137:195 */     add("despite");
/* 138:196 */     add("did");
/* 139:197 */     add("different");
/* 140:198 */     add("do");
/* 141:199 */     add("does");
/* 142:200 */     add("doing");
/* 143:201 */     add("done");
/* 144:202 */     add("down");
/* 145:203 */     add("downwards");
/* 146:204 */     add("during");
/* 147:205 */     add("e");
/* 148:206 */     add("each");
/* 149:207 */     add("edu");
/* 150:208 */     add("eg");
/* 151:209 */     add("eight");
/* 152:210 */     add("either");
/* 153:211 */     add("else");
/* 154:212 */     add("elsewhere");
/* 155:213 */     add("enough");
/* 156:214 */     add("entirely");
/* 157:215 */     add("especially");
/* 158:216 */     add("et");
/* 159:217 */     add("etc");
/* 160:218 */     add("even");
/* 161:219 */     add("ever");
/* 162:220 */     add("every");
/* 163:221 */     add("everybody");
/* 164:222 */     add("everyone");
/* 165:223 */     add("everything");
/* 166:224 */     add("everywhere");
/* 167:225 */     add("ex");
/* 168:226 */     add("exactly");
/* 169:227 */     add("example");
/* 170:228 */     add("except");
/* 171:229 */     add("f");
/* 172:230 */     add("far");
/* 173:231 */     add("few");
/* 174:232 */     add("fifth");
/* 175:233 */     add("first");
/* 176:234 */     add("five");
/* 177:235 */     add("followed");
/* 178:236 */     add("following");
/* 179:237 */     add("follows");
/* 180:238 */     add("for");
/* 181:239 */     add("former");
/* 182:240 */     add("formerly");
/* 183:241 */     add("forth");
/* 184:242 */     add("four");
/* 185:243 */     add("from");
/* 186:244 */     add("further");
/* 187:245 */     add("furthermore");
/* 188:246 */     add("g");
/* 189:247 */     add("get");
/* 190:248 */     add("gets");
/* 191:249 */     add("getting");
/* 192:250 */     add("given");
/* 193:251 */     add("gives");
/* 194:252 */     add("go");
/* 195:253 */     add("goes");
/* 196:254 */     add("going");
/* 197:255 */     add("gone");
/* 198:256 */     add("got");
/* 199:257 */     add("gotten");
/* 200:258 */     add("greetings");
/* 201:259 */     add("h");
/* 202:260 */     add("had");
/* 203:261 */     add("happens");
/* 204:262 */     add("hardly");
/* 205:263 */     add("has");
/* 206:264 */     add("have");
/* 207:265 */     add("having");
/* 208:266 */     add("he");
/* 209:267 */     add("hello");
/* 210:268 */     add("help");
/* 211:269 */     add("hence");
/* 212:270 */     add("her");
/* 213:271 */     add("here");
/* 214:272 */     add("hereafter");
/* 215:273 */     add("hereby");
/* 216:274 */     add("herein");
/* 217:275 */     add("hereupon");
/* 218:276 */     add("hers");
/* 219:277 */     add("herself");
/* 220:278 */     add("hi");
/* 221:279 */     add("him");
/* 222:280 */     add("himself");
/* 223:281 */     add("his");
/* 224:282 */     add("hither");
/* 225:283 */     add("hopefully");
/* 226:284 */     add("how");
/* 227:285 */     add("howbeit");
/* 228:286 */     add("however");
/* 229:287 */     add("i");
/* 230:288 */     add("ie");
/* 231:289 */     add("if");
/* 232:290 */     add("ignored");
/* 233:291 */     add("immediate");
/* 234:292 */     add("in");
/* 235:293 */     add("inasmuch");
/* 236:294 */     add("inc");
/* 237:295 */     add("indeed");
/* 238:296 */     add("indicate");
/* 239:297 */     add("indicated");
/* 240:298 */     add("indicates");
/* 241:299 */     add("inner");
/* 242:300 */     add("insofar");
/* 243:301 */     add("instead");
/* 244:302 */     add("into");
/* 245:303 */     add("inward");
/* 246:304 */     add("is");
/* 247:305 */     add("it");
/* 248:306 */     add("its");
/* 249:307 */     add("itself");
/* 250:308 */     add("j");
/* 251:309 */     add("just");
/* 252:310 */     add("k");
/* 253:311 */     add("keep");
/* 254:312 */     add("keeps");
/* 255:313 */     add("kept");
/* 256:314 */     add("know");
/* 257:315 */     add("knows");
/* 258:316 */     add("known");
/* 259:317 */     add("l");
/* 260:318 */     add("last");
/* 261:319 */     add("lately");
/* 262:320 */     add("later");
/* 263:321 */     add("latter");
/* 264:322 */     add("latterly");
/* 265:323 */     add("least");
/* 266:324 */     add("less");
/* 267:325 */     add("lest");
/* 268:326 */     add("let");
/* 269:327 */     add("like");
/* 270:328 */     add("liked");
/* 271:329 */     add("likely");
/* 272:330 */     add("little");
/* 273:331 */     add("ll");
/* 274:332 */     add("look");
/* 275:333 */     add("looking");
/* 276:334 */     add("looks");
/* 277:335 */     add("ltd");
/* 278:336 */     add("m");
/* 279:337 */     add("mainly");
/* 280:338 */     add("many");
/* 281:339 */     add("may");
/* 282:340 */     add("maybe");
/* 283:341 */     add("me");
/* 284:342 */     add("mean");
/* 285:343 */     add("meanwhile");
/* 286:344 */     add("merely");
/* 287:345 */     add("might");
/* 288:346 */     add("more");
/* 289:347 */     add("moreover");
/* 290:348 */     add("most");
/* 291:349 */     add("mostly");
/* 292:350 */     add("much");
/* 293:351 */     add("must");
/* 294:352 */     add("my");
/* 295:353 */     add("myself");
/* 296:354 */     add("n");
/* 297:355 */     add("name");
/* 298:356 */     add("namely");
/* 299:357 */     add("nd");
/* 300:358 */     add("near");
/* 301:359 */     add("nearly");
/* 302:360 */     add("necessary");
/* 303:361 */     add("need");
/* 304:362 */     add("needs");
/* 305:363 */     add("neither");
/* 306:364 */     add("never");
/* 307:365 */     add("nevertheless");
/* 308:366 */     add("new");
/* 309:367 */     add("next");
/* 310:368 */     add("nine");
/* 311:369 */     add("no");
/* 312:370 */     add("nobody");
/* 313:371 */     add("non");
/* 314:372 */     add("none");
/* 315:373 */     add("noone");
/* 316:374 */     add("nor");
/* 317:375 */     add("normally");
/* 318:376 */     add("not");
/* 319:377 */     add("nothing");
/* 320:378 */     add("novel");
/* 321:379 */     add("now");
/* 322:380 */     add("nowhere");
/* 323:381 */     add("o");
/* 324:382 */     add("obviously");
/* 325:383 */     add("of");
/* 326:384 */     add("off");
/* 327:385 */     add("often");
/* 328:386 */     add("oh");
/* 329:387 */     add("ok");
/* 330:388 */     add("okay");
/* 331:389 */     add("old");
/* 332:390 */     add("on");
/* 333:391 */     add("once");
/* 334:392 */     add("one");
/* 335:393 */     add("ones");
/* 336:394 */     add("only");
/* 337:395 */     add("onto");
/* 338:396 */     add("or");
/* 339:397 */     add("other");
/* 340:398 */     add("others");
/* 341:399 */     add("otherwise");
/* 342:400 */     add("ought");
/* 343:401 */     add("our");
/* 344:402 */     add("ours");
/* 345:403 */     add("ourselves");
/* 346:404 */     add("out");
/* 347:405 */     add("outside");
/* 348:406 */     add("over");
/* 349:407 */     add("overall");
/* 350:408 */     add("own");
/* 351:409 */     add("p");
/* 352:410 */     add("particular");
/* 353:411 */     add("particularly");
/* 354:412 */     add("per");
/* 355:413 */     add("perhaps");
/* 356:414 */     add("placed");
/* 357:415 */     add("please");
/* 358:416 */     add("plus");
/* 359:417 */     add("possible");
/* 360:418 */     add("presumably");
/* 361:419 */     add("probably");
/* 362:420 */     add("provides");
/* 363:421 */     add("q");
/* 364:422 */     add("que");
/* 365:423 */     add("quite");
/* 366:424 */     add("qv");
/* 367:425 */     add("r");
/* 368:426 */     add("rather");
/* 369:427 */     add("rd");
/* 370:428 */     add("re");
/* 371:429 */     add("really");
/* 372:430 */     add("reasonably");
/* 373:431 */     add("regarding");
/* 374:432 */     add("regardless");
/* 375:433 */     add("regards");
/* 376:434 */     add("relatively");
/* 377:435 */     add("respectively");
/* 378:436 */     add("right");
/* 379:437 */     add("s");
/* 380:438 */     add("said");
/* 381:439 */     add("same");
/* 382:440 */     add("saw");
/* 383:441 */     add("say");
/* 384:442 */     add("saying");
/* 385:443 */     add("says");
/* 386:444 */     add("second");
/* 387:445 */     add("secondly");
/* 388:446 */     add("see");
/* 389:447 */     add("seeing");
/* 390:448 */     add("seem");
/* 391:449 */     add("seemed");
/* 392:450 */     add("seeming");
/* 393:451 */     add("seems");
/* 394:452 */     add("seen");
/* 395:453 */     add("self");
/* 396:454 */     add("selves");
/* 397:455 */     add("sensible");
/* 398:456 */     add("sent");
/* 399:457 */     add("serious");
/* 400:458 */     add("seriously");
/* 401:459 */     add("seven");
/* 402:460 */     add("several");
/* 403:461 */     add("shall");
/* 404:462 */     add("she");
/* 405:463 */     add("should");
/* 406:464 */     add("since");
/* 407:465 */     add("six");
/* 408:466 */     add("so");
/* 409:467 */     add("some");
/* 410:468 */     add("somebody");
/* 411:469 */     add("somehow");
/* 412:470 */     add("someone");
/* 413:471 */     add("something");
/* 414:472 */     add("sometime");
/* 415:473 */     add("sometimes");
/* 416:474 */     add("somewhat");
/* 417:475 */     add("somewhere");
/* 418:476 */     add("soon");
/* 419:477 */     add("sorry");
/* 420:478 */     add("specified");
/* 421:479 */     add("specify");
/* 422:480 */     add("specifying");
/* 423:481 */     add("still");
/* 424:482 */     add("sub");
/* 425:483 */     add("such");
/* 426:484 */     add("sup");
/* 427:485 */     add("sure");
/* 428:486 */     add("t");
/* 429:487 */     add("take");
/* 430:488 */     add("taken");
/* 431:489 */     add("tell");
/* 432:490 */     add("tends");
/* 433:491 */     add("th");
/* 434:492 */     add("than");
/* 435:493 */     add("thank");
/* 436:494 */     add("thanks");
/* 437:495 */     add("thanx");
/* 438:496 */     add("that");
/* 439:497 */     add("thats");
/* 440:498 */     add("the");
/* 441:499 */     add("their");
/* 442:500 */     add("theirs");
/* 443:501 */     add("them");
/* 444:502 */     add("themselves");
/* 445:503 */     add("then");
/* 446:504 */     add("thence");
/* 447:505 */     add("there");
/* 448:506 */     add("thereafter");
/* 449:507 */     add("thereby");
/* 450:508 */     add("therefore");
/* 451:509 */     add("therein");
/* 452:510 */     add("theres");
/* 453:511 */     add("thereupon");
/* 454:512 */     add("these");
/* 455:513 */     add("they");
/* 456:514 */     add("think");
/* 457:515 */     add("third");
/* 458:516 */     add("this");
/* 459:517 */     add("thorough");
/* 460:518 */     add("thoroughly");
/* 461:519 */     add("those");
/* 462:520 */     add("though");
/* 463:521 */     add("three");
/* 464:522 */     add("through");
/* 465:523 */     add("throughout");
/* 466:524 */     add("thru");
/* 467:525 */     add("thus");
/* 468:526 */     add("to");
/* 469:527 */     add("together");
/* 470:528 */     add("too");
/* 471:529 */     add("took");
/* 472:530 */     add("toward");
/* 473:531 */     add("towards");
/* 474:532 */     add("tried");
/* 475:533 */     add("tries");
/* 476:534 */     add("truly");
/* 477:535 */     add("try");
/* 478:536 */     add("trying");
/* 479:537 */     add("twice");
/* 480:538 */     add("two");
/* 481:539 */     add("u");
/* 482:540 */     add("un");
/* 483:541 */     add("under");
/* 484:542 */     add("unfortunately");
/* 485:543 */     add("unless");
/* 486:544 */     add("unlikely");
/* 487:545 */     add("until");
/* 488:546 */     add("unto");
/* 489:547 */     add("up");
/* 490:548 */     add("upon");
/* 491:549 */     add("us");
/* 492:550 */     add("use");
/* 493:551 */     add("used");
/* 494:552 */     add("useful");
/* 495:553 */     add("uses");
/* 496:554 */     add("using");
/* 497:555 */     add("usually");
/* 498:556 */     add("uucp");
/* 499:557 */     add("v");
/* 500:558 */     add("value");
/* 501:559 */     add("various");
/* 502:560 */     add("ve");
/* 503:561 */     add("very");
/* 504:562 */     add("via");
/* 505:563 */     add("viz");
/* 506:564 */     add("vs");
/* 507:565 */     add("w");
/* 508:566 */     add("want");
/* 509:567 */     add("wants");
/* 510:568 */     add("was");
/* 511:569 */     add("way");
/* 512:570 */     add("we");
/* 513:571 */     add("welcome");
/* 514:572 */     add("well");
/* 515:573 */     add("went");
/* 516:574 */     add("were");
/* 517:575 */     add("what");
/* 518:576 */     add("whatever");
/* 519:577 */     add("when");
/* 520:578 */     add("whence");
/* 521:579 */     add("whenever");
/* 522:580 */     add("where");
/* 523:581 */     add("whereafter");
/* 524:582 */     add("whereas");
/* 525:583 */     add("whereby");
/* 526:584 */     add("wherein");
/* 527:585 */     add("whereupon");
/* 528:586 */     add("wherever");
/* 529:587 */     add("whether");
/* 530:588 */     add("which");
/* 531:589 */     add("while");
/* 532:590 */     add("whither");
/* 533:591 */     add("who");
/* 534:592 */     add("whoever");
/* 535:593 */     add("whole");
/* 536:594 */     add("whom");
/* 537:595 */     add("whose");
/* 538:596 */     add("why");
/* 539:597 */     add("will");
/* 540:598 */     add("willing");
/* 541:599 */     add("wish");
/* 542:600 */     add("with");
/* 543:601 */     add("within");
/* 544:602 */     add("without");
/* 545:603 */     add("wonder");
/* 546:604 */     add("would");
/* 547:605 */     add("would");
/* 548:606 */     add("x");
/* 549:607 */     add("y");
/* 550:608 */     add("yes");
/* 551:609 */     add("yet");
/* 552:610 */     add("you");
/* 553:611 */     add("your");
/* 554:612 */     add("yours");
/* 555:613 */     add("yourself");
/* 556:614 */     add("yourselves");
/* 557:615 */     add("z");
/* 558:616 */     add("zero");
/* 559:    */   }
/* 560:    */   
/* 561:    */   public void clear()
/* 562:    */   {
/* 563:623 */     this.m_Words.clear();
/* 564:    */   }
/* 565:    */   
/* 566:    */   public void add(String word)
/* 567:    */   {
/* 568:633 */     if (word.trim().length() > 0) {
/* 569:634 */       this.m_Words.add(word.trim().toLowerCase());
/* 570:    */     }
/* 571:    */   }
/* 572:    */   
/* 573:    */   public boolean remove(String word)
/* 574:    */   {
/* 575:645 */     return this.m_Words.remove(word);
/* 576:    */   }
/* 577:    */   
/* 578:    */   public boolean is(String word)
/* 579:    */   {
/* 580:655 */     return this.m_Words.contains(word.toLowerCase());
/* 581:    */   }
/* 582:    */   
/* 583:    */   public Enumeration<String> elements()
/* 584:    */   {
/* 585:665 */     Vector<String> list = new Vector();
/* 586:    */     
/* 587:667 */     list.addAll(this.m_Words);
/* 588:    */     
/* 589:    */ 
/* 590:670 */     Collections.sort(list);
/* 591:    */     
/* 592:672 */     return list.elements();
/* 593:    */   }
/* 594:    */   
/* 595:    */   public void read(String filename)
/* 596:    */     throws Exception
/* 597:    */   {
/* 598:682 */     read(new File(filename));
/* 599:    */   }
/* 600:    */   
/* 601:    */   public void read(File file)
/* 602:    */     throws Exception
/* 603:    */   {
/* 604:692 */     read(new BufferedReader(new FileReader(file)));
/* 605:    */   }
/* 606:    */   
/* 607:    */   public void read(BufferedReader reader)
/* 608:    */     throws Exception
/* 609:    */   {
/* 610:705 */     clear();
/* 611:    */     String line;
/* 612:707 */     while ((line = reader.readLine()) != null)
/* 613:    */     {
/* 614:708 */       line = line.trim();
/* 615:710 */       if (!line.startsWith("#")) {
/* 616:713 */         add(line);
/* 617:    */       }
/* 618:    */     }
/* 619:716 */     reader.close();
/* 620:    */   }
/* 621:    */   
/* 622:    */   public void write(String filename)
/* 623:    */     throws Exception
/* 624:    */   {
/* 625:726 */     write(new File(filename));
/* 626:    */   }
/* 627:    */   
/* 628:    */   public void write(File file)
/* 629:    */     throws Exception
/* 630:    */   {
/* 631:736 */     write(new BufferedWriter(new FileWriter(file)));
/* 632:    */   }
/* 633:    */   
/* 634:    */   public void write(BufferedWriter writer)
/* 635:    */     throws Exception
/* 636:    */   {
/* 637:750 */     writer.write("# generated " + new Date());
/* 638:751 */     writer.newLine();
/* 639:    */     
/* 640:753 */     Enumeration<String> enm = elements();
/* 641:755 */     while (enm.hasMoreElements())
/* 642:    */     {
/* 643:756 */       writer.write(((String)enm.nextElement()).toString());
/* 644:757 */       writer.newLine();
/* 645:    */     }
/* 646:760 */     writer.flush();
/* 647:761 */     writer.close();
/* 648:    */   }
/* 649:    */   
/* 650:    */   public String toString()
/* 651:    */   {
/* 652:774 */     StringBuffer result = new StringBuffer();
/* 653:775 */     Enumeration<String> enm = elements();
/* 654:776 */     while (enm.hasMoreElements())
/* 655:    */     {
/* 656:777 */       result.append(((String)enm.nextElement()).toString());
/* 657:778 */       if (enm.hasMoreElements()) {
/* 658:779 */         result.append(",");
/* 659:    */       }
/* 660:    */     }
/* 661:783 */     return result.toString();
/* 662:    */   }
/* 663:    */   
/* 664:    */   public static boolean isStopword(String str)
/* 665:    */   {
/* 666:793 */     return m_Stopwords.is(str.toLowerCase());
/* 667:    */   }
/* 668:    */   
/* 669:    */   public String getRevision()
/* 670:    */   {
/* 671:803 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 672:    */   }
/* 673:    */   
/* 674:    */   public static void main(String[] args)
/* 675:    */     throws Exception
/* 676:    */   {
/* 677:828 */     String input = Utils.getOption('i', args);
/* 678:829 */     String output = Utils.getOption('o', args);
/* 679:830 */     boolean print = Utils.getFlag('p', args);
/* 680:    */     
/* 681:    */ 
/* 682:833 */     Vector<String> words = new Vector();
/* 683:834 */     for (String arg : args) {
/* 684:835 */       if (arg.trim().length() > 0) {
/* 685:836 */         words.add(arg.trim());
/* 686:    */       }
/* 687:    */     }
/* 688:840 */     Stopwords stopwords = new Stopwords();
/* 689:843 */     if (input.length() != 0) {
/* 690:844 */       stopwords.read(input);
/* 691:    */     }
/* 692:848 */     if (output.length() != 0) {
/* 693:849 */       stopwords.write(output);
/* 694:    */     }
/* 695:853 */     if (print)
/* 696:    */     {
/* 697:854 */       System.out.println("\nStopwords:");
/* 698:855 */       Enumeration<String> enm = stopwords.elements();
/* 699:856 */       int i = 0;
/* 700:857 */       while (enm.hasMoreElements())
/* 701:    */       {
/* 702:858 */         System.out.println(i + 1 + ". " + (String)enm.nextElement());
/* 703:859 */         i++;
/* 704:    */       }
/* 705:    */     }
/* 706:864 */     if (words.size() > 0)
/* 707:    */     {
/* 708:865 */       System.out.println("\nChecking for stopwords:");
/* 709:866 */       for (int i = 0; i < words.size(); i++) {
/* 710:867 */         System.out.println(i + 1 + ". " + (String)words.get(i) + ": " + stopwords.is(((String)words.get(i)).toString()));
/* 711:    */       }
/* 712:    */     }
/* 713:    */   }
/* 714:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Stopwords
 * JD-Core Version:    0.7.0.1
 */