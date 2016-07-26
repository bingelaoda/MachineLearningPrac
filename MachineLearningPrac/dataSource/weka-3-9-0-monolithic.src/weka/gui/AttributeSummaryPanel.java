/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.GridBagConstraints;
/*   6:    */ import java.awt.GridBagLayout;
/*   7:    */ import java.awt.event.ActionEvent;
/*   8:    */ import java.awt.event.ActionListener;
/*   9:    */ import java.awt.event.WindowAdapter;
/*  10:    */ import java.awt.event.WindowEvent;
/*  11:    */ import java.io.BufferedReader;
/*  12:    */ import java.io.FileReader;
/*  13:    */ import java.io.PrintStream;
/*  14:    */ import java.io.Reader;
/*  15:    */ import javax.swing.BorderFactory;
/*  16:    */ import javax.swing.DefaultComboBoxModel;
/*  17:    */ import javax.swing.JComboBox;
/*  18:    */ import javax.swing.JFrame;
/*  19:    */ import javax.swing.JLabel;
/*  20:    */ import javax.swing.JPanel;
/*  21:    */ import javax.swing.JScrollPane;
/*  22:    */ import javax.swing.JTable;
/*  23:    */ import javax.swing.ListSelectionModel;
/*  24:    */ import javax.swing.SwingUtilities;
/*  25:    */ import javax.swing.table.DefaultTableCellRenderer;
/*  26:    */ import javax.swing.table.DefaultTableModel;
/*  27:    */ import javax.swing.table.TableColumn;
/*  28:    */ import javax.swing.table.TableColumnModel;
/*  29:    */ import weka.core.Attribute;
/*  30:    */ import weka.core.AttributeStats;
/*  31:    */ import weka.core.Instance;
/*  32:    */ import weka.core.Instances;
/*  33:    */ import weka.core.Utils;
/*  34:    */ import weka.experiment.Stats;
/*  35:    */ 
/*  36:    */ public class AttributeSummaryPanel
/*  37:    */   extends JPanel
/*  38:    */ {
/*  39:    */   static final long serialVersionUID = -5434987925737735880L;
/*  40:    */   protected static final String NO_SOURCE = "None";
/*  41: 62 */   protected JLabel m_AttributeNameLab = new JLabel("None");
/*  42: 65 */   protected JLabel m_AttributeTypeLab = new JLabel("None");
/*  43: 68 */   protected JLabel m_MissingLab = new JLabel("None");
/*  44: 71 */   protected JLabel m_UniqueLab = new JLabel("None");
/*  45: 74 */   protected JLabel m_DistinctLab = new JLabel("None");
/*  46: 77 */   protected JTable m_StatsTable = new JTable()
/*  47:    */   {
/*  48:    */     private static final long serialVersionUID = 7165142874670048578L;
/*  49:    */     
/*  50:    */     public boolean isCellEditable(int row, int column)
/*  51:    */     {
/*  52: 90 */       return false;
/*  53:    */     }
/*  54:    */   };
/*  55:    */   protected Instances m_Instances;
/*  56:    */   protected AttributeStats[] m_AttributeStats;
/*  57:101 */   protected boolean m_allEqualWeights = true;
/*  58:    */   
/*  59:    */   public AttributeSummaryPanel()
/*  60:    */   {
/*  61:108 */     JPanel simple = new JPanel();
/*  62:109 */     GridBagLayout gbL = new GridBagLayout();
/*  63:110 */     simple.setLayout(gbL);
/*  64:111 */     JLabel lab = new JLabel("Name:", 4);
/*  65:112 */     lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
/*  66:113 */     GridBagConstraints gbC = new GridBagConstraints();
/*  67:114 */     gbC.anchor = 13;
/*  68:115 */     gbC.fill = 2;
/*  69:116 */     gbC.gridy = 0;
/*  70:117 */     gbC.gridx = 0;
/*  71:118 */     gbL.setConstraints(lab, gbC);
/*  72:119 */     simple.add(lab);
/*  73:120 */     gbC = new GridBagConstraints();
/*  74:121 */     gbC.anchor = 17;
/*  75:122 */     gbC.fill = 2;
/*  76:123 */     gbC.gridy = 0;
/*  77:124 */     gbC.gridx = 1;
/*  78:125 */     gbC.weightx = 100.0D;
/*  79:126 */     gbC.gridwidth = 3;
/*  80:127 */     gbL.setConstraints(this.m_AttributeNameLab, gbC);
/*  81:128 */     simple.add(this.m_AttributeNameLab);
/*  82:129 */     this.m_AttributeNameLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
/*  83:    */     
/*  84:131 */     lab = new JLabel("Type:", 4);
/*  85:132 */     lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
/*  86:133 */     gbC = new GridBagConstraints();
/*  87:134 */     gbC.anchor = 13;
/*  88:135 */     gbC.fill = 2;
/*  89:136 */     gbC.gridy = 0;
/*  90:137 */     gbC.gridx = 4;
/*  91:138 */     gbL.setConstraints(lab, gbC);
/*  92:139 */     simple.add(lab);
/*  93:140 */     gbC = new GridBagConstraints();
/*  94:141 */     gbC.anchor = 17;
/*  95:142 */     gbC.fill = 2;
/*  96:143 */     gbC.gridy = 0;
/*  97:144 */     gbC.gridx = 5;
/*  98:145 */     gbC.weightx = 100.0D;
/*  99:146 */     gbL.setConstraints(this.m_AttributeTypeLab, gbC);
/* 100:147 */     simple.add(this.m_AttributeTypeLab);
/* 101:148 */     this.m_AttributeTypeLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
/* 102:    */     
/* 103:    */ 
/* 104:151 */     lab = new JLabel("Missing:", 4);
/* 105:152 */     lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
/* 106:153 */     gbC = new GridBagConstraints();
/* 107:154 */     gbC.anchor = 13;
/* 108:155 */     gbC.fill = 2;
/* 109:156 */     gbC.gridy = 1;
/* 110:157 */     gbC.gridx = 0;
/* 111:158 */     gbL.setConstraints(lab, gbC);
/* 112:159 */     simple.add(lab);
/* 113:160 */     gbC = new GridBagConstraints();
/* 114:161 */     gbC.anchor = 17;
/* 115:162 */     gbC.fill = 2;
/* 116:163 */     gbC.gridy = 1;
/* 117:164 */     gbC.gridx = 1;
/* 118:165 */     gbC.weightx = 100.0D;
/* 119:166 */     gbL.setConstraints(this.m_MissingLab, gbC);
/* 120:167 */     simple.add(this.m_MissingLab);
/* 121:168 */     this.m_MissingLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 10));
/* 122:    */     
/* 123:170 */     lab = new JLabel("Distinct:", 4);
/* 124:171 */     lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
/* 125:172 */     gbC = new GridBagConstraints();
/* 126:173 */     gbC.anchor = 13;
/* 127:174 */     gbC.fill = 2;
/* 128:175 */     gbC.gridy = 1;
/* 129:176 */     gbC.gridx = 2;
/* 130:177 */     gbL.setConstraints(lab, gbC);
/* 131:178 */     simple.add(lab);
/* 132:179 */     gbC = new GridBagConstraints();
/* 133:180 */     gbC.anchor = 17;
/* 134:181 */     gbC.fill = 2;
/* 135:182 */     gbC.gridy = 1;
/* 136:183 */     gbC.gridx = 3;
/* 137:184 */     gbC.weightx = 100.0D;
/* 138:185 */     gbL.setConstraints(this.m_DistinctLab, gbC);
/* 139:186 */     simple.add(this.m_DistinctLab);
/* 140:187 */     this.m_DistinctLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 10));
/* 141:    */     
/* 142:189 */     lab = new JLabel("Unique:", 4);
/* 143:190 */     lab.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
/* 144:191 */     gbC = new GridBagConstraints();
/* 145:192 */     gbC.anchor = 13;
/* 146:193 */     gbC.fill = 2;
/* 147:194 */     gbC.gridy = 1;
/* 148:195 */     gbC.gridx = 4;
/* 149:196 */     gbL.setConstraints(lab, gbC);
/* 150:197 */     simple.add(lab);
/* 151:198 */     gbC = new GridBagConstraints();
/* 152:199 */     gbC.anchor = 17;
/* 153:200 */     gbC.fill = 2;
/* 154:201 */     gbC.gridy = 1;
/* 155:202 */     gbC.gridx = 5;
/* 156:203 */     gbC.weightx = 100.0D;
/* 157:204 */     gbL.setConstraints(this.m_UniqueLab, gbC);
/* 158:205 */     simple.add(this.m_UniqueLab);
/* 159:206 */     this.m_UniqueLab.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 10));
/* 160:    */     
/* 161:208 */     setLayout(new BorderLayout());
/* 162:209 */     add(simple, "North");
/* 163:210 */     add(new JScrollPane(this.m_StatsTable), "Center");
/* 164:211 */     this.m_StatsTable.getSelectionModel().setSelectionMode(0);
/* 165:    */   }
/* 166:    */   
/* 167:    */   public void setInstances(Instances inst)
/* 168:    */   {
/* 169:222 */     this.m_Instances = inst;
/* 170:223 */     this.m_AttributeStats = new AttributeStats[inst.numAttributes()];
/* 171:224 */     this.m_AttributeNameLab.setText("None");
/* 172:225 */     this.m_AttributeTypeLab.setText("None");
/* 173:226 */     this.m_MissingLab.setText("None");
/* 174:227 */     this.m_UniqueLab.setText("None");
/* 175:228 */     this.m_DistinctLab.setText("None");
/* 176:229 */     this.m_StatsTable.setModel(new DefaultTableModel());
/* 177:    */     
/* 178:231 */     this.m_allEqualWeights = true;
/* 179:232 */     if (this.m_Instances.numInstances() == 0) {
/* 180:233 */       return;
/* 181:    */     }
/* 182:235 */     double w = this.m_Instances.instance(0).weight();
/* 183:236 */     for (int i = 1; i < this.m_Instances.numInstances(); i++) {
/* 184:237 */       if (this.m_Instances.instance(i).weight() != w)
/* 185:    */       {
/* 186:238 */         this.m_allEqualWeights = false;
/* 187:239 */         break;
/* 188:    */       }
/* 189:    */     }
/* 190:    */   }
/* 191:    */   
/* 192:    */   public void setAttribute(final int index)
/* 193:    */   {
/* 194:251 */     setHeader(index);
/* 195:252 */     if (this.m_AttributeStats[index] == null)
/* 196:    */     {
/* 197:253 */       Thread t = new Thread()
/* 198:    */       {
/* 199:    */         public void run()
/* 200:    */         {
/* 201:256 */           AttributeSummaryPanel.this.m_AttributeStats[index] = AttributeSummaryPanel.this.m_Instances.attributeStats(index);
/* 202:257 */           SwingUtilities.invokeLater(new Runnable()
/* 203:    */           {
/* 204:    */             public void run()
/* 205:    */             {
/* 206:260 */               AttributeSummaryPanel.this.setDerived(AttributeSummaryPanel.2.this.val$index);
/* 207:261 */               AttributeSummaryPanel.this.m_StatsTable.sizeColumnsToFit(-1);
/* 208:262 */               AttributeSummaryPanel.this.m_StatsTable.revalidate();
/* 209:263 */               AttributeSummaryPanel.this.m_StatsTable.repaint();
/* 210:    */             }
/* 211:    */           });
/* 212:    */         }
/* 213:267 */       };
/* 214:268 */       t.setPriority(1);
/* 215:269 */       t.start();
/* 216:    */     }
/* 217:    */     else
/* 218:    */     {
/* 219:271 */       setDerived(index);
/* 220:    */     }
/* 221:    */   }
/* 222:    */   
/* 223:    */   protected void setDerived(int index)
/* 224:    */   {
/* 225:283 */     AttributeStats as = this.m_AttributeStats[index];
/* 226:284 */     long percent = Math.round(100.0D * as.missingCount / as.totalCount);
/* 227:285 */     this.m_MissingLab.setText("" + as.missingCount + " (" + percent + "%)");
/* 228:286 */     percent = Math.round(100.0D * as.uniqueCount / as.totalCount);
/* 229:287 */     this.m_UniqueLab.setText("" + as.uniqueCount + " (" + percent + "%)");
/* 230:288 */     this.m_DistinctLab.setText("" + as.distinctCount);
/* 231:289 */     setTable(as, index);
/* 232:    */   }
/* 233:    */   
/* 234:    */   protected void setTable(AttributeStats as, int index)
/* 235:    */   {
/* 236:300 */     if (as.nominalCounts != null)
/* 237:    */     {
/* 238:301 */       Attribute att = this.m_Instances.attribute(index);
/* 239:302 */       Object[] colNames = { "No.", "Label", "Count", "Weight" };
/* 240:303 */       Object[][] data = new Object[as.nominalCounts.length][4];
/* 241:304 */       for (int i = 0; i < as.nominalCounts.length; i++)
/* 242:    */       {
/* 243:305 */         data[i][0] = new Integer(i + 1);
/* 244:306 */         data[i][1] = att.value(i);
/* 245:307 */         data[i][2] = new Integer(as.nominalCounts[i]);
/* 246:308 */         data[i][3] = new Double(Utils.doubleToString(as.nominalWeights[i], 3));
/* 247:    */       }
/* 248:310 */       this.m_StatsTable.setModel(new DefaultTableModel(data, colNames));
/* 249:311 */       this.m_StatsTable.getColumnModel().getColumn(0).setMaxWidth(60);
/* 250:312 */       DefaultTableCellRenderer tempR = new DefaultTableCellRenderer();
/* 251:313 */       tempR.setHorizontalAlignment(4);
/* 252:314 */       this.m_StatsTable.getColumnModel().getColumn(0).setCellRenderer(tempR);
/* 253:    */     }
/* 254:315 */     else if (as.numericStats != null)
/* 255:    */     {
/* 256:316 */       Object[] colNames = { "Statistic", "Value" };
/* 257:317 */       Object[][] data = new Object[4][2];
/* 258:318 */       data[0][0] = "Minimum";
/* 259:319 */       data[0][1] = Utils.doubleToString(as.numericStats.min, 3);
/* 260:320 */       data[1][0] = "Maximum";
/* 261:321 */       data[1][1] = Utils.doubleToString(as.numericStats.max, 3);
/* 262:322 */       data[2][0] = ("Mean" + (!this.m_allEqualWeights ? " (weighted)" : ""));
/* 263:323 */       data[2][1] = Utils.doubleToString(as.numericStats.mean, 3);
/* 264:324 */       data[3][0] = ("StdDev" + (!this.m_allEqualWeights ? " (weighted)" : ""));
/* 265:325 */       data[3][1] = Utils.doubleToString(as.numericStats.stdDev, 3);
/* 266:326 */       this.m_StatsTable.setModel(new DefaultTableModel(data, colNames));
/* 267:    */     }
/* 268:    */     else
/* 269:    */     {
/* 270:328 */       this.m_StatsTable.setModel(new DefaultTableModel());
/* 271:    */     }
/* 272:330 */     this.m_StatsTable.getColumnModel().setColumnMargin(4);
/* 273:    */   }
/* 274:    */   
/* 275:    */   protected void setHeader(int index)
/* 276:    */   {
/* 277:340 */     Attribute att = this.m_Instances.attribute(index);
/* 278:341 */     this.m_AttributeNameLab.setText(att.name());
/* 279:342 */     switch (att.type())
/* 280:    */     {
/* 281:    */     case 1: 
/* 282:344 */       this.m_AttributeTypeLab.setText("Nominal");
/* 283:345 */       break;
/* 284:    */     case 0: 
/* 285:347 */       this.m_AttributeTypeLab.setText("Numeric");
/* 286:348 */       break;
/* 287:    */     case 2: 
/* 288:350 */       this.m_AttributeTypeLab.setText("String");
/* 289:351 */       break;
/* 290:    */     case 3: 
/* 291:353 */       this.m_AttributeTypeLab.setText("Date");
/* 292:354 */       break;
/* 293:    */     case 4: 
/* 294:356 */       this.m_AttributeTypeLab.setText("Relational");
/* 295:357 */       break;
/* 296:    */     default: 
/* 297:359 */       this.m_AttributeTypeLab.setText("Unknown");
/* 298:    */     }
/* 299:362 */     this.m_MissingLab.setText("...");
/* 300:363 */     this.m_UniqueLab.setText("...");
/* 301:364 */     this.m_DistinctLab.setText("...");
/* 302:    */   }
/* 303:    */   
/* 304:    */   public static void main(String[] args)
/* 305:    */   {
/* 306:    */     try
/* 307:    */     {
/* 308:375 */       JFrame jf = new JFrame("Attribute Panel");
/* 309:376 */       jf.getContentPane().setLayout(new BorderLayout());
/* 310:377 */       AttributeSummaryPanel p = new AttributeSummaryPanel();
/* 311:378 */       p.setBorder(BorderFactory.createTitledBorder("Attribute"));
/* 312:379 */       jf.getContentPane().add(p, "Center");
/* 313:380 */       final JComboBox j = new JComboBox();
/* 314:381 */       j.setEnabled(false);
/* 315:382 */       j.addActionListener(new ActionListener()
/* 316:    */       {
/* 317:    */         public void actionPerformed(ActionEvent e)
/* 318:    */         {
/* 319:385 */           this.val$p.setAttribute(j.getSelectedIndex());
/* 320:    */         }
/* 321:387 */       });
/* 322:388 */       jf.getContentPane().add(j, "North");
/* 323:389 */       jf.addWindowListener(new WindowAdapter()
/* 324:    */       {
/* 325:    */         public void windowClosing(WindowEvent e)
/* 326:    */         {
/* 327:392 */           this.val$jf.dispose();
/* 328:393 */           System.exit(0);
/* 329:    */         }
/* 330:395 */       });
/* 331:396 */       jf.pack();
/* 332:397 */       jf.setVisible(true);
/* 333:398 */       if (args.length == 1)
/* 334:    */       {
/* 335:399 */         Reader r = new BufferedReader(new FileReader(args[0]));
/* 336:    */         
/* 337:401 */         Instances inst = new Instances(r);
/* 338:402 */         p.setInstances(inst);
/* 339:403 */         p.setAttribute(0);
/* 340:404 */         String[] names = new String[inst.numAttributes()];
/* 341:405 */         for (int i = 0; i < names.length; i++) {
/* 342:406 */           names[i] = inst.attribute(i).name();
/* 343:    */         }
/* 344:408 */         j.setModel(new DefaultComboBoxModel(names));
/* 345:409 */         j.setEnabled(true);
/* 346:    */       }
/* 347:    */     }
/* 348:    */     catch (Exception ex)
/* 349:    */     {
/* 350:412 */       ex.printStackTrace();
/* 351:413 */       System.err.println(ex.getMessage());
/* 352:    */     }
/* 353:    */   }
/* 354:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.AttributeSummaryPanel
 * JD-Core Version:    0.7.0.1
 */