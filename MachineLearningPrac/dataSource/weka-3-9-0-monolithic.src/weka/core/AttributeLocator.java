/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.BitSet;
/*   6:    */ 
/*   7:    */ public class AttributeLocator
/*   8:    */   implements Serializable, Comparable<AttributeLocator>, RevisionHandler
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -2932848827681070345L;
/*  11: 42 */   protected int[] m_AllowedIndices = null;
/*  12: 45 */   protected BitSet m_Attributes = null;
/*  13: 48 */   protected ArrayList<AttributeLocator> m_Locators = null;
/*  14: 51 */   protected int m_Type = -1;
/*  15: 54 */   protected Instances m_Data = null;
/*  16: 57 */   protected int[] m_Indices = null;
/*  17: 60 */   protected int[] m_LocatorIndices = null;
/*  18:    */   
/*  19:    */   public AttributeLocator(Instances data, int type)
/*  20:    */   {
/*  21: 70 */     this(data, type, 0, data.numAttributes() - 1);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public AttributeLocator(Instances data, int type, int fromIndex, int toIndex)
/*  25:    */   {
/*  26: 85 */     int[] indices = new int[toIndex - fromIndex + 1];
/*  27: 86 */     for (int i = 0; i < indices.length; i++) {
/*  28: 87 */       indices[i] = (fromIndex + i);
/*  29:    */     }
/*  30: 89 */     initialize(data, type, indices);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public AttributeLocator(Instances data, int type, int[] indices)
/*  34:    */   {
/*  35:103 */     initialize(data, type, indices);
/*  36:    */   }
/*  37:    */   
/*  38:    */   protected void initialize(Instances data, int type, int[] indices)
/*  39:    */   {
/*  40:114 */     this.m_Data = new Instances(data, 0);
/*  41:115 */     this.m_Type = type;
/*  42:    */     
/*  43:117 */     this.m_AllowedIndices = new int[indices.length];
/*  44:118 */     System.arraycopy(indices, 0, this.m_AllowedIndices, 0, indices.length);
/*  45:    */     
/*  46:120 */     locate();
/*  47:    */     
/*  48:122 */     this.m_Indices = find(true);
/*  49:123 */     this.m_LocatorIndices = find(false);
/*  50:    */   }
/*  51:    */   
/*  52:    */   public int getType()
/*  53:    */   {
/*  54:132 */     return this.m_Type;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public int[] getAllowedIndices()
/*  58:    */   {
/*  59:141 */     return this.m_AllowedIndices;
/*  60:    */   }
/*  61:    */   
/*  62:    */   protected void locate()
/*  63:    */   {
/*  64:150 */     this.m_Attributes = new BitSet(this.m_AllowedIndices.length);
/*  65:151 */     this.m_Locators = new ArrayList();
/*  66:153 */     for (int i = 0; i < this.m_AllowedIndices.length; i++)
/*  67:    */     {
/*  68:154 */       if (this.m_Data.attribute(this.m_AllowedIndices[i]).type() == 4) {
/*  69:155 */         this.m_Locators.add(new AttributeLocator(this.m_Data.attribute(this.m_AllowedIndices[i]).relation(), getType()));
/*  70:    */       } else {
/*  71:157 */         this.m_Locators.add(null);
/*  72:    */       }
/*  73:159 */       this.m_Attributes.set(i, this.m_Data.attribute(this.m_AllowedIndices[i]).type() == getType());
/*  74:    */     }
/*  75:    */   }
/*  76:    */   
/*  77:    */   public Instances getData()
/*  78:    */   {
/*  79:169 */     return this.m_Data;
/*  80:    */   }
/*  81:    */   
/*  82:    */   protected int[] find(boolean findAtts)
/*  83:    */   {
/*  84:186 */     ArrayList<Integer> indices = new ArrayList();
/*  85:187 */     if (findAtts) {
/*  86:188 */       for (int i = 0; i < this.m_Attributes.size(); i++) {
/*  87:189 */         if (this.m_Attributes.get(i)) {
/*  88:190 */           indices.add(new Integer(i));
/*  89:    */         }
/*  90:    */       }
/*  91:    */     }
/*  92:194 */     for (int i = 0; i < this.m_Locators.size(); i++) {
/*  93:195 */       if (this.m_Locators.get(i) != null) {
/*  94:196 */         indices.add(new Integer(i));
/*  95:    */       }
/*  96:    */     }
/*  97:201 */     int[] result = new int[indices.size()];
/*  98:202 */     for (i = 0; i < indices.size(); i++) {
/*  99:203 */       result[i] = ((Integer)indices.get(i)).intValue();
/* 100:    */     }
/* 101:205 */     return result;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public int getActualIndex(int index)
/* 105:    */   {
/* 106:215 */     return this.m_AllowedIndices[index];
/* 107:    */   }
/* 108:    */   
/* 109:    */   public int[] getAttributeIndices()
/* 110:    */   {
/* 111:227 */     return this.m_Indices;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public int[] getLocatorIndices()
/* 115:    */   {
/* 116:239 */     return this.m_LocatorIndices;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public AttributeLocator getLocator(int index)
/* 120:    */   {
/* 121:250 */     return (AttributeLocator)this.m_Locators.get(index);
/* 122:    */   }
/* 123:    */   
/* 124:    */   public int compareTo(AttributeLocator o)
/* 125:    */   {
/* 126:267 */     int result = 0;
/* 127:270 */     if (getType() < o.getType()) {
/* 128:271 */       result = -1;
/* 129:273 */     } else if (getType() > o.getType()) {
/* 130:274 */       result = 1;
/* 131:278 */     } else if (getAllowedIndices().length < o.getAllowedIndices().length) {
/* 132:279 */       result = -1;
/* 133:281 */     } else if (getAllowedIndices().length > o.getAllowedIndices().length) {
/* 134:282 */       result = 1;
/* 135:    */     } else {
/* 136:285 */       for (int i = 0; i < getAllowedIndices().length; i++)
/* 137:    */       {
/* 138:286 */         if (getAllowedIndices()[i] < o.getAllowedIndices()[i])
/* 139:    */         {
/* 140:287 */           result = -1;
/* 141:288 */           break;
/* 142:    */         }
/* 143:290 */         if (getAllowedIndices()[i] > o.getAllowedIndices()[i])
/* 144:    */         {
/* 145:291 */           result = 1;
/* 146:292 */           break;
/* 147:    */         }
/* 148:295 */         result = 0;
/* 149:    */       }
/* 150:    */     }
/* 151:301 */     return result;
/* 152:    */   }
/* 153:    */   
/* 154:    */   public boolean equals(Object o)
/* 155:    */   {
/* 156:313 */     return compareTo((AttributeLocator)o) == 0;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public String toString()
/* 160:    */   {
/* 161:322 */     return this.m_Attributes.toString();
/* 162:    */   }
/* 163:    */   
/* 164:    */   public String getRevision()
/* 165:    */   {
/* 166:331 */     return RevisionUtils.extract("$Revision: 10649 $");
/* 167:    */   }
/* 168:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.AttributeLocator
 * JD-Core Version:    0.7.0.1
 */