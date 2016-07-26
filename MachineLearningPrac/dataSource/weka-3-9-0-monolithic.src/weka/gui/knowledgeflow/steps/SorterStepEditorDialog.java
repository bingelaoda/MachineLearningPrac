/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.GridLayout;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.awt.event.KeyAdapter;
/*   9:    */ import java.awt.event.KeyEvent;
/*  10:    */ import java.util.Iterator;
/*  11:    */ import java.util.Map;
/*  12:    */ import java.util.Set;
/*  13:    */ import javax.swing.BorderFactory;
/*  14:    */ import javax.swing.ComboBoxEditor;
/*  15:    */ import javax.swing.DefaultListModel;
/*  16:    */ import javax.swing.JButton;
/*  17:    */ import javax.swing.JComboBox;
/*  18:    */ import javax.swing.JList;
/*  19:    */ import javax.swing.JPanel;
/*  20:    */ import javax.swing.JScrollPane;
/*  21:    */ import javax.swing.JTextField;
/*  22:    */ import javax.swing.event.ListSelectionEvent;
/*  23:    */ import javax.swing.event.ListSelectionListener;
/*  24:    */ import weka.core.Attribute;
/*  25:    */ import weka.core.Instances;
/*  26:    */ import weka.core.WekaException;
/*  27:    */ import weka.gui.JListHelper;
/*  28:    */ import weka.gui.PropertySheetPanel;
/*  29:    */ import weka.gui.knowledgeflow.GOEStepEditorDialog;
/*  30:    */ import weka.knowledgeflow.StepManager;
/*  31:    */ import weka.knowledgeflow.steps.Sorter;
/*  32:    */ import weka.knowledgeflow.steps.Sorter.SortRule;
/*  33:    */ import weka.knowledgeflow.steps.Step;
/*  34:    */ 
/*  35:    */ public class SorterStepEditorDialog
/*  36:    */   extends GOEStepEditorDialog
/*  37:    */ {
/*  38:    */   private static final long serialVersionUID = -1258170590422372948L;
/*  39: 52 */   protected JComboBox<String> m_attCombo = new JComboBox();
/*  40: 55 */   protected JComboBox<String> m_descending = new JComboBox();
/*  41: 58 */   protected JList<Sorter.SortRule> m_list = new JList();
/*  42:    */   protected DefaultListModel<Sorter.SortRule> m_listModel;
/*  43: 64 */   protected JButton m_newBut = new JButton("New");
/*  44: 67 */   protected JButton m_deleteBut = new JButton("Delete");
/*  45: 70 */   protected JButton m_upBut = new JButton("Move up");
/*  46: 73 */   protected JButton m_downBut = new JButton("Move down");
/*  47:    */   
/*  48:    */   protected void setStepToEdit(Step step)
/*  49:    */   {
/*  50: 82 */     copyOriginal(step);
/*  51: 83 */     createAboutPanel(step);
/*  52: 84 */     this.m_editor = new PropertySheetPanel(false);
/*  53: 85 */     this.m_editor.setUseEnvironmentPropertyEditors(true);
/*  54: 86 */     this.m_editor.setEnvironment(this.m_env);
/*  55: 87 */     this.m_editor.setTarget(this.m_stepToEdit);
/*  56:    */     
/*  57: 89 */     this.m_primaryEditorHolder.setLayout(new BorderLayout());
/*  58: 90 */     this.m_primaryEditorHolder.add(this.m_editor, "Center");
/*  59:    */     
/*  60: 92 */     this.m_editorHolder.setLayout(new BorderLayout());
/*  61: 93 */     this.m_editorHolder.add(this.m_primaryEditorHolder, "North");
/*  62: 94 */     this.m_editorHolder.add(createSorterPanel(), "Center");
/*  63: 95 */     add(this.m_editorHolder, "Center");
/*  64:    */     
/*  65: 97 */     String sString = ((Sorter)getStepToEdit()).getSortDetails();
/*  66: 98 */     this.m_listModel = new DefaultListModel();
/*  67: 99 */     this.m_list.setModel(this.m_listModel);
/*  68:100 */     if ((sString != null) && (sString.length() > 0))
/*  69:    */     {
/*  70:101 */       String[] parts = sString.split("@@sort-rule@@");
/*  71:103 */       if (parts.length > 0)
/*  72:    */       {
/*  73:104 */         this.m_upBut.setEnabled(true);
/*  74:105 */         this.m_downBut.setEnabled(true);
/*  75:106 */         for (String sPart : parts)
/*  76:    */         {
/*  77:107 */           Sorter.SortRule s = new Sorter.SortRule(sPart);
/*  78:108 */           this.m_listModel.addElement(s);
/*  79:    */         }
/*  80:    */       }
/*  81:112 */       this.m_list.repaint();
/*  82:    */     }
/*  83:116 */     if (((Sorter)getStepToEdit()).getStepManager().numIncomingConnections() > 0)
/*  84:    */     {
/*  85:119 */       String incomingConnName = (String)((Sorter)getStepToEdit()).getStepManager().getIncomingConnections().keySet().iterator().next();
/*  86:    */       try
/*  87:    */       {
/*  88:123 */         Instances connectedFormat = ((Sorter)getStepToEdit()).getStepManager().getIncomingStructureForConnectionType(incomingConnName);
/*  89:126 */         if (connectedFormat != null) {
/*  90:127 */           for (int i = 0; i < connectedFormat.numAttributes(); i++) {
/*  91:128 */             this.m_attCombo.addItem(connectedFormat.attribute(i).name());
/*  92:    */           }
/*  93:    */         }
/*  94:    */       }
/*  95:    */       catch (WekaException ex)
/*  96:    */       {
/*  97:132 */         showErrorDialog(ex);
/*  98:    */       }
/*  99:    */     }
/* 100:    */   }
/* 101:    */   
/* 102:    */   protected JPanel createSorterPanel()
/* 103:    */   {
/* 104:143 */     JPanel sorterPanel = new JPanel(new BorderLayout());
/* 105:    */     
/* 106:145 */     JPanel fieldHolder = new JPanel();
/* 107:146 */     fieldHolder.setLayout(new GridLayout(0, 2));
/* 108:    */     
/* 109:148 */     JPanel attListP = new JPanel();
/* 110:149 */     attListP.setLayout(new BorderLayout());
/* 111:150 */     attListP.setBorder(BorderFactory.createTitledBorder("Sort on attribute"));
/* 112:151 */     attListP.add(this.m_attCombo, "Center");
/* 113:152 */     this.m_attCombo.setEditable(true);
/* 114:153 */     this.m_attCombo.setToolTipText("<html>Accepts an attribute name, index or <br> the special string \"/first\" and \"/last\"</html>");
/* 115:    */     
/* 116:    */ 
/* 117:156 */     this.m_descending.addItem("No");
/* 118:157 */     this.m_descending.addItem("Yes");
/* 119:158 */     JPanel descendingP = new JPanel();
/* 120:159 */     descendingP.setLayout(new BorderLayout());
/* 121:160 */     descendingP.setBorder(BorderFactory.createTitledBorder("Sort descending"));
/* 122:161 */     descendingP.add(this.m_descending, "Center");
/* 123:    */     
/* 124:163 */     fieldHolder.add(attListP);
/* 125:164 */     fieldHolder.add(descendingP);
/* 126:    */     
/* 127:166 */     sorterPanel.add(fieldHolder, "North");
/* 128:    */     
/* 129:168 */     this.m_list.setVisibleRowCount(5);
/* 130:169 */     this.m_deleteBut.setEnabled(false);
/* 131:170 */     JPanel listPanel = new JPanel();
/* 132:171 */     listPanel.setLayout(new BorderLayout());
/* 133:    */     
/* 134:173 */     JPanel butHolder = new JPanel();
/* 135:174 */     butHolder.setLayout(new GridLayout(1, 0));
/* 136:175 */     butHolder.add(this.m_newBut);
/* 137:176 */     butHolder.add(this.m_deleteBut);
/* 138:177 */     butHolder.add(this.m_upBut);
/* 139:178 */     butHolder.add(this.m_downBut);
/* 140:179 */     this.m_upBut.setEnabled(false);
/* 141:180 */     this.m_downBut.setEnabled(false);
/* 142:    */     
/* 143:182 */     listPanel.add(butHolder, "North");
/* 144:183 */     JScrollPane js = new JScrollPane(this.m_list);
/* 145:184 */     js.setBorder(BorderFactory.createTitledBorder("Sort-by list (rows applied in order)"));
/* 146:    */     
/* 147:186 */     listPanel.add(js, "Center");
/* 148:    */     
/* 149:188 */     sorterPanel.add(listPanel, "Center");
/* 150:    */     
/* 151:190 */     this.m_list.addListSelectionListener(new ListSelectionListener()
/* 152:    */     {
/* 153:    */       public void valueChanged(ListSelectionEvent e)
/* 154:    */       {
/* 155:193 */         if (!e.getValueIsAdjusting())
/* 156:    */         {
/* 157:194 */           if (!SorterStepEditorDialog.this.m_deleteBut.isEnabled()) {
/* 158:195 */             SorterStepEditorDialog.this.m_deleteBut.setEnabled(true);
/* 159:    */           }
/* 160:198 */           Object entry = SorterStepEditorDialog.this.m_list.getSelectedValue();
/* 161:199 */           if (entry != null)
/* 162:    */           {
/* 163:200 */             Sorter.SortRule m = (Sorter.SortRule)entry;
/* 164:201 */             SorterStepEditorDialog.this.m_attCombo.setSelectedItem(m.getAttribute());
/* 165:202 */             if (m.getDescending()) {
/* 166:203 */               SorterStepEditorDialog.this.m_descending.setSelectedIndex(1);
/* 167:    */             } else {
/* 168:205 */               SorterStepEditorDialog.this.m_descending.setSelectedIndex(0);
/* 169:    */             }
/* 170:    */           }
/* 171:    */         }
/* 172:    */       }
/* 173:211 */     });
/* 174:212 */     this.m_newBut.addActionListener(new ActionListener()
/* 175:    */     {
/* 176:    */       public void actionPerformed(ActionEvent e)
/* 177:    */       {
/* 178:215 */         Sorter.SortRule m = new Sorter.SortRule();
/* 179:    */         
/* 180:217 */         String att = SorterStepEditorDialog.this.m_attCombo.getSelectedItem() != null ? SorterStepEditorDialog.this.m_attCombo.getSelectedItem().toString() : "";
/* 181:    */         
/* 182:    */ 
/* 183:220 */         m.setAttribute(att);
/* 184:221 */         m.setDescending(SorterStepEditorDialog.this.m_descending.getSelectedIndex() == 1);
/* 185:    */         
/* 186:223 */         SorterStepEditorDialog.this.m_listModel.addElement(m);
/* 187:225 */         if (SorterStepEditorDialog.this.m_listModel.size() > 1)
/* 188:    */         {
/* 189:226 */           SorterStepEditorDialog.this.m_upBut.setEnabled(true);
/* 190:227 */           SorterStepEditorDialog.this.m_downBut.setEnabled(true);
/* 191:    */         }
/* 192:230 */         SorterStepEditorDialog.this.m_list.setSelectedIndex(SorterStepEditorDialog.this.m_listModel.size() - 1);
/* 193:    */       }
/* 194:233 */     });
/* 195:234 */     this.m_deleteBut.addActionListener(new ActionListener()
/* 196:    */     {
/* 197:    */       public void actionPerformed(ActionEvent e)
/* 198:    */       {
/* 199:237 */         int selected = SorterStepEditorDialog.this.m_list.getSelectedIndex();
/* 200:238 */         if (selected >= 0)
/* 201:    */         {
/* 202:239 */           SorterStepEditorDialog.this.m_listModel.removeElementAt(selected);
/* 203:241 */           if (SorterStepEditorDialog.this.m_listModel.size() <= 1)
/* 204:    */           {
/* 205:242 */             SorterStepEditorDialog.this.m_upBut.setEnabled(false);
/* 206:243 */             SorterStepEditorDialog.this.m_downBut.setEnabled(false);
/* 207:    */           }
/* 208:    */         }
/* 209:    */       }
/* 210:248 */     });
/* 211:249 */     this.m_upBut.addActionListener(new ActionListener()
/* 212:    */     {
/* 213:    */       public void actionPerformed(ActionEvent e)
/* 214:    */       {
/* 215:252 */         JListHelper.moveUp(SorterStepEditorDialog.this.m_list);
/* 216:    */       }
/* 217:255 */     });
/* 218:256 */     this.m_downBut.addActionListener(new ActionListener()
/* 219:    */     {
/* 220:    */       public void actionPerformed(ActionEvent e)
/* 221:    */       {
/* 222:259 */         JListHelper.moveDown(SorterStepEditorDialog.this.m_list);
/* 223:    */       }
/* 224:262 */     });
/* 225:263 */     this.m_attCombo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter()
/* 226:    */     {
/* 227:    */       public void keyReleased(KeyEvent e)
/* 228:    */       {
/* 229:267 */         Object m = SorterStepEditorDialog.this.m_list.getSelectedValue();
/* 230:268 */         String text = "";
/* 231:269 */         if (SorterStepEditorDialog.this.m_attCombo.getSelectedItem() != null) {
/* 232:270 */           text = SorterStepEditorDialog.this.m_attCombo.getSelectedItem().toString();
/* 233:    */         }
/* 234:272 */         Component theEditor = SorterStepEditorDialog.this.m_attCombo.getEditor().getEditorComponent();
/* 235:274 */         if ((theEditor instanceof JTextField)) {
/* 236:275 */           text = ((JTextField)theEditor).getText();
/* 237:    */         }
/* 238:277 */         if (m != null)
/* 239:    */         {
/* 240:278 */           ((Sorter.SortRule)m).setAttribute(text);
/* 241:279 */           SorterStepEditorDialog.this.m_list.repaint();
/* 242:    */         }
/* 243:    */       }
/* 244:283 */     });
/* 245:284 */     this.m_attCombo.addActionListener(new ActionListener()
/* 246:    */     {
/* 247:    */       public void actionPerformed(ActionEvent e)
/* 248:    */       {
/* 249:287 */         Object m = SorterStepEditorDialog.this.m_list.getSelectedValue();
/* 250:288 */         Object selected = SorterStepEditorDialog.this.m_attCombo.getSelectedItem();
/* 251:289 */         if ((m != null) && (selected != null))
/* 252:    */         {
/* 253:290 */           ((Sorter.SortRule)m).setAttribute(selected.toString());
/* 254:291 */           SorterStepEditorDialog.this.m_list.repaint();
/* 255:    */         }
/* 256:    */       }
/* 257:295 */     });
/* 258:296 */     this.m_descending.addActionListener(new ActionListener()
/* 259:    */     {
/* 260:    */       public void actionPerformed(ActionEvent e)
/* 261:    */       {
/* 262:299 */         Object m = SorterStepEditorDialog.this.m_list.getSelectedValue();
/* 263:300 */         if (m != null)
/* 264:    */         {
/* 265:301 */           ((Sorter.SortRule)m).setDescending(SorterStepEditorDialog.this.m_descending.getSelectedIndex() == 1);
/* 266:302 */           SorterStepEditorDialog.this.m_list.repaint();
/* 267:    */         }
/* 268:    */       }
/* 269:306 */     });
/* 270:307 */     return sorterPanel;
/* 271:    */   }
/* 272:    */   
/* 273:    */   public void okPressed()
/* 274:    */   {
/* 275:315 */     StringBuilder buff = new StringBuilder();
/* 276:316 */     for (int i = 0; i < this.m_listModel.size(); i++)
/* 277:    */     {
/* 278:317 */       Sorter.SortRule m = (Sorter.SortRule)this.m_listModel.elementAt(i);
/* 279:    */       
/* 280:319 */       buff.append(m.toStringInternal());
/* 281:320 */       if (i < this.m_listModel.size() - 1) {
/* 282:321 */         buff.append("@@sort-rule@@");
/* 283:    */       }
/* 284:    */     }
/* 285:325 */     ((Sorter)getStepToEdit()).setSortDetails(buff.toString());
/* 286:    */   }
/* 287:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.SorterStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */