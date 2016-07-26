/*   1:    */ package weka.associations;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Collection;
/*   5:    */ import weka.core.Tag;
/*   6:    */ import weka.core.Utils;
/*   7:    */ 
/*   8:    */ public class DefaultAssociationRule
/*   9:    */   extends AssociationRule
/*  10:    */   implements Serializable
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = -661269018702294489L;
/*  13:    */   
/*  14:    */   public static abstract enum METRIC_TYPE
/*  15:    */   {
/*  16: 43 */     CONFIDENCE("conf"),  LIFT("lift"),  LEVERAGE("lev"),  CONVICTION("conv");
/*  17:    */     
/*  18:    */     private final String m_stringVal;
/*  19:    */     
/*  20:    */     private METRIC_TYPE(String name)
/*  21:    */     {
/*  22: 87 */       this.m_stringVal = name;
/*  23:    */     }
/*  24:    */     
/*  25:    */     abstract double compute(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
/*  26:    */     
/*  27:    */     public String toString()
/*  28:    */     {
/*  29: 94 */       return this.m_stringVal;
/*  30:    */     }
/*  31:    */     
/*  32:    */     public String toStringMetric(int premiseSupport, int consequenceSupport, int totalSupport, int totalTransactions)
/*  33:    */     {
/*  34: 99 */       return this.m_stringVal + ":(" + Utils.doubleToString(compute(premiseSupport, consequenceSupport, totalSupport, totalTransactions), 2) + ")";
/*  35:    */     }
/*  36:    */     
/*  37:    */     public String toXML(int premiseSupport, int consequenceSupport, int totalSupport, int totalTransactions)
/*  38:    */     {
/*  39:105 */       String result = "<CRITERE name=\"" + this.m_stringVal + "\" value=\" " + Utils.doubleToString(compute(premiseSupport, consequenceSupport, totalSupport, totalTransactions), 2) + "\"/>";
/*  40:    */       
/*  41:    */ 
/*  42:    */ 
/*  43:109 */       return result;
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:114 */   public static final Tag[] TAGS_SELECTION = { new Tag(METRIC_TYPE.CONFIDENCE.ordinal(), "Confidence"), new Tag(METRIC_TYPE.LIFT.ordinal(), "Lift"), new Tag(METRIC_TYPE.LEVERAGE.ordinal(), "Leverage"), new Tag(METRIC_TYPE.CONVICTION.ordinal(), "Conviction") };
/*  48:122 */   protected METRIC_TYPE m_metricType = METRIC_TYPE.CONFIDENCE;
/*  49:    */   protected Collection<Item> m_premise;
/*  50:    */   protected Collection<Item> m_consequence;
/*  51:    */   protected int m_premiseSupport;
/*  52:    */   protected int m_consequenceSupport;
/*  53:    */   protected int m_totalSupport;
/*  54:    */   protected int m_totalTransactions;
/*  55:    */   
/*  56:    */   public DefaultAssociationRule(Collection<Item> premise, Collection<Item> consequence, METRIC_TYPE metric, int premiseSupport, int consequenceSupport, int totalSupport, int totalTransactions)
/*  57:    */   {
/*  58:157 */     this.m_premise = premise;
/*  59:158 */     this.m_consequence = consequence;
/*  60:159 */     this.m_metricType = metric;
/*  61:160 */     this.m_premiseSupport = premiseSupport;
/*  62:161 */     this.m_consequenceSupport = consequenceSupport;
/*  63:162 */     this.m_totalSupport = totalSupport;
/*  64:163 */     this.m_totalTransactions = totalTransactions;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public Collection<Item> getPremise()
/*  68:    */   {
/*  69:170 */     return this.m_premise;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public Collection<Item> getConsequence()
/*  73:    */   {
/*  74:177 */     return this.m_consequence;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public String getPrimaryMetricName()
/*  78:    */   {
/*  79:184 */     return TAGS_SELECTION[this.m_metricType.ordinal()].getReadable();
/*  80:    */   }
/*  81:    */   
/*  82:    */   public double getPrimaryMetricValue()
/*  83:    */   {
/*  84:191 */     return this.m_metricType.compute(this.m_premiseSupport, this.m_consequenceSupport, this.m_totalSupport, this.m_totalTransactions);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public double getNamedMetricValue(String metricName)
/*  88:    */     throws Exception
/*  89:    */   {
/*  90:200 */     METRIC_TYPE requested = null;
/*  91:202 */     for (METRIC_TYPE m : METRIC_TYPE.values()) {
/*  92:203 */       if (TAGS_SELECTION[m.ordinal()].getReadable().equals(metricName)) {
/*  93:204 */         requested = m;
/*  94:    */       }
/*  95:    */     }
/*  96:208 */     if (requested == null) {
/*  97:209 */       throw new Exception("[AssociationRule] Unknown metric: " + metricName);
/*  98:    */     }
/*  99:212 */     return requested.compute(this.m_premiseSupport, this.m_consequenceSupport, this.m_totalSupport, this.m_totalTransactions);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public int getNumberOfMetricsForRule()
/* 103:    */   {
/* 104:220 */     return METRIC_TYPE.values().length;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public String[] getMetricNamesForRule()
/* 108:    */   {
/* 109:227 */     String[] metricNames = new String[TAGS_SELECTION.length];
/* 110:229 */     for (int i = 0; i < TAGS_SELECTION.length; i++) {
/* 111:230 */       metricNames[i] = TAGS_SELECTION[i].getReadable();
/* 112:    */     }
/* 113:233 */     return metricNames;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public double[] getMetricValuesForRule()
/* 117:    */     throws Exception
/* 118:    */   {
/* 119:240 */     double[] values = new double[TAGS_SELECTION.length];
/* 120:242 */     for (int i = 0; i < TAGS_SELECTION.length; i++) {
/* 121:243 */       values[i] = getNamedMetricValue(TAGS_SELECTION[i].getReadable());
/* 122:    */     }
/* 123:246 */     return values;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public int getPremiseSupport()
/* 127:    */   {
/* 128:253 */     return this.m_premiseSupport;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public int getConsequenceSupport()
/* 132:    */   {
/* 133:260 */     return this.m_consequenceSupport;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public int getTotalSupport()
/* 137:    */   {
/* 138:267 */     return this.m_totalSupport;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public int getTotalTransactions()
/* 142:    */   {
/* 143:274 */     return this.m_totalTransactions;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public String toString()
/* 147:    */   {
/* 148:283 */     StringBuffer result = new StringBuffer();
/* 149:    */     
/* 150:285 */     result.append(this.m_premise.toString() + ": " + this.m_premiseSupport + " ==> " + this.m_consequence.toString() + ": " + this.m_totalSupport + "   ");
/* 151:288 */     for (METRIC_TYPE m : METRIC_TYPE.values()) {
/* 152:289 */       if (m.equals(this.m_metricType)) {
/* 153:290 */         result.append("<" + m.toStringMetric(this.m_premiseSupport, this.m_consequenceSupport, this.m_totalSupport, this.m_totalTransactions) + "> ");
/* 154:    */       } else {
/* 155:294 */         result.append("" + m.toStringMetric(this.m_premiseSupport, this.m_consequenceSupport, this.m_totalSupport, this.m_totalTransactions) + " ");
/* 156:    */       }
/* 157:    */     }
/* 158:299 */     return result.toString();
/* 159:    */   }
/* 160:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.DefaultAssociationRule
 * JD-Core Version:    0.7.0.1
 */