/*   1:    */ package org.boon.collections;
/*   2:    */ 
/*   3:    */ import java.util.Collection;
/*   4:    */ import java.util.Iterator;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.Set;
/*   7:    */ import java.util.concurrent.CopyOnWriteArrayList;
/*   8:    */ 
/*   9:    */ public class ConcurrentLinkedHashSet<T>
/*  10:    */   implements Set<T>
/*  11:    */ {
/*  12: 40 */   List<T> list = new CopyOnWriteArrayList();
/*  13: 41 */   Set<T> set = new ConcurrentHashSet();
/*  14:    */   
/*  15:    */   public int size()
/*  16:    */   {
/*  17: 45 */     return this.list.size();
/*  18:    */   }
/*  19:    */   
/*  20:    */   public boolean isEmpty()
/*  21:    */   {
/*  22: 50 */     return this.set.isEmpty();
/*  23:    */   }
/*  24:    */   
/*  25:    */   public boolean contains(Object o)
/*  26:    */   {
/*  27: 55 */     return this.set.contains(o);
/*  28:    */   }
/*  29:    */   
/*  30:    */   public Iterator<T> iterator()
/*  31:    */   {
/*  32: 60 */     return this.list.iterator();
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Object[] toArray()
/*  36:    */   {
/*  37: 65 */     return this.list.toArray(new Object[this.list.size()]);
/*  38:    */   }
/*  39:    */   
/*  40:    */   public <T1> T1[] toArray(T1[] a)
/*  41:    */   {
/*  42: 71 */     return this.list.toArray(a);
/*  43:    */   }
/*  44:    */   
/*  45:    */   public synchronized boolean add(T t)
/*  46:    */   {
/*  47: 76 */     this.list.remove(t);
/*  48: 77 */     this.list.add(t);
/*  49: 78 */     return this.set.add(t);
/*  50:    */   }
/*  51:    */   
/*  52:    */   public synchronized boolean addFirst(T t)
/*  53:    */   {
/*  54: 83 */     this.list.remove(t);
/*  55: 84 */     this.list.add(0, t);
/*  56: 85 */     return this.set.add(t);
/*  57:    */   }
/*  58:    */   
/*  59:    */   public synchronized boolean remove(Object o)
/*  60:    */   {
/*  61: 90 */     this.list.remove(o);
/*  62: 91 */     return this.set.remove(o);
/*  63:    */   }
/*  64:    */   
/*  65:    */   public boolean containsAll(Collection<?> c)
/*  66:    */   {
/*  67: 96 */     return this.set.containsAll(c);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public boolean addAll(Collection<? extends T> c)
/*  71:    */   {
/*  72:101 */     return this.set.addAll(c);
/*  73:    */   }
/*  74:    */   
/*  75:    */   public synchronized boolean retainAll(Collection<?> c)
/*  76:    */   {
/*  77:106 */     this.list.retainAll(c);
/*  78:107 */     return this.set.retainAll(c);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public synchronized boolean removeAll(Collection<?> c)
/*  82:    */   {
/*  83:112 */     this.list.removeAll(c);
/*  84:113 */     return this.set.removeAll(c);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public synchronized void clear()
/*  88:    */   {
/*  89:118 */     this.set.clear();
/*  90:119 */     this.list.clear();
/*  91:    */   }
/*  92:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.collections.ConcurrentLinkedHashSet
 * JD-Core Version:    0.7.0.1
 */