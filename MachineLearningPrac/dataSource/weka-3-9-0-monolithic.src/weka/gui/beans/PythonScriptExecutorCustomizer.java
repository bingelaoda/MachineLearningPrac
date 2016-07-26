/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.GridLayout;
/*   6:    */ import java.awt.Window;
/*   7:    */ import java.awt.event.ActionEvent;
/*   8:    */ import java.awt.event.ActionListener;
/*   9:    */ import java.io.BufferedReader;
/*  10:    */ import java.io.BufferedWriter;
/*  11:    */ import java.io.FileReader;
/*  12:    */ import java.io.FileWriter;
/*  13:    */ import javax.swing.BorderFactory;
/*  14:    */ import javax.swing.JButton;
/*  15:    */ import javax.swing.JCheckBox;
/*  16:    */ import javax.swing.JDialog;
/*  17:    */ import javax.swing.JEditorPane;
/*  18:    */ import javax.swing.JFileChooser;
/*  19:    */ import javax.swing.JLabel;
/*  20:    */ import javax.swing.JMenu;
/*  21:    */ import javax.swing.JMenuBar;
/*  22:    */ import javax.swing.JMenuItem;
/*  23:    */ import javax.swing.JOptionPane;
/*  24:    */ import javax.swing.JPanel;
/*  25:    */ import javax.swing.JScrollPane;
/*  26:    */ import javax.swing.KeyStroke;
/*  27:    */ import javax.swing.text.BadLocationException;
/*  28:    */ import javax.swing.text.Document;
/*  29:    */ import jsyntaxpane.DefaultSyntaxKit;
/*  30:    */ import weka.core.Environment;
/*  31:    */ import weka.core.EnvironmentHandler;
/*  32:    */ import weka.gui.EnvironmentField;
/*  33:    */ import weka.gui.FileEnvironmentField;
/*  34:    */ import weka.gui.PropertySheetPanel;
/*  35:    */ import weka.python.PythonSession;
/*  36:    */ 
/*  37:    */ public class PythonScriptExecutorCustomizer
/*  38:    */   extends JPanel
/*  39:    */   implements BeanCustomizer, EnvironmentHandler, CustomizerCloseRequester
/*  40:    */ {
/*  41:    */   public static final String PROPERTIES_FILE = "weka/gui/scripting/Jython.props";
/*  42:    */   private static final long serialVersionUID = 2816018471336846833L;
/*  43: 76 */   protected BeanCustomizer.ModifyListener m_modifyL = null;
/*  44: 77 */   protected Environment m_env = Environment.getSystemWide();
/*  45:    */   protected Window m_parent;
/*  46:    */   protected PythonScriptExecutor m_executor;
/*  47:    */   protected JEditorPane m_scriptEditor;
/*  48:    */   protected FileEnvironmentField m_scriptLoader;
/*  49: 88 */   private boolean m_pyAvailable = true;
/*  50: 91 */   protected PropertySheetPanel m_tempEditor = new PropertySheetPanel();
/*  51:    */   protected JMenuBar m_menuBar;
/*  52: 97 */   protected EnvironmentField m_outputVarsField = new EnvironmentField();
/*  53:100 */   protected JCheckBox m_outputDebugInfo = new JCheckBox();
/*  54:    */   
/*  55:    */   public PythonScriptExecutorCustomizer()
/*  56:    */   {
/*  57:103 */     setLayout(new BorderLayout());
/*  58:    */   }
/*  59:    */   
/*  60:    */   private void setup()
/*  61:    */   {
/*  62:108 */     DefaultSyntaxKit.initKit();
/*  63:109 */     this.m_scriptEditor = new JEditorPane();
/*  64:    */     
/*  65:    */ 
/*  66:112 */     this.m_pyAvailable = true;
/*  67:113 */     String envEvalResults = null;
/*  68:114 */     Exception envEvalEx = null;
/*  69:115 */     if (!PythonSession.pythonAvailable()) {
/*  70:    */       try
/*  71:    */       {
/*  72:118 */         if (!PythonSession.initSession("python", this.m_executor.getDebug()))
/*  73:    */         {
/*  74:119 */           envEvalResults = PythonSession.getPythonEnvCheckResults();
/*  75:120 */           this.m_pyAvailable = false;
/*  76:    */         }
/*  77:    */       }
/*  78:    */       catch (Exception ex)
/*  79:    */       {
/*  80:123 */         envEvalEx = ex;
/*  81:124 */         this.m_pyAvailable = false;
/*  82:125 */         ex.printStackTrace();
/*  83:    */       }
/*  84:    */     }
/*  85:129 */     JPanel editorPan = new JPanel();
/*  86:130 */     editorPan.setLayout(new BorderLayout());
/*  87:    */     
/*  88:132 */     JPanel topHolder = new JPanel(new BorderLayout());
/*  89:133 */     JScrollPane editorScroller = new JScrollPane(this.m_scriptEditor);
/*  90:134 */     this.m_scriptEditor.setContentType("text/python");
/*  91:135 */     editorScroller.setBorder(BorderFactory.createTitledBorder("Python Script"));
/*  92:136 */     topHolder.add(editorScroller, "North");
/*  93:137 */     editorPan.add(topHolder, "North");
/*  94:138 */     add(editorPan, "Center");
/*  95:139 */     Dimension d = new Dimension(450, 200);
/*  96:140 */     this.m_scriptEditor.setMinimumSize(d);
/*  97:141 */     this.m_scriptEditor.setPreferredSize(d);
/*  98:    */     try
/*  99:    */     {
/* 100:144 */       if (this.m_pyAvailable)
/* 101:    */       {
/* 102:145 */         this.m_scriptEditor.setText(this.m_executor.getPythonScript());
/* 103:    */       }
/* 104:    */       else
/* 105:    */       {
/* 106:147 */         String message = "Python does not seem to be available:\n\n" + ((envEvalResults != null) && (envEvalResults.length() > 0) ? envEvalResults : envEvalEx.getMessage());
/* 107:    */         
/* 108:    */ 
/* 109:    */ 
/* 110:151 */         this.m_scriptEditor.getDocument().insertString(0, message, null);
/* 111:    */       }
/* 112:    */     }
/* 113:    */     catch (BadLocationException ex)
/* 114:    */     {
/* 115:154 */       ex.printStackTrace();
/* 116:    */     }
/* 117:157 */     JPanel varsPanel = new JPanel(new GridLayout(0, 2));
/* 118:158 */     JLabel varsLab = new JLabel("Variables to get from python", 4);
/* 119:    */     
/* 120:160 */     varsLab.setToolTipText("Output these variables from python");
/* 121:161 */     varsPanel.add(varsLab);
/* 122:162 */     varsPanel.add(this.m_outputVarsField);
/* 123:163 */     if (this.m_executor.getVariablesToGetFromPython() != null) {
/* 124:164 */       this.m_outputVarsField.setText(this.m_executor.getVariablesToGetFromPython());
/* 125:    */     }
/* 126:166 */     topHolder.add(varsPanel, "South");
/* 127:    */     
/* 128:168 */     JPanel scriptLoaderP = new JPanel();
/* 129:169 */     scriptLoaderP.setLayout(new GridLayout(0, 2));
/* 130:170 */     JLabel loadL = new JLabel("Load script file", 4);
/* 131:171 */     loadL.setToolTipText("Load script file at run-time - overides editor script");
/* 132:    */     
/* 133:173 */     varsPanel.add(loadL);
/* 134:174 */     this.m_scriptLoader = new FileEnvironmentField(this.m_env);
/* 135:175 */     if ((this.m_executor.getScriptFile() != null) && (this.m_executor.getScriptFile().length() > 0)) {
/* 136:177 */       this.m_scriptLoader.setText(this.m_executor.getScriptFile());
/* 137:    */     }
/* 138:179 */     varsPanel.add(this.m_scriptLoader);
/* 139:    */     
/* 140:181 */     JLabel debugL = new JLabel("Output debugging info", 4);
/* 141:182 */     varsPanel.add(debugL);
/* 142:183 */     varsPanel.add(this.m_outputDebugInfo);
/* 143:184 */     this.m_outputDebugInfo.setSelected(this.m_executor.getDebug());
/* 144:    */     
/* 145:186 */     JPanel aboutP = this.m_tempEditor.getAboutPanel();
/* 146:187 */     add(aboutP, "North");
/* 147:    */     
/* 148:189 */     addButtons();
/* 149:    */     
/* 150:191 */     final JFileChooser fileChooser = new JFileChooser();
/* 151:192 */     fileChooser.setAcceptAllFileFilterUsed(true);
/* 152:193 */     fileChooser.setMultiSelectionEnabled(false);
/* 153:    */     
/* 154:195 */     this.m_menuBar = new JMenuBar();
/* 155:    */     
/* 156:197 */     JMenu fileM = new JMenu();
/* 157:198 */     this.m_menuBar.add(fileM);
/* 158:199 */     fileM.setText("File");
/* 159:200 */     fileM.setMnemonic('F');
/* 160:    */     
/* 161:202 */     JMenuItem newItem = new JMenuItem();
/* 162:203 */     fileM.add(newItem);
/* 163:204 */     newItem.setText("New");
/* 164:205 */     newItem.setAccelerator(KeyStroke.getKeyStroke(78, 2));
/* 165:    */     
/* 166:    */ 
/* 167:208 */     newItem.addActionListener(new ActionListener()
/* 168:    */     {
/* 169:    */       public void actionPerformed(ActionEvent e)
/* 170:    */       {
/* 171:211 */         PythonScriptExecutorCustomizer.this.m_scriptEditor.setText("");
/* 172:    */       }
/* 173:214 */     });
/* 174:215 */     JMenuItem loadItem = new JMenuItem();
/* 175:216 */     fileM.add(loadItem);
/* 176:217 */     loadItem.setText("Open File...");
/* 177:218 */     loadItem.setAccelerator(KeyStroke.getKeyStroke(79, 2));
/* 178:    */     
/* 179:220 */     loadItem.addActionListener(new ActionListener()
/* 180:    */     {
/* 181:    */       public void actionPerformed(ActionEvent e)
/* 182:    */       {
/* 183:223 */         int retVal = fileChooser.showOpenDialog(PythonScriptExecutorCustomizer.this);
/* 184:225 */         if (retVal == 0)
/* 185:    */         {
/* 186:227 */           StringBuilder sb = new StringBuilder();
/* 187:    */           try
/* 188:    */           {
/* 189:229 */             BufferedReader br = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
/* 190:    */             String line;
/* 191:232 */             while ((line = br.readLine()) != null) {
/* 192:233 */               sb.append(line).append("\n");
/* 193:    */             }
/* 194:237 */             PythonScriptExecutorCustomizer.this.m_scriptEditor.setText(sb.toString());
/* 195:238 */             br.close();
/* 196:    */           }
/* 197:    */           catch (Exception ex)
/* 198:    */           {
/* 199:240 */             JOptionPane.showMessageDialog(PythonScriptExecutorCustomizer.this, "Couldn't open file '" + fileChooser.getSelectedFile() + "'!");
/* 200:    */             
/* 201:242 */             ex.printStackTrace();
/* 202:    */           }
/* 203:    */         }
/* 204:    */       }
/* 205:247 */     });
/* 206:248 */     JMenuItem saveAsItem = new JMenuItem();
/* 207:249 */     fileM.add(saveAsItem);
/* 208:    */     
/* 209:251 */     saveAsItem.setText("Save As...");
/* 210:252 */     saveAsItem.setAccelerator(KeyStroke.getKeyStroke(65, 2));
/* 211:    */     
/* 212:254 */     saveAsItem.addActionListener(new ActionListener()
/* 213:    */     {
/* 214:    */       public void actionPerformed(ActionEvent e)
/* 215:    */       {
/* 216:258 */         if ((PythonScriptExecutorCustomizer.this.m_scriptEditor.getText() != null) && (PythonScriptExecutorCustomizer.this.m_scriptEditor.getText().length() > 0))
/* 217:    */         {
/* 218:261 */           int retVal = fileChooser.showSaveDialog(PythonScriptExecutorCustomizer.this);
/* 219:263 */           if (retVal == 0) {
/* 220:    */             try
/* 221:    */             {
/* 222:265 */               BufferedWriter bw = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()));
/* 223:    */               
/* 224:    */ 
/* 225:268 */               bw.write(PythonScriptExecutorCustomizer.this.m_scriptEditor.getText());
/* 226:269 */               bw.flush();
/* 227:270 */               bw.close();
/* 228:    */             }
/* 229:    */             catch (Exception ex)
/* 230:    */             {
/* 231:272 */               JOptionPane.showMessageDialog(PythonScriptExecutorCustomizer.this, "Unable to save script file '" + fileChooser.getSelectedFile() + "'!");
/* 232:    */               
/* 233:    */ 
/* 234:    */ 
/* 235:276 */               ex.printStackTrace();
/* 236:    */             }
/* 237:    */           }
/* 238:    */         }
/* 239:    */       }
/* 240:    */     });
/* 241:    */   }
/* 242:    */   
/* 243:    */   private void addButtons()
/* 244:    */   {
/* 245:285 */     JButton okBut = new JButton("OK");
/* 246:286 */     JButton cancelBut = new JButton("Cancel");
/* 247:    */     
/* 248:288 */     JPanel butHolder = new JPanel();
/* 249:289 */     butHolder.setLayout(new GridLayout(1, 2));
/* 250:290 */     butHolder.add(okBut);
/* 251:291 */     butHolder.add(cancelBut);
/* 252:292 */     add(butHolder, "South");
/* 253:    */     
/* 254:294 */     okBut.addActionListener(new ActionListener()
/* 255:    */     {
/* 256:    */       public void actionPerformed(ActionEvent e)
/* 257:    */       {
/* 258:297 */         PythonScriptExecutorCustomizer.this.closingOK();
/* 259:    */         
/* 260:299 */         PythonScriptExecutorCustomizer.this.m_parent.dispose();
/* 261:    */       }
/* 262:302 */     });
/* 263:303 */     cancelBut.addActionListener(new ActionListener()
/* 264:    */     {
/* 265:    */       public void actionPerformed(ActionEvent e)
/* 266:    */       {
/* 267:306 */         PythonScriptExecutorCustomizer.this.closingCancel();
/* 268:    */         
/* 269:308 */         PythonScriptExecutorCustomizer.this.m_parent.dispose();
/* 270:    */       }
/* 271:    */     });
/* 272:    */   }
/* 273:    */   
/* 274:    */   public void closingOK()
/* 275:    */   {
/* 276:314 */     if (!this.m_pyAvailable) {
/* 277:315 */       return;
/* 278:    */     }
/* 279:318 */     if ((!this.m_scriptEditor.getText().equals(this.m_executor.getPythonScript())) && 
/* 280:319 */       (this.m_modifyL != null)) {
/* 281:320 */       this.m_modifyL.setModifiedStatus(this, true);
/* 282:    */     }
/* 283:324 */     if ((!this.m_scriptLoader.getText().equals(this.m_executor.getScriptFile())) && 
/* 284:325 */       (this.m_modifyL != null)) {
/* 285:326 */       this.m_modifyL.setModifiedStatus(this, true);
/* 286:    */     }
/* 287:330 */     this.m_executor.setPythonScript(this.m_scriptEditor.getText());
/* 288:331 */     this.m_executor.setScriptFile(this.m_scriptLoader.getText());
/* 289:332 */     this.m_executor.setVariablesToGetFromPython(this.m_outputVarsField.getText());
/* 290:333 */     this.m_executor.setDebug(this.m_outputDebugInfo.isSelected());
/* 291:    */   }
/* 292:    */   
/* 293:    */   public void closingCancel() {}
/* 294:    */   
/* 295:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/* 296:    */   {
/* 297:342 */     this.m_modifyL = l;
/* 298:    */   }
/* 299:    */   
/* 300:    */   public void setObject(Object bean)
/* 301:    */   {
/* 302:347 */     if ((bean instanceof PythonScriptExecutor))
/* 303:    */     {
/* 304:348 */       this.m_executor = ((PythonScriptExecutor)bean);
/* 305:349 */       this.m_tempEditor.setTarget(bean);
/* 306:350 */       setup();
/* 307:    */     }
/* 308:    */   }
/* 309:    */   
/* 310:    */   public void setParentWindow(Window parent)
/* 311:    */   {
/* 312:356 */     this.m_parent = parent;
/* 313:358 */     if ((parent instanceof JDialog))
/* 314:    */     {
/* 315:359 */       ((JDialog)this.m_parent).setJMenuBar(this.m_menuBar);
/* 316:360 */       ((JDialog)this.m_parent).setTitle("Python Script Editor");
/* 317:    */     }
/* 318:    */   }
/* 319:    */   
/* 320:    */   public void setEnvironment(Environment env)
/* 321:    */   {
/* 322:366 */     this.m_env = env;
/* 323:    */   }
/* 324:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.PythonScriptExecutorCustomizer
 * JD-Core Version:    0.7.0.1
 */