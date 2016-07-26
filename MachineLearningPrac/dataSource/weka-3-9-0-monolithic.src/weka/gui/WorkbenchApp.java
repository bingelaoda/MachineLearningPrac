/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Image;
/*   6:    */ import java.awt.Toolkit;
/*   7:    */ import java.io.File;
/*   8:    */ import java.io.IOException;
/*   9:    */ import java.io.PrintStream;
/*  10:    */ import java.util.List;
/*  11:    */ import javax.swing.JFrame;
/*  12:    */ import weka.core.Capabilities;
/*  13:    */ import weka.core.Defaults;
/*  14:    */ import weka.core.Environment;
/*  15:    */ import weka.core.Memory;
/*  16:    */ import weka.core.Settings;
/*  17:    */ import weka.core.converters.AbstractFileLoader;
/*  18:    */ import weka.core.converters.ConverterUtils;
/*  19:    */ import weka.gui.explorer.Explorer.CapabilitiesFilterChangeEvent;
/*  20:    */ import weka.gui.explorer.Explorer.CapabilitiesFilterChangeListener;
/*  21:    */ import weka.gui.explorer.PreprocessPanel;
/*  22:    */ import weka.gui.knowledgeflow.AttributeSummaryPerspective;
/*  23:    */ import weka.gui.knowledgeflow.SQLViewerPerspective;
/*  24:    */ import weka.gui.knowledgeflow.ScatterPlotMatrixPerspective;
/*  25:    */ 
/*  26:    */ public class WorkbenchApp
/*  27:    */   extends AbstractGUIApplication
/*  28:    */ {
/*  29:    */   private static final long serialVersionUID = -2357486011273897728L;
/*  30: 52 */   protected static Memory m_Memory = new Memory(true);
/*  31:    */   protected static WorkbenchApp m_workbench;
/*  32:    */   protected PreprocessPanel m_mainPerspective;
/*  33:    */   protected Settings m_workbenchSettings;
/*  34:    */   
/*  35:    */   public WorkbenchApp()
/*  36:    */   {
/*  37: 70 */     super(true, new String[0], new String[] { AttributeSummaryPerspective.class.getCanonicalName(), ScatterPlotMatrixPerspective.class.getCanonicalName(), SQLViewerPerspective.class.getCanonicalName() });
/*  38:    */     
/*  39:    */ 
/*  40:    */ 
/*  41:    */ 
/*  42:    */ 
/*  43: 76 */     this.m_perspectiveManager.addSettingsMenuItemToProgramMenu(getApplicationSettings());
/*  44:    */     
/*  45: 78 */     showPerspectivesToolBar();
/*  46:    */     
/*  47: 80 */     List<Perspective> perspectives = this.m_perspectiveManager.getLoadedPerspectives();
/*  48: 82 */     for (Perspective p : perspectives) {
/*  49: 83 */       this.m_perspectiveManager.setEnablePerspectiveTab(p.getPerspectiveID(), p.okToBeActive());
/*  50:    */     }
/*  51:    */   }
/*  52:    */   
/*  53:    */   public String getApplicationName()
/*  54:    */   {
/*  55: 95 */     return "Workbench";
/*  56:    */   }
/*  57:    */   
/*  58:    */   public String getApplicationID()
/*  59:    */   {
/*  60:105 */     return "workbench";
/*  61:    */   }
/*  62:    */   
/*  63:    */   public Perspective getMainPerspective()
/*  64:    */   {
/*  65:116 */     if (this.m_mainPerspective == null) {
/*  66:117 */       this.m_mainPerspective = new PreprocessPanel();
/*  67:    */     }
/*  68:119 */     return this.m_mainPerspective;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void settingsChanged()
/*  72:    */   {
/*  73:127 */     GenericObjectEditor.setShowGlobalInfoToolTips(((Boolean)getApplicationSettings().getSetting("workbench", WorkbenchDefaults.SHOW_JTREE_TIP_TEXT_KEY, Boolean.valueOf(true), Environment.getSystemWide())).booleanValue());
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void notifyCapabilitiesFilterListeners(Capabilities filter)
/*  77:    */   {
/*  78:140 */     for (Perspective p : getPerspectiveManager().getVisiblePerspectives()) {
/*  79:141 */       if ((p instanceof Explorer.CapabilitiesFilterChangeListener)) {
/*  80:142 */         ((Explorer.CapabilitiesFilterChangeListener)p).capabilitiesFilterChanged(new Explorer.CapabilitiesFilterChangeEvent(this, filter));
/*  81:    */       }
/*  82:    */     }
/*  83:    */   }
/*  84:    */   
/*  85:    */   public Defaults getApplicationDefaults()
/*  86:    */   {
/*  87:156 */     return new WorkbenchDefaults();
/*  88:    */   }
/*  89:    */   
/*  90:    */   public static void main(String[] args)
/*  91:    */   {
/*  92:    */     try
/*  93:    */     {
/*  94:166 */       LookAndFeel.setLookAndFeel("workbench", "workbench.lookAndFeel", "javax.swing.plaf.nimbus.NimbusLookAndFeel");
/*  95:    */     }
/*  96:    */     catch (IOException ex)
/*  97:    */     {
/*  98:169 */       ex.printStackTrace();
/*  99:    */     }
/* 100:171 */     GenericObjectEditor.determineClasses();
/* 101:    */     try
/* 102:    */     {
/* 103:174 */       if (System.getProperty("os.name").contains("Mac")) {
/* 104:175 */         System.setProperty("apple.laf.useScreenMenuBar", "true");
/* 105:    */       }
/* 106:177 */       m_workbench = new WorkbenchApp();
/* 107:178 */       JFrame jf = new JFrame("Weka " + m_workbench.getApplicationName());
/* 108:    */       
/* 109:180 */       jf.getContentPane().setLayout(new BorderLayout());
/* 110:    */       
/* 111:182 */       Image icon = Toolkit.getDefaultToolkit().getImage(WorkbenchApp.class.getClassLoader().getResource("weka/gui/weka_icon_new_48.png"));
/* 112:    */       
/* 113:    */ 
/* 114:    */ 
/* 115:186 */       jf.setIconImage(icon);
/* 116:    */       
/* 117:188 */       jf.getContentPane().add(m_workbench, "Center");
/* 118:189 */       jf.setDefaultCloseOperation(3);
/* 119:190 */       jf.pack();
/* 120:191 */       m_workbench.showMenuBar(jf);
/* 121:192 */       jf.setSize(1024, 768);
/* 122:193 */       jf.setVisible(true);
/* 123:195 */       if (args.length == 1)
/* 124:    */       {
/* 125:196 */         System.err.println("Loading instances from " + args[0]);
/* 126:197 */         AbstractFileLoader loader = ConverterUtils.getLoaderForFile(args[0]);
/* 127:198 */         loader.setFile(new File(args[0]));
/* 128:199 */         m_workbench.getPerspectiveManager().getMainPerspective().setInstances(loader.getDataSet());
/* 129:    */       }
/* 130:203 */       Thread memMonitor = new Thread()
/* 131:    */       {
/* 132:    */         public void run()
/* 133:    */         {
/* 134:    */           for (;;)
/* 135:    */           {
/* 136:211 */             if (WorkbenchApp.m_Memory.isOutOfMemory())
/* 137:    */             {
/* 138:213 */               this.val$jf.dispose();
/* 139:214 */               WorkbenchApp.m_workbench = null;
/* 140:215 */               System.gc();
/* 141:    */               
/* 142:    */ 
/* 143:218 */               System.err.println("\ndisplayed message:");
/* 144:219 */               WorkbenchApp.m_Memory.showOutOfMemory();
/* 145:220 */               System.err.println("\nexiting");
/* 146:221 */               System.exit(-1);
/* 147:    */             }
/* 148:    */           }
/* 149:    */         }
/* 150:226 */       };
/* 151:227 */       memMonitor.setPriority(10);
/* 152:228 */       memMonitor.start();
/* 153:    */     }
/* 154:    */     catch (Exception ex)
/* 155:    */     {
/* 156:230 */       ex.printStackTrace();
/* 157:    */     }
/* 158:    */   }
/* 159:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.WorkbenchApp
 * JD-Core Version:    0.7.0.1
 */