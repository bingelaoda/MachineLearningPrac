/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.StringTokenizer;
/*   6:    */ import java.util.Vector;
/*   7:    */ 
/*   8:    */ public class HierarchyPropertyParser
/*   9:    */   implements Serializable
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = -4151103338506077544L;
/*  12:    */   private final TreeNode m_Root;
/*  13:    */   private TreeNode m_Current;
/*  14: 56 */   private String m_Seperator = ".";
/*  15: 59 */   private int m_Depth = 0;
/*  16:    */   
/*  17:    */   private class TreeNode
/*  18:    */     implements Serializable
/*  19:    */   {
/*  20:    */     private static final long serialVersionUID = 6495148851062003641L;
/*  21: 72 */     public TreeNode parent = null;
/*  22: 75 */     public String value = null;
/*  23: 78 */     public Vector<TreeNode> children = null;
/*  24: 81 */     public int level = 0;
/*  25: 84 */     public String context = null;
/*  26:    */     
/*  27:    */     private TreeNode() {}
/*  28:    */   }
/*  29:    */   
/*  30:    */   public HierarchyPropertyParser()
/*  31:    */   {
/*  32: 89 */     this.m_Root = new TreeNode(null);
/*  33: 90 */     this.m_Root.parent = null;
/*  34: 91 */     this.m_Root.children = new Vector();
/*  35: 92 */     goToRoot();
/*  36:    */   }
/*  37:    */   
/*  38:    */   public HierarchyPropertyParser(String p, String delim)
/*  39:    */     throws Exception
/*  40:    */   {
/*  41:103 */     this();
/*  42:104 */     build(p, delim);
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setSeperator(String s)
/*  46:    */   {
/*  47:113 */     this.m_Seperator = s;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public String getSeperator()
/*  51:    */   {
/*  52:122 */     return this.m_Seperator;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void build(String p, String delim)
/*  56:    */     throws Exception
/*  57:    */   {
/*  58:132 */     StringTokenizer st = new StringTokenizer(p, delim);
/*  59:134 */     while (st.hasMoreTokens())
/*  60:    */     {
/*  61:135 */       String property = st.nextToken().trim();
/*  62:136 */       if (!isHierachic(property)) {
/*  63:137 */         throw new Exception("The given property is not inhierachy structure with seperators!");
/*  64:    */       }
/*  65:140 */       add(property);
/*  66:    */     }
/*  67:142 */     goToRoot();
/*  68:    */   }
/*  69:    */   
/*  70:    */   public synchronized void add(String property)
/*  71:    */   {
/*  72:151 */     String[] values = tokenize(property);
/*  73:152 */     if (this.m_Root.value == null) {
/*  74:153 */       this.m_Root.value = values[0];
/*  75:    */     }
/*  76:156 */     buildBranch(this.m_Root, values, 1);
/*  77:    */   }
/*  78:    */   
/*  79:    */   private void buildBranch(TreeNode parent, String[] values, int lvl)
/*  80:    */   {
/*  81:169 */     if (lvl == values.length)
/*  82:    */     {
/*  83:170 */       parent.children = null;
/*  84:171 */       return;
/*  85:    */     }
/*  86:174 */     if (lvl > this.m_Depth - 1) {
/*  87:175 */       this.m_Depth = (lvl + 1);
/*  88:    */     }
/*  89:178 */     Vector<TreeNode> kids = parent.children;
/*  90:179 */     int index = search(kids, values[lvl]);
/*  91:180 */     if (index != -1)
/*  92:    */     {
/*  93:181 */       TreeNode newParent = (TreeNode)kids.elementAt(index);
/*  94:182 */       if (newParent.children == null) {
/*  95:183 */         newParent.children = new Vector();
/*  96:    */       }
/*  97:185 */       buildBranch(newParent, values, lvl + 1);
/*  98:    */     }
/*  99:    */     else
/* 100:    */     {
/* 101:187 */       TreeNode added = new TreeNode(null);
/* 102:188 */       added.parent = parent;
/* 103:189 */       added.value = values[lvl];
/* 104:190 */       added.children = new Vector();
/* 105:191 */       added.level = lvl;
/* 106:192 */       if (parent != this.m_Root) {
/* 107:193 */         added.context = (parent.context + this.m_Seperator + parent.value);
/* 108:    */       } else {
/* 109:195 */         added.context = parent.value;
/* 110:    */       }
/* 111:198 */       kids.addElement(added);
/* 112:199 */       buildBranch(added, values, lvl + 1);
/* 113:    */     }
/* 114:    */   }
/* 115:    */   
/* 116:    */   public String[] tokenize(String rawString)
/* 117:    */   {
/* 118:211 */     Vector<String> result = new Vector();
/* 119:212 */     StringTokenizer tk = new StringTokenizer(rawString, this.m_Seperator);
/* 120:213 */     while (tk.hasMoreTokens()) {
/* 121:214 */       result.addElement(tk.nextToken());
/* 122:    */     }
/* 123:217 */     String[] newStrings = new String[result.size()];
/* 124:218 */     for (int i = 0; i < result.size(); i++) {
/* 125:219 */       newStrings[i] = ((String)result.elementAt(i));
/* 126:    */     }
/* 127:222 */     return newStrings;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public boolean contains(String string)
/* 131:    */   {
/* 132:232 */     String[] item = tokenize(string);
/* 133:233 */     if (!item[0].equals(this.m_Root.value)) {
/* 134:234 */       return false;
/* 135:    */     }
/* 136:237 */     return isContained(this.m_Root, item, 1);
/* 137:    */   }
/* 138:    */   
/* 139:    */   private boolean isContained(TreeNode parent, String[] values, int lvl)
/* 140:    */   {
/* 141:250 */     if (lvl == values.length) {
/* 142:251 */       return true;
/* 143:    */     }
/* 144:252 */     if (lvl > values.length) {
/* 145:253 */       return false;
/* 146:    */     }
/* 147:255 */     Vector<TreeNode> kids = parent.children;
/* 148:256 */     int index = search(kids, values[lvl]);
/* 149:257 */     if (index != -1)
/* 150:    */     {
/* 151:258 */       TreeNode newParent = (TreeNode)kids.elementAt(index);
/* 152:259 */       return isContained(newParent, values, lvl + 1);
/* 153:    */     }
/* 154:261 */     return false;
/* 155:    */   }
/* 156:    */   
/* 157:    */   public boolean isHierachic(String string)
/* 158:    */   {
/* 159:272 */     int index = string.indexOf(this.m_Seperator);
/* 160:274 */     if ((index == string.length() - 1) || (index == -1)) {
/* 161:275 */       return false;
/* 162:    */     }
/* 163:278 */     return true;
/* 164:    */   }
/* 165:    */   
/* 166:    */   public int search(Vector<TreeNode> vct, String target)
/* 167:    */   {
/* 168:291 */     if (vct == null) {
/* 169:292 */       return -1;
/* 170:    */     }
/* 171:295 */     for (int i = 0; i < vct.size(); i++) {
/* 172:296 */       if (target.equals(((TreeNode)vct.elementAt(i)).value)) {
/* 173:297 */         return i;
/* 174:    */       }
/* 175:    */     }
/* 176:301 */     return -1;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public synchronized boolean goTo(String path)
/* 180:    */   {
/* 181:314 */     if (!isHierachic(path))
/* 182:    */     {
/* 183:315 */       if (this.m_Root.value.equals(path))
/* 184:    */       {
/* 185:316 */         goToRoot();
/* 186:317 */         return true;
/* 187:    */       }
/* 188:319 */       return false;
/* 189:    */     }
/* 190:323 */     TreeNode old = this.m_Current;
/* 191:324 */     this.m_Current = new TreeNode(null);
/* 192:325 */     goToRoot();
/* 193:326 */     String[] nodes = tokenize(path);
/* 194:327 */     if (!this.m_Current.value.equals(nodes[0])) {
/* 195:328 */       return false;
/* 196:    */     }
/* 197:331 */     for (int i = 1; i < nodes.length; i++)
/* 198:    */     {
/* 199:332 */       int pos = search(this.m_Current.children, nodes[i]);
/* 200:333 */       if (pos == -1)
/* 201:    */       {
/* 202:334 */         this.m_Current = old;
/* 203:335 */         return false;
/* 204:    */       }
/* 205:337 */       this.m_Current = ((TreeNode)this.m_Current.children.elementAt(pos));
/* 206:    */     }
/* 207:340 */     return true;
/* 208:    */   }
/* 209:    */   
/* 210:    */   public synchronized boolean goDown(String path)
/* 211:    */   {
/* 212:353 */     if (!isHierachic(path)) {
/* 213:354 */       return goToChild(path);
/* 214:    */     }
/* 215:357 */     TreeNode old = this.m_Current;
/* 216:358 */     this.m_Current = new TreeNode(null);
/* 217:359 */     String[] nodes = tokenize(path);
/* 218:360 */     int pos = search(old.children, nodes[0]);
/* 219:361 */     if (pos == -1)
/* 220:    */     {
/* 221:362 */       this.m_Current = old;
/* 222:363 */       return false;
/* 223:    */     }
/* 224:366 */     this.m_Current = ((TreeNode)old.children.elementAt(pos));
/* 225:367 */     for (int i = 1; i < nodes.length; i++)
/* 226:    */     {
/* 227:368 */       pos = search(this.m_Current.children, nodes[i]);
/* 228:369 */       if (pos == -1)
/* 229:    */       {
/* 230:370 */         this.m_Current = old;
/* 231:371 */         return false;
/* 232:    */       }
/* 233:374 */       this.m_Current = ((TreeNode)this.m_Current.children.elementAt(pos));
/* 234:    */     }
/* 235:377 */     return true;
/* 236:    */   }
/* 237:    */   
/* 238:    */   public synchronized void goToRoot()
/* 239:    */   {
/* 240:384 */     this.m_Current = this.m_Root;
/* 241:    */   }
/* 242:    */   
/* 243:    */   public synchronized void goToParent()
/* 244:    */   {
/* 245:392 */     if (this.m_Current.parent != null) {
/* 246:393 */       this.m_Current = this.m_Current.parent;
/* 247:    */     }
/* 248:    */   }
/* 249:    */   
/* 250:    */   public synchronized boolean goToChild(String value)
/* 251:    */   {
/* 252:407 */     if (this.m_Current.children == null) {
/* 253:408 */       return false;
/* 254:    */     }
/* 255:411 */     int pos = search(this.m_Current.children, value);
/* 256:412 */     if (pos == -1) {
/* 257:413 */       return false;
/* 258:    */     }
/* 259:416 */     this.m_Current = ((TreeNode)this.m_Current.children.elementAt(pos));
/* 260:417 */     return true;
/* 261:    */   }
/* 262:    */   
/* 263:    */   public synchronized void goToChild(int pos)
/* 264:    */     throws Exception
/* 265:    */   {
/* 266:428 */     if ((this.m_Current.children == null) || (pos < 0) || (pos >= this.m_Current.children.size())) {
/* 267:430 */       throw new Exception("Position out of range or leaf reached");
/* 268:    */     }
/* 269:433 */     this.m_Current = ((TreeNode)this.m_Current.children.elementAt(pos));
/* 270:    */   }
/* 271:    */   
/* 272:    */   public synchronized int numChildren()
/* 273:    */   {
/* 274:442 */     if (this.m_Current.children == null) {
/* 275:443 */       return 0;
/* 276:    */     }
/* 277:446 */     return this.m_Current.children.size();
/* 278:    */   }
/* 279:    */   
/* 280:    */   public synchronized String[] childrenValues()
/* 281:    */   {
/* 282:455 */     if (this.m_Current.children == null) {
/* 283:456 */       return null;
/* 284:    */     }
/* 285:458 */     Vector<TreeNode> kids = this.m_Current.children;
/* 286:459 */     String[] values = new String[kids.size()];
/* 287:460 */     for (int i = 0; i < kids.size(); i++) {
/* 288:461 */       values[i] = ((TreeNode)kids.elementAt(i)).value;
/* 289:    */     }
/* 290:463 */     return values;
/* 291:    */   }
/* 292:    */   
/* 293:    */   public synchronized String parentValue()
/* 294:    */   {
/* 295:473 */     if (this.m_Current.parent != null) {
/* 296:474 */       return this.m_Current.parent.value;
/* 297:    */     }
/* 298:476 */     return null;
/* 299:    */   }
/* 300:    */   
/* 301:    */   public synchronized boolean isLeafReached()
/* 302:    */   {
/* 303:486 */     return this.m_Current.children == null;
/* 304:    */   }
/* 305:    */   
/* 306:    */   public synchronized boolean isRootReached()
/* 307:    */   {
/* 308:495 */     return this.m_Current.parent == null;
/* 309:    */   }
/* 310:    */   
/* 311:    */   public synchronized String getValue()
/* 312:    */   {
/* 313:504 */     return this.m_Current.value;
/* 314:    */   }
/* 315:    */   
/* 316:    */   public synchronized int getLevel()
/* 317:    */   {
/* 318:513 */     return this.m_Current.level;
/* 319:    */   }
/* 320:    */   
/* 321:    */   public int depth()
/* 322:    */   {
/* 323:522 */     return this.m_Depth;
/* 324:    */   }
/* 325:    */   
/* 326:    */   public synchronized String context()
/* 327:    */   {
/* 328:533 */     return this.m_Current.context;
/* 329:    */   }
/* 330:    */   
/* 331:    */   public synchronized String fullValue()
/* 332:    */   {
/* 333:543 */     if (this.m_Current == this.m_Root) {
/* 334:544 */       return this.m_Root.value;
/* 335:    */     }
/* 336:546 */     return this.m_Current.context + this.m_Seperator + this.m_Current.value;
/* 337:    */   }
/* 338:    */   
/* 339:    */   public String showTree()
/* 340:    */   {
/* 341:556 */     return showNode(this.m_Root, null);
/* 342:    */   }
/* 343:    */   
/* 344:    */   private String showNode(TreeNode node, boolean[] hasBar)
/* 345:    */   {
/* 346:566 */     StringBuffer text = new StringBuffer();
/* 347:568 */     for (int i = 0; i < node.level - 1; i++) {
/* 348:569 */       if (hasBar[i] != 0) {
/* 349:570 */         text.append("  |       ");
/* 350:    */       } else {
/* 351:572 */         text.append("          ");
/* 352:    */       }
/* 353:    */     }
/* 354:576 */     if (node.level != 0) {
/* 355:577 */       text.append("  |------ ");
/* 356:    */     }
/* 357:579 */     text.append(node.value + "(" + node.level + ")" + "[" + node.context + "]\n");
/* 358:582 */     if (node.children != null) {
/* 359:583 */       for (int i = 0; i < node.children.size(); i++)
/* 360:    */       {
/* 361:584 */         boolean[] newBar = new boolean[node.level + 1];
/* 362:585 */         int lvl = node.level;
/* 363:587 */         if (hasBar != null) {
/* 364:588 */           for (int j = 0; j < lvl; j++) {
/* 365:589 */             newBar[j] = hasBar[j];
/* 366:    */           }
/* 367:    */         }
/* 368:593 */         if (i == node.children.size() - 1) {
/* 369:594 */           newBar[lvl] = false;
/* 370:    */         } else {
/* 371:596 */           newBar[lvl] = true;
/* 372:    */         }
/* 373:599 */         text.append(showNode((TreeNode)node.children.elementAt(i), newBar));
/* 374:    */       }
/* 375:    */     }
/* 376:603 */     return text.toString();
/* 377:    */   }
/* 378:    */   
/* 379:    */   public static void main(String[] args)
/* 380:    */   {
/* 381:612 */     StringBuffer sb = new StringBuffer();
/* 382:613 */     sb.append("node1.node1_1.node1_1_1.node1_1_1_1, ");
/* 383:614 */     sb.append("node1.node1_1.node1_1_1.node1_1_1_2, ");
/* 384:615 */     sb.append("node1.node1_1.node1_1_1.node1_1_1_3, ");
/* 385:616 */     sb.append("node1.node1_1.node1_1_2.node1_1_2_1, ");
/* 386:617 */     sb.append("node1.node1_1.node1_1_3.node1_1_3_1, ");
/* 387:618 */     sb.append("node1.node1_2.node1_2_1.node1_2_1_1, ");
/* 388:619 */     sb.append("node1.node1_2.node1_2_3.node1_2_3_1, ");
/* 389:620 */     sb.append("node1.node1_3.node1_3_3.node1_3_3_1, ");
/* 390:621 */     sb.append("node1.node1_3.node1_3_3.node1_3_3_2, ");
/* 391:    */     
/* 392:623 */     String p = sb.toString();
/* 393:    */     try
/* 394:    */     {
/* 395:625 */       HierarchyPropertyParser hpp = new HierarchyPropertyParser(p, ", ");
/* 396:626 */       System.out.println("seperator: " + hpp.getSeperator());
/* 397:627 */       System.out.println("depth: " + hpp.depth());
/* 398:628 */       System.out.println("The tree:\n\n" + hpp.showTree());
/* 399:629 */       hpp.goToRoot();
/* 400:630 */       System.out.println("goto: " + hpp.goTo("node1.node1_2.node1_2_1") + ": " + hpp.getValue() + " | " + hpp.fullValue() + " leaf? " + hpp.isLeafReached());
/* 401:    */       
/* 402:    */ 
/* 403:633 */       System.out.println("go down(wrong): " + hpp.goDown("node1"));
/* 404:634 */       System.out.println("Stay still? " + hpp.getValue());
/* 405:635 */       System.out.println("go to child: " + hpp.goToChild("node1_2_1_1") + ": " + hpp.getValue() + " | " + hpp.fullValue() + " leaf? " + hpp.isLeafReached() + " root? " + hpp.isRootReached());
/* 406:    */       
/* 407:    */ 
/* 408:638 */       System.out.println("parent: " + hpp.parentValue());
/* 409:639 */       System.out.println("level: " + hpp.getLevel());
/* 410:640 */       System.out.println("context: " + hpp.context());
/* 411:641 */       hpp.goToRoot();
/* 412:642 */       System.out.println("After gotoRoot. leaf? " + hpp.isLeafReached() + " root? " + hpp.isRootReached());
/* 413:    */       
/* 414:644 */       System.out.println("Go down(correct): " + hpp.goDown("node1_1.node1_1_1") + " value: " + hpp.getValue() + " | " + hpp.fullValue() + " level: " + hpp.getLevel() + " leaf? " + hpp.isLeafReached() + " root? " + hpp.isRootReached());
/* 415:    */       
/* 416:    */ 
/* 417:    */ 
/* 418:648 */       hpp.goToParent();
/* 419:649 */       System.out.println("value: " + hpp.getValue() + " | " + hpp.fullValue());
/* 420:650 */       System.out.println("level: " + hpp.getLevel());
/* 421:    */       
/* 422:652 */       String[] chd = hpp.childrenValues();
/* 423:653 */       for (int i = 0; i < chd.length; i++)
/* 424:    */       {
/* 425:654 */         System.out.print("children " + i + ": " + chd[i]);
/* 426:655 */         hpp.goDown(chd[i]);
/* 427:656 */         System.out.println("real value: " + hpp.getValue() + " | " + hpp.fullValue() + "(level: " + hpp.getLevel() + ")");
/* 428:    */         
/* 429:658 */         hpp.goToParent();
/* 430:    */       }
/* 431:661 */       System.out.println("Another way to go to root:" + hpp.goTo("node1") + ": " + hpp.getValue() + " | " + hpp.fullValue());
/* 432:    */     }
/* 433:    */     catch (Exception e)
/* 434:    */     {
/* 435:664 */       System.out.println(e.getMessage());
/* 436:665 */       e.printStackTrace();
/* 437:    */     }
/* 438:    */   }
/* 439:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.HierarchyPropertyParser
 * JD-Core Version:    0.7.0.1
 */