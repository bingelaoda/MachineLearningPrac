/*   1:    */ package org.boon.datarepo.modification;
/*   2:    */ 
/*   3:    */ public abstract class ModificationEvent<KEY, ITEM>
/*   4:    */ {
/*   5:    */   public static final String ROOT_PROPERTY = "ROOT";
/*   6:    */   private KEY key;
/*   7:    */   private ITEM item;
/*   8: 38 */   private String property = "ROOT";
/*   9:    */   private ModificationType type;
/*  10:    */   
/*  11:    */   public ModificationEvent() {}
/*  12:    */   
/*  13:    */   public ModificationEvent(KEY k, ITEM i, ModificationType t, String p)
/*  14:    */   {
/*  15: 47 */     this.key = k;
/*  16: 48 */     this.item = i;
/*  17: 49 */     this.type = t;
/*  18: 50 */     if (p != null) {
/*  19: 51 */       this.property = p;
/*  20:    */     }
/*  21:    */   }
/*  22:    */   
/*  23:    */   public ITEM getItem()
/*  24:    */   {
/*  25: 56 */     return this.item;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public KEY getKey()
/*  29:    */   {
/*  30: 60 */     return this.key;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public abstract boolean booleanValue();
/*  34:    */   
/*  35:    */   public abstract int intValue();
/*  36:    */   
/*  37:    */   public abstract short shortValue();
/*  38:    */   
/*  39:    */   public abstract char charValue();
/*  40:    */   
/*  41:    */   public abstract byte byteValue();
/*  42:    */   
/*  43:    */   public abstract long longValue();
/*  44:    */   
/*  45:    */   public abstract float floatValue();
/*  46:    */   
/*  47:    */   public abstract double doubleValue();
/*  48:    */   
/*  49:    */   public abstract Object objectValue();
/*  50:    */   
/*  51:    */   public abstract String value();
/*  52:    */   
/*  53:    */   public String toString()
/*  54:    */   {
/*  55: 86 */     return "ModificationEvent{key=" + this.key + ", item=" + this.item + ", property='" + this.property + '\'' + ", type=" + this.type + '}';
/*  56:    */   }
/*  57:    */   
/*  58:    */   static class ModficationEventImpl<KEY, ITEM>
/*  59:    */     extends ModificationEvent<KEY, ITEM>
/*  60:    */   {
/*  61:    */     public ModficationEventImpl() {}
/*  62:    */     
/*  63:    */     public ModficationEventImpl(KEY k, ITEM i, ModificationType t, String p)
/*  64:    */     {
/*  65:102 */       super(i, t, p);
/*  66:    */     }
/*  67:    */     
/*  68:    */     public boolean booleanValue()
/*  69:    */     {
/*  70:107 */       throw new UnsupportedOperationException("not supported");
/*  71:    */     }
/*  72:    */     
/*  73:    */     public int intValue()
/*  74:    */     {
/*  75:112 */       throw new UnsupportedOperationException("not supported");
/*  76:    */     }
/*  77:    */     
/*  78:    */     public short shortValue()
/*  79:    */     {
/*  80:117 */       throw new UnsupportedOperationException("not supported");
/*  81:    */     }
/*  82:    */     
/*  83:    */     public char charValue()
/*  84:    */     {
/*  85:122 */       throw new UnsupportedOperationException("not supported");
/*  86:    */     }
/*  87:    */     
/*  88:    */     public byte byteValue()
/*  89:    */     {
/*  90:127 */       throw new UnsupportedOperationException("not supported");
/*  91:    */     }
/*  92:    */     
/*  93:    */     public long longValue()
/*  94:    */     {
/*  95:132 */       throw new UnsupportedOperationException("not supported");
/*  96:    */     }
/*  97:    */     
/*  98:    */     public float floatValue()
/*  99:    */     {
/* 100:138 */       throw new UnsupportedOperationException("not supported");
/* 101:    */     }
/* 102:    */     
/* 103:    */     public double doubleValue()
/* 104:    */     {
/* 105:143 */       throw new UnsupportedOperationException("not supported");
/* 106:    */     }
/* 107:    */     
/* 108:    */     public String value()
/* 109:    */     {
/* 110:148 */       throw new UnsupportedOperationException("not supported");
/* 111:    */     }
/* 112:    */     
/* 113:    */     public Object objectValue()
/* 114:    */     {
/* 115:153 */       throw new UnsupportedOperationException("not supported");
/* 116:    */     }
/* 117:    */   }
/* 118:    */   
/* 119:    */   public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification(ModificationType type, KEY key, ITEM item, String property, final boolean value)
/* 120:    */   {
/* 121:162 */     new ModficationEventImpl(key, item, type, property)
/* 122:    */     {
/* 123:163 */       boolean v = value;
/* 124:    */       
/* 125:    */       public boolean booleanValue()
/* 126:    */       {
/* 127:166 */         return this.v;
/* 128:    */       }
/* 129:    */     };
/* 130:    */   }
/* 131:    */   
/* 132:    */   public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification(ModificationType type, KEY key, ITEM item, String property, final Object value)
/* 133:    */   {
/* 134:173 */     new ModficationEventImpl(key, item, type, property)
/* 135:    */     {
/* 136:174 */       Object v = value;
/* 137:    */       
/* 138:    */       public Object objectValue()
/* 139:    */       {
/* 140:177 */         return this.v;
/* 141:    */       }
/* 142:    */     };
/* 143:    */   }
/* 144:    */   
/* 145:    */   public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification(ModificationType type, KEY key, ITEM item, String property, final String value)
/* 146:    */   {
/* 147:184 */     new ModficationEventImpl(key, item, type, property)
/* 148:    */     {
/* 149:185 */       String v = value;
/* 150:    */       
/* 151:    */       public String value()
/* 152:    */       {
/* 153:188 */         return this.v;
/* 154:    */       }
/* 155:    */     };
/* 156:    */   }
/* 157:    */   
/* 158:    */   public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification(ModificationType type, KEY key, ITEM item, String property, final byte value)
/* 159:    */   {
/* 160:195 */     new ModficationEventImpl(key, item, type, property)
/* 161:    */     {
/* 162:196 */       byte v = value;
/* 163:    */       
/* 164:    */       public byte byteValue()
/* 165:    */       {
/* 166:199 */         return this.v;
/* 167:    */       }
/* 168:    */     };
/* 169:    */   }
/* 170:    */   
/* 171:    */   public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification(ModificationType type, KEY key, ITEM item, String property, final short value)
/* 172:    */   {
/* 173:206 */     new ModficationEventImpl(key, item, type, property)
/* 174:    */     {
/* 175:207 */       short v = value;
/* 176:    */       
/* 177:    */       public short shortValue()
/* 178:    */       {
/* 179:210 */         return this.v;
/* 180:    */       }
/* 181:    */     };
/* 182:    */   }
/* 183:    */   
/* 184:    */   public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification(ModificationType type, KEY key, ITEM item, String property, final int value)
/* 185:    */   {
/* 186:217 */     new ModficationEventImpl(key, item, type, property)
/* 187:    */     {
/* 188:218 */       int v = value;
/* 189:    */       
/* 190:    */       public int intValue()
/* 191:    */       {
/* 192:221 */         return this.v;
/* 193:    */       }
/* 194:    */     };
/* 195:    */   }
/* 196:    */   
/* 197:    */   public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification(ModificationType type, KEY key, ITEM item, String property, final long value)
/* 198:    */   {
/* 199:228 */     new ModficationEventImpl(key, item, type, property)
/* 200:    */     {
/* 201:229 */       long v = value;
/* 202:    */       
/* 203:    */       public long longValue()
/* 204:    */       {
/* 205:232 */         return this.v;
/* 206:    */       }
/* 207:    */     };
/* 208:    */   }
/* 209:    */   
/* 210:    */   public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification(ModificationType type, KEY key, ITEM item, String property, final float value)
/* 211:    */   {
/* 212:239 */     new ModficationEventImpl(key, item, type, property)
/* 213:    */     {
/* 214:240 */       float v = value;
/* 215:    */       
/* 216:    */       public float floatValue()
/* 217:    */       {
/* 218:243 */         return this.v;
/* 219:    */       }
/* 220:    */     };
/* 221:    */   }
/* 222:    */   
/* 223:    */   public static <KEY, ITEM> ModificationEvent<KEY, ITEM> createModification(ModificationType type, KEY key, ITEM item, String property, final double value)
/* 224:    */   {
/* 225:250 */     new ModficationEventImpl(key, item, type, property)
/* 226:    */     {
/* 227:251 */       double v = value;
/* 228:    */       
/* 229:    */       public double doubleValue()
/* 230:    */       {
/* 231:254 */         return this.v;
/* 232:    */       }
/* 233:    */     };
/* 234:    */   }
/* 235:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.modification.ModificationEvent
 * JD-Core Version:    0.7.0.1
 */