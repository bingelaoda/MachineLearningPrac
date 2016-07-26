/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.Container;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.awt.event.WindowAdapter;
/*   9:    */ import java.awt.event.WindowEvent;
/*  10:    */ import java.io.BufferedWriter;
/*  11:    */ import java.io.File;
/*  12:    */ import java.io.FileWriter;
/*  13:    */ import java.io.PrintStream;
/*  14:    */ import java.io.PrintWriter;
/*  15:    */ import javax.swing.JButton;
/*  16:    */ import javax.swing.JDialog;
/*  17:    */ import javax.swing.JFileChooser;
/*  18:    */ import javax.swing.JFrame;
/*  19:    */ import javax.swing.JOptionPane;
/*  20:    */ 
/*  21:    */ public class SaveBuffer
/*  22:    */ {
/*  23:    */   private Logger m_Log;
/*  24:    */   private Component m_parentComponent;
/*  25: 52 */   private String m_lastvisitedDirectory = null;
/*  26:    */   
/*  27:    */   public SaveBuffer(Logger log, Component parent)
/*  28:    */   {
/*  29: 60 */     this.m_Log = log;
/*  30: 61 */     this.m_parentComponent = parent;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public boolean save(StringBuffer buf)
/*  34:    */   {
/*  35: 70 */     if (buf != null)
/*  36:    */     {
/*  37:    */       JFileChooser fileChooser;
/*  38:    */       JFileChooser fileChooser;
/*  39: 72 */       if (this.m_lastvisitedDirectory == null) {
/*  40: 73 */         fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/*  41:    */       } else {
/*  42: 76 */         fileChooser = new JFileChooser(this.m_lastvisitedDirectory);
/*  43:    */       }
/*  44: 79 */       fileChooser.setFileSelectionMode(0);
/*  45: 80 */       int returnVal = fileChooser.showSaveDialog(this.m_parentComponent);
/*  46: 81 */       if (returnVal == 0)
/*  47:    */       {
/*  48: 82 */         File sFile = fileChooser.getSelectedFile();
/*  49: 83 */         this.m_lastvisitedDirectory = sFile.getPath();
/*  50: 85 */         if (sFile.exists())
/*  51:    */         {
/*  52: 86 */           Object[] options = new String[4];
/*  53: 87 */           options[0] = "Append";
/*  54: 88 */           options[1] = "Overwrite";
/*  55: 89 */           options[2] = "Choose new name";
/*  56: 90 */           options[3] = "Cancel";
/*  57:    */           
/*  58: 92 */           JOptionPane jop = new JOptionPane("File exists", 3, 1, null, options);
/*  59:    */           
/*  60:    */ 
/*  61:    */ 
/*  62:    */ 
/*  63: 97 */           JDialog dialog = jop.createDialog(this.m_parentComponent, "File query");
/*  64: 98 */           dialog.setVisible(true);
/*  65: 99 */           Object selectedValue = jop.getValue();
/*  66:100 */           if (selectedValue != null) {
/*  67:102 */             for (int i = 0; i < 4; i++) {
/*  68:103 */               if (options[i].equals(selectedValue)) {
/*  69:104 */                 switch (i)
/*  70:    */                 {
/*  71:    */                 case 0: 
/*  72:107 */                   return saveOverwriteAppend(buf, sFile, true);
/*  73:    */                 case 1: 
/*  74:110 */                   return saveOverwriteAppend(buf, sFile, false);
/*  75:    */                 case 2: 
/*  76:113 */                   return save(buf);
/*  77:    */                 }
/*  78:    */               }
/*  79:    */             }
/*  80:    */           }
/*  81:    */         }
/*  82:    */         else
/*  83:    */         {
/*  84:121 */           saveOverwriteAppend(buf, sFile, false);
/*  85:    */         }
/*  86:    */       }
/*  87:    */       else
/*  88:    */       {
/*  89:124 */         return false;
/*  90:    */       }
/*  91:    */     }
/*  92:127 */     return false;
/*  93:    */   }
/*  94:    */   
/*  95:    */   private boolean saveOverwriteAppend(StringBuffer buf, File sFile, boolean append)
/*  96:    */   {
/*  97:    */     try
/*  98:    */     {
/*  99:142 */       String path = sFile.getPath();
/* 100:143 */       if (this.m_Log != null) {
/* 101:144 */         if (append) {
/* 102:145 */           this.m_Log.statusMessage("Appending to file...");
/* 103:    */         } else {
/* 104:147 */           this.m_Log.statusMessage("Saving to file...");
/* 105:    */         }
/* 106:    */       }
/* 107:150 */       PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, append)));
/* 108:    */       
/* 109:    */ 
/* 110:153 */       out.write(buf.toString(), 0, buf.toString().length());
/* 111:154 */       out.close();
/* 112:155 */       if (this.m_Log != null) {
/* 113:156 */         this.m_Log.statusMessage("OK");
/* 114:    */       }
/* 115:    */     }
/* 116:    */     catch (Exception ex)
/* 117:    */     {
/* 118:159 */       ex.printStackTrace();
/* 119:160 */       if (this.m_Log != null) {
/* 120:161 */         this.m_Log.logMessage(ex.getMessage());
/* 121:    */       }
/* 122:163 */       return false;
/* 123:    */     }
/* 124:166 */     return true;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public static void main(String[] args)
/* 128:    */   {
/* 129:    */     try
/* 130:    */     {
/* 131:174 */       JFrame jf = new JFrame("SaveBuffer test");
/* 132:    */       
/* 133:176 */       jf.getContentPane().setLayout(new BorderLayout());
/* 134:177 */       LogPanel lp = new LogPanel();
/* 135:178 */       JButton jb = new JButton("Save");
/* 136:179 */       jf.getContentPane().add(jb, "South");
/* 137:180 */       jf.getContentPane().add(lp, "Center");
/* 138:181 */       SaveBuffer svb = new SaveBuffer(lp, jf);
/* 139:182 */       jb.addActionListener(new ActionListener()
/* 140:    */       {
/* 141:    */         public void actionPerformed(ActionEvent e)
/* 142:    */         {
/* 143:184 */           this.val$svb.save(new StringBuffer("A bit of test text"));
/* 144:    */         }
/* 145:187 */       });
/* 146:188 */       jf.addWindowListener(new WindowAdapter()
/* 147:    */       {
/* 148:    */         public void windowClosing(WindowEvent e)
/* 149:    */         {
/* 150:190 */           this.val$jf.dispose();
/* 151:191 */           System.exit(0);
/* 152:    */         }
/* 153:193 */       });
/* 154:194 */       jf.pack();
/* 155:195 */       jf.setVisible(true);
/* 156:    */     }
/* 157:    */     catch (Exception ex)
/* 158:    */     {
/* 159:197 */       ex.printStackTrace();
/* 160:198 */       System.err.println(ex.getMessage());
/* 161:    */     }
/* 162:    */   }
/* 163:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.SaveBuffer
 * JD-Core Version:    0.7.0.1
 */