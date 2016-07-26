/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.Dimension;
/*   4:    */ import java.awt.Font;
/*   5:    */ import java.awt.FontMetrics;
/*   6:    */ import java.awt.Graphics;
/*   7:    */ import java.awt.Point;
/*   8:    */ import java.awt.Rectangle;
/*   9:    */ import java.beans.Beans;
/*  10:    */ import java.io.PrintStream;
/*  11:    */ import java.io.Serializable;
/*  12:    */ import java.util.ArrayList;
/*  13:    */ import java.util.List;
/*  14:    */ import java.util.Vector;
/*  15:    */ import javax.swing.JComponent;
/*  16:    */ 
/*  17:    */ public class BeanInstance
/*  18:    */   implements Serializable
/*  19:    */ {
/*  20:    */   private static final long serialVersionUID = -7575653109025406342L;
/*  21: 50 */   private static ArrayList<Vector<Object>> TABBED_COMPONENTS = new ArrayList();
/*  22:    */   public static final int IDLE = 0;
/*  23:    */   public static final int BEAN_EXECUTING = 1;
/*  24:    */   private Object m_bean;
/*  25:    */   private int m_x;
/*  26:    */   private int m_y;
/*  27:    */   
/*  28:    */   public static void init()
/*  29:    */   {
/*  30: 61 */     TABBED_COMPONENTS.clear();
/*  31: 62 */     TABBED_COMPONENTS.add(new Vector());
/*  32:    */   }
/*  33:    */   
/*  34:    */   public static void removeAllBeansFromContainer(JComponent container, Integer... tab)
/*  35:    */   {
/*  36: 88 */     int index = 0;
/*  37: 89 */     if (tab.length > 0) {
/*  38: 90 */       index = tab[0].intValue();
/*  39:    */     }
/*  40: 93 */     Vector<Object> components = null;
/*  41: 94 */     if ((TABBED_COMPONENTS.size() > 0) && (index < TABBED_COMPONENTS.size())) {
/*  42: 95 */       components = (Vector)TABBED_COMPONENTS.get(index);
/*  43:    */     }
/*  44: 98 */     if (container != null)
/*  45:    */     {
/*  46: 99 */       if (components != null) {
/*  47:100 */         for (int i = 0; i < components.size(); i++)
/*  48:    */         {
/*  49:101 */           Object tempInstance = components.elementAt(i);
/*  50:102 */           Object tempBean = ((BeanInstance)tempInstance).getBean();
/*  51:103 */           if (Beans.isInstanceOf(tempBean, JComponent.class)) {
/*  52:104 */             container.remove((JComponent)tempBean);
/*  53:    */           }
/*  54:    */         }
/*  55:    */       }
/*  56:108 */       container.revalidate();
/*  57:    */     }
/*  58:    */   }
/*  59:    */   
/*  60:    */   public static void addAllBeansToContainer(JComponent container, Integer... tab)
/*  61:    */   {
/*  62:119 */     int index = 0;
/*  63:120 */     if (tab.length > 0) {
/*  64:121 */       index = tab[0].intValue();
/*  65:    */     }
/*  66:124 */     Vector<Object> components = null;
/*  67:125 */     if ((TABBED_COMPONENTS.size() > 0) && (index < TABBED_COMPONENTS.size())) {
/*  68:126 */       components = (Vector)TABBED_COMPONENTS.get(index);
/*  69:    */     }
/*  70:129 */     if (container != null)
/*  71:    */     {
/*  72:130 */       if (components != null) {
/*  73:131 */         for (int i = 0; i < components.size(); i++)
/*  74:    */         {
/*  75:132 */           BeanInstance tempInstance = (BeanInstance)components.elementAt(i);
/*  76:133 */           Object tempBean = tempInstance.getBean();
/*  77:134 */           if (Beans.isInstanceOf(tempBean, JComponent.class)) {
/*  78:135 */             container.add((JComponent)tempBean);
/*  79:    */           }
/*  80:    */         }
/*  81:    */       }
/*  82:139 */       container.revalidate();
/*  83:    */     }
/*  84:    */   }
/*  85:    */   
/*  86:    */   public static Vector<Object> getBeanInstances(Integer... tab)
/*  87:    */   {
/*  88:153 */     Vector<Object> returnV = null;
/*  89:154 */     int index = 0;
/*  90:155 */     if (tab.length > 0) {
/*  91:156 */       index = tab[0].intValue();
/*  92:    */     }
/*  93:158 */     if (TABBED_COMPONENTS.size() > 0) {
/*  94:159 */       returnV = (Vector)TABBED_COMPONENTS.get(index);
/*  95:    */     }
/*  96:162 */     return returnV;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public static void setBeanInstances(Vector<Object> beanInstances, JComponent container, Integer... tab)
/* 100:    */   {
/* 101:176 */     removeAllBeansFromContainer(container, tab);
/* 102:178 */     if (container != null)
/* 103:    */     {
/* 104:179 */       for (int i = 0; i < beanInstances.size(); i++)
/* 105:    */       {
/* 106:180 */         Object bean = ((BeanInstance)beanInstances.elementAt(i)).getBean();
/* 107:181 */         if (Beans.isInstanceOf(bean, JComponent.class)) {
/* 108:182 */           container.add((JComponent)bean);
/* 109:    */         }
/* 110:    */       }
/* 111:185 */       container.revalidate();
/* 112:186 */       container.repaint();
/* 113:    */     }
/* 114:189 */     int index = 0;
/* 115:190 */     if (tab.length > 0) {
/* 116:191 */       index = tab[0].intValue();
/* 117:    */     }
/* 118:194 */     if (index < TABBED_COMPONENTS.size()) {
/* 119:195 */       TABBED_COMPONENTS.set(index, beanInstances);
/* 120:    */     }
/* 121:    */   }
/* 122:    */   
/* 123:    */   public static void addBeanInstances(Vector<Object> beanInstances, JComponent container)
/* 124:    */   {
/* 125:212 */     if (container != null)
/* 126:    */     {
/* 127:213 */       for (int i = 0; i < beanInstances.size(); i++)
/* 128:    */       {
/* 129:214 */         Object bean = ((BeanInstance)beanInstances.elementAt(i)).getBean();
/* 130:215 */         if (Beans.isInstanceOf(bean, JComponent.class)) {
/* 131:216 */           container.add((JComponent)bean);
/* 132:    */         }
/* 133:    */       }
/* 134:219 */       container.revalidate();
/* 135:220 */       container.repaint();
/* 136:    */     }
/* 137:223 */     TABBED_COMPONENTS.add(beanInstances);
/* 138:    */   }
/* 139:    */   
/* 140:    */   public static void removeBeanInstances(JComponent container, Integer tab)
/* 141:    */   {
/* 142:234 */     if ((tab.intValue() >= 0) && (tab.intValue() < TABBED_COMPONENTS.size()))
/* 143:    */     {
/* 144:235 */       System.out.println("Removing vector of beans at index: " + tab);
/* 145:236 */       removeAllBeansFromContainer(container, new Integer[] { tab });
/* 146:237 */       TABBED_COMPONENTS.remove(tab.intValue());
/* 147:    */     }
/* 148:    */   }
/* 149:    */   
/* 150:    */   public static void paintLabels(Graphics gx, Integer... tab)
/* 151:    */   {
/* 152:247 */     int index = 0;
/* 153:248 */     if (tab.length > 0) {
/* 154:249 */       index = tab[0].intValue();
/* 155:    */     }
/* 156:252 */     Vector<Object> components = null;
/* 157:253 */     if ((TABBED_COMPONENTS.size() > 0) && (index < TABBED_COMPONENTS.size())) {
/* 158:254 */       components = (Vector)TABBED_COMPONENTS.get(index);
/* 159:    */     }
/* 160:257 */     if (components != null)
/* 161:    */     {
/* 162:258 */       gx.setFont(new Font(null, 0, 9));
/* 163:259 */       FontMetrics fm = gx.getFontMetrics();
/* 164:260 */       int hf = fm.getAscent();
/* 165:261 */       for (int i = 0; i < components.size(); i++)
/* 166:    */       {
/* 167:262 */         BeanInstance bi = (BeanInstance)components.elementAt(i);
/* 168:263 */         if ((bi.getBean() instanceof Visible))
/* 169:    */         {
/* 170:266 */           int cx = bi.getX();
/* 171:267 */           int cy = bi.getY();
/* 172:268 */           int width = ((JComponent)bi.getBean()).getWidth();
/* 173:269 */           int height = ((JComponent)bi.getBean()).getHeight();
/* 174:270 */           String label = ((Visible)bi.getBean()).getVisual().getText();
/* 175:271 */           int labelwidth = fm.stringWidth(label);
/* 176:272 */           if (labelwidth < width)
/* 177:    */           {
/* 178:273 */             gx.drawString(label, cx + width / 2 - labelwidth / 2, cy + height + hf + 2);
/* 179:    */           }
/* 180:    */           else
/* 181:    */           {
/* 182:279 */             int mid = label.length() / 2;
/* 183:    */             
/* 184:281 */             int closest = label.length();
/* 185:282 */             int closestI = -1;
/* 186:283 */             for (int z = 0; z < label.length(); z++) {
/* 187:284 */               if ((label.charAt(z) < 'a') && 
/* 188:285 */                 (Math.abs(mid - z) < closest))
/* 189:    */               {
/* 190:286 */                 closest = Math.abs(mid - z);
/* 191:287 */                 closestI = z;
/* 192:    */               }
/* 193:    */             }
/* 194:291 */             if (closestI != -1)
/* 195:    */             {
/* 196:292 */               String left = label.substring(0, closestI);
/* 197:293 */               String right = label.substring(closestI, label.length());
/* 198:294 */               if ((left.length() > 1) && (right.length() > 1))
/* 199:    */               {
/* 200:295 */                 gx.drawString(left, cx + width / 2 - fm.stringWidth(left) / 2, cy + height + hf * 1 + 2);
/* 201:    */                 
/* 202:297 */                 gx.drawString(right, cx + width / 2 - fm.stringWidth(right) / 2, cy + height + hf * 2 + 2);
/* 203:    */               }
/* 204:    */               else
/* 205:    */               {
/* 206:300 */                 gx.drawString(label, cx + width / 2 - fm.stringWidth(label) / 2, cy + height + hf * 1 + 2);
/* 207:    */               }
/* 208:    */             }
/* 209:    */             else
/* 210:    */             {
/* 211:304 */               gx.drawString(label, cx + width / 2 - fm.stringWidth(label) / 2, cy + height + hf * 1 + 2);
/* 212:    */             }
/* 213:    */           }
/* 214:    */         }
/* 215:    */       }
/* 216:    */     }
/* 217:    */   }
/* 218:    */   
/* 219:    */   public static List<BeanInstance> getStartPoints(Integer... tab)
/* 220:    */   {
/* 221:320 */     List<BeanInstance> startPoints = new ArrayList();
/* 222:    */     
/* 223:322 */     int index = 0;
/* 224:323 */     if (tab.length > 0) {
/* 225:324 */       index = tab[0].intValue();
/* 226:    */     }
/* 227:327 */     Vector<Object> components = null;
/* 228:328 */     if ((TABBED_COMPONENTS.size() > 0) && (index < TABBED_COMPONENTS.size()))
/* 229:    */     {
/* 230:329 */       components = (Vector)TABBED_COMPONENTS.get(index);
/* 231:331 */       for (int i = 0; i < components.size(); i++)
/* 232:    */       {
/* 233:332 */         BeanInstance t = (BeanInstance)components.elementAt(i);
/* 234:333 */         if ((t.getBean() instanceof Startable)) {
/* 235:334 */           startPoints.add(t);
/* 236:    */         }
/* 237:    */       }
/* 238:    */     }
/* 239:339 */     return startPoints;
/* 240:    */   }
/* 241:    */   
/* 242:    */   public static BeanInstance findInstance(String beanName, Integer... tab)
/* 243:    */   {
/* 244:350 */     BeanInstance found = null;
/* 245:    */     
/* 246:352 */     int index = 0;
/* 247:353 */     if (tab.length > 0) {
/* 248:354 */       index = tab[0].intValue();
/* 249:    */     }
/* 250:357 */     Vector<Object> components = null;
/* 251:358 */     if ((TABBED_COMPONENTS.size() > 0) && (index < TABBED_COMPONENTS.size()))
/* 252:    */     {
/* 253:359 */       components = (Vector)TABBED_COMPONENTS.get(index);
/* 254:361 */       for (int i = 0; i < components.size(); i++)
/* 255:    */       {
/* 256:362 */         BeanInstance t = (BeanInstance)components.elementAt(i);
/* 257:364 */         if ((t.getBean() instanceof BeanCommon))
/* 258:    */         {
/* 259:365 */           String bN = ((BeanCommon)t).getCustomName();
/* 260:367 */           if (bN.equals(beanName))
/* 261:    */           {
/* 262:368 */             found = t;
/* 263:369 */             break;
/* 264:    */           }
/* 265:    */         }
/* 266:    */       }
/* 267:    */     }
/* 268:375 */     return found;
/* 269:    */   }
/* 270:    */   
/* 271:    */   public static BeanInstance findInstance(Point p, Integer... tab)
/* 272:    */   {
/* 273:386 */     int index = 0;
/* 274:387 */     if (tab.length > 0) {
/* 275:388 */       index = tab[0].intValue();
/* 276:    */     }
/* 277:391 */     Vector<Object> components = null;
/* 278:392 */     if ((TABBED_COMPONENTS.size() > 0) && (index < TABBED_COMPONENTS.size())) {
/* 279:393 */       components = (Vector)TABBED_COMPONENTS.get(index);
/* 280:    */     }
/* 281:396 */     Rectangle tempBounds = new Rectangle();
/* 282:397 */     for (int i = 0; i < components.size(); i++)
/* 283:    */     {
/* 284:399 */       BeanInstance t = (BeanInstance)components.elementAt(i);
/* 285:400 */       JComponent temp = (JComponent)t.getBean();
/* 286:    */       
/* 287:402 */       tempBounds = temp.getBounds(tempBounds);
/* 288:403 */       if (tempBounds.contains(p)) {
/* 289:404 */         return t;
/* 290:    */       }
/* 291:    */     }
/* 292:407 */     return null;
/* 293:    */   }
/* 294:    */   
/* 295:    */   public static Vector<Object> findInstances(Rectangle boundingBox, Integer... tab)
/* 296:    */   {
/* 297:419 */     int index = 0;
/* 298:420 */     if (tab.length > 0) {
/* 299:421 */       index = tab[0].intValue();
/* 300:    */     }
/* 301:424 */     Vector<Object> components = null;
/* 302:425 */     if ((TABBED_COMPONENTS.size() > 0) && (index < TABBED_COMPONENTS.size())) {
/* 303:426 */       components = (Vector)TABBED_COMPONENTS.get(index);
/* 304:    */     }
/* 305:429 */     Graphics gx = null;
/* 306:430 */     FontMetrics fm = null;
/* 307:    */     
/* 308:    */ 
/* 309:433 */     int minX = 2147483647;
/* 310:434 */     int minY = 2147483647;
/* 311:435 */     int maxX = -2147483648;
/* 312:436 */     int maxY = -2147483648;
/* 313:437 */     Vector<Object> result = new Vector();
/* 314:438 */     for (int i = 0; i < components.size(); i++)
/* 315:    */     {
/* 316:439 */       BeanInstance t = (BeanInstance)components.elementAt(i);
/* 317:440 */       int centerX = t.getX() + t.getWidth() / 2;
/* 318:441 */       int centerY = t.getY() + t.getHeight() / 2;
/* 319:442 */       if (boundingBox.contains(centerX, centerY))
/* 320:    */       {
/* 321:443 */         result.addElement(t);
/* 322:447 */         if (gx == null)
/* 323:    */         {
/* 324:448 */           gx = ((JComponent)t.getBean()).getGraphics();
/* 325:449 */           gx.setFont(new Font(null, 0, 9));
/* 326:450 */           fm = gx.getFontMetrics();
/* 327:    */         }
/* 328:453 */         String label = "";
/* 329:454 */         if ((t.getBean() instanceof Visible)) {
/* 330:455 */           label = ((Visible)t.getBean()).getVisual().getText();
/* 331:    */         }
/* 332:457 */         int labelwidth = fm.stringWidth(label);
/* 333:    */         
/* 334:    */ 
/* 335:    */ 
/* 336:461 */         int brx = 0;
/* 337:462 */         int blx = 0;
/* 338:463 */         if (centerX - labelwidth / 2 - 2 < t.getX())
/* 339:    */         {
/* 340:464 */           blx = centerX - labelwidth / 2 - 2;
/* 341:465 */           brx = centerX + labelwidth / 2 + 2;
/* 342:    */         }
/* 343:    */         else
/* 344:    */         {
/* 345:467 */           blx = t.getX() - 2;
/* 346:468 */           brx = t.getX() + t.getWidth() + 2;
/* 347:    */         }
/* 348:471 */         if (blx < minX) {
/* 349:472 */           minX = blx;
/* 350:    */         }
/* 351:474 */         if (brx > maxX) {
/* 352:475 */           maxX = brx;
/* 353:    */         }
/* 354:477 */         if (t.getY() - 2 < minY) {
/* 355:478 */           minY = t.getY() - 2;
/* 356:    */         }
/* 357:480 */         if (t.getY() + t.getHeight() + 2 > maxY) {
/* 358:481 */           maxY = t.getY() + t.getHeight() + 2;
/* 359:    */         }
/* 360:    */       }
/* 361:    */     }
/* 362:485 */     boundingBox.setBounds(minX, minY, maxX - minX, maxY - minY);
/* 363:    */     
/* 364:487 */     return result;
/* 365:    */   }
/* 366:    */   
/* 367:    */   public BeanInstance(JComponent container, Object bean, int x, int y, Integer... tab)
/* 368:    */   {
/* 369:500 */     this.m_bean = bean;
/* 370:501 */     this.m_x = x;
/* 371:502 */     this.m_y = y;
/* 372:503 */     addBean(container, tab);
/* 373:    */   }
/* 374:    */   
/* 375:    */   public BeanInstance(JComponent container, String beanName, int x, int y, Integer... tab)
/* 376:    */   {
/* 377:517 */     this.m_x = x;
/* 378:518 */     this.m_y = y;
/* 379:    */     try
/* 380:    */     {
/* 381:522 */       this.m_bean = Beans.instantiate(null, beanName);
/* 382:    */     }
/* 383:    */     catch (Exception ex)
/* 384:    */     {
/* 385:524 */       ex.printStackTrace();
/* 386:525 */       return;
/* 387:    */     }
/* 388:528 */     addBean(container, tab);
/* 389:    */   }
/* 390:    */   
/* 391:    */   public void removeBean(JComponent container, Integer... tab)
/* 392:    */   {
/* 393:537 */     int index = 0;
/* 394:538 */     if (tab.length > 0) {
/* 395:539 */       index = tab[0].intValue();
/* 396:    */     }
/* 397:542 */     Vector<Object> components = null;
/* 398:543 */     if ((TABBED_COMPONENTS.size() > 0) && (index < TABBED_COMPONENTS.size())) {
/* 399:544 */       components = (Vector)TABBED_COMPONENTS.get(index);
/* 400:    */     }
/* 401:547 */     for (int i = 0; i < components.size(); i++) {
/* 402:548 */       if (components.elementAt(i) == this)
/* 403:    */       {
/* 404:549 */         System.out.println("Removing bean");
/* 405:550 */         components.removeElementAt(i);
/* 406:    */       }
/* 407:    */     }
/* 408:553 */     if (container != null)
/* 409:    */     {
/* 410:554 */       container.remove((JComponent)this.m_bean);
/* 411:555 */       container.revalidate();
/* 412:556 */       container.repaint();
/* 413:    */     }
/* 414:    */   }
/* 415:    */   
/* 416:    */   public static void appendBeans(JComponent container, Vector<Object> beans, int tab)
/* 417:    */   {
/* 418:562 */     if ((TABBED_COMPONENTS.size() > 0) && (tab < TABBED_COMPONENTS.size()))
/* 419:    */     {
/* 420:563 */       Vector<Object> components = (Vector)TABBED_COMPONENTS.get(tab);
/* 421:566 */       for (int i = 0; i < beans.size(); i++)
/* 422:    */       {
/* 423:567 */         components.add(beans.get(i));
/* 424:568 */         if (container != null)
/* 425:    */         {
/* 426:569 */           Object bean = ((BeanInstance)beans.elementAt(i)).getBean();
/* 427:570 */           if (Beans.isInstanceOf(bean, JComponent.class)) {
/* 428:571 */             container.add((JComponent)bean);
/* 429:    */           }
/* 430:    */         }
/* 431:    */       }
/* 432:576 */       if (container != null)
/* 433:    */       {
/* 434:577 */         container.revalidate();
/* 435:578 */         container.repaint();
/* 436:    */       }
/* 437:    */     }
/* 438:    */   }
/* 439:    */   
/* 440:    */   public void addBean(JComponent container, Integer... tab)
/* 441:    */   {
/* 442:592 */     int index = 0;
/* 443:593 */     if (tab.length > 0) {
/* 444:594 */       index = tab[0].intValue();
/* 445:    */     }
/* 446:597 */     Vector<Object> components = null;
/* 447:598 */     if ((TABBED_COMPONENTS.size() > 0) && (index < TABBED_COMPONENTS.size())) {
/* 448:599 */       components = (Vector)TABBED_COMPONENTS.get(index);
/* 449:    */     }
/* 450:603 */     if (components.contains(this)) {
/* 451:604 */       return;
/* 452:    */     }
/* 453:608 */     if (!Beans.isInstanceOf(this.m_bean, JComponent.class))
/* 454:    */     {
/* 455:609 */       System.out.println("Component is invisible!");
/* 456:610 */       return;
/* 457:    */     }
/* 458:613 */     components.addElement(this);
/* 459:    */     
/* 460:    */ 
/* 461:616 */     JComponent c = (JComponent)this.m_bean;
/* 462:617 */     Dimension d = c.getPreferredSize();
/* 463:618 */     int dx = (int)(d.getWidth() / 2.0D);
/* 464:619 */     int dy = (int)(d.getHeight() / 2.0D);
/* 465:620 */     this.m_x -= dx;
/* 466:621 */     this.m_y -= dy;
/* 467:622 */     c.setLocation(this.m_x, this.m_y);
/* 468:    */     
/* 469:624 */     c.validate();
/* 470:627 */     if (container != null)
/* 471:    */     {
/* 472:628 */       container.add(c);
/* 473:629 */       container.revalidate();
/* 474:    */     }
/* 475:    */   }
/* 476:    */   
/* 477:    */   public Object getBean()
/* 478:    */   {
/* 479:639 */     return this.m_bean;
/* 480:    */   }
/* 481:    */   
/* 482:    */   public int getX()
/* 483:    */   {
/* 484:648 */     return this.m_x;
/* 485:    */   }
/* 486:    */   
/* 487:    */   public int getY()
/* 488:    */   {
/* 489:657 */     return this.m_y;
/* 490:    */   }
/* 491:    */   
/* 492:    */   public int getWidth()
/* 493:    */   {
/* 494:666 */     return ((JComponent)this.m_bean).getWidth();
/* 495:    */   }
/* 496:    */   
/* 497:    */   public int getHeight()
/* 498:    */   {
/* 499:675 */     return ((JComponent)this.m_bean).getHeight();
/* 500:    */   }
/* 501:    */   
/* 502:    */   public void setXY(int newX, int newY)
/* 503:    */   {
/* 504:685 */     setX(newX);
/* 505:686 */     setY(newY);
/* 506:687 */     if ((getBean() instanceof MetaBean)) {
/* 507:688 */       ((MetaBean)getBean()).shiftBeans(this, false);
/* 508:    */     }
/* 509:    */   }
/* 510:    */   
/* 511:    */   public void setX(int newX)
/* 512:    */   {
/* 513:698 */     this.m_x = newX;
/* 514:699 */     ((JComponent)this.m_bean).setLocation(this.m_x, this.m_y);
/* 515:700 */     ((JComponent)this.m_bean).validate();
/* 516:    */   }
/* 517:    */   
/* 518:    */   public void setY(int newY)
/* 519:    */   {
/* 520:709 */     this.m_y = newY;
/* 521:710 */     ((JComponent)this.m_bean).setLocation(this.m_x, this.m_y);
/* 522:711 */     ((JComponent)this.m_bean).validate();
/* 523:    */   }
/* 524:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.BeanInstance
 * JD-Core Version:    0.7.0.1
 */