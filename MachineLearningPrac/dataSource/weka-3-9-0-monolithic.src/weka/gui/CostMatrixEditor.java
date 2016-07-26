/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.Graphics;
/*   6:    */ import java.awt.GridBagConstraints;
/*   7:    */ import java.awt.GridBagLayout;
/*   8:    */ import java.awt.GridLayout;
/*   9:    */ import java.awt.Insets;
/*  10:    */ import java.awt.Rectangle;
/*  11:    */ import java.awt.event.ActionEvent;
/*  12:    */ import java.awt.event.ActionListener;
/*  13:    */ import java.beans.PropertyChangeListener;
/*  14:    */ import java.beans.PropertyChangeSupport;
/*  15:    */ import java.beans.PropertyEditor;
/*  16:    */ import java.io.BufferedReader;
/*  17:    */ import java.io.BufferedWriter;
/*  18:    */ import java.io.File;
/*  19:    */ import java.io.FileReader;
/*  20:    */ import java.io.FileWriter;
/*  21:    */ import java.io.PrintStream;
/*  22:    */ import java.io.Reader;
/*  23:    */ import java.io.Writer;
/*  24:    */ import javax.swing.JButton;
/*  25:    */ import javax.swing.JFileChooser;
/*  26:    */ import javax.swing.JLabel;
/*  27:    */ import javax.swing.JOptionPane;
/*  28:    */ import javax.swing.JPanel;
/*  29:    */ import javax.swing.JTable;
/*  30:    */ import javax.swing.JTextField;
/*  31:    */ import javax.swing.event.TableModelEvent;
/*  32:    */ import javax.swing.event.TableModelListener;
/*  33:    */ import javax.swing.table.AbstractTableModel;
/*  34:    */ import weka.classifiers.CostMatrix;
/*  35:    */ 
/*  36:    */ public class CostMatrixEditor
/*  37:    */   implements PropertyEditor
/*  38:    */ {
/*  39:    */   private CostMatrix m_matrix;
/*  40:    */   private final PropertyChangeSupport m_propSupport;
/*  41:    */   private final CustomEditor m_customEditor;
/*  42: 79 */   private final JFileChooser m_fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/*  43:    */   
/*  44:    */   private class CostMatrixTableModel
/*  45:    */     extends AbstractTableModel
/*  46:    */   {
/*  47:    */     static final long serialVersionUID = -2762326138357037181L;
/*  48:    */     
/*  49:    */     private CostMatrixTableModel() {}
/*  50:    */     
/*  51:    */     public int getRowCount()
/*  52:    */     {
/*  53:100 */       return CostMatrixEditor.this.m_matrix.size();
/*  54:    */     }
/*  55:    */     
/*  56:    */     public int getColumnCount()
/*  57:    */     {
/*  58:112 */       return CostMatrixEditor.this.m_matrix.size();
/*  59:    */     }
/*  60:    */     
/*  61:    */     public Object getValueAt(int row, int column)
/*  62:    */     {
/*  63:    */       try
/*  64:    */       {
/*  65:127 */         return CostMatrixEditor.this.m_matrix.getCell(row, column);
/*  66:    */       }
/*  67:    */       catch (Exception ex)
/*  68:    */       {
/*  69:129 */         ex.printStackTrace();
/*  70:    */       }
/*  71:131 */       return new Double(0.0D);
/*  72:    */     }
/*  73:    */     
/*  74:    */     public void setValueAt(Object aValue, int rowIndex, int columnIndex)
/*  75:    */     {
/*  76:    */       Double val;
/*  77:    */       try
/*  78:    */       {
/*  79:149 */         val = new Double((String)aValue);
/*  80:    */       }
/*  81:    */       catch (Exception ex)
/*  82:    */       {
/*  83:151 */         val = null;
/*  84:    */       }
/*  85:153 */       if (val == null) {
/*  86:154 */         CostMatrixEditor.this.m_matrix.setCell(rowIndex, columnIndex, aValue);
/*  87:    */       } else {
/*  88:156 */         CostMatrixEditor.this.m_matrix.setCell(rowIndex, columnIndex, val);
/*  89:    */       }
/*  90:158 */       fireTableCellUpdated(rowIndex, columnIndex);
/*  91:    */     }
/*  92:    */     
/*  93:    */     public boolean isCellEditable(int rowIndex, int columnIndex)
/*  94:    */     {
/*  95:172 */       return true;
/*  96:    */     }
/*  97:    */     
/*  98:    */     public Class<?> getColumnClass(int columnIndex)
/*  99:    */     {
/* 100:186 */       return Object.class;
/* 101:    */     }
/* 102:    */   }
/* 103:    */   
/* 104:    */   private class CustomEditor
/* 105:    */     extends JPanel
/* 106:    */     implements ActionListener, TableModelListener
/* 107:    */   {
/* 108:    */     static final long serialVersionUID = -2931593489871197274L;
/* 109:    */     private final CostMatrixEditor.CostMatrixTableModel m_tableModel;
/* 110:    */     private final JButton m_defaultButton;
/* 111:    */     private final JButton m_openButton;
/* 112:    */     private final JButton m_saveButton;
/* 113:    */     private final JTextField m_classesField;
/* 114:    */     private final JButton m_resizeButton;
/* 115:    */     
/* 116:    */     public CustomEditor()
/* 117:    */     {
/* 118:225 */       CostMatrixEditor.this.m_fileChooser.setFileFilter(new ExtensionFileFilter(CostMatrix.FILE_EXTENSION, "Cost files"));
/* 119:    */       
/* 120:227 */       CostMatrixEditor.this.m_fileChooser.setFileSelectionMode(0);
/* 121:    */       
/* 122:    */ 
/* 123:230 */       this.m_defaultButton = new JButton("Defaults");
/* 124:231 */       this.m_openButton = new JButton("Open...");
/* 125:232 */       this.m_saveButton = new JButton("Save...");
/* 126:233 */       this.m_resizeButton = new JButton("Resize");
/* 127:234 */       this.m_classesField = new JTextField("" + CostMatrixEditor.this.m_matrix.size());
/* 128:    */       
/* 129:236 */       this.m_defaultButton.addActionListener(this);
/* 130:237 */       this.m_openButton.addActionListener(this);
/* 131:238 */       this.m_saveButton.addActionListener(this);
/* 132:239 */       this.m_resizeButton.addActionListener(this);
/* 133:240 */       this.m_classesField.addActionListener(this);
/* 134:    */       
/* 135:    */ 
/* 136:243 */       JPanel classesPanel = new JPanel();
/* 137:244 */       classesPanel.setLayout(new GridLayout(1, 2, 0, 0));
/* 138:245 */       classesPanel.add(new JLabel("Classes:", 4));
/* 139:246 */       classesPanel.add(this.m_classesField);
/* 140:    */       
/* 141:248 */       JPanel rightPanel = new JPanel();
/* 142:    */       
/* 143:250 */       GridBagLayout gridBag = new GridBagLayout();
/* 144:251 */       GridBagConstraints gbc = new GridBagConstraints();
/* 145:252 */       rightPanel.setLayout(gridBag);
/* 146:253 */       gbc.gridx = 0;
/* 147:254 */       gbc.gridy = -1;
/* 148:255 */       gbc.insets = new Insets(2, 10, 2, 10);
/* 149:256 */       gbc.fill = 2;
/* 150:257 */       gridBag.setConstraints(this.m_defaultButton, gbc);
/* 151:258 */       rightPanel.add(this.m_defaultButton);
/* 152:    */       
/* 153:260 */       gridBag.setConstraints(this.m_openButton, gbc);
/* 154:261 */       rightPanel.add(this.m_openButton);
/* 155:    */       
/* 156:263 */       gridBag.setConstraints(this.m_saveButton, gbc);
/* 157:264 */       rightPanel.add(this.m_saveButton);
/* 158:    */       
/* 159:266 */       gridBag.setConstraints(classesPanel, gbc);
/* 160:267 */       rightPanel.add(classesPanel);
/* 161:    */       
/* 162:269 */       gridBag.setConstraints(this.m_resizeButton, gbc);
/* 163:270 */       rightPanel.add(this.m_resizeButton);
/* 164:    */       
/* 165:272 */       JPanel fill = new JPanel();
/* 166:273 */       gbc.weightx = 1.0D;
/* 167:274 */       gbc.weighty = 1.0D;
/* 168:275 */       gbc.fill = 1;
/* 169:    */       
/* 170:277 */       gridBag.setConstraints(fill, gbc);
/* 171:278 */       rightPanel.add(fill);
/* 172:    */       
/* 173:280 */       this.m_tableModel = new CostMatrixEditor.CostMatrixTableModel(CostMatrixEditor.this, null);
/* 174:281 */       this.m_tableModel.addTableModelListener(this);
/* 175:282 */       JTable matrixTable = new JTable(this.m_tableModel);
/* 176:    */       
/* 177:284 */       setLayout(new BorderLayout());
/* 178:285 */       add(matrixTable, "Center");
/* 179:286 */       add(rightPanel, "East");
/* 180:    */     }
/* 181:    */     
/* 182:    */     public void actionPerformed(ActionEvent e)
/* 183:    */     {
/* 184:297 */       if (e.getSource() == this.m_defaultButton)
/* 185:    */       {
/* 186:298 */         CostMatrixEditor.this.m_matrix.initialize();
/* 187:299 */         matrixChanged();
/* 188:    */       }
/* 189:300 */       else if (e.getSource() == this.m_openButton)
/* 190:    */       {
/* 191:301 */         openMatrix();
/* 192:    */       }
/* 193:302 */       else if (e.getSource() == this.m_saveButton)
/* 194:    */       {
/* 195:303 */         saveMatrix();
/* 196:    */       }
/* 197:304 */       else if ((e.getSource() == this.m_classesField) || (e.getSource() == this.m_resizeButton))
/* 198:    */       {
/* 199:    */         try
/* 200:    */         {
/* 201:307 */           int newNumClasses = Integer.parseInt(this.m_classesField.getText());
/* 202:308 */           if ((newNumClasses > 0) && (newNumClasses != CostMatrixEditor.this.m_matrix.size())) {
/* 203:309 */             CostMatrixEditor.this.setValue(new CostMatrix(newNumClasses));
/* 204:    */           }
/* 205:    */         }
/* 206:    */         catch (Exception ex) {}
/* 207:    */       }
/* 208:    */     }
/* 209:    */     
/* 210:    */     public void tableChanged(TableModelEvent e)
/* 211:    */     {
/* 212:324 */       CostMatrixEditor.this.m_propSupport.firePropertyChange(null, null, null);
/* 213:    */     }
/* 214:    */     
/* 215:    */     public void matrixChanged()
/* 216:    */     {
/* 217:333 */       this.m_tableModel.fireTableStructureChanged();
/* 218:334 */       this.m_classesField.setText("" + CostMatrixEditor.this.m_matrix.size());
/* 219:    */     }
/* 220:    */     
/* 221:    */     private void openMatrix()
/* 222:    */     {
/* 223:343 */       int returnVal = CostMatrixEditor.this.m_fileChooser.showOpenDialog(this);
/* 224:344 */       if (returnVal == 0)
/* 225:    */       {
/* 226:345 */         File selectedFile = CostMatrixEditor.this.m_fileChooser.getSelectedFile();
/* 227:346 */         Reader reader = null;
/* 228:    */         try
/* 229:    */         {
/* 230:348 */           reader = new BufferedReader(new FileReader(selectedFile));
/* 231:349 */           CostMatrixEditor.this.m_matrix = new CostMatrix(reader);
/* 232:350 */           reader.close();
/* 233:351 */           matrixChanged();
/* 234:    */         }
/* 235:    */         catch (Exception ex)
/* 236:    */         {
/* 237:353 */           JOptionPane.showMessageDialog(this, "Error reading file '" + selectedFile.getName() + "':\n" + ex.getMessage(), "Load failed", 0);
/* 238:    */           
/* 239:    */ 
/* 240:356 */           System.out.println(ex.getMessage());
/* 241:    */         }
/* 242:    */       }
/* 243:    */     }
/* 244:    */     
/* 245:    */     private void saveMatrix()
/* 246:    */     {
/* 247:367 */       int returnVal = CostMatrixEditor.this.m_fileChooser.showSaveDialog(this);
/* 248:368 */       if (returnVal == 0)
/* 249:    */       {
/* 250:369 */         File selectedFile = CostMatrixEditor.this.m_fileChooser.getSelectedFile();
/* 251:372 */         if (!selectedFile.getName().toLowerCase().endsWith(CostMatrix.FILE_EXTENSION)) {
/* 252:374 */           selectedFile = new File(selectedFile.getParent(), selectedFile.getName() + CostMatrix.FILE_EXTENSION);
/* 253:    */         }
/* 254:378 */         Writer writer = null;
/* 255:    */         try
/* 256:    */         {
/* 257:380 */           writer = new BufferedWriter(new FileWriter(selectedFile));
/* 258:381 */           CostMatrixEditor.this.m_matrix.write(writer);
/* 259:382 */           writer.close();
/* 260:    */         }
/* 261:    */         catch (Exception ex)
/* 262:    */         {
/* 263:384 */           JOptionPane.showMessageDialog(this, "Error writing file '" + selectedFile.getName() + "':\n" + ex.getMessage(), "Save failed", 0);
/* 264:    */           
/* 265:    */ 
/* 266:387 */           System.out.println(ex.getMessage());
/* 267:    */         }
/* 268:    */       }
/* 269:    */     }
/* 270:    */   }
/* 271:    */   
/* 272:    */   public CostMatrixEditor()
/* 273:    */   {
/* 274:399 */     this.m_matrix = new CostMatrix(2);
/* 275:400 */     this.m_propSupport = new PropertyChangeSupport(this);
/* 276:401 */     this.m_customEditor = new CustomEditor();
/* 277:    */   }
/* 278:    */   
/* 279:    */   public void setValue(Object value)
/* 280:    */   {
/* 281:412 */     this.m_matrix = ((CostMatrix)value);
/* 282:413 */     this.m_customEditor.matrixChanged();
/* 283:    */   }
/* 284:    */   
/* 285:    */   public Object getValue()
/* 286:    */   {
/* 287:424 */     return this.m_matrix;
/* 288:    */   }
/* 289:    */   
/* 290:    */   public boolean isPaintable()
/* 291:    */   {
/* 292:436 */     return true;
/* 293:    */   }
/* 294:    */   
/* 295:    */   public void paintValue(Graphics gfx, Rectangle box)
/* 296:    */   {
/* 297:449 */     gfx.drawString(this.m_matrix.size() + " x " + this.m_matrix.size() + " cost matrix", box.x, box.y + box.height);
/* 298:    */   }
/* 299:    */   
/* 300:    */   public String getJavaInitializationString()
/* 301:    */   {
/* 302:463 */     return "new CostMatrix(" + this.m_matrix.size() + ")";
/* 303:    */   }
/* 304:    */   
/* 305:    */   public String getAsText()
/* 306:    */   {
/* 307:474 */     return null;
/* 308:    */   }
/* 309:    */   
/* 310:    */   public void setAsText(String text)
/* 311:    */   {
/* 312:485 */     throw new IllegalArgumentException("CostMatrixEditor: CostMatrix properties cannot be expressed as text");
/* 313:    */   }
/* 314:    */   
/* 315:    */   public String[] getTags()
/* 316:    */   {
/* 317:497 */     return null;
/* 318:    */   }
/* 319:    */   
/* 320:    */   public Component getCustomEditor()
/* 321:    */   {
/* 322:508 */     return this.m_customEditor;
/* 323:    */   }
/* 324:    */   
/* 325:    */   public boolean supportsCustomEditor()
/* 326:    */   {
/* 327:519 */     return true;
/* 328:    */   }
/* 329:    */   
/* 330:    */   public void addPropertyChangeListener(PropertyChangeListener listener)
/* 331:    */   {
/* 332:531 */     this.m_propSupport.addPropertyChangeListener(listener);
/* 333:    */   }
/* 334:    */   
/* 335:    */   public void removePropertyChangeListener(PropertyChangeListener listener)
/* 336:    */   {
/* 337:543 */     this.m_propSupport.removePropertyChangeListener(listener);
/* 338:    */   }
/* 339:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.CostMatrixEditor
 * JD-Core Version:    0.7.0.1
 */