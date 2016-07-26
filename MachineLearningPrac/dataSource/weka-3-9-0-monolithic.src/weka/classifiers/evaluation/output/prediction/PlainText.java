/*   1:    */ package weka.classifiers.evaluation.output.prediction;
/*   2:    */ 
/*   3:    */ import weka.classifiers.Classifier;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Instance;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.Range;
/*   8:    */ import weka.core.Utils;
/*   9:    */ 
/*  10:    */ public class PlainText
/*  11:    */   extends AbstractOutput
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = 2033389864898242735L;
/*  14:    */   
/*  15:    */   public String globalInfo()
/*  16:    */   {
/*  17: 77 */     return "Outputs the predictions in plain text.";
/*  18:    */   }
/*  19:    */   
/*  20:    */   public String getDisplay()
/*  21:    */   {
/*  22: 86 */     return "Plain text";
/*  23:    */   }
/*  24:    */   
/*  25:    */   protected void doPrintHeader()
/*  26:    */   {
/*  27: 93 */     if (this.m_Header.classAttribute().isNominal())
/*  28:    */     {
/*  29: 94 */       if (this.m_OutputDistribution) {
/*  30: 95 */         append("    inst#     actual  predicted error distribution");
/*  31:    */       } else {
/*  32: 97 */         append("    inst#     actual  predicted error prediction");
/*  33:    */       }
/*  34:    */     }
/*  35:    */     else {
/*  36: 99 */       append("    inst#     actual  predicted      error");
/*  37:    */     }
/*  38:101 */     if (this.m_Attributes != null)
/*  39:    */     {
/*  40:102 */       append(" (");
/*  41:103 */       boolean first = true;
/*  42:104 */       for (int i = 0; i < this.m_Header.numAttributes(); i++) {
/*  43:105 */         if (i != this.m_Header.classIndex()) {
/*  44:108 */           if (this.m_Attributes.isInRange(i))
/*  45:    */           {
/*  46:109 */             if (!first) {
/*  47:110 */               append(",");
/*  48:    */             }
/*  49:111 */             append(this.m_Header.attribute(i).name());
/*  50:112 */             first = false;
/*  51:    */           }
/*  52:    */         }
/*  53:    */       }
/*  54:115 */       append(")");
/*  55:    */     }
/*  56:118 */     append("\n");
/*  57:    */   }
/*  58:    */   
/*  59:    */   protected String attributeValuesString(Instance instance)
/*  60:    */   {
/*  61:129 */     StringBuffer text = new StringBuffer();
/*  62:130 */     if (this.m_Attributes != null)
/*  63:    */     {
/*  64:131 */       boolean firstOutput = true;
/*  65:132 */       this.m_Attributes.setUpper(instance.numAttributes() - 1);
/*  66:133 */       for (int i = 0; i < instance.numAttributes(); i++) {
/*  67:134 */         if ((this.m_Attributes.isInRange(i)) && (i != instance.classIndex()))
/*  68:    */         {
/*  69:135 */           if (firstOutput) {
/*  70:135 */             text.append("(");
/*  71:    */           } else {
/*  72:136 */             text.append(",");
/*  73:    */           }
/*  74:137 */           text.append(instance.toString(i));
/*  75:138 */           firstOutput = false;
/*  76:    */         }
/*  77:    */       }
/*  78:140 */       if (!firstOutput) {
/*  79:140 */         text.append(")");
/*  80:    */       }
/*  81:    */     }
/*  82:142 */     return text.toString();
/*  83:    */   }
/*  84:    */   
/*  85:    */   protected void doPrintClassification(double[] dist, Instance inst, int index)
/*  86:    */     throws Exception
/*  87:    */   {
/*  88:154 */     int width = 7 + this.m_NumDecimals;
/*  89:155 */     int prec = this.m_NumDecimals;
/*  90:    */     
/*  91:157 */     Instance withMissing = (Instance)inst.copy();
/*  92:158 */     withMissing.setDataset(inst.dataset());
/*  93:    */     
/*  94:160 */     double predValue = 0.0D;
/*  95:161 */     if (Utils.sum(dist) == 0.0D) {
/*  96:162 */       predValue = Utils.missingValue();
/*  97:164 */     } else if (inst.classAttribute().isNominal()) {
/*  98:165 */       predValue = Utils.maxIndex(dist);
/*  99:    */     } else {
/* 100:167 */       predValue = dist[0];
/* 101:    */     }
/* 102:172 */     append(Utils.padLeftAndAllowOverflow("" + (index + 1), 9));
/* 103:174 */     if (inst.dataset().classAttribute().isNumeric())
/* 104:    */     {
/* 105:176 */       if (inst.classIsMissing()) {
/* 106:177 */         append(" " + Utils.padLeft("?", width));
/* 107:    */       } else {
/* 108:179 */         append(" " + Utils.doubleToString(inst.classValue(), width, prec));
/* 109:    */       }
/* 110:181 */       if (Utils.isMissingValue(predValue)) {
/* 111:182 */         append(" " + Utils.padLeft("?", width));
/* 112:    */       } else {
/* 113:184 */         append(" " + Utils.doubleToString(predValue, width, prec));
/* 114:    */       }
/* 115:186 */       if ((Utils.isMissingValue(predValue)) || (inst.classIsMissing())) {
/* 116:187 */         append(" " + Utils.padLeft("?", width));
/* 117:    */       } else {
/* 118:189 */         append(" " + Utils.doubleToString(predValue - inst.classValue(), width, prec));
/* 119:    */       }
/* 120:    */     }
/* 121:    */     else
/* 122:    */     {
/* 123:192 */       append(" " + Utils.padLeftAndAllowOverflow(new StringBuilder().append((int)inst.classValue() + 1).append(":").append(inst.toString(inst.classIndex())).toString(), width));
/* 124:194 */       if (Utils.isMissingValue(predValue)) {
/* 125:195 */         append(" " + Utils.padLeft("?", width));
/* 126:    */       } else {
/* 127:197 */         append(" " + Utils.padLeftAndAllowOverflow(new StringBuilder().append((int)predValue + 1).append(":").append(inst.dataset().classAttribute().value((int)predValue)).toString(), width));
/* 128:    */       }
/* 129:199 */       if ((!Utils.isMissingValue(predValue)) && (!inst.classIsMissing()) && ((int)predValue + 1 != (int)inst.classValue() + 1)) {
/* 130:200 */         append("   +  ");
/* 131:    */       } else {
/* 132:202 */         append("      ");
/* 133:    */       }
/* 134:204 */       if (this.m_OutputDistribution)
/* 135:    */       {
/* 136:205 */         if (Utils.isMissingValue(predValue))
/* 137:    */         {
/* 138:206 */           append(" ?");
/* 139:    */         }
/* 140:    */         else
/* 141:    */         {
/* 142:209 */           append(" ");
/* 143:210 */           for (int n = 0; n < dist.length; n++)
/* 144:    */           {
/* 145:211 */             if (n > 0) {
/* 146:212 */               append(",");
/* 147:    */             }
/* 148:213 */             if (n == (int)predValue) {
/* 149:214 */               append("*");
/* 150:    */             }
/* 151:215 */             append(Utils.doubleToString(dist[n], prec));
/* 152:    */           }
/* 153:    */         }
/* 154:    */       }
/* 155:220 */       else if (Utils.isMissingValue(predValue)) {
/* 156:221 */         append(" ?");
/* 157:    */       } else {
/* 158:223 */         append(" " + Utils.doubleToString(dist[((int)predValue)], prec));
/* 159:    */       }
/* 160:    */     }
/* 161:228 */     append(" " + attributeValuesString(withMissing) + "\n");
/* 162:    */   }
/* 163:    */   
/* 164:    */   protected void doPrintClassification(Classifier classifier, Instance inst, int index)
/* 165:    */     throws Exception
/* 166:    */   {
/* 167:242 */     double[] d = classifier.distributionForInstance(inst);
/* 168:243 */     doPrintClassification(d, inst, index);
/* 169:    */   }
/* 170:    */   
/* 171:    */   protected void doPrintFooter() {}
/* 172:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.output.prediction.PlainText
 * JD-Core Version:    0.7.0.1
 */