/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Window;
/*   5:    */ import java.awt.event.ActionEvent;
/*   6:    */ import java.awt.event.ActionListener;
/*   7:    */ import javax.swing.JButton;
/*   8:    */ import javax.swing.JPanel;
/*   9:    */ import javax.swing.JScrollPane;
/*  10:    */ import javax.swing.JTextArea;
/*  11:    */ 
/*  12:    */ public class NoteCustomizer
/*  13:    */   extends JPanel
/*  14:    */   implements BeanCustomizer, CustomizerCloseRequester, CustomizerClosingListener
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = 995648616684953391L;
/*  17:    */   protected Window m_parentWindow;
/*  18:    */   protected Note m_note;
/*  19: 56 */   protected JTextArea m_textArea = new JTextArea(5, 20);
/*  20:    */   private BeanCustomizer.ModifyListener m_modifyListener;
/*  21:    */   
/*  22:    */   public NoteCustomizer()
/*  23:    */   {
/*  24: 68 */     setLayout(new BorderLayout());
/*  25: 69 */     this.m_textArea.setLineWrap(true);
/*  26:    */     
/*  27: 71 */     JScrollPane sc = new JScrollPane(this.m_textArea);
/*  28:    */     
/*  29: 73 */     add(sc, "Center");
/*  30:    */     
/*  31: 75 */     JButton okBut = new JButton("OK");
/*  32: 76 */     add(okBut, "South");
/*  33: 77 */     okBut.addActionListener(new ActionListener()
/*  34:    */     {
/*  35:    */       public void actionPerformed(ActionEvent e)
/*  36:    */       {
/*  37: 79 */         NoteCustomizer.this.customizerClosing();
/*  38: 80 */         if (NoteCustomizer.this.m_parentWindow != null) {
/*  39: 81 */           NoteCustomizer.this.m_parentWindow.dispose();
/*  40:    */         }
/*  41:    */       }
/*  42:    */     });
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setParentWindow(Window parent)
/*  46:    */   {
/*  47: 90 */     this.m_parentWindow = parent;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setObject(Object ob)
/*  51:    */   {
/*  52: 96 */     this.m_note = ((Note)ob);
/*  53: 97 */     this.m_textArea.setText(this.m_note.getNoteText());
/*  54: 98 */     this.m_textArea.selectAll();
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void customizerClosing()
/*  58:    */   {
/*  59:103 */     if (this.m_note != null)
/*  60:    */     {
/*  61:104 */       this.m_note.setNoteText(this.m_textArea.getText());
/*  62:106 */       if (this.m_modifyListener != null) {
/*  63:107 */         this.m_modifyListener.setModifiedStatus(this, true);
/*  64:    */       }
/*  65:    */     }
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/*  69:    */   {
/*  70:114 */     this.m_modifyListener = l;
/*  71:    */   }
/*  72:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.NoteCustomizer
 * JD-Core Version:    0.7.0.1
 */