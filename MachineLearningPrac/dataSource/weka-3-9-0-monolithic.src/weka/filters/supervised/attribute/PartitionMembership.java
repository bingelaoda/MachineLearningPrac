/*   1:    */ package weka.filters.supervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.trees.J48;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.PartitionGenerator;
/*  15:    */ import weka.core.RevisionHandler;
/*  16:    */ import weka.core.RevisionUtils;
/*  17:    */ import weka.core.SparseInstance;
/*  18:    */ import weka.core.TechnicalInformation;
/*  19:    */ import weka.core.TechnicalInformation.Field;
/*  20:    */ import weka.core.TechnicalInformation.Type;
/*  21:    */ import weka.core.TechnicalInformationHandler;
/*  22:    */ import weka.core.Utils;
/*  23:    */ import weka.filters.Filter;
/*  24:    */ import weka.filters.SupervisedFilter;
/*  25:    */ 
/*  26:    */ public class PartitionMembership
/*  27:    */   extends Filter
/*  28:    */   implements SupervisedFilter, OptionHandler, RevisionHandler, TechnicalInformationHandler
/*  29:    */ {
/*  30:    */   static final long serialVersionUID = 333532554667754026L;
/*  31: 65 */   protected PartitionGenerator m_partitionGenerator = new J48();
/*  32:    */   
/*  33:    */   public Capabilities getCapabilities()
/*  34:    */   {
/*  35: 76 */     Capabilities result = this.m_partitionGenerator.getCapabilities();
/*  36:    */     
/*  37: 78 */     result.setMinimumNumberInstances(0);
/*  38:    */     
/*  39: 80 */     return result;
/*  40:    */   }
/*  41:    */   
/*  42:    */   protected void testInputFormat(Instances instanceInfo)
/*  43:    */     throws Exception
/*  44:    */   {
/*  45: 92 */     getCapabilities().testWithFail(instanceInfo);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public boolean setInputFormat(Instances instanceInfo)
/*  49:    */     throws Exception
/*  50:    */   {
/*  51:107 */     super.setInputFormat(instanceInfo);
/*  52:    */     
/*  53:109 */     return false;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public boolean batchFinished()
/*  57:    */     throws Exception
/*  58:    */   {
/*  59:121 */     if (getInputFormat() == null) {
/*  60:122 */       throw new IllegalStateException("No input instance format defined");
/*  61:    */     }
/*  62:125 */     if (outputFormatPeek() == null)
/*  63:    */     {
/*  64:126 */       Instances toFilter = getInputFormat();
/*  65:    */       
/*  66:    */ 
/*  67:129 */       this.m_partitionGenerator.generatePartition(toFilter);
/*  68:    */       
/*  69:    */ 
/*  70:132 */       ArrayList<Attribute> attInfo = new ArrayList();
/*  71:133 */       for (int i = 0; i < this.m_partitionGenerator.numElements(); i++) {
/*  72:134 */         attInfo.add(new Attribute("partition_" + i));
/*  73:    */       }
/*  74:136 */       if (toFilter.classIndex() >= 0) {
/*  75:137 */         attInfo.add((Attribute)toFilter.classAttribute().copy());
/*  76:    */       }
/*  77:139 */       attInfo.trimToSize();
/*  78:140 */       Instances filtered = new Instances(toFilter.relationName() + "_partitionMembership", attInfo, 0);
/*  79:142 */       if (toFilter.classIndex() >= 0) {
/*  80:143 */         filtered.setClassIndex(filtered.numAttributes() - 1);
/*  81:    */       }
/*  82:145 */       setOutputFormat(filtered);
/*  83:148 */       for (int i = 0; i < toFilter.numInstances(); i++) {
/*  84:149 */         convertInstance(toFilter.instance(i));
/*  85:    */       }
/*  86:    */     }
/*  87:152 */     flushInput();
/*  88:    */     
/*  89:154 */     this.m_NewBatch = true;
/*  90:155 */     return numPendingOutput() != 0;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public boolean input(Instance instance)
/*  94:    */     throws Exception
/*  95:    */   {
/*  96:170 */     if (getInputFormat() == null) {
/*  97:171 */       throw new IllegalStateException("No input instance format defined");
/*  98:    */     }
/*  99:173 */     if (this.m_NewBatch)
/* 100:    */     {
/* 101:174 */       resetQueue();
/* 102:175 */       this.m_NewBatch = false;
/* 103:    */     }
/* 104:178 */     if (outputFormatPeek() != null)
/* 105:    */     {
/* 106:179 */       convertInstance(instance);
/* 107:180 */       return true;
/* 108:    */     }
/* 109:183 */     bufferInput(instance);
/* 110:184 */     return false;
/* 111:    */   }
/* 112:    */   
/* 113:    */   protected void convertInstance(Instance instance)
/* 114:    */     throws Exception
/* 115:    */   {
/* 116:197 */     Instance cp = (Instance)instance.copy();
/* 117:198 */     cp.setWeight(1.0D);
/* 118:    */     
/* 119:    */ 
/* 120:201 */     double[] instanceVals = new double[outputFormatPeek().numAttributes()];
/* 121:202 */     double[] vals = this.m_partitionGenerator.getMembershipValues(cp);
/* 122:203 */     System.arraycopy(vals, 0, instanceVals, 0, vals.length);
/* 123:204 */     if (instance.classIndex() >= 0) {
/* 124:205 */       instanceVals[(instanceVals.length - 1)] = instance.classValue();
/* 125:    */     }
/* 126:208 */     push(new SparseInstance(instance.weight(), instanceVals));
/* 127:    */   }
/* 128:    */   
/* 129:    */   public Enumeration<Option> listOptions()
/* 130:    */   {
/* 131:219 */     Vector<Option> newVector = new Vector(1);
/* 132:    */     
/* 133:221 */     newVector.addElement(new Option("\tFull name of partition generator to use, e.g.:\n\t\tweka.classifiers.trees.J48\n\tAdditional options after the '--'.\n\t(default: weka.classifiers.trees.J48)", "W", 1, "-W <name of partition generator>"));
/* 134:    */     
/* 135:    */ 
/* 136:    */ 
/* 137:    */ 
/* 138:    */ 
/* 139:    */ 
/* 140:228 */     return newVector.elements();
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void setOptions(String[] options)
/* 144:    */     throws Exception
/* 145:    */   {
/* 146:254 */     String generatorString = Utils.getOption('W', options);
/* 147:255 */     if (generatorString.length() == 0) {
/* 148:256 */       generatorString = J48.class.getName();
/* 149:    */     }
/* 150:258 */     setPartitionGenerator((PartitionGenerator)Utils.forName(PartitionGenerator.class, generatorString, Utils.partitionOptions(options)));
/* 151:    */     
/* 152:    */ 
/* 153:    */ 
/* 154:262 */     Utils.checkForRemainingOptions(options);
/* 155:    */   }
/* 156:    */   
/* 157:    */   public String[] getOptions()
/* 158:    */   {
/* 159:273 */     Vector<String> options = new Vector();
/* 160:275 */     if (this.m_partitionGenerator != null)
/* 161:    */     {
/* 162:276 */       options.add("-W");
/* 163:277 */       options.add(getPartitionGenerator().getClass().getName());
/* 164:    */     }
/* 165:280 */     if ((this.m_partitionGenerator != null) && ((this.m_partitionGenerator instanceof OptionHandler)))
/* 166:    */     {
/* 167:282 */       String[] generatorOptions = ((OptionHandler)this.m_partitionGenerator).getOptions();
/* 168:285 */       if (generatorOptions.length > 0)
/* 169:    */       {
/* 170:286 */         options.add("--");
/* 171:287 */         Collections.addAll(options, generatorOptions);
/* 172:    */       }
/* 173:    */     }
/* 174:290 */     return (String[])options.toArray(new String[0]);
/* 175:    */   }
/* 176:    */   
/* 177:    */   public String globalInfo()
/* 178:    */   {
/* 179:301 */     return "A filter that uses a PartitionGenerator to generate partition membership values; filtered instances are composed of these values plus the class attribute (if set in the input data) and rendered as sparse instances. See Section 3 of\n" + getTechnicalInformation().toString();
/* 180:    */   }
/* 181:    */   
/* 182:    */   public TechnicalInformation getTechnicalInformation()
/* 183:    */   {
/* 184:318 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/* 185:319 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Eibe Frank and Bernhard Pfahringer");
/* 186:320 */     result.setValue(TechnicalInformation.Field.TITLE, "Propositionalisation of Multi-instance Data Using Random Forests");
/* 187:    */     
/* 188:322 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "AI 2013: Advances in Artificial Intelligence");
/* 189:323 */     result.setValue(TechnicalInformation.Field.YEAR, "2013");
/* 190:324 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/* 191:325 */     result.setValue(TechnicalInformation.Field.PAGES, "362-373");
/* 192:    */     
/* 193:327 */     return result;
/* 194:    */   }
/* 195:    */   
/* 196:    */   public String partitionGeneratorTipText()
/* 197:    */   {
/* 198:337 */     return "The partition generator that will generate membership values for the instances.";
/* 199:    */   }
/* 200:    */   
/* 201:    */   public void setPartitionGenerator(PartitionGenerator newPartitionGenerator)
/* 202:    */   {
/* 203:346 */     this.m_partitionGenerator = newPartitionGenerator;
/* 204:    */   }
/* 205:    */   
/* 206:    */   public PartitionGenerator getPartitionGenerator()
/* 207:    */   {
/* 208:355 */     return this.m_partitionGenerator;
/* 209:    */   }
/* 210:    */   
/* 211:    */   public static void main(String[] argv)
/* 212:    */   {
/* 213:364 */     runFilter(new PartitionMembership(), argv);
/* 214:    */   }
/* 215:    */   
/* 216:    */   public String getRevision()
/* 217:    */   {
/* 218:369 */     return RevisionUtils.extract("$Revision: 11932 $");
/* 219:    */   }
/* 220:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.supervised.attribute.PartitionMembership
 * JD-Core Version:    0.7.0.1
 */