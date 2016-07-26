/*   1:    */ package weka.datagenerators;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Instance;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.RevisionHandler;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ import weka.core.Utils;
/*  10:    */ 
/*  11:    */ public class Test
/*  12:    */   implements Serializable, RevisionHandler
/*  13:    */ {
/*  14:    */   static final long serialVersionUID = -8890645875887157782L;
/*  15:    */   int m_AttIndex;
/*  16:    */   double m_Split;
/*  17:    */   boolean m_Not;
/*  18:    */   Instances m_Dataset;
/*  19:    */   
/*  20:    */   public Test(int i, double s, Instances dataset)
/*  21:    */   {
/*  22: 99 */     this.m_AttIndex = i;
/*  23:100 */     this.m_Split = s;
/*  24:101 */     this.m_Dataset = dataset;
/*  25:    */     
/*  26:103 */     this.m_Not = false;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public Test(int i, double s, Instances dataset, boolean n)
/*  30:    */   {
/*  31:115 */     this.m_AttIndex = i;
/*  32:116 */     this.m_Split = s;
/*  33:117 */     this.m_Dataset = dataset;
/*  34:118 */     this.m_Not = n;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public Test getNot()
/*  38:    */   {
/*  39:127 */     return new Test(this.m_AttIndex, this.m_Split, this.m_Dataset, !this.m_Not);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public boolean passesTest(Instance inst)
/*  43:    */     throws Exception
/*  44:    */   {
/*  45:138 */     if (inst.isMissing(this.m_AttIndex)) {
/*  46:139 */       return false;
/*  47:    */     }
/*  48:142 */     boolean isNominal = inst.attribute(this.m_AttIndex).isNominal();
/*  49:143 */     double attribVal = inst.value(this.m_AttIndex);
/*  50:144 */     if (!this.m_Not)
/*  51:    */     {
/*  52:145 */       if (isNominal)
/*  53:    */       {
/*  54:146 */         if ((int)attribVal != (int)this.m_Split) {
/*  55:147 */           return false;
/*  56:    */         }
/*  57:    */       }
/*  58:149 */       else if (attribVal >= this.m_Split) {
/*  59:150 */         return false;
/*  60:    */       }
/*  61:    */     }
/*  62:153 */     else if (isNominal)
/*  63:    */     {
/*  64:154 */       if ((int)attribVal == (int)this.m_Split) {
/*  65:155 */         return false;
/*  66:    */       }
/*  67:    */     }
/*  68:157 */     else if (attribVal < this.m_Split) {
/*  69:158 */       return false;
/*  70:    */     }
/*  71:161 */     return true;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public String toString()
/*  75:    */   {
/*  76:171 */     return this.m_Dataset.attribute(this.m_AttIndex).name() + " " + testComparisonString();
/*  77:    */   }
/*  78:    */   
/*  79:    */   public String toPrologString()
/*  80:    */   {
/*  81:180 */     Attribute att = this.m_Dataset.attribute(this.m_AttIndex);
/*  82:181 */     StringBuffer str = new StringBuffer();
/*  83:182 */     String attName = this.m_Dataset.attribute(this.m_AttIndex).name();
/*  84:183 */     if (att.isNumeric())
/*  85:    */     {
/*  86:184 */       str = str.append(attName + " ");
/*  87:185 */       if (this.m_Not) {
/*  88:186 */         str = str.append(">= " + Utils.doubleToString(this.m_Split, 3));
/*  89:    */       } else {
/*  90:188 */         str = str.append("< " + Utils.doubleToString(this.m_Split, 3));
/*  91:    */       }
/*  92:    */     }
/*  93:    */     else
/*  94:    */     {
/*  95:191 */       String value = att.value((int)this.m_Split);
/*  96:193 */       if (value == "false") {
/*  97:194 */         str = str.append("not(" + attName + ")");
/*  98:    */       } else {
/*  99:196 */         str = str.append(attName);
/* 100:    */       }
/* 101:    */     }
/* 102:199 */     return str.toString();
/* 103:    */   }
/* 104:    */   
/* 105:    */   private String testComparisonString()
/* 106:    */   {
/* 107:209 */     Attribute att = this.m_Dataset.attribute(this.m_AttIndex);
/* 108:210 */     if (att.isNumeric()) {
/* 109:211 */       return (this.m_Not ? ">= " : "< ") + Utils.doubleToString(this.m_Split, 3);
/* 110:    */     }
/* 111:213 */     if (att.numValues() != 2) {
/* 112:214 */       return (this.m_Not ? "!= " : "= ") + att.value((int)this.m_Split);
/* 113:    */     }
/* 114:216 */     return "= " + (this.m_Not ? att.value((int)this.m_Split == 0 ? 1 : 0) : att.value((int)this.m_Split));
/* 115:    */   }
/* 116:    */   
/* 117:    */   public boolean equalTo(Test t)
/* 118:    */   {
/* 119:229 */     return (this.m_AttIndex == t.m_AttIndex) && (this.m_Split == t.m_Split) && (this.m_Not == t.m_Not);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public String getRevision()
/* 123:    */   {
/* 124:239 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 125:    */   }
/* 126:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.datagenerators.Test
 * JD-Core Version:    0.7.0.1
 */