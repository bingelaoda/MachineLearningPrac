/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.event.WindowAdapter;
/*   6:    */ import java.awt.event.WindowEvent;
/*   7:    */ import java.beans.PropertyEditorSupport;
/*   8:    */ import java.io.PrintStream;
/*   9:    */ import javax.swing.JFrame;
/*  10:    */ import weka.core.SelectedTag;
/*  11:    */ import weka.core.Tag;
/*  12:    */ 
/*  13:    */ public class SelectedTagEditor
/*  14:    */   extends PropertyEditorSupport
/*  15:    */ {
/*  16:    */   public String getJavaInitializationString()
/*  17:    */   {
/*  18: 52 */     SelectedTag s = (SelectedTag)getValue();
/*  19: 53 */     Tag[] tags = s.getTags();
/*  20: 54 */     String result = "new SelectedTag(" + s.getSelectedTag().getID() + ", {\n";
/*  21: 57 */     for (int i = 0; i < tags.length; i++)
/*  22:    */     {
/*  23: 58 */       result = result + "new Tag(" + tags[i].getID() + ",\"" + tags[i].getReadable() + "\")";
/*  24: 61 */       if (i < tags.length - 1) {
/*  25: 62 */         result = result + ',';
/*  26:    */       }
/*  27: 64 */       result = result + '\n';
/*  28:    */     }
/*  29: 66 */     return result + "})";
/*  30:    */   }
/*  31:    */   
/*  32:    */   public String getAsText()
/*  33:    */   {
/*  34: 76 */     SelectedTag s = (SelectedTag)getValue();
/*  35: 77 */     return s.getSelectedTag().getReadable();
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void setAsText(String text)
/*  39:    */   {
/*  40: 89 */     SelectedTag s = (SelectedTag)getValue();
/*  41: 90 */     Tag[] tags = s.getTags();
/*  42:    */     try
/*  43:    */     {
/*  44: 92 */       for (int i = 0; i < tags.length; i++) {
/*  45: 93 */         if (text.equals(tags[i].getReadable()))
/*  46:    */         {
/*  47: 94 */           setValue(new SelectedTag(tags[i].getID(), tags));
/*  48: 95 */           return;
/*  49:    */         }
/*  50:    */       }
/*  51:    */     }
/*  52:    */     catch (Exception ex)
/*  53:    */     {
/*  54: 99 */       throw new IllegalArgumentException(text);
/*  55:    */     }
/*  56:    */   }
/*  57:    */   
/*  58:    */   public String[] getTags()
/*  59:    */   {
/*  60:110 */     SelectedTag s = (SelectedTag)getValue();
/*  61:111 */     Tag[] tags = s.getTags();
/*  62:112 */     String[] result = new String[tags.length];
/*  63:113 */     for (int i = 0; i < tags.length; i++) {
/*  64:114 */       result[i] = tags[i].getReadable();
/*  65:    */     }
/*  66:116 */     return result;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public static void main(String[] args)
/*  70:    */   {
/*  71:    */     try
/*  72:    */     {
/*  73:127 */       GenericObjectEditor.registerEditors();
/*  74:128 */       Tag[] tags = { new Tag(1, "First option"), new Tag(2, "Second option"), new Tag(3, "Third option"), new Tag(4, "Fourth option"), new Tag(5, "Fifth option") };
/*  75:    */       
/*  76:    */ 
/*  77:    */ 
/*  78:    */ 
/*  79:    */ 
/*  80:    */ 
/*  81:135 */       SelectedTag initial = new SelectedTag(1, tags);
/*  82:136 */       SelectedTagEditor ce = new SelectedTagEditor();
/*  83:137 */       ce.setValue(initial);
/*  84:138 */       PropertyValueSelector ps = new PropertyValueSelector(ce);
/*  85:139 */       JFrame f = new JFrame();
/*  86:140 */       f.addWindowListener(new WindowAdapter()
/*  87:    */       {
/*  88:    */         public void windowClosing(WindowEvent e)
/*  89:    */         {
/*  90:142 */           System.exit(0);
/*  91:    */         }
/*  92:144 */       });
/*  93:145 */       f.getContentPane().setLayout(new BorderLayout());
/*  94:146 */       f.getContentPane().add(ps, "Center");
/*  95:147 */       f.pack();
/*  96:148 */       f.setVisible(true);
/*  97:    */     }
/*  98:    */     catch (Exception ex)
/*  99:    */     {
/* 100:150 */       ex.printStackTrace();
/* 101:151 */       System.err.println(ex.getMessage());
/* 102:    */     }
/* 103:    */   }
/* 104:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.SelectedTagEditor
 * JD-Core Version:    0.7.0.1
 */