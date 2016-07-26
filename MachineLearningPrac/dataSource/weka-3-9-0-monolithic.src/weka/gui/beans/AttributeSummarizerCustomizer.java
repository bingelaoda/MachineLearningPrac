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
/*  21:    */ public class AttributeSummarizerCustomizer
/*  22:    */   extends JPanel
/*  23:    */   implements BeanCustomizer, EnvironmentHandler, CustomizerClosingListener, CustomizerCloseRequester
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = -6973666187676272788L;
/*  26:    */   private DataVisualizer m_dataVis;
/*  27: 64 */   private Environment m_env = Environment.getSystemWide();
/*  28:    */   private BeanCustomizer.ModifyListener m_modifyListener;
/*  29:    */   private Window m_parent;
/*  30:    */   private String m_rendererNameBack;
/*  31:    */   private String m_xAxisBack;
/*  32:    */   private String m_widthBack;
/*  33:    */   private String m_heightBack;
/*  34:    */   private String m_optsBack;
/*  35:    */   private JComboBox m_rendererCombo;
/*  36:    */   private EnvironmentField m_xAxis;
/*  37:    */   private EnvironmentField m_width;
/*  38:    */   private EnvironmentField m_height;
/*  39:    */   private EnvironmentField m_opts;
/*  40:    */   
/*  41:    */   public AttributeSummarizerCustomizer()
/*  42:    */   {
/*  43: 87 */     setLayout(new BorderLayout());
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setObject(Object object)
/*  47:    */   {
/*  48: 97 */     this.m_dataVis = ((DataVisualizer)object);
/*  49: 98 */     this.m_rendererNameBack = this.m_dataVis.getOffscreenRendererName();
/*  50: 99 */     this.m_xAxisBack = this.m_dataVis.getOffscreenXAxis();
/*  51:100 */     this.m_widthBack = this.m_dataVis.getOffscreenWidth();
/*  52:101 */     this.m_heightBack = this.m_dataVis.getOffscreenHeight();
/*  53:102 */     this.m_optsBack = this.m_dataVis.getOffscreenAdditionalOpts();
/*  54:    */     
/*  55:104 */     setup();
/*  56:    */   }
/*  57:    */   
/*  58:    */   private void setup()
/*  59:    */   {
/*  60:108 */     JPanel holder = new JPanel();
/*  61:109 */     holder.setLayout(new GridLayout(5, 2));
/*  62:    */     
/*  63:111 */     Vector<String> comboItems = new Vector();
/*  64:112 */     comboItems.add("Weka Chart Renderer");
/*  65:113 */     Set<String> pluginRenderers = PluginManager.getPluginNamesOfType("weka.gui.beans.OffscreenChartRenderer");
/*  66:115 */     if (pluginRenderers != null) {
/*  67:116 */       for (String plugin : pluginRenderers) {
/*  68:117 */         comboItems.add(plugin);
/*  69:    */       }
/*  70:    */     }
/*  71:121 */     JLabel rendererLab = new JLabel("Renderer", 4);
/*  72:122 */     holder.add(rendererLab);
/*  73:123 */     this.m_rendererCombo = new JComboBox(comboItems);
/*  74:124 */     holder.add(this.m_rendererCombo);
/*  75:    */     
/*  76:126 */     JLabel xLab = new JLabel("Attribute to chart", 4);
/*  77:127 */     xLab.setToolTipText("Attribute name or /first or /last or /<index>");
/*  78:128 */     this.m_xAxis = new EnvironmentField(this.m_env);
/*  79:129 */     this.m_xAxis.setText(this.m_xAxisBack);
/*  80:    */     
/*  81:131 */     JLabel widthLab = new JLabel("Chart width (pixels)", 4);
/*  82:132 */     this.m_width = new EnvironmentField(this.m_env);
/*  83:133 */     this.m_width.setText(this.m_widthBack);
/*  84:    */     
/*  85:135 */     JLabel heightLab = new JLabel("Chart height (pixels)", 4);
/*  86:136 */     this.m_height = new EnvironmentField(this.m_env);
/*  87:137 */     this.m_height.setText(this.m_heightBack);
/*  88:    */     
/*  89:139 */     final JLabel optsLab = new JLabel("Renderer options", 4);
/*  90:140 */     this.m_opts = new EnvironmentField(this.m_env);
/*  91:141 */     this.m_opts.setText(this.m_optsBack);
/*  92:142 */     holder.add(xLab);holder.add(this.m_xAxis);
/*  93:143 */     holder.add(widthLab);holder.add(this.m_width);
/*  94:144 */     holder.add(heightLab);holder.add(this.m_height);
/*  95:145 */     holder.add(optsLab);holder.add(this.m_opts);
/*  96:    */     
/*  97:147 */     add(holder, "Center");
/*  98:    */     
/*  99:149 */     String globalInfo = this.m_dataVis.globalInfo();
/* 100:150 */     globalInfo = globalInfo + " This dialog allows you to configure offscreen rendering options. Offscreen images are passed via 'image' connections.";
/* 101:    */     
/* 102:    */ 
/* 103:    */ 
/* 104:154 */     JTextArea jt = new JTextArea();
/* 105:155 */     jt.setColumns(30);
/* 106:156 */     jt.setFont(new Font("SansSerif", 0, 12));
/* 107:157 */     jt.setEditable(false);
/* 108:158 */     jt.setLineWrap(true);
/* 109:159 */     jt.setWrapStyleWord(true);
/* 110:160 */     jt.setText(globalInfo);
/* 111:161 */     jt.setBackground(getBackground());
/* 112:162 */     JPanel jp = new JPanel();
/* 113:163 */     jp.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("About"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
/* 114:    */     
/* 115:    */ 
/* 116:    */ 
/* 117:167 */     jp.setLayout(new BorderLayout());
/* 118:168 */     jp.add(jt, "Center");
/* 119:169 */     add(jp, "North");
/* 120:    */     
/* 121:171 */     addButtons();
/* 122:    */     
/* 123:173 */     this.m_rendererCombo.addActionListener(new ActionListener()
/* 124:    */     {
/* 125:    */       public void actionPerformed(ActionEvent e)
/* 126:    */       {
/* 127:175 */         AttributeSummarizerCustomizer.this.setupRendererOptsTipText(optsLab);
/* 128:    */       }
/* 129:177 */     });
/* 130:178 */     this.m_rendererCombo.setSelectedItem(this.m_rendererNameBack);
/* 131:    */     
/* 132:180 */     setupRendererOptsTipText(optsLab);
/* 133:    */   }
/* 134:    */   
/* 135:    */   private void setupRendererOptsTipText(JLabel optsLab)
/* 136:    */   {
/* 137:184 */     String renderer = this.m_rendererCombo.getSelectedItem().toString();
/* 138:185 */     if (renderer.equalsIgnoreCase("weka chart renderer"))
/* 139:    */     {
/* 140:187 */       WekaOffscreenChartRenderer rcr = new WekaOffscreenChartRenderer();
/* 141:188 */       String tipText = rcr.optionsTipTextHTML();
/* 142:189 */       tipText = tipText.replace("<html>", "<html>Comma separated list of options:<br>");
/* 143:190 */       optsLab.setToolTipText(tipText);
/* 144:    */     }
/* 145:    */     else
/* 146:    */     {
/* 147:    */       try
/* 148:    */       {
/* 149:193 */         Object rendererO = PluginManager.getPluginInstance("weka.gui.beans.OffscreenChartRenderer", renderer);
/* 150:196 */         if (rendererO != null)
/* 151:    */         {
/* 152:197 */           String tipText = ((OffscreenChartRenderer)rendererO).optionsTipTextHTML();
/* 153:198 */           if ((tipText != null) && (tipText.length() > 0)) {
/* 154:199 */             optsLab.setToolTipText(tipText);
/* 155:    */           }
/* 156:    */         }
/* 157:    */       }
/* 158:    */       catch (Exception ex) {}
/* 159:    */     }
/* 160:    */   }
/* 161:    */   
/* 162:    */   private void addButtons()
/* 163:    */   {
/* 164:209 */     JButton okBut = new JButton("OK");
/* 165:210 */     JButton cancelBut = new JButton("Cancel");
/* 166:    */     
/* 167:212 */     JPanel butHolder = new JPanel();
/* 168:213 */     butHolder.setLayout(new GridLayout(1, 2));
/* 169:214 */     butHolder.add(okBut);butHolder.add(cancelBut);
/* 170:215 */     add(butHolder, "South");
/* 171:    */     
/* 172:217 */     okBut.addActionListener(new ActionListener()
/* 173:    */     {
/* 174:    */       public void actionPerformed(ActionEvent e)
/* 175:    */       {
/* 176:219 */         AttributeSummarizerCustomizer.this.m_dataVis.setOffscreenXAxis(AttributeSummarizerCustomizer.this.m_xAxis.getText());
/* 177:220 */         AttributeSummarizerCustomizer.this.m_dataVis.setOffscreenWidth(AttributeSummarizerCustomizer.this.m_width.getText());
/* 178:221 */         AttributeSummarizerCustomizer.this.m_dataVis.setOffscreenHeight(AttributeSummarizerCustomizer.this.m_height.getText());
/* 179:222 */         AttributeSummarizerCustomizer.this.m_dataVis.setOffscreenAdditionalOpts(AttributeSummarizerCustomizer.this.m_opts.getText());
/* 180:223 */         AttributeSummarizerCustomizer.this.m_dataVis.setOffscreenRendererName(AttributeSummarizerCustomizer.this.m_rendererCombo.getSelectedItem().toString());
/* 181:226 */         if (AttributeSummarizerCustomizer.this.m_modifyListener != null) {
/* 182:227 */           AttributeSummarizerCustomizer.this.m_modifyListener.setModifiedStatus(AttributeSummarizerCustomizer.this, true);
/* 183:    */         }
/* 184:230 */         if (AttributeSummarizerCustomizer.this.m_parent != null) {
/* 185:231 */           AttributeSummarizerCustomizer.this.m_parent.dispose();
/* 186:    */         }
/* 187:    */       }
/* 188:235 */     });
/* 189:236 */     cancelBut.addActionListener(new ActionListener()
/* 190:    */     {
/* 191:    */       public void actionPerformed(ActionEvent e)
/* 192:    */       {
/* 193:239 */         AttributeSummarizerCustomizer.this.customizerClosing();
/* 194:240 */         if (AttributeSummarizerCustomizer.this.m_parent != null) {
/* 195:241 */           AttributeSummarizerCustomizer.this.m_parent.dispose();
/* 196:    */         }
/* 197:    */       }
/* 198:    */     });
/* 199:    */   }
/* 200:    */   
/* 201:    */   public void setParentWindow(Window parent)
/* 202:    */   {
/* 203:253 */     this.m_parent = parent;
/* 204:    */   }
/* 205:    */   
/* 206:    */   public void customizerClosing()
/* 207:    */   {
/* 208:262 */     this.m_dataVis.setOffscreenXAxis(this.m_xAxisBack);
/* 209:263 */     this.m_dataVis.setOffscreenWidth(this.m_widthBack);
/* 210:264 */     this.m_dataVis.setOffscreenHeight(this.m_heightBack);
/* 211:265 */     this.m_dataVis.setOffscreenAdditionalOpts(this.m_optsBack);
/* 212:266 */     this.m_dataVis.setOffscreenRendererName(this.m_rendererNameBack);
/* 213:    */   }
/* 214:    */   
/* 215:    */   public void setEnvironment(Environment env)
/* 216:    */   {
/* 217:275 */     this.m_env = env;
/* 218:    */   }
/* 219:    */   
/* 220:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/* 221:    */   {
/* 222:285 */     this.m_modifyListener = l;
/* 223:    */   }
/* 224:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.AttributeSummarizerCustomizer
 * JD-Core Version:    0.7.0.1
 */