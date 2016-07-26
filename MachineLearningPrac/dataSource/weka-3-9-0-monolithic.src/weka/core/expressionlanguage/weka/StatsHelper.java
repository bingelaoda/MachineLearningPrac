/*   1:    */ package weka.core.expressionlanguage.weka;
/*   2:    */ 
/*   3:    */ import java.util.HashMap;
/*   4:    */ import java.util.HashSet;
/*   5:    */ import java.util.Map;
/*   6:    */ import java.util.Set;
/*   7:    */ import weka.core.expressionlanguage.common.Primitives.DoubleExpression;
/*   8:    */ import weka.core.expressionlanguage.core.Node;
/*   9:    */ import weka.core.expressionlanguage.core.VariableDeclarations;
/*  10:    */ import weka.experiment.Stats;
/*  11:    */ 
/*  12:    */ public class StatsHelper
/*  13:    */   implements VariableDeclarations
/*  14:    */ {
/*  15: 53 */   private final Map<String, Node> variables = new HashMap();
/*  16: 54 */   private Set<String> usedVariables = new HashSet();
/*  17:    */   private Stats stats;
/*  18:    */   
/*  19:    */   public StatsHelper()
/*  20:    */   {
/*  21: 61 */     this.variables.put("MAX", new Max(null));
/*  22: 62 */     this.variables.put("MIN", new Min(null));
/*  23: 63 */     this.variables.put("MEAN", new Mean(null));
/*  24: 64 */     this.variables.put("SD", new StdDev(null));
/*  25: 65 */     this.variables.put("COUNT", new Count(null));
/*  26: 66 */     this.variables.put("SUM", new Sum(null));
/*  27: 67 */     this.variables.put("SUMSQUARED", new SumSq(null));
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void setStats(Stats stats)
/*  31:    */   {
/*  32: 76 */     this.stats = stats;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public boolean used()
/*  36:    */   {
/*  37: 87 */     return this.usedVariables.isEmpty();
/*  38:    */   }
/*  39:    */   
/*  40:    */   public boolean used(String name)
/*  41:    */   {
/*  42:101 */     return this.usedVariables.contains(name);
/*  43:    */   }
/*  44:    */   
/*  45:    */   public boolean hasVariable(String name)
/*  46:    */   {
/*  47:112 */     return this.variables.containsKey(name);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public Node getVariable(String name)
/*  51:    */   {
/*  52:125 */     if (this.variables.containsKey(name))
/*  53:    */     {
/*  54:126 */       this.usedVariables.add(name);
/*  55:127 */       return (Node)this.variables.get(name);
/*  56:    */     }
/*  57:129 */     throw new RuntimeException("Variable '" + name + "' undefined!");
/*  58:    */   }
/*  59:    */   
/*  60:    */   private class Max
/*  61:    */     implements Primitives.DoubleExpression
/*  62:    */   {
/*  63:    */     private Max() {}
/*  64:    */     
/*  65:    */     public double evaluate()
/*  66:    */     {
/*  67:135 */       return StatsHelper.this.stats.max;
/*  68:    */     }
/*  69:    */   }
/*  70:    */   
/*  71:    */   private class Min
/*  72:    */     implements Primitives.DoubleExpression
/*  73:    */   {
/*  74:    */     private Min() {}
/*  75:    */     
/*  76:    */     public double evaluate()
/*  77:    */     {
/*  78:142 */       return StatsHelper.this.stats.min;
/*  79:    */     }
/*  80:    */   }
/*  81:    */   
/*  82:    */   private class Mean
/*  83:    */     implements Primitives.DoubleExpression
/*  84:    */   {
/*  85:    */     private Mean() {}
/*  86:    */     
/*  87:    */     public double evaluate()
/*  88:    */     {
/*  89:149 */       return StatsHelper.this.stats.mean;
/*  90:    */     }
/*  91:    */   }
/*  92:    */   
/*  93:    */   private class StdDev
/*  94:    */     implements Primitives.DoubleExpression
/*  95:    */   {
/*  96:    */     private StdDev() {}
/*  97:    */     
/*  98:    */     public double evaluate()
/*  99:    */     {
/* 100:156 */       return StatsHelper.this.stats.stdDev;
/* 101:    */     }
/* 102:    */   }
/* 103:    */   
/* 104:    */   private class Count
/* 105:    */     implements Primitives.DoubleExpression
/* 106:    */   {
/* 107:    */     private Count() {}
/* 108:    */     
/* 109:    */     public double evaluate()
/* 110:    */     {
/* 111:163 */       return StatsHelper.this.stats.count;
/* 112:    */     }
/* 113:    */   }
/* 114:    */   
/* 115:    */   private class Sum
/* 116:    */     implements Primitives.DoubleExpression
/* 117:    */   {
/* 118:    */     private Sum() {}
/* 119:    */     
/* 120:    */     public double evaluate()
/* 121:    */     {
/* 122:170 */       return StatsHelper.this.stats.sum;
/* 123:    */     }
/* 124:    */   }
/* 125:    */   
/* 126:    */   private class SumSq
/* 127:    */     implements Primitives.DoubleExpression
/* 128:    */   {
/* 129:    */     private SumSq() {}
/* 130:    */     
/* 131:    */     public double evaluate()
/* 132:    */     {
/* 133:177 */       return StatsHelper.this.stats.sumSq;
/* 134:    */     }
/* 135:    */   }
/* 136:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.expressionlanguage.weka.StatsHelper
 * JD-Core Version:    0.7.0.1
 */