/*   1:    */ package weka.classifiers.meta;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.Classifier;
/*   8:    */ import weka.classifiers.lazy.IBk;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.OptionHandler;
/*  13:    */ import weka.core.Randomizable;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.Utils;
/*  16:    */ import weka.filters.Filter;
/*  17:    */ import weka.filters.unsupervised.attribute.RandomProjection;
/*  18:    */ 
/*  19:    */ public class RandomizableFilteredClassifier
/*  20:    */   extends FilteredClassifier
/*  21:    */   implements Randomizable
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = -4523466618555717333L;
/*  24:108 */   protected int m_Seed = 1;
/*  25:    */   
/*  26:    */   public String globalInfo()
/*  27:    */   {
/*  28:116 */     return "A simple variant of the FilteredClassifier that implements the Randomizable interface, useful for building ensemble classifiers using the RandomCommittee meta learner. It requires that either the filter or the base learner implement the Randomizable interface.";
/*  29:    */   }
/*  30:    */   
/*  31:    */   protected String defaultClassifierString()
/*  32:    */   {
/*  33:128 */     return "weka.classifiers.lazy.IBk";
/*  34:    */   }
/*  35:    */   
/*  36:    */   protected String defaultFilterString()
/*  37:    */   {
/*  38:136 */     return "weka.filters.unsupervised.attribute.RandomProjection";
/*  39:    */   }
/*  40:    */   
/*  41:    */   public RandomizableFilteredClassifier()
/*  42:    */   {
/*  43:144 */     this.m_Classifier = new IBk();
/*  44:145 */     this.m_Filter = new RandomProjection();
/*  45:    */   }
/*  46:    */   
/*  47:    */   public Enumeration<Option> listOptions()
/*  48:    */   {
/*  49:155 */     Vector<Option> newVector = new Vector(1);
/*  50:    */     
/*  51:157 */     newVector.addElement(new Option("\tRandom number seed.\n\t(default 1)", "S", 1, "-S <num>"));
/*  52:    */     
/*  53:    */ 
/*  54:    */ 
/*  55:    */ 
/*  56:162 */     newVector.addAll(Collections.list(super.listOptions()));
/*  57:164 */     if ((getFilter() instanceof OptionHandler))
/*  58:    */     {
/*  59:165 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to filter " + getFilter().getClass().getName() + ":"));
/*  60:    */       
/*  61:    */ 
/*  62:    */ 
/*  63:169 */       newVector.addAll(Collections.list(getFilter().listOptions()));
/*  64:    */     }
/*  65:172 */     return newVector.elements();
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void setOptions(String[] options)
/*  69:    */     throws Exception
/*  70:    */   {
/*  71:194 */     String seed = Utils.getOption('S', options);
/*  72:195 */     if (seed.length() != 0) {
/*  73:196 */       setSeed(Integer.parseInt(seed));
/*  74:    */     } else {
/*  75:198 */       setSeed(1);
/*  76:    */     }
/*  77:201 */     super.setOptions(options);
/*  78:    */     
/*  79:203 */     Utils.checkForRemainingOptions(options);
/*  80:    */   }
/*  81:    */   
/*  82:    */   public String[] getOptions()
/*  83:    */   {
/*  84:213 */     Vector<String> options = new Vector();
/*  85:    */     
/*  86:215 */     options.add("-S");
/*  87:216 */     options.add("" + getSeed());
/*  88:    */     
/*  89:218 */     Collections.addAll(options, super.getOptions());
/*  90:    */     
/*  91:220 */     return (String[])options.toArray(new String[0]);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public String seedTipText()
/*  95:    */   {
/*  96:230 */     return "The random number seed to be used.";
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void setSeed(int seed)
/* 100:    */   {
/* 101:240 */     this.m_Seed = seed;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public int getSeed()
/* 105:    */   {
/* 106:250 */     return this.m_Seed;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void buildClassifier(Instances data)
/* 110:    */     throws Exception
/* 111:    */   {
/* 112:261 */     if (this.m_Classifier == null) {
/* 113:262 */       throw new Exception("No base classifiers have been set!");
/* 114:    */     }
/* 115:265 */     if ((!(this.m_Classifier instanceof Randomizable)) && (!(this.m_Filter instanceof Randomizable))) {
/* 116:267 */       throw new Exception("Either the classifier or the filter must implement the Randomizable interface.");
/* 117:    */     }
/* 118:271 */     getCapabilities().testWithFail(data);
/* 119:    */     
/* 120:    */ 
/* 121:274 */     data = new Instances(data);
/* 122:276 */     if (data.numInstances() == 0) {
/* 123:277 */       throw new Exception("No training instances.");
/* 124:    */     }
/* 125:    */     try
/* 126:    */     {
/* 127:283 */       Random r = data.getRandomNumberGenerator(this.m_Seed);
/* 128:285 */       if ((this.m_Filter instanceof Randomizable)) {
/* 129:286 */         ((Randomizable)this.m_Filter).setSeed(r.nextInt());
/* 130:    */       }
/* 131:289 */       this.m_Filter.setInputFormat(data);
/* 132:290 */       data = Filter.useFilter(data, this.m_Filter);
/* 133:    */       
/* 134:    */ 
/* 135:293 */       getClassifier().getCapabilities().testWithFail(data);
/* 136:    */       
/* 137:295 */       this.m_FilteredInstances = data.stringFreeStructure();
/* 138:297 */       if ((this.m_Classifier instanceof Randomizable)) {
/* 139:298 */         ((Randomizable)this.m_Classifier).setSeed(r.nextInt());
/* 140:    */       }
/* 141:300 */       this.m_Classifier.buildClassifier(data);
/* 142:    */     }
/* 143:    */     catch (Exception e)
/* 144:    */     {
/* 145:303 */       e.printStackTrace();
/* 146:304 */       System.exit(1);
/* 147:    */     }
/* 148:    */   }
/* 149:    */   
/* 150:    */   public String toString()
/* 151:    */   {
/* 152:315 */     if (this.m_FilteredInstances == null) {
/* 153:316 */       return "RandomizableFilteredClassifier: No model built yet.";
/* 154:    */     }
/* 155:319 */     String result = "RandomizableFilteredClassifier using " + getClassifierSpec() + " on data filtered through " + getFilterSpec() + "\n\nFiltered Header\n" + this.m_FilteredInstances.toString() + "\n\nClassifier Model\n" + this.m_Classifier.toString();
/* 156:    */     
/* 157:    */ 
/* 158:    */ 
/* 159:    */ 
/* 160:    */ 
/* 161:    */ 
/* 162:    */ 
/* 163:327 */     return result;
/* 164:    */   }
/* 165:    */   
/* 166:    */   public String getRevision()
/* 167:    */   {
/* 168:336 */     return RevisionUtils.extract("$Revision: 9117 $");
/* 169:    */   }
/* 170:    */   
/* 171:    */   public static void main(String[] argv)
/* 172:    */   {
/* 173:346 */     runClassifier(new RandomizableFilteredClassifier(), argv);
/* 174:    */   }
/* 175:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.RandomizableFilteredClassifier
 * JD-Core Version:    0.7.0.1
 */