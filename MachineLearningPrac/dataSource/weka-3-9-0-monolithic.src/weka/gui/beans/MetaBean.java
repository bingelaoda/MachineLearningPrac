/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Point;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.awt.event.MouseAdapter;
/*   9:    */ import java.awt.event.MouseEvent;
/*  10:    */ import java.beans.BeanInfo;
/*  11:    */ import java.beans.EventSetDescriptor;
/*  12:    */ import java.beans.IntrospectionException;
/*  13:    */ import java.beans.Introspector;
/*  14:    */ import java.beans.PropertyChangeListener;
/*  15:    */ import java.io.Serializable;
/*  16:    */ import java.util.Enumeration;
/*  17:    */ import java.util.Set;
/*  18:    */ import java.util.TreeMap;
/*  19:    */ import java.util.Vector;
/*  20:    */ import javax.swing.ImageIcon;
/*  21:    */ import javax.swing.JComponent;
/*  22:    */ import javax.swing.JLabel;
/*  23:    */ import javax.swing.JPanel;
/*  24:    */ import javax.swing.JWindow;
/*  25:    */ import javax.swing.Timer;
/*  26:    */ import weka.gui.Logger;
/*  27:    */ 
/*  28:    */ public class MetaBean
/*  29:    */   extends JPanel
/*  30:    */   implements BeanCommon, Visible, EventConstraints, Serializable, UserRequestAcceptor, Startable
/*  31:    */ {
/*  32:    */   private static final long serialVersionUID = -6582768902038027077L;
/*  33: 61 */   protected BeanVisual m_visual = new BeanVisual("Group", "weka/gui/beans/icons/DiamondPlain.gif", "weka/gui/beans/icons/DiamondPlain.gif");
/*  34: 64 */   private transient Logger m_log = null;
/*  35: 65 */   private transient JWindow m_previewWindow = null;
/*  36: 66 */   private transient Timer m_previewTimer = null;
/*  37: 68 */   protected Vector<Object> m_subFlow = new Vector();
/*  38: 69 */   protected Vector<Object> m_inputs = new Vector();
/*  39: 70 */   protected Vector<Object> m_outputs = new Vector();
/*  40: 73 */   protected Vector<BeanConnection> m_associatedConnections = new Vector();
/*  41: 76 */   protected ImageIcon m_subFlowPreview = null;
/*  42: 80 */   protected int m_xCreate = 0;
/*  43: 81 */   protected int m_yCreate = 0;
/*  44: 82 */   protected int m_xDrop = 0;
/*  45: 83 */   protected int m_yDrop = 0;
/*  46:    */   private Vector<Point> m_originalCoords;
/*  47:    */   
/*  48:    */   public MetaBean()
/*  49:    */   {
/*  50: 86 */     setLayout(new BorderLayout());
/*  51: 87 */     add(this.m_visual, "Center");
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void setCustomName(String name)
/*  55:    */   {
/*  56: 97 */     this.m_visual.setText(name);
/*  57:    */   }
/*  58:    */   
/*  59:    */   public String getCustomName()
/*  60:    */   {
/*  61:107 */     return this.m_visual.getText();
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void setAssociatedConnections(Vector<BeanConnection> ac)
/*  65:    */   {
/*  66:111 */     this.m_associatedConnections = ac;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public Vector<BeanConnection> getAssociatedConnections()
/*  70:    */   {
/*  71:115 */     return this.m_associatedConnections;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setSubFlow(Vector<Object> sub)
/*  75:    */   {
/*  76:119 */     this.m_subFlow = sub;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public Vector<Object> getSubFlow()
/*  80:    */   {
/*  81:123 */     return this.m_subFlow;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setInputs(Vector<Object> inputs)
/*  85:    */   {
/*  86:127 */     this.m_inputs = inputs;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public Vector<Object> getInputs()
/*  90:    */   {
/*  91:131 */     return this.m_inputs;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void setOutputs(Vector<Object> outputs)
/*  95:    */   {
/*  96:135 */     this.m_outputs = outputs;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public Vector<Object> getOutputs()
/* 100:    */   {
/* 101:139 */     return this.m_outputs;
/* 102:    */   }
/* 103:    */   
/* 104:    */   private Vector<Object> getBeans(Vector<Object> beans, int type)
/* 105:    */   {
/* 106:143 */     Vector<Object> comps = new Vector();
/* 107:144 */     for (int i = 0; i < beans.size(); i++)
/* 108:    */     {
/* 109:145 */       BeanInstance temp = (BeanInstance)beans.elementAt(i);
/* 110:147 */       if ((temp.getBean() instanceof MetaBean)) {
/* 111:148 */         switch (type)
/* 112:    */         {
/* 113:    */         case 0: 
/* 114:150 */           comps.addAll(((MetaBean)temp.getBean()).getBeansInSubFlow());
/* 115:151 */           break;
/* 116:    */         case 1: 
/* 117:153 */           comps.addAll(((MetaBean)temp.getBean()).getBeansInInputs());
/* 118:154 */           break;
/* 119:    */         case 2: 
/* 120:156 */           comps.addAll(((MetaBean)temp.getBean()).getBeansInOutputs());
/* 121:    */         }
/* 122:    */       } else {
/* 123:160 */         comps.add(temp);
/* 124:    */       }
/* 125:    */     }
/* 126:163 */     return comps;
/* 127:    */   }
/* 128:    */   
/* 129:    */   private boolean beanSetContains(Vector<Object> set, BeanInstance toCheck)
/* 130:    */   {
/* 131:167 */     boolean ok = false;
/* 132:169 */     for (int i = 0; i < set.size(); i++)
/* 133:    */     {
/* 134:170 */       BeanInstance temp = (BeanInstance)set.elementAt(i);
/* 135:171 */       if (toCheck == temp)
/* 136:    */       {
/* 137:172 */         ok = true;
/* 138:173 */         break;
/* 139:    */       }
/* 140:    */     }
/* 141:176 */     return ok;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public boolean subFlowContains(BeanInstance toCheck)
/* 145:    */   {
/* 146:180 */     return beanSetContains(this.m_subFlow, toCheck);
/* 147:    */   }
/* 148:    */   
/* 149:    */   public boolean inputsContains(BeanInstance toCheck)
/* 150:    */   {
/* 151:184 */     return beanSetContains(this.m_inputs, toCheck);
/* 152:    */   }
/* 153:    */   
/* 154:    */   public boolean outputsContains(BeanInstance toCheck)
/* 155:    */   {
/* 156:188 */     return beanSetContains(this.m_outputs, toCheck);
/* 157:    */   }
/* 158:    */   
/* 159:    */   public Vector<Object> getBeansInSubFlow()
/* 160:    */   {
/* 161:197 */     return getBeans(this.m_subFlow, 0);
/* 162:    */   }
/* 163:    */   
/* 164:    */   public Vector<Object> getBeansInInputs()
/* 165:    */   {
/* 166:206 */     return getBeans(this.m_inputs, 1);
/* 167:    */   }
/* 168:    */   
/* 169:    */   public Vector<Object> getBeansInOutputs()
/* 170:    */   {
/* 171:215 */     return getBeans(this.m_outputs, 2);
/* 172:    */   }
/* 173:    */   
/* 174:    */   private Vector<BeanInfo> getBeanInfos(Vector<Object> beans, int type)
/* 175:    */   {
/* 176:219 */     Vector<BeanInfo> infos = new Vector();
/* 177:220 */     for (int i = 0; i < beans.size(); i++)
/* 178:    */     {
/* 179:221 */       BeanInstance temp = (BeanInstance)beans.elementAt(i);
/* 180:222 */       if ((temp.getBean() instanceof MetaBean)) {
/* 181:223 */         switch (type)
/* 182:    */         {
/* 183:    */         case 0: 
/* 184:225 */           infos.addAll(((MetaBean)temp.getBean()).getBeanInfoSubFlow());
/* 185:226 */           break;
/* 186:    */         case 1: 
/* 187:228 */           infos.addAll(((MetaBean)temp.getBean()).getBeanInfoInputs());
/* 188:229 */           break;
/* 189:    */         case 2: 
/* 190:231 */           infos.addAll(((MetaBean)temp.getBean()).getBeanInfoOutputs());
/* 191:    */         }
/* 192:    */       } else {
/* 193:    */         try
/* 194:    */         {
/* 195:235 */           infos.add(Introspector.getBeanInfo(temp.getBean().getClass()));
/* 196:    */         }
/* 197:    */         catch (IntrospectionException ex)
/* 198:    */         {
/* 199:237 */           ex.printStackTrace();
/* 200:    */         }
/* 201:    */       }
/* 202:    */     }
/* 203:241 */     return infos;
/* 204:    */   }
/* 205:    */   
/* 206:    */   public Vector<BeanInfo> getBeanInfoSubFlow()
/* 207:    */   {
/* 208:245 */     return getBeanInfos(this.m_subFlow, 0);
/* 209:    */   }
/* 210:    */   
/* 211:    */   public Vector<BeanInfo> getBeanInfoInputs()
/* 212:    */   {
/* 213:249 */     return getBeanInfos(this.m_inputs, 1);
/* 214:    */   }
/* 215:    */   
/* 216:    */   public Vector<BeanInfo> getBeanInfoOutputs()
/* 217:    */   {
/* 218:253 */     return getBeanInfos(this.m_outputs, 2);
/* 219:    */   }
/* 220:    */   
/* 221:    */   public Vector<Point> getOriginalCoords()
/* 222:    */   {
/* 223:268 */     return this.m_originalCoords;
/* 224:    */   }
/* 225:    */   
/* 226:    */   public void setOriginalCoords(Vector<Point> value)
/* 227:    */   {
/* 228:279 */     this.m_originalCoords = value;
/* 229:    */   }
/* 230:    */   
/* 231:    */   public void shiftBeans(BeanInstance toShiftTo, boolean save)
/* 232:    */   {
/* 233:294 */     if (save) {
/* 234:295 */       this.m_originalCoords = new Vector();
/* 235:    */     }
/* 236:297 */     int targetX = toShiftTo.getX();
/* 237:298 */     int targetY = toShiftTo.getY();
/* 238:300 */     for (int i = 0; i < this.m_subFlow.size(); i++)
/* 239:    */     {
/* 240:301 */       BeanInstance temp = (BeanInstance)this.m_subFlow.elementAt(i);
/* 241:302 */       if (save)
/* 242:    */       {
/* 243:304 */         Point p = new Point(temp.getX() - targetX, temp.getY() - targetY);
/* 244:305 */         this.m_originalCoords.add(p);
/* 245:    */       }
/* 246:307 */       temp.setX(targetX);
/* 247:308 */       temp.setY(targetY);
/* 248:    */     }
/* 249:    */   }
/* 250:    */   
/* 251:    */   public void restoreBeans(int x, int y)
/* 252:    */   {
/* 253:313 */     for (int i = 0; i < this.m_subFlow.size(); i++)
/* 254:    */     {
/* 255:314 */       BeanInstance temp = (BeanInstance)this.m_subFlow.elementAt(i);
/* 256:315 */       Point p = (Point)this.m_originalCoords.elementAt(i);
/* 257:316 */       JComponent c = (JComponent)temp.getBean();
/* 258:317 */       c.getPreferredSize();
/* 259:318 */       temp.setX(x + (int)p.getX());
/* 260:319 */       temp.setY(y + (int)p.getY());
/* 261:    */     }
/* 262:    */   }
/* 263:    */   
/* 264:    */   public boolean eventGeneratable(EventSetDescriptor esd)
/* 265:    */   {
/* 266:331 */     String eventName = esd.getName();
/* 267:332 */     return eventGeneratable(eventName);
/* 268:    */   }
/* 269:    */   
/* 270:    */   public boolean eventGeneratable(String eventName)
/* 271:    */   {
/* 272:345 */     for (int i = 0; i < this.m_subFlow.size(); i++)
/* 273:    */     {
/* 274:346 */       BeanInstance output = (BeanInstance)this.m_subFlow.elementAt(i);
/* 275:347 */       if (((output.getBean() instanceof EventConstraints)) && 
/* 276:348 */         (((EventConstraints)output.getBean()).eventGeneratable(eventName))) {
/* 277:349 */         return true;
/* 278:    */       }
/* 279:    */     }
/* 280:353 */     return false;
/* 281:    */   }
/* 282:    */   
/* 283:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/* 284:    */   {
/* 285:365 */     Vector<BeanInstance> targets = getSuitableTargets(esd);
/* 286:366 */     for (int i = 0; i < targets.size(); i++)
/* 287:    */     {
/* 288:367 */       BeanInstance input = (BeanInstance)targets.elementAt(i);
/* 289:368 */       if ((input.getBean() instanceof BeanCommon))
/* 290:    */       {
/* 291:370 */         if (((BeanCommon)input.getBean()).connectionAllowed(esd)) {
/* 292:371 */           return true;
/* 293:    */         }
/* 294:    */       }
/* 295:    */       else {
/* 296:374 */         return true;
/* 297:    */       }
/* 298:    */     }
/* 299:377 */     return false;
/* 300:    */   }
/* 301:    */   
/* 302:    */   public boolean connectionAllowed(String eventName)
/* 303:    */   {
/* 304:382 */     return false;
/* 305:    */   }
/* 306:    */   
/* 307:    */   public synchronized void connectionNotification(String eventName, Object source) {}
/* 308:    */   
/* 309:    */   public synchronized void disconnectionNotification(String eventName, Object source) {}
/* 310:    */   
/* 311:    */   public void stop()
/* 312:    */   {
/* 313:421 */     for (int i = 0; i < this.m_subFlow.size(); i++)
/* 314:    */     {
/* 315:422 */       Object temp = this.m_subFlow.elementAt(i);
/* 316:423 */       if ((temp instanceof BeanCommon)) {
/* 317:424 */         ((BeanCommon)temp).stop();
/* 318:    */       }
/* 319:    */     }
/* 320:    */   }
/* 321:    */   
/* 322:    */   public boolean isBusy()
/* 323:    */   {
/* 324:437 */     boolean result = false;
/* 325:438 */     for (int i = 0; i < this.m_subFlow.size(); i++)
/* 326:    */     {
/* 327:439 */       Object temp = this.m_subFlow.elementAt(i);
/* 328:440 */       if (((temp instanceof BeanCommon)) && 
/* 329:441 */         (((BeanCommon)temp).isBusy()))
/* 330:    */       {
/* 331:442 */         result = true;
/* 332:443 */         break;
/* 333:    */       }
/* 334:    */     }
/* 335:447 */     return result;
/* 336:    */   }
/* 337:    */   
/* 338:    */   public void setVisual(BeanVisual newVisual)
/* 339:    */   {
/* 340:457 */     this.m_visual = newVisual;
/* 341:    */   }
/* 342:    */   
/* 343:    */   public BeanVisual getVisual()
/* 344:    */   {
/* 345:465 */     return this.m_visual;
/* 346:    */   }
/* 347:    */   
/* 348:    */   public void useDefaultVisual()
/* 349:    */   {
/* 350:473 */     this.m_visual.loadIcons("weka/gui/beans/icons/DiamondPlain.gif", "weka/gui/beans/icons/DiamondPlain.gif");
/* 351:    */   }
/* 352:    */   
/* 353:    */   public String getStartMessage()
/* 354:    */   {
/* 355:479 */     String message = "Start loading";
/* 356:480 */     for (int i = 0; i < this.m_subFlow.size(); i++)
/* 357:    */     {
/* 358:481 */       BeanInstance temp = (BeanInstance)this.m_subFlow.elementAt(i);
/* 359:482 */       if ((temp.getBean() instanceof Startable))
/* 360:    */       {
/* 361:483 */         String s = ((Startable)temp.getBean()).getStartMessage();
/* 362:484 */         if (s.startsWith("$"))
/* 363:    */         {
/* 364:485 */           message = "$" + message;
/* 365:486 */           break;
/* 366:    */         }
/* 367:    */       }
/* 368:    */     }
/* 369:491 */     return message;
/* 370:    */   }
/* 371:    */   
/* 372:    */   public void start()
/* 373:    */   {
/* 374:496 */     TreeMap<Integer, Startable> startables = new TreeMap();
/* 375:497 */     for (int i = 0; i < this.m_subFlow.size(); i++)
/* 376:    */     {
/* 377:498 */       BeanInstance temp = (BeanInstance)this.m_subFlow.elementAt(i);
/* 378:499 */       if ((temp.getBean() instanceof Startable))
/* 379:    */       {
/* 380:500 */         Startable s = (Startable)temp.getBean();
/* 381:501 */         String beanName = s.getClass().getName();
/* 382:502 */         String customName = beanName;
/* 383:    */         
/* 384:504 */         boolean ok = false;
/* 385:505 */         Integer position = null;
/* 386:506 */         boolean launch = true;
/* 387:508 */         if ((s instanceof BeanCommon))
/* 388:    */         {
/* 389:509 */           customName = ((BeanCommon)s).getCustomName();
/* 390:510 */           beanName = customName;
/* 391:512 */           if (customName.indexOf(':') > 0) {
/* 392:513 */             if (customName.substring(0, customName.indexOf(':')).startsWith("!"))
/* 393:    */             {
/* 394:515 */               launch = false;
/* 395:    */             }
/* 396:    */             else
/* 397:    */             {
/* 398:517 */               String startPos = customName.substring(0, customName.indexOf(':'));
/* 399:    */               try
/* 400:    */               {
/* 401:521 */                 position = new Integer(startPos);
/* 402:522 */                 ok = true;
/* 403:    */               }
/* 404:    */               catch (NumberFormatException n) {}
/* 405:    */             }
/* 406:    */           }
/* 407:    */         }
/* 408:529 */         if ((!ok) && (launch)) {
/* 409:530 */           if (startables.size() == 0)
/* 410:    */           {
/* 411:531 */             position = new Integer(0);
/* 412:    */           }
/* 413:    */           else
/* 414:    */           {
/* 415:533 */             int newPos = ((Integer)startables.lastKey()).intValue();
/* 416:534 */             newPos++;
/* 417:535 */             position = new Integer(newPos);
/* 418:    */           }
/* 419:    */         }
/* 420:539 */         if ((s.getStartMessage().charAt(0) != '$') && 
/* 421:540 */           (launch))
/* 422:    */         {
/* 423:541 */           if (this.m_log != null) {
/* 424:542 */             this.m_log.logMessage(statusMessagePrefix() + "adding start point " + beanName + " to the execution list (position " + position + ")");
/* 425:    */           }
/* 426:546 */           startables.put(position, s);
/* 427:    */         }
/* 428:    */       }
/* 429:    */     }
/* 430:552 */     if (startables.size() > 0)
/* 431:    */     {
/* 432:553 */       if (this.m_log != null) {
/* 433:554 */         this.m_log.logMessage(statusMessagePrefix() + "Starting " + startables.size() + " sub-flow start points sequentially.");
/* 434:    */       }
/* 435:557 */       Set<Integer> s = startables.keySet();
/* 436:558 */       for (Integer i : s) {
/* 437:    */         try
/* 438:    */         {
/* 439:560 */           Startable startPoint = (Startable)startables.get(i);
/* 440:561 */           String bN = startPoint.getClass().getName();
/* 441:562 */           if ((startPoint instanceof BeanCommon)) {
/* 442:563 */             bN = ((BeanCommon)startPoint).getCustomName();
/* 443:    */           }
/* 444:565 */           if (this.m_log != null)
/* 445:    */           {
/* 446:566 */             this.m_log.statusMessage(statusMessagePrefix() + "Starting sub-flow start point: " + bN);
/* 447:    */             
/* 448:568 */             this.m_log.logMessage(statusMessagePrefix() + "Starting sub-flow start point: " + bN);
/* 449:    */           }
/* 450:571 */           startPoint.start();
/* 451:572 */           Thread.sleep(500L);
/* 452:573 */           while (isBusy()) {
/* 453:574 */             Thread.sleep(2000L);
/* 454:    */           }
/* 455:    */         }
/* 456:    */         catch (Exception ex)
/* 457:    */         {
/* 458:577 */           if (this.m_log != null) {
/* 459:578 */             this.m_log.logMessage(statusMessagePrefix() + "A problem occurred when launching start points in sub-flow: " + ex.getMessage());
/* 460:    */           }
/* 461:582 */           stop();
/* 462:583 */           if (this.m_log != null) {
/* 463:584 */             this.m_log.statusMessage(statusMessagePrefix() + "ERROR (see log for details)");
/* 464:    */           }
/* 465:    */         }
/* 466:    */       }
/* 467:589 */       if (this.m_log != null) {
/* 468:590 */         this.m_log.statusMessage(statusMessagePrefix() + "Finished.");
/* 469:    */       }
/* 470:    */     }
/* 471:    */   }
/* 472:    */   
/* 473:    */   public Enumeration<String> enumerateRequests()
/* 474:    */   {
/* 475:602 */     Vector<String> newVector = new Vector();
/* 476:603 */     if (this.m_subFlowPreview != null)
/* 477:    */     {
/* 478:604 */       String text = "Show preview";
/* 479:605 */       if (this.m_previewWindow != null) {
/* 480:606 */         text = "$" + text;
/* 481:    */       }
/* 482:608 */       newVector.addElement(text);
/* 483:    */     }
/* 484:610 */     for (int i = 0; i < this.m_subFlow.size(); i++)
/* 485:    */     {
/* 486:611 */       BeanInstance temp = (BeanInstance)this.m_subFlow.elementAt(i);
/* 487:612 */       if ((temp.getBean() instanceof UserRequestAcceptor))
/* 488:    */       {
/* 489:613 */         String prefix = "";
/* 490:614 */         if ((temp.getBean() instanceof BeanCommon))
/* 491:    */         {
/* 492:615 */           prefix = ((BeanCommon)temp.getBean()).getCustomName();
/* 493:    */         }
/* 494:    */         else
/* 495:    */         {
/* 496:617 */           prefix = temp.getBean().getClass().getName();
/* 497:618 */           prefix = prefix.substring(prefix.lastIndexOf('.') + 1, prefix.length());
/* 498:    */         }
/* 499:621 */         prefix = "" + (i + 1) + ": (" + prefix + ")";
/* 500:622 */         Enumeration<String> en = ((UserRequestAcceptor)temp.getBean()).enumerateRequests();
/* 501:624 */         while (en.hasMoreElements())
/* 502:    */         {
/* 503:625 */           String req = (String)en.nextElement();
/* 504:626 */           if (req.charAt(0) == '$')
/* 505:    */           {
/* 506:627 */             prefix = '$' + prefix;
/* 507:628 */             req = req.substring(1, req.length());
/* 508:    */           }
/* 509:631 */           if (req.charAt(0) == '?')
/* 510:    */           {
/* 511:632 */             prefix = '?' + prefix;
/* 512:633 */             req = req.substring(1, req.length());
/* 513:    */           }
/* 514:635 */           newVector.add(prefix + " " + req);
/* 515:    */         }
/* 516:    */       }
/* 517:637 */       else if ((temp.getBean() instanceof Startable))
/* 518:    */       {
/* 519:638 */         String prefix = "";
/* 520:639 */         if ((temp.getBean() instanceof BeanCommon))
/* 521:    */         {
/* 522:640 */           prefix = ((BeanCommon)temp.getBean()).getCustomName();
/* 523:    */         }
/* 524:    */         else
/* 525:    */         {
/* 526:642 */           prefix = temp.getBean().getClass().getName();
/* 527:643 */           prefix = prefix.substring(prefix.lastIndexOf('.') + 1, prefix.length());
/* 528:    */         }
/* 529:646 */         prefix = "" + (i + 1) + ": (" + prefix + ")";
/* 530:647 */         String startMessage = ((Startable)temp.getBean()).getStartMessage();
/* 531:648 */         if (startMessage.charAt(0) == '$')
/* 532:    */         {
/* 533:649 */           prefix = '$' + prefix;
/* 534:650 */           startMessage = startMessage.substring(1, startMessage.length());
/* 535:    */         }
/* 536:652 */         newVector.add(prefix + " " + startMessage);
/* 537:    */       }
/* 538:    */     }
/* 539:656 */     return newVector.elements();
/* 540:    */   }
/* 541:    */   
/* 542:    */   public void setSubFlowPreview(ImageIcon sfp)
/* 543:    */   {
/* 544:660 */     this.m_subFlowPreview = sfp;
/* 545:    */   }
/* 546:    */   
/* 547:    */   private void showPreview()
/* 548:    */   {
/* 549:664 */     if (this.m_previewWindow == null)
/* 550:    */     {
/* 551:666 */       JLabel jl = new JLabel(this.m_subFlowPreview);
/* 552:    */       
/* 553:668 */       jl.setLocation(0, 0);
/* 554:669 */       this.m_previewWindow = new JWindow();
/* 555:    */       
/* 556:671 */       this.m_previewWindow.getContentPane().add(jl);
/* 557:672 */       this.m_previewWindow.validate();
/* 558:673 */       this.m_previewWindow.setSize(this.m_subFlowPreview.getIconWidth(), this.m_subFlowPreview.getIconHeight());
/* 559:    */       
/* 560:    */ 
/* 561:676 */       this.m_previewWindow.addMouseListener(new MouseAdapter()
/* 562:    */       {
/* 563:    */         public void mouseClicked(MouseEvent e)
/* 564:    */         {
/* 565:679 */           MetaBean.this.m_previewWindow.dispose();
/* 566:680 */           MetaBean.this.m_previewWindow = null;
/* 567:    */         }
/* 568:683 */       });
/* 569:684 */       this.m_previewWindow.setLocation(getParent().getLocationOnScreen().x + getX() + getWidth() / 2 - this.m_subFlowPreview.getIconWidth() / 2, getParent().getLocationOnScreen().y + getY() + getHeight() / 2 - this.m_subFlowPreview.getIconHeight() / 2);
/* 570:    */       
/* 571:    */ 
/* 572:    */ 
/* 573:    */ 
/* 574:689 */       this.m_previewWindow.setVisible(true);
/* 575:690 */       this.m_previewTimer = new Timer(8000, new ActionListener()
/* 576:    */       {
/* 577:    */         public void actionPerformed(ActionEvent e)
/* 578:    */         {
/* 579:694 */           if (MetaBean.this.m_previewWindow != null)
/* 580:    */           {
/* 581:695 */             MetaBean.this.m_previewWindow.dispose();
/* 582:696 */             MetaBean.this.m_previewWindow = null;
/* 583:697 */             MetaBean.this.m_previewTimer = null;
/* 584:    */           }
/* 585:    */         }
/* 586:700 */       });
/* 587:701 */       this.m_previewTimer.setRepeats(false);
/* 588:702 */       this.m_previewTimer.start();
/* 589:    */     }
/* 590:    */   }
/* 591:    */   
/* 592:    */   public void performRequest(String request)
/* 593:    */   {
/* 594:714 */     if (request.compareTo("Show preview") == 0)
/* 595:    */     {
/* 596:715 */       showPreview();
/* 597:716 */       return;
/* 598:    */     }
/* 599:719 */     if (request.indexOf(":") < 0) {
/* 600:720 */       return;
/* 601:    */     }
/* 602:722 */     String tempI = request.substring(0, request.indexOf(':'));
/* 603:723 */     int index = Integer.parseInt(tempI);
/* 604:724 */     index--;
/* 605:725 */     String req = request.substring(request.indexOf(')') + 1, request.length()).trim();
/* 606:    */     
/* 607:    */ 
/* 608:728 */     Object target = ((BeanInstance)this.m_subFlow.elementAt(index)).getBean();
/* 609:729 */     if (((target instanceof Startable)) && (req.equals(((Startable)target).getStartMessage()))) {
/* 610:    */       try
/* 611:    */       {
/* 612:732 */         ((Startable)target).start();
/* 613:    */       }
/* 614:    */       catch (Exception ex)
/* 615:    */       {
/* 616:734 */         if (this.m_log != null)
/* 617:    */         {
/* 618:735 */           String compName = (target instanceof BeanCommon) ? ((BeanCommon)target).getCustomName() : "";
/* 619:    */           
/* 620:737 */           this.m_log.logMessage("Problem starting subcomponent " + compName);
/* 621:    */         }
/* 622:    */       }
/* 623:    */     } else {
/* 624:741 */       ((UserRequestAcceptor)target).performRequest(req);
/* 625:    */     }
/* 626:    */   }
/* 627:    */   
/* 628:    */   public void setLog(Logger logger)
/* 629:    */   {
/* 630:752 */     this.m_log = logger;
/* 631:    */   }
/* 632:    */   
/* 633:    */   public void removePropertyChangeListenersSubFlow(PropertyChangeListener pcl)
/* 634:    */   {
/* 635:756 */     for (int i = 0; i < this.m_subFlow.size(); i++)
/* 636:    */     {
/* 637:757 */       BeanInstance temp = (BeanInstance)this.m_subFlow.elementAt(i);
/* 638:758 */       if ((temp.getBean() instanceof Visible)) {
/* 639:759 */         ((Visible)temp.getBean()).getVisual().removePropertyChangeListener(pcl);
/* 640:    */       }
/* 641:762 */       if ((temp.getBean() instanceof MetaBean)) {
/* 642:763 */         ((MetaBean)temp.getBean()).removePropertyChangeListenersSubFlow(pcl);
/* 643:    */       }
/* 644:    */     }
/* 645:    */   }
/* 646:    */   
/* 647:    */   public void addPropertyChangeListenersSubFlow(PropertyChangeListener pcl)
/* 648:    */   {
/* 649:769 */     for (int i = 0; i < this.m_subFlow.size(); i++)
/* 650:    */     {
/* 651:770 */       BeanInstance temp = (BeanInstance)this.m_subFlow.elementAt(i);
/* 652:771 */       if ((temp.getBean() instanceof Visible)) {
/* 653:772 */         ((Visible)temp.getBean()).getVisual().addPropertyChangeListener(pcl);
/* 654:    */       }
/* 655:774 */       if ((temp.getBean() instanceof MetaBean)) {
/* 656:775 */         ((MetaBean)temp.getBean()).addPropertyChangeListenersSubFlow(pcl);
/* 657:    */       }
/* 658:    */     }
/* 659:    */   }
/* 660:    */   
/* 661:    */   public boolean canAcceptConnection(Class<?> listenerClass)
/* 662:    */   {
/* 663:787 */     for (int i = 0; i < this.m_inputs.size(); i++)
/* 664:    */     {
/* 665:788 */       BeanInstance input = (BeanInstance)this.m_inputs.elementAt(i);
/* 666:789 */       if (listenerClass.isInstance(input.getBean())) {
/* 667:790 */         return true;
/* 668:    */       }
/* 669:    */     }
/* 670:793 */     return false;
/* 671:    */   }
/* 672:    */   
/* 673:    */   public Vector<BeanInstance> getSuitableTargets(EventSetDescriptor esd)
/* 674:    */   {
/* 675:803 */     Class<?> listenerClass = esd.getListenerType();
/* 676:804 */     Vector<BeanInstance> targets = new Vector();
/* 677:805 */     for (int i = 0; i < this.m_inputs.size(); i++)
/* 678:    */     {
/* 679:806 */       BeanInstance input = (BeanInstance)this.m_inputs.elementAt(i);
/* 680:807 */       if (listenerClass.isInstance(input.getBean())) {
/* 681:808 */         targets.add(input);
/* 682:    */       }
/* 683:    */     }
/* 684:811 */     return targets;
/* 685:    */   }
/* 686:    */   
/* 687:    */   private String statusMessagePrefix()
/* 688:    */   {
/* 689:815 */     return getCustomName() + "$" + hashCode() + "|";
/* 690:    */   }
/* 691:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.MetaBean
 * JD-Core Version:    0.7.0.1
 */