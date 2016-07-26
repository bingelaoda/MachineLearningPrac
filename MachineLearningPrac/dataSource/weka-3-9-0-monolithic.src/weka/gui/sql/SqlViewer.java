/*   1:    */ package weka.gui.sql;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.event.WindowAdapter;
/*   7:    */ import java.awt.event.WindowEvent;
/*   8:    */ import java.io.BufferedInputStream;
/*   9:    */ import java.io.BufferedOutputStream;
/*  10:    */ import java.io.File;
/*  11:    */ import java.io.FileInputStream;
/*  12:    */ import java.io.FileOutputStream;
/*  13:    */ import java.io.PrintStream;
/*  14:    */ import java.util.Properties;
/*  15:    */ import javax.swing.BorderFactory;
/*  16:    */ import javax.swing.DefaultListModel;
/*  17:    */ import javax.swing.JFrame;
/*  18:    */ import javax.swing.JPanel;
/*  19:    */ import weka.core.Memory;
/*  20:    */ import weka.core.logging.Logger;
/*  21:    */ import weka.core.logging.Logger.Level;
/*  22:    */ import weka.gui.LookAndFeel;
/*  23:    */ import weka.gui.sql.event.ConnectionEvent;
/*  24:    */ import weka.gui.sql.event.ConnectionListener;
/*  25:    */ import weka.gui.sql.event.HistoryChangedEvent;
/*  26:    */ import weka.gui.sql.event.HistoryChangedListener;
/*  27:    */ import weka.gui.sql.event.QueryExecuteEvent;
/*  28:    */ import weka.gui.sql.event.QueryExecuteListener;
/*  29:    */ import weka.gui.sql.event.ResultChangedEvent;
/*  30:    */ import weka.gui.sql.event.ResultChangedListener;
/*  31:    */ 
/*  32:    */ public class SqlViewer
/*  33:    */   extends JPanel
/*  34:    */   implements ConnectionListener, HistoryChangedListener, QueryExecuteListener, ResultChangedListener
/*  35:    */ {
/*  36:    */   private static final long serialVersionUID = -4395028775566514329L;
/*  37:    */   protected static final String HISTORY_FILE = "SqlViewerHistory.props";
/*  38:    */   public static final String WIDTH = "width";
/*  39:    */   public static final String HEIGHT = "height";
/*  40:    */   protected JFrame m_Parent;
/*  41:    */   protected ConnectionPanel m_ConnectionPanel;
/*  42:    */   protected QueryPanel m_QueryPanel;
/*  43:    */   protected ResultPanel m_ResultPanel;
/*  44:    */   protected InfoPanel m_InfoPanel;
/*  45:    */   protected String m_URL;
/*  46:    */   protected String m_User;
/*  47:    */   protected String m_Password;
/*  48:    */   protected String m_Query;
/*  49:    */   protected Properties m_History;
/*  50:    */   
/*  51:    */   public SqlViewer(JFrame parent)
/*  52:    */   {
/*  53:110 */     this.m_Parent = parent;
/*  54:111 */     this.m_URL = "";
/*  55:112 */     this.m_User = "";
/*  56:113 */     this.m_Password = "";
/*  57:114 */     this.m_Query = "";
/*  58:115 */     this.m_History = new Properties();
/*  59:    */     
/*  60:117 */     createPanel();
/*  61:    */   }
/*  62:    */   
/*  63:    */   protected void createPanel()
/*  64:    */   {
/*  65:127 */     setLayout(new BorderLayout());
/*  66:    */     
/*  67:    */ 
/*  68:130 */     this.m_ConnectionPanel = new ConnectionPanel(this.m_Parent);
/*  69:131 */     JPanel panel = new JPanel(new BorderLayout());
/*  70:132 */     add(panel, "North");
/*  71:133 */     panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Connection"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
/*  72:    */     
/*  73:    */ 
/*  74:136 */     panel.add(this.m_ConnectionPanel, "Center");
/*  75:    */     
/*  76:    */ 
/*  77:139 */     this.m_QueryPanel = new QueryPanel(this.m_Parent);
/*  78:140 */     panel = new JPanel(new BorderLayout());
/*  79:141 */     add(panel, "Center");
/*  80:142 */     JPanel panel2 = new JPanel(new BorderLayout());
/*  81:143 */     panel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Query"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
/*  82:    */     
/*  83:    */ 
/*  84:146 */     panel2.add(this.m_QueryPanel, "North");
/*  85:147 */     panel.add(panel2, "North");
/*  86:    */     
/*  87:    */ 
/*  88:150 */     this.m_ResultPanel = new ResultPanel(this.m_Parent);
/*  89:151 */     this.m_ResultPanel.setQueryPanel(this.m_QueryPanel);
/*  90:152 */     panel2 = new JPanel(new BorderLayout());
/*  91:153 */     panel2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Result"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
/*  92:    */     
/*  93:    */ 
/*  94:156 */     panel2.add(this.m_ResultPanel, "Center");
/*  95:157 */     panel.add(panel2, "Center");
/*  96:    */     
/*  97:    */ 
/*  98:160 */     this.m_InfoPanel = new InfoPanel(this.m_Parent);
/*  99:161 */     panel = new JPanel(new BorderLayout());
/* 100:162 */     add(panel, "South");
/* 101:163 */     panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Info"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
/* 102:    */     
/* 103:    */ 
/* 104:166 */     panel.add(this.m_InfoPanel, "Center");
/* 105:    */     
/* 106:    */ 
/* 107:169 */     addConnectionListener(this);
/* 108:170 */     addConnectionListener(this.m_QueryPanel);
/* 109:171 */     addQueryExecuteListener(this);
/* 110:172 */     addQueryExecuteListener(this.m_ResultPanel);
/* 111:173 */     addResultChangedListener(this);
/* 112:174 */     addHistoryChangedListener(this);
/* 113:    */     
/* 114:    */ 
/* 115:177 */     loadHistory(true);
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void connectionChange(ConnectionEvent evt)
/* 119:    */   {
/* 120:188 */     if (evt.getType() == 1) {
/* 121:189 */       this.m_InfoPanel.append("disconnect from: " + evt.getDbUtils().getDatabaseURL(), "information_small.gif");
/* 122:    */     } else {
/* 123:192 */       this.m_InfoPanel.append("connecting to: " + evt.getDbUtils().getDatabaseURL() + " = " + evt.isConnected(), "information_small.gif");
/* 124:    */     }
/* 125:197 */     if (evt.getException() != null) {
/* 126:198 */       this.m_InfoPanel.append("exception: " + evt.getException(), "error_small.gif");
/* 127:    */     }
/* 128:202 */     if (evt.isConnected()) {
/* 129:203 */       this.m_QueryPanel.setFocus();
/* 130:    */     } else {
/* 131:205 */       this.m_ConnectionPanel.setFocus();
/* 132:    */     }
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void queryExecuted(QueryExecuteEvent evt)
/* 136:    */   {
/* 137:218 */     if (evt.failed())
/* 138:    */     {
/* 139:219 */       this.m_InfoPanel.append("Query:" + evt.getQuery(), "error_small.gif");
/* 140:220 */       this.m_InfoPanel.append("exception: " + evt.getException(), "error_small.gif");
/* 141:    */     }
/* 142:    */     else
/* 143:    */     {
/* 144:222 */       this.m_InfoPanel.append("Query: " + evt.getQuery(), "information_small.gif");
/* 145:    */       try
/* 146:    */       {
/* 147:224 */         if (evt.hasResult())
/* 148:    */         {
/* 149:225 */           ResultSetHelper helper = new ResultSetHelper(evt.getResultSet());
/* 150:226 */           if ((evt.getMaxRows() > 0) && (helper.getRowCount() >= evt.getMaxRows())) {
/* 151:228 */             this.m_InfoPanel.append(helper.getRowCount() + " rows selected (" + evt.getMaxRows() + " displayed).", "information_small.gif");
/* 152:231 */           } else if (helper.getRowCount() == -1) {
/* 153:232 */             this.m_InfoPanel.append("Unknown number of rows selected (due to JDBC driver restrictions).", "information_small.gif");
/* 154:    */           } else {
/* 155:237 */             this.m_InfoPanel.append(helper.getRowCount() + " rows selected.", "information_small.gif");
/* 156:    */           }
/* 157:    */         }
/* 158:243 */         loadHistory(false);
/* 159:244 */         this.m_History.setProperty("max_rows", Integer.toString(evt.getMaxRows()));
/* 160:    */         
/* 161:246 */         saveHistory();
/* 162:    */       }
/* 163:    */       catch (Exception e)
/* 164:    */       {
/* 165:248 */         e.printStackTrace();
/* 166:    */       }
/* 167:    */     }
/* 168:    */   }
/* 169:    */   
/* 170:    */   public void resultChanged(ResultChangedEvent evt)
/* 171:    */   {
/* 172:260 */     this.m_URL = evt.getURL();
/* 173:261 */     this.m_User = evt.getUser();
/* 174:262 */     this.m_Password = evt.getPassword();
/* 175:263 */     this.m_Query = evt.getQuery();
/* 176:    */   }
/* 177:    */   
/* 178:    */   public void historyChanged(HistoryChangedEvent evt)
/* 179:    */   {
/* 180:275 */     loadHistory(false);
/* 181:    */     
/* 182:277 */     this.m_History.setProperty(evt.getHistoryName(), modelToString(evt.getHistory()));
/* 183:    */     
/* 184:    */ 
/* 185:    */ 
/* 186:281 */     saveHistory();
/* 187:    */   }
/* 188:    */   
/* 189:    */   protected String getHistoryFilename()
/* 190:    */   {
/* 191:290 */     return System.getProperties().getProperty("user.home") + File.separatorChar + "SqlViewerHistory.props";
/* 192:    */   }
/* 193:    */   
/* 194:    */   protected DefaultListModel stringToModel(String s)
/* 195:    */   {
/* 196:309 */     DefaultListModel result = new DefaultListModel();
/* 197:    */     
/* 198:    */ 
/* 199:312 */     String[] find = { "\"\"", "\\n", "\\r", "\\t" };
/* 200:313 */     String[] replace = { "\"", "\n", "\r", "\t" };
/* 201:314 */     for (int i = 0; i < find.length; i++)
/* 202:    */     {
/* 203:315 */       String tmpStr = "";
/* 204:316 */       while (s.length() > 0)
/* 205:    */       {
/* 206:317 */         int index = s.indexOf(find[i]);
/* 207:318 */         if (index > -1)
/* 208:    */         {
/* 209:319 */           tmpStr = tmpStr + s.substring(0, index) + replace[i];
/* 210:320 */           s = s.substring(index + 2);
/* 211:    */         }
/* 212:    */         else
/* 213:    */         {
/* 214:322 */           tmpStr = tmpStr + s;
/* 215:323 */           s = "";
/* 216:    */         }
/* 217:    */       }
/* 218:326 */       s = tmpStr;
/* 219:    */     }
/* 220:329 */     boolean quote = false;
/* 221:330 */     String tmpStr = "";
/* 222:331 */     for (i = 0; i < s.length(); i++) {
/* 223:332 */       if (s.charAt(i) == '"')
/* 224:    */       {
/* 225:333 */         quote = !quote;
/* 226:334 */         tmpStr = tmpStr + "" + s.charAt(i);
/* 227:    */       }
/* 228:335 */       else if (s.charAt(i) == ',')
/* 229:    */       {
/* 230:336 */         if (quote)
/* 231:    */         {
/* 232:337 */           tmpStr = tmpStr + "" + s.charAt(i);
/* 233:    */         }
/* 234:    */         else
/* 235:    */         {
/* 236:339 */           if (tmpStr.startsWith("\"")) {
/* 237:340 */             tmpStr = tmpStr.substring(1, tmpStr.length() - 1);
/* 238:    */           }
/* 239:342 */           result.addElement(tmpStr);
/* 240:343 */           tmpStr = "";
/* 241:    */         }
/* 242:    */       }
/* 243:    */       else
/* 244:    */       {
/* 245:346 */         tmpStr = tmpStr + "" + s.charAt(i);
/* 246:    */       }
/* 247:    */     }
/* 248:351 */     if (!tmpStr.equals(""))
/* 249:    */     {
/* 250:352 */       if (tmpStr.startsWith("\"")) {
/* 251:353 */         tmpStr = tmpStr.substring(1, tmpStr.length() - 1);
/* 252:    */       }
/* 253:355 */       result.addElement(tmpStr);
/* 254:    */     }
/* 255:358 */     return result;
/* 256:    */   }
/* 257:    */   
/* 258:    */   protected String modelToString(DefaultListModel m)
/* 259:    */   {
/* 260:374 */     String result = "";
/* 261:376 */     for (int i = 0; i < m.size(); i++)
/* 262:    */     {
/* 263:377 */       if (i > 0) {
/* 264:378 */         result = result + ",";
/* 265:    */       }
/* 266:381 */       String tmpStr = m.get(i).toString();
/* 267:382 */       boolean quote = (tmpStr.indexOf(",") > -1) || (tmpStr.indexOf(" ") > -1);
/* 268:384 */       if (quote) {
/* 269:385 */         result = result + "\"";
/* 270:    */       }
/* 271:388 */       for (int n = 0; n < tmpStr.length(); n++) {
/* 272:390 */         if (tmpStr.charAt(n) == '"') {
/* 273:391 */           result = result + "\"\"";
/* 274:    */         } else {
/* 275:394 */           result = result + "" + tmpStr.charAt(n);
/* 276:    */         }
/* 277:    */       }
/* 278:398 */       if (quote) {
/* 279:399 */         result = result + "\"";
/* 280:    */       }
/* 281:    */     }
/* 282:403 */     return result;
/* 283:    */   }
/* 284:    */   
/* 285:    */   protected void loadHistory(boolean set)
/* 286:    */   {
/* 287:    */     try
/* 288:    */     {
/* 289:419 */       File file = new File(getHistoryFilename());
/* 290:420 */       if (file.exists())
/* 291:    */       {
/* 292:421 */         BufferedInputStream str = new BufferedInputStream(new FileInputStream(getHistoryFilename()));
/* 293:422 */         this.m_History.load(str);
/* 294:    */       }
/* 295:    */     }
/* 296:    */     catch (Exception e)
/* 297:    */     {
/* 298:425 */       e.printStackTrace();
/* 299:    */     }
/* 300:429 */     if (set)
/* 301:    */     {
/* 302:430 */       this.m_ConnectionPanel.setHistory(stringToModel(this.m_History.getProperty("connection", "")));
/* 303:    */       
/* 304:432 */       this.m_QueryPanel.setHistory(stringToModel(this.m_History.getProperty("query", "")));
/* 305:    */       
/* 306:434 */       this.m_QueryPanel.setMaxRows(Integer.parseInt(this.m_History.getProperty("max_rows", "100")));
/* 307:    */       
/* 308:    */ 
/* 309:437 */       int width = Integer.parseInt(this.m_History.getProperty("width", "0"));
/* 310:438 */       int height = Integer.parseInt(this.m_History.getProperty("height", "0"));
/* 311:439 */       if ((width != 0) && (height != 0)) {
/* 312:440 */         setPreferredSize(new Dimension(width, height));
/* 313:    */       }
/* 314:    */     }
/* 315:    */   }
/* 316:    */   
/* 317:    */   protected void saveHistory()
/* 318:    */   {
/* 319:    */     try
/* 320:    */     {
/* 321:454 */       BufferedOutputStream str = new BufferedOutputStream(new FileOutputStream(getHistoryFilename()));
/* 322:455 */       this.m_History.store(str, "SQL-Viewer-History");
/* 323:    */     }
/* 324:    */     catch (Exception e)
/* 325:    */     {
/* 326:457 */       e.printStackTrace();
/* 327:    */     }
/* 328:    */   }
/* 329:    */   
/* 330:    */   public void saveSize()
/* 331:    */   {
/* 332:467 */     this.m_History.setProperty("width", "" + getSize().width);
/* 333:468 */     this.m_History.setProperty("height", "" + getSize().height);
/* 334:    */     
/* 335:470 */     saveHistory();
/* 336:    */   }
/* 337:    */   
/* 338:    */   public void clear()
/* 339:    */   {
/* 340:478 */     this.m_ConnectionPanel.clear();
/* 341:479 */     this.m_QueryPanel.clear();
/* 342:480 */     this.m_ResultPanel.clear();
/* 343:481 */     this.m_InfoPanel.clear();
/* 344:    */   }
/* 345:    */   
/* 346:    */   public String getURL()
/* 347:    */   {
/* 348:492 */     return this.m_URL;
/* 349:    */   }
/* 350:    */   
/* 351:    */   public String getUser()
/* 352:    */   {
/* 353:503 */     return this.m_User;
/* 354:    */   }
/* 355:    */   
/* 356:    */   public String getPassword()
/* 357:    */   {
/* 358:514 */     return this.m_Password;
/* 359:    */   }
/* 360:    */   
/* 361:    */   public String getQuery()
/* 362:    */   {
/* 363:525 */     return this.m_Query;
/* 364:    */   }
/* 365:    */   
/* 366:    */   public void addConnectionListener(ConnectionListener l)
/* 367:    */   {
/* 368:534 */     this.m_ConnectionPanel.addConnectionListener(l);
/* 369:    */   }
/* 370:    */   
/* 371:    */   public void removeConnectionListener(ConnectionListener l)
/* 372:    */   {
/* 373:543 */     this.m_ConnectionPanel.removeConnectionListener(l);
/* 374:    */   }
/* 375:    */   
/* 376:    */   public void addQueryExecuteListener(QueryExecuteListener l)
/* 377:    */   {
/* 378:552 */     this.m_QueryPanel.addQueryExecuteListener(l);
/* 379:    */   }
/* 380:    */   
/* 381:    */   public void removeQueryExecuteListener(QueryExecuteListener l)
/* 382:    */   {
/* 383:561 */     this.m_QueryPanel.removeQueryExecuteListener(l);
/* 384:    */   }
/* 385:    */   
/* 386:    */   public void addResultChangedListener(ResultChangedListener l)
/* 387:    */   {
/* 388:570 */     this.m_ResultPanel.addResultChangedListener(l);
/* 389:    */   }
/* 390:    */   
/* 391:    */   public void removeResultChangedListener(ResultChangedListener l)
/* 392:    */   {
/* 393:579 */     this.m_ResultPanel.removeResultChangedListener(l);
/* 394:    */   }
/* 395:    */   
/* 396:    */   public void addHistoryChangedListener(HistoryChangedListener l)
/* 397:    */   {
/* 398:588 */     this.m_ConnectionPanel.addHistoryChangedListener(l);
/* 399:589 */     this.m_QueryPanel.addHistoryChangedListener(l);
/* 400:    */   }
/* 401:    */   
/* 402:    */   public void removeHistoryChangedListener(HistoryChangedListener l)
/* 403:    */   {
/* 404:598 */     this.m_ConnectionPanel.removeHistoryChangedListener(l);
/* 405:599 */     this.m_QueryPanel.removeHistoryChangedListener(l);
/* 406:    */   }
/* 407:    */   
/* 408:603 */   private static Memory m_Memory = new Memory(true);
/* 409:    */   private static SqlViewer m_Viewer;
/* 410:    */   
/* 411:    */   public static void main(String[] args)
/* 412:    */   {
/* 413:614 */     Logger.log(Logger.Level.INFO, "Logging started");
/* 414:    */     
/* 415:616 */     LookAndFeel.setLookAndFeel();
/* 416:    */     try
/* 417:    */     {
/* 418:622 */       JFrame jf = new JFrame("Weka SQL-Viewer");
/* 419:623 */       m_Viewer = new SqlViewer(jf);
/* 420:624 */       jf.getContentPane().setLayout(new BorderLayout());
/* 421:625 */       jf.getContentPane().add(m_Viewer, "Center");
/* 422:626 */       jf.addWindowListener(new WindowAdapter()
/* 423:    */       {
/* 424:    */         public void windowClosing(WindowEvent e)
/* 425:    */         {
/* 426:629 */           SqlViewer.m_Viewer.saveSize();
/* 427:630 */           this.val$jf.dispose();
/* 428:631 */           System.exit(0);
/* 429:    */         }
/* 430:633 */       });
/* 431:634 */       jf.pack();
/* 432:635 */       jf.setSize(800, 600);
/* 433:636 */       jf.setVisible(true);
/* 434:    */       
/* 435:638 */       Thread memMonitor = new Thread()
/* 436:    */       {
/* 437:    */         public void run()
/* 438:    */         {
/* 439:    */           for (;;)
/* 440:    */           {
/* 441:645 */             if (SqlViewer.m_Memory.isOutOfMemory())
/* 442:    */             {
/* 443:647 */               this.val$jf.dispose();
/* 444:648 */               SqlViewer.access$002(null);
/* 445:649 */               System.gc();
/* 446:    */               
/* 447:    */ 
/* 448:652 */               System.err.println("\ndisplayed message:");
/* 449:653 */               SqlViewer.m_Memory.showOutOfMemory();
/* 450:654 */               System.err.println("\nexiting");
/* 451:655 */               System.exit(-1);
/* 452:    */             }
/* 453:    */           }
/* 454:    */         }
/* 455:664 */       };
/* 456:665 */       memMonitor.setPriority(10);
/* 457:666 */       memMonitor.start();
/* 458:    */     }
/* 459:    */     catch (Exception ex)
/* 460:    */     {
/* 461:668 */       ex.printStackTrace();
/* 462:669 */       System.err.println(ex.getMessage());
/* 463:    */     }
/* 464:    */   }
/* 465:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.SqlViewer
 * JD-Core Version:    0.7.0.1
 */