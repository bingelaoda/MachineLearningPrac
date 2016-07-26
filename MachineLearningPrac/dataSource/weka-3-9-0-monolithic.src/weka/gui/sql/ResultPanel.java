/*   1:    */ package weka.gui.sql;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.event.ActionEvent;
/*   6:    */ import java.awt.event.ActionListener;
/*   7:    */ import java.io.PrintStream;
/*   8:    */ import java.util.HashSet;
/*   9:    */ import java.util.Iterator;
/*  10:    */ import javax.swing.JButton;
/*  11:    */ import javax.swing.JFrame;
/*  12:    */ import javax.swing.JLabel;
/*  13:    */ import javax.swing.JPanel;
/*  14:    */ import javax.swing.JScrollPane;
/*  15:    */ import javax.swing.JTabbedPane;
/*  16:    */ import javax.swing.JViewport;
/*  17:    */ import javax.swing.event.ChangeEvent;
/*  18:    */ import javax.swing.event.ChangeListener;
/*  19:    */ import weka.gui.JTableHelper;
/*  20:    */ import weka.gui.sql.event.QueryExecuteEvent;
/*  21:    */ import weka.gui.sql.event.QueryExecuteListener;
/*  22:    */ import weka.gui.sql.event.ResultChangedEvent;
/*  23:    */ import weka.gui.sql.event.ResultChangedListener;
/*  24:    */ 
/*  25:    */ public class ResultPanel
/*  26:    */   extends JPanel
/*  27:    */   implements QueryExecuteListener, ChangeListener
/*  28:    */ {
/*  29:    */   private static final long serialVersionUID = 278654800344034571L;
/*  30:    */   protected JFrame m_Parent;
/*  31:    */   protected HashSet<ResultChangedListener> m_Listeners;
/*  32:    */   protected QueryPanel m_QueryPanel;
/*  33:    */   protected JTabbedPane m_TabbedPane;
/*  34: 72 */   protected JButton m_ButtonClose = new JButton("Close");
/*  35: 75 */   protected JButton m_ButtonCloseAll = new JButton("Close all");
/*  36: 78 */   protected JButton m_ButtonCopyQuery = new JButton("Re-use query");
/*  37: 81 */   protected JButton m_ButtonOptWidth = new JButton("Optimal width");
/*  38:    */   protected int m_NameCounter;
/*  39:    */   
/*  40:    */   public ResultPanel(JFrame parent)
/*  41:    */   {
/*  42: 94 */     this.m_Parent = parent;
/*  43: 95 */     this.m_QueryPanel = null;
/*  44: 96 */     this.m_NameCounter = 0;
/*  45: 97 */     this.m_Listeners = new HashSet();
/*  46:    */     
/*  47: 99 */     createPanel();
/*  48:    */   }
/*  49:    */   
/*  50:    */   protected void createPanel()
/*  51:    */   {
/*  52:111 */     setLayout(new BorderLayout());
/*  53:112 */     setPreferredSize(new Dimension(0, 200));
/*  54:    */     
/*  55:    */ 
/*  56:115 */     this.m_TabbedPane = new JTabbedPane(3);
/*  57:116 */     this.m_TabbedPane.addChangeListener(this);
/*  58:117 */     add(this.m_TabbedPane, "Center");
/*  59:    */     
/*  60:    */ 
/*  61:120 */     JPanel panel = new JPanel(new BorderLayout());
/*  62:121 */     add(panel, "East");
/*  63:122 */     JPanel panel2 = new JPanel(new BorderLayout());
/*  64:123 */     panel.add(panel2, "Center");
/*  65:124 */     JPanel panel3 = new JPanel(new BorderLayout());
/*  66:125 */     panel2.add(panel3, "Center");
/*  67:126 */     JPanel panel4 = new JPanel(new BorderLayout());
/*  68:127 */     panel3.add(panel4, "Center");
/*  69:    */     
/*  70:129 */     this.m_ButtonClose.setMnemonic('l');
/*  71:130 */     this.m_ButtonClose.addActionListener(new ActionListener()
/*  72:    */     {
/*  73:    */       public void actionPerformed(ActionEvent e)
/*  74:    */       {
/*  75:133 */         ResultPanel.this.close();
/*  76:    */       }
/*  77:135 */     });
/*  78:136 */     panel.add(this.m_ButtonClose, "North");
/*  79:    */     
/*  80:138 */     this.m_ButtonCloseAll.setMnemonic('a');
/*  81:139 */     this.m_ButtonCloseAll.addActionListener(new ActionListener()
/*  82:    */     {
/*  83:    */       public void actionPerformed(ActionEvent e)
/*  84:    */       {
/*  85:142 */         ResultPanel.this.closeAll();
/*  86:    */       }
/*  87:144 */     });
/*  88:145 */     panel2.add(this.m_ButtonCloseAll, "North");
/*  89:    */     
/*  90:147 */     this.m_ButtonCopyQuery.setMnemonic('Q');
/*  91:148 */     this.m_ButtonCopyQuery.setToolTipText("Copies the query of the currently selected tab into the query field.");
/*  92:    */     
/*  93:150 */     this.m_ButtonCopyQuery.addActionListener(new ActionListener()
/*  94:    */     {
/*  95:    */       public void actionPerformed(ActionEvent e)
/*  96:    */       {
/*  97:153 */         ResultPanel.this.copyQuery();
/*  98:    */       }
/*  99:155 */     });
/* 100:156 */     panel3.add(this.m_ButtonCopyQuery, "North");
/* 101:    */     
/* 102:158 */     this.m_ButtonOptWidth.setMnemonic('p');
/* 103:159 */     this.m_ButtonOptWidth.setToolTipText("Calculates the optimal column width for the current table.");
/* 104:    */     
/* 105:161 */     this.m_ButtonOptWidth.addActionListener(new ActionListener()
/* 106:    */     {
/* 107:    */       public void actionPerformed(ActionEvent e)
/* 108:    */       {
/* 109:164 */         ResultPanel.this.calcOptimalWidth();
/* 110:    */       }
/* 111:166 */     });
/* 112:167 */     panel4.add(this.m_ButtonOptWidth, "North");
/* 113:    */     
/* 114:    */ 
/* 115:    */ 
/* 116:171 */     panel4.add(new JLabel(" "), "Center");
/* 117:172 */     panel4.add(new JLabel(" "), "South");
/* 118:    */     
/* 119:    */ 
/* 120:175 */     setButtons();
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void clear()
/* 124:    */   {
/* 125:182 */     closeAll();
/* 126:    */   }
/* 127:    */   
/* 128:    */   public void setFocus()
/* 129:    */   {
/* 130:189 */     this.m_TabbedPane.requestFocus();
/* 131:    */   }
/* 132:    */   
/* 133:    */   protected void setButtons()
/* 134:    */   {
/* 135:198 */     int index = this.m_TabbedPane.getSelectedIndex();
/* 136:    */     
/* 137:200 */     this.m_ButtonClose.setEnabled(index > -1);
/* 138:201 */     this.m_ButtonCloseAll.setEnabled(this.m_TabbedPane.getTabCount() > 0);
/* 139:202 */     this.m_ButtonCopyQuery.setEnabled(index > -1);
/* 140:203 */     this.m_ButtonOptWidth.setEnabled(index > -1);
/* 141:    */   }
/* 142:    */   
/* 143:    */   protected String getNextTabName()
/* 144:    */   {
/* 145:210 */     this.m_NameCounter += 1;
/* 146:211 */     return "Query" + this.m_NameCounter;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public void queryExecuted(QueryExecuteEvent evt)
/* 150:    */   {
/* 151:222 */     if (evt.failed()) {
/* 152:223 */       return;
/* 153:    */     }
/* 154:227 */     if (!evt.hasResult()) {
/* 155:228 */       return;
/* 156:    */     }
/* 157:    */     try
/* 158:    */     {
/* 159:232 */       ResultSetTable table = new ResultSetTable(evt.getDbUtils().getDatabaseURL(), evt.getDbUtils().getUsername(), evt.getDbUtils().getPassword(), evt.getQuery(), new ResultSetTableModel(evt.getResultSet(), evt.getMaxRows()));
/* 160:    */       
/* 161:    */ 
/* 162:    */ 
/* 163:236 */       this.m_TabbedPane.addTab(getNextTabName(), new JScrollPane(table));
/* 164:    */       
/* 165:    */ 
/* 166:239 */       this.m_TabbedPane.setSelectedIndex(this.m_TabbedPane.getTabCount() - 1);
/* 167:    */     }
/* 168:    */     catch (Exception e)
/* 169:    */     {
/* 170:241 */       e.printStackTrace();
/* 171:    */     }
/* 172:245 */     setButtons();
/* 173:    */   }
/* 174:    */   
/* 175:    */   public void stateChanged(ChangeEvent e)
/* 176:    */   {
/* 177:254 */     setButtons();
/* 178:257 */     if (getCurrentTable() != null) {
/* 179:258 */       notifyListeners(getCurrentTable().getURL(), getCurrentTable().getUser(), getCurrentTable().getPassword(), getCurrentTable().getQuery());
/* 180:    */     }
/* 181:    */   }
/* 182:    */   
/* 183:    */   public QueryPanel getQueryPanel()
/* 184:    */   {
/* 185:269 */     return this.m_QueryPanel;
/* 186:    */   }
/* 187:    */   
/* 188:    */   public void setQueryPanel(QueryPanel panel)
/* 189:    */   {
/* 190:278 */     this.m_QueryPanel = panel;
/* 191:    */   }
/* 192:    */   
/* 193:    */   protected ResultSetTable getCurrentTable()
/* 194:    */   {
/* 195:292 */     ResultSetTable table = null;
/* 196:    */     
/* 197:294 */     int index = this.m_TabbedPane.getSelectedIndex();
/* 198:295 */     if (index > -1)
/* 199:    */     {
/* 200:296 */       JScrollPane pane = (JScrollPane)this.m_TabbedPane.getComponentAt(index);
/* 201:297 */       JViewport port = (JViewport)pane.getComponent(0);
/* 202:298 */       table = (ResultSetTable)port.getComponent(0);
/* 203:    */     }
/* 204:301 */     return table;
/* 205:    */   }
/* 206:    */   
/* 207:    */   protected void close()
/* 208:    */   {
/* 209:310 */     int index = this.m_TabbedPane.getSelectedIndex();
/* 210:312 */     if (index > -1)
/* 211:    */     {
/* 212:    */       try
/* 213:    */       {
/* 214:314 */         getCurrentTable().finalize();
/* 215:    */       }
/* 216:    */       catch (Throwable t)
/* 217:    */       {
/* 218:316 */         System.out.println(t);
/* 219:    */       }
/* 220:318 */       this.m_TabbedPane.removeTabAt(index);
/* 221:    */     }
/* 222:322 */     setButtons();
/* 223:    */   }
/* 224:    */   
/* 225:    */   protected void closeAll()
/* 226:    */   {
/* 227:329 */     while (this.m_TabbedPane.getTabCount() > 0)
/* 228:    */     {
/* 229:330 */       this.m_TabbedPane.setSelectedIndex(0);
/* 230:    */       try
/* 231:    */       {
/* 232:332 */         getCurrentTable().finalize();
/* 233:    */       }
/* 234:    */       catch (Throwable t)
/* 235:    */       {
/* 236:334 */         System.out.println(t);
/* 237:    */       }
/* 238:336 */       this.m_TabbedPane.removeTabAt(0);
/* 239:    */     }
/* 240:340 */     setButtons();
/* 241:    */   }
/* 242:    */   
/* 243:    */   protected void copyQuery()
/* 244:    */   {
/* 245:347 */     if ((getCurrentTable() != null) && (getQueryPanel() != null)) {
/* 246:348 */       getQueryPanel().setQuery(getCurrentTable().getQuery());
/* 247:    */     }
/* 248:    */   }
/* 249:    */   
/* 250:    */   protected void calcOptimalWidth()
/* 251:    */   {
/* 252:356 */     if (getCurrentTable() != null) {
/* 253:357 */       JTableHelper.setOptimalColumnWidth(getCurrentTable());
/* 254:    */     }
/* 255:    */   }
/* 256:    */   
/* 257:    */   public void addResultChangedListener(ResultChangedListener l)
/* 258:    */   {
/* 259:367 */     this.m_Listeners.add(l);
/* 260:    */   }
/* 261:    */   
/* 262:    */   public void removeResultChangedListener(ResultChangedListener l)
/* 263:    */   {
/* 264:376 */     this.m_Listeners.remove(l);
/* 265:    */   }
/* 266:    */   
/* 267:    */   protected void notifyListeners(String url, String user, String pw, String query)
/* 268:    */   {
/* 269:392 */     Iterator<ResultChangedListener> iter = this.m_Listeners.iterator();
/* 270:393 */     while (iter.hasNext())
/* 271:    */     {
/* 272:394 */       ResultChangedListener l = (ResultChangedListener)iter.next();
/* 273:395 */       l.resultChanged(new ResultChangedEvent(this, url, user, pw, query));
/* 274:    */     }
/* 275:    */   }
/* 276:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.ResultPanel
 * JD-Core Version:    0.7.0.1
 */