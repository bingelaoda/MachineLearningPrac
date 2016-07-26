/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import org.w3c.dom.Element;
/*   5:    */ import org.w3c.dom.Node;
/*   6:    */ import org.w3c.dom.NodeList;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Utils;
/*   9:    */ 
/*  10:    */ public class NormContinuous
/*  11:    */   extends Expression
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = 4714332374909851542L;
/*  14:    */   protected String m_fieldName;
/*  15:    */   protected int m_fieldIndex;
/*  16: 54 */   protected boolean m_mapMissingDefined = false;
/*  17:    */   protected double m_mapMissingTo;
/*  18: 60 */   protected MiningFieldMetaInfo.Outlier m_outlierTreatmentMethod = MiningFieldMetaInfo.Outlier.ASIS;
/*  19:    */   protected double[] m_linearNormOrig;
/*  20:    */   protected double[] m_linearNormNorm;
/*  21:    */   
/*  22:    */   public NormContinuous(Element normCont, FieldMetaInfo.Optype opType, ArrayList<Attribute> fieldDefs)
/*  23:    */     throws Exception
/*  24:    */   {
/*  25: 71 */     super(opType, fieldDefs);
/*  26: 73 */     if (opType != FieldMetaInfo.Optype.CONTINUOUS) {
/*  27: 74 */       throw new Exception("[NormContinuous] can only have a continuous optype");
/*  28:    */     }
/*  29: 77 */     this.m_fieldName = normCont.getAttribute("field");
/*  30:    */     
/*  31: 79 */     String mapMissing = normCont.getAttribute("mapMissingTo");
/*  32: 80 */     if ((mapMissing != null) && (mapMissing.length() > 0))
/*  33:    */     {
/*  34: 81 */       this.m_mapMissingTo = Double.parseDouble(mapMissing);
/*  35: 82 */       this.m_mapMissingDefined = true;
/*  36:    */     }
/*  37: 85 */     String outliers = normCont.getAttribute("outliers");
/*  38: 86 */     if ((outliers != null) && (outliers.length() > 0)) {
/*  39: 87 */       for (MiningFieldMetaInfo.Outlier o : MiningFieldMetaInfo.Outlier.values()) {
/*  40: 88 */         if (o.toString().equals(outliers))
/*  41:    */         {
/*  42: 89 */           this.m_outlierTreatmentMethod = o;
/*  43: 90 */           break;
/*  44:    */         }
/*  45:    */       }
/*  46:    */     }
/*  47: 96 */     NodeList lnL = normCont.getElementsByTagName("LinearNorm");
/*  48: 97 */     if (lnL.getLength() < 2) {
/*  49: 98 */       throw new Exception("[NormContinuous] Must be at least 2 LinearNorm elements!");
/*  50:    */     }
/*  51:100 */     this.m_linearNormOrig = new double[lnL.getLength()];
/*  52:101 */     this.m_linearNormNorm = new double[lnL.getLength()];
/*  53:103 */     for (int i = 0; i < lnL.getLength(); i++)
/*  54:    */     {
/*  55:104 */       Node lnN = lnL.item(i);
/*  56:105 */       if (lnN.getNodeType() == 1)
/*  57:    */       {
/*  58:106 */         Element lnE = (Element)lnN;
/*  59:    */         
/*  60:108 */         String orig = lnE.getAttribute("orig");
/*  61:109 */         this.m_linearNormOrig[i] = Double.parseDouble(orig);
/*  62:    */         
/*  63:111 */         String norm = lnE.getAttribute("norm");
/*  64:112 */         this.m_linearNormNorm[i] = Double.parseDouble(norm);
/*  65:    */       }
/*  66:    */     }
/*  67:116 */     if (fieldDefs != null) {
/*  68:117 */       setUpField();
/*  69:    */     }
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setFieldDefs(ArrayList<Attribute> fieldDefs)
/*  73:    */     throws Exception
/*  74:    */   {
/*  75:128 */     super.setFieldDefs(fieldDefs);
/*  76:129 */     setUpField();
/*  77:    */   }
/*  78:    */   
/*  79:    */   private void setUpField()
/*  80:    */     throws Exception
/*  81:    */   {
/*  82:133 */     this.m_fieldIndex = -1;
/*  83:135 */     if (this.m_fieldDefs != null)
/*  84:    */     {
/*  85:136 */       this.m_fieldIndex = getFieldDefIndex(this.m_fieldName);
/*  86:138 */       if (this.m_fieldIndex < 0) {
/*  87:139 */         throw new Exception("[NormContinuous] Can't find field " + this.m_fieldName + " in the supplied field definitions.");
/*  88:    */       }
/*  89:143 */       Attribute field = (Attribute)this.m_fieldDefs.get(this.m_fieldIndex);
/*  90:144 */       if (!field.isNumeric()) {
/*  91:145 */         throw new Exception("[NormContinuous] reference field " + this.m_fieldName + " must be continuous.");
/*  92:    */       }
/*  93:    */     }
/*  94:    */   }
/*  95:    */   
/*  96:    */   protected Attribute getOutputDef()
/*  97:    */   {
/*  98:159 */     return new Attribute(this.m_fieldName + "_normContinuous");
/*  99:    */   }
/* 100:    */   
/* 101:    */   public double getResult(double[] incoming)
/* 102:    */     throws Exception
/* 103:    */   {
/* 104:174 */     double[] a = this.m_linearNormOrig;
/* 105:175 */     double[] b = this.m_linearNormNorm;
/* 106:    */     
/* 107:177 */     return computeNorm(a, b, incoming);
/* 108:    */   }
/* 109:    */   
/* 110:    */   public double getResultInverse(double[] incoming)
/* 111:    */   {
/* 112:187 */     double[] a = this.m_linearNormNorm;
/* 113:188 */     double[] b = this.m_linearNormOrig;
/* 114:    */     
/* 115:190 */     return computeNorm(a, b, incoming);
/* 116:    */   }
/* 117:    */   
/* 118:    */   private double computeNorm(double[] a, double[] b, double[] incoming)
/* 119:    */   {
/* 120:194 */     double result = 0.0D;
/* 121:196 */     if (Utils.isMissingValue(incoming[this.m_fieldIndex]))
/* 122:    */     {
/* 123:197 */       if (this.m_mapMissingDefined) {
/* 124:198 */         result = this.m_mapMissingTo;
/* 125:    */       } else {
/* 126:200 */         result = incoming[this.m_fieldIndex];
/* 127:    */       }
/* 128:    */     }
/* 129:    */     else
/* 130:    */     {
/* 131:203 */       double x = incoming[this.m_fieldIndex];
/* 132:208 */       if (x < a[0])
/* 133:    */       {
/* 134:209 */         if (this.m_outlierTreatmentMethod == MiningFieldMetaInfo.Outlier.ASIS)
/* 135:    */         {
/* 136:210 */           double slope = (b[1] - b[0]) / (a[1] - a[0]);
/* 137:    */           
/* 138:212 */           double offset = b[0] - slope * a[0];
/* 139:213 */           result = slope * x + offset;
/* 140:    */         }
/* 141:214 */         else if (this.m_outlierTreatmentMethod == MiningFieldMetaInfo.Outlier.ASEXTREMEVALUES)
/* 142:    */         {
/* 143:215 */           result = b[0];
/* 144:    */         }
/* 145:    */         else
/* 146:    */         {
/* 147:218 */           result = this.m_mapMissingTo;
/* 148:    */         }
/* 149:    */       }
/* 150:220 */       else if (x > a[(a.length - 1)])
/* 151:    */       {
/* 152:221 */         int length = a.length;
/* 153:222 */         if (this.m_outlierTreatmentMethod == MiningFieldMetaInfo.Outlier.ASIS)
/* 154:    */         {
/* 155:223 */           double slope = (b[(length - 1)] - b[(length - 2)]) / (a[(length - 1)] - a[(length - 2)]);
/* 156:    */           
/* 157:225 */           double offset = b[(length - 1)] - slope * a[(length - 1)];
/* 158:226 */           result = slope * x + offset;
/* 159:    */         }
/* 160:227 */         else if (this.m_outlierTreatmentMethod == MiningFieldMetaInfo.Outlier.ASEXTREMEVALUES)
/* 161:    */         {
/* 162:228 */           result = b[(length - 1)];
/* 163:    */         }
/* 164:    */         else
/* 165:    */         {
/* 166:231 */           result = this.m_mapMissingTo;
/* 167:    */         }
/* 168:    */       }
/* 169:    */       else
/* 170:    */       {
/* 171:235 */         for (int i = 1; i < a.length; i++) {
/* 172:236 */           if (x <= a[i])
/* 173:    */           {
/* 174:237 */             result = b[(i - 1)];
/* 175:238 */             result += (x - a[(i - 1)]) / (a[i] - a[(i - 1)]) * (b[i] - b[(i - 1)]);
/* 176:    */             
/* 177:240 */             break;
/* 178:    */           }
/* 179:    */         }
/* 180:    */       }
/* 181:    */     }
/* 182:245 */     return result;
/* 183:    */   }
/* 184:    */   
/* 185:    */   public String getResultCategorical(double[] incoming)
/* 186:    */     throws Exception
/* 187:    */   {
/* 188:256 */     throw new Exception("[NormContinuous] Can't return the result as a categorical value!");
/* 189:    */   }
/* 190:    */   
/* 191:    */   public String toString(String pad)
/* 192:    */   {
/* 193:260 */     StringBuffer buff = new StringBuffer();
/* 194:    */     
/* 195:262 */     buff.append(pad + "NormContinuous (" + this.m_fieldName + "):\n" + pad + "linearNorm: ");
/* 196:263 */     for (int i = 0; i < this.m_linearNormOrig.length; i++) {
/* 197:264 */       buff.append("" + this.m_linearNormOrig[i] + ":" + this.m_linearNormNorm[i] + " ");
/* 198:    */     }
/* 199:266 */     buff.append("\n" + pad);
/* 200:267 */     buff.append("outlier treatment: " + this.m_outlierTreatmentMethod.toString());
/* 201:268 */     if (this.m_mapMissingDefined)
/* 202:    */     {
/* 203:269 */       buff.append("\n" + pad);
/* 204:270 */       buff.append("map missing values to: " + this.m_mapMissingTo);
/* 205:    */     }
/* 206:273 */     return buff.toString();
/* 207:    */   }
/* 208:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.NormContinuous
 * JD-Core Version:    0.7.0.1
 */