/*   1:    */ package weka.classifiers;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.Option;
/*  10:    */ import weka.core.OptionHandler;
/*  11:    */ import weka.core.RevisionHandler;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.Utils;
/*  14:    */ import weka.core.converters.ConverterUtils.DataSource;
/*  15:    */ 
/*  16:    */ public class CheckSource
/*  17:    */   implements OptionHandler, RevisionHandler
/*  18:    */ {
/*  19: 73 */   protected Classifier m_Classifier = null;
/*  20: 76 */   protected Classifier m_SourceCode = null;
/*  21: 79 */   protected File m_Dataset = null;
/*  22: 82 */   protected int m_ClassIndex = -1;
/*  23:    */   
/*  24:    */   public Enumeration<Option> listOptions()
/*  25:    */   {
/*  26: 90 */     Vector<Option> result = new Vector();
/*  27:    */     
/*  28: 92 */     result.addElement(new Option("\tThe classifier (incl. options) that was used to generate\n\tthe source code.", "W", 1, "-W <classname and options>"));
/*  29:    */     
/*  30:    */ 
/*  31:    */ 
/*  32:    */ 
/*  33: 97 */     result.addElement(new Option("\tThe classname of the generated source code.", "S", 1, "-S <classname>"));
/*  34:    */     
/*  35:    */ 
/*  36:    */ 
/*  37:101 */     result.addElement(new Option("\tThe training set with which the source code was generated.", "t", 1, "-t <file>"));
/*  38:    */     
/*  39:    */ 
/*  40:    */ 
/*  41:105 */     result.addElement(new Option("\tThe class index of the training set. 'first' and 'last' are\n\tvalid indices.\n\t(default: last)", "c", 1, "-c <index>"));
/*  42:    */     
/*  43:    */ 
/*  44:    */ 
/*  45:    */ 
/*  46:    */ 
/*  47:111 */     return result.elements();
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setOptions(String[] options)
/*  51:    */     throws Exception
/*  52:    */   {
/*  53:148 */     String tmpStr = Utils.getOption('W', options);
/*  54:149 */     if (tmpStr.length() > 0)
/*  55:    */     {
/*  56:150 */       String[] spec = Utils.splitOptions(tmpStr);
/*  57:151 */       if (spec.length == 0) {
/*  58:152 */         throw new IllegalArgumentException("Invalid classifier specification string");
/*  59:    */       }
/*  60:153 */       String classname = spec[0];
/*  61:154 */       spec[0] = "";
/*  62:155 */       setClassifier((Classifier)Utils.forName(Classifier.class, classname, spec));
/*  63:    */     }
/*  64:    */     else
/*  65:    */     {
/*  66:158 */       throw new Exception("No classifier (classname + options) provided!");
/*  67:    */     }
/*  68:    */     String classname;
/*  69:    */     String[] spec;
/*  70:161 */     tmpStr = Utils.getOption('S', options);
/*  71:162 */     if (tmpStr.length() > 0)
/*  72:    */     {
/*  73:163 */       spec = Utils.splitOptions(tmpStr);
/*  74:164 */       if (spec.length != 1) {
/*  75:165 */         throw new IllegalArgumentException("Invalid source code specification string");
/*  76:    */       }
/*  77:166 */       classname = spec[0];
/*  78:167 */       spec[0] = "";
/*  79:168 */       setSourceCode((Classifier)Utils.forName(Classifier.class, classname, spec));
/*  80:    */     }
/*  81:    */     else
/*  82:    */     {
/*  83:171 */       throw new Exception("No source code (classname) provided!");
/*  84:    */     }
/*  85:174 */     tmpStr = Utils.getOption('t', options);
/*  86:175 */     if (tmpStr.length() != 0) {
/*  87:176 */       setDataset(new File(tmpStr));
/*  88:    */     } else {
/*  89:178 */       throw new Exception("No dataset provided!");
/*  90:    */     }
/*  91:180 */     tmpStr = Utils.getOption('c', options);
/*  92:181 */     if (tmpStr.length() != 0)
/*  93:    */     {
/*  94:182 */       if (tmpStr.equals("first")) {
/*  95:183 */         setClassIndex(0);
/*  96:184 */       } else if (tmpStr.equals("last")) {
/*  97:185 */         setClassIndex(-1);
/*  98:    */       } else {
/*  99:187 */         setClassIndex(Integer.parseInt(tmpStr) - 1);
/* 100:    */       }
/* 101:    */     }
/* 102:    */     else {
/* 103:190 */       setClassIndex(-1);
/* 104:    */     }
/* 105:    */   }
/* 106:    */   
/* 107:    */   public String[] getOptions()
/* 108:    */   {
/* 109:202 */     Vector<String> result = new Vector();
/* 110:204 */     if (getClassifier() != null)
/* 111:    */     {
/* 112:205 */       result.add("-W");
/* 113:206 */       result.add(getClassifier().getClass().getName() + " " + Utils.joinOptions(((OptionHandler)getClassifier()).getOptions()));
/* 114:    */     }
/* 115:210 */     if (getSourceCode() != null)
/* 116:    */     {
/* 117:211 */       result.add("-S");
/* 118:212 */       result.add(getSourceCode().getClass().getName());
/* 119:    */     }
/* 120:215 */     if (getDataset() != null)
/* 121:    */     {
/* 122:216 */       result.add("-t");
/* 123:217 */       result.add(this.m_Dataset.getAbsolutePath());
/* 124:    */     }
/* 125:220 */     result.add("-c");
/* 126:221 */     if (getClassIndex() == -1) {
/* 127:222 */       result.add("last");
/* 128:223 */     } else if (getClassIndex() == 0) {
/* 129:224 */       result.add("first");
/* 130:    */     } else {
/* 131:226 */       result.add("" + (getClassIndex() + 1));
/* 132:    */     }
/* 133:228 */     return (String[])result.toArray(new String[result.size()]);
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void setClassifier(Classifier value)
/* 137:    */   {
/* 138:237 */     this.m_Classifier = value;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public Classifier getClassifier()
/* 142:    */   {
/* 143:246 */     return this.m_Classifier;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public void setSourceCode(Classifier value)
/* 147:    */   {
/* 148:255 */     this.m_SourceCode = value;
/* 149:    */   }
/* 150:    */   
/* 151:    */   public Classifier getSourceCode()
/* 152:    */   {
/* 153:264 */     return this.m_SourceCode;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public void setDataset(File value)
/* 157:    */   {
/* 158:273 */     if (!value.exists()) {
/* 159:274 */       throw new IllegalArgumentException("Dataset '" + value.getAbsolutePath() + "' does not exist!");
/* 160:    */     }
/* 161:277 */     this.m_Dataset = value;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public File getDataset()
/* 165:    */   {
/* 166:286 */     return this.m_Dataset;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void setClassIndex(int value)
/* 170:    */   {
/* 171:295 */     this.m_ClassIndex = value;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public int getClassIndex()
/* 175:    */   {
/* 176:304 */     return this.m_ClassIndex;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public boolean execute()
/* 180:    */     throws Exception
/* 181:    */   {
/* 182:325 */     boolean result = true;
/* 183:328 */     if (getClassifier() == null) {
/* 184:329 */       throw new Exception("No classifier set!");
/* 185:    */     }
/* 186:330 */     if (getSourceCode() == null) {
/* 187:331 */       throw new Exception("No source code set!");
/* 188:    */     }
/* 189:332 */     if (getDataset() == null) {
/* 190:333 */       throw new Exception("No dataset set!");
/* 191:    */     }
/* 192:334 */     if (!getDataset().exists()) {
/* 193:335 */       throw new Exception("Dataset '" + getDataset().getAbsolutePath() + "' does not exist!");
/* 194:    */     }
/* 195:339 */     ConverterUtils.DataSource source = new ConverterUtils.DataSource(getDataset().getAbsolutePath());
/* 196:340 */     Instances data = source.getDataSet();
/* 197:341 */     if (getClassIndex() == -1) {
/* 198:342 */       data.setClassIndex(data.numAttributes() - 1);
/* 199:    */     } else {
/* 200:344 */       data.setClassIndex(getClassIndex());
/* 201:    */     }
/* 202:345 */     boolean numeric = data.classAttribute().isNumeric();
/* 203:    */     
/* 204:    */ 
/* 205:348 */     Classifier cls = AbstractClassifier.makeCopy(getClassifier());
/* 206:349 */     cls.buildClassifier(data);
/* 207:    */     
/* 208:351 */     Classifier code = getSourceCode();
/* 209:354 */     for (int i = 0; i < data.numInstances(); i++)
/* 210:    */     {
/* 211:356 */       double predClassifier = cls.classifyInstance(data.instance(i));
/* 212:357 */       double predSource = code.classifyInstance(data.instance(i));
/* 213:    */       boolean different;
/* 214:    */       boolean different;
/* 215:360 */       if ((Double.isNaN(predClassifier)) && (Double.isNaN(predSource)))
/* 216:    */       {
/* 217:361 */         different = false;
/* 218:    */       }
/* 219:    */       else
/* 220:    */       {
/* 221:    */         boolean different;
/* 222:364 */         if (numeric) {
/* 223:365 */           different = !Utils.eq(predClassifier, predSource);
/* 224:    */         } else {
/* 225:367 */           different = (int)predClassifier != (int)predSource;
/* 226:    */         }
/* 227:    */       }
/* 228:370 */       if (different)
/* 229:    */       {
/* 230:371 */         result = false;
/* 231:372 */         if (numeric) {
/* 232:373 */           System.out.println(i + 1 + ". instance (Classifier/Source code): " + predClassifier + " != " + predSource);
/* 233:    */         } else {
/* 234:377 */           System.out.println(i + 1 + ". instance (Classifier/Source code): " + data.classAttribute().value((int)predClassifier) + " != " + data.classAttribute().value((int)predSource));
/* 235:    */         }
/* 236:    */       }
/* 237:    */     }
/* 238:384 */     return result;
/* 239:    */   }
/* 240:    */   
/* 241:    */   public String getRevision()
/* 242:    */   {
/* 243:393 */     return RevisionUtils.extract("$Revision: 10141 $");
/* 244:    */   }
/* 245:    */   
/* 246:    */   public static void main(String[] args)
/* 247:    */     throws Exception
/* 248:    */   {
/* 249:407 */     CheckSource check = new CheckSource();
/* 250:408 */     if (Utils.getFlag('h', args))
/* 251:    */     {
/* 252:409 */       StringBuffer text = new StringBuffer();
/* 253:410 */       text.append("\nHelp requested:\n\n");
/* 254:411 */       Enumeration<Option> enm = check.listOptions();
/* 255:412 */       while (enm.hasMoreElements())
/* 256:    */       {
/* 257:413 */         Option option = (Option)enm.nextElement();
/* 258:414 */         text.append(option.synopsis() + "\n");
/* 259:415 */         text.append(option.description() + "\n");
/* 260:    */       }
/* 261:417 */       System.out.println("\n" + text + "\n");
/* 262:    */     }
/* 263:    */     else
/* 264:    */     {
/* 265:420 */       check.setOptions(args);
/* 266:421 */       if (check.execute()) {
/* 267:422 */         System.out.println("Tests OK!");
/* 268:    */       } else {
/* 269:424 */         System.out.println("Tests failed!");
/* 270:    */       }
/* 271:    */     }
/* 272:    */   }
/* 273:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.CheckSource
 * JD-Core Version:    0.7.0.1
 */