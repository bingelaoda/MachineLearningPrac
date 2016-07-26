/*   1:    */ package weka.gui.treevisualizer;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.FontMetrics;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.io.StringReader;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.core.Instances;
/*  10:    */ 
/*  11:    */ public class Node
/*  12:    */ {
/*  13:    */   private int m_shape;
/*  14:    */   private Color m_color;
/*  15:    */   private final String m_label;
/*  16:    */   private final Vector<String> m_lines;
/*  17:    */   private double m_center;
/*  18:    */   private double m_top;
/*  19:    */   private boolean m_cVisible;
/*  20:    */   private boolean m_visible;
/*  21:    */   private boolean m_root;
/*  22:    */   private final Vector<Edge> m_parent;
/*  23:    */   private final Vector<Edge> m_children;
/*  24:    */   private String m_refer;
/*  25:    */   private String m_data;
/*  26:    */   private Instances m_theData;
/*  27:    */   
/*  28:    */   public Node(String label, String refer, int backstyle, int shape, Color color, String d)
/*  29:    */   {
/*  30:112 */     this.m_label = label;
/*  31:    */     
/*  32:114 */     this.m_shape = shape;
/*  33:115 */     this.m_color = color;
/*  34:116 */     this.m_refer = refer;
/*  35:    */     
/*  36:118 */     this.m_center = 0.0D;
/*  37:119 */     this.m_top = 0.0D;
/*  38:    */     
/*  39:121 */     this.m_cVisible = true;
/*  40:122 */     this.m_visible = true;
/*  41:123 */     this.m_root = false;
/*  42:124 */     this.m_parent = new Vector(1, 1);
/*  43:125 */     this.m_children = new Vector(20, 10);
/*  44:126 */     this.m_lines = new Vector(4, 2);
/*  45:127 */     breakupLabel();
/*  46:128 */     this.m_data = d;
/*  47:129 */     this.m_theData = null;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public Instances getInstances()
/*  51:    */   {
/*  52:139 */     if ((this.m_theData == null) && (this.m_data != null))
/*  53:    */     {
/*  54:    */       try
/*  55:    */       {
/*  56:141 */         this.m_theData = new Instances(new StringReader(this.m_data));
/*  57:    */       }
/*  58:    */       catch (Exception e)
/*  59:    */       {
/*  60:143 */         System.out.println("Error : " + e);
/*  61:    */       }
/*  62:145 */       this.m_data = null;
/*  63:    */     }
/*  64:147 */     return this.m_theData;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public boolean getCVisible()
/*  68:    */   {
/*  69:156 */     return this.m_cVisible;
/*  70:    */   }
/*  71:    */   
/*  72:    */   private void childVis(Node r)
/*  73:    */   {
/*  74:167 */     r.setVisible(true);
/*  75:168 */     if (r.getCVisible())
/*  76:    */     {
/*  77:    */       Edge e;
/*  78:169 */       for (int noa = 0; (e = r.getChild(noa)) != null; noa++) {
/*  79:170 */         childVis(e.getTarget());
/*  80:    */       }
/*  81:    */     }
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setCVisible(boolean v)
/*  85:    */   {
/*  86:181 */     this.m_cVisible = v;
/*  87:182 */     if (v) {
/*  88:183 */       childVis(this);
/*  89:184 */     } else if (!v) {
/*  90:185 */       childInv(this);
/*  91:    */     }
/*  92:    */   }
/*  93:    */   
/*  94:    */   private void childInv(Node r)
/*  95:    */   {
/*  96:    */     Edge e;
/*  97:199 */     for (int noa = 0; (e = r.getChild(noa)) != null; noa++)
/*  98:    */     {
/*  99:200 */       Node s = e.getTarget();
/* 100:201 */       s.setVisible(false);
/* 101:202 */       childInv(s);
/* 102:    */     }
/* 103:    */   }
/* 104:    */   
/* 105:    */   public String getRefer()
/* 106:    */   {
/* 107:213 */     return this.m_refer;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public void setRefer(String v)
/* 111:    */   {
/* 112:223 */     this.m_refer = v;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public int getShape()
/* 116:    */   {
/* 117:233 */     return this.m_shape;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void setShape(int v)
/* 121:    */   {
/* 122:243 */     this.m_shape = v;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public Color getColor()
/* 126:    */   {
/* 127:253 */     return this.m_color;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void setColor(Color v)
/* 131:    */   {
/* 132:263 */     this.m_color = v;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public String getLabel()
/* 136:    */   {
/* 137:273 */     return this.m_label;
/* 138:    */   }
/* 139:    */   
/* 140:    */   private void breakupLabel()
/* 141:    */   {
/* 142:281 */     int prev = 0;
/* 143:282 */     for (int noa = 0; noa < this.m_label.length(); noa++) {
/* 144:283 */       if (this.m_label.charAt(noa) == '\n')
/* 145:    */       {
/* 146:284 */         this.m_lines.addElement(this.m_label.substring(prev, noa));
/* 147:285 */         prev = noa + 1;
/* 148:    */       }
/* 149:    */     }
/* 150:288 */     this.m_lines.addElement(this.m_label.substring(prev, noa));
/* 151:    */   }
/* 152:    */   
/* 153:    */   public Dimension stringSize(FontMetrics f)
/* 154:    */   {
/* 155:300 */     Dimension d = new Dimension();
/* 156:301 */     int old = 0;
/* 157:    */     
/* 158:303 */     int noa = 0;
/* 159:    */     String s;
/* 160:304 */     while ((s = getLine(noa)) != null)
/* 161:    */     {
/* 162:305 */       noa++;
/* 163:306 */       old = f.stringWidth(s);
/* 164:308 */       if (old > d.width) {
/* 165:309 */         d.width = old;
/* 166:    */       }
/* 167:    */     }
/* 168:312 */     d.height = (noa * f.getHeight());
/* 169:313 */     return d;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public String getLine(int n)
/* 173:    */   {
/* 174:324 */     if (n < this.m_lines.size()) {
/* 175:325 */       return (String)this.m_lines.elementAt(n);
/* 176:    */     }
/* 177:327 */     return null;
/* 178:    */   }
/* 179:    */   
/* 180:    */   public double getCenter()
/* 181:    */   {
/* 182:338 */     return this.m_center;
/* 183:    */   }
/* 184:    */   
/* 185:    */   public void setCenter(double v)
/* 186:    */   {
/* 187:348 */     this.m_center = v;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public void adjustCenter(double v)
/* 191:    */   {
/* 192:357 */     this.m_center += v;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public double getTop()
/* 196:    */   {
/* 197:367 */     return this.m_top;
/* 198:    */   }
/* 199:    */   
/* 200:    */   public void setTop(double v)
/* 201:    */   {
/* 202:377 */     this.m_top = v;
/* 203:    */   }
/* 204:    */   
/* 205:    */   public boolean getVisible()
/* 206:    */   {
/* 207:387 */     return this.m_visible;
/* 208:    */   }
/* 209:    */   
/* 210:    */   private void setVisible(boolean v)
/* 211:    */   {
/* 212:397 */     this.m_visible = v;
/* 213:    */   }
/* 214:    */   
/* 215:    */   public boolean getRoot()
/* 216:    */   {
/* 217:407 */     return this.m_root;
/* 218:    */   }
/* 219:    */   
/* 220:    */   public void setRoot(boolean v)
/* 221:    */   {
/* 222:417 */     this.m_root = v;
/* 223:    */   }
/* 224:    */   
/* 225:    */   public Edge getParent(int i)
/* 226:    */   {
/* 227:428 */     if (i < this.m_parent.size()) {
/* 228:429 */       return (Edge)this.m_parent.elementAt(i);
/* 229:    */     }
/* 230:431 */     return null;
/* 231:    */   }
/* 232:    */   
/* 233:    */   public void setParent(Edge v)
/* 234:    */   {
/* 235:443 */     this.m_parent.addElement(v);
/* 236:    */   }
/* 237:    */   
/* 238:    */   public Edge getChild(int i)
/* 239:    */   {
/* 240:454 */     if (i < this.m_children.size()) {
/* 241:455 */       return (Edge)this.m_children.elementAt(i);
/* 242:    */     }
/* 243:457 */     return null;
/* 244:    */   }
/* 245:    */   
/* 246:    */   public void addChild(Edge v)
/* 247:    */   {
/* 248:467 */     this.m_children.addElement(v);
/* 249:    */   }
/* 250:    */   
/* 251:    */   public static int getGCount(Node r, int n)
/* 252:    */   {
/* 253:480 */     if ((r.getChild(0) != null) && (r.getCVisible()))
/* 254:    */     {
/* 255:481 */       n++;
/* 256:    */       Edge e;
/* 257:482 */       for (int noa = 0; (e = r.getChild(noa)) != null; noa++) {
/* 258:483 */         n = getGCount(e.getTarget(), n);
/* 259:    */       }
/* 260:    */     }
/* 261:486 */     return n;
/* 262:    */   }
/* 263:    */   
/* 264:    */   public static int getTotalGCount(Node r, int n)
/* 265:    */   {
/* 266:499 */     if (r.getChild(0) != null)
/* 267:    */     {
/* 268:500 */       n++;
/* 269:    */       Edge e;
/* 270:501 */       for (int noa = 0; (e = r.getChild(noa)) != null; noa++) {
/* 271:502 */         n = getTotalGCount(e.getTarget(), n);
/* 272:    */       }
/* 273:    */     }
/* 274:505 */     return n;
/* 275:    */   }
/* 276:    */   
/* 277:    */   public static int getCount(Node r, int n)
/* 278:    */   {
/* 279:    */     
/* 280:    */     Edge e;
/* 281:519 */     for (int noa = 0; ((e = r.getChild(noa)) != null) && (r.getCVisible()); noa++) {
/* 282:520 */       n = getCount(e.getTarget(), n);
/* 283:    */     }
/* 284:522 */     return n;
/* 285:    */   }
/* 286:    */   
/* 287:    */   public static int getTotalCount(Node r, int n)
/* 288:    */   {
/* 289:    */     
/* 290:    */     Edge e;
/* 291:536 */     for (int noa = 0; (e = r.getChild(noa)) != null; noa++) {
/* 292:537 */       n = getTotalCount(e.getTarget(), n);
/* 293:    */     }
/* 294:539 */     return n;
/* 295:    */   }
/* 296:    */   
/* 297:    */   public static int getHeight(Node r, int l)
/* 298:    */   {
/* 299:550 */     l++;
/* 300:551 */     int lev = l;int temp = 0;
/* 301:    */     Edge e;
/* 302:554 */     for (int noa = 0; ((e = r.getChild(noa)) != null) && (r.getCVisible()); noa++)
/* 303:    */     {
/* 304:555 */       temp = getHeight(e.getTarget(), l);
/* 305:556 */       if (temp > lev) {
/* 306:557 */         lev = temp;
/* 307:    */       }
/* 308:    */     }
/* 309:562 */     return lev;
/* 310:    */   }
/* 311:    */   
/* 312:    */   public static int getTotalHeight(Node r, int l)
/* 313:    */   {
/* 314:574 */     l++;
/* 315:575 */     int lev = l;int temp = 0;
/* 316:    */     Edge e;
/* 317:578 */     for (int noa = 0; (e = r.getChild(noa)) != null; noa++)
/* 318:    */     {
/* 319:579 */       temp = getTotalHeight(e.getTarget(), l);
/* 320:580 */       if (temp > lev) {
/* 321:581 */         lev = temp;
/* 322:    */       }
/* 323:    */     }
/* 324:584 */     return lev;
/* 325:    */   }
/* 326:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.treevisualizer.Node
 * JD-Core Version:    0.7.0.1
 */