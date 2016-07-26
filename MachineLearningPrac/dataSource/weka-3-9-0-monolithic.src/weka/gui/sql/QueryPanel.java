/*   1:    */ package weka.gui.sql;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.FlowLayout;
/*   6:    */ import java.awt.Font;
/*   7:    */ import java.awt.event.ActionEvent;
/*   8:    */ import java.awt.event.ActionListener;
/*   9:    */ import java.sql.ResultSet;
/*  10:    */ import java.util.HashSet;
/*  11:    */ import java.util.Iterator;
/*  12:    */ import javax.swing.DefaultListModel;
/*  13:    */ import javax.swing.JButton;
/*  14:    */ import javax.swing.JFrame;
/*  15:    */ import javax.swing.JLabel;
/*  16:    */ import javax.swing.JList;
/*  17:    */ import javax.swing.JPanel;
/*  18:    */ import javax.swing.JScrollPane;
/*  19:    */ import javax.swing.JSpinner;
/*  20:    */ import javax.swing.JTextArea;
/*  21:    */ import javax.swing.SpinnerNumberModel;
/*  22:    */ import javax.swing.event.CaretEvent;
/*  23:    */ import javax.swing.event.CaretListener;
/*  24:    */ import weka.gui.ListSelectorDialog;
/*  25:    */ import weka.gui.sql.event.ConnectionEvent;
/*  26:    */ import weka.gui.sql.event.ConnectionListener;
/*  27:    */ import weka.gui.sql.event.HistoryChangedEvent;
/*  28:    */ import weka.gui.sql.event.HistoryChangedListener;
/*  29:    */ import weka.gui.sql.event.QueryExecuteEvent;
/*  30:    */ import weka.gui.sql.event.QueryExecuteListener;
/*  31:    */ 
/*  32:    */ public class QueryPanel
/*  33:    */   extends JPanel
/*  34:    */   implements ConnectionListener, CaretListener
/*  35:    */ {
/*  36:    */   private static final long serialVersionUID = 4348967824619706636L;
/*  37:    */   public static final String HISTORY_NAME = "query";
/*  38:    */   public static final String MAX_ROWS = "max_rows";
/*  39:    */   protected JFrame m_Parent;
/*  40:    */   protected JTextArea m_TextQuery;
/*  41: 80 */   protected JButton m_ButtonExecute = new JButton("Execute");
/*  42: 83 */   protected JButton m_ButtonClear = new JButton("Clear");
/*  43: 86 */   protected JButton m_ButtonHistory = new JButton("History...");
/*  44: 89 */   protected JSpinner m_SpinnerMaxRows = new JSpinner();
/*  45:    */   protected HashSet<QueryExecuteListener> m_QueryExecuteListeners;
/*  46:    */   protected HashSet<HistoryChangedListener> m_HistoryChangedListeners;
/*  47:    */   protected DbUtils m_DbUtils;
/*  48:    */   protected boolean m_Connected;
/*  49:104 */   protected DefaultListModel m_History = new DefaultListModel();
/*  50:    */   
/*  51:    */   public QueryPanel(JFrame parent)
/*  52:    */   {
/*  53:114 */     this.m_Parent = parent;
/*  54:115 */     this.m_QueryExecuteListeners = new HashSet();
/*  55:116 */     this.m_HistoryChangedListeners = new HashSet();
/*  56:117 */     this.m_DbUtils = null;
/*  57:118 */     this.m_Connected = false;
/*  58:    */     
/*  59:120 */     createPanel();
/*  60:    */   }
/*  61:    */   
/*  62:    */   protected void createPanel()
/*  63:    */   {
/*  64:132 */     setLayout(new BorderLayout());
/*  65:    */     
/*  66:    */ 
/*  67:135 */     this.m_TextQuery = new JTextArea();
/*  68:136 */     this.m_TextQuery.addCaretListener(this);
/*  69:137 */     this.m_TextQuery.setFont(new Font("Monospaced", 0, this.m_TextQuery.getFont().getSize()));
/*  70:    */     
/*  71:139 */     add(new JScrollPane(this.m_TextQuery), "Center");
/*  72:    */     
/*  73:    */ 
/*  74:142 */     JPanel panel = new JPanel(new BorderLayout());
/*  75:143 */     add(panel, "East");
/*  76:144 */     this.m_ButtonExecute.setMnemonic('E');
/*  77:145 */     this.m_ButtonExecute.addActionListener(new ActionListener()
/*  78:    */     {
/*  79:    */       public void actionPerformed(ActionEvent e)
/*  80:    */       {
/*  81:148 */         QueryPanel.this.execute();
/*  82:    */       }
/*  83:150 */     });
/*  84:151 */     panel.add(this.m_ButtonExecute, "North");
/*  85:152 */     JPanel panel2 = new JPanel(new BorderLayout());
/*  86:153 */     panel.add(panel2, "Center");
/*  87:154 */     this.m_ButtonClear.setMnemonic('r');
/*  88:155 */     this.m_ButtonClear.addActionListener(new ActionListener()
/*  89:    */     {
/*  90:    */       public void actionPerformed(ActionEvent e)
/*  91:    */       {
/*  92:158 */         QueryPanel.this.clear();
/*  93:    */       }
/*  94:160 */     });
/*  95:161 */     panel2.add(this.m_ButtonClear, "North");
/*  96:162 */     JPanel panel3 = new JPanel(new BorderLayout());
/*  97:163 */     panel2.add(panel3, "Center");
/*  98:164 */     this.m_ButtonHistory.addActionListener(new ActionListener()
/*  99:    */     {
/* 100:    */       public void actionPerformed(ActionEvent e)
/* 101:    */       {
/* 102:167 */         QueryPanel.this.showHistory();
/* 103:    */       }
/* 104:169 */     });
/* 105:170 */     panel3.add(this.m_ButtonHistory, "North");
/* 106:    */     
/* 107:    */ 
/* 108:173 */     panel3 = new JPanel(new FlowLayout());
/* 109:174 */     panel3.add(new JLabel("max. rows"));
/* 110:175 */     panel3.add(this.m_SpinnerMaxRows);
/* 111:176 */     panel2.add(panel3, "South");
/* 112:177 */     SpinnerNumberModel model = (SpinnerNumberModel)this.m_SpinnerMaxRows.getModel();
/* 113:178 */     model.setMaximum(new Integer(2147483647));
/* 114:179 */     model.setMinimum(new Integer(0));
/* 115:180 */     model.setValue(new Integer(100));
/* 116:181 */     model.setStepSize(new Integer(100));
/* 117:182 */     this.m_SpinnerMaxRows.setMinimumSize(new Dimension(50, this.m_SpinnerMaxRows.getHeight()));
/* 118:    */     
/* 119:184 */     this.m_SpinnerMaxRows.setToolTipText("with 0 all rows are retrieved");
/* 120:    */     
/* 121:    */ 
/* 122:187 */     setButtons();
/* 123:    */   }
/* 124:    */   
/* 125:    */   public void setFocus()
/* 126:    */   {
/* 127:194 */     this.m_TextQuery.requestFocus();
/* 128:    */   }
/* 129:    */   
/* 130:    */   protected void setButtons()
/* 131:    */   {
/* 132:203 */     boolean isEmpty = this.m_TextQuery.getText().trim().equals("");
/* 133:    */     
/* 134:205 */     this.m_ButtonExecute.setEnabled((this.m_Connected) && (!isEmpty));
/* 135:206 */     this.m_ButtonClear.setEnabled(!isEmpty);
/* 136:207 */     this.m_ButtonHistory.setEnabled(this.m_History.size() > 0);
/* 137:    */   }
/* 138:    */   
/* 139:    */   public void connectionChange(ConnectionEvent evt)
/* 140:    */   {
/* 141:218 */     this.m_Connected = evt.isConnected();
/* 142:219 */     this.m_DbUtils = evt.getDbUtils();
/* 143:220 */     setButtons();
/* 144:    */   }
/* 145:    */   
/* 146:    */   public void execute()
/* 147:    */   {
/* 148:231 */     if (!this.m_ButtonExecute.isEnabled()) {
/* 149:232 */       return;
/* 150:    */     }
/* 151:236 */     if (this.m_TextQuery.getText().trim().equals("")) {
/* 152:237 */       return;
/* 153:    */     }
/* 154:    */     try
/* 155:    */     {
/* 156:242 */       if (this.m_DbUtils.getResultSet() != null) {
/* 157:243 */         this.m_DbUtils.close();
/* 158:    */       }
/* 159:    */     }
/* 160:    */     catch (Exception e) {}
/* 161:249 */     Exception ex = null;
/* 162:250 */     ResultSet rs = null;
/* 163:    */     try
/* 164:    */     {
/* 165:253 */       if (this.m_DbUtils.execute(getQuery()))
/* 166:    */       {
/* 167:254 */         rs = this.m_DbUtils.getResultSet();
/* 168:    */         
/* 169:256 */         addHistory(getQuery());
/* 170:    */       }
/* 171:    */     }
/* 172:    */     catch (Exception e)
/* 173:    */     {
/* 174:259 */       ex = new Exception(e.getMessage());
/* 175:    */     }
/* 176:262 */     notifyQueryExecuteListeners(rs, ex);
/* 177:    */     
/* 178:264 */     setButtons();
/* 179:    */   }
/* 180:    */   
/* 181:    */   public void clear()
/* 182:    */   {
/* 183:271 */     this.m_TextQuery.setText("");
/* 184:272 */     this.m_SpinnerMaxRows.setValue(new Integer(100));
/* 185:    */   }
/* 186:    */   
/* 187:    */   protected void addHistory(String s)
/* 188:    */   {
/* 189:281 */     if (s.equals("")) {
/* 190:282 */       return;
/* 191:    */     }
/* 192:286 */     if (this.m_History.contains(s)) {
/* 193:287 */       this.m_History.removeElement(s);
/* 194:    */     }
/* 195:290 */     this.m_History.add(0, s);
/* 196:    */     
/* 197:    */ 
/* 198:293 */     notifyHistoryChangedListeners();
/* 199:    */   }
/* 200:    */   
/* 201:    */   public void setHistory(DefaultListModel history)
/* 202:    */   {
/* 203:304 */     this.m_History.clear();
/* 204:305 */     for (int i = 0; i < history.size(); i++) {
/* 205:306 */       this.m_History.addElement(history.get(i));
/* 206:    */     }
/* 207:309 */     setButtons();
/* 208:    */   }
/* 209:    */   
/* 210:    */   public DefaultListModel getHistory()
/* 211:    */   {
/* 212:318 */     return this.m_History;
/* 213:    */   }
/* 214:    */   
/* 215:    */   public void showHistory()
/* 216:    */   {
/* 217:328 */     JList list = new JList(this.m_History);
/* 218:329 */     ListSelectorDialog dialog = new ListSelectorDialog(this.m_Parent, list);
/* 219:331 */     if ((dialog.showDialog() == 0) && 
/* 220:332 */       (list.getSelectedValue() != null)) {
/* 221:333 */       setQuery(list.getSelectedValue().toString());
/* 222:    */     }
/* 223:337 */     setButtons();
/* 224:    */   }
/* 225:    */   
/* 226:    */   public void setQuery(String query)
/* 227:    */   {
/* 228:346 */     this.m_TextQuery.setText(query);
/* 229:    */   }
/* 230:    */   
/* 231:    */   public String getQuery()
/* 232:    */   {
/* 233:355 */     return this.m_TextQuery.getText();
/* 234:    */   }
/* 235:    */   
/* 236:    */   public void setMaxRows(int rows)
/* 237:    */   {
/* 238:364 */     if (rows >= 0) {
/* 239:365 */       this.m_SpinnerMaxRows.setValue(new Integer(rows));
/* 240:    */     }
/* 241:    */   }
/* 242:    */   
/* 243:    */   public int getMaxRows()
/* 244:    */   {
/* 245:376 */     return ((Integer)this.m_SpinnerMaxRows.getValue()).intValue();
/* 246:    */   }
/* 247:    */   
/* 248:    */   public void addQueryExecuteListener(QueryExecuteListener l)
/* 249:    */   {
/* 250:385 */     this.m_QueryExecuteListeners.add(l);
/* 251:    */   }
/* 252:    */   
/* 253:    */   public void removeQueryExecuteListener(QueryExecuteListener l)
/* 254:    */   {
/* 255:394 */     this.m_QueryExecuteListeners.remove(l);
/* 256:    */   }
/* 257:    */   
/* 258:    */   protected void notifyQueryExecuteListeners(ResultSet rs, Exception ex)
/* 259:    */   {
/* 260:407 */     Iterator<QueryExecuteListener> iter = this.m_QueryExecuteListeners.iterator();
/* 261:408 */     while (iter.hasNext())
/* 262:    */     {
/* 263:409 */       QueryExecuteListener l = (QueryExecuteListener)iter.next();
/* 264:410 */       l.queryExecuted(new QueryExecuteEvent(this, this.m_DbUtils, getQuery(), getMaxRows(), rs, ex));
/* 265:    */     }
/* 266:    */   }
/* 267:    */   
/* 268:    */   public void addHistoryChangedListener(HistoryChangedListener l)
/* 269:    */   {
/* 270:421 */     this.m_HistoryChangedListeners.add(l);
/* 271:    */   }
/* 272:    */   
/* 273:    */   public void removeHistoryChangedListener(HistoryChangedListener l)
/* 274:    */   {
/* 275:430 */     this.m_HistoryChangedListeners.remove(l);
/* 276:    */   }
/* 277:    */   
/* 278:    */   protected void notifyHistoryChangedListeners()
/* 279:    */   {
/* 280:440 */     Iterator<HistoryChangedListener> iter = this.m_HistoryChangedListeners.iterator();
/* 281:441 */     while (iter.hasNext())
/* 282:    */     {
/* 283:442 */       HistoryChangedListener l = (HistoryChangedListener)iter.next();
/* 284:443 */       l.historyChanged(new HistoryChangedEvent(this, "query", getHistory()));
/* 285:    */     }
/* 286:    */   }
/* 287:    */   
/* 288:    */   public void caretUpdate(CaretEvent event)
/* 289:    */   {
/* 290:454 */     setButtons();
/* 291:    */   }
/* 292:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.QueryPanel
 * JD-Core Version:    0.7.0.1
 */