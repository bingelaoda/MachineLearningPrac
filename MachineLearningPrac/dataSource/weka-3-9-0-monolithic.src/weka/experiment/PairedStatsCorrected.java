/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import weka.core.RevisionUtils;
/*   4:    */ import weka.core.Statistics;
/*   5:    */ import weka.core.Utils;
/*   6:    */ 
/*   7:    */ public class PairedStatsCorrected
/*   8:    */   extends PairedStats
/*   9:    */ {
/*  10:    */   protected double m_testTrainRatio;
/*  11:    */   
/*  12:    */   public PairedStatsCorrected(double sig, double testTrainRatio)
/*  13:    */   {
/*  14: 56 */     super(sig);
/*  15: 57 */     this.m_testTrainRatio = testTrainRatio;
/*  16:    */   }
/*  17:    */   
/*  18:    */   public void calculateDerived()
/*  19:    */   {
/*  20: 65 */     this.xStats.calculateDerived();
/*  21: 66 */     this.yStats.calculateDerived();
/*  22: 67 */     this.differencesStats.calculateDerived();
/*  23:    */     
/*  24: 69 */     this.correlation = (0.0D / 0.0D);
/*  25: 70 */     if ((!Double.isNaN(this.xStats.stdDev)) && (!Double.isNaN(this.yStats.stdDev)) && (!Utils.eq(this.xStats.stdDev, 0.0D)))
/*  26:    */     {
/*  27: 72 */       double slope = (this.xySum - this.xStats.sum * this.yStats.sum / this.count) / (this.xStats.sumSq - this.xStats.sum * this.xStats.mean);
/*  28: 74 */       if (!Utils.eq(this.yStats.stdDev, 0.0D)) {
/*  29: 75 */         this.correlation = (slope * this.xStats.stdDev / this.yStats.stdDev);
/*  30:    */       } else {
/*  31: 77 */         this.correlation = 1.0D;
/*  32:    */       }
/*  33:    */     }
/*  34: 81 */     if (Utils.gr(this.differencesStats.stdDev, 0.0D))
/*  35:    */     {
/*  36: 83 */       double tval = this.differencesStats.mean / Math.sqrt((1.0D / this.count + this.m_testTrainRatio) * this.differencesStats.stdDev * this.differencesStats.stdDev);
/*  37: 87 */       if (this.count > 1.0D) {
/*  38: 88 */         this.differencesProbability = Statistics.FProbability(tval * tval, 1, (int)this.count - 1);
/*  39:    */       } else {
/*  40: 90 */         this.differencesProbability = 1.0D;
/*  41:    */       }
/*  42:    */     }
/*  43: 92 */     else if (this.differencesStats.sumSq == 0.0D)
/*  44:    */     {
/*  45: 93 */       this.differencesProbability = 1.0D;
/*  46:    */     }
/*  47:    */     else
/*  48:    */     {
/*  49: 95 */       this.differencesProbability = 0.0D;
/*  50:    */     }
/*  51: 98 */     this.differencesSignificance = 0;
/*  52: 99 */     if (this.differencesProbability <= this.sigLevel) {
/*  53:100 */       if (this.xStats.mean > this.yStats.mean) {
/*  54:101 */         this.differencesSignificance = 1;
/*  55:    */       } else {
/*  56:103 */         this.differencesSignificance = -1;
/*  57:    */       }
/*  58:    */     }
/*  59:    */   }
/*  60:    */   
/*  61:    */   public String getRevision()
/*  62:    */   {
/*  63:114 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  64:    */   }
/*  65:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.PairedStatsCorrected
 * JD-Core Version:    0.7.0.1
 */