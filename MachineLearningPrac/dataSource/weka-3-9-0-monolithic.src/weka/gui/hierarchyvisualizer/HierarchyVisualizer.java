/*   1:    */ package weka.gui.hierarchyvisualizer;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.Graphics;
/*   7:    */ import java.awt.Graphics2D;
/*   8:    */ import java.awt.event.ComponentEvent;
/*   9:    */ import java.awt.event.ComponentListener;
/*  10:    */ import java.io.PrintStream;
/*  11:    */ import javax.swing.JFrame;
/*  12:    */ import weka.gui.visualize.PrintablePanel;
/*  13:    */ 
/*  14:    */ public class HierarchyVisualizer
/*  15:    */   extends PrintablePanel
/*  16:    */   implements ComponentListener
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = 1L;
/*  19:    */   String m_sNewick;
/*  20:    */   Node m_tree;
/*  21:    */   int m_nLeafs;
/*  22:    */   double m_fHeight;
/*  23: 46 */   double m_fScaleX = 10.0D;
/*  24: 47 */   double m_fScaleY = 10.0D;
/*  25:    */   double m_fTmpLength;
/*  26:    */   
/*  27:    */   public HierarchyVisualizer(String sNewick)
/*  28:    */   {
/*  29:    */     try
/*  30:    */     {
/*  31: 51 */       parseNewick(sNewick);
/*  32:    */     }
/*  33:    */     catch (Exception e)
/*  34:    */     {
/*  35: 53 */       e.printStackTrace();
/*  36: 54 */       System.exit(0);
/*  37:    */     }
/*  38: 56 */     addComponentListener(this);
/*  39:    */   }
/*  40:    */   
/*  41:    */   int positionLeafs(Node node, int nPosX)
/*  42:    */   {
/*  43: 60 */     if (node.isLeaf())
/*  44:    */     {
/*  45: 61 */       node.m_fPosX = (nPosX + 0.5D);
/*  46: 62 */       nPosX++;
/*  47: 63 */       return nPosX;
/*  48:    */     }
/*  49: 65 */     for (int i = 0; i < node.m_children.length; i++) {
/*  50: 66 */       nPosX = positionLeafs(node.m_children[i], nPosX);
/*  51:    */     }
/*  52: 69 */     return nPosX;
/*  53:    */   }
/*  54:    */   
/*  55:    */   double positionRest(Node node)
/*  56:    */   {
/*  57: 72 */     if (node.isLeaf()) {
/*  58: 73 */       return node.m_fPosX;
/*  59:    */     }
/*  60: 75 */     double fPosX = 0.0D;
/*  61: 76 */     for (int i = 0; i < node.m_children.length; i++) {
/*  62: 77 */       fPosX += positionRest(node.m_children[i]);
/*  63:    */     }
/*  64: 79 */     fPosX /= node.m_children.length;
/*  65: 80 */     node.m_fPosX = fPosX;
/*  66: 81 */     return fPosX;
/*  67:    */   }
/*  68:    */   
/*  69:    */   double positionHeight(Node node, double fOffSet)
/*  70:    */   {
/*  71: 85 */     if (node.isLeaf())
/*  72:    */     {
/*  73: 86 */       node.m_fPosY = (fOffSet + node.m_fLength);
/*  74: 87 */       return node.m_fPosY;
/*  75:    */     }
/*  76: 89 */     double fPosY = fOffSet + node.m_fLength;
/*  77: 90 */     double fYMax = 0.0D;
/*  78: 91 */     for (int i = 0; i < node.m_children.length; i++) {
/*  79: 92 */       fYMax = Math.max(fYMax, positionHeight(node.m_children[i], fPosY));
/*  80:    */     }
/*  81: 94 */     node.m_fPosY = fPosY;
/*  82: 95 */     return fYMax;
/*  83:    */   }
/*  84:    */   
/*  85:    */   class Node
/*  86:    */   {
/*  87:102 */     double m_fLength = -1.0D;
/*  88:103 */     double m_fPosX = 0.0D;
/*  89:104 */     double m_fPosY = 0.0D;
/*  90:    */     String m_sLabel;
/*  91:    */     String m_sMetaData;
/*  92:    */     Node[] m_children;
/*  93:113 */     Node m_Parent = null;
/*  94:    */     
/*  95:    */     Node() {}
/*  96:    */     
/*  97:    */     Node getParent()
/*  98:    */     {
/*  99:116 */       return this.m_Parent;
/* 100:    */     }
/* 101:    */     
/* 102:    */     void setParent(Node parent)
/* 103:    */     {
/* 104:120 */       this.m_Parent = parent;
/* 105:    */     }
/* 106:    */     
/* 107:    */     boolean isRoot()
/* 108:    */     {
/* 109:125 */       return this.m_Parent == null;
/* 110:    */     }
/* 111:    */     
/* 112:    */     boolean isLeaf()
/* 113:    */     {
/* 114:128 */       return this.m_children == null;
/* 115:    */     }
/* 116:    */     
/* 117:    */     int getChildCount()
/* 118:    */     {
/* 119:134 */       if (this.m_children == null) {
/* 120:134 */         return 0;
/* 121:    */       }
/* 122:135 */       return this.m_children.length;
/* 123:    */     }
/* 124:    */     
/* 125:    */     Node getChild(int iChild)
/* 126:    */     {
/* 127:139 */       return this.m_children[iChild];
/* 128:    */     }
/* 129:    */     
/* 130:    */     int getNodeCount()
/* 131:    */     {
/* 132:145 */       if (this.m_children == null) {
/* 133:146 */         return 1;
/* 134:    */       }
/* 135:148 */       int n = 1;
/* 136:149 */       for (int i = 0; i < this.m_children.length; i++) {
/* 137:150 */         n += this.m_children[i].getNodeCount();
/* 138:    */       }
/* 139:152 */       return n;
/* 140:    */     }
/* 141:    */     
/* 142:    */     public String toString()
/* 143:    */     {
/* 144:155 */       StringBuffer buf = new StringBuffer();
/* 145:156 */       if (this.m_children != null)
/* 146:    */       {
/* 147:157 */         buf.append("(");
/* 148:158 */         for (int i = 0; i < this.m_children.length - 1; i++)
/* 149:    */         {
/* 150:159 */           buf.append(this.m_children[i].toString());
/* 151:160 */           buf.append(',');
/* 152:    */         }
/* 153:162 */         buf.append(this.m_children[(this.m_children.length - 1)].toString());
/* 154:163 */         buf.append(")");
/* 155:    */       }
/* 156:    */       else
/* 157:    */       {
/* 158:165 */         buf.append(this.m_sLabel);
/* 159:    */       }
/* 160:167 */       if (this.m_sMetaData != null)
/* 161:    */       {
/* 162:168 */         buf.append('[');
/* 163:169 */         buf.append(this.m_sMetaData);
/* 164:170 */         buf.append(']');
/* 165:    */       }
/* 166:172 */       buf.append(":" + this.m_fLength);
/* 167:173 */       return buf.toString();
/* 168:    */     }
/* 169:    */     
/* 170:    */     double draw(Graphics g)
/* 171:    */     {
/* 172:177 */       if (isLeaf())
/* 173:    */       {
/* 174:178 */         int x = (int)(this.m_fPosX * HierarchyVisualizer.this.m_fScaleX);
/* 175:179 */         int y = (int)(this.m_fPosY * HierarchyVisualizer.this.m_fScaleY);
/* 176:180 */         g.drawString(this.m_sLabel, x, y);
/* 177:181 */         g.drawLine((int)(this.m_fPosX * HierarchyVisualizer.this.m_fScaleX), (int)(this.m_fPosY * HierarchyVisualizer.this.m_fScaleY), (int)(this.m_fPosX * HierarchyVisualizer.this.m_fScaleX), (int)((this.m_fPosY - this.m_fLength) * HierarchyVisualizer.this.m_fScaleY));
/* 178:    */       }
/* 179:    */       else
/* 180:    */       {
/* 181:183 */         double fPosX1 = 1.7976931348623157E+308D;
/* 182:184 */         double fPosX2 = -1.797693134862316E+308D;
/* 183:185 */         for (int i = 0; i < this.m_children.length; i++)
/* 184:    */         {
/* 185:186 */           double f = this.m_children[i].draw(g);
/* 186:187 */           if (f < fPosX1) {
/* 187:187 */             fPosX1 = f;
/* 188:    */           }
/* 189:188 */           if (f > fPosX2) {
/* 190:188 */             fPosX2 = f;
/* 191:    */           }
/* 192:    */         }
/* 193:190 */         g.drawLine((int)(this.m_fPosX * HierarchyVisualizer.this.m_fScaleX), (int)(this.m_fPosY * HierarchyVisualizer.this.m_fScaleY), (int)(this.m_fPosX * HierarchyVisualizer.this.m_fScaleX), (int)((this.m_fPosY - this.m_fLength) * HierarchyVisualizer.this.m_fScaleY));
/* 194:191 */         g.drawLine((int)(fPosX1 * HierarchyVisualizer.this.m_fScaleX), (int)(this.m_fPosY * HierarchyVisualizer.this.m_fScaleY), (int)(fPosX2 * HierarchyVisualizer.this.m_fScaleX), (int)(this.m_fPosY * HierarchyVisualizer.this.m_fScaleY));
/* 195:    */       }
/* 196:193 */       return this.m_fPosX;
/* 197:    */     }
/* 198:    */   }
/* 199:    */   
/* 200:    */   int nextNode(String sStr, int i)
/* 201:    */   {
/* 202:199 */     int nBraces = 0;
/* 203:200 */     char c = sStr.charAt(i);
/* 204:    */     do
/* 205:    */     {
/* 206:202 */       i++;
/* 207:203 */       if (i < sStr.length())
/* 208:    */       {
/* 209:204 */         c = sStr.charAt(i);
/* 210:206 */         if (c == '[')
/* 211:    */         {
/* 212:207 */           while ((i < sStr.length()) && (sStr.charAt(i) != ']')) {
/* 213:208 */             i++;
/* 214:    */           }
/* 215:210 */           i++;
/* 216:211 */           if (i < sStr.length()) {
/* 217:212 */             c = sStr.charAt(i);
/* 218:    */           }
/* 219:    */         }
/* 220:216 */         switch (c)
/* 221:    */         {
/* 222:    */         case '(': 
/* 223:218 */           nBraces++;
/* 224:219 */           break;
/* 225:    */         case ')': 
/* 226:221 */           nBraces--;
/* 227:    */         }
/* 228:    */       }
/* 229:228 */     } while ((i < sStr.length()) && ((nBraces > 0) || ((c != ',') && (c != ')') && (c != '('))));
/* 230:229 */     if ((i >= sStr.length()) || (nBraces < 0)) {
/* 231:230 */       return -1;
/* 232:    */     }
/* 233:231 */     if (sStr.charAt(i) == ')')
/* 234:    */     {
/* 235:232 */       i++;
/* 236:233 */       if (sStr.charAt(i) == '[')
/* 237:    */       {
/* 238:234 */         while ((i < sStr.length()) && (sStr.charAt(i) != ']')) {
/* 239:235 */           i++;
/* 240:    */         }
/* 241:237 */         i++;
/* 242:238 */         if (i >= sStr.length()) {
/* 243:239 */           return -1;
/* 244:    */         }
/* 245:    */       }
/* 246:242 */       if (sStr.charAt(i) == ':')
/* 247:    */       {
/* 248:243 */         i++;
/* 249:244 */         c = sStr.charAt(i);
/* 250:245 */         while (((i < sStr.length()) && ((c == '.') || (Character.isDigit(c)))) || (c == '-'))
/* 251:    */         {
/* 252:246 */           i++;
/* 253:247 */           if (i < sStr.length()) {
/* 254:248 */             c = sStr.charAt(i);
/* 255:    */           }
/* 256:    */         }
/* 257:    */       }
/* 258:    */     }
/* 259:253 */     return i;
/* 260:    */   }
/* 261:    */   
/* 262:    */   void parseNewick(String sNewick)
/* 263:    */     throws Exception
/* 264:    */   {
/* 265:264 */     this.m_sNewick = sNewick;
/* 266:265 */     int i = this.m_sNewick.indexOf('(');
/* 267:266 */     if (i > 0) {
/* 268:267 */       this.m_sNewick = this.m_sNewick.substring(i);
/* 269:    */     }
/* 270:269 */     System.err.println(this.m_sNewick);
/* 271:270 */     this.m_tree = parseNewick2(this.m_sNewick);
/* 272:271 */     System.err.println(this.m_tree.toString());
/* 273:272 */     this.m_nLeafs = positionLeafs(this.m_tree, 0);
/* 274:273 */     positionRest(this.m_tree);
/* 275:274 */     this.m_fHeight = positionHeight(this.m_tree, 0.0D);
/* 276:    */   }
/* 277:    */   
/* 278:    */   Node parseNewick2(String sStr)
/* 279:    */     throws Exception
/* 280:    */   {
/* 281:279 */     if ((sStr == null) || (sStr.length() == 0)) {
/* 282:280 */       return null;
/* 283:    */     }
/* 284:282 */     Node node = new Node();
/* 285:283 */     if (sStr.startsWith("("))
/* 286:    */     {
/* 287:284 */       int i1 = nextNode(sStr, 0);
/* 288:285 */       int i2 = nextNode(sStr, i1);
/* 289:286 */       node.m_children = new Node[2];
/* 290:287 */       node.m_children[0] = parseNewick2(sStr.substring(1, i1));
/* 291:288 */       node.m_children[0].m_Parent = node;
/* 292:289 */       String sStr2 = sStr.substring(i1 + 1, i2 > 0 ? i2 : sStr.length());
/* 293:290 */       node.m_children[1] = parseNewick2(sStr2);
/* 294:291 */       node.m_children[1].m_Parent = node;
/* 295:292 */       if (sStr.lastIndexOf('[') > sStr.lastIndexOf(')'))
/* 296:    */       {
/* 297:293 */         sStr = sStr.substring(sStr.lastIndexOf('['));
/* 298:294 */         i2 = sStr.indexOf(']');
/* 299:295 */         if (i2 < 0) {
/* 300:296 */           throw new Exception("unbalanced square bracket found:" + sStr);
/* 301:    */         }
/* 302:298 */         sStr2 = sStr.substring(1, i2);
/* 303:299 */         node.m_sMetaData = sStr2;
/* 304:    */       }
/* 305:301 */       if (sStr.lastIndexOf(':') > sStr.lastIndexOf(')'))
/* 306:    */       {
/* 307:302 */         sStr = sStr.substring(sStr.lastIndexOf(':'));
/* 308:303 */         sStr = sStr.replaceAll("[,\\):]", "");
/* 309:304 */         node.m_fLength = new Double(sStr).doubleValue();
/* 310:    */       }
/* 311:    */       else
/* 312:    */       {
/* 313:306 */         node.m_fLength = 1.0D;
/* 314:    */       }
/* 315:    */     }
/* 316:    */     else
/* 317:    */     {
/* 318:310 */       if (sStr.contains("["))
/* 319:    */       {
/* 320:312 */         int i1 = sStr.indexOf('[');
/* 321:313 */         int i2 = sStr.indexOf(']');
/* 322:314 */         if (i2 < 0) {
/* 323:315 */           throw new Exception("unbalanced square bracket found:" + sStr);
/* 324:    */         }
/* 325:317 */         String sStr2 = sStr.substring(i1 + 1, i2);
/* 326:318 */         sStr = sStr.substring(0, i1) + sStr.substring(i2 + 1);
/* 327:319 */         node.m_sMetaData = sStr2;
/* 328:    */       }
/* 329:321 */       if (sStr.indexOf(')') >= 0) {
/* 330:322 */         sStr = sStr.substring(0, sStr.indexOf(')'));
/* 331:    */       }
/* 332:324 */       sStr = sStr.replaceFirst("[,\\)]", "");
/* 333:326 */       if (sStr.length() > 0)
/* 334:    */       {
/* 335:327 */         if (sStr.indexOf(':') >= 0)
/* 336:    */         {
/* 337:328 */           int iColon = sStr.indexOf(':');
/* 338:329 */           node.m_sLabel = sStr.substring(0, iColon);
/* 339:330 */           if (sStr.indexOf(':', iColon + 1) >= 0)
/* 340:    */           {
/* 341:331 */             int iColon2 = sStr.indexOf(':', iColon + 1);
/* 342:332 */             node.m_fLength = new Double(sStr.substring(iColon + 1, iColon2)).doubleValue();
/* 343:333 */             this.m_fTmpLength = new Double(sStr.substring(iColon2 + 1)).doubleValue();
/* 344:    */           }
/* 345:    */           else
/* 346:    */           {
/* 347:335 */             node.m_fLength = new Double(sStr.substring(iColon + 1)).doubleValue();
/* 348:    */           }
/* 349:    */         }
/* 350:    */         else
/* 351:    */         {
/* 352:338 */           node.m_sLabel = sStr;
/* 353:339 */           node.m_fLength = 1.0D;
/* 354:    */         }
/* 355:    */       }
/* 356:    */       else {
/* 357:342 */         return null;
/* 358:    */       }
/* 359:    */     }
/* 360:345 */     return node;
/* 361:    */   }
/* 362:    */   
/* 363:    */   public void fitToScreen()
/* 364:    */   {
/* 365:354 */     this.m_fScaleX = 10.0D;
/* 366:355 */     int nW = getWidth();
/* 367:356 */     if (this.m_nLeafs > 0) {
/* 368:357 */       this.m_fScaleX = (nW / this.m_nLeafs);
/* 369:    */     }
/* 370:359 */     this.m_fScaleY = 10.0D;
/* 371:360 */     int nH = getHeight();
/* 372:361 */     if (this.m_fHeight > 0.0D) {
/* 373:362 */       this.m_fScaleY = ((nH - 10) / this.m_fHeight);
/* 374:    */     }
/* 375:364 */     repaint();
/* 376:    */   }
/* 377:    */   
/* 378:    */   public void paintComponent(Graphics g)
/* 379:    */   {
/* 380:374 */     Color oldBackground = ((Graphics2D)g).getBackground();
/* 381:    */     
/* 382:376 */     ((Graphics2D)g).setBackground(Color.WHITE);
/* 383:377 */     g.clearRect(0, 0, getSize().width, getSize().height);
/* 384:378 */     ((Graphics2D)g).setBackground(oldBackground);
/* 385:379 */     g.setClip(3, 7, getWidth() - 6, getHeight() - 10);
/* 386:380 */     this.m_tree.draw(g);
/* 387:381 */     g.setClip(0, 0, getWidth(), getHeight());
/* 388:    */   }
/* 389:    */   
/* 390:    */   public void componentHidden(ComponentEvent e) {}
/* 391:    */   
/* 392:    */   public void componentMoved(ComponentEvent e) {}
/* 393:    */   
/* 394:    */   public void componentResized(ComponentEvent e)
/* 395:    */   {
/* 396:390 */     fitToScreen();
/* 397:    */   }
/* 398:    */   
/* 399:    */   public void componentShown(ComponentEvent e) {}
/* 400:    */   
/* 401:    */   public static void main(String[] args)
/* 402:    */   {
/* 403:403 */     HierarchyVisualizer a = new HierarchyVisualizer("((1:0.4,2:0.6):-0.4,3:0.4)");
/* 404:404 */     a.setSize(800, 600);
/* 405:    */     
/* 406:406 */     JFrame f = new JFrame();
/* 407:407 */     Container contentPane = f.getContentPane();
/* 408:408 */     contentPane.add(a);
/* 409:409 */     f.setDefaultCloseOperation(2);
/* 410:410 */     f.setSize(800, 600);
/* 411:411 */     f.setVisible(true);
/* 412:412 */     a.fitToScreen();
/* 413:    */   }
/* 414:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.hierarchyvisualizer.HierarchyVisualizer
 * JD-Core Version:    0.7.0.1
 */