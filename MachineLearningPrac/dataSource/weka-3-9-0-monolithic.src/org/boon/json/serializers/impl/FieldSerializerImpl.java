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
/*  11:    */ import org.boon.core.reflection.FastStringUtils;
/*  12:    */ import org.boon.core.reflection.fields.FieldAccess;
/*  13:    */ import org.boon.json.serializers.FieldSerializer;
/*  14:    */ import org.boon.json.serializers.JsonSerializerInternal;
/*  15:    */ import org.boon.primitive.CharBuf;
/*  16:    */ 
/*  17:    */ public class FieldSerializerImpl
/*  18:    */   implements FieldSerializer
/*  19:    */ {
/*  20:    */   private void serializeFieldName(String name, CharBuf builder)
/*  21:    */   {
/*  22: 53 */     builder.addJsonFieldName(FastStringUtils.toCharArray(name));
/*  23:    */   }
/*  24:    */   
/*  25:    */   public final boolean serializeField(JsonSerializerInternal serializer, Object parent, FieldAccess fieldAccess, CharBuf builder)
/*  26:    */   {
/*  27: 59 */     String fieldName = fieldAccess.name();
/*  28: 60 */     TypeType typeEnum = fieldAccess.typeEnum();
/*  29: 61 */     switch (1.$SwitchMap$org$boon$core$TypeType[typeEnum.ordinal()])
/*  30:    */     {
/*  31:    */     case 1: 
/*  32: 63 */       int value = fieldAccess.getInt(parent);
/*  33: 64 */       if (value != 0)
/*  34:    */       {
/*  35: 65 */         serializeFieldName(fieldName, builder);
/*  36: 66 */         builder.addInt(value);
/*  37: 67 */         return true;
/*  38:    */       }
/*  39: 69 */       return false;
/*  40:    */     case 2: 
/*  41: 71 */       boolean bvalue = fieldAccess.getBoolean(parent);
/*  42: 72 */       if (bvalue)
/*  43:    */       {
/*  44: 73 */         serializeFieldName(fieldName, builder);
/*  45: 74 */         builder.addBoolean(bvalue);
/*  46: 75 */         return true;
/*  47:    */       }
/*  48: 77 */       return false;
/*  49:    */     case 3: 
/*  50: 80 */       byte byvalue = fieldAccess.getByte(parent);
/*  51: 81 */       if (byvalue != 0)
/*  52:    */       {
/*  53: 82 */         serializeFieldName(fieldName, builder);
/*  54: 83 */         builder.addByte(byvalue);
/*  55: 84 */         return true;
/*  56:    */       }
/*  57: 86 */       return false;
/*  58:    */     case 4: 
/*  59: 88 */       long lvalue = fieldAccess.getLong(parent);
/*  60: 89 */       if (lvalue != 0L)
/*  61:    */       {
/*  62: 90 */         serializeFieldName(fieldName, builder);
/*  63: 91 */         builder.addLong(lvalue);
/*  64: 92 */         return true;
/*  65:    */       }
/*  66: 94 */       return false;
/*  67:    */     case 5: 
/*  68: 96 */       double dvalue = fieldAccess.getDouble(parent);
/*  69: 97 */       if (dvalue != 0.0D)
/*  70:    */       {
/*  71: 98 */         serializeFieldName(fieldName, builder);
/*  72: 99 */         builder.addDouble(dvalue);
/*  73:100 */         return true;
/*  74:    */       }
/*  75:102 */       return false;
/*  76:    */     case 6: 
/*  77:104 */       float fvalue = fieldAccess.getFloat(parent);
/*  78:105 */       if (fvalue != 0.0F)
/*  79:    */       {
/*  80:106 */         serializeFieldName(fieldName, builder);
/*  81:107 */         builder.addFloat(fvalue);
/*  82:108 */         return true;
/*  83:    */       }
/*  84:110 */       return false;
/*  85:    */     case 7: 
/*  86:112 */       short svalue = fieldAccess.getShort(parent);
/*  87:113 */       if (svalue != 0)
/*  88:    */       {
/*  89:114 */         serializeFieldName(fieldName, builder);
/*  90:115 */         builder.addShort(svalue);
/*  91:116 */         return true;
/*  92:    */       }
/*  93:118 */       return false;
/*  94:    */     case 8: 
/*  95:120 */       char cvalue = fieldAccess.getChar(parent);
/*  96:121 */       if (cvalue != 0)
/*  97:    */       {
/*  98:122 */         serializeFieldName(fieldName, builder);
/*  99:123 */         builder.addChar(cvalue);
/* 100:124 */         return true;
/* 101:    */       }
/* 102:126 */       return false;
/* 103:    */     }
/* 104:130 */     Object value = fieldAccess.getObject(parent);
/* 105:132 */     if (value == null) {
/* 106:133 */       return false;
/* 107:    */     }
/* 108:137 */     if (value == parent) {
/* 109:138 */       return false;
/* 110:    */     }
/* 111:141 */     switch (1.$SwitchMap$org$boon$core$TypeType[typeEnum.ordinal()])
/* 112:    */     {
/* 113:    */     case 9: 
/* 114:143 */       serializeFieldName(fieldName, builder);
/* 115:144 */       builder.addBigDecimal((BigDecimal)value);
/* 116:145 */       return true;
/* 117:    */     case 10: 
/* 118:147 */       serializeFieldName(fieldName, builder);
/* 119:148 */       builder.addBigInteger((BigInteger)value);
/* 120:149 */       return true;
/* 121:    */     case 11: 
/* 122:151 */       serializeFieldName(fieldName, builder);
/* 123:152 */       serializer.serializeDate((Date)value, builder);
/* 124:153 */       return true;
/* 125:    */     case 12: 
/* 126:155 */       serializeFieldName(fieldName, builder);
/* 127:156 */       serializer.serializeString((String)value, builder);
/* 128:157 */       return true;
/* 129:    */     case 13: 
/* 130:159 */       serializeFieldName(fieldName, builder);
/* 131:160 */       builder.addQuoted(((Class)value).getName());
/* 132:161 */       return true;
/* 133:    */     case 14: 
/* 134:165 */       serializeFieldName(fieldName, builder);
/* 135:166 */       TimeZone zone = (TimeZone)value;
/* 136:    */       
/* 137:168 */       builder.addQuoted(zone.getID());
/* 138:169 */       return true;
/* 139:    */     case 15: 
/* 140:171 */       serializeFieldName(fieldName, builder);
/* 141:172 */       serializer.serializeString(value.toString(), builder);
/* 142:173 */       return true;
/* 143:    */     case 16: 
/* 144:175 */       serializeFieldName(fieldName, builder);
/* 145:176 */       builder.addString(value.toString());
/* 146:177 */       return true;
/* 147:    */     case 17: 
/* 148:180 */       serializeFieldName(fieldName, builder);
/* 149:181 */       builder.addInt((Integer)value);
/* 150:182 */       return true;
/* 151:    */     case 18: 
/* 152:184 */       serializeFieldName(fieldName, builder);
/* 153:185 */       builder.addLong((Long)value);
/* 154:186 */       return true;
/* 155:    */     case 19: 
/* 156:188 */       serializeFieldName(fieldName, builder);
/* 157:189 */       builder.addFloat((Float)value);
/* 158:190 */       return true;
/* 159:    */     case 20: 
/* 160:192 */       serializeFieldName(fieldName, builder);
/* 161:193 */       builder.addDouble((Double)value);
/* 162:194 */       return true;
/* 163:    */     case 21: 
/* 164:196 */       serializeFieldName(fieldName, builder);
/* 165:197 */       builder.addShort(((Short)value).shortValue());
/* 166:198 */       return true;
/* 167:    */     case 22: 
/* 168:200 */       serializeFieldName(fieldName, builder);
/* 169:201 */       builder.addByte(((Byte)value).byteValue());
/* 170:202 */       return true;
/* 171:    */     case 23: 
/* 172:204 */       serializeFieldName(fieldName, builder);
/* 173:205 */       builder.addChar(((Character)value).charValue());
/* 174:206 */       return true;
/* 175:    */     case 24: 
/* 176:208 */       serializeFieldName(fieldName, builder);
/* 177:209 */       builder.addQuoted(value.toString());
/* 178:210 */       return true;
/* 179:    */     case 25: 
/* 180:    */     case 26: 
/* 181:    */     case 27: 
/* 182:214 */       Collection collection = (Collection)value;
/* 183:215 */       if (collection.size() > 0)
/* 184:    */       {
/* 185:216 */         serializeFieldName(fieldName, builder);
/* 186:217 */         serializer.serializeCollection(collection, builder);
/* 187:218 */         return true;
/* 188:    */       }
/* 189:220 */       return false;
/* 190:    */     case 28: 
/* 191:222 */       Map map = (Map)value;
/* 192:223 */       if (map.size() > 0)
/* 193:    */       {
/* 194:224 */         serializeFieldName(fieldName, builder);
/* 195:225 */         serializer.serializeMap(map, builder);
/* 196:226 */         return true;
/* 197:    */       }
/* 198:228 */       return false;
/* 199:    */     case 29: 
/* 200:    */     case 30: 
/* 201:    */     case 31: 
/* 202:    */     case 32: 
/* 203:    */     case 33: 
/* 204:    */     case 34: 
/* 205:    */     case 35: 
/* 206:    */     case 36: 
/* 207:    */     case 37: 
/* 208:240 */       Object[] array = (Object[])value;
/* 209:241 */       if (array.length > 0)
/* 210:    */       {
/* 211:242 */         serializeFieldName(fieldName, builder);
/* 212:243 */         serializer.serializeArray(value, builder);
/* 213:244 */         return true;
/* 214:    */       }
/* 215:246 */       return false;
/* 216:    */     case 38: 
/* 217:    */     case 39: 
/* 218:252 */       serializeFieldName(fieldName, builder);
/* 219:253 */       serializer.serializeSubtypeInstance(value, builder);
/* 220:254 */       return true;
/* 221:    */     case 40: 
/* 222:257 */       serializeFieldName(fieldName, builder);
/* 223:258 */       if (fieldAccess.type() == value.getClass()) {
/* 224:259 */         serializer.serializeInstance(value, builder);
/* 225:    */       } else {
/* 226:261 */         serializer.serializeSubtypeInstance(value, builder);
/* 227:    */       }
/* 228:263 */       return true;
/* 229:    */     case 41: 
/* 230:266 */       serializeFieldName(fieldName, builder);
/* 231:267 */       builder.addCurrency((Currency)value);
/* 232:268 */       return true;
/* 233:    */     }
/* 234:271 */     serializeFieldName(fieldName, builder);
/* 235:272 */     serializer.serializeUnknown(value, builder);
/* 236:273 */     return true;
/* 237:    */   }
/* 238:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.serializers.impl.FieldSerializerImpl
 * JD-Core Version:    0.7.0.1
 */