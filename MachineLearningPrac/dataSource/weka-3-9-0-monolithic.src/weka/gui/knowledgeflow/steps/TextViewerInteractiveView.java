/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Font;
/*   5:    */ import java.awt.event.ActionEvent;
/*   6:    */ import java.awt.event.ActionListener;
/*   7:    */ import java.awt.event.MouseAdapter;
/*   8:    */ import java.awt.event.MouseEvent;
/*   9:    */ import java.util.Map;
/*  10:    */ import java.util.Map.Entry;
/*  11:    */ import javax.swing.BorderFactory;
/*  12:    */ import javax.swing.JButton;
/*  13:    */ import javax.swing.JList;
/*  14:    */ import javax.swing.JMenuItem;
/*  15:    */ import javax.swing.JPanel;
/*  16:    */ import javax.swing.JPopupMenu;
/*  17:    */ import javax.swing.JScrollPane;
/*  18:    */ import javax.swing.JSplitPane;
/*  19:    */ import javax.swing.JTextArea;
/*  20:    */ import weka.core.Defaults;
/*  21:    */ import weka.core.Environment;
/*  22:    */ import weka.core.Settings;
/*  23:    */ import weka.core.Settings.SettingKey;
/*  24:    */ import weka.gui.ResultHistoryPanel;
/*  25:    */ import weka.gui.SaveBuffer;
/*  26:    */ import weka.gui.knowledgeflow.BaseInteractiveViewer;
/*  27:    */ import weka.knowledgeflow.steps.TextViewer;
/*  28:    */ import weka.knowledgeflow.steps.TextViewer.TextNotificationListener;
/*  29:    */ 
/*  30:    */ public class TextViewerInteractiveView
/*  31:    */   extends BaseInteractiveViewer
/*  32:    */   implements TextViewer.TextNotificationListener
/*  33:    */ {
/*  34:    */   private static final long serialVersionUID = -3164518320257969282L;
/*  35:    */   protected JButton m_clearButton;
/*  36:    */   protected ResultHistoryPanel m_history;
/*  37:    */   protected JTextArea m_outText;
/*  38:    */   protected JScrollPane m_textScroller;
/*  39:    */   
/*  40:    */   public TextViewerInteractiveView()
/*  41:    */   {
/*  42: 53 */     this.m_clearButton = new JButton("Clear results");
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void init()
/*  46:    */   {
/*  47: 69 */     addButton(this.m_clearButton);
/*  48: 70 */     this.m_outText = new JTextArea(20, 80);
/*  49: 71 */     this.m_outText.setEditable(false);
/*  50: 72 */     this.m_outText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  51:    */     
/*  52: 74 */     this.m_history = new ResultHistoryPanel(this.m_outText);
/*  53: 75 */     this.m_history.setBorder(BorderFactory.createTitledBorder("Result list"));
/*  54: 76 */     this.m_history.setHandleRightClicks(false);
/*  55: 77 */     this.m_history.getList().addMouseListener(new MouseAdapter()
/*  56:    */     {
/*  57:    */       public void mouseClicked(MouseEvent e)
/*  58:    */       {
/*  59: 80 */         if (((e.getModifiers() & 0x10) != 16) || (e.isAltDown()))
/*  60:    */         {
/*  61: 82 */           int index = TextViewerInteractiveView.this.m_history.getList().locationToIndex(e.getPoint());
/*  62: 83 */           if (index != -1)
/*  63:    */           {
/*  64: 84 */             String name = TextViewerInteractiveView.this.m_history.getNameAtIndex(index);
/*  65: 85 */             TextViewerInteractiveView.this.visualize(name, e.getX(), e.getY());
/*  66:    */           }
/*  67:    */           else
/*  68:    */           {
/*  69: 87 */             TextViewerInteractiveView.this.visualize(null, e.getX(), e.getY());
/*  70:    */           }
/*  71:    */         }
/*  72:    */       }
/*  73: 92 */     });
/*  74: 93 */     this.m_textScroller = new JScrollPane(this.m_outText);
/*  75: 94 */     this.m_textScroller.setBorder(BorderFactory.createTitledBorder("Text"));
/*  76: 95 */     JSplitPane p2 = new JSplitPane(1, this.m_history, this.m_textScroller);
/*  77:    */     
/*  78: 97 */     add(p2, "Center");
/*  79:    */     
/*  80:    */ 
/*  81:100 */     Map<String, String> runResults = ((TextViewer)getStep()).getResults();
/*  82:101 */     if (runResults.size() > 0)
/*  83:    */     {
/*  84:102 */       boolean first = true;
/*  85:103 */       String firstKey = "";
/*  86:104 */       for (Map.Entry<String, String> e : runResults.entrySet())
/*  87:    */       {
/*  88:105 */         if (first)
/*  89:    */         {
/*  90:106 */           firstKey = (String)e.getKey();
/*  91:107 */           first = false;
/*  92:    */         }
/*  93:109 */         this.m_history.addResult((String)e.getKey(), new StringBuffer().append((String)e.getValue()));
/*  94:    */       }
/*  95:112 */       this.m_history.setSingle(firstKey);
/*  96:    */     }
/*  97:115 */     this.m_clearButton.addActionListener(new ActionListener()
/*  98:    */     {
/*  99:    */       public void actionPerformed(ActionEvent e)
/* 100:    */       {
/* 101:118 */         TextViewerInteractiveView.this.m_history.clearResults();
/* 102:119 */         ((TextViewer)TextViewerInteractiveView.this.getStep()).getResults().clear();
/* 103:120 */         TextViewerInteractiveView.this.m_outText.setText("");
/* 104:    */       }
/* 105:123 */     });
/* 106:124 */     applySettings(getSettings());
/* 107:125 */     ((TextViewer)getStep()).setTextNotificationListener(this);
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void closePressed()
/* 111:    */   {
/* 112:133 */     ((TextViewer)getStep()).removeTextNotificationListener(this);
/* 113:    */   }
/* 114:    */   
/* 115:    */   public void applySettings(Settings settings)
/* 116:    */   {
/* 117:144 */     this.m_outText.setFont((Font)settings.getSetting("weka.gui.knowledgeflow.steps.textviewer", TextViewerInteractiveViewDefaults.OUTPUT_FONT_KEY, TextViewerInteractiveViewDefaults.OUTPUT_FONT, Environment.getSystemWide()));
/* 118:    */     
/* 119:    */ 
/* 120:    */ 
/* 121:148 */     this.m_history.setFont((Font)settings.getSetting("weka.gui.knowledgeflow.steps.textviewer", TextViewerInteractiveViewDefaults.OUTPUT_FONT_KEY, TextViewerInteractiveViewDefaults.OUTPUT_FONT, Environment.getSystemWide()));
/* 122:    */     
/* 123:    */ 
/* 124:    */ 
/* 125:152 */     this.m_outText.setForeground((Color)settings.getSetting("weka.gui.knowledgeflow.steps.textviewer", TextViewerInteractiveViewDefaults.OUTPUT_TEXT_COLOR_KEY, TextViewerInteractiveViewDefaults.OUTPUT_TEXT_COLOR, Environment.getSystemWide()));
/* 126:    */     
/* 127:    */ 
/* 128:    */ 
/* 129:    */ 
/* 130:157 */     this.m_outText.setBackground((Color)settings.getSetting("weka.gui.knowledgeflow.steps.textviewer", TextViewerInteractiveViewDefaults.OUTPUT_BACKGROUND_COLOR_KEY, TextViewerInteractiveViewDefaults.OUTPUT_BACKGROUND_COLOR, Environment.getSystemWide()));
/* 131:    */     
/* 132:    */ 
/* 133:    */ 
/* 134:    */ 
/* 135:162 */     this.m_textScroller.setBackground((Color)settings.getSetting("weka.gui.knowledgeflow.steps.textviewer", TextViewerInteractiveViewDefaults.OUTPUT_BACKGROUND_COLOR_KEY, TextViewerInteractiveViewDefaults.OUTPUT_BACKGROUND_COLOR, Environment.getSystemWide()));
/* 136:    */     
/* 137:    */ 
/* 138:    */ 
/* 139:    */ 
/* 140:167 */     this.m_outText.setRows(((Integer)settings.getSetting("weka.gui.knowledgeflow.steps.textviewer", TextViewerInteractiveViewDefaults.NUM_ROWS_KEY, Integer.valueOf(20), Environment.getSystemWide())).intValue());
/* 141:    */     
/* 142:    */ 
/* 143:170 */     this.m_outText.setColumns(((Integer)settings.getSetting("weka.gui.knowledgeflow.steps.textviewer", TextViewerInteractiveViewDefaults.NUM_COLUMNS_KEY, Integer.valueOf(80), Environment.getSystemWide())).intValue());
/* 144:    */     
/* 145:    */ 
/* 146:    */ 
/* 147:    */ 
/* 148:    */ 
/* 149:176 */     this.m_history.setBackground((Color)settings.getSetting("weka.gui.knowledgeflow.steps.textviewer", TextViewerInteractiveViewDefaults.OUTPUT_BACKGROUND_COLOR_KEY, TextViewerInteractiveViewDefaults.OUTPUT_BACKGROUND_COLOR, Environment.getSystemWide()));
/* 150:    */   }
/* 151:    */   
/* 152:    */   public String getViewerName()
/* 153:    */   {
/* 154:190 */     return "Text Viewer";
/* 155:    */   }
/* 156:    */   
/* 157:    */   protected void visualize(String name, int x, int y)
/* 158:    */   {
/* 159:202 */     final JPanel panel = this;
/* 160:203 */     final String selectedName = name;
/* 161:204 */     JPopupMenu resultListMenu = new JPopupMenu();
/* 162:    */     
/* 163:206 */     JMenuItem visMainBuffer = new JMenuItem("View in main window");
/* 164:207 */     if (selectedName != null) {
/* 165:208 */       visMainBuffer.addActionListener(new ActionListener()
/* 166:    */       {
/* 167:    */         public void actionPerformed(ActionEvent e)
/* 168:    */         {
/* 169:211 */           TextViewerInteractiveView.this.m_history.setSingle(selectedName);
/* 170:    */         }
/* 171:    */       });
/* 172:    */     } else {
/* 173:215 */       visMainBuffer.setEnabled(false);
/* 174:    */     }
/* 175:217 */     resultListMenu.add(visMainBuffer);
/* 176:    */     
/* 177:219 */     JMenuItem visSepBuffer = new JMenuItem("View in separate window");
/* 178:220 */     if (selectedName != null) {
/* 179:221 */       visSepBuffer.addActionListener(new ActionListener()
/* 180:    */       {
/* 181:    */         public void actionPerformed(ActionEvent e)
/* 182:    */         {
/* 183:224 */           TextViewerInteractiveView.this.m_history.openFrame(selectedName);
/* 184:    */         }
/* 185:    */       });
/* 186:    */     } else {
/* 187:228 */       visSepBuffer.setEnabled(false);
/* 188:    */     }
/* 189:230 */     resultListMenu.add(visSepBuffer);
/* 190:    */     
/* 191:232 */     JMenuItem saveOutput = new JMenuItem("Save result buffer");
/* 192:233 */     if (selectedName != null) {
/* 193:234 */       saveOutput.addActionListener(new ActionListener()
/* 194:    */       {
/* 195:    */         public void actionPerformed(ActionEvent e)
/* 196:    */         {
/* 197:237 */           SaveBuffer saveOut = new SaveBuffer(null, panel);
/* 198:238 */           StringBuffer sb = TextViewerInteractiveView.this.m_history.getNamedBuffer(selectedName);
/* 199:239 */           if (sb != null) {
/* 200:240 */             saveOut.save(sb);
/* 201:    */           }
/* 202:    */         }
/* 203:    */       });
/* 204:    */     } else {
/* 205:245 */       saveOutput.setEnabled(false);
/* 206:    */     }
/* 207:247 */     resultListMenu.add(saveOutput);
/* 208:    */     
/* 209:249 */     JMenuItem deleteOutput = new JMenuItem("Delete result buffer");
/* 210:250 */     if (selectedName != null) {
/* 211:251 */       deleteOutput.addActionListener(new ActionListener()
/* 212:    */       {
/* 213:    */         public void actionPerformed(ActionEvent e)
/* 214:    */         {
/* 215:254 */           TextViewerInteractiveView.this.m_history.removeResult(selectedName);
/* 216:    */         }
/* 217:    */       });
/* 218:    */     } else {
/* 219:258 */       deleteOutput.setEnabled(false);
/* 220:    */     }
/* 221:260 */     resultListMenu.add(deleteOutput);
/* 222:    */     
/* 223:262 */     resultListMenu.show(this.m_history.getList(), x, y);
/* 224:    */   }
/* 225:    */   
/* 226:    */   public Defaults getDefaultSettings()
/* 227:    */   {
/* 228:272 */     return new TextViewerInteractiveViewDefaults();
/* 229:    */   }
/* 230:    */   
/* 231:    */   public void acceptTextResult(String name, String text)
/* 232:    */   {
/* 233:283 */     this.m_history.addResult(name, new StringBuffer().append(text));
/* 234:284 */     this.m_history.setSingle(name);
/* 235:    */   }
/* 236:    */   
/* 237:    */   protected static final class TextViewerInteractiveViewDefaults
/* 238:    */     extends Defaults
/* 239:    */   {
/* 240:    */     public static final String ID = "weka.gui.knowledgeflow.steps.textviewer";
/* 241:295 */     protected static final Settings.SettingKey OUTPUT_FONT_KEY = new Settings.SettingKey("weka.gui.knowledgeflow.steps.textviewer.outputFont", "Font for text output", "Font to use in the output area");
/* 242:298 */     protected static final Font OUTPUT_FONT = new Font("Monospaced", 0, 12);
/* 243:301 */     protected static final Settings.SettingKey OUTPUT_TEXT_COLOR_KEY = new Settings.SettingKey("weka.gui.knowledgeflow.steps.textviewer.outputFontColor", "Output text color", "Color of output text");
/* 244:304 */     protected static final Color OUTPUT_TEXT_COLOR = Color.black;
/* 245:306 */     protected static final Settings.SettingKey OUTPUT_BACKGROUND_COLOR_KEY = new Settings.SettingKey("weka.gui.knowledgeflow.steps.textviewer.outputBackgroundColor", "Output background color", "Output background color");
/* 246:309 */     protected static final Color OUTPUT_BACKGROUND_COLOR = Color.white;
/* 247:311 */     protected static final Settings.SettingKey NUM_COLUMNS_KEY = new Settings.SettingKey("weka.gui.knowledgeflow.steps.textviewer.numColumns", "Number of columns of text", "Number of columns of text");
/* 248:    */     protected static final int NUM_COLUMNS = 80;
/* 249:316 */     protected static final Settings.SettingKey NUM_ROWS_KEY = new Settings.SettingKey("weka.gui.knowledgeflow.steps.textviewer.numRows", "Number of rows of text", "Number of rows of text");
/* 250:    */     protected static final int NUM_ROWS = 20;
/* 251:    */     private static final long serialVersionUID = 8361658568822013306L;
/* 252:    */     
/* 253:    */     public TextViewerInteractiveViewDefaults()
/* 254:    */     {
/* 255:324 */       super();
/* 256:325 */       this.m_defaults.put(OUTPUT_FONT_KEY, OUTPUT_FONT);
/* 257:326 */       this.m_defaults.put(OUTPUT_TEXT_COLOR_KEY, OUTPUT_TEXT_COLOR);
/* 258:327 */       this.m_defaults.put(OUTPUT_BACKGROUND_COLOR_KEY, OUTPUT_BACKGROUND_COLOR);
/* 259:328 */       this.m_defaults.put(NUM_COLUMNS_KEY, Integer.valueOf(80));
/* 260:329 */       this.m_defaults.put(NUM_ROWS_KEY, Integer.valueOf(20));
/* 261:    */     }
/* 262:    */   }
/* 263:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.TextViewerInteractiveView
 * JD-Core Version:    0.7.0.1
 */