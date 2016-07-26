/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ import java.util.Iterator;
/*   5:    */ import no.uib.cipr.matrix.AbstractMatrix;
/*   6:    */ import no.uib.cipr.matrix.DenseVector;
/*   7:    */ import no.uib.cipr.matrix.Matrix;
/*   8:    */ import no.uib.cipr.matrix.MatrixEntry;
/*   9:    */ import no.uib.cipr.matrix.Vector;
/*  10:    */ import no.uib.cipr.matrix.VectorEntry;
/*  11:    */ 
/*  12:    */ public class FlexCompColMatrix
/*  13:    */   extends AbstractMatrix
/*  14:    */ {
/*  15:    */   SparseVector[] colD;
/*  16:    */   
/*  17:    */   public FlexCompColMatrix(int numRows, int numColumns)
/*  18:    */   {
/*  19: 53 */     super(numRows, numColumns);
/*  20:    */     
/*  21: 55 */     this.colD = new SparseVector[numColumns];
/*  22: 56 */     for (int i = 0; i < numColumns; i++) {
/*  23: 57 */       this.colD[i] = new SparseVector(numRows);
/*  24:    */     }
/*  25:    */   }
/*  26:    */   
/*  27:    */   public FlexCompColMatrix(Matrix A, boolean deep)
/*  28:    */   {
/*  29: 70 */     super(A);
/*  30: 71 */     this.colD = new SparseVector[this.numColumns];
/*  31: 73 */     if (deep)
/*  32:    */     {
/*  33: 74 */       for (int i = 0; i < this.numColumns; i++) {
/*  34: 75 */         this.colD[i] = new SparseVector(this.numRows);
/*  35:    */       }
/*  36: 76 */       set(A);
/*  37:    */     }
/*  38:    */     else
/*  39:    */     {
/*  40: 78 */       FlexCompColMatrix Ar = (FlexCompColMatrix)A;
/*  41: 79 */       for (int i = 0; i < this.numColumns; i++) {
/*  42: 80 */         this.colD[i] = Ar.getColumn(i);
/*  43:    */       }
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:    */   public FlexCompColMatrix(Matrix A)
/*  48:    */   {
/*  49: 91 */     this(A, true);
/*  50:    */   }
/*  51:    */   
/*  52:    */   public SparseVector getColumn(int i)
/*  53:    */   {
/*  54: 98 */     return this.colD[i];
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setColumn(int i, SparseVector x)
/*  58:    */   {
/*  59:105 */     if (x.size() != this.numRows) {
/*  60:106 */       throw new IllegalArgumentException("New column must be of the same size as existing column");
/*  61:    */     }
/*  62:108 */     this.colD[i] = x;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public Vector multAdd(double alpha, Vector x, Vector y)
/*  66:    */   {
/*  67:113 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/*  68:114 */       return super.multAdd(alpha, x, y);
/*  69:    */     }
/*  70:116 */     checkMultAdd(x, y);
/*  71:    */     
/*  72:118 */     double[] xd = ((DenseVector)x).getData();
/*  73:119 */     double[] yd = ((DenseVector)y).getData();
/*  74:    */     
/*  75:    */ 
/*  76:122 */     y.scale(1.0D / alpha);
/*  77:125 */     for (int i = 0; i < this.numColumns; i++)
/*  78:    */     {
/*  79:126 */       SparseVector v = this.colD[i];
/*  80:127 */       int[] index = v.getIndex();
/*  81:128 */       double[] data = v.getData();
/*  82:129 */       int length = v.getUsed();
/*  83:130 */       for (int j = 0; j < length; j++) {
/*  84:131 */         yd[index[j]] += data[j] * xd[i];
/*  85:    */       }
/*  86:    */     }
/*  87:135 */     return y.scale(alpha);
/*  88:    */   }
/*  89:    */   
/*  90:    */   public Vector transMultAdd(double alpha, Vector x, Vector y)
/*  91:    */   {
/*  92:141 */     checkTransMultAdd(x, y);
/*  93:143 */     for (int i = 0; i < this.numColumns; i++) {
/*  94:144 */       y.add(i, alpha * this.colD[i].dot(x));
/*  95:    */     }
/*  96:146 */     return y;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void add(int row, int column, double value)
/* 100:    */   {
/* 101:151 */     this.colD[column].add(row, value);
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void set(int row, int column, double value)
/* 105:    */   {
/* 106:156 */     this.colD[column].set(row, value);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public double get(int row, int column)
/* 110:    */   {
/* 111:161 */     return this.colD[column].get(row);
/* 112:    */   }
/* 113:    */   
/* 114:    */   public Iterator<MatrixEntry> iterator()
/* 115:    */   {
/* 116:166 */     return new ColMatrixIterator(null);
/* 117:    */   }
/* 118:    */   
/* 119:    */   public FlexCompColMatrix copy()
/* 120:    */   {
/* 121:171 */     return new FlexCompColMatrix(this);
/* 122:    */   }
/* 123:    */   
/* 124:    */   public FlexCompColMatrix zero()
/* 125:    */   {
/* 126:176 */     for (int i = 0; i < this.numColumns; i++) {
/* 127:177 */       this.colD[i].zero();
/* 128:    */     }
/* 129:178 */     return this;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public void compact()
/* 133:    */   {
/* 134:185 */     for (Vector v : this.colD) {
/* 135:186 */       ((SparseVector)v).compact();
/* 136:    */     }
/* 137:    */   }
/* 138:    */   
/* 139:    */   private class ColMatrixIterator
/* 140:    */     implements Iterator<MatrixEntry>
/* 141:    */   {
/* 142:197 */     private SuperIterator<SparseVector, VectorEntry> iterator = new SuperIterator(
/* 143:198 */       Arrays.asList(FlexCompColMatrix.this.colD));
/* 144:203 */     private FlexCompColMatrix.ColMatrixEntry entry = new FlexCompColMatrix.ColMatrixEntry(null);
/* 145:    */     
/* 146:    */     private ColMatrixIterator() {}
/* 147:    */     
/* 148:    */     public boolean hasNext()
/* 149:    */     {
/* 150:206 */       return this.iterator.hasNext();
/* 151:    */     }
/* 152:    */     
/* 153:    */     public MatrixEntry next()
/* 154:    */     {
/* 155:210 */       SuperIterator.SuperIteratorEntry<VectorEntry> se = this.iterator.next();
/* 156:211 */       this.entry.update(se.index(), (VectorEntry)se.get());
/* 157:212 */       return this.entry;
/* 158:    */     }
/* 159:    */     
/* 160:    */     public void remove()
/* 161:    */     {
/* 162:216 */       this.iterator.remove();
/* 163:    */     }
/* 164:    */   }
/* 165:    */   
/* 166:    */   private static class ColMatrixEntry
/* 167:    */     implements MatrixEntry
/* 168:    */   {
/* 169:    */     private int column;
/* 170:    */     private VectorEntry entry;
/* 171:    */     
/* 172:    */     public void update(int column, VectorEntry entry)
/* 173:    */     {
/* 174:231 */       this.column = column;
/* 175:232 */       this.entry = entry;
/* 176:    */     }
/* 177:    */     
/* 178:    */     public int row()
/* 179:    */     {
/* 180:236 */       return this.entry.index();
/* 181:    */     }
/* 182:    */     
/* 183:    */     public int column()
/* 184:    */     {
/* 185:240 */       return this.column;
/* 186:    */     }
/* 187:    */     
/* 188:    */     public double get()
/* 189:    */     {
/* 190:244 */       return this.entry.get();
/* 191:    */     }
/* 192:    */     
/* 193:    */     public void set(double value)
/* 194:    */     {
/* 195:248 */       this.entry.set(value);
/* 196:    */     }
/* 197:    */   }
/* 198:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.FlexCompColMatrix
 * JD-Core Version:    0.7.0.1
 */