/*   1:    */ package weka.classifiers.functions.supportVector;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import weka.core.RevisionHandler;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ 
/*   8:    */ public class SMOset
/*   9:    */   implements Serializable, RevisionHandler
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = -8364829283188675777L;
/*  12:    */   private int m_number;
/*  13:    */   private int m_first;
/*  14:    */   private boolean[] m_indicators;
/*  15:    */   private int[] m_next;
/*  16:    */   private int[] m_previous;
/*  17:    */   
/*  18:    */   public SMOset(int size)
/*  19:    */   {
/*  20: 61 */     this.m_indicators = new boolean[size];
/*  21: 62 */     this.m_next = new int[size];
/*  22: 63 */     this.m_previous = new int[size];
/*  23: 64 */     this.m_number = 0;
/*  24: 65 */     this.m_first = -1;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public boolean contains(int index)
/*  28:    */   {
/*  29: 73 */     return this.m_indicators[index];
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void delete(int index)
/*  33:    */   {
/*  34: 81 */     if (this.m_indicators[index] != 0)
/*  35:    */     {
/*  36: 82 */       if (this.m_first == index) {
/*  37: 83 */         this.m_first = this.m_next[index];
/*  38:    */       } else {
/*  39: 85 */         this.m_next[this.m_previous[index]] = this.m_next[index];
/*  40:    */       }
/*  41: 87 */       if (this.m_next[index] != -1) {
/*  42: 88 */         this.m_previous[this.m_next[index]] = this.m_previous[index];
/*  43:    */       }
/*  44: 90 */       this.m_indicators[index] = false;
/*  45: 91 */       this.m_number -= 1;
/*  46:    */     }
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void insert(int index)
/*  50:    */   {
/*  51:100 */     if (this.m_indicators[index] == 0)
/*  52:    */     {
/*  53:101 */       if (this.m_number == 0)
/*  54:    */       {
/*  55:102 */         this.m_first = index;
/*  56:103 */         this.m_next[index] = -1;
/*  57:104 */         this.m_previous[index] = -1;
/*  58:    */       }
/*  59:    */       else
/*  60:    */       {
/*  61:106 */         this.m_previous[this.m_first] = index;
/*  62:107 */         this.m_next[index] = this.m_first;
/*  63:108 */         this.m_previous[index] = -1;
/*  64:109 */         this.m_first = index;
/*  65:    */       }
/*  66:111 */       this.m_indicators[index] = true;
/*  67:112 */       this.m_number += 1;
/*  68:    */     }
/*  69:    */   }
/*  70:    */   
/*  71:    */   public int getNext(int index)
/*  72:    */   {
/*  73:121 */     if (index == -1) {
/*  74:122 */       return this.m_first;
/*  75:    */     }
/*  76:124 */     return this.m_next[index];
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void printElements()
/*  80:    */   {
/*  81:133 */     for (int i = getNext(-1); i != -1; i = getNext(i)) {
/*  82:134 */       System.err.print(i + " ");
/*  83:    */     }
/*  84:136 */     System.err.println();
/*  85:137 */     for (int i = 0; i < this.m_indicators.length; i++) {
/*  86:138 */       if (this.m_indicators[i] != 0) {
/*  87:139 */         System.err.print(i + " ");
/*  88:    */       }
/*  89:    */     }
/*  90:142 */     System.err.println();
/*  91:143 */     System.err.println(this.m_number);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public int numElements()
/*  95:    */   {
/*  96:151 */     return this.m_number;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public String getRevision()
/* 100:    */   {
/* 101:160 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 102:    */   }
/* 103:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.supportVector.SMOset
 * JD-Core Version:    0.7.0.1
 */