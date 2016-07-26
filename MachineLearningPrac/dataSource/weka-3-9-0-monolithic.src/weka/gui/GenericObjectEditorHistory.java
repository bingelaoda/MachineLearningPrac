/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.event.ActionEvent;
/*   4:    */ import java.awt.event.ActionListener;
/*   5:    */ import java.io.Serializable;
/*   6:    */ import java.util.EventObject;
/*   7:    */ import java.util.Vector;
/*   8:    */ import javax.swing.JMenu;
/*   9:    */ import javax.swing.JMenuItem;
/*  10:    */ import javax.swing.JPopupMenu;
/*  11:    */ import weka.core.SerializedObject;
/*  12:    */ import weka.core.Utils;
/*  13:    */ 
/*  14:    */ public class GenericObjectEditorHistory
/*  15:    */   implements Serializable
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = -1255734638729633595L;
/*  18:    */   public static final int MAX_HISTORY_COUNT = 10;
/*  19:    */   public static final int MAX_HISTORY_LENGTH = 200;
/*  20:    */   public static final int MAX_LINE_LENGTH = 80;
/*  21:    */   protected Vector<Object> m_History;
/*  22:    */   
/*  23:    */   public static abstract interface HistorySelectionListener
/*  24:    */   {
/*  25:    */     public abstract void historySelected(GenericObjectEditorHistory.HistorySelectionEvent paramHistorySelectionEvent);
/*  26:    */   }
/*  27:    */   
/*  28:    */   public static class HistorySelectionEvent
/*  29:    */     extends EventObject
/*  30:    */   {
/*  31:    */     private static final long serialVersionUID = 45824542929908105L;
/*  32:    */     protected Object m_HistoryItem;
/*  33:    */     
/*  34:    */     public HistorySelectionEvent(Object source, Object historyItem)
/*  35:    */     {
/*  36: 68 */       super();
/*  37:    */       
/*  38: 70 */       this.m_HistoryItem = historyItem;
/*  39:    */     }
/*  40:    */     
/*  41:    */     public Object getHistoryItem()
/*  42:    */     {
/*  43: 79 */       return this.m_HistoryItem;
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:    */   public GenericObjectEditorHistory()
/*  48:    */   {
/*  49:117 */     initialize();
/*  50:    */   }
/*  51:    */   
/*  52:    */   protected void initialize()
/*  53:    */   {
/*  54:124 */     this.m_History = new Vector();
/*  55:    */   }
/*  56:    */   
/*  57:    */   public synchronized void clear()
/*  58:    */   {
/*  59:131 */     this.m_History.clear();
/*  60:    */   }
/*  61:    */   
/*  62:    */   public synchronized void add(Object obj)
/*  63:    */   {
/*  64:140 */     obj = copy(obj);
/*  65:142 */     if (this.m_History.contains(obj)) {
/*  66:143 */       this.m_History.remove(obj);
/*  67:    */     }
/*  68:145 */     this.m_History.insertElementAt(obj, 0);
/*  69:147 */     while (this.m_History.size() > 10) {
/*  70:148 */       this.m_History.remove(this.m_History.size() - 1);
/*  71:    */     }
/*  72:    */   }
/*  73:    */   
/*  74:    */   public synchronized int size()
/*  75:    */   {
/*  76:158 */     return this.m_History.size();
/*  77:    */   }
/*  78:    */   
/*  79:    */   public synchronized Vector<Object> getHistory()
/*  80:    */   {
/*  81:167 */     return this.m_History;
/*  82:    */   }
/*  83:    */   
/*  84:    */   protected Object copy(Object obj)
/*  85:    */   {
/*  86:    */     Object result;
/*  87:    */     try
/*  88:    */     {
/*  89:180 */       SerializedObject so = new SerializedObject(obj);
/*  90:181 */       result = so.getObject();
/*  91:    */     }
/*  92:    */     catch (Exception e)
/*  93:    */     {
/*  94:183 */       result = null;
/*  95:184 */       e.printStackTrace();
/*  96:    */     }
/*  97:187 */     return result;
/*  98:    */   }
/*  99:    */   
/* 100:    */   protected String generateMenuItemCaption(Object obj)
/* 101:    */   {
/* 102:202 */     StringBuffer result = new StringBuffer();
/* 103:    */     
/* 104:204 */     String cmd = Utils.toCommandLine(obj);
/* 105:205 */     if (cmd.length() > 200) {
/* 106:206 */       cmd = cmd.substring(0, 200) + "...";
/* 107:    */     }
/* 108:209 */     String[] lines = Utils.breakUp(cmd, 80);
/* 109:210 */     result.append("<html>");
/* 110:211 */     for (int i = 0; i < lines.length; i++)
/* 111:    */     {
/* 112:212 */       if (i > 0) {
/* 113:213 */         result.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
/* 114:    */       }
/* 115:215 */       result.append(lines[i].trim());
/* 116:    */     }
/* 117:217 */     result.append("</html>");
/* 118:    */     
/* 119:219 */     return result.toString();
/* 120:    */   }
/* 121:    */   
/* 122:    */   public void customizePopupMenu(JPopupMenu menu, Object current, HistorySelectionListener listener)
/* 123:    */   {
/* 124:235 */     if (this.m_History.size() == 0) {
/* 125:236 */       return;
/* 126:    */     }
/* 127:239 */     JMenu submenu = new JMenu("History");
/* 128:240 */     menu.addSeparator();
/* 129:241 */     menu.add(submenu);
/* 130:    */     
/* 131:    */ 
/* 132:244 */     JMenuItem item = new JMenuItem("Clear history");
/* 133:245 */     item.addActionListener(new ActionListener()
/* 134:    */     {
/* 135:    */       public void actionPerformed(ActionEvent e)
/* 136:    */       {
/* 137:248 */         GenericObjectEditorHistory.this.m_History.clear();
/* 138:    */       }
/* 139:250 */     });
/* 140:251 */     submenu.add(item);
/* 141:    */     
/* 142:    */ 
/* 143:254 */     final HistorySelectionListener fListener = listener;
/* 144:255 */     for (int i = 0; i < this.m_History.size(); i++)
/* 145:    */     {
/* 146:256 */       if (i == 0) {
/* 147:257 */         submenu.addSeparator();
/* 148:    */       }
/* 149:259 */       final Object history = this.m_History.get(i);
/* 150:260 */       item = new JMenuItem(generateMenuItemCaption(history));
/* 151:261 */       item.addActionListener(new ActionListener()
/* 152:    */       {
/* 153:    */         public void actionPerformed(ActionEvent e)
/* 154:    */         {
/* 155:264 */           fListener.historySelected(new GenericObjectEditorHistory.HistorySelectionEvent(fListener, history));
/* 156:    */         }
/* 157:267 */       });
/* 158:268 */       submenu.add(item);
/* 159:    */     }
/* 160:    */   }
/* 161:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.GenericObjectEditorHistory
 * JD-Core Version:    0.7.0.1
 */