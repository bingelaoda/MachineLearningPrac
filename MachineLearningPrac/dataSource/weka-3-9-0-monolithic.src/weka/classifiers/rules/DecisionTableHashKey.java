/*   1:    */ package weka.classifiers.rules;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Instance;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.RevisionHandler;
/*   9:    */ import weka.core.RevisionUtils;
/*  10:    */ 
/*  11:    */ public class DecisionTableHashKey
/*  12:    */   implements Serializable, RevisionHandler
/*  13:    */ {
/*  14:    */   static final long serialVersionUID = 5674163500154964602L;
/*  15:    */   private double[] attributes;
/*  16:    */   private boolean[] missing;
/*  17:    */   private int key;
/*  18:    */   
/*  19:    */   public DecisionTableHashKey(Instance t, int numAtts, boolean ignoreClass)
/*  20:    */     throws Exception
/*  21:    */   {
/*  22: 59 */     int cindex = t.classIndex();
/*  23:    */     
/*  24: 61 */     this.key = -999;
/*  25: 62 */     this.attributes = new double[numAtts];
/*  26: 63 */     this.missing = new boolean[numAtts];
/*  27: 64 */     for (int i = 0; i < numAtts; i++) {
/*  28: 65 */       if ((i == cindex) && (!ignoreClass)) {
/*  29: 66 */         this.missing[i] = true;
/*  30: 68 */       } else if (!(this.missing[i] = t.isMissing(i))) {
/*  31: 69 */         this.attributes[i] = t.value(i);
/*  32:    */       }
/*  33:    */     }
/*  34:    */   }
/*  35:    */   
/*  36:    */   public String toString(Instances t, int maxColWidth)
/*  37:    */   {
/*  38: 85 */     int cindex = t.classIndex();
/*  39: 86 */     StringBuffer text = new StringBuffer();
/*  40: 88 */     for (int i = 0; i < this.attributes.length; i++) {
/*  41: 89 */       if (i != cindex) {
/*  42: 90 */         if (this.missing[i] != 0)
/*  43:    */         {
/*  44: 91 */           text.append("?");
/*  45: 92 */           for (int j = 0; j < maxColWidth; j++) {
/*  46: 93 */             text.append(" ");
/*  47:    */           }
/*  48:    */         }
/*  49:    */         else
/*  50:    */         {
/*  51: 96 */           String ss = t.attribute(i).value((int)this.attributes[i]);
/*  52: 97 */           StringBuffer sb = new StringBuffer(ss);
/*  53: 99 */           for (int j = 0; j < maxColWidth - ss.length() + 1; j++) {
/*  54:100 */             sb.append(" ");
/*  55:    */           }
/*  56:102 */           text.append(sb);
/*  57:    */         }
/*  58:    */       }
/*  59:    */     }
/*  60:106 */     return text.toString();
/*  61:    */   }
/*  62:    */   
/*  63:    */   public DecisionTableHashKey(double[] t)
/*  64:    */   {
/*  65:117 */     int l = t.length;
/*  66:    */     
/*  67:119 */     this.key = -999;
/*  68:120 */     this.attributes = new double[l];
/*  69:121 */     this.missing = new boolean[l];
/*  70:122 */     for (int i = 0; i < l; i++) {
/*  71:123 */       if (t[i] == 1.7976931348623157E+308D)
/*  72:    */       {
/*  73:124 */         this.missing[i] = true;
/*  74:    */       }
/*  75:    */       else
/*  76:    */       {
/*  77:126 */         this.missing[i] = false;
/*  78:127 */         this.attributes[i] = t[i];
/*  79:    */       }
/*  80:    */     }
/*  81:    */   }
/*  82:    */   
/*  83:    */   public int hashCode()
/*  84:    */   {
/*  85:139 */     int hv = 0;
/*  86:141 */     if (this.key != -999) {
/*  87:142 */       return this.key;
/*  88:    */     }
/*  89:143 */     for (int i = 0; i < this.attributes.length; i++) {
/*  90:144 */       if (this.missing[i] != 0) {
/*  91:145 */         hv += i * 13;
/*  92:    */       } else {
/*  93:147 */         hv = (int)(hv + i * 5 * (this.attributes[i] + 1.0D));
/*  94:    */       }
/*  95:    */     }
/*  96:150 */     if (this.key == -999) {
/*  97:151 */       this.key = hv;
/*  98:    */     }
/*  99:153 */     return hv;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public boolean equals(Object b)
/* 103:    */   {
/* 104:164 */     if ((b == null) || (!b.getClass().equals(getClass()))) {
/* 105:165 */       return false;
/* 106:    */     }
/* 107:167 */     boolean ok = true;
/* 108:169 */     if ((b instanceof DecisionTableHashKey))
/* 109:    */     {
/* 110:170 */       DecisionTableHashKey n = (DecisionTableHashKey)b;
/* 111:171 */       for (int i = 0; i < this.attributes.length; i++)
/* 112:    */       {
/* 113:172 */         boolean l = n.missing[i];
/* 114:173 */         if ((this.missing[i] != 0) || (l))
/* 115:    */         {
/* 116:174 */           if (((this.missing[i] != 0) && (!l)) || ((this.missing[i] == 0) && (l)))
/* 117:    */           {
/* 118:175 */             ok = false;
/* 119:176 */             break;
/* 120:    */           }
/* 121:    */         }
/* 122:179 */         else if (this.attributes[i] != n.attributes[i])
/* 123:    */         {
/* 124:180 */           ok = false;
/* 125:181 */           break;
/* 126:    */         }
/* 127:    */       }
/* 128:    */     }
/* 129:    */     else
/* 130:    */     {
/* 131:186 */       return false;
/* 132:    */     }
/* 133:188 */     return ok;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void print_hash_code()
/* 137:    */   {
/* 138:195 */     System.out.println("Hash val: " + hashCode());
/* 139:    */   }
/* 140:    */   
/* 141:    */   public String getRevision()
/* 142:    */   {
/* 143:204 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 144:    */   }
/* 145:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.rules.DecisionTableHashKey
 * JD-Core Version:    0.7.0.1
 */