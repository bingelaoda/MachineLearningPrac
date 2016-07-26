/*   1:    */ package org.boon.collections;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collection;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Iterator;
/*   7:    */ import java.util.List;
/*   8:    */ import java.util.ListIterator;
/*   9:    */ import java.util.concurrent.locks.Lock;
/*  10:    */ import java.util.concurrent.locks.ReadWriteLock;
/*  11:    */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*  12:    */ 
/*  13:    */ public class SortableConcurrentList<T extends Comparable>
/*  14:    */   implements List<T>
/*  15:    */ {
/*  16: 42 */   private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
/*  17:    */   private final List<T> list;
/*  18:    */   
/*  19:    */   public SortableConcurrentList(List<T> list)
/*  20:    */   {
/*  21: 46 */     this.list = list;
/*  22:    */   }
/*  23:    */   
/*  24:    */   public SortableConcurrentList()
/*  25:    */   {
/*  26: 50 */     this.list = new ArrayList();
/*  27:    */   }
/*  28:    */   
/*  29:    */   public boolean remove(Object o)
/*  30:    */   {
/*  31: 54 */     this.readWriteLock.writeLock().lock();
/*  32:    */     boolean ret;
/*  33:    */     try
/*  34:    */     {
/*  35: 57 */       ret = this.list.remove(o);
/*  36:    */     }
/*  37:    */     finally
/*  38:    */     {
/*  39: 59 */       this.readWriteLock.writeLock().unlock();
/*  40:    */     }
/*  41: 61 */     return ret;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public boolean containsAll(Collection<?> c)
/*  45:    */   {
/*  46: 66 */     this.readWriteLock.readLock().lock();
/*  47:    */     try
/*  48:    */     {
/*  49: 68 */       return this.list.containsAll(c);
/*  50:    */     }
/*  51:    */     finally
/*  52:    */     {
/*  53: 70 */       this.readWriteLock.readLock().unlock();
/*  54:    */     }
/*  55:    */   }
/*  56:    */   
/*  57:    */   public boolean addAll(Collection<? extends T> c)
/*  58:    */   {
/*  59: 76 */     this.readWriteLock.writeLock().lock();
/*  60:    */     try
/*  61:    */     {
/*  62: 78 */       return this.list.addAll(c);
/*  63:    */     }
/*  64:    */     finally
/*  65:    */     {
/*  66: 80 */       this.readWriteLock.writeLock().unlock();
/*  67:    */     }
/*  68:    */   }
/*  69:    */   
/*  70:    */   public boolean addAll(int index, Collection<? extends T> c)
/*  71:    */   {
/*  72: 86 */     this.readWriteLock.writeLock().lock();
/*  73:    */     try
/*  74:    */     {
/*  75: 88 */       return this.list.addAll(index, c);
/*  76:    */     }
/*  77:    */     finally
/*  78:    */     {
/*  79: 90 */       this.readWriteLock.writeLock().unlock();
/*  80:    */     }
/*  81:    */   }
/*  82:    */   
/*  83:    */   public boolean removeAll(Collection<?> c)
/*  84:    */   {
/*  85: 96 */     this.readWriteLock.writeLock().lock();
/*  86:    */     try
/*  87:    */     {
/*  88: 98 */       return this.list.removeAll(c);
/*  89:    */     }
/*  90:    */     finally
/*  91:    */     {
/*  92:100 */       this.readWriteLock.writeLock().unlock();
/*  93:    */     }
/*  94:    */   }
/*  95:    */   
/*  96:    */   public boolean retainAll(Collection<?> c)
/*  97:    */   {
/*  98:106 */     this.readWriteLock.writeLock().lock();
/*  99:    */     try
/* 100:    */     {
/* 101:108 */       return this.list.retainAll(c);
/* 102:    */     }
/* 103:    */     finally
/* 104:    */     {
/* 105:110 */       this.readWriteLock.writeLock().unlock();
/* 106:    */     }
/* 107:    */   }
/* 108:    */   
/* 109:    */   public boolean add(T t)
/* 110:    */   {
/* 111:115 */     this.readWriteLock.writeLock().lock();
/* 112:    */     boolean ret;
/* 113:    */     try
/* 114:    */     {
/* 115:118 */       ret = this.list.add(t);
/* 116:    */     }
/* 117:    */     finally
/* 118:    */     {
/* 119:120 */       this.readWriteLock.writeLock().unlock();
/* 120:    */     }
/* 121:122 */     return ret;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void clear()
/* 125:    */   {
/* 126:126 */     this.readWriteLock.writeLock().lock();
/* 127:    */     try
/* 128:    */     {
/* 129:128 */       this.list.clear();
/* 130:    */     }
/* 131:    */     finally
/* 132:    */     {
/* 133:130 */       this.readWriteLock.writeLock().unlock();
/* 134:    */     }
/* 135:    */   }
/* 136:    */   
/* 137:    */   public int size()
/* 138:    */   {
/* 139:136 */     this.readWriteLock.readLock().lock();
/* 140:    */     try
/* 141:    */     {
/* 142:138 */       return this.list.size();
/* 143:    */     }
/* 144:    */     finally
/* 145:    */     {
/* 146:140 */       this.readWriteLock.readLock().unlock();
/* 147:    */     }
/* 148:    */   }
/* 149:    */   
/* 150:    */   public boolean isEmpty()
/* 151:    */   {
/* 152:146 */     this.readWriteLock.readLock().lock();
/* 153:    */     try
/* 154:    */     {
/* 155:148 */       return this.list.isEmpty();
/* 156:    */     }
/* 157:    */     finally
/* 158:    */     {
/* 159:150 */       this.readWriteLock.readLock().unlock();
/* 160:    */     }
/* 161:    */   }
/* 162:    */   
/* 163:    */   public boolean contains(Object o)
/* 164:    */   {
/* 165:155 */     this.readWriteLock.readLock().lock();
/* 166:    */     try
/* 167:    */     {
/* 168:157 */       return this.list.contains(o);
/* 169:    */     }
/* 170:    */     finally
/* 171:    */     {
/* 172:159 */       this.readWriteLock.readLock().unlock();
/* 173:    */     }
/* 174:    */   }
/* 175:    */   
/* 176:    */   public Iterator<T> iterator()
/* 177:    */   {
/* 178:165 */     this.readWriteLock.readLock().lock();
/* 179:    */     try
/* 180:    */     {
/* 181:167 */       return new ArrayList(this.list).iterator();
/* 182:    */     }
/* 183:    */     finally
/* 184:    */     {
/* 185:169 */       this.readWriteLock.readLock().unlock();
/* 186:    */     }
/* 187:    */   }
/* 188:    */   
/* 189:    */   public Object[] toArray()
/* 190:    */   {
/* 191:176 */     this.readWriteLock.readLock().lock();
/* 192:    */     try
/* 193:    */     {
/* 194:178 */       return this.list.toArray();
/* 195:    */     }
/* 196:    */     finally
/* 197:    */     {
/* 198:180 */       this.readWriteLock.readLock().unlock();
/* 199:    */     }
/* 200:    */   }
/* 201:    */   
/* 202:    */   public <T> T[] toArray(T[] a)
/* 203:    */   {
/* 204:187 */     this.readWriteLock.readLock().lock();
/* 205:    */     try
/* 206:    */     {
/* 207:189 */       return this.list.toArray(a);
/* 208:    */     }
/* 209:    */     finally
/* 210:    */     {
/* 211:191 */       this.readWriteLock.readLock().unlock();
/* 212:    */     }
/* 213:    */   }
/* 214:    */   
/* 215:    */   public T get(int index)
/* 216:    */   {
/* 217:196 */     this.readWriteLock.readLock().lock();
/* 218:    */     try
/* 219:    */     {
/* 220:198 */       return (Comparable)this.list.get(index);
/* 221:    */     }
/* 222:    */     finally
/* 223:    */     {
/* 224:200 */       this.readWriteLock.readLock().unlock();
/* 225:    */     }
/* 226:    */   }
/* 227:    */   
/* 228:    */   public T set(int index, T element)
/* 229:    */   {
/* 230:206 */     this.readWriteLock.writeLock().lock();
/* 231:    */     try
/* 232:    */     {
/* 233:208 */       return (Comparable)this.list.set(index, element);
/* 234:    */     }
/* 235:    */     finally
/* 236:    */     {
/* 237:210 */       this.readWriteLock.writeLock().unlock();
/* 238:    */     }
/* 239:    */   }
/* 240:    */   
/* 241:    */   public void add(int index, T element)
/* 242:    */   {
/* 243:216 */     this.readWriteLock.writeLock().lock();
/* 244:    */     try
/* 245:    */     {
/* 246:218 */       this.list.add(index, element);
/* 247:    */     }
/* 248:    */     finally
/* 249:    */     {
/* 250:220 */       this.readWriteLock.writeLock().unlock();
/* 251:    */     }
/* 252:    */   }
/* 253:    */   
/* 254:    */   public T remove(int index)
/* 255:    */   {
/* 256:227 */     this.readWriteLock.writeLock().lock();
/* 257:    */     try
/* 258:    */     {
/* 259:229 */       return (Comparable)this.list.remove(index);
/* 260:    */     }
/* 261:    */     finally
/* 262:    */     {
/* 263:231 */       this.readWriteLock.writeLock().unlock();
/* 264:    */     }
/* 265:    */   }
/* 266:    */   
/* 267:    */   public int indexOf(Object o)
/* 268:    */   {
/* 269:237 */     this.readWriteLock.readLock().lock();
/* 270:    */     try
/* 271:    */     {
/* 272:239 */       return this.list.indexOf(o);
/* 273:    */     }
/* 274:    */     finally
/* 275:    */     {
/* 276:241 */       this.readWriteLock.readLock().unlock();
/* 277:    */     }
/* 278:    */   }
/* 279:    */   
/* 280:    */   public int lastIndexOf(Object o)
/* 281:    */   {
/* 282:247 */     this.readWriteLock.readLock().lock();
/* 283:    */     try
/* 284:    */     {
/* 285:249 */       return this.list.lastIndexOf(o);
/* 286:    */     }
/* 287:    */     finally
/* 288:    */     {
/* 289:251 */       this.readWriteLock.readLock().unlock();
/* 290:    */     }
/* 291:    */   }
/* 292:    */   
/* 293:    */   public ListIterator<T> listIterator()
/* 294:    */   {
/* 295:257 */     this.readWriteLock.readLock().lock();
/* 296:    */     try
/* 297:    */     {
/* 298:259 */       return new ArrayList(this.list).listIterator();
/* 299:    */     }
/* 300:    */     finally
/* 301:    */     {
/* 302:261 */       this.readWriteLock.readLock().unlock();
/* 303:    */     }
/* 304:    */   }
/* 305:    */   
/* 306:    */   public ListIterator<T> listIterator(int index)
/* 307:    */   {
/* 308:267 */     this.readWriteLock.readLock().lock();
/* 309:    */     try
/* 310:    */     {
/* 311:269 */       return new ArrayList(this.list).listIterator(index);
/* 312:    */     }
/* 313:    */     finally
/* 314:    */     {
/* 315:271 */       this.readWriteLock.readLock().unlock();
/* 316:    */     }
/* 317:    */   }
/* 318:    */   
/* 319:    */   public List<T> subList(int fromIndex, int toIndex)
/* 320:    */   {
/* 321:277 */     this.readWriteLock.readLock().lock();
/* 322:    */     try
/* 323:    */     {
/* 324:279 */       return this.list.subList(fromIndex, toIndex);
/* 325:    */     }
/* 326:    */     finally
/* 327:    */     {
/* 328:281 */       this.readWriteLock.readLock().unlock();
/* 329:    */     }
/* 330:    */   }
/* 331:    */   
/* 332:    */   public String toString()
/* 333:    */   {
/* 334:287 */     this.readWriteLock.readLock().lock();
/* 335:    */     try
/* 336:    */     {
/* 337:289 */       return this.list.toString();
/* 338:    */     }
/* 339:    */     finally
/* 340:    */     {
/* 341:291 */       this.readWriteLock.readLock().unlock();
/* 342:    */     }
/* 343:    */   }
/* 344:    */   
/* 345:    */   public void sort()
/* 346:    */   {
/* 347:297 */     this.readWriteLock.writeLock().lock();
/* 348:    */     try
/* 349:    */     {
/* 350:300 */       Collections.sort(this.list);
/* 351:    */     }
/* 352:    */     finally
/* 353:    */     {
/* 354:302 */       this.readWriteLock.writeLock().unlock();
/* 355:    */     }
/* 356:    */   }
/* 357:    */   
/* 358:    */   public List<T> sortAndReturnPurgeList(float removePercent)
/* 359:    */   {
/* 360:308 */     this.readWriteLock.writeLock().lock();
/* 361:    */     try
/* 362:    */     {
/* 363:310 */       int size = this.list.size();
/* 364:311 */       int removeSize = (int)(size - size * removePercent);
/* 365:312 */       int start = size - removeSize;
/* 366:    */       
/* 367:314 */       Collections.sort(this.list);
/* 368:    */       
/* 369:316 */       List<T> removeList = new ArrayList(this.list.subList(0, start));
/* 370:317 */       this.list.removeAll(removeList);
/* 371:318 */       return removeList;
/* 372:    */     }
/* 373:    */     finally
/* 374:    */     {
/* 375:320 */       this.readWriteLock.writeLock().unlock();
/* 376:    */     }
/* 377:    */   }
/* 378:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.collections.SortableConcurrentList
 * JD-Core Version:    0.7.0.1
 */