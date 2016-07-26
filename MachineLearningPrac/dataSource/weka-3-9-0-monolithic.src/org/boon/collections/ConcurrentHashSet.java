/*   1:    */ package org.boon.collections;
/*   2:    */ 
/*   3:    */ import java.util.Collection;
/*   4:    */ import java.util.Iterator;
/*   5:    */ import java.util.Map;
/*   6:    */ import java.util.Set;
/*   7:    */ import java.util.concurrent.ConcurrentHashMap;
/*   8:    */ 
/*   9:    */ public class ConcurrentHashSet<T>
/*  10:    */   implements Set<T>
/*  11:    */ {
/*  12: 44 */   private static final Object NOTHING = new Object();
/*  13:    */   private final Map<T, Object> map;
/*  14:    */   
/*  15:    */   public ConcurrentHashSet(int size)
/*  16:    */   {
/*  17: 48 */     this.map = new ConcurrentHashMap(size);
/*  18:    */   }
/*  19:    */   
/*  20:    */   public ConcurrentHashSet()
/*  21:    */   {
/*  22: 52 */     this.map = new ConcurrentHashMap();
/*  23:    */   }
/*  24:    */   
/*  25:    */   public boolean add(T e)
/*  26:    */   {
/*  27: 58 */     return this.map.put(e, NOTHING) == null;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public boolean remove(Object o)
/*  31:    */   {
/*  32: 63 */     return this.map.remove(o) == null;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public boolean containsAll(Collection<?> collection)
/*  36:    */   {
/*  37: 68 */     return this.map.keySet().containsAll(collection);
/*  38:    */   }
/*  39:    */   
/*  40:    */   public boolean addAll(Collection<? extends T> collection)
/*  41:    */   {
/*  42: 73 */     boolean added = false;
/*  43: 74 */     for (T e : collection) {
/*  44: 75 */       if (this.map.put(e, NOTHING) == null) {
/*  45: 76 */         added = true;
/*  46:    */       }
/*  47:    */     }
/*  48: 79 */     return added;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public boolean retainAll(Collection<?> c)
/*  52:    */   {
/*  53: 84 */     throw new UnsupportedOperationException();
/*  54:    */   }
/*  55:    */   
/*  56:    */   public boolean removeAll(Collection<?> c)
/*  57:    */   {
/*  58: 89 */     throw new UnsupportedOperationException();
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void clear()
/*  62:    */   {
/*  63: 94 */     this.map.clear();
/*  64:    */   }
/*  65:    */   
/*  66:    */   public int size()
/*  67:    */   {
/*  68:100 */     return this.map.size();
/*  69:    */   }
/*  70:    */   
/*  71:    */   public boolean isEmpty()
/*  72:    */   {
/*  73:105 */     return this.map.isEmpty();
/*  74:    */   }
/*  75:    */   
/*  76:    */   public boolean contains(Object o)
/*  77:    */   {
/*  78:110 */     return this.map.containsKey(o);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public Iterator<T> iterator()
/*  82:    */   {
/*  83:115 */     return this.map.keySet().iterator();
/*  84:    */   }
/*  85:    */   
/*  86:    */   public Object[] toArray()
/*  87:    */   {
/*  88:120 */     return this.map.keySet().toArray();
/*  89:    */   }
/*  90:    */   
/*  91:    */   public <T> T[] toArray(T[] a)
/*  92:    */   {
/*  93:125 */     return this.map.keySet().toArray(a);
/*  94:    */   }
/*  95:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.collections.ConcurrentHashSet
 * JD-Core Version:    0.7.0.1
 */