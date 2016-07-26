/*   1:    */ package weka.gui.experiment;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dialog.ModalityType;
/*   6:    */ import java.awt.Dimension;
/*   7:    */ import java.awt.FlowLayout;
/*   8:    */ import java.awt.Frame;
/*   9:    */ import java.awt.GridLayout;
/*  10:    */ import java.awt.event.ActionEvent;
/*  11:    */ import java.awt.event.ActionListener;
/*  12:    */ import java.beans.PropertyChangeEvent;
/*  13:    */ import java.beans.PropertyChangeListener;
/*  14:    */ import java.io.PrintStream;
/*  15:    */ import java.util.Vector;
/*  16:    */ import javax.swing.BorderFactory;
/*  17:    */ import javax.swing.JButton;
/*  18:    */ import javax.swing.JCheckBox;
/*  19:    */ import javax.swing.JComboBox;
/*  20:    */ import javax.swing.JDialog;
/*  21:    */ import javax.swing.JLabel;
/*  22:    */ import javax.swing.JPanel;
/*  23:    */ import javax.swing.JRootPane;
/*  24:    */ import javax.swing.JSpinner;
/*  25:    */ import javax.swing.SpinnerNumberModel;
/*  26:    */ import javax.swing.event.ChangeEvent;
/*  27:    */ import javax.swing.event.ChangeListener;
/*  28:    */ import weka.experiment.ResultMatrix;
/*  29:    */ import weka.experiment.ResultMatrixPlainText;
/*  30:    */ import weka.gui.GenericObjectEditor;
/*  31:    */ import weka.gui.GenericObjectEditor.GOEPanel;
/*  32:    */ import weka.gui.PropertyPanel;
/*  33:    */ 
/*  34:    */ public class OutputFormatDialog
/*  35:    */   extends JDialog
/*  36:    */ {
/*  37:    */   private static final long serialVersionUID = 2169792738187807378L;
/*  38:    */   public static final int APPROVE_OPTION = 0;
/*  39:    */   public static final int CANCEL_OPTION = 1;
/*  40:    */   protected int m_Result;
/*  41:    */   protected Vector<Class<?>> m_OutputFormatClasses;
/*  42:    */   protected Vector<String> m_OutputFormatNames;
/*  43:    */   protected GenericObjectEditor m_ResultMatrixEditor;
/*  44:    */   protected PropertyPanel m_ResultMatrixPanel;
/*  45:    */   protected JLabel m_ResultMatrixLabel;
/*  46:    */   protected ResultMatrix m_ResultMatrix;
/*  47:    */   protected JComboBox m_OutputFormatComboBox;
/*  48:    */   protected JLabel m_OutputFormatLabel;
/*  49:    */   protected JSpinner m_MeanPrecSpinner;
/*  50:    */   protected JLabel m_MeanPrecLabel;
/*  51:    */   protected JSpinner m_StdDevPrecSpinner;
/*  52:    */   protected JLabel m_StdDevPrecLabel;
/*  53:    */   protected JCheckBox m_ShowAverageCheckBox;
/*  54:    */   protected JLabel m_ShowAverageLabel;
/*  55:    */   protected JCheckBox m_RemoveFilterNameCheckBox;
/*  56:    */   protected JLabel m_RemoveFilterNameLabel;
/*  57:    */   protected JButton m_OkButton;
/*  58:    */   protected JButton m_CancelButton;
/*  59:    */   protected boolean m_IgnoreChanges;
/*  60:    */   
/*  61:    */   public OutputFormatDialog(Frame parent)
/*  62:    */   {
/*  63:135 */     super(parent, "Output Format...", Dialog.ModalityType.DOCUMENT_MODAL);
/*  64:    */     
/*  65:137 */     this.m_IgnoreChanges = true;
/*  66:    */     
/*  67:139 */     initialize();
/*  68:140 */     initGUI();
/*  69:    */     
/*  70:142 */     this.m_IgnoreChanges = false;
/*  71:    */   }
/*  72:    */   
/*  73:    */   protected void initialize()
/*  74:    */   {
/*  75:154 */     this.m_Result = 1;
/*  76:156 */     if (this.m_OutputFormatClasses == null)
/*  77:    */     {
/*  78:157 */       Vector<String> classes = GenericObjectEditor.getClassnames(ResultMatrix.class.getName());
/*  79:    */       
/*  80:    */ 
/*  81:160 */       this.m_OutputFormatClasses = new Vector();
/*  82:161 */       this.m_OutputFormatNames = new Vector();
/*  83:162 */       for (int i = 0; i < classes.size(); i++) {
/*  84:    */         try
/*  85:    */         {
/*  86:164 */           Class<?> cls = Class.forName(((String)classes.get(i)).toString());
/*  87:165 */           ResultMatrix matrix = (ResultMatrix)cls.newInstance();
/*  88:166 */           this.m_OutputFormatClasses.add(cls);
/*  89:167 */           this.m_OutputFormatNames.add(matrix.getDisplayName());
/*  90:    */         }
/*  91:    */         catch (Exception e)
/*  92:    */         {
/*  93:169 */           e.printStackTrace();
/*  94:    */         }
/*  95:    */       }
/*  96:    */     }
/*  97:    */   }
/*  98:    */   
/*  99:    */   protected void initGUI()
/* 100:    */   {
/* 101:183 */     getContentPane().setLayout(new BorderLayout());
/* 102:    */     
/* 103:185 */     JPanel panel = new JPanel(new GridLayout(6, 1));
/* 104:186 */     panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/* 105:187 */     getContentPane().add(panel, "Center");
/* 106:    */     
/* 107:    */ 
/* 108:190 */     this.m_MeanPrecSpinner = new JSpinner();
/* 109:191 */     this.m_MeanPrecSpinner.addChangeListener(new ChangeListener()
/* 110:    */     {
/* 111:    */       public void stateChanged(ChangeEvent e)
/* 112:    */       {
/* 113:194 */         OutputFormatDialog.this.getData();
/* 114:    */       }
/* 115:196 */     });
/* 116:197 */     SpinnerNumberModel model = (SpinnerNumberModel)this.m_MeanPrecSpinner.getModel();
/* 117:198 */     model.setMaximum(new Integer(20));
/* 118:199 */     model.setMinimum(new Integer(0));
/* 119:200 */     this.m_MeanPrecLabel = new JLabel("Mean Precision");
/* 120:201 */     this.m_MeanPrecLabel.setDisplayedMnemonic('M');
/* 121:202 */     this.m_MeanPrecLabel.setLabelFor(this.m_MeanPrecSpinner);
/* 122:203 */     JPanel panel2 = new JPanel(new FlowLayout(0));
/* 123:204 */     panel2.add(this.m_MeanPrecLabel);
/* 124:205 */     panel2.add(this.m_MeanPrecSpinner);
/* 125:206 */     panel.add(panel2);
/* 126:    */     
/* 127:    */ 
/* 128:209 */     this.m_StdDevPrecSpinner = new JSpinner();
/* 129:210 */     this.m_StdDevPrecSpinner.addChangeListener(new ChangeListener()
/* 130:    */     {
/* 131:    */       public void stateChanged(ChangeEvent e)
/* 132:    */       {
/* 133:213 */         OutputFormatDialog.this.getData();
/* 134:    */       }
/* 135:215 */     });
/* 136:216 */     model = (SpinnerNumberModel)this.m_StdDevPrecSpinner.getModel();
/* 137:217 */     model.setMaximum(new Integer(20));
/* 138:218 */     model.setMinimum(new Integer(0));
/* 139:219 */     this.m_StdDevPrecLabel = new JLabel("StdDev. Precision");
/* 140:220 */     this.m_StdDevPrecLabel.setDisplayedMnemonic('S');
/* 141:221 */     this.m_StdDevPrecLabel.setLabelFor(this.m_StdDevPrecSpinner);
/* 142:222 */     panel2 = new JPanel(new FlowLayout(0));
/* 143:223 */     panel2.add(this.m_StdDevPrecLabel);
/* 144:224 */     panel2.add(this.m_StdDevPrecSpinner);
/* 145:225 */     panel.add(panel2);
/* 146:    */     
/* 147:    */ 
/* 148:228 */     this.m_OutputFormatComboBox = new JComboBox(this.m_OutputFormatNames);
/* 149:229 */     this.m_OutputFormatComboBox.addActionListener(new ActionListener()
/* 150:    */     {
/* 151:    */       public void actionPerformed(ActionEvent e)
/* 152:    */       {
/* 153:232 */         OutputFormatDialog.this.getData();
/* 154:    */       }
/* 155:234 */     });
/* 156:235 */     this.m_OutputFormatLabel = new JLabel("Output Format");
/* 157:236 */     this.m_OutputFormatLabel.setDisplayedMnemonic('F');
/* 158:237 */     this.m_OutputFormatLabel.setLabelFor(this.m_OutputFormatComboBox);
/* 159:238 */     panel2 = new JPanel(new FlowLayout(0));
/* 160:239 */     panel2.add(this.m_OutputFormatLabel);
/* 161:240 */     panel2.add(this.m_OutputFormatComboBox);
/* 162:241 */     panel.add(panel2);
/* 163:    */     
/* 164:    */ 
/* 165:244 */     this.m_ShowAverageCheckBox = new JCheckBox("");
/* 166:245 */     this.m_ShowAverageCheckBox.addChangeListener(new ChangeListener()
/* 167:    */     {
/* 168:    */       public void stateChanged(ChangeEvent e)
/* 169:    */       {
/* 170:248 */         OutputFormatDialog.this.getData();
/* 171:    */       }
/* 172:250 */     });
/* 173:251 */     this.m_ShowAverageLabel = new JLabel("Show Average");
/* 174:252 */     this.m_ShowAverageLabel.setDisplayedMnemonic('A');
/* 175:253 */     this.m_ShowAverageLabel.setLabelFor(this.m_ShowAverageCheckBox);
/* 176:254 */     panel2 = new JPanel(new FlowLayout(0));
/* 177:255 */     panel2.add(this.m_ShowAverageLabel);
/* 178:256 */     panel2.add(this.m_ShowAverageCheckBox);
/* 179:257 */     panel.add(panel2);
/* 180:    */     
/* 181:    */ 
/* 182:260 */     this.m_RemoveFilterNameCheckBox = new JCheckBox("");
/* 183:261 */     this.m_RemoveFilterNameCheckBox.addChangeListener(new ChangeListener()
/* 184:    */     {
/* 185:    */       public void stateChanged(ChangeEvent e)
/* 186:    */       {
/* 187:264 */         OutputFormatDialog.this.getData();
/* 188:    */       }
/* 189:266 */     });
/* 190:267 */     this.m_RemoveFilterNameLabel = new JLabel("Remove filter classnames");
/* 191:268 */     this.m_RemoveFilterNameLabel.setDisplayedMnemonic('R');
/* 192:269 */     this.m_RemoveFilterNameLabel.setLabelFor(this.m_RemoveFilterNameCheckBox);
/* 193:270 */     panel2 = new JPanel(new FlowLayout(0));
/* 194:271 */     panel2.add(this.m_RemoveFilterNameLabel);
/* 195:272 */     panel2.add(this.m_RemoveFilterNameCheckBox);
/* 196:273 */     panel.add(panel2);
/* 197:    */     
/* 198:    */ 
/* 199:276 */     this.m_ResultMatrix = ExperimenterDefaults.getOutputFormat();
/* 200:277 */     this.m_ResultMatrixEditor = new GenericObjectEditor(true);
/* 201:278 */     this.m_ResultMatrixEditor.setClassType(ResultMatrix.class);
/* 202:279 */     this.m_ResultMatrixEditor.setValue(this.m_ResultMatrix);
/* 203:280 */     this.m_ResultMatrixEditor.addPropertyChangeListener(new PropertyChangeListener()
/* 204:    */     {
/* 205:    */       public void propertyChange(PropertyChangeEvent e)
/* 206:    */       {
/* 207:285 */         if (!OutputFormatDialog.this.m_ResultMatrix.getClass().equals(OutputFormatDialog.this.m_ResultMatrixEditor.getValue().getClass()))
/* 208:    */         {
/* 209:289 */           if (OutputFormatDialog.this.m_ResultMatrixEditor.getValue().getClass().equals(ExperimenterDefaults.getOutputFormat().getClass()))
/* 210:    */           {
/* 211:291 */             OutputFormatDialog.this.m_ResultMatrix = ExperimenterDefaults.getOutputFormat();
/* 212:292 */             OutputFormatDialog.this.m_ResultMatrixEditor.setValue(ExperimenterDefaults.getOutputFormat());
/* 213:    */           }
/* 214:    */           else
/* 215:    */           {
/* 216:295 */             OutputFormatDialog.this.m_ResultMatrix = ((ResultMatrix)OutputFormatDialog.this.m_ResultMatrixEditor.getValue());
/* 217:    */           }
/* 218:297 */           OutputFormatDialog.this.setData();
/* 219:    */         }
/* 220:299 */         OutputFormatDialog.this.repaint();
/* 221:    */       }
/* 222:301 */     });
/* 223:302 */     ((GenericObjectEditor.GOEPanel)this.m_ResultMatrixEditor.getCustomEditor()).addOkListener(new ActionListener()
/* 224:    */     {
/* 225:    */       public void actionPerformed(ActionEvent e)
/* 226:    */       {
/* 227:306 */         OutputFormatDialog.this.m_ResultMatrix = ((ResultMatrix)OutputFormatDialog.this.m_ResultMatrixEditor.getValue());
/* 228:307 */         OutputFormatDialog.this.setData();
/* 229:    */       }
/* 230:309 */     });
/* 231:310 */     this.m_ResultMatrixPanel = new PropertyPanel(this.m_ResultMatrixEditor, false);
/* 232:311 */     this.m_ResultMatrixLabel = new JLabel("Advanced setup");
/* 233:312 */     panel2 = new JPanel(new FlowLayout(0));
/* 234:313 */     panel2.add(this.m_ResultMatrixLabel);
/* 235:314 */     panel2.add(this.m_ResultMatrixPanel);
/* 236:315 */     panel.add(panel2);
/* 237:    */     
/* 238:    */ 
/* 239:318 */     panel = new JPanel(new FlowLayout(2));
/* 240:319 */     getContentPane().add(panel, "South");
/* 241:320 */     this.m_CancelButton = new JButton("Cancel");
/* 242:321 */     this.m_CancelButton.setMnemonic('C');
/* 243:322 */     this.m_CancelButton.addActionListener(new ActionListener()
/* 244:    */     {
/* 245:    */       public void actionPerformed(ActionEvent e)
/* 246:    */       {
/* 247:325 */         OutputFormatDialog.this.m_Result = 1;
/* 248:326 */         OutputFormatDialog.this.setVisible(false);
/* 249:    */       }
/* 250:328 */     });
/* 251:329 */     this.m_OkButton = new JButton("OK");
/* 252:330 */     this.m_OkButton.setMnemonic('O');
/* 253:331 */     this.m_OkButton.addActionListener(new ActionListener()
/* 254:    */     {
/* 255:    */       public void actionPerformed(ActionEvent e)
/* 256:    */       {
/* 257:334 */         OutputFormatDialog.this.getData();
/* 258:335 */         OutputFormatDialog.this.m_Result = 0;
/* 259:336 */         OutputFormatDialog.this.setVisible(false);
/* 260:    */       }
/* 261:338 */     });
/* 262:339 */     panel.add(this.m_OkButton);
/* 263:340 */     panel.add(this.m_CancelButton);
/* 264:    */     
/* 265:    */ 
/* 266:343 */     getRootPane().setDefaultButton(this.m_OkButton);
/* 267:    */     
/* 268:    */ 
/* 269:346 */     pack();
/* 270:    */     
/* 271:    */ 
/* 272:349 */     this.m_MeanPrecLabel.setPreferredSize(new Dimension(this.m_RemoveFilterNameLabel.getWidth(), this.m_MeanPrecLabel.getHeight()));
/* 273:    */     
/* 274:351 */     this.m_MeanPrecSpinner.setPreferredSize(new Dimension(this.m_MeanPrecSpinner.getWidth() * 3, this.m_MeanPrecSpinner.getHeight()));
/* 275:    */     
/* 276:353 */     this.m_StdDevPrecLabel.setPreferredSize(new Dimension(this.m_RemoveFilterNameLabel.getWidth(), this.m_StdDevPrecLabel.getHeight()));
/* 277:    */     
/* 278:355 */     this.m_StdDevPrecSpinner.setPreferredSize(new Dimension(this.m_StdDevPrecSpinner.getWidth() * 3, this.m_StdDevPrecSpinner.getHeight()));
/* 279:    */     
/* 280:357 */     this.m_OutputFormatLabel.setPreferredSize(new Dimension(this.m_RemoveFilterNameLabel.getWidth(), this.m_OutputFormatLabel.getHeight()));
/* 281:    */     
/* 282:359 */     this.m_ShowAverageLabel.setPreferredSize(new Dimension(this.m_RemoveFilterNameLabel.getWidth(), this.m_ShowAverageLabel.getHeight()));
/* 283:    */     
/* 284:361 */     this.m_ResultMatrixLabel.setPreferredSize(new Dimension(this.m_RemoveFilterNameLabel.getWidth(), this.m_ResultMatrixLabel.getHeight()));
/* 285:    */     
/* 286:363 */     this.m_ResultMatrixPanel.setPreferredSize(new Dimension((int)(this.m_ResultMatrixPanel.getWidth() * 1.5D), this.m_ResultMatrixPanel.getHeight()));
/* 287:    */     
/* 288:    */ 
/* 289:    */ 
/* 290:    */ 
/* 291:368 */     pack();
/* 292:    */   }
/* 293:    */   
/* 294:    */   private void setData()
/* 295:    */   {
/* 296:375 */     this.m_IgnoreChanges = true;
/* 297:    */     
/* 298:    */ 
/* 299:378 */     this.m_MeanPrecSpinner.setValue(Integer.valueOf(this.m_ResultMatrix.getMeanPrec()));
/* 300:379 */     this.m_StdDevPrecSpinner.setValue(Integer.valueOf(this.m_ResultMatrix.getStdDevPrec()));
/* 301:382 */     for (int i = 0; i < this.m_OutputFormatClasses.size(); i++) {
/* 302:383 */       if (((Class)this.m_OutputFormatClasses.get(i)).equals(this.m_ResultMatrix.getClass()))
/* 303:    */       {
/* 304:384 */         this.m_OutputFormatComboBox.setSelectedItem(this.m_OutputFormatNames.get(i));
/* 305:385 */         break;
/* 306:    */       }
/* 307:    */     }
/* 308:390 */     this.m_ShowAverageCheckBox.setSelected(this.m_ResultMatrix.getShowAverage());
/* 309:    */     
/* 310:    */ 
/* 311:393 */     this.m_RemoveFilterNameCheckBox.setSelected(this.m_ResultMatrix.getRemoveFilterName());
/* 312:    */     
/* 313:    */ 
/* 314:    */ 
/* 315:397 */     this.m_ResultMatrixEditor.setValue(this.m_ResultMatrix);
/* 316:    */     
/* 317:399 */     this.m_IgnoreChanges = false;
/* 318:    */   }
/* 319:    */   
/* 320:    */   private void getData()
/* 321:    */   {
/* 322:406 */     if (this.m_IgnoreChanges) {
/* 323:407 */       return;
/* 324:    */     }
/* 325:    */     try
/* 326:    */     {
/* 327:412 */       if (!this.m_ResultMatrix.getClass().equals(this.m_OutputFormatClasses.get(this.m_OutputFormatComboBox.getSelectedIndex()))) {
/* 328:414 */         if (((Class)this.m_OutputFormatClasses.get(this.m_OutputFormatComboBox.getSelectedIndex())).equals(ExperimenterDefaults.getOutputFormat().getClass())) {
/* 329:417 */           this.m_ResultMatrix = ExperimenterDefaults.getOutputFormat();
/* 330:    */         } else {
/* 331:419 */           this.m_ResultMatrix = ((ResultMatrix)((Class)this.m_OutputFormatClasses.get(this.m_OutputFormatComboBox.getSelectedIndex())).newInstance());
/* 332:    */         }
/* 333:    */       }
/* 334:    */     }
/* 335:    */     catch (Exception e)
/* 336:    */     {
/* 337:424 */       e.printStackTrace();
/* 338:425 */       this.m_ResultMatrix = new ResultMatrixPlainText();
/* 339:    */     }
/* 340:429 */     this.m_ResultMatrix.setMeanPrec(Integer.parseInt(this.m_MeanPrecSpinner.getValue().toString()));
/* 341:    */     
/* 342:431 */     this.m_ResultMatrix.setStdDevPrec(Integer.parseInt(this.m_StdDevPrecSpinner.getValue().toString()));
/* 343:    */     
/* 344:    */ 
/* 345:    */ 
/* 346:435 */     this.m_ResultMatrix.setShowAverage(this.m_ShowAverageCheckBox.isSelected());
/* 347:    */     
/* 348:    */ 
/* 349:438 */     this.m_ResultMatrix.setRemoveFilterName(this.m_RemoveFilterNameCheckBox.isSelected());
/* 350:    */     
/* 351:    */ 
/* 352:441 */     this.m_ResultMatrixEditor.setValue(this.m_ResultMatrix);
/* 353:    */   }
/* 354:    */   
/* 355:    */   public void setResultMatrix(ResultMatrix matrix)
/* 356:    */   {
/* 357:450 */     this.m_ResultMatrix = matrix;
/* 358:451 */     setData();
/* 359:    */   }
/* 360:    */   
/* 361:    */   public ResultMatrix getResultMatrix()
/* 362:    */   {
/* 363:460 */     return this.m_ResultMatrix;
/* 364:    */   }
/* 365:    */   
/* 366:    */   protected void setFormat()
/* 367:    */   {
/* 368:467 */     for (int i = 0; i < this.m_OutputFormatClasses.size(); i++) {
/* 369:468 */       if (((String)this.m_OutputFormatNames.get(i)).equals(this.m_OutputFormatComboBox.getItemAt(i).toString()))
/* 370:    */       {
/* 371:470 */         this.m_OutputFormatComboBox.setSelectedIndex(i);
/* 372:471 */         break;
/* 373:    */       }
/* 374:    */     }
/* 375:    */   }
/* 376:    */   
/* 377:    */   public int getResult()
/* 378:    */   {
/* 379:485 */     return this.m_Result;
/* 380:    */   }
/* 381:    */   
/* 382:    */   public int showDialog()
/* 383:    */   {
/* 384:494 */     this.m_Result = 1;
/* 385:495 */     setData();
/* 386:496 */     setVisible(true);
/* 387:497 */     return this.m_Result;
/* 388:    */   }
/* 389:    */   
/* 390:    */   public static void main(String[] args)
/* 391:    */   {
/* 392:508 */     OutputFormatDialog dialog = new OutputFormatDialog(null);
/* 393:509 */     if (dialog.showDialog() == 0) {
/* 394:510 */       System.out.println("Accepted");
/* 395:    */     } else {
/* 396:512 */       System.out.println("Aborted");
/* 397:    */     }
/* 398:    */   }
/* 399:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.experiment.OutputFormatDialog
 * JD-Core Version:    0.7.0.1
 */