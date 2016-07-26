/*   1:    */ package weka.gui.experiment;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.GridLayout;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.awt.event.WindowAdapter;
/*   9:    */ import java.awt.event.WindowEvent;
/*  10:    */ import java.io.PrintStream;
/*  11:    */ import javax.swing.BorderFactory;
/*  12:    */ import javax.swing.ButtonGroup;
/*  13:    */ import javax.swing.JButton;
/*  14:    */ import javax.swing.JCheckBox;
/*  15:    */ import javax.swing.JFrame;
/*  16:    */ import javax.swing.JPanel;
/*  17:    */ import javax.swing.JRadioButton;
/*  18:    */ import weka.experiment.Experiment;
/*  19:    */ import weka.experiment.RemoteExperiment;
/*  20:    */ 
/*  21:    */ public class DistributeExperimentPanel
/*  22:    */   extends JPanel
/*  23:    */ {
/*  24:    */   private static final long serialVersionUID = 5206721431754800278L;
/*  25: 58 */   RemoteExperiment m_Exp = null;
/*  26: 61 */   protected JCheckBox m_enableDistributedExperiment = new JCheckBox();
/*  27: 65 */   protected JButton m_configureHostNames = new JButton("Hosts");
/*  28: 68 */   protected HostListPanel m_hostList = new HostListPanel();
/*  29: 73 */   protected JRadioButton m_splitByDataSet = new JRadioButton("By data set");
/*  30: 78 */   protected JRadioButton m_splitByRun = new JRadioButton("By run");
/*  31: 83 */   protected JRadioButton m_splitByProperty = new JRadioButton("By property");
/*  32: 86 */   ActionListener m_radioListener = new ActionListener()
/*  33:    */   {
/*  34:    */     public void actionPerformed(ActionEvent e)
/*  35:    */     {
/*  36: 88 */       DistributeExperimentPanel.this.updateRadioLinks();
/*  37:    */     }
/*  38:    */   };
/*  39:    */   
/*  40:    */   public DistributeExperimentPanel()
/*  41:    */   {
/*  42: 96 */     this.m_enableDistributedExperiment.setSelected(false);
/*  43: 97 */     this.m_enableDistributedExperiment.setToolTipText("Allow this experiment to be distributed to remote hosts");
/*  44:    */     
/*  45: 99 */     this.m_enableDistributedExperiment.setEnabled(false);
/*  46:100 */     this.m_configureHostNames.setEnabled(false);
/*  47:101 */     this.m_configureHostNames.setToolTipText("Edit the list of remote hosts");
/*  48:    */     
/*  49:103 */     this.m_enableDistributedExperiment.addActionListener(new ActionListener()
/*  50:    */     {
/*  51:    */       public void actionPerformed(ActionEvent e)
/*  52:    */       {
/*  53:105 */         DistributeExperimentPanel.this.m_configureHostNames.setEnabled(DistributeExperimentPanel.this.m_enableDistributedExperiment.isSelected());
/*  54:    */         
/*  55:107 */         DistributeExperimentPanel.this.m_splitByDataSet.setEnabled(DistributeExperimentPanel.this.m_enableDistributedExperiment.isSelected());
/*  56:    */         
/*  57:109 */         DistributeExperimentPanel.this.m_splitByRun.setEnabled(DistributeExperimentPanel.this.m_enableDistributedExperiment.isSelected());
/*  58:    */         
/*  59:111 */         DistributeExperimentPanel.this.m_splitByProperty.setEnabled(DistributeExperimentPanel.this.m_enableDistributedExperiment.isSelected());
/*  60:    */       }
/*  61:115 */     });
/*  62:116 */     this.m_configureHostNames.addActionListener(new ActionListener()
/*  63:    */     {
/*  64:    */       public void actionPerformed(ActionEvent e)
/*  65:    */       {
/*  66:118 */         DistributeExperimentPanel.this.popupHostPanel();
/*  67:    */       }
/*  68:121 */     });
/*  69:122 */     this.m_splitByDataSet.setToolTipText("Distribute experiment by data set");
/*  70:123 */     this.m_splitByRun.setToolTipText("Distribute experiment by run number");
/*  71:124 */     this.m_splitByProperty.setToolTipText("Distribute experiment by property");
/*  72:125 */     this.m_splitByDataSet.setSelected(true);
/*  73:126 */     this.m_splitByDataSet.setEnabled(false);
/*  74:127 */     this.m_splitByRun.setEnabled(false);
/*  75:128 */     this.m_splitByProperty.setEnabled(false);
/*  76:129 */     this.m_splitByDataSet.addActionListener(this.m_radioListener);
/*  77:130 */     this.m_splitByRun.addActionListener(this.m_radioListener);
/*  78:131 */     this.m_splitByProperty.addActionListener(this.m_radioListener);
/*  79:    */     
/*  80:133 */     ButtonGroup bg = new ButtonGroup();
/*  81:134 */     bg.add(this.m_splitByDataSet);
/*  82:135 */     bg.add(this.m_splitByRun);
/*  83:136 */     bg.add(this.m_splitByProperty);
/*  84:    */     
/*  85:138 */     JPanel rbuts = new JPanel();
/*  86:139 */     rbuts.setLayout(new GridLayout(1, 2));
/*  87:140 */     rbuts.add(this.m_splitByDataSet);
/*  88:141 */     rbuts.add(this.m_splitByRun);
/*  89:142 */     rbuts.add(this.m_splitByProperty);
/*  90:    */     
/*  91:144 */     setLayout(new BorderLayout());
/*  92:145 */     setBorder(BorderFactory.createTitledBorder("Distribute experiment"));
/*  93:146 */     add(this.m_enableDistributedExperiment, "West");
/*  94:147 */     add(this.m_configureHostNames, "Center");
/*  95:148 */     add(rbuts, "South");
/*  96:    */   }
/*  97:    */   
/*  98:    */   public DistributeExperimentPanel(Experiment exp)
/*  99:    */   {
/* 100:157 */     this();
/* 101:158 */     setExperiment(exp);
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void setExperiment(Experiment exp)
/* 105:    */   {
/* 106:168 */     this.m_enableDistributedExperiment.setEnabled(true);
/* 107:169 */     this.m_Exp = null;
/* 108:170 */     if ((exp instanceof RemoteExperiment))
/* 109:    */     {
/* 110:171 */       this.m_Exp = ((RemoteExperiment)exp);
/* 111:172 */       this.m_enableDistributedExperiment.setSelected(true);
/* 112:173 */       this.m_configureHostNames.setEnabled(true);
/* 113:174 */       this.m_hostList.setExperiment(this.m_Exp);
/* 114:175 */       this.m_splitByDataSet.setEnabled(true);
/* 115:176 */       this.m_splitByRun.setEnabled(true);
/* 116:177 */       this.m_splitByProperty.setEnabled(true);
/* 117:178 */       this.m_splitByDataSet.setSelected(this.m_Exp.getSplitByDataSet());
/* 118:179 */       this.m_splitByRun.setSelected((!this.m_Exp.getSplitByDataSet()) && (!this.m_Exp.getSplitByProperty()));
/* 119:180 */       this.m_splitByProperty.setSelected(this.m_Exp.getSplitByProperty());
/* 120:    */     }
/* 121:    */   }
/* 122:    */   
/* 123:    */   private void popupHostPanel()
/* 124:    */   {
/* 125:    */     try
/* 126:    */     {
/* 127:189 */       final JFrame jf = new JFrame("Edit host names");
/* 128:    */       
/* 129:191 */       jf.getContentPane().setLayout(new BorderLayout());
/* 130:192 */       jf.getContentPane().add(this.m_hostList, "Center");
/* 131:    */       
/* 132:194 */       jf.addWindowListener(new WindowAdapter()
/* 133:    */       {
/* 134:    */         public void windowClosing(WindowEvent e)
/* 135:    */         {
/* 136:196 */           jf.dispose();
/* 137:    */         }
/* 138:198 */       });
/* 139:199 */       jf.pack();
/* 140:200 */       jf.setVisible(true);
/* 141:    */     }
/* 142:    */     catch (Exception ex)
/* 143:    */     {
/* 144:202 */       ex.printStackTrace();
/* 145:203 */       System.err.println(ex.getMessage());
/* 146:    */     }
/* 147:    */   }
/* 148:    */   
/* 149:    */   public boolean distributedExperimentSelected()
/* 150:    */   {
/* 151:212 */     return this.m_enableDistributedExperiment.isSelected();
/* 152:    */   }
/* 153:    */   
/* 154:    */   public void addCheckBoxActionListener(ActionListener al)
/* 155:    */   {
/* 156:220 */     this.m_enableDistributedExperiment.addActionListener(al);
/* 157:    */   }
/* 158:    */   
/* 159:    */   private void updateRadioLinks()
/* 160:    */   {
/* 161:227 */     if (this.m_Exp != null)
/* 162:    */     {
/* 163:228 */       this.m_Exp.setSplitByDataSet(this.m_splitByDataSet.isSelected());
/* 164:229 */       this.m_Exp.setSplitByProperty(this.m_splitByProperty.isSelected());
/* 165:    */     }
/* 166:    */   }
/* 167:    */   
/* 168:    */   public static void main(String[] args)
/* 169:    */   {
/* 170:    */     try
/* 171:    */     {
/* 172:240 */       JFrame jf = new JFrame("DistributeExperiment");
/* 173:241 */       jf.getContentPane().setLayout(new BorderLayout());
/* 174:242 */       jf.getContentPane().add(new DistributeExperimentPanel(new Experiment()), "Center");
/* 175:    */       
/* 176:244 */       jf.addWindowListener(new WindowAdapter()
/* 177:    */       {
/* 178:    */         public void windowClosing(WindowEvent e)
/* 179:    */         {
/* 180:246 */           this.val$jf.dispose();
/* 181:247 */           System.exit(0);
/* 182:    */         }
/* 183:249 */       });
/* 184:250 */       jf.pack();
/* 185:251 */       jf.setVisible(true);
/* 186:    */     }
/* 187:    */     catch (Exception ex)
/* 188:    */     {
/* 189:253 */       ex.printStackTrace();
/* 190:254 */       System.err.println(ex.getMessage());
/* 191:    */     }
/* 192:    */   }
/* 193:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.experiment.DistributeExperimentPanel
 * JD-Core Version:    0.7.0.1
 */