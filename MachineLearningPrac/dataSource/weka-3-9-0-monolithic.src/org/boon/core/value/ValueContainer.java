/*   1:    */ package org.boon.core.value;
/*   2:    */ 
/*   3:    */ import java.math.BigDecimal;
/*   4:    */ import java.math.BigInteger;
/*   5:    */ import java.util.Currency;
/*   6:    */ import java.util.Date;
/*   7:    */ import java.util.List;
/*   8:    */ import java.util.Map;
/*   9:    */ import org.boon.Boon;
/*  10:    */ import org.boon.Exceptions;
/*  11:    */ import org.boon.core.TypeType;
/*  12:    */ import org.boon.core.Value;
/*  13:    */ import org.boon.primitive.CharBuf;
/*  14:    */ 
/*  15:    */ public class ValueContainer
/*  16:    */   implements CharSequence, Value
/*  17:    */ {
/*  18: 48 */   public static final Value TRUE = new ValueContainer(TypeType.TRUE);
/*  19: 49 */   public static final Value FALSE = new ValueContainer(TypeType.FALSE);
/*  20: 50 */   public static final Value NULL = new ValueContainer(TypeType.NULL);
/*  21:    */   public Object value;
/*  22:    */   public final TypeType type;
/*  23:    */   private boolean container;
/*  24:    */   public boolean decodeStrings;
/*  25:    */   
/*  26:    */   public static Object toObject(Object o)
/*  27:    */   {
/*  28: 60 */     if ((o instanceof ValueContainer)) {
/*  29: 61 */       o = ((ValueContainer)o).toValue();
/*  30:    */     }
/*  31: 63 */     return o;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public ValueContainer(Object value, TypeType type, boolean decodeStrings)
/*  35:    */   {
/*  36: 68 */     this.value = value;
/*  37: 69 */     this.type = type;
/*  38: 70 */     this.decodeStrings = decodeStrings;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public ValueContainer(TypeType type)
/*  42:    */   {
/*  43: 74 */     this.type = type;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public ValueContainer(Map<String, Object> map)
/*  47:    */   {
/*  48: 78 */     this.value = map;
/*  49: 79 */     this.type = TypeType.MAP;
/*  50: 80 */     this.container = true;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public ValueContainer(List<Object> list)
/*  54:    */   {
/*  55: 84 */     this.value = list;
/*  56: 85 */     this.type = TypeType.LIST;
/*  57: 86 */     this.container = true;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public int intValue()
/*  61:    */   {
/*  62: 91 */     return ((Integer)Exceptions.die(Integer.TYPE, Boon.sputs(new Object[] { "intValue not supported for type ", this.type }))).intValue();
/*  63:    */   }
/*  64:    */   
/*  65:    */   public long longValue()
/*  66:    */   {
/*  67: 96 */     return ((Integer)Exceptions.die(Integer.TYPE, Boon.sputs(new Object[] { "intValue not supported for type ", this.type }))).intValue();
/*  68:    */   }
/*  69:    */   
/*  70:    */   public boolean booleanValue()
/*  71:    */   {
/*  72:103 */     switch (1.$SwitchMap$org$boon$core$TypeType[this.type.ordinal()])
/*  73:    */     {
/*  74:    */     case 1: 
/*  75:105 */       return false;
/*  76:    */     case 2: 
/*  77:107 */       return true;
/*  78:    */     }
/*  79:109 */     Exceptions.die();
/*  80:110 */     return false;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public String stringValue()
/*  84:    */   {
/*  85:117 */     if (this.type == TypeType.NULL) {
/*  86:118 */       return null;
/*  87:    */     }
/*  88:120 */     return this.type.toString();
/*  89:    */   }
/*  90:    */   
/*  91:    */   public String stringValue(CharBuf charBuf)
/*  92:    */   {
/*  93:126 */     if (this.type == TypeType.NULL) {
/*  94:127 */       return null;
/*  95:    */     }
/*  96:129 */     return this.type.toString();
/*  97:    */   }
/*  98:    */   
/*  99:    */   public String stringValueEncoded()
/* 100:    */   {
/* 101:135 */     return toString();
/* 102:    */   }
/* 103:    */   
/* 104:    */   public String toString()
/* 105:    */   {
/* 106:140 */     return this.type.toString();
/* 107:    */   }
/* 108:    */   
/* 109:    */   public Object toValue()
/* 110:    */   {
/* 111:145 */     if (this.value != null) {
/* 112:146 */       return this.value;
/* 113:    */     }
/* 114:148 */     switch (1.$SwitchMap$org$boon$core$TypeType[this.type.ordinal()])
/* 115:    */     {
/* 116:    */     case 1: 
/* 117:150 */       return this.value = Boolean.valueOf(false);
/* 118:    */     case 2: 
/* 119:153 */       return this.value = Boolean.valueOf(true);
/* 120:    */     case 3: 
/* 121:155 */       return null;
/* 122:    */     }
/* 123:157 */     Exceptions.die();
/* 124:158 */     return null;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public <T extends Enum> T toEnum(Class<T> cls)
/* 128:    */   {
/* 129:164 */     return (Enum)this.value;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public boolean isContainer()
/* 133:    */   {
/* 134:169 */     return this.container;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public void chop() {}
/* 138:    */   
/* 139:    */   public char charValue()
/* 140:    */   {
/* 141:178 */     return '\000';
/* 142:    */   }
/* 143:    */   
/* 144:    */   public TypeType type()
/* 145:    */   {
/* 146:183 */     return this.type;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public int length()
/* 150:    */   {
/* 151:188 */     return 0;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public char charAt(int index)
/* 155:    */   {
/* 156:193 */     return '0';
/* 157:    */   }
/* 158:    */   
/* 159:    */   public CharSequence subSequence(int start, int end)
/* 160:    */   {
/* 161:198 */     return "";
/* 162:    */   }
/* 163:    */   
/* 164:    */   public Date dateValue()
/* 165:    */   {
/* 166:204 */     return null;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public byte byteValue()
/* 170:    */   {
/* 171:209 */     return 0;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public short shortValue()
/* 175:    */   {
/* 176:213 */     return 0;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public BigDecimal bigDecimalValue()
/* 180:    */   {
/* 181:218 */     return null;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public BigInteger bigIntegerValue()
/* 185:    */   {
/* 186:222 */     return null;
/* 187:    */   }
/* 188:    */   
/* 189:    */   public double doubleValue()
/* 190:    */   {
/* 191:228 */     return 0.0D;
/* 192:    */   }
/* 193:    */   
/* 194:    */   public float floatValue()
/* 195:    */   {
/* 196:234 */     return 0.0F;
/* 197:    */   }
/* 198:    */   
/* 199:    */   public Currency currencyValue()
/* 200:    */   {
/* 201:239 */     return null;
/* 202:    */   }
/* 203:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.value.ValueContainer
 * JD-Core Version:    0.7.0.1
 */