/*   1:    */ package weka.gui.visualize;
/*   2:    */ 
/*   3:    */ import java.util.Hashtable;
/*   4:    */ import javax.swing.JPanel;
/*   5:    */ 
/*   6:    */ public class PrintablePanel
/*   7:    */   extends JPanel
/*   8:    */   implements PrintableHandler
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = 6281532227633417538L;
/*  11: 46 */   protected PrintableComponent m_Printer = null;
/*  12:    */   
/*  13:    */   public PrintablePanel()
/*  14:    */   {
/*  15: 53 */     this.m_Printer = new PrintableComponent(this);
/*  16:    */   }
/*  17:    */   
/*  18:    */   public Hashtable<String, JComponentWriter> getWriters()
/*  19:    */   {
/*  20: 65 */     return this.m_Printer.getWriters();
/*  21:    */   }
/*  22:    */   
/*  23:    */   public JComponentWriter getWriter(String name)
/*  24:    */   {
/*  25: 77 */     return this.m_Printer.getWriter(name);
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void setSaveDialogTitle(String title)
/*  29:    */   {
/*  30: 85 */     this.m_Printer.setSaveDialogTitle(title);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public String getSaveDialogTitle()
/*  34:    */   {
/*  35: 93 */     return this.m_Printer.getSaveDialogTitle();
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void setScale(double x, double y)
/*  39:    */   {
/*  40:104 */     this.m_Printer.setScale(x, y);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public double getXScale()
/*  44:    */   {
/*  45:112 */     return this.m_Printer.getXScale();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public double getYScale()
/*  49:    */   {
/*  50:120 */     return this.m_Printer.getYScale();
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void saveComponent()
/*  54:    */   {
/*  55:128 */     this.m_Printer.saveComponent();
/*  56:    */   }
/*  57:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.PrintablePanel
 * JD-Core Version:    0.7.0.1
 */