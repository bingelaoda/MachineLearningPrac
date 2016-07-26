/*  1:   */ package weka.gui.beans;
/*  2:   */ 
/*  3:   */ import java.util.Arrays;
/*  4:   */ import java.util.List;
/*  5:   */ import weka.core.Copyright;
/*  6:   */ import weka.core.Version;
/*  7:   */ import weka.core.logging.Logger;
/*  8:   */ import weka.core.logging.Logger.Level;
/*  9:   */ import weka.gui.SplashWindow;
/* 10:   */ 
/* 11:   */ public class KnowledgeFlow
/* 12:   */ {
/* 13:   */   public static void startApp()
/* 14:   */   {
/* 15:43 */     KnowledgeFlowApp.addStartupListener(new StartUpListener()
/* 16:   */     {
/* 17:   */       public void startUpComplete() {}
/* 18:48 */     });
/* 19:49 */     List<String> message = Arrays.asList(new String[] { "WEKA Knowledge Flow", "Version " + Version.VERSION, "(c) " + Copyright.getFromYear() + " - " + Copyright.getToYear(), "The University of Waikato", "Hamilton, New Zealand" });
/* 20:   */     
/* 21:   */ 
/* 22:   */ 
/* 23:53 */     SplashWindow.splash(ClassLoader.getSystemResource("weka/gui/weka_icon_new.png"), message);
/* 24:   */     
/* 25:   */ 
/* 26:   */ 
/* 27:57 */     Thread nt = new Thread()
/* 28:   */     {
/* 29:   */       public void run()
/* 30:   */       {
/* 31:59 */         SplashWindow.invokeMethod("weka.gui.beans.KnowledgeFlowApp", "createSingleton", null);
/* 32:   */       }
/* 33:62 */     };
/* 34:63 */     nt.start();
/* 35:   */   }
/* 36:   */   
/* 37:   */   public static void main(String[] args)
/* 38:   */   {
/* 39:73 */     Logger.log(Logger.Level.INFO, "Logging started");
/* 40:   */     
/* 41:75 */     List<String> message = Arrays.asList(new String[] { "WEKA Knowledge Flow", "Version " + Version.VERSION, "(c) " + Copyright.getFromYear() + " - " + Copyright.getToYear(), "The University of Waikato", "Hamilton, New Zealand" });
/* 42:   */     
/* 43:   */ 
/* 44:   */ 
/* 45:79 */     SplashWindow.splash(ClassLoader.getSystemResource("weka/gui/weka_icon_new.png"), message);
/* 46:   */     
/* 47:   */ 
/* 48:82 */     SplashWindow.invokeMain("weka.gui.beans.KnowledgeFlowApp", args);
/* 49:83 */     SplashWindow.disposeSplash();
/* 50:   */   }
/* 51:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.KnowledgeFlow
 * JD-Core Version:    0.7.0.1
 */