/*  1:   */ package weka.gui;
/*  2:   */ 
/*  3:   */ import java.util.Arrays;
/*  4:   */ import java.util.List;
/*  5:   */ import javax.swing.JMenuBar;
/*  6:   */ import weka.core.Copyright;
/*  7:   */ import weka.core.Version;
/*  8:   */ 
/*  9:   */ public class GUIChooser
/* 10:   */ {
/* 11:   */   public static abstract interface GUIChooserMenuPlugin
/* 12:   */   {
/* 13:   */     public abstract String getApplicationName();
/* 14:   */     
/* 15:   */     public abstract Menu getMenuToDisplayIn();
/* 16:   */     
/* 17:   */     public abstract String getMenuEntryText();
/* 18:   */     
/* 19:   */     public abstract JMenuBar getMenuBar();
/* 20:   */     
/* 21:   */     public static enum Menu
/* 22:   */     {
/* 23:50 */       TOOLS,  VISUALIZATION;
/* 24:   */       
/* 25:   */       private Menu() {}
/* 26:   */     }
/* 27:   */   }
/* 28:   */   
/* 29:   */   public static void main(String[] args)
/* 30:   */   {
/* 31:85 */     List<String> message = Arrays.asList(new String[] { "Waikato Environment for Knowledge Analysis", "Version " + Version.VERSION, "(c) " + Copyright.getFromYear() + " - " + Copyright.getToYear(), "The University of Waikato", "Hamilton, New Zealand" });
/* 32:   */     
/* 33:   */ 
/* 34:   */ 
/* 35:   */ 
/* 36:90 */     SplashWindow.splash(ClassLoader.getSystemResource("weka/gui/weka_icon_new.png"), message);
/* 37:   */     
/* 38:92 */     SplashWindow.invokeMain("weka.gui.GUIChooserApp", args);
/* 39:93 */     SplashWindow.disposeSplash();
/* 40:   */   }
/* 41:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.GUIChooser
 * JD-Core Version:    0.7.0.1
 */