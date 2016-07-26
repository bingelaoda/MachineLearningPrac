/*   1:    */ package weka.classifiers.bayes.net;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Instances;
/*   6:    */ import weka.core.RevisionHandler;
/*   7:    */ import weka.core.RevisionUtils;
/*   8:    */ 
/*   9:    */ public class ParentSet
/*  10:    */   implements Serializable, RevisionHandler
/*  11:    */ {
/*  12:    */   static final long serialVersionUID = 4155021284407181838L;
/*  13:    */   private int[] m_nParents;
/*  14:    */   
/*  15:    */   public int getParent(int iParent)
/*  16:    */   {
/*  17: 54 */     return this.m_nParents[iParent];
/*  18:    */   }
/*  19:    */   
/*  20:    */   public int[] getParents()
/*  21:    */   {
/*  22: 56 */     return this.m_nParents;
/*  23:    */   }
/*  24:    */   
/*  25:    */   public void SetParent(int iParent, int nNode)
/*  26:    */   {
/*  27: 65 */     this.m_nParents[iParent] = nNode;
/*  28:    */   }
/*  29:    */   
/*  30: 72 */   private int m_nNrOfParents = 0;
/*  31:    */   
/*  32:    */   public int getNrOfParents()
/*  33:    */   {
/*  34: 79 */     return this.m_nNrOfParents;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public boolean contains(int iNode)
/*  38:    */   {
/*  39: 88 */     for (int iParent = 0; iParent < this.m_nNrOfParents; iParent++) {
/*  40: 89 */       if (this.m_nParents[iParent] == iNode) {
/*  41: 90 */         return true;
/*  42:    */       }
/*  43:    */     }
/*  44: 93 */     return false;
/*  45:    */   }
/*  46:    */   
/*  47: 98 */   private int m_nCardinalityOfParents = 1;
/*  48:    */   
/*  49:    */   public int getCardinalityOfParents()
/*  50:    */   {
/*  51:106 */     return this.m_nCardinalityOfParents;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public int getFreshCardinalityOfParents(Instances _Instances)
/*  55:    */   {
/*  56:115 */     this.m_nCardinalityOfParents = 1;
/*  57:116 */     for (int iParent = 0; iParent < this.m_nNrOfParents; iParent++) {
/*  58:117 */       this.m_nCardinalityOfParents *= _Instances.attribute(this.m_nParents[iParent]).numValues();
/*  59:    */     }
/*  60:119 */     return this.m_nCardinalityOfParents;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public ParentSet()
/*  64:    */   {
/*  65:125 */     this.m_nParents = new int[10];
/*  66:126 */     this.m_nNrOfParents = 0;
/*  67:127 */     this.m_nCardinalityOfParents = 1;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public ParentSet(int nMaxNrOfParents)
/*  71:    */   {
/*  72:135 */     this.m_nParents = new int[nMaxNrOfParents];
/*  73:136 */     this.m_nNrOfParents = 0;
/*  74:137 */     this.m_nCardinalityOfParents = 1;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public ParentSet(ParentSet other)
/*  78:    */   {
/*  79:145 */     this.m_nNrOfParents = other.m_nNrOfParents;
/*  80:146 */     this.m_nCardinalityOfParents = other.m_nCardinalityOfParents;
/*  81:147 */     this.m_nParents = new int[this.m_nNrOfParents];
/*  82:149 */     for (int iParent = 0; iParent < this.m_nNrOfParents; iParent++) {
/*  83:150 */       this.m_nParents[iParent] = other.m_nParents[iParent];
/*  84:    */     }
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void maxParentSetSize(int nSize)
/*  88:    */   {
/*  89:160 */     this.m_nParents = new int[nSize];
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void addParent(int nParent, Instances _Instances)
/*  93:    */   {
/*  94:170 */     if (this.m_nNrOfParents == this.m_nParents.length)
/*  95:    */     {
/*  96:172 */       int[] nParents = new int[2 * this.m_nParents.length];
/*  97:173 */       for (int i = 0; i < this.m_nNrOfParents; i++) {
/*  98:174 */         nParents[i] = this.m_nParents[i];
/*  99:    */       }
/* 100:176 */       this.m_nParents = nParents;
/* 101:    */     }
/* 102:178 */     this.m_nParents[this.m_nNrOfParents] = nParent;
/* 103:179 */     this.m_nNrOfParents += 1;
/* 104:180 */     this.m_nCardinalityOfParents *= _Instances.attribute(nParent).numValues();
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void addParent(int nParent, int iParent, Instances _Instances)
/* 108:    */   {
/* 109:192 */     if (this.m_nNrOfParents == this.m_nParents.length)
/* 110:    */     {
/* 111:194 */       int[] nParents = new int[2 * this.m_nParents.length];
/* 112:195 */       for (int i = 0; i < this.m_nNrOfParents; i++) {
/* 113:196 */         nParents[i] = this.m_nParents[i];
/* 114:    */       }
/* 115:198 */       this.m_nParents = nParents;
/* 116:    */     }
/* 117:200 */     for (int iParent2 = this.m_nNrOfParents; iParent2 > iParent; iParent2--) {
/* 118:201 */       this.m_nParents[iParent2] = this.m_nParents[(iParent2 - 1)];
/* 119:    */     }
/* 120:203 */     this.m_nParents[iParent] = nParent;
/* 121:204 */     this.m_nNrOfParents += 1;
/* 122:205 */     this.m_nCardinalityOfParents *= _Instances.attribute(nParent).numValues();
/* 123:    */   }
/* 124:    */   
/* 125:    */   public int deleteParent(int nParent, Instances _Instances)
/* 126:    */   {
/* 127:215 */     int iParent = 0;
/* 128:216 */     while ((this.m_nParents[iParent] != nParent) && (iParent < this.m_nNrOfParents)) {
/* 129:217 */       iParent++;
/* 130:    */     }
/* 131:219 */     int iParent2 = -1;
/* 132:220 */     if (iParent < this.m_nNrOfParents) {
/* 133:221 */       iParent2 = iParent;
/* 134:    */     }
/* 135:223 */     if (iParent < this.m_nNrOfParents)
/* 136:    */     {
/* 137:224 */       while (iParent < this.m_nNrOfParents - 1)
/* 138:    */       {
/* 139:225 */         this.m_nParents[iParent] = this.m_nParents[(iParent + 1)];
/* 140:226 */         iParent++;
/* 141:    */       }
/* 142:228 */       this.m_nNrOfParents -= 1;
/* 143:229 */       this.m_nCardinalityOfParents /= _Instances.attribute(nParent).numValues();
/* 144:    */     }
/* 145:231 */     return iParent2;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public void deleteLastParent(Instances _Instances)
/* 149:    */   {
/* 150:240 */     this.m_nNrOfParents -= 1;
/* 151:241 */     this.m_nCardinalityOfParents /= _Instances.attribute(this.m_nParents[this.m_nNrOfParents]).numValues();
/* 152:    */   }
/* 153:    */   
/* 154:    */   public void copy(ParentSet other)
/* 155:    */   {
/* 156:251 */     this.m_nCardinalityOfParents = other.m_nCardinalityOfParents;
/* 157:252 */     this.m_nNrOfParents = other.m_nNrOfParents;
/* 158:253 */     for (int iParent = 0; iParent < this.m_nNrOfParents; iParent++) {
/* 159:254 */       this.m_nParents[iParent] = other.m_nParents[iParent];
/* 160:    */     }
/* 161:    */   }
/* 162:    */   
/* 163:    */   public String getRevision()
/* 164:    */   {
/* 165:264 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 166:    */   }
/* 167:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.ParentSet
 * JD-Core Version:    0.7.0.1
 */