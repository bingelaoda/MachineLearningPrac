/*   1:    */ package weka.classifiers.evaluation.output.prediction;
/*   2:    */ 
/*   3:    */ import weka.classifiers.Classifier;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Instance;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.Range;
/*   8:    */ import weka.core.Utils;
/*   9:    */ import weka.core.Version;
/*  10:    */ 
/*  11:    */ public class XML
/*  12:    */   extends AbstractOutput
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = -3165514277316824801L;
/*  15:    */   public static final String DTD_DOCTYPE = "DOCTYPE";
/*  16:    */   public static final String DTD_ELEMENT = "ELEMENT";
/*  17:    */   public static final String DTD_ATTLIST = "ATTLIST";
/*  18:    */   public static final String DTD_OPTIONAL = "?";
/*  19:    */   public static final String DTD_AT_LEAST_ONE = "+";
/*  20:    */   public static final String DTD_ZERO_OR_MORE = "*";
/*  21:    */   public static final String DTD_SEPARATOR = "|";
/*  22:    */   public static final String DTD_CDATA = "CDATA";
/*  23:    */   public static final String DTD_ANY = "ANY";
/*  24:    */   public static final String DTD_PCDATA = "#PCDATA";
/*  25:    */   public static final String DTD_IMPLIED = "#IMPLIED";
/*  26:    */   public static final String DTD_REQUIRED = "#REQUIRED";
/*  27:    */   public static final String ATT_VERSION = "version";
/*  28:    */   public static final String ATT_NAME = "name";
/*  29:    */   public static final String ATT_TYPE = "type";
/*  30:    */   public static final String VAL_YES = "yes";
/*  31:    */   public static final String VAL_NO = "no";
/*  32:    */   public static final String TAG_PREDICTIONS = "predictions";
/*  33:    */   public static final String TAG_PREDICTION = "prediction";
/*  34:    */   public static final String TAG_ACTUAL_LABEL = "actual_label";
/*  35:    */   public static final String TAG_PREDICTED_LABEL = "predicted_label";
/*  36:    */   public static final String TAG_ERROR = "error";
/*  37:    */   public static final String TAG_DISTRIBUTION = "distribution";
/*  38:    */   public static final String TAG_CLASS_LABEL = "class_label";
/*  39:    */   public static final String TAG_ACTUAL_VALUE = "actual_value";
/*  40:    */   public static final String TAG_PREDICTED_VALUE = "predicted_value";
/*  41:    */   public static final String TAG_ATTRIBUTES = "attributes";
/*  42:    */   public static final String TAG_ATTRIBUTE = "attribute";
/*  43:    */   public static final String ATT_INDEX = "index";
/*  44:    */   public static final String ATT_PREDICTED = "predicted";
/*  45:195 */   public static final String DTD = "<!DOCTYPE predictions\n[\n  <!ELEMENT predictions (prediction*)>\n  <!ATTLIST predictions version CDATA \"" + Version.VERSION + "\"" + ">\n" + "  <!" + "ATTLIST" + " " + "predictions" + " " + "name" + " " + "CDATA" + " " + "#REQUIRED" + ">\n" + "\n" + "  <!" + "ELEMENT" + " " + "prediction" + " " + "(" + "(" + "actual_label" + "," + "predicted_label" + "," + "error" + "," + "(" + "prediction" + "|" + "distribution" + ")" + "," + "attributes" + "?" + ")" + "|" + "(" + "actual_value" + "," + "predicted_value" + "," + "error" + "," + "attributes" + "?" + ")" + ")" + ">\n" + "  <!" + "ATTLIST" + " " + "prediction" + " " + "index" + " " + "CDATA" + " " + "#REQUIRED" + ">\n" + "\n" + "  <!" + "ELEMENT" + " " + "actual_label" + " " + "ANY" + ">\n" + "  <!" + "ATTLIST" + " " + "actual_label" + " " + "index" + " " + "CDATA" + " " + "#REQUIRED" + ">\n" + "  <!" + "ELEMENT" + " " + "predicted_label" + " " + "ANY" + ">\n" + "  <!" + "ATTLIST" + " " + "predicted_label" + " " + "index" + " " + "CDATA" + " " + "#REQUIRED" + ">\n" + "  <!" + "ELEMENT" + " " + "error" + " " + "ANY" + ">\n" + "  <!" + "ELEMENT" + " " + "prediction" + " " + "ANY" + ">\n" + "  <!" + "ELEMENT" + " " + "distribution" + " (" + "class_label" + "+" + ")" + ">\n" + "  <!" + "ELEMENT" + " " + "class_label" + " " + "ANY" + ">\n" + "  <!" + "ATTLIST" + " " + "class_label" + " " + "index" + " " + "CDATA" + " " + "#REQUIRED" + ">\n" + "  <!" + "ATTLIST" + " " + "class_label" + " " + "predicted" + " (" + "yes" + "|" + "no" + ") " + "\"" + "no" + "\"" + ">\n" + "  <!" + "ELEMENT" + " " + "actual_value" + " " + "ANY" + ">\n" + "  <!" + "ELEMENT" + " " + "predicted_value" + " " + "ANY" + ">\n" + "  <!" + "ELEMENT" + " " + "attributes" + " (" + "attribute" + "+" + ")" + ">\n" + "  <!" + "ELEMENT" + " " + "attribute" + " " + "ANY" + ">\n" + "  <!" + "ATTLIST" + " " + "attribute" + " " + "index" + " " + "CDATA" + " " + "#REQUIRED" + ">\n" + "  <!" + "ATTLIST" + " " + "attribute" + " " + "name" + " " + "CDATA" + " " + "#REQUIRED" + ">\n" + "  <!" + "ATTLIST" + " " + "attribute" + " " + "type" + " " + "(" + Attribute.typeToString(0) + "|" + Attribute.typeToString(3) + "|" + Attribute.typeToString(1) + "|" + Attribute.typeToString(2) + "|" + Attribute.typeToString(4) + ")" + " " + "#REQUIRED" + ">\n" + "]\n" + ">";
/*  46:    */   
/*  47:    */   public String globalInfo()
/*  48:    */   {
/*  49:237 */     return "Outputs the predictions in XML.\n\nThe following DTD is used:\n\n" + DTD;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public String getDisplay()
/*  53:    */   {
/*  54:249 */     return "XML";
/*  55:    */   }
/*  56:    */   
/*  57:    */   protected String sanitize(String s)
/*  58:    */   {
/*  59:261 */     String result = s;
/*  60:262 */     result = result.replaceAll("&", "&amp;");
/*  61:263 */     result = result.replaceAll("<", "&lt;");
/*  62:264 */     result = result.replaceAll(">", "&gt;");
/*  63:265 */     result = result.replaceAll("\"", "&quot;");
/*  64:    */     
/*  65:267 */     return result;
/*  66:    */   }
/*  67:    */   
/*  68:    */   protected void doPrintHeader()
/*  69:    */   {
/*  70:274 */     append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
/*  71:275 */     append("\n");
/*  72:276 */     append(DTD + "\n\n");
/*  73:277 */     append("<predictions version=\"" + Version.VERSION + "\"" + " " + "name" + "=\"" + sanitize(this.m_Header.relationName()) + "\">\n");
/*  74:    */   }
/*  75:    */   
/*  76:    */   protected String attributeValuesString(Instance instance)
/*  77:    */   {
/*  78:288 */     StringBuffer text = new StringBuffer();
/*  79:289 */     if (this.m_Attributes != null)
/*  80:    */     {
/*  81:290 */       text.append("    <attributes>\n");
/*  82:291 */       this.m_Attributes.setUpper(instance.numAttributes() - 1);
/*  83:292 */       for (int i = 0; i < instance.numAttributes(); i++) {
/*  84:293 */         if ((this.m_Attributes.isInRange(i)) && (i != instance.classIndex()))
/*  85:    */         {
/*  86:294 */           text.append("      <attribute index=\"" + (i + 1) + "\"" + " " + "name" + "=\"" + sanitize(instance.attribute(i).name()) + "\"" + " " + "type" + "=\"" + Attribute.typeToString(instance.attribute(i).type()) + "\"" + ">");
/*  87:295 */           text.append(sanitize(instance.toString(i)));
/*  88:296 */           text.append("</attribute>\n");
/*  89:    */         }
/*  90:    */       }
/*  91:299 */       text.append("    </attributes>\n");
/*  92:    */     }
/*  93:301 */     return text.toString();
/*  94:    */   }
/*  95:    */   
/*  96:    */   protected void doPrintClassification(double[] dist, Instance inst, int index)
/*  97:    */     throws Exception
/*  98:    */   {
/*  99:313 */     int prec = this.m_NumDecimals;
/* 100:    */     
/* 101:315 */     Instance withMissing = (Instance)inst.copy();
/* 102:316 */     withMissing.setDataset(inst.dataset());
/* 103:    */     
/* 104:318 */     double predValue = 0.0D;
/* 105:319 */     if (Utils.sum(dist) == 0.0D) {
/* 106:320 */       predValue = Utils.missingValue();
/* 107:322 */     } else if (inst.classAttribute().isNominal()) {
/* 108:323 */       predValue = Utils.maxIndex(dist);
/* 109:    */     } else {
/* 110:325 */       predValue = dist[0];
/* 111:    */     }
/* 112:330 */     append("  <prediction index=\"" + (index + 1) + "\">\n");
/* 113:332 */     if (inst.dataset().classAttribute().isNumeric())
/* 114:    */     {
/* 115:334 */       append("    <actual_value>");
/* 116:335 */       if (inst.classIsMissing()) {
/* 117:336 */         append("?");
/* 118:    */       } else {
/* 119:338 */         append(Utils.doubleToString(inst.classValue(), prec));
/* 120:    */       }
/* 121:339 */       append("</actual_value>\n");
/* 122:    */       
/* 123:341 */       append("    <predicted_value>");
/* 124:342 */       if (inst.classIsMissing()) {
/* 125:343 */         append("?");
/* 126:    */       } else {
/* 127:345 */         append(Utils.doubleToString(predValue, prec));
/* 128:    */       }
/* 129:346 */       append("</predicted_value>\n");
/* 130:    */       
/* 131:348 */       append("    <error>");
/* 132:349 */       if ((Utils.isMissingValue(predValue)) || (inst.classIsMissing())) {
/* 133:350 */         append("?");
/* 134:    */       } else {
/* 135:352 */         append(Utils.doubleToString(predValue - inst.classValue(), prec));
/* 136:    */       }
/* 137:353 */       append("</error>\n");
/* 138:    */     }
/* 139:    */     else
/* 140:    */     {
/* 141:356 */       append("    <actual_label index=\"" + ((int)inst.classValue() + 1) + "\"" + ">");
/* 142:357 */       append(sanitize(inst.toString(inst.classIndex())));
/* 143:358 */       append("</actual_label>\n");
/* 144:    */       
/* 145:360 */       append("    <predicted_label index=\"" + ((int)predValue + 1) + "\"" + ">");
/* 146:361 */       if (Utils.isMissingValue(predValue)) {
/* 147:362 */         append("?");
/* 148:    */       } else {
/* 149:364 */         append(sanitize(inst.dataset().classAttribute().value((int)predValue)));
/* 150:    */       }
/* 151:365 */       append("</predicted_label>\n");
/* 152:    */       
/* 153:367 */       append("    <error>");
/* 154:368 */       if ((!Utils.isMissingValue(predValue)) && (!inst.classIsMissing()) && ((int)predValue + 1 != (int)inst.classValue() + 1)) {
/* 155:369 */         append("yes");
/* 156:    */       } else {
/* 157:371 */         append("no");
/* 158:    */       }
/* 159:372 */       append("</error>\n");
/* 160:374 */       if (this.m_OutputDistribution)
/* 161:    */       {
/* 162:375 */         append("    <distribution>\n");
/* 163:376 */         for (int n = 0; n < dist.length; n++)
/* 164:    */         {
/* 165:377 */           append("      <class_label index=\"" + (n + 1) + "\"");
/* 166:378 */           if ((!Utils.isMissingValue(predValue)) && (n == (int)predValue)) {
/* 167:379 */             append(" predicted=\"yes\"");
/* 168:    */           }
/* 169:380 */           append(">");
/* 170:381 */           append(Utils.doubleToString(dist[n], prec));
/* 171:382 */           append("</class_label>\n");
/* 172:    */         }
/* 173:384 */         append("    </distribution>\n");
/* 174:    */       }
/* 175:    */       else
/* 176:    */       {
/* 177:387 */         append("    <prediction>");
/* 178:388 */         if (Utils.isMissingValue(predValue)) {
/* 179:389 */           append("?");
/* 180:    */         } else {
/* 181:391 */           append(Utils.doubleToString(dist[((int)predValue)], prec));
/* 182:    */         }
/* 183:392 */         append("</prediction>\n");
/* 184:    */       }
/* 185:    */     }
/* 186:397 */     if (this.m_Attributes != null) {
/* 187:398 */       append(attributeValuesString(withMissing));
/* 188:    */     }
/* 189:401 */     append("  </prediction>\n");
/* 190:    */   }
/* 191:    */   
/* 192:    */   protected void doPrintClassification(Classifier classifier, Instance inst, int index)
/* 193:    */     throws Exception
/* 194:    */   {
/* 195:414 */     double[] d = classifier.distributionForInstance(inst);
/* 196:415 */     doPrintClassification(d, inst, index);
/* 197:    */   }
/* 198:    */   
/* 199:    */   protected void doPrintFooter()
/* 200:    */   {
/* 201:422 */     append("</predictions>\n");
/* 202:    */   }
/* 203:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.output.prediction.XML
 * JD-Core Version:    0.7.0.1
 */