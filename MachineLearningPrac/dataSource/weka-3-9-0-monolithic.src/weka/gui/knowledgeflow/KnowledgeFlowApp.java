/*   1:    */ package weka.gui.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Image;
/*   6:    */ import java.awt.Toolkit;
/*   7:    */ import java.io.File;
/*   8:    */ import java.io.IOException;
/*   9:    */ import java.io.PrintStream;
/*  10:    */ import java.util.ArrayList;
/*  11:    */ import java.util.List;
/*  12:    */ import java.util.Map;
/*  13:    */ import java.util.Set;
/*  14:    */ import javax.swing.JFrame;
/*  15:    */ import weka.core.Defaults;
/*  16:    */ import weka.core.Environment;
/*  17:    */ import weka.core.Memory;
/*  18:    */ import weka.core.PluginManager;
/*  19:    */ import weka.core.Settings;
/*  20:    */ import weka.core.Settings.SettingKey;
/*  21:    */ import weka.gui.AbstractGUIApplication;
/*  22:    */ import weka.gui.GenericObjectEditor;
/*  23:    */ import weka.gui.LookAndFeel;
/*  24:    */ import weka.gui.Perspective;
/*  25:    */ import weka.gui.PerspectiveManager;
/*  26:    */ import weka.knowledgeflow.BaseExecutionEnvironment;
/*  27:    */ import weka.knowledgeflow.ExecutionEnvironment;
/*  28:    */ import weka.knowledgeflow.KFDefaults;
/*  29:    */ 
/*  30:    */ public class KnowledgeFlowApp
/*  31:    */   extends AbstractGUIApplication
/*  32:    */ {
/*  33:    */   private static final long serialVersionUID = -1460599392623083983L;
/*  34: 57 */   protected static Memory m_Memory = new Memory(true);
/*  35:    */   protected static KnowledgeFlowApp m_kfApp;
/*  36:    */   protected Settings m_kfProperties;
/*  37:    */   protected MainKFPerspective m_mainPerspective;
/*  38:    */   
/*  39:    */   public KnowledgeFlowApp()
/*  40:    */   {
/*  41: 75 */     this(true);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public KnowledgeFlowApp(boolean layoutComponent)
/*  45:    */   {
/*  46: 87 */     super(layoutComponent, new String[] { "weka.gui.knowledgeflow", "weka.gui.SimpleCLIPanel" });
/*  47:    */     
/*  48:    */ 
/*  49: 90 */     ((MainKFPerspective)this.m_perspectiveManager.getMainPerspective()).addUntitledTab();
/*  50:    */     
/*  51:    */ 
/*  52: 93 */     this.m_perspectiveManager.addSettingsMenuItemToProgramMenu(getApplicationSettings());
/*  53: 96 */     if (this.m_perspectiveManager.userRequestedPerspectiveToolbarVisibleOnStartup(getApplicationSettings())) {
/*  54: 98 */       showPerspectivesToolBar();
/*  55:    */     }
/*  56:    */   }
/*  57:    */   
/*  58:    */   public String getApplicationName()
/*  59:    */   {
/*  60:109 */     return "Knowledge Flow";
/*  61:    */   }
/*  62:    */   
/*  63:    */   public String getApplicationID()
/*  64:    */   {
/*  65:119 */     return "knowledgeflow";
/*  66:    */   }
/*  67:    */   
/*  68:    */   public Perspective getMainPerspective()
/*  69:    */   {
/*  70:129 */     if (this.m_mainPerspective == null) {
/*  71:130 */       this.m_mainPerspective = new MainKFPerspective();
/*  72:    */     }
/*  73:132 */     return this.m_mainPerspective;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public PerspectiveManager getPerspectiveManager()
/*  77:    */   {
/*  78:142 */     return this.m_perspectiveManager;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public Settings getApplicationSettings()
/*  82:    */   {
/*  83:152 */     if (this.m_kfProperties == null)
/*  84:    */     {
/*  85:153 */       this.m_kfProperties = new Settings("weka", "knowledgeflow");
/*  86:154 */       Defaults kfDefaults = new KnowledgeFlowGeneralDefaults();
/*  87:    */       
/*  88:156 */       String envName = (String)this.m_kfProperties.getSetting("knowledgeflow", KnowledgeFlowGeneralDefaults.EXECUTION_ENV_KEY, "Default execution environment", Environment.getSystemWide());
/*  89:    */       try
/*  90:    */       {
/*  91:162 */         ExecutionEnvironment envForDefaults = (ExecutionEnvironment)(envName.equals("Default execution environment") ? new BaseExecutionEnvironment() : PluginManager.getPluginInstance(ExecutionEnvironment.class.getCanonicalName(), envName));
/*  92:    */         
/*  93:    */ 
/*  94:    */ 
/*  95:    */ 
/*  96:    */ 
/*  97:168 */         Defaults envDefaults = envForDefaults.getDefaultSettings();
/*  98:169 */         if (envDefaults != null) {
/*  99:170 */           kfDefaults.add(envDefaults);
/* 100:    */         }
/* 101:    */       }
/* 102:    */       catch (Exception ex)
/* 103:    */       {
/* 104:173 */         ex.printStackTrace();
/* 105:    */       }
/* 106:176 */       this.m_kfProperties.applyDefaults(kfDefaults);
/* 107:    */     }
/* 108:178 */     return this.m_kfProperties;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public Defaults getApplicationDefaults()
/* 112:    */   {
/* 113:188 */     return new KFDefaults();
/* 114:    */   }
/* 115:    */   
/* 116:    */   public void settingsChanged()
/* 117:    */   {
/* 118:196 */     boolean showTipText = ((Boolean)getApplicationSettings().getSetting("knowledgeflow", KFDefaults.SHOW_JTREE_TIP_TEXT_KEY, Boolean.valueOf(true), Environment.getSystemWide())).booleanValue();
/* 119:    */     
/* 120:    */ 
/* 121:    */ 
/* 122:200 */     GenericObjectEditor.setShowGlobalInfoToolTips(showTipText);
/* 123:    */     
/* 124:202 */     this.m_mainPerspective.m_stepTree.setShowLeafTipText(showTipText);
/* 125:    */   }
/* 126:    */   
/* 127:    */   public static class KnowledgeFlowGeneralDefaults
/* 128:    */     extends Defaults
/* 129:    */   {
/* 130:    */     private static final long serialVersionUID = 6957165806947500265L;
/* 131:212 */     public static final Settings.SettingKey LAF_KEY = new Settings.SettingKey("knowledgeflow.lookAndFeel", "Look and feel for UI", "Note: a restart is required for this setting ot come into effect");
/* 132:    */     public static final String LAF = "";
/* 133:217 */     public static final Settings.SettingKey EXECUTION_ENV_KEY = new Settings.SettingKey("knowledgeflow.exec_env", "Execution environment", "Executor for flow processes");
/* 134:    */     public static final String EXECUTION_ENV = "Default execution environment";
/* 135:    */     
/* 136:    */     public KnowledgeFlowGeneralDefaults()
/* 137:    */     {
/* 138:225 */       super();
/* 139:    */       
/* 140:227 */       List<String> lafs = LookAndFeel.getAvailableLookAndFeelClasses();
/* 141:228 */       lafs.add(0, "<use platform default>");
/* 142:229 */       LAF_KEY.setPickList(lafs);
/* 143:230 */       this.m_defaults.put(LAF_KEY, "");
/* 144:231 */       this.m_defaults.put(KFDefaults.SHOW_JTREE_TIP_TEXT_KEY, Boolean.valueOf(true));
/* 145:    */       
/* 146:    */ 
/* 147:234 */       Set<String> execs = PluginManager.getPluginNamesOfType(ExecutionEnvironment.class.getCanonicalName());
/* 148:    */       
/* 149:    */ 
/* 150:237 */       List<String> execList = new ArrayList();
/* 151:    */       
/* 152:239 */       execList.add("Default execution environment");
/* 153:240 */       if (execs != null) {
/* 154:241 */         for (String e : execs) {
/* 155:242 */           if (!e.equals("Default execution environment")) {
/* 156:243 */             execList.add(e);
/* 157:    */           }
/* 158:    */         }
/* 159:    */       }
/* 160:247 */       EXECUTION_ENV_KEY.setPickList(execList);
/* 161:248 */       this.m_defaults.put(EXECUTION_ENV_KEY, "Default execution environment");
/* 162:    */     }
/* 163:    */   }
/* 164:    */   
/* 165:    */   public static void main(String[] args)
/* 166:    */   {
/* 167:    */     try
/* 168:    */     {
/* 169:259 */       LookAndFeel.setLookAndFeel("knowledgeflow", "knowledgeflow.lookAndFeel", "javax.swing.plaf.nimbus.NimbusLookAndFeel");
/* 170:    */     }
/* 171:    */     catch (IOException ex)
/* 172:    */     {
/* 173:262 */       ex.printStackTrace();
/* 174:    */     }
/* 175:264 */     GenericObjectEditor.determineClasses();
/* 176:    */     try
/* 177:    */     {
/* 178:267 */       if (System.getProperty("os.name").contains("Mac")) {
/* 179:268 */         System.setProperty("apple.laf.useScreenMenuBar", "true");
/* 180:    */       }
/* 181:270 */       m_kfApp = new KnowledgeFlowApp();
/* 182:272 */       if (args.length == 1)
/* 183:    */       {
/* 184:273 */         File toLoad = new File(args[0]);
/* 185:274 */         if ((toLoad.exists()) && (toLoad.isFile())) {
/* 186:275 */           ((MainKFPerspective)m_kfApp.getMainPerspective()).loadLayout(toLoad, false);
/* 187:    */         }
/* 188:    */       }
/* 189:279 */       JFrame jf = new JFrame("Weka " + m_kfApp.getApplicationName());
/* 190:    */       
/* 191:281 */       jf.getContentPane().setLayout(new BorderLayout());
/* 192:    */       
/* 193:283 */       Image icon = Toolkit.getDefaultToolkit().getImage(KnowledgeFlowApp.class.getClassLoader().getResource("weka/gui/weka_icon_new_48.png"));
/* 194:    */       
/* 195:    */ 
/* 196:    */ 
/* 197:287 */       jf.setIconImage(icon);
/* 198:    */       
/* 199:289 */       jf.getContentPane().add(m_kfApp, "Center");
/* 200:    */       
/* 201:291 */       jf.setDefaultCloseOperation(3);
/* 202:    */       
/* 203:293 */       jf.pack();
/* 204:294 */       m_kfApp.showMenuBar(jf);
/* 205:295 */       jf.setSize(1023, 768);
/* 206:296 */       jf.setVisible(true);
/* 207:    */       
/* 208:    */ 
/* 209:    */ 
/* 210:    */ 
/* 211:    */ 
/* 212:    */ 
/* 213:    */ 
/* 214:    */ 
/* 215:305 */       jf.setSize(1024, 768);
/* 216:    */       
/* 217:307 */       Thread memMonitor = new Thread()
/* 218:    */       {
/* 219:    */         public void run()
/* 220:    */         {
/* 221:    */           for (;;)
/* 222:    */           {
/* 223:315 */             if (KnowledgeFlowApp.m_Memory.isOutOfMemory())
/* 224:    */             {
/* 225:317 */               this.val$jf.dispose();
/* 226:318 */               KnowledgeFlowApp.m_kfApp = null;
/* 227:319 */               System.gc();
/* 228:    */               
/* 229:    */ 
/* 230:322 */               System.err.println("\ndisplayed message:");
/* 231:323 */               KnowledgeFlowApp.m_Memory.showOutOfMemory();
/* 232:324 */               System.err.println("\nexiting");
/* 233:325 */               System.exit(-1);
/* 234:    */             }
/* 235:    */           }
/* 236:    */         }
/* 237:330 */       };
/* 238:331 */       memMonitor.setPriority(10);
/* 239:332 */       memMonitor.start();
/* 240:    */     }
/* 241:    */     catch (Exception ex)
/* 242:    */     {
/* 243:334 */       ex.printStackTrace();
/* 244:    */     }
/* 245:    */   }
/* 246:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.KnowledgeFlowApp
 * JD-Core Version:    0.7.0.1
 */