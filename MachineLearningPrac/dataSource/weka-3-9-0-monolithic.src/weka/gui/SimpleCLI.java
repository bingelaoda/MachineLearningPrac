/*  1:   */ package weka.gui;
/*  2:   */ 
/*  3:   */ import java.awt.BorderLayout;
/*  4:   */ import javax.swing.ImageIcon;
/*  5:   */ import javax.swing.JFrame;
/*  6:   */ import weka.gui.scripting.ScriptingPanel;
/*  7:   */ 
/*  8:   */ public class SimpleCLI
/*  9:   */   extends JFrame
/* 10:   */ {
/* 11:   */   static final long serialVersionUID = -50661410800566036L;
/* 12:   */   
/* 13:   */   public SimpleCLI()
/* 14:   */     throws Exception
/* 15:   */   {
/* 16:54 */     SimpleCLIPanel panel = new SimpleCLIPanel();
/* 17:   */     
/* 18:56 */     setLayout(new BorderLayout());
/* 19:57 */     setTitle(panel.getTitle());
/* 20:58 */     setDefaultCloseOperation(2);
/* 21:59 */     setIconImage(panel.getIcon().getImage());
/* 22:60 */     add(panel);
/* 23:61 */     pack();
/* 24:62 */     setSize(600, 500);
/* 25:63 */     setLocationRelativeTo(null);
/* 26:64 */     setVisible(true);
/* 27:   */   }
/* 28:   */   
/* 29:   */   public static void main(String[] args)
/* 30:   */   {
/* 31:73 */     ScriptingPanel.showPanel(new SimpleCLIPanel(), args, 600, 500);
/* 32:   */   }
/* 33:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.SimpleCLI
 * JD-Core Version:    0.7.0.1
 */