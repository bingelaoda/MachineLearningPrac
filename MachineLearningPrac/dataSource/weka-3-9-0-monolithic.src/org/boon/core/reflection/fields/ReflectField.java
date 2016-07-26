/*   1:    */ package org.boon.core.reflection.fields;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Field;
/*   4:    */ import org.boon.Exceptions;
/*   5:    */ 
/*   6:    */ public class ReflectField
/*   7:    */   extends BaseField
/*   8:    */ {
/*   9:    */   private final Field field;
/*  10:    */   
/*  11:    */   public ReflectField(Field field)
/*  12:    */   {
/*  13: 42 */     super(field);
/*  14: 43 */     this.field = field;
/*  15:    */   }
/*  16:    */   
/*  17:    */   public Object getObject(Object obj)
/*  18:    */   {
/*  19:    */     try
/*  20:    */     {
/*  21: 51 */       return this.field.get(obj);
/*  22:    */     }
/*  23:    */     catch (Exception e)
/*  24:    */     {
/*  25: 53 */       e.printStackTrace();
/*  26: 54 */       analyzeError(e, obj);
/*  27:    */     }
/*  28: 55 */     return null;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public boolean getBoolean(Object obj)
/*  32:    */   {
/*  33:    */     try
/*  34:    */     {
/*  35: 62 */       return this.field.getBoolean(obj);
/*  36:    */     }
/*  37:    */     catch (Exception e)
/*  38:    */     {
/*  39: 64 */       analyzeError(e, obj);
/*  40:    */     }
/*  41: 65 */     return false;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public int getInt(Object obj)
/*  45:    */   {
/*  46:    */     try
/*  47:    */     {
/*  48: 73 */       return this.field.getInt(obj);
/*  49:    */     }
/*  50:    */     catch (Exception e)
/*  51:    */     {
/*  52: 75 */       analyzeError(e, obj);
/*  53:    */     }
/*  54: 76 */     return 0;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public short getShort(Object obj)
/*  58:    */   {
/*  59:    */     try
/*  60:    */     {
/*  61: 83 */       return this.field.getShort(obj);
/*  62:    */     }
/*  63:    */     catch (Exception e)
/*  64:    */     {
/*  65: 85 */       analyzeError(e, obj);
/*  66:    */     }
/*  67: 86 */     return 0;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public char getChar(Object obj)
/*  71:    */   {
/*  72:    */     try
/*  73:    */     {
/*  74: 93 */       return this.field.getChar(obj);
/*  75:    */     }
/*  76:    */     catch (Exception e)
/*  77:    */     {
/*  78: 95 */       analyzeError(e, obj);
/*  79:    */     }
/*  80: 96 */     return '\000';
/*  81:    */   }
/*  82:    */   
/*  83:    */   public long getLong(Object obj)
/*  84:    */   {
/*  85:    */     try
/*  86:    */     {
/*  87:103 */       return this.field.getLong(obj);
/*  88:    */     }
/*  89:    */     catch (Exception e)
/*  90:    */     {
/*  91:105 */       analyzeError(e, obj);
/*  92:    */     }
/*  93:106 */     return 0L;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public double getDouble(Object obj)
/*  97:    */   {
/*  98:    */     try
/*  99:    */     {
/* 100:113 */       return this.field.getDouble(obj);
/* 101:    */     }
/* 102:    */     catch (Exception e)
/* 103:    */     {
/* 104:115 */       analyzeError(e, obj);
/* 105:    */     }
/* 106:116 */     return 0.0D;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public float getFloat(Object obj)
/* 110:    */   {
/* 111:    */     try
/* 112:    */     {
/* 113:124 */       return this.field.getFloat(obj);
/* 114:    */     }
/* 115:    */     catch (Exception e)
/* 116:    */     {
/* 117:126 */       analyzeError(e, obj);
/* 118:    */     }
/* 119:127 */     return 0.0F;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public byte getByte(Object obj)
/* 123:    */   {
/* 124:    */     try
/* 125:    */     {
/* 126:134 */       return this.field.getByte(obj);
/* 127:    */     }
/* 128:    */     catch (Exception e)
/* 129:    */     {
/* 130:136 */       analyzeError(e, obj);
/* 131:    */     }
/* 132:137 */     return 0;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public boolean getStaticBoolean()
/* 136:    */   {
/* 137:143 */     return getBoolean(null);
/* 138:    */   }
/* 139:    */   
/* 140:    */   public int getStaticInt()
/* 141:    */   {
/* 142:147 */     return getInt(null);
/* 143:    */   }
/* 144:    */   
/* 145:    */   public short getStaticShort()
/* 146:    */   {
/* 147:152 */     return getShort(null);
/* 148:    */   }
/* 149:    */   
/* 150:    */   public long getStaticLong()
/* 151:    */   {
/* 152:157 */     return getLong(null);
/* 153:    */   }
/* 154:    */   
/* 155:    */   public double getStaticDouble()
/* 156:    */   {
/* 157:162 */     return getDouble(null);
/* 158:    */   }
/* 159:    */   
/* 160:    */   public float getStaticFloat()
/* 161:    */   {
/* 162:166 */     return getFloat(null);
/* 163:    */   }
/* 164:    */   
/* 165:    */   public byte getStaticByte()
/* 166:    */   {
/* 167:170 */     return getByte(null);
/* 168:    */   }
/* 169:    */   
/* 170:    */   public Object getObject()
/* 171:    */   {
/* 172:174 */     return getObject(null);
/* 173:    */   }
/* 174:    */   
/* 175:    */   public final Field getField()
/* 176:    */   {
/* 177:179 */     return this.field;
/* 178:    */   }
/* 179:    */   
/* 180:    */   public void setStaticValue(Object newValue)
/* 181:    */   {
/* 182:    */     try
/* 183:    */     {
/* 184:186 */       this.field.set(null, newValue);
/* 185:    */     }
/* 186:    */     catch (IllegalAccessException e)
/* 187:    */     {
/* 188:188 */       Exceptions.handle(e);
/* 189:    */     }
/* 190:    */   }
/* 191:    */   
/* 192:    */   public void setBoolean(Object obj, boolean value)
/* 193:    */   {
/* 194:    */     try
/* 195:    */     {
/* 196:196 */       this.field.setBoolean(obj, value);
/* 197:    */     }
/* 198:    */     catch (IllegalAccessException e)
/* 199:    */     {
/* 200:198 */       analyzeError(e, obj);
/* 201:    */     }
/* 202:    */   }
/* 203:    */   
/* 204:    */   public void setInt(Object obj, int value)
/* 205:    */   {
/* 206:    */     try
/* 207:    */     {
/* 208:206 */       this.field.setInt(obj, value);
/* 209:    */     }
/* 210:    */     catch (IllegalAccessException e)
/* 211:    */     {
/* 212:208 */       analyzeError(e, obj);
/* 213:    */     }
/* 214:    */   }
/* 215:    */   
/* 216:    */   public void setShort(Object obj, short value)
/* 217:    */   {
/* 218:    */     try
/* 219:    */     {
/* 220:216 */       this.field.setShort(obj, value);
/* 221:    */     }
/* 222:    */     catch (IllegalAccessException e)
/* 223:    */     {
/* 224:218 */       analyzeError(e, obj);
/* 225:    */     }
/* 226:    */   }
/* 227:    */   
/* 228:    */   public void setChar(Object obj, char value)
/* 229:    */   {
/* 230:    */     try
/* 231:    */     {
/* 232:226 */       this.field.setChar(obj, value);
/* 233:    */     }
/* 234:    */     catch (IllegalAccessException e)
/* 235:    */     {
/* 236:228 */       analyzeError(e, obj);
/* 237:    */     }
/* 238:    */   }
/* 239:    */   
/* 240:    */   public void setLong(Object obj, long value)
/* 241:    */   {
/* 242:    */     try
/* 243:    */     {
/* 244:236 */       this.field.setLong(obj, value);
/* 245:    */     }
/* 246:    */     catch (IllegalAccessException e)
/* 247:    */     {
/* 248:238 */       analyzeError(e, obj);
/* 249:    */     }
/* 250:    */   }
/* 251:    */   
/* 252:    */   public void setDouble(Object obj, double value)
/* 253:    */   {
/* 254:    */     try
/* 255:    */     {
/* 256:246 */       this.field.setDouble(obj, value);
/* 257:    */     }
/* 258:    */     catch (IllegalAccessException e)
/* 259:    */     {
/* 260:248 */       analyzeError(e, obj);
/* 261:    */     }
/* 262:    */   }
/* 263:    */   
/* 264:    */   public void setFloat(Object obj, float value)
/* 265:    */   {
/* 266:    */     try
/* 267:    */     {
/* 268:256 */       this.field.setFloat(obj, value);
/* 269:    */     }
/* 270:    */     catch (IllegalAccessException e)
/* 271:    */     {
/* 272:258 */       analyzeError(e, obj);
/* 273:    */     }
/* 274:    */   }
/* 275:    */   
/* 276:    */   public void setByte(Object obj, byte value)
/* 277:    */   {
/* 278:    */     try
/* 279:    */     {
/* 280:266 */       this.field.setByte(obj, value);
/* 281:    */     }
/* 282:    */     catch (IllegalAccessException e)
/* 283:    */     {
/* 284:268 */       analyzeError(e, obj);
/* 285:    */     }
/* 286:    */   }
/* 287:    */   
/* 288:    */   public void setObject(Object obj, Object value)
/* 289:    */   {
/* 290:    */     try
/* 291:    */     {
/* 292:276 */       this.field.set(obj, value);
/* 293:    */     }
/* 294:    */     catch (IllegalAccessException e)
/* 295:    */     {
/* 296:278 */       analyzeError(e, obj);
/* 297:    */     }
/* 298:    */   }
/* 299:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.reflection.fields.ReflectField
 * JD-Core Version:    0.7.0.1
 */