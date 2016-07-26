/*   1:    */ package weka.gui.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.Font;
/*   7:    */ import java.awt.Frame;
/*   8:    */ import java.awt.GridLayout;
/*   9:    */ import java.awt.Point;
/*  10:    */ import java.awt.Window;
/*  11:    */ import java.awt.event.ActionEvent;
/*  12:    */ import java.awt.event.ActionListener;
/*  13:    */ import java.awt.event.WindowAdapter;
/*  14:    */ import java.awt.event.WindowEvent;
/*  15:    */ import java.io.IOException;
/*  16:    */ import java.lang.reflect.Method;
/*  17:    */ import javax.swing.BorderFactory;
/*  18:    */ import javax.swing.JButton;
/*  19:    */ import javax.swing.JDialog;
/*  20:    */ import javax.swing.JPanel;
/*  21:    */ import javax.swing.JScrollPane;
/*  22:    */ import javax.swing.JTextArea;
/*  23:    */ import javax.swing.JTextPane;
/*  24:    */ import weka.core.Defaults;
/*  25:    */ import weka.core.Environment;
/*  26:    */ import weka.core.EnvironmentHandler;
/*  27:    */ import weka.core.Settings;
/*  28:    */ import weka.core.Utils;
/*  29:    */ import weka.gui.GUIApplication;
/*  30:    */ import weka.gui.PropertyDialog;
/*  31:    */ import weka.gui.SettingsEditor;
/*  32:    */ import weka.knowledgeflow.steps.Step;
/*  33:    */ 
/*  34:    */ public abstract class StepEditorDialog
/*  35:    */   extends JPanel
/*  36:    */   implements EnvironmentHandler
/*  37:    */ {
/*  38:    */   private static final long serialVersionUID = -4860182109190301676L;
/*  39:    */   protected boolean m_isEdited;
/*  40: 64 */   protected Environment m_env = Environment.getSystemWide();
/*  41: 67 */   protected JPanel m_buttonHolder = new JPanel(new GridLayout(1, 0));
/*  42: 70 */   protected JButton m_okBut = new JButton("OK");
/*  43: 73 */   protected JButton m_cancelBut = new JButton("Cancel");
/*  44: 76 */   protected JButton m_settingsBut = new JButton("Settings");
/*  45:    */   protected MainKFPerspective m_mainPerspective;
/*  46:    */   protected Window m_parent;
/*  47:    */   protected ClosingListener m_closingListener;
/*  48:    */   protected Step m_stepToEdit;
/*  49: 91 */   protected StringBuilder m_helpText = new StringBuilder();
/*  50: 94 */   protected JButton m_helpBut = new JButton("About");
/*  51:    */   
/*  52:    */   public StepEditorDialog()
/*  53:    */   {
/*  54:100 */     setLayout(new BorderLayout());
/*  55:    */     
/*  56:102 */     this.m_buttonHolder.add(this.m_okBut);
/*  57:103 */     this.m_buttonHolder.add(this.m_cancelBut);
/*  58:104 */     add(this.m_buttonHolder, "South");
/*  59:    */     
/*  60:106 */     this.m_okBut.addActionListener(new ActionListener()
/*  61:    */     {
/*  62:    */       public void actionPerformed(ActionEvent e)
/*  63:    */       {
/*  64:109 */         StepEditorDialog.this.ok();
/*  65:    */       }
/*  66:112 */     });
/*  67:113 */     this.m_cancelBut.addActionListener(new ActionListener()
/*  68:    */     {
/*  69:    */       public void actionPerformed(ActionEvent e)
/*  70:    */       {
/*  71:116 */         StepEditorDialog.this.cancel();
/*  72:    */       }
/*  73:    */     });
/*  74:    */   }
/*  75:    */   
/*  76:    */   protected void setMainPerspective(MainKFPerspective main)
/*  77:    */   {
/*  78:127 */     this.m_mainPerspective = main;
/*  79:    */   }
/*  80:    */   
/*  81:    */   protected MainKFPerspective getMainPerspective()
/*  82:    */   {
/*  83:136 */     return this.m_mainPerspective;
/*  84:    */   }
/*  85:    */   
/*  86:    */   protected void showErrorDialog(Exception cause)
/*  87:    */   {
/*  88:145 */     this.m_mainPerspective.showErrorDialog(cause);
/*  89:    */   }
/*  90:    */   
/*  91:    */   protected void showInfoDialog(Object information, String title, boolean isWarning)
/*  92:    */   {
/*  93:157 */     this.m_mainPerspective.showInfoDialog(information, title, isWarning);
/*  94:    */   }
/*  95:    */   
/*  96:    */   protected Step getStepToEdit()
/*  97:    */   {
/*  98:166 */     return this.m_stepToEdit;
/*  99:    */   }
/* 100:    */   
/* 101:    */   protected void setStepToEdit(Step step)
/* 102:    */   {
/* 103:175 */     this.m_stepToEdit = step;
/* 104:    */     
/* 105:177 */     createAboutPanel(step);
/* 106:178 */     if (step.getDefaultSettings() != null) {
/* 107:179 */       addSettingsButton();
/* 108:    */     }
/* 109:181 */     layoutEditor();
/* 110:    */   }
/* 111:    */   
/* 112:    */   protected void layoutEditor() {}
/* 113:    */   
/* 114:    */   protected void addSettingsButton()
/* 115:    */   {
/* 116:197 */     getMainPerspective().getMainApplication().getApplicationSettings().applyDefaults(getStepToEdit().getDefaultSettings());
/* 117:    */     
/* 118:199 */     this.m_buttonHolder.add(this.m_settingsBut);
/* 119:200 */     this.m_settingsBut.addActionListener(new ActionListener()
/* 120:    */     {
/* 121:    */       public void actionPerformed(ActionEvent e)
/* 122:    */       {
/* 123:    */         try
/* 124:    */         {
/* 125:204 */           SettingsEditor.showSingleSettingsEditor(StepEditorDialog.this.getMainPerspective().getMainApplication().getApplicationSettings(), StepEditorDialog.this.getStepToEdit().getDefaultSettings().getID(), StepEditorDialog.this.getStepToEdit().getName(), StepEditorDialog.this);
/* 126:    */         }
/* 127:    */         catch (IOException ex)
/* 128:    */         {
/* 129:209 */           StepEditorDialog.this.showErrorDialog(ex);
/* 130:    */         }
/* 131:    */       }
/* 132:    */     });
/* 133:    */   }
/* 134:    */   
/* 135:    */   protected void setParentWindow(Window parent)
/* 136:    */   {
/* 137:221 */     this.m_parent = parent;
/* 138:    */   }
/* 139:    */   
/* 140:    */   protected void setClosingListener(ClosingListener c)
/* 141:    */   {
/* 142:230 */     this.m_closingListener = c;
/* 143:    */   }
/* 144:    */   
/* 145:    */   protected boolean isEdited()
/* 146:    */   {
/* 147:239 */     return this.m_isEdited;
/* 148:    */   }
/* 149:    */   
/* 150:    */   protected void setEdited(boolean edited)
/* 151:    */   {
/* 152:248 */     this.m_isEdited = edited;
/* 153:    */   }
/* 154:    */   
/* 155:    */   private void ok()
/* 156:    */   {
/* 157:252 */     setEdited(true);
/* 158:    */     
/* 159:254 */     okPressed();
/* 160:255 */     if (this.m_parent != null) {
/* 161:256 */       this.m_parent.dispose();
/* 162:    */     }
/* 163:259 */     if (this.m_closingListener != null) {
/* 164:260 */       this.m_closingListener.closing();
/* 165:    */     }
/* 166:    */   }
/* 167:    */   
/* 168:    */   protected void okPressed() {}
/* 169:    */   
/* 170:    */   protected void cancelPressed() {}
/* 171:    */   
/* 172:    */   private void cancel()
/* 173:    */   {
/* 174:281 */     setEdited(false);
/* 175:    */     
/* 176:283 */     cancelPressed();
/* 177:284 */     if (this.m_parent != null) {
/* 178:285 */       this.m_parent.dispose();
/* 179:    */     }
/* 180:288 */     if (this.m_closingListener != null) {
/* 181:289 */       this.m_closingListener.closing();
/* 182:    */     }
/* 183:    */   }
/* 184:    */   
/* 185:    */   protected void createAboutPanel(Step step)
/* 186:    */   {
/* 187:299 */     String globalFirstSentence = "";
/* 188:300 */     String globalInfo = Utils.getGlobalInfo(step, false);
/* 189:301 */     if (globalInfo == null)
/* 190:    */     {
/* 191:302 */       globalInfo = "No info available";
/* 192:303 */       globalFirstSentence = globalInfo;
/* 193:    */     }
/* 194:    */     else
/* 195:    */     {
/* 196:305 */       globalInfo = globalInfo.replace("font color=blue", "font color=black");
/* 197:    */       try
/* 198:    */       {
/* 199:307 */         Method gI = step.getClass().getMethod("globalInfo", new Class[0]);
/* 200:308 */         String globalInfoNoHTML = gI.invoke(step, new Object[0]).toString();
/* 201:309 */         globalFirstSentence = globalInfoNoHTML.contains(".") ? globalInfoNoHTML.substring(0, globalInfoNoHTML.indexOf('.')) : globalInfoNoHTML;
/* 202:    */       }
/* 203:    */       catch (Exception ex)
/* 204:    */       {
/* 205:313 */         ex.printStackTrace();
/* 206:    */       }
/* 207:    */     }
/* 208:317 */     createAboutPanel(globalInfo, globalFirstSentence);
/* 209:    */   }
/* 210:    */   
/* 211:    */   private void createAboutPanel(String about, String firstSentence)
/* 212:    */   {
/* 213:321 */     JTextArea jt = new JTextArea();
/* 214:    */     
/* 215:323 */     this.m_helpText.append(about);
/* 216:324 */     jt.setColumns(30);
/* 217:    */     
/* 218:326 */     jt.setFont(new Font("SansSerif", 0, 12));
/* 219:327 */     jt.setEditable(false);
/* 220:328 */     jt.setLineWrap(true);
/* 221:329 */     jt.setWrapStyleWord(true);
/* 222:    */     
/* 223:331 */     jt.setText(firstSentence);
/* 224:332 */     jt.setBackground(getBackground());
/* 225:    */     
/* 226:334 */     String className = this.m_stepToEdit.getClass().getName();
/* 227:335 */     className = className.substring(className.lastIndexOf('.') + 1, className.length());
/* 228:    */     
/* 229:337 */     this.m_helpBut.setToolTipText("More information about " + className);
/* 230:    */     
/* 231:339 */     final JPanel jp = new JPanel();
/* 232:340 */     jp.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("About"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
/* 233:    */     
/* 234:    */ 
/* 235:343 */     jp.setLayout(new BorderLayout());
/* 236:344 */     jp.add(new JScrollPane(jt), "Center");
/* 237:    */     
/* 238:346 */     JPanel p2 = new JPanel();
/* 239:347 */     p2.setLayout(new BorderLayout());
/* 240:348 */     p2.add(this.m_helpBut, "North");
/* 241:349 */     jp.add(p2, "East");
/* 242:    */     
/* 243:351 */     add(jp, "North");
/* 244:    */     
/* 245:353 */     int preferredWidth = jt.getPreferredSize().width;
/* 246:354 */     jt.setSize(new Dimension(Math.min(preferredWidth, 600), 32767));
/* 247:355 */     Dimension d = jt.getPreferredSize();
/* 248:356 */     jt.setPreferredSize(new Dimension(Math.min(preferredWidth, 600), d.height));
/* 249:    */     
/* 250:358 */     this.m_helpBut.addActionListener(new ActionListener()
/* 251:    */     {
/* 252:    */       public void actionPerformed(ActionEvent a)
/* 253:    */       {
/* 254:361 */         StepEditorDialog.this.openHelpFrame(jp);
/* 255:362 */         StepEditorDialog.this.m_helpBut.setEnabled(false);
/* 256:    */       }
/* 257:    */     });
/* 258:    */   }
/* 259:    */   
/* 260:    */   private void openHelpFrame(JPanel aboutPanel)
/* 261:    */   {
/* 262:368 */     JTextPane ta = new JTextPane();
/* 263:369 */     ta.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/* 264:370 */     ta.setContentType("text/html");
/* 265:    */     
/* 266:    */ 
/* 267:    */ 
/* 268:374 */     ta.setEditable(false);
/* 269:375 */     ta.setText(this.m_helpText.toString());
/* 270:376 */     ta.setCaretPosition(0);
/* 271:    */     JDialog jdtmp;
/* 272:    */     JDialog jdtmp;
/* 273:378 */     if (PropertyDialog.getParentDialog(this) != null)
/* 274:    */     {
/* 275:379 */       jdtmp = new JDialog(PropertyDialog.getParentDialog(this), "Information");
/* 276:    */     }
/* 277:    */     else
/* 278:    */     {
/* 279:    */       JDialog jdtmp;
/* 280:380 */       if (PropertyDialog.getParentFrame(this) != null) {
/* 281:381 */         jdtmp = new JDialog(PropertyDialog.getParentFrame(this), "Information");
/* 282:    */       } else {
/* 283:383 */         jdtmp = new JDialog((Frame)null, "Information");
/* 284:    */       }
/* 285:    */     }
/* 286:385 */     final JDialog jd = jdtmp;
/* 287:386 */     jd.addWindowListener(new WindowAdapter()
/* 288:    */     {
/* 289:    */       public void windowClosing(WindowEvent e)
/* 290:    */       {
/* 291:389 */         jd.dispose();
/* 292:390 */         StepEditorDialog.this.m_helpBut.setEnabled(true);
/* 293:    */       }
/* 294:392 */     });
/* 295:393 */     jd.getContentPane().setLayout(new BorderLayout());
/* 296:394 */     jd.getContentPane().add(new JScrollPane(ta), "Center");
/* 297:395 */     jd.pack();
/* 298:396 */     jd.setSize(400, 350);
/* 299:397 */     jd.setLocation(aboutPanel.getTopLevelAncestor().getLocationOnScreen().x + aboutPanel.getTopLevelAncestor().getSize().width, aboutPanel.getTopLevelAncestor().getLocationOnScreen().y);
/* 300:    */     
/* 301:    */ 
/* 302:400 */     jd.setVisible(true);
/* 303:    */   }
/* 304:    */   
/* 305:    */   public Environment getEnvironment()
/* 306:    */   {
/* 307:409 */     return this.m_env;
/* 308:    */   }
/* 309:    */   
/* 310:    */   public void setEnvironment(Environment env)
/* 311:    */   {
/* 312:419 */     this.m_env = env;
/* 313:    */   }
/* 314:    */   
/* 315:    */   public String environmentSubstitute(String source)
/* 316:    */   {
/* 317:430 */     String result = source;
/* 318:431 */     if (result != null) {
/* 319:    */       try
/* 320:    */       {
/* 321:433 */         result = this.m_env.substitute(result);
/* 322:    */       }
/* 323:    */       catch (Exception ex) {}
/* 324:    */     }
/* 325:439 */     return result;
/* 326:    */   }
/* 327:    */   
/* 328:    */   public static abstract interface ClosingListener
/* 329:    */   {
/* 330:    */     public abstract void closing();
/* 331:    */   }
/* 332:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.StepEditorDialog
 * JD-Core Version:    0.7.0.1
 */