/*   1:    */ package jsyntaxpane.actions;
/*   2:    */ 
/*   3:    */ import javax.swing.JLabel;
/*   4:    */ import javax.swing.event.CaretEvent;
/*   5:    */ import javax.swing.event.CaretListener;
/*   6:    */ import javax.swing.text.BadLocationException;
/*   7:    */ import javax.swing.text.JTextComponent;
/*   8:    */ import jsyntaxpane.SyntaxDocument;
/*   9:    */ 
/*  10:    */ public class CaretMonitor
/*  11:    */   implements CaretListener
/*  12:    */ {
/*  13:    */   private JLabel label;
/*  14:    */   private JTextComponent text;
/*  15: 41 */   private String noSelectionFormat = "%d:%d (%d)";
/*  16: 53 */   private String selectionFormat = "%d:%d - %d:%d (%d)";
/*  17:    */   
/*  18:    */   public CaretMonitor(JTextComponent text, JLabel label)
/*  19:    */   {
/*  20: 56 */     this.label = label;
/*  21: 57 */     this.text = text;
/*  22: 58 */     text.addCaretListener(this);
/*  23:    */   }
/*  24:    */   
/*  25:    */   public void caretUpdate(CaretEvent evt)
/*  26:    */   {
/*  27: 63 */     if ((this.text.getDocument() instanceof SyntaxDocument)) {
/*  28:    */       try
/*  29:    */       {
/*  30: 65 */         if (this.text.getSelectionStart() == this.text.getSelectionEnd())
/*  31:    */         {
/*  32: 66 */           int pos = evt.getDot();
/*  33: 67 */           String loc = String.format(this.noSelectionFormat, new Object[] { Integer.valueOf(ActionUtils.getLineNumber(this.text, pos) + 1), Integer.valueOf(ActionUtils.getColumnNumber(this.text, pos) + 1), Integer.valueOf(pos) });
/*  34:    */           
/*  35:    */ 
/*  36:    */ 
/*  37: 71 */           this.label.setText(loc);
/*  38:    */         }
/*  39:    */         else
/*  40:    */         {
/*  41: 73 */           int start = this.text.getSelectionStart();
/*  42: 74 */           int end = this.text.getSelectionEnd();
/*  43: 75 */           String loc = String.format(this.selectionFormat, new Object[] { Integer.valueOf(ActionUtils.getLineNumber(this.text, start) + 1), Integer.valueOf(ActionUtils.getColumnNumber(this.text, start) + 1), Integer.valueOf(ActionUtils.getLineNumber(this.text, end) + 1), Integer.valueOf(ActionUtils.getColumnNumber(this.text, end) + 1), Integer.valueOf(end - start), Integer.valueOf(start), Integer.valueOf(end) });
/*  44:    */           
/*  45:    */ 
/*  46:    */ 
/*  47:    */ 
/*  48:    */ 
/*  49:    */ 
/*  50:    */ 
/*  51: 83 */           this.label.setText(loc);
/*  52:    */         }
/*  53:    */       }
/*  54:    */       catch (BadLocationException ex)
/*  55:    */       {
/*  56: 86 */         this.label.setText("Ex: " + ex.getMessage());
/*  57:    */       }
/*  58:    */     } else {
/*  59: 89 */       this.label.setText("Position");
/*  60:    */     }
/*  61:    */   }
/*  62:    */   
/*  63:    */   protected void finalize()
/*  64:    */     throws Throwable
/*  65:    */   {
/*  66: 95 */     this.text.removeCaretListener(this);
/*  67: 96 */     super.finalize();
/*  68:    */   }
/*  69:    */   
/*  70:    */   public String getNoSelectionFormat()
/*  71:    */   {
/*  72:100 */     return this.noSelectionFormat;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void setNoSelectionFormat(String noSelectionFormat)
/*  76:    */   {
/*  77:104 */     this.noSelectionFormat = noSelectionFormat;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public String getSelectionFormat()
/*  81:    */   {
/*  82:108 */     return this.selectionFormat;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setSelectionFormat(String selectionFormat)
/*  86:    */   {
/*  87:112 */     this.selectionFormat = selectionFormat;
/*  88:    */   }
/*  89:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.CaretMonitor
 * JD-Core Version:    0.7.0.1
 */