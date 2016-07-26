/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.LinkedList;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.DenseInstance;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.Option;
/*  15:    */ import weka.core.OptionHandler;
/*  16:    */ import weka.core.RevisionUtils;
/*  17:    */ import weka.core.TechnicalInformation;
/*  18:    */ import weka.core.TechnicalInformation.Field;
/*  19:    */ import weka.core.TechnicalInformation.Type;
/*  20:    */ import weka.core.TechnicalInformationHandler;
/*  21:    */ import weka.core.Utils;
/*  22:    */ import weka.filters.SimpleBatchFilter;
/*  23:    */ import weka.filters.UnsupervisedFilter;
/*  24:    */ 
/*  25:    */ public class MILESFilter
/*  26:    */   extends SimpleBatchFilter
/*  27:    */   implements UnsupervisedFilter, OptionHandler, TechnicalInformationHandler
/*  28:    */ {
/*  29:    */   static final long serialVersionUID = 4694489111366063853L;
/*  30:    */   public static final int BAG_ATTRIBUTE = 1;
/*  31:    */   public static final int LABEL_ATTRIBUTE = 2;
/*  32:112 */   private double m_sigma = Math.sqrt(800000.0D);
/*  33:115 */   private LinkedList<Instance> m_allInsts = null;
/*  34:    */   
/*  35:    */   public String sigmaTipText()
/*  36:    */   {
/*  37:124 */     return "The value of the sigma parameter.";
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void setSigma(double sigma)
/*  41:    */   {
/*  42:133 */     this.m_sigma = sigma;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public double getSigma()
/*  46:    */   {
/*  47:142 */     return this.m_sigma;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public String globalInfo()
/*  51:    */   {
/*  52:150 */     return "Implements the MILES transformation that maps multiple instance bags into a high-dimensional single-instance feature space.\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  53:    */   }
/*  54:    */   
/*  55:    */   public TechnicalInformation getTechnicalInformation()
/*  56:    */   {
/*  57:168 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  58:169 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Y. Chen and J. Bi and J.Z. Wang");
/*  59:170 */     result.setValue(TechnicalInformation.Field.TITLE, "MILES: Multiple-instance learning via embedded instance selection");
/*  60:    */     
/*  61:172 */     result.setValue(TechnicalInformation.Field.JOURNAL, "IEEE PAMI");
/*  62:173 */     result.setValue(TechnicalInformation.Field.YEAR, "2006");
/*  63:174 */     result.setValue(TechnicalInformation.Field.VOLUME, "28");
/*  64:175 */     result.setValue(TechnicalInformation.Field.PAGES, "1931-1947");
/*  65:176 */     result.setValue(TechnicalInformation.Field.NUMBER, "12");
/*  66:    */     
/*  67:178 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.INPROCEEDINGS);
/*  68:179 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "James Foulds and Eibe Frank");
/*  69:180 */     additional.setValue(TechnicalInformation.Field.TITLE, "Revisiting multiple-instance learning via embedded instance selection");
/*  70:    */     
/*  71:182 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "21st Australasian Joint Conference on Artificial Intelligence");
/*  72:    */     
/*  73:184 */     additional.setValue(TechnicalInformation.Field.YEAR, "2008");
/*  74:185 */     additional.setValue(TechnicalInformation.Field.PAGES, "300-310");
/*  75:186 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*  76:    */     
/*  77:188 */     return result;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public Capabilities getCapabilities()
/*  81:    */   {
/*  82:196 */     Capabilities result = super.getCapabilities();
/*  83:197 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/*  84:198 */     return result;
/*  85:    */   }
/*  86:    */   
/*  87:    */   protected Instances determineOutputFormat(Instances inputFormat)
/*  88:    */   {
/*  89:208 */     ArrayList<Attribute> atts = new ArrayList();
/*  90:209 */     this.m_allInsts = new LinkedList();
/*  91:210 */     for (int i = 0; i < getInputFormat().numInstances(); i++)
/*  92:    */     {
/*  93:211 */       Instances bag = getInputFormat().instance(i).relationalValue(1);
/*  94:213 */       for (int j = 0; j < bag.numInstances(); j++) {
/*  95:214 */         this.m_allInsts.add(bag.instance(j));
/*  96:    */       }
/*  97:    */     }
/*  98:217 */     for (int i = 0; i < this.m_allInsts.size(); i++) {
/*  99:218 */       atts.add(new Attribute("" + i));
/* 100:    */     }
/* 101:220 */     atts.add(inputFormat.attribute(2));
/* 102:    */     
/* 103:    */ 
/* 104:223 */     Instances returner = new Instances("", atts, 0);
/* 105:224 */     returner.setClassIndex(returner.numAttributes() - 1);
/* 106:    */     
/* 107:226 */     return returner;
/* 108:    */   }
/* 109:    */   
/* 110:    */   protected Instances process(Instances inst)
/* 111:    */   {
/* 112:236 */     Instances result = getOutputFormat();
/* 113:237 */     result.setClassIndex(result.numAttributes() - 1);
/* 114:240 */     if (inst.numInstances() == 0) {
/* 115:241 */       return result;
/* 116:    */     }
/* 117:245 */     for (int i = 0; i < inst.numInstances(); i++)
/* 118:    */     {
/* 119:249 */       double[] outputInstance = new double[result.numAttributes()];
/* 120:    */       
/* 121:    */ 
/* 122:252 */       Instances bag = inst.instance(i).relationalValue(1);
/* 123:253 */       int k = 0;
/* 124:254 */       for (Instance x_k : this.m_allInsts)
/* 125:    */       {
/* 126:257 */         double dSquared = 1.7976931348623157E+308D;
/* 127:258 */         for (int j = 0; j < bag.numInstances(); j++)
/* 128:    */         {
/* 129:262 */           double total = 0.0D;
/* 130:263 */           Instance x_ij = bag.instance(j);
/* 131:264 */           double numMissingValues = 0.0D;
/* 132:265 */           for (int l = 0; l < x_k.numAttributes(); l++) {
/* 133:268 */             if (!x_k.isMissing(l)) {
/* 134:273 */               if (!x_ij.isMissing(l)) {
/* 135:274 */                 total += (x_ij.value(l) - x_k.value(l)) * (x_ij.value(l) - x_k.value(l));
/* 136:    */               } else {
/* 137:277 */                 numMissingValues += 1.0D;
/* 138:    */               }
/* 139:    */             }
/* 140:    */           }
/* 141:281 */           total *= x_k.numAttributes() / (x_k.numAttributes() - numMissingValues);
/* 142:285 */           if ((total < dSquared) || (dSquared == 1.7976931348623157E+308D)) {
/* 143:286 */             dSquared = total;
/* 144:    */           }
/* 145:    */         }
/* 146:289 */         if (dSquared == 1.7976931348623157E+308D) {
/* 147:290 */           outputInstance[k] = 0.0D;
/* 148:    */         } else {
/* 149:292 */           outputInstance[k] = Math.exp(-1.0D * dSquared / (this.m_sigma * this.m_sigma));
/* 150:    */         }
/* 151:294 */         k++;
/* 152:    */       }
/* 153:298 */       double label = inst.instance(i).value(2);
/* 154:299 */       outputInstance[(outputInstance.length - 1)] = label;
/* 155:    */       
/* 156:    */ 
/* 157:302 */       result.add(new DenseInstance(inst.instance(i).weight(), outputInstance));
/* 158:    */     }
/* 159:305 */     return result;
/* 160:    */   }
/* 161:    */   
/* 162:    */   public Enumeration<Option> listOptions()
/* 163:    */   {
/* 164:316 */     Vector<Option> newVector = new Vector(1);
/* 165:    */     
/* 166:318 */     newVector.add(new Option("\tSpecify the sigma parameter (default: sqrt(800000)", "S", 1, "-S <num>"));
/* 167:    */     
/* 168:    */ 
/* 169:    */ 
/* 170:322 */     newVector.addAll(Collections.list(super.listOptions()));
/* 171:    */     
/* 172:324 */     return newVector.elements();
/* 173:    */   }
/* 174:    */   
/* 175:    */   public void setOptions(String[] options)
/* 176:    */     throws Exception
/* 177:    */   {
/* 178:347 */     String sigmaString = Utils.getOption('S', options);
/* 179:348 */     if (sigmaString.length() != 0) {
/* 180:349 */       setSigma(Double.parseDouble(sigmaString));
/* 181:    */     } else {
/* 182:351 */       setSigma(Math.sqrt(800000.0D));
/* 183:    */     }
/* 184:354 */     super.setOptions(options);
/* 185:    */     
/* 186:356 */     Utils.checkForRemainingOptions(options);
/* 187:    */   }
/* 188:    */   
/* 189:    */   public String[] getOptions()
/* 190:    */   {
/* 191:367 */     Vector<String> options = new Vector();
/* 192:    */     
/* 193:369 */     options.add("-S");
/* 194:370 */     options.add("" + getSigma());
/* 195:    */     
/* 196:372 */     Collections.addAll(options, super.getOptions());
/* 197:    */     
/* 198:374 */     return (String[])options.toArray(new String[0]);
/* 199:    */   }
/* 200:    */   
/* 201:    */   public static void main(String[] args)
/* 202:    */   {
/* 203:378 */     runFilter(new MILESFilter(), args);
/* 204:    */   }
/* 205:    */   
/* 206:    */   public String getRevision()
/* 207:    */   {
/* 208:383 */     return RevisionUtils.extract("$Revision: 12118 $");
/* 209:    */   }
/* 210:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.MILESFilter
 * JD-Core Version:    0.7.0.1
 */