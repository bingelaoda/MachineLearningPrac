/*   1:    */ package weka.core.pmml;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.io.StreamTokenizer;
/*   5:    */ import java.io.StringReader;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.List;
/*   8:    */ import org.w3c.dom.Element;
/*   9:    */ import org.w3c.dom.Node;
/*  10:    */ import org.w3c.dom.NodeList;
/*  11:    */ 
/*  12:    */ public class SparseArray
/*  13:    */   extends Array
/*  14:    */   implements Serializable
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = 8129550573612673674L;
/*  17:    */   protected int m_numValues;
/*  18:    */   protected int m_numNonZero;
/*  19:    */   protected List<Integer> m_indices;
/*  20:    */   
/*  21:    */   protected void initialize(Element arrayE)
/*  22:    */     throws Exception
/*  23:    */   {
/*  24: 60 */     this.m_indices = new ArrayList();
/*  25:    */     
/*  26: 62 */     String arrayS = arrayE.getTagName();
/*  27: 63 */     String entriesName = null;
/*  28: 65 */     if (arrayS.equals(Array.ArrayType.REAL_SPARSE.toString()))
/*  29:    */     {
/*  30: 66 */       this.m_type = Array.ArrayType.REAL_SPARSE;
/*  31: 67 */       entriesName = "REAL-Entries";
/*  32:    */     }
/*  33:    */     else
/*  34:    */     {
/*  35: 69 */       this.m_type = Array.ArrayType.INT_SPARSE;
/*  36: 70 */       entriesName = "INT-Entries";
/*  37:    */     }
/*  38: 75 */     String N = arrayE.getAttribute("n");
/*  39: 76 */     if ((N != null) && (N.length() > 0)) {
/*  40: 77 */       this.m_numValues = Integer.parseInt(N);
/*  41:    */     }
/*  42: 81 */     NodeList v = arrayE.getElementsByTagName(entriesName);
/*  43: 82 */     if ((v == null) || (v.getLength() == 0))
/*  44:    */     {
/*  45: 85 */       this.m_numNonZero = 0;
/*  46:    */     }
/*  47:    */     else
/*  48:    */     {
/*  49: 87 */       Element entries = (Element)v.item(0);
/*  50: 88 */       String contents = entries.getChildNodes().item(0).getNodeValue();
/*  51: 89 */       StringReader sr = new StringReader(contents);
/*  52: 90 */       StreamTokenizer st = new StreamTokenizer(sr);
/*  53: 91 */       st.resetSyntax();
/*  54: 92 */       st.whitespaceChars(0, 32);
/*  55: 93 */       st.wordChars(33, 255);
/*  56: 94 */       st.whitespaceChars(32, 32);
/*  57: 95 */       st.quoteChar(34);
/*  58: 96 */       st.quoteChar(39);
/*  59:    */       
/*  60:    */ 
/*  61: 99 */       st.nextToken();
/*  62:100 */       while ((st.ttype != -1) && (st.ttype != 10))
/*  63:    */       {
/*  64:102 */         this.m_values.add(st.sval);
/*  65:103 */         st.nextToken();
/*  66:    */       }
/*  67:107 */       NodeList i = arrayE.getElementsByTagName("Indices");
/*  68:108 */       Element indices = (Element)i.item(0);
/*  69:109 */       contents = indices.getChildNodes().item(0).getNodeValue();
/*  70:110 */       sr = new StringReader(contents);
/*  71:111 */       st = new StreamTokenizer(sr);
/*  72:112 */       st.resetSyntax();
/*  73:113 */       st.whitespaceChars(0, 32);
/*  74:114 */       st.wordChars(33, 255);
/*  75:115 */       st.whitespaceChars(32, 32);
/*  76:116 */       st.quoteChar(34);
/*  77:117 */       st.quoteChar(39);
/*  78:    */       
/*  79:    */ 
/*  80:120 */       st.nextToken();
/*  81:121 */       while ((st.ttype != -1) && (st.ttype != 10))
/*  82:    */       {
/*  83:123 */         Integer newInt = new Integer(Integer.parseInt(st.sval) - 1);
/*  84:124 */         this.m_indices.add(newInt);
/*  85:125 */         st.nextToken();
/*  86:    */       }
/*  87:128 */       this.m_numNonZero = this.m_indices.size();
/*  88:    */     }
/*  89:    */   }
/*  90:    */   
/*  91:    */   protected SparseArray(Element arrayE)
/*  92:    */     throws Exception
/*  93:    */   {
/*  94:140 */     super(arrayE);
/*  95:    */   }
/*  96:    */   
/*  97:    */   protected SparseArray(Array.ArrayType type, List<Object> values, List<Integer> indices)
/*  98:    */   {
/*  99:152 */     super(type, values);
/* 100:    */     
/* 101:154 */     this.m_indices = indices;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public boolean isSparse()
/* 105:    */   {
/* 106:163 */     return true;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public int numValues()
/* 110:    */   {
/* 111:172 */     return this.m_numValues;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public int numNonZero()
/* 115:    */   {
/* 116:181 */     return this.m_numNonZero;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public int index(int position)
/* 120:    */   {
/* 121:191 */     return ((Integer)this.m_indices.get(position)).intValue();
/* 122:    */   }
/* 123:    */   
/* 124:    */   public int locateIndex(int index)
/* 125:    */   {
/* 126:203 */     int min = 0;int max = this.m_indices.size() - 1;
/* 127:205 */     if (max == -1) {
/* 128:206 */       return -1;
/* 129:    */     }
/* 130:211 */     while ((((Integer)this.m_indices.get(min)).intValue() <= index) && (((Integer)this.m_indices.get(max)).intValue() >= index))
/* 131:    */     {
/* 132:212 */       int current = (max + min) / 2;
/* 133:213 */       if (((Integer)this.m_indices.get(current)).intValue() > index) {
/* 134:214 */         max = current - 1;
/* 135:215 */       } else if (((Integer)this.m_indices.get(current)).intValue() < index) {
/* 136:216 */         min = current + 1;
/* 137:    */       } else {
/* 138:218 */         return current;
/* 139:    */       }
/* 140:    */     }
/* 141:221 */     if (((Integer)this.m_indices.get(max)).intValue() < index) {
/* 142:222 */       return max;
/* 143:    */     }
/* 144:224 */     return min - 1;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public String value(int arrIndex)
/* 148:    */     throws Exception
/* 149:    */   {
/* 150:236 */     int index = locateIndex(arrIndex);
/* 151:237 */     if ((index >= 0) && (((Integer)this.m_indices.get(index)).intValue() == arrIndex)) {
/* 152:239 */       return (String)this.m_values.get(index);
/* 153:    */     }
/* 154:241 */     return "0";
/* 155:    */   }
/* 156:    */   
/* 157:    */   public String toString()
/* 158:    */   {
/* 159:246 */     StringBuffer text = new StringBuffer();
/* 160:    */     
/* 161:248 */     text.append("<");
/* 162:249 */     for (int i = 0; i < this.m_indices.size(); i++)
/* 163:    */     {
/* 164:250 */       text.append(((Integer)this.m_indices.get(i)).toString() + " ");
/* 165:251 */       text.append((String)this.m_values.get(i));
/* 166:252 */       if (i < this.m_indices.size() - 1) {
/* 167:253 */         text.append(",");
/* 168:    */       }
/* 169:    */     }
/* 170:257 */     text.append(">");
/* 171:258 */     return text.toString();
/* 172:    */   }
/* 173:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.SparseArray
 * JD-Core Version:    0.7.0.1
 */