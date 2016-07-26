/*   1:    */ package org.boon.core.reflection.fields;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Field;
/*   4:    */ import java.lang.reflect.Method;
/*   5:    */ import org.boon.Boon;
/*   6:    */ import org.boon.Exceptions;
/*   7:    */ 
/*   8:    */ public class PropertyField
/*   9:    */   extends BaseField
/*  10:    */ {
/*  11:    */   final Method getter;
/*  12:    */   final Method setter;
/*  13:    */   
/*  14:    */   public PropertyField(String name, Method setter, Method getter)
/*  15:    */   {
/*  16: 45 */     super(name, getter, setter);
/*  17:    */     
/*  18: 47 */     this.getter = getter;
/*  19: 48 */     this.setter = setter;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public Object getObject(Object obj)
/*  23:    */   {
/*  24:    */     try
/*  25:    */     {
/*  26: 98 */       return this.getter.invoke(obj, new Object[0]);
/*  27:    */     }
/*  28:    */     catch (Throwable e)
/*  29:    */     {
/*  30:100 */       return Exceptions.handle(Object.class, Boon.sputs(new Object[] { "unable to call getObject for property ", this.name, "for class ", this.type }), e);
/*  31:    */     }
/*  32:    */   }
/*  33:    */   
/*  34:    */   public final void setObject(Object obj, Object value)
/*  35:    */   {
/*  36:    */     try
/*  37:    */     {
/*  38:111 */       if (!isReadOnly()) {
/*  39:112 */         this.setter.invoke(obj, new Object[] { value });
/*  40:    */       }
/*  41:    */     }
/*  42:    */     catch (Throwable e)
/*  43:    */     {
/*  44:114 */       Exceptions.handle(String.format("You tried to modify property %s of %s for instance %s with set %s using %s, and this property read only status is %s", new Object[] { this.name, obj.getClass().getSimpleName(), obj, value, name(), Boolean.valueOf(isReadOnly()) }), e);
/*  45:    */     }
/*  46:    */   }
/*  47:    */   
/*  48:    */   public final boolean getBoolean(Object obj)
/*  49:    */   {
/*  50:    */     try
/*  51:    */     {
/*  52:126 */       return ((Boolean)getObject(obj)).booleanValue();
/*  53:    */     }
/*  54:    */     catch (Exception e)
/*  55:    */     {
/*  56:128 */       return ((Boolean)Exceptions.handle(Boolean.TYPE, Boon.sputs(new Object[] { "unable to call getObject for property", this.name }), e)).booleanValue();
/*  57:    */     }
/*  58:    */   }
/*  59:    */   
/*  60:    */   public final int getInt(Object obj)
/*  61:    */   {
/*  62:    */     try
/*  63:    */     {
/*  64:136 */       return ((Integer)getObject(obj)).intValue();
/*  65:    */     }
/*  66:    */     catch (Exception e)
/*  67:    */     {
/*  68:138 */       return ((Integer)Exceptions.handle(Integer.TYPE, Boon.sputs(new Object[] { "unable to call getObject for property", this.name }), e)).intValue();
/*  69:    */     }
/*  70:    */   }
/*  71:    */   
/*  72:    */   public final short getShort(Object obj)
/*  73:    */   {
/*  74:    */     try
/*  75:    */     {
/*  76:145 */       return ((Short)getObject(obj)).shortValue();
/*  77:    */     }
/*  78:    */     catch (Exception e)
/*  79:    */     {
/*  80:147 */       return ((Short)Exceptions.handle(Short.TYPE, Boon.sputs(new Object[] { "unable to call getObject for property", this.name }), e)).shortValue();
/*  81:    */     }
/*  82:    */   }
/*  83:    */   
/*  84:    */   public final char getChar(Object obj)
/*  85:    */   {
/*  86:    */     try
/*  87:    */     {
/*  88:154 */       return ((Character)getObject(obj)).charValue();
/*  89:    */     }
/*  90:    */     catch (Exception e)
/*  91:    */     {
/*  92:156 */       return ((Character)Exceptions.handle(Character.TYPE, Boon.sputs(new Object[] { "unable to call getObject for property", this.name }), e)).charValue();
/*  93:    */     }
/*  94:    */   }
/*  95:    */   
/*  96:    */   public final long getLong(Object obj)
/*  97:    */   {
/*  98:    */     try
/*  99:    */     {
/* 100:163 */       return ((Long)getObject(obj)).longValue();
/* 101:    */     }
/* 102:    */     catch (Exception e)
/* 103:    */     {
/* 104:165 */       throw new RuntimeException(e);
/* 105:    */     }
/* 106:    */   }
/* 107:    */   
/* 108:    */   public final double getDouble(Object obj)
/* 109:    */   {
/* 110:    */     try
/* 111:    */     {
/* 112:172 */       return ((Double)getObject(obj)).doubleValue();
/* 113:    */     }
/* 114:    */     catch (Exception e)
/* 115:    */     {
/* 116:174 */       throw new RuntimeException(e);
/* 117:    */     }
/* 118:    */   }
/* 119:    */   
/* 120:    */   public final float getFloat(Object obj)
/* 121:    */   {
/* 122:    */     try
/* 123:    */     {
/* 124:182 */       return ((Float)getObject(obj)).floatValue();
/* 125:    */     }
/* 126:    */     catch (Exception e)
/* 127:    */     {
/* 128:184 */       throw new RuntimeException(e);
/* 129:    */     }
/* 130:    */   }
/* 131:    */   
/* 132:    */   public final byte getByte(Object obj)
/* 133:    */   {
/* 134:    */     try
/* 135:    */     {
/* 136:191 */       return ((Byte)getObject(obj)).byteValue();
/* 137:    */     }
/* 138:    */     catch (Exception e)
/* 139:    */     {
/* 140:193 */       throw new RuntimeException(e);
/* 141:    */     }
/* 142:    */   }
/* 143:    */   
/* 144:    */   public final Field getField()
/* 145:    */   {
/* 146:200 */     return null;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public void setStaticValue(Object newValue) {}
/* 150:    */   
/* 151:    */   public final void setBoolean(Object obj, boolean value)
/* 152:    */   {
/* 153:    */     try
/* 154:    */     {
/* 155:212 */       setObject(obj, Boolean.valueOf(value));
/* 156:    */     }
/* 157:    */     catch (Exception e)
/* 158:    */     {
/* 159:214 */       throw new RuntimeException(e);
/* 160:    */     }
/* 161:    */   }
/* 162:    */   
/* 163:    */   public final void setInt(Object obj, int value)
/* 164:    */   {
/* 165:    */     try
/* 166:    */     {
/* 167:222 */       setObject(obj, Integer.valueOf(value));
/* 168:    */     }
/* 169:    */     catch (Exception e)
/* 170:    */     {
/* 171:224 */       throw new RuntimeException(e);
/* 172:    */     }
/* 173:    */   }
/* 174:    */   
/* 175:    */   public final void setShort(Object obj, short value)
/* 176:    */   {
/* 177:    */     try
/* 178:    */     {
/* 179:232 */       setObject(obj, Short.valueOf(value));
/* 180:    */     }
/* 181:    */     catch (Exception e)
/* 182:    */     {
/* 183:234 */       throw new RuntimeException(e);
/* 184:    */     }
/* 185:    */   }
/* 186:    */   
/* 187:    */   public final void setChar(Object obj, char value)
/* 188:    */   {
/* 189:    */     try
/* 190:    */     {
/* 191:242 */       setObject(obj, Character.valueOf(value));
/* 192:    */     }
/* 193:    */     catch (Exception e)
/* 194:    */     {
/* 195:244 */       throw new RuntimeException(e);
/* 196:    */     }
/* 197:    */   }
/* 198:    */   
/* 199:    */   public final void setLong(Object obj, long value)
/* 200:    */   {
/* 201:    */     try
/* 202:    */     {
/* 203:252 */       setObject(obj, Long.valueOf(value));
/* 204:    */     }
/* 205:    */     catch (Exception e)
/* 206:    */     {
/* 207:254 */       throw new RuntimeException(e);
/* 208:    */     }
/* 209:    */   }
/* 210:    */   
/* 211:    */   public final void setDouble(Object obj, double value)
/* 212:    */   {
/* 213:    */     try
/* 214:    */     {
/* 215:262 */       setObject(obj, Double.valueOf(value));
/* 216:    */     }
/* 217:    */     catch (Exception e)
/* 218:    */     {
/* 219:264 */       throw new RuntimeException(e);
/* 220:    */     }
/* 221:    */   }
/* 222:    */   
/* 223:    */   public final void setFloat(Object obj, float value)
/* 224:    */   {
/* 225:    */     try
/* 226:    */     {
/* 227:272 */       setObject(obj, Float.valueOf(value));
/* 228:    */     }
/* 229:    */     catch (Exception e)
/* 230:    */     {
/* 231:274 */       throw new RuntimeException(e);
/* 232:    */     }
/* 233:    */   }
/* 234:    */   
/* 235:    */   public final void setByte(Object obj, byte value)
/* 236:    */   {
/* 237:    */     try
/* 238:    */     {
/* 239:282 */       setObject(obj, Byte.valueOf(value));
/* 240:    */     }
/* 241:    */     catch (Exception e)
/* 242:    */     {
/* 243:284 */       throw new RuntimeException(e);
/* 244:    */     }
/* 245:    */   }
/* 246:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.fields.PropertyField
 * JD-Core Version:    0.7.0.1
 */