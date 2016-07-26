/*   1:    */ package weka.classifiers.trees.adtree;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import weka.classifiers.trees.ADTree;
/*   7:    */ import weka.core.RevisionHandler;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ import weka.core.WekaEnumeration;
/*  10:    */ 
/*  11:    */ public final class PredictionNode
/*  12:    */   implements Serializable, Cloneable, RevisionHandler
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = 6018958856358698814L;
/*  15:    */   private double value;
/*  16:    */   private final ArrayList<Splitter> children;
/*  17:    */   
/*  18:    */   public PredictionNode(double newValue)
/*  19:    */   {
/*  20: 58 */     this.value = newValue;
/*  21: 59 */     this.children = new ArrayList();
/*  22:    */   }
/*  23:    */   
/*  24:    */   public final void setValue(double newValue)
/*  25:    */   {
/*  26: 69 */     this.value = newValue;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public final double getValue()
/*  30:    */   {
/*  31: 79 */     return this.value;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public final ArrayList<Splitter> getChildren()
/*  35:    */   {
/*  36: 89 */     return this.children;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public final Enumeration<Splitter> children()
/*  40:    */   {
/*  41: 99 */     return new WekaEnumeration(this.children);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public final void addChild(Splitter newChild, ADTree addingTo)
/*  45:    */   {
/*  46:112 */     Splitter oldEqual = null;
/*  47:113 */     for (Enumeration<Splitter> e = children(); e.hasMoreElements();)
/*  48:    */     {
/*  49:114 */       Splitter split = (Splitter)e.nextElement();
/*  50:115 */       if (newChild.equalTo(split))
/*  51:    */       {
/*  52:116 */         oldEqual = split;
/*  53:117 */         break;
/*  54:    */       }
/*  55:    */     }
/*  56:120 */     if (oldEqual == null)
/*  57:    */     {
/*  58:121 */       Splitter addChild = (Splitter)newChild.clone();
/*  59:122 */       setOrderAddedSubtree(addChild, addingTo);
/*  60:123 */       this.children.add(addChild);
/*  61:    */     }
/*  62:    */     else
/*  63:    */     {
/*  64:125 */       for (int i = 0; i < newChild.getNumOfBranches(); i++)
/*  65:    */       {
/*  66:126 */         PredictionNode oldPred = oldEqual.getChildForBranch(i);
/*  67:127 */         PredictionNode newPred = newChild.getChildForBranch(i);
/*  68:128 */         if ((oldPred != null) && (newPred != null)) {
/*  69:129 */           oldPred.merge(newPred, addingTo);
/*  70:    */         }
/*  71:    */       }
/*  72:    */     }
/*  73:    */   }
/*  74:    */   
/*  75:    */   public final Object clone()
/*  76:    */   {
/*  77:143 */     PredictionNode clone = new PredictionNode(this.value);
/*  78:144 */     Enumeration<Splitter> e = new WekaEnumeration(this.children);
/*  79:145 */     while (e.hasMoreElements()) {
/*  80:146 */       clone.children.add((Splitter)((Splitter)e.nextElement()).clone());
/*  81:    */     }
/*  82:148 */     return clone;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public final void merge(PredictionNode merger, ADTree mergingTo)
/*  86:    */   {
/*  87:160 */     this.value += merger.value;
/*  88:161 */     for (Enumeration<Splitter> e = merger.children(); e.hasMoreElements();) {
/*  89:162 */       addChild((Splitter)e.nextElement(), mergingTo);
/*  90:    */     }
/*  91:    */   }
/*  92:    */   
/*  93:    */   private final void setOrderAddedSubtree(Splitter addChild, ADTree addingTo)
/*  94:    */   {
/*  95:174 */     addChild.orderAdded = addingTo.nextSplitAddedOrder();
/*  96:    */     Enumeration<Splitter> e;
/*  97:175 */     for (int i = 0; i < addChild.getNumOfBranches(); i++)
/*  98:    */     {
/*  99:176 */       PredictionNode node = addChild.getChildForBranch(i);
/* 100:177 */       if (node != null) {
/* 101:178 */         for (e = node.children(); e.hasMoreElements();) {
/* 102:179 */           setOrderAddedSubtree((Splitter)e.nextElement(), addingTo);
/* 103:    */         }
/* 104:    */       }
/* 105:    */     }
/* 106:    */   }
/* 107:    */   
/* 108:    */   public String getRevision()
/* 109:    */   {
/* 110:192 */     return RevisionUtils.extract("$Revision: 10324 $");
/* 111:    */   }
/* 112:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.adtree.PredictionNode
 * JD-Core Version:    0.7.0.1
 */