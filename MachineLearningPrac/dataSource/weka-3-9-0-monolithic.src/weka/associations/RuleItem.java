/*   1:    */ package weka.associations;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Hashtable;
/*   5:    */ import weka.core.Instances;
/*   6:    */ import weka.core.RevisionHandler;
/*   7:    */ import weka.core.RevisionUtils;
/*   8:    */ 
/*   9:    */ public class RuleItem
/*  10:    */   implements Comparable<RuleItem>, Serializable, RevisionHandler
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = -3761299128347476534L;
/*  13:    */   protected ItemSet m_premise;
/*  14:    */   protected ItemSet m_consequence;
/*  15:    */   protected double m_accuracy;
/*  16:    */   protected int m_genTime;
/*  17:    */   
/*  18:    */   public RuleItem() {}
/*  19:    */   
/*  20:    */   public RuleItem(RuleItem toCopy)
/*  21:    */   {
/*  22: 72 */     this.m_premise = toCopy.m_premise;
/*  23: 73 */     this.m_consequence = toCopy.m_consequence;
/*  24: 74 */     this.m_accuracy = toCopy.m_accuracy;
/*  25: 75 */     this.m_genTime = toCopy.m_genTime;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public RuleItem(ItemSet premise, ItemSet consequence, int genTime, int ruleSupport, double[] m_midPoints, Hashtable<Double, Double> m_priors)
/*  29:    */   {
/*  30: 91 */     this.m_premise = premise;
/*  31: 92 */     this.m_consequence = consequence;
/*  32: 93 */     this.m_accuracy = RuleGeneration.expectation(ruleSupport, this.m_premise.m_counter, m_midPoints, m_priors);
/*  33: 96 */     if ((Double.isNaN(this.m_accuracy)) || (this.m_accuracy < 0.0D)) {
/*  34: 97 */       this.m_accuracy = 4.9E-324D;
/*  35:    */     }
/*  36: 99 */     this.m_consequence.m_counter = ruleSupport;
/*  37:100 */     this.m_genTime = genTime;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public RuleItem generateRuleItem(ItemSet premise, ItemSet consequence, Instances instances, int genTime, int minRuleCount, double[] m_midPoints, Hashtable<Double, Double> m_priors)
/*  41:    */   {
/*  42:120 */     ItemSet rule = new ItemSet(instances.numInstances());
/*  43:121 */     rule.m_items = new int[consequence.m_items.length];
/*  44:122 */     System.arraycopy(premise.m_items, 0, rule.m_items, 0, premise.m_items.length);
/*  45:124 */     for (int k = 0; k < consequence.m_items.length; k++) {
/*  46:125 */       if (consequence.m_items[k] != -1) {
/*  47:126 */         rule.m_items[k] = consequence.m_items[k];
/*  48:    */       }
/*  49:    */     }
/*  50:129 */     for (int i = 0; i < instances.numInstances(); i++) {
/*  51:130 */       rule.upDateCounter(instances.instance(i));
/*  52:    */     }
/*  53:132 */     int ruleSupport = rule.support();
/*  54:133 */     if (ruleSupport > minRuleCount)
/*  55:    */     {
/*  56:134 */       RuleItem newRule = new RuleItem(premise, consequence, genTime, ruleSupport, m_midPoints, m_priors);
/*  57:    */       
/*  58:136 */       return newRule;
/*  59:    */     }
/*  60:138 */     return null;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public int compareTo(RuleItem o)
/*  64:    */   {
/*  65:153 */     if (this.m_accuracy == o.m_accuracy)
/*  66:    */     {
/*  67:154 */       if (this.m_genTime == o.m_genTime) {
/*  68:155 */         return 0;
/*  69:    */       }
/*  70:157 */       if (this.m_genTime > o.m_genTime) {
/*  71:158 */         return -1;
/*  72:    */       }
/*  73:160 */       if (this.m_genTime < o.m_genTime) {
/*  74:161 */         return 1;
/*  75:    */       }
/*  76:    */     }
/*  77:164 */     if (this.m_accuracy < o.m_accuracy) {
/*  78:165 */       return -1;
/*  79:    */     }
/*  80:167 */     return 1;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public boolean equals(Object o)
/*  84:    */   {
/*  85:179 */     if (o == null) {
/*  86:180 */       return false;
/*  87:    */     }
/*  88:182 */     if ((this.m_premise.equals(((RuleItem)o).m_premise)) && (this.m_consequence.equals(((RuleItem)o).m_consequence))) {
/*  89:184 */       return true;
/*  90:    */     }
/*  91:186 */     return false;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public double accuracy()
/*  95:    */   {
/*  96:196 */     return this.m_accuracy;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public ItemSet premise()
/* 100:    */   {
/* 101:206 */     return this.m_premise;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public ItemSet consequence()
/* 105:    */   {
/* 106:216 */     return this.m_consequence;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public String getRevision()
/* 110:    */   {
/* 111:226 */     return RevisionUtils.extract("$Revision: 10201 $");
/* 112:    */   }
/* 113:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.RuleItem
 * JD-Core Version:    0.7.0.1
 */