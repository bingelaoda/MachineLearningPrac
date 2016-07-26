/*   1:    */ package org.boon.json.serializers.impl;
/*   2:    */ 
/*   3:    */ import java.util.Collection;
/*   4:    */ import java.util.Map;
/*   5:    */ import org.boon.core.reflection.fields.FieldAccess;
/*   6:    */ import org.boon.json.serializers.InstanceSerializer;
/*   7:    */ import org.boon.json.serializers.JsonSerializerInternal;
/*   8:    */ import org.boon.primitive.CharBuf;
/*   9:    */ 
/*  10:    */ public class InstanceSerializerImpl
/*  11:    */   implements InstanceSerializer
/*  12:    */ {
/*  13:    */   public final void serializeInstance(JsonSerializerInternal serializer, Object instance, CharBuf builder)
/*  14:    */   {
/*  15: 46 */     Map<String, FieldAccess> fieldAccessors = serializer.getFields(instance.getClass());
/*  16: 47 */     Collection<FieldAccess> values = fieldAccessors.values();
/*  17:    */     
/*  18:    */ 
/*  19:    */ 
/*  20: 51 */     builder.addChar('{');
/*  21:    */     
/*  22: 53 */     int index = 0;
/*  23: 54 */     for (FieldAccess fieldAccess : values) {
/*  24: 55 */       if (serializer.serializeField(instance, fieldAccess, builder))
/*  25:    */       {
/*  26: 56 */         builder.addChar(',');
/*  27: 57 */         index++;
/*  28:    */       }
/*  29:    */     }
/*  30: 60 */     if (index > 0) {
/*  31: 61 */       builder.removeLastChar();
/*  32:    */     }
/*  33: 63 */     builder.addChar('}');
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void serializeSubtypeInstance(JsonSerializerInternal serializer, Object instance, CharBuf builder)
/*  37:    */   {
/*  38: 71 */     builder.addString("{\"class\":");
/*  39: 72 */     builder.addQuoted(instance.getClass().getName());
/*  40: 73 */     Map<String, FieldAccess> fieldAccessors = serializer.getFields(instance.getClass());
/*  41:    */     
/*  42: 75 */     int index = 0;
/*  43: 76 */     Collection<FieldAccess> values = fieldAccessors.values();
/*  44: 77 */     int length = values.size();
/*  45: 79 */     if (length > 0)
/*  46:    */     {
/*  47: 80 */       builder.addChar(',');
/*  48: 83 */       for (FieldAccess fieldAccess : values)
/*  49:    */       {
/*  50: 84 */         boolean sent = serializer.serializeField(instance, fieldAccess, builder);
/*  51: 85 */         if (sent)
/*  52:    */         {
/*  53: 86 */           index++;
/*  54: 87 */           builder.addChar(',');
/*  55:    */         }
/*  56:    */       }
/*  57: 92 */       if (index > 0) {
/*  58: 93 */         builder.removeLastChar();
/*  59:    */       }
/*  60: 96 */       builder.addChar('}');
/*  61:    */     }
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void serializeInstance(JsonSerializerImpl serializer, Object instance, CharBuf builder, boolean includeTypeInfo)
/*  65:    */   {
/*  66:106 */     Map<String, FieldAccess> fieldAccessors = serializer.getFields(instance.getClass());
/*  67:107 */     Collection<FieldAccess> values = fieldAccessors.values();
/*  68:109 */     if (includeTypeInfo)
/*  69:    */     {
/*  70:110 */       builder.addString("{\"class\":");
/*  71:111 */       builder.addQuoted(instance.getClass().getName());
/*  72:112 */       builder.addChar(',');
/*  73:    */     }
/*  74:    */     else
/*  75:    */     {
/*  76:115 */       builder.addChar('{');
/*  77:    */     }
/*  78:117 */     int index = 0;
/*  79:118 */     for (FieldAccess fieldAccess : values) {
/*  80:119 */       if (serializer.serializeField(instance, fieldAccess, builder))
/*  81:    */       {
/*  82:120 */         builder.addChar(',');
/*  83:121 */         index++;
/*  84:    */       }
/*  85:    */     }
/*  86:124 */     if (index > 0) {
/*  87:125 */       builder.removeLastChar();
/*  88:    */     }
/*  89:127 */     builder.addChar('}');
/*  90:    */   }
/*  91:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.impl.InstanceSerializerImpl
 * JD-Core Version:    0.7.0.1
 */