/*   1:    */ package org.boon.core.reflection.fields;
/*   2:    */ 
/*   3:    */ import java.util.LinkedHashMap;
/*   4:    */ import java.util.Map;
/*   5:    */ import java.util.Map.Entry;
/*   6:    */ import java.util.concurrent.ConcurrentHashMap;
/*   7:    */ import org.boon.core.reflection.Reflection;
/*   8:    */ 
/*   9:    */ public class FieldsAccessorsPropertyThenField
/*  10:    */   implements FieldsAccessor
/*  11:    */ {
/*  12: 39 */   private final Map<Class<?>, Map<String, FieldAccess>> fieldMap = new ConcurrentHashMap();
/*  13:    */   private final boolean useAlias;
/*  14:    */   private final boolean caseInsensitive;
/*  15:    */   
/*  16:    */   public FieldsAccessorsPropertyThenField(boolean useAlias)
/*  17:    */   {
/*  18: 44 */     this(useAlias, false);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public FieldsAccessorsPropertyThenField(boolean useAlias, boolean caseInsensitive)
/*  22:    */   {
/*  23: 48 */     this.useAlias = useAlias;
/*  24: 49 */     this.caseInsensitive = caseInsensitive;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public final Map<String, FieldAccess> getFields(Class<? extends Object> aClass)
/*  28:    */   {
/*  29: 53 */     Map<String, FieldAccess> map = (Map)this.fieldMap.get(aClass);
/*  30: 54 */     if (map == null)
/*  31:    */     {
/*  32: 55 */       map = doGetFields(aClass);
/*  33: 56 */       this.fieldMap.put(aClass, map);
/*  34:    */     }
/*  35: 58 */     return map;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public boolean isCaseInsensitive()
/*  39:    */   {
/*  40: 63 */     return this.caseInsensitive;
/*  41:    */   }
/*  42:    */   
/*  43:    */   private final Map<String, FieldAccess> doGetFields(Class<? extends Object> aClass)
/*  44:    */   {
/*  45: 68 */     Map<String, FieldAccess> fieldAccessMap = Reflection.getPropertyFieldAccessMapPropertyFirstForSerializer(aClass);
/*  46:    */     
/*  47:    */ 
/*  48: 71 */     Map<String, FieldAccess> mapOld = fieldAccessMap;
/*  49: 72 */     fieldAccessMap = new LinkedHashMap();
/*  50: 73 */     for (Map.Entry<String, FieldAccess> entry : mapOld.entrySet()) {
/*  51: 74 */       if (!((FieldAccess)entry.getValue()).isStatic()) {
/*  52: 77 */         fieldAccessMap.put(entry.getKey(), entry.getValue());
/*  53:    */       }
/*  54:    */     }
/*  55: 80 */     if (this.caseInsensitive)
/*  56:    */     {
/*  57: 81 */       mapOld = fieldAccessMap;
/*  58: 82 */       fieldAccessMap = new LinkedHashMap();
/*  59: 83 */       for (Map.Entry<String, FieldAccess> entry : mapOld.entrySet()) {
/*  60: 84 */         if (!((FieldAccess)entry.getValue()).isStatic())
/*  61:    */         {
/*  62: 88 */           fieldAccessMap.put(((String)entry.getKey()).toLowerCase(), entry.getValue());
/*  63:    */           
/*  64: 90 */           fieldAccessMap.put(((String)entry.getKey()).toUpperCase(), entry.getValue());
/*  65:    */           
/*  66: 92 */           fieldAccessMap.put(entry.getKey(), entry.getValue());
/*  67:    */         }
/*  68:    */       }
/*  69:    */     }
/*  70: 96 */     if (this.useAlias)
/*  71:    */     {
/*  72: 97 */       Map<String, FieldAccess> fieldAccessMap2 = new LinkedHashMap(fieldAccessMap.size());
/*  73: 99 */       for (FieldAccess fa : fieldAccessMap.values()) {
/*  74:100 */         if (!fa.isStatic())
/*  75:    */         {
/*  76:103 */           String alias = fa.alias();
/*  77:104 */           if (this.caseInsensitive) {
/*  78:105 */             alias = alias.toLowerCase();
/*  79:    */           }
/*  80:107 */           fieldAccessMap2.put(alias, fa);
/*  81:    */         }
/*  82:    */       }
/*  83:109 */       return fieldAccessMap2;
/*  84:    */     }
/*  85:111 */     return fieldAccessMap;
/*  86:    */   }
/*  87:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.fields.FieldsAccessorsPropertyThenField
 * JD-Core Version:    0.7.0.1
 */