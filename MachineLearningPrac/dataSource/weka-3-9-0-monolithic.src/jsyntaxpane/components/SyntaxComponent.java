/*  1:   */ package jsyntaxpane.components;
/*  2:   */ 
/*  3:   */ import javax.swing.JEditorPane;
/*  4:   */ import jsyntaxpane.util.Configuration;
/*  5:   */ 
/*  6:   */ public abstract interface SyntaxComponent
/*  7:   */ {
/*  8:   */   public abstract void config(Configuration paramConfiguration);
/*  9:   */   
/* 10:   */   public abstract void install(JEditorPane paramJEditorPane);
/* 11:   */   
/* 12:   */   public abstract void deinstall(JEditorPane paramJEditorPane);
/* 13:   */   
/* 14:   */   public static enum Status
/* 15:   */   {
/* 16:52 */     INSTALLING,  DEINSTALLING;
/* 17:   */     
/* 18:   */     private Status() {}
/* 19:   */   }
/* 20:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.components.SyntaxComponent
 * JD-Core Version:    0.7.0.1
 */