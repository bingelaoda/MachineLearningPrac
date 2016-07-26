/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import java.text.DecimalFormat;
/*   4:    */ import java.util.Arrays;
/*   5:    */ 
/*   6:    */ abstract class AbstractDenseMatrix
/*   7:    */   extends AbstractMatrix
/*   8:    */ {
/*   9:    */   double[] data;
/*  10:    */   
/*  11:    */   public AbstractDenseMatrix(int numRows, int numColumns)
/*  12:    */   {
/*  13: 46 */     super(numRows, numColumns);
/*  14:    */     
/*  15:    */ 
/*  16:    */ 
/*  17:    */ 
/*  18: 51 */     long size = numRows * numColumns;
/*  19: 52 */     if (size > 2147483647L) {
/*  20: 53 */       throw new IllegalArgumentException("Matrix of " + numRows + " x " + numColumns + " = " + size + " elements is too large to be allocated using a single Java array.");
/*  21:    */     }
/*  22: 63 */     this.data = new double[numRows * numColumns];
/*  23:    */   }
/*  24:    */   
/*  25:    */   public AbstractDenseMatrix(Matrix A)
/*  26:    */   {
/*  27: 74 */     this(A, true);
/*  28:    */   }
/*  29:    */   
/*  30:    */   public AbstractDenseMatrix(Matrix A, boolean deep)
/*  31:    */   {
/*  32: 87 */     super(A);
/*  33: 89 */     if (deep)
/*  34:    */     {
/*  35: 90 */       this.data = new double[this.numRows * this.numColumns];
/*  36: 91 */       copy(A);
/*  37:    */     }
/*  38:    */     else
/*  39:    */     {
/*  40: 93 */       this.data = ((AbstractDenseMatrix)A).getData();
/*  41:    */     }
/*  42:    */   }
/*  43:    */   
/*  44:    */   abstract void copy(Matrix paramMatrix);
/*  45:    */   
/*  46:    */   public double[] getData()
/*  47:    */   {
/*  48:106 */     return this.data;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void add(int row, int column, double value)
/*  52:    */   {
/*  53:111 */     this.data[getIndex(row, column)] += value;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void set(int row, int column, double value)
/*  57:    */   {
/*  58:116 */     this.data[getIndex(row, column)] = value;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public double get(int row, int column)
/*  62:    */   {
/*  63:121 */     return this.data[getIndex(row, column)];
/*  64:    */   }
/*  65:    */   
/*  66:    */   int getIndex(int row, int column)
/*  67:    */   {
/*  68:128 */     check(row, column);
/*  69:129 */     return row + column * this.numRows;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public Matrix set(Matrix B)
/*  73:    */   {
/*  74:136 */     if (!getClass().isAssignableFrom(B.getClass())) {
/*  75:137 */       return super.set(B);
/*  76:    */     }
/*  77:139 */     checkSize(B);
/*  78:    */     
/*  79:141 */     double[] Bd = ((AbstractDenseMatrix)B).getData();
/*  80:143 */     if (Bd == this.data) {
/*  81:144 */       return this;
/*  82:    */     }
/*  83:146 */     System.arraycopy(Bd, 0, this.data, 0, this.data.length);
/*  84:    */     
/*  85:148 */     return this;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public Matrix zero()
/*  89:    */   {
/*  90:153 */     Arrays.fill(this.data, 0.0D);
/*  91:154 */     return this;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public String toString()
/*  95:    */   {
/*  96:159 */     StringBuilder out = new StringBuilder();
/*  97:160 */     DecimalFormat df = new DecimalFormat("####0.00");
/*  98:162 */     for (int i = 0; i < numRows(); i++)
/*  99:    */     {
/* 100:163 */       for (int j = 0; j < numColumns(); j++)
/* 101:    */       {
/* 102:164 */         double value = get(i, j);
/* 103:165 */         if (value >= 0.0D) {
/* 104:166 */           out.append(" ");
/* 105:    */         }
/* 106:167 */         out.append(" " + df.format(value));
/* 107:    */       }
/* 108:169 */       out.append("\n");
/* 109:    */     }
/* 110:172 */     return out.toString();
/* 111:    */   }
/* 112:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.AbstractDenseMatrix
 * JD-Core Version:    0.7.0.1
 */