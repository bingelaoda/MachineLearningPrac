/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Capabilities;
/*   9:    */ import weka.core.Capabilities.Capability;
/*  10:    */ import weka.core.DenseInstance;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Option;
/*  14:    */ import weka.core.Range;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SparseInstance;
/*  17:    */ import weka.core.Utils;
/*  18:    */ import weka.core.expressionlanguage.common.IfElseMacro;
/*  19:    */ import weka.core.expressionlanguage.common.JavaMacro;
/*  20:    */ import weka.core.expressionlanguage.common.MacroDeclarationsCompositor;
/*  21:    */ import weka.core.expressionlanguage.common.MathFunctions;
/*  22:    */ import weka.core.expressionlanguage.common.Primitives.DoubleExpression;
/*  23:    */ import weka.core.expressionlanguage.common.SimpleVariableDeclarations;
/*  24:    */ import weka.core.expressionlanguage.common.SimpleVariableDeclarations.VariableInitializer;
/*  25:    */ import weka.core.expressionlanguage.common.VariableDeclarationsCompositor;
/*  26:    */ import weka.core.expressionlanguage.core.MacroDeclarations;
/*  27:    */ import weka.core.expressionlanguage.core.Node;
/*  28:    */ import weka.core.expressionlanguage.core.VariableDeclarations;
/*  29:    */ import weka.core.expressionlanguage.parser.Parser;
/*  30:    */ import weka.core.expressionlanguage.weka.InstancesHelper;
/*  31:    */ import weka.core.expressionlanguage.weka.StatsHelper;
/*  32:    */ import weka.experiment.Stats;
/*  33:    */ import weka.filters.UnsupervisedFilter;
/*  34:    */ 
/*  35:    */ public class MathExpression
/*  36:    */   extends PotentialClassIgnorer
/*  37:    */   implements UnsupervisedFilter
/*  38:    */ {
/*  39:    */   static final long serialVersionUID = -3713222714671997901L;
/*  40:104 */   protected Range m_SelectCols = new Range();
/*  41:    */   public static final String m_defaultExpression = "(A-MIN)/(MAX-MIN)";
/*  42:110 */   private String m_expression = "(A-MIN)/(MAX-MIN)";
/*  43:    */   private Primitives.DoubleExpression m_CompiledExpression;
/*  44:    */   private Stats[] m_attStats;
/*  45:    */   private InstancesHelper m_InstancesHelper;
/*  46:    */   private StatsHelper m_StatsHelper;
/*  47:    */   private SimpleVariableDeclarations.VariableInitializer m_CurrentValue;
/*  48:    */   
/*  49:    */   public MathExpression()
/*  50:    */   {
/*  51:132 */     setInvertSelection(false);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String globalInfo()
/*  55:    */   {
/*  56:143 */     return "Modify numeric attributes according to a given expression ";
/*  57:    */   }
/*  58:    */   
/*  59:    */   public Capabilities getCapabilities()
/*  60:    */   {
/*  61:154 */     Capabilities result = super.getCapabilities();
/*  62:155 */     result.disableAll();
/*  63:    */     
/*  64:    */ 
/*  65:158 */     result.enableAllAttributes();
/*  66:159 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  67:    */     
/*  68:    */ 
/*  69:162 */     result.enableAllClasses();
/*  70:163 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  71:164 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  72:    */     
/*  73:166 */     return result;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public boolean setInputFormat(Instances instanceInfo)
/*  77:    */     throws Exception
/*  78:    */   {
/*  79:180 */     this.m_SelectCols.setUpper(instanceInfo.numAttributes() - 1);
/*  80:181 */     super.setInputFormat(instanceInfo);
/*  81:182 */     setOutputFormat(instanceInfo);
/*  82:    */     
/*  83:184 */     this.m_attStats = new Stats[instanceInfo.numAttributes()];
/*  84:186 */     for (int i = 0; i < instanceInfo.numAttributes(); i++) {
/*  85:187 */       if ((this.m_SelectCols.isInRange(i)) && (instanceInfo.attribute(i).isNumeric()) && (instanceInfo.classIndex() != i)) {
/*  86:191 */         this.m_attStats[i] = new Stats();
/*  87:    */       }
/*  88:    */     }
/*  89:195 */     if (instanceInfo != null) {
/*  90:196 */       compile();
/*  91:    */     }
/*  92:198 */     return true;
/*  93:    */   }
/*  94:    */   
/*  95:    */   private void compile()
/*  96:    */     throws Exception
/*  97:    */   {
/*  98:209 */     this.m_InstancesHelper = new InstancesHelper(getInputFormat());
/*  99:210 */     this.m_StatsHelper = new StatsHelper();
/* 100:211 */     SimpleVariableDeclarations currentValueDeclaration = new SimpleVariableDeclarations();
/* 101:212 */     currentValueDeclaration.addDouble("A");
/* 102:    */     
/* 103:214 */     Node node = Parser.parse(this.m_expression, new VariableDeclarationsCompositor(new VariableDeclarations[] { this.m_InstancesHelper, this.m_StatsHelper, currentValueDeclaration }), new MacroDeclarationsCompositor(new MacroDeclarations[] { this.m_InstancesHelper, new MathFunctions(), new IfElseMacro(), new JavaMacro() }));
/* 104:232 */     if (!(node instanceof Primitives.DoubleExpression)) {
/* 105:233 */       throw new Exception("Expression must be of type double!");
/* 106:    */     }
/* 107:235 */     this.m_CurrentValue = currentValueDeclaration.getInitializer();
/* 108:    */     
/* 109:237 */     this.m_CompiledExpression = ((Primitives.DoubleExpression)node);
/* 110:    */   }
/* 111:    */   
/* 112:    */   public boolean input(Instance instance)
/* 113:    */     throws Exception
/* 114:    */   {
/* 115:252 */     if (getInputFormat() == null) {
/* 116:253 */       throw new IllegalStateException("No input instance format defined");
/* 117:    */     }
/* 118:255 */     if (this.m_NewBatch)
/* 119:    */     {
/* 120:256 */       resetQueue();
/* 121:257 */       this.m_NewBatch = false;
/* 122:    */     }
/* 123:259 */     if (!this.m_FirstBatchDone)
/* 124:    */     {
/* 125:260 */       for (int i = 0; i < instance.numAttributes(); i++) {
/* 126:261 */         if ((this.m_SelectCols.isInRange(i)) && (instance.attribute(i).isNumeric()) && (getInputFormat().classIndex() != i) && (!instance.isMissing(i))) {
/* 127:266 */           this.m_attStats[i].add(instance.value(i), instance.weight());
/* 128:    */         }
/* 129:    */       }
/* 130:270 */       bufferInput(instance);
/* 131:271 */       return false;
/* 132:    */     }
/* 133:273 */     convertInstance(instance);
/* 134:274 */     return true;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public boolean batchFinished()
/* 138:    */     throws Exception
/* 139:    */   {
/* 140:289 */     if (getInputFormat() == null) {
/* 141:290 */       throw new IllegalStateException("No input instance format defined");
/* 142:    */     }
/* 143:292 */     if (!this.m_FirstBatchDone)
/* 144:    */     {
/* 145:294 */       Instances input = getInputFormat();
/* 146:296 */       for (int i = 0; i < input.numAttributes(); i++) {
/* 147:297 */         if ((this.m_SelectCols.isInRange(i)) && (input.attribute(i).isNumeric()) && (input.classIndex() != i)) {
/* 148:301 */           this.m_attStats[i].calculateDerived();
/* 149:    */         }
/* 150:    */       }
/* 151:307 */       for (int i = 0; i < input.numInstances(); i++) {
/* 152:308 */         convertInstance(input.instance(i));
/* 153:    */       }
/* 154:    */     }
/* 155:312 */     flushInput();
/* 156:    */     
/* 157:314 */     this.m_NewBatch = true;
/* 158:315 */     this.m_FirstBatchDone = true;
/* 159:316 */     return numPendingOutput() != 0;
/* 160:    */   }
/* 161:    */   
/* 162:    */   private void convertInstance(Instance instance)
/* 163:    */     throws Exception
/* 164:    */   {
/* 165:328 */     double[] vals = instance.toDoubleArray();
/* 166:329 */     for (int i = 0; i < instance.numAttributes(); i++) {
/* 167:331 */       if ((this.m_SelectCols.isInRange(i)) && (instance.attribute(i).isNumeric()) && (!Utils.isMissingValue(vals[i])) && (getInputFormat().classIndex() != i))
/* 168:    */       {
/* 169:339 */         this.m_InstancesHelper.setInstance(instance);
/* 170:340 */         this.m_StatsHelper.setStats(this.m_attStats[i]);
/* 171:341 */         if (this.m_CurrentValue.hasVariable("A")) {
/* 172:342 */           this.m_CurrentValue.setDouble("A", vals[i]);
/* 173:    */         }
/* 174:345 */         double value = this.m_CompiledExpression.evaluate();
/* 175:348 */         if ((Double.isNaN(value)) || (Double.isInfinite(value)) || (this.m_InstancesHelper.missingAccessed()))
/* 176:    */         {
/* 177:350 */           System.err.println("WARNING:Error in evaluating the expression: missing value set");
/* 178:    */           
/* 179:352 */           vals[i] = Utils.missingValue();
/* 180:    */         }
/* 181:    */         else
/* 182:    */         {
/* 183:354 */           vals[i] = value;
/* 184:    */         }
/* 185:    */       }
/* 186:    */     }
/* 187:    */     Instance outInstance;
/* 188:    */     Instance outInstance;
/* 189:361 */     if ((instance instanceof SparseInstance)) {
/* 190:362 */       outInstance = new SparseInstance(instance.weight(), vals);
/* 191:    */     } else {
/* 192:364 */       outInstance = new DenseInstance(instance.weight(), vals);
/* 193:    */     }
/* 194:366 */     outInstance.setDataset(instance.dataset());
/* 195:367 */     push(outInstance, false);
/* 196:    */   }
/* 197:    */   
/* 198:    */   public void setOptions(String[] options)
/* 199:    */     throws Exception
/* 200:    */   {
/* 201:414 */     String expString = Utils.getOption('E', options);
/* 202:415 */     if (expString.length() != 0) {
/* 203:416 */       setExpression(expString);
/* 204:    */     } else {
/* 205:418 */       setExpression("(A-MIN)/(MAX-MIN)");
/* 206:    */     }
/* 207:421 */     String ignoreList = Utils.getOption('R', options);
/* 208:422 */     if (ignoreList.length() != 0) {
/* 209:423 */       setIgnoreRange(ignoreList);
/* 210:    */     }
/* 211:426 */     setInvertSelection(Utils.getFlag('V', options));
/* 212:    */     
/* 213:428 */     super.setOptions(options);
/* 214:    */     
/* 215:430 */     Utils.checkForRemainingOptions(options);
/* 216:    */   }
/* 217:    */   
/* 218:    */   public String[] getOptions()
/* 219:    */   {
/* 220:441 */     Vector<String> result = new Vector();
/* 221:    */     
/* 222:443 */     result.add("-E");
/* 223:444 */     result.add(getExpression());
/* 224:446 */     if (getInvertSelection()) {
/* 225:447 */       result.add("-V");
/* 226:    */     }
/* 227:450 */     if (!getIgnoreRange().equals(""))
/* 228:    */     {
/* 229:451 */       result.add("-R");
/* 230:452 */       result.add(getIgnoreRange());
/* 231:    */     }
/* 232:455 */     Collections.addAll(result, super.getOptions());
/* 233:    */     
/* 234:457 */     return (String[])result.toArray(new String[result.size()]);
/* 235:    */   }
/* 236:    */   
/* 237:    */   public Enumeration<Option> listOptions()
/* 238:    */   {
/* 239:468 */     Vector<Option> result = new Vector();
/* 240:    */     
/* 241:470 */     result.addElement(new Option("\tSpecify the expression to apply. Eg. pow(A,6)/(MEAN+MAX)\n\tSupported operators are +, -, *, /, pow, log,\n\tabs, cos, exp, sqrt, tan, sin, ceil, floor, rint, (, ), \n\tMEAN, MAX, MIN, SD, COUNT, SUM, SUMSQUARED, ifelse. The 'A'\n\tletter refers to the value of the attribute being processed.\n\tOther attribute values (numeric only) can be accessed through\n\tthe variables A1, A2, A3, ...", "E", 1, "-E <expression>"));
/* 242:    */     
/* 243:    */ 
/* 244:    */ 
/* 245:    */ 
/* 246:    */ 
/* 247:    */ 
/* 248:    */ 
/* 249:    */ 
/* 250:479 */     result.addElement(new Option("\tSpecify list of columns to ignore. First and last are valid\n\tindexes. (default none)", "R", 1, "-R <index1,index2-index4,...>"));
/* 251:    */     
/* 252:    */ 
/* 253:    */ 
/* 254:    */ 
/* 255:    */ 
/* 256:485 */     result.addElement(new Option("\tInvert matching sense (i.e. only modify specified columns)", "V", 0, "-V"));
/* 257:    */     
/* 258:    */ 
/* 259:    */ 
/* 260:489 */     result.addAll(Collections.list(super.listOptions()));
/* 261:    */     
/* 262:491 */     return result.elements();
/* 263:    */   }
/* 264:    */   
/* 265:    */   public String expressionTipText()
/* 266:    */   {
/* 267:501 */     return "Specify the expression to apply. The 'A' letterrefers to the value of the attribute being processed. MIN,MAX,MEAN,SDrefer respectively to minimum, maximum, mean andstandard deviation of the attribute being processed. Other attribute values (numeric only) can be accessed through the variables A1, A2, A3, ...\n\tSupported operators are +, -, *, /, pow, log,abs, cos, exp, sqrt, tan, sin, ceil, floor, rint, (, ),A,MEAN, MAX, MIN, SD, COUNT, SUM, SUMSQUARED, ifelse\n\tEg. pow(A,6)/(MEAN+MAX)*ifelse(A<0,0,sqrt(A))+ifelse(![A>9 && A<15])";
/* 268:    */   }
/* 269:    */   
/* 270:    */   public void setExpression(String expr)
/* 271:    */     throws Exception
/* 272:    */   {
/* 273:521 */     this.m_expression = expr;
/* 274:522 */     if (getInputFormat() != null) {
/* 275:523 */       compile();
/* 276:    */     }
/* 277:    */   }
/* 278:    */   
/* 279:    */   public String getExpression()
/* 280:    */   {
/* 281:532 */     return this.m_expression;
/* 282:    */   }
/* 283:    */   
/* 284:    */   public String invertSelectionTipText()
/* 285:    */   {
/* 286:543 */     return "Determines whether action is to select or unselect. If set to true, only the specified attributes will be modified; If set to false, specified attributes will not be modified.";
/* 287:    */   }
/* 288:    */   
/* 289:    */   public boolean getInvertSelection()
/* 290:    */   {
/* 291:555 */     return !this.m_SelectCols.getInvert();
/* 292:    */   }
/* 293:    */   
/* 294:    */   public void setInvertSelection(boolean invert)
/* 295:    */   {
/* 296:567 */     this.m_SelectCols.setInvert(!invert);
/* 297:    */   }
/* 298:    */   
/* 299:    */   public String ignoreRangeTipText()
/* 300:    */   {
/* 301:578 */     return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
/* 302:    */   }
/* 303:    */   
/* 304:    */   public String getIgnoreRange()
/* 305:    */   {
/* 306:591 */     return this.m_SelectCols.getRanges();
/* 307:    */   }
/* 308:    */   
/* 309:    */   public void setIgnoreRange(String rangeList)
/* 310:    */   {
/* 311:604 */     this.m_SelectCols.setRanges(rangeList);
/* 312:    */   }
/* 313:    */   
/* 314:    */   public String getRevision()
/* 315:    */   {
/* 316:614 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 317:    */   }
/* 318:    */   
/* 319:    */   public static void main(String[] argv)
/* 320:    */   {
/* 321:623 */     runFilter(new MathExpression(), argv);
/* 322:    */   }
/* 323:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.MathExpression
 * JD-Core Version:    0.7.0.1
 */