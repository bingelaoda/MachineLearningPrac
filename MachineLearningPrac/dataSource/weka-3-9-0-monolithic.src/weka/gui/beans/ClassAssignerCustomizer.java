/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.GridLayout;
/*   5:    */ import java.awt.Window;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.beans.PropertyChangeListener;
/*   9:    */ import java.beans.PropertyChangeSupport;
/*  10:    */ import java.io.PrintStream;
/*  11:    */ import javax.swing.BorderFactory;
/*  12:    */ import javax.swing.DefaultComboBoxModel;
/*  13:    */ import javax.swing.JButton;
/*  14:    */ import javax.swing.JComboBox;
/*  15:    */ import javax.swing.JLabel;
/*  16:    */ import javax.swing.JPanel;
/*  17:    */ import weka.core.Attribute;
/*  18:    */ import weka.core.Instances;
/*  19:    */ import weka.gui.PropertySheetPanel;
/*  20:    */ 
/*  21:    */ public class ClassAssignerCustomizer
/*  22:    */   extends JPanel
/*  23:    */   implements BeanCustomizer, CustomizerClosingListener, CustomizerCloseRequester, DataFormatListener
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = 476539385765301907L;
/*  26: 56 */   private boolean m_displayColNames = false;
/*  27:    */   private transient ClassAssigner m_classAssigner;
/*  28: 60 */   private transient PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
/*  29: 63 */   private transient PropertySheetPanel m_caEditor = new PropertySheetPanel();
/*  30: 66 */   private transient JComboBox m_ClassCombo = new JComboBox();
/*  31: 67 */   private transient JPanel m_holderP = new JPanel();
/*  32:    */   private transient BeanCustomizer.ModifyListener m_modifyListener;
/*  33:    */   private transient Window m_parent;
/*  34:    */   private transient String m_backup;
/*  35:    */   
/*  36:    */   public ClassAssignerCustomizer()
/*  37:    */   {
/*  38: 76 */     setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
/*  39:    */     
/*  40: 78 */     setLayout(new BorderLayout());
/*  41: 79 */     add(new JLabel("ClassAssignerCustomizer"), "North");
/*  42:    */     
/*  43: 81 */     this.m_holderP.setLayout(new BorderLayout());
/*  44: 82 */     this.m_holderP.setBorder(BorderFactory.createTitledBorder("Choose class attribute"));
/*  45: 83 */     this.m_holderP.add(this.m_ClassCombo, "Center");
/*  46: 84 */     this.m_ClassCombo.setEditable(true);
/*  47: 85 */     this.m_ClassCombo.addActionListener(new ActionListener()
/*  48:    */     {
/*  49:    */       public void actionPerformed(ActionEvent e)
/*  50:    */       {
/*  51: 87 */         if ((ClassAssignerCustomizer.this.m_classAssigner != null) && (ClassAssignerCustomizer.this.m_displayColNames == true))
/*  52:    */         {
/*  53: 89 */           String selectedI = (String)ClassAssignerCustomizer.this.m_ClassCombo.getSelectedItem();
/*  54: 90 */           selectedI = selectedI.replace("(Num)", "").replace("(Nom)", "").replace("(Str)", "").replace("(Dat)", "").replace("(Rel)", "").replace("(???)", "").trim();
/*  55: 93 */           if (selectedI.equals("NO CLASS")) {
/*  56: 96 */             selectedI = "0";
/*  57:    */           }
/*  58: 99 */           ClassAssignerCustomizer.this.m_classAssigner.setClassColumn(selectedI);
/*  59:    */         }
/*  60:    */       }
/*  61:102 */     });
/*  62:103 */     add(this.m_caEditor, "Center");
/*  63:104 */     addButtons();
/*  64:    */   }
/*  65:    */   
/*  66:    */   private void addButtons()
/*  67:    */   {
/*  68:108 */     JButton okBut = new JButton("OK");
/*  69:109 */     JButton cancelBut = new JButton("Cancel");
/*  70:    */     
/*  71:111 */     JPanel butHolder = new JPanel();
/*  72:112 */     butHolder.setLayout(new GridLayout(1, 2));
/*  73:113 */     butHolder.add(okBut);butHolder.add(cancelBut);
/*  74:114 */     add(butHolder, "South");
/*  75:    */     
/*  76:116 */     okBut.addActionListener(new ActionListener()
/*  77:    */     {
/*  78:    */       public void actionPerformed(ActionEvent e)
/*  79:    */       {
/*  80:118 */         ClassAssignerCustomizer.this.m_modifyListener.setModifiedStatus(ClassAssignerCustomizer.this, true);
/*  81:119 */         if (ClassAssignerCustomizer.this.m_parent != null) {
/*  82:120 */           ClassAssignerCustomizer.this.m_parent.dispose();
/*  83:    */         }
/*  84:    */       }
/*  85:124 */     });
/*  86:125 */     cancelBut.addActionListener(new ActionListener()
/*  87:    */     {
/*  88:    */       public void actionPerformed(ActionEvent e)
/*  89:    */       {
/*  90:127 */         ClassAssignerCustomizer.this.customizerClosing();
/*  91:128 */         if (ClassAssignerCustomizer.this.m_parent != null) {
/*  92:129 */           ClassAssignerCustomizer.this.m_parent.dispose();
/*  93:    */         }
/*  94:    */       }
/*  95:    */     });
/*  96:    */   }
/*  97:    */   
/*  98:    */   private void setUpStandardSelection()
/*  99:    */   {
/* 100:136 */     if (this.m_displayColNames == true)
/* 101:    */     {
/* 102:137 */       remove(this.m_holderP);
/* 103:138 */       this.m_caEditor.setTarget(this.m_classAssigner);
/* 104:139 */       add(this.m_caEditor, "Center");
/* 105:140 */       this.m_displayColNames = false;
/* 106:    */     }
/* 107:142 */     validate();repaint();
/* 108:    */   }
/* 109:    */   
/* 110:    */   private void setUpColumnSelection(Instances format)
/* 111:    */   {
/* 112:146 */     if (!this.m_displayColNames) {
/* 113:147 */       remove(this.m_caEditor);
/* 114:    */     }
/* 115:150 */     int existingClassCol = 0;
/* 116:    */     
/* 117:152 */     String classColString = this.m_classAssigner.getClassColumn();
/* 118:153 */     if ((classColString.trim().toLowerCase().compareTo("last") == 0) || (classColString.equalsIgnoreCase("/last")))
/* 119:    */     {
/* 120:155 */       existingClassCol = format.numAttributes() - 1;
/* 121:    */     }
/* 122:156 */     else if ((classColString.trim().toLowerCase().compareTo("first") != 0) && (!classColString.equalsIgnoreCase("/first")))
/* 123:    */     {
/* 124:161 */       Attribute classAtt = format.attribute(classColString);
/* 125:162 */       if (classAtt != null)
/* 126:    */       {
/* 127:163 */         existingClassCol = classAtt.index();
/* 128:    */       }
/* 129:    */       else
/* 130:    */       {
/* 131:    */         try
/* 132:    */         {
/* 133:167 */           existingClassCol = Integer.parseInt(classColString);
/* 134:    */         }
/* 135:    */         catch (NumberFormatException ex)
/* 136:    */         {
/* 137:169 */           System.err.println("Warning : can't parse '" + classColString + "' as a number " + " or find it as an attribute in the incoming data (ClassAssigner)");
/* 138:    */         }
/* 139:172 */         if (existingClassCol < 0) {
/* 140:173 */           existingClassCol = -1;
/* 141:174 */         } else if (existingClassCol > format.numAttributes() - 1) {
/* 142:175 */           existingClassCol = format.numAttributes() - 1;
/* 143:    */         } else {
/* 144:177 */           existingClassCol--;
/* 145:    */         }
/* 146:    */       }
/* 147:    */     }
/* 148:187 */     String[] attribNames = new String[format.numAttributes() + 1];
/* 149:188 */     attribNames[0] = "NO CLASS";
/* 150:189 */     for (int i = 1; i < attribNames.length; i++)
/* 151:    */     {
/* 152:190 */       String type = "(" + Attribute.typeToStringShort(format.attribute(i - 1)) + ") ";
/* 153:191 */       attribNames[i] = (type + format.attribute(i - 1).name());
/* 154:    */     }
/* 155:193 */     this.m_ClassCombo.setModel(new DefaultComboBoxModel(attribNames));
/* 156:194 */     if (attribNames.length > 0) {
/* 157:195 */       this.m_ClassCombo.setSelectedIndex(existingClassCol + 1);
/* 158:    */     }
/* 159:197 */     if (!this.m_displayColNames)
/* 160:    */     {
/* 161:198 */       add(this.m_holderP, "Center");
/* 162:199 */       this.m_displayColNames = true;
/* 163:    */     }
/* 164:201 */     validate();repaint();
/* 165:    */   }
/* 166:    */   
/* 167:    */   public void setObject(Object object)
/* 168:    */   {
/* 169:210 */     if (this.m_classAssigner != (ClassAssigner)object)
/* 170:    */     {
/* 171:211 */       this.m_classAssigner = ((ClassAssigner)object);
/* 172:    */       
/* 173:    */ 
/* 174:    */ 
/* 175:    */ 
/* 176:    */ 
/* 177:    */ 
/* 178:    */ 
/* 179:219 */       this.m_caEditor.setTarget(this.m_classAssigner);
/* 180:220 */       if (this.m_classAssigner.getConnectedFormat() != null) {
/* 181:221 */         setUpColumnSelection(this.m_classAssigner.getConnectedFormat());
/* 182:    */       }
/* 183:223 */       this.m_backup = this.m_classAssigner.getClassColumn();
/* 184:    */     }
/* 185:    */   }
/* 186:    */   
/* 187:    */   public void customizerClosing()
/* 188:    */   {
/* 189:229 */     if (this.m_classAssigner != null) {
/* 190:231 */       this.m_classAssigner.removeDataFormatListener(this);
/* 191:    */     }
/* 192:234 */     if (this.m_backup != null) {
/* 193:235 */       this.m_classAssigner.setClassColumn(this.m_backup);
/* 194:    */     }
/* 195:    */   }
/* 196:    */   
/* 197:    */   public void newDataFormat(DataSetEvent dse)
/* 198:    */   {
/* 199:240 */     if (dse.getDataSet() != null) {
/* 200:242 */       setUpColumnSelection(this.m_classAssigner.getConnectedFormat());
/* 201:    */     } else {
/* 202:244 */       setUpStandardSelection();
/* 203:    */     }
/* 204:    */   }
/* 205:    */   
/* 206:    */   public void addPropertyChangeListener(PropertyChangeListener pcl)
/* 207:    */   {
/* 208:254 */     this.m_pcSupport.addPropertyChangeListener(pcl);
/* 209:    */   }
/* 210:    */   
/* 211:    */   public void removePropertyChangeListener(PropertyChangeListener pcl)
/* 212:    */   {
/* 213:263 */     this.m_pcSupport.removePropertyChangeListener(pcl);
/* 214:    */   }
/* 215:    */   
/* 216:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/* 217:    */   {
/* 218:268 */     this.m_modifyListener = l;
/* 219:    */   }
/* 220:    */   
/* 221:    */   public void setParentWindow(Window parent)
/* 222:    */   {
/* 223:273 */     this.m_parent = parent;
/* 224:    */   }
/* 225:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ClassAssignerCustomizer
 * JD-Core Version:    0.7.0.1
 */