/*   1:    */ package weka.gui.experiment;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.GridBagConstraints;
/*   6:    */ import java.awt.GridBagLayout;
/*   7:    */ import java.awt.Insets;
/*   8:    */ import java.awt.event.ActionEvent;
/*   9:    */ import java.awt.event.ActionListener;
/*  10:    */ import java.awt.event.WindowAdapter;
/*  11:    */ import java.awt.event.WindowEvent;
/*  12:    */ import java.io.BufferedInputStream;
/*  13:    */ import java.io.File;
/*  14:    */ import java.io.FileInputStream;
/*  15:    */ import java.io.ObjectInputStream;
/*  16:    */ import java.io.PrintStream;
/*  17:    */ import java.io.Serializable;
/*  18:    */ import javax.swing.BorderFactory;
/*  19:    */ import javax.swing.DefaultListModel;
/*  20:    */ import javax.swing.JButton;
/*  21:    */ import javax.swing.JFrame;
/*  22:    */ import javax.swing.JPanel;
/*  23:    */ import weka.core.Memory;
/*  24:    */ import weka.core.SerializedObject;
/*  25:    */ import weka.core.Utils;
/*  26:    */ import weka.experiment.Experiment;
/*  27:    */ import weka.experiment.RemoteExperiment;
/*  28:    */ import weka.experiment.RemoteExperimentEvent;
/*  29:    */ import weka.experiment.RemoteExperimentListener;
/*  30:    */ import weka.gui.LogPanel;
/*  31:    */ 
/*  32:    */ public class RunPanel
/*  33:    */   extends JPanel
/*  34:    */   implements ActionListener
/*  35:    */ {
/*  36:    */   private static final long serialVersionUID = 1691868018596872051L;
/*  37:    */   protected static final String NOT_RUNNING = "Not running";
/*  38: 68 */   protected JButton m_StartBut = new JButton("Start");
/*  39: 71 */   protected JButton m_StopBut = new JButton("Stop");
/*  40: 73 */   protected LogPanel m_Log = new LogPanel();
/*  41:    */   protected Experiment m_Exp;
/*  42: 79 */   protected Thread m_RunThread = null;
/*  43: 82 */   protected ResultsPanel m_ResultsPanel = null;
/*  44:    */   
/*  45:    */   class ExperimentRunner
/*  46:    */     extends Thread
/*  47:    */     implements Serializable
/*  48:    */   {
/*  49:    */     private static final long serialVersionUID = -5591889874714150118L;
/*  50:    */     Experiment m_ExpCopy;
/*  51:    */     
/*  52:    */     public ExperimentRunner(Experiment exp)
/*  53:    */       throws Exception
/*  54:    */     {
/*  55:100 */       if (exp == null) {
/*  56:101 */         System.err.println("Null experiment!!!");
/*  57:    */       } else {
/*  58:103 */         System.err.println("Running experiment: " + exp.toString());
/*  59:    */       }
/*  60:105 */       System.err.println("Writing experiment copy");
/*  61:106 */       SerializedObject so = new SerializedObject(exp);
/*  62:107 */       System.err.println("Reading experiment copy");
/*  63:108 */       this.m_ExpCopy = ((Experiment)so.getObject());
/*  64:109 */       System.err.println("Made experiment copy");
/*  65:    */     }
/*  66:    */     
/*  67:    */     public void abortExperiment()
/*  68:    */     {
/*  69:113 */       if ((this.m_ExpCopy instanceof RemoteExperiment))
/*  70:    */       {
/*  71:114 */         ((RemoteExperiment)this.m_ExpCopy).abortExperiment();
/*  72:    */         
/*  73:116 */         RunPanel.this.m_StopBut.setEnabled(false);
/*  74:    */       }
/*  75:    */     }
/*  76:    */     
/*  77:    */     public void run()
/*  78:    */     {
/*  79:126 */       RunPanel.this.m_StartBut.setEnabled(false);
/*  80:127 */       RunPanel.this.m_StopBut.setEnabled(true);
/*  81:128 */       if (RunPanel.this.m_ResultsPanel != null) {
/*  82:129 */         RunPanel.this.m_ResultsPanel.setExperiment(null);
/*  83:    */       }
/*  84:    */       try
/*  85:    */       {
/*  86:132 */         if ((this.m_ExpCopy instanceof RemoteExperiment))
/*  87:    */         {
/*  88:134 */           System.err.println("Adding a listener");
/*  89:135 */           ((RemoteExperiment)this.m_ExpCopy).addRemoteExperimentListener(new RemoteExperimentListener()
/*  90:    */           {
/*  91:    */             public void remoteExperimentStatus(RemoteExperimentEvent e)
/*  92:    */             {
/*  93:138 */               if (e.m_statusMessage) {
/*  94:139 */                 RunPanel.this.statusMessage(e.m_messageString);
/*  95:    */               }
/*  96:141 */               if (e.m_logMessage) {
/*  97:142 */                 RunPanel.this.logMessage(e.m_messageString);
/*  98:    */               }
/*  99:144 */               if (e.m_experimentFinished)
/* 100:    */               {
/* 101:145 */                 RunPanel.this.m_RunThread = null;
/* 102:146 */                 RunPanel.this.m_StartBut.setEnabled(true);
/* 103:147 */                 RunPanel.this.m_StopBut.setEnabled(false);
/* 104:148 */                 RunPanel.this.statusMessage("Not running");
/* 105:    */               }
/* 106:    */             }
/* 107:    */           });
/* 108:    */         }
/* 109:153 */         RunPanel.this.logMessage("Started");
/* 110:154 */         RunPanel.this.statusMessage("Initializing...");
/* 111:155 */         this.m_ExpCopy.initialize();
/* 112:156 */         int errors = 0;
/* 113:157 */         if (!(this.m_ExpCopy instanceof RemoteExperiment))
/* 114:    */         {
/* 115:158 */           RunPanel.this.statusMessage("Iterating...");
/* 116:159 */           while ((RunPanel.this.m_RunThread != null) && (this.m_ExpCopy.hasMoreIterations())) {
/* 117:    */             try
/* 118:    */             {
/* 119:161 */               String current = "Iteration:";
/* 120:162 */               if (this.m_ExpCopy.getUsePropertyIterator())
/* 121:    */               {
/* 122:163 */                 int cnum = this.m_ExpCopy.getCurrentPropertyNumber();
/* 123:164 */                 String ctype = this.m_ExpCopy.getPropertyArray().getClass().getComponentType().getName();
/* 124:165 */                 int lastDot = ctype.lastIndexOf('.');
/* 125:166 */                 if (lastDot != -1) {
/* 126:167 */                   ctype = ctype.substring(lastDot + 1);
/* 127:    */                 }
/* 128:169 */                 String cname = " " + ctype + "=" + (cnum + 1) + ":" + this.m_ExpCopy.getPropertyArrayValue(cnum).getClass().getName();
/* 129:    */                 
/* 130:    */ 
/* 131:172 */                 current = current + cname;
/* 132:    */               }
/* 133:174 */               String dname = ((File)this.m_ExpCopy.getDatasets().elementAt(this.m_ExpCopy.getCurrentDatasetNumber())).getName();
/* 134:    */               
/* 135:    */ 
/* 136:177 */               current = current + " Dataset=" + dname + " Run=" + this.m_ExpCopy.getCurrentRunNumber();
/* 137:    */               
/* 138:179 */               RunPanel.this.statusMessage(current);
/* 139:180 */               this.m_ExpCopy.nextIteration();
/* 140:    */             }
/* 141:    */             catch (Exception ex)
/* 142:    */             {
/* 143:182 */               errors++;
/* 144:183 */               RunPanel.this.logMessage(ex.getMessage());
/* 145:184 */               ex.printStackTrace();
/* 146:185 */               boolean continueAfterError = false;
/* 147:186 */               if (continueAfterError) {
/* 148:187 */                 this.m_ExpCopy.advanceCounters();
/* 149:    */               } else {
/* 150:189 */                 RunPanel.this.m_RunThread = null;
/* 151:    */               }
/* 152:    */             }
/* 153:    */           }
/* 154:193 */           RunPanel.this.statusMessage("Postprocessing...");
/* 155:194 */           this.m_ExpCopy.postProcess();
/* 156:195 */           if (RunPanel.this.m_RunThread == null) {
/* 157:196 */             RunPanel.this.logMessage("Interrupted");
/* 158:    */           } else {
/* 159:198 */             RunPanel.this.logMessage("Finished");
/* 160:    */           }
/* 161:200 */           if (errors == 1) {
/* 162:201 */             RunPanel.this.logMessage("There was " + errors + " error");
/* 163:    */           } else {
/* 164:203 */             RunPanel.this.logMessage("There were " + errors + " errors");
/* 165:    */           }
/* 166:205 */           RunPanel.this.statusMessage("Not running");
/* 167:    */         }
/* 168:    */         else
/* 169:    */         {
/* 170:207 */           RunPanel.this.statusMessage("Remote experiment running...");
/* 171:208 */           ((RemoteExperiment)this.m_ExpCopy).runExperiment();
/* 172:    */         }
/* 173:    */       }
/* 174:    */       catch (Exception ex)
/* 175:    */       {
/* 176:211 */         ex.printStackTrace();
/* 177:212 */         System.err.println(ex.getMessage());
/* 178:213 */         RunPanel.this.statusMessage(ex.getMessage());
/* 179:    */       }
/* 180:    */       finally
/* 181:    */       {
/* 182:215 */         if (RunPanel.this.m_ResultsPanel != null) {
/* 183:216 */           RunPanel.this.m_ResultsPanel.setExperiment(this.m_ExpCopy);
/* 184:    */         }
/* 185:218 */         if (!(this.m_ExpCopy instanceof RemoteExperiment))
/* 186:    */         {
/* 187:219 */           RunPanel.this.m_RunThread = null;
/* 188:220 */           RunPanel.this.m_StartBut.setEnabled(true);
/* 189:221 */           RunPanel.this.m_StopBut.setEnabled(false);
/* 190:222 */           System.err.println("Done...");
/* 191:    */         }
/* 192:    */       }
/* 193:    */     }
/* 194:    */   }
/* 195:    */   
/* 196:    */   public void setResultsPanel(ResultsPanel rp)
/* 197:    */   {
/* 198:233 */     this.m_ResultsPanel = rp;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public RunPanel()
/* 202:    */   {
/* 203:241 */     this.m_StartBut.addActionListener(this);
/* 204:242 */     this.m_StopBut.addActionListener(this);
/* 205:243 */     this.m_StartBut.setEnabled(false);
/* 206:244 */     this.m_StopBut.setEnabled(false);
/* 207:245 */     this.m_StartBut.setMnemonic('S');
/* 208:246 */     this.m_StopBut.setMnemonic('t');
/* 209:247 */     this.m_Log.statusMessage("Not running");
/* 210:    */     
/* 211:    */ 
/* 212:250 */     JPanel controls = new JPanel();
/* 213:251 */     GridBagLayout gb = new GridBagLayout();
/* 214:252 */     GridBagConstraints constraints = new GridBagConstraints();
/* 215:253 */     controls.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
/* 216:    */     
/* 217:255 */     controls.setLayout(gb);
/* 218:256 */     constraints.gridx = 0;constraints.gridy = 0;constraints.weightx = 5.0D;
/* 219:257 */     constraints.fill = 2;
/* 220:258 */     constraints.gridwidth = 1;constraints.gridheight = 1;
/* 221:259 */     constraints.insets = new Insets(0, 2, 0, 2);
/* 222:260 */     controls.add(this.m_StartBut, constraints);
/* 223:261 */     constraints.gridx = 1;constraints.gridy = 0;constraints.weightx = 5.0D;
/* 224:262 */     constraints.gridwidth = 1;constraints.gridheight = 1;
/* 225:263 */     controls.add(this.m_StopBut, constraints);
/* 226:264 */     setLayout(new BorderLayout());
/* 227:265 */     add(controls, "North");
/* 228:266 */     add(this.m_Log, "Center");
/* 229:    */   }
/* 230:    */   
/* 231:    */   public RunPanel(Experiment exp)
/* 232:    */   {
/* 233:276 */     this();
/* 234:277 */     setExperiment(exp);
/* 235:    */   }
/* 236:    */   
/* 237:    */   public void setExperiment(Experiment exp)
/* 238:    */   {
/* 239:287 */     this.m_Exp = exp;
/* 240:288 */     this.m_StartBut.setEnabled(this.m_RunThread == null);
/* 241:289 */     this.m_StopBut.setEnabled(this.m_RunThread != null);
/* 242:    */   }
/* 243:    */   
/* 244:    */   public void actionPerformed(ActionEvent e)
/* 245:    */   {
/* 246:299 */     if (e.getSource() == this.m_StartBut)
/* 247:    */     {
/* 248:300 */       if (this.m_RunThread == null)
/* 249:    */       {
/* 250:301 */         boolean proceed = true;
/* 251:302 */         if (Experimenter.m_Memory.memoryIsLow()) {
/* 252:303 */           proceed = Experimenter.m_Memory.showMemoryIsLow();
/* 253:    */         }
/* 254:305 */         if (proceed) {
/* 255:    */           try
/* 256:    */           {
/* 257:307 */             this.m_RunThread = new ExperimentRunner(this.m_Exp);
/* 258:308 */             this.m_RunThread.setPriority(1);
/* 259:309 */             this.m_RunThread.start();
/* 260:    */           }
/* 261:    */           catch (Exception ex)
/* 262:    */           {
/* 263:311 */             ex.printStackTrace();
/* 264:312 */             logMessage("Problem creating experiment copy to run: " + ex.getMessage());
/* 265:    */           }
/* 266:    */         }
/* 267:    */       }
/* 268:    */     }
/* 269:317 */     else if (e.getSource() == this.m_StopBut)
/* 270:    */     {
/* 271:318 */       this.m_StopBut.setEnabled(false);
/* 272:319 */       logMessage("User aborting experiment. ");
/* 273:320 */       if ((this.m_Exp instanceof RemoteExperiment)) {
/* 274:321 */         logMessage("Waiting for remote tasks to complete...");
/* 275:    */       }
/* 276:324 */       ((ExperimentRunner)this.m_RunThread).abortExperiment();
/* 277:    */       
/* 278:326 */       this.m_RunThread = null;
/* 279:    */     }
/* 280:    */   }
/* 281:    */   
/* 282:    */   protected void logMessage(String message)
/* 283:    */   {
/* 284:337 */     this.m_Log.logMessage(message);
/* 285:    */   }
/* 286:    */   
/* 287:    */   protected void statusMessage(String message)
/* 288:    */   {
/* 289:347 */     this.m_Log.statusMessage(message);
/* 290:    */   }
/* 291:    */   
/* 292:    */   public static void main(String[] args)
/* 293:    */   {
/* 294:    */     try
/* 295:    */     {
/* 296:358 */       boolean readExp = Utils.getFlag('l', args);
/* 297:359 */       String expFile = Utils.getOption('f', args);
/* 298:360 */       if ((readExp) && (expFile.length() == 0)) {
/* 299:361 */         throw new Exception("A filename must be given with the -f option");
/* 300:    */       }
/* 301:363 */       Experiment exp = null;
/* 302:364 */       if (readExp)
/* 303:    */       {
/* 304:365 */         FileInputStream fi = new FileInputStream(expFile);
/* 305:366 */         ObjectInputStream oi = new ObjectInputStream(new BufferedInputStream(fi));
/* 306:    */         
/* 307:368 */         Object to = oi.readObject();
/* 308:369 */         if ((to instanceof RemoteExperiment)) {
/* 309:370 */           exp = (RemoteExperiment)to;
/* 310:    */         } else {
/* 311:372 */           exp = (Experiment)to;
/* 312:    */         }
/* 313:374 */         oi.close();
/* 314:    */       }
/* 315:    */       else
/* 316:    */       {
/* 317:376 */         exp = new Experiment();
/* 318:    */       }
/* 319:378 */       System.err.println("Initial Experiment:\n" + exp.toString());
/* 320:379 */       final JFrame jf = new JFrame("Run Weka Experiment");
/* 321:380 */       jf.getContentPane().setLayout(new BorderLayout());
/* 322:381 */       RunPanel sp = new RunPanel(exp);
/* 323:    */       
/* 324:383 */       jf.getContentPane().add(sp, "Center");
/* 325:384 */       jf.addWindowListener(new WindowAdapter()
/* 326:    */       {
/* 327:    */         public void windowClosing(WindowEvent e)
/* 328:    */         {
/* 329:386 */           System.err.println("\nExperiment Configuration\n" + this.val$sp.m_Exp.toString());
/* 330:    */           
/* 331:388 */           jf.dispose();
/* 332:389 */           System.exit(0);
/* 333:    */         }
/* 334:391 */       });
/* 335:392 */       jf.pack();
/* 336:393 */       jf.setVisible(true);
/* 337:    */     }
/* 338:    */     catch (Exception ex)
/* 339:    */     {
/* 340:395 */       ex.printStackTrace();
/* 341:396 */       System.err.println(ex.getMessage());
/* 342:    */     }
/* 343:    */   }
/* 344:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.experiment.RunPanel
 * JD-Core Version:    0.7.0.1
 */