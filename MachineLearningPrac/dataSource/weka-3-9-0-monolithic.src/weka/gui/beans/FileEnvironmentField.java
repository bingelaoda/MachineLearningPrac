/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Point;
/*   5:    */ import java.awt.event.ActionEvent;
/*   6:    */ import java.awt.event.ActionListener;
/*   7:    */ import java.beans.PropertyChangeEvent;
/*   8:    */ import java.beans.PropertyChangeListener;
/*   9:    */ import java.io.File;
/*  10:    */ import javax.swing.JButton;
/*  11:    */ import javax.swing.JFileChooser;
/*  12:    */ import javax.swing.JPanel;
/*  13:    */ import javax.swing.filechooser.FileFilter;
/*  14:    */ import weka.core.Environment;
/*  15:    */ import weka.gui.ExtensionFileFilter;
/*  16:    */ import weka.gui.FileEditor;
/*  17:    */ import weka.gui.PropertyDialog;
/*  18:    */ 
/*  19:    */ @Deprecated
/*  20:    */ public class FileEnvironmentField
/*  21:    */   extends EnvironmentField
/*  22:    */ {
/*  23:    */   private static final long serialVersionUID = -233731548086207652L;
/*  24: 60 */   protected FileEditor m_fileEditor = new FileEditor();
/*  25:    */   protected PropertyDialog m_fileEditorDialog;
/*  26:    */   protected JButton m_browseBut;
/*  27:    */   
/*  28:    */   public FileEnvironmentField()
/*  29:    */   {
/*  30: 72 */     this("", 0, false);
/*  31: 73 */     setEnvironment(Environment.getSystemWide());
/*  32:    */   }
/*  33:    */   
/*  34:    */   public FileEnvironmentField(Environment env)
/*  35:    */   {
/*  36: 82 */     this("", 0, false);
/*  37: 83 */     setEnvironment(env);
/*  38:    */   }
/*  39:    */   
/*  40:    */   public FileEnvironmentField(String label, Environment env)
/*  41:    */   {
/*  42: 87 */     this(label, 0, false);
/*  43: 88 */     setEnvironment(env);
/*  44:    */   }
/*  45:    */   
/*  46:    */   public FileEnvironmentField(String label, Environment env, int fileChooserType)
/*  47:    */   {
/*  48:100 */     this(label, fileChooserType, false);
/*  49:101 */     setEnvironment(env);
/*  50:    */   }
/*  51:    */   
/*  52:    */   public FileEnvironmentField(String label, Environment env, int fileChooserType, boolean directoriesOnly)
/*  53:    */   {
/*  54:116 */     this(label, fileChooserType, directoriesOnly);
/*  55:117 */     setEnvironment(env);
/*  56:    */   }
/*  57:    */   
/*  58:    */   public FileEnvironmentField(String label, int fileChooserType, boolean directoriesOnly)
/*  59:    */   {
/*  60:129 */     super(label);
/*  61:    */     
/*  62:131 */     this.m_fileEditor.addPropertyChangeListener(new PropertyChangeListener()
/*  63:    */     {
/*  64:    */       public void propertyChange(PropertyChangeEvent evt)
/*  65:    */       {
/*  66:134 */         File selected = (File)FileEnvironmentField.this.m_fileEditor.getValue();
/*  67:135 */         if (selected != null) {
/*  68:136 */           FileEnvironmentField.this.setText(selected.toString());
/*  69:    */         }
/*  70:    */       }
/*  71:140 */     });
/*  72:141 */     final JFileChooser embeddedEditor = (JFileChooser)this.m_fileEditor.getCustomEditor();
/*  73:143 */     if (directoriesOnly) {
/*  74:144 */       embeddedEditor.setFileSelectionMode(1);
/*  75:    */     } else {
/*  76:146 */       embeddedEditor.setFileSelectionMode(0);
/*  77:    */     }
/*  78:148 */     embeddedEditor.setDialogType(fileChooserType);
/*  79:149 */     ExtensionFileFilter ff = new ExtensionFileFilter(".model", "Serialized Weka classifier (*.model)");
/*  80:    */     
/*  81:151 */     embeddedEditor.addChoosableFileFilter(ff);
/*  82:    */     
/*  83:153 */     this.m_browseBut = new JButton("Browse...");
/*  84:154 */     this.m_browseBut.addActionListener(new ActionListener()
/*  85:    */     {
/*  86:    */       public void actionPerformed(ActionEvent e)
/*  87:    */       {
/*  88:    */         try
/*  89:    */         {
/*  90:158 */           String modelPath = FileEnvironmentField.this.getText();
/*  91:159 */           if (modelPath != null)
/*  92:    */           {
/*  93:    */             try
/*  94:    */             {
/*  95:161 */               modelPath = FileEnvironmentField.this.m_env.substitute(modelPath);
/*  96:    */             }
/*  97:    */             catch (Exception ex) {}
/*  98:165 */             File toSet = new File(modelPath);
/*  99:166 */             if (toSet.isFile())
/* 100:    */             {
/* 101:167 */               FileEnvironmentField.this.m_fileEditor.setValue(new File(modelPath));
/* 102:168 */               toSet = toSet.getParentFile();
/* 103:    */             }
/* 104:170 */             if (toSet.isDirectory()) {
/* 105:171 */               embeddedEditor.setCurrentDirectory(toSet);
/* 106:    */             }
/* 107:    */           }
/* 108:175 */           FileEnvironmentField.this.showFileEditor();
/* 109:    */         }
/* 110:    */         catch (Exception ex)
/* 111:    */         {
/* 112:177 */           ex.printStackTrace();
/* 113:    */         }
/* 114:    */       }
/* 115:181 */     });
/* 116:182 */     JPanel bP = new JPanel();
/* 117:183 */     bP.setLayout(new BorderLayout());
/* 118:    */     
/* 119:185 */     bP.add(this.m_browseBut, "Center");
/* 120:    */     
/* 121:187 */     add(bP, "East");
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void addFileFilter(FileFilter toSet)
/* 125:    */   {
/* 126:196 */     JFileChooser embeddedEditor = (JFileChooser)this.m_fileEditor.getCustomEditor();
/* 127:197 */     embeddedEditor.addChoosableFileFilter(toSet);
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void resetFileFilters()
/* 131:    */   {
/* 132:204 */     JFileChooser embeddedEditor = (JFileChooser)this.m_fileEditor.getCustomEditor();
/* 133:205 */     embeddedEditor.resetChoosableFileFilters();
/* 134:    */   }
/* 135:    */   
/* 136:    */   private void showFileEditor()
/* 137:    */   {
/* 138:209 */     if (this.m_fileEditorDialog == null)
/* 139:    */     {
/* 140:210 */       int x = getLocationOnScreen().x;
/* 141:211 */       int y = getLocationOnScreen().y;
/* 142:212 */       if (PropertyDialog.getParentDialog(this) != null) {
/* 143:213 */         this.m_fileEditorDialog = new PropertyDialog(PropertyDialog.getParentDialog(this), this.m_fileEditor, x, y);
/* 144:    */       } else {
/* 145:216 */         this.m_fileEditorDialog = new PropertyDialog(PropertyDialog.getParentFrame(this), this.m_fileEditor, x, y);
/* 146:    */       }
/* 147:    */     }
/* 148:220 */     this.m_fileEditorDialog.setVisible(true);
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void removeNotify()
/* 152:    */   {
/* 153:225 */     super.removeNotify();
/* 154:226 */     if (this.m_fileEditorDialog != null)
/* 155:    */     {
/* 156:227 */       this.m_fileEditorDialog.dispose();
/* 157:228 */       this.m_fileEditorDialog = null;
/* 158:    */     }
/* 159:    */   }
/* 160:    */   
/* 161:    */   public void setEnabled(boolean enabled)
/* 162:    */   {
/* 163:239 */     super.setEnabled(enabled);
/* 164:240 */     this.m_browseBut.setEnabled(enabled);
/* 165:    */   }
/* 166:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.FileEnvironmentField
 * JD-Core Version:    0.7.0.1
 */