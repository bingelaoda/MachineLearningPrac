/*  1:   */ package org.boon.core.reflection.fields;
/*  2:   */ 
/*  3:   */ public enum FieldAccessMode
/*  4:   */ {
/*  5:33 */   PROPERTY,  FIELD,  FIELD_THEN_PROPERTY,  PROPERTY_THEN_FIELD;
/*  6:   */   
/*  7:   */   private FieldAccessMode() {}
/*  8:   */   
/*  9:   */   public FieldsAccessor create(boolean useAlias)
/* 10:   */   {
/* 11:40 */     return create(this, useAlias, false);
/* 12:   */   }
/* 13:   */   
/* 14:   */   public FieldsAccessor create(boolean useAlias, boolean caseInsensitive)
/* 15:   */   {
/* 16:44 */     return create(this, useAlias, caseInsensitive);
/* 17:   */   }
/* 18:   */   
/* 19:   */   public static FieldsAccessor create(FieldAccessMode fieldAccessType, boolean useAlias)
/* 20:   */   {
/* 21:48 */     return create(fieldAccessType, useAlias, false);
/* 22:   */   }
/* 23:   */   
/* 24:   */   public static FieldsAccessor create(FieldAccessMode fieldAccessType, boolean useAlias, boolean caseInsensitive)
/* 25:   */   {
/* 26:52 */     FieldsAccessor fieldsAccessor = null;
/* 27:54 */     switch (1.$SwitchMap$org$boon$core$reflection$fields$FieldAccessMode[fieldAccessType.ordinal()])
/* 28:   */     {
/* 29:   */     case 1: 
/* 30:56 */       fieldsAccessor = new FieldFieldsAccessor(useAlias, caseInsensitive);
/* 31:57 */       break;
/* 32:   */     case 2: 
/* 33:59 */       fieldsAccessor = new PropertyFieldAccessor(useAlias, caseInsensitive);
/* 34:60 */       break;
/* 35:   */     case 3: 
/* 36:62 */       fieldsAccessor = new FieldsAccessorFieldThenProp(useAlias, caseInsensitive);
/* 37:63 */       break;
/* 38:   */     case 4: 
/* 39:65 */       fieldsAccessor = new FieldsAccessorsPropertyThenField(useAlias, caseInsensitive);
/* 40:66 */       break;
/* 41:   */     default: 
/* 42:68 */       fieldsAccessor = new FieldFieldsAccessor(useAlias, caseInsensitive);
/* 43:   */     }
/* 44:72 */     return fieldsAccessor;
/* 45:   */   }
/* 46:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.fields.FieldAccessMode
 * JD-Core Version:    0.7.0.1
 */