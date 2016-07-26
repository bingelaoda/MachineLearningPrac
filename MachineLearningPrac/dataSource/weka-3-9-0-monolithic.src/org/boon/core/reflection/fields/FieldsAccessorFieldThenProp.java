/*   1:    */ package org.boon.core.reflection.fields;
/*   2:    */ 
/*   3:    */ import java.util.LinkedHashMap;
/*   4:    */ import java.util.Map;
/*   5:    */ import java.util.Map.Entry;
/*   6:    */ import java.util.concurrent.ConcurrentHashMap;
/*   7:    */ import org.boon.core.reflection.Reflection;
/*   8:    */ 
/*   9:    */ public class FieldsAccessorFieldThenProp
/*  10:    */   implements FieldsAccessor
/*  11:    */ {
/*  12: 39 */   private final Map<Class<?>, Map<String, FieldAccess>> fieldMap = new ConcurrentHashMap();
/*  13:    */   private final boolean useAlias;
/*  14:    */   private final boolean caseInsensitive;
/*  15:    */   
/*  16:    */   public FieldsAccessorFieldThenProp(boolean useAlias)
/*  17:    */   {
/*  18: 47 */     this(useAlias, false);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public FieldsAccessorFieldThenProp(boolean useAlias, boolean caseInsensitive)
/*  22:    */   {
/*  23: 51 */     this.useAlias = useAlias;
/*  24: 52 */     this.caseInsensitive = caseInsensitive;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public final Map<String, FieldAccess> getFields(Class<? extends Object> aClass)
/*  28:    */   {
/*  29: 57 */     Map<String, FieldAccess> map = (Map)this.fieldMap.get(aClass);
/*  30: 58 */     if (map == null)
/*  31:    */     {
/*  32: 59 */       map = doGetFields(aClass);
/*  33: 60 */       this.fieldMap.put(aClass, map);
/*  34:    */     }
/*  35: 62 */     return map;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public boolean isCaseInsensitive()
/*  39:    */   {
/*  40: 67 */     return this.caseInsensitive;
/*  41:    */   }
/*  42:    */   
/*  43:    */   private final Map<String, FieldAccess> doGetFields(Class<? extends Object> aClass)
/*  44:    */   {
/*  45: 71 */     Map<String, FieldAccess> fieldAccessMap = Reflection.getPropertyFieldAccessMapFieldFirstForSerializer(aClass);
/*  46:    */     
/*  47:    */ 
/*  48: 74 */     Map<String, FieldAccess> mapOld = fieldAccessMap;
/*  49: 75 */     fieldAccessMap = new LinkedHashMap();
/*  50: 76 */     for (Map.Entry<String, FieldAccess> entry : mapOld.entrySet()) {
/*  51: 77 */       if (!((FieldAccess)entry.getValue()).isStatic()) {
/*  52: 80 */         fieldAccessMap.put(entry.getKey(), entry.getValue());
/*  53:    */       }
/*  54:    */     }
/*  55: 83 */     if (this.caseInsensitive)
/*  56:    */     {
/*  57: 84 */       mapOld = fieldAccessMap;
/*  58: 85 */       fieldAccessMap = new LinkedHashMap();
/*  59: 86 */       for (Map.Entry<String, FieldAccess> entry : mapOld.entrySet()) {
/*  60: 87 */         if (!((FieldAccess)entry.getValue()).isStatic())
/*  61:    */         {
/*  62: 90 */           fieldAccessMap.put(((String)entry.getKey()).toLowerCase(), entry.getValue());
/*  63:    */           
/*  64: 92 */           fieldAccessMap.put(((String)entry.getKey()).toUpperCase(), entry.getValue());
/*  65:    */           
/*  66: 94 */           fieldAccessMap.put(entry.getKey(), entry.getValue());
/*  67:    */         }
/*  68:    */       }
/*  69:    */     }
/*  70: 98 */     if (this.useAlias)
/*  71:    */     {
/*  72: 99 */       Map<String, FieldAccess> fieldAccessMap2 = new LinkedHashMap(fieldAccessMap.size());
/*  73:101 */       for (FieldAccess fa : fieldAccessMap.values()) {
/*  74:102 */         if (!fa.isStatic())
/*  75:    */         {
/*  76:105 */           String alias = fa.alias();
/*  77:106 */           if (this.caseInsensitive) {
/*  78:107 */             alias = alias.toLowerCase();
/*  79:    */           }
/*  80:109 */           fieldAccessMap2.put(alias, fa);
/*  81:    */         }
/*  82:    */       }
/*  83:111 */       return fieldAccessMap2;
/*  84:    */     }
/*  85:113 */     return fieldAccessMap;
/*  86:    */   }
/*  87:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.fields.FieldsAccessorFieldThenProp
 * JD-Core Version:    0.7.0.1
 */