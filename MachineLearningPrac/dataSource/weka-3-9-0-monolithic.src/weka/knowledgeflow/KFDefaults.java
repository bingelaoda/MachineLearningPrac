/*   1:    */ package weka.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.util.Map;
/*   5:    */ import javax.swing.JPanel;
/*   6:    */ import weka.core.Defaults;
/*   7:    */ import weka.core.Settings.SettingKey;
/*   8:    */ 
/*   9:    */ public class KFDefaults
/*  10:    */   extends Defaults
/*  11:    */ {
/*  12:    */   public static final String APP_NAME = "Knowledge Flow";
/*  13:    */   public static final String APP_ID = "knowledgeflow";
/*  14:    */   public static final String MAIN_PERSPECTIVE_ID = "knowledgeflow.main";
/*  15: 42 */   public static final Settings.SettingKey MAX_UNDO_POINTS_KEY = new Settings.SettingKey("knowledgeflow.main.maxUndoPoints", "Maximum undo points", "Maximum number of states to keep in the undobuffer");
/*  16:    */   public static final int MAX_UNDO_POINTS = 20;
/*  17: 48 */   public static final Settings.SettingKey LAYOUT_COLOR_KEY = new Settings.SettingKey("knowledgeflow.main.layoutcolor", "Layout background color", "");
/*  18: 51 */   private static Color JP_COLOR = new JPanel().getBackground();
/*  19: 52 */   public static final Color LAYOUT_COLOR = new Color(JP_COLOR.getRGB());
/*  20: 54 */   public static final Settings.SettingKey SHOW_GRID_KEY = new Settings.SettingKey("knowledgeflow.main.showgrid", "Show grid", "The snap-to-grid grid");
/*  21:    */   public static final boolean SHOW_GRID = false;
/*  22: 60 */   public static final Settings.SettingKey GRID_COLOR_KEY = new Settings.SettingKey("knowledgeflow.main.gridcolor", "Grid line color", "The snap-to-grid line color");
/*  23: 63 */   public static final Color GRID_COLOR = Color.LIGHT_GRAY;
/*  24: 65 */   public static final Settings.SettingKey GRID_SPACING_KEY = new Settings.SettingKey("knowledgeflow.main.gridSpacing", "Grid spacing", "The spacing for snap-to-grid");
/*  25:    */   public static final int GRID_SPACING = 40;
/*  26:    */   public static final int SCROLL_BAR_INCREMENT_LAYOUT = 20;
/*  27: 72 */   public static final Settings.SettingKey LAYOUT_WIDTH_KEY = new Settings.SettingKey("knowledgeflow.main.layoutWidth", "Layout width", "The width (in pixels) of the flow layout");
/*  28: 75 */   public static final Settings.SettingKey LAYOUT_HEIGHT_KEY = new Settings.SettingKey("knowledgeflow.main.layoutHeight", "Layout height", "The height (in pixels) of the flow layout");
/*  29:    */   public static final int LAYOUT_WIDTH = 2560;
/*  30:    */   public static final int LAYOUT_HEIGHT = 1440;
/*  31: 82 */   public static final Settings.SettingKey STEP_LABEL_FONT_SIZE_KEY = new Settings.SettingKey("knowledgeflow.main.stepLabelFontSize", "Font size for step/connection labels", "The point size of the font used to render the names of steps and connections on the layout");
/*  32:    */   public static final int STEP_LABEL_FONT_SIZE = 9;
/*  33: 89 */   public static final Settings.SettingKey LOGGING_LEVEL_KEY = new Settings.SettingKey("knowledgeflow.main.loggingLevel", "Logging level", "The logging level to use");
/*  34: 92 */   public static final LoggingLevel LOGGING_LEVEL = LoggingLevel.BASIC;
/*  35: 94 */   protected static final Settings.SettingKey LOG_MESSAGE_FONT_SIZE_KEY = new Settings.SettingKey("knowledgeflow.main.logMessageFontSize", "Size of font for log messages", "Size of font for log messages (-1 = system default)");
/*  36:    */   protected static final int LOG_MESSAGE_FONT_SIZE = -1;
/*  37:101 */   public static final Settings.SettingKey SHOW_JTREE_TIP_TEXT_KEY = new Settings.SettingKey("knowledgeflow.showGlobalInfoTipText", "Show scheme tool tips in tree view", "");
/*  38:    */   public static final boolean SHOW_JTREE_GLOBAL_INFO_TIPS = true;
/*  39:    */   public static final String LAF = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
/*  40:108 */   protected static final Settings.SettingKey[] DEFAULT_KEYS = { MAX_UNDO_POINTS_KEY, LAYOUT_COLOR_KEY, SHOW_GRID_KEY, GRID_COLOR_KEY, GRID_SPACING_KEY, LAYOUT_WIDTH_KEY, LAYOUT_HEIGHT_KEY, STEP_LABEL_FONT_SIZE_KEY, LOGGING_LEVEL_KEY, LOG_MESSAGE_FONT_SIZE_KEY };
/*  41:112 */   protected static final Object[] DEFAULT_VALUES = { Integer.valueOf(20), LAYOUT_COLOR, Boolean.valueOf(false), GRID_COLOR, Integer.valueOf(40), Integer.valueOf(2560), Integer.valueOf(1440), Integer.valueOf(9), LOGGING_LEVEL, Integer.valueOf(-1) };
/*  42:    */   
/*  43:    */   public KFDefaults()
/*  44:    */   {
/*  45:117 */     super("knowledgeflow.main");
/*  46:118 */     for (int i = 0; i < DEFAULT_KEYS.length; i++) {
/*  47:119 */       this.m_defaults.put(DEFAULT_KEYS[i], DEFAULT_VALUES[i]);
/*  48:    */     }
/*  49:    */   }
/*  50:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.KFDefaults
 * JD-Core Version:    0.7.0.1
 */