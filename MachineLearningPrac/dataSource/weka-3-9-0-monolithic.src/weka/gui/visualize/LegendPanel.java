/*   1:    */ package weka.gui.visualize;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Component;
/*   6:    */ import java.awt.Container;
/*   7:    */ import java.awt.Dimension;
/*   8:    */ import java.awt.Graphics;
/*   9:    */ import java.awt.GridBagConstraints;
/*  10:    */ import java.awt.GridBagLayout;
/*  11:    */ import java.awt.Insets;
/*  12:    */ import java.awt.event.MouseAdapter;
/*  13:    */ import java.awt.event.MouseEvent;
/*  14:    */ import java.awt.event.WindowAdapter;
/*  15:    */ import java.awt.event.WindowEvent;
/*  16:    */ import java.io.BufferedReader;
/*  17:    */ import java.io.FileReader;
/*  18:    */ import java.io.PrintStream;
/*  19:    */ import java.io.Reader;
/*  20:    */ import java.util.ArrayList;
/*  21:    */ import javax.swing.JColorChooser;
/*  22:    */ import javax.swing.JFrame;
/*  23:    */ import javax.swing.JLabel;
/*  24:    */ import javax.swing.JPanel;
/*  25:    */ import javax.swing.JScrollPane;
/*  26:    */ import javax.swing.ToolTipManager;
/*  27:    */ import weka.core.Instances;
/*  28:    */ 
/*  29:    */ public class LegendPanel
/*  30:    */   extends JScrollPane
/*  31:    */ {
/*  32:    */   private static final long serialVersionUID = -1262384440543001505L;
/*  33:    */   protected ArrayList<PlotData2D> m_plots;
/*  34: 60 */   protected JPanel m_span = null;
/*  35: 65 */   protected ArrayList<Component> m_Repainters = new ArrayList();
/*  36:    */   
/*  37:    */   protected class LegendEntry
/*  38:    */     extends JPanel
/*  39:    */   {
/*  40:    */     private static final long serialVersionUID = 3879990289042935670L;
/*  41: 76 */     private PlotData2D m_plotData = null;
/*  42:    */     private final int m_dataIndex;
/*  43:    */     private final JLabel m_legendText;
/*  44:    */     private final JPanel m_pointShape;
/*  45:    */     
/*  46:    */     public LegendEntry(PlotData2D data, int dataIndex)
/*  47:    */     {
/*  48: 91 */       ToolTipManager.sharedInstance().setDismissDelay(5000);
/*  49: 92 */       this.m_plotData = data;
/*  50: 93 */       this.m_dataIndex = dataIndex;
/*  51:100 */       if (this.m_plotData.m_useCustomColour) {
/*  52:101 */         addMouseListener(new MouseAdapter()
/*  53:    */         {
/*  54:    */           public void mouseClicked(MouseEvent e)
/*  55:    */           {
/*  56:105 */             if ((e.getModifiers() & 0x10) == 16)
/*  57:    */             {
/*  58:106 */               Color tmp = JColorChooser.showDialog(LegendPanel.this, "Select new Color", LegendPanel.LegendEntry.this.m_plotData.m_customColour);
/*  59:109 */               if (tmp != null)
/*  60:    */               {
/*  61:110 */                 LegendPanel.LegendEntry.this.m_plotData.m_customColour = tmp;
/*  62:111 */                 LegendPanel.LegendEntry.this.m_legendText.setForeground(tmp);
/*  63:113 */                 if (LegendPanel.this.m_Repainters.size() > 0) {
/*  64:114 */                   for (int i = 0; i < LegendPanel.this.m_Repainters.size(); i++) {
/*  65:115 */                     ((Component)LegendPanel.this.m_Repainters.get(i)).repaint();
/*  66:    */                   }
/*  67:    */                 }
/*  68:118 */                 LegendPanel.this.repaint();
/*  69:    */               }
/*  70:    */             }
/*  71:    */           }
/*  72:    */         });
/*  73:    */       }
/*  74:125 */       this.m_legendText = new JLabel(this.m_plotData.m_plotName);
/*  75:126 */       this.m_legendText.setToolTipText(this.m_plotData.getPlotNameHTML());
/*  76:127 */       if (this.m_plotData.m_useCustomColour) {
/*  77:128 */         this.m_legendText.setForeground(this.m_plotData.m_customColour);
/*  78:    */       }
/*  79:130 */       setLayout(new BorderLayout());
/*  80:131 */       add(this.m_legendText, "Center");
/*  81:    */       
/*  82:    */ 
/*  83:    */ 
/*  84:    */ 
/*  85:    */ 
/*  86:    */ 
/*  87:138 */       this.m_pointShape = new JPanel()
/*  88:    */       {
/*  89:    */         private static final long serialVersionUID = -7048435221580488238L;
/*  90:    */         
/*  91:    */         public void paintComponent(Graphics gx)
/*  92:    */         {
/*  93:143 */           super.paintComponent(gx);
/*  94:144 */           if (!LegendPanel.LegendEntry.this.m_plotData.m_useCustomColour) {
/*  95:145 */             gx.setColor(Color.black);
/*  96:    */           } else {
/*  97:147 */             gx.setColor(LegendPanel.LegendEntry.this.m_plotData.m_customColour);
/*  98:    */           }
/*  99:149 */           Plot2D.drawDataPoint(10.0D, 10.0D, 3, LegendPanel.LegendEntry.this.m_dataIndex, gx);
/* 100:    */         }
/* 101:152 */       };
/* 102:153 */       this.m_pointShape.setPreferredSize(new Dimension(20, 20));
/* 103:154 */       this.m_pointShape.setMinimumSize(new Dimension(20, 20));
/* 104:155 */       add(this.m_pointShape, "West");
/* 105:    */     }
/* 106:    */   }
/* 107:    */   
/* 108:    */   public LegendPanel()
/* 109:    */   {
/* 110:163 */     setBackground(Color.blue);
/* 111:164 */     setVerticalScrollBarPolicy(22);
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void setPlotList(ArrayList<PlotData2D> pl)
/* 115:    */   {
/* 116:173 */     this.m_plots = pl;
/* 117:174 */     updateLegends();
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void addRepaintNotify(Component c)
/* 121:    */   {
/* 122:184 */     this.m_Repainters.add(c);
/* 123:    */   }
/* 124:    */   
/* 125:    */   private void updateLegends()
/* 126:    */   {
/* 127:191 */     if (this.m_span == null) {
/* 128:192 */       this.m_span = new JPanel();
/* 129:    */     }
/* 130:195 */     JPanel padder = new JPanel();
/* 131:196 */     JPanel padd2 = new JPanel();
/* 132:    */     
/* 133:198 */     this.m_span.setPreferredSize(new Dimension(this.m_span.getPreferredSize().width, (this.m_plots.size() + 1) * 20));
/* 134:    */     
/* 135:200 */     this.m_span.setMaximumSize(new Dimension(this.m_span.getPreferredSize().width, (this.m_plots.size() + 1) * 20));
/* 136:    */     
/* 137:    */ 
/* 138:    */ 
/* 139:    */ 
/* 140:205 */     GridBagLayout gb = new GridBagLayout();
/* 141:206 */     GridBagLayout gb2 = new GridBagLayout();
/* 142:207 */     GridBagConstraints constraints = new GridBagConstraints();
/* 143:    */     
/* 144:209 */     this.m_span.removeAll();
/* 145:    */     
/* 146:211 */     padder.setLayout(gb);
/* 147:212 */     this.m_span.setLayout(gb2);
/* 148:213 */     constraints.anchor = 10;
/* 149:214 */     constraints.gridx = 0;
/* 150:215 */     constraints.gridy = 0;
/* 151:216 */     constraints.weightx = 5.0D;
/* 152:217 */     constraints.fill = 2;
/* 153:218 */     constraints.gridwidth = 1;
/* 154:219 */     constraints.gridheight = 1;
/* 155:220 */     constraints.insets = new Insets(0, 0, 0, 0);
/* 156:221 */     padder.add(this.m_span, constraints);
/* 157:    */     
/* 158:223 */     constraints.gridx = 0;
/* 159:224 */     constraints.gridy = 1;
/* 160:225 */     constraints.weightx = 5.0D;
/* 161:226 */     constraints.fill = 1;
/* 162:227 */     constraints.gridwidth = 1;
/* 163:228 */     constraints.gridheight = 1;
/* 164:229 */     constraints.weighty = 5.0D;
/* 165:230 */     constraints.insets = new Insets(0, 0, 0, 0);
/* 166:231 */     padder.add(padd2, constraints);
/* 167:    */     
/* 168:233 */     constraints.weighty = 0.0D;
/* 169:234 */     setViewportView(padder);
/* 170:    */     
/* 171:236 */     constraints.anchor = 10;
/* 172:237 */     constraints.gridx = 0;
/* 173:238 */     constraints.gridy = 0;
/* 174:239 */     constraints.weightx = 5.0D;
/* 175:240 */     constraints.fill = 2;
/* 176:241 */     constraints.gridwidth = 1;
/* 177:242 */     constraints.gridheight = 1;
/* 178:243 */     constraints.weighty = 5.0D;
/* 179:244 */     constraints.insets = new Insets(2, 4, 2, 4);
/* 180:247 */     for (int i = 0; i < this.m_plots.size(); i++)
/* 181:    */     {
/* 182:248 */       LegendEntry tmp = new LegendEntry((PlotData2D)this.m_plots.get(i), i);
/* 183:249 */       constraints.gridy = i;
/* 184:    */       
/* 185:    */ 
/* 186:    */ 
/* 187:    */ 
/* 188:254 */       this.m_span.add(tmp, constraints);
/* 189:    */     }
/* 190:    */   }
/* 191:    */   
/* 192:    */   public static void main(String[] args)
/* 193:    */   {
/* 194:    */     try
/* 195:    */     {
/* 196:265 */       if (args.length < 1)
/* 197:    */       {
/* 198:266 */         System.err.println("Usage : weka.gui.visualize.LegendPanel <dataset> [dataset2], [dataset3],...");
/* 199:    */         
/* 200:268 */         System.exit(1);
/* 201:    */       }
/* 202:271 */       JFrame jf = new JFrame("Weka Explorer: Legend");
/* 203:    */       
/* 204:273 */       jf.setSize(100, 100);
/* 205:274 */       jf.getContentPane().setLayout(new BorderLayout());
/* 206:275 */       LegendPanel p2 = new LegendPanel();
/* 207:276 */       jf.getContentPane().add(p2, "Center");
/* 208:277 */       jf.addWindowListener(new WindowAdapter()
/* 209:    */       {
/* 210:    */         public void windowClosing(WindowEvent e)
/* 211:    */         {
/* 212:280 */           this.val$jf.dispose();
/* 213:281 */           System.exit(0);
/* 214:    */         }
/* 215:284 */       });
/* 216:285 */       ArrayList<PlotData2D> plotList = new ArrayList();
/* 217:286 */       for (int j = 0; j < args.length; j++)
/* 218:    */       {
/* 219:287 */         System.err.println("Loading instances from " + args[j]);
/* 220:288 */         Reader r = new BufferedReader(new FileReader(args[j]));
/* 221:    */         
/* 222:290 */         Instances i = new Instances(r);
/* 223:291 */         PlotData2D tmp = new PlotData2D(i);
/* 224:292 */         if (j != 1)
/* 225:    */         {
/* 226:293 */           tmp.m_useCustomColour = true;
/* 227:294 */           tmp.m_customColour = Color.red;
/* 228:    */         }
/* 229:296 */         tmp.setPlotName(i.relationName());
/* 230:297 */         plotList.add(tmp);
/* 231:    */       }
/* 232:300 */       p2.setPlotList(plotList);
/* 233:301 */       jf.setVisible(true);
/* 234:    */     }
/* 235:    */     catch (Exception ex)
/* 236:    */     {
/* 237:303 */       System.err.println(ex.getMessage());
/* 238:304 */       ex.printStackTrace();
/* 239:    */     }
/* 240:    */   }
/* 241:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.LegendPanel
 * JD-Core Version:    0.7.0.1
 */