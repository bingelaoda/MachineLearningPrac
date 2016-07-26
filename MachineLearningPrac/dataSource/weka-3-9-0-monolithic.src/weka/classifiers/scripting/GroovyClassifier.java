/*   1:    */ package weka.classifiers.scripting;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.classifiers.AbstractClassifier;
/*   9:    */ import weka.classifiers.Classifier;
/*  10:    */ import weka.core.Capabilities;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.OptionHandler;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.Utils;
/*  17:    */ import weka.core.scripting.Groovy;
/*  18:    */ 
/*  19:    */ public class GroovyClassifier
/*  20:    */   extends AbstractClassifier
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = -9078371491735496175L;
/*  23: 84 */   protected File m_GroovyModule = new File(System.getProperty("user.dir"));
/*  24: 87 */   protected String[] m_GroovyOptions = new String[0];
/*  25: 90 */   protected transient Classifier m_GroovyObject = null;
/*  26:    */   
/*  27:    */   public String globalInfo()
/*  28:    */   {
/*  29:106 */     return "A wrapper class for Groovy code. Even though the classifier is serializable, the trained classifier cannot be stored persistently. I.e., one cannot store a model file and re-load it at a later point in time again to make predictions.";
/*  30:    */   }
/*  31:    */   
/*  32:    */   public Enumeration<Option> listOptions()
/*  33:    */   {
/*  34:120 */     Vector<Option> result = new Vector();
/*  35:    */     
/*  36:122 */     result.addElement(new Option("\tThe Groovy module to load (full path)\n\tOptions after '--' will be passed on to the Groovy module.", "G", 1, "-G <filename>"));
/*  37:    */     
/*  38:    */ 
/*  39:    */ 
/*  40:126 */     result.addAll(Collections.list(super.listOptions()));
/*  41:    */     
/*  42:128 */     return result.elements();
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setOptions(String[] options)
/*  46:    */     throws Exception
/*  47:    */   {
/*  48:141 */     this.m_GroovyOptions = new String[0];
/*  49:    */     
/*  50:143 */     String tmpStr = Utils.getOption('G', options);
/*  51:144 */     if (tmpStr.length() != 0) {
/*  52:145 */       setGroovyModule(new File(tmpStr));
/*  53:    */     } else {
/*  54:147 */       setGroovyModule(new File(System.getProperty("user.dir")));
/*  55:    */     }
/*  56:150 */     setGroovyOptions(Utils.joinOptions((String[])Utils.partitionOptions(options).clone()));
/*  57:    */     
/*  58:152 */     super.setOptions(options);
/*  59:    */     
/*  60:154 */     Utils.checkForRemainingOptions(options);
/*  61:    */   }
/*  62:    */   
/*  63:    */   public String[] getOptions()
/*  64:    */   {
/*  65:165 */     Vector<String> result = new Vector();
/*  66:    */     
/*  67:167 */     result.add("-G");
/*  68:168 */     result.add("" + getGroovyModule().getAbsolutePath());
/*  69:    */     
/*  70:170 */     Collections.addAll(result, super.getOptions());
/*  71:172 */     if (this.m_GroovyOptions.length > 0)
/*  72:    */     {
/*  73:173 */       String[] options = this.m_GroovyOptions;
/*  74:174 */       result.add("--");
/*  75:175 */       for (String option : options) {
/*  76:176 */         result.add(option);
/*  77:    */       }
/*  78:    */     }
/*  79:180 */     return (String[])result.toArray(new String[result.size()]);
/*  80:    */   }
/*  81:    */   
/*  82:    */   public String GroovyModuleTipText()
/*  83:    */   {
/*  84:190 */     return "The Groovy module to load and execute.";
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void setGroovyModule(File value)
/*  88:    */   {
/*  89:199 */     this.m_GroovyModule = value;
/*  90:200 */     initGroovyObject();
/*  91:    */   }
/*  92:    */   
/*  93:    */   public File getGroovyModule()
/*  94:    */   {
/*  95:209 */     return this.m_GroovyModule;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public String GroovyOptionsTipText()
/*  99:    */   {
/* 100:219 */     return "The options for the Groovy module.";
/* 101:    */   }
/* 102:    */   
/* 103:    */   public void setGroovyOptions(String value)
/* 104:    */   {
/* 105:    */     try
/* 106:    */     {
/* 107:229 */       this.m_GroovyOptions = ((String[])Utils.splitOptions(value).clone());
/* 108:230 */       initGroovyObject();
/* 109:    */     }
/* 110:    */     catch (Exception e)
/* 111:    */     {
/* 112:232 */       this.m_GroovyOptions = new String[0];
/* 113:233 */       e.printStackTrace();
/* 114:    */     }
/* 115:    */   }
/* 116:    */   
/* 117:    */   public String getGroovyOptions()
/* 118:    */   {
/* 119:243 */     return Utils.joinOptions(this.m_GroovyOptions);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public Capabilities getCapabilities()
/* 123:    */   {
/* 124:    */     Capabilities result;
/* 125:255 */     if (this.m_GroovyObject == null)
/* 126:    */     {
/* 127:256 */       Capabilities result = new Capabilities(this);
/* 128:257 */       result.disableAll();
/* 129:    */     }
/* 130:    */     else
/* 131:    */     {
/* 132:259 */       result = this.m_GroovyObject.getCapabilities();
/* 133:    */     }
/* 134:262 */     result.enableAllAttributeDependencies();
/* 135:263 */     result.enableAllClassDependencies();
/* 136:    */     
/* 137:265 */     return result;
/* 138:    */   }
/* 139:    */   
/* 140:    */   protected void initGroovyObject()
/* 141:    */   {
/* 142:    */     try
/* 143:    */     {
/* 144:273 */       if (this.m_GroovyModule.isFile()) {
/* 145:274 */         this.m_GroovyObject = ((Classifier)Groovy.newInstance(this.m_GroovyModule, Classifier.class));
/* 146:    */       } else {
/* 147:277 */         this.m_GroovyObject = null;
/* 148:    */       }
/* 149:280 */       if (this.m_GroovyObject != null) {
/* 150:281 */         ((OptionHandler)this.m_GroovyObject).setOptions((String[])this.m_GroovyOptions.clone());
/* 151:    */       }
/* 152:    */     }
/* 153:    */     catch (Exception e)
/* 154:    */     {
/* 155:284 */       this.m_GroovyObject = null;
/* 156:285 */       e.printStackTrace();
/* 157:    */     }
/* 158:    */   }
/* 159:    */   
/* 160:    */   public void buildClassifier(Instances instances)
/* 161:    */     throws Exception
/* 162:    */   {
/* 163:297 */     if (!Groovy.isPresent()) {
/* 164:298 */       throw new Exception("Groovy classes not in CLASSPATH!");
/* 165:    */     }
/* 166:302 */     initGroovyObject();
/* 167:305 */     if (this.m_GroovyObject != null) {
/* 168:306 */       this.m_GroovyObject.buildClassifier(instances);
/* 169:    */     } else {
/* 170:308 */       System.err.println("buildClassifier: No Groovy object present!");
/* 171:    */     }
/* 172:    */   }
/* 173:    */   
/* 174:    */   public double classifyInstance(Instance instance)
/* 175:    */     throws Exception
/* 176:    */   {
/* 177:321 */     if (this.m_GroovyObject != null) {
/* 178:322 */       return this.m_GroovyObject.classifyInstance(instance);
/* 179:    */     }
/* 180:324 */     return Utils.missingValue();
/* 181:    */   }
/* 182:    */   
/* 183:    */   public double[] distributionForInstance(Instance instance)
/* 184:    */     throws Exception
/* 185:    */   {
/* 186:337 */     if (this.m_GroovyObject != null) {
/* 187:338 */       return this.m_GroovyObject.distributionForInstance(instance);
/* 188:    */     }
/* 189:340 */     return new double[instance.numClasses()];
/* 190:    */   }
/* 191:    */   
/* 192:    */   public String toString()
/* 193:    */   {
/* 194:351 */     if (this.m_GroovyObject != null) {
/* 195:352 */       return this.m_GroovyObject.toString();
/* 196:    */     }
/* 197:354 */     return "No Groovy module loaded.";
/* 198:    */   }
/* 199:    */   
/* 200:    */   public String getRevision()
/* 201:    */   {
/* 202:365 */     return RevisionUtils.extract("$Revision: 10375 $");
/* 203:    */   }
/* 204:    */   
/* 205:    */   public static void main(String[] args)
/* 206:    */   {
/* 207:374 */     runClassifier(new GroovyClassifier(), args);
/* 208:    */   }
/* 209:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.scripting.GroovyClassifier
 * JD-Core Version:    0.7.0.1
 */