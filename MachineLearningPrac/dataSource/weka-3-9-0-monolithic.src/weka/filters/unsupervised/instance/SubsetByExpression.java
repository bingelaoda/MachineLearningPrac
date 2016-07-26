/*   1:    */ package weka.filters.unsupervised.instance;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.Utils;
/*  13:    */ import weka.core.expressionlanguage.common.IfElseMacro;
/*  14:    */ import weka.core.expressionlanguage.common.JavaMacro;
/*  15:    */ import weka.core.expressionlanguage.common.MacroDeclarationsCompositor;
/*  16:    */ import weka.core.expressionlanguage.common.MathFunctions;
/*  17:    */ import weka.core.expressionlanguage.common.Primitives.BooleanExpression;
/*  18:    */ import weka.core.expressionlanguage.core.MacroDeclarations;
/*  19:    */ import weka.core.expressionlanguage.core.Node;
/*  20:    */ import weka.core.expressionlanguage.parser.Parser;
/*  21:    */ import weka.core.expressionlanguage.weka.InstancesHelper;
/*  22:    */ import weka.filters.SimpleBatchFilter;
/*  23:    */ 
/*  24:    */ public class SubsetByExpression
/*  25:    */   extends SimpleBatchFilter
/*  26:    */ {
/*  27:    */   private static final long serialVersionUID = 5628686110979589602L;
/*  28: 90 */   protected String m_Expression = "true";
/*  29: 93 */   protected boolean m_filterAfterFirstBatch = false;
/*  30:    */   
/*  31:    */   public String globalInfo()
/*  32:    */   {
/*  33:103 */     return "Filters instances according to a user-specified expression.\n\n\nExamples:\n- extracting only mammals and birds from the 'zoo' UCI dataset:\n  (CLASS is 'mammal') or (CLASS is 'bird')\n- extracting only animals with at least 2 legs from the 'zoo' UCI dataset:\n  (ATT14 >= 2)\n- extracting only instances with non-missing 'wage-increase-second-year'\n  from the 'labor' UCI dataset:\n  not ismissing(ATT3)\n";
/*  34:    */   }
/*  35:    */   
/*  36:    */   public boolean mayRemoveInstanceAfterFirstBatchDone()
/*  37:    */   {
/*  38:125 */     return true;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public boolean input(Instance instance)
/*  42:    */     throws Exception
/*  43:    */   {
/*  44:142 */     if (getInputFormat() == null) {
/*  45:143 */       throw new IllegalStateException("No input instance format defined");
/*  46:    */     }
/*  47:146 */     if (this.m_NewBatch)
/*  48:    */     {
/*  49:147 */       resetQueue();
/*  50:148 */       this.m_NewBatch = false;
/*  51:    */     }
/*  52:151 */     bufferInput(instance);
/*  53:    */     
/*  54:153 */     int numReturnedFromParser = 0;
/*  55:154 */     if (isFirstBatchDone())
/*  56:    */     {
/*  57:155 */       Instances inst = new Instances(getInputFormat());
/*  58:156 */       inst = process(inst);
/*  59:157 */       numReturnedFromParser = inst.numInstances();
/*  60:158 */       for (int i = 0; i < inst.numInstances(); i++) {
/*  61:159 */         push(inst.instance(i), false);
/*  62:    */       }
/*  63:161 */       flushInput();
/*  64:    */     }
/*  65:164 */     return numReturnedFromParser > 0;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public Enumeration<Option> listOptions()
/*  69:    */   {
/*  70:175 */     Vector<Option> result = new Vector();
/*  71:    */     
/*  72:177 */     result.addElement(new Option("\tThe expression to use for filtering\n\t(default: true).", "E", 1, "-E <expr>"));
/*  73:    */     
/*  74:    */ 
/*  75:180 */     result.addElement(new Option("\tApply the filter to instances that arrive after the first\n\t(training) batch. The default is to not apply the filter (i.e.\n\talways return the instance)", "F", 0, "-F"));
/*  76:    */     
/*  77:    */ 
/*  78:    */ 
/*  79:    */ 
/*  80:185 */     result.addAll(Collections.list(super.listOptions()));
/*  81:    */     
/*  82:187 */     return result.elements();
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setOptions(String[] options)
/*  86:    */     throws Exception
/*  87:    */   {
/*  88:222 */     String tmpStr = Utils.getOption('E', options);
/*  89:223 */     if (tmpStr.length() != 0) {
/*  90:224 */       setExpression(tmpStr);
/*  91:    */     } else {
/*  92:226 */       setExpression("true");
/*  93:    */     }
/*  94:229 */     this.m_filterAfterFirstBatch = Utils.getFlag('F', options);
/*  95:231 */     if (getInputFormat() != null) {
/*  96:232 */       setInputFormat(getInputFormat());
/*  97:    */     }
/*  98:235 */     super.setOptions(options);
/*  99:    */     
/* 100:237 */     Utils.checkForRemainingOptions(options);
/* 101:    */   }
/* 102:    */   
/* 103:    */   public String[] getOptions()
/* 104:    */   {
/* 105:248 */     Vector<String> result = new Vector();
/* 106:    */     
/* 107:250 */     result.add("-E");
/* 108:251 */     result.add("" + getExpression());
/* 109:253 */     if (this.m_filterAfterFirstBatch) {
/* 110:254 */       result.add("-F");
/* 111:    */     }
/* 112:257 */     Collections.addAll(result, super.getOptions());
/* 113:    */     
/* 114:259 */     return (String[])result.toArray(new String[result.size()]);
/* 115:    */   }
/* 116:    */   
/* 117:    */   public Capabilities getCapabilities()
/* 118:    */   {
/* 119:270 */     Capabilities result = super.getCapabilities();
/* 120:271 */     result.disableAll();
/* 121:    */     
/* 122:    */ 
/* 123:274 */     result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
/* 124:275 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 125:276 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 126:277 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 127:278 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 128:    */     
/* 129:    */ 
/* 130:281 */     result.enable(Capabilities.Capability.STRING_CLASS);
/* 131:282 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 132:283 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/* 133:284 */     result.enable(Capabilities.Capability.DATE_CLASS);
/* 134:285 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 135:286 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 136:    */     
/* 137:288 */     return result;
/* 138:    */   }
/* 139:    */   
/* 140:    */   public void setExpression(String value)
/* 141:    */   {
/* 142:297 */     this.m_Expression = value;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public String getExpression()
/* 146:    */   {
/* 147:306 */     return this.m_Expression;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public String expressionTipText()
/* 151:    */   {
/* 152:316 */     return "The expression to used for filtering the dataset.";
/* 153:    */   }
/* 154:    */   
/* 155:    */   public void setFilterAfterFirstBatch(boolean b)
/* 156:    */   {
/* 157:330 */     this.m_filterAfterFirstBatch = b;
/* 158:    */   }
/* 159:    */   
/* 160:    */   public boolean getFilterAfterFirstBatch()
/* 161:    */   {
/* 162:344 */     return this.m_filterAfterFirstBatch;
/* 163:    */   }
/* 164:    */   
/* 165:    */   public String filterAfterFirstBatchTipText()
/* 166:    */   {
/* 167:354 */     return "Whether to apply the filtering process to instances that are input after the first (training) batch. The default is false so that, when used in a FilteredClassifier, test instances do not potentially get 'consumed' by the filter an a prediction is always made.";
/* 168:    */   }
/* 169:    */   
/* 170:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 171:    */     throws Exception
/* 172:    */   {
/* 173:372 */     return new Instances(inputFormat, 0);
/* 174:    */   }
/* 175:    */   
/* 176:    */   protected Instances process(Instances instances)
/* 177:    */     throws Exception
/* 178:    */   {
/* 179:386 */     if ((!isFirstBatchDone()) || (this.m_filterAfterFirstBatch))
/* 180:    */     {
/* 181:389 */       Instances output = new Instances(instances, 0);
/* 182:    */       
/* 183:    */ 
/* 184:392 */       InstancesHelper instancesHelper = new InstancesHelper(instances);
/* 185:393 */       Node node = Parser.parse(this.m_Expression, instancesHelper, new MacroDeclarationsCompositor(new MacroDeclarations[] { instancesHelper, new MathFunctions(), new IfElseMacro(), new JavaMacro() }));
/* 186:407 */       if (!(node instanceof Primitives.BooleanExpression)) {
/* 187:408 */         throw new Exception("Expression must be of boolean type!");
/* 188:    */       }
/* 189:410 */       Primitives.BooleanExpression condition = (Primitives.BooleanExpression)node;
/* 190:413 */       for (int i = 0; i < instances.numInstances(); i++)
/* 191:    */       {
/* 192:414 */         Instance instance = instances.get(i);
/* 193:    */         
/* 194:416 */         instancesHelper.setInstance(instance);
/* 195:419 */         if (condition.evaluate()) {
/* 196:420 */           output.add((Instance)instance.copy());
/* 197:    */         }
/* 198:    */       }
/* 199:423 */       return output;
/* 200:    */     }
/* 201:425 */     return instances;
/* 202:    */   }
/* 203:    */   
/* 204:    */   public String getRevision()
/* 205:    */   {
/* 206:436 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 207:    */   }
/* 208:    */   
/* 209:    */   public static void main(String[] args)
/* 210:    */   {
/* 211:445 */     runFilter(new SubsetByExpression(), args);
/* 212:    */   }
/* 213:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.instance.SubsetByExpression
 * JD-Core Version:    0.7.0.1
 */