/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.GridBagConstraints;
/*   5:    */ import java.awt.GridBagLayout;
/*   6:    */ import java.awt.Insets;
/*   7:    */ import java.awt.event.ActionEvent;
/*   8:    */ import java.awt.event.ActionListener;
/*   9:    */ import java.util.Set;
/*  10:    */ import javax.swing.BorderFactory;
/*  11:    */ import javax.swing.JComboBox;
/*  12:    */ import javax.swing.JLabel;
/*  13:    */ import javax.swing.JPanel;
/*  14:    */ import weka.core.PluginManager;
/*  15:    */ import weka.gui.EnvironmentField;
/*  16:    */ import weka.gui.PropertySheetPanel;
/*  17:    */ import weka.gui.beans.OffscreenChartRenderer;
/*  18:    */ import weka.gui.beans.WekaOffscreenChartRenderer;
/*  19:    */ import weka.gui.knowledgeflow.GOEStepEditorDialog;
/*  20:    */ import weka.knowledgeflow.steps.ModelPerformanceChart;
/*  21:    */ import weka.knowledgeflow.steps.Step;
/*  22:    */ 
/*  23:    */ public class ModelPerformanceChartStepEditorDialog
/*  24:    */   extends GOEStepEditorDialog
/*  25:    */ {
/*  26:    */   private static final long serialVersionUID = -3031265139980301695L;
/*  27: 50 */   protected EnvironmentField m_rendererOptions = new EnvironmentField();
/*  28: 53 */   protected JComboBox<String> m_offscreenSelector = new JComboBox();
/*  29:    */   protected String m_currentRendererName;
/*  30:    */   protected String m_currentRendererOptions;
/*  31:    */   
/*  32:    */   protected void setStepToEdit(Step step)
/*  33:    */   {
/*  34: 69 */     copyOriginal(step);
/*  35: 70 */     createAboutPanel(step);
/*  36: 71 */     this.m_editor = new PropertySheetPanel(false);
/*  37: 72 */     this.m_editor.setUseEnvironmentPropertyEditors(true);
/*  38: 73 */     this.m_editor.setEnvironment(this.m_env);
/*  39: 74 */     this.m_editor.setTarget(this.m_stepToEdit);
/*  40:    */     
/*  41: 76 */     this.m_primaryEditorHolder.setLayout(new BorderLayout());
/*  42: 77 */     this.m_primaryEditorHolder.add(this.m_editor, "Center");
/*  43:    */     
/*  44: 79 */     GridBagLayout gbLayout = new GridBagLayout();
/*  45: 80 */     JPanel p = new JPanel(gbLayout);
/*  46: 81 */     GridBagConstraints gbc = new GridBagConstraints();
/*  47: 82 */     gbc.anchor = 13;
/*  48: 83 */     gbc.fill = 2;
/*  49: 84 */     gbc.gridy = 0;
/*  50: 85 */     gbc.gridx = 0;
/*  51: 86 */     gbc.insets = new Insets(0, 5, 0, 5);
/*  52: 87 */     JLabel renderLabel = new JLabel("Renderer", 4);
/*  53: 88 */     gbLayout.setConstraints(renderLabel, gbc);
/*  54: 89 */     p.add(renderLabel);
/*  55:    */     
/*  56: 91 */     JPanel newPanel = new JPanel(new BorderLayout());
/*  57: 92 */     gbc = new GridBagConstraints();
/*  58: 93 */     gbc.anchor = 17;
/*  59: 94 */     gbc.fill = 1;
/*  60: 95 */     gbc.gridy = 0;
/*  61: 96 */     gbc.gridx = 1;
/*  62: 97 */     gbc.weightx = 100.0D;
/*  63: 98 */     newPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 10));
/*  64: 99 */     newPanel.add(this.m_offscreenSelector, "Center");
/*  65:100 */     gbLayout.setConstraints(newPanel, gbc);
/*  66:101 */     p.add(newPanel);
/*  67:    */     
/*  68:103 */     gbc = new GridBagConstraints();
/*  69:104 */     gbc.anchor = 13;
/*  70:105 */     gbc.fill = 2;
/*  71:106 */     gbc.gridy = 1;
/*  72:107 */     gbc.gridx = 0;
/*  73:108 */     gbc.insets = new Insets(0, 5, 0, 5);
/*  74:109 */     final JLabel rendererOptsLabel = new JLabel("Renderer options", 4);
/*  75:    */     
/*  76:111 */     gbLayout.setConstraints(rendererOptsLabel, gbc);
/*  77:112 */     p.add(rendererOptsLabel);
/*  78:    */     
/*  79:114 */     newPanel = new JPanel(new BorderLayout());
/*  80:115 */     gbc = new GridBagConstraints();
/*  81:116 */     gbc.anchor = 17;
/*  82:117 */     gbc.fill = 1;
/*  83:118 */     gbc.gridy = 1;
/*  84:119 */     gbc.gridx = 1;
/*  85:120 */     gbc.weightx = 100.0D;
/*  86:121 */     newPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 10));
/*  87:122 */     newPanel.add(this.m_rendererOptions, "Center");
/*  88:    */     
/*  89:124 */     gbLayout.setConstraints(newPanel, gbc);
/*  90:125 */     p.add(newPanel);
/*  91:    */     
/*  92:127 */     this.m_primaryEditorHolder.add(p, "North");
/*  93:    */     
/*  94:129 */     this.m_editorHolder.add(this.m_primaryEditorHolder, "North");
/*  95:130 */     add(this.m_editorHolder, "Center");
/*  96:    */     
/*  97:132 */     this.m_offscreenSelector.addItem("Weka Chart Renderer");
/*  98:133 */     Set<String> pluginRenderers = PluginManager.getPluginNamesOfType("weka.gui.beans.OffscreenChartRenderer");
/*  99:136 */     if (pluginRenderers != null) {
/* 100:137 */       for (String plugin : pluginRenderers) {
/* 101:138 */         this.m_offscreenSelector.addItem(plugin);
/* 102:    */       }
/* 103:    */     }
/* 104:142 */     this.m_offscreenSelector.addActionListener(new ActionListener()
/* 105:    */     {
/* 106:    */       public void actionPerformed(ActionEvent e)
/* 107:    */       {
/* 108:145 */         ModelPerformanceChartStepEditorDialog.this.setupRendererOptsTipText(rendererOptsLabel);
/* 109:    */       }
/* 110:148 */     });
/* 111:149 */     getCurrentSettings();
/* 112:150 */     this.m_offscreenSelector.setSelectedItem(this.m_currentRendererName);
/* 113:151 */     this.m_rendererOptions.setText(this.m_currentRendererOptions);
/* 114:152 */     setupRendererOptsTipText(rendererOptsLabel);
/* 115:    */   }
/* 116:    */   
/* 117:    */   protected void getCurrentSettings()
/* 118:    */   {
/* 119:160 */     this.m_currentRendererName = ((ModelPerformanceChart)getStepToEdit()).getOffscreenRendererName();
/* 120:    */     
/* 121:162 */     this.m_currentRendererOptions = ((ModelPerformanceChart)getStepToEdit()).getOffscreenAdditionalOpts();
/* 122:    */   }
/* 123:    */   
/* 124:    */   private void setupRendererOptsTipText(JLabel optsLab)
/* 125:    */   {
/* 126:167 */     String renderer = this.m_offscreenSelector.getSelectedItem().toString();
/* 127:168 */     if (renderer.equalsIgnoreCase("weka chart renderer"))
/* 128:    */     {
/* 129:170 */       WekaOffscreenChartRenderer rcr = new WekaOffscreenChartRenderer();
/* 130:171 */       String tipText = rcr.optionsTipTextHTML();
/* 131:172 */       tipText = tipText.replace("<html>", "<html>Comma separated list of options:<br>");
/* 132:    */       
/* 133:174 */       optsLab.setToolTipText(tipText);
/* 134:    */     }
/* 135:    */     else
/* 136:    */     {
/* 137:    */       try
/* 138:    */       {
/* 139:177 */         Object rendererO = PluginManager.getPluginInstance("weka.gui.beans.OffscreenChartRenderer", renderer);
/* 140:181 */         if (rendererO != null)
/* 141:    */         {
/* 142:182 */           String tipText = ((OffscreenChartRenderer)rendererO).optionsTipTextHTML();
/* 143:184 */           if ((tipText != null) && (tipText.length() > 0)) {
/* 144:185 */             optsLab.setToolTipText(tipText);
/* 145:    */           }
/* 146:    */         }
/* 147:    */       }
/* 148:    */       catch (Exception ex)
/* 149:    */       {
/* 150:189 */         showErrorDialog(ex);
/* 151:    */       }
/* 152:    */     }
/* 153:    */   }
/* 154:    */   
/* 155:    */   protected void okPressed()
/* 156:    */   {
/* 157:199 */     ((ModelPerformanceChart)getStepToEdit()).setOffscreenRendererName(this.m_offscreenSelector.getSelectedItem().toString());
/* 158:    */     
/* 159:    */ 
/* 160:202 */     ((ModelPerformanceChart)getStepToEdit()).setOffscreenAdditionalOpts(this.m_rendererOptions.getText());
/* 161:    */   }
/* 162:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.ModelPerformanceChartStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */