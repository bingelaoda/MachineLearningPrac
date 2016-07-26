/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Component;
/*   4:    */ import java.awt.Image;
/*   5:    */ import java.net.URL;
/*   6:    */ import javax.swing.ImageIcon;
/*   7:    */ import javax.swing.JOptionPane;
/*   8:    */ 
/*   9:    */ public class ComponentHelper
/*  10:    */ {
/*  11: 42 */   public static final String[] IMAGES = { "weka/gui/", "weka/gui/images/" };
/*  12:    */   
/*  13:    */   public static ImageIcon getImageIcon(String dir, String filename)
/*  14:    */   {
/*  15: 56 */     ImageIcon result = null;
/*  16: 57 */     URL url = Loader.getURL(dir, filename);
/*  17: 60 */     if (url == null) {
/*  18: 61 */       for (int i = 0; i < IMAGES.length; i++)
/*  19:    */       {
/*  20: 62 */         url = Loader.getURL(IMAGES[i], filename);
/*  21: 63 */         if (url != null) {
/*  22:    */           break;
/*  23:    */         }
/*  24:    */       }
/*  25:    */     }
/*  26: 68 */     if (url != null) {
/*  27: 69 */       result = new ImageIcon(url);
/*  28:    */     }
/*  29: 71 */     return result;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public static ImageIcon getImageIcon(String filename)
/*  33:    */   {
/*  34: 81 */     return getImageIcon("", filename);
/*  35:    */   }
/*  36:    */   
/*  37:    */   public static Image getImage(String dir, String filename)
/*  38:    */   {
/*  39: 95 */     Image result = null;
/*  40: 96 */     ImageIcon img = getImageIcon(dir, filename);
/*  41: 98 */     if (img != null) {
/*  42: 99 */       result = img.getImage();
/*  43:    */     }
/*  44:101 */     return result;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public static Image getImage(String filename)
/*  48:    */   {
/*  49:114 */     Image result = null;
/*  50:115 */     ImageIcon img = getImageIcon(filename);
/*  51:117 */     if (img != null) {
/*  52:118 */       result = img.getImage();
/*  53:    */     }
/*  54:120 */     return result;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public static int showMessageBox(Component parent, String title, String msg, int buttons, int messageType)
/*  58:    */   {
/*  59:    */     String icon;
/*  60:138 */     switch (messageType)
/*  61:    */     {
/*  62:    */     case 0: 
/*  63:140 */       icon = "weka/gui/images/error.gif";
/*  64:141 */       break;
/*  65:    */     case 1: 
/*  66:143 */       icon = "weka/gui/images/information.gif";
/*  67:144 */       break;
/*  68:    */     case 2: 
/*  69:146 */       icon = "weka/gui/images/information.gif";
/*  70:147 */       break;
/*  71:    */     case 3: 
/*  72:149 */       icon = "weka/gui/images/question.gif";
/*  73:150 */       break;
/*  74:    */     default: 
/*  75:152 */       icon = "weka/gui/images/information.gif";
/*  76:    */     }
/*  77:156 */     return JOptionPane.showConfirmDialog(parent, msg, title, buttons, messageType, getImageIcon(icon));
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static String showInputBox(Component parent, String title, String msg, Object initialValue)
/*  81:    */   {
/*  82:171 */     if (title == null) {
/*  83:172 */       title = "Input...";
/*  84:    */     }
/*  85:174 */     Object result = JOptionPane.showInputDialog(parent, msg, title, 3, getImageIcon("question.gif"), null, initialValue);
/*  86:177 */     if (result != null) {
/*  87:178 */       return result.toString();
/*  88:    */     }
/*  89:180 */     return null;
/*  90:    */   }
/*  91:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.ComponentHelper
 * JD-Core Version:    0.7.0.1
 */