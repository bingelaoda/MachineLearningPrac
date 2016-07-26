/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Component;
/*   4:    */ import java.awt.event.KeyAdapter;
/*   5:    */ import java.awt.event.KeyEvent;
/*   6:    */ import java.awt.event.MouseAdapter;
/*   7:    */ import java.awt.event.MouseEvent;
/*   8:    */ import java.util.Vector;
/*   9:    */ import javax.swing.DefaultListModel;
/*  10:    */ import javax.swing.JCheckBox;
/*  11:    */ import javax.swing.JList;
/*  12:    */ import javax.swing.ListCellRenderer;
/*  13:    */ import javax.swing.ListModel;
/*  14:    */ 
/*  15:    */ public class CheckBoxList
/*  16:    */   extends JList
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = -4359573373359270258L;
/*  19:    */   
/*  20:    */   protected class CheckBoxListItem
/*  21:    */   {
/*  22: 60 */     private boolean m_Checked = false;
/*  23: 63 */     private Object m_Content = null;
/*  24:    */     
/*  25:    */     public CheckBoxListItem(Object o)
/*  26:    */     {
/*  27: 71 */       this(o, false);
/*  28:    */     }
/*  29:    */     
/*  30:    */     public CheckBoxListItem(Object o, boolean checked)
/*  31:    */     {
/*  32: 82 */       this.m_Checked = checked;
/*  33: 83 */       this.m_Content = o;
/*  34:    */     }
/*  35:    */     
/*  36:    */     public Object getContent()
/*  37:    */     {
/*  38: 90 */       return this.m_Content;
/*  39:    */     }
/*  40:    */     
/*  41:    */     public void setChecked(boolean value)
/*  42:    */     {
/*  43: 97 */       this.m_Checked = value;
/*  44:    */     }
/*  45:    */     
/*  46:    */     public boolean getChecked()
/*  47:    */     {
/*  48:104 */       return this.m_Checked;
/*  49:    */     }
/*  50:    */     
/*  51:    */     public String toString()
/*  52:    */     {
/*  53:112 */       return this.m_Content.toString();
/*  54:    */     }
/*  55:    */     
/*  56:    */     public boolean equals(Object o)
/*  57:    */     {
/*  58:125 */       if (!(o instanceof CheckBoxListItem)) {
/*  59:126 */         throw new IllegalArgumentException("Must be a CheckBoxListItem!");
/*  60:    */       }
/*  61:129 */       return getContent().equals(((CheckBoxListItem)o).getContent());
/*  62:    */     }
/*  63:    */   }
/*  64:    */   
/*  65:    */   public class CheckBoxListModel
/*  66:    */     extends DefaultListModel
/*  67:    */   {
/*  68:    */     private static final long serialVersionUID = 7772455499540273507L;
/*  69:    */     
/*  70:    */     public CheckBoxListModel() {}
/*  71:    */     
/*  72:    */     public CheckBoxListModel(Object[] listData)
/*  73:    */     {
/*  74:159 */       for (Object element : listData) {
/*  75:160 */         addElement(element);
/*  76:    */       }
/*  77:    */     }
/*  78:    */     
/*  79:    */     public CheckBoxListModel(Vector listData)
/*  80:    */     {
/*  81:169 */       for (int i = 0; i < listData.size(); i++) {
/*  82:170 */         addElement(listData.get(i));
/*  83:    */       }
/*  84:    */     }
/*  85:    */     
/*  86:    */     public void add(int index, Object element)
/*  87:    */     {
/*  88:182 */       if (!(element instanceof CheckBoxList.CheckBoxListItem)) {
/*  89:183 */         super.add(index, new CheckBoxList.CheckBoxListItem(CheckBoxList.this, element));
/*  90:    */       } else {
/*  91:185 */         super.add(index, element);
/*  92:    */       }
/*  93:    */     }
/*  94:    */     
/*  95:    */     public void addElement(Object obj)
/*  96:    */     {
/*  97:196 */       if (!(obj instanceof CheckBoxList.CheckBoxListItem)) {
/*  98:197 */         super.addElement(new CheckBoxList.CheckBoxListItem(CheckBoxList.this, obj));
/*  99:    */       } else {
/* 100:199 */         super.addElement(obj);
/* 101:    */       }
/* 102:    */     }
/* 103:    */     
/* 104:    */     public boolean contains(Object elem)
/* 105:    */     {
/* 106:211 */       if (!(elem instanceof CheckBoxList.CheckBoxListItem)) {
/* 107:212 */         return super.contains(new CheckBoxList.CheckBoxListItem(CheckBoxList.this, elem));
/* 108:    */       }
/* 109:214 */       return super.contains(elem);
/* 110:    */     }
/* 111:    */     
/* 112:    */     public void copyInto(Object[] anArray)
/* 113:    */     {
/* 114:226 */       if (anArray.length < getSize()) {
/* 115:227 */         throw new IndexOutOfBoundsException("Array not big enough!");
/* 116:    */       }
/* 117:230 */       for (int i = 0; i < getSize(); i++) {
/* 118:231 */         anArray[i] = ((CheckBoxList.CheckBoxListItem)getElementAt(i)).getContent();
/* 119:    */       }
/* 120:    */     }
/* 121:    */     
/* 122:    */     public Object elementAt(int index)
/* 123:    */     {
/* 124:246 */       return ((CheckBoxList.CheckBoxListItem)super.elementAt(index)).getContent();
/* 125:    */     }
/* 126:    */     
/* 127:    */     public Object firstElement()
/* 128:    */     {
/* 129:258 */       return ((CheckBoxList.CheckBoxListItem)super.firstElement()).getContent();
/* 130:    */     }
/* 131:    */     
/* 132:    */     public Object get(int index)
/* 133:    */     {
/* 134:269 */       return ((CheckBoxList.CheckBoxListItem)super.get(index)).getContent();
/* 135:    */     }
/* 136:    */     
/* 137:    */     public Object getElementAt(int index)
/* 138:    */     {
/* 139:281 */       return ((CheckBoxList.CheckBoxListItem)super.getElementAt(index)).getContent();
/* 140:    */     }
/* 141:    */     
/* 142:    */     public int indexOf(Object elem)
/* 143:    */     {
/* 144:293 */       if (!(elem instanceof CheckBoxList.CheckBoxListItem)) {
/* 145:294 */         return super.indexOf(new CheckBoxList.CheckBoxListItem(CheckBoxList.this, elem));
/* 146:    */       }
/* 147:296 */       return super.indexOf(elem);
/* 148:    */     }
/* 149:    */     
/* 150:    */     public int indexOf(Object elem, int index)
/* 151:    */     {
/* 152:310 */       if (!(elem instanceof CheckBoxList.CheckBoxListItem)) {
/* 153:311 */         return super.indexOf(new CheckBoxList.CheckBoxListItem(CheckBoxList.this, elem), index);
/* 154:    */       }
/* 155:313 */       return super.indexOf(elem, index);
/* 156:    */     }
/* 157:    */     
/* 158:    */     public void insertElementAt(Object obj, int index)
/* 159:    */     {
/* 160:327 */       if (!(obj instanceof CheckBoxList.CheckBoxListItem)) {
/* 161:328 */         super.insertElementAt(new CheckBoxList.CheckBoxListItem(CheckBoxList.this, obj), index);
/* 162:    */       } else {
/* 163:330 */         super.insertElementAt(obj, index);
/* 164:    */       }
/* 165:    */     }
/* 166:    */     
/* 167:    */     public Object lastElement()
/* 168:    */     {
/* 169:343 */       return ((CheckBoxList.CheckBoxListItem)super.lastElement()).getContent();
/* 170:    */     }
/* 171:    */     
/* 172:    */     public int lastIndexOf(Object elem)
/* 173:    */     {
/* 174:355 */       if (!(elem instanceof CheckBoxList.CheckBoxListItem)) {
/* 175:356 */         return super.lastIndexOf(new CheckBoxList.CheckBoxListItem(CheckBoxList.this, elem));
/* 176:    */       }
/* 177:358 */       return super.lastIndexOf(elem);
/* 178:    */     }
/* 179:    */     
/* 180:    */     public int lastIndexOf(Object elem, int index)
/* 181:    */     {
/* 182:373 */       if (!(elem instanceof CheckBoxList.CheckBoxListItem)) {
/* 183:374 */         return super.lastIndexOf(new CheckBoxList.CheckBoxListItem(CheckBoxList.this, elem), index);
/* 184:    */       }
/* 185:376 */       return super.lastIndexOf(elem, index);
/* 186:    */     }
/* 187:    */     
/* 188:    */     public Object remove(int index)
/* 189:    */     {
/* 190:389 */       return ((CheckBoxList.CheckBoxListItem)super.remove(index)).getContent();
/* 191:    */     }
/* 192:    */     
/* 193:    */     public boolean removeElement(Object obj)
/* 194:    */     {
/* 195:402 */       if (!(obj instanceof CheckBoxList.CheckBoxListItem)) {
/* 196:403 */         return super.removeElement(new CheckBoxList.CheckBoxListItem(CheckBoxList.this, obj));
/* 197:    */       }
/* 198:405 */       return super.removeElement(obj);
/* 199:    */     }
/* 200:    */     
/* 201:    */     public Object set(int index, Object element)
/* 202:    */     {
/* 203:419 */       if (!(element instanceof CheckBoxList.CheckBoxListItem)) {
/* 204:420 */         return ((CheckBoxList.CheckBoxListItem)super.set(index, new CheckBoxList.CheckBoxListItem(CheckBoxList.this, element))).getContent();
/* 205:    */       }
/* 206:423 */       return ((CheckBoxList.CheckBoxListItem)super.set(index, element)).getContent();
/* 207:    */     }
/* 208:    */     
/* 209:    */     public void setElementAt(Object obj, int index)
/* 210:    */     {
/* 211:437 */       if (!(obj instanceof CheckBoxList.CheckBoxListItem)) {
/* 212:438 */         super.setElementAt(new CheckBoxList.CheckBoxListItem(CheckBoxList.this, obj), index);
/* 213:    */       } else {
/* 214:440 */         super.setElementAt(obj, index);
/* 215:    */       }
/* 216:    */     }
/* 217:    */     
/* 218:    */     public Object[] toArray()
/* 219:    */     {
/* 220:456 */       Object[] internal = super.toArray();
/* 221:457 */       Object[] result = new Object[internal.length];
/* 222:459 */       for (int i = 0; i < internal.length; i++) {
/* 223:460 */         result[i] = ((CheckBoxList.CheckBoxListItem)internal[i]).getContent();
/* 224:    */       }
/* 225:463 */       return result;
/* 226:    */     }
/* 227:    */     
/* 228:    */     public boolean getChecked(int index)
/* 229:    */     {
/* 230:473 */       return ((CheckBoxList.CheckBoxListItem)super.getElementAt(index)).getChecked();
/* 231:    */     }
/* 232:    */     
/* 233:    */     public void setChecked(int index, boolean checked)
/* 234:    */     {
/* 235:483 */       ((CheckBoxList.CheckBoxListItem)super.getElementAt(index)).setChecked(checked);
/* 236:    */     }
/* 237:    */   }
/* 238:    */   
/* 239:    */   public class CheckBoxListRenderer
/* 240:    */     extends JCheckBox
/* 241:    */     implements ListCellRenderer
/* 242:    */   {
/* 243:    */     private static final long serialVersionUID = 1059591605858524586L;
/* 244:    */     
/* 245:    */     public CheckBoxListRenderer() {}
/* 246:    */     
/* 247:    */     public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/* 248:    */     {
/* 249:515 */       setText(value.toString());
/* 250:516 */       setSelected(((CheckBoxList)list).getChecked(index));
/* 251:517 */       setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
/* 252:    */       
/* 253:519 */       setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
/* 254:    */       
/* 255:521 */       setFocusPainted(false);
/* 256:    */       
/* 257:523 */       return this;
/* 258:    */     }
/* 259:    */   }
/* 260:    */   
/* 261:    */   public CheckBoxList()
/* 262:    */   {
/* 263:531 */     this(null);
/* 264:    */   }
/* 265:    */   
/* 266:    */   public CheckBoxList(CheckBoxListModel model)
/* 267:    */   {
/* 268:542 */     if (model == null) {
/* 269:543 */       model = new CheckBoxListModel();
/* 270:    */     }
/* 271:546 */     setModel(model);
/* 272:547 */     setCellRenderer(new CheckBoxListRenderer());
/* 273:    */     
/* 274:549 */     addMouseListener(new MouseAdapter()
/* 275:    */     {
/* 276:    */       public void mousePressed(MouseEvent e)
/* 277:    */       {
/* 278:552 */         int index = CheckBoxList.this.locationToIndex(e.getPoint());
/* 279:554 */         if (index != -1)
/* 280:    */         {
/* 281:555 */           CheckBoxList.this.setChecked(index, !CheckBoxList.this.getChecked(index));
/* 282:556 */           CheckBoxList.this.repaint();
/* 283:    */         }
/* 284:    */       }
/* 285:560 */     });
/* 286:561 */     addKeyListener(new KeyAdapter()
/* 287:    */     {
/* 288:    */       public void keyTyped(KeyEvent e)
/* 289:    */       {
/* 290:564 */         if ((e.getKeyChar() == ' ') && (e.getModifiers() == 0))
/* 291:    */         {
/* 292:565 */           int index = CheckBoxList.this.getSelectedIndex();
/* 293:566 */           CheckBoxList.this.setChecked(index, !CheckBoxList.this.getChecked(index));
/* 294:567 */           e.consume();
/* 295:568 */           CheckBoxList.this.repaint();
/* 296:    */         }
/* 297:    */       }
/* 298:    */     });
/* 299:    */   }
/* 300:    */   
/* 301:    */   public void setModel(ListModel model)
/* 302:    */   {
/* 303:584 */     if (!(model instanceof CheckBoxListModel)) {
/* 304:585 */       throw new IllegalArgumentException("Model must be an instance of CheckBoxListModel!");
/* 305:    */     }
/* 306:589 */     super.setModel(model);
/* 307:    */   }
/* 308:    */   
/* 309:    */   public void setListData(Object[] listData)
/* 310:    */   {
/* 311:600 */     setModel(new CheckBoxListModel(listData));
/* 312:    */   }
/* 313:    */   
/* 314:    */   public void setListData(Vector listData)
/* 315:    */   {
/* 316:609 */     setModel(new CheckBoxListModel(listData));
/* 317:    */   }
/* 318:    */   
/* 319:    */   public boolean getChecked(int index)
/* 320:    */   {
/* 321:619 */     return ((CheckBoxListModel)getModel()).getChecked(index);
/* 322:    */   }
/* 323:    */   
/* 324:    */   public void setChecked(int index, boolean checked)
/* 325:    */   {
/* 326:629 */     ((CheckBoxListModel)getModel()).setChecked(index, checked);
/* 327:    */   }
/* 328:    */   
/* 329:    */   public int[] getCheckedIndices()
/* 330:    */   {
/* 331:643 */     Vector<Integer> list = new Vector();
/* 332:644 */     for (int i = 0; i < getModel().getSize(); i++) {
/* 333:645 */       if (getChecked(i)) {
/* 334:646 */         list.add(new Integer(i));
/* 335:    */       }
/* 336:    */     }
/* 337:651 */     int[] result = new int[list.size()];
/* 338:652 */     for (i = 0; i < list.size(); i++) {
/* 339:653 */       result[i] = ((Integer)list.get(i)).intValue();
/* 340:    */     }
/* 341:656 */     return result;
/* 342:    */   }
/* 343:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.CheckBoxList
 * JD-Core Version:    0.7.0.1
 */