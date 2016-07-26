/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.event.ActionEvent;
/*   6:    */ import java.awt.event.ActionListener;
/*   7:    */ import java.io.BufferedReader;
/*   8:    */ import java.io.BufferedWriter;
/*   9:    */ import java.io.FileReader;
/*  10:    */ import java.io.FileWriter;
/*  11:    */ import javax.swing.BorderFactory;
/*  12:    */ import javax.swing.JDialog;
/*  13:    */ import javax.swing.JEditorPane;
/*  14:    */ import javax.swing.JFileChooser;
/*  15:    */ import javax.swing.JMenu;
/*  16:    */ import javax.swing.JMenuBar;
/*  17:    */ import javax.swing.JMenuItem;
/*  18:    */ import javax.swing.JOptionPane;
/*  19:    */ import javax.swing.JPanel;
/*  20:    */ import javax.swing.JScrollPane;
/*  21:    */ import javax.swing.KeyStroke;
/*  22:    */ import javax.swing.text.BadLocationException;
/*  23:    */ import javax.swing.text.Document;
/*  24:    */ import jsyntaxpane.DefaultSyntaxKit;
/*  25:    */ import weka.gui.knowledgeflow.GOEStepEditorDialog;
/*  26:    */ import weka.knowledgeflow.steps.PythonScriptExecutor;
/*  27:    */ import weka.knowledgeflow.steps.Step;
/*  28:    */ import weka.python.PythonSession;
/*  29:    */ 
/*  30:    */ public class PythonScriptExecutorStepEditorDialog
/*  31:    */   extends GOEStepEditorDialog
/*  32:    */ {
/*  33:    */   private static final long serialVersionUID = 4072516424536789076L;
/*  34: 62 */   private boolean m_pyAvailable = true;
/*  35:    */   protected JEditorPane m_scriptEditor;
/*  36:    */   protected JMenuBar m_menuBar;
/*  37:    */   
/*  38:    */   public void setStepToEdit(Step step)
/*  39:    */   {
/*  40: 77 */     copyOriginal(step);
/*  41:    */     
/*  42: 79 */     addPrimaryEditorPanel("Center");
/*  43:    */     
/*  44: 81 */     String script = ((PythonScriptExecutor)getStepToEdit()).getPythonScript();
/*  45: 82 */     DefaultSyntaxKit.initKit();
/*  46: 83 */     this.m_scriptEditor = new JEditorPane();
/*  47:    */     
/*  48:    */ 
/*  49: 86 */     this.m_pyAvailable = true;
/*  50: 87 */     String envEvalResults = null;
/*  51: 88 */     Exception envEvalEx = null;
/*  52: 89 */     if (!PythonSession.pythonAvailable()) {
/*  53:    */       try
/*  54:    */       {
/*  55: 92 */         if (!PythonSession.initSession("python", ((PythonScriptExecutor)getStepToEdit()).getDebug()))
/*  56:    */         {
/*  57: 94 */           envEvalResults = PythonSession.getPythonEnvCheckResults();
/*  58: 95 */           this.m_pyAvailable = false;
/*  59:    */         }
/*  60:    */       }
/*  61:    */       catch (Exception ex)
/*  62:    */       {
/*  63: 98 */         envEvalEx = ex;
/*  64: 99 */         this.m_pyAvailable = false;
/*  65:100 */         showErrorDialog(ex);
/*  66:    */       }
/*  67:    */     }
/*  68:104 */     final JFileChooser fileChooser = new JFileChooser();
/*  69:105 */     fileChooser.setAcceptAllFileFilterUsed(true);
/*  70:106 */     fileChooser.setMultiSelectionEnabled(false);
/*  71:    */     
/*  72:108 */     this.m_menuBar = new JMenuBar();
/*  73:    */     
/*  74:110 */     JMenu fileM = new JMenu();
/*  75:111 */     this.m_menuBar.add(fileM);
/*  76:112 */     fileM.setText("File");
/*  77:113 */     fileM.setMnemonic('F');
/*  78:    */     
/*  79:115 */     JMenuItem newItem = new JMenuItem();
/*  80:116 */     fileM.add(newItem);
/*  81:117 */     newItem.setText("New");
/*  82:118 */     newItem.setAccelerator(KeyStroke.getKeyStroke(78, 2));
/*  83:    */     
/*  84:    */ 
/*  85:121 */     newItem.addActionListener(new ActionListener()
/*  86:    */     {
/*  87:    */       public void actionPerformed(ActionEvent e)
/*  88:    */       {
/*  89:124 */         PythonScriptExecutorStepEditorDialog.this.m_scriptEditor.setText("");
/*  90:    */       }
/*  91:127 */     });
/*  92:128 */     JMenuItem loadItem = new JMenuItem();
/*  93:129 */     fileM.add(loadItem);
/*  94:130 */     loadItem.setText("Open File...");
/*  95:131 */     loadItem.setAccelerator(KeyStroke.getKeyStroke(79, 2));
/*  96:    */     
/*  97:133 */     loadItem.addActionListener(new ActionListener()
/*  98:    */     {
/*  99:    */       public void actionPerformed(ActionEvent e)
/* 100:    */       {
/* 101:136 */         int retVal = fileChooser.showOpenDialog(PythonScriptExecutorStepEditorDialog.this);
/* 102:138 */         if (retVal == 0)
/* 103:    */         {
/* 104:140 */           StringBuilder sb = new StringBuilder();
/* 105:    */           try
/* 106:    */           {
/* 107:142 */             BufferedReader br = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
/* 108:    */             String line;
/* 109:145 */             while ((line = br.readLine()) != null) {
/* 110:146 */               sb.append(line).append("\n");
/* 111:    */             }
/* 112:150 */             PythonScriptExecutorStepEditorDialog.this.m_scriptEditor.setText(sb.toString());
/* 113:151 */             br.close();
/* 114:    */           }
/* 115:    */           catch (Exception ex)
/* 116:    */           {
/* 117:153 */             JOptionPane.showMessageDialog(PythonScriptExecutorStepEditorDialog.this, "Couldn't open file '" + fileChooser.getSelectedFile() + "'!");
/* 118:    */             
/* 119:    */ 
/* 120:156 */             ex.printStackTrace();
/* 121:    */           }
/* 122:    */         }
/* 123:    */       }
/* 124:161 */     });
/* 125:162 */     JMenuItem saveAsItem = new JMenuItem();
/* 126:163 */     fileM.add(saveAsItem);
/* 127:    */     
/* 128:165 */     saveAsItem.setText("Save As...");
/* 129:166 */     saveAsItem.setAccelerator(KeyStroke.getKeyStroke(65, 2));
/* 130:    */     
/* 131:168 */     saveAsItem.addActionListener(new ActionListener()
/* 132:    */     {
/* 133:    */       public void actionPerformed(ActionEvent e)
/* 134:    */       {
/* 135:172 */         if ((PythonScriptExecutorStepEditorDialog.this.m_scriptEditor.getText() != null) && (PythonScriptExecutorStepEditorDialog.this.m_scriptEditor.getText().length() > 0))
/* 136:    */         {
/* 137:175 */           int retVal = fileChooser.showSaveDialog(PythonScriptExecutorStepEditorDialog.this);
/* 138:177 */           if (retVal == 0) {
/* 139:    */             try
/* 140:    */             {
/* 141:179 */               BufferedWriter bw = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()));
/* 142:    */               
/* 143:181 */               bw.write(PythonScriptExecutorStepEditorDialog.this.m_scriptEditor.getText());
/* 144:182 */               bw.flush();
/* 145:183 */               bw.close();
/* 146:    */             }
/* 147:    */             catch (Exception ex)
/* 148:    */             {
/* 149:185 */               JOptionPane.showMessageDialog(PythonScriptExecutorStepEditorDialog.this, "Unable to save script file '" + fileChooser.getSelectedFile() + "'!");
/* 150:    */               
/* 151:    */ 
/* 152:    */ 
/* 153:189 */               ex.printStackTrace();
/* 154:    */             }
/* 155:    */           }
/* 156:    */         }
/* 157:    */       }
/* 158:195 */     });
/* 159:196 */     JPanel p = new JPanel(new BorderLayout());
/* 160:197 */     JScrollPane editorScroller = new JScrollPane(this.m_scriptEditor);
/* 161:198 */     this.m_scriptEditor.setContentType("text/python");
/* 162:199 */     editorScroller.setBorder(BorderFactory.createTitledBorder("Python Script"));
/* 163:200 */     p.add(editorScroller, "Center");
/* 164:201 */     Dimension d = new Dimension(450, 200);
/* 165:202 */     editorScroller.setMinimumSize(d);
/* 166:203 */     editorScroller.setPreferredSize(d);
/* 167:    */     
/* 168:205 */     this.m_primaryEditorHolder.add(p, "Center");
/* 169:206 */     add(this.m_editorHolder, "Center");
/* 170:    */     try
/* 171:    */     {
/* 172:209 */       if (this.m_pyAvailable)
/* 173:    */       {
/* 174:210 */         this.m_scriptEditor.setText(script);
/* 175:    */       }
/* 176:    */       else
/* 177:    */       {
/* 178:212 */         String message = "Python does not seem to be available:\n\n" + (envEvalEx != null ? envEvalEx.getMessage() : (envEvalResults != null) && (envEvalResults.length() > 0) ? envEvalResults : "");
/* 179:    */         
/* 180:    */ 
/* 181:    */ 
/* 182:216 */         this.m_scriptEditor.getDocument().insertString(0, message, null);
/* 183:    */       }
/* 184:    */     }
/* 185:    */     catch (BadLocationException ex)
/* 186:    */     {
/* 187:219 */       ex.printStackTrace();
/* 188:    */     }
/* 189:222 */     if ((this.m_parent instanceof JDialog)) {
/* 190:223 */       ((JDialog)this.m_parent).setJMenuBar(this.m_menuBar);
/* 191:    */     }
/* 192:    */   }
/* 193:    */   
/* 194:    */   protected void okPressed()
/* 195:    */   {
/* 196:233 */     if (!this.m_pyAvailable) {
/* 197:234 */       return;
/* 198:    */     }
/* 199:237 */     ((PythonScriptExecutor)getStepToEdit()).setPythonScript(this.m_scriptEditor.getText());
/* 200:    */   }
/* 201:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.PythonScriptExecutorStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */