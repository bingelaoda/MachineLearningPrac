/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.GridLayout;
/*   5:    */ import java.awt.Window;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.awt.event.FocusEvent;
/*   9:    */ import java.awt.event.FocusListener;
/*  10:    */ import java.beans.PropertyChangeListener;
/*  11:    */ import java.beans.PropertyChangeSupport;
/*  12:    */ import java.util.HashMap;
/*  13:    */ import javax.swing.BorderFactory;
/*  14:    */ import javax.swing.JButton;
/*  15:    */ import javax.swing.JCheckBox;
/*  16:    */ import javax.swing.JLabel;
/*  17:    */ import javax.swing.JPanel;
/*  18:    */ import javax.swing.JTextField;
/*  19:    */ import weka.classifiers.UpdateableClassifier;
/*  20:    */ import weka.core.Environment;
/*  21:    */ import weka.core.EnvironmentHandler;
/*  22:    */ import weka.gui.GenericObjectEditor;
/*  23:    */ import weka.gui.PropertySheetPanel;
/*  24:    */ 
/*  25:    */ public class ClassifierCustomizer
/*  26:    */   extends JPanel
/*  27:    */   implements BeanCustomizer, CustomizerClosingListener, CustomizerCloseRequester, EnvironmentHandler
/*  28:    */ {
/*  29:    */   private static final long serialVersionUID = -6688000820160821429L;
/*  30: 63 */   private final PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
/*  31:    */   private Classifier m_dsClassifier;
/*  32: 71 */   private final PropertySheetPanel m_ClassifierEditor = new PropertySheetPanel();
/*  33: 73 */   private final JPanel m_incrementalPanel = new JPanel();
/*  34: 74 */   private final JCheckBox m_resetIncrementalClassifier = new JCheckBox("Reset classifier at the start of the stream");
/*  35: 76 */   private final JCheckBox m_updateIncrementalClassifier = new JCheckBox("Update classifier on incoming instance stream");
/*  36: 78 */   private boolean m_panelVisible = false;
/*  37: 80 */   private final JPanel m_holderPanel = new JPanel();
/*  38: 81 */   private final JTextField m_executionSlotsText = new JTextField();
/*  39:    */   private final JLabel m_executionSlotsLabel;
/*  40:    */   private final JPanel m_executionSlotsPanel;
/*  41: 85 */   private final JCheckBox m_blockOnLastFold = new JCheckBox("Block on last fold of last run");
/*  42:    */   private FileEnvironmentField m_loadModelField;
/*  43:    */   private Window m_parentWindow;
/*  44:    */   protected weka.classifiers.Classifier m_backup;
/*  45: 95 */   private Environment m_env = Environment.getSystemWide();
/*  46:    */   private BeanCustomizer.ModifyListener m_modifyListener;
/*  47:    */   
/*  48:    */   public ClassifierCustomizer()
/*  49:    */   {
/*  50:105 */     this.m_ClassifierEditor.setBorder(BorderFactory.createTitledBorder("Classifier options"));
/*  51:    */     
/*  52:    */ 
/*  53:108 */     this.m_incrementalPanel.setLayout(new GridLayout(0, 1));
/*  54:109 */     this.m_resetIncrementalClassifier.setToolTipText("Reset the classifier before processing the first incoming instance");
/*  55:    */     
/*  56:111 */     this.m_updateIncrementalClassifier.setToolTipText("Train the classifier on each individual incoming streamed instance.");
/*  57:    */     
/*  58:113 */     this.m_updateIncrementalClassifier.addActionListener(new ActionListener()
/*  59:    */     {
/*  60:    */       public void actionPerformed(ActionEvent e)
/*  61:    */       {
/*  62:116 */         if (ClassifierCustomizer.this.m_dsClassifier != null) {
/*  63:117 */           ClassifierCustomizer.this.m_dsClassifier.setUpdateIncrementalClassifier(ClassifierCustomizer.this.m_updateIncrementalClassifier.isSelected());
/*  64:    */         }
/*  65:    */       }
/*  66:122 */     });
/*  67:123 */     this.m_resetIncrementalClassifier.addActionListener(new ActionListener()
/*  68:    */     {
/*  69:    */       public void actionPerformed(ActionEvent e)
/*  70:    */       {
/*  71:126 */         if (ClassifierCustomizer.this.m_dsClassifier != null) {
/*  72:127 */           ClassifierCustomizer.this.m_dsClassifier.setResetIncrementalClassifier(ClassifierCustomizer.this.m_resetIncrementalClassifier.isSelected());
/*  73:    */         }
/*  74:    */       }
/*  75:133 */     });
/*  76:134 */     this.m_incrementalPanel.add(this.m_resetIncrementalClassifier);
/*  77:135 */     this.m_incrementalPanel.add(this.m_updateIncrementalClassifier);
/*  78:    */     
/*  79:137 */     this.m_executionSlotsText.addActionListener(new ActionListener()
/*  80:    */     {
/*  81:    */       public void actionPerformed(ActionEvent e)
/*  82:    */       {
/*  83:140 */         if ((ClassifierCustomizer.this.m_dsClassifier != null) && (ClassifierCustomizer.this.m_executionSlotsText.getText().length() > 0))
/*  84:    */         {
/*  85:142 */           int newSlots = Integer.parseInt(ClassifierCustomizer.this.m_executionSlotsText.getText());
/*  86:143 */           ClassifierCustomizer.this.m_dsClassifier.setExecutionSlots(newSlots);
/*  87:    */         }
/*  88:    */       }
/*  89:147 */     });
/*  90:148 */     this.m_executionSlotsText.addFocusListener(new FocusListener()
/*  91:    */     {
/*  92:    */       public void focusGained(FocusEvent e) {}
/*  93:    */       
/*  94:    */       public void focusLost(FocusEvent e)
/*  95:    */       {
/*  96:155 */         if ((ClassifierCustomizer.this.m_dsClassifier != null) && (ClassifierCustomizer.this.m_executionSlotsText.getText().length() > 0))
/*  97:    */         {
/*  98:157 */           int newSlots = Integer.parseInt(ClassifierCustomizer.this.m_executionSlotsText.getText());
/*  99:158 */           ClassifierCustomizer.this.m_dsClassifier.setExecutionSlots(newSlots);
/* 100:    */         }
/* 101:    */       }
/* 102:162 */     });
/* 103:163 */     this.m_blockOnLastFold.addActionListener(new ActionListener()
/* 104:    */     {
/* 105:    */       public void actionPerformed(ActionEvent e)
/* 106:    */       {
/* 107:166 */         if (ClassifierCustomizer.this.m_dsClassifier != null) {
/* 108:167 */           ClassifierCustomizer.this.m_dsClassifier.setBlockOnLastFold(ClassifierCustomizer.this.m_blockOnLastFold.isSelected());
/* 109:    */         }
/* 110:    */       }
/* 111:171 */     });
/* 112:172 */     this.m_executionSlotsPanel = new JPanel();
/* 113:173 */     this.m_executionSlotsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/* 114:    */     
/* 115:175 */     this.m_executionSlotsLabel = new JLabel("Execution slots");
/* 116:176 */     this.m_executionSlotsPanel.setLayout(new BorderLayout());
/* 117:177 */     this.m_executionSlotsPanel.add(this.m_executionSlotsLabel, "West");
/* 118:178 */     this.m_executionSlotsPanel.add(this.m_executionSlotsText, "Center");
/* 119:179 */     this.m_holderPanel.setBorder(BorderFactory.createTitledBorder("More options"));
/* 120:180 */     this.m_holderPanel.setLayout(new BorderLayout());
/* 121:181 */     this.m_holderPanel.add(this.m_executionSlotsPanel, "North");
/* 122:    */     
/* 123:183 */     this.m_holderPanel.add(this.m_blockOnLastFold, "South");
/* 124:    */     
/* 125:185 */     JPanel holder2 = new JPanel();
/* 126:186 */     holder2.setLayout(new BorderLayout());
/* 127:187 */     holder2.add(this.m_holderPanel, "North");
/* 128:188 */     JButton OKBut = new JButton("OK");
/* 129:189 */     JButton CancelBut = new JButton("Cancel");
/* 130:190 */     OKBut.addActionListener(new ActionListener()
/* 131:    */     {
/* 132:    */       public void actionPerformed(ActionEvent e)
/* 133:    */       {
/* 134:195 */         ClassifierCustomizer.this.m_dsClassifier.setClassifierTemplate(ClassifierCustomizer.this.m_dsClassifier.getClassifierTemplate());
/* 135:197 */         if (ClassifierCustomizer.this.m_loadModelField != null)
/* 136:    */         {
/* 137:198 */           String loadFName = ClassifierCustomizer.this.m_loadModelField.getText();
/* 138:199 */           if ((loadFName != null) && (loadFName.length() > 0)) {
/* 139:200 */             ClassifierCustomizer.this.m_dsClassifier.setLoadClassifierFileName(ClassifierCustomizer.this.m_loadModelField.getText());
/* 140:    */           } else {
/* 141:202 */             ClassifierCustomizer.this.m_dsClassifier.setLoadClassifierFileName("");
/* 142:    */           }
/* 143:    */         }
/* 144:206 */         if (ClassifierCustomizer.this.m_modifyListener != null) {
/* 145:207 */           ClassifierCustomizer.this.m_modifyListener.setModifiedStatus(ClassifierCustomizer.this, true);
/* 146:    */         }
/* 147:210 */         ClassifierCustomizer.this.m_parentWindow.dispose();
/* 148:    */       }
/* 149:213 */     });
/* 150:214 */     CancelBut.addActionListener(new ActionListener()
/* 151:    */     {
/* 152:    */       public void actionPerformed(ActionEvent e)
/* 153:    */       {
/* 154:219 */         if (ClassifierCustomizer.this.m_backup != null) {
/* 155:220 */           ClassifierCustomizer.this.m_dsClassifier.setClassifierTemplate(ClassifierCustomizer.this.m_backup);
/* 156:    */         }
/* 157:223 */         if (ClassifierCustomizer.this.m_modifyListener != null) {
/* 158:224 */           ClassifierCustomizer.this.m_modifyListener.setModifiedStatus(ClassifierCustomizer.this, false);
/* 159:    */         }
/* 160:227 */         ClassifierCustomizer.this.m_parentWindow.dispose();
/* 161:    */       }
/* 162:230 */     });
/* 163:231 */     JPanel butHolder = new JPanel();
/* 164:232 */     butHolder.setLayout(new GridLayout(1, 2));
/* 165:233 */     butHolder.add(OKBut);
/* 166:234 */     butHolder.add(CancelBut);
/* 167:235 */     holder2.add(butHolder, "South");
/* 168:    */     
/* 169:237 */     setLayout(new BorderLayout());
/* 170:238 */     add(this.m_ClassifierEditor, "Center");
/* 171:239 */     add(holder2, "South");
/* 172:    */   }
/* 173:    */   
/* 174:    */   private void checkOnClassifierType()
/* 175:    */   {
/* 176:243 */     weka.classifiers.Classifier editedC = this.m_dsClassifier.getClassifierTemplate();
/* 177:244 */     if (((editedC instanceof UpdateableClassifier)) && (this.m_dsClassifier.hasIncomingStreamInstances()))
/* 178:    */     {
/* 179:246 */       if (!this.m_panelVisible)
/* 180:    */       {
/* 181:247 */         this.m_holderPanel.add(this.m_incrementalPanel, "South");
/* 182:248 */         this.m_panelVisible = true;
/* 183:249 */         this.m_executionSlotsText.setEnabled(false);
/* 184:250 */         this.m_loadModelField = new FileEnvironmentField("Load model from file", this.m_env);
/* 185:    */         
/* 186:252 */         this.m_incrementalPanel.add(this.m_loadModelField);
/* 187:253 */         this.m_loadModelField.setText(this.m_dsClassifier.getLoadClassifierFileName());
/* 188:    */       }
/* 189:    */     }
/* 190:    */     else
/* 191:    */     {
/* 192:256 */       if (this.m_panelVisible) {
/* 193:257 */         this.m_holderPanel.remove(this.m_incrementalPanel);
/* 194:    */       }
/* 195:260 */       if (this.m_dsClassifier.hasIncomingStreamInstances())
/* 196:    */       {
/* 197:261 */         this.m_loadModelField = new FileEnvironmentField("Load model from file", this.m_env);
/* 198:    */         
/* 199:263 */         this.m_executionSlotsPanel.add(this.m_loadModelField, "South");
/* 200:264 */         this.m_executionSlotsText.setEnabled(false);
/* 201:265 */         this.m_blockOnLastFold.setEnabled(false);
/* 202:266 */         this.m_loadModelField.setText(this.m_dsClassifier.getLoadClassifierFileName());
/* 203:    */       }
/* 204:    */       else
/* 205:    */       {
/* 206:268 */         this.m_executionSlotsText.setEnabled(true);
/* 207:269 */         this.m_blockOnLastFold.setEnabled(true);
/* 208:    */       }
/* 209:271 */       this.m_panelVisible = false;
/* 210:273 */       if ((this.m_dsClassifier.hasIncomingBatchInstances()) && (!this.m_dsClassifier.m_listenees.containsKey("trainingSet")))
/* 211:    */       {
/* 212:275 */         this.m_holderPanel.remove(this.m_blockOnLastFold);
/* 213:276 */         this.m_holderPanel.remove(this.m_executionSlotsPanel);
/* 214:277 */         this.m_loadModelField = new FileEnvironmentField("Load model from file", this.m_env);
/* 215:    */         
/* 216:279 */         this.m_holderPanel.add(this.m_loadModelField, "South");
/* 217:280 */         this.m_loadModelField.setText(this.m_dsClassifier.getLoadClassifierFileName());
/* 218:    */       }
/* 219:    */     }
/* 220:    */   }
/* 221:    */   
/* 222:    */   public void setObject(Object object)
/* 223:    */   {
/* 224:292 */     this.m_dsClassifier = ((Classifier)object);
/* 225:    */     try
/* 226:    */     {
/* 227:295 */       this.m_backup = ((weka.classifiers.Classifier)GenericObjectEditor.makeCopy(this.m_dsClassifier.getClassifierTemplate()));
/* 228:    */     }
/* 229:    */     catch (Exception ex) {}
/* 230:300 */     this.m_ClassifierEditor.setEnvironment(this.m_env);
/* 231:301 */     this.m_ClassifierEditor.setTarget(this.m_dsClassifier.getClassifierTemplate());
/* 232:302 */     this.m_resetIncrementalClassifier.setSelected(this.m_dsClassifier.getResetIncrementalClassifier());
/* 233:    */     
/* 234:304 */     this.m_updateIncrementalClassifier.setSelected(this.m_dsClassifier.getUpdateIncrementalClassifier());
/* 235:    */     
/* 236:306 */     this.m_executionSlotsText.setText("" + this.m_dsClassifier.getExecutionSlots());
/* 237:307 */     this.m_blockOnLastFold.setSelected(this.m_dsClassifier.getBlockOnLastFold());
/* 238:308 */     checkOnClassifierType();
/* 239:    */   }
/* 240:    */   
/* 241:    */   public void customizerClosing()
/* 242:    */   {
/* 243:318 */     if (this.m_executionSlotsText.getText().length() > 0)
/* 244:    */     {
/* 245:319 */       int newSlots = Integer.parseInt(this.m_executionSlotsText.getText());
/* 246:320 */       this.m_dsClassifier.setExecutionSlots(newSlots);
/* 247:    */     }
/* 248:    */   }
/* 249:    */   
/* 250:    */   public void addPropertyChangeListener(PropertyChangeListener pcl)
/* 251:    */   {
/* 252:331 */     this.m_pcSupport.addPropertyChangeListener(pcl);
/* 253:    */   }
/* 254:    */   
/* 255:    */   public void removePropertyChangeListener(PropertyChangeListener pcl)
/* 256:    */   {
/* 257:341 */     this.m_pcSupport.removePropertyChangeListener(pcl);
/* 258:    */   }
/* 259:    */   
/* 260:    */   public void setParentWindow(Window parent)
/* 261:    */   {
/* 262:346 */     this.m_parentWindow = parent;
/* 263:    */   }
/* 264:    */   
/* 265:    */   public void setEnvironment(Environment env)
/* 266:    */   {
/* 267:356 */     this.m_env = env;
/* 268:    */   }
/* 269:    */   
/* 270:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/* 271:    */   {
/* 272:361 */     this.m_modifyListener = l;
/* 273:    */   }
/* 274:    */   
/* 275:    */   static {}
/* 276:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ClassifierCustomizer
 * JD-Core Version:    0.7.0.1
 */