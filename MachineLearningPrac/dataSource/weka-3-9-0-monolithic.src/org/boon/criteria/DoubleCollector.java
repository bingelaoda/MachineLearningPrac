/*  1:   */ package org.boon.criteria;
/*  2:   */ 
/*  3:   */ import java.util.Collection;
/*  4:   */ import java.util.List;
/*  5:   */ import java.util.Map;
/*  6:   */ import org.boon.collections.DoubleList;
/*  7:   */ import org.boon.core.reflection.BeanUtils;
/*  8:   */ import org.boon.core.reflection.fields.FieldAccess;
/*  9:   */ 
/* 10:   */ public class DoubleCollector
/* 11:   */   extends Selector
/* 12:   */ {
/* 13:   */   private DoubleList list;
/* 14:   */   
/* 15:   */   public static DoubleCollector intCollector(String propertyName)
/* 16:   */   {
/* 17:56 */     return new DoubleCollector(propertyName);
/* 18:   */   }
/* 19:   */   
/* 20:   */   public DoubleCollector(String fieldName)
/* 21:   */   {
/* 22:62 */     super(fieldName);
/* 23:   */   }
/* 24:   */   
/* 25:   */   public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 26:   */   {
/* 27:   */     double value;
/* 28:   */     double value;
/* 29:68 */     if (this.path) {
/* 30:69 */       value = BeanUtils.idxDouble(item, this.name);
/* 31:   */     } else {
/* 32:71 */       value = ((FieldAccess)fields.get(this.name)).getDouble(item);
/* 33:   */     }
/* 34:73 */     this.list.add(value);
/* 35:   */   }
/* 36:   */   
/* 37:   */   public void handleStart(Collection<?> results)
/* 38:   */   {
/* 39:78 */     this.list = new DoubleList(results.size());
/* 40:   */   }
/* 41:   */   
/* 42:   */   public void handleComplete(List<Map<String, Object>> rows) {}
/* 43:   */   
/* 44:   */   public DoubleList list()
/* 45:   */   {
/* 46:89 */     return this.list;
/* 47:   */   }
/* 48:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.criteria.DoubleCollector
 * JD-Core Version:    0.7.0.1
 */