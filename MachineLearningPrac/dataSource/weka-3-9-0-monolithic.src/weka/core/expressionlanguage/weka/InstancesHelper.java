/*   1:    */ package weka.core.expressionlanguage.weka;
/*   2:    */ 
/*   3:    */ import java.util.regex.Matcher;
/*   4:    */ import java.util.regex.Pattern;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Instance;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.Utils;
/*   9:    */ import weka.core.expressionlanguage.common.Primitives.BooleanExpression;
/*  10:    */ import weka.core.expressionlanguage.common.Primitives.DoubleExpression;
/*  11:    */ import weka.core.expressionlanguage.common.Primitives.StringExpression;
/*  12:    */ import weka.core.expressionlanguage.core.Macro;
/*  13:    */ import weka.core.expressionlanguage.core.MacroDeclarations;
/*  14:    */ import weka.core.expressionlanguage.core.Node;
/*  15:    */ import weka.core.expressionlanguage.core.SemanticException;
/*  16:    */ import weka.core.expressionlanguage.core.VariableDeclarations;
/*  17:    */ 
/*  18:    */ public class InstancesHelper
/*  19:    */   implements VariableDeclarations, Macro, MacroDeclarations
/*  20:    */ {
/*  21: 56 */   private static final Pattern ATTRIBUTE1 = Pattern.compile("[aA][0-9]+");
/*  22: 59 */   private static final Pattern ATTRIBUTE2 = Pattern.compile("ATT[0-9]+");
/*  23:    */   private static final String CLASS = "CLASS";
/*  24:    */   private static final String IS_MISSING = "ismissing";
/*  25:    */   private static final long serialVersionUID = -4398876812339967703L;
/*  26:    */   private final Instances dataset;
/*  27:    */   private Instance instance;
/*  28: 76 */   private boolean missingAccessed = false;
/*  29:    */   
/*  30:    */   public InstancesHelper(Instances dataset)
/*  31:    */   {
/*  32: 84 */     assert (dataset != null);
/*  33: 85 */     this.dataset = dataset;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public boolean hasMacro(String name)
/*  37:    */   {
/*  38: 96 */     return "ismissing".equals(name);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public Macro getMacro(String name)
/*  42:    */   {
/*  43:109 */     if (hasMacro(name)) {
/*  44:110 */       return this;
/*  45:    */     }
/*  46:111 */     throw new RuntimeException("Macro '" + name + "' undefined!");
/*  47:    */   }
/*  48:    */   
/*  49:    */   public Node evaluate(Node... params)
/*  50:    */     throws SemanticException
/*  51:    */   {
/*  52:121 */     if (params.length != 1) {
/*  53:122 */       throw new SemanticException("Macro ismissing takes exactly one argument!");
/*  54:    */     }
/*  55:123 */     if ((params[0] instanceof Value)) {
/*  56:124 */       return new isMissing((Value)params[0]);
/*  57:    */     }
/*  58:125 */     throw new SemanticException("ismissing is only applicable to a dataset value!");
/*  59:    */   }
/*  60:    */   
/*  61:    */   private static class isMissing
/*  62:    */     implements Primitives.BooleanExpression
/*  63:    */   {
/*  64:    */     private static final long serialVersionUID = -3805035561340865906L;
/*  65:    */     private final InstancesHelper.Value value;
/*  66:    */     
/*  67:    */     public isMissing(InstancesHelper.Value value)
/*  68:    */     {
/*  69:135 */       this.value = value;
/*  70:    */     }
/*  71:    */     
/*  72:    */     public boolean evaluate()
/*  73:    */     {
/*  74:140 */       return this.value.isMissing();
/*  75:    */     }
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void setInstance(int i)
/*  79:    */   {
/*  80:150 */     setInstance(this.dataset.get(i));
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void setInstance(Instance instance)
/*  84:    */   {
/*  85:159 */     assert (this.dataset.equalHeaders(instance.dataset()));
/*  86:160 */     this.instance = instance;
/*  87:161 */     this.missingAccessed = false;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public boolean missingAccessed()
/*  91:    */   {
/*  92:173 */     return this.missingAccessed;
/*  93:    */   }
/*  94:    */   
/*  95:    */   private int getIndex(String attribute)
/*  96:    */   {
/*  97:178 */     if (ATTRIBUTE1.matcher(attribute).matches()) {
/*  98:179 */       return Integer.parseInt(attribute.substring(1)) - 1;
/*  99:    */     }
/* 100:180 */     if (ATTRIBUTE2.matcher(attribute).matches()) {
/* 101:181 */       return Integer.parseInt(attribute.substring(3)) - 1;
/* 102:    */     }
/* 103:182 */     if ("CLASS".equals(attribute)) {
/* 104:183 */       return this.dataset.classIndex();
/* 105:    */     }
/* 106:185 */     return -1;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public boolean hasVariable(String name)
/* 110:    */   {
/* 111:196 */     int index = getIndex(name);
/* 112:198 */     if ((0 <= index) && (index < this.dataset.numAttributes())) {
/* 113:199 */       return true;
/* 114:    */     }
/* 115:200 */     return false;
/* 116:    */   }
/* 117:    */   
/* 118:    */   public Node getVariable(String name)
/* 119:    */   {
/* 120:213 */     int index = getIndex(name);
/* 121:215 */     if ((index < 0) || (index >= this.dataset.numAttributes())) {
/* 122:216 */       throw new RuntimeException("Variable '" + name + "' undefined!");
/* 123:    */     }
/* 124:218 */     if (this.dataset.attribute(index).isNumeric()) {
/* 125:219 */       return new DoubleValue(index);
/* 126:    */     }
/* 127:220 */     if ((this.dataset.attribute(index).isString()) || (this.dataset.attribute(index).isNominal())) {
/* 128:221 */       return new StringValue(index);
/* 129:    */     }
/* 130:222 */     throw new RuntimeException("Attributes of type '" + this.dataset.attribute(index).toString() + "' not supported!");
/* 131:    */   }
/* 132:    */   
/* 133:    */   private abstract class Value
/* 134:    */     implements Node
/* 135:    */   {
/* 136:    */     private static final long serialVersionUID = 5839070716097467627L;
/* 137:    */     private final int index;
/* 138:    */     
/* 139:    */     public Value(int index)
/* 140:    */     {
/* 141:233 */       this.index = index;
/* 142:    */     }
/* 143:    */     
/* 144:    */     public int getIndex()
/* 145:    */     {
/* 146:237 */       return this.index;
/* 147:    */     }
/* 148:    */     
/* 149:    */     public boolean isMissing()
/* 150:    */     {
/* 151:241 */       return InstancesHelper.this.instance.isMissing(getIndex());
/* 152:    */     }
/* 153:    */   }
/* 154:    */   
/* 155:    */   private class DoubleValue
/* 156:    */     extends InstancesHelper.Value
/* 157:    */     implements Primitives.DoubleExpression
/* 158:    */   {
/* 159:    */     private static final long serialVersionUID = -1001674545929082424L;
/* 160:    */     
/* 161:    */     public DoubleValue(int index)
/* 162:    */     {
/* 163:250 */       super(index);
/* 164:251 */       assert (InstancesHelper.this.dataset.attribute(getIndex()).isNumeric());
/* 165:    */     }
/* 166:    */     
/* 167:    */     public double evaluate()
/* 168:    */     {
/* 169:256 */       if (isMissing())
/* 170:    */       {
/* 171:257 */         InstancesHelper.this.missingAccessed = true;
/* 172:258 */         return Utils.missingValue();
/* 173:    */       }
/* 174:260 */       return InstancesHelper.this.instance.value(getIndex());
/* 175:    */     }
/* 176:    */   }
/* 177:    */   
/* 178:    */   private class StringValue
/* 179:    */     extends InstancesHelper.Value
/* 180:    */     implements Primitives.StringExpression
/* 181:    */   {
/* 182:    */     private static final long serialVersionUID = -249974216283801876L;
/* 183:    */     
/* 184:    */     public StringValue(int index)
/* 185:    */     {
/* 186:269 */       super(index);
/* 187:270 */       assert ((InstancesHelper.this.dataset.attribute(index).isString()) || (InstancesHelper.this.dataset.attribute(index).isNominal()));
/* 188:    */     }
/* 189:    */     
/* 190:    */     public String evaluate()
/* 191:    */     {
/* 192:275 */       if (isMissing())
/* 193:    */       {
/* 194:276 */         InstancesHelper.this.missingAccessed = true;
/* 195:277 */         return "";
/* 196:    */       }
/* 197:279 */       return InstancesHelper.this.instance.stringValue(getIndex());
/* 198:    */     }
/* 199:    */   }
/* 200:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.expressionlanguage.weka.InstancesHelper
 * JD-Core Version:    0.7.0.1
 */