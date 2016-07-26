/*   1:    */ package weka.classifiers.evaluation;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import weka.classifiers.CostMatrix;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ import weka.core.Utils;
/*   7:    */ import weka.core.matrix.Matrix;
/*   8:    */ 
/*   9:    */ public class ConfusionMatrix
/*  10:    */   extends Matrix
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = -181789981401504090L;
/*  13:    */   protected String[] m_ClassNames;
/*  14:    */   
/*  15:    */   public ConfusionMatrix(String[] classNames)
/*  16:    */   {
/*  17: 53 */     super(classNames.length, classNames.length);
/*  18: 54 */     this.m_ClassNames = ((String[])classNames.clone());
/*  19:    */   }
/*  20:    */   
/*  21:    */   public ConfusionMatrix makeWeighted(CostMatrix costs)
/*  22:    */     throws Exception
/*  23:    */   {
/*  24: 69 */     if (costs.size() != size()) {
/*  25: 70 */       throw new Exception("Cost and confusion matrices must be the same size");
/*  26:    */     }
/*  27: 72 */     ConfusionMatrix weighted = new ConfusionMatrix(this.m_ClassNames);
/*  28: 73 */     for (int row = 0; row < size(); row++) {
/*  29: 74 */       for (int col = 0; col < size(); col++) {
/*  30: 75 */         weighted.set(row, col, get(row, col) * costs.getElement(row, col));
/*  31:    */       }
/*  32:    */     }
/*  33: 78 */     return weighted;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public Object clone()
/*  37:    */   {
/*  38: 89 */     ConfusionMatrix m = (ConfusionMatrix)super.clone();
/*  39: 90 */     m.m_ClassNames = ((String[])this.m_ClassNames.clone());
/*  40: 91 */     return m;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public int size()
/*  44:    */   {
/*  45:101 */     return this.m_ClassNames.length;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public String className(int index)
/*  49:    */   {
/*  50:112 */     return this.m_ClassNames[index];
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void addPrediction(NominalPrediction pred)
/*  54:    */     throws Exception
/*  55:    */   {
/*  56:123 */     if (pred.predicted() == NominalPrediction.MISSING_VALUE) {
/*  57:124 */       throw new Exception("No predicted value given.");
/*  58:    */     }
/*  59:126 */     if (pred.actual() == NominalPrediction.MISSING_VALUE) {
/*  60:127 */       throw new Exception("No actual value given.");
/*  61:    */     }
/*  62:129 */     set((int)pred.actual(), (int)pred.predicted(), get((int)pred.actual(), (int)pred.predicted()) + pred.weight());
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void addPredictions(ArrayList<Prediction> predictions)
/*  66:    */     throws Exception
/*  67:    */   {
/*  68:144 */     for (int i = 0; i < predictions.size(); i++) {
/*  69:145 */       addPrediction((NominalPrediction)predictions.get(i));
/*  70:    */     }
/*  71:    */   }
/*  72:    */   
/*  73:    */   public TwoClassStats getTwoClassStats(int classIndex)
/*  74:    */   {
/*  75:158 */     double fp = 0.0D;double tp = 0.0D;double fn = 0.0D;double tn = 0.0D;
/*  76:159 */     for (int row = 0; row < size(); row++) {
/*  77:160 */       for (int col = 0; col < size(); col++) {
/*  78:161 */         if (row == classIndex)
/*  79:    */         {
/*  80:162 */           if (col == classIndex) {
/*  81:163 */             tp += get(row, col);
/*  82:    */           } else {
/*  83:165 */             fn += get(row, col);
/*  84:    */           }
/*  85:    */         }
/*  86:168 */         else if (col == classIndex) {
/*  87:169 */           fp += get(row, col);
/*  88:    */         } else {
/*  89:171 */           tn += get(row, col);
/*  90:    */         }
/*  91:    */       }
/*  92:    */     }
/*  93:176 */     return new TwoClassStats(tp, fp, tn, fn);
/*  94:    */   }
/*  95:    */   
/*  96:    */   public double correct()
/*  97:    */   {
/*  98:188 */     double correct = 0.0D;
/*  99:189 */     for (int i = 0; i < size(); i++) {
/* 100:190 */       correct += get(i, i);
/* 101:    */     }
/* 102:192 */     return correct;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public double incorrect()
/* 106:    */   {
/* 107:204 */     double incorrect = 0.0D;
/* 108:205 */     for (int row = 0; row < size(); row++) {
/* 109:206 */       for (int col = 0; col < size(); col++) {
/* 110:207 */         if (row != col) {
/* 111:208 */           incorrect += get(row, col);
/* 112:    */         }
/* 113:    */       }
/* 114:    */     }
/* 115:212 */     return incorrect;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public double total()
/* 119:    */   {
/* 120:223 */     double total = 0.0D;
/* 121:224 */     for (int row = 0; row < size(); row++) {
/* 122:225 */       for (int col = 0; col < size(); col++) {
/* 123:226 */         total += get(row, col);
/* 124:    */       }
/* 125:    */     }
/* 126:229 */     return total;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public double errorRate()
/* 130:    */   {
/* 131:239 */     return incorrect() / total();
/* 132:    */   }
/* 133:    */   
/* 134:    */   public String toString()
/* 135:    */   {
/* 136:250 */     return toString("=== Confusion Matrix ===\n");
/* 137:    */   }
/* 138:    */   
/* 139:    */   public String toString(String title)
/* 140:    */   {
/* 141:262 */     StringBuffer text = new StringBuffer();
/* 142:263 */     char[] IDChars = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
/* 143:    */     
/* 144:    */ 
/* 145:266 */     boolean fractional = false;
/* 146:    */     
/* 147:    */ 
/* 148:    */ 
/* 149:270 */     double maxval = 0.0D;
/* 150:271 */     for (int i = 0; i < size(); i++) {
/* 151:272 */       for (int j = 0; j < size(); j++)
/* 152:    */       {
/* 153:273 */         double current = get(i, j);
/* 154:274 */         if (current < 0.0D) {
/* 155:275 */           current *= -10.0D;
/* 156:    */         }
/* 157:277 */         if (current > maxval) {
/* 158:278 */           maxval = current;
/* 159:    */         }
/* 160:280 */         double fract = current - Math.rint(current);
/* 161:281 */         if ((!fractional) && (Math.log(fract) / Math.log(10.0D) >= -2.0D)) {
/* 162:282 */           fractional = true;
/* 163:    */         }
/* 164:    */       }
/* 165:    */     }
/* 166:287 */     int IDWidth = 1 + Math.max((int)(Math.log(maxval) / Math.log(10.0D) + (fractional ? 3 : 0)), (int)(Math.log(size()) / Math.log(IDChars.length)));
/* 167:    */     
/* 168:    */ 
/* 169:290 */     text.append(title).append("\n");
/* 170:291 */     for (int i = 0; i < size(); i++) {
/* 171:292 */       if (fractional) {
/* 172:293 */         text.append(" ").append(num2ShortID(i, IDChars, IDWidth - 3)).append("   ");
/* 173:    */       } else {
/* 174:296 */         text.append(" ").append(num2ShortID(i, IDChars, IDWidth));
/* 175:    */       }
/* 176:    */     }
/* 177:299 */     text.append("     actual class\n");
/* 178:300 */     for (int i = 0; i < size(); i++)
/* 179:    */     {
/* 180:301 */       for (int j = 0; j < size(); j++) {
/* 181:302 */         text.append(" ").append(Utils.doubleToString(get(i, j), IDWidth, fractional ? 2 : 0));
/* 182:    */       }
/* 183:305 */       text.append(" | ").append(num2ShortID(i, IDChars, IDWidth)).append(" = ").append(this.m_ClassNames[i]).append("\n");
/* 184:    */     }
/* 185:308 */     return text.toString();
/* 186:    */   }
/* 187:    */   
/* 188:    */   private static String num2ShortID(int num, char[] IDChars, int IDWidth)
/* 189:    */   {
/* 190:319 */     char[] ID = new char[IDWidth];
/* 191:322 */     for (int i = IDWidth - 1; i >= 0; i--)
/* 192:    */     {
/* 193:323 */       ID[i] = IDChars[(num % IDChars.length)];
/* 194:324 */       num = num / IDChars.length - 1;
/* 195:325 */       if (num < 0) {
/* 196:    */         break;
/* 197:    */       }
/* 198:    */     }
/* 199:329 */     for (i--; i >= 0; i--) {
/* 200:330 */       ID[i] = ' ';
/* 201:    */     }
/* 202:333 */     return new String(ID);
/* 203:    */   }
/* 204:    */   
/* 205:    */   public String getRevision()
/* 206:    */   {
/* 207:343 */     return RevisionUtils.extract("$Revision: 10169 $");
/* 208:    */   }
/* 209:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.ConfusionMatrix
 * JD-Core Version:    0.7.0.1
 */