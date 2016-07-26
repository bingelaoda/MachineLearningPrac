/*   1:    */ package weka.gui.experiment;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.FlowLayout;
/*   5:    */ import java.awt.event.ActionEvent;
/*   6:    */ import java.awt.event.ActionListener;
/*   7:    */ import java.beans.PropertyChangeListener;
/*   8:    */ import java.io.PrintStream;
/*   9:    */ import javax.swing.JComboBox;
/*  10:    */ import javax.swing.JLabel;
/*  11:    */ import javax.swing.JPanel;
/*  12:    */ import weka.experiment.Experiment;
/*  13:    */ 
/*  14:    */ public class SetupModePanel
/*  15:    */   extends JPanel
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = -3758035565520727822L;
/*  18: 48 */   protected AbstractSetupPanel[] m_Panels = AbstractSetupPanel.getPanels();
/*  19:    */   protected JComboBox m_ComboBoxPanels;
/*  20: 54 */   protected AbstractSetupPanel m_defaultPanel = null;
/*  21: 57 */   protected AbstractSetupPanel m_advancedPanel = null;
/*  22:    */   protected AbstractSetupPanel m_CurrentPanel;
/*  23:    */   
/*  24:    */   public SetupModePanel()
/*  25:    */   {
/*  26: 68 */     if (this.m_Panels.length == 0)
/*  27:    */     {
/*  28: 69 */       System.err.println("No experimenter setup panels discovered? Using fallback (simple, advanced).");
/*  29: 70 */       this.m_Panels = new AbstractSetupPanel[] { new SetupPanel(), new SimpleSetupPanel() };
/*  30:    */     }
/*  31: 76 */     for (AbstractSetupPanel panel : this.m_Panels)
/*  32:    */     {
/*  33: 77 */       if (panel.getClass().getName().equals(ExperimenterDefaults.getSetupPanel())) {
/*  34: 78 */         this.m_defaultPanel = panel;
/*  35:    */       }
/*  36: 79 */       if ((panel instanceof SetupPanel)) {
/*  37: 80 */         this.m_advancedPanel = panel;
/*  38:    */       }
/*  39: 81 */       panel.setModePanel(this);
/*  40:    */     }
/*  41: 85 */     if (this.m_defaultPanel == null) {
/*  42: 86 */       for (AbstractSetupPanel panel : this.m_Panels) {
/*  43: 87 */         if ((panel instanceof SimpleSetupPanel)) {
/*  44: 88 */           this.m_defaultPanel = panel;
/*  45:    */         }
/*  46:    */       }
/*  47:    */     }
/*  48: 92 */     this.m_CurrentPanel = this.m_defaultPanel;
/*  49:    */     
/*  50: 94 */     this.m_ComboBoxPanels = new JComboBox(this.m_Panels);
/*  51: 95 */     this.m_ComboBoxPanels.setSelectedItem(this.m_defaultPanel);
/*  52: 96 */     this.m_ComboBoxPanels.addActionListener(new ActionListener()
/*  53:    */     {
/*  54:    */       public void actionPerformed(ActionEvent e)
/*  55:    */       {
/*  56: 99 */         if (SetupModePanel.this.m_ComboBoxPanels.getSelectedIndex() == -1) {
/*  57:100 */           return;
/*  58:    */         }
/*  59:101 */         AbstractSetupPanel panel = (AbstractSetupPanel)SetupModePanel.this.m_ComboBoxPanels.getSelectedItem();
/*  60:102 */         SetupModePanel.this.switchTo(panel, null);
/*  61:    */       }
/*  62:105 */     });
/*  63:106 */     JPanel switchPanel = new JPanel();
/*  64:107 */     switchPanel.setLayout(new FlowLayout(0));
/*  65:108 */     switchPanel.add(new JLabel("Experiment Configuration Mode"));
/*  66:109 */     switchPanel.add(this.m_ComboBoxPanels);
/*  67:    */     
/*  68:111 */     setLayout(new BorderLayout());
/*  69:112 */     add(switchPanel, "North");
/*  70:113 */     add(this.m_defaultPanel, "Center");
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void switchToAdvanced(Experiment exp)
/*  74:    */   {
/*  75:122 */     switchTo(this.m_advancedPanel, exp);
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void switchTo(AbstractSetupPanel panel, Experiment exp)
/*  79:    */   {
/*  80:132 */     if (exp == null) {
/*  81:133 */       exp = this.m_CurrentPanel.getExperiment();
/*  82:    */     }
/*  83:135 */     remove(this.m_CurrentPanel);
/*  84:136 */     this.m_CurrentPanel.cleanUpAfterSwitch();
/*  85:138 */     if (exp != null) {
/*  86:139 */       panel.setExperiment(exp);
/*  87:    */     }
/*  88:140 */     add(panel, "Center");
/*  89:141 */     validate();
/*  90:142 */     repaint();
/*  91:    */     
/*  92:144 */     this.m_CurrentPanel = panel;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void addPropertyChangeListener(PropertyChangeListener l)
/*  96:    */   {
/*  97:153 */     if (this.m_Panels != null) {
/*  98:154 */       for (AbstractSetupPanel panel : this.m_Panels) {
/*  99:155 */         panel.addPropertyChangeListener(l);
/* 100:    */       }
/* 101:    */     }
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void removePropertyChangeListener(PropertyChangeListener l)
/* 105:    */   {
/* 106:165 */     if (this.m_Panels != null) {
/* 107:166 */       for (AbstractSetupPanel panel : this.m_Panels) {
/* 108:167 */         panel.removePropertyChangeListener(l);
/* 109:    */       }
/* 110:    */     }
/* 111:    */   }
/* 112:    */   
/* 113:    */   public Experiment getExperiment()
/* 114:    */   {
/* 115:177 */     return this.m_CurrentPanel.getExperiment();
/* 116:    */   }
/* 117:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.experiment.SetupModePanel
 * JD-Core Version:    0.7.0.1
 */