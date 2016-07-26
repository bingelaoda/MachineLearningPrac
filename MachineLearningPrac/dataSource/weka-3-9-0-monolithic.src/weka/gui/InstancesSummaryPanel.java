/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.GridBagConstraints;
/*   6:    */ import java.awt.GridBagLayout;
/*   7:    */ import java.awt.event.WindowAdapter;
/*   8:    */ import java.awt.event.WindowEvent;
/*   9:    */ import java.io.BufferedReader;
/*  10:    */ import java.io.FileReader;
/*  11:    */ import java.io.PrintStream;
/*  12:    */ import java.io.Reader;
/*  13:    */ import javax.swing.BorderFactory;
/*  14:    */ import javax.swing.JFrame;
/*  15:    */ import javax.swing.JLabel;
/*  16:    */ import javax.swing.JPanel;
/*  17:    */ import weka.core.Instances;
/*  18:    */ import weka.core.Utils;
/*  19:    */ 
/*  20:    */ public class InstancesSummaryPanel
/*  21:    */   extends JPanel
/*  22:    */ {
/*  23:    */   private static final long serialVersionUID = -5243579535296681063L;
/*  24:    */   protected static final String NO_SOURCE = "None";
/*  25: 52 */   protected JLabel m_RelationNameLab = new JLabel("None");
/*  26: 55 */   protected JLabel m_NumInstancesLab = new JLabel("None");
/*  27: 58 */   protected JLabel m_NumAttributesLab = new JLabel("None");
/*  28: 61 */   protected JLabel m_sumOfWeightsLab = new JLabel("None");
/*  29:    */   protected Instances m_Instances;
/*  30: 73 */   protected boolean m_showZeroInstancesAsUnknown = false;
/*  31:    */   
/*  32:    */   public InstancesSummaryPanel()
/*  33:    */   {
/*  34: 80 */     GridBagLayout gbLayout = new GridBagLayout();
/*  35: 81 */     setLayout(gbLayout);
/*  36: 82 */     JLabel lab = new JLabel("Relation:", 4);
/*  37: 83 */     lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
/*  38: 84 */     GridBagConstraints gbConstraints = new GridBagConstraints();
/*  39: 85 */     gbConstraints.anchor = 13;
/*  40: 86 */     gbConstraints.fill = 2;
/*  41: 87 */     gbConstraints.gridy = 0;
/*  42: 88 */     gbConstraints.gridx = 0;
/*  43: 89 */     gbLayout.setConstraints(lab, gbConstraints);
/*  44: 90 */     add(lab);
/*  45: 91 */     gbConstraints = new GridBagConstraints();
/*  46: 92 */     gbConstraints.anchor = 17;
/*  47: 93 */     gbConstraints.fill = 2;
/*  48: 94 */     gbConstraints.gridy = 0;
/*  49: 95 */     gbConstraints.gridx = 1;
/*  50: 96 */     gbConstraints.weightx = 100.0D;
/*  51:    */     
/*  52: 98 */     gbLayout.setConstraints(this.m_RelationNameLab, gbConstraints);
/*  53: 99 */     add(this.m_RelationNameLab);
/*  54:100 */     this.m_RelationNameLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
/*  55:    */     
/*  56:102 */     lab = new JLabel("Instances:", 4);
/*  57:103 */     lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
/*  58:104 */     gbConstraints = new GridBagConstraints();
/*  59:105 */     gbConstraints.anchor = 13;
/*  60:106 */     gbConstraints.fill = 2;
/*  61:107 */     gbConstraints.gridy = 1;
/*  62:108 */     gbConstraints.gridx = 0;
/*  63:109 */     gbLayout.setConstraints(lab, gbConstraints);
/*  64:110 */     add(lab);
/*  65:111 */     gbConstraints = new GridBagConstraints();
/*  66:112 */     gbConstraints.anchor = 17;
/*  67:113 */     gbConstraints.fill = 2;
/*  68:114 */     gbConstraints.gridy = 1;
/*  69:115 */     gbConstraints.gridx = 1;
/*  70:116 */     gbConstraints.weightx = 100.0D;
/*  71:117 */     gbLayout.setConstraints(this.m_NumInstancesLab, gbConstraints);
/*  72:118 */     add(this.m_NumInstancesLab);
/*  73:119 */     this.m_NumInstancesLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
/*  74:    */     
/*  75:121 */     lab = new JLabel("Attributes:", 4);
/*  76:122 */     lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
/*  77:123 */     gbConstraints = new GridBagConstraints();
/*  78:124 */     gbConstraints.anchor = 13;
/*  79:125 */     gbConstraints.fill = 2;
/*  80:126 */     gbConstraints.gridy = 0;
/*  81:127 */     gbConstraints.gridx = 2;
/*  82:128 */     gbLayout.setConstraints(lab, gbConstraints);
/*  83:129 */     add(lab);
/*  84:130 */     gbConstraints = new GridBagConstraints();
/*  85:131 */     gbConstraints.anchor = 17;
/*  86:132 */     gbConstraints.fill = 2;
/*  87:133 */     gbConstraints.gridy = 0;
/*  88:134 */     gbConstraints.gridx = 3;
/*  89:    */     
/*  90:136 */     gbLayout.setConstraints(this.m_NumAttributesLab, gbConstraints);
/*  91:137 */     add(this.m_NumAttributesLab);
/*  92:138 */     this.m_NumAttributesLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
/*  93:    */     
/*  94:140 */     lab = new JLabel("Sum of weights:", 4);
/*  95:141 */     lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
/*  96:142 */     gbConstraints = new GridBagConstraints();
/*  97:143 */     gbConstraints.anchor = 13;
/*  98:144 */     gbConstraints.fill = 2;
/*  99:145 */     gbConstraints.gridy = 1;
/* 100:146 */     gbConstraints.gridx = 2;
/* 101:147 */     gbLayout.setConstraints(lab, gbConstraints);
/* 102:148 */     add(lab);
/* 103:149 */     gbConstraints = new GridBagConstraints();
/* 104:150 */     gbConstraints.anchor = 17;
/* 105:151 */     gbConstraints.fill = 2;
/* 106:152 */     gbConstraints.gridy = 1;
/* 107:153 */     gbConstraints.gridx = 3;
/* 108:    */     
/* 109:155 */     gbLayout.setConstraints(this.m_sumOfWeightsLab, gbConstraints);
/* 110:156 */     add(this.m_sumOfWeightsLab);
/* 111:157 */     this.m_sumOfWeightsLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void setShowZeroInstancesAsUnknown(boolean zeroAsUnknown)
/* 115:    */   {
/* 116:170 */     this.m_showZeroInstancesAsUnknown = zeroAsUnknown;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public boolean getShowZeroInstancesAsUnknown()
/* 120:    */   {
/* 121:181 */     return this.m_showZeroInstancesAsUnknown;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void setInstances(Instances inst)
/* 125:    */   {
/* 126:190 */     this.m_Instances = inst;
/* 127:191 */     this.m_RelationNameLab.setText(this.m_Instances.relationName());
/* 128:192 */     this.m_RelationNameLab.setToolTipText(this.m_Instances.relationName());
/* 129:193 */     this.m_NumInstancesLab.setText("" + ((this.m_showZeroInstancesAsUnknown) && (this.m_Instances.numInstances() == 0) ? "?" : new StringBuilder().append("").append(this.m_Instances.numInstances()).toString()));
/* 130:    */     
/* 131:    */ 
/* 132:    */ 
/* 133:197 */     this.m_NumAttributesLab.setText("" + this.m_Instances.numAttributes());
/* 134:198 */     this.m_sumOfWeightsLab.setText("" + ((this.m_showZeroInstancesAsUnknown) && (this.m_Instances.numInstances() == 0) ? "?" : new StringBuilder().append("").append(Utils.doubleToString(this.m_Instances.sumOfWeights(), 3)).toString()));
/* 135:    */   }
/* 136:    */   
/* 137:    */   public static void main(String[] args)
/* 138:    */   {
/* 139:    */     try
/* 140:    */     {
/* 141:212 */       JFrame jf = new JFrame("Instances Panel");
/* 142:213 */       jf.getContentPane().setLayout(new BorderLayout());
/* 143:214 */       InstancesSummaryPanel p = new InstancesSummaryPanel();
/* 144:215 */       p.setBorder(BorderFactory.createTitledBorder("Relation"));
/* 145:216 */       jf.getContentPane().add(p, "Center");
/* 146:217 */       jf.addWindowListener(new WindowAdapter()
/* 147:    */       {
/* 148:    */         public void windowClosing(WindowEvent e)
/* 149:    */         {
/* 150:220 */           this.val$jf.dispose();
/* 151:221 */           System.exit(0);
/* 152:    */         }
/* 153:223 */       });
/* 154:224 */       jf.pack();
/* 155:225 */       jf.setVisible(true);
/* 156:226 */       if (args.length == 1)
/* 157:    */       {
/* 158:227 */         Reader r = new BufferedReader(new FileReader(args[0]));
/* 159:    */         
/* 160:229 */         Instances i = new Instances(r);
/* 161:230 */         p.setInstances(i);
/* 162:    */       }
/* 163:    */     }
/* 164:    */     catch (Exception ex)
/* 165:    */     {
/* 166:233 */       ex.printStackTrace();
/* 167:234 */       System.err.println(ex.getMessage());
/* 168:    */     }
/* 169:    */   }
/* 170:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.InstancesSummaryPanel
 * JD-Core Version:    0.7.0.1
 */