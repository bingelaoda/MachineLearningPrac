/*   1:    */ package weka.core.matrix;
/*   2:    */ 
/*   3:    */ import java.text.DecimalFormat;
/*   4:    */ import java.text.FieldPosition;
/*   5:    */ import weka.core.RevisionHandler;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ 
/*   8:    */ public class FlexibleDecimalFormat
/*   9:    */   extends DecimalFormat
/*  10:    */   implements RevisionHandler
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = 110912192794064140L;
/*  13: 40 */   private DecimalFormat nf = null;
/*  14: 41 */   private int digits = 7;
/*  15: 42 */   private boolean exp = false;
/*  16: 43 */   private int intDigits = 1;
/*  17: 44 */   private int decimalDigits = 0;
/*  18: 45 */   private int expDecimalDigits = 0;
/*  19: 46 */   private int power = 2;
/*  20: 47 */   private boolean trailing = false;
/*  21: 48 */   private boolean grouping = false;
/*  22: 49 */   private boolean sign = false;
/*  23:    */   
/*  24:    */   public FlexibleDecimalFormat()
/*  25:    */   {
/*  26: 52 */     this(5);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public FlexibleDecimalFormat(int digits)
/*  30:    */   {
/*  31: 56 */     if (digits < 1) {
/*  32: 57 */       throw new IllegalArgumentException("digits < 1");
/*  33:    */     }
/*  34: 59 */     this.digits = digits;
/*  35: 60 */     this.intDigits = 1;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public FlexibleDecimalFormat(int digits, boolean trailing)
/*  39:    */   {
/*  40: 64 */     this(digits);
/*  41: 65 */     this.trailing = trailing;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public FlexibleDecimalFormat(int digits, boolean exp, boolean trailing, boolean grouping)
/*  45:    */   {
/*  46: 70 */     this.trailing = trailing;
/*  47: 71 */     this.exp = exp;
/*  48: 72 */     this.digits = digits;
/*  49: 73 */     this.grouping = grouping;
/*  50: 74 */     if (exp)
/*  51:    */     {
/*  52: 75 */       this.intDigits = 1;
/*  53: 76 */       this.decimalDigits = (digits - this.intDigits);
/*  54:    */     }
/*  55:    */     else
/*  56:    */     {
/*  57: 78 */       this.intDigits = Math.max(1, digits - this.decimalDigits);
/*  58:    */     }
/*  59:    */   }
/*  60:    */   
/*  61:    */   public FlexibleDecimalFormat(double d)
/*  62:    */   {
/*  63: 83 */     newFormat(d);
/*  64:    */   }
/*  65:    */   
/*  66:    */   private void newFormat(double d)
/*  67:    */   {
/*  68: 87 */     if (needExponentialFormat(d))
/*  69:    */     {
/*  70: 88 */       this.exp = true;
/*  71: 89 */       this.intDigits = 1;
/*  72: 90 */       this.expDecimalDigits = decimalDigits(d, true);
/*  73: 91 */       if (d < 0.0D) {
/*  74: 92 */         this.sign = true;
/*  75:    */       } else {
/*  76: 94 */         this.sign = false;
/*  77:    */       }
/*  78:    */     }
/*  79:    */     else
/*  80:    */     {
/*  81: 97 */       this.exp = false;
/*  82: 98 */       this.intDigits = Math.max(1, intDigits(d));
/*  83: 99 */       this.decimalDigits = decimalDigits(d, false);
/*  84:100 */       if (d < 0.0D) {
/*  85:101 */         this.sign = true;
/*  86:    */       } else {
/*  87:103 */         this.sign = false;
/*  88:    */       }
/*  89:    */     }
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void update(double d)
/*  93:    */   {
/*  94:109 */     if (Math.abs(intDigits(d) - 1) > 99) {
/*  95:110 */       this.power = 3;
/*  96:    */     }
/*  97:112 */     this.expDecimalDigits = Math.max(this.expDecimalDigits, decimalDigits(d, true));
/*  98:113 */     if (d < 0.0D) {
/*  99:114 */       this.sign = true;
/* 100:    */     }
/* 101:116 */     if ((needExponentialFormat(d)) || (this.exp))
/* 102:    */     {
/* 103:117 */       this.exp = true;
/* 104:    */     }
/* 105:    */     else
/* 106:    */     {
/* 107:119 */       this.intDigits = Math.max(this.intDigits, intDigits(d));
/* 108:120 */       this.decimalDigits = Math.max(this.decimalDigits, decimalDigits(d, false));
/* 109:121 */       if (d < 0.0D) {
/* 110:122 */         this.sign = true;
/* 111:    */       }
/* 112:    */     }
/* 113:    */   }
/* 114:    */   
/* 115:    */   private static int intDigits(double d)
/* 116:    */   {
/* 117:128 */     return (int)Math.floor(Math.log(Math.abs(d * 1.00000000000001D)) / Math.log(10.0D)) + 1;
/* 118:    */   }
/* 119:    */   
/* 120:    */   private int decimalDigits(double d, boolean expo)
/* 121:    */   {
/* 122:132 */     if (d == 0.0D) {
/* 123:133 */       return 0;
/* 124:    */     }
/* 125:135 */     d = Math.abs(d);
/* 126:136 */     int e = intDigits(d);
/* 127:137 */     if (expo)
/* 128:    */     {
/* 129:138 */       d /= Math.pow(10.0D, e - 1);
/* 130:139 */       e = 1;
/* 131:    */     }
/* 132:141 */     if (e >= this.digits) {
/* 133:142 */       return 0;
/* 134:    */     }
/* 135:144 */     int iD = Math.max(1, e);
/* 136:145 */     int dD = this.digits - e;
/* 137:146 */     if ((!this.trailing) && (dD > 0))
/* 138:    */     {
/* 139:147 */       FloatingPointFormat f = new FloatingPointFormat(iD + 1 + dD, dD, true);
/* 140:148 */       String dString = f.nf.format(d);
/* 141:149 */       while ((dD > 0) && 
/* 142:150 */         (dString.charAt(iD + 1 + dD - 1) == '0')) {
/* 143:151 */         dD--;
/* 144:    */       }
/* 145:    */     }
/* 146:157 */     return dD;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public boolean needExponentialFormat(double d)
/* 150:    */   {
/* 151:161 */     if (d == 0.0D) {
/* 152:162 */       return false;
/* 153:    */     }
/* 154:164 */     int e = intDigits(d);
/* 155:165 */     if ((e > this.digits + 5) || (e < -3)) {
/* 156:166 */       return true;
/* 157:    */     }
/* 158:168 */     return false;
/* 159:    */   }
/* 160:    */   
/* 161:    */   public void grouping(boolean grouping)
/* 162:    */   {
/* 163:173 */     this.grouping = grouping;
/* 164:    */   }
/* 165:    */   
/* 166:    */   private void setFormat()
/* 167:    */   {
/* 168:177 */     int dot = 1;
/* 169:178 */     if (this.decimalDigits == 0) {
/* 170:179 */       dot = 0;
/* 171:    */     }
/* 172:181 */     if (this.exp)
/* 173:    */     {
/* 174:182 */       this.nf = new ExponentialFormat(1 + this.expDecimalDigits, this.power, this.sign, (this.grouping) || (this.trailing));
/* 175:    */     }
/* 176:    */     else
/* 177:    */     {
/* 178:185 */       int s = this.sign ? 1 : 0;
/* 179:186 */       this.nf = new FloatingPointFormat(s + this.intDigits + dot + this.decimalDigits, this.decimalDigits, (this.grouping) || (this.trailing));
/* 180:    */     }
/* 181:    */   }
/* 182:    */   
/* 183:    */   private void setFormat(double d)
/* 184:    */   {
/* 185:192 */     newFormat(d);
/* 186:193 */     setFormat();
/* 187:    */   }
/* 188:    */   
/* 189:    */   public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos)
/* 190:    */   {
/* 191:199 */     if (this.grouping)
/* 192:    */     {
/* 193:200 */       if (this.nf == null) {
/* 194:201 */         setFormat();
/* 195:    */       }
/* 196:    */     }
/* 197:    */     else {
/* 198:204 */       setFormat(number);
/* 199:    */     }
/* 200:207 */     return toAppendTo.append(this.nf.format(number));
/* 201:    */   }
/* 202:    */   
/* 203:    */   public int width()
/* 204:    */   {
/* 205:212 */     if ((!this.trailing) && (!this.grouping)) {
/* 206:213 */       throw new RuntimeException("flexible width");
/* 207:    */     }
/* 208:216 */     return format(0.0D).length();
/* 209:    */   }
/* 210:    */   
/* 211:    */   public StringBuffer formatString(String str)
/* 212:    */   {
/* 213:220 */     int w = width();
/* 214:221 */     int h = (w - str.length()) / 2;
/* 215:222 */     StringBuffer text = new StringBuffer();
/* 216:223 */     for (int i = 0; i < h; i++) {
/* 217:224 */       text.append(' ');
/* 218:    */     }
/* 219:226 */     text.append(str);
/* 220:227 */     for (int i = 0; i < w - h - str.length(); i++) {
/* 221:228 */       text.append(' ');
/* 222:    */     }
/* 223:230 */     return text;
/* 224:    */   }
/* 225:    */   
/* 226:    */   public String getRevision()
/* 227:    */   {
/* 228:240 */     return RevisionUtils.extract("$Revision: 10835 $");
/* 229:    */   }
/* 230:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.matrix.FlexibleDecimalFormat
 * JD-Core Version:    0.7.0.1
 */