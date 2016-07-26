/*  1:   */ package weka.gui;
/*  2:   */ 
/*  3:   */ import java.util.List;
/*  4:   */ import java.util.Map;
/*  5:   */ import weka.core.Defaults;
/*  6:   */ import weka.core.Settings.SettingKey;
/*  7:   */ 
/*  8:   */ public class WorkbenchDefaults
/*  9:   */   extends Defaults
/* 10:   */ {
/* 11:   */   public static final String APP_NAME = "Workbench";
/* 12:   */   public static final String APP_ID = "workbench";
/* 13:40 */   protected static final Settings.SettingKey LAF_KEY = new Settings.SettingKey("workbench.lookAndFeel", "Look and feel for UI", "Note: a restart is required for this setting to come into effect");
/* 14:   */   protected static final String LAF = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
/* 15:44 */   protected static final Settings.SettingKey SHOW_JTREE_TIP_TEXT_KEY = new Settings.SettingKey("workbench.showGlobalInfoTipText", "Show scheme tool tips in tree view", "");
/* 16:   */   protected static final boolean SHOW_JTREE_GLOBAL_INFO_TIPS = true;
/* 17:48 */   protected static final Settings.SettingKey LOG_MESSAGE_FONT_SIZE_KEY = new Settings.SettingKey("workbench.logMessageFontSize", "Size of font for log messages", "Size of font for log messages (-1 = system default)");
/* 18:   */   protected static final int LOG_MESSAGE_FONT_SIZE = -1;
/* 19:   */   private static final long serialVersionUID = 7881327795923189743L;
/* 20:   */   
/* 21:   */   public WorkbenchDefaults()
/* 22:   */   {
/* 23:59 */     super("workbench");
/* 24:   */     
/* 25:61 */     List<String> lafs = LookAndFeel.getAvailableLookAndFeelClasses();
/* 26:62 */     lafs.add(0, "<use platform default>");
/* 27:63 */     LAF_KEY.setPickList(lafs);
/* 28:64 */     this.m_defaults.put(LAF_KEY, "javax.swing.plaf.nimbus.NimbusLookAndFeel");
/* 29:65 */     this.m_defaults.put(SHOW_JTREE_TIP_TEXT_KEY, Boolean.valueOf(true));
/* 30:66 */     this.m_defaults.put(LOG_MESSAGE_FONT_SIZE_KEY, Integer.valueOf(-1));
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.WorkbenchDefaults
 * JD-Core Version:    0.7.0.1
 */