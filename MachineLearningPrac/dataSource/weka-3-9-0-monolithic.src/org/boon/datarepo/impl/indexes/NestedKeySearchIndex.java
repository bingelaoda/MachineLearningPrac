/*  1:   */ package org.boon.datarepo.impl.indexes;
/*  2:   */ 
/*  3:   */ import java.util.List;
/*  4:   */ import org.boon.core.Conversions;
/*  5:   */ import org.boon.core.reflection.BeanUtils;
/*  6:   */ 
/*  7:   */ public class NestedKeySearchIndex
/*  8:   */   extends BaseIndexWrapper
/*  9:   */ {
/* 10:   */   public NestedKeySearchIndex(String... path)
/* 11:   */   {
/* 12:44 */     super(path);
/* 13:   */   }
/* 14:   */   
/* 15:   */   public boolean add(Object o)
/* 16:   */   {
/* 17:51 */     List keys = getKeys(o);
/* 18:52 */     this.index.addManyKeys(o, keys);
/* 19:53 */     return true;
/* 20:   */   }
/* 21:   */   
/* 22:   */   protected List getKeys(Object o)
/* 23:   */   {
/* 24:58 */     Object list = BeanUtils.getPropByPath(o, this.path);
/* 25:59 */     return Conversions.toList(list);
/* 26:   */   }
/* 27:   */   
/* 28:   */   public boolean delete(Object o)
/* 29:   */   {
/* 30:64 */     List keys = getKeys(o);
/* 31:65 */     this.index.removeManyKeys(o, keys);
/* 32:   */     
/* 33:67 */     return true;
/* 34:   */   }
/* 35:   */   
/* 36:   */   public boolean has(Object o)
/* 37:   */   {
/* 38:72 */     return this.index.has(o);
/* 39:   */   }
/* 40:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.indexes.NestedKeySearchIndex
 * JD-Core Version:    0.7.0.1
 */