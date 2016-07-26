/*  1:   */ package org.boon.datarepo.impl.indexes;
/*  2:   */ 
/*  3:   */ import java.util.ArrayList;
/*  4:   */ import java.util.List;
/*  5:   */ 
/*  6:   */ class MultiValue<T>
/*  7:   */ {
/*  8:41 */   List<T> values = null;
/*  9:   */   
/* 10:   */   public static <T> MultiValue<T> add(MultiValue<T> org, T newItem, int bucketSize)
/* 11:   */   {
/* 12:44 */     if (org == null) {
/* 13:45 */       return new MultiValue(newItem, bucketSize);
/* 14:   */     }
/* 15:48 */     org.add(newItem);
/* 16:   */     
/* 17:50 */     return org;
/* 18:   */   }
/* 19:   */   
/* 20:   */   public static <T> MultiValue<T> remove(MultiValue<T> org, T removeItem)
/* 21:   */   {
/* 22:54 */     if (org == null) {
/* 23:55 */       return null;
/* 24:   */     }
/* 25:58 */     if (removeItem != null) {
/* 26:59 */       org.remove(removeItem);
/* 27:   */     }
/* 28:62 */     return org.size() == 0 ? null : org;
/* 29:   */   }
/* 30:   */   
/* 31:   */   private MultiValue() {}
/* 32:   */   
/* 33:   */   private MultiValue(T item, int bucketSize)
/* 34:   */   {
/* 35:70 */     this.values = new ArrayList(bucketSize);
/* 36:71 */     this.values.add(item);
/* 37:   */   }
/* 38:   */   
/* 39:   */   private void add(T item)
/* 40:   */   {
/* 41:77 */     this.values.add(item);
/* 42:   */   }
/* 43:   */   
/* 44:   */   private void remove(T item)
/* 45:   */   {
/* 46:81 */     this.values.remove(item);
/* 47:   */   }
/* 48:   */   
/* 49:   */   T getValue()
/* 50:   */   {
/* 51:86 */     return this.values.size() > 0 ? this.values.get(0) : null;
/* 52:   */   }
/* 53:   */   
/* 54:   */   final List<T> getValues()
/* 55:   */   {
/* 56:90 */     return this.values;
/* 57:   */   }
/* 58:   */   
/* 59:   */   int size()
/* 60:   */   {
/* 61:95 */     return this.values.size();
/* 62:   */   }
/* 63:   */   
/* 64:   */   void addTo(List<T> results)
/* 65:   */   {
/* 66:99 */     results.addAll(this.values);
/* 67:   */   }
/* 68:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.indexes.MultiValue
 * JD-Core Version:    0.7.0.1
 */