/*   1:    */ package org.boon.json.serializers.impl;
/*   2:    */ 
/*   3:    */ import java.util.Collection;
/*   4:    */ import java.util.Date;
/*   5:    */ import java.util.Map;
/*   6:    */ import org.boon.Exceptions;
/*   7:    */ import org.boon.core.reflection.fields.FieldAccess;
/*   8:    */ import org.boon.core.reflection.fields.FieldsAccessor;
/*   9:    */ import org.boon.core.reflection.fields.FieldsAccessorFieldThenProp;
/*  10:    */ import org.boon.json.serializers.ArraySerializer;
/*  11:    */ import org.boon.json.serializers.CollectionSerializer;
/*  12:    */ import org.boon.json.serializers.DateSerializer;
/*  13:    */ import org.boon.json.serializers.FieldSerializer;
/*  14:    */ import org.boon.json.serializers.InstanceSerializer;
/*  15:    */ import org.boon.json.serializers.JsonSerializerInternal;
/*  16:    */ import org.boon.json.serializers.MapSerializer;
/*  17:    */ import org.boon.json.serializers.ObjectSerializer;
/*  18:    */ import org.boon.json.serializers.StringSerializer;
/*  19:    */ import org.boon.json.serializers.UnknownSerializer;
/*  20:    */ import org.boon.primitive.CharBuf;
/*  21:    */ 
/*  22:    */ public class JsonSerializerImpl
/*  23:    */   implements JsonSerializerInternal
/*  24:    */ {
/*  25:    */   private final ObjectSerializer objectSerializer;
/*  26:    */   private final StringSerializer stringSerializer;
/*  27:    */   private final MapSerializer mapSerializer;
/*  28:    */   private final FieldSerializer fieldSerializer;
/*  29:    */   private final InstanceSerializer instanceSerializer;
/*  30:    */   private final CollectionSerializer collectionSerializer;
/*  31:    */   private final ArraySerializer arraySerializer;
/*  32:    */   private final UnknownSerializer unknownSerializer;
/*  33:    */   private final DateSerializer dateSerializer;
/*  34:    */   private final FieldsAccessor fieldsAccessor;
/*  35: 62 */   private CharBuf builder = CharBuf.create(4000);
/*  36:    */   
/*  37:    */   public JsonSerializerImpl()
/*  38:    */   {
/*  39: 66 */     this.instanceSerializer = new InstanceSerializerImpl();
/*  40: 67 */     this.objectSerializer = new BasicObjectSerializerImpl(false, true);
/*  41: 68 */     this.stringSerializer = new StringSerializerImpl(true, false);
/*  42: 69 */     this.mapSerializer = new MapSerializerImpl(false);
/*  43: 70 */     this.fieldSerializer = new FieldSerializerImpl();
/*  44: 71 */     this.collectionSerializer = new CollectionSerializerImpl();
/*  45: 72 */     this.arraySerializer = ((ArraySerializer)this.collectionSerializer);
/*  46: 73 */     this.unknownSerializer = new UnknownSerializerImpl();
/*  47: 74 */     this.dateSerializer = new DateSerializerImpl();
/*  48: 75 */     this.fieldsAccessor = new FieldsAccessorFieldThenProp(true);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public JsonSerializerImpl(ObjectSerializer objectSerializer, StringSerializer stringSerializer, MapSerializer mapSerializer, FieldSerializer fieldSerializer, InstanceSerializer instanceSerializer, CollectionSerializer collectionSerializer, ArraySerializer arraySerializer, UnknownSerializer unknownSerializer, DateSerializer dateSerializer, FieldsAccessor fieldsAccessor, boolean asAscii)
/*  52:    */   {
/*  53: 96 */     if (fieldsAccessor == null) {
/*  54: 97 */       this.fieldsAccessor = new FieldsAccessorFieldThenProp(true);
/*  55:    */     } else {
/*  56: 99 */       this.fieldsAccessor = fieldsAccessor;
/*  57:    */     }
/*  58:103 */     if (dateSerializer == null) {
/*  59:104 */       this.dateSerializer = new DateSerializerImpl();
/*  60:    */     } else {
/*  61:106 */       this.dateSerializer = dateSerializer;
/*  62:    */     }
/*  63:109 */     if (unknownSerializer == null) {
/*  64:110 */       this.unknownSerializer = new UnknownSerializerImpl();
/*  65:    */     } else {
/*  66:112 */       this.unknownSerializer = unknownSerializer;
/*  67:    */     }
/*  68:116 */     if (arraySerializer == null) {
/*  69:117 */       this.arraySerializer = new CollectionSerializerImpl();
/*  70:    */     } else {
/*  71:119 */       this.arraySerializer = arraySerializer;
/*  72:    */     }
/*  73:122 */     if (collectionSerializer == null) {
/*  74:123 */       this.collectionSerializer = new CollectionSerializerImpl();
/*  75:    */     } else {
/*  76:125 */       this.collectionSerializer = collectionSerializer;
/*  77:    */     }
/*  78:129 */     if (instanceSerializer == null) {
/*  79:130 */       this.instanceSerializer = new InstanceSerializerImpl();
/*  80:    */     } else {
/*  81:132 */       this.instanceSerializer = instanceSerializer;
/*  82:    */     }
/*  83:135 */     if (objectSerializer == null) {
/*  84:136 */       this.objectSerializer = new BasicObjectSerializerImpl(false, true);
/*  85:    */     } else {
/*  86:138 */       this.objectSerializer = objectSerializer;
/*  87:    */     }
/*  88:141 */     if (stringSerializer == null) {
/*  89:142 */       this.stringSerializer = new StringSerializerImpl(true, asAscii);
/*  90:    */     } else {
/*  91:144 */       this.stringSerializer = stringSerializer;
/*  92:    */     }
/*  93:147 */     if (mapSerializer == null) {
/*  94:148 */       this.mapSerializer = new MapSerializerImpl(false);
/*  95:    */     } else {
/*  96:150 */       this.mapSerializer = mapSerializer;
/*  97:    */     }
/*  98:153 */     if (fieldSerializer == null) {
/*  99:154 */       this.fieldSerializer = new FieldSerializerImpl();
/* 100:    */     } else {
/* 101:156 */       this.fieldSerializer = fieldSerializer;
/* 102:    */     }
/* 103:    */   }
/* 104:    */   
/* 105:    */   public final CharBuf serialize(Object obj)
/* 106:    */   {
/* 107:169 */     this.builder.readForRecycle();
/* 108:    */     try
/* 109:    */     {
/* 110:171 */       serializeObject(obj, this.builder);
/* 111:    */     }
/* 112:    */     catch (Exception ex)
/* 113:    */     {
/* 114:173 */       return (CharBuf)Exceptions.handle(CharBuf.class, "unable to serializeObject", ex);
/* 115:    */     }
/* 116:175 */     return this.builder;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public final boolean serializeField(Object parent, FieldAccess fieldAccess, CharBuf builder)
/* 120:    */   {
/* 121:181 */     return this.fieldSerializer.serializeField(this, parent, fieldAccess, builder);
/* 122:    */   }
/* 123:    */   
/* 124:    */   public final void serializeObject(Object obj, CharBuf builder)
/* 125:    */   {
/* 126:186 */     this.objectSerializer.serializeObject(this, obj, builder);
/* 127:    */   }
/* 128:    */   
/* 129:    */   public final void serializeString(String str, CharBuf builder)
/* 130:    */   {
/* 131:191 */     this.stringSerializer.serializeString(this, str, builder);
/* 132:    */   }
/* 133:    */   
/* 134:    */   public final void serializeMap(Map<Object, Object> map, CharBuf builder)
/* 135:    */   {
/* 136:196 */     this.mapSerializer.serializeMap(this, map, builder);
/* 137:    */   }
/* 138:    */   
/* 139:    */   public final void serializeCollection(Collection<?> collection, CharBuf builder)
/* 140:    */   {
/* 141:202 */     this.collectionSerializer.serializeCollection(this, collection, builder);
/* 142:    */   }
/* 143:    */   
/* 144:    */   public final void serializeArray(Object obj, CharBuf builder)
/* 145:    */   {
/* 146:208 */     this.arraySerializer.serializeArray(this, obj, builder);
/* 147:    */   }
/* 148:    */   
/* 149:    */   public final void serializeUnknown(Object obj, CharBuf builder)
/* 150:    */   {
/* 151:214 */     this.unknownSerializer.serializeUnknown(this, obj, builder);
/* 152:    */   }
/* 153:    */   
/* 154:    */   public final void serializeDate(Date date, CharBuf builder)
/* 155:    */   {
/* 156:219 */     this.dateSerializer.serializeDate(this, date, builder);
/* 157:    */   }
/* 158:    */   
/* 159:    */   public final void serializeInstance(Object obj, CharBuf builder)
/* 160:    */   {
/* 161:228 */     this.instanceSerializer.serializeInstance(this, obj, builder);
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void serializeInstance(Object obj, CharBuf builder, boolean includeTypeInfo)
/* 165:    */   {
/* 166:234 */     this.instanceSerializer.serializeInstance(this, obj, builder, includeTypeInfo);
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void serializeSubtypeInstance(Object obj, CharBuf builder)
/* 170:    */   {
/* 171:240 */     this.instanceSerializer.serializeSubtypeInstance(this, obj, builder);
/* 172:    */   }
/* 173:    */   
/* 174:    */   public final Map<String, FieldAccess> getFields(Class<? extends Object> aClass)
/* 175:    */   {
/* 176:244 */     return this.fieldsAccessor.getFields(aClass);
/* 177:    */   }
/* 178:    */   
/* 179:    */   public void serialize(CharBuf builder, Object obj)
/* 180:    */   {
/* 181:    */     try
/* 182:    */     {
/* 183:252 */       serializeObject(obj, builder);
/* 184:    */     }
/* 185:    */     catch (Exception ex)
/* 186:    */     {
/* 187:254 */       Exceptions.handle("unable to serializeObject", ex);
/* 188:    */     }
/* 189:    */   }
/* 190:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.impl.JsonSerializerImpl
 * JD-Core Version:    0.7.0.1
 */