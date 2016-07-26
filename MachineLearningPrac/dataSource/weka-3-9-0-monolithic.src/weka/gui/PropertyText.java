/*  1:   */ package weka.gui;
/*  2:   */ 
/*  3:   */ import java.awt.event.FocusAdapter;
/*  4:   */ import java.awt.event.FocusEvent;
/*  5:   */ import java.awt.event.KeyAdapter;
/*  6:   */ import java.awt.event.KeyEvent;
/*  7:   */ import java.beans.PropertyEditor;
/*  8:   */ import javax.swing.JTextField;
/*  9:   */ 
/* 10:   */ class PropertyText
/* 11:   */   extends JTextField
/* 12:   */ {
/* 13:   */   private static final long serialVersionUID = -3915342928825822730L;
/* 14:   */   private PropertyEditor m_Editor;
/* 15:   */   
/* 16:   */   PropertyText(PropertyEditor pe)
/* 17:   */   {
/* 18:57 */     super(pe.getAsText().equals("null") ? "" : pe.getAsText());
/* 19:58 */     this.m_Editor = pe;
/* 20:   */     
/* 21:   */ 
/* 22:   */ 
/* 23:   */ 
/* 24:   */ 
/* 25:   */ 
/* 26:65 */     addKeyListener(new KeyAdapter()
/* 27:   */     {
/* 28:   */       public void keyReleased(KeyEvent e)
/* 29:   */       {
/* 30:68 */         PropertyText.this.updateEditor();
/* 31:   */       }
/* 32:71 */     });
/* 33:72 */     addFocusListener(new FocusAdapter()
/* 34:   */     {
/* 35:   */       public void focusLost(FocusEvent e)
/* 36:   */       {
/* 37:74 */         PropertyText.this.updateEditor();
/* 38:   */       }
/* 39:   */     });
/* 40:   */   }
/* 41:   */   
/* 42:   */   protected void updateUs()
/* 43:   */   {
/* 44:   */     try
/* 45:   */     {
/* 46:84 */       setText(this.m_Editor.getAsText());
/* 47:   */     }
/* 48:   */     catch (IllegalArgumentException ex) {}
/* 49:   */   }
/* 50:   */   
/* 51:   */   protected void updateEditor()
/* 52:   */   {
/* 53:   */     try
/* 54:   */     {
/* 55:95 */       this.m_Editor.setAsText(getText());
/* 56:   */     }
/* 57:   */     catch (IllegalArgumentException ex) {}
/* 58:   */   }
/* 59:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.PropertyText
 * JD-Core Version:    0.7.0.1
 */