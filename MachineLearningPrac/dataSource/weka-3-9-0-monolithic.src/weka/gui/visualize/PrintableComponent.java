/*   1:    */ package weka.gui.visualize;
/*   2:    */ 
/*   3:    */ import java.awt.Dimension;
/*   4:    */ import java.awt.event.ItemEvent;
/*   5:    */ import java.awt.event.ItemListener;
/*   6:    */ import java.awt.event.MouseAdapter;
/*   7:    */ import java.awt.event.MouseEvent;
/*   8:    */ import java.io.File;
/*   9:    */ import java.io.PrintStream;
/*  10:    */ import java.util.Collections;
/*  11:    */ import java.util.Hashtable;
/*  12:    */ import java.util.Properties;
/*  13:    */ import java.util.Vector;
/*  14:    */ import javax.swing.JCheckBox;
/*  15:    */ import javax.swing.JComponent;
/*  16:    */ import javax.swing.JFileChooser;
/*  17:    */ import javax.swing.JLabel;
/*  18:    */ import javax.swing.JPanel;
/*  19:    */ import javax.swing.JTextField;
/*  20:    */ import javax.swing.event.DocumentEvent;
/*  21:    */ import javax.swing.event.DocumentListener;
/*  22:    */ import javax.swing.text.Document;
/*  23:    */ import weka.gui.ExtensionFileFilter;
/*  24:    */ import weka.gui.GenericObjectEditor;
/*  25:    */ 
/*  26:    */ public class PrintableComponent
/*  27:    */   implements PrintableHandler
/*  28:    */ {
/*  29:    */   protected JComponent m_Component;
/*  30:    */   protected static JFileChooser m_FileChooserPanel;
/*  31:    */   protected static JCheckBox m_CustomDimensionsCheckBox;
/*  32:    */   protected static JTextField m_CustomWidthText;
/*  33:    */   protected static JTextField m_CustomHeightText;
/*  34:    */   protected static JCheckBox m_AspectRatioCheckBox;
/*  35: 84 */   protected String m_SaveDialogTitle = "Save as...";
/*  36: 87 */   protected double m_xScale = 1.0D;
/*  37: 90 */   protected double m_yScale = 1.0D;
/*  38:    */   protected double m_AspectRatio;
/*  39:    */   protected boolean m_IgnoreChange;
/*  40:    */   private static final boolean DEBUG = false;
/*  41:102 */   protected static boolean m_ToolTipUserAsked = false;
/*  42:    */   protected static final String PROPERTY_SHOW = "PrintableComponentToolTipShow";
/*  43:    */   protected static final String PROPERTY_USERASKED = "PrintableComponentToolTipUserAsked";
/*  44:111 */   protected static boolean m_ShowToolTip = true;
/*  45:    */   
/*  46:    */   static
/*  47:    */   {
/*  48:    */     try
/*  49:    */     {
/*  50:114 */       m_ShowToolTip = Boolean.valueOf(VisualizeUtils.VISUALIZE_PROPERTIES.getProperty("PrintableComponentToolTipShow", "true")).booleanValue();
/*  51:    */       
/*  52:    */ 
/*  53:117 */       m_ToolTipUserAsked = Boolean.valueOf(VisualizeUtils.VISUALIZE_PROPERTIES.getProperty("PrintableComponentToolTipUserAsked", "false")).booleanValue();
/*  54:    */     }
/*  55:    */     catch (Exception e)
/*  56:    */     {
/*  57:122 */       m_ToolTipUserAsked = false;
/*  58:123 */       m_ShowToolTip = true;
/*  59:    */     }
/*  60:    */   }
/*  61:    */   
/*  62:    */   public PrintableComponent(JComponent component)
/*  63:    */   {
/*  64:142 */     this.m_Component = component;
/*  65:143 */     this.m_AspectRatio = (0.0D / 0.0D);
/*  66:    */     
/*  67:145 */     getComponent().addMouseListener(new PrintMouseListener(this));
/*  68:146 */     getComponent().setToolTipText(getToolTipText(this));
/*  69:147 */     initFileChooser();
/*  70:    */   }
/*  71:    */   
/*  72:    */   public JComponent getComponent()
/*  73:    */   {
/*  74:156 */     return this.m_Component;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public static String getToolTipText(PrintableComponent component)
/*  78:    */   {
/*  79:179 */     return null;
/*  80:    */   }
/*  81:    */   
/*  82:    */   protected void initFileChooser()
/*  83:    */   {
/*  84:264 */     if (m_FileChooserPanel != null) {
/*  85:265 */       return;
/*  86:    */     }
/*  87:268 */     m_FileChooserPanel = new JFileChooser();
/*  88:269 */     m_FileChooserPanel.resetChoosableFileFilters();
/*  89:270 */     m_FileChooserPanel.setAcceptAllFileFilterUsed(false);
/*  90:    */     
/*  91:    */ 
/*  92:273 */     JPanel accessory = new JPanel();
/*  93:274 */     accessory.setLayout(null);
/*  94:275 */     accessory.setPreferredSize(new Dimension(200, 200));
/*  95:276 */     accessory.revalidate();
/*  96:277 */     m_FileChooserPanel.setAccessory(accessory);
/*  97:    */     
/*  98:279 */     m_CustomDimensionsCheckBox = new JCheckBox("Use custom dimensions");
/*  99:280 */     m_CustomDimensionsCheckBox.setBounds(14, 7, 200, 21);
/* 100:281 */     m_CustomDimensionsCheckBox.addItemListener(new ItemListener()
/* 101:    */     {
/* 102:    */       public void itemStateChanged(ItemEvent e)
/* 103:    */       {
/* 104:284 */         boolean custom = PrintableComponent.m_CustomDimensionsCheckBox.isSelected();
/* 105:285 */         PrintableComponent.m_CustomWidthText.setEnabled(custom);
/* 106:286 */         PrintableComponent.m_CustomHeightText.setEnabled(custom);
/* 107:287 */         PrintableComponent.m_AspectRatioCheckBox.setEnabled(custom);
/* 108:288 */         if (custom)
/* 109:    */         {
/* 110:289 */           PrintableComponent.this.m_IgnoreChange = true;
/* 111:290 */           PrintableComponent.m_CustomWidthText.setText("" + PrintableComponent.this.m_Component.getWidth());
/* 112:291 */           PrintableComponent.m_CustomHeightText.setText("" + PrintableComponent.this.m_Component.getHeight());
/* 113:292 */           PrintableComponent.this.m_IgnoreChange = false;
/* 114:    */         }
/* 115:    */         else
/* 116:    */         {
/* 117:294 */           PrintableComponent.this.m_IgnoreChange = true;
/* 118:295 */           PrintableComponent.m_CustomWidthText.setText("-1");
/* 119:296 */           PrintableComponent.m_CustomHeightText.setText("-1");
/* 120:297 */           PrintableComponent.this.m_IgnoreChange = false;
/* 121:    */         }
/* 122:    */       }
/* 123:300 */     });
/* 124:301 */     accessory.add(m_CustomDimensionsCheckBox);
/* 125:    */     
/* 126:303 */     m_CustomWidthText = new JTextField(5);
/* 127:304 */     m_CustomWidthText.setText("-1");
/* 128:305 */     m_CustomWidthText.setEnabled(false);
/* 129:306 */     m_CustomWidthText.setBounds(65, 35, 50, 21);
/* 130:307 */     m_CustomWidthText.getDocument().addDocumentListener(new DocumentListener()
/* 131:    */     {
/* 132:    */       public void changedUpdate(DocumentEvent e)
/* 133:    */       {
/* 134:310 */         PrintableComponent.this.updateDimensions(PrintableComponent.m_CustomWidthText);
/* 135:    */       }
/* 136:    */       
/* 137:    */       public void insertUpdate(DocumentEvent e)
/* 138:    */       {
/* 139:315 */         PrintableComponent.this.updateDimensions(PrintableComponent.m_CustomWidthText);
/* 140:    */       }
/* 141:    */       
/* 142:    */       public void removeUpdate(DocumentEvent e)
/* 143:    */       {
/* 144:320 */         PrintableComponent.this.updateDimensions(PrintableComponent.m_CustomWidthText);
/* 145:    */       }
/* 146:322 */     });
/* 147:323 */     JLabel label = new JLabel("Width");
/* 148:324 */     label.setLabelFor(m_CustomWidthText);
/* 149:325 */     label.setDisplayedMnemonic('W');
/* 150:326 */     label.setBounds(14, 35, 50, 21);
/* 151:327 */     accessory.add(label);
/* 152:328 */     accessory.add(m_CustomWidthText);
/* 153:    */     
/* 154:330 */     m_CustomHeightText = new JTextField(5);
/* 155:331 */     m_CustomHeightText.setText("-1");
/* 156:332 */     m_CustomHeightText.setEnabled(false);
/* 157:333 */     m_CustomHeightText.setBounds(65, 63, 50, 21);
/* 158:334 */     m_CustomHeightText.getDocument().addDocumentListener(new DocumentListener()
/* 159:    */     {
/* 160:    */       public void changedUpdate(DocumentEvent e)
/* 161:    */       {
/* 162:338 */         PrintableComponent.this.updateDimensions(PrintableComponent.m_CustomHeightText);
/* 163:    */       }
/* 164:    */       
/* 165:    */       public void insertUpdate(DocumentEvent e)
/* 166:    */       {
/* 167:343 */         PrintableComponent.this.updateDimensions(PrintableComponent.m_CustomHeightText);
/* 168:    */       }
/* 169:    */       
/* 170:    */       public void removeUpdate(DocumentEvent e)
/* 171:    */       {
/* 172:348 */         PrintableComponent.this.updateDimensions(PrintableComponent.m_CustomHeightText);
/* 173:    */       }
/* 174:350 */     });
/* 175:351 */     label = new JLabel("Height");
/* 176:352 */     label.setLabelFor(m_CustomHeightText);
/* 177:353 */     label.setDisplayedMnemonic('H');
/* 178:354 */     label.setBounds(14, 63, 50, 21);
/* 179:355 */     accessory.add(label);
/* 180:356 */     accessory.add(m_CustomHeightText);
/* 181:    */     
/* 182:358 */     m_AspectRatioCheckBox = new JCheckBox("Keep aspect ratio");
/* 183:359 */     m_AspectRatioCheckBox.setBounds(14, 91, 200, 21);
/* 184:360 */     m_AspectRatioCheckBox.setEnabled(false);
/* 185:361 */     m_AspectRatioCheckBox.setSelected(true);
/* 186:362 */     m_AspectRatioCheckBox.addItemListener(new ItemListener()
/* 187:    */     {
/* 188:    */       public void itemStateChanged(ItemEvent e)
/* 189:    */       {
/* 190:365 */         boolean keep = PrintableComponent.m_AspectRatioCheckBox.isSelected();
/* 191:366 */         if (keep)
/* 192:    */         {
/* 193:367 */           PrintableComponent.this.m_IgnoreChange = true;
/* 194:368 */           PrintableComponent.m_CustomWidthText.setText("" + PrintableComponent.this.m_Component.getWidth());
/* 195:369 */           PrintableComponent.m_CustomHeightText.setText("" + PrintableComponent.this.m_Component.getHeight());
/* 196:370 */           PrintableComponent.this.m_IgnoreChange = false;
/* 197:    */         }
/* 198:    */       }
/* 199:373 */     });
/* 200:374 */     accessory.add(m_AspectRatioCheckBox);
/* 201:    */     
/* 202:    */ 
/* 203:377 */     Vector<String> writerNames = GenericObjectEditor.getClassnames(JComponentWriter.class.getName());
/* 204:    */     
/* 205:379 */     Collections.sort(writerNames);
/* 206:380 */     for (int i = 0; i < writerNames.size(); i++) {
/* 207:    */       try
/* 208:    */       {
/* 209:382 */         Class<?> cls = Class.forName(((String)writerNames.get(i)).toString());
/* 210:383 */         JComponentWriter writer = (JComponentWriter)cls.newInstance();
/* 211:384 */         m_FileChooserPanel.addChoosableFileFilter(new JComponentWriterFileFilter(writer.getExtension(), writer.getDescription() + " (*" + writer.getExtension() + ")", writer));
/* 212:    */       }
/* 213:    */       catch (Exception e)
/* 214:    */       {
/* 215:389 */         System.err.println((String)writerNames.get(i) + ": " + e);
/* 216:    */       }
/* 217:    */     }
/* 218:394 */     if (m_FileChooserPanel.getChoosableFileFilters().length > 0) {
/* 219:395 */       m_FileChooserPanel.setFileFilter(m_FileChooserPanel.getChoosableFileFilters()[0]);
/* 220:    */     }
/* 221:    */   }
/* 222:    */   
/* 223:    */   protected void updateDimensions(JTextField sender)
/* 224:    */   {
/* 225:410 */     if ((!m_AspectRatioCheckBox.isSelected()) || (this.m_IgnoreChange)) {
/* 226:411 */       return;
/* 227:    */     }
/* 228:413 */     if ((!(sender instanceof JTextField)) || (sender == null)) {
/* 229:414 */       return;
/* 230:    */     }
/* 231:416 */     if (sender.getText().length() == 0) {
/* 232:    */       return;
/* 233:    */     }
/* 234:    */     int baseValue;
/* 235:    */     int newValue;
/* 236:    */     try
/* 237:    */     {
/* 238:422 */       baseValue = Integer.parseInt(sender.getText());
/* 239:423 */       newValue = 0;
/* 240:424 */       if (baseValue <= 0) {
/* 241:425 */         return;
/* 242:    */       }
/* 243:428 */       if (Double.isNaN(this.m_AspectRatio)) {
/* 244:429 */         this.m_AspectRatio = (getComponent().getWidth() / getComponent().getHeight());
/* 245:    */       }
/* 246:    */     }
/* 247:    */     catch (Exception e)
/* 248:    */     {
/* 249:434 */       return;
/* 250:    */     }
/* 251:438 */     this.m_IgnoreChange = true;
/* 252:439 */     if (sender == m_CustomWidthText)
/* 253:    */     {
/* 254:440 */       newValue = (int)(baseValue * (1.0D / this.m_AspectRatio));
/* 255:441 */       m_CustomHeightText.setText("" + newValue);
/* 256:    */     }
/* 257:442 */     else if (sender == m_CustomHeightText)
/* 258:    */     {
/* 259:443 */       newValue = (int)(baseValue * this.m_AspectRatio);
/* 260:444 */       m_CustomWidthText.setText("" + newValue);
/* 261:    */     }
/* 262:446 */     this.m_IgnoreChange = false;
/* 263:    */   }
/* 264:    */   
/* 265:    */   public Hashtable<String, JComponentWriter> getWriters()
/* 266:    */   {
/* 267:462 */     Hashtable<String, JComponentWriter> result = new Hashtable();
/* 268:464 */     for (int i = 0; i < m_FileChooserPanel.getChoosableFileFilters().length; i++)
/* 269:    */     {
/* 270:465 */       JComponentWriter writer = ((JComponentWriterFileFilter)m_FileChooserPanel.getChoosableFileFilters()[i]).getWriter();
/* 271:    */       
/* 272:467 */       result.put(writer.getDescription(), writer);
/* 273:    */     }
/* 274:470 */     return result;
/* 275:    */   }
/* 276:    */   
/* 277:    */   public JComponentWriter getWriter(String name)
/* 278:    */   {
/* 279:483 */     return (JComponentWriter)getWriters().get(name);
/* 280:    */   }
/* 281:    */   
/* 282:    */   public void setSaveDialogTitle(String title)
/* 283:    */   {
/* 284:493 */     this.m_SaveDialogTitle = title;
/* 285:    */   }
/* 286:    */   
/* 287:    */   public String getSaveDialogTitle()
/* 288:    */   {
/* 289:503 */     return this.m_SaveDialogTitle;
/* 290:    */   }
/* 291:    */   
/* 292:    */   public void setScale(double x, double y)
/* 293:    */   {
/* 294:514 */     this.m_xScale = x;
/* 295:515 */     this.m_yScale = y;
/* 296:    */   }
/* 297:    */   
/* 298:    */   public double getXScale()
/* 299:    */   {
/* 300:528 */     return this.m_xScale;
/* 301:    */   }
/* 302:    */   
/* 303:    */   public double getYScale()
/* 304:    */   {
/* 305:538 */     return this.m_xScale;
/* 306:    */   }
/* 307:    */   
/* 308:    */   public void saveComponent()
/* 309:    */   {
/* 310:558 */     m_FileChooserPanel.setDialogTitle(getSaveDialogTitle());
/* 311:    */     do
/* 312:    */     {
/* 313:560 */       int result = m_FileChooserPanel.showSaveDialog(getComponent());
/* 314:561 */       if (result != 0) {
/* 315:562 */         return;
/* 316:    */       }
/* 317:564 */     } while (m_FileChooserPanel.getSelectedFile() == null);
/* 318:    */     try
/* 319:    */     {
/* 320:568 */       JComponentWriterFileFilter filter = (JComponentWriterFileFilter)m_FileChooserPanel.getFileFilter();
/* 321:569 */       File file = m_FileChooserPanel.getSelectedFile();
/* 322:570 */       JComponentWriter writer = filter.getWriter();
/* 323:571 */       if (!file.getAbsolutePath().toLowerCase().endsWith(writer.getExtension().toLowerCase())) {
/* 324:573 */         file = new File(file.getAbsolutePath() + writer.getExtension());
/* 325:    */       }
/* 326:575 */       writer.setComponent(getComponent());
/* 327:576 */       writer.setFile(file);
/* 328:577 */       writer.setScale(getXScale(), getYScale());
/* 329:578 */       writer.setUseCustomDimensions(m_CustomDimensionsCheckBox.isSelected());
/* 330:579 */       if (m_CustomDimensionsCheckBox.isSelected())
/* 331:    */       {
/* 332:580 */         writer.setCustomWidth(Integer.parseInt(m_CustomWidthText.getText()));
/* 333:581 */         writer.setCustomHeight(Integer.parseInt(m_CustomHeightText.getText()));
/* 334:    */       }
/* 335:    */       else
/* 336:    */       {
/* 337:583 */         writer.setCustomWidth(-1);
/* 338:584 */         writer.setCustomHeight(-1);
/* 339:    */       }
/* 340:586 */       writer.toOutput();
/* 341:    */     }
/* 342:    */     catch (Exception e)
/* 343:    */     {
/* 344:588 */       e.printStackTrace();
/* 345:    */     }
/* 346:    */   }
/* 347:    */   
/* 348:    */   protected class JComponentWriterFileFilter
/* 349:    */     extends ExtensionFileFilter
/* 350:    */   {
/* 351:    */     private static final long serialVersionUID = 8540426888094207515L;
/* 352:    */     private final JComponentWriter m_Writer;
/* 353:    */     
/* 354:    */     public JComponentWriterFileFilter(String extension, String description, JComponentWriter writer)
/* 355:    */     {
/* 356:611 */       super(description);
/* 357:612 */       this.m_Writer = writer;
/* 358:    */     }
/* 359:    */     
/* 360:    */     public JComponentWriter getWriter()
/* 361:    */     {
/* 362:621 */       return this.m_Writer;
/* 363:    */     }
/* 364:    */   }
/* 365:    */   
/* 366:    */   private class PrintMouseListener
/* 367:    */     extends MouseAdapter
/* 368:    */   {
/* 369:    */     private final PrintableComponent m_Component;
/* 370:    */     
/* 371:    */     public PrintMouseListener(PrintableComponent component)
/* 372:    */     {
/* 373:638 */       this.m_Component = component;
/* 374:    */     }
/* 375:    */     
/* 376:    */     public void mouseClicked(MouseEvent e)
/* 377:    */     {
/* 378:648 */       int modifiers = e.getModifiers();
/* 379:649 */       if (((modifiers & 0x1) == 1) && ((modifiers & 0x8) == 8) && ((modifiers & 0x10) == 16))
/* 380:    */       {
/* 381:652 */         e.consume();
/* 382:653 */         this.m_Component.saveComponent();
/* 383:    */       }
/* 384:    */     }
/* 385:    */   }
/* 386:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.PrintableComponent
 * JD-Core Version:    0.7.0.1
 */