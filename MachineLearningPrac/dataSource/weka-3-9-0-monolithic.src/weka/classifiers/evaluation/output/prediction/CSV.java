/*   1:    */ package weka.classifiers.evaluation.output.prediction;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.Classifier;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.Range;
/*  12:    */ import weka.core.Utils;
/*  13:    */ 
/*  14:    */ public class CSV
/*  15:    */   extends AbstractOutput
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = 3401604538169573720L;
/*  18: 90 */   protected String m_Delimiter = ",";
/*  19:    */   
/*  20:    */   public String globalInfo()
/*  21:    */   {
/*  22: 99 */     return "Outputs the predictions as CSV.";
/*  23:    */   }
/*  24:    */   
/*  25:    */   public String getDisplay()
/*  26:    */   {
/*  27:109 */     return "CSV";
/*  28:    */   }
/*  29:    */   
/*  30:    */   public Enumeration<Option> listOptions()
/*  31:    */   {
/*  32:121 */     Vector<Option> result = new Vector();
/*  33:    */     
/*  34:123 */     result.addElement(new Option("\tWhether to use TAB as separator instead of comma.\n\t(default: comma)", "use-tab", 0, "-use-tab"));
/*  35:    */     
/*  36:    */ 
/*  37:    */ 
/*  38:127 */     result.addAll(Collections.list(super.listOptions()));
/*  39:    */     
/*  40:129 */     return result.elements();
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void setOptions(String[] options)
/*  44:    */     throws Exception
/*  45:    */   {
/*  46:142 */     setUseTab(Utils.getFlag("use-tab", options));
/*  47:143 */     super.setOptions(options);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public String[] getOptions()
/*  51:    */   {
/*  52:153 */     Vector<String> result = new Vector();
/*  53:155 */     if (getUseTab()) {
/*  54:156 */       result.add("-use-tab");
/*  55:    */     }
/*  56:159 */     Collections.addAll(result, super.getOptions());
/*  57:    */     
/*  58:161 */     return (String[])result.toArray(new String[result.size()]);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setUseTab(boolean value)
/*  62:    */   {
/*  63:170 */     if (value) {
/*  64:171 */       this.m_Delimiter = "\t";
/*  65:    */     } else {
/*  66:173 */       this.m_Delimiter = ",";
/*  67:    */     }
/*  68:    */   }
/*  69:    */   
/*  70:    */   public boolean getUseTab()
/*  71:    */   {
/*  72:183 */     return this.m_Delimiter.equals("\t");
/*  73:    */   }
/*  74:    */   
/*  75:    */   public String useTabTipText()
/*  76:    */   {
/*  77:192 */     return "Whether to use TAB instead of COMMA as column separator.";
/*  78:    */   }
/*  79:    */   
/*  80:    */   protected void doPrintHeader()
/*  81:    */   {
/*  82:200 */     if (this.m_Header.classAttribute().isNominal())
/*  83:    */     {
/*  84:201 */       if (this.m_OutputDistribution)
/*  85:    */       {
/*  86:202 */         append("inst#" + this.m_Delimiter + "actual" + this.m_Delimiter + "predicted" + this.m_Delimiter + "error" + this.m_Delimiter + "distribution");
/*  87:204 */         for (int i = 1; i < this.m_Header.classAttribute().numValues(); i++) {
/*  88:205 */           append(this.m_Delimiter);
/*  89:    */         }
/*  90:    */       }
/*  91:    */       else
/*  92:    */       {
/*  93:208 */         append("inst#" + this.m_Delimiter + "actual" + this.m_Delimiter + "predicted" + this.m_Delimiter + "error" + this.m_Delimiter + "prediction");
/*  94:    */       }
/*  95:    */     }
/*  96:    */     else {
/*  97:212 */       append("inst#" + this.m_Delimiter + "actual" + this.m_Delimiter + "predicted" + this.m_Delimiter + "error");
/*  98:    */     }
/*  99:216 */     if (this.m_Attributes != null)
/* 100:    */     {
/* 101:217 */       append(this.m_Delimiter);
/* 102:218 */       boolean first = true;
/* 103:219 */       for (int i = 0; i < this.m_Header.numAttributes(); i++) {
/* 104:220 */         if (i != this.m_Header.classIndex()) {
/* 105:224 */           if (this.m_Attributes.isInRange(i))
/* 106:    */           {
/* 107:225 */             if (!first) {
/* 108:226 */               append(this.m_Delimiter);
/* 109:    */             }
/* 110:228 */             append(this.m_Header.attribute(i).name());
/* 111:229 */             first = false;
/* 112:    */           }
/* 113:    */         }
/* 114:    */       }
/* 115:    */     }
/* 116:234 */     append("\n");
/* 117:    */   }
/* 118:    */   
/* 119:    */   protected String attributeValuesString(Instance instance)
/* 120:    */   {
/* 121:245 */     StringBuffer text = new StringBuffer();
/* 122:246 */     if (this.m_Attributes != null)
/* 123:    */     {
/* 124:247 */       this.m_Attributes.setUpper(instance.numAttributes() - 1);
/* 125:248 */       boolean first = true;
/* 126:249 */       for (int i = 0; i < instance.numAttributes(); i++) {
/* 127:250 */         if ((this.m_Attributes.isInRange(i)) && (i != instance.classIndex()))
/* 128:    */         {
/* 129:251 */           if (!first) {
/* 130:252 */             text.append(this.m_Delimiter);
/* 131:    */           }
/* 132:254 */           text.append(instance.toString(i));
/* 133:255 */           first = false;
/* 134:    */         }
/* 135:    */       }
/* 136:    */     }
/* 137:259 */     return text.toString();
/* 138:    */   }
/* 139:    */   
/* 140:    */   protected void doPrintClassification(double[] dist, Instance inst, int index)
/* 141:    */     throws Exception
/* 142:    */   {
/* 143:273 */     int prec = this.m_NumDecimals;
/* 144:    */     
/* 145:275 */     Instance withMissing = (Instance)inst.copy();
/* 146:276 */     withMissing.setDataset(inst.dataset());
/* 147:    */     
/* 148:278 */     double predValue = 0.0D;
/* 149:279 */     if (Utils.sum(dist) == 0.0D) {
/* 150:280 */       predValue = Utils.missingValue();
/* 151:282 */     } else if (inst.classAttribute().isNominal()) {
/* 152:283 */       predValue = Utils.maxIndex(dist);
/* 153:    */     } else {
/* 154:285 */       predValue = dist[0];
/* 155:    */     }
/* 156:290 */     append("" + (index + 1));
/* 157:292 */     if (inst.dataset().classAttribute().isNumeric())
/* 158:    */     {
/* 159:294 */       if (inst.classIsMissing()) {
/* 160:295 */         append(this.m_Delimiter + "?");
/* 161:    */       } else {
/* 162:297 */         append(this.m_Delimiter + Utils.doubleToString(inst.classValue(), prec));
/* 163:    */       }
/* 164:300 */       if (Utils.isMissingValue(predValue)) {
/* 165:301 */         append(this.m_Delimiter + "?");
/* 166:    */       } else {
/* 167:303 */         append(this.m_Delimiter + Utils.doubleToString(predValue, prec));
/* 168:    */       }
/* 169:306 */       if ((Utils.isMissingValue(predValue)) || (inst.classIsMissing())) {
/* 170:307 */         append(this.m_Delimiter + "?");
/* 171:    */       } else {
/* 172:309 */         append(this.m_Delimiter + Utils.doubleToString(predValue - inst.classValue(), prec));
/* 173:    */       }
/* 174:    */     }
/* 175:    */     else
/* 176:    */     {
/* 177:314 */       append(this.m_Delimiter + ((int)inst.classValue() + 1) + ":" + inst.toString(inst.classIndex()));
/* 178:317 */       if (Utils.isMissingValue(predValue)) {
/* 179:318 */         append(this.m_Delimiter + "?");
/* 180:    */       } else {
/* 181:320 */         append(this.m_Delimiter + ((int)predValue + 1) + ":" + inst.dataset().classAttribute().value((int)predValue));
/* 182:    */       }
/* 183:324 */       if ((!Utils.isMissingValue(predValue)) && (!inst.classIsMissing()) && ((int)predValue + 1 != (int)inst.classValue() + 1)) {
/* 184:326 */         append(this.m_Delimiter + "+");
/* 185:    */       } else {
/* 186:328 */         append(this.m_Delimiter + "");
/* 187:    */       }
/* 188:331 */       if (this.m_OutputDistribution)
/* 189:    */       {
/* 190:332 */         if (Utils.isMissingValue(predValue))
/* 191:    */         {
/* 192:333 */           append(this.m_Delimiter + "?");
/* 193:    */         }
/* 194:    */         else
/* 195:    */         {
/* 196:335 */           append(this.m_Delimiter);
/* 197:336 */           for (int n = 0; n < dist.length; n++)
/* 198:    */           {
/* 199:337 */             if (n > 0) {
/* 200:338 */               append(this.m_Delimiter);
/* 201:    */             }
/* 202:340 */             if (n == (int)predValue) {
/* 203:341 */               append("*");
/* 204:    */             }
/* 205:343 */             append(Utils.doubleToString(dist[n], prec));
/* 206:    */           }
/* 207:    */         }
/* 208:    */       }
/* 209:347 */       else if (Utils.isMissingValue(predValue)) {
/* 210:348 */         append(this.m_Delimiter + "?");
/* 211:    */       } else {
/* 212:350 */         append(this.m_Delimiter + Utils.doubleToString(dist[((int)predValue)], prec));
/* 213:    */       }
/* 214:    */     }
/* 215:357 */     if (this.m_Attributes != null) {
/* 216:358 */       append(this.m_Delimiter + attributeValuesString(withMissing));
/* 217:    */     }
/* 218:360 */     append("\n");
/* 219:    */   }
/* 220:    */   
/* 221:    */   protected void doPrintClassification(Classifier classifier, Instance inst, int index)
/* 222:    */     throws Exception
/* 223:    */   {
/* 224:375 */     double[] d = classifier.distributionForInstance(inst);
/* 225:376 */     doPrintClassification(d, inst, index);
/* 226:    */   }
/* 227:    */   
/* 228:    */   protected void doPrintFooter() {}
/* 229:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.output.prediction.CSV
 * JD-Core Version:    0.7.0.1
 */