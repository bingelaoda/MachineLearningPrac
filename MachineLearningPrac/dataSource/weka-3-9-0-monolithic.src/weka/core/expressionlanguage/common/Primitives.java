/*   1:    */ package weka.core.expressionlanguage.common;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.expressionlanguage.core.Node;
/*   5:    */ 
/*   6:    */ public class Primitives
/*   7:    */   implements Serializable
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = -6356635298310530223L;
/*  10:    */   
/*  11:    */   public static abstract interface BooleanExpression
/*  12:    */     extends Node
/*  13:    */   {
/*  14:    */     public abstract boolean evaluate();
/*  15:    */   }
/*  16:    */   
/*  17:    */   public static abstract interface DoubleExpression
/*  18:    */     extends Node
/*  19:    */   {
/*  20:    */     public abstract double evaluate();
/*  21:    */   }
/*  22:    */   
/*  23:    */   public static abstract interface StringExpression
/*  24:    */     extends Node
/*  25:    */   {
/*  26:    */     public abstract String evaluate();
/*  27:    */   }
/*  28:    */   
/*  29:    */   public static class BooleanConstant
/*  30:    */     implements Primitives.BooleanExpression, Serializable
/*  31:    */   {
/*  32:    */     private static final long serialVersionUID = -7104666336890622673L;
/*  33:    */     private final boolean value;
/*  34:    */     
/*  35:    */     public BooleanConstant(boolean value)
/*  36:    */     {
/*  37:103 */       this.value = value;
/*  38:    */     }
/*  39:    */     
/*  40:    */     public boolean evaluate()
/*  41:    */     {
/*  42:108 */       return this.value;
/*  43:    */     }
/*  44:    */   }
/*  45:    */   
/*  46:    */   public static class DoubleConstant
/*  47:    */     implements Primitives.DoubleExpression, Serializable
/*  48:    */   {
/*  49:    */     private static final long serialVersionUID = 6876724986473710563L;
/*  50:    */     private final double value;
/*  51:    */     
/*  52:    */     public DoubleConstant(double value)
/*  53:    */     {
/*  54:125 */       this.value = value;
/*  55:    */     }
/*  56:    */     
/*  57:    */     public double evaluate()
/*  58:    */     {
/*  59:130 */       return this.value;
/*  60:    */     }
/*  61:    */   }
/*  62:    */   
/*  63:    */   public static class StringConstant
/*  64:    */     implements Primitives.StringExpression, Serializable
/*  65:    */   {
/*  66:    */     private static final long serialVersionUID = 491766938196527684L;
/*  67:    */     private final String value;
/*  68:    */     
/*  69:    */     public StringConstant(String value)
/*  70:    */     {
/*  71:148 */       this.value = value;
/*  72:    */     }
/*  73:    */     
/*  74:    */     public String evaluate()
/*  75:    */     {
/*  76:153 */       return this.value;
/*  77:    */     }
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static class BooleanVariable
/*  81:    */     implements Primitives.BooleanExpression, Serializable
/*  82:    */   {
/*  83:    */     private static final long serialVersionUID = 6041670101306161521L;
/*  84:    */     private boolean value;
/*  85:    */     private final String name;
/*  86:    */     
/*  87:    */     public BooleanVariable(String name)
/*  88:    */     {
/*  89:171 */       this.name = name;
/*  90:    */     }
/*  91:    */     
/*  92:    */     public boolean evaluate()
/*  93:    */     {
/*  94:176 */       return this.value;
/*  95:    */     }
/*  96:    */     
/*  97:    */     public String getName()
/*  98:    */     {
/*  99:180 */       return this.name;
/* 100:    */     }
/* 101:    */     
/* 102:    */     public boolean getValue()
/* 103:    */     {
/* 104:184 */       return this.value;
/* 105:    */     }
/* 106:    */     
/* 107:    */     public void setValue(boolean value)
/* 108:    */     {
/* 109:188 */       this.value = value;
/* 110:    */     }
/* 111:    */   }
/* 112:    */   
/* 113:    */   public static class DoubleVariable
/* 114:    */     implements Primitives.DoubleExpression, Serializable
/* 115:    */   {
/* 116:    */     private static final long serialVersionUID = -6059066803856814750L;
/* 117:    */     private double value;
/* 118:    */     private final String name;
/* 119:    */     
/* 120:    */     public DoubleVariable(String name)
/* 121:    */     {
/* 122:206 */       this.name = name;
/* 123:    */     }
/* 124:    */     
/* 125:    */     public double evaluate()
/* 126:    */     {
/* 127:211 */       return this.value;
/* 128:    */     }
/* 129:    */     
/* 130:    */     public String getName()
/* 131:    */     {
/* 132:215 */       return this.name;
/* 133:    */     }
/* 134:    */     
/* 135:    */     public double getValue()
/* 136:    */     {
/* 137:219 */       return this.value;
/* 138:    */     }
/* 139:    */     
/* 140:    */     public void setValue(double value)
/* 141:    */     {
/* 142:223 */       this.value = value;
/* 143:    */     }
/* 144:    */   }
/* 145:    */   
/* 146:    */   public static class StringVariable
/* 147:    */     implements Primitives.StringExpression, Serializable
/* 148:    */   {
/* 149:    */     private static final long serialVersionUID = -7332982581964981085L;
/* 150:    */     private final String name;
/* 151:    */     private String value;
/* 152:    */     
/* 153:    */     public StringVariable(String name)
/* 154:    */     {
/* 155:241 */       this.name = name;
/* 156:    */     }
/* 157:    */     
/* 158:    */     public String evaluate()
/* 159:    */     {
/* 160:246 */       return this.value;
/* 161:    */     }
/* 162:    */     
/* 163:    */     public String getName()
/* 164:    */     {
/* 165:250 */       return this.name;
/* 166:    */     }
/* 167:    */     
/* 168:    */     public String getValue()
/* 169:    */     {
/* 170:254 */       return this.value;
/* 171:    */     }
/* 172:    */     
/* 173:    */     public void setValue(String value)
/* 174:    */     {
/* 175:258 */       this.value = value;
/* 176:    */     }
/* 177:    */   }
/* 178:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.expressionlanguage.common.Primitives
 * JD-Core Version:    0.7.0.1
 */