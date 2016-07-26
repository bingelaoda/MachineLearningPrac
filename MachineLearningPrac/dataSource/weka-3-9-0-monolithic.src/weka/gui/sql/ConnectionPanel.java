/*   1:    */ package weka.gui.sql;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.FlowLayout;
/*   5:    */ import java.awt.event.ActionEvent;
/*   6:    */ import java.awt.event.ActionListener;
/*   7:    */ import java.util.HashSet;
/*   8:    */ import java.util.Iterator;
/*   9:    */ import javax.swing.DefaultListModel;
/*  10:    */ import javax.swing.JButton;
/*  11:    */ import javax.swing.JFileChooser;
/*  12:    */ import javax.swing.JFrame;
/*  13:    */ import javax.swing.JLabel;
/*  14:    */ import javax.swing.JList;
/*  15:    */ import javax.swing.JPanel;
/*  16:    */ import javax.swing.JTextField;
/*  17:    */ import javax.swing.event.CaretEvent;
/*  18:    */ import javax.swing.event.CaretListener;
/*  19:    */ import weka.gui.ComponentHelper;
/*  20:    */ import weka.gui.DatabaseConnectionDialog;
/*  21:    */ import weka.gui.ExtensionFileFilter;
/*  22:    */ import weka.gui.ListSelectorDialog;
/*  23:    */ import weka.gui.sql.event.ConnectionEvent;
/*  24:    */ import weka.gui.sql.event.ConnectionListener;
/*  25:    */ import weka.gui.sql.event.HistoryChangedEvent;
/*  26:    */ import weka.gui.sql.event.HistoryChangedListener;
/*  27:    */ 
/*  28:    */ public class ConnectionPanel
/*  29:    */   extends JPanel
/*  30:    */   implements CaretListener
/*  31:    */ {
/*  32:    */   static final long serialVersionUID = 3499317023969723490L;
/*  33:    */   public static final String HISTORY_NAME = "connection";
/*  34: 68 */   protected JFrame m_Parent = null;
/*  35:    */   protected DatabaseConnectionDialog m_DbDialog;
/*  36: 74 */   protected String m_URL = "";
/*  37: 77 */   protected String m_User = "";
/*  38: 80 */   protected String m_Password = "";
/*  39: 83 */   protected JLabel m_LabelURL = new JLabel("URL ");
/*  40: 86 */   protected JTextField m_TextURL = new JTextField(40);
/*  41: 89 */   protected JButton m_ButtonDatabase = new JButton(ComponentHelper.getImageIcon("user.png"));
/*  42: 93 */   protected JButton m_ButtonConnect = new JButton(ComponentHelper.getImageIcon("connect.png"));
/*  43: 97 */   protected JButton m_ButtonHistory = new JButton(ComponentHelper.getImageIcon("history.png"));
/*  44:101 */   protected JButton m_ButtonSetup = new JButton(ComponentHelper.getImageIcon("properties.gif"));
/*  45:    */   protected HashSet<ConnectionListener> m_ConnectionListeners;
/*  46:    */   protected HashSet<HistoryChangedListener> m_HistoryChangedListeners;
/*  47:    */   protected DbUtils m_DbUtils;
/*  48:114 */   protected DefaultListModel m_History = new DefaultListModel();
/*  49:    */   protected JFileChooser m_SetupFileChooser;
/*  50:    */   
/*  51:    */   public ConnectionPanel(JFrame parent)
/*  52:    */   {
/*  53:127 */     this.m_Parent = parent;
/*  54:128 */     this.m_ConnectionListeners = new HashSet();
/*  55:129 */     this.m_HistoryChangedListeners = new HashSet();
/*  56:130 */     this.m_SetupFileChooser = new JFileChooser();
/*  57:131 */     this.m_SetupFileChooser.setDialogTitle("Switch database setup");
/*  58:132 */     this.m_SetupFileChooser.setFileSelectionMode(0);
/*  59:133 */     this.m_SetupFileChooser.setMultiSelectionEnabled(false);
/*  60:134 */     this.m_SetupFileChooser.setAcceptAllFileFilterUsed(true);
/*  61:135 */     ExtensionFileFilter filter = new ExtensionFileFilter(".props", "Properties file");
/*  62:    */     
/*  63:137 */     this.m_SetupFileChooser.addChoosableFileFilter(filter);
/*  64:138 */     this.m_SetupFileChooser.setFileFilter(filter);
/*  65:    */     try
/*  66:    */     {
/*  67:141 */       this.m_DbUtils = new DbUtils();
/*  68:142 */       this.m_URL = this.m_DbUtils.getDatabaseURL();
/*  69:143 */       this.m_User = this.m_DbUtils.getUsername();
/*  70:144 */       this.m_Password = this.m_DbUtils.getPassword();
/*  71:    */     }
/*  72:    */     catch (Exception e)
/*  73:    */     {
/*  74:146 */       e.printStackTrace();
/*  75:147 */       this.m_URL = "";
/*  76:148 */       this.m_User = "";
/*  77:149 */       this.m_Password = "";
/*  78:    */     }
/*  79:152 */     createPanel();
/*  80:    */   }
/*  81:    */   
/*  82:    */   protected void createPanel()
/*  83:    */   {
/*  84:162 */     setLayout(new BorderLayout());
/*  85:163 */     JPanel panel2 = new JPanel(new FlowLayout());
/*  86:164 */     add(panel2, "West");
/*  87:    */     
/*  88:    */ 
/*  89:167 */     this.m_LabelURL.setLabelFor(this.m_ButtonDatabase);
/*  90:168 */     this.m_LabelURL.setDisplayedMnemonic('U');
/*  91:169 */     panel2.add(this.m_LabelURL);
/*  92:    */     
/*  93:    */ 
/*  94:172 */     this.m_TextURL.setText(this.m_URL);
/*  95:173 */     this.m_TextURL.addCaretListener(this);
/*  96:174 */     panel2.add(this.m_TextURL);
/*  97:    */     
/*  98:    */ 
/*  99:177 */     JPanel panel = new JPanel(new FlowLayout());
/* 100:178 */     panel2.add(panel);
/* 101:    */     
/* 102:180 */     this.m_ButtonDatabase.setToolTipText("Set user and password");
/* 103:181 */     this.m_ButtonDatabase.addActionListener(new ActionListener()
/* 104:    */     {
/* 105:    */       public void actionPerformed(ActionEvent e)
/* 106:    */       {
/* 107:184 */         ConnectionPanel.this.showDialog();
/* 108:    */       }
/* 109:186 */     });
/* 110:187 */     panel.add(this.m_ButtonDatabase);
/* 111:    */     
/* 112:189 */     this.m_ButtonConnect.setToolTipText("Connect to the database");
/* 113:190 */     this.m_ButtonConnect.addActionListener(new ActionListener()
/* 114:    */     {
/* 115:    */       public void actionPerformed(ActionEvent e)
/* 116:    */       {
/* 117:193 */         ConnectionPanel.this.connect();
/* 118:    */       }
/* 119:195 */     });
/* 120:196 */     panel.add(this.m_ButtonConnect);
/* 121:    */     
/* 122:198 */     this.m_ButtonHistory.setToolTipText("Select a previously used connection");
/* 123:199 */     this.m_ButtonHistory.addActionListener(new ActionListener()
/* 124:    */     {
/* 125:    */       public void actionPerformed(ActionEvent e)
/* 126:    */       {
/* 127:202 */         ConnectionPanel.this.showHistory();
/* 128:    */       }
/* 129:204 */     });
/* 130:205 */     panel.add(this.m_ButtonHistory);
/* 131:    */     
/* 132:207 */     this.m_ButtonSetup.setToolTipText("Switch database setup");
/* 133:208 */     this.m_ButtonSetup.addActionListener(new ActionListener()
/* 134:    */     {
/* 135:    */       public void actionPerformed(ActionEvent e)
/* 136:    */       {
/* 137:211 */         ConnectionPanel.this.switchSetup();
/* 138:    */       }
/* 139:213 */     });
/* 140:214 */     panel.add(this.m_ButtonSetup);
/* 141:    */     
/* 142:216 */     setButtons();
/* 143:    */   }
/* 144:    */   
/* 145:    */   protected void setButtons()
/* 146:    */   {
/* 147:225 */     boolean isEmpty = this.m_TextURL.getText().equals("");
/* 148:    */     
/* 149:227 */     this.m_ButtonConnect.setEnabled(!isEmpty);
/* 150:228 */     this.m_ButtonDatabase.setEnabled(!isEmpty);
/* 151:229 */     this.m_ButtonHistory.setEnabled(this.m_History.size() > 0);
/* 152:230 */     this.m_ButtonSetup.setEnabled(true);
/* 153:    */   }
/* 154:    */   
/* 155:    */   public void clear()
/* 156:    */   {
/* 157:237 */     setURL(this.m_DbUtils.getDatabaseURL());
/* 158:238 */     setUser(this.m_DbUtils.getUsername());
/* 159:239 */     setPassword(this.m_DbUtils.getPassword());
/* 160:    */   }
/* 161:    */   
/* 162:    */   public void setFocus()
/* 163:    */   {
/* 164:246 */     this.m_TextURL.requestFocus();
/* 165:    */   }
/* 166:    */   
/* 167:    */   public void setURL(String url)
/* 168:    */   {
/* 169:255 */     this.m_URL = url;
/* 170:256 */     this.m_TextURL.setText(url);
/* 171:    */   }
/* 172:    */   
/* 173:    */   public String getURL()
/* 174:    */   {
/* 175:265 */     this.m_URL = this.m_TextURL.getText();
/* 176:266 */     return this.m_URL;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public void setUser(String user)
/* 180:    */   {
/* 181:275 */     this.m_User = user;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public String getUser()
/* 185:    */   {
/* 186:284 */     return this.m_User;
/* 187:    */   }
/* 188:    */   
/* 189:    */   public void setPassword(String pw)
/* 190:    */   {
/* 191:293 */     this.m_Password = pw;
/* 192:    */   }
/* 193:    */   
/* 194:    */   public String getPassword()
/* 195:    */   {
/* 196:302 */     return this.m_Password;
/* 197:    */   }
/* 198:    */   
/* 199:    */   protected void addHistory(String s)
/* 200:    */   {
/* 201:311 */     if (s.equals("")) {
/* 202:312 */       return;
/* 203:    */     }
/* 204:316 */     if (this.m_History.contains(s)) {
/* 205:317 */       this.m_History.removeElement(s);
/* 206:    */     }
/* 207:320 */     this.m_History.add(0, s);
/* 208:    */     
/* 209:    */ 
/* 210:323 */     notifyHistoryChangedListeners();
/* 211:    */   }
/* 212:    */   
/* 213:    */   public void setHistory(DefaultListModel history)
/* 214:    */   {
/* 215:334 */     this.m_History.clear();
/* 216:335 */     for (int i = 0; i < history.size(); i++) {
/* 217:336 */       this.m_History.addElement(history.get(i));
/* 218:    */     }
/* 219:339 */     setButtons();
/* 220:    */   }
/* 221:    */   
/* 222:    */   public DefaultListModel getHistory()
/* 223:    */   {
/* 224:348 */     return this.m_History;
/* 225:    */   }
/* 226:    */   
/* 227:    */   protected void showDialog()
/* 228:    */   {
/* 229:355 */     this.m_DbDialog = new DatabaseConnectionDialog(this.m_Parent, getURL(), getUser(), false);
/* 230:    */     
/* 231:357 */     this.m_DbDialog.setVisible(true);
/* 232:358 */     if (this.m_DbDialog.getReturnValue() == 0)
/* 233:    */     {
/* 234:359 */       setURL(this.m_DbDialog.getURL());
/* 235:360 */       setUser(this.m_DbDialog.getUsername());
/* 236:361 */       setPassword(this.m_DbDialog.getPassword());
/* 237:    */     }
/* 238:364 */     setButtons();
/* 239:    */   }
/* 240:    */   
/* 241:    */   protected void connect()
/* 242:    */   {
/* 243:372 */     if (this.m_DbUtils.isConnected()) {
/* 244:    */       try
/* 245:    */       {
/* 246:374 */         this.m_DbUtils.disconnectFromDatabase();
/* 247:375 */         notifyConnectionListeners(1);
/* 248:    */       }
/* 249:    */       catch (Exception e)
/* 250:    */       {
/* 251:377 */         e.printStackTrace();
/* 252:378 */         notifyConnectionListeners(1, e);
/* 253:    */       }
/* 254:    */     }
/* 255:    */     try
/* 256:    */     {
/* 257:384 */       this.m_DbUtils.setDatabaseURL(getURL());
/* 258:385 */       this.m_DbUtils.setUsername(getUser());
/* 259:386 */       this.m_DbUtils.setPassword(getPassword());
/* 260:387 */       this.m_DbUtils.connectToDatabase();
/* 261:388 */       notifyConnectionListeners(0);
/* 262:    */       
/* 263:390 */       addHistory(getUser() + "@" + getURL());
/* 264:    */     }
/* 265:    */     catch (Exception e)
/* 266:    */     {
/* 267:392 */       e.printStackTrace();
/* 268:393 */       notifyConnectionListeners(0, e);
/* 269:    */     }
/* 270:396 */     setButtons();
/* 271:    */   }
/* 272:    */   
/* 273:    */   public void showHistory()
/* 274:    */   {
/* 275:407 */     JList list = new JList(this.m_History);
/* 276:408 */     ListSelectorDialog dialog = new ListSelectorDialog(this.m_Parent, list);
/* 277:410 */     if ((dialog.showDialog() == 0) && 
/* 278:411 */       (list.getSelectedValue() != null))
/* 279:    */     {
/* 280:412 */       String tmpStr = list.getSelectedValue().toString();
/* 281:413 */       if (tmpStr.indexOf("@") > -1)
/* 282:    */       {
/* 283:414 */         setUser(tmpStr.substring(0, tmpStr.indexOf("@")));
/* 284:415 */         setURL(tmpStr.substring(tmpStr.indexOf("@") + 1));
/* 285:416 */         showDialog();
/* 286:    */       }
/* 287:    */       else
/* 288:    */       {
/* 289:418 */         setUser("");
/* 290:419 */         setURL(tmpStr);
/* 291:    */       }
/* 292:    */     }
/* 293:424 */     setButtons();
/* 294:    */   }
/* 295:    */   
/* 296:    */   public void switchSetup()
/* 297:    */   {
/* 298:434 */     int retVal = this.m_SetupFileChooser.showOpenDialog(this);
/* 299:435 */     if (retVal != 0) {
/* 300:436 */       return;
/* 301:    */     }
/* 302:439 */     this.m_DbUtils.initialize(this.m_SetupFileChooser.getSelectedFile());
/* 303:    */     
/* 304:441 */     this.m_URL = this.m_DbUtils.getDatabaseURL();
/* 305:442 */     this.m_User = this.m_DbUtils.getUsername();
/* 306:443 */     this.m_Password = this.m_DbUtils.getPassword();
/* 307:    */     
/* 308:445 */     this.m_TextURL.setText(this.m_URL);
/* 309:    */   }
/* 310:    */   
/* 311:    */   public void addConnectionListener(ConnectionListener l)
/* 312:    */   {
/* 313:454 */     this.m_ConnectionListeners.add(l);
/* 314:    */   }
/* 315:    */   
/* 316:    */   public void removeConnectionListener(ConnectionListener l)
/* 317:    */   {
/* 318:463 */     this.m_ConnectionListeners.remove(l);
/* 319:    */   }
/* 320:    */   
/* 321:    */   protected void notifyConnectionListeners(int type)
/* 322:    */   {
/* 323:472 */     notifyConnectionListeners(type, null);
/* 324:    */   }
/* 325:    */   
/* 326:    */   protected void notifyConnectionListeners(int type, Exception ex)
/* 327:    */   {
/* 328:485 */     Iterator<ConnectionListener> iter = this.m_ConnectionListeners.iterator();
/* 329:486 */     while (iter.hasNext())
/* 330:    */     {
/* 331:487 */       ConnectionListener l = (ConnectionListener)iter.next();
/* 332:488 */       l.connectionChange(new ConnectionEvent(this, type, this.m_DbUtils, ex));
/* 333:    */     }
/* 334:    */   }
/* 335:    */   
/* 336:    */   public void addHistoryChangedListener(HistoryChangedListener l)
/* 337:    */   {
/* 338:498 */     this.m_HistoryChangedListeners.add(l);
/* 339:    */   }
/* 340:    */   
/* 341:    */   public void removeHistoryChangedListener(HistoryChangedListener l)
/* 342:    */   {
/* 343:507 */     this.m_HistoryChangedListeners.remove(l);
/* 344:    */   }
/* 345:    */   
/* 346:    */   protected void notifyHistoryChangedListeners()
/* 347:    */   {
/* 348:517 */     Iterator<HistoryChangedListener> iter = this.m_HistoryChangedListeners.iterator();
/* 349:518 */     while (iter.hasNext())
/* 350:    */     {
/* 351:519 */       HistoryChangedListener l = (HistoryChangedListener)iter.next();
/* 352:520 */       l.historyChanged(new HistoryChangedEvent(this, "connection", getHistory()));
/* 353:    */     }
/* 354:    */   }
/* 355:    */   
/* 356:    */   public void caretUpdate(CaretEvent event)
/* 357:    */   {
/* 358:531 */     setButtons();
/* 359:    */   }
/* 360:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.ConnectionPanel
 * JD-Core Version:    0.7.0.1
 */