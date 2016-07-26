/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Component;
/*   6:    */ import java.awt.Container;
/*   7:    */ import java.awt.event.ActionEvent;
/*   8:    */ import java.awt.event.ActionListener;
/*   9:    */ import java.awt.event.WindowAdapter;
/*  10:    */ import java.awt.event.WindowEvent;
/*  11:    */ import java.text.DecimalFormat;
/*  12:    */ import java.util.Collection;
/*  13:    */ import java.util.HashMap;
/*  14:    */ import java.util.Iterator;
/*  15:    */ import java.util.Set;
/*  16:    */ import javax.swing.JFrame;
/*  17:    */ import javax.swing.JPanel;
/*  18:    */ import javax.swing.JScrollPane;
/*  19:    */ import javax.swing.JTabbedPane;
/*  20:    */ import javax.swing.JTable;
/*  21:    */ import javax.swing.SwingUtilities;
/*  22:    */ import javax.swing.Timer;
/*  23:    */ import javax.swing.table.DefaultTableModel;
/*  24:    */ import javax.swing.table.TableCellRenderer;
/*  25:    */ import javax.swing.table.TableColumn;
/*  26:    */ import javax.swing.table.TableColumnModel;
/*  27:    */ import javax.swing.table.TableModel;
/*  28:    */ import weka.gui.Logger;
/*  29:    */ 
/*  30:    */ public class LogPanel
/*  31:    */   extends JPanel
/*  32:    */   implements Logger
/*  33:    */ {
/*  34:    */   private static final long serialVersionUID = 6583097154513435548L;
/*  35: 59 */   protected HashMap<String, Integer> m_tableIndexes = new HashMap();
/*  36: 65 */   private final HashMap<String, Timer> m_timers = new HashMap();
/*  37:    */   private final DefaultTableModel m_tableModel;
/*  38:    */   private JTable m_table;
/*  39: 80 */   private final JTabbedPane m_tabs = new JTabbedPane();
/*  40: 85 */   private final DecimalFormat m_formatter = new DecimalFormat("00");
/*  41: 90 */   private final weka.gui.LogPanel m_logPanel = new weka.gui.LogPanel(null, false, true, false);
/*  42:    */   
/*  43:    */   public LogPanel()
/*  44:    */   {
/*  45: 95 */     String[] columnNames = { "Component", "Parameters", "Time", "Status" };
/*  46: 96 */     this.m_tableModel = new DefaultTableModel(columnNames, 0);
/*  47:    */     
/*  48:    */ 
/*  49: 99 */     this.m_table = new JTable()
/*  50:    */     {
/*  51:    */       private static final long serialVersionUID = 5883722364387855125L;
/*  52:    */       
/*  53:    */       public Class<?> getColumnClass(int column)
/*  54:    */       {
/*  55:106 */         return getValueAt(0, column).getClass();
/*  56:    */       }
/*  57:    */       
/*  58:    */       public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
/*  59:    */       {
/*  60:112 */         Component c = super.prepareRenderer(renderer, row, column);
/*  61:113 */         if (!c.getBackground().equals(getSelectionBackground()))
/*  62:    */         {
/*  63:114 */           String type = (String)getModel().getValueAt(row, 3);
/*  64:115 */           Color backgroundIndicator = null;
/*  65:116 */           if (type.startsWith("ERROR")) {
/*  66:117 */             backgroundIndicator = Color.RED;
/*  67:118 */           } else if (type.startsWith("WARNING")) {
/*  68:119 */             backgroundIndicator = Color.YELLOW;
/*  69:120 */           } else if (type.startsWith("INTERRUPTED")) {
/*  70:121 */             backgroundIndicator = Color.MAGENTA;
/*  71:    */           }
/*  72:123 */           c.setBackground(backgroundIndicator);
/*  73:    */         }
/*  74:125 */         return c;
/*  75:    */       }
/*  76:128 */     };
/*  77:129 */     this.m_table.setModel(this.m_tableModel);
/*  78:130 */     this.m_table.getColumnModel().getColumn(0).setPreferredWidth(100);
/*  79:131 */     this.m_table.getColumnModel().getColumn(1).setPreferredWidth(150);
/*  80:132 */     this.m_table.getColumnModel().getColumn(2).setPreferredWidth(40);
/*  81:133 */     this.m_table.getColumnModel().getColumn(3).setPreferredWidth(350);
/*  82:134 */     this.m_table.setShowVerticalLines(true);
/*  83:    */     
/*  84:136 */     JPanel statusPan = new JPanel();
/*  85:137 */     statusPan.setLayout(new BorderLayout());
/*  86:138 */     statusPan.add(new JScrollPane(this.m_table), "Center");
/*  87:139 */     this.m_tabs.addTab("Status", statusPan);
/*  88:140 */     this.m_tabs.addTab("Log", this.m_logPanel);
/*  89:    */     
/*  90:142 */     setLayout(new BorderLayout());
/*  91:143 */     add(this.m_tabs, "Center");
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void clearStatus()
/*  95:    */   {
/*  96:152 */     Iterator<Timer> i = this.m_timers.values().iterator();
/*  97:153 */     while (i.hasNext()) {
/*  98:154 */       ((Timer)i.next()).stop();
/*  99:    */     }
/* 100:158 */     this.m_timers.clear();
/* 101:159 */     this.m_tableIndexes.clear();
/* 102:162 */     while (this.m_tableModel.getRowCount() > 0) {
/* 103:163 */       this.m_tableModel.removeRow(0);
/* 104:    */     }
/* 105:    */   }
/* 106:    */   
/* 107:    */   public JTable getStatusTable()
/* 108:    */   {
/* 109:174 */     return this.m_table;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public synchronized void logMessage(String message)
/* 113:    */   {
/* 114:186 */     this.m_logPanel.logMessage(message);
/* 115:    */   }
/* 116:    */   
/* 117:    */   public synchronized void statusMessage(String message)
/* 118:    */   {
/* 119:203 */     boolean hasDelimiters = message.indexOf('|') > 0;
/* 120:204 */     String stepName = "";
/* 121:205 */     String stepHash = "";
/* 122:206 */     String stepParameters = "";
/* 123:207 */     String stepStatus = "";
/* 124:208 */     boolean noTimer = false;
/* 125:210 */     if (!hasDelimiters)
/* 126:    */     {
/* 127:211 */       stepName = "Unknown";
/* 128:212 */       stepHash = "Unknown";
/* 129:213 */       stepStatus = message;
/* 130:    */     }
/* 131:    */     else
/* 132:    */     {
/* 133:216 */       stepHash = message.substring(0, message.indexOf('|'));
/* 134:217 */       message = message.substring(message.indexOf('|') + 1, message.length());
/* 135:219 */       if (stepHash.indexOf('$') > 0) {
/* 136:221 */         stepName = stepHash.substring(0, stepHash.indexOf('$'));
/* 137:    */       } else {
/* 138:223 */         stepName = stepHash;
/* 139:    */       }
/* 140:226 */       if (stepName.startsWith("@!@"))
/* 141:    */       {
/* 142:227 */         noTimer = true;
/* 143:228 */         stepName = stepName.substring(3, stepName.length());
/* 144:    */       }
/* 145:232 */       if (message.indexOf('|') >= 0)
/* 146:    */       {
/* 147:233 */         stepParameters = message.substring(0, message.indexOf('|'));
/* 148:234 */         stepStatus = message.substring(message.indexOf('|') + 1, message.length());
/* 149:    */       }
/* 150:    */       else
/* 151:    */       {
/* 152:238 */         stepStatus = message;
/* 153:    */       }
/* 154:    */     }
/* 155:243 */     if (this.m_tableIndexes.containsKey(stepHash))
/* 156:    */     {
/* 157:245 */       final Integer rowNum = (Integer)this.m_tableIndexes.get(stepHash);
/* 158:246 */       if ((stepStatus.equalsIgnoreCase("remove")) || (stepStatus.equalsIgnoreCase("remove.")))
/* 159:    */       {
/* 160:250 */         this.m_tableIndexes.remove(stepHash);
/* 161:251 */         Timer t = (Timer)this.m_timers.get(stepHash);
/* 162:252 */         if (t != null)
/* 163:    */         {
/* 164:253 */           t.stop();
/* 165:254 */           this.m_timers.remove(stepHash);
/* 166:    */         }
/* 167:259 */         Iterator<String> i = this.m_tableIndexes.keySet().iterator();
/* 168:260 */         while (i.hasNext())
/* 169:    */         {
/* 170:261 */           String nextKey = (String)i.next();
/* 171:262 */           int index = ((Integer)this.m_tableIndexes.get(nextKey)).intValue();
/* 172:263 */           if (index > rowNum.intValue())
/* 173:    */           {
/* 174:264 */             index--;
/* 175:    */             
/* 176:    */ 
/* 177:267 */             this.m_tableIndexes.put(nextKey, Integer.valueOf(index));
/* 178:    */           }
/* 179:    */         }
/* 180:274 */         if (!SwingUtilities.isEventDispatchThread()) {
/* 181:    */           try
/* 182:    */           {
/* 183:276 */             SwingUtilities.invokeLater(new Runnable()
/* 184:    */             {
/* 185:    */               public void run()
/* 186:    */               {
/* 187:279 */                 LogPanel.this.m_tableModel.removeRow(rowNum.intValue());
/* 188:    */               }
/* 189:    */             });
/* 190:    */           }
/* 191:    */           catch (Exception ex)
/* 192:    */           {
/* 193:283 */             ex.printStackTrace();
/* 194:    */           }
/* 195:    */         } else {
/* 196:286 */           this.m_tableModel.removeRow(rowNum.intValue());
/* 197:    */         }
/* 198:    */       }
/* 199:    */       else
/* 200:    */       {
/* 201:289 */         final String stepNameCopy = stepName;
/* 202:290 */         final String stepStatusCopy = stepStatus;
/* 203:291 */         final String stepParametersCopy = stepParameters;
/* 204:293 */         if (!SwingUtilities.isEventDispatchThread())
/* 205:    */         {
/* 206:    */           try
/* 207:    */           {
/* 208:295 */             SwingUtilities.invokeLater(new Runnable()
/* 209:    */             {
/* 210:    */               public void run()
/* 211:    */               {
/* 212:298 */                 String currentStatus = LogPanel.this.m_tableModel.getValueAt(rowNum.intValue(), 3).toString();
/* 213:299 */                 if ((currentStatus.startsWith("INTERRUPTED")) || (currentStatus.startsWith("ERROR"))) {
/* 214:301 */                   return;
/* 215:    */                 }
/* 216:304 */                 if ((!stepStatusCopy.startsWith("INTERRUPTED")) || (!((String)LogPanel.this.m_tableModel.getValueAt(rowNum.intValue(), 3)).startsWith("ERROR")))
/* 217:    */                 {
/* 218:306 */                   LogPanel.this.m_tableModel.setValueAt(stepNameCopy, rowNum.intValue(), 0);
/* 219:307 */                   LogPanel.this.m_tableModel.setValueAt(stepParametersCopy, rowNum.intValue(), 1);
/* 220:    */                   
/* 221:309 */                   LogPanel.this.m_tableModel.setValueAt(LogPanel.this.m_table.getValueAt(rowNum.intValue(), 2), rowNum.intValue(), 2);
/* 222:    */                   
/* 223:    */ 
/* 224:312 */                   LogPanel.this.m_tableModel.setValueAt(stepStatusCopy, rowNum.intValue(), 3);
/* 225:    */                 }
/* 226:    */               }
/* 227:    */             });
/* 228:    */           }
/* 229:    */           catch (Exception ex)
/* 230:    */           {
/* 231:317 */             ex.printStackTrace();
/* 232:    */           }
/* 233:    */         }
/* 234:    */         else
/* 235:    */         {
/* 236:320 */           String currentStatus = this.m_tableModel.getValueAt(rowNum.intValue(), 3).toString();
/* 237:321 */           if ((currentStatus.startsWith("INTERRUPTED")) || (currentStatus.startsWith("ERROR"))) {
/* 238:323 */             return;
/* 239:    */           }
/* 240:325 */           if ((!stepStatusCopy.startsWith("INTERRUPTED")) || (!((String)this.m_tableModel.getValueAt(rowNum.intValue(), 3)).startsWith("ERROR")))
/* 241:    */           {
/* 242:327 */             this.m_tableModel.setValueAt(stepNameCopy, rowNum.intValue(), 0);
/* 243:328 */             this.m_tableModel.setValueAt(stepParametersCopy, rowNum.intValue(), 1);
/* 244:329 */             this.m_tableModel.setValueAt(this.m_table.getValueAt(rowNum.intValue(), 2), rowNum.intValue(), 2);
/* 245:331 */             if ((this.m_table.getValueAt(rowNum.intValue(), 3) != null) && (!this.m_table.getValueAt(rowNum.intValue(), 3).toString().toLowerCase().startsWith("finished"))) {
/* 246:335 */               this.m_tableModel.setValueAt(stepStatusCopy, rowNum.intValue(), 3);
/* 247:    */             }
/* 248:    */           }
/* 249:    */         }
/* 250:339 */         if ((stepStatus.startsWith("ERROR")) || (stepStatus.startsWith("INTERRUPTED")) || (stepStatus.toLowerCase().startsWith("finished")) || (stepStatus.toLowerCase().startsWith("done")) || (stepStatus.equalsIgnoreCase("stopped")) || (stepStatus.equalsIgnoreCase("stopped.")))
/* 251:    */         {
/* 252:350 */           Timer t = (Timer)this.m_timers.get(stepHash);
/* 253:351 */           if (t != null) {
/* 254:352 */             t.stop();
/* 255:    */           }
/* 256:    */         }
/* 257:354 */         else if ((this.m_timers.get(stepHash) != null) && (!((Timer)this.m_timers.get(stepHash)).isRunning()))
/* 258:    */         {
/* 259:358 */           installTimer(stepHash);
/* 260:    */         }
/* 261:    */       }
/* 262:    */     }
/* 263:362 */     else if ((!stepStatus.equalsIgnoreCase("Remove")) && (!stepStatus.equalsIgnoreCase("Remove.")))
/* 264:    */     {
/* 265:365 */       int numKeys = this.m_tableIndexes.keySet().size();
/* 266:366 */       this.m_tableIndexes.put(stepHash, Integer.valueOf(numKeys));
/* 267:    */       
/* 268:    */ 
/* 269:369 */       final Object[] newRow = new Object[4];
/* 270:370 */       newRow[0] = stepName;
/* 271:371 */       newRow[1] = stepParameters;
/* 272:372 */       newRow[2] = "-";
/* 273:373 */       newRow[3] = stepStatus;
/* 274:374 */       String stepHashCopy = stepHash;
/* 275:    */       try
/* 276:    */       {
/* 277:376 */         if (!SwingUtilities.isEventDispatchThread()) {
/* 278:377 */           SwingUtilities.invokeLater(new Runnable()
/* 279:    */           {
/* 280:    */             public void run()
/* 281:    */             {
/* 282:380 */               LogPanel.this.m_tableModel.addRow(newRow);
/* 283:    */             }
/* 284:    */           });
/* 285:    */         } else {
/* 286:385 */           this.m_tableModel.addRow(newRow);
/* 287:    */         }
/* 288:388 */         if ((!noTimer) && (!stepStatus.toLowerCase().startsWith("finished")) && (!stepStatus.toLowerCase().startsWith("done"))) {
/* 289:390 */           installTimer(stepHashCopy);
/* 290:    */         }
/* 291:    */       }
/* 292:    */       catch (Exception ex)
/* 293:    */       {
/* 294:393 */         ex.printStackTrace();
/* 295:    */       }
/* 296:    */     }
/* 297:    */   }
/* 298:    */   
/* 299:    */   private void installTimer(final String stepHash)
/* 300:    */   {
/* 301:399 */     final long startTime = System.currentTimeMillis();
/* 302:400 */     Timer newTimer = new Timer(1000, new ActionListener()
/* 303:    */     {
/* 304:    */       public void actionPerformed(ActionEvent e)
/* 305:    */       {
/* 306:403 */         synchronized (LogPanel.this)
/* 307:    */         {
/* 308:404 */           if (LogPanel.this.m_tableIndexes.containsKey(stepHash))
/* 309:    */           {
/* 310:405 */             Integer rn = (Integer)LogPanel.this.m_tableIndexes.get(stepHash);
/* 311:406 */             long elapsed = System.currentTimeMillis() - startTime;
/* 312:407 */             long seconds = elapsed / 1000L;
/* 313:408 */             long minutes = seconds / 60L;
/* 314:409 */             final long hours = minutes / 60L;
/* 315:410 */             seconds -= minutes * 60L;
/* 316:411 */             minutes -= hours * 60L;
/* 317:412 */             final long seconds2 = seconds;
/* 318:413 */             long minutes2 = minutes;
/* 319:414 */             if (!SwingUtilities.isEventDispatchThread()) {
/* 320:    */               try
/* 321:    */               {
/* 322:416 */                 SwingUtilities.invokeLater(new Runnable()
/* 323:    */                 {
/* 324:    */                   public void run()
/* 325:    */                   {
/* 326:419 */                     LogPanel.this.m_tableModel.setValueAt("" + LogPanel.this.m_formatter.format(hours) + ":" + LogPanel.this.m_formatter.format(seconds2) + ":" + LogPanel.this.m_formatter.format(this.val$seconds2), this.val$rn.intValue(), 2);
/* 327:    */                   }
/* 328:    */                 });
/* 329:    */               }
/* 330:    */               catch (Exception ex)
/* 331:    */               {
/* 332:426 */                 ex.printStackTrace();
/* 333:    */               }
/* 334:    */             } else {
/* 335:429 */               LogPanel.this.m_tableModel.setValueAt("" + LogPanel.this.m_formatter.format(hours) + ":" + LogPanel.this.m_formatter.format(minutes2) + ":" + LogPanel.this.m_formatter.format(seconds2), rn.intValue(), 2);
/* 336:    */             }
/* 337:    */           }
/* 338:    */         }
/* 339:    */       }
/* 340:437 */     });
/* 341:438 */     this.m_timers.put(stepHash, newTimer);
/* 342:439 */     newTimer.start();
/* 343:    */   }
/* 344:    */   
/* 345:    */   public void setLoggingFontSize(int size)
/* 346:    */   {
/* 347:449 */     this.m_logPanel.setLoggingFontSize(size);
/* 348:    */   }
/* 349:    */   
/* 350:    */   public static void main(String[] args)
/* 351:    */   {
/* 352:    */     try
/* 353:    */     {
/* 354:459 */       JFrame jf = new JFrame("Status/Log Panel");
/* 355:    */       
/* 356:461 */       jf.getContentPane().setLayout(new BorderLayout());
/* 357:462 */       LogPanel lp = new LogPanel();
/* 358:463 */       jf.getContentPane().add(lp, "Center");
/* 359:    */       
/* 360:465 */       jf.getContentPane().add(lp, "Center");
/* 361:466 */       jf.addWindowListener(new WindowAdapter()
/* 362:    */       {
/* 363:    */         public void windowClosing(WindowEvent e)
/* 364:    */         {
/* 365:469 */           this.val$jf.dispose();
/* 366:470 */           System.exit(0);
/* 367:    */         }
/* 368:472 */       });
/* 369:473 */       jf.pack();
/* 370:474 */       jf.setVisible(true);
/* 371:475 */       lp.statusMessage("Step 1|Some options here|A status message");
/* 372:476 */       lp.statusMessage("Step 2$hashkey|Status message: no options");
/* 373:477 */       Thread.sleep(3000L);
/* 374:478 */       lp.statusMessage("Step 2$hashkey|Funky Chickens!!!");
/* 375:479 */       Thread.sleep(3000L);
/* 376:480 */       lp.statusMessage("Step 1|Some options here|finished");
/* 377:    */       
/* 378:482 */       Thread.sleep(3000L);
/* 379:483 */       lp.statusMessage("Step 2$hashkey|ERROR! More Funky Chickens!!!");
/* 380:484 */       Thread.sleep(3000L);
/* 381:485 */       lp.statusMessage("Step 2$hashkey|WARNING - now a warning...");
/* 382:486 */       Thread.sleep(3000L);
/* 383:487 */       lp.statusMessage("Step 2$hashkey|Back to normal.");
/* 384:488 */       Thread.sleep(3000L);
/* 385:489 */       lp.statusMessage("Step 2$hashkey|INTERRUPTED.");
/* 386:    */     }
/* 387:    */     catch (Exception ex)
/* 388:    */     {
/* 389:492 */       ex.printStackTrace();
/* 390:    */     }
/* 391:    */   }
/* 392:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.LogPanel
 * JD-Core Version:    0.7.0.1
 */