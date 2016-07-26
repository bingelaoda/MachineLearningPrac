/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Dimension;
/*   4:    */ import java.awt.KeyEventDispatcher;
/*   5:    */ import java.awt.KeyboardFocusManager;
/*   6:    */ import java.awt.event.KeyEvent;
/*   7:    */ import java.io.IOException;
/*   8:    */ import java.io.PrintStream;
/*   9:    */ import java.util.LinkedList;
/*  10:    */ import java.util.List;
/*  11:    */ import java.util.Properties;
/*  12:    */ import javax.swing.JOptionPane;
/*  13:    */ import javax.swing.UIDefaults;
/*  14:    */ import javax.swing.UIManager;
/*  15:    */ import javax.swing.UIManager.LookAndFeelInfo;
/*  16:    */ import weka.core.Environment;
/*  17:    */ import weka.core.Settings;
/*  18:    */ import weka.core.Utils;
/*  19:    */ 
/*  20:    */ public class LookAndFeel
/*  21:    */ {
/*  22: 53 */   public static String PROPERTY_FILE = "weka/gui/LookAndFeel.props";
/*  23:    */   protected static Properties LOOKANDFEEL_PROPERTIES;
/*  24:    */   
/*  25:    */   static
/*  26:    */   {
/*  27:    */     try
/*  28:    */     {
/*  29: 60 */       LOOKANDFEEL_PROPERTIES = Utils.readProperties(PROPERTY_FILE);
/*  30:    */     }
/*  31:    */     catch (Exception ex)
/*  32:    */     {
/*  33: 62 */       JOptionPane.showMessageDialog(null, "LookAndFeel: Could not read a LookAndFeel configuration file.\nAn example file is included in the Weka distribution.\nThis file should be named \"" + PROPERTY_FILE + "\"  and\n" + "should be placed either in your user home (which is set\n" + "to \"" + System.getProperties().getProperty("user.home") + "\")\n" + "or the directory that java was started from\n", "LookAndFeel", 0);
/*  34:    */     }
/*  35:    */   }
/*  36:    */   
/*  37:    */   public static List<String> getAvailableLookAndFeelClasses()
/*  38:    */   {
/*  39: 80 */     List<String> lafs = new LinkedList();
/*  40: 82 */     for (UIManager.LookAndFeelInfo i : UIManager.getInstalledLookAndFeels()) {
/*  41: 83 */       lafs.add(i.getClassName());
/*  42:    */     }
/*  43: 86 */     return lafs;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public static boolean setLookAndFeel(String classname)
/*  47:    */   {
/*  48:    */     boolean result;
/*  49:    */     try
/*  50:    */     {
/*  51: 99 */       UIManager.setLookAndFeel(classname);
/*  52:100 */       result = true;
/*  53:102 */       if ((System.getProperty("os.name").toLowerCase().contains("mac os x")) && (!classname.contains("com.apple.laf"))) {
/*  54:104 */         KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher()
/*  55:    */         {
/*  56:    */           public boolean dispatchKeyEvent(KeyEvent e)
/*  57:    */           {
/*  58:108 */             if ((!e.isConsumed()) && 
/*  59:109 */               (e.isMetaDown()) && (
/*  60:110 */               (e.getKeyCode() == 86) || (e.getKeyCode() == 65) || (e.getKeyCode() == 67) || (e.getKeyCode() == 88))) {
/*  61:114 */               e.setModifiers(128);
/*  62:    */             }
/*  63:118 */             return false;
/*  64:    */           }
/*  65:    */         });
/*  66:    */       }
/*  67:125 */       if (classname.toLowerCase().contains("nimbus"))
/*  68:    */       {
/*  69:126 */         javax.swing.LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
/*  70:127 */         UIDefaults defaults = lookAndFeel.getDefaults();
/*  71:128 */         defaults.put("ScrollBar.minimumThumbSize", new Dimension(30, 30));
/*  72:    */       }
/*  73:    */     }
/*  74:    */     catch (Exception e)
/*  75:    */     {
/*  76:131 */       e.printStackTrace();
/*  77:132 */       result = false;
/*  78:    */     }
/*  79:135 */     return result;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public static void setLookAndFeel(String appID, String lookAndFeelKey, String defaultLookAndFeel)
/*  83:    */     throws IOException
/*  84:    */   {
/*  85:147 */     Settings forLookAndFeelOnly = new Settings("weka", appID);
/*  86:    */     
/*  87:149 */     String laf = (String)forLookAndFeelOnly.getSetting(appID, lookAndFeelKey, defaultLookAndFeel, Environment.getSystemWide());
/*  88:153 */     if ((laf.length() <= 0) || (!laf.contains(".")) || (!setLookAndFeel(laf))) {
/*  89:156 */       setLookAndFeel();
/*  90:    */     }
/*  91:    */   }
/*  92:    */   
/*  93:    */   public static boolean setLookAndFeel()
/*  94:    */   {
/*  95:169 */     String classname = LOOKANDFEEL_PROPERTIES.getProperty("Theme", "");
/*  96:170 */     if (classname.equals(""))
/*  97:    */     {
/*  98:174 */       if (System.getProperty("os.name").equalsIgnoreCase("linux")) {
/*  99:175 */         return true;
/* 100:    */       }
/* 101:177 */       classname = getSystemLookAndFeel();
/* 102:    */     }
/* 103:181 */     return setLookAndFeel(classname);
/* 104:    */   }
/* 105:    */   
/* 106:    */   public static String getSystemLookAndFeel()
/* 107:    */   {
/* 108:190 */     return UIManager.getSystemLookAndFeelClassName();
/* 109:    */   }
/* 110:    */   
/* 111:    */   public static String[] getInstalledLookAndFeels()
/* 112:    */   {
/* 113:203 */     UIManager.LookAndFeelInfo[] laf = UIManager.getInstalledLookAndFeels();
/* 114:204 */     String[] result = new String[laf.length];
/* 115:205 */     for (int i = 0; i < laf.length; i++) {
/* 116:206 */       result[i] = laf[i].getClassName();
/* 117:    */     }
/* 118:208 */     return result;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public static void main(String[] args)
/* 122:    */   {
/* 123:220 */     System.out.println("\nInstalled Look and Feel themes:");
/* 124:221 */     String[] list = getInstalledLookAndFeels();
/* 125:222 */     for (int i = 0; i < list.length; i++) {
/* 126:223 */       System.out.println(i + 1 + ". " + list[i]);
/* 127:    */     }
/* 128:225 */     System.out.println("\nNote: a theme can be set in '" + PROPERTY_FILE + "'.");
/* 129:    */   }
/* 130:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.LookAndFeel
 * JD-Core Version:    0.7.0.1
 */