/*   1:    */ package weka.core.expressionlanguage.common;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Constructor;
/*   4:    */ import java.util.HashMap;
/*   5:    */ import java.util.Map;
/*   6:    */ import weka.core.expressionlanguage.core.Macro;
/*   7:    */ import weka.core.expressionlanguage.core.MacroDeclarations;
/*   8:    */ import weka.core.expressionlanguage.core.Node;
/*   9:    */ import weka.core.expressionlanguage.core.SemanticException;
/*  10:    */ 
/*  11:    */ public class MathFunctions
/*  12:    */   implements MacroDeclarations
/*  13:    */ {
/*  14: 57 */   private static Map<String, Macro> macros = new HashMap();
/*  15:    */   
/*  16:    */   static
/*  17:    */   {
/*  18: 60 */     macros.put("abs", new DoubleUnaryMacro(AbsFunction.class));
/*  19: 61 */     macros.put("sqrt", new DoubleUnaryMacro(SqrtFunction.class));
/*  20: 62 */     macros.put("log", new DoubleUnaryMacro(LogFunction.class));
/*  21: 63 */     macros.put("exp", new DoubleUnaryMacro(ExpFunction.class));
/*  22: 64 */     macros.put("sin", new DoubleUnaryMacro(SinFunction.class));
/*  23: 65 */     macros.put("cos", new DoubleUnaryMacro(CosFunction.class));
/*  24: 66 */     macros.put("tan", new DoubleUnaryMacro(TanFunction.class));
/*  25: 67 */     macros.put("rint", new DoubleUnaryMacro(RintFunction.class));
/*  26: 68 */     macros.put("floor", new DoubleUnaryMacro(FloorFunction.class));
/*  27: 69 */     macros.put("ceil", new DoubleUnaryMacro(CeilFunction.class));
/*  28: 70 */     macros.put("pow", new PowMacro(null));
/*  29:    */   }
/*  30:    */   
/*  31:    */   public boolean hasMacro(String name)
/*  32:    */   {
/*  33: 81 */     return macros.containsKey(name);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public Macro getMacro(String name)
/*  37:    */   {
/*  38: 94 */     if (macros.containsKey(name)) {
/*  39: 95 */       return (Macro)macros.get(name);
/*  40:    */     }
/*  41: 96 */     throw new RuntimeException("Macro '" + name + "' undefined!");
/*  42:    */   }
/*  43:    */   
/*  44:    */   private static class DoubleUnaryMacro
/*  45:    */     implements Macro
/*  46:    */   {
/*  47:    */     private final Class<? extends MathFunctions.DoubleUnaryFunction> func;
/*  48:    */     
/*  49:    */     public DoubleUnaryMacro(Class<? extends MathFunctions.DoubleUnaryFunction> func)
/*  50:    */     {
/*  51:104 */       this.func = func;
/*  52:    */     }
/*  53:    */     
/*  54:    */     private String name()
/*  55:    */     {
/*  56:108 */       return this.func.getSimpleName();
/*  57:    */     }
/*  58:    */     
/*  59:    */     public Node evaluate(Node... params)
/*  60:    */       throws SemanticException
/*  61:    */     {
/*  62:113 */       if (params.length != 1) {
/*  63:114 */         throw new SemanticException("'" + name() + "' takes exactly one argument!");
/*  64:    */       }
/*  65:115 */       if (!(params[0] instanceof Primitives.DoubleExpression)) {
/*  66:116 */         throw new SemanticException("'" + name() + "'s first argument must be double!");
/*  67:    */       }
/*  68:    */       try
/*  69:    */       {
/*  70:119 */         return (Node)this.func.getConstructor(new Class[] { Primitives.DoubleExpression.class }).newInstance(new Object[] { params[0] });
/*  71:    */       }
/*  72:    */       catch (Exception e)
/*  73:    */       {
/*  74:122 */         throw new RuntimeException("Failed to instantiate '" + name() + "'!", e);
/*  75:    */       }
/*  76:    */     }
/*  77:    */   }
/*  78:    */   
/*  79:    */   private static abstract class DoubleUnaryFunction
/*  80:    */     implements Primitives.DoubleExpression
/*  81:    */   {
/*  82:    */     final Primitives.DoubleExpression expr;
/*  83:    */     
/*  84:    */     DoubleUnaryFunction(Primitives.DoubleExpression expr)
/*  85:    */     {
/*  86:133 */       this.expr = expr;
/*  87:    */     }
/*  88:    */   }
/*  89:    */   
/*  90:    */   private static class AbsFunction
/*  91:    */     extends MathFunctions.DoubleUnaryFunction
/*  92:    */   {
/*  93:    */     public AbsFunction(Primitives.DoubleExpression expr)
/*  94:    */     {
/*  95:141 */       super();
/*  96:    */     }
/*  97:    */     
/*  98:    */     public double evaluate()
/*  99:    */     {
/* 100:146 */       return Math.abs(this.expr.evaluate());
/* 101:    */     }
/* 102:    */   }
/* 103:    */   
/* 104:    */   private static class SqrtFunction
/* 105:    */     extends MathFunctions.DoubleUnaryFunction
/* 106:    */   {
/* 107:    */     public SqrtFunction(Primitives.DoubleExpression expr)
/* 108:    */     {
/* 109:154 */       super();
/* 110:    */     }
/* 111:    */     
/* 112:    */     public double evaluate()
/* 113:    */     {
/* 114:159 */       return Math.sqrt(this.expr.evaluate());
/* 115:    */     }
/* 116:    */   }
/* 117:    */   
/* 118:    */   private static class LogFunction
/* 119:    */     extends MathFunctions.DoubleUnaryFunction
/* 120:    */   {
/* 121:    */     public LogFunction(Primitives.DoubleExpression expr)
/* 122:    */     {
/* 123:167 */       super();
/* 124:    */     }
/* 125:    */     
/* 126:    */     public double evaluate()
/* 127:    */     {
/* 128:172 */       return Math.log(this.expr.evaluate());
/* 129:    */     }
/* 130:    */   }
/* 131:    */   
/* 132:    */   private static class ExpFunction
/* 133:    */     extends MathFunctions.DoubleUnaryFunction
/* 134:    */   {
/* 135:    */     public ExpFunction(Primitives.DoubleExpression expr)
/* 136:    */     {
/* 137:180 */       super();
/* 138:    */     }
/* 139:    */     
/* 140:    */     public double evaluate()
/* 141:    */     {
/* 142:185 */       return Math.exp(this.expr.evaluate());
/* 143:    */     }
/* 144:    */   }
/* 145:    */   
/* 146:    */   private static class SinFunction
/* 147:    */     extends MathFunctions.DoubleUnaryFunction
/* 148:    */   {
/* 149:    */     public SinFunction(Primitives.DoubleExpression expr)
/* 150:    */     {
/* 151:193 */       super();
/* 152:    */     }
/* 153:    */     
/* 154:    */     public double evaluate()
/* 155:    */     {
/* 156:198 */       return Math.sin(this.expr.evaluate());
/* 157:    */     }
/* 158:    */   }
/* 159:    */   
/* 160:    */   private static class CosFunction
/* 161:    */     extends MathFunctions.DoubleUnaryFunction
/* 162:    */   {
/* 163:    */     public CosFunction(Primitives.DoubleExpression expr)
/* 164:    */     {
/* 165:207 */       super();
/* 166:    */     }
/* 167:    */     
/* 168:    */     public double evaluate()
/* 169:    */     {
/* 170:212 */       return Math.cos(this.expr.evaluate());
/* 171:    */     }
/* 172:    */   }
/* 173:    */   
/* 174:    */   private static class TanFunction
/* 175:    */     extends MathFunctions.DoubleUnaryFunction
/* 176:    */   {
/* 177:    */     public TanFunction(Primitives.DoubleExpression expr)
/* 178:    */     {
/* 179:220 */       super();
/* 180:    */     }
/* 181:    */     
/* 182:    */     public double evaluate()
/* 183:    */     {
/* 184:225 */       return Math.tan(this.expr.evaluate());
/* 185:    */     }
/* 186:    */   }
/* 187:    */   
/* 188:    */   private static class RintFunction
/* 189:    */     extends MathFunctions.DoubleUnaryFunction
/* 190:    */   {
/* 191:    */     public RintFunction(Primitives.DoubleExpression expr)
/* 192:    */     {
/* 193:233 */       super();
/* 194:    */     }
/* 195:    */     
/* 196:    */     public double evaluate()
/* 197:    */     {
/* 198:238 */       return Math.rint(this.expr.evaluate());
/* 199:    */     }
/* 200:    */   }
/* 201:    */   
/* 202:    */   private static class FloorFunction
/* 203:    */     extends MathFunctions.DoubleUnaryFunction
/* 204:    */   {
/* 205:    */     public FloorFunction(Primitives.DoubleExpression expr)
/* 206:    */     {
/* 207:246 */       super();
/* 208:    */     }
/* 209:    */     
/* 210:    */     public double evaluate()
/* 211:    */     {
/* 212:251 */       return Math.floor(this.expr.evaluate());
/* 213:    */     }
/* 214:    */   }
/* 215:    */   
/* 216:    */   private static class CeilFunction
/* 217:    */     extends MathFunctions.DoubleUnaryFunction
/* 218:    */   {
/* 219:    */     public CeilFunction(Primitives.DoubleExpression expr)
/* 220:    */     {
/* 221:259 */       super();
/* 222:    */     }
/* 223:    */     
/* 224:    */     public double evaluate()
/* 225:    */     {
/* 226:264 */       return Math.ceil(this.expr.evaluate());
/* 227:    */     }
/* 228:    */   }
/* 229:    */   
/* 230:    */   private static class PowMacro
/* 231:    */     implements Macro
/* 232:    */   {
/* 233:    */     public Node evaluate(Node... params)
/* 234:    */       throws SemanticException
/* 235:    */     {
/* 236:273 */       if (params.length != 2) {
/* 237:274 */         throw new SemanticException("pow takes exactly two arguments!");
/* 238:    */       }
/* 239:275 */       if (!(params[0] instanceof Primitives.DoubleExpression)) {
/* 240:276 */         throw new SemanticException("pow's first argument must be double!");
/* 241:    */       }
/* 242:277 */       if (!(params[1] instanceof Primitives.DoubleExpression)) {
/* 243:278 */         throw new SemanticException("pow's second argument must be double!");
/* 244:    */       }
/* 245:279 */       return new MathFunctions.PowFunction((Primitives.DoubleExpression)params[0], (Primitives.DoubleExpression)params[1]);
/* 246:    */     }
/* 247:    */   }
/* 248:    */   
/* 249:    */   private static class PowFunction
/* 250:    */     implements Primitives.DoubleExpression
/* 251:    */   {
/* 252:    */     private final Primitives.DoubleExpression base;
/* 253:    */     private final Primitives.DoubleExpression exponent;
/* 254:    */     
/* 255:    */     public PowFunction(Primitives.DoubleExpression base, Primitives.DoubleExpression exponent)
/* 256:    */     {
/* 257:290 */       this.base = base;
/* 258:291 */       this.exponent = exponent;
/* 259:    */     }
/* 260:    */     
/* 261:    */     public double evaluate()
/* 262:    */     {
/* 263:296 */       return Math.pow(this.base.evaluate(), this.exponent.evaluate());
/* 264:    */     }
/* 265:    */   }
/* 266:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.expressionlanguage.common.MathFunctions
 * JD-Core Version:    0.7.0.1
 */