/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.lang.reflect.Array;
/*   6:    */ import java.util.Collection;
/*   7:    */ import java.util.Enumeration;
/*   8:    */ import java.util.Hashtable;
/*   9:    */ import java.util.Iterator;
/*  10:    */ import java.util.Vector;
/*  11:    */ import javax.swing.tree.DefaultMutableTreeNode;
/*  12:    */ 
/*  13:    */ public class Trie
/*  14:    */   implements Serializable, Cloneable, Collection<String>, RevisionHandler
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = -5897980928817779048L;
/*  17:    */   protected TrieNode m_Root;
/*  18:    */   protected int m_HashCode;
/*  19:    */   protected boolean m_RecalcHashCode;
/*  20:    */   
/*  21:    */   public static class TrieNode
/*  22:    */     extends DefaultMutableTreeNode
/*  23:    */     implements RevisionHandler
/*  24:    */   {
/*  25:    */     private static final long serialVersionUID = -2252907099391881148L;
/*  26: 60 */     public static final Character STOP = Character.valueOf('\000');
/*  27:    */     protected Hashtable<Character, TrieNode> m_Children;
/*  28:    */     
/*  29:    */     public TrieNode(char c)
/*  30:    */     {
/*  31: 71 */       this(new Character(c));
/*  32:    */     }
/*  33:    */     
/*  34:    */     public TrieNode(Character c)
/*  35:    */     {
/*  36: 80 */       super();
/*  37:    */       
/*  38: 82 */       this.m_Children = new Hashtable(100);
/*  39:    */     }
/*  40:    */     
/*  41:    */     public Character getChar()
/*  42:    */     {
/*  43: 91 */       return (Character)getUserObject();
/*  44:    */     }
/*  45:    */     
/*  46:    */     public void setChar(Character value)
/*  47:    */     {
/*  48:100 */       setUserObject(value);
/*  49:    */     }
/*  50:    */     
/*  51:    */     public boolean add(String suffix)
/*  52:    */     {
/*  53:115 */       boolean result = false;
/*  54:116 */       Character c = Character.valueOf(suffix.charAt(0));
/*  55:117 */       String newSuffix = suffix.substring(1);
/*  56:    */       
/*  57:    */ 
/*  58:120 */       TrieNode child = (TrieNode)this.m_Children.get(c);
/*  59:121 */       if (child == null)
/*  60:    */       {
/*  61:122 */         result = true;
/*  62:123 */         child = add(c);
/*  63:    */       }
/*  64:127 */       if (newSuffix.length() > 0) {
/*  65:128 */         result = (child.add(newSuffix)) || (result);
/*  66:    */       }
/*  67:131 */       return result;
/*  68:    */     }
/*  69:    */     
/*  70:    */     protected TrieNode add(Character c)
/*  71:    */     {
/*  72:143 */       TrieNode child = new TrieNode(c);
/*  73:144 */       add(child);
/*  74:145 */       this.m_Children.put(c, child);
/*  75:    */       
/*  76:147 */       return child;
/*  77:    */     }
/*  78:    */     
/*  79:    */     protected void remove(Character c)
/*  80:    */     {
/*  81:158 */       TrieNode child = (TrieNode)this.m_Children.get(c);
/*  82:159 */       remove(child);
/*  83:160 */       this.m_Children.remove(c);
/*  84:    */     }
/*  85:    */     
/*  86:    */     public boolean remove(String suffix)
/*  87:    */     {
/*  88:175 */       Character c = Character.valueOf(suffix.charAt(0));
/*  89:176 */       String newSuffix = suffix.substring(1);
/*  90:177 */       TrieNode child = (TrieNode)this.m_Children.get(c);
/*  91:    */       boolean result;
/*  92:    */       boolean result;
/*  93:179 */       if (child == null)
/*  94:    */       {
/*  95:180 */         result = false;
/*  96:    */       }
/*  97:    */       else
/*  98:    */       {
/*  99:    */         boolean result;
/* 100:181 */         if (newSuffix.length() == 0)
/* 101:    */         {
/* 102:182 */           remove(c);
/* 103:183 */           result = true;
/* 104:    */         }
/* 105:    */         else
/* 106:    */         {
/* 107:185 */           result = child.remove(newSuffix);
/* 108:186 */           if (child.getChildCount() == 0) {
/* 109:187 */             remove(child.getChar());
/* 110:    */           }
/* 111:    */         }
/* 112:    */       }
/* 113:191 */       return result;
/* 114:    */     }
/* 115:    */     
/* 116:    */     public boolean contains(String suffix)
/* 117:    */     {
/* 118:206 */       Character c = Character.valueOf(suffix.charAt(0));
/* 119:207 */       String newSuffix = suffix.substring(1);
/* 120:208 */       TrieNode child = (TrieNode)this.m_Children.get(c);
/* 121:    */       boolean result;
/* 122:    */       boolean result;
/* 123:210 */       if (child == null)
/* 124:    */       {
/* 125:211 */         result = false;
/* 126:    */       }
/* 127:    */       else
/* 128:    */       {
/* 129:    */         boolean result;
/* 130:212 */         if (newSuffix.length() == 0) {
/* 131:213 */           result = true;
/* 132:    */         } else {
/* 133:215 */           result = child.contains(newSuffix);
/* 134:    */         }
/* 135:    */       }
/* 136:218 */       return result;
/* 137:    */     }
/* 138:    */     
/* 139:    */     public Object clone()
/* 140:    */     {
/* 141:233 */       TrieNode result = new TrieNode(getChar());
/* 142:234 */       Enumeration<Character> keys = this.m_Children.keys();
/* 143:235 */       while (keys.hasMoreElements())
/* 144:    */       {
/* 145:236 */         Character key = (Character)keys.nextElement();
/* 146:237 */         TrieNode child = (TrieNode)((TrieNode)this.m_Children.get(key)).clone();
/* 147:238 */         result.add(child);
/* 148:239 */         result.m_Children.put(key, child);
/* 149:    */       }
/* 150:242 */       return result;
/* 151:    */     }
/* 152:    */     
/* 153:    */     public boolean equals(Object obj)
/* 154:    */     {
/* 155:258 */       TrieNode node = (TrieNode)obj;
/* 156:    */       boolean result;
/* 157:    */       boolean result;
/* 158:261 */       if (getChar() == null) {
/* 159:262 */         result = node.getChar() == null;
/* 160:    */       } else {
/* 161:264 */         result = getChar().equals(node.getChar());
/* 162:    */       }
/* 163:268 */       if (result)
/* 164:    */       {
/* 165:269 */         Enumeration<Character> keys = this.m_Children.keys();
/* 166:270 */         while (keys.hasMoreElements())
/* 167:    */         {
/* 168:271 */           Character key = (Character)keys.nextElement();
/* 169:272 */           result = ((TrieNode)this.m_Children.get(key)).equals(node.m_Children.get(key));
/* 170:273 */           if (!result) {
/* 171:    */             break;
/* 172:    */           }
/* 173:    */         }
/* 174:    */       }
/* 175:279 */       return result;
/* 176:    */     }
/* 177:    */     
/* 178:    */     public TrieNode find(String suffix)
/* 179:    */     {
/* 180:294 */       Character c = Character.valueOf(suffix.charAt(0));
/* 181:295 */       String newSuffix = suffix.substring(1);
/* 182:296 */       TrieNode child = (TrieNode)this.m_Children.get(c);
/* 183:    */       TrieNode result;
/* 184:    */       TrieNode result;
/* 185:298 */       if (child == null)
/* 186:    */       {
/* 187:299 */         result = null;
/* 188:    */       }
/* 189:    */       else
/* 190:    */       {
/* 191:    */         TrieNode result;
/* 192:300 */         if (newSuffix.length() == 0) {
/* 193:301 */           result = child;
/* 194:    */         } else {
/* 195:303 */           result = child.find(newSuffix);
/* 196:    */         }
/* 197:    */       }
/* 198:306 */       return result;
/* 199:    */     }
/* 200:    */     
/* 201:    */     public String getCommonPrefix()
/* 202:    */     {
/* 203:316 */       return getCommonPrefix("");
/* 204:    */     }
/* 205:    */     
/* 206:    */     public String getCommonPrefix(String startPrefix)
/* 207:    */     {
/* 208:    */       TrieNode startNode;
/* 209:    */       TrieNode startNode;
/* 210:332 */       if (startPrefix.length() == 0) {
/* 211:333 */         startNode = this;
/* 212:    */       } else {
/* 213:335 */         startNode = find(startPrefix);
/* 214:    */       }
/* 215:    */       String result;
/* 216:    */       String result;
/* 217:338 */       if (startNode == null) {
/* 218:339 */         result = null;
/* 219:    */       } else {
/* 220:341 */         result = startPrefix + startNode.determineCommonPrefix("");
/* 221:    */       }
/* 222:344 */       return result;
/* 223:    */     }
/* 224:    */     
/* 225:    */     protected String determineCommonPrefix(String currentPrefix)
/* 226:    */     {
/* 227:    */       String newPrefix;
/* 228:    */       String newPrefix;
/* 229:357 */       if ((!isRoot()) && (getChar() != STOP)) {
/* 230:358 */         newPrefix = currentPrefix + getChar();
/* 231:    */       } else {
/* 232:360 */         newPrefix = currentPrefix;
/* 233:    */       }
/* 234:    */       String result;
/* 235:    */       String result;
/* 236:363 */       if (this.m_Children.size() == 1) {
/* 237:364 */         result = ((TrieNode)getChildAt(0)).determineCommonPrefix(newPrefix);
/* 238:    */       } else {
/* 239:366 */         result = newPrefix;
/* 240:    */       }
/* 241:369 */       return result;
/* 242:    */     }
/* 243:    */     
/* 244:    */     public int size()
/* 245:    */     {
/* 246:381 */       int result = 0;
/* 247:382 */       TrieNode leaf = (TrieNode)getFirstLeaf();
/* 248:383 */       while (leaf != null)
/* 249:    */       {
/* 250:384 */         if (leaf != getRoot()) {
/* 251:385 */           result++;
/* 252:    */         }
/* 253:387 */         leaf = (TrieNode)leaf.getNextLeaf();
/* 254:    */       }
/* 255:390 */       return result;
/* 256:    */     }
/* 257:    */     
/* 258:    */     public String getString()
/* 259:    */     {
/* 260:402 */       char[] result = new char[getLevel()];
/* 261:403 */       TrieNode node = this;
/* 262:404 */       while ((node.getParent() != null) && 
/* 263:405 */         (!node.isRoot()))
/* 264:    */       {
/* 265:408 */         result[(node.getLevel() - 1)] = node.getChar().charValue();
/* 266:    */         
/* 267:410 */         node = (TrieNode)node.getParent();
/* 268:    */       }
/* 269:413 */       return new String(result);
/* 270:    */     }
/* 271:    */     
/* 272:    */     public String toString()
/* 273:    */     {
/* 274:423 */       return "" + getChar();
/* 275:    */     }
/* 276:    */     
/* 277:    */     public String getRevision()
/* 278:    */     {
/* 279:433 */       return RevisionUtils.extract("$Revision: 10203 $");
/* 280:    */     }
/* 281:    */   }
/* 282:    */   
/* 283:    */   public static class TrieIterator
/* 284:    */     implements Iterator<String>, RevisionHandler
/* 285:    */   {
/* 286:    */     protected Trie.TrieNode m_Root;
/* 287:    */     protected Trie.TrieNode m_LastLeaf;
/* 288:    */     protected Trie.TrieNode m_CurrentLeaf;
/* 289:    */     
/* 290:    */     public TrieIterator(Trie.TrieNode node)
/* 291:    */     {
/* 292:462 */       this.m_Root = node;
/* 293:463 */       this.m_CurrentLeaf = ((Trie.TrieNode)this.m_Root.getFirstLeaf());
/* 294:464 */       this.m_LastLeaf = ((Trie.TrieNode)this.m_Root.getLastLeaf());
/* 295:    */     }
/* 296:    */     
/* 297:    */     public boolean hasNext()
/* 298:    */     {
/* 299:474 */       return this.m_CurrentLeaf != null;
/* 300:    */     }
/* 301:    */     
/* 302:    */     public String next()
/* 303:    */     {
/* 304:486 */       String result = this.m_CurrentLeaf.getString();
/* 305:487 */       result = result.substring(0, result.length() - 1);
/* 306:488 */       if (this.m_CurrentLeaf != this.m_LastLeaf) {
/* 307:489 */         this.m_CurrentLeaf = ((Trie.TrieNode)this.m_CurrentLeaf.getNextLeaf());
/* 308:    */       } else {
/* 309:491 */         this.m_CurrentLeaf = null;
/* 310:    */       }
/* 311:494 */       return result;
/* 312:    */     }
/* 313:    */     
/* 314:    */     public void remove() {}
/* 315:    */     
/* 316:    */     public String getRevision()
/* 317:    */     {
/* 318:511 */       return RevisionUtils.extract("$Revision: 10203 $");
/* 319:    */     }
/* 320:    */   }
/* 321:    */   
/* 322:    */   public Trie()
/* 323:    */   {
/* 324:533 */     this.m_Root = new TrieNode(null);
/* 325:534 */     this.m_RecalcHashCode = true;
/* 326:    */   }
/* 327:    */   
/* 328:    */   public boolean add(String o)
/* 329:    */   {
/* 330:545 */     return this.m_Root.add(o + TrieNode.STOP);
/* 331:    */   }
/* 332:    */   
/* 333:    */   public boolean addAll(Collection<? extends String> c)
/* 334:    */   {
/* 335:558 */     boolean result = false;
/* 336:    */     
/* 337:560 */     Iterator<? extends String> iter = c.iterator();
/* 338:561 */     while (iter.hasNext()) {
/* 339:562 */       result = (add((String)iter.next())) || (result);
/* 340:    */     }
/* 341:565 */     return result;
/* 342:    */   }
/* 343:    */   
/* 344:    */   public void clear()
/* 345:    */   {
/* 346:573 */     this.m_Root.removeAllChildren();
/* 347:574 */     this.m_RecalcHashCode = true;
/* 348:    */   }
/* 349:    */   
/* 350:    */   public Object clone()
/* 351:    */   {
/* 352:586 */     Trie result = new Trie();
/* 353:587 */     result.m_Root = ((TrieNode)this.m_Root.clone());
/* 354:    */     
/* 355:589 */     return result;
/* 356:    */   }
/* 357:    */   
/* 358:    */   public boolean contains(Object o)
/* 359:    */   {
/* 360:600 */     return this.m_Root.contains((String)o + TrieNode.STOP);
/* 361:    */   }
/* 362:    */   
/* 363:    */   public boolean containsAll(Collection<?> c)
/* 364:    */   {
/* 365:615 */     boolean result = true;
/* 366:    */     
/* 367:617 */     Iterator<?> iter = c.iterator();
/* 368:618 */     while (iter.hasNext()) {
/* 369:619 */       if (!contains(iter.next())) {
/* 370:620 */         result = false;
/* 371:    */       }
/* 372:    */     }
/* 373:625 */     return result;
/* 374:    */   }
/* 375:    */   
/* 376:    */   public boolean containsPrefix(String prefix)
/* 377:    */   {
/* 378:635 */     return this.m_Root.contains(prefix);
/* 379:    */   }
/* 380:    */   
/* 381:    */   public boolean equals(Object o)
/* 382:    */   {
/* 383:645 */     return this.m_Root.equals(((Trie)o).getRoot());
/* 384:    */   }
/* 385:    */   
/* 386:    */   public String getCommonPrefix()
/* 387:    */   {
/* 388:654 */     return this.m_Root.getCommonPrefix();
/* 389:    */   }
/* 390:    */   
/* 391:    */   public TrieNode getRoot()
/* 392:    */   {
/* 393:663 */     return this.m_Root;
/* 394:    */   }
/* 395:    */   
/* 396:    */   public Vector<String> getWithPrefix(String prefix)
/* 397:    */   {
/* 398:677 */     Vector<String> result = new Vector();
/* 399:679 */     if (containsPrefix(prefix))
/* 400:    */     {
/* 401:680 */       TrieNode node = this.m_Root.find(prefix);
/* 402:681 */       TrieIterator iter = new TrieIterator(node);
/* 403:682 */       while (iter.hasNext()) {
/* 404:683 */         result.add(iter.next());
/* 405:    */       }
/* 406:    */     }
/* 407:687 */     return result;
/* 408:    */   }
/* 409:    */   
/* 410:    */   public int hashCode()
/* 411:    */   {
/* 412:697 */     if (this.m_RecalcHashCode)
/* 413:    */     {
/* 414:698 */       this.m_HashCode = toString().hashCode();
/* 415:699 */       this.m_RecalcHashCode = false;
/* 416:    */     }
/* 417:702 */     return this.m_HashCode;
/* 418:    */   }
/* 419:    */   
/* 420:    */   public boolean isEmpty()
/* 421:    */   {
/* 422:712 */     return this.m_Root.getChildCount() == 0;
/* 423:    */   }
/* 424:    */   
/* 425:    */   public Iterator<String> iterator()
/* 426:    */   {
/* 427:722 */     return new TrieIterator(this.m_Root);
/* 428:    */   }
/* 429:    */   
/* 430:    */   public boolean remove(Object o)
/* 431:    */   {
/* 432:736 */     boolean result = this.m_Root.remove((String)o + TrieNode.STOP);
/* 433:    */     
/* 434:738 */     this.m_RecalcHashCode = result;
/* 435:    */     
/* 436:740 */     return result;
/* 437:    */   }
/* 438:    */   
/* 439:    */   public boolean removeAll(Collection<?> c)
/* 440:    */   {
/* 441:755 */     boolean result = false;
/* 442:    */     
/* 443:757 */     Iterator<?> iter = c.iterator();
/* 444:758 */     while (iter.hasNext()) {
/* 445:759 */       result = (remove(iter.next())) || (result);
/* 446:    */     }
/* 447:762 */     this.m_RecalcHashCode = result;
/* 448:    */     
/* 449:764 */     return result;
/* 450:    */   }
/* 451:    */   
/* 452:    */   public boolean retainAll(Collection<?> c)
/* 453:    */   {
/* 454:780 */     boolean result = false;
/* 455:781 */     Iterator<?> iter = iterator();
/* 456:782 */     while (iter.hasNext())
/* 457:    */     {
/* 458:783 */       Object o = iter.next();
/* 459:784 */       if (!c.contains(o)) {
/* 460:785 */         result = (remove(o)) || (result);
/* 461:    */       }
/* 462:    */     }
/* 463:789 */     this.m_RecalcHashCode = result;
/* 464:    */     
/* 465:791 */     return result;
/* 466:    */   }
/* 467:    */   
/* 468:    */   public int size()
/* 469:    */   {
/* 470:801 */     return this.m_Root.size();
/* 471:    */   }
/* 472:    */   
/* 473:    */   public Object[] toArray()
/* 474:    */   {
/* 475:811 */     return toArray(new String[0]);
/* 476:    */   }
/* 477:    */   
/* 478:    */   public <T> T[] toArray(T[] a)
/* 479:    */   {
/* 480:829 */     Vector<T> list = new Vector();
/* 481:830 */     Iterator<T> iter = (Iterator)Utils.cast(iterator());
/* 482:831 */     while (iter.hasNext()) {
/* 483:832 */       list.add(iter.next());
/* 484:    */     }
/* 485:    */     T[] result;
/* 486:    */     T[] result;
/* 487:835 */     if (Array.getLength(a) != list.size()) {
/* 488:836 */       result = (Object[])Utils.cast(Array.newInstance(a.getClass().getComponentType(), list.size()));
/* 489:    */     } else {
/* 490:839 */       result = a;
/* 491:    */     }
/* 492:842 */     for (int i = 0; i < list.size(); i++) {
/* 493:843 */       result[i] = list.get(i);
/* 494:    */     }
/* 495:846 */     return result;
/* 496:    */   }
/* 497:    */   
/* 498:    */   protected String toString(TrieNode node)
/* 499:    */   {
/* 500:860 */     StringBuffer result = new StringBuffer();
/* 501:    */     
/* 502:    */ 
/* 503:863 */     StringBuffer indentation = new StringBuffer();
/* 504:864 */     for (int i = 0; i < node.getLevel(); i++) {
/* 505:865 */       indentation.append(" | ");
/* 506:    */     }
/* 507:867 */     result.append(indentation.toString());
/* 508:870 */     if (node.getChar() == null) {
/* 509:871 */       result.append("<root>");
/* 510:872 */     } else if (node.getChar() == TrieNode.STOP) {
/* 511:873 */       result.append("STOP");
/* 512:    */     } else {
/* 513:875 */       result.append("'" + node.getChar() + "'");
/* 514:    */     }
/* 515:877 */     result.append("\n");
/* 516:880 */     for (i = 0; i < node.getChildCount(); i++) {
/* 517:881 */       result.append(toString((TrieNode)node.getChildAt(i)));
/* 518:    */     }
/* 519:884 */     return result.toString();
/* 520:    */   }
/* 521:    */   
/* 522:    */   public String toString()
/* 523:    */   {
/* 524:894 */     return toString(this.m_Root);
/* 525:    */   }
/* 526:    */   
/* 527:    */   public String getRevision()
/* 528:    */   {
/* 529:904 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 530:    */   }
/* 531:    */   
/* 532:    */   public static void main(String[] args)
/* 533:    */   {
/* 534:    */     String[] data;
/* 535:916 */     if (args.length == 0)
/* 536:    */     {
/* 537:917 */       String[] data = new String[3];
/* 538:918 */       data[0] = "this is a test";
/* 539:919 */       data[1] = "this is another test";
/* 540:920 */       data[2] = "and something else";
/* 541:    */     }
/* 542:    */     else
/* 543:    */     {
/* 544:922 */       data = (String[])args.clone();
/* 545:    */     }
/* 546:926 */     Trie t = new Trie();
/* 547:927 */     for (String element : data) {
/* 548:928 */       t.add(element);
/* 549:    */     }
/* 550:930 */     System.out.println(t);
/* 551:    */   }
/* 552:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Trie
 * JD-Core Version:    0.7.0.1
 */