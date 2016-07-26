/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.FlowLayout;
/*   5:    */ import java.awt.GridLayout;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.beans.PropertyChangeListener;
/*   9:    */ import java.beans.PropertyChangeSupport;
/*  10:    */ import java.io.File;
/*  11:    */ import java.net.URL;
/*  12:    */ import javax.swing.BorderFactory;
/*  13:    */ import javax.swing.DefaultComboBoxModel;
/*  14:    */ import javax.swing.JButton;
/*  15:    */ import javax.swing.JComboBox;
/*  16:    */ import javax.swing.JFrame;
/*  17:    */ import javax.swing.JLabel;
/*  18:    */ import javax.swing.JOptionPane;
/*  19:    */ import javax.swing.JPanel;
/*  20:    */ import weka.core.Attribute;
/*  21:    */ import weka.core.Instances;
/*  22:    */ import weka.core.converters.ConverterUtils;
/*  23:    */ import weka.core.converters.FileSourcedConverter;
/*  24:    */ import weka.core.converters.IncrementalConverter;
/*  25:    */ import weka.core.converters.Loader;
/*  26:    */ import weka.core.converters.URLSourcedLoader;
/*  27:    */ 
/*  28:    */ public class SetInstancesPanel
/*  29:    */   extends JPanel
/*  30:    */ {
/*  31:    */   private static final long serialVersionUID = -384804041420453735L;
/*  32:    */   public static final String NO_CLASS = "No class";
/*  33: 74 */   protected JButton m_OpenFileBut = new JButton("Open file...");
/*  34: 77 */   protected JButton m_OpenURLBut = new JButton("Open URL...");
/*  35: 80 */   protected JButton m_CloseBut = new JButton("Close");
/*  36: 83 */   protected InstancesSummaryPanel m_Summary = new InstancesSummaryPanel();
/*  37: 86 */   protected JLabel m_ClassLabel = new JLabel("Class");
/*  38: 89 */   protected JComboBox m_ClassComboBox = new JComboBox(new DefaultComboBoxModel(new String[] { "No class" }));
/*  39: 93 */   protected ConverterFileChooser m_FileChooser = new ConverterFileChooser(new File(System.getProperty("user.dir")));
/*  40: 97 */   protected String m_LastURL = "http://";
/*  41:    */   protected Thread m_IOThread;
/*  42:106 */   protected PropertyChangeSupport m_Support = new PropertyChangeSupport(this);
/*  43:    */   protected Instances m_Instances;
/*  44:    */   protected Loader m_Loader;
/*  45:115 */   protected JFrame m_ParentFrame = null;
/*  46:118 */   protected JPanel m_CloseButPanel = null;
/*  47:121 */   protected boolean m_readIncrementally = true;
/*  48:124 */   protected boolean m_showZeroInstancesAsUnknown = false;
/*  49:    */   protected boolean m_showClassComboBox;
/*  50:    */   
/*  51:    */   public SetInstancesPanel()
/*  52:    */   {
/*  53:136 */     this(false, false, null);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public SetInstancesPanel(boolean showZeroInstancesAsUnknown, boolean showClassComboBox, ConverterFileChooser chooser)
/*  57:    */   {
/*  58:151 */     this.m_showZeroInstancesAsUnknown = showZeroInstancesAsUnknown;
/*  59:152 */     this.m_showClassComboBox = showClassComboBox;
/*  60:154 */     if (chooser != null) {
/*  61:155 */       this.m_FileChooser = chooser;
/*  62:    */     }
/*  63:158 */     this.m_OpenFileBut.setToolTipText("Open a set of instances from a file");
/*  64:159 */     this.m_OpenURLBut.setToolTipText("Open a set of instances from a URL");
/*  65:160 */     this.m_CloseBut.setToolTipText("Closes the dialog");
/*  66:161 */     this.m_FileChooser.setFileSelectionMode(0);
/*  67:162 */     this.m_OpenURLBut.addActionListener(new ActionListener()
/*  68:    */     {
/*  69:    */       public void actionPerformed(ActionEvent e)
/*  70:    */       {
/*  71:165 */         SetInstancesPanel.this.setInstancesFromURLQ();
/*  72:    */       }
/*  73:167 */     });
/*  74:168 */     this.m_OpenFileBut.addActionListener(new ActionListener()
/*  75:    */     {
/*  76:    */       public void actionPerformed(ActionEvent e)
/*  77:    */       {
/*  78:171 */         SetInstancesPanel.this.setInstancesFromFileQ();
/*  79:    */       }
/*  80:173 */     });
/*  81:174 */     this.m_CloseBut.addActionListener(new ActionListener()
/*  82:    */     {
/*  83:    */       public void actionPerformed(ActionEvent e)
/*  84:    */       {
/*  85:177 */         SetInstancesPanel.this.closeFrame();
/*  86:    */       }
/*  87:179 */     });
/*  88:180 */     this.m_Summary.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
/*  89:    */     
/*  90:182 */     this.m_ClassComboBox.addActionListener(new ActionListener()
/*  91:    */     {
/*  92:    */       public void actionPerformed(ActionEvent e)
/*  93:    */       {
/*  94:185 */         if ((SetInstancesPanel.this.m_Instances != null) && (SetInstancesPanel.this.m_ClassComboBox.getSelectedIndex() != -1) && 
/*  95:186 */           (SetInstancesPanel.this.m_Instances.numAttributes() >= SetInstancesPanel.this.m_ClassComboBox.getSelectedIndex()))
/*  96:    */         {
/*  97:187 */           SetInstancesPanel.this.m_Instances.setClassIndex(SetInstancesPanel.this.m_ClassComboBox.getSelectedIndex() - 1);
/*  98:    */           
/*  99:    */ 
/* 100:    */ 
/* 101:    */ 
/* 102:192 */           SetInstancesPanel.this.m_Support.firePropertyChange("", null, null);
/* 103:    */         }
/* 104:    */       }
/* 105:197 */     });
/* 106:198 */     JPanel panelButtons = new JPanel(new FlowLayout(0));
/* 107:199 */     panelButtons.add(this.m_OpenFileBut);
/* 108:200 */     panelButtons.add(this.m_OpenURLBut);
/* 109:    */     
/* 110:202 */     JPanel panelClass = new JPanel(new FlowLayout(0));
/* 111:203 */     panelClass.add(this.m_ClassLabel);
/* 112:204 */     panelClass.add(this.m_ClassComboBox);
/* 113:    */     JPanel panelButtonsAndClass;
/* 114:207 */     if (this.m_showClassComboBox)
/* 115:    */     {
/* 116:208 */       JPanel panelButtonsAndClass = new JPanel(new GridLayout(2, 1));
/* 117:209 */       panelButtonsAndClass.add(panelButtons);
/* 118:210 */       panelButtonsAndClass.add(panelClass);
/* 119:    */     }
/* 120:    */     else
/* 121:    */     {
/* 122:212 */       panelButtonsAndClass = new JPanel(new GridLayout(1, 1));
/* 123:213 */       panelButtonsAndClass.add(panelButtons);
/* 124:    */     }
/* 125:216 */     this.m_CloseButPanel = new JPanel(new FlowLayout(2));
/* 126:217 */     this.m_CloseButPanel.add(this.m_CloseBut);
/* 127:218 */     this.m_CloseButPanel.setVisible(false);
/* 128:    */     
/* 129:220 */     JPanel panelButtonsAll = new JPanel(new BorderLayout());
/* 130:221 */     panelButtonsAll.add(panelButtonsAndClass, "Center");
/* 131:222 */     panelButtonsAll.add(this.m_CloseButPanel, "South");
/* 132:    */     
/* 133:224 */     setLayout(new BorderLayout());
/* 134:225 */     add(this.m_Summary, "Center");
/* 135:226 */     add(panelButtonsAll, "South");
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void setParentFrame(JFrame parent)
/* 139:    */   {
/* 140:236 */     this.m_ParentFrame = parent;
/* 141:237 */     this.m_CloseButPanel.setVisible(this.m_ParentFrame != null);
/* 142:    */   }
/* 143:    */   
/* 144:    */   public JFrame getParentFrame()
/* 145:    */   {
/* 146:247 */     return this.m_ParentFrame;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public void closeFrame()
/* 150:    */   {
/* 151:254 */     if (this.m_ParentFrame != null) {
/* 152:255 */       this.m_ParentFrame.setVisible(false);
/* 153:    */     }
/* 154:    */   }
/* 155:    */   
/* 156:    */   public void setInstancesFromFileQ()
/* 157:    */   {
/* 158:265 */     if (this.m_IOThread == null)
/* 159:    */     {
/* 160:266 */       int returnVal = this.m_FileChooser.showOpenDialog(this);
/* 161:267 */       if (returnVal == 0)
/* 162:    */       {
/* 163:268 */         final File selected = this.m_FileChooser.getSelectedFile();
/* 164:269 */         this.m_IOThread = new Thread()
/* 165:    */         {
/* 166:    */           public void run()
/* 167:    */           {
/* 168:272 */             SetInstancesPanel.this.setInstancesFromFile(selected);
/* 169:273 */             SetInstancesPanel.this.m_IOThread = null;
/* 170:    */           }
/* 171:275 */         };
/* 172:276 */         this.m_IOThread.setPriority(1);
/* 173:277 */         this.m_IOThread.start();
/* 174:    */       }
/* 175:    */     }
/* 176:    */     else
/* 177:    */     {
/* 178:280 */       JOptionPane.showMessageDialog(this, "Can't load at this time,\ncurrently busy with other IO", "Load Instances", 2);
/* 179:    */     }
/* 180:    */   }
/* 181:    */   
/* 182:    */   public void setInstancesFromURLQ()
/* 183:    */   {
/* 184:293 */     if (this.m_IOThread == null) {
/* 185:    */       try
/* 186:    */       {
/* 187:295 */         String urlName = (String)JOptionPane.showInputDialog(this, "Enter the source URL", "Load Instances", 3, null, null, this.m_LastURL);
/* 188:298 */         if (urlName != null)
/* 189:    */         {
/* 190:299 */           this.m_LastURL = urlName;
/* 191:300 */           final URL url = new URL(urlName);
/* 192:301 */           this.m_IOThread = new Thread()
/* 193:    */           {
/* 194:    */             public void run()
/* 195:    */             {
/* 196:304 */               SetInstancesPanel.this.setInstancesFromURL(url);
/* 197:305 */               SetInstancesPanel.this.m_IOThread = null;
/* 198:    */             }
/* 199:307 */           };
/* 200:308 */           this.m_IOThread.setPriority(1);
/* 201:309 */           this.m_IOThread.start();
/* 202:    */         }
/* 203:    */       }
/* 204:    */       catch (Exception ex)
/* 205:    */       {
/* 206:312 */         JOptionPane.showMessageDialog(this, "Problem with URL:\n" + ex.getMessage(), "Load Instances", 0);
/* 207:    */       }
/* 208:    */     } else {
/* 209:317 */       JOptionPane.showMessageDialog(this, "Can't load at this time,\ncurrently busy with other IO", "Load Instances", 2);
/* 210:    */     }
/* 211:    */   }
/* 212:    */   
/* 213:    */   protected void setInstancesFromFile(File f)
/* 214:    */   {
/* 215:329 */     boolean incremental = this.m_readIncrementally;
/* 216:    */     try
/* 217:    */     {
/* 218:333 */       this.m_Loader = this.m_FileChooser.getLoader();
/* 219:334 */       if (this.m_Loader == null) {
/* 220:335 */         throw new Exception("No suitable FileSourcedConverter found for file!\n" + f);
/* 221:    */       }
/* 222:339 */       if (!(this.m_Loader instanceof IncrementalConverter)) {
/* 223:340 */         incremental = false;
/* 224:    */       }
/* 225:343 */       ((FileSourcedConverter)this.m_Loader).setFile(f);
/* 226:344 */       if (incremental)
/* 227:    */       {
/* 228:345 */         this.m_Summary.setShowZeroInstancesAsUnknown(this.m_showZeroInstancesAsUnknown);
/* 229:346 */         setInstances(this.m_Loader.getStructure());
/* 230:    */       }
/* 231:    */       else
/* 232:    */       {
/* 233:350 */         this.m_Summary.setShowZeroInstancesAsUnknown(false);
/* 234:351 */         setInstances(this.m_Loader.getDataSet());
/* 235:    */       }
/* 236:    */     }
/* 237:    */     catch (Exception ex)
/* 238:    */     {
/* 239:354 */       JOptionPane.showMessageDialog(this, "Couldn't read from file:\n" + f.getName(), "Load Instances", 0);
/* 240:    */     }
/* 241:    */   }
/* 242:    */   
/* 243:    */   protected void setInstancesFromURL(URL u)
/* 244:    */   {
/* 245:366 */     boolean incremental = this.m_readIncrementally;
/* 246:    */     try
/* 247:    */     {
/* 248:369 */       this.m_Loader = ConverterUtils.getURLLoaderForFile(u.toString());
/* 249:370 */       if (this.m_Loader == null) {
/* 250:371 */         throw new Exception("No suitable URLSourcedLoader found for URL!\n" + u);
/* 251:    */       }
/* 252:374 */       if (!(this.m_Loader instanceof IncrementalConverter)) {
/* 253:375 */         incremental = false;
/* 254:    */       }
/* 255:378 */       ((URLSourcedLoader)this.m_Loader).setURL(u.toString());
/* 256:379 */       if (incremental)
/* 257:    */       {
/* 258:380 */         this.m_Summary.setShowZeroInstancesAsUnknown(this.m_showZeroInstancesAsUnknown);
/* 259:381 */         setInstances(this.m_Loader.getStructure());
/* 260:    */       }
/* 261:    */       else
/* 262:    */       {
/* 263:383 */         this.m_Summary.setShowZeroInstancesAsUnknown(false);
/* 264:384 */         setInstances(this.m_Loader.getDataSet());
/* 265:    */       }
/* 266:    */     }
/* 267:    */     catch (Exception ex)
/* 268:    */     {
/* 269:387 */       JOptionPane.showMessageDialog(this, "Couldn't read from URL:\n" + u, "Load Instances", 0);
/* 270:    */     }
/* 271:    */   }
/* 272:    */   
/* 273:    */   public void setInstances(Instances i)
/* 274:    */   {
/* 275:399 */     this.m_Instances = i;
/* 276:400 */     this.m_Summary.setInstances(this.m_Instances);
/* 277:402 */     if (this.m_showClassComboBox)
/* 278:    */     {
/* 279:403 */       DefaultComboBoxModel model = (DefaultComboBoxModel)this.m_ClassComboBox.getModel();
/* 280:    */       
/* 281:405 */       model.removeAllElements();
/* 282:406 */       model.addElement("No class");
/* 283:407 */       for (int n = 0; n < this.m_Instances.numAttributes(); n++)
/* 284:    */       {
/* 285:408 */         Attribute att = this.m_Instances.attribute(n);
/* 286:409 */         String type = "(" + Attribute.typeToStringShort(att) + ")";
/* 287:410 */         model.addElement(type + " " + att.name());
/* 288:    */       }
/* 289:412 */       if (this.m_Instances.classIndex() == -1) {
/* 290:413 */         this.m_ClassComboBox.setSelectedIndex(this.m_Instances.numAttributes());
/* 291:    */       } else {
/* 292:415 */         this.m_ClassComboBox.setSelectedIndex(this.m_Instances.classIndex() + 1);
/* 293:    */       }
/* 294:    */     }
/* 295:423 */     this.m_Support.firePropertyChange("", null, null);
/* 296:    */   }
/* 297:    */   
/* 298:    */   public Instances getInstances()
/* 299:    */   {
/* 300:433 */     return this.m_Instances;
/* 301:    */   }
/* 302:    */   
/* 303:    */   public int getClassIndex()
/* 304:    */   {
/* 305:442 */     if (this.m_ClassComboBox.getSelectedIndex() <= 0) {
/* 306:443 */       return -1;
/* 307:    */     }
/* 308:445 */     return this.m_ClassComboBox.getSelectedIndex() - 1;
/* 309:    */   }
/* 310:    */   
/* 311:    */   public Loader getLoader()
/* 312:    */   {
/* 313:454 */     return this.m_Loader;
/* 314:    */   }
/* 315:    */   
/* 316:    */   public InstancesSummaryPanel getSummary()
/* 317:    */   {
/* 318:463 */     return this.m_Summary;
/* 319:    */   }
/* 320:    */   
/* 321:    */   public void setReadIncrementally(boolean incremental)
/* 322:    */   {
/* 323:479 */     this.m_readIncrementally = incremental;
/* 324:    */   }
/* 325:    */   
/* 326:    */   public boolean getReadIncrementally()
/* 327:    */   {
/* 328:488 */     return this.m_readIncrementally;
/* 329:    */   }
/* 330:    */   
/* 331:    */   public void addPropertyChangeListener(PropertyChangeListener l)
/* 332:    */   {
/* 333:498 */     if (this.m_Support != null) {
/* 334:499 */       this.m_Support.addPropertyChangeListener(l);
/* 335:    */     }
/* 336:    */   }
/* 337:    */   
/* 338:    */   public void removePropertyChangeListener(PropertyChangeListener l)
/* 339:    */   {
/* 340:510 */     this.m_Support.removePropertyChangeListener(l);
/* 341:    */   }
/* 342:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.SetInstancesPanel
 * JD-Core Version:    0.7.0.1
 */