/*   1:    */ package org.boon.json.serializers.impl;
/*   2:    */ 
/*   3:    */ import java.math.BigDecimal;
/*   4:    */ import java.math.BigInteger;
/*   5:    */ import java.util.Collection;
/*   6:    */ import java.util.Currency;
/*   7:    */ import java.util.Date;
/*   8:    */ import java.util.Map;
/*   9:    */ import java.util.Set;
/*  10:    */ import java.util.TimeZone;
/*  11:    */ import org.boon.Sets;
/*  12:    */ import org.boon.core.TypeType;
/*  13:    */ import org.boon.json.serializers.CustomObjectSerializer;
/*  14:    */ import org.boon.json.serializers.JsonSerializerInternal;
/*  15:    */ import org.boon.json.serializers.ObjectSerializer;
/*  16:    */ import org.boon.primitive.CharBuf;
/*  17:    */ 
/*  18:    */ public class CustomObjectSerializerImpl
/*  19:    */   implements ObjectSerializer
/*  20:    */ {
/*  21:    */   private final Map<Class, CustomObjectSerializer> overrideMap;
/*  22: 21 */   private final Set<Class> noHandle = Sets.safeSet(Class.class);
/*  23:    */   private final boolean typeInfo;
/*  24:    */   private final boolean includeNulls;
/*  25:    */   
/*  26:    */   public CustomObjectSerializerImpl(boolean typeInfo, Map<Class, CustomObjectSerializer> overrideMap, boolean includeNulls)
/*  27:    */   {
/*  28: 29 */     this.overrideMap = overrideMap;
/*  29: 30 */     this.typeInfo = typeInfo;
/*  30: 31 */     this.includeNulls = includeNulls;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public final void serializeObject(JsonSerializerInternal jsonSerializer, Object obj, CharBuf builder)
/*  34:    */   {
/*  35: 38 */     TypeType type = TypeType.getInstanceType(obj);
/*  36: 39 */     switch (1.$SwitchMap$org$boon$core$TypeType[type.ordinal()])
/*  37:    */     {
/*  38:    */     case 1: 
/*  39: 42 */       if (this.includeNulls) {
/*  40: 42 */         builder.addNull();
/*  41:    */       }
/*  42: 43 */       return;
/*  43:    */     case 2: 
/*  44: 45 */       builder.addInt((Integer)Integer.TYPE.cast(obj));
/*  45: 46 */       return;
/*  46:    */     case 3: 
/*  47: 48 */       builder.addBoolean(((Boolean)Boolean.TYPE.cast(obj)).booleanValue());
/*  48: 49 */       return;
/*  49:    */     case 4: 
/*  50: 51 */       builder.addByte(((Byte)Byte.TYPE.cast(obj)).byteValue());
/*  51: 52 */       return;
/*  52:    */     case 5: 
/*  53: 54 */       builder.addLong((Long)Long.TYPE.cast(obj));
/*  54: 55 */       return;
/*  55:    */     case 6: 
/*  56: 57 */       builder.addDouble((Double)Double.TYPE.cast(obj));
/*  57: 58 */       return;
/*  58:    */     case 7: 
/*  59: 60 */       builder.addFloat((Float)Float.TYPE.cast(obj));
/*  60: 61 */       return;
/*  61:    */     case 8: 
/*  62: 63 */       builder.addShort(((Short)Short.TYPE.cast(obj)).shortValue());
/*  63: 64 */       return;
/*  64:    */     case 9: 
/*  65: 66 */       builder.addChar(((Character)Character.TYPE.cast(obj)).charValue());
/*  66: 67 */       return;
/*  67:    */     case 10: 
/*  68: 69 */       builder.addBigDecimal((BigDecimal)obj);
/*  69: 70 */       return;
/*  70:    */     case 11: 
/*  71: 72 */       builder.addBigInteger((BigInteger)obj);
/*  72: 73 */       return;
/*  73:    */     case 12: 
/*  74: 75 */       jsonSerializer.serializeDate((Date)obj, builder);
/*  75: 76 */       return;
/*  76:    */     case 13: 
/*  77: 78 */       jsonSerializer.serializeString((String)obj, builder);
/*  78: 79 */       return;
/*  79:    */     case 14: 
/*  80: 81 */       builder.addQuoted(((Class)obj).getName());
/*  81: 82 */       return;
/*  82:    */     case 15: 
/*  83: 85 */       TimeZone zone = (TimeZone)obj;
/*  84:    */       
/*  85: 87 */       builder.addQuoted(zone.getID());
/*  86: 88 */       return;
/*  87:    */     case 16: 
/*  88: 92 */       jsonSerializer.serializeString(obj.toString(), builder);
/*  89: 93 */       return;
/*  90:    */     case 17: 
/*  91: 95 */       builder.addBoolean(((Boolean)obj).booleanValue());
/*  92: 96 */       return;
/*  93:    */     case 18: 
/*  94: 98 */       builder.addInt((Integer)obj);
/*  95: 99 */       return;
/*  96:    */     case 19: 
/*  97:101 */       builder.addLong((Long)obj);
/*  98:102 */       return;
/*  99:    */     case 20: 
/* 100:104 */       builder.addFloat((Float)obj);
/* 101:105 */       return;
/* 102:    */     case 21: 
/* 103:107 */       builder.addDouble((Double)obj);
/* 104:108 */       return;
/* 105:    */     case 22: 
/* 106:110 */       builder.addShort(((Short)obj).shortValue());
/* 107:111 */       return;
/* 108:    */     case 23: 
/* 109:113 */       builder.addByte(((Byte)obj).byteValue());
/* 110:114 */       return;
/* 111:    */     case 24: 
/* 112:116 */       builder.addChar(((Character)obj).charValue());
/* 113:117 */       return;
/* 114:    */     case 25: 
/* 115:119 */       builder.addQuoted(obj.toString());
/* 116:120 */       return;
/* 117:    */     case 26: 
/* 118:    */     case 27: 
/* 119:    */     case 28: 
/* 120:125 */       jsonSerializer.serializeCollection((Collection)obj, builder);
/* 121:126 */       return;
/* 122:    */     case 29: 
/* 123:128 */       jsonSerializer.serializeMap((Map)obj, builder);
/* 124:129 */       return;
/* 125:    */     case 30: 
/* 126:    */     case 31: 
/* 127:    */     case 32: 
/* 128:    */     case 33: 
/* 129:    */     case 34: 
/* 130:    */     case 35: 
/* 131:    */     case 36: 
/* 132:    */     case 37: 
/* 133:    */     case 38: 
/* 134:140 */       jsonSerializer.serializeArray(obj, builder);
/* 135:141 */       return;
/* 136:    */     case 39: 
/* 137:143 */       SerializeUtils.handleInstance(jsonSerializer, obj, builder, this.overrideMap, this.noHandle, this.typeInfo, type);
/* 138:    */       
/* 139:145 */       return;
/* 140:    */     case 40: 
/* 141:147 */       builder.addCurrency((Currency)obj);
/* 142:148 */       return;
/* 143:    */     }
/* 144:150 */     jsonSerializer.serializeUnknown(obj, builder);
/* 145:    */   }
/* 146:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.impl.CustomObjectSerializerImpl
 * JD-Core Version:    0.7.0.1
 */