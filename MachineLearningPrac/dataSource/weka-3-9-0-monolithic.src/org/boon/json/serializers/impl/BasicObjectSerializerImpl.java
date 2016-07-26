/*   1:    */ package org.boon.json.serializers.impl;
/*   2:    */ 
/*   3:    */ import java.math.BigDecimal;
/*   4:    */ import java.math.BigInteger;
/*   5:    */ import java.util.Collection;
/*   6:    */ import java.util.Currency;
/*   7:    */ import java.util.Date;
/*   8:    */ import java.util.Map;
/*   9:    */ import java.util.TimeZone;
/*  10:    */ import org.boon.core.TypeType;
/*  11:    */ import org.boon.json.serializers.JsonSerializerInternal;
/*  12:    */ import org.boon.json.serializers.ObjectSerializer;
/*  13:    */ import org.boon.primitive.CharBuf;
/*  14:    */ 
/*  15:    */ public class BasicObjectSerializerImpl
/*  16:    */   implements ObjectSerializer
/*  17:    */ {
/*  18:    */   private final boolean includeNulls;
/*  19:    */   private final boolean includeTypeInfo;
/*  20:    */   
/*  21:    */   public BasicObjectSerializerImpl(boolean includeNulls, boolean includeTypeInfo)
/*  22:    */   {
/*  23: 52 */     this.includeNulls = includeNulls;
/*  24: 53 */     this.includeTypeInfo = includeTypeInfo;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public final void serializeObject(JsonSerializerInternal jsonSerializer, Object obj, CharBuf builder)
/*  28:    */   {
/*  29: 60 */     TypeType type = TypeType.getInstanceType(obj);
/*  30: 62 */     switch (1.$SwitchMap$org$boon$core$TypeType[type.ordinal()])
/*  31:    */     {
/*  32:    */     case 1: 
/*  33: 65 */       if (this.includeNulls) {
/*  34: 65 */         builder.addNull();
/*  35:    */       }
/*  36: 66 */       return;
/*  37:    */     case 2: 
/*  38: 68 */       builder.addInt((Integer)Integer.TYPE.cast(obj));
/*  39: 69 */       return;
/*  40:    */     case 3: 
/*  41: 71 */       builder.addBoolean(((Boolean)Boolean.TYPE.cast(obj)).booleanValue());
/*  42: 72 */       return;
/*  43:    */     case 4: 
/*  44: 74 */       builder.addByte(((Byte)Byte.TYPE.cast(obj)).byteValue());
/*  45: 75 */       return;
/*  46:    */     case 5: 
/*  47: 77 */       builder.addLong((Long)Long.TYPE.cast(obj));
/*  48: 78 */       return;
/*  49:    */     case 6: 
/*  50: 80 */       builder.addDouble((Double)Double.TYPE.cast(obj));
/*  51: 81 */       return;
/*  52:    */     case 7: 
/*  53: 83 */       builder.addFloat((Float)Float.TYPE.cast(obj));
/*  54: 84 */       return;
/*  55:    */     case 8: 
/*  56: 86 */       builder.addShort(((Short)Short.TYPE.cast(obj)).shortValue());
/*  57: 87 */       return;
/*  58:    */     case 9: 
/*  59: 89 */       builder.addChar(((Character)Character.TYPE.cast(obj)).charValue());
/*  60: 90 */       return;
/*  61:    */     case 10: 
/*  62: 92 */       builder.addBigDecimal((BigDecimal)obj);
/*  63: 93 */       return;
/*  64:    */     case 11: 
/*  65: 95 */       builder.addBigInteger((BigInteger)obj);
/*  66: 96 */       return;
/*  67:    */     case 12: 
/*  68: 98 */       jsonSerializer.serializeDate((Date)obj, builder);
/*  69: 99 */       return;
/*  70:    */     case 13: 
/*  71:101 */       jsonSerializer.serializeString((String)obj, builder);
/*  72:102 */       return;
/*  73:    */     case 14: 
/*  74:104 */       builder.addQuoted(((Class)obj).getName());
/*  75:105 */       return;
/*  76:    */     case 15: 
/*  77:108 */       TimeZone zone = (TimeZone)obj;
/*  78:    */       
/*  79:110 */       builder.addQuoted(zone.getID());
/*  80:111 */       return;
/*  81:    */     case 16: 
/*  82:115 */       jsonSerializer.serializeString(obj.toString(), builder);
/*  83:116 */       return;
/*  84:    */     case 17: 
/*  85:118 */       builder.addBoolean(((Boolean)obj).booleanValue());
/*  86:119 */       return;
/*  87:    */     case 18: 
/*  88:121 */       builder.addInt((Integer)obj);
/*  89:122 */       return;
/*  90:    */     case 19: 
/*  91:124 */       builder.addLong((Long)obj);
/*  92:125 */       return;
/*  93:    */     case 20: 
/*  94:127 */       builder.addFloat((Float)obj);
/*  95:128 */       return;
/*  96:    */     case 21: 
/*  97:130 */       builder.addDouble((Double)obj);
/*  98:131 */       return;
/*  99:    */     case 22: 
/* 100:133 */       builder.addShort(((Short)obj).shortValue());
/* 101:134 */       return;
/* 102:    */     case 23: 
/* 103:136 */       builder.addByte(((Byte)obj).byteValue());
/* 104:137 */       return;
/* 105:    */     case 24: 
/* 106:139 */       builder.addChar(((Character)obj).charValue());
/* 107:140 */       return;
/* 108:    */     case 25: 
/* 109:142 */       builder.addQuoted(obj.toString());
/* 110:143 */       return;
/* 111:    */     case 26: 
/* 112:    */     case 27: 
/* 113:    */     case 28: 
/* 114:148 */       jsonSerializer.serializeCollection((Collection)obj, builder);
/* 115:149 */       return;
/* 116:    */     case 29: 
/* 117:151 */       jsonSerializer.serializeMap((Map)obj, builder);
/* 118:152 */       return;
/* 119:    */     case 30: 
/* 120:    */     case 31: 
/* 121:    */     case 32: 
/* 122:    */     case 33: 
/* 123:    */     case 34: 
/* 124:    */     case 35: 
/* 125:    */     case 36: 
/* 126:    */     case 37: 
/* 127:    */     case 38: 
/* 128:163 */       jsonSerializer.serializeArray(obj, builder);
/* 129:164 */       return;
/* 130:    */     case 39: 
/* 131:166 */       jsonSerializer.serializeInstance(obj, builder, this.includeTypeInfo);
/* 132:167 */       return;
/* 133:    */     case 40: 
/* 134:169 */       builder.addCurrency((Currency)obj);
/* 135:170 */       return;
/* 136:    */     }
/* 137:172 */     jsonSerializer.serializeUnknown(obj, builder);
/* 138:    */   }
/* 139:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.impl.BasicObjectSerializerImpl
 * JD-Core Version:    0.7.0.1
 */