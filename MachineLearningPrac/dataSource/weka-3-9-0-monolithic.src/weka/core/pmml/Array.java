/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.io.StreamTokenizer;
/*   5:    */ import java.io.StringReader;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.List;
/*   8:    */ import org.w3c.dom.Element;
/*   9:    */ import org.w3c.dom.Node;
/*  10:    */ import org.w3c.dom.NodeList;
/*  11:    */ 
/*  12:    */ public class Array
/*  13:    */   implements Serializable
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = 4286234448957826177L;
/*  16:    */   
/*  17:    */   public static boolean isArray(Element arrayE)
/*  18:    */   {
/*  19: 50 */     String name = arrayE.getTagName();
/*  20: 52 */     if ((name.equals("Array")) || (name.equals("NUM-ARRAY")) || (name.equals("INT-ARRAY")) || (name.equals("REAL-ARRAY")) || (name.equals("STRING-ARRAY")) || (isSparseArray(arrayE))) {
/*  21: 55 */       return true;
/*  22:    */     }
/*  23: 57 */     return false;
/*  24:    */   }
/*  25:    */   
/*  26:    */   private static boolean isSparseArray(Element arrayE)
/*  27:    */   {
/*  28: 67 */     String name = arrayE.getTagName();
/*  29: 69 */     if ((name.equals("INT-SparseArray")) || (name.equals("REAL-SparseArray"))) {
/*  30: 70 */       return true;
/*  31:    */     }
/*  32: 73 */     return false;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public static Array create(List<Object> values, List<Integer> indices)
/*  36:    */     throws Exception
/*  37:    */   {
/*  38: 79 */     ArrayType type = null;
/*  39:    */     
/*  40: 81 */     Object first = values.get(0);
/*  41: 82 */     if (((first instanceof Double)) || ((first instanceof Float))) {
/*  42: 83 */       type = ArrayType.REAL;
/*  43: 84 */     } else if (((first instanceof Integer)) || ((first instanceof Long))) {
/*  44: 85 */       type = ArrayType.INT;
/*  45: 86 */     } else if ((first instanceof String)) {
/*  46: 87 */       type = ArrayType.STRING;
/*  47:    */     } else {
/*  48: 89 */       throw new Exception("[Array] unsupport type!");
/*  49:    */     }
/*  50: 92 */     if (indices != null)
/*  51:    */     {
/*  52: 95 */       if (indices.size() != values.size()) {
/*  53: 96 */         throw new Exception("[Array] num values is not equal to num indices!!");
/*  54:    */       }
/*  55: 99 */       if (type == ArrayType.REAL) {
/*  56:100 */         type = ArrayType.REAL_SPARSE;
/*  57:101 */       } else if (type == ArrayType.INT) {
/*  58:102 */         type = ArrayType.INT_SPARSE;
/*  59:    */       } else {
/*  60:104 */         throw new Exception("[Array] sparse arrays can only be integer, long, float or double!");
/*  61:    */       }
/*  62:108 */       return new SparseArray(type, values, indices);
/*  63:    */     }
/*  64:111 */     return new Array(type, values);
/*  65:    */   }
/*  66:    */   
/*  67:    */   public static Array create(Element arrayE)
/*  68:    */     throws Exception
/*  69:    */   {
/*  70:123 */     if (!isArray(arrayE)) {
/*  71:124 */       throw new Exception("[Array] the supplied element does not contain an array!");
/*  72:    */     }
/*  73:128 */     if (isSparseArray(arrayE)) {
/*  74:129 */       return new SparseArray(arrayE);
/*  75:    */     }
/*  76:132 */     return new Array(arrayE);
/*  77:    */   }
/*  78:    */   
/*  79:    */   public static enum ArrayType
/*  80:    */   {
/*  81:136 */     NUM("NUM-ARRAY"),  INT("INT-ARRAY"),  REAL("REAL-ARRAY"),  STRING("STRING-ARRAY"),  REAL_SPARSE("REAL-SparseArray"),  INT_SPARSE("INT-SparseArray");
/*  82:    */     
/*  83:    */     private final String m_stringVal;
/*  84:    */     
/*  85:    */     private ArrayType(String name)
/*  86:    */     {
/*  87:143 */       this.m_stringVal = name;
/*  88:    */     }
/*  89:    */     
/*  90:    */     public String toString()
/*  91:    */     {
/*  92:148 */       return this.m_stringVal;
/*  93:    */     }
/*  94:    */   }
/*  95:    */   
/*  96:153 */   protected ArrayList<String> m_values = new ArrayList();
/*  97:156 */   protected ArrayType m_type = ArrayType.NUM;
/*  98:    */   
/*  99:    */   protected void initialize(Element arrayE)
/* 100:    */     throws Exception
/* 101:    */   {
/* 102:159 */     String arrayS = arrayE.getTagName();
/* 103:162 */     if (arrayS.equals("Array"))
/* 104:    */     {
/* 105:163 */       String type = arrayE.getAttribute("type");
/* 106:164 */       if (type.equals("int")) {
/* 107:165 */         this.m_type = ArrayType.INT;
/* 108:166 */       } else if (type.equals("real")) {
/* 109:167 */         this.m_type = ArrayType.REAL;
/* 110:168 */       } else if (type.equals("string")) {
/* 111:169 */         this.m_type = ArrayType.STRING;
/* 112:    */       }
/* 113:    */     }
/* 114:    */     else
/* 115:    */     {
/* 116:172 */       for (ArrayType a : ArrayType.values()) {
/* 117:173 */         if (a.toString().equals(arrayS))
/* 118:    */         {
/* 119:174 */           this.m_type = a;
/* 120:175 */           break;
/* 121:    */         }
/* 122:    */       }
/* 123:    */     }
/* 124:180 */     String contents = arrayE.getChildNodes().item(0).getNodeValue();
/* 125:181 */     StringReader sr = new StringReader(contents);
/* 126:182 */     StreamTokenizer st = new StreamTokenizer(sr);
/* 127:183 */     st.resetSyntax();
/* 128:184 */     st.whitespaceChars(0, 32);
/* 129:185 */     st.wordChars(33, 255);
/* 130:186 */     st.whitespaceChars(32, 32);
/* 131:187 */     st.quoteChar(34);
/* 132:188 */     st.quoteChar(39);
/* 133:    */     
/* 134:    */ 
/* 135:191 */     st.nextToken();
/* 136:193 */     while ((st.ttype != -1) && (st.ttype != 10))
/* 137:    */     {
/* 138:194 */       this.m_values.add(st.sval);
/* 139:195 */       st.nextToken();
/* 140:    */     }
/* 141:    */   }
/* 142:    */   
/* 143:    */   protected Array(Element arrayE)
/* 144:    */     throws Exception
/* 145:    */   {
/* 146:206 */     initialize(arrayE);
/* 147:    */   }
/* 148:    */   
/* 149:    */   protected Array(ArrayType type, List<Object> values)
/* 150:    */   {
/* 151:216 */     this.m_values = new ArrayList();
/* 152:217 */     this.m_type = type;
/* 153:219 */     for (Object o : values) {
/* 154:220 */       this.m_values.add(o.toString());
/* 155:    */     }
/* 156:    */   }
/* 157:    */   
/* 158:    */   public ArrayType getType()
/* 159:    */   {
/* 160:230 */     return this.m_type;
/* 161:    */   }
/* 162:    */   
/* 163:    */   public boolean isSparse()
/* 164:    */   {
/* 165:239 */     return false;
/* 166:    */   }
/* 167:    */   
/* 168:    */   public int numValues()
/* 169:    */   {
/* 170:248 */     return this.m_values.size();
/* 171:    */   }
/* 172:    */   
/* 173:    */   public boolean contains(String value)
/* 174:    */   {
/* 175:258 */     return this.m_values.contains(value);
/* 176:    */   }
/* 177:    */   
/* 178:    */   public boolean contains(int value)
/* 179:    */   {
/* 180:268 */     return contains(new Integer(value).toString());
/* 181:    */   }
/* 182:    */   
/* 183:    */   public boolean contains(double value)
/* 184:    */   {
/* 185:278 */     return contains(new Double(value).toString());
/* 186:    */   }
/* 187:    */   
/* 188:    */   public boolean contains(float value)
/* 189:    */   {
/* 190:288 */     return contains(new Float(value).toString());
/* 191:    */   }
/* 192:    */   
/* 193:    */   private void checkInRange(int index)
/* 194:    */     throws Exception
/* 195:    */   {
/* 196:292 */     if ((index >= this.m_values.size()) || (index < 0)) {
/* 197:293 */       throw new IllegalArgumentException("[Array] index out of range " + index);
/* 198:    */     }
/* 199:    */   }
/* 200:    */   
/* 201:    */   public int index(int position)
/* 202:    */   {
/* 203:304 */     return position;
/* 204:    */   }
/* 205:    */   
/* 206:    */   public String value(int index)
/* 207:    */     throws Exception
/* 208:    */   {
/* 209:315 */     return actualValue(index);
/* 210:    */   }
/* 211:    */   
/* 212:    */   protected String actualValue(int index)
/* 213:    */     throws Exception
/* 214:    */   {
/* 215:326 */     checkInRange(index);
/* 216:327 */     return (String)this.m_values.get(index);
/* 217:    */   }
/* 218:    */   
/* 219:    */   public String valueString(int index)
/* 220:    */     throws Exception
/* 221:    */   {
/* 222:338 */     return value(index);
/* 223:    */   }
/* 224:    */   
/* 225:    */   public double valueDouble(int index)
/* 226:    */     throws Exception
/* 227:    */   {
/* 228:349 */     if (this.m_type == ArrayType.STRING) {
/* 229:350 */       throw new Exception("[Array] Array does not contain numbers!");
/* 230:    */     }
/* 231:352 */     return Double.parseDouble(value(index));
/* 232:    */   }
/* 233:    */   
/* 234:    */   public float valueFloat(int index)
/* 235:    */     throws Exception
/* 236:    */   {
/* 237:363 */     if (this.m_type == ArrayType.STRING) {
/* 238:364 */       throw new Exception("[Array] Array does not contain numbers!");
/* 239:    */     }
/* 240:366 */     return Float.parseFloat(value(index));
/* 241:    */   }
/* 242:    */   
/* 243:    */   public int valueInt(int index)
/* 244:    */     throws Exception
/* 245:    */   {
/* 246:377 */     if ((this.m_type != ArrayType.INT) && (this.m_type != ArrayType.INT_SPARSE)) {
/* 247:378 */       throw new Exception("[Array] Array does not contain integers!");
/* 248:    */     }
/* 249:380 */     return Integer.parseInt(value(index));
/* 250:    */   }
/* 251:    */   
/* 252:    */   public String valueSparse(int indexOfIndex)
/* 253:    */     throws Exception
/* 254:    */   {
/* 255:392 */     return actualValue(indexOfIndex);
/* 256:    */   }
/* 257:    */   
/* 258:    */   public String valueSparseString(int indexOfIndex)
/* 259:    */     throws Exception
/* 260:    */   {
/* 261:404 */     return valueSparse(indexOfIndex);
/* 262:    */   }
/* 263:    */   
/* 264:    */   public double valueSparseDouble(int indexOfIndex)
/* 265:    */     throws Exception
/* 266:    */   {
/* 267:416 */     return Double.parseDouble(actualValue(indexOfIndex));
/* 268:    */   }
/* 269:    */   
/* 270:    */   public float valueSparseFloat(int indexOfIndex)
/* 271:    */     throws Exception
/* 272:    */   {
/* 273:428 */     return Float.parseFloat(actualValue(indexOfIndex));
/* 274:    */   }
/* 275:    */   
/* 276:    */   public int valueSparseInt(int indexOfIndex)
/* 277:    */     throws Exception
/* 278:    */   {
/* 279:440 */     return Integer.parseInt(actualValue(indexOfIndex));
/* 280:    */   }
/* 281:    */   
/* 282:    */   public String toString()
/* 283:    */   {
/* 284:445 */     StringBuffer text = new StringBuffer();
/* 285:    */     
/* 286:447 */     text.append("<");
/* 287:448 */     for (int i = 0; i < this.m_values.size(); i++)
/* 288:    */     {
/* 289:449 */       text.append((String)this.m_values.get(i));
/* 290:450 */       if (i < this.m_values.size() - 1) {
/* 291:451 */         text.append(",");
/* 292:    */       }
/* 293:    */     }
/* 294:455 */     text.append(">");
/* 295:456 */     return text.toString();
/* 296:    */   }
/* 297:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.Array
 * JD-Core Version:    0.7.0.1
 */