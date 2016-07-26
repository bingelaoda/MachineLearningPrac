/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.List;
/*   6:    */ import org.w3c.dom.Element;
/*   7:    */ import org.w3c.dom.NodeList;
/*   8:    */ 
/*   9:    */ public class VectorInstance
/*  10:    */   implements Serializable
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = -7543200367512646290L;
/*  13:    */   protected String m_ID;
/*  14:    */   protected Array m_values;
/*  15:    */   protected List<FieldRef> m_vectorFields;
/*  16:    */   
/*  17:    */   public VectorInstance(Array values, List<FieldRef> vectorFields)
/*  18:    */   {
/*  19: 58 */     this.m_values = values;
/*  20: 59 */     this.m_vectorFields = vectorFields;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public VectorInstance(Element vecElement, List<FieldRef> vectorFields)
/*  24:    */     throws Exception
/*  25:    */   {
/*  26: 72 */     this.m_vectorFields = vectorFields;
/*  27:    */     
/*  28:    */ 
/*  29:    */ 
/*  30: 76 */     String id = vecElement.getAttribute("id");
/*  31: 77 */     if ((id == null) || (id.length() == 0)) {
/*  32: 78 */       throw new Exception("[VectorInstance] no ID attribute defined!");
/*  33:    */     }
/*  34: 81 */     this.m_ID = id;
/*  35:    */     
/*  36:    */ 
/*  37: 84 */     NodeList s_arrL = vecElement.getElementsByTagName("REAL-SparseArray");
/*  38: 85 */     NodeList d_arrL = vecElement.getElementsByTagName("REAL-ARRAY");
/*  39: 87 */     if ((s_arrL.getLength() == 0) && (d_arrL.getLength() == 0)) {
/*  40: 88 */       throw new Exception("[VectorInstance] no arrays defined!");
/*  41:    */     }
/*  42: 91 */     NodeList arrL = s_arrL.getLength() > 0 ? s_arrL : d_arrL;
/*  43:    */     
/*  44:    */ 
/*  45:    */ 
/*  46:    */ 
/*  47: 96 */     Element theArray = (Element)arrL.item(0);
/*  48:    */     
/*  49: 98 */     this.m_values = Array.create(theArray);
/*  50:    */   }
/*  51:    */   
/*  52:    */   public String getID()
/*  53:    */   {
/*  54:107 */     return this.m_ID;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public Array getValues()
/*  58:    */   {
/*  59:116 */     return this.m_values;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public List<FieldRef> getVectorFields()
/*  63:    */   {
/*  64:125 */     return this.m_vectorFields;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public VectorInstance subtract(double[] other)
/*  68:    */     throws Exception
/*  69:    */   {
/*  70:138 */     ArrayList<Object> diffVals = new ArrayList();
/*  71:139 */     for (int i = 0; i < other.length; i++)
/*  72:    */     {
/*  73:140 */       double x = this.m_values.valueDouble(i);
/*  74:141 */       double y = other[i];
/*  75:    */       
/*  76:143 */       double result = x - y;
/*  77:144 */       diffVals.add(new Double(result));
/*  78:    */     }
/*  79:147 */     Array newArray = Array.create(diffVals, null);
/*  80:    */     
/*  81:149 */     return new VectorInstance(newArray, this.m_vectorFields);
/*  82:    */   }
/*  83:    */   
/*  84:    */   public VectorInstance subtract(VectorInstance other)
/*  85:    */     throws Exception
/*  86:    */   {
/*  87:163 */     if (this.m_vectorFields.size() != other.getVectorFields().size()) {
/*  88:164 */       throw new Exception("[VectorInstance.dotProduct] supplied vector instance does not have the same number of vector fields as this vector instance!");
/*  89:    */     }
/*  90:168 */     ArrayList<Object> diffVals = new ArrayList();
/*  91:169 */     for (int i = 0; i < this.m_vectorFields.size(); i++)
/*  92:    */     {
/*  93:170 */       double x = this.m_values.valueDouble(i);
/*  94:171 */       double y = other.getValues().valueDouble(i);
/*  95:172 */       double result = x - y;
/*  96:173 */       diffVals.add(new Double(result));
/*  97:    */     }
/*  98:176 */     Array newArray = Array.create(diffVals, null);
/*  99:    */     
/* 100:178 */     return new VectorInstance(newArray, this.m_vectorFields);
/* 101:    */   }
/* 102:    */   
/* 103:    */   public double dotProduct(VectorInstance other)
/* 104:    */     throws Exception
/* 105:    */   {
/* 106:192 */     if (this.m_vectorFields.size() != other.getVectorFields().size()) {
/* 107:193 */       throw new Exception("[VectorInstance.dotProduct] supplied vector instance does not have the same number of vector fields as this vector instance!");
/* 108:    */     }
/* 109:196 */     double result = 0.0D;
/* 110:    */     
/* 111:198 */     Array otherValues = other.getValues();
/* 112:    */     
/* 113:    */ 
/* 114:201 */     int n1 = this.m_values.numValues();
/* 115:202 */     int n2 = otherValues.numValues();
/* 116:    */     
/* 117:204 */     int p1 = 0;
/* 118:204 */     for (int p2 = 0; (p1 < n1) && (p2 < n2);)
/* 119:    */     {
/* 120:205 */       int ind1 = this.m_values.index(p1);
/* 121:206 */       int ind2 = otherValues.index(p2);
/* 122:208 */       if (ind1 == ind2)
/* 123:    */       {
/* 124:210 */         result += this.m_values.valueSparseDouble(p1) * otherValues.valueSparseDouble(p2);
/* 125:211 */         p1++;
/* 126:212 */         p2++;
/* 127:    */       }
/* 128:213 */       else if (ind1 > ind2)
/* 129:    */       {
/* 130:214 */         p2++;
/* 131:    */       }
/* 132:    */       else
/* 133:    */       {
/* 134:216 */         p1++;
/* 135:    */       }
/* 136:    */     }
/* 137:220 */     return result;
/* 138:    */   }
/* 139:    */   
/* 140:    */   public double dotProduct(double[] other)
/* 141:    */     throws Exception
/* 142:    */   {
/* 143:234 */     double result = 0.0D;
/* 144:    */     
/* 145:236 */     int n1 = this.m_values.numValues();
/* 146:237 */     for (int i = 0; i < n1; i++)
/* 147:    */     {
/* 148:238 */       int ind1 = this.m_values.index(i);
/* 149:    */       
/* 150:240 */       result += this.m_values.valueSparseDouble(i) * other[ind1];
/* 151:    */     }
/* 152:243 */     return result;
/* 153:    */   }
/* 154:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.VectorInstance
 * JD-Core Version:    0.7.0.1
 */