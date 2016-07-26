/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.experiment.Stats;
/*   5:    */ 
/*   6:    */ public class AttributeStats
/*   7:    */   implements Serializable, RevisionHandler
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = 4434688832743939380L;
/*  10: 40 */   public int intCount = 0;
/*  11: 43 */   public int realCount = 0;
/*  12: 46 */   public int missingCount = 0;
/*  13: 49 */   public int distinctCount = 0;
/*  14: 52 */   public int uniqueCount = 0;
/*  15: 55 */   public int totalCount = 0;
/*  16:    */   public Stats numericStats;
/*  17:    */   public int[] nominalCounts;
/*  18:    */   public double[] nominalWeights;
/*  19:    */   
/*  20:    */   protected void addDistinct(double value, int count, double weight)
/*  21:    */   {
/*  22: 76 */     if (count > 0)
/*  23:    */     {
/*  24: 77 */       if (count == 1) {
/*  25: 78 */         this.uniqueCount += 1;
/*  26:    */       }
/*  27: 80 */       if (Utils.eq(value, (int)value)) {
/*  28: 81 */         this.intCount += count;
/*  29:    */       } else {
/*  30: 83 */         this.realCount += count;
/*  31:    */       }
/*  32: 85 */       if (this.nominalCounts != null)
/*  33:    */       {
/*  34: 86 */         this.nominalCounts[((int)value)] = count;
/*  35: 87 */         this.nominalWeights[((int)value)] = weight;
/*  36:    */       }
/*  37: 89 */       if (this.numericStats != null)
/*  38:    */       {
/*  39: 91 */         this.numericStats.add(value, weight);
/*  40: 92 */         this.numericStats.calculateDerived();
/*  41:    */       }
/*  42:    */     }
/*  43: 95 */     this.distinctCount += 1;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public String toString()
/*  47:    */   {
/*  48:105 */     StringBuffer sb = new StringBuffer();
/*  49:106 */     sb.append(Utils.padLeft("Type", 4)).append(Utils.padLeft("Nom", 5));
/*  50:107 */     sb.append(Utils.padLeft("Int", 5)).append(Utils.padLeft("Real", 5));
/*  51:108 */     sb.append(Utils.padLeft("Missing", 12));
/*  52:109 */     sb.append(Utils.padLeft("Unique", 12));
/*  53:110 */     sb.append(Utils.padLeft("Dist", 6));
/*  54:111 */     if (this.nominalCounts != null)
/*  55:    */     {
/*  56:112 */       sb.append(' ');
/*  57:113 */       for (int i = 0; i < this.nominalCounts.length; i++) {
/*  58:114 */         sb.append(Utils.padLeft("C[" + i + "]", 5));
/*  59:    */       }
/*  60:    */     }
/*  61:117 */     sb.append('\n');
/*  62:    */     
/*  63:    */ 
/*  64:120 */     long percent = Math.round(100.0D * this.intCount / this.totalCount);
/*  65:121 */     if (this.nominalCounts != null)
/*  66:    */     {
/*  67:122 */       sb.append(Utils.padLeft("Nom", 4)).append(' ');
/*  68:123 */       sb.append(Utils.padLeft("" + percent, 3)).append("% ");
/*  69:124 */       sb.append(Utils.padLeft("0", 3)).append("% ");
/*  70:    */     }
/*  71:    */     else
/*  72:    */     {
/*  73:126 */       sb.append(Utils.padLeft("Num", 4)).append(' ');
/*  74:127 */       sb.append(Utils.padLeft("0", 3)).append("% ");
/*  75:128 */       sb.append(Utils.padLeft("" + percent, 3)).append("% ");
/*  76:    */     }
/*  77:130 */     percent = Math.round(100.0D * this.realCount / this.totalCount);
/*  78:131 */     sb.append(Utils.padLeft("" + percent, 3)).append("% ");
/*  79:132 */     sb.append(Utils.padLeft("" + this.missingCount, 5)).append(" /");
/*  80:133 */     percent = Math.round(100.0D * this.missingCount / this.totalCount);
/*  81:134 */     sb.append(Utils.padLeft("" + percent, 3)).append("% ");
/*  82:135 */     sb.append(Utils.padLeft("" + this.uniqueCount, 5)).append(" /");
/*  83:136 */     percent = Math.round(100.0D * this.uniqueCount / this.totalCount);
/*  84:137 */     sb.append(Utils.padLeft("" + percent, 3)).append("% ");
/*  85:138 */     sb.append(Utils.padLeft("" + this.distinctCount, 5)).append(' ');
/*  86:139 */     if (this.nominalCounts != null) {
/*  87:140 */       for (int i = 0; i < this.nominalCounts.length; i++) {
/*  88:141 */         sb.append(Utils.padLeft("" + this.nominalCounts[i], 5));
/*  89:    */       }
/*  90:    */     }
/*  91:144 */     sb.append('\n');
/*  92:145 */     return sb.toString();
/*  93:    */   }
/*  94:    */   
/*  95:    */   public String getRevision()
/*  96:    */   {
/*  97:154 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  98:    */   }
/*  99:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.AttributeStats
 * JD-Core Version:    0.7.0.1
 */