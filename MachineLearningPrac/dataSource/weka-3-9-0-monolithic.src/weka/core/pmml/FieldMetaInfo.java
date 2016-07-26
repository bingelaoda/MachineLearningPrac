/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import org.w3c.dom.Element;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ 
/*   7:    */ public abstract class FieldMetaInfo
/*   8:    */   implements Serializable
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -6116715567129830143L;
/*  11:    */   protected String m_fieldName;
/*  12:    */   
/*  13:    */   public static class Value
/*  14:    */     implements Serializable
/*  15:    */   {
/*  16:    */     private static final long serialVersionUID = -3981030320273649739L;
/*  17:    */     protected String m_value;
/*  18:    */     protected String m_displayValue;
/*  19:    */     
/*  20:    */     public static enum Property
/*  21:    */     {
/*  22: 65 */       VALID("valid"),  INVALID("invalid"),  MISSING("missing");
/*  23:    */       
/*  24:    */       private final String m_stringVal;
/*  25:    */       
/*  26:    */       private Property(String name)
/*  27:    */       {
/*  28: 70 */         this.m_stringVal = name;
/*  29:    */       }
/*  30:    */       
/*  31:    */       public String toString()
/*  32:    */       {
/*  33: 75 */         return this.m_stringVal;
/*  34:    */       }
/*  35:    */     }
/*  36:    */     
/*  37: 79 */     protected Property m_property = Property.VALID;
/*  38:    */     
/*  39:    */     protected Value(Element value)
/*  40:    */       throws Exception
/*  41:    */     {
/*  42: 88 */       this.m_value = value.getAttribute("value");
/*  43: 89 */       String displayV = value.getAttribute("displayValue");
/*  44: 90 */       if ((displayV != null) && (displayV.length() > 0)) {
/*  45: 91 */         this.m_displayValue = displayV;
/*  46:    */       }
/*  47: 93 */       String property = value.getAttribute("property");
/*  48: 94 */       if ((property != null) && (property.length() > 0)) {
/*  49: 95 */         for (Property p : Property.values()) {
/*  50: 96 */           if (p.toString().equals(property))
/*  51:    */           {
/*  52: 97 */             this.m_property = p;
/*  53: 98 */             break;
/*  54:    */           }
/*  55:    */         }
/*  56:    */       }
/*  57:    */     }
/*  58:    */     
/*  59:    */     public String toString()
/*  60:    */     {
/*  61:106 */       String retV = this.m_value;
/*  62:107 */       if (this.m_displayValue != null) {
/*  63:108 */         retV = retV + "(" + this.m_displayValue + "): " + this.m_property.toString();
/*  64:    */       }
/*  65:110 */       return retV;
/*  66:    */     }
/*  67:    */     
/*  68:    */     public String getValue()
/*  69:    */     {
/*  70:114 */       return this.m_value;
/*  71:    */     }
/*  72:    */     
/*  73:    */     public String getDisplayValue()
/*  74:    */     {
/*  75:118 */       return this.m_displayValue;
/*  76:    */     }
/*  77:    */     
/*  78:    */     public Property getProperty()
/*  79:    */     {
/*  80:122 */       return this.m_property;
/*  81:    */     }
/*  82:    */   }
/*  83:    */   
/*  84:    */   public static class Interval
/*  85:    */     implements Serializable
/*  86:    */   {
/*  87:    */     private static final long serialVersionUID = -7339790632684638012L;
/*  88:137 */     protected double m_leftMargin = (-1.0D / 0.0D);
/*  89:140 */     protected double m_rightMargin = (1.0D / 0.0D);
/*  90:    */     
/*  91:    */     public static enum Closure
/*  92:    */     {
/*  93:146 */       OPENCLOSED("openClosed", "(", "]"),  OPENOPEN("openOpen", "(", ")"),  CLOSEDOPEN("closedOpen", "[", ")"),  CLOSEDCLOSED("closedClosed", "[", "]");
/*  94:    */       
/*  95:    */       private final String m_stringVal;
/*  96:    */       private final String m_left;
/*  97:    */       private final String m_right;
/*  98:    */       
/*  99:    */       private Closure(String name, String left, String right)
/* 100:    */       {
/* 101:154 */         this.m_stringVal = name;
/* 102:155 */         this.m_left = left;
/* 103:156 */         this.m_right = right;
/* 104:    */       }
/* 105:    */       
/* 106:    */       public String toString()
/* 107:    */       {
/* 108:161 */         return this.m_stringVal;
/* 109:    */       }
/* 110:    */       
/* 111:    */       public String toString(double leftMargin, double rightMargin)
/* 112:    */       {
/* 113:165 */         return this.m_left + leftMargin + "-" + rightMargin + this.m_right;
/* 114:    */       }
/* 115:    */     }
/* 116:    */     
/* 117:169 */     protected Closure m_closure = Closure.OPENOPEN;
/* 118:    */     
/* 119:    */     protected Interval(Element interval)
/* 120:    */       throws Exception
/* 121:    */     {
/* 122:178 */       String leftM = interval.getAttribute("leftMargin");
/* 123:    */       try
/* 124:    */       {
/* 125:180 */         this.m_leftMargin = Double.parseDouble(leftM);
/* 126:    */       }
/* 127:    */       catch (IllegalArgumentException ex)
/* 128:    */       {
/* 129:182 */         throw new Exception("[Interval] Can't parse left margin as a number");
/* 130:    */       }
/* 131:185 */       String rightM = interval.getAttribute("rightMargin");
/* 132:    */       try
/* 133:    */       {
/* 134:187 */         this.m_rightMargin = Double.parseDouble(rightM);
/* 135:    */       }
/* 136:    */       catch (IllegalArgumentException ex)
/* 137:    */       {
/* 138:189 */         throw new Exception("[Interval] Can't parse right margin as a number");
/* 139:    */       }
/* 140:192 */       String closure = interval.getAttribute("closure");
/* 141:193 */       if ((closure == null) || (closure.length() == 0)) {
/* 142:194 */         throw new Exception("[Interval] No closure specified!");
/* 143:    */       }
/* 144:196 */       for (Closure c : Closure.values()) {
/* 145:197 */         if (c.toString().equals(closure))
/* 146:    */         {
/* 147:198 */           this.m_closure = c;
/* 148:199 */           break;
/* 149:    */         }
/* 150:    */       }
/* 151:    */     }
/* 152:    */     
/* 153:    */     public boolean containsValue(double value)
/* 154:    */     {
/* 155:211 */       boolean result = false;
/* 156:213 */       switch (FieldMetaInfo.1.$SwitchMap$weka$core$pmml$FieldMetaInfo$Interval$Closure[this.m_closure.ordinal()])
/* 157:    */       {
/* 158:    */       case 1: 
/* 159:215 */         if ((value > this.m_leftMargin) && (value <= this.m_rightMargin)) {
/* 160:216 */           result = true;
/* 161:    */         }
/* 162:    */         break;
/* 163:    */       case 2: 
/* 164:220 */         if ((value > this.m_leftMargin) && (value < this.m_rightMargin)) {
/* 165:221 */           result = true;
/* 166:    */         }
/* 167:    */         break;
/* 168:    */       case 3: 
/* 169:225 */         if ((value >= this.m_leftMargin) && (value < this.m_rightMargin)) {
/* 170:226 */           result = true;
/* 171:    */         }
/* 172:    */         break;
/* 173:    */       case 4: 
/* 174:230 */         if ((value >= this.m_leftMargin) && (value <= this.m_rightMargin)) {
/* 175:231 */           result = true;
/* 176:    */         }
/* 177:    */         break;
/* 178:    */       default: 
/* 179:235 */         result = false;
/* 180:    */       }
/* 181:239 */       return result;
/* 182:    */     }
/* 183:    */     
/* 184:    */     public String toString()
/* 185:    */     {
/* 186:244 */       return this.m_closure.toString(this.m_leftMargin, this.m_rightMargin);
/* 187:    */     }
/* 188:    */   }
/* 189:    */   
/* 190:    */   public static enum Optype
/* 191:    */   {
/* 192:257 */     NONE("none"),  CONTINUOUS("continuous"),  CATEGORICAL("categorical"),  ORDINAL("ordinal");
/* 193:    */     
/* 194:    */     private final String m_stringVal;
/* 195:    */     
/* 196:    */     private Optype(String name)
/* 197:    */     {
/* 198:263 */       this.m_stringVal = name;
/* 199:    */     }
/* 200:    */     
/* 201:    */     public String toString()
/* 202:    */     {
/* 203:268 */       return this.m_stringVal;
/* 204:    */     }
/* 205:    */   }
/* 206:    */   
/* 207:273 */   protected Optype m_optype = Optype.NONE;
/* 208:    */   
/* 209:    */   public Optype getOptype()
/* 210:    */   {
/* 211:281 */     return this.m_optype;
/* 212:    */   }
/* 213:    */   
/* 214:    */   public String getFieldName()
/* 215:    */   {
/* 216:290 */     return this.m_fieldName;
/* 217:    */   }
/* 218:    */   
/* 219:    */   public FieldMetaInfo(Element field)
/* 220:    */   {
/* 221:299 */     this.m_fieldName = field.getAttribute("name");
/* 222:    */     
/* 223:301 */     String opType = field.getAttribute("optype");
/* 224:302 */     if ((opType != null) && (opType.length() > 0)) {
/* 225:303 */       for (Optype o : Optype.values()) {
/* 226:304 */         if (o.toString().equals(opType))
/* 227:    */         {
/* 228:305 */           this.m_optype = o;
/* 229:306 */           break;
/* 230:    */         }
/* 231:    */       }
/* 232:    */     }
/* 233:    */   }
/* 234:    */   
/* 235:    */   public abstract Attribute getFieldAsAttribute();
/* 236:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.FieldMetaInfo
 * JD-Core Version:    0.7.0.1
 */