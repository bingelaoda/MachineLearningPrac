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
/*  12:    */ public class FlexCompRowMatrix
/*  13:    */   extends AbstractMatrix
/*  14:    */ {
/*  15:    */   SparseVector[] rowD;
/*  16:    */   
/*  17:    */   public FlexCompRowMatrix(int numRows, int numColumns)
/*  18:    */   {
/*  19: 53 */     super(numRows, numColumns);
/*  20:    */     
/*  21: 55 */     this.rowD = new SparseVector[numRows];
/*  22: 56 */     for (int i = 0; i < numRows; i++) {
/*  23: 57 */       this.rowD[i] = new SparseVector(numColumns);
/*  24:    */     }
/*  25:    */   }
/*  26:    */   
/*  27:    */   public FlexCompRowMatrix(Matrix A, boolean deep)
/*  28:    */   {
/*  29: 70 */     super(A);
/*  30: 71 */     this.rowD = new SparseVector[this.numRows];
/*  31: 73 */     if (deep)
/*  32:    */     {
/*  33: 74 */       for (int i = 0; i < this.numRows; i++) {
/*  34: 75 */         this.rowD[i] = new SparseVector(this.numColumns);
/*  35:    */       }
/*  36: 76 */       set(A);
/*  37:    */     }
/*  38:    */     else
/*  39:    */     {
/*  40: 78 */       FlexCompRowMatrix Ar = (FlexCompRowMatrix)A;
/*  41: 79 */       for (int i = 0; i < this.numRows; i++) {
/*  42: 80 */         this.rowD[i] = Ar.getRow(i);
/*  43:    */       }
/*  44:    */     }
/*  45:    */   }
/*  46:    */   
/*  47:    */   public FlexCompRowMatrix(Matrix A)
/*  48:    */   {
/*  49: 91 */     this(A, true);
/*  50:    */   }
/*  51:    */   
/*  52:    */   public SparseVector getRow(int i)
/*  53:    */   {
/*  54: 98 */     return this.rowD[i];
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setRow(int i, SparseVector x)
/*  58:    */   {
/*  59:105 */     if (x.size() != this.numColumns) {
/*  60:106 */       throw new IllegalArgumentException("New row must be of the same size as existing row");
/*  61:    */     }
/*  62:108 */     this.rowD[i] = x;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public Vector multAdd(double alpha, Vector x, Vector y)
/*  66:    */   {
/*  67:113 */     checkMultAdd(x, y);
/*  68:115 */     for (int i = 0; i < this.numRows; i++) {
/*  69:116 */       y.add(i, alpha * this.rowD[i].dot(x));
/*  70:    */     }
/*  71:118 */     return y;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public Vector transMultAdd(double alpha, Vector x, Vector y)
/*  75:    */   {
/*  76:123 */     if ((!(x instanceof DenseVector)) || (!(y instanceof DenseVector))) {
/*  77:124 */       return super.transMultAdd(alpha, x, y);
/*  78:    */     }
/*  79:126 */     checkTransMultAdd(x, y);
/*  80:    */     
/*  81:128 */     double[] xd = ((DenseVector)x).getData();
/*  82:129 */     double[] yd = ((DenseVector)y).getData();
/*  83:    */     
/*  84:    */ 
/*  85:132 */     y.scale(1.0D / alpha);
/*  86:135 */     for (int i = 0; i < this.numRows; i++)
/*  87:    */     {
/*  88:136 */       SparseVector v = this.rowD[i];
/*  89:137 */       int[] index = v.getIndex();
/*  90:138 */       double[] data = v.getData();
/*  91:139 */       int length = v.getUsed();
/*  92:140 */       for (int j = 0; j < length; j++) {
/*  93:141 */         yd[index[j]] += data[j] * xd[i];
/*  94:    */       }
/*  95:    */     }
/*  96:145 */     return y.scale(alpha);
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void add(int row, int column, double value)
/* 100:    */   {
/* 101:150 */     this.rowD[row].add(column, value);
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void set(int row, int column, double value)
/* 105:    */   {
/* 106:155 */     this.rowD[row].set(column, value);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public double get(int row, int column)
/* 110:    */   {
/* 111:160 */     return this.rowD[row].get(column);
/* 112:    */   }
/* 113:    */   
/* 114:    */   public Iterator<MatrixEntry> iterator()
/* 115:    */   {
/* 116:165 */     return new RowMatrixIterator(null);
/* 117:    */   }
/* 118:    */   
/* 119:    */   public Matrix copy()
/* 120:    */   {
/* 121:170 */     return new FlexCompRowMatrix(this);
/* 122:    */   }
/* 123:    */   
/* 124:    */   public FlexCompRowMatrix zero()
/* 125:    */   {
/* 126:175 */     for (int i = 0; i < this.numRows; i++) {
/* 127:176 */       this.rowD[i].zero();
/* 128:    */     }
/* 129:177 */     return this;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public Matrix set(Matrix B)
/* 133:    */   {
/* 134:182 */     if (!(B instanceof FlexCompRowMatrix)) {
/* 135:183 */       return super.set(B);
/* 136:    */     }
/* 137:185 */     checkSize(B);
/* 138:    */     
/* 139:187 */     FlexCompRowMatrix Bc = (FlexCompRowMatrix)B;
/* 140:189 */     for (int i = 0; i < this.numRows; i++) {
/* 141:190 */       this.rowD[i].set(Bc.rowD[i]);
/* 142:    */     }
/* 143:192 */     return this;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public void compact()
/* 147:    */   {
/* 148:199 */     for (Vector v : this.rowD) {
/* 149:200 */       ((SparseVector)v).compact();
/* 150:    */     }
/* 151:    */   }
/* 152:    */   
/* 153:    */   private class RowMatrixIterator
/* 154:    */     implements Iterator<MatrixEntry>
/* 155:    */   {
/* 156:211 */     private SuperIterator<SparseVector, VectorEntry> iterator = new SuperIterator(
/* 157:212 */       Arrays.asList(FlexCompRowMatrix.this.rowD));
/* 158:217 */     private FlexCompRowMatrix.RowMatrixEntry entry = new FlexCompRowMatrix.RowMatrixEntry(null);
/* 159:    */     
/* 160:    */     private RowMatrixIterator() {}
/* 161:    */     
/* 162:    */     public boolean hasNext()
/* 163:    */     {
/* 164:220 */       return this.iterator.hasNext();
/* 165:    */     }
/* 166:    */     
/* 167:    */     public MatrixEntry next()
/* 168:    */     {
/* 169:224 */       SuperIterator.SuperIteratorEntry<VectorEntry> se = this.iterator.next();
/* 170:225 */       this.entry.update(se.index(), (VectorEntry)se.get());
/* 171:226 */       return this.entry;
/* 172:    */     }
/* 173:    */     
/* 174:    */     public void remove()
/* 175:    */     {
/* 176:230 */       this.iterator.remove();
/* 177:    */     }
/* 178:    */   }
/* 179:    */   
/* 180:    */   private static class RowMatrixEntry
/* 181:    */     implements MatrixEntry
/* 182:    */   {
/* 183:    */     private int row;
/* 184:    */     private VectorEntry entry;
/* 185:    */     
/* 186:    */     public void update(int row, VectorEntry entry)
/* 187:    */     {
/* 188:245 */       this.row = row;
/* 189:246 */       this.entry = entry;
/* 190:    */     }
/* 191:    */     
/* 192:    */     public int row()
/* 193:    */     {
/* 194:250 */       return this.row;
/* 195:    */     }
/* 196:    */     
/* 197:    */     public int column()
/* 198:    */     {
/* 199:254 */       return this.entry.index();
/* 200:    */     }
/* 201:    */     
/* 202:    */     public double get()
/* 203:    */     {
/* 204:258 */       return this.entry.get();
/* 205:    */     }
/* 206:    */     
/* 207:    */     public void set(double value)
/* 208:    */     {
/* 209:262 */       this.entry.set(value);
/* 210:    */     }
/* 211:    */   }
/* 212:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.FlexCompRowMatrix
 * JD-Core Version:    0.7.0.1
 */