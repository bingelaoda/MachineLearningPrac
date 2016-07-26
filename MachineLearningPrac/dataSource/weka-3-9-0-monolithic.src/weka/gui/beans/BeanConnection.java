/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Graphics;
/*   5:    */ import java.awt.Point;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.beans.BeanInfo;
/*   9:    */ import java.beans.EventSetDescriptor;
/*  10:    */ import java.beans.IntrospectionException;
/*  11:    */ import java.beans.Introspector;
/*  12:    */ import java.io.PrintStream;
/*  13:    */ import java.io.Serializable;
/*  14:    */ import java.lang.reflect.Method;
/*  15:    */ import java.util.ArrayList;
/*  16:    */ import java.util.Vector;
/*  17:    */ import javax.swing.JComponent;
/*  18:    */ import javax.swing.JLabel;
/*  19:    */ import javax.swing.JMenuItem;
/*  20:    */ import javax.swing.JPopupMenu;
/*  21:    */ 
/*  22:    */ public class BeanConnection
/*  23:    */   implements Serializable
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = 8804264241791332064L;
/*  26: 56 */   private static ArrayList<Vector<BeanConnection>> TABBED_CONNECTIONS = new ArrayList();
/*  27:    */   private final BeanInstance m_source;
/*  28:    */   private final BeanInstance m_target;
/*  29:    */   private final String m_eventName;
/*  30: 72 */   private boolean m_hidden = false;
/*  31:    */   
/*  32:    */   public static void init()
/*  33:    */   {
/*  34: 84 */     TABBED_CONNECTIONS.clear();
/*  35: 85 */     TABBED_CONNECTIONS.add(new Vector());
/*  36:    */   }
/*  37:    */   
/*  38:    */   public static Vector<BeanConnection> getConnections(Integer... tab)
/*  39:    */   {
/*  40: 94 */     Vector<BeanConnection> returnV = null;
/*  41: 95 */     int index = 0;
/*  42: 96 */     if (tab.length > 0) {
/*  43: 97 */       index = tab[0].intValue();
/*  44:    */     }
/*  45:100 */     if (TABBED_CONNECTIONS.size() > 0) {
/*  46:101 */       returnV = (Vector)TABBED_CONNECTIONS.get(index);
/*  47:    */     }
/*  48:104 */     return returnV;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static void setConnections(Vector<BeanConnection> connections, Integer... tab)
/*  52:    */   {
/*  53:114 */     int index = 0;
/*  54:115 */     if (tab.length > 0) {
/*  55:116 */       index = tab[0].intValue();
/*  56:    */     }
/*  57:119 */     if (index < TABBED_CONNECTIONS.size()) {
/*  58:120 */       TABBED_CONNECTIONS.set(index, connections);
/*  59:    */     }
/*  60:    */   }
/*  61:    */   
/*  62:    */   public static void addConnections(Vector<BeanConnection> connections)
/*  63:    */   {
/*  64:131 */     TABBED_CONNECTIONS.add(connections);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public static void appendConnections(Vector<BeanConnection> connections, int tab)
/*  68:    */   {
/*  69:142 */     if (tab < TABBED_CONNECTIONS.size())
/*  70:    */     {
/*  71:143 */       Vector<BeanConnection> cons = (Vector)TABBED_CONNECTIONS.get(tab);
/*  72:145 */       for (int i = 0; i < connections.size(); i++) {
/*  73:146 */         cons.add(connections.get(i));
/*  74:    */       }
/*  75:    */     }
/*  76:    */   }
/*  77:    */   
/*  78:    */   private static boolean previousLink(BeanInstance source, BeanInstance target, int index, Integer... tab)
/*  79:    */   {
/*  80:162 */     int tabIndex = 0;
/*  81:163 */     if (tab.length > 0) {
/*  82:164 */       tabIndex = tab[0].intValue();
/*  83:    */     }
/*  84:167 */     Vector<BeanConnection> connections = (Vector)TABBED_CONNECTIONS.get(tabIndex);
/*  85:169 */     for (int i = 0; i < connections.size(); i++)
/*  86:    */     {
/*  87:170 */       BeanConnection bc = (BeanConnection)connections.elementAt(i);
/*  88:171 */       BeanInstance compSource = bc.getSource();
/*  89:172 */       BeanInstance compTarget = bc.getTarget();
/*  90:174 */       if ((compSource == source) && (compTarget == target) && (index < i)) {
/*  91:175 */         return true;
/*  92:    */       }
/*  93:    */     }
/*  94:178 */     return false;
/*  95:    */   }
/*  96:    */   
/*  97:    */   private static boolean checkTargetConstraint(BeanInstance candidate, Vector<Object> listToCheck, Integer... tab)
/*  98:    */   {
/*  99:187 */     int tabIndex = 0;
/* 100:188 */     if (tab.length > 0) {
/* 101:189 */       tabIndex = tab[0].intValue();
/* 102:    */     }
/* 103:192 */     Vector<BeanConnection> connections = (Vector)TABBED_CONNECTIONS.get(tabIndex);
/* 104:194 */     for (int i = 0; i < connections.size(); i++)
/* 105:    */     {
/* 106:195 */       BeanConnection bc = (BeanConnection)connections.elementAt(i);
/* 107:196 */       if (bc.getTarget() == candidate) {
/* 108:197 */         for (int j = 0; j < listToCheck.size(); j++)
/* 109:    */         {
/* 110:198 */           BeanInstance tempSource = (BeanInstance)listToCheck.elementAt(j);
/* 111:199 */           if (bc.getSource() == tempSource) {
/* 112:200 */             return false;
/* 113:    */           }
/* 114:    */         }
/* 115:    */       }
/* 116:    */     }
/* 117:205 */     return true;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public static Vector<BeanConnection> associatedConnections(Vector<Object> subFlow, Integer... tab)
/* 121:    */   {
/* 122:218 */     int tabIndex = 0;
/* 123:219 */     if (tab.length > 0) {
/* 124:220 */       tabIndex = tab[0].intValue();
/* 125:    */     }
/* 126:223 */     Vector<BeanConnection> connections = (Vector)TABBED_CONNECTIONS.get(tabIndex);
/* 127:    */     
/* 128:225 */     Vector<BeanConnection> associatedConnections = new Vector();
/* 129:226 */     for (int i = 0; i < connections.size(); i++)
/* 130:    */     {
/* 131:227 */       BeanConnection bc = (BeanConnection)connections.elementAt(i);
/* 132:228 */       BeanInstance tempSource = bc.getSource();
/* 133:229 */       BeanInstance tempTarget = bc.getTarget();
/* 134:230 */       boolean sourceInSubFlow = false;
/* 135:231 */       boolean targetInSubFlow = false;
/* 136:232 */       for (int j = 0; j < subFlow.size(); j++)
/* 137:    */       {
/* 138:233 */         BeanInstance toCheck = (BeanInstance)subFlow.elementAt(j);
/* 139:234 */         if (toCheck == tempSource) {
/* 140:235 */           sourceInSubFlow = true;
/* 141:    */         }
/* 142:237 */         if (toCheck == tempTarget) {
/* 143:238 */           targetInSubFlow = true;
/* 144:    */         }
/* 145:240 */         if ((sourceInSubFlow) && (targetInSubFlow))
/* 146:    */         {
/* 147:241 */           associatedConnections.add(bc);
/* 148:242 */           break;
/* 149:    */         }
/* 150:    */       }
/* 151:    */     }
/* 152:246 */     return associatedConnections;
/* 153:    */   }
/* 154:    */   
/* 155:    */   public static Vector<Object> inputs(Vector<Object> subset, Integer... tab)
/* 156:    */   {
/* 157:257 */     Vector<Object> result = new Vector();
/* 158:258 */     for (int i = 0; i < subset.size(); i++)
/* 159:    */     {
/* 160:259 */       BeanInstance temp = (BeanInstance)subset.elementAt(i);
/* 161:262 */       if (checkTargetConstraint(temp, subset, tab)) {
/* 162:263 */         result.add(temp);
/* 163:    */       }
/* 164:    */     }
/* 165:267 */     return result;
/* 166:    */   }
/* 167:    */   
/* 168:    */   private static boolean checkForTarget(BeanInstance candidate, Vector<Object> listToCheck, Integer... tab)
/* 169:    */   {
/* 170:276 */     int tabIndex = 0;
/* 171:277 */     if (tab.length > 0) {
/* 172:278 */       tabIndex = tab[0].intValue();
/* 173:    */     }
/* 174:281 */     Vector<BeanConnection> connections = (Vector)TABBED_CONNECTIONS.get(tabIndex);
/* 175:283 */     for (int i = 0; i < connections.size(); i++)
/* 176:    */     {
/* 177:284 */       BeanConnection bc = (BeanConnection)connections.elementAt(i);
/* 178:285 */       if (bc.getTarget() == candidate) {
/* 179:290 */         for (int j = 0; j < listToCheck.size(); j++)
/* 180:    */         {
/* 181:291 */           BeanInstance tempSource = (BeanInstance)listToCheck.elementAt(j);
/* 182:292 */           if (bc.getSource() == tempSource) {
/* 183:293 */             return true;
/* 184:    */           }
/* 185:    */         }
/* 186:    */       }
/* 187:    */     }
/* 188:297 */     return false;
/* 189:    */   }
/* 190:    */   
/* 191:    */   private static boolean isInList(BeanInstance candidate, Vector<Object> listToCheck)
/* 192:    */   {
/* 193:302 */     for (int i = 0; i < listToCheck.size(); i++)
/* 194:    */     {
/* 195:303 */       BeanInstance temp = (BeanInstance)listToCheck.elementAt(i);
/* 196:304 */       if (candidate == temp) {
/* 197:305 */         return true;
/* 198:    */       }
/* 199:    */     }
/* 200:308 */     return false;
/* 201:    */   }
/* 202:    */   
/* 203:    */   private static boolean checkSourceConstraint(BeanInstance candidate, Vector<Object> listToCheck, Integer... tab)
/* 204:    */   {
/* 205:317 */     int tabIndex = 0;
/* 206:318 */     if (tab.length > 0) {
/* 207:319 */       tabIndex = tab[0].intValue();
/* 208:    */     }
/* 209:322 */     Vector<BeanConnection> connections = (Vector)TABBED_CONNECTIONS.get(tabIndex);
/* 210:    */     
/* 211:324 */     boolean result = true;
/* 212:325 */     for (int i = 0; i < connections.size(); i++)
/* 213:    */     {
/* 214:326 */       BeanConnection bc = (BeanConnection)connections.elementAt(i);
/* 215:327 */       if (bc.getSource() == candidate)
/* 216:    */       {
/* 217:328 */         BeanInstance cTarget = bc.getTarget();
/* 218:330 */         if (!isInList(cTarget, listToCheck)) {
/* 219:331 */           return true;
/* 220:    */         }
/* 221:333 */         for (int j = 0; j < listToCheck.size(); j++)
/* 222:    */         {
/* 223:334 */           BeanInstance tempTarget = (BeanInstance)listToCheck.elementAt(j);
/* 224:335 */           if (bc.getTarget() == tempTarget) {
/* 225:336 */             result = false;
/* 226:    */           }
/* 227:    */         }
/* 228:    */       }
/* 229:    */     }
/* 230:341 */     return result;
/* 231:    */   }
/* 232:    */   
/* 233:    */   public static Vector<Object> outputs(Vector<Object> subset, Integer... tab)
/* 234:    */   {
/* 235:352 */     Vector<Object> result = new Vector();
/* 236:353 */     for (int i = 0; i < subset.size(); i++)
/* 237:    */     {
/* 238:354 */       BeanInstance temp = (BeanInstance)subset.elementAt(i);
/* 239:355 */       if (checkForTarget(temp, subset, tab)) {
/* 240:357 */         if (checkSourceConstraint(temp, subset, tab)) {
/* 241:    */           try
/* 242:    */           {
/* 243:360 */             BeanInfo bi = Introspector.getBeanInfo(temp.getBean().getClass());
/* 244:361 */             EventSetDescriptor[] esd = bi.getEventSetDescriptors();
/* 245:362 */             if ((esd != null) && (esd.length > 0)) {
/* 246:363 */               result.add(temp);
/* 247:    */             }
/* 248:    */           }
/* 249:    */           catch (IntrospectionException ex) {}
/* 250:    */         }
/* 251:    */       }
/* 252:    */     }
/* 253:371 */     return result;
/* 254:    */   }
/* 255:    */   
/* 256:    */   public static void paintConnections(Graphics gx, Integer... tab)
/* 257:    */   {
/* 258:380 */     int tabIndex = 0;
/* 259:381 */     if (tab.length > 0) {
/* 260:382 */       tabIndex = tab[0].intValue();
/* 261:    */     }
/* 262:385 */     Vector<BeanConnection> connections = (Vector)TABBED_CONNECTIONS.get(tabIndex);
/* 263:387 */     for (int i = 0; i < connections.size(); i++)
/* 264:    */     {
/* 265:388 */       BeanConnection bc = (BeanConnection)connections.elementAt(i);
/* 266:389 */       if (!bc.isHidden())
/* 267:    */       {
/* 268:390 */         BeanInstance source = bc.getSource();
/* 269:391 */         BeanInstance target = bc.getTarget();
/* 270:392 */         EventSetDescriptor srcEsd = bc.getSourceEventSetDescriptor();
/* 271:393 */         BeanVisual sourceVisual = (source.getBean() instanceof Visible) ? ((Visible)source.getBean()).getVisual() : null;
/* 272:    */         
/* 273:395 */         BeanVisual targetVisual = (target.getBean() instanceof Visible) ? ((Visible)target.getBean()).getVisual() : null;
/* 274:397 */         if ((sourceVisual != null) && (targetVisual != null))
/* 275:    */         {
/* 276:398 */           Point bestSourcePt = sourceVisual.getClosestConnectorPoint(new Point(target.getX() + target.getWidth() / 2, target.getY() + target.getHeight() / 2));
/* 277:    */           
/* 278:    */ 
/* 279:401 */           Point bestTargetPt = targetVisual.getClosestConnectorPoint(new Point(source.getX() + source.getWidth() / 2, source.getY() + source.getHeight() / 2));
/* 280:    */           
/* 281:    */ 
/* 282:404 */           gx.setColor(Color.red);
/* 283:405 */           boolean active = true;
/* 284:406 */           if (((source.getBean() instanceof EventConstraints)) && 
/* 285:407 */             (!((EventConstraints)source.getBean()).eventGeneratable(srcEsd.getName())))
/* 286:    */           {
/* 287:409 */             gx.setColor(Color.gray);
/* 288:410 */             active = false;
/* 289:    */           }
/* 290:413 */           gx.drawLine((int)bestSourcePt.getX(), (int)bestSourcePt.getY(), (int)bestTargetPt.getX(), (int)bestTargetPt.getY());
/* 291:    */           double angle;
/* 292:    */           try
/* 293:    */           {
/* 294:419 */             double a = (bestSourcePt.getY() - bestTargetPt.getY()) / (bestSourcePt.getX() - bestTargetPt.getX());
/* 295:    */             
/* 296:421 */             angle = Math.atan(a);
/* 297:    */           }
/* 298:    */           catch (Exception ex)
/* 299:    */           {
/* 300:423 */             angle = 1.570796326794897D;
/* 301:    */           }
/* 302:426 */           Point arrowstart = new Point(bestTargetPt.x, bestTargetPt.y);
/* 303:427 */           Point arrowoffset = new Point((int)(7.0D * Math.cos(angle)), (int)(7.0D * Math.sin(angle)));
/* 304:    */           Point arrowend;
/* 305:    */           Point arrowend;
/* 306:430 */           if (bestSourcePt.getX() >= bestTargetPt.getX()) {
/* 307:432 */             arrowend = new Point(arrowstart.x + arrowoffset.x, arrowstart.y + arrowoffset.y);
/* 308:    */           } else {
/* 309:435 */             arrowend = new Point(arrowstart.x - arrowoffset.x, arrowstart.y - arrowoffset.y);
/* 310:    */           }
/* 311:438 */           int[] xs = { arrowstart.x, arrowend.x + (int)(7.0D * Math.cos(angle + 1.570796326794897D)), arrowend.x + (int)(7.0D * Math.cos(angle - 1.570796326794897D)) };
/* 312:    */           
/* 313:    */ 
/* 314:441 */           int[] ys = { arrowstart.y, arrowend.y + (int)(7.0D * Math.sin(angle + 1.570796326794897D)), arrowend.y + (int)(7.0D * Math.sin(angle - 1.570796326794897D)) };
/* 315:    */           
/* 316:    */ 
/* 317:444 */           gx.fillPolygon(xs, ys, 3);
/* 318:    */           
/* 319:    */ 
/* 320:    */ 
/* 321:448 */           int midx = (int)bestSourcePt.getX();
/* 322:449 */           midx += (int)((bestTargetPt.getX() - bestSourcePt.getX()) / 2.0D);
/* 323:450 */           int midy = (int)bestSourcePt.getY();
/* 324:451 */           midy += (int)((bestTargetPt.getY() - bestSourcePt.getY()) / 2.0D) - 2;
/* 325:452 */           gx.setColor(active ? Color.blue : Color.gray);
/* 326:453 */           if (previousLink(source, target, i, tab)) {
/* 327:454 */             midy -= 15;
/* 328:    */           }
/* 329:456 */           gx.drawString(srcEsd.getName(), midx, midy);
/* 330:    */         }
/* 331:    */       }
/* 332:    */     }
/* 333:    */   }
/* 334:    */   
/* 335:    */   public static Vector<BeanConnection> getClosestConnections(Point pt, int delta, Integer... tab)
/* 336:    */   {
/* 337:471 */     int tabIndex = 0;
/* 338:472 */     if (tab.length > 0) {
/* 339:473 */       tabIndex = tab[0].intValue();
/* 340:    */     }
/* 341:476 */     Vector<BeanConnection> connections = (Vector)TABBED_CONNECTIONS.get(tabIndex);
/* 342:    */     
/* 343:478 */     Vector<BeanConnection> closestConnections = new Vector();
/* 344:480 */     for (int i = 0; i < connections.size(); i++)
/* 345:    */     {
/* 346:481 */       BeanConnection bc = (BeanConnection)connections.elementAt(i);
/* 347:482 */       BeanInstance source = bc.getSource();
/* 348:483 */       BeanInstance target = bc.getTarget();
/* 349:484 */       bc.getSourceEventSetDescriptor();
/* 350:485 */       BeanVisual sourceVisual = (source.getBean() instanceof Visible) ? ((Visible)source.getBean()).getVisual() : null;
/* 351:    */       
/* 352:487 */       BeanVisual targetVisual = (target.getBean() instanceof Visible) ? ((Visible)target.getBean()).getVisual() : null;
/* 353:489 */       if ((sourceVisual != null) && (targetVisual != null))
/* 354:    */       {
/* 355:490 */         Point bestSourcePt = sourceVisual.getClosestConnectorPoint(new Point(target.getX() + target.getWidth() / 2, target.getY() + target.getHeight() / 2));
/* 356:    */         
/* 357:    */ 
/* 358:493 */         Point bestTargetPt = targetVisual.getClosestConnectorPoint(new Point(source.getX() + source.getWidth() / 2, source.getY() + source.getHeight() / 2));
/* 359:    */         
/* 360:    */ 
/* 361:    */ 
/* 362:497 */         int minx = (int)Math.min(bestSourcePt.getX(), bestTargetPt.getX());
/* 363:498 */         int maxx = (int)Math.max(bestSourcePt.getX(), bestTargetPt.getX());
/* 364:499 */         int miny = (int)Math.min(bestSourcePt.getY(), bestTargetPt.getY());
/* 365:500 */         int maxy = (int)Math.max(bestSourcePt.getY(), bestTargetPt.getY());
/* 366:502 */         if ((pt.getX() >= minx - delta) && (pt.getX() <= maxx + delta) && (pt.getY() >= miny - delta) && (pt.getY() <= maxy + delta))
/* 367:    */         {
/* 368:506 */           double a = bestSourcePt.getY() - bestTargetPt.getY();
/* 369:507 */           double b = bestTargetPt.getX() - bestSourcePt.getX();
/* 370:508 */           double c = bestSourcePt.getX() * bestTargetPt.getY() - bestTargetPt.getX() * bestSourcePt.getY();
/* 371:    */           
/* 372:    */ 
/* 373:511 */           double distance = Math.abs(a * pt.getX() + b * pt.getY() + c);
/* 374:512 */           distance /= Math.abs(Math.sqrt(a * a + b * b));
/* 375:514 */           if (distance <= delta) {
/* 376:515 */             closestConnections.addElement(bc);
/* 377:    */           }
/* 378:    */         }
/* 379:    */       }
/* 380:    */     }
/* 381:520 */     return closestConnections;
/* 382:    */   }
/* 383:    */   
/* 384:    */   public static void removeConnectionList(Integer tab)
/* 385:    */   {
/* 386:533 */     TABBED_CONNECTIONS.remove(tab.intValue());
/* 387:    */   }
/* 388:    */   
/* 389:    */   public static void removeConnections(BeanInstance instance, Integer... tab)
/* 390:    */   {
/* 391:547 */     int tabIndex = 0;
/* 392:548 */     if (tab.length > 0) {
/* 393:549 */       tabIndex = tab[0].intValue();
/* 394:    */     }
/* 395:552 */     Vector<BeanConnection> connections = (Vector)TABBED_CONNECTIONS.get(tabIndex);
/* 396:    */     
/* 397:554 */     Vector<Object> instancesToRemoveFor = new Vector();
/* 398:555 */     if ((instance.getBean() instanceof MetaBean)) {
/* 399:556 */       instancesToRemoveFor = ((MetaBean)instance.getBean()).getBeansInSubFlow();
/* 400:    */     } else {
/* 401:559 */       instancesToRemoveFor.add(instance);
/* 402:    */     }
/* 403:561 */     Vector<BeanConnection> removeVector = new Vector();
/* 404:562 */     for (int j = 0; j < instancesToRemoveFor.size(); j++)
/* 405:    */     {
/* 406:563 */       BeanInstance tempInstance = (BeanInstance)instancesToRemoveFor.elementAt(j);
/* 407:565 */       for (int i = 0; i < connections.size(); i++)
/* 408:    */       {
/* 409:568 */         BeanConnection bc = (BeanConnection)connections.elementAt(i);
/* 410:569 */         BeanInstance tempTarget = bc.getTarget();
/* 411:570 */         BeanInstance tempSource = bc.getSource();
/* 412:    */         
/* 413:572 */         EventSetDescriptor tempEsd = bc.getSourceEventSetDescriptor();
/* 414:573 */         if (tempInstance == tempTarget)
/* 415:    */         {
/* 416:    */           try
/* 417:    */           {
/* 418:576 */             Method deregisterMethod = tempEsd.getRemoveListenerMethod();
/* 419:577 */             Object targetBean = tempTarget.getBean();
/* 420:578 */             Object[] args = new Object[1];
/* 421:579 */             args[0] = targetBean;
/* 422:580 */             deregisterMethod.invoke(tempSource.getBean(), args);
/* 423:    */             
/* 424:582 */             removeVector.addElement(bc);
/* 425:    */           }
/* 426:    */           catch (Exception ex)
/* 427:    */           {
/* 428:584 */             ex.printStackTrace();
/* 429:    */           }
/* 430:    */         }
/* 431:586 */         else if (tempInstance == tempSource)
/* 432:    */         {
/* 433:587 */           removeVector.addElement(bc);
/* 434:588 */           if ((tempTarget.getBean() instanceof BeanCommon)) {
/* 435:591 */             ((BeanCommon)tempTarget.getBean()).disconnectionNotification(tempEsd.getName(), tempSource.getBean());
/* 436:    */           }
/* 437:    */         }
/* 438:    */       }
/* 439:    */     }
/* 440:597 */     for (int i = 0; i < removeVector.size(); i++) {
/* 441:599 */       connections.removeElement(removeVector.elementAt(i));
/* 442:    */     }
/* 443:    */   }
/* 444:    */   
/* 445:    */   public static void doMetaConnection(BeanInstance source, BeanInstance target, final EventSetDescriptor esd, final JComponent displayComponent, final int tab)
/* 446:    */   {
/* 447:607 */     Object targetBean = target.getBean();
/* 448:608 */     BeanInstance realTarget = null;
/* 449:609 */     BeanInstance realSource = source;
/* 450:610 */     if ((targetBean instanceof MetaBean))
/* 451:    */     {
/* 452:611 */       Vector<BeanInstance> receivers = ((MetaBean)targetBean).getSuitableTargets(esd);
/* 453:613 */       if (receivers.size() == 1)
/* 454:    */       {
/* 455:614 */         realTarget = (BeanInstance)receivers.elementAt(0);
/* 456:615 */         new BeanConnection(realSource, realTarget, esd, new Integer[] { Integer.valueOf(tab) });
/* 457:    */       }
/* 458:    */       else
/* 459:    */       {
/* 460:618 */         int menuItemCount = 0;
/* 461:619 */         JPopupMenu targetConnectionMenu = new JPopupMenu();
/* 462:620 */         targetConnectionMenu.insert(new JLabel("Select target", 0), menuItemCount++);
/* 463:622 */         for (int i = 0; i < receivers.size(); i++)
/* 464:    */         {
/* 465:623 */           final BeanInstance tempTarget = (BeanInstance)receivers.elementAt(i);
/* 466:624 */           String tName = "" + (i + 1) + ": " + ((tempTarget.getBean() instanceof BeanCommon) ? ((BeanCommon)tempTarget.getBean()).getCustomName() : tempTarget.getBean().getClass().getName());
/* 467:    */           
/* 468:    */ 
/* 469:    */ 
/* 470:    */ 
/* 471:    */ 
/* 472:630 */           JMenuItem targetItem = new JMenuItem(tName);
/* 473:631 */           targetItem.addActionListener(new ActionListener()
/* 474:    */           {
/* 475:    */             public void actionPerformed(ActionEvent e)
/* 476:    */             {
/* 477:634 */               new BeanConnection(this.val$realSource, tempTarget, esd, new Integer[] { Integer.valueOf(tab) });
/* 478:635 */               displayComponent.repaint();
/* 479:    */             }
/* 480:637 */           });
/* 481:638 */           targetConnectionMenu.add(targetItem);
/* 482:639 */           menuItemCount++;
/* 483:    */         }
/* 484:641 */         targetConnectionMenu.show(displayComponent, target.getX(), target.getY());
/* 485:    */       }
/* 486:    */     }
/* 487:    */   }
/* 488:    */   
/* 489:    */   public BeanConnection(BeanInstance source, BeanInstance target, EventSetDescriptor esd, Integer... tab)
/* 490:    */   {
/* 491:658 */     int tabIndex = 0;
/* 492:659 */     if (tab.length > 0) {
/* 493:660 */       tabIndex = tab[0].intValue();
/* 494:    */     }
/* 495:663 */     Vector<BeanConnection> connections = (Vector)TABBED_CONNECTIONS.get(tabIndex);
/* 496:    */     
/* 497:665 */     this.m_source = source;
/* 498:666 */     this.m_target = target;
/* 499:    */     
/* 500:668 */     this.m_eventName = esd.getName();
/* 501:    */     
/* 502:    */ 
/* 503:    */ 
/* 504:672 */     Method registrationMethod = esd.getAddListenerMethod();
/* 505:    */     
/* 506:    */ 
/* 507:    */ 
/* 508:676 */     Object targetBean = this.m_target.getBean();
/* 509:    */     
/* 510:678 */     Object[] args = new Object[1];
/* 511:679 */     args[0] = targetBean;
/* 512:680 */     Class<?> listenerClass = esd.getListenerType();
/* 513:681 */     if (listenerClass.isInstance(targetBean)) {
/* 514:    */       try
/* 515:    */       {
/* 516:683 */         registrationMethod.invoke(this.m_source.getBean(), args);
/* 517:687 */         if ((targetBean instanceof BeanCommon)) {
/* 518:688 */           ((BeanCommon)targetBean).connectionNotification(esd.getName(), this.m_source.getBean());
/* 519:    */         }
/* 520:691 */         connections.addElement(this);
/* 521:    */       }
/* 522:    */       catch (Exception ex)
/* 523:    */       {
/* 524:693 */         System.err.println("[BeanConnection] Unable to connect beans");
/* 525:694 */         ex.printStackTrace();
/* 526:    */       }
/* 527:    */     } else {
/* 528:697 */       System.err.println("[BeanConnection] Unable to connect beans");
/* 529:    */     }
/* 530:    */   }
/* 531:    */   
/* 532:    */   public void setHidden(boolean hidden)
/* 533:    */   {
/* 534:707 */     this.m_hidden = hidden;
/* 535:    */   }
/* 536:    */   
/* 537:    */   public boolean isHidden()
/* 538:    */   {
/* 539:716 */     return this.m_hidden;
/* 540:    */   }
/* 541:    */   
/* 542:    */   public void remove(Integer... tab)
/* 543:    */   {
/* 544:723 */     int tabIndex = 0;
/* 545:724 */     if (tab.length > 0) {
/* 546:725 */       tabIndex = tab[0].intValue();
/* 547:    */     }
/* 548:728 */     Vector<BeanConnection> connections = (Vector)TABBED_CONNECTIONS.get(tabIndex);
/* 549:    */     
/* 550:730 */     EventSetDescriptor tempEsd = getSourceEventSetDescriptor();
/* 551:    */     try
/* 552:    */     {
/* 553:733 */       Method deregisterMethod = tempEsd.getRemoveListenerMethod();
/* 554:734 */       Object targetBean = getTarget().getBean();
/* 555:735 */       Object[] args = new Object[1];
/* 556:736 */       args[0] = targetBean;
/* 557:737 */       deregisterMethod.invoke(getSource().getBean(), args);
/* 558:    */     }
/* 559:    */     catch (Exception ex)
/* 560:    */     {
/* 561:740 */       ex.printStackTrace();
/* 562:    */     }
/* 563:743 */     if ((getTarget().getBean() instanceof BeanCommon)) {
/* 564:745 */       ((BeanCommon)getTarget().getBean()).disconnectionNotification(tempEsd.getName(), getSource().getBean());
/* 565:    */     }
/* 566:749 */     connections.remove(this);
/* 567:    */   }
/* 568:    */   
/* 569:    */   public BeanInstance getSource()
/* 570:    */   {
/* 571:758 */     return this.m_source;
/* 572:    */   }
/* 573:    */   
/* 574:    */   public BeanInstance getTarget()
/* 575:    */   {
/* 576:767 */     return this.m_target;
/* 577:    */   }
/* 578:    */   
/* 579:    */   public String getEventName()
/* 580:    */   {
/* 581:776 */     return this.m_eventName;
/* 582:    */   }
/* 583:    */   
/* 584:    */   protected EventSetDescriptor getSourceEventSetDescriptor()
/* 585:    */   {
/* 586:786 */     JComponent bc = (JComponent)this.m_source.getBean();
/* 587:    */     try
/* 588:    */     {
/* 589:788 */       BeanInfo sourceInfo = Introspector.getBeanInfo(bc.getClass());
/* 590:789 */       if (sourceInfo == null)
/* 591:    */       {
/* 592:790 */         System.err.println("[BeanConnection] Error getting bean info, source info is null.");
/* 593:    */       }
/* 594:    */       else
/* 595:    */       {
/* 596:793 */         EventSetDescriptor[] esds = sourceInfo.getEventSetDescriptors();
/* 597:794 */         for (EventSetDescriptor esd : esds) {
/* 598:795 */           if (esd.getName().compareTo(this.m_eventName) == 0) {
/* 599:796 */             return esd;
/* 600:    */           }
/* 601:    */         }
/* 602:    */       }
/* 603:    */     }
/* 604:    */     catch (Exception ex)
/* 605:    */     {
/* 606:801 */       System.err.println("[BeanConnection] Problem retrieving event set descriptor");
/* 607:    */     }
/* 608:804 */     return null;
/* 609:    */   }
/* 610:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.BeanConnection
 * JD-Core Version:    0.7.0.1
 */