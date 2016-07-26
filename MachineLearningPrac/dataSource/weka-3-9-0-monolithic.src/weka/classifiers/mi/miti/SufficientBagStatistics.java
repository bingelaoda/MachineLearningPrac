/*   1:    */ package weka.classifiers.mi.miti;
/*   2:    */ 
/*   3:    */ import java.util.HashMap;
/*   4:    */ import java.util.List;
/*   5:    */ import java.util.Map.Entry;
/*   6:    */ import weka.core.Instance;
/*   7:    */ 
/*   8:    */ public class SufficientBagStatistics
/*   9:    */   implements SufficientStatistics
/*  10:    */ {
/*  11:    */   private HashMap<Bag, Integer> leftPositiveBags;
/*  12:    */   private HashMap<Bag, Integer> rightPositiveBags;
/*  13:    */   private HashMap<Bag, Integer> leftTotalBags;
/*  14:    */   private HashMap<Bag, Integer> rightTotalBags;
/*  15:    */   private double m_instanceConstant;
/*  16:    */   private double positiveCountLeft;
/*  17:    */   private double totalCountLeft;
/*  18:    */   private double positiveCountRight;
/*  19:    */   private double totalCountRight;
/*  20:    */   
/*  21:    */   public SufficientBagStatistics(List<Instance> allInstances, HashMap<Instance, Bag> instanceBags, double instanceConstant)
/*  22:    */   {
/*  23: 59 */     this.m_instanceConstant = instanceConstant;
/*  24:    */     
/*  25: 61 */     this.leftPositiveBags = new HashMap();
/*  26: 62 */     this.rightPositiveBags = new HashMap();
/*  27: 63 */     this.leftTotalBags = new HashMap();
/*  28: 64 */     this.rightTotalBags = new HashMap();
/*  29: 66 */     for (Instance i : allInstances)
/*  30:    */     {
/*  31: 68 */       Bag bag = (Bag)instanceBags.get(i);
/*  32: 69 */       if (bag.isPositive()) {
/*  33: 71 */         if (this.rightPositiveBags.containsKey(bag)) {
/*  34: 72 */           this.rightPositiveBags.put(bag, Integer.valueOf(((Integer)this.rightPositiveBags.get(bag)).intValue() + 1));
/*  35:    */         } else {
/*  36: 74 */           this.rightPositiveBags.put(bag, Integer.valueOf(1));
/*  37:    */         }
/*  38:    */       }
/*  39: 77 */       if (this.rightTotalBags.containsKey(bag)) {
/*  40: 78 */         this.rightTotalBags.put(bag, Integer.valueOf(((Integer)this.rightTotalBags.get(bag)).intValue() + 1));
/*  41:    */       } else {
/*  42: 80 */         this.rightTotalBags.put(bag, Integer.valueOf(1));
/*  43:    */       }
/*  44:    */     }
/*  45: 83 */     this.totalCountRight = 0.0D;
/*  46: 84 */     this.positiveCountRight = 0.0D;
/*  47: 85 */     for (Map.Entry<Bag, Integer> e : this.rightTotalBags.entrySet())
/*  48:    */     {
/*  49: 91 */       double weight = 1.0D - Math.pow(instanceConstant, ((Integer)e.getValue()).intValue());
/*  50: 92 */       if (((Bag)e.getKey()).isPositive()) {
/*  51: 93 */         this.positiveCountRight += weight;
/*  52:    */       }
/*  53: 95 */       this.totalCountRight += weight;
/*  54:    */     }
/*  55: 97 */     this.totalCountLeft = 0.0D;
/*  56: 98 */     this.positiveCountLeft = 0.0D;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void updateStats(Instance i, HashMap<Instance, Bag> instanceBags)
/*  60:    */   {
/*  61:107 */     Bag bag = (Bag)instanceBags.get(i);
/*  62:108 */     boolean positive = bag.isPositive();
/*  63:109 */     double prob = this.m_instanceConstant;
/*  64:111 */     if (positive)
/*  65:    */     {
/*  66:113 */       int countRP = ((Integer)this.rightPositiveBags.get(bag)).intValue();
/*  67:114 */       this.positiveCountRight += Math.pow(prob, countRP - 1) * (prob - 1.0D);
/*  68:    */       
/*  69:    */ 
/*  70:117 */       this.rightPositiveBags.put(bag, Integer.valueOf(countRP - 1));
/*  71:119 */       if (this.leftPositiveBags.containsKey(bag))
/*  72:    */       {
/*  73:120 */         int countLP = ((Integer)this.leftPositiveBags.get(bag)).intValue();
/*  74:121 */         this.positiveCountLeft += Math.pow(prob, countLP) * (1.0D - prob);
/*  75:    */         
/*  76:    */ 
/*  77:124 */         this.leftPositiveBags.put(bag, Integer.valueOf(countLP + 1));
/*  78:    */       }
/*  79:    */       else
/*  80:    */       {
/*  81:126 */         this.leftPositiveBags.put(bag, Integer.valueOf(1));
/*  82:127 */         this.positiveCountLeft += 1.0D - prob;
/*  83:    */       }
/*  84:    */     }
/*  85:131 */     int countRT = ((Integer)this.rightTotalBags.get(bag)).intValue();
/*  86:132 */     this.totalCountRight += Math.pow(prob, countRT - 1) * (prob - 1.0D);
/*  87:    */     
/*  88:    */ 
/*  89:    */ 
/*  90:136 */     this.rightTotalBags.put(bag, Integer.valueOf(countRT - 1));
/*  91:138 */     if (this.leftTotalBags.containsKey(bag))
/*  92:    */     {
/*  93:139 */       int countLT = ((Integer)this.leftTotalBags.get(bag)).intValue();
/*  94:140 */       this.totalCountLeft += Math.pow(prob, countLT) * (1.0D - prob);
/*  95:    */       
/*  96:    */ 
/*  97:143 */       this.leftTotalBags.put(bag, Integer.valueOf(countLT + 1));
/*  98:    */     }
/*  99:    */     else
/* 100:    */     {
/* 101:145 */       this.leftTotalBags.put(bag, Integer.valueOf(1));
/* 102:146 */       this.totalCountLeft += 1.0D - prob;
/* 103:    */     }
/* 104:    */   }
/* 105:    */   
/* 106:    */   public double positiveCountLeft()
/* 107:    */   {
/* 108:155 */     return this.positiveCountLeft;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public double positiveCountRight()
/* 112:    */   {
/* 113:164 */     return this.positiveCountRight;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public double totalCountLeft()
/* 117:    */   {
/* 118:173 */     return this.totalCountLeft;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public double totalCountRight()
/* 122:    */   {
/* 123:181 */     return this.totalCountRight;
/* 124:    */   }
/* 125:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.miti.SufficientBagStatistics
 * JD-Core Version:    0.7.0.1
 */