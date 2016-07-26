/*   1:    */ package weka.core.matrix;
/*   2:    */ 
/*   3:    */ import java.text.DecimalFormat;
/*   4:    */ import java.text.FieldPosition;
/*   5:    */ import weka.core.RevisionHandler;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ 
/*   8:    */ public class FloatingPointFormat
/*   9:    */   extends DecimalFormat
/*  10:    */   implements RevisionHandler
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = 4500373755333429499L;
/*  13:    */   protected DecimalFormat nf;
/*  14:    */   protected int width;
/*  15:    */   protected int decimal;
/*  16: 46 */   protected boolean trailing = true;
/*  17:    */   
/*  18:    */   public FloatingPointFormat()
/*  19:    */   {
/*  20: 52 */     this(8, 5);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public FloatingPointFormat(int digits)
/*  24:    */   {
/*  25: 56 */     this(8, 2);
/*  26:    */   }
/*  27:    */   
/*  28:    */   public FloatingPointFormat(int w, int d)
/*  29:    */   {
/*  30: 60 */     this.width = w;
/*  31: 61 */     this.decimal = d;
/*  32: 62 */     this.nf = new DecimalFormat(pattern(w, d));
/*  33: 63 */     this.nf.setPositivePrefix(" ");
/*  34: 64 */     this.nf.setNegativePrefix("-");
/*  35:    */   }
/*  36:    */   
/*  37:    */   public FloatingPointFormat(int w, int d, boolean trailingZeros)
/*  38:    */   {
/*  39: 68 */     this(w, d);
/*  40: 69 */     this.trailing = trailingZeros;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos)
/*  44:    */   {
/*  45: 74 */     StringBuffer s = new StringBuffer(this.nf.format(number));
/*  46: 75 */     if (s.length() > this.width)
/*  47:    */     {
/*  48: 76 */       if ((s.charAt(0) == ' ') && (s.length() == this.width + 1))
/*  49:    */       {
/*  50: 77 */         s.deleteCharAt(0);
/*  51:    */       }
/*  52:    */       else
/*  53:    */       {
/*  54: 80 */         s.setLength(this.width);
/*  55: 81 */         for (int i = 0; i < this.width; i++) {
/*  56: 82 */           s.setCharAt(i, '*');
/*  57:    */         }
/*  58:    */       }
/*  59:    */     }
/*  60:    */     else {
/*  61: 86 */       for (int i = 0; i < this.width - s.length(); i++) {
/*  62: 87 */         s.insert(0, ' ');
/*  63:    */       }
/*  64:    */     }
/*  65: 89 */     if ((!this.trailing) && (this.decimal > 0))
/*  66:    */     {
/*  67: 90 */       while (s.charAt(s.length() - 1) == '0') {
/*  68: 91 */         s.deleteCharAt(s.length() - 1);
/*  69:    */       }
/*  70: 92 */       if (s.charAt(s.length() - 1) == '.') {
/*  71: 93 */         s.deleteCharAt(s.length() - 1);
/*  72:    */       }
/*  73:    */     }
/*  74: 96 */     return toAppendTo.append(s);
/*  75:    */   }
/*  76:    */   
/*  77:    */   public static String pattern(int w, int d)
/*  78:    */   {
/*  79:100 */     StringBuffer s = new StringBuffer();
/*  80:101 */     s.append(padding(w - d - 3, '#'));
/*  81:102 */     if (d == 0)
/*  82:    */     {
/*  83:102 */       s.append('0');
/*  84:    */     }
/*  85:    */     else
/*  86:    */     {
/*  87:104 */       s.append("0.");
/*  88:105 */       s.append(padding(d, '0'));
/*  89:    */     }
/*  90:107 */     return s.toString();
/*  91:    */   }
/*  92:    */   
/*  93:    */   private static StringBuffer padding(int n, char c)
/*  94:    */   {
/*  95:111 */     StringBuffer text = new StringBuffer();
/*  96:113 */     for (int i = 0; i < n; i++) {
/*  97:114 */       text.append(c);
/*  98:    */     }
/*  99:117 */     return text;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public int width()
/* 103:    */   {
/* 104:121 */     if (!this.trailing) {
/* 105:121 */       throw new RuntimeException("flexible width");
/* 106:    */     }
/* 107:122 */     return this.width;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public String getRevision()
/* 111:    */   {
/* 112:131 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 113:    */   }
/* 114:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.matrix.FloatingPointFormat
 * JD-Core Version:    0.7.0.1
 */