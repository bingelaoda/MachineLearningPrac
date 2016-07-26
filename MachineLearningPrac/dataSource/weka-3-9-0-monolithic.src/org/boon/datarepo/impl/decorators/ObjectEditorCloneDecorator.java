/*  1:   */ package org.boon.datarepo.impl.decorators;
/*  2:   */ 
/*  3:   */ import org.boon.core.reflection.BeanUtils;
/*  4:   */ import org.boon.datarepo.ObjectEditor;
/*  5:   */ 
/*  6:   */ public class ObjectEditorCloneDecorator<KEY, ITEM>
/*  7:   */   extends ObjectEditorDecoratorBase<KEY, ITEM>
/*  8:   */ {
/*  9:   */   public ObjectEditorCloneDecorator() {}
/* 10:   */   
/* 11:   */   public void modify(ITEM item)
/* 12:   */   {
/* 13:44 */     item = BeanUtils.copy(item);
/* 14:45 */     super.modify(item);
/* 15:   */   }
/* 16:   */   
/* 17:   */   public void put(ITEM item)
/* 18:   */   {
/* 19:50 */     item = BeanUtils.copy(item);
/* 20:51 */     super.put(item);
/* 21:   */   }
/* 22:   */   
/* 23:   */   public boolean add(ITEM item)
/* 24:   */   {
/* 25:57 */     item = BeanUtils.copy(item);
/* 26:58 */     return super.add(item);
/* 27:   */   }
/* 28:   */   
/* 29:   */   public ITEM get(KEY key)
/* 30:   */   {
/* 31:63 */     ITEM item = super.get(key);
/* 32:64 */     if (item != null) {
/* 33:65 */       return BeanUtils.copy(item);
/* 34:   */     }
/* 35:67 */     return item;
/* 36:   */   }
/* 37:   */   
/* 38:   */   public ObjectEditorCloneDecorator(ObjectEditor oe)
/* 39:   */   {
/* 40:71 */     super(oe);
/* 41:   */   }
/* 42:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.decorators.ObjectEditorCloneDecorator
 * JD-Core Version:    0.7.0.1
 */