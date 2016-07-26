/*   1:    */ package weka.gui.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.GridLayout;
/*   5:    */ import java.awt.Window;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.awt.event.WindowAdapter;
/*   9:    */ import java.awt.event.WindowEvent;
/*  10:    */ import java.io.IOException;
/*  11:    */ import javax.swing.JButton;
/*  12:    */ import javax.swing.JPanel;
/*  13:    */ import weka.core.Defaults;
/*  14:    */ import weka.core.Settings;
/*  15:    */ import weka.gui.GUIApplication;
/*  16:    */ import weka.gui.SettingsEditor;
/*  17:    */ import weka.knowledgeflow.steps.Step;
/*  18:    */ 
/*  19:    */ public abstract class BaseInteractiveViewer
/*  20:    */   extends JPanel
/*  21:    */   implements StepInteractiveViewer
/*  22:    */ {
/*  23:    */   private static final long serialVersionUID = -1191494001428785466L;
/*  24:    */   protected Step m_step;
/*  25: 63 */   protected JButton m_closeBut = new JButton("Close");
/*  26: 66 */   protected JPanel m_buttonHolder = new JPanel(new GridLayout());
/*  27:    */   protected Window m_parent;
/*  28:    */   protected MainKFPerspective m_mainPerspective;
/*  29:    */   
/*  30:    */   public BaseInteractiveViewer()
/*  31:    */   {
/*  32: 79 */     setLayout(new BorderLayout());
/*  33:    */     
/*  34: 81 */     JPanel tempP = new JPanel(new BorderLayout());
/*  35: 82 */     tempP.add(this.m_buttonHolder, "West");
/*  36: 83 */     add(tempP, "South");
/*  37:    */     
/*  38: 85 */     this.m_closeBut.addActionListener(new ActionListener()
/*  39:    */     {
/*  40:    */       public void actionPerformed(ActionEvent e)
/*  41:    */       {
/*  42: 88 */         BaseInteractiveViewer.this.close();
/*  43:    */       }
/*  44: 90 */     });
/*  45: 91 */     addButton(this.m_closeBut);
/*  46: 93 */     if (getDefaultSettings() != null)
/*  47:    */     {
/*  48: 94 */       JButton editSettings = new JButton("Settings");
/*  49: 95 */       editSettings.addActionListener(new ActionListener()
/*  50:    */       {
/*  51:    */         public void actionPerformed(ActionEvent e)
/*  52:    */         {
/*  53:    */           try
/*  54:    */           {
/*  55:101 */             if (SettingsEditor.showSingleSettingsEditor(BaseInteractiveViewer.this.getMainKFPerspective().getMainApplication().getApplicationSettings(), BaseInteractiveViewer.this.getDefaultSettings().getID(), BaseInteractiveViewer.this.getViewerName(), BaseInteractiveViewer.this) == 0) {
/*  56:105 */               BaseInteractiveViewer.this.applySettings(BaseInteractiveViewer.this.getSettings());
/*  57:    */             }
/*  58:    */           }
/*  59:    */           catch (IOException ex)
/*  60:    */           {
/*  61:108 */             BaseInteractiveViewer.this.getMainKFPerspective().getMainApplication().showErrorDialog(ex);
/*  62:    */           }
/*  63:    */         }
/*  64:111 */       });
/*  65:112 */       addButton(editSettings);
/*  66:    */     }
/*  67:    */   }
/*  68:    */   
/*  69:    */   public Settings getSettings()
/*  70:    */   {
/*  71:123 */     return this.m_mainPerspective.getMainApplication().getApplicationSettings();
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void applySettings(Settings settings) {}
/*  75:    */   
/*  76:    */   public void setMainKFPerspective(MainKFPerspective perspective)
/*  77:    */   {
/*  78:145 */     this.m_mainPerspective = perspective;
/*  79:    */     
/*  80:147 */     this.m_mainPerspective.getMainApplication().getApplicationSettings().applyDefaults(getDefaultSettings());
/*  81:    */   }
/*  82:    */   
/*  83:    */   public MainKFPerspective getMainKFPerspective()
/*  84:    */   {
/*  85:159 */     return this.m_mainPerspective;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void setStep(Step step)
/*  89:    */   {
/*  90:170 */     this.m_step = step;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public Step getStep()
/*  94:    */   {
/*  95:179 */     return this.m_step;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void nowVisible() {}
/*  99:    */   
/* 100:    */   public void setParentWindow(Window parent)
/* 101:    */   {
/* 102:198 */     this.m_parent = parent;
/* 103:199 */     this.m_parent.addWindowListener(new WindowAdapter()
/* 104:    */     {
/* 105:    */       public void windowClosing(WindowEvent e)
/* 106:    */       {
/* 107:202 */         super.windowClosing(e);
/* 108:203 */         BaseInteractiveViewer.this.closePressed();
/* 109:    */       }
/* 110:    */     });
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void addButton(JButton button)
/* 114:    */   {
/* 115:214 */     this.m_buttonHolder.add(button);
/* 116:    */   }
/* 117:    */   
/* 118:    */   private void close()
/* 119:    */   {
/* 120:218 */     closePressed();
/* 121:220 */     if (this.m_parent != null) {
/* 122:221 */       this.m_parent.dispose();
/* 123:    */     }
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void closePressed() {}
/* 127:    */   
/* 128:    */   public Defaults getDefaultSettings()
/* 129:    */   {
/* 130:244 */     return null;
/* 131:    */   }
/* 132:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.BaseInteractiveViewer
 * JD-Core Version:    0.7.0.1
 */