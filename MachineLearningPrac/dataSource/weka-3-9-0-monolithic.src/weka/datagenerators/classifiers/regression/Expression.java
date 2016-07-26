/*   1:    */ package weka.datagenerators.classifiers.regression;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Random;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.DenseInstance;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.Utils;
/*  15:    */ import weka.filters.unsupervised.attribute.AddExpression;
/*  16:    */ 
/*  17:    */ public class Expression
/*  18:    */   extends MexicanHat
/*  19:    */ {
/*  20:    */   static final long serialVersionUID = -4237047357682277211L;
/*  21:    */   protected String m_Expression;
/*  22:    */   protected AddExpression m_Filter;
/*  23:    */   protected Instances m_RawData;
/*  24:    */   
/*  25:    */   public Expression()
/*  26:    */   {
/*  27:137 */     setExpression(defaultExpression());
/*  28:    */   }
/*  29:    */   
/*  30:    */   public String globalInfo()
/*  31:    */   {
/*  32:148 */     return "A data generator for generating y according to a given expression out of randomly generated x.\nE.g., the mexican hat can be generated like this:\n   sin(abs(a1)) / abs(a1)\nIn addition to this function, the amplitude can be changed and gaussian noise can be added.";
/*  33:    */   }
/*  34:    */   
/*  35:    */   public Enumeration<Option> listOptions()
/*  36:    */   {
/*  37:164 */     Vector<Option> result = enumToVector(super.listOptions());
/*  38:    */     
/*  39:166 */     result.addElement(new Option("\tThe expression to use for generating y out of x \n\t(default " + defaultExpression() + ").", "E", 1, "-E <expression>"));
/*  40:    */     
/*  41:    */ 
/*  42:    */ 
/*  43:170 */     return result.elements();
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setOptions(String[] options)
/*  47:    */     throws Exception
/*  48:    */   {
/*  49:246 */     super.setOptions(options);
/*  50:    */     
/*  51:248 */     String tmpStr = Utils.getOption('E', options);
/*  52:249 */     if (tmpStr.length() != 0) {
/*  53:250 */       setExpression(tmpStr);
/*  54:    */     } else {
/*  55:252 */       setExpression(defaultExpression());
/*  56:    */     }
/*  57:    */   }
/*  58:    */   
/*  59:    */   public String[] getOptions()
/*  60:    */   {
/*  61:266 */     Vector<String> result = new Vector();
/*  62:267 */     String[] options = super.getOptions();
/*  63:268 */     Collections.addAll(result, options);
/*  64:    */     
/*  65:270 */     result.add("-E");
/*  66:271 */     result.add("" + getExpression());
/*  67:    */     
/*  68:273 */     return (String[])result.toArray(new String[result.size()]);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public String amplitudeTipText()
/*  72:    */   {
/*  73:284 */     return "The amplitude to multiply the y value with.";
/*  74:    */   }
/*  75:    */   
/*  76:    */   protected String defaultExpression()
/*  77:    */   {
/*  78:293 */     return "sin(abs(a1)) / abs(a1)";
/*  79:    */   }
/*  80:    */   
/*  81:    */   public String getExpression()
/*  82:    */   {
/*  83:302 */     return this.m_Expression;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setExpression(String value)
/*  87:    */   {
/*  88:311 */     if (value.length() != 0) {
/*  89:312 */       this.m_Expression = value;
/*  90:    */     } else {
/*  91:314 */       throw new IllegalArgumentException("An expression has to be provided!");
/*  92:    */     }
/*  93:    */   }
/*  94:    */   
/*  95:    */   public String expressionTipText()
/*  96:    */   {
/*  97:325 */     return "The expression for generating y out of x.";
/*  98:    */   }
/*  99:    */   
/* 100:    */   public boolean getSingleModeFlag()
/* 101:    */     throws Exception
/* 102:    */   {
/* 103:337 */     return true;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public Instances defineDataFormat()
/* 107:    */     throws Exception
/* 108:    */   {
/* 109:354 */     ArrayList<Attribute> atts = new ArrayList();
/* 110:355 */     atts.add(new Attribute("x"));
/* 111:    */     
/* 112:357 */     this.m_RawData = new Instances(getRelationNameToUse(), atts, 0);
/* 113:    */     
/* 114:359 */     this.m_Filter = new AddExpression();
/* 115:360 */     this.m_Filter.setName("y");
/* 116:361 */     this.m_Filter.setExpression(getExpression());
/* 117:362 */     this.m_Filter.setInputFormat(this.m_RawData);
/* 118:    */     
/* 119:364 */     return super.defineDataFormat();
/* 120:    */   }
/* 121:    */   
/* 122:    */   public Instance generateExample()
/* 123:    */     throws Exception
/* 124:    */   {
/* 125:384 */     Random rand = getRandom();
/* 126:386 */     if (this.m_DatasetFormat == null) {
/* 127:387 */       throw new Exception("Dataset format not defined.");
/* 128:    */     }
/* 129:391 */     double x = rand.nextDouble();
/* 130:    */     
/* 131:393 */     x = x * (getMaxRange() - getMinRange()) + getMinRange();
/* 132:    */     
/* 133:    */ 
/* 134:396 */     double[] atts = new double[1];
/* 135:397 */     atts[0] = x;
/* 136:398 */     Instance inst = new DenseInstance(1.0D, atts);
/* 137:399 */     inst.setDataset(this.m_RawData);
/* 138:400 */     this.m_Filter.input(inst);
/* 139:401 */     this.m_Filter.batchFinished();
/* 140:402 */     inst = this.m_Filter.output();
/* 141:    */     
/* 142:    */ 
/* 143:405 */     double y = inst.value(1) + getAmplitude() * this.m_NoiseRandom.nextGaussian() * getNoiseRate() * getNoiseVariance();
/* 144:    */     
/* 145:    */ 
/* 146:    */ 
/* 147:409 */     atts = new double[this.m_DatasetFormat.numAttributes()];
/* 148:    */     
/* 149:411 */     atts[0] = x;
/* 150:412 */     atts[1] = y;
/* 151:413 */     Instance result = new DenseInstance(1.0D, atts);
/* 152:    */     
/* 153:    */ 
/* 154:416 */     result.setDataset(this.m_DatasetFormat);
/* 155:    */     
/* 156:418 */     return result;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public Instances generateExamples()
/* 160:    */     throws Exception
/* 161:    */   {
/* 162:436 */     Instances result = new Instances(this.m_DatasetFormat, 0);
/* 163:437 */     this.m_Random = new Random(getSeed());
/* 164:439 */     for (int i = 0; i < getNumExamplesAct(); i++) {
/* 165:440 */       result.add(generateExample());
/* 166:    */     }
/* 167:443 */     return result;
/* 168:    */   }
/* 169:    */   
/* 170:    */   public String generateStart()
/* 171:    */   {
/* 172:455 */     return "";
/* 173:    */   }
/* 174:    */   
/* 175:    */   public String generateFinished()
/* 176:    */     throws Exception
/* 177:    */   {
/* 178:467 */     return "";
/* 179:    */   }
/* 180:    */   
/* 181:    */   public String getRevision()
/* 182:    */   {
/* 183:477 */     return RevisionUtils.extract("$Revision: 11504 $");
/* 184:    */   }
/* 185:    */   
/* 186:    */   public static void main(String[] args)
/* 187:    */   {
/* 188:486 */     runDataGenerator(new Expression(), args);
/* 189:    */   }
/* 190:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.datagenerators.classifiers.regression.Expression
 * JD-Core Version:    0.7.0.1
 */