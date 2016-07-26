/*   1:    */ package weka.gui.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.Font;
/*   6:    */ import java.awt.GridLayout;
/*   7:    */ import java.awt.event.ActionEvent;
/*   8:    */ import java.awt.event.ActionListener;
/*   9:    */ import java.util.ArrayList;
/*  10:    */ import java.util.List;
/*  11:    */ import java.util.Map;
/*  12:    */ import java.util.Vector;
/*  13:    */ import javax.swing.BorderFactory;
/*  14:    */ import javax.swing.DefaultComboBoxModel;
/*  15:    */ import javax.swing.JComboBox;
/*  16:    */ import javax.swing.JLabel;
/*  17:    */ import javax.swing.JPanel;
/*  18:    */ import javax.swing.JScrollPane;
/*  19:    */ import weka.core.Attribute;
/*  20:    */ import weka.core.Defaults;
/*  21:    */ import weka.core.Environment;
/*  22:    */ import weka.core.Instances;
/*  23:    */ import weka.core.Settings;
/*  24:    */ import weka.core.Settings.SettingKey;
/*  25:    */ import weka.gui.AbstractPerspective;
/*  26:    */ import weka.gui.AttributeVisualizationPanel;
/*  27:    */ import weka.gui.GUIApplication;
/*  28:    */ import weka.gui.PerspectiveInfo;
/*  29:    */ 
/*  30:    */ @PerspectiveInfo(ID="attributesummary", title="Attribute summary", toolTipText="Histogram summary charts", iconPath="weka/gui/knowledgeflow/icons/chart_bar.png")
/*  31:    */ public class AttributeSummaryPerspective
/*  32:    */   extends AbstractPerspective
/*  33:    */ {
/*  34:    */   private static final long serialVersionUID = 6697308901346612850L;
/*  35:    */   protected Instances m_visualizeDataSet;
/*  36:    */   protected transient List<AttributeVisualizationPanel> m_plots;
/*  37: 62 */   protected int m_coloringIndex = -1;
/*  38:    */   
/*  39:    */   public AttributeSummaryPerspective()
/*  40:    */   {
/*  41: 68 */     setLayout(new BorderLayout());
/*  42:    */   }
/*  43:    */   
/*  44:    */   protected void setup(Settings settings)
/*  45:    */   {
/*  46: 77 */     removeAll();
/*  47: 78 */     if (this.m_visualizeDataSet == null) {
/*  48: 79 */       return;
/*  49:    */     }
/*  50: 82 */     JScrollPane hp = makePanel(settings == null ? this.m_mainApplication.getApplicationSettings() : settings);
/*  51:    */     
/*  52:    */ 
/*  53: 85 */     add(hp, "Center");
/*  54:    */     
/*  55: 87 */     Vector<String> atts = new Vector();
/*  56: 88 */     for (int i = 0; i < this.m_visualizeDataSet.numAttributes(); i++) {
/*  57: 89 */       atts.add("(" + Attribute.typeToStringShort(this.m_visualizeDataSet.attribute(i)) + ") " + this.m_visualizeDataSet.attribute(i).name());
/*  58:    */     }
/*  59: 94 */     final JComboBox<String> classCombo = new JComboBox();
/*  60: 95 */     classCombo.setModel(new DefaultComboBoxModel(atts));
/*  61: 97 */     if (atts.size() > 0)
/*  62:    */     {
/*  63: 98 */       if (this.m_visualizeDataSet.classIndex() < 0) {
/*  64: 99 */         classCombo.setSelectedIndex(atts.size() - 1);
/*  65:    */       } else {
/*  66:101 */         classCombo.setSelectedIndex(this.m_visualizeDataSet.classIndex());
/*  67:    */       }
/*  68:103 */       classCombo.setEnabled(true);
/*  69:104 */       for (int i = 0; i < this.m_plots.size(); i++) {
/*  70:105 */         ((AttributeVisualizationPanel)this.m_plots.get(i)).setColoringIndex(classCombo.getSelectedIndex());
/*  71:    */       }
/*  72:    */     }
/*  73:109 */     JPanel comboHolder = new JPanel();
/*  74:110 */     comboHolder.setLayout(new BorderLayout());
/*  75:111 */     JPanel tempHolder = new JPanel();
/*  76:112 */     tempHolder.setLayout(new BorderLayout());
/*  77:113 */     tempHolder.add(new JLabel("Class: "), "West");
/*  78:114 */     tempHolder.add(classCombo, "East");
/*  79:115 */     comboHolder.add(tempHolder, "West");
/*  80:116 */     add(comboHolder, "North");
/*  81:    */     
/*  82:118 */     classCombo.addActionListener(new ActionListener()
/*  83:    */     {
/*  84:    */       public void actionPerformed(ActionEvent e)
/*  85:    */       {
/*  86:121 */         int selected = classCombo.getSelectedIndex();
/*  87:122 */         if (selected >= 0) {
/*  88:123 */           for (int i = 0; i < AttributeSummaryPerspective.this.m_plots.size(); i++) {
/*  89:124 */             ((AttributeVisualizationPanel)AttributeSummaryPerspective.this.m_plots.get(i)).setColoringIndex(selected);
/*  90:    */           }
/*  91:    */         }
/*  92:    */       }
/*  93:    */     });
/*  94:    */   }
/*  95:    */   
/*  96:    */   private JScrollPane makePanel(Settings settings)
/*  97:    */   {
/*  98:139 */     String fontFamily = getFont().getFamily();
/*  99:140 */     Font newFont = new Font(fontFamily, 0, 10);
/* 100:141 */     JPanel hp = new JPanel();
/* 101:142 */     hp.setFont(newFont);
/* 102:143 */     int gridWidth = ((Integer)settings.getSetting("attributesummary", AttDefaults.GRID_WIDTH_KEY, Integer.valueOf(4), Environment.getSystemWide())).intValue();
/* 103:    */     
/* 104:    */ 
/* 105:146 */     int maxPlots = ((Integer)settings.getSetting("attributesummary", AttDefaults.MAX_PLOTS_KEY, Integer.valueOf(100), Environment.getSystemWide())).intValue();
/* 106:    */     
/* 107:    */ 
/* 108:149 */     int numPlots = Math.min(this.m_visualizeDataSet.numAttributes(), maxPlots);
/* 109:150 */     int gridHeight = numPlots / gridWidth;
/* 110:152 */     if (numPlots % gridWidth != 0) {
/* 111:153 */       gridHeight++;
/* 112:    */     }
/* 113:155 */     hp.setLayout(new GridLayout(gridHeight, 4));
/* 114:    */     
/* 115:157 */     this.m_plots = new ArrayList();
/* 116:159 */     for (int i = 0; i < numPlots; i++)
/* 117:    */     {
/* 118:160 */       JPanel temp = new JPanel();
/* 119:161 */       temp.setLayout(new BorderLayout());
/* 120:162 */       temp.setBorder(BorderFactory.createTitledBorder(this.m_visualizeDataSet.attribute(i).name()));
/* 121:    */       
/* 122:    */ 
/* 123:165 */       AttributeVisualizationPanel ap = new AttributeVisualizationPanel();
/* 124:166 */       this.m_plots.add(ap);
/* 125:167 */       ap.setInstances(this.m_visualizeDataSet);
/* 126:168 */       if ((this.m_coloringIndex < 0) && (this.m_visualizeDataSet.classIndex() >= 0)) {
/* 127:169 */         ap.setColoringIndex(this.m_visualizeDataSet.classIndex());
/* 128:    */       } else {
/* 129:171 */         ap.setColoringIndex(this.m_coloringIndex);
/* 130:    */       }
/* 131:173 */       temp.add(ap, "Center");
/* 132:174 */       ap.setAttribute(i);
/* 133:175 */       hp.add(temp);
/* 134:    */     }
/* 135:178 */     Dimension d = new Dimension(830, gridHeight * 100);
/* 136:179 */     hp.setMinimumSize(d);
/* 137:180 */     hp.setMaximumSize(d);
/* 138:181 */     hp.setPreferredSize(d);
/* 139:    */     
/* 140:183 */     JScrollPane scroller = new JScrollPane(hp);
/* 141:    */     
/* 142:185 */     return scroller;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public Defaults getDefaultSettings()
/* 146:    */   {
/* 147:195 */     return new AttDefaults();
/* 148:    */   }
/* 149:    */   
/* 150:    */   public void setInstances(Instances instances)
/* 151:    */   {
/* 152:205 */     this.m_visualizeDataSet = instances;
/* 153:206 */     setup(null);
/* 154:    */   }
/* 155:    */   
/* 156:    */   public void setInstances(Instances instances, Settings settings)
/* 157:    */   {
/* 158:216 */     this.m_visualizeDataSet = instances;
/* 159:217 */     setup(settings);
/* 160:    */   }
/* 161:    */   
/* 162:    */   public boolean okToBeActive()
/* 163:    */   {
/* 164:230 */     return this.m_visualizeDataSet != null;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public boolean acceptsInstances()
/* 168:    */   {
/* 169:240 */     return true;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public static class AttDefaults
/* 173:    */     extends Defaults
/* 174:    */   {
/* 175:    */     public static final String ID = "attributesummary";
/* 176:250 */     protected static final Settings.SettingKey GRID_WIDTH_KEY = new Settings.SettingKey("weka.knowledgeflow.attributesummary.gridWidth", "Number of plots to display horizontally", "");
/* 177:    */     protected static final int GRID_WIDTH = 4;
/* 178:256 */     protected static final Settings.SettingKey MAX_PLOTS_KEY = new Settings.SettingKey("weka.knowledgeflow.attributesummary.maxPlots", "Maximum number of plots to render", "");
/* 179:    */     protected static final int MAX_PLOTS = 100;
/* 180:    */     private static final long serialVersionUID = -32801466385262321L;
/* 181:    */     
/* 182:    */     public AttDefaults()
/* 183:    */     {
/* 184:264 */       super();
/* 185:    */       
/* 186:266 */       this.m_defaults.put(GRID_WIDTH_KEY, Integer.valueOf(4));
/* 187:267 */       this.m_defaults.put(MAX_PLOTS_KEY, Integer.valueOf(100));
/* 188:    */     }
/* 189:    */   }
/* 190:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.AttributeSummaryPerspective
 * JD-Core Version:    0.7.0.1
 */