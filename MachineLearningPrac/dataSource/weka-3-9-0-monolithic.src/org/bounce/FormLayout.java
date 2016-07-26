/*   1:    */ package org.bounce;
/*   2:    */ 
/*   3:    */ import java.awt.Component;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.Insets;
/*   7:    */ import java.awt.LayoutManager2;
/*   8:    */ import java.awt.Rectangle;
/*   9:    */ import java.util.HashMap;
/*  10:    */ 
/*  11:    */ public class FormLayout
/*  12:    */   implements LayoutManager2
/*  13:    */ {
/*  14:    */   private static final int MINIMUM = 0;
/*  15:    */   private static final int PREFERRED = 1;
/*  16:    */   private static final int MAXIMUM = 2;
/*  17: 72 */   public static final FormConstraints LEFT = new FormConstraints(0);
/*  18: 74 */   public static final FormConstraints RIGHT = new FormConstraints(2);
/*  19: 79 */   public static final FormConstraints RIGHT_FILL = new FormConstraints(2, true);
/*  20: 81 */   public static final FormConstraints FULL = new FormConstraints(5);
/*  21: 86 */   public static final FormConstraints FULL_FILL = new FormConstraints(5, true);
/*  22:    */   private int horizontalGap;
/*  23:    */   private int verticalGap;
/*  24:    */   private HashMap<Component, FormConstraints> constraints;
/*  25:    */   
/*  26:    */   public FormLayout()
/*  27:    */   {
/*  28: 97 */     this(0, 0);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public FormLayout(int paramInt1, int paramInt2)
/*  32:    */   {
/*  33:108 */     this.horizontalGap = paramInt1;
/*  34:109 */     this.verticalGap = paramInt2;
/*  35:110 */     this.constraints = new HashMap();
/*  36:    */   }
/*  37:    */   
/*  38:    */   public int getHgap()
/*  39:    */   {
/*  40:120 */     return this.horizontalGap;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void setHgap(int paramInt)
/*  44:    */   {
/*  45:130 */     this.horizontalGap = paramInt;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public int getVgap()
/*  49:    */   {
/*  50:139 */     return this.verticalGap;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void setVgap(int paramInt)
/*  54:    */   {
/*  55:148 */     this.verticalGap = paramInt;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setConstraints(Component paramComponent, Object paramObject)
/*  59:    */   {
/*  60:160 */     if ((paramObject != null) && ((paramObject instanceof FormConstraints))) {
/*  61:161 */       this.constraints.put(paramComponent, new FormConstraints((FormConstraints)paramObject));
/*  62:    */     } else {
/*  63:163 */       throw new IllegalArgumentException("cannot add to layout: constraint must be of type FormConstraints");
/*  64:    */     }
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void removeLayoutComponent(Component paramComponent)
/*  68:    */   {
/*  69:173 */     this.constraints.remove(paramComponent);
/*  70:    */   }
/*  71:    */   
/*  72:    */   public Dimension preferredLayoutSize(Container paramContainer)
/*  73:    */   {
/*  74:185 */     synchronized (paramContainer.getTreeLock())
/*  75:    */     {
/*  76:186 */       return getSize(1, paramContainer);
/*  77:    */     }
/*  78:    */   }
/*  79:    */   
/*  80:    */   public Dimension minimumLayoutSize(Container paramContainer)
/*  81:    */   {
/*  82:199 */     synchronized (paramContainer.getTreeLock())
/*  83:    */     {
/*  84:200 */       return getSize(0, paramContainer);
/*  85:    */     }
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void layoutContainer(Container paramContainer)
/*  89:    */   {
/*  90:210 */     synchronized (paramContainer.getTreeLock())
/*  91:    */     {
/*  92:211 */       Insets localInsets = paramContainer.getInsets();
/*  93:212 */       int i = paramContainer.getComponentCount();
/*  94:    */       
/*  95:    */ 
/*  96:215 */       Dimension localDimension = paramContainer.getSize();
/*  97:216 */       int j = getWidth(0, 1, paramContainer);
/*  98:217 */       int k = localDimension.width - (localInsets.left + localInsets.right);
/*  99:218 */       int m = localInsets.top;
/* 100:219 */       int n = 0;
/* 101:221 */       while (n < i)
/* 102:    */       {
/* 103:222 */         Component localComponent1 = paramContainer.getComponent(n);
/* 104:224 */         if (localComponent1.isVisible())
/* 105:    */         {
/* 106:225 */           FormConstraints localFormConstraints1 = (FormConstraints)this.constraints.get(localComponent1);
/* 107:226 */           int i1 = localComponent1.getPreferredSize().height;
/* 108:228 */           if (localFormConstraints1.getPosition() == 0)
/* 109:    */           {
/* 110:230 */             n++;
/* 111:    */             
/* 112:232 */             Component localComponent2 = paramContainer.getComponent(n);
/* 113:233 */             FormConstraints localFormConstraints2 = (FormConstraints)this.constraints.get(localComponent2);
/* 114:235 */             if (localComponent1.isVisible())
/* 115:    */             {
/* 116:236 */               if (localComponent2.getPreferredSize().height > localComponent1.getPreferredSize().height) {
/* 117:237 */                 i1 = localComponent2.getPreferredSize().height;
/* 118:    */               } else {
/* 119:239 */                 i1 = localComponent1.getPreferredSize().height;
/* 120:    */               }
/* 121:242 */               if (localFormConstraints2.getPosition() == 2) {
/* 122:244 */                 align(localFormConstraints2, new Rectangle(localInsets.left + j + this.horizontalGap, m, k - j - this.horizontalGap, i1), localComponent2);
/* 123:    */               } else {
/* 124:251 */                 n--;
/* 125:    */               }
/* 126:    */             }
/* 127:    */             else
/* 128:    */             {
/* 129:255 */               n--;
/* 130:    */             }
/* 131:259 */             align(localFormConstraints1, new Rectangle(localInsets.left, m, j, i1), localComponent1);
/* 132:    */           }
/* 133:267 */           else if (localFormConstraints1.getPosition() == 2)
/* 134:    */           {
/* 135:268 */             align(localFormConstraints1, new Rectangle(localInsets.left + j + this.horizontalGap, m, k - j - this.horizontalGap, i1), localComponent1);
/* 136:    */           }
/* 137:275 */           else if (localFormConstraints1.getPosition() == 5)
/* 138:    */           {
/* 139:276 */             align(localFormConstraints1, new Rectangle(localInsets.left, m, k, i1), localComponent1);
/* 140:    */           }
/* 141:284 */           m += this.verticalGap + i1;
/* 142:    */         }
/* 143:287 */         n++;
/* 144:    */       }
/* 145:    */     }
/* 146:    */   }
/* 147:    */   
/* 148:    */   public void addLayoutComponent(Component paramComponent, Object paramObject)
/* 149:    */   {
/* 150:300 */     setConstraints(paramComponent, paramObject);
/* 151:    */   }
/* 152:    */   
/* 153:    */   public Dimension maximumLayoutSize(Container paramContainer)
/* 154:    */   {
/* 155:314 */     return new Dimension(2147483647, 2147483647);
/* 156:    */   }
/* 157:    */   
/* 158:    */   public float getLayoutAlignmentX(Container paramContainer)
/* 159:    */   {
/* 160:329 */     return 0.5F;
/* 161:    */   }
/* 162:    */   
/* 163:    */   public float getLayoutAlignmentY(Container paramContainer)
/* 164:    */   {
/* 165:344 */     return 0.5F;
/* 166:    */   }
/* 167:    */   
/* 168:    */   public void invalidateLayout(Container paramContainer) {}
/* 169:    */   
/* 170:    */   public void addLayoutComponent(String paramString, Component paramComponent) {}
/* 171:    */   
/* 172:    */   private int getHeight(int paramInt, Container paramContainer)
/* 173:    */   {
/* 174:369 */     int i = 1;
/* 175:370 */     int j = 0;
/* 176:371 */     int k = 0;
/* 177:372 */     int m = 0;
/* 178:373 */     int n = 0;
/* 179:375 */     for (int i1 = 0; i1 < paramContainer.getComponentCount(); i1++)
/* 180:    */     {
/* 181:376 */       Component localComponent = paramContainer.getComponent(i1);
/* 182:378 */       if (localComponent.isVisible())
/* 183:    */       {
/* 184:379 */         m = getSize(paramInt, localComponent).height;
/* 185:380 */         FormConstraints localFormConstraints = (FormConstraints)this.constraints.get(localComponent);
/* 186:383 */         if (localFormConstraints.getPosition() == 0)
/* 187:    */         {
/* 188:385 */           if (i == 0)
/* 189:    */           {
/* 190:386 */             j += k;
/* 191:387 */             n++;
/* 192:    */           }
/* 193:390 */           k = m;
/* 194:391 */           i = 0;
/* 195:    */         }
/* 196:394 */         else if (localFormConstraints.getPosition() == 2)
/* 197:    */         {
/* 198:395 */           if (i == 0)
/* 199:    */           {
/* 200:396 */             if (m < k) {
/* 201:397 */               m = k;
/* 202:    */             }
/* 203:400 */             i = 1;
/* 204:    */           }
/* 205:    */         }
/* 206:404 */         else if ((localFormConstraints.getPosition() == 5) && 
/* 207:405 */           (i == 0))
/* 208:    */         {
/* 209:406 */           m += k;
/* 210:407 */           i = 1;
/* 211:    */         }
/* 212:412 */         if (i != 0)
/* 213:    */         {
/* 214:413 */           j += m;
/* 215:414 */           n++;
/* 216:    */         }
/* 217:    */       }
/* 218:    */     }
/* 219:420 */     if (i == 0)
/* 220:    */     {
/* 221:421 */       j += m;
/* 222:422 */       n++;
/* 223:    */     }
/* 224:425 */     return j + this.verticalGap * (n - 1);
/* 225:    */   }
/* 226:    */   
/* 227:    */   private Dimension getSize(int paramInt, Component paramComponent)
/* 228:    */   {
/* 229:432 */     Dimension localDimension = null;
/* 230:434 */     switch (paramInt)
/* 231:    */     {
/* 232:    */     case 0: 
/* 233:436 */       localDimension = paramComponent.getMinimumSize();
/* 234:437 */       break;
/* 235:    */     case 1: 
/* 236:440 */       localDimension = paramComponent.getPreferredSize();
/* 237:441 */       break;
/* 238:    */     case 2: 
/* 239:444 */       localDimension = paramComponent.getMaximumSize();
/* 240:    */     }
/* 241:448 */     return localDimension;
/* 242:    */   }
/* 243:    */   
/* 244:    */   private Dimension getSize(int paramInt, Container paramContainer)
/* 245:    */   {
/* 246:455 */     Dimension localDimension = new Dimension(0, 0);
/* 247:    */     
/* 248:457 */     int i = getHeight(paramInt, paramContainer);
/* 249:    */     
/* 250:459 */     Insets localInsets = paramContainer.getInsets();
/* 251:    */     
/* 252:461 */     int j = getWidth(0, paramInt, paramContainer);
/* 253:462 */     int k = getWidth(2, paramInt, paramContainer);
/* 254:463 */     int m = getWidth(5, paramInt, paramContainer);
/* 255:467 */     if (m < j + k + this.horizontalGap) {
/* 256:468 */       localDimension.width = (j + k + this.horizontalGap + localInsets.left + localInsets.right);
/* 257:    */     } else {
/* 258:470 */       localDimension.width = (m + localInsets.left + localInsets.right);
/* 259:    */     }
/* 260:472 */     localDimension.height = (i + localInsets.top + localInsets.bottom);
/* 261:    */     
/* 262:474 */     return localDimension;
/* 263:    */   }
/* 264:    */   
/* 265:    */   private int getWidth(int paramInt1, int paramInt2, Container paramContainer)
/* 266:    */   {
/* 267:481 */     int i = 0;
/* 268:483 */     for (int j = 0; j < paramContainer.getComponentCount(); j++)
/* 269:    */     {
/* 270:484 */       Component localComponent = paramContainer.getComponent(j);
/* 271:486 */       if (localComponent.isVisible())
/* 272:    */       {
/* 273:487 */         FormConstraints localFormConstraints = (FormConstraints)this.constraints.get(localComponent);
/* 274:489 */         if ((localFormConstraints.getPosition() == paramInt1) && (!localFormConstraints.isFilled()))
/* 275:    */         {
/* 276:490 */           Dimension localDimension = getSize(paramInt2, localComponent);
/* 277:492 */           if (localDimension.width > i) {
/* 278:493 */             i = localDimension.width;
/* 279:    */           }
/* 280:    */         }
/* 281:    */       }
/* 282:    */     }
/* 283:499 */     return i;
/* 284:    */   }
/* 285:    */   
/* 286:    */   private void align(FormConstraints paramFormConstraints, Rectangle paramRectangle, Component paramComponent)
/* 287:    */   {
/* 288:506 */     if (!paramFormConstraints.isFilled())
/* 289:    */     {
/* 290:    */       int i;
/* 291:507 */       if (paramFormConstraints.getHorizontalAlignment() == 4)
/* 292:    */       {
/* 293:508 */         i = paramRectangle.x + (paramRectangle.width - paramComponent.getPreferredSize().width) / 2;
/* 294:510 */         if (i > paramRectangle.x) {
/* 295:511 */           paramRectangle.x = i;
/* 296:    */         }
/* 297:    */       }
/* 298:513 */       else if (paramFormConstraints.getHorizontalAlignment() == 2)
/* 299:    */       {
/* 300:514 */         i = paramRectangle.width + paramRectangle.x - paramComponent.getPreferredSize().width;
/* 301:516 */         if (i > paramRectangle.x) {
/* 302:517 */           paramRectangle.x = i;
/* 303:    */         }
/* 304:    */       }
/* 305:521 */       if (paramComponent.getPreferredSize().height != paramRectangle.height)
/* 306:    */       {
/* 307:522 */         if (paramFormConstraints.getVerticalAlignment() == 4)
/* 308:    */         {
/* 309:523 */           i = paramRectangle.y + (paramRectangle.height - paramComponent.getPreferredSize().height) / 2;
/* 310:525 */           if (i > paramRectangle.y) {
/* 311:526 */             paramRectangle.y = i;
/* 312:    */           }
/* 313:    */         }
/* 314:529 */         else if (paramFormConstraints.getVerticalAlignment() == 1)
/* 315:    */         {
/* 316:530 */           i = paramRectangle.height + paramRectangle.y - paramComponent.getPreferredSize().height;
/* 317:532 */           if (i > paramRectangle.y) {
/* 318:533 */             paramRectangle.y = i;
/* 319:    */           }
/* 320:    */         }
/* 321:537 */         paramRectangle.height = paramComponent.getPreferredSize().height;
/* 322:    */       }
/* 323:540 */       paramRectangle.width = paramComponent.getPreferredSize().width;
/* 324:    */     }
/* 325:543 */     paramComponent.setBounds(paramRectangle);
/* 326:    */   }
/* 327:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.bounce.FormLayout
 * JD-Core Version:    0.7.0.1
 */