/*   1:    */ package weka.core.expressionlanguage.common;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.HashMap;
/*   5:    */ import java.util.Map;
/*   6:    */ import java.util.Set;
/*   7:    */ import weka.core.expressionlanguage.core.Node;
/*   8:    */ import weka.core.expressionlanguage.core.VariableDeclarations;
/*   9:    */ 
/*  10:    */ public class SimpleVariableDeclarations
/*  11:    */   implements VariableDeclarations
/*  12:    */ {
/*  13:    */   private Map<String, Node> variables;
/*  14:    */   private VariableInitializer initializer;
/*  15:    */   
/*  16:    */   public SimpleVariableDeclarations()
/*  17:    */   {
/*  18: 44 */     this.variables = new HashMap();
/*  19:    */     
/*  20:    */ 
/*  21: 47 */     this.initializer = new VariableInitializer();
/*  22:    */   }
/*  23:    */   
/*  24:    */   public boolean hasVariable(String name)
/*  25:    */   {
/*  26: 57 */     return this.variables.containsKey(name);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public Node getVariable(String name)
/*  30:    */   {
/*  31: 68 */     if (!this.variables.containsKey(name)) {
/*  32: 69 */       throw new RuntimeException("Variable '" + name + "' doesn't exist!");
/*  33:    */     }
/*  34: 71 */     this.initializer.add((Node)this.variables.get(name));
/*  35: 72 */     return (Node)this.variables.get(name);
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void addBoolean(String name)
/*  39:    */   {
/*  40: 83 */     if (this.variables.containsKey(name)) {
/*  41: 84 */       throw new RuntimeException("Variable '" + name + "' already exists!");
/*  42:    */     }
/*  43: 85 */     this.variables.put(name, new Primitives.BooleanVariable(name));
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void addDouble(String name)
/*  47:    */   {
/*  48: 96 */     if (this.variables.containsKey(name)) {
/*  49: 97 */       throw new RuntimeException("Variable '" + name + "' already exists!");
/*  50:    */     }
/*  51: 98 */     this.variables.put(name, new Primitives.DoubleVariable(name));
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void addString(String name)
/*  55:    */   {
/*  56:109 */     if (this.variables.containsKey(name)) {
/*  57:110 */       throw new RuntimeException("Variable '" + name + "' already exists!");
/*  58:    */     }
/*  59:111 */     this.variables.put(name, new Primitives.StringVariable(name));
/*  60:    */   }
/*  61:    */   
/*  62:    */   public VariableInitializer getInitializer()
/*  63:    */   {
/*  64:120 */     return this.initializer;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public static class VariableInitializer
/*  68:    */     implements Serializable
/*  69:    */   {
/*  70:140 */     private Map<String, Node> variables = new HashMap();
/*  71:    */     
/*  72:    */     private void add(Node var)
/*  73:    */     {
/*  74:155 */       assert (((var instanceof Primitives.BooleanVariable)) || ((var instanceof Primitives.DoubleVariable)) || ((var instanceof Primitives.StringVariable)));
/*  75:158 */       if ((var instanceof Primitives.BooleanVariable)) {
/*  76:159 */         this.variables.put(((Primitives.BooleanVariable)var).getName(), var);
/*  77:160 */       } else if ((var instanceof Primitives.DoubleVariable)) {
/*  78:161 */         this.variables.put(((Primitives.DoubleVariable)var).getName(), var);
/*  79:162 */       } else if ((var instanceof Primitives.StringVariable)) {
/*  80:163 */         this.variables.put(((Primitives.StringVariable)var).getName(), var);
/*  81:    */       }
/*  82:    */     }
/*  83:    */     
/*  84:    */     public Set<String> getVariables()
/*  85:    */     {
/*  86:172 */       return this.variables.keySet();
/*  87:    */     }
/*  88:    */     
/*  89:    */     public boolean hasVariable(String variable)
/*  90:    */     {
/*  91:182 */       return this.variables.containsKey(variable);
/*  92:    */     }
/*  93:    */     
/*  94:    */     public void setBoolean(String name, boolean value)
/*  95:    */     {
/*  96:194 */       if (!this.variables.containsKey(name)) {
/*  97:195 */         throw new RuntimeException("Variable '" + name + "' doesn't exist!");
/*  98:    */       }
/*  99:196 */       if (!(this.variables.get(name) instanceof Primitives.BooleanVariable)) {
/* 100:197 */         throw new RuntimeException("Variable '" + name + "' is not of boolean type!");
/* 101:    */       }
/* 102:199 */       ((Primitives.BooleanVariable)this.variables.get(name)).setValue(value);
/* 103:    */     }
/* 104:    */     
/* 105:    */     public void setDouble(String name, double value)
/* 106:    */     {
/* 107:211 */       if (!this.variables.containsKey(name)) {
/* 108:212 */         throw new RuntimeException("Variable '" + name + "' doesn't exist!");
/* 109:    */       }
/* 110:213 */       if (!(this.variables.get(name) instanceof Primitives.DoubleVariable)) {
/* 111:214 */         throw new RuntimeException("Variable '" + name + "' is not of double type!");
/* 112:    */       }
/* 113:216 */       ((Primitives.DoubleVariable)this.variables.get(name)).setValue(value);
/* 114:    */     }
/* 115:    */     
/* 116:    */     public void setString(String name, String value)
/* 117:    */     {
/* 118:228 */       if (!this.variables.containsKey(name)) {
/* 119:229 */         throw new RuntimeException("Variable '" + name + "' doesn't exist!");
/* 120:    */       }
/* 121:230 */       if (!(this.variables.get(name) instanceof Primitives.StringVariable)) {
/* 122:231 */         throw new RuntimeException("Variable '" + name + "' is not of String type!");
/* 123:    */       }
/* 124:233 */       ((Primitives.StringVariable)this.variables.get(name)).setValue(value);
/* 125:    */     }
/* 126:    */   }
/* 127:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.expressionlanguage.common.SimpleVariableDeclarations
 * JD-Core Version:    0.7.0.1
 */