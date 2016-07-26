/*   1:    */ package weka.core.expressionlanguage.common;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.lang.reflect.Method;
/*   5:    */ import java.lang.reflect.Modifier;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.Arrays;
/*   8:    */ import java.util.List;
/*   9:    */ import java.util.StringTokenizer;
/*  10:    */ import weka.core.expressionlanguage.core.Macro;
/*  11:    */ import weka.core.expressionlanguage.core.MacroDeclarations;
/*  12:    */ import weka.core.expressionlanguage.core.Node;
/*  13:    */ import weka.core.expressionlanguage.core.SemanticException;
/*  14:    */ 
/*  15:    */ public class JavaMacro
/*  16:    */   implements MacroDeclarations, Macro
/*  17:    */ {
/*  18:    */   private static final String JAVA_MACRO = "java";
/*  19:    */   private static final String BOOLEAN = "boolean";
/*  20:    */   private static final String DOUBLE = "double";
/*  21:    */   private static final String STRING = "String";
/*  22:    */   
/*  23:    */   public Node evaluate(Node... nodes)
/*  24:    */     throws SemanticException
/*  25:    */   {
/*  26: 78 */     if (nodes.length < 2) {
/*  27: 79 */       throw new SemanticException("The java macro takes at least 2 arguments!");
/*  28:    */     }
/*  29: 80 */     if ((!(nodes[0] instanceof Primitives.StringConstant)) || (!(nodes[1] instanceof Primitives.StringConstant))) {
/*  30: 81 */       throw new SemanticException("java's first and second argument must be String constants!");
/*  31:    */     }
/*  32: 83 */     Node[] parameterNodes = (Node[])Arrays.copyOfRange(nodes, 2, nodes.length);
/*  33:    */     
/*  34: 85 */     String className = ((Primitives.StringConstant)nodes[0]).evaluate();
/*  35: 86 */     String signature = ((Primitives.StringConstant)nodes[1]).evaluate();
/*  36:    */     
/*  37:    */ 
/*  38: 89 */     List<Class<?>> parameterTypes = new ArrayList();
/*  39: 90 */     String name = parseSignature(signature, parameterTypes);
/*  40: 91 */     Class<?> returnType = (Class)parameterTypes.remove(0);
/*  41:    */     Method m;
/*  42:    */     try
/*  43:    */     {
/*  44: 96 */       m = Class.forName(className).getMethod(name, (Class[])parameterTypes.toArray(new Class[0]));
/*  45:    */     }
/*  46:    */     catch (Exception e)
/*  47:    */     {
/*  48: 98 */       throw new SemanticException("Failed to load method '" + className + "." + name + "' in " + "java" + " macro!", e);
/*  49:    */     }
/*  50:103 */     if (parameterTypes.size() != parameterNodes.length) {
/*  51:104 */       throw new SemanticException("Wrong amount of parameters given in java macro!");
/*  52:    */     }
/*  53:105 */     for (int i = 0; (i < parameterTypes.size()) && (i < parameterNodes.length); i++) {
/*  54:106 */       if ((!((Class)parameterTypes.get(i)).equals(Boolean.TYPE)) || (!(parameterNodes[i] instanceof Primitives.BooleanExpression))) {
/*  55:108 */         if ((!((Class)parameterTypes.get(i)).equals(Double.TYPE)) || (!(parameterNodes[i] instanceof Primitives.DoubleExpression))) {
/*  56:110 */           if ((!((Class)parameterTypes.get(i)).equals(String.class)) || (!(parameterNodes[i] instanceof Primitives.StringExpression))) {
/*  57:112 */             throw new SemanticException("Type error in java macro!");
/*  58:    */           }
/*  59:    */         }
/*  60:    */       }
/*  61:    */     }
/*  62:115 */     if (returnType.equals(Boolean.TYPE)) {
/*  63:116 */       return new BooleanJavaMethod(m, parameterNodes);
/*  64:    */     }
/*  65:117 */     if (returnType.equals(Double.TYPE)) {
/*  66:118 */       return new DoubleJavaMethod(m, parameterNodes);
/*  67:    */     }
/*  68:119 */     if (returnType.equals(String.class)) {
/*  69:120 */       return new StringJavaMethod(m, parameterNodes);
/*  70:    */     }
/*  71:122 */     if (!$assertionsDisabled) {
/*  72:122 */       throw new AssertionError();
/*  73:    */     }
/*  74:123 */     throw new SemanticException("Internal error in java macro!");
/*  75:    */   }
/*  76:    */   
/*  77:    */   private String parseSignature(String signature, List<Class<?>> types)
/*  78:    */     throws JavaMacro.InvalidSignature
/*  79:    */   {
/*  80:137 */     List<String> tokens = tokenize(signature);
/*  81:139 */     if (tokens.size() < 4) {
/*  82:140 */       throw new InvalidSignature("Not enough tokens in '" + signature + "'");
/*  83:    */     }
/*  84:143 */     types.add(getType((String)tokens.get(0)));
/*  85:146 */     if (!isJavaIdentifier((String)tokens.get(1))) {
/*  86:147 */       throw new InvalidSignature("Invalid function name '" + (String)tokens.get(1) + "'");
/*  87:    */     }
/*  88:148 */     String name = (String)tokens.get(1);
/*  89:151 */     if (!"(".equals(tokens.get(2))) {
/*  90:152 */       throw new InvalidSignature("Missing opening bracket, got '" + (String)tokens.get(2) + "' instead");
/*  91:    */     }
/*  92:154 */     boolean first = true;
/*  93:155 */     for (int i = 3; (i < tokens.size()) && (!")".equals(tokens.get(i))); i++)
/*  94:    */     {
/*  95:159 */       if ((!first) && (!",".equals(tokens.get(i)))) {
/*  96:160 */         throw new InvalidSignature("Missing comma between parameters, got '" + (String)tokens.get(i) + "' instead");
/*  97:    */       }
/*  98:162 */       if (!first) {
/*  99:163 */         i++;
/* 100:    */       }
/* 101:166 */       if (i >= tokens.size()) {
/* 102:167 */         throw new InvalidSignature("No parameter after comma!");
/* 103:    */       }
/* 104:168 */       types.add(getType((String)tokens.get(i)));
/* 105:    */       
/* 106:170 */       first = false;
/* 107:    */     }
/* 108:174 */     if ((i < tokens.size()) && (!")".equals(tokens.get(i))))
/* 109:    */     {
/* 110:175 */       System.out.println(i);
/* 111:176 */       System.out.println(tokens);
/* 112:177 */       throw new InvalidSignature("Missing closing bracket, got '" + (String)tokens.get(i) + "' instead");
/* 113:    */     }
/* 114:181 */     if (i != tokens.size() - 1) {
/* 115:182 */       throw new InvalidSignature("Failed parsing signature at token '" + (String)tokens.get(i) + "'");
/* 116:    */     }
/* 117:184 */     return name;
/* 118:    */   }
/* 119:    */   
/* 120:    */   private List<String> tokenize(String signature)
/* 121:    */   {
/* 122:195 */     String[] whiteSpaceTokens = signature.split("\\s+");
/* 123:    */     
/* 124:    */ 
/* 125:198 */     List<String> tokens = new ArrayList();
/* 126:199 */     for (String token : whiteSpaceTokens)
/* 127:    */     {
/* 128:200 */       StringTokenizer tokenizer = new StringTokenizer(token, ",()", true);
/* 129:201 */       while (tokenizer.hasMoreElements()) {
/* 130:202 */         tokens.add(tokenizer.nextToken());
/* 131:    */       }
/* 132:    */     }
/* 133:205 */     return tokens;
/* 134:    */   }
/* 135:    */   
/* 136:    */   private Class<?> getType(String type)
/* 137:    */     throws JavaMacro.InvalidSignature
/* 138:    */   {
/* 139:216 */     if (type.equals("boolean")) {
/* 140:217 */       return Boolean.TYPE;
/* 141:    */     }
/* 142:218 */     if (type.equals("double")) {
/* 143:219 */       return Double.TYPE;
/* 144:    */     }
/* 145:220 */     if (type.equals("String")) {
/* 146:221 */       return String.class;
/* 147:    */     }
/* 148:223 */     throw new InvalidSignature("Expected type, got '" + type + "' instead");
/* 149:    */   }
/* 150:    */   
/* 151:    */   private boolean isJavaIdentifier(String identifier)
/* 152:    */   {
/* 153:234 */     if (identifier.length() == 0) {
/* 154:235 */       return false;
/* 155:    */     }
/* 156:236 */     if (!Character.isJavaIdentifierStart(identifier.charAt(0))) {
/* 157:237 */       return false;
/* 158:    */     }
/* 159:238 */     for (int i = 1; i < identifier.length(); i++) {
/* 160:239 */       if (!Character.isJavaIdentifierPart(identifier.charAt(i))) {
/* 161:240 */         return false;
/* 162:    */       }
/* 163:    */     }
/* 164:241 */     return true;
/* 165:    */   }
/* 166:    */   
/* 167:    */   private static class InvalidSignature
/* 168:    */     extends SemanticException
/* 169:    */   {
/* 170:    */     private static final long serialVersionUID = -4198745015342335018L;
/* 171:    */     
/* 172:    */     public InvalidSignature(String reason)
/* 173:    */     {
/* 174:249 */       super();
/* 175:    */     }
/* 176:    */   }
/* 177:    */   
/* 178:    */   public boolean hasMacro(String name)
/* 179:    */   {
/* 180:261 */     return "java".equals(name);
/* 181:    */   }
/* 182:    */   
/* 183:    */   public Macro getMacro(String name)
/* 184:    */   {
/* 185:266 */     if (hasMacro(name)) {
/* 186:267 */       return this;
/* 187:    */     }
/* 188:268 */     throw new RuntimeException("Undefined macro '" + name + "'!");
/* 189:    */   }
/* 190:    */   
/* 191:    */   private static abstract class JavaMethod
/* 192:    */     implements Node
/* 193:    */   {
/* 194:    */     protected final Method method;
/* 195:    */     protected final Node[] params;
/* 196:    */     protected final Object[] args;
/* 197:    */     
/* 198:    */     public JavaMethod(Method method, Node... params)
/* 199:    */     {
/* 200:287 */       assert (Modifier.isStatic(method.getModifiers()));
/* 201:    */       
/* 202:289 */       this.method = method;
/* 203:290 */       this.params = params;
/* 204:291 */       this.args = new Object[params.length];
/* 205:    */     }
/* 206:    */     
/* 207:    */     protected void evaluateArgs()
/* 208:    */     {
/* 209:296 */       for (int i = 0; i < this.params.length; i++) {
/* 210:297 */         if ((this.params[i] instanceof Primitives.BooleanExpression)) {
/* 211:298 */           this.args[i] = new Boolean(((Primitives.BooleanExpression)this.params[i]).evaluate());
/* 212:299 */         } else if ((this.params[i] instanceof Primitives.DoubleExpression)) {
/* 213:300 */           this.args[i] = new Double(((Primitives.DoubleExpression)this.params[i]).evaluate());
/* 214:301 */         } else if ((this.params[i] instanceof Primitives.StringExpression)) {
/* 215:302 */           this.args[i] = ((Primitives.StringExpression)this.params[i]).evaluate();
/* 216:    */         }
/* 217:    */       }
/* 218:    */     }
/* 219:    */   }
/* 220:    */   
/* 221:    */   private static class BooleanJavaMethod
/* 222:    */     extends JavaMacro.JavaMethod
/* 223:    */     implements Primitives.BooleanExpression
/* 224:    */   {
/* 225:    */     public BooleanJavaMethod(Method method, Node... params)
/* 226:    */     {
/* 227:313 */       super(params);
/* 228:314 */       assert (Boolean.TYPE.equals(method.getReturnType()));
/* 229:    */     }
/* 230:    */     
/* 231:    */     public boolean evaluate()
/* 232:    */     {
/* 233:    */       try
/* 234:    */       {
/* 235:320 */         evaluateArgs();
/* 236:321 */         return ((Boolean)this.method.invoke(null, this.args)).booleanValue();
/* 237:    */       }
/* 238:    */       catch (Exception e)
/* 239:    */       {
/* 240:323 */         throw new RuntimeException("Failed to execute java function '" + this.method.getName() + "'!", e);
/* 241:    */       }
/* 242:    */     }
/* 243:    */   }
/* 244:    */   
/* 245:    */   private static class DoubleJavaMethod
/* 246:    */     extends JavaMacro.JavaMethod
/* 247:    */     implements Primitives.DoubleExpression
/* 248:    */   {
/* 249:    */     public DoubleJavaMethod(Method method, Node... params)
/* 250:    */     {
/* 251:332 */       super(params);
/* 252:333 */       assert (Double.TYPE.equals(method.getReturnType()));
/* 253:    */     }
/* 254:    */     
/* 255:    */     public double evaluate()
/* 256:    */     {
/* 257:    */       try
/* 258:    */       {
/* 259:339 */         evaluateArgs();
/* 260:340 */         return ((Double)this.method.invoke(null, this.args)).doubleValue();
/* 261:    */       }
/* 262:    */       catch (Exception e)
/* 263:    */       {
/* 264:342 */         throw new RuntimeException("Failed to execute java function '" + this.method.getName() + "'!", e);
/* 265:    */       }
/* 266:    */     }
/* 267:    */   }
/* 268:    */   
/* 269:    */   private static class StringJavaMethod
/* 270:    */     extends JavaMacro.JavaMethod
/* 271:    */     implements Primitives.StringExpression
/* 272:    */   {
/* 273:    */     public StringJavaMethod(Method method, Node... params)
/* 274:    */     {
/* 275:351 */       super(params);
/* 276:352 */       assert (String.class.equals(method.getReturnType()));
/* 277:    */     }
/* 278:    */     
/* 279:    */     public String evaluate()
/* 280:    */     {
/* 281:    */       try
/* 282:    */       {
/* 283:358 */         evaluateArgs();
/* 284:359 */         return (String)this.method.invoke(null, this.args);
/* 285:    */       }
/* 286:    */       catch (Exception e)
/* 287:    */       {
/* 288:361 */         throw new RuntimeException("Failed to execute java function '" + this.method.getName() + "'!", e);
/* 289:    */       }
/* 290:    */     }
/* 291:    */   }
/* 292:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.expressionlanguage.common.JavaMacro
 * JD-Core Version:    0.7.0.1
 */