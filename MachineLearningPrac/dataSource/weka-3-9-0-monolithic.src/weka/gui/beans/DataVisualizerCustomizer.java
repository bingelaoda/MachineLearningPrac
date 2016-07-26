/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Font;
/*   5:    */ import java.awt.GridLayout;
/*   6:    */ import java.awt.Window;
/*   7:    */ import java.awt.event.ActionEvent;
/*   8:    */ import java.awt.event.ActionListener;
/*   9:    */ import java.util.Set;
/*  10:    */ import java.util.Vector;
/*  11:    */ import javax.swing.BorderFactory;
/*  12:    */ import javax.swing.JButton;
/*  13:    */ import javax.swing.JComboBox;
/*  14:    */ import javax.swing.JLabel;
/*  15:    */ import javax.swing.JPanel;
/*  16:    */ import javax.swing.JTextArea;
/*  17:    */ import weka.core.Environment;
/*  18:    */ import weka.core.EnvironmentHandler;
/*  19:    */ import weka.core.PluginManager;
/*  20:    */ 
/*  21:    */ public class DataVisualizerCustomizer
/*  22:    */   extends JPanel
/*  23:    */   implements BeanCustomizer, EnvironmentHandler, CustomizerClosingListener, CustomizerCloseRequester
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = 27802741348090392L;
/*  26:    */   private DataVisualizer m_dataVis;
/*  27: 63 */   private Environment m_env = Environment.getSystemWide();
/*  28:    */   private BeanCustomizer.ModifyListener m_modifyListener;
/*  29:    */   private Window m_parent;
/*  30:    */   private String m_rendererNameBack;
/*  31:    */   private String m_xAxisBack;
/*  32:    */   private String m_yAxisBack;
/*  33:    */   private String m_widthBack;
/*  34:    */   private String m_heightBack;
/*  35:    */   private String m_optsBack;
/*  36:    */   private JComboBox m_rendererCombo;
/*  37:    */   private EnvironmentField m_xAxis;
/*  38:    */   private EnvironmentField m_yAxis;
/*  39:    */   private EnvironmentField m_width;
/*  40:    */   private EnvironmentField m_height;
/*  41:    */   private EnvironmentField m_opts;
/*  42:    */   
/*  43:    */   public DataVisualizerCustomizer()
/*  44:    */   {
/*  45: 88 */     setLayout(new BorderLayout());
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setObject(Object object)
/*  49:    */   {
/*  50: 98 */     this.m_dataVis = ((DataVisualizer)object);
/*  51: 99 */     this.m_rendererNameBack = this.m_dataVis.getOffscreenRendererName();
/*  52:100 */     this.m_xAxisBack = this.m_dataVis.getOffscreenXAxis();
/*  53:101 */     this.m_yAxisBack = this.m_dataVis.getOffscreenYAxis();
/*  54:102 */     this.m_widthBack = this.m_dataVis.getOffscreenWidth();
/*  55:103 */     this.m_heightBack = this.m_dataVis.getOffscreenHeight();
/*  56:104 */     this.m_optsBack = this.m_dataVis.getOffscreenAdditionalOpts();
/*  57:    */     
/*  58:106 */     setup();
/*  59:    */   }
/*  60:    */   
/*  61:    */   private void setup()
/*  62:    */   {
/*  63:110 */     JPanel holder = new JPanel();
/*  64:111 */     holder.setLayout(new GridLayout(6, 2));
/*  65:    */     
/*  66:113 */     Vector<String> comboItems = new Vector();
/*  67:114 */     comboItems.add("Weka Chart Renderer");
/*  68:115 */     Set<String> pluginRenderers = PluginManager.getPluginNamesOfType("weka.gui.beans.OffscreenChartRenderer");
/*  69:117 */     if (pluginRenderers != null) {
/*  70:118 */       for (String plugin : pluginRenderers) {
/*  71:119 */         comboItems.add(plugin);
/*  72:    */       }
/*  73:    */     }
/*  74:123 */     JLabel rendererLab = new JLabel("Renderer", 4);
/*  75:124 */     holder.add(rendererLab);
/*  76:125 */     this.m_rendererCombo = new JComboBox(comboItems);
/*  77:126 */     holder.add(this.m_rendererCombo);
/*  78:    */     
/*  79:128 */     JLabel xLab = new JLabel("X-axis attribute", 4);
/*  80:129 */     xLab.setToolTipText("Attribute name or /first or /last or /<index>");
/*  81:130 */     this.m_xAxis = new EnvironmentField(this.m_env);
/*  82:131 */     this.m_xAxis.setText(this.m_xAxisBack);
/*  83:    */     
/*  84:133 */     JLabel yLab = new JLabel("Y-axis attribute", 4);
/*  85:134 */     yLab.setToolTipText("Attribute name or /first or /last or /<index>");
/*  86:135 */     this.m_yAxis = new EnvironmentField(this.m_env);
/*  87:136 */     this.m_yAxis.setText(this.m_yAxisBack);
/*  88:    */     
/*  89:138 */     JLabel widthLab = new JLabel("Chart width (pixels)", 4);
/*  90:139 */     this.m_width = new EnvironmentField(this.m_env);
/*  91:140 */     this.m_width.setText(this.m_widthBack);
/*  92:    */     
/*  93:142 */     JLabel heightLab = new JLabel("Chart height (pixels)", 4);
/*  94:143 */     this.m_height = new EnvironmentField(this.m_env);
/*  95:144 */     this.m_height.setText(this.m_heightBack);
/*  96:    */     
/*  97:146 */     final JLabel optsLab = new JLabel("Renderer options", 4);
/*  98:147 */     this.m_opts = new EnvironmentField(this.m_env);
/*  99:148 */     this.m_opts.setText(this.m_optsBack);
/* 100:149 */     holder.add(xLab);holder.add(this.m_xAxis);
/* 101:150 */     holder.add(yLab);holder.add(this.m_yAxis);
/* 102:151 */     holder.add(widthLab);holder.add(this.m_width);
/* 103:152 */     holder.add(heightLab);holder.add(this.m_height);
/* 104:153 */     holder.add(optsLab);holder.add(this.m_opts);
/* 105:    */     
/* 106:155 */     add(holder, "Center");
/* 107:    */     
/* 108:157 */     String globalInfo = this.m_dataVis.globalInfo();
/* 109:158 */     globalInfo = globalInfo + " This dialog allows you to configure offscreen rendering options. Offscreen images are passed via 'image' connections.";
/* 110:    */     
/* 111:    */ 
/* 112:    */ 
/* 113:162 */     JTextArea jt = new JTextArea();
/* 114:163 */     jt.setColumns(30);
/* 115:164 */     jt.setFont(new Font("SansSerif", 0, 12));
/* 116:165 */     jt.setEditable(false);
/* 117:166 */     jt.setLineWrap(true);
/* 118:167 */     jt.setWrapStyleWord(true);
/* 119:168 */     jt.setText(globalInfo);
/* 120:169 */     jt.setBackground(getBackground());
/* 121:170 */     JPanel jp = new JPanel();
/* 122:171 */     jp.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("About"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
/* 123:    */     
/* 124:    */ 
/* 125:    */ 
/* 126:175 */     jp.setLayout(new BorderLayout());
/* 127:176 */     jp.add(jt, "Center");
/* 128:177 */     add(jp, "North");
/* 129:    */     
/* 130:179 */     addButtons();
/* 131:    */     
/* 132:181 */     this.m_rendererCombo.addActionListener(new ActionListener()
/* 133:    */     {
/* 134:    */       public void actionPerformed(ActionEvent e)
/* 135:    */       {
/* 136:183 */         DataVisualizerCustomizer.this.setupRendererOptsTipText(optsLab);
/* 137:    */       }
/* 138:185 */     });
/* 139:186 */     this.m_rendererCombo.setSelectedItem(this.m_rendererNameBack);
/* 140:    */     
/* 141:188 */     setupRendererOptsTipText(optsLab);
/* 142:    */   }
/* 143:    */   
/* 144:    */   private void setupRendererOptsTipText(JLabel optsLab)
/* 145:    */   {
/* 146:192 */     String renderer = this.m_rendererCombo.getSelectedItem().toString();
/* 147:193 */     if (renderer.equalsIgnoreCase("weka chart renderer"))
/* 148:    */     {
/* 149:195 */       WekaOffscreenChartRenderer rcr = new WekaOffscreenChartRenderer();
/* 150:196 */       String tipText = rcr.optionsTipTextHTML();
/* 151:197 */       tipText = tipText.replace("<html>", "<html>Comma separated list of options:<br>");
/* 152:198 */       optsLab.setToolTipText(tipText);
/* 153:    */     }
/* 154:    */     else
/* 155:    */     {
/* 156:    */       try
/* 157:    */       {
/* 158:201 */         Object rendererO = PluginManager.getPluginInstance("weka.gui.beans.OffscreenChartRenderer", renderer);
/* 159:204 */         if (rendererO != null)
/* 160:    */         {
/* 161:205 */           String tipText = ((OffscreenChartRenderer)rendererO).optionsTipTextHTML();
/* 162:206 */           if ((tipText != null) && (tipText.length() > 0)) {
/* 163:207 */             optsLab.setToolTipText(tipText);
/* 164:    */           }
/* 165:    */         }
/* 166:    */       }
/* 167:    */       catch (Exception ex) {}
/* 168:    */     }
/* 169:    */   }
/* 170:    */   
/* 171:    */   private void addButtons()
/* 172:    */   {
/* 173:216 */     JButton okBut = new JButton("OK");
/* 174:217 */     JButton cancelBut = new JButton("Cancel");
/* 175:    */     
/* 176:219 */     JPanel butHolder = new JPanel();
/* 177:220 */     butHolder.setLayout(new GridLayout(1, 2));
/* 178:221 */     butHolder.add(okBut);butHolder.add(cancelBut);
/* 179:222 */     add(butHolder, "South");
/* 180:    */     
/* 181:224 */     okBut.addActionListener(new ActionListener()
/* 182:    */     {
/* 183:    */       public void actionPerformed(ActionEvent e)
/* 184:    */       {
/* 185:226 */         DataVisualizerCustomizer.this.m_dataVis.setOffscreenXAxis(DataVisualizerCustomizer.this.m_xAxis.getText());
/* 186:227 */         DataVisualizerCustomizer.this.m_dataVis.setOffscreenYAxis(DataVisualizerCustomizer.this.m_yAxis.getText());
/* 187:228 */         DataVisualizerCustomizer.this.m_dataVis.setOffscreenWidth(DataVisualizerCustomizer.this.m_width.getText());
/* 188:229 */         DataVisualizerCustomizer.this.m_dataVis.setOffscreenHeight(DataVisualizerCustomizer.this.m_height.getText());
/* 189:230 */         DataVisualizerCustomizer.this.m_dataVis.setOffscreenAdditionalOpts(DataVisualizerCustomizer.this.m_opts.getText());
/* 190:231 */         DataVisualizerCustomizer.this.m_dataVis.setOffscreenRendererName(DataVisualizerCustomizer.this.m_rendererCombo.getSelectedItem().toString());
/* 191:234 */         if (DataVisualizerCustomizer.this.m_modifyListener != null) {
/* 192:235 */           DataVisualizerCustomizer.this.m_modifyListener.setModifiedStatus(DataVisualizerCustomizer.this, true);
/* 193:    */         }
/* 194:238 */         if (DataVisualizerCustomizer.this.m_parent != null) {
/* 195:239 */           DataVisualizerCustomizer.this.m_parent.dispose();
/* 196:    */         }
/* 197:    */       }
/* 198:243 */     });
/* 199:244 */     cancelBut.addActionListener(new ActionListener()
/* 200:    */     {
/* 201:    */       public void actionPerformed(ActionEvent e)
/* 202:    */       {
/* 203:247 */         DataVisualizerCustomizer.this.customizerClosing();
/* 204:248 */         if (DataVisualizerCustomizer.this.m_parent != null) {
/* 205:249 */           DataVisualizerCustomizer.this.m_parent.dispose();
/* 206:    */         }
/* 207:    */       }
/* 208:    */     });
/* 209:    */   }
/* 210:    */   
/* 211:    */   public void setParentWindow(Window parent)
/* 212:    */   {
/* 213:261 */     this.m_parent = parent;
/* 214:    */   }
/* 215:    */   
/* 216:    */   public void customizerClosing()
/* 217:    */   {
/* 218:270 */     this.m_dataVis.setOffscreenXAxis(this.m_xAxisBack);
/* 219:271 */     this.m_dataVis.setOffscreenYAxis(this.m_yAxisBack);
/* 220:272 */     this.m_dataVis.setOffscreenWidth(this.m_widthBack);
/* 221:273 */     this.m_dataVis.setOffscreenHeight(this.m_heightBack);
/* 222:274 */     this.m_dataVis.setOffscreenAdditionalOpts(this.m_optsBack);
/* 223:275 */     this.m_dataVis.setOffscreenRendererName(this.m_rendererNameBack);
/* 224:    */   }
/* 225:    */   
/* 226:    */   public void setEnvironment(Environment env)
/* 227:    */   {
/* 228:284 */     this.m_env = env;
/* 229:    */   }
/* 230:    */   
/* 231:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/* 232:    */   {
/* 233:294 */     this.m_modifyListener = l;
/* 234:    */   }
/* 235:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.DataVisualizerCustomizer
 * JD-Core Version:    0.7.0.1
 */