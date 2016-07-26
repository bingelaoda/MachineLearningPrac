/*   1:    */ package weka.gui.treevisualizer;
/*   2:    */ 
/*   3:    */ import java.util.Vector;
/*   4:    */ 
/*   5:    */ public class PlaceNode2
/*   6:    */   implements NodePlace
/*   7:    */ {
/*   8:    */   private double m_yRatio;
/*   9:    */   private Group[] m_groups;
/*  10:    */   private Level[] m_levels;
/*  11:    */   private int m_groupNum;
/*  12:    */   private int m_levelNum;
/*  13:    */   
/*  14:    */   public void place(Node r)
/*  15:    */   {
/*  16: 69 */     this.m_groupNum = Node.getGCount(r, 0);
/*  17:    */     
/*  18: 71 */     this.m_groups = new Group[this.m_groupNum];
/*  19: 73 */     for (int noa = 0; noa < this.m_groupNum; noa++)
/*  20:    */     {
/*  21: 74 */       this.m_groups[noa] = new Group(null);
/*  22: 75 */       this.m_groups[noa].m_gap = 3.0D;
/*  23: 76 */       this.m_groups[noa].m_start = -1;
/*  24:    */     }
/*  25: 79 */     groupBuild(r);
/*  26: 80 */     this.m_levelNum = Node.getHeight(r, 0);
/*  27: 81 */     this.m_yRatio = (1.0D / (this.m_levelNum + 1));
/*  28:    */     
/*  29: 83 */     this.m_levels = new Level[this.m_levelNum];
/*  30: 85 */     for (int noa = 0; noa < this.m_levelNum; noa++) {
/*  31: 86 */       this.m_levels[noa] = new Level(null);
/*  32:    */     }
/*  33: 88 */     r.setTop(this.m_yRatio);
/*  34: 89 */     yPlacer();
/*  35: 90 */     r.setCenter(0.0D);
/*  36: 91 */     xPlacer(0);
/*  37:    */     
/*  38:    */ 
/*  39:    */ 
/*  40:    */ 
/*  41:    */ 
/*  42:    */ 
/*  43:    */ 
/*  44: 99 */     untangle2();
/*  45:    */     
/*  46:101 */     scaleByMax();
/*  47:    */   }
/*  48:    */   
/*  49:    */   private void xPlacer(int start)
/*  50:    */   {
/*  51:155 */     if (this.m_groupNum > 0)
/*  52:    */     {
/*  53:156 */       this.m_groups[0].m_p.setCenter(0.0D);
/*  54:157 */       for (int noa = start; noa < this.m_groupNum; noa++)
/*  55:    */       {
/*  56:158 */         int alter = 0;
/*  57:159 */         double c = this.m_groups[noa].m_gap;
/*  58:160 */         Node r = this.m_groups[noa].m_p;
/*  59:    */         Edge e;
/*  60:161 */         for (int nob = 0; (e = r.getChild(nob)) != null; nob++) {
/*  61:162 */           if (e.getTarget().getParent(0) == e) {
/*  62:163 */             e.getTarget().setCenter(nob * c);
/*  63:    */           } else {
/*  64:165 */             alter++;
/*  65:    */           }
/*  66:    */         }
/*  67:168 */         this.m_groups[noa].m_size = ((nob - 1 - alter) * c);
/*  68:169 */         xShift(noa);
/*  69:    */       }
/*  70:    */     }
/*  71:    */   }
/*  72:    */   
/*  73:    */   private void xShift(int n)
/*  74:    */   {
/*  75:181 */     Node r = this.m_groups[n].m_p;
/*  76:182 */     double h = this.m_groups[n].m_size / 2.0D;
/*  77:183 */     double c = this.m_groups[n].m_p.getCenter();
/*  78:184 */     double m = c - h;
/*  79:185 */     this.m_groups[n].m_left = m;
/*  80:186 */     this.m_groups[n].m_right = (c + h);
/*  81:    */     Edge e;
/*  82:188 */     for (int noa = 0; (e = r.getChild(noa)) != null; noa++) {
/*  83:189 */       if (e.getTarget().getParent(0) == e) {
/*  84:190 */         e.getTarget().adjustCenter(m);
/*  85:    */       }
/*  86:    */     }
/*  87:    */   }
/*  88:    */   
/*  89:    */   private void scaleByMax()
/*  90:    */   {
/*  91:202 */     double l_x = 5000.0D;double h_x = -5000.0D;
/*  92:203 */     for (int noa = 0; noa < this.m_groupNum; noa++)
/*  93:    */     {
/*  94:204 */       if (l_x > this.m_groups[noa].m_left) {
/*  95:205 */         l_x = this.m_groups[noa].m_left;
/*  96:    */       }
/*  97:208 */       if (h_x < this.m_groups[noa].m_right) {
/*  98:209 */         h_x = this.m_groups[noa].m_right;
/*  99:    */       }
/* 100:    */     }
/* 101:215 */     double m_scale = h_x - l_x + 1.0D;
/* 102:216 */     if (this.m_groupNum > 0)
/* 103:    */     {
/* 104:217 */       Node r = this.m_groups[0].m_p;
/* 105:218 */       r.setCenter((r.getCenter() - l_x) / m_scale);
/* 106:220 */       for (int noa = 0; noa < this.m_groupNum; noa++)
/* 107:    */       {
/* 108:221 */         r = this.m_groups[noa].m_p;
/* 109:    */         Edge e;
/* 110:222 */         for (int nob = 0; (e = r.getChild(nob)) != null; nob++)
/* 111:    */         {
/* 112:223 */           Node s = e.getTarget();
/* 113:224 */           if (s.getParent(0) == e) {
/* 114:225 */             s.setCenter((s.getCenter() - l_x) / m_scale);
/* 115:    */           }
/* 116:    */         }
/* 117:    */       }
/* 118:    */     }
/* 119:    */   }
/* 120:    */   
/* 121:    */   private void untangle2()
/* 122:    */   {
/* 123:239 */     Node nf = null;Node ns = null;
/* 124:240 */     int l = 0;
/* 125:241 */     int tf = 0;int ts = 0;
/* 126:    */     Ease a;
/* 127:242 */     while ((a = overlap(l)) != null)
/* 128:    */     {
/* 129:245 */       int f = a.m_place;
/* 130:246 */       int s = a.m_place + 1;
/* 131:247 */       while (f != s)
/* 132:    */       {
/* 133:248 */         a.m_lev -= 1;
/* 134:249 */         tf = f;
/* 135:250 */         ts = s;
/* 136:251 */         f = this.m_groups[f].m_pg;
/* 137:252 */         s = this.m_groups[s].m_pg;
/* 138:    */       }
/* 139:254 */       l = a.m_lev;
/* 140:255 */       int pf = 0;
/* 141:256 */       int ps = 0;
/* 142:257 */       Node r = this.m_groups[f].m_p;
/* 143:258 */       Node mark = this.m_groups[tf].m_p;
/* 144:259 */       nf = null;
/* 145:260 */       ns = null;
/* 146:261 */       for (int noa = 0; nf != mark; noa++)
/* 147:    */       {
/* 148:262 */         pf++;
/* 149:263 */         nf = r.getChild(noa).getTarget();
/* 150:    */       }
/* 151:265 */       mark = this.m_groups[ts].m_p;
/* 152:266 */       for (int noa = pf; ns != mark; noa++)
/* 153:    */       {
/* 154:267 */         ps++;
/* 155:268 */         ns = r.getChild(noa).getTarget();
/* 156:    */       }
/* 157:274 */       Vector<Double> o_pos = new Vector(20, 10);
/* 158:    */       Edge e;
/* 159:275 */       for (int noa = 0; (e = r.getChild(noa)) != null; noa++) {
/* 160:276 */         if (e.getTarget().getParent(0) == e)
/* 161:    */         {
/* 162:277 */           Double tem = new Double(e.getTarget().getCenter());
/* 163:278 */           o_pos.addElement(tem);
/* 164:    */         }
/* 165:    */       }
/* 166:282 */       pf--;
/* 167:283 */       double inc = a.m_amount / ps;
/* 168:284 */       for (int noa = 0; (e = r.getChild(noa)) != null; noa++)
/* 169:    */       {
/* 170:285 */         ns = e.getTarget();
/* 171:286 */         if (ns.getParent(0) == e) {
/* 172:287 */           if (noa > pf + ps) {
/* 173:288 */             ns.adjustCenter(a.m_amount);
/* 174:289 */           } else if (noa > pf) {
/* 175:290 */             ns.adjustCenter(inc * (noa - pf));
/* 176:    */           }
/* 177:    */         }
/* 178:    */       }
/* 179:295 */       nf = r.getChild(0).getTarget();
/* 180:296 */       inc = ns.getCenter() - nf.getCenter();
/* 181:297 */       this.m_groups[f].m_size = inc;
/* 182:298 */       this.m_groups[f].m_left = (r.getCenter() - inc / 2.0D);
/* 183:299 */       this.m_groups[f].m_right = (this.m_groups[f].m_left + inc);
/* 184:300 */       inc = this.m_groups[f].m_left - nf.getCenter();
/* 185:    */       
/* 186:    */ 
/* 187:303 */       int g_num = 0;
/* 188:304 */       for (int noa = 0; (e = r.getChild(noa)) != null; noa++)
/* 189:    */       {
/* 190:305 */         ns = e.getTarget();
/* 191:306 */         if (ns.getParent(0) == e)
/* 192:    */         {
/* 193:307 */           ns.adjustCenter(inc);
/* 194:308 */           double shift = ns.getCenter() - ((Double)o_pos.elementAt(noa)).doubleValue();
/* 195:309 */           if (ns.getChild(0) != null)
/* 196:    */           {
/* 197:310 */             moveSubtree(this.m_groups[f].m_start + g_num, shift);
/* 198:311 */             g_num++;
/* 199:    */           }
/* 200:    */         }
/* 201:    */       }
/* 202:    */     }
/* 203:    */   }
/* 204:    */   
/* 205:    */   private void moveSubtree(int n, double o)
/* 206:    */   {
/* 207:331 */     Node r = this.m_groups[n].m_p;
/* 208:    */     Edge e;
/* 209:332 */     for (int noa = 0; (e = r.getChild(noa)) != null; noa++) {
/* 210:333 */       if (e.getTarget().getParent(0) == e) {
/* 211:334 */         e.getTarget().adjustCenter(o);
/* 212:    */       }
/* 213:    */     }
/* 214:337 */     this.m_groups[n].m_left += o;
/* 215:338 */     this.m_groups[n].m_right += o;
/* 216:339 */     if (this.m_groups[n].m_start != -1) {
/* 217:340 */       for (int noa = this.m_groups[n].m_start; noa <= this.m_groups[n].m_end; noa++) {
/* 218:341 */         moveSubtree(noa, o);
/* 219:    */       }
/* 220:    */     }
/* 221:    */   }
/* 222:    */   
/* 223:    */   private Ease overlap(int l)
/* 224:    */   {
/* 225:355 */     Ease a = new Ease(null);
/* 226:356 */     for (int noa = l; noa < this.m_levelNum; noa++) {
/* 227:357 */       for (int nob = this.m_levels[noa].m_start; nob < this.m_levels[noa].m_end; nob++)
/* 228:    */       {
/* 229:358 */         a.m_amount = (this.m_groups[nob].m_right - this.m_groups[(nob + 1)].m_left + 2.0D);
/* 230:361 */         if (a.m_amount >= 0.0D)
/* 231:    */         {
/* 232:362 */           a.m_amount += 1.0D;
/* 233:363 */           a.m_lev = noa;
/* 234:364 */           a.m_place = nob;
/* 235:365 */           return a;
/* 236:    */         }
/* 237:    */       }
/* 238:    */     }
/* 239:369 */     return null;
/* 240:    */   }
/* 241:    */   
/* 242:    */   private void yPlacer()
/* 243:    */   {
/* 244:387 */     double changer = this.m_yRatio;
/* 245:388 */     int lev_place = 0;
/* 246:389 */     if (this.m_groupNum > 0)
/* 247:    */     {
/* 248:390 */       this.m_groups[0].m_p.setTop(this.m_yRatio);
/* 249:391 */       this.m_levels[0].m_start = 0;
/* 250:393 */       for (int noa = 0; noa < this.m_groupNum; noa++)
/* 251:    */       {
/* 252:394 */         if (this.m_groups[noa].m_p.getTop() != changer)
/* 253:    */         {
/* 254:395 */           this.m_levels[lev_place].m_end = (noa - 1);
/* 255:396 */           lev_place++;
/* 256:397 */           this.m_levels[lev_place].m_start = noa;
/* 257:398 */           changer = this.m_groups[noa].m_p.getTop();
/* 258:    */         }
/* 259:400 */         nodeY(this.m_groups[noa].m_p);
/* 260:    */       }
/* 261:402 */       this.m_levels[lev_place].m_end = (this.m_groupNum - 1);
/* 262:    */     }
/* 263:    */   }
/* 264:    */   
/* 265:    */   private void nodeY(Node r)
/* 266:    */   {
/* 267:414 */     double h = r.getTop() + this.m_yRatio;
/* 268:    */     Edge e;
/* 269:415 */     for (int noa = 0; (e = r.getChild(noa)) != null; noa++) {
/* 270:416 */       if (e.getTarget().getParent(0) == e)
/* 271:    */       {
/* 272:417 */         e.getTarget().setTop(h);
/* 273:418 */         if (e.getTarget().getVisible()) {}
/* 274:    */       }
/* 275:    */     }
/* 276:    */   }
/* 277:    */   
/* 278:    */   private void groupBuild(Node r)
/* 279:    */   {
/* 280:433 */     if (this.m_groupNum > 0)
/* 281:    */     {
/* 282:434 */       this.m_groupNum = 0;
/* 283:435 */       this.m_groups[0].m_p = r;
/* 284:436 */       this.m_groupNum += 1;
/* 285:439 */       for (int noa = 0; noa < this.m_groupNum; noa++) {
/* 286:440 */         groupFind(this.m_groups[noa].m_p, noa);
/* 287:    */       }
/* 288:    */     }
/* 289:    */   }
/* 290:    */   
/* 291:    */   private void groupFind(Node r, int pg)
/* 292:    */   {
/* 293:453 */     boolean first = true;
/* 294:    */     Edge e;
/* 295:454 */     for (int noa = 0; (e = r.getChild(noa)) != null; noa++) {
/* 296:455 */       if ((e.getTarget().getParent(0) == e) && 
/* 297:456 */         (e.getTarget().getChild(0) != null) && (e.getTarget().getCVisible()))
/* 298:    */       {
/* 299:457 */         if (first)
/* 300:    */         {
/* 301:458 */           this.m_groups[pg].m_start = this.m_groupNum;
/* 302:459 */           first = false;
/* 303:    */         }
/* 304:461 */         this.m_groups[pg].m_end = this.m_groupNum;
/* 305:462 */         this.m_groups[this.m_groupNum].m_p = e.getTarget();
/* 306:463 */         this.m_groups[this.m_groupNum].m_pg = pg;
/* 307:    */         
/* 308:    */ 
/* 309:466 */         this.m_groupNum += 1;
/* 310:    */       }
/* 311:    */     }
/* 312:    */   }
/* 313:    */   
/* 314:    */   private class Ease
/* 315:    */   {
/* 316:    */     public int m_place;
/* 317:    */     public double m_amount;
/* 318:    */     public int m_lev;
/* 319:    */     
/* 320:    */     private Ease() {}
/* 321:    */   }
/* 322:    */   
/* 323:    */   private class Group
/* 324:    */   {
/* 325:    */     public Node m_p;
/* 326:    */     public int m_pg;
/* 327:    */     public double m_gap;
/* 328:    */     public double m_left;
/* 329:    */     public double m_right;
/* 330:    */     public double m_size;
/* 331:    */     public int m_start;
/* 332:    */     public int m_end;
/* 333:    */     
/* 334:    */     private Group() {}
/* 335:    */   }
/* 336:    */   
/* 337:    */   private class Level
/* 338:    */   {
/* 339:    */     public int m_start;
/* 340:    */     public int m_end;
/* 341:    */     
/* 342:    */     private Level() {}
/* 343:    */   }
/* 344:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.treevisualizer.PlaceNode2
 * JD-Core Version:    0.7.0.1
 */