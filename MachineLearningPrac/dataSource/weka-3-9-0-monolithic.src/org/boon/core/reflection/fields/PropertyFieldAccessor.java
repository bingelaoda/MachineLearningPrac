/*   1:    */ package org.boon.core.reflection.fields;
/*   2:    */ 
/*   3:    */ import java.util.LinkedHashMap;
/*   4:    */ import java.util.Map;
/*   5:    */ import java.util.Map.Entry;
/*   6:    */ import java.util.concurrent.ConcurrentHashMap;
/*   7:    */ import org.boon.core.reflection.Reflection;
/*   8:    */ 
/*   9:    */ public class PropertyFieldAccessor
/*  10:    */   implements FieldsAccessor
/*  11:    */ {
/*  12: 42 */   private final Map<Class<?>, Map<String, FieldAccess>> fieldMap = new ConcurrentHashMap();
/*  13:    */   private final boolean useAlias;
/*  14:    */   private final boolean caseInsensitive;
/*  15:    */   
/*  16:    */   public PropertyFieldAccessor(boolean useAlias)
/*  17:    */   {
/*  18: 49 */     this(useAlias, false);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public PropertyFieldAccessor(boolean useAlias, boolean caseInsensitive)
/*  22:    */   {
/*  23: 53 */     this.useAlias = useAlias;
/*  24: 54 */     this.caseInsensitive = caseInsensitive;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public final Map<String, FieldAccess> getFields(Class<? extends Object> aClass)
/*  28:    */   {
/*  29: 59 */     Map<String, FieldAccess> map = (Map)this.fieldMap.get(aClass);
/*  30: 60 */     if (map == null)
/*  31:    */     {
/*  32: 61 */       map = doGetFields(aClass);
/*  33: 62 */       this.fieldMap.put(aClass, map);
/*  34:    */     }
/*  35: 64 */     return map;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public boolean isCaseInsensitive()
/*  39:    */   {
/*  40: 69 */     return this.caseInsensitive;
/*  41:    */   }
/*  42:    */   
/*  43:    */   private final Map<String, FieldAccess> doGetFields(Class<? extends Object> aClass)
/*  44:    */   {
/*  45: 75 */     Map<String, FieldAccess> fieldAccessMap = Reflection.getPropertyFieldAccessors(aClass);
/*  46:    */     
/*  47:    */ 
/*  48: 78 */     Map<String, FieldAccess> mapOld = fieldAccessMap;
/*  49: 79 */     fieldAccessMap = new LinkedHashMap();
/*  50: 80 */     for (Map.Entry<String, FieldAccess> entry : mapOld.entrySet()) {
/*  51: 81 */       if (!((FieldAccess)entry.getValue()).isStatic()) {
/*  52: 84 */         fieldAccessMap.put(entry.getKey(), entry.getValue());
/*  53:    */       }
/*  54:    */     }
/*  55: 87 */     if (this.caseInsensitive)
/*  56:    */     {
/*  57: 88 */       mapOld = fieldAccessMap;
/*  58: 89 */       fieldAccessMap = new LinkedHashMap();
/*  59: 90 */       for (Map.Entry<String, FieldAccess> entry : mapOld.entrySet()) {
/*  60: 91 */         if (!((FieldAccess)entry.getValue()).isStatic())
/*  61:    */         {
/*  62: 94 */           fieldAccessMap.put(((String)entry.getKey()).toLowerCase(), entry.getValue());
/*  63:    */           
/*  64: 96 */           fieldAccessMap.put(((String)entry.getKey()).toUpperCase(), entry.getValue());
/*  65:    */           
/*  66: 98 */           fieldAccessMap.put(entry.getKey(), entry.getValue());
/*  67:    */         }
/*  68:    */       }
/*  69:    */     }
/*  70:102 */     if (this.useAlias)
/*  71:    */     {
/*  72:103 */       Map<String, FieldAccess> fieldAccessMap2 = new LinkedHashMap(fieldAccessMap.size());
/*  73:105 */       for (FieldAccess fa : fieldAccessMap.values()) {
/*  74:106 */         if (!fa.isStatic())
/*  75:    */         {
/*  76:109 */           String alias = fa.alias();
/*  77:110 */           if (this.caseInsensitive) {
/*  78:111 */             alias = alias.toLowerCase();
/*  79:    */           }
/*  80:113 */           fieldAccessMap2.put(alias, fa);
/*  81:    */         }
/*  82:    */       }
/*  83:115 */       return fieldAccessMap2;
/*  84:    */     }
/*  85:117 */     return fieldAccessMap;
/*  86:    */   }
/*  87:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.fields.PropertyFieldAccessor
 * JD-Core Version:    0.7.0.1
 */