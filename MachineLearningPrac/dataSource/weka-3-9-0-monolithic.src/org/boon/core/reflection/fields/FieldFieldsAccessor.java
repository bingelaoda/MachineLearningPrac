/*   1:    */ package org.boon.core.reflection.fields;
/*   2:    */ 
/*   3:    */ import java.util.LinkedHashMap;
/*   4:    */ import java.util.Map;
/*   5:    */ import java.util.Map.Entry;
/*   6:    */ import java.util.concurrent.ConcurrentHashMap;
/*   7:    */ import org.boon.core.reflection.Reflection;
/*   8:    */ 
/*   9:    */ public class FieldFieldsAccessor
/*  10:    */   implements FieldsAccessor
/*  11:    */ {
/*  12: 44 */   private final Map<Class<?>, Map<String, FieldAccess>> fieldMap = new ConcurrentHashMap();
/*  13:    */   private final boolean useAlias;
/*  14:    */   private final boolean caseInsensitive;
/*  15:    */   
/*  16:    */   public FieldFieldsAccessor(boolean useAlias)
/*  17:    */   {
/*  18: 51 */     this(useAlias, false);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public FieldFieldsAccessor(boolean useAlias, boolean caseInsensitive)
/*  22:    */   {
/*  23: 55 */     this.useAlias = useAlias;
/*  24: 56 */     this.caseInsensitive = caseInsensitive;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public final Map<String, FieldAccess> getFields(Class<? extends Object> aClass)
/*  28:    */   {
/*  29: 62 */     Map<String, FieldAccess> map = (Map)this.fieldMap.get(aClass);
/*  30: 63 */     if (map == null)
/*  31:    */     {
/*  32: 64 */       map = doGetFields(aClass);
/*  33: 65 */       this.fieldMap.put(aClass, map);
/*  34:    */     }
/*  35: 67 */     return map;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public boolean isCaseInsensitive()
/*  39:    */   {
/*  40: 74 */     return this.caseInsensitive;
/*  41:    */   }
/*  42:    */   
/*  43:    */   private final Map<String, FieldAccess> doGetFields(Class<? extends Object> aClass)
/*  44:    */   {
/*  45: 80 */     Map<String, FieldAccess> fieldAccessMap = Reflection.getAllAccessorFields(aClass, true);
/*  46:    */     
/*  47: 82 */     Map<String, FieldAccess> mapOld = fieldAccessMap;
/*  48: 83 */     fieldAccessMap = new LinkedHashMap();
/*  49: 84 */     for (Map.Entry<String, FieldAccess> entry : mapOld.entrySet()) {
/*  50: 85 */       if (!((FieldAccess)entry.getValue()).isStatic()) {
/*  51: 88 */         fieldAccessMap.put(entry.getKey(), entry.getValue());
/*  52:    */       }
/*  53:    */     }
/*  54: 91 */     if (this.caseInsensitive)
/*  55:    */     {
/*  56: 92 */       mapOld = fieldAccessMap;
/*  57: 93 */       fieldAccessMap = new LinkedHashMap();
/*  58: 94 */       for (Map.Entry<String, FieldAccess> entry : mapOld.entrySet()) {
/*  59: 95 */         if (!((FieldAccess)entry.getValue()).isStatic())
/*  60:    */         {
/*  61: 98 */           fieldAccessMap.put(((String)entry.getKey()).toLowerCase(), entry.getValue());
/*  62:    */           
/*  63:100 */           fieldAccessMap.put(entry.getKey(), entry.getValue());
/*  64:    */           
/*  65:102 */           fieldAccessMap.put(((String)entry.getKey()).toUpperCase(), entry.getValue());
/*  66:    */         }
/*  67:    */       }
/*  68:    */     }
/*  69:106 */     if (this.useAlias)
/*  70:    */     {
/*  71:107 */       Map<String, FieldAccess> fieldAccessMap2 = new LinkedHashMap(fieldAccessMap.size());
/*  72:109 */       for (FieldAccess fa : fieldAccessMap.values()) {
/*  73:110 */         if (!fa.isStatic())
/*  74:    */         {
/*  75:113 */           String alias = fa.alias();
/*  76:114 */           if (this.caseInsensitive) {
/*  77:115 */             alias = alias.toLowerCase();
/*  78:    */           }
/*  79:117 */           fieldAccessMap2.put(alias, fa);
/*  80:    */         }
/*  81:    */       }
/*  82:119 */       return fieldAccessMap2;
/*  83:    */     }
/*  84:121 */     return fieldAccessMap;
/*  85:    */   }
/*  86:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.fields.FieldFieldsAccessor
 * JD-Core Version:    0.7.0.1
 */