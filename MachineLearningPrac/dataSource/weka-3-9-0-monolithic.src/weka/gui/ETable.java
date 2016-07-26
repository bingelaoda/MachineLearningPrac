/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Component;
/*   6:    */ import java.awt.Container;
/*   7:    */ import java.awt.Dimension;
/*   8:    */ import java.awt.Graphics;
/*   9:    */ import java.awt.Point;
/*  10:    */ import java.awt.Rectangle;
/*  11:    */ import java.awt.event.MouseEvent;
/*  12:    */ import javax.swing.BorderFactory;
/*  13:    */ import javax.swing.JCheckBox;
/*  14:    */ import javax.swing.JComponent;
/*  15:    */ import javax.swing.JLabel;
/*  16:    */ import javax.swing.JPanel;
/*  17:    */ import javax.swing.JScrollPane;
/*  18:    */ import javax.swing.JTable;
/*  19:    */ import javax.swing.JViewport;
/*  20:    */ import javax.swing.UIManager;
/*  21:    */ import javax.swing.border.Border;
/*  22:    */ import javax.swing.table.JTableHeader;
/*  23:    */ import javax.swing.table.TableCellRenderer;
/*  24:    */ import javax.swing.table.TableColumn;
/*  25:    */ import javax.swing.table.TableColumnModel;
/*  26:    */ 
/*  27:    */ public class ETable
/*  28:    */   extends JTable
/*  29:    */ {
/*  30:    */   private static final long serialVersionUID = -3028630226368293049L;
/*  31: 56 */   private final Color MAC_FOCUSED_SELECTED_CELL_HORIZONTAL_LINE_COLOR = new Color(8235754);
/*  32: 58 */   private final Color MAC_UNFOCUSED_SELECTED_CELL_HORIZONTAL_LINE_COLOR = new Color(14737632);
/*  33: 61 */   private final Color MAC_UNFOCUSED_SELECTED_CELL_BACKGROUND_COLOR = new Color(12632256);
/*  34: 64 */   private final Color MAC_FOCUSED_UNSELECTED_VERTICAL_LINE_COLOR = new Color(14277081);
/*  35: 66 */   private final Color MAC_FOCUSED_SELECTED_VERTICAL_LINE_COLOR = new Color(3435966);
/*  36: 68 */   private final Color MAC_UNFOCUSED_UNSELECTED_VERTICAL_LINE_COLOR = new Color(14277081);
/*  37: 70 */   private final Color MAC_UNFOCUSED_SELECTED_VERTICAL_LINE_COLOR = new Color(11316396);
/*  38: 73 */   private final Color MAC_OS_ALTERNATE_ROW_COLOR = new Color(0.92F, 0.95F, 0.99F);
/*  39:    */   
/*  40:    */   public ETable()
/*  41:    */   {
/*  42: 83 */     setShowGrid(false);
/*  43:    */     
/*  44:    */ 
/*  45:    */ 
/*  46: 87 */     setIntercellSpacing(new Dimension());
/*  47:    */     
/*  48:    */ 
/*  49: 90 */     getTableHeader().setReorderingAllowed(false);
/*  50: 92 */     if (System.getProperty("os.name").contains("Mac"))
/*  51:    */     {
/*  52: 94 */       ((JLabel)JLabel.class.cast(getTableHeader().getDefaultRenderer())).setHorizontalAlignment(10);
/*  53:    */       
/*  54:    */ 
/*  55:    */ 
/*  56: 98 */       setShowHorizontalLines(false);
/*  57: 99 */       setShowVerticalLines(true);
/*  58:    */     }
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void paint(Graphics g)
/*  62:    */   {
/*  63:108 */     super.paint(g);
/*  64:109 */     paintEmptyRows(g);
/*  65:    */   }
/*  66:    */   
/*  67:    */   protected void paintEmptyRows(Graphics g)
/*  68:    */   {
/*  69:118 */     int rowCount = getRowCount();
/*  70:119 */     Rectangle clip = g.getClipBounds();
/*  71:120 */     int height = clip.y + clip.height;
/*  72:121 */     if (rowCount * this.rowHeight < height)
/*  73:    */     {
/*  74:122 */       for (int i = rowCount; i <= height / this.rowHeight; i++)
/*  75:    */       {
/*  76:123 */         g.setColor(colorForRow(i));
/*  77:124 */         g.fillRect(clip.x, i * this.rowHeight, clip.width, this.rowHeight);
/*  78:    */       }
/*  79:129 */       if ((System.getProperty("os.name").contains("Mac")) && (getShowVerticalLines()))
/*  80:    */       {
/*  81:131 */         g.setColor(this.MAC_UNFOCUSED_UNSELECTED_VERTICAL_LINE_COLOR);
/*  82:132 */         TableColumnModel columnModel = getColumnModel();
/*  83:133 */         int x = 0;
/*  84:134 */         for (int i = 0; i < columnModel.getColumnCount(); i++)
/*  85:    */         {
/*  86:135 */           TableColumn column = columnModel.getColumn(i);
/*  87:136 */           x += column.getWidth();
/*  88:137 */           g.drawLine(x - 1, rowCount * this.rowHeight, x - 1, height);
/*  89:    */         }
/*  90:    */       }
/*  91:    */     }
/*  92:    */   }
/*  93:    */   
/*  94:    */   public boolean getScrollableTracksViewportHeight()
/*  95:    */   {
/*  96:150 */     if ((getParent() instanceof JViewport))
/*  97:    */     {
/*  98:151 */       JViewport parent = (JViewport)getParent();
/*  99:152 */       return parent.getHeight() > getPreferredSize().height;
/* 100:    */     }
/* 101:154 */     return false;
/* 102:    */   }
/* 103:    */   
/* 104:    */   protected Color colorForRow(int row)
/* 105:    */   {
/* 106:161 */     return row % 2 == 0 ? alternateRowColor() : getBackground();
/* 107:    */   }
/* 108:    */   
/* 109:    */   private Color alternateRowColor()
/* 110:    */   {
/* 111:165 */     return UIManager.getLookAndFeel().getClass().getName().contains("GTK") ? Color.WHITE : this.MAC_OS_ALTERNATE_ROW_COLOR;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
/* 115:    */   {
/* 116:175 */     Component c = super.prepareRenderer(renderer, row, column);
/* 117:176 */     boolean focused = hasFocus();
/* 118:177 */     boolean selected = isCellSelected(row, column);
/* 119:178 */     if (selected)
/* 120:    */     {
/* 121:179 */       if ((System.getProperty("os.name").contains("Mac")) && (!focused))
/* 122:    */       {
/* 123:183 */         c.setBackground(this.MAC_UNFOCUSED_SELECTED_CELL_BACKGROUND_COLOR);
/* 124:184 */         c.setForeground(UIManager.getColor("Table.foreground"));
/* 125:    */       }
/* 126:    */       else
/* 127:    */       {
/* 128:186 */         c.setBackground(UIManager.getColor("Table.selectionBackground"));
/* 129:187 */         c.setForeground(UIManager.getColor("Table.selectionForeground"));
/* 130:    */       }
/* 131:    */     }
/* 132:    */     else
/* 133:    */     {
/* 134:191 */       c.setBackground(colorForRow(row));
/* 135:192 */       c.setForeground(UIManager.getColor("Table.foreground"));
/* 136:    */     }
/* 137:195 */     if ((c instanceof JComponent))
/* 138:    */     {
/* 139:196 */       JComponent jc = (JComponent)c;
/* 140:201 */       if ((UIManager.getLookAndFeel().getClass().getName().contains("GTK")) && ((c instanceof JCheckBox))) {
/* 141:203 */         jc.setOpaque(true);
/* 142:    */       }
/* 143:206 */       if ((!getCellSelectionEnabled()) && (!isEditing())) {
/* 144:207 */         if (System.getProperty("os.name").contains("Mac")) {
/* 145:211 */           fixMacOsCellRendererBorder(jc, selected, focused);
/* 146:    */         } else {
/* 147:215 */           jc.setBorder(null);
/* 148:    */         }
/* 149:    */       }
/* 150:219 */       initToolTip(jc, row, column);
/* 151:    */     }
/* 152:222 */     return c;
/* 153:    */   }
/* 154:    */   
/* 155:    */   private void fixMacOsCellRendererBorder(JComponent renderer, boolean selected, boolean focused)
/* 156:    */   {
/* 157:    */     Border border;
/* 158:    */     Border border;
/* 159:228 */     if (selected) {
/* 160:229 */       border = BorderFactory.createMatteBorder(0, 0, 1, 0, focused ? this.MAC_FOCUSED_SELECTED_CELL_HORIZONTAL_LINE_COLOR : this.MAC_UNFOCUSED_SELECTED_CELL_HORIZONTAL_LINE_COLOR);
/* 161:    */     } else {
/* 162:234 */       border = BorderFactory.createEmptyBorder(0, 0, 1, 0);
/* 163:    */     }
/* 164:239 */     if (getShowVerticalLines())
/* 165:    */     {
/* 166:    */       Color verticalLineColor;
/* 167:    */       Color verticalLineColor;
/* 168:241 */       if (focused) {
/* 169:242 */         verticalLineColor = selected ? this.MAC_FOCUSED_SELECTED_VERTICAL_LINE_COLOR : this.MAC_FOCUSED_UNSELECTED_VERTICAL_LINE_COLOR;
/* 170:    */       } else {
/* 171:246 */         verticalLineColor = selected ? this.MAC_UNFOCUSED_SELECTED_VERTICAL_LINE_COLOR : this.MAC_UNFOCUSED_UNSELECTED_VERTICAL_LINE_COLOR;
/* 172:    */       }
/* 173:250 */       Border verticalBorder = BorderFactory.createMatteBorder(0, 0, 0, 1, verticalLineColor);
/* 174:    */       
/* 175:252 */       border = BorderFactory.createCompoundBorder(border, verticalBorder);
/* 176:    */     }
/* 177:255 */     renderer.setBorder(border);
/* 178:    */   }
/* 179:    */   
/* 180:    */   private void initToolTip(JComponent c, int row, int column)
/* 181:    */   {
/* 182:264 */     String toolTipText = null;
/* 183:265 */     if (c.getPreferredSize().width > getCellRect(row, column, false).width) {
/* 184:266 */       toolTipText = getValueAt(row, column).toString();
/* 185:    */     }
/* 186:268 */     c.setToolTipText(toolTipText);
/* 187:    */   }
/* 188:    */   
/* 189:    */   public Point getToolTipLocation(MouseEvent e)
/* 190:    */   {
/* 191:285 */     if (getToolTipText(e) == null) {
/* 192:286 */       return null;
/* 193:    */     }
/* 194:288 */     int row = rowAtPoint(e.getPoint());
/* 195:289 */     int column = columnAtPoint(e.getPoint());
/* 196:290 */     if ((row == -1) || (column == -1)) {
/* 197:291 */       return null;
/* 198:    */     }
/* 199:293 */     return getCellRect(row, column, false).getLocation();
/* 200:    */   }
/* 201:    */   
/* 202:    */   protected void configureEnclosingScrollPane()
/* 203:    */   {
/* 204:302 */     super.configureEnclosingScrollPane();
/* 205:304 */     if (!System.getProperty("os.name").contains("Mac")) {
/* 206:305 */       return;
/* 207:    */     }
/* 208:308 */     Container p = getParent();
/* 209:309 */     if ((p instanceof JViewport))
/* 210:    */     {
/* 211:310 */       Container gp = p.getParent();
/* 212:311 */       if ((gp instanceof JScrollPane))
/* 213:    */       {
/* 214:312 */         JScrollPane scrollPane = (JScrollPane)gp;
/* 215:    */         
/* 216:    */ 
/* 217:    */ 
/* 218:316 */         JViewport viewport = scrollPane.getViewport();
/* 219:317 */         if ((viewport == null) || (viewport.getView() != this)) {
/* 220:318 */           return;
/* 221:    */         }
/* 222:324 */         Component renderer = new JTableHeader().getDefaultRenderer().getTableCellRendererComponent(null, "", false, false, -1, 0);
/* 223:    */         
/* 224:    */ 
/* 225:327 */         JPanel panel = new JPanel(new BorderLayout());
/* 226:328 */         panel.add(renderer, "Center");
/* 227:329 */         scrollPane.setCorner("UPPER_RIGHT_CORNER", panel);
/* 228:    */       }
/* 229:    */     }
/* 230:    */   }
/* 231:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.ETable
 * JD-Core Version:    0.7.0.1
 */