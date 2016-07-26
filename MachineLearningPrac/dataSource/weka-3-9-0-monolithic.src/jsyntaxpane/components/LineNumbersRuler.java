/*   1:    */ package jsyntaxpane.components;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.FontMetrics;
/*   7:    */ import java.awt.Graphics;
/*   8:    */ import java.awt.Rectangle;
/*   9:    */ import java.awt.event.MouseAdapter;
/*  10:    */ import java.awt.event.MouseEvent;
/*  11:    */ import java.awt.event.MouseListener;
/*  12:    */ import java.beans.PropertyChangeEvent;
/*  13:    */ import java.beans.PropertyChangeListener;
/*  14:    */ import java.util.logging.Logger;
/*  15:    */ import javax.swing.JComponent;
/*  16:    */ import javax.swing.JEditorPane;
/*  17:    */ import javax.swing.JScrollPane;
/*  18:    */ import javax.swing.event.DocumentEvent;
/*  19:    */ import javax.swing.event.DocumentListener;
/*  20:    */ import javax.swing.text.Document;
/*  21:    */ import javax.swing.text.JTextComponent;
/*  22:    */ import jsyntaxpane.SyntaxDocument;
/*  23:    */ import jsyntaxpane.actions.ActionUtils;
/*  24:    */ import jsyntaxpane.actions.gui.GotoLineDialog;
/*  25:    */ import jsyntaxpane.util.Configuration;
/*  26:    */ 
/*  27:    */ public class LineNumbersRuler
/*  28:    */   extends JComponent
/*  29:    */   implements SyntaxComponent, PropertyChangeListener, DocumentListener
/*  30:    */ {
/*  31:    */   public static final String PROPERTY_BACKGROUND = "LineNumbers.Background";
/*  32:    */   public static final String PROPERTY_FOREGROUND = "LineNumbers.Foreground";
/*  33:    */   public static final String PROPERTY_LEFT_MARGIN = "LineNumbers.LeftMargin";
/*  34:    */   public static final String PROPERTY_RIGHT_MARGIN = "LineNumbers.RightMargin";
/*  35:    */   public static final String PROPERTY_Y_OFFSET = "LineNumbers.YOFFset";
/*  36:    */   public static final int DEFAULT_R_MARGIN = 5;
/*  37:    */   public static final int DEFAULT_L_MARGIN = 5;
/*  38:    */   public static final int DEFAULT_Y_OFFSET = -2;
/*  39:    */   private JEditorPane pane;
/*  40:    */   private String format;
/*  41: 53 */   private int lineCount = -1;
/*  42: 54 */   private int r_margin = 5;
/*  43: 55 */   private int l_margin = 5;
/*  44: 56 */   private int y_offset = -2;
/*  45:    */   private int charHeight;
/*  46:    */   private int charWidth;
/*  47: 59 */   private MouseListener mouseListener = null;
/*  48:    */   private SyntaxComponent.Status status;
/*  49:    */   
/*  50:    */   protected void paintComponent(Graphics g)
/*  51:    */   {
/*  52: 69 */     g.setFont(this.pane.getFont());
/*  53: 70 */     Rectangle clip = g.getClipBounds();
/*  54: 71 */     g.setColor(getBackground());
/*  55: 72 */     g.fillRect(clip.x, clip.y, clip.width, clip.height);
/*  56: 73 */     g.setColor(getForeground());
/*  57: 74 */     int lh = this.charHeight;
/*  58: 75 */     int end = clip.y + clip.height + lh;
/*  59: 76 */     int lineNum = clip.y / lh + 1;
/*  60: 79 */     for (int y = clip.y / lh * lh + lh + this.y_offset; y <= end; y += lh)
/*  61:    */     {
/*  62: 80 */       String text = String.format(this.format, new Object[] { Integer.valueOf(lineNum) });
/*  63: 81 */       g.drawString(text, this.l_margin, y);
/*  64: 82 */       lineNum++;
/*  65: 83 */       if (lineNum > this.lineCount) {
/*  66:    */         break;
/*  67:    */       }
/*  68:    */     }
/*  69:    */   }
/*  70:    */   
/*  71:    */   private void updateSize()
/*  72:    */   {
/*  73: 93 */     int newLineCount = ActionUtils.getLineCount(this.pane);
/*  74: 94 */     if (newLineCount == this.lineCount) {
/*  75: 95 */       return;
/*  76:    */     }
/*  77: 97 */     this.lineCount = newLineCount;
/*  78: 98 */     int h = this.lineCount * this.charHeight + this.pane.getHeight();
/*  79: 99 */     int d = (int)Math.log10(this.lineCount) + 1;
/*  80:100 */     if (d < 1) {
/*  81:101 */       d = 1;
/*  82:    */     }
/*  83:103 */     int w = d * this.charWidth + this.r_margin + this.l_margin;
/*  84:104 */     this.format = ("%" + d + "d");
/*  85:105 */     setPreferredSize(new Dimension(w, h));
/*  86:106 */     getParent().doLayout();
/*  87:    */   }
/*  88:    */   
/*  89:    */   public JScrollPane getScrollPane(JTextComponent editorPane)
/*  90:    */   {
/*  91:116 */     Container p = editorPane.getParent();
/*  92:117 */     while (p != null)
/*  93:    */     {
/*  94:118 */       if ((p instanceof JScrollPane)) {
/*  95:119 */         return (JScrollPane)p;
/*  96:    */       }
/*  97:121 */       p = p.getParent();
/*  98:    */     }
/*  99:123 */     return null;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void config(Configuration config)
/* 103:    */   {
/* 104:128 */     this.r_margin = config.getInteger("LineNumbers.RightMargin", 5);
/* 105:129 */     this.l_margin = config.getInteger("LineNumbers.LeftMargin", 5);
/* 106:130 */     this.y_offset = config.getInteger("LineNumbers.YOFFset", -2);
/* 107:131 */     Color foreground = config.getColor("LineNumbers.Foreground", Color.BLACK);
/* 108:132 */     setForeground(foreground);
/* 109:133 */     Color back = config.getColor("LineNumbers.Background", Color.WHITE);
/* 110:134 */     setBackground(back);
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void install(JEditorPane editor)
/* 114:    */   {
/* 115:139 */     this.pane = editor;
/* 116:140 */     this.charHeight = this.pane.getFontMetrics(this.pane.getFont()).getHeight();
/* 117:141 */     this.charWidth = this.pane.getFontMetrics(this.pane.getFont()).charWidth('0');
/* 118:142 */     editor.addPropertyChangeListener(this);
/* 119:143 */     JScrollPane sp = getScrollPane(this.pane);
/* 120:144 */     if (sp == null)
/* 121:    */     {
/* 122:145 */       Logger.getLogger(getClass().getName()).warning("JEditorPane is not enclosed in JScrollPane, no LineNumbers will be displayed");
/* 123:    */     }
/* 124:    */     else
/* 125:    */     {
/* 126:149 */       sp.setRowHeaderView(this);
/* 127:150 */       this.pane.getDocument().addDocumentListener(this);
/* 128:151 */       updateSize();
/* 129:152 */       this.mouseListener = new MouseAdapter()
/* 130:    */       {
/* 131:    */         public void mouseClicked(MouseEvent e)
/* 132:    */         {
/* 133:156 */           GotoLineDialog.showForEditor(LineNumbersRuler.this.pane);
/* 134:    */         }
/* 135:158 */       };
/* 136:159 */       addMouseListener(this.mouseListener);
/* 137:    */     }
/* 138:161 */     this.status = SyntaxComponent.Status.INSTALLING;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public void deinstall(JEditorPane editor)
/* 142:    */   {
/* 143:166 */     removeMouseListener(this.mouseListener);
/* 144:167 */     this.status = SyntaxComponent.Status.DEINSTALLING;
/* 145:168 */     JScrollPane sp = getScrollPane(editor);
/* 146:169 */     if (sp != null)
/* 147:    */     {
/* 148:170 */       editor.getDocument().removeDocumentListener(this);
/* 149:171 */       sp.setRowHeaderView(null);
/* 150:    */     }
/* 151:    */   }
/* 152:    */   
/* 153:    */   public void propertyChange(PropertyChangeEvent evt)
/* 154:    */   {
/* 155:177 */     if (evt.getPropertyName().equals("document"))
/* 156:    */     {
/* 157:178 */       if ((evt.getOldValue() instanceof SyntaxDocument))
/* 158:    */       {
/* 159:179 */         SyntaxDocument syntaxDocument = (SyntaxDocument)evt.getOldValue();
/* 160:180 */         syntaxDocument.removeDocumentListener(this);
/* 161:    */       }
/* 162:182 */       if (((evt.getNewValue() instanceof SyntaxDocument)) && (this.status.equals(SyntaxComponent.Status.INSTALLING)))
/* 163:    */       {
/* 164:183 */         SyntaxDocument syntaxDocument = (SyntaxDocument)evt.getNewValue();
/* 165:184 */         syntaxDocument.addDocumentListener(this);
/* 166:185 */         updateSize();
/* 167:    */       }
/* 168:    */     }
/* 169:187 */     else if (evt.getPropertyName().equals("font"))
/* 170:    */     {
/* 171:188 */       this.charHeight = this.pane.getFontMetrics(this.pane.getFont()).getHeight();
/* 172:189 */       this.charWidth = this.pane.getFontMetrics(this.pane.getFont()).charWidth('0');
/* 173:    */     }
/* 174:    */   }
/* 175:    */   
/* 176:    */   public void insertUpdate(DocumentEvent e)
/* 177:    */   {
/* 178:195 */     updateSize();
/* 179:    */   }
/* 180:    */   
/* 181:    */   public void removeUpdate(DocumentEvent e)
/* 182:    */   {
/* 183:200 */     updateSize();
/* 184:    */   }
/* 185:    */   
/* 186:    */   public void changedUpdate(DocumentEvent e)
/* 187:    */   {
/* 188:205 */     updateSize();
/* 189:    */   }
/* 190:    */   
/* 191:    */   public int getLeftMargin()
/* 192:    */   {
/* 193:209 */     return this.l_margin;
/* 194:    */   }
/* 195:    */   
/* 196:    */   public void setLeftMargin(int l_margin)
/* 197:    */   {
/* 198:213 */     this.l_margin = l_margin;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public int getRightMargin()
/* 202:    */   {
/* 203:217 */     return this.r_margin;
/* 204:    */   }
/* 205:    */   
/* 206:    */   public void setRightMargin(int r_margin)
/* 207:    */   {
/* 208:221 */     this.r_margin = r_margin;
/* 209:    */   }
/* 210:    */   
/* 211:    */   public int getYOffset()
/* 212:    */   {
/* 213:225 */     return this.y_offset;
/* 214:    */   }
/* 215:    */   
/* 216:    */   public void setYOffset(int y_offset)
/* 217:    */   {
/* 218:229 */     this.y_offset = y_offset;
/* 219:    */   }
/* 220:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.components.LineNumbersRuler
 * JD-Core Version:    0.7.0.1
 */