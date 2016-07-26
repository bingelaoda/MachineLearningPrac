/*   1:    */ package weka.classifiers.mi.miti;
/*   2:    */ 
/*   3:    */ import java.util.HashMap;
/*   4:    */ import java.util.List;
/*   5:    */ import weka.core.Instance;
/*   6:    */ 
/*   7:    */ public class SufficientInstanceStatistics
/*   8:    */   implements SufficientStatistics
/*   9:    */ {
/*  10:    */   private double positiveInstancesLeft;
/*  11:    */   private double totalInstancesLeft;
/*  12:    */   private double positiveInstancesRight;
/*  13:    */   private double totalInstancesRight;
/*  14:    */   
/*  15:    */   public SufficientInstanceStatistics(List<Instance> allInstances, HashMap<Instance, Bag> instanceBags)
/*  16:    */   {
/*  17: 47 */     this.positiveInstancesLeft = 0.0D;
/*  18: 48 */     this.positiveInstancesRight = 0.0D;
/*  19: 49 */     this.totalInstancesLeft = 0.0D;
/*  20: 50 */     this.totalInstancesRight = 0.0D;
/*  21: 51 */     for (Instance i : allInstances)
/*  22:    */     {
/*  23: 53 */       Bag bag = (Bag)instanceBags.get(i);
/*  24: 54 */       if (bag.isPositive()) {
/*  25: 55 */         this.positiveInstancesRight += bag.bagWeight();
/*  26:    */       }
/*  27: 56 */       this.totalInstancesRight += bag.bagWeight();
/*  28:    */     }
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void updateStats(Instance i, HashMap<Instance, Bag> instanceBags)
/*  32:    */   {
/*  33: 66 */     Bag bag = (Bag)instanceBags.get(i);
/*  34: 67 */     boolean positive = bag.isPositive();
/*  35: 68 */     if (positive)
/*  36:    */     {
/*  37: 70 */       this.positiveInstancesRight -= bag.bagWeight();
/*  38: 71 */       this.positiveInstancesLeft += bag.bagWeight();
/*  39:    */     }
/*  40: 74 */     this.totalInstancesLeft += bag.bagWeight();
/*  41: 75 */     this.totalInstancesRight -= bag.bagWeight();
/*  42:    */   }
/*  43:    */   
/*  44:    */   public double positiveCountLeft()
/*  45:    */   {
/*  46: 83 */     return this.positiveInstancesLeft;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public double positiveCountRight()
/*  50:    */   {
/*  51: 91 */     return this.positiveInstancesRight;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public double totalCountLeft()
/*  55:    */   {
/*  56: 99 */     return this.totalInstancesLeft;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public double totalCountRight()
/*  60:    */   {
/*  61:107 */     return this.totalInstancesRight;
/*  62:    */   }
/*  63:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.miti.SufficientInstanceStatistics
 * JD-Core Version:    0.7.0.1
 */