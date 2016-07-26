/*   1:    */ package weka.gui;
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
/*  15:    */ 
/*  16:    */ public class FileEnvironmentField
/*  17:    */   extends EnvironmentField
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = -233731548086207652L;
/*  20: 54 */   protected FileEditor m_fileEditor = new FileEditor();
/*  21:    */   protected PropertyDialog m_fileEditorDialog;
/*  22:    */   protected JButton m_browseBut;
/*  23:    */   
/*  24:    */   public FileEnvironmentField()
/*  25:    */   {
/*  26: 66 */     this("", 0, false);
/*  27: 67 */     setEnvironment(Environment.getSystemWide());
/*  28:    */   }
/*  29:    */   
/*  30:    */   public FileEnvironmentField(Environment env)
/*  31:    */   {
/*  32: 76 */     this("", 0, false);
/*  33: 77 */     setEnvironment(env);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public FileEnvironmentField(String label, Environment env)
/*  37:    */   {
/*  38: 81 */     this(label, 0, false);
/*  39: 82 */     setEnvironment(env);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public FileEnvironmentField(String label, Environment env, int fileChooserType)
/*  43:    */   {
/*  44: 94 */     this(label, fileChooserType, false);
/*  45: 95 */     setEnvironment(env);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public FileEnvironmentField(String label, Environment env, int fileChooserType, boolean directoriesOnly)
/*  49:    */   {
/*  50:110 */     this(label, fileChooserType, directoriesOnly);
/*  51:111 */     setEnvironment(env);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public FileEnvironmentField(String label, int fileChooserType, boolean directoriesOnly)
/*  55:    */   {
/*  56:123 */     super(label);
/*  57:    */     
/*  58:125 */     this.m_fileEditor.addPropertyChangeListener(new PropertyChangeListener()
/*  59:    */     {
/*  60:    */       public void propertyChange(PropertyChangeEvent evt)
/*  61:    */       {
/*  62:128 */         File selected = (File)FileEnvironmentField.this.m_fileEditor.getValue();
/*  63:129 */         if (selected != null) {
/*  64:130 */           FileEnvironmentField.this.setText(selected.toString());
/*  65:    */         }
/*  66:    */       }
/*  67:134 */     });
/*  68:135 */     final JFileChooser embeddedEditor = (JFileChooser)this.m_fileEditor.getCustomEditor();
/*  69:137 */     if (directoriesOnly) {
/*  70:138 */       embeddedEditor.setFileSelectionMode(1);
/*  71:    */     } else {
/*  72:140 */       embeddedEditor.setFileSelectionMode(0);
/*  73:    */     }
/*  74:142 */     embeddedEditor.setDialogType(fileChooserType);
/*  75:143 */     ExtensionFileFilter ff = new ExtensionFileFilter(".model", "Serialized Weka classifier (*.model)");
/*  76:    */     
/*  77:145 */     embeddedEditor.addChoosableFileFilter(ff);
/*  78:    */     
/*  79:147 */     this.m_browseBut = new JButton("Browse...");
/*  80:148 */     this.m_browseBut.addActionListener(new ActionListener()
/*  81:    */     {
/*  82:    */       public void actionPerformed(ActionEvent e)
/*  83:    */       {
/*  84:    */         try
/*  85:    */         {
/*  86:152 */           String modelPath = FileEnvironmentField.this.getText();
/*  87:153 */           if (modelPath != null)
/*  88:    */           {
/*  89:    */             try
/*  90:    */             {
/*  91:155 */               modelPath = FileEnvironmentField.this.m_env.substitute(modelPath);
/*  92:    */             }
/*  93:    */             catch (Exception ex) {}
/*  94:159 */             File toSet = new File(modelPath);
/*  95:160 */             if (toSet.isFile())
/*  96:    */             {
/*  97:161 */               FileEnvironmentField.this.m_fileEditor.setValue(new File(modelPath));
/*  98:162 */               toSet = toSet.getParentFile();
/*  99:    */             }
/* 100:164 */             if (toSet.isDirectory()) {
/* 101:165 */               embeddedEditor.setCurrentDirectory(toSet);
/* 102:    */             }
/* 103:    */           }
/* 104:169 */           FileEnvironmentField.this.showFileEditor();
/* 105:    */         }
/* 106:    */         catch (Exception ex)
/* 107:    */         {
/* 108:171 */           ex.printStackTrace();
/* 109:    */         }
/* 110:    */       }
/* 111:175 */     });
/* 112:176 */     JPanel bP = new JPanel();
/* 113:177 */     bP.setLayout(new BorderLayout());
/* 114:    */     
/* 115:179 */     bP.add(this.m_browseBut, "Center");
/* 116:    */     
/* 117:181 */     add(bP, "East");
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void addFileFilter(FileFilter toSet)
/* 121:    */   {
/* 122:190 */     JFileChooser embeddedEditor = (JFileChooser)this.m_fileEditor.getCustomEditor();
/* 123:191 */     embeddedEditor.addChoosableFileFilter(toSet);
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void setCurrentDirectory(String directory)
/* 127:    */   {
/* 128:195 */     setCurrentDirectory(new File(directory));
/* 129:    */   }
/* 130:    */   
/* 131:    */   public void setCurrentDirectory(File directory)
/* 132:    */   {
/* 133:199 */     String tmpString = directory.toString();
/* 134:200 */     if (Environment.containsEnvVariables(tmpString)) {
/* 135:    */       try
/* 136:    */       {
/* 137:202 */         tmpString = this.m_env.substitute(tmpString);
/* 138:    */       }
/* 139:    */       catch (Exception ex) {}
/* 140:    */     }
/* 141:207 */     File tmp2 = new File(new File(tmpString).getAbsolutePath());
/* 142:208 */     JFileChooser embeddedEditor = (JFileChooser)this.m_fileEditor.getCustomEditor();
/* 143:209 */     if (tmp2.isDirectory())
/* 144:    */     {
/* 145:210 */       embeddedEditor.setCurrentDirectory(tmp2);
/* 146:211 */       if (embeddedEditor.getFileSelectionMode() == 1) {
/* 147:212 */         super.setAsText(directory.toString());
/* 148:    */       }
/* 149:    */     }
/* 150:    */     else
/* 151:    */     {
/* 152:215 */       embeddedEditor.setSelectedFile(tmp2);
/* 153:216 */       if (embeddedEditor.getFileSelectionMode() == 0) {
/* 154:217 */         super.setAsText(directory.toString());
/* 155:    */       }
/* 156:    */     }
/* 157:    */   }
/* 158:    */   
/* 159:    */   public void resetFileFilters()
/* 160:    */   {
/* 161:226 */     JFileChooser embeddedEditor = (JFileChooser)this.m_fileEditor.getCustomEditor();
/* 162:227 */     embeddedEditor.resetChoosableFileFilters();
/* 163:    */   }
/* 164:    */   
/* 165:    */   private void showFileEditor()
/* 166:    */   {
/* 167:231 */     if (this.m_fileEditorDialog == null)
/* 168:    */     {
/* 169:232 */       int x = getLocationOnScreen().x;
/* 170:233 */       int y = getLocationOnScreen().y;
/* 171:234 */       if (PropertyDialog.getParentDialog(this) != null) {
/* 172:235 */         this.m_fileEditorDialog = new PropertyDialog(PropertyDialog.getParentDialog(this), this.m_fileEditor, x, y);
/* 173:    */       } else {
/* 174:239 */         this.m_fileEditorDialog = new PropertyDialog(PropertyDialog.getParentFrame(this), this.m_fileEditor, x, y);
/* 175:    */       }
/* 176:    */     }
/* 177:244 */     this.m_fileEditorDialog.setVisible(true);
/* 178:    */   }
/* 179:    */   
/* 180:    */   public void removeNotify()
/* 181:    */   {
/* 182:249 */     super.removeNotify();
/* 183:250 */     if (this.m_fileEditorDialog != null)
/* 184:    */     {
/* 185:251 */       this.m_fileEditorDialog.dispose();
/* 186:252 */       this.m_fileEditorDialog = null;
/* 187:    */     }
/* 188:    */   }
/* 189:    */   
/* 190:    */   public void setEnabled(boolean enabled)
/* 191:    */   {
/* 192:263 */     super.setEnabled(enabled);
/* 193:264 */     this.m_browseBut.setEnabled(enabled);
/* 194:    */   }
/* 195:    */   
/* 196:    */   public Object getValue()
/* 197:    */   {
/* 198:269 */     String path = getAsText();
/* 199:270 */     if ((path != null) && (path.length() > 0)) {
/* 200:271 */       return new File(path);
/* 201:    */     }
/* 202:274 */     return new File(".");
/* 203:    */   }
/* 204:    */   
/* 205:    */   public void setValue(Object value)
/* 206:    */   {
/* 207:279 */     if ((value instanceof File)) {
/* 208:280 */       setAsText(((File)value).toString());
/* 209:    */     }
/* 210:    */   }
/* 211:    */   
/* 212:    */   public void setAsText(String val)
/* 213:    */   {
/* 214:286 */     setCurrentDirectory(val);
/* 215:    */   }
/* 216:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.FileEnvironmentField
 * JD-Core Version:    0.7.0.1
 */