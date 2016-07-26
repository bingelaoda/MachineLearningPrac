/*  1:   */ package weka.gui;
/*  2:   */ 
/*  3:   */ import java.util.Arrays;
/*  4:   */ import java.util.List;
/*  5:   */ import weka.core.Copyright;
/*  6:   */ import weka.core.Version;
/*  7:   */ 
/*  8:   */ public class Workbench
/*  9:   */ {
/* 10:   */   public static void main(String[] args)
/* 11:   */   {
/* 12:40 */     List<String> message = Arrays.asList(new String[] { "WEKA Workbench", "Version " + Version.VERSION, "(c) " + Copyright.getFromYear() + " - " + Copyright.getToYear(), "The University of Waikato", "Hamilton, New Zealand" });
/* 13:   */     
/* 14:   */ 
/* 15:   */ 
/* 16:44 */     SplashWindow.splash(ClassLoader.getSystemResource("weka/gui/weka_icon_new.png"), message);
/* 17:   */     
/* 18:46 */     SplashWindow.invokeMain("weka.gui.WorkbenchApp", args);
/* 19:47 */     SplashWindow.disposeSplash();
/* 20:   */   }
/* 21:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.Workbench
 * JD-Core Version:    0.7.0.1
 */