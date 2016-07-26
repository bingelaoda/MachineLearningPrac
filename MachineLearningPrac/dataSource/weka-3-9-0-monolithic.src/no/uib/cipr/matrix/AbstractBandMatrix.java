/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ import java.util.Iterator;
/*   5:    */ 
/*   6:    */ abstract class AbstractBandMatrix
/*   7:    */   extends AbstractMatrix
/*   8:    */ {
/*   9:    */   double[] data;
/*  10:    */   int kl;
/*  11:    */   int ku;
/*  12:    */   int n;
/*  13:    */   
/*  14:    */   public AbstractBandMatrix(int n, int kl, int ku)
/*  15:    */   {
/*  16: 58 */     super(n, n);
/*  17:    */     
/*  18: 60 */     this.n = n;
/*  19: 61 */     if ((kl < 0) || (ku < 0)) {
/*  20: 62 */       throw new IllegalArgumentException("kl < 0 || ku < 0");
/*  21:    */     }
/*  22: 63 */     this.kl = kl;
/*  23: 64 */     this.ku = ku;
/*  24:    */     
/*  25: 66 */     this.data = new double[this.numColumns * (1 + kl + ku)];
/*  26:    */   }
/*  27:    */   
/*  28:    */   public AbstractBandMatrix(Matrix A, int kl, int ku)
/*  29:    */   {
/*  30: 80 */     this(A, kl, ku, true);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public AbstractBandMatrix(Matrix A, int kl, int ku, boolean deep)
/*  34:    */   {
/*  35: 97 */     super(A);
/*  36: 99 */     if ((kl < 0) || (ku < 0)) {
/*  37:100 */       throw new IllegalArgumentException("kl < 0 || ku < 0");
/*  38:    */     }
/*  39:101 */     if (!isSquare()) {
/*  40:102 */       throw new IllegalArgumentException("Band matrix must be square");
/*  41:    */     }
/*  42:103 */     this.n = this.numRows;
/*  43:104 */     this.kl = kl;
/*  44:105 */     this.ku = ku;
/*  45:107 */     if (deep)
/*  46:    */     {
/*  47:108 */       this.data = new double[this.numColumns * (1 + kl + ku)];
/*  48:109 */       copy(A);
/*  49:    */     }
/*  50:    */     else
/*  51:    */     {
/*  52:111 */       this.data = ((AbstractBandMatrix)A).getData();
/*  53:    */     }
/*  54:    */   }
/*  55:    */   
/*  56:    */   public double[] getData()
/*  57:    */   {
/*  58:118 */     return this.data;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void add(int row, int column, double value)
/*  62:    */   {
/*  63:123 */     checkBand(row, column);
/*  64:124 */     this.data[getIndex(row, column)] += value;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void set(int row, int column, double value)
/*  68:    */   {
/*  69:129 */     checkBand(row, column);
/*  70:130 */     this.data[getIndex(row, column)] = value;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public double get(int row, int column)
/*  74:    */   {
/*  75:135 */     if (!inBand(row, column)) {
/*  76:136 */       return 0.0D;
/*  77:    */     }
/*  78:137 */     return this.data[getIndex(row, column)];
/*  79:    */   }
/*  80:    */   
/*  81:    */   public int numSubDiagonals()
/*  82:    */   {
/*  83:144 */     return this.kl;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public int numSuperDiagonals()
/*  87:    */   {
/*  88:151 */     return this.ku;
/*  89:    */   }
/*  90:    */   
/*  91:    */   boolean inBand(int row, int column)
/*  92:    */   {
/*  93:158 */     return (column - this.ku <= row) && (row <= column + this.kl);
/*  94:    */   }
/*  95:    */   
/*  96:    */   void checkBand(int row, int column)
/*  97:    */   {
/*  98:165 */     if (!inBand(row, column)) {
/*  99:166 */       throw new IndexOutOfBoundsException("Insertion index out of band");
/* 100:    */     }
/* 101:    */   }
/* 102:    */   
/* 103:    */   int getIndex(int row, int column)
/* 104:    */   {
/* 105:173 */     check(row, column);
/* 106:174 */     return this.ku + row - column + column * (this.kl + this.ku + 1);
/* 107:    */   }
/* 108:    */   
/* 109:    */   void copy(Matrix A)
/* 110:    */   {
/* 111:181 */     for (MatrixEntry e : A) {
/* 112:182 */       if (inBand(e.row(), e.column())) {
/* 113:183 */         set(e.row(), e.column(), e.get());
/* 114:    */       }
/* 115:    */     }
/* 116:    */   }
/* 117:    */   
/* 118:    */   public Matrix set(Matrix B)
/* 119:    */   {
/* 120:188 */     if (!(B instanceof AbstractBandMatrix)) {
/* 121:189 */       return super.set(B);
/* 122:    */     }
/* 123:191 */     checkSize(B);
/* 124:    */     
/* 125:193 */     AbstractBandMatrix Bb = (AbstractBandMatrix)B;
/* 126:194 */     if (Bb.kl != this.kl) {
/* 127:195 */       throw new IllegalArgumentException("B.kl != kl");
/* 128:    */     }
/* 129:196 */     if (Bb.ku != this.ku) {
/* 130:197 */       throw new IllegalArgumentException("B.ku != ku");
/* 131:    */     }
/* 132:199 */     double[] Bd = Bb.getData();
/* 133:201 */     if (Bd == this.data) {
/* 134:202 */       return this;
/* 135:    */     }
/* 136:204 */     System.arraycopy(Bd, 0, this.data, 0, this.data.length);
/* 137:    */     
/* 138:206 */     return this;
/* 139:    */   }
/* 140:    */   
/* 141:    */   public Matrix zero()
/* 142:    */   {
/* 143:211 */     Arrays.fill(this.data, 0.0D);
/* 144:212 */     return this;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public Iterator<MatrixEntry> iterator()
/* 148:    */   {
/* 149:217 */     return new BandMatrixIterator();
/* 150:    */   }
/* 151:    */   
/* 152:    */   class BandMatrixIterator
/* 153:    */     extends AbstractMatrix.RefMatrixIterator
/* 154:    */   {
/* 155:    */     private final int lkl;
/* 156:    */     private final int lku;
/* 157:    */     
/* 158:    */     public BandMatrixIterator(int lkl, int lku)
/* 159:    */     {
/* 160:231 */       super();
/* 161:232 */       this.lkl = lkl;
/* 162:233 */       this.lku = lku;
/* 163:    */     }
/* 164:    */     
/* 165:    */     public BandMatrixIterator()
/* 166:    */     {
/* 167:237 */       this(AbstractBandMatrix.this.kl, AbstractBandMatrix.this.ku);
/* 168:    */     }
/* 169:    */     
/* 170:    */     public MatrixEntry next()
/* 171:    */     {
/* 172:242 */       this.entry.update(this.row, this.column);
/* 173:245 */       if ((this.row < Math.min(this.column + this.lkl, AbstractBandMatrix.this.n - 1)) && 
/* 174:246 */         (this.row >= Math.max(this.column - this.lku, 0)))
/* 175:    */       {
/* 176:247 */         this.row += 1;
/* 177:    */       }
/* 178:    */       else
/* 179:    */       {
/* 180:249 */         this.column += 1;
/* 181:250 */         this.row = Math.max(this.column - this.lku, 0);
/* 182:    */       }
/* 183:253 */       return this.entry;
/* 184:    */     }
/* 185:    */   }
/* 186:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.AbstractBandMatrix
 * JD-Core Version:    0.7.0.1
 */