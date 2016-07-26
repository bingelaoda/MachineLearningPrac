/*   1:    */ package org.apache.commons.compress.archivers.zip;
/*   2:    */ 
/*   3:    */ import java.io.DataInputStream;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.InputStream;
/*   6:    */ import java.util.Arrays;
/*   7:    */ 
/*   8:    */ class BinaryTree
/*   9:    */ {
/*  10:    */   private static final int UNDEFINED = -1;
/*  11:    */   private static final int NODE = -2;
/*  12:    */   private final int[] tree;
/*  13:    */   
/*  14:    */   public BinaryTree(int depth)
/*  15:    */   {
/*  16: 48 */     this.tree = new int[(1 << depth + 1) - 1];
/*  17: 49 */     Arrays.fill(this.tree, -1);
/*  18:    */   }
/*  19:    */   
/*  20:    */   public void addLeaf(int node, int path, int depth, int value)
/*  21:    */   {
/*  22: 61 */     if (depth == 0)
/*  23:    */     {
/*  24: 63 */       if (this.tree[node] == -1) {
/*  25: 64 */         this.tree[node] = value;
/*  26:    */       } else {
/*  27: 66 */         throw new IllegalArgumentException("Tree value at index " + node + " has already been assigned (" + this.tree[node] + ")");
/*  28:    */       }
/*  29:    */     }
/*  30:    */     else
/*  31:    */     {
/*  32: 70 */       this.tree[node] = -2;
/*  33:    */       
/*  34:    */ 
/*  35: 73 */       int nextChild = 2 * node + 1 + (path & 0x1);
/*  36: 74 */       addLeaf(nextChild, path >>> 1, depth - 1, value);
/*  37:    */     }
/*  38:    */   }
/*  39:    */   
/*  40:    */   public int read(BitStream stream)
/*  41:    */     throws IOException
/*  42:    */   {
/*  43: 85 */     int currentIndex = 0;
/*  44:    */     for (;;)
/*  45:    */     {
/*  46: 88 */       int bit = stream.nextBit();
/*  47: 89 */       if (bit == -1) {
/*  48: 90 */         return -1;
/*  49:    */       }
/*  50: 93 */       int childIndex = 2 * currentIndex + 1 + bit;
/*  51: 94 */       int value = this.tree[childIndex];
/*  52: 95 */       if (value == -2)
/*  53:    */       {
/*  54: 97 */         currentIndex = childIndex;
/*  55:    */       }
/*  56:    */       else
/*  57:    */       {
/*  58: 98 */         if (value != -1) {
/*  59: 99 */           return value;
/*  60:    */         }
/*  61:101 */         throw new IOException("The child " + bit + " of node at index " + currentIndex + " is not defined");
/*  62:    */       }
/*  63:    */     }
/*  64:    */   }
/*  65:    */   
/*  66:    */   static BinaryTree decode(InputStream in, int totalNumberOfValues)
/*  67:    */     throws IOException
/*  68:    */   {
/*  69:112 */     int size = in.read() + 1;
/*  70:113 */     if (size == 0) {
/*  71:114 */       throw new IOException("Cannot read the size of the encoded tree, unexpected end of stream");
/*  72:    */     }
/*  73:117 */     byte[] encodedTree = new byte[size];
/*  74:118 */     new DataInputStream(in).readFully(encodedTree);
/*  75:    */     
/*  76:    */ 
/*  77:121 */     int maxLength = 0;
/*  78:    */     
/*  79:123 */     int[] originalBitLengths = new int[totalNumberOfValues];
/*  80:124 */     int pos = 0;
/*  81:125 */     for (byte b : encodedTree)
/*  82:    */     {
/*  83:127 */       int numberOfValues = ((b & 0xF0) >> 4) + 1;
/*  84:128 */       int bitLength = (b & 0xF) + 1;
/*  85:130 */       for (int j = 0; j < numberOfValues; j++) {
/*  86:131 */         originalBitLengths[(pos++)] = bitLength;
/*  87:    */       }
/*  88:134 */       maxLength = Math.max(maxLength, bitLength);
/*  89:    */     }
/*  90:138 */     int[] permutation = new int[originalBitLengths.length];
/*  91:139 */     for (int k = 0; k < permutation.length; k++) {
/*  92:140 */       permutation[k] = k;
/*  93:    */     }
/*  94:143 */     int c = 0;
/*  95:144 */     int[] sortedBitLengths = new int[originalBitLengths.length];
/*  96:145 */     for (int k = 0; k < originalBitLengths.length; k++) {
/*  97:147 */       for (int l = 0; l < originalBitLengths.length; l++) {
/*  98:149 */         if (originalBitLengths[l] == k)
/*  99:    */         {
/* 100:151 */           sortedBitLengths[c] = k;
/* 101:    */           
/* 102:    */ 
/* 103:154 */           permutation[c] = l;
/* 104:    */           
/* 105:156 */           c++;
/* 106:    */         }
/* 107:    */       }
/* 108:    */     }
/* 109:162 */     int code = 0;
/* 110:163 */     int codeIncrement = 0;
/* 111:164 */     int lastBitLength = 0;
/* 112:    */     
/* 113:166 */     int[] codes = new int[totalNumberOfValues];
/* 114:168 */     for (int i = totalNumberOfValues - 1; i >= 0; i--)
/* 115:    */     {
/* 116:169 */       code += codeIncrement;
/* 117:170 */       if (sortedBitLengths[i] != lastBitLength)
/* 118:    */       {
/* 119:171 */         lastBitLength = sortedBitLengths[i];
/* 120:172 */         codeIncrement = 1 << 16 - lastBitLength;
/* 121:    */       }
/* 122:174 */       codes[permutation[i]] = code;
/* 123:    */     }
/* 124:178 */     BinaryTree tree = new BinaryTree(maxLength);
/* 125:180 */     for (int k = 0; k < codes.length; k++)
/* 126:    */     {
/* 127:181 */       int bitLength = originalBitLengths[k];
/* 128:182 */       if (bitLength > 0) {
/* 129:183 */         tree.addLeaf(0, Integer.reverse(codes[k] << 16), bitLength, k);
/* 130:    */       }
/* 131:    */     }
/* 132:187 */     return tree;
/* 133:    */   }
/* 134:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.compress.archivers.zip.BinaryTree
 * JD-Core Version:    0.7.0.1
 */