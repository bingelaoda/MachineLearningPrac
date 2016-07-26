/*   1:    */ package weka.classifiers.functions.pace;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import weka.core.RevisionHandler;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ import weka.core.matrix.DoubleVector;
/*   7:    */ import weka.core.matrix.FlexibleDecimalFormat;
/*   8:    */ import weka.core.matrix.IntVector;
/*   9:    */ 
/*  10:    */ public class DiscreteFunction
/*  11:    */   implements RevisionHandler
/*  12:    */ {
/*  13:    */   protected DoubleVector points;
/*  14:    */   protected DoubleVector values;
/*  15:    */   
/*  16:    */   public DiscreteFunction()
/*  17:    */   {
/*  18: 48 */     this(null, null);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public DiscreteFunction(DoubleVector p)
/*  22:    */   {
/*  23: 57 */     this(p, null);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public DiscreteFunction(DoubleVector p, DoubleVector v)
/*  27:    */   {
/*  28: 66 */     this.points = p;
/*  29: 67 */     this.values = v;
/*  30: 68 */     formalize();
/*  31:    */   }
/*  32:    */   
/*  33:    */   private DiscreteFunction formalize()
/*  34:    */   {
/*  35: 73 */     if (this.points == null) {
/*  36: 73 */       this.points = new DoubleVector();
/*  37:    */     }
/*  38: 74 */     if (this.values == null) {
/*  39: 74 */       this.values = new DoubleVector();
/*  40:    */     }
/*  41: 76 */     if (this.points.isEmpty())
/*  42:    */     {
/*  43: 77 */       if (!this.values.isEmpty()) {
/*  44: 78 */         throw new IllegalArgumentException("sizes not match");
/*  45:    */       }
/*  46:    */     }
/*  47:    */     else
/*  48:    */     {
/*  49: 81 */       int n = this.points.size();
/*  50: 82 */       if (this.values.isEmpty()) {
/*  51: 83 */         this.values = new DoubleVector(n, 1.0D / n);
/*  52: 86 */       } else if (this.values.size() != n) {
/*  53: 87 */         throw new IllegalArgumentException("sizes not match");
/*  54:    */       }
/*  55:    */     }
/*  56: 90 */     return this;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public DiscreteFunction normalize()
/*  60:    */   {
/*  61: 98 */     if (!this.values.isEmpty())
/*  62:    */     {
/*  63: 99 */       double s = this.values.sum();
/*  64:100 */       if ((s != 0.0D) && (s != 1.0D)) {
/*  65:100 */         this.values.timesEquals(1.0D / s);
/*  66:    */       }
/*  67:    */     }
/*  68:102 */     return this;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void sort()
/*  72:    */   {
/*  73:110 */     IntVector index = this.points.sortWithIndex();
/*  74:111 */     this.values = this.values.subvector(index);
/*  75:    */   }
/*  76:    */   
/*  77:    */   public Object clone()
/*  78:    */   {
/*  79:119 */     DiscreteFunction d = new DiscreteFunction();
/*  80:120 */     d.points = ((DoubleVector)this.points.clone());
/*  81:121 */     d.values = ((DoubleVector)this.values.clone());
/*  82:122 */     return d;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public DiscreteFunction unique()
/*  86:    */   {
/*  87:130 */     int count = 0;
/*  88:132 */     if (size() < 2) {
/*  89:132 */       return this;
/*  90:    */     }
/*  91:133 */     for (int i = 1; i <= size() - 1; i++) {
/*  92:134 */       if (this.points.get(count) != this.points.get(i))
/*  93:    */       {
/*  94:135 */         count++;
/*  95:136 */         this.points.set(count, this.points.get(i));
/*  96:137 */         this.values.set(count, this.values.get(i));
/*  97:    */       }
/*  98:    */       else
/*  99:    */       {
/* 100:140 */         this.values.set(count, this.values.get(count) + this.values.get(i));
/* 101:    */       }
/* 102:    */     }
/* 103:143 */     this.points = this.points.subvector(0, count);
/* 104:144 */     this.values = this.values.subvector(0, count);
/* 105:145 */     return this;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public int size()
/* 109:    */   {
/* 110:153 */     if (this.points == null) {
/* 111:153 */       return 0;
/* 112:    */     }
/* 113:154 */     return this.points.size();
/* 114:    */   }
/* 115:    */   
/* 116:    */   public double getPointValue(int i)
/* 117:    */   {
/* 118:163 */     return this.points.get(i);
/* 119:    */   }
/* 120:    */   
/* 121:    */   public double getFunctionValue(int i)
/* 122:    */   {
/* 123:172 */     return this.values.get(i);
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void setPointValue(int i, double p)
/* 127:    */   {
/* 128:181 */     this.points.set(i, p);
/* 129:    */   }
/* 130:    */   
/* 131:    */   public void setFunctionValue(int i, double v)
/* 132:    */   {
/* 133:190 */     this.values.set(i, v);
/* 134:    */   }
/* 135:    */   
/* 136:    */   protected DoubleVector getPointValues()
/* 137:    */   {
/* 138:198 */     return this.points;
/* 139:    */   }
/* 140:    */   
/* 141:    */   protected DoubleVector getFunctionValues()
/* 142:    */   {
/* 143:206 */     return this.values;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public boolean isEmpty()
/* 147:    */   {
/* 148:214 */     if (size() == 0) {
/* 149:214 */       return true;
/* 150:    */     }
/* 151:215 */     return false;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public DiscreteFunction plus(DiscreteFunction d)
/* 155:    */   {
/* 156:230 */     return ((DiscreteFunction)clone()).plusEquals(d);
/* 157:    */   }
/* 158:    */   
/* 159:    */   public DiscreteFunction plusEquals(DiscreteFunction d)
/* 160:    */   {
/* 161:240 */     this.points = this.points.cat(d.points);
/* 162:241 */     this.values = this.values.cat(d.values);
/* 163:242 */     return this;
/* 164:    */   }
/* 165:    */   
/* 166:    */   public DiscreteFunction timesEquals(double x)
/* 167:    */   {
/* 168:251 */     this.values.timesEquals(x);
/* 169:252 */     return this;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public String toString()
/* 173:    */   {
/* 174:260 */     StringBuffer text = new StringBuffer();
/* 175:261 */     FlexibleDecimalFormat nf1 = new FlexibleDecimalFormat(5);
/* 176:262 */     nf1.grouping(true);
/* 177:263 */     FlexibleDecimalFormat nf2 = new FlexibleDecimalFormat(5);
/* 178:264 */     nf2.grouping(true);
/* 179:265 */     for (int i = 0; i < size(); i++)
/* 180:    */     {
/* 181:266 */       nf1.update(this.points.get(i));
/* 182:267 */       nf2.update(this.values.get(i));
/* 183:    */     }
/* 184:270 */     text.append("\t" + nf1.formatString("Points") + "\t" + nf2.formatString("Values") + "\n\n");
/* 185:272 */     for (int i = 0; i <= size() - 1; i++) {
/* 186:273 */       text.append("\t" + nf1.format(this.points.get(i)) + "\t" + nf2.format(this.values.get(i)) + "\n");
/* 187:    */     }
/* 188:277 */     return text.toString();
/* 189:    */   }
/* 190:    */   
/* 191:    */   public String getRevision()
/* 192:    */   {
/* 193:286 */     return RevisionUtils.extract("$Revision: 8109 $");
/* 194:    */   }
/* 195:    */   
/* 196:    */   public static void main(String[] args)
/* 197:    */   {
/* 198:292 */     double[] points = { 2.0D, 1.0D, 2.0D, 3.0D, 3.0D };
/* 199:293 */     double[] values = { 3.0D, 2.0D, 4.0D, 1.0D, 3.0D };
/* 200:294 */     DiscreteFunction d = new DiscreteFunction(new DoubleVector(points), new DoubleVector(values));
/* 201:    */     
/* 202:296 */     System.out.println(d);
/* 203:297 */     d.normalize();
/* 204:298 */     System.out.println("d (after normalize) = \n" + d);
/* 205:299 */     points[1] = 10.0D;
/* 206:300 */     System.out.println("d (after setting [1]) = \n" + d);
/* 207:301 */     d.sort();
/* 208:302 */     System.out.println("d (after sorting) = \n" + d);
/* 209:303 */     d.unique();
/* 210:304 */     System.out.println("d (after unique) = \n" + d);
/* 211:    */   }
/* 212:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.pace.DiscreteFunction
 * JD-Core Version:    0.7.0.1
 */