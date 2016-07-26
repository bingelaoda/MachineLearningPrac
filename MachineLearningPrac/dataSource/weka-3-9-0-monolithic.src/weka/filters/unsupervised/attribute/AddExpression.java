/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.DenseInstance;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.OptionHandler;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.SparseInstance;
/*  15:    */ import weka.core.Utils;
/*  16:    */ import weka.core.expressionlanguage.common.IfElseMacro;
/*  17:    */ import weka.core.expressionlanguage.common.JavaMacro;
/*  18:    */ import weka.core.expressionlanguage.common.MacroDeclarationsCompositor;
/*  19:    */ import weka.core.expressionlanguage.common.MathFunctions;
/*  20:    */ import weka.core.expressionlanguage.common.Primitives.DoubleExpression;
/*  21:    */ import weka.core.expressionlanguage.core.MacroDeclarations;
/*  22:    */ import weka.core.expressionlanguage.core.Node;
/*  23:    */ import weka.core.expressionlanguage.parser.Parser;
/*  24:    */ import weka.core.expressionlanguage.weka.InstancesHelper;
/*  25:    */ import weka.filters.Filter;
/*  26:    */ import weka.filters.StreamableFilter;
/*  27:    */ import weka.filters.UnsupervisedFilter;
/*  28:    */ 
/*  29:    */ public class AddExpression
/*  30:    */   extends Filter
/*  31:    */   implements UnsupervisedFilter, StreamableFilter, OptionHandler
/*  32:    */ {
/*  33:    */   static final long serialVersionUID = 402130384261736245L;
/*  34: 94 */   private String m_infixExpression = "0.0";
/*  35:100 */   private String m_attributeName = "expression";
/*  36:106 */   private boolean m_Debug = false;
/*  37:108 */   private Primitives.DoubleExpression m_Expression = null;
/*  38:    */   private InstancesHelper m_InstancesHelper;
/*  39:    */   
/*  40:    */   public String globalInfo()
/*  41:    */   {
/*  42:119 */     return "An instance filter that creates a new attribute by applying a mathematical expression to existing attributes. The expression can contain attribute references and numeric constants. Supported operators are :\n+, -, *, /, ^, log, abs, cos, exp, sqrt, floor, ceil, rint, tan, sin, (, )\nAttributes are specified by prefixing with 'a', eg. a7 is attribute number 7 (starting from 1).\nExample expression : a1^2*a5/log(a7*4.0).";
/*  43:    */   }
/*  44:    */   
/*  45:    */   public Enumeration<Option> listOptions()
/*  46:    */   {
/*  47:138 */     Vector<Option> newVector = new Vector(3);
/*  48:    */     
/*  49:140 */     newVector.addElement(new Option("\tSpecify the expression to apply. Eg a1^2*a5/log(a7*4.0).\n\tSupported opperators: ,+, -, *, /, ^, log, abs, cos, \n\texp, sqrt, floor, ceil, rint, tan, sin, (, )\n\t(default: a1^2)", "E", 1, "-E <expression>"));
/*  50:    */     
/*  51:    */ 
/*  52:    */ 
/*  53:    */ 
/*  54:    */ 
/*  55:146 */     newVector.addElement(new Option("\tSpecify the name for the new attribute. (default is the expression provided with -E)", "N", 1, "-N <name>"));
/*  56:    */     
/*  57:    */ 
/*  58:    */ 
/*  59:150 */     newVector.addElement(new Option("\tDebug. Names attribute with the postfix parse of the expression.", "D", 0, "-D"));
/*  60:    */     
/*  61:    */ 
/*  62:    */ 
/*  63:    */ 
/*  64:155 */     return newVector.elements();
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setOptions(String[] options)
/*  68:    */     throws Exception
/*  69:    */   {
/*  70:190 */     String expString = Utils.getOption('E', options);
/*  71:191 */     if (expString.length() != 0) {
/*  72:192 */       setExpression(expString);
/*  73:    */     } else {
/*  74:194 */       setExpression("a1^2");
/*  75:    */     }
/*  76:197 */     String name = Utils.getOption('N', options);
/*  77:198 */     if (name.length() != 0) {
/*  78:199 */       setName(name);
/*  79:    */     }
/*  80:202 */     setDebug(Utils.getFlag('D', options));
/*  81:    */     
/*  82:204 */     Utils.checkForRemainingOptions(options);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public String[] getOptions()
/*  86:    */   {
/*  87:215 */     Vector<String> options = new Vector();
/*  88:    */     
/*  89:217 */     options.add("-E");
/*  90:218 */     options.add(getExpression());
/*  91:219 */     options.add("-N");
/*  92:220 */     options.add(getName());
/*  93:222 */     if (getDebug()) {
/*  94:223 */       options.add("-D");
/*  95:    */     }
/*  96:226 */     return (String[])options.toArray(new String[0]);
/*  97:    */   }
/*  98:    */   
/*  99:    */   public String nameTipText()
/* 100:    */   {
/* 101:236 */     return "Set the name of the new attribute.";
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void setName(String name)
/* 105:    */   {
/* 106:246 */     this.m_attributeName = name;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public String getName()
/* 110:    */   {
/* 111:255 */     return this.m_attributeName;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public String debugTipText()
/* 115:    */   {
/* 116:265 */     return "Set debug mode. If true then the new attribute will be named with the postfix parse of the supplied expression.";
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void setDebug(boolean d)
/* 120:    */   {
/* 121:276 */     this.m_Debug = d;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public boolean getDebug()
/* 125:    */   {
/* 126:285 */     return this.m_Debug;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public String expressionTipText()
/* 130:    */   {
/* 131:295 */     return "Set the math expression to apply. Eg. a1^2*a5/log(a7*4.0)";
/* 132:    */   }
/* 133:    */   
/* 134:    */   public void setExpression(String expr)
/* 135:    */   {
/* 136:304 */     this.m_infixExpression = expr;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public String getExpression()
/* 140:    */   {
/* 141:313 */     return this.m_infixExpression;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public Capabilities getCapabilities()
/* 145:    */   {
/* 146:324 */     Capabilities result = super.getCapabilities();
/* 147:325 */     result.disableAll();
/* 148:    */     
/* 149:    */ 
/* 150:328 */     result.enableAllAttributes();
/* 151:329 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 152:    */     
/* 153:    */ 
/* 154:332 */     result.enableAllClasses();
/* 155:333 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 156:334 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 157:    */     
/* 158:336 */     return result;
/* 159:    */   }
/* 160:    */   
/* 161:    */   public boolean setInputFormat(Instances instanceInfo)
/* 162:    */     throws Exception
/* 163:    */   {
/* 164:351 */     this.m_InstancesHelper = new InstancesHelper(instanceInfo);
/* 165:352 */     Node node = Parser.parse(this.m_infixExpression, this.m_InstancesHelper, new MacroDeclarationsCompositor(new MacroDeclarations[] { this.m_InstancesHelper, new MathFunctions(), new IfElseMacro(), new JavaMacro() }));
/* 166:366 */     if (!(node instanceof Primitives.DoubleExpression)) {
/* 167:367 */       throw new Exception("Expression must be of double type!");
/* 168:    */     }
/* 169:369 */     this.m_Expression = ((Primitives.DoubleExpression)node);
/* 170:    */     
/* 171:371 */     super.setInputFormat(instanceInfo);
/* 172:    */     
/* 173:373 */     Instances outputFormat = new Instances(instanceInfo, 0);
/* 174:    */     Attribute newAttribute;
/* 175:    */     Attribute newAttribute;
/* 176:375 */     if (this.m_attributeName.compareTo("expression") != 0) {
/* 177:376 */       newAttribute = new Attribute(this.m_attributeName);
/* 178:    */     } else {
/* 179:378 */       newAttribute = new Attribute(this.m_infixExpression);
/* 180:    */     }
/* 181:380 */     outputFormat.insertAttributeAt(newAttribute, instanceInfo.numAttributes());
/* 182:381 */     setOutputFormat(outputFormat);
/* 183:382 */     return true;
/* 184:    */   }
/* 185:    */   
/* 186:    */   public boolean input(Instance instance)
/* 187:    */     throws Exception
/* 188:    */   {
/* 189:398 */     if (getInputFormat() == null) {
/* 190:399 */       throw new IllegalStateException("No input instance format defined");
/* 191:    */     }
/* 192:401 */     if (this.m_NewBatch)
/* 193:    */     {
/* 194:402 */       resetQueue();
/* 195:403 */       this.m_NewBatch = false;
/* 196:    */     }
/* 197:406 */     double[] vals = new double[instance.numAttributes() + 1];
/* 198:407 */     System.arraycopy(instance.toDoubleArray(), 0, vals, 0, instance.numAttributes());
/* 199:    */     
/* 200:409 */     this.m_InstancesHelper.setInstance(instance);
/* 201:410 */     vals[(vals.length - 1)] = this.m_Expression.evaluate();
/* 202:411 */     if (this.m_InstancesHelper.missingAccessed()) {
/* 203:412 */       vals[(vals.length - 1)] = Utils.missingValue();
/* 204:    */     }
/* 205:414 */     Instance inst = null;
/* 206:415 */     if ((instance instanceof SparseInstance)) {
/* 207:416 */       inst = new SparseInstance(instance.weight(), vals);
/* 208:    */     } else {
/* 209:418 */       inst = new DenseInstance(instance.weight(), vals);
/* 210:    */     }
/* 211:421 */     copyValues(inst, false, instance.dataset(), outputFormatPeek());
/* 212:    */     
/* 213:423 */     push(inst);
/* 214:424 */     return true;
/* 215:    */   }
/* 216:    */   
/* 217:    */   public String getRevision()
/* 218:    */   {
/* 219:434 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 220:    */   }
/* 221:    */   
/* 222:    */   public static void main(String[] args)
/* 223:    */   {
/* 224:443 */     runFilter(new AddExpression(), args);
/* 225:    */   }
/* 226:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.AddExpression
 * JD-Core Version:    0.7.0.1
 */