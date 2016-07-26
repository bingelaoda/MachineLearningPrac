/*  1:   */ package org.boon.criteria;
/*  2:   */ 
/*  3:   */ import java.util.Collection;
/*  4:   */ import java.util.List;
/*  5:   */ import java.util.Map;
/*  6:   */ import org.boon.collections.FloatList;
/*  7:   */ import org.boon.core.reflection.BeanUtils;
/*  8:   */ import org.boon.core.reflection.fields.FieldAccess;
/*  9:   */ 
/* 10:   */ public class FloatCollector
/* 11:   */   extends Selector
/* 12:   */ {
/* 13:   */   private FloatList list;
/* 14:   */   
/* 15:   */   public static DoubleCollector intCollector(String propertyName)
/* 16:   */   {
/* 17:54 */     return new DoubleCollector(propertyName);
/* 18:   */   }
/* 19:   */   
/* 20:   */   public FloatCollector(String fieldName)
/* 21:   */   {
/* 22:60 */     super(fieldName);
/* 23:   */   }
/* 24:   */   
/* 25:   */   public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 26:   */   {
/* 27:   */     float value;
/* 28:   */     float value;
/* 29:66 */     if (this.path) {
/* 30:67 */       value = BeanUtils.idxFloat(item, this.name);
/* 31:   */     } else {
/* 32:69 */       value = ((FieldAccess)fields.get(this.name)).getFloat(item);
/* 33:   */     }
/* 34:71 */     this.list.add(value);
/* 35:   */   }
/* 36:   */   
/* 37:   */   public void handleStart(Collection<?> results)
/* 38:   */   {
/* 39:76 */     this.list = new FloatList(results.size());
/* 40:   */   }
/* 41:   */   
/* 42:   */   public void handleComplete(List<Map<String, Object>> rows) {}
/* 43:   */   
/* 44:   */   public FloatList list()
/* 45:   */   {
/* 46:87 */     return this.list;
/* 47:   */   }
/* 48:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.criteria.FloatCollector
 * JD-Core Version:    0.7.0.1
 */