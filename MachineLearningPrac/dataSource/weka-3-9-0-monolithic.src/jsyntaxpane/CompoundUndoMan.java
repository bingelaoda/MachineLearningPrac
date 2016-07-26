/*   1:    */ package jsyntaxpane;
/*   2:    */ 
/*   3:    */ import javax.swing.event.UndoableEditEvent;
/*   4:    */ import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
/*   5:    */ import javax.swing.undo.CannotUndoException;
/*   6:    */ import javax.swing.undo.CompoundEdit;
/*   7:    */ import javax.swing.undo.UndoManager;
/*   8:    */ import javax.swing.undo.UndoableEdit;
/*   9:    */ 
/*  10:    */ public class CompoundUndoMan
/*  11:    */   extends UndoManager
/*  12:    */ {
/*  13:    */   private SyntaxDocument doc;
/*  14:    */   private CompoundEdit compoundEdit;
/*  15: 48 */   private boolean startCombine = false;
/*  16:    */   
/*  17:    */   public CompoundUndoMan(SyntaxDocument doc)
/*  18:    */   {
/*  19: 51 */     this.doc = doc;
/*  20: 52 */     doc.addUndoableEditListener(this);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public void undoableEditHappened(UndoableEditEvent e)
/*  24:    */   {
/*  25: 63 */     AbstractDocument.DefaultDocumentEvent docEvt = (AbstractDocument.DefaultDocumentEvent)e.getEdit();
/*  26: 65 */     if (this.compoundEdit == null)
/*  27:    */     {
/*  28: 66 */       this.compoundEdit = startCompoundEdit(e.getEdit());
/*  29: 67 */       this.startCombine = false;
/*  30: 68 */       return;
/*  31:    */     }
/*  32: 74 */     if ((this.startCombine) || (Math.abs(docEvt.getLength()) == 1))
/*  33:    */     {
/*  34: 75 */       this.compoundEdit.addEdit(e.getEdit());
/*  35: 76 */       this.startCombine = false;
/*  36: 77 */       return;
/*  37:    */     }
/*  38: 82 */     this.compoundEdit.end();
/*  39: 83 */     this.compoundEdit = startCompoundEdit(e.getEdit());
/*  40:    */   }
/*  41:    */   
/*  42:    */   private CompoundEdit startCompoundEdit(UndoableEdit anEdit)
/*  43:    */   {
/*  44: 92 */     AbstractDocument.DefaultDocumentEvent docEvt = (AbstractDocument.DefaultDocumentEvent)anEdit;
/*  45:    */     
/*  46:    */ 
/*  47:    */ 
/*  48: 96 */     this.compoundEdit = new MyCompoundEdit();
/*  49: 97 */     this.compoundEdit.addEdit(anEdit);
/*  50:    */     
/*  51:    */ 
/*  52:    */ 
/*  53:    */ 
/*  54:102 */     addEdit(this.compoundEdit);
/*  55:    */     
/*  56:104 */     return this.compoundEdit;
/*  57:    */   }
/*  58:    */   
/*  59:    */   class MyCompoundEdit
/*  60:    */     extends CompoundEdit
/*  61:    */   {
/*  62:    */     MyCompoundEdit() {}
/*  63:    */     
/*  64:    */     public boolean isInProgress()
/*  65:    */     {
/*  66:113 */       return false;
/*  67:    */     }
/*  68:    */     
/*  69:    */     public void undo()
/*  70:    */       throws CannotUndoException
/*  71:    */     {
/*  72:120 */       if (CompoundUndoMan.this.compoundEdit != null) {
/*  73:121 */         CompoundUndoMan.this.compoundEdit.end();
/*  74:    */       }
/*  75:124 */       super.undo();
/*  76:    */       
/*  77:    */ 
/*  78:    */ 
/*  79:128 */       CompoundUndoMan.this.compoundEdit = null;
/*  80:    */     }
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void startCombine()
/*  84:    */   {
/*  85:137 */     this.startCombine = true;
/*  86:    */   }
/*  87:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.CompoundUndoMan
 * JD-Core Version:    0.7.0.1
 */