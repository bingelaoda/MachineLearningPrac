/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Random;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.core.Attribute;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Capabilities.Capability;
/*  12:    */ import weka.core.DenseInstance;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.RevisionUtils;
/*  17:    */ import weka.core.Utils;
/*  18:    */ import weka.filters.SimpleStreamFilter;
/*  19:    */ 
/*  20:    */ public class RandomSubset
/*  21:    */   extends SimpleStreamFilter
/*  22:    */ {
/*  23:    */   private static final long serialVersionUID = 2911221724251628050L;
/*  24: 83 */   protected double m_NumAttributes = 0.5D;
/*  25: 86 */   protected int m_Seed = 1;
/*  26: 89 */   protected int[] m_Indices = null;
/*  27:    */   protected boolean m_invertSelection;
/*  28:    */   
/*  29:    */   public String globalInfo()
/*  30:    */   {
/*  31:102 */     return "Chooses a random subset of attributes, either an absolute number or a percentage. The class is always included in the output (as the last attribute).";
/*  32:    */   }
/*  33:    */   
/*  34:    */   public Enumeration<Option> listOptions()
/*  35:    */   {
/*  36:115 */     Vector<Option> result = new Vector();
/*  37:    */     
/*  38:117 */     result.addElement(new Option("\tThe number of attributes to randomly select.\n\tIf < 1 then percentage, >= 1 absolute number.\n\t(default: 0.5)", "N", 1, "-N <double>"));
/*  39:    */     
/*  40:    */ 
/*  41:    */ 
/*  42:    */ 
/*  43:122 */     result.addElement(new Option("\tInvert selection - i.e. randomly remove rather than select.", "V", 0, "-V"));
/*  44:    */     
/*  45:    */ 
/*  46:    */ 
/*  47:126 */     result.addElement(new Option("\tThe seed value.\n\t(default: 1)", "S", 1, "-S <int>"));
/*  48:    */     
/*  49:    */ 
/*  50:129 */     result.addAll(Collections.list(super.listOptions()));
/*  51:    */     
/*  52:131 */     return result.elements();
/*  53:    */   }
/*  54:    */   
/*  55:    */   public String[] getOptions()
/*  56:    */   {
/*  57:142 */     Vector<String> result = new Vector();
/*  58:    */     
/*  59:144 */     result.add("-N");
/*  60:145 */     result.add("" + this.m_NumAttributes);
/*  61:147 */     if (getInvertSelection()) {
/*  62:148 */       result.add("-V");
/*  63:    */     }
/*  64:151 */     result.add("-S");
/*  65:152 */     result.add("" + this.m_Seed);
/*  66:    */     
/*  67:154 */     Collections.addAll(result, super.getOptions());
/*  68:    */     
/*  69:156 */     return (String[])result.toArray(new String[result.size()]);
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setOptions(String[] options)
/*  73:    */     throws Exception
/*  74:    */   {
/*  75:195 */     String tmpStr = Utils.getOption("N", options);
/*  76:196 */     if (tmpStr.length() != 0) {
/*  77:197 */       setNumAttributes(Double.parseDouble(tmpStr));
/*  78:    */     } else {
/*  79:199 */       setNumAttributes(0.5D);
/*  80:    */     }
/*  81:202 */     setInvertSelection(Utils.getFlag('V', options));
/*  82:    */     
/*  83:204 */     tmpStr = Utils.getOption("S", options);
/*  84:205 */     if (tmpStr.length() != 0) {
/*  85:206 */       setSeed(Integer.parseInt(tmpStr));
/*  86:    */     } else {
/*  87:208 */       setSeed(1);
/*  88:    */     }
/*  89:211 */     super.setOptions(options);
/*  90:    */   }
/*  91:    */   
/*  92:    */   public String numAttributesTipText()
/*  93:    */   {
/*  94:221 */     return "The number of attributes to choose: < 1 percentage, >= 1 absolute number.";
/*  95:    */   }
/*  96:    */   
/*  97:    */   public double getNumAttributes()
/*  98:    */   {
/*  99:230 */     return this.m_NumAttributes;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void setNumAttributes(double value)
/* 103:    */   {
/* 104:239 */     this.m_NumAttributes = value;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public String invertSelectionTipText()
/* 108:    */   {
/* 109:249 */     return "Randomly remove rather than select attributes.";
/* 110:    */   }
/* 111:    */   
/* 112:    */   public void setInvertSelection(boolean inv)
/* 113:    */   {
/* 114:259 */     this.m_invertSelection = inv;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public boolean getInvertSelection()
/* 118:    */   {
/* 119:269 */     return this.m_invertSelection;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public String seedTipText()
/* 123:    */   {
/* 124:279 */     return "The seed value for the random number generator.";
/* 125:    */   }
/* 126:    */   
/* 127:    */   public int getSeed()
/* 128:    */   {
/* 129:288 */     return this.m_Seed;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public void setSeed(int value)
/* 133:    */   {
/* 134:297 */     this.m_Seed = value;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public Capabilities getCapabilities()
/* 138:    */   {
/* 139:308 */     Capabilities result = super.getCapabilities();
/* 140:309 */     result.disableAll();
/* 141:    */     
/* 142:    */ 
/* 143:312 */     result.enableAllAttributes();
/* 144:313 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 145:    */     
/* 146:    */ 
/* 147:316 */     result.enableAllClasses();
/* 148:317 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 149:318 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 150:    */     
/* 151:320 */     return result;
/* 152:    */   }
/* 153:    */   
/* 154:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 155:    */     throws Exception
/* 156:    */   {
/* 157:347 */     int numAtts = inputFormat.numAttributes();
/* 158:348 */     if (inputFormat.classIndex() > -1) {
/* 159:349 */       numAtts--;
/* 160:    */     }
/* 161:352 */     if (this.m_NumAttributes < 1.0D) {
/* 162:353 */       numAtts = (int)Math.round(numAtts * this.m_NumAttributes);
/* 163:355 */     } else if (this.m_NumAttributes < numAtts) {
/* 164:356 */       numAtts = (int)this.m_NumAttributes;
/* 165:    */     }
/* 166:359 */     if (getDebug()) {
/* 167:360 */       System.out.println("# of atts: " + numAtts);
/* 168:    */     }
/* 169:364 */     Vector<Integer> indices = new Vector();
/* 170:365 */     for (int i = 0; i < inputFormat.numAttributes(); i++) {
/* 171:366 */       if (i != inputFormat.classIndex()) {
/* 172:369 */         indices.add(Integer.valueOf(i));
/* 173:    */       }
/* 174:    */     }
/* 175:372 */     Vector<Integer> subset = new Vector();
/* 176:373 */     Random rand = new Random(this.m_Seed);
/* 177:374 */     for (i = 0; i < numAtts; i++)
/* 178:    */     {
/* 179:375 */       int index = rand.nextInt(indices.size());
/* 180:376 */       subset.add(indices.get(index));
/* 181:377 */       indices.remove(index);
/* 182:    */     }
/* 183:380 */     if (this.m_invertSelection) {
/* 184:381 */       subset = indices;
/* 185:    */     }
/* 186:384 */     Collections.sort(subset);
/* 187:385 */     if (inputFormat.classIndex() > -1) {
/* 188:386 */       subset.add(Integer.valueOf(inputFormat.classIndex()));
/* 189:    */     }
/* 190:388 */     if (getDebug()) {
/* 191:389 */       System.out.println("indices: " + subset);
/* 192:    */     }
/* 193:393 */     ArrayList<Attribute> atts = new ArrayList();
/* 194:394 */     this.m_Indices = new int[subset.size()];
/* 195:395 */     for (i = 0; i < subset.size(); i++)
/* 196:    */     {
/* 197:396 */       atts.add(inputFormat.attribute(((Integer)subset.get(i)).intValue()));
/* 198:397 */       this.m_Indices[i] = ((Integer)subset.get(i)).intValue();
/* 199:    */     }
/* 200:399 */     Instances result = new Instances(inputFormat.relationName(), atts, 0);
/* 201:400 */     if (inputFormat.classIndex() > -1) {
/* 202:401 */       result.setClassIndex(result.numAttributes() - 1);
/* 203:    */     }
/* 204:404 */     return result;
/* 205:    */   }
/* 206:    */   
/* 207:    */   protected Instance process(Instance instance)
/* 208:    */     throws Exception
/* 209:    */   {
/* 210:421 */     double[] values = new double[this.m_Indices.length];
/* 211:422 */     for (int i = 0; i < this.m_Indices.length; i++) {
/* 212:423 */       values[i] = instance.value(this.m_Indices[i]);
/* 213:    */     }
/* 214:426 */     Instance result = new DenseInstance(instance.weight(), values);
/* 215:    */     
/* 216:428 */     copyValues(result, false, instance.dataset(), outputFormatPeek());
/* 217:    */     
/* 218:430 */     return result;
/* 219:    */   }
/* 220:    */   
/* 221:    */   public String getRevision()
/* 222:    */   {
/* 223:440 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 224:    */   }
/* 225:    */   
/* 226:    */   public static void main(String[] args)
/* 227:    */   {
/* 228:449 */     runFilter(new RandomSubset(), args);
/* 229:    */   }
/* 230:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.RandomSubset
 * JD-Core Version:    0.7.0.1
 */