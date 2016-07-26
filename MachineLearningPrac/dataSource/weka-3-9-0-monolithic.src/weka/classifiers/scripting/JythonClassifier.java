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
/*  17:    */ import weka.core.scripting.Jython;
/*  18:    */ 
/*  19:    */ public class JythonClassifier
/*  20:    */   extends AbstractClassifier
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = -9078371491735496175L;
/*  23: 85 */   protected File m_JythonModule = new File(System.getProperty("user.dir"));
/*  24: 88 */   protected String[] m_JythonOptions = new String[0];
/*  25: 91 */   protected File[] m_JythonPaths = new File[0];
/*  26: 94 */   protected transient Classifier m_JythonObject = null;
/*  27:    */   
/*  28:    */   public String globalInfo()
/*  29:    */   {
/*  30:110 */     return "A wrapper class for Jython code. Even though the classifier is serializable, the trained classifier cannot be stored persistently. I.e., one cannot store a model file and re-load it at a later point in time again to make predictions.";
/*  31:    */   }
/*  32:    */   
/*  33:    */   public Enumeration<Option> listOptions()
/*  34:    */   {
/*  35:123 */     Vector<Option> result = new Vector();
/*  36:    */     
/*  37:125 */     result.addElement(new Option("\tThe Jython module to load (full path)\n\tOptions after '--' will be passed on to the Jython module.", "J", 1, "-J <filename>"));
/*  38:    */     
/*  39:    */ 
/*  40:    */ 
/*  41:129 */     result.addElement(new Option("\tThe paths to add to 'sys.path' (comma-separated list).", "P", 1, "-P <directory[,directory,...]>"));
/*  42:    */     
/*  43:    */ 
/*  44:    */ 
/*  45:133 */     result.addAll(Collections.list(super.listOptions()));
/*  46:    */     
/*  47:135 */     return result.elements();
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setOptions(String[] options)
/*  51:    */     throws Exception
/*  52:    */   {
/*  53:148 */     this.m_JythonOptions = new String[0];
/*  54:    */     
/*  55:150 */     setJythonPaths(Utils.getOption('P', options));
/*  56:    */     
/*  57:152 */     String tmpStr = Utils.getOption('J', options);
/*  58:153 */     if (tmpStr.length() != 0) {
/*  59:154 */       setJythonModule(new File(tmpStr));
/*  60:    */     } else {
/*  61:156 */       setJythonModule(new File(System.getProperty("user.dir")));
/*  62:    */     }
/*  63:159 */     setJythonOptions(Utils.joinOptions((String[])Utils.partitionOptions(options).clone()));
/*  64:    */     
/*  65:161 */     super.setOptions(options);
/*  66:    */     
/*  67:163 */     Utils.checkForRemainingOptions(options);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public String[] getOptions()
/*  71:    */   {
/*  72:174 */     Vector<String> result = new Vector();
/*  73:176 */     if (getJythonPaths().length() > 0)
/*  74:    */     {
/*  75:177 */       result.add("-P");
/*  76:178 */       result.add("" + getJythonPaths());
/*  77:    */     }
/*  78:181 */     result.add("-J");
/*  79:182 */     result.add("" + getJythonModule().getAbsolutePath());
/*  80:    */     
/*  81:184 */     Collections.addAll(result, super.getOptions());
/*  82:186 */     if (this.m_JythonOptions.length > 0)
/*  83:    */     {
/*  84:187 */       String[] options = this.m_JythonOptions;
/*  85:188 */       result.add("--");
/*  86:189 */       for (String option : options) {
/*  87:190 */         result.add(option);
/*  88:    */       }
/*  89:    */     }
/*  90:194 */     return (String[])result.toArray(new String[result.size()]);
/*  91:    */   }
/*  92:    */   
/*  93:    */   public String jythonModuleTipText()
/*  94:    */   {
/*  95:204 */     return "The Jython module to load and execute.";
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void setJythonModule(File value)
/*  99:    */   {
/* 100:213 */     this.m_JythonModule = value;
/* 101:214 */     initJythonObject();
/* 102:    */   }
/* 103:    */   
/* 104:    */   public File getJythonModule()
/* 105:    */   {
/* 106:223 */     return this.m_JythonModule;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public String jythonOptionsTipText()
/* 110:    */   {
/* 111:233 */     return "The options for the Jython module.";
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void setJythonOptions(String value)
/* 115:    */   {
/* 116:    */     try
/* 117:    */     {
/* 118:243 */       this.m_JythonOptions = ((String[])Utils.splitOptions(value).clone());
/* 119:244 */       initJythonObject();
/* 120:    */     }
/* 121:    */     catch (Exception e)
/* 122:    */     {
/* 123:246 */       this.m_JythonOptions = new String[0];
/* 124:247 */       e.printStackTrace();
/* 125:    */     }
/* 126:    */   }
/* 127:    */   
/* 128:    */   public String getJythonOptions()
/* 129:    */   {
/* 130:257 */     return Utils.joinOptions(this.m_JythonOptions);
/* 131:    */   }
/* 132:    */   
/* 133:    */   public String jythonPathsTipText()
/* 134:    */   {
/* 135:267 */     return "Comma-separated list of additional paths that get added to 'sys.path'.";
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void setJythonPaths(String value)
/* 139:    */   {
/* 140:279 */     if (value.length() == 0)
/* 141:    */     {
/* 142:280 */       this.m_JythonPaths = new File[0];
/* 143:    */     }
/* 144:    */     else
/* 145:    */     {
/* 146:282 */       String[] paths = value.split(",");
/* 147:283 */       this.m_JythonPaths = new File[paths.length];
/* 148:284 */       for (int i = 0; i < this.m_JythonPaths.length; i++) {
/* 149:285 */         this.m_JythonPaths[i] = new File(paths[i]);
/* 150:    */       }
/* 151:    */     }
/* 152:    */   }
/* 153:    */   
/* 154:    */   public String getJythonPaths()
/* 155:    */   {
/* 156:299 */     String result = "";
/* 157:301 */     for (int i = 0; i < this.m_JythonPaths.length; i++)
/* 158:    */     {
/* 159:302 */       if (i > 0) {
/* 160:303 */         result = result + ",";
/* 161:    */       }
/* 162:305 */       result = result + this.m_JythonPaths[i].getAbsolutePath();
/* 163:    */     }
/* 164:308 */     return result;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public Capabilities getCapabilities()
/* 168:    */   {
/* 169:    */     Capabilities result;
/* 170:320 */     if (this.m_JythonObject == null)
/* 171:    */     {
/* 172:321 */       Capabilities result = new Capabilities(this);
/* 173:322 */       result.disableAll();
/* 174:    */     }
/* 175:    */     else
/* 176:    */     {
/* 177:324 */       result = this.m_JythonObject.getCapabilities();
/* 178:    */     }
/* 179:327 */     result.enableAllAttributeDependencies();
/* 180:328 */     result.enableAllClassDependencies();
/* 181:    */     
/* 182:330 */     return result;
/* 183:    */   }
/* 184:    */   
/* 185:    */   protected void initJythonObject()
/* 186:    */   {
/* 187:    */     try
/* 188:    */     {
/* 189:338 */       if (this.m_JythonModule.isFile()) {
/* 190:339 */         this.m_JythonObject = ((Classifier)Jython.newInstance(this.m_JythonModule, Classifier.class, this.m_JythonPaths));
/* 191:    */       } else {
/* 192:342 */         this.m_JythonObject = null;
/* 193:    */       }
/* 194:345 */       if (this.m_JythonObject != null) {
/* 195:346 */         ((OptionHandler)this.m_JythonObject).setOptions((String[])this.m_JythonOptions.clone());
/* 196:    */       }
/* 197:    */     }
/* 198:    */     catch (Exception e)
/* 199:    */     {
/* 200:349 */       this.m_JythonObject = null;
/* 201:350 */       e.printStackTrace();
/* 202:    */     }
/* 203:    */   }
/* 204:    */   
/* 205:    */   public void buildClassifier(Instances instances)
/* 206:    */     throws Exception
/* 207:    */   {
/* 208:362 */     if (!Jython.isPresent()) {
/* 209:363 */       throw new Exception("Jython classes not in CLASSPATH!");
/* 210:    */     }
/* 211:367 */     initJythonObject();
/* 212:370 */     if (this.m_JythonObject != null) {
/* 213:371 */       this.m_JythonObject.buildClassifier(instances);
/* 214:    */     } else {
/* 215:373 */       System.err.println("buildClassifier: No Jython object present!");
/* 216:    */     }
/* 217:    */   }
/* 218:    */   
/* 219:    */   public double classifyInstance(Instance instance)
/* 220:    */     throws Exception
/* 221:    */   {
/* 222:386 */     if (this.m_JythonObject != null) {
/* 223:387 */       return this.m_JythonObject.classifyInstance(instance);
/* 224:    */     }
/* 225:389 */     return Utils.missingValue();
/* 226:    */   }
/* 227:    */   
/* 228:    */   public double[] distributionForInstance(Instance instance)
/* 229:    */     throws Exception
/* 230:    */   {
/* 231:402 */     if (this.m_JythonObject != null) {
/* 232:403 */       return this.m_JythonObject.distributionForInstance(instance);
/* 233:    */     }
/* 234:405 */     return new double[instance.numClasses()];
/* 235:    */   }
/* 236:    */   
/* 237:    */   public String toString()
/* 238:    */   {
/* 239:416 */     if (this.m_JythonObject != null) {
/* 240:417 */       return this.m_JythonObject.toString();
/* 241:    */     }
/* 242:419 */     return "No Jython module loaded.";
/* 243:    */   }
/* 244:    */   
/* 245:    */   public String getRevision()
/* 246:    */   {
/* 247:430 */     return RevisionUtils.extract("$Revision: 10375 $");
/* 248:    */   }
/* 249:    */   
/* 250:    */   public static void main(String[] args)
/* 251:    */   {
/* 252:439 */     runClassifier(new JythonClassifier(), args);
/* 253:    */   }
/* 254:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.scripting.JythonClassifier
 * JD-Core Version:    0.7.0.1
 */