/*   1:    */ package weka.classifiers.mi;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.Classifier;
/*   7:    */ import weka.classifiers.SingleClassifierEnhancer;
/*   8:    */ import weka.classifiers.meta.LogitBoost;
/*   9:    */ import weka.classifiers.trees.J48;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Capabilities.Capability;
/*  12:    */ import weka.core.Instance;
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.MultiInstanceCapabilitiesHandler;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.OptionHandler;
/*  17:    */ import weka.core.PartitionGenerator;
/*  18:    */ import weka.core.RevisionUtils;
/*  19:    */ import weka.core.TechnicalInformation;
/*  20:    */ import weka.core.TechnicalInformation.Field;
/*  21:    */ import weka.core.TechnicalInformation.Type;
/*  22:    */ import weka.core.TechnicalInformationHandler;
/*  23:    */ import weka.core.Utils;
/*  24:    */ import weka.filters.Filter;
/*  25:    */ import weka.filters.MultiFilter;
/*  26:    */ import weka.filters.supervised.attribute.PartitionMembership;
/*  27:    */ import weka.filters.unsupervised.attribute.MultiInstanceWrapper;
/*  28:    */ import weka.filters.unsupervised.attribute.Remove;
/*  29:    */ 
/*  30:    */ public class TLC
/*  31:    */   extends SingleClassifierEnhancer
/*  32:    */   implements TechnicalInformationHandler, MultiInstanceCapabilitiesHandler
/*  33:    */ {
/*  34:    */   private static final long serialVersionUID = -4444591375578585231L;
/*  35:252 */   protected PartitionGenerator m_partitionGenerator = new J48();
/*  36:255 */   protected MultiFilter m_Filter = null;
/*  37:    */   
/*  38:    */   public String globalInfo()
/*  39:    */   {
/*  40:264 */     return "Implements basic two-level classification method for multi-instance data, without attribute selection.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  41:    */   }
/*  42:    */   
/*  43:    */   public TechnicalInformation getTechnicalInformation()
/*  44:    */   {
/*  45:282 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  46:283 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Nils Weidmann and Eibe Frank and Bernhard Pfahringer");
/*  47:    */     
/*  48:285 */     result.setValue(TechnicalInformation.Field.TITLE, "A two-level learning method for generalized multi-instance problems");
/*  49:    */     
/*  50:287 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Fourteenth European Conference on Machine Learning");
/*  51:    */     
/*  52:289 */     result.setValue(TechnicalInformation.Field.YEAR, "2003");
/*  53:290 */     result.setValue(TechnicalInformation.Field.PAGES, "468-479");
/*  54:291 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*  55:    */     
/*  56:293 */     return result;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public TLC()
/*  60:    */   {
/*  61:301 */     this.m_Classifier = new LogitBoost();
/*  62:    */   }
/*  63:    */   
/*  64:    */   protected String defaultClassifierString()
/*  65:    */   {
/*  66:310 */     return "weka.classifiers.meta.LogitBoost";
/*  67:    */   }
/*  68:    */   
/*  69:    */   public String partitionGeneratorTipText()
/*  70:    */   {
/*  71:321 */     return "The partition generator that will generate membership values for the instances.";
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setPartitionGenerator(PartitionGenerator newPartitionGenerator)
/*  75:    */   {
/*  76:331 */     this.m_partitionGenerator = newPartitionGenerator;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public PartitionGenerator getPartitionGenerator()
/*  80:    */   {
/*  81:341 */     return this.m_partitionGenerator;
/*  82:    */   }
/*  83:    */   
/*  84:    */   protected String getPartitionGeneratorSpec()
/*  85:    */   {
/*  86:352 */     PartitionGenerator c = getPartitionGenerator();
/*  87:353 */     if ((c instanceof OptionHandler)) {
/*  88:354 */       return c.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)c).getOptions());
/*  89:    */     }
/*  90:357 */     return c.getClass().getName();
/*  91:    */   }
/*  92:    */   
/*  93:    */   public Enumeration<Option> listOptions()
/*  94:    */   {
/*  95:368 */     Vector<Option> newVector = new Vector(1);
/*  96:    */     
/*  97:370 */     newVector.addElement(new Option("\tPartition generator to use, including options.\n\tQuotes are needed when options are specified.\n\t(default: weka.classifiers.trees.J48)", "P", 1, "-P \"<name and options of partition generator>\""));
/*  98:    */     
/*  99:    */ 
/* 100:    */ 
/* 101:    */ 
/* 102:    */ 
/* 103:376 */     newVector.addAll(Collections.list(super.listOptions()));
/* 104:    */     
/* 105:378 */     newVector.addElement(new Option("", "", 0, "\nOptions specific to partition generator " + getPartitionGenerator().getClass().getName() + ":"));
/* 106:    */     
/* 107:    */ 
/* 108:    */ 
/* 109:382 */     newVector.addAll(Collections.list(((OptionHandler)getPartitionGenerator()).listOptions()));
/* 110:    */     
/* 111:    */ 
/* 112:385 */     return newVector.elements();
/* 113:    */   }
/* 114:    */   
/* 115:    */   public void setOptions(String[] options)
/* 116:    */     throws Exception
/* 117:    */   {
/* 118:554 */     String partitionGeneratorString = Utils.getOption('P', options);
/* 119:555 */     if (partitionGeneratorString.length() > 0)
/* 120:    */     {
/* 121:556 */       String[] partitionGeneratorSpec = Utils.splitOptions(partitionGeneratorString);
/* 122:558 */       if (partitionGeneratorSpec.length == 0) {
/* 123:559 */         throw new IllegalArgumentException("Invalid partition generator specification string");
/* 124:    */       }
/* 125:562 */       String partitionGeneratorName = partitionGeneratorSpec[0];
/* 126:563 */       partitionGeneratorSpec[0] = "";
/* 127:564 */       setPartitionGenerator((PartitionGenerator)Utils.forName(PartitionGenerator.class, partitionGeneratorName, partitionGeneratorSpec));
/* 128:    */     }
/* 129:    */     else
/* 130:    */     {
/* 131:568 */       setPartitionGenerator(new J48());
/* 132:    */     }
/* 133:570 */     super.setOptions(options);
/* 134:571 */     Utils.checkForRemainingOptions(options);
/* 135:    */   }
/* 136:    */   
/* 137:    */   public String[] getOptions()
/* 138:    */   {
/* 139:582 */     Vector<String> options = new Vector();
/* 140:    */     
/* 141:584 */     options.add("-P");
/* 142:585 */     options.add("" + getPartitionGeneratorSpec());
/* 143:    */     
/* 144:587 */     Collections.addAll(options, super.getOptions());
/* 145:    */     
/* 146:589 */     return (String[])options.toArray(new String[0]);
/* 147:    */   }
/* 148:    */   
/* 149:    */   public void buildClassifier(Instances data)
/* 150:    */     throws Exception
/* 151:    */   {
/* 152:598 */     data = new Instances(data);
/* 153:599 */     data.deleteWithMissingClass();
/* 154:    */     
/* 155:601 */     getCapabilities().testWithFail(data);
/* 156:    */     
/* 157:603 */     this.m_Filter = new MultiFilter();
/* 158:604 */     Filter[] twoFilters = new Filter[2];
/* 159:605 */     PartitionMembership pm = new PartitionMembership();
/* 160:606 */     pm.setPartitionGenerator(getPartitionGenerator());
/* 161:607 */     MultiInstanceWrapper miw = new MultiInstanceWrapper();
/* 162:608 */     miw.setFilter(pm);
/* 163:609 */     twoFilters[0] = miw;
/* 164:610 */     twoFilters[1] = new Remove();
/* 165:611 */     ((Remove)twoFilters[1]).setAttributeIndices("1");
/* 166:612 */     this.m_Filter.setFilters(twoFilters);
/* 167:613 */     this.m_Filter.setInputFormat(data);
/* 168:614 */     Instances propositionalData = Filter.useFilter(data, this.m_Filter);
/* 169:    */     
/* 170:    */ 
/* 171:617 */     getClassifier().getCapabilities().testWithFail(propositionalData);
/* 172:    */     
/* 173:619 */     this.m_Classifier.buildClassifier(propositionalData);
/* 174:    */   }
/* 175:    */   
/* 176:    */   public String toString()
/* 177:    */   {
/* 178:628 */     if (this.m_Classifier == null) {
/* 179:629 */       return "Classifier not built yet.";
/* 180:    */     }
/* 181:631 */     return "Partition Generator:\n\n" + getPartitionGenerator().toString() + "\n\nClassifier:\n\n" + getClassifier().toString();
/* 182:    */   }
/* 183:    */   
/* 184:    */   public double[] distributionForInstance(Instance inst)
/* 185:    */     throws Exception
/* 186:    */   {
/* 187:641 */     inst = (Instance)inst.copy();
/* 188:642 */     this.m_Filter.input(inst);
/* 189:643 */     this.m_Filter.batchFinished();
/* 190:644 */     return this.m_Classifier.distributionForInstance(this.m_Filter.output());
/* 191:    */   }
/* 192:    */   
/* 193:    */   public String getRevision()
/* 194:    */   {
/* 195:655 */     return RevisionUtils.extract("$Revision: 10369 $");
/* 196:    */   }
/* 197:    */   
/* 198:    */   public Capabilities getCapabilities()
/* 199:    */   {
/* 200:667 */     Capabilities result = super.getCapabilities();
/* 201:668 */     result.disableAll();
/* 202:    */     
/* 203:    */ 
/* 204:671 */     result.disableAllAttributes();
/* 205:672 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 206:673 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/* 207:674 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 208:    */     
/* 209:    */ 
/* 210:677 */     result.disableAllClasses();
/* 211:678 */     result.disableAllClassDependencies();
/* 212:679 */     if (super.getCapabilities().handles(Capabilities.Capability.NOMINAL_CLASS)) {
/* 213:680 */       result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 214:    */     }
/* 215:682 */     if (super.getCapabilities().handles(Capabilities.Capability.BINARY_CLASS)) {
/* 216:683 */       result.enable(Capabilities.Capability.BINARY_CLASS);
/* 217:    */     }
/* 218:685 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 219:    */     
/* 220:    */ 
/* 221:688 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/* 222:    */     
/* 223:690 */     return result;
/* 224:    */   }
/* 225:    */   
/* 226:    */   public Capabilities getMultiInstanceCapabilities()
/* 227:    */   {
/* 228:703 */     Capabilities result = this.m_partitionGenerator.getCapabilities();
/* 229:704 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 230:    */     
/* 231:    */ 
/* 232:707 */     result.setMinimumNumberInstances(0);
/* 233:    */     
/* 234:709 */     return result;
/* 235:    */   }
/* 236:    */   
/* 237:    */   public static void main(String[] options)
/* 238:    */   {
/* 239:717 */     runClassifier(new TLC(), options);
/* 240:    */   }
/* 241:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.TLC
 * JD-Core Version:    0.7.0.1
 */