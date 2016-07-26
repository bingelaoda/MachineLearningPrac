/*  1:   */ package org.boon.criteria;
/*  2:   */ 
/*  3:   */ import java.util.Collection;
/*  4:   */ import java.util.List;
/*  5:   */ import java.util.Map;
/*  6:   */ import org.boon.collections.IntList;
/*  7:   */ import org.boon.core.reflection.BeanUtils;
/*  8:   */ import org.boon.core.reflection.fields.FieldAccess;
/*  9:   */ 
/* 10:   */ public class IntCollector
/* 11:   */   extends Selector
/* 12:   */ {
/* 13:   */   private IntList list;
/* 14:   */   
/* 15:   */   public static IntCollector intCollector(String propertyName)
/* 16:   */   {
/* 17:53 */     return new IntCollector(propertyName);
/* 18:   */   }
/* 19:   */   
/* 20:   */   public IntCollector(String fieldName)
/* 21:   */   {
/* 22:59 */     super(fieldName);
/* 23:   */   }
/* 24:   */   
/* 25:   */   public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 26:   */   {
/* 27:   */     int value;
/* 28:   */     int value;
/* 29:65 */     if (this.path) {
/* 30:66 */       value = BeanUtils.idxInt(item, this.name);
/* 31:   */     } else {
/* 32:68 */       value = ((FieldAccess)fields.get(this.name)).getInt(item);
/* 33:   */     }
/* 34:70 */     this.list.add(value);
/* 35:   */   }
/* 36:   */   
/* 37:   */   public void handleStart(Collection<?> results)
/* 38:   */   {
/* 39:75 */     this.list = new IntList(results.size());
/* 40:   */   }
/* 41:   */   
/* 42:   */   public void handleComplete(List<Map<String, Object>> rows) {}
/* 43:   */   
/* 44:   */   public IntList list()
/* 45:   */   {
/* 46:86 */     return this.list;
/* 47:   */   }
/* 48:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.criteria.IntCollector
 * JD-Core Version:    0.7.0.1
 */