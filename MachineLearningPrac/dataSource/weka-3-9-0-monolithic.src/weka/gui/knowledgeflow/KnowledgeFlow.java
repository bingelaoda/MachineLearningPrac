/*  1:   */ package weka.gui.knowledgeflow;
/*  2:   */ 
/*  3:   */ import java.util.Arrays;
/*  4:   */ import java.util.List;
/*  5:   */ import weka.core.Copyright;
/*  6:   */ import weka.core.Version;
/*  7:   */ import weka.gui.SplashWindow;
/*  8:   */ 
/*  9:   */ public class KnowledgeFlow
/* 10:   */ {
/* 11:   */   public static void main(String[] args)
/* 12:   */   {
/* 13:39 */     List<String> message = Arrays.asList(new String[] { "WEKA Knowledge Flow", "Version " + Version.VERSION, "(c) " + Copyright.getFromYear() + " - " + Copyright.getToYear(), "The University of Waikato", "Hamilton, New Zealand" });
/* 14:   */     
/* 15:   */ 
/* 16:   */ 
/* 17:43 */     SplashWindow.splash(ClassLoader.getSystemResource("weka/gui/weka_icon_new.png"), message);
/* 18:   */     
/* 19:45 */     SplashWindow.invokeMain("weka.gui.knowledgeflow.KnowledgeFlowApp", args);
/* 20:   */     
/* 21:47 */     SplashWindow.disposeSplash();
/* 22:   */   }
/* 23:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.KnowledgeFlow
 * JD-Core Version:    0.7.0.1
 */