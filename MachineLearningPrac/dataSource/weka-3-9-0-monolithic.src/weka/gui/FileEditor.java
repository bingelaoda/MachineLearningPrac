/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Component;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dialog;
/*   6:    */ import java.awt.FontMetrics;
/*   7:    */ import java.awt.Graphics;
/*   8:    */ import java.awt.Rectangle;
/*   9:    */ import java.awt.event.ActionEvent;
/*  10:    */ import java.awt.event.ActionListener;
/*  11:    */ import java.beans.PropertyEditorSupport;
/*  12:    */ import java.io.File;
/*  13:    */ import javax.swing.JFileChooser;
/*  14:    */ 
/*  15:    */ public class FileEditor
/*  16:    */   extends PropertyEditorSupport
/*  17:    */ {
/*  18:    */   protected JFileChooser m_FileChooser;
/*  19:    */   
/*  20:    */   public String getJavaInitializationString()
/*  21:    */   {
/*  22: 54 */     File f = (File)getValue();
/*  23: 55 */     if (f == null) {
/*  24: 56 */       return "null";
/*  25:    */     }
/*  26: 58 */     return "new File(\"" + f.getName() + "\")";
/*  27:    */   }
/*  28:    */   
/*  29:    */   public boolean supportsCustomEditor()
/*  30:    */   {
/*  31: 67 */     return true;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public Component getCustomEditor()
/*  35:    */   {
/*  36: 77 */     if (this.m_FileChooser == null)
/*  37:    */     {
/*  38: 78 */       File currentFile = (File)getValue();
/*  39: 79 */       if (currentFile != null)
/*  40:    */       {
/*  41: 80 */         this.m_FileChooser = new JFileChooser();
/*  42:    */         
/*  43: 82 */         this.m_FileChooser.setSelectedFile(currentFile);
/*  44:    */       }
/*  45:    */       else
/*  46:    */       {
/*  47: 84 */         this.m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/*  48:    */       }
/*  49: 87 */       this.m_FileChooser.setApproveButtonText("Select");
/*  50: 88 */       this.m_FileChooser.setApproveButtonMnemonic('S');
/*  51: 89 */       this.m_FileChooser.setFileSelectionMode(2);
/*  52: 90 */       this.m_FileChooser.addActionListener(new ActionListener()
/*  53:    */       {
/*  54:    */         public void actionPerformed(ActionEvent e)
/*  55:    */         {
/*  56: 92 */           String cmdString = e.getActionCommand();
/*  57: 93 */           if (cmdString.equals("ApproveSelection"))
/*  58:    */           {
/*  59: 94 */             File newVal = FileEditor.this.m_FileChooser.getSelectedFile();
/*  60: 95 */             FileEditor.this.setValue(newVal);
/*  61:    */           }
/*  62: 97 */           FileEditor.this.closeDialog();
/*  63:    */         }
/*  64:    */       });
/*  65:    */     }
/*  66:101 */     return this.m_FileChooser;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public boolean isPaintable()
/*  70:    */   {
/*  71:110 */     return true;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void paintValue(Graphics gfx, Rectangle box)
/*  75:    */   {
/*  76:121 */     FontMetrics fm = gfx.getFontMetrics();
/*  77:122 */     int vpad = (box.height - fm.getHeight()) / 2;
/*  78:123 */     File f = (File)getValue();
/*  79:124 */     String val = "No file";
/*  80:125 */     if (f != null) {
/*  81:126 */       val = f.getName();
/*  82:    */     }
/*  83:128 */     gfx.drawString(val, 2, fm.getHeight() + vpad);
/*  84:    */   }
/*  85:    */   
/*  86:    */   protected void closeDialog()
/*  87:    */   {
/*  88:135 */     if ((this.m_FileChooser instanceof Container))
/*  89:    */     {
/*  90:136 */       Dialog dlg = PropertyDialog.getParentDialog(this.m_FileChooser);
/*  91:137 */       if (dlg != null) {
/*  92:138 */         dlg.setVisible(false);
/*  93:    */       }
/*  94:    */     }
/*  95:    */   }
/*  96:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.FileEditor
 * JD-Core Version:    0.7.0.1
 */