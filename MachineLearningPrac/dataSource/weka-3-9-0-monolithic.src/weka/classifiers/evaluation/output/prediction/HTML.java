/*   1:    */ package weka.classifiers.evaluation.output.prediction;
/*   2:    */ 
/*   3:    */ import weka.classifiers.Classifier;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Instance;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.Range;
/*   8:    */ import weka.core.Utils;
/*   9:    */ 
/*  10:    */ public class HTML
/*  11:    */   extends AbstractOutput
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = 7241252244954353300L;
/*  14:    */   
/*  15:    */   public String globalInfo()
/*  16:    */   {
/*  17: 77 */     return "Outputs the predictions in HTML.";
/*  18:    */   }
/*  19:    */   
/*  20:    */   public String getDisplay()
/*  21:    */   {
/*  22: 86 */     return "HTML";
/*  23:    */   }
/*  24:    */   
/*  25:    */   protected String sanitize(String s)
/*  26:    */   {
/*  27: 98 */     String result = s;
/*  28: 99 */     result = result.replaceAll("&", "&amp;");
/*  29:100 */     result = result.replaceAll("<", "&lt;");
/*  30:101 */     result = result.replaceAll(">", "&gt;");
/*  31:102 */     result = result.replaceAll("\"", "&quot;");
/*  32:    */     
/*  33:104 */     return result;
/*  34:    */   }
/*  35:    */   
/*  36:    */   protected void doPrintHeader()
/*  37:    */   {
/*  38:111 */     append("<html>\n");
/*  39:112 */     append("<head>\n");
/*  40:113 */     append("<title>Predictions for dataset " + sanitize(this.m_Header.relationName()) + "</title>\n");
/*  41:114 */     append("</head>\n");
/*  42:115 */     append("<body>\n");
/*  43:116 */     append("<div align=\"center\">\n");
/*  44:117 */     append("<h3>Predictions for dataset " + sanitize(this.m_Header.relationName()) + "</h3>\n");
/*  45:118 */     append("<table border=\"1\">\n");
/*  46:119 */     append("<tr>\n");
/*  47:120 */     if (this.m_Header.classAttribute().isNominal())
/*  48:    */     {
/*  49:121 */       if (this.m_OutputDistribution) {
/*  50:122 */         append("<td>inst#</td><td>actual</td><td>predicted</td><td>error</td><td colspan=\"" + this.m_Header.classAttribute().numValues() + "\">distribution</td>");
/*  51:    */       } else {
/*  52:124 */         append("<td>inst#</td><td>actual</td><td>predicted</td><td>error</td><td>prediction</td>");
/*  53:    */       }
/*  54:    */     }
/*  55:    */     else {
/*  56:126 */       append("<td>inst#</td><td>actual</td><td>predicted</td><td>error</td>");
/*  57:    */     }
/*  58:128 */     if (this.m_Attributes != null)
/*  59:    */     {
/*  60:129 */       append("<td>");
/*  61:130 */       boolean first = true;
/*  62:131 */       for (int i = 0; i < this.m_Header.numAttributes(); i++) {
/*  63:132 */         if (i != this.m_Header.classIndex()) {
/*  64:135 */           if (this.m_Attributes.isInRange(i))
/*  65:    */           {
/*  66:136 */             if (!first) {
/*  67:137 */               append("</td><td>");
/*  68:    */             }
/*  69:138 */             append(sanitize(this.m_Header.attribute(i).name()));
/*  70:139 */             first = false;
/*  71:    */           }
/*  72:    */         }
/*  73:    */       }
/*  74:142 */       append("</td>");
/*  75:    */     }
/*  76:145 */     append("</tr>\n");
/*  77:    */   }
/*  78:    */   
/*  79:    */   protected String attributeValuesString(Instance instance)
/*  80:    */   {
/*  81:156 */     StringBuffer text = new StringBuffer();
/*  82:157 */     if (this.m_Attributes != null)
/*  83:    */     {
/*  84:158 */       boolean firstOutput = true;
/*  85:159 */       this.m_Attributes.setUpper(instance.numAttributes() - 1);
/*  86:160 */       for (int i = 0; i < instance.numAttributes(); i++) {
/*  87:161 */         if ((this.m_Attributes.isInRange(i)) && (i != instance.classIndex()))
/*  88:    */         {
/*  89:162 */           if (!firstOutput) {
/*  90:163 */             text.append("</td>");
/*  91:    */           }
/*  92:164 */           if (this.m_Header.attribute(i).isNumeric()) {
/*  93:165 */             text.append("<td align=\"right\">");
/*  94:    */           } else {
/*  95:167 */             text.append("<td>");
/*  96:    */           }
/*  97:168 */           text.append(sanitize(instance.toString(i)));
/*  98:169 */           firstOutput = false;
/*  99:    */         }
/* 100:    */       }
/* 101:171 */       if (!firstOutput) {
/* 102:172 */         text.append("</td>");
/* 103:    */       }
/* 104:    */     }
/* 105:174 */     return text.toString();
/* 106:    */   }
/* 107:    */   
/* 108:    */   protected void doPrintClassification(double[] dist, Instance inst, int index)
/* 109:    */     throws Exception
/* 110:    */   {
/* 111:178 */     int prec = this.m_NumDecimals;
/* 112:    */     
/* 113:180 */     Instance withMissing = (Instance)inst.copy();
/* 114:181 */     withMissing.setDataset(inst.dataset());
/* 115:    */     
/* 116:183 */     double predValue = 0.0D;
/* 117:184 */     if (Utils.sum(dist) == 0.0D) {
/* 118:185 */       predValue = Utils.missingValue();
/* 119:187 */     } else if (inst.classAttribute().isNominal()) {
/* 120:188 */       predValue = Utils.maxIndex(dist);
/* 121:    */     } else {
/* 122:190 */       predValue = dist[0];
/* 123:    */     }
/* 124:195 */     append("<tr>");
/* 125:196 */     append("<td>" + (index + 1) + "</td>");
/* 126:198 */     if (inst.dataset().classAttribute().isNumeric())
/* 127:    */     {
/* 128:200 */       if (inst.classIsMissing()) {
/* 129:201 */         append("<td align=\"right\">?</td>");
/* 130:    */       } else {
/* 131:203 */         append("<td align=\"right\">" + Utils.doubleToString(inst.classValue(), prec) + "</td>");
/* 132:    */       }
/* 133:205 */       if (Utils.isMissingValue(predValue)) {
/* 134:206 */         append("<td align=\"right\">?</td>");
/* 135:    */       } else {
/* 136:208 */         append("<td align=\"right\">" + Utils.doubleToString(predValue, prec) + "</td>");
/* 137:    */       }
/* 138:210 */       if ((Utils.isMissingValue(predValue)) || (inst.classIsMissing())) {
/* 139:211 */         append("<td align=\"right\">?</td>");
/* 140:    */       } else {
/* 141:213 */         append("<td align=\"right\">" + Utils.doubleToString(predValue - inst.classValue(), prec) + "</td>");
/* 142:    */       }
/* 143:    */     }
/* 144:    */     else
/* 145:    */     {
/* 146:216 */       append("<td>" + ((int)inst.classValue() + 1) + ":" + sanitize(inst.toString(inst.classIndex())) + "</td>");
/* 147:218 */       if (Utils.isMissingValue(predValue)) {
/* 148:219 */         append("<td>?</td>");
/* 149:    */       } else {
/* 150:221 */         append("<td>" + ((int)predValue + 1) + ":" + sanitize(inst.dataset().classAttribute().value((int)predValue)) + "</td>");
/* 151:    */       }
/* 152:223 */       if ((!Utils.isMissingValue(predValue)) && (!inst.classIsMissing()) && ((int)predValue + 1 != (int)inst.classValue() + 1)) {
/* 153:224 */         append("<td>+</td>");
/* 154:    */       } else {
/* 155:226 */         append("<td>&nbsp;</td>");
/* 156:    */       }
/* 157:228 */       if (this.m_OutputDistribution)
/* 158:    */       {
/* 159:229 */         if (Utils.isMissingValue(predValue))
/* 160:    */         {
/* 161:230 */           append("<td>?</td>");
/* 162:    */         }
/* 163:    */         else
/* 164:    */         {
/* 165:233 */           append("<td align=\"right\">");
/* 166:234 */           for (int n = 0; n < dist.length; n++)
/* 167:    */           {
/* 168:235 */             if (n > 0) {
/* 169:236 */               append("</td><td align=\"right\">");
/* 170:    */             }
/* 171:237 */             if (n == (int)predValue) {
/* 172:238 */               append("*");
/* 173:    */             }
/* 174:239 */             append(Utils.doubleToString(dist[n], prec));
/* 175:    */           }
/* 176:241 */           append("</td>");
/* 177:    */         }
/* 178:    */       }
/* 179:245 */       else if (Utils.isMissingValue(predValue)) {
/* 180:246 */         append("<td align=\"right\">?</td>");
/* 181:    */       } else {
/* 182:248 */         append("<td align=\"right\">" + Utils.doubleToString(dist[((int)predValue)], prec) + "</td>");
/* 183:    */       }
/* 184:    */     }
/* 185:253 */     append(attributeValuesString(withMissing) + "</tr>\n");
/* 186:    */   }
/* 187:    */   
/* 188:    */   protected void doPrintClassification(Classifier classifier, Instance inst, int index)
/* 189:    */     throws Exception
/* 190:    */   {
/* 191:266 */     double[] d = classifier.distributionForInstance(inst);
/* 192:267 */     doPrintClassification(d, inst, index);
/* 193:    */   }
/* 194:    */   
/* 195:    */   protected void doPrintFooter()
/* 196:    */   {
/* 197:274 */     append("</table>\n");
/* 198:275 */     append("</div>\n");
/* 199:276 */     append("</body>\n");
/* 200:277 */     append("</html>\n");
/* 201:    */   }
/* 202:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.output.prediction.HTML
 * JD-Core Version:    0.7.0.1
 */