/*  1:   */ package org.boon.collections;
/*  2:   */ 
/*  3:   */ import java.util.AbstractSet;
/*  4:   */ import java.util.Iterator;
/*  5:   */ import java.util.List;
/*  6:   */ import java.util.Map.Entry;
/*  7:   */ import org.boon.Lists;
/*  8:   */ import org.boon.Maps;
/*  9:   */ 
/* 10:   */ public class FakeMapEntrySet
/* 11:   */   extends AbstractSet<Map.Entry<String, Object>>
/* 12:   */ {
/* 13:   */   Map.Entry<String, Object>[] array;
/* 14:   */   
/* 15:   */   public FakeMapEntrySet(int size, String[] keys, Object[] values)
/* 16:   */   {
/* 17:20 */     this.array = new Map.Entry[size];
/* 18:22 */     for (int index = 0; index < size; index++) {
/* 19:23 */       this.array[index] = Maps.entry(keys[index], values[index]);
/* 20:   */     }
/* 21:   */   }
/* 22:   */   
/* 23:   */   public Iterator<Map.Entry<String, Object>> iterator()
/* 24:   */   {
/* 25:29 */     return Lists.list(this.array).iterator();
/* 26:   */   }
/* 27:   */   
/* 28:   */   public int size()
/* 29:   */   {
/* 30:34 */     return this.array.length;
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.collections.FakeMapEntrySet
 * JD-Core Version:    0.7.0.1
 */