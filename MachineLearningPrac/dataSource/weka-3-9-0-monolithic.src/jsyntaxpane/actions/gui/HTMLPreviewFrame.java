/*   1:    */ package jsyntaxpane.actions.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Container;
/*   4:    */ import java.awt.event.WindowAdapter;
/*   5:    */ import java.awt.event.WindowEvent;
/*   6:    */ import java.util.logging.Level;
/*   7:    */ import java.util.logging.Logger;
/*   8:    */ import javax.swing.GroupLayout;
/*   9:    */ import javax.swing.GroupLayout.Alignment;
/*  10:    */ import javax.swing.GroupLayout.ParallelGroup;
/*  11:    */ import javax.swing.JEditorPane;
/*  12:    */ import javax.swing.JFrame;
/*  13:    */ import javax.swing.JScrollPane;
/*  14:    */ import javax.swing.event.DocumentEvent;
/*  15:    */ import javax.swing.event.DocumentListener;
/*  16:    */ import javax.swing.text.BadLocationException;
/*  17:    */ import javax.swing.text.Document;
/*  18:    */ 
/*  19:    */ public class HTMLPreviewFrame
/*  20:    */   extends JFrame
/*  21:    */   implements DocumentListener
/*  22:    */ {
/*  23:    */   Document doc;
/*  24:    */   private JEditorPane jEdtHtml;
/*  25:    */   private JScrollPane jScrollPane1;
/*  26:    */   
/*  27:    */   public HTMLPreviewFrame(Document doc)
/*  28:    */   {
/*  29: 36 */     initComponents();
/*  30: 37 */     this.doc = doc;
/*  31: 38 */     doc.addDocumentListener(this);
/*  32: 39 */     updateHTML();
/*  33:    */   }
/*  34:    */   
/*  35:    */   private void updateHTML()
/*  36:    */   {
/*  37:    */     try
/*  38:    */     {
/*  39: 44 */       this.jEdtHtml.setText(this.doc.getText(0, this.doc.getLength()));
/*  40:    */     }
/*  41:    */     catch (BadLocationException ex)
/*  42:    */     {
/*  43: 46 */       Logger.getLogger(HTMLPreviewFrame.class.getName()).log(Level.SEVERE, null, ex);
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:    */   private void initComponents()
/*  48:    */   {
/*  49: 60 */     this.jScrollPane1 = new JScrollPane();
/*  50: 61 */     this.jEdtHtml = new JEditorPane();
/*  51:    */     
/*  52: 63 */     setDefaultCloseOperation(2);
/*  53: 64 */     setTitle("HTML Preview");
/*  54: 65 */     addWindowListener(new WindowAdapter()
/*  55:    */     {
/*  56:    */       public void windowClosed(WindowEvent evt)
/*  57:    */       {
/*  58: 67 */         HTMLPreviewFrame.this.onWindowClosed(evt);
/*  59:    */       }
/*  60: 70 */     });
/*  61: 71 */     this.jEdtHtml.setContentType("text/html");
/*  62: 72 */     this.jEdtHtml.setEditable(false);
/*  63: 73 */     this.jScrollPane1.setViewportView(this.jEdtHtml);
/*  64:    */     
/*  65: 75 */     GroupLayout layout = new GroupLayout(getContentPane());
/*  66: 76 */     getContentPane().setLayout(layout);
/*  67: 77 */     layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jScrollPane1, -1, 688, 32767));
/*  68:    */     
/*  69:    */ 
/*  70:    */ 
/*  71: 81 */     layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jScrollPane1, -1, 449, 32767));
/*  72:    */     
/*  73:    */ 
/*  74:    */ 
/*  75:    */ 
/*  76: 86 */     pack();
/*  77:    */   }
/*  78:    */   
/*  79:    */   private void onWindowClosed(WindowEvent evt)
/*  80:    */   {
/*  81: 90 */     this.doc.removeDocumentListener(this);
/*  82: 91 */     this.doc.putProperty("html-preview-window", null);
/*  83: 92 */     this.doc = null;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void insertUpdate(DocumentEvent e)
/*  87:    */   {
/*  88:102 */     updateHTML();
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void removeUpdate(DocumentEvent e)
/*  92:    */   {
/*  93:107 */     updateHTML();
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void changedUpdate(DocumentEvent e)
/*  97:    */   {
/*  98:112 */     updateHTML();
/*  99:    */   }
/* 100:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.gui.HTMLPreviewFrame
 * JD-Core Version:    0.7.0.1
 */