/*   1:    */ package weka.classifiers.mi.miti;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.List;
/*   6:    */ import weka.core.Instance;
/*   7:    */ import weka.core.Instances;
/*   8:    */ 
/*   9:    */ public class Bag
/*  10:    */   implements Serializable
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = 3890971117182125677L;
/*  13:    */   private final boolean positive;
/*  14:    */   private final Instances instances;
/*  15:    */   private final String id;
/*  16:    */   private final double bagWeight;
/*  17:    */   private double bagWeightMultiplied;
/*  18: 58 */   private boolean enabled = true;
/*  19:    */   
/*  20:    */   public Bag(Instance bagInstance)
/*  21:    */   {
/*  22: 64 */     this.instances = bagInstance.relationalValue(1);
/*  23: 65 */     this.positive = (bagInstance.classValue() == 1.0D);
/*  24: 66 */     this.bagWeight = (1.0D / this.instances.numInstances());
/*  25: 67 */     this.bagWeightMultiplied = this.bagWeight;
/*  26: 68 */     this.id = bagInstance.stringValue(0);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public Instances instances()
/*  30:    */   {
/*  31: 75 */     return this.instances;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public boolean isPositive()
/*  35:    */   {
/*  36: 82 */     return this.positive;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public double bagWeight()
/*  40:    */   {
/*  41: 89 */     return this.bagWeightMultiplied;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void setBagWeightMultiplier(double multiplier)
/*  45:    */   {
/*  46: 96 */     this.bagWeightMultiplied = (multiplier * this.bagWeight);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public boolean isEnabled()
/*  50:    */   {
/*  51:103 */     return this.enabled;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void disableInstances(List<String> deactivated)
/*  55:    */   {
/*  56:110 */     if (this.enabled) {
/*  57:111 */       deactivated.add(this.id);
/*  58:    */     }
/*  59:113 */     this.enabled = false;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public static void printDeactivatedInstances(List<String> deactivated)
/*  63:    */   {
/*  64:120 */     System.out.print("DEACTIVATING examples [");
/*  65:121 */     System.out.print((String)deactivated.get(0));
/*  66:122 */     for (int i = 1; i < deactivated.size(); i++) {
/*  67:123 */       System.out.print(", " + (String)deactivated.get(i));
/*  68:    */     }
/*  69:125 */     System.out.println("]");
/*  70:    */   }
/*  71:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.miti.Bag
 * JD-Core Version:    0.7.0.1
 */