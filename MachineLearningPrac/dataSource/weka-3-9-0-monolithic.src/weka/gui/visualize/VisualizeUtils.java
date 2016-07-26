/*   1:    */ package weka.gui.visualize;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.Map;
/*   6:    */ import java.util.Properties;
/*   7:    */ import javax.swing.JOptionPane;
/*   8:    */ import weka.core.Defaults;
/*   9:    */ import weka.core.Settings.SettingKey;
/*  10:    */ import weka.core.Utils;
/*  11:    */ 
/*  12:    */ public class VisualizeUtils
/*  13:    */ {
/*  14: 42 */   protected static String PROPERTY_FILE = "weka/gui/visualize/Visualize.props";
/*  15:    */   protected static Properties VISUALIZE_PROPERTIES;
/*  16: 48 */   protected static int MAX_PRECISION = 10;
/*  17:    */   
/*  18:    */   static
/*  19:    */   {
/*  20:    */     try
/*  21:    */     {
/*  22: 53 */       VISUALIZE_PROPERTIES = Utils.readProperties(PROPERTY_FILE);
/*  23: 54 */       String precision = VISUALIZE_PROPERTIES.getProperty("weka.gui.visualize.precision");
/*  24: 56 */       if (precision != null) {
/*  25: 63 */         MAX_PRECISION = Integer.parseInt(precision);
/*  26:    */       }
/*  27:    */     }
/*  28:    */     catch (Exception ex)
/*  29:    */     {
/*  30: 67 */       JOptionPane.showMessageDialog(null, "VisualizeUtils: Could not read a visualization configuration file.\nAn example file is included in the Weka distribution.\nThis file should be named \"" + PROPERTY_FILE + "\"  and\n" + "should be placed either in your user home (which is set\n" + "to \"" + System.getProperties().getProperty("user.home") + "\")\n" + "or the directory that java was started from\n", "Plot2D", 0);
/*  31:    */     }
/*  32:    */   }
/*  33:    */   
/*  34:    */   public static Color processColour(String colourDef, Color defaultColour)
/*  35:    */   {
/*  36: 86 */     String colourDefBack = new String(colourDef);
/*  37: 87 */     Color retC = defaultColour;
/*  38: 88 */     if (colourDef.indexOf(",") >= 0) {
/*  39:    */       try
/*  40:    */       {
/*  41: 91 */         int index = colourDef.indexOf(",");
/*  42: 92 */         int R = Integer.parseInt(colourDef.substring(0, index));
/*  43: 93 */         colourDef = colourDef.substring(index + 1, colourDef.length());
/*  44: 94 */         index = colourDef.indexOf(",");
/*  45: 95 */         int G = Integer.parseInt(colourDef.substring(0, index));
/*  46: 96 */         colourDef = colourDef.substring(index + 1, colourDef.length());
/*  47: 97 */         int B = Integer.parseInt(colourDef);
/*  48:    */         
/*  49: 99 */         retC = new Color(R, G, B);
/*  50:    */       }
/*  51:    */       catch (Exception ex)
/*  52:    */       {
/*  53:101 */         System.err.println("VisualizeUtils: Problem parsing colour property value (" + colourDefBack + ").");
/*  54:    */       }
/*  55:106 */     } else if (colourDef.compareTo("black") == 0) {
/*  56:107 */       retC = Color.black;
/*  57:108 */     } else if (colourDef.compareTo("blue") == 0) {
/*  58:109 */       retC = Color.blue;
/*  59:110 */     } else if (colourDef.compareTo("cyan") == 0) {
/*  60:111 */       retC = Color.cyan;
/*  61:112 */     } else if (colourDef.compareTo("darkGray") == 0) {
/*  62:113 */       retC = Color.darkGray;
/*  63:114 */     } else if (colourDef.compareTo("gray") == 0) {
/*  64:115 */       retC = Color.gray;
/*  65:116 */     } else if (colourDef.compareTo("green") == 0) {
/*  66:117 */       retC = Color.green;
/*  67:118 */     } else if (colourDef.compareTo("lightGray") == 0) {
/*  68:119 */       retC = Color.lightGray;
/*  69:120 */     } else if (colourDef.compareTo("magenta") == 0) {
/*  70:121 */       retC = Color.magenta;
/*  71:122 */     } else if (colourDef.compareTo("orange") == 0) {
/*  72:123 */       retC = Color.orange;
/*  73:124 */     } else if (colourDef.compareTo("pink") == 0) {
/*  74:125 */       retC = Color.pink;
/*  75:126 */     } else if (colourDef.compareTo("red") == 0) {
/*  76:127 */       retC = Color.red;
/*  77:128 */     } else if (colourDef.compareTo("white") == 0) {
/*  78:129 */       retC = Color.white;
/*  79:130 */     } else if (colourDef.compareTo("yellow") == 0) {
/*  80:131 */       retC = Color.yellow;
/*  81:    */     } else {
/*  82:133 */       System.err.println("VisualizeUtils: colour property name not recognized (" + colourDefBack + ").");
/*  83:    */     }
/*  84:138 */     return retC;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public static class VisualizeDefaults
/*  88:    */     extends Defaults
/*  89:    */   {
/*  90:    */     public static final String ID = "weka.gui.visualize.visualizepanel";
/*  91:150 */     public static final Settings.SettingKey AXIS_COLOUR_KEY = new Settings.SettingKey("weka.gui.visualize.visualizepanel.axisColor", "Colour of the axis", "");
/*  92:154 */     public static final Color AXIS_COLOR = Color.GREEN;
/*  93:157 */     public static final Settings.SettingKey BACKGROUND_COLOUR_KEY = new Settings.SettingKey("weka.gui.visualize.visualizepanel.backgroundColor", "Background colour of scatter plot", "");
/*  94:162 */     public static final Color BACKGROUND_COLOR = Color.WHITE;
/*  95:165 */     public static final Settings.SettingKey BAR_BACKGROUND_COLOUR_KEY = new Settings.SettingKey("weka.gui.visualize.visualizepanel.attributeBarBackgroundColor", "Background colour for the 1-dimensional attribute bars", "");
/*  96:170 */     public static final Color BAR_BACKGROUND_COLOUR = Color.WHITE;
/*  97:    */     private static final long serialVersionUID = 4227480313375404688L;
/*  98:    */     
/*  99:    */     public VisualizeDefaults()
/* 100:    */     {
/* 101:179 */       super();
/* 102:    */       
/* 103:181 */       this.m_defaults.put(AXIS_COLOUR_KEY, AXIS_COLOR);
/* 104:182 */       this.m_defaults.put(BACKGROUND_COLOUR_KEY, BACKGROUND_COLOR);
/* 105:183 */       this.m_defaults.put(BAR_BACKGROUND_COLOUR_KEY, BAR_BACKGROUND_COLOUR);
/* 106:    */     }
/* 107:    */   }
/* 108:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.VisualizeUtils
 * JD-Core Version:    0.7.0.1
 */