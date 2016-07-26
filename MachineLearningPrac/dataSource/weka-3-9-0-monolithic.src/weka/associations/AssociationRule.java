/*   1:    */ package weka.associations;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collection;
/*   5:    */ 
/*   6:    */ public abstract class AssociationRule
/*   7:    */   implements Comparable<AssociationRule>
/*   8:    */ {
/*   9:    */   public abstract Collection<Item> getPremise();
/*  10:    */   
/*  11:    */   public abstract Collection<Item> getConsequence();
/*  12:    */   
/*  13:    */   public abstract String getPrimaryMetricName();
/*  14:    */   
/*  15:    */   public abstract double getPrimaryMetricValue();
/*  16:    */   
/*  17:    */   public abstract double getNamedMetricValue(String paramString)
/*  18:    */     throws Exception;
/*  19:    */   
/*  20:    */   public abstract int getNumberOfMetricsForRule();
/*  21:    */   
/*  22:    */   public abstract String[] getMetricNamesForRule();
/*  23:    */   
/*  24:    */   public abstract double[] getMetricValuesForRule()
/*  25:    */     throws Exception;
/*  26:    */   
/*  27:    */   public abstract int getPremiseSupport();
/*  28:    */   
/*  29:    */   public abstract int getConsequenceSupport();
/*  30:    */   
/*  31:    */   public abstract int getTotalSupport();
/*  32:    */   
/*  33:    */   public abstract int getTotalTransactions();
/*  34:    */   
/*  35:    */   public int compareTo(AssociationRule other)
/*  36:    */   {
/*  37:132 */     return -Double.compare(getPrimaryMetricValue(), other.getPrimaryMetricValue());
/*  38:    */   }
/*  39:    */   
/*  40:    */   public boolean equals(Object other)
/*  41:    */   {
/*  42:141 */     if (!(other instanceof AssociationRule)) {
/*  43:142 */       return false;
/*  44:    */     }
/*  45:145 */     AssociationRule otherRule = (AssociationRule)other;
/*  46:146 */     boolean result = (getPremise().equals(otherRule.getPremise())) && (getConsequence().equals(otherRule.getConsequence())) && (getPrimaryMetricValue() == otherRule.getPrimaryMetricValue());
/*  47:    */     
/*  48:    */ 
/*  49:    */ 
/*  50:150 */     return result;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public boolean containsItems(ArrayList<Item> items, boolean useOr)
/*  54:    */   {
/*  55:154 */     int numItems = items.size();
/*  56:155 */     int count = 0;
/*  57:157 */     for (Item i : getPremise()) {
/*  58:158 */       if (items.contains(i))
/*  59:    */       {
/*  60:159 */         if (useOr) {
/*  61:160 */           return true;
/*  62:    */         }
/*  63:162 */         count++;
/*  64:    */       }
/*  65:    */     }
/*  66:167 */     for (Item i : getConsequence()) {
/*  67:168 */       if (items.contains(i))
/*  68:    */       {
/*  69:169 */         if (useOr) {
/*  70:170 */           return true;
/*  71:    */         }
/*  72:172 */         count++;
/*  73:    */       }
/*  74:    */     }
/*  75:177 */     if ((!useOr) && 
/*  76:178 */       (count == numItems)) {
/*  77:179 */       return true;
/*  78:    */     }
/*  79:183 */     return false;
/*  80:    */   }
/*  81:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.AssociationRule
 * JD-Core Version:    0.7.0.1
 */