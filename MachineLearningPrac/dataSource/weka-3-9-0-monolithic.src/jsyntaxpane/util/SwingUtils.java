/*  1:   */ package jsyntaxpane.util;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import java.awt.event.ActionListener;
/*  5:   */ import javax.swing.JRootPane;
/*  6:   */ import javax.swing.KeyStroke;
/*  7:   */ import jsyntaxpane.actions.gui.EscapeListener;
/*  8:   */ 
/*  9:   */ public class SwingUtils
/* 10:   */ {
/* 11:   */   public static void addEscapeListener(EscapeListener dialog)
/* 12:   */   {
/* 13:36 */     ActionListener escListener = new ActionListener()
/* 14:   */     {
/* 15:   */       public void actionPerformed(ActionEvent e)
/* 16:   */       {
/* 17:40 */         this.val$dialog.escapePressed();
/* 18:   */       }
/* 19:43 */     };
/* 20:44 */     dialog.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(27, 0), 2);
/* 21:   */   }
/* 22:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.util.SwingUtils
 * JD-Core Version:    0.7.0.1
 */