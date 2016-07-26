/*   1:    */ package weka.core.matrix;
/*   2:    */ 
/*   3:    */ import java.text.DecimalFormat;
/*   4:    */ import java.text.FieldPosition;
/*   5:    */ import weka.core.RevisionHandler;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ 
/*   8:    */ public class ExponentialFormat
/*   9:    */   extends DecimalFormat
/*  10:    */   implements RevisionHandler
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = -5298981701073897741L;
/*  13:    */   protected DecimalFormat nf;
/*  14:    */   protected boolean sign;
/*  15:    */   protected int digits;
/*  16:    */   protected int exp;
/*  17: 45 */   protected boolean trailing = true;
/*  18:    */   
/*  19:    */   public ExponentialFormat()
/*  20:    */   {
/*  21: 48 */     this(5);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public ExponentialFormat(int digits)
/*  25:    */   {
/*  26: 52 */     this(digits, false);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public ExponentialFormat(int digits, boolean trailing)
/*  30:    */   {
/*  31: 56 */     this(digits, 2, true, trailing);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public ExponentialFormat(int digits, int exp, boolean sign, boolean trailing)
/*  35:    */   {
/*  36: 61 */     this.digits = digits;
/*  37: 62 */     this.exp = exp;
/*  38: 63 */     this.sign = sign;
/*  39: 64 */     this.trailing = trailing;
/*  40: 65 */     this.nf = new DecimalFormat(pattern());
/*  41: 66 */     this.nf.setPositivePrefix("+");
/*  42: 67 */     this.nf.setNegativePrefix("-");
/*  43:    */   }
/*  44:    */   
/*  45:    */   public int width()
/*  46:    */   {
/*  47: 71 */     if (!this.trailing) {
/*  48: 71 */       throw new RuntimeException("flexible width");
/*  49:    */     }
/*  50: 72 */     if (this.sign) {
/*  51: 72 */       return 1 + this.digits + 2 + this.exp;
/*  52:    */     }
/*  53: 73 */     return this.digits + 2 + this.exp;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos)
/*  57:    */   {
/*  58: 78 */     StringBuffer s = new StringBuffer(this.nf.format(number));
/*  59: 79 */     if (this.sign)
/*  60:    */     {
/*  61: 80 */       if (s.charAt(0) == '+') {
/*  62: 80 */         s.setCharAt(0, ' ');
/*  63:    */       }
/*  64:    */     }
/*  65: 83 */     else if (s.charAt(0) == '-') {
/*  66: 83 */       s.setCharAt(0, '*');
/*  67:    */     } else {
/*  68: 84 */       s.deleteCharAt(0);
/*  69:    */     }
/*  70: 87 */     return toAppendTo.append(s);
/*  71:    */   }
/*  72:    */   
/*  73:    */   private String pattern()
/*  74:    */   {
/*  75: 91 */     StringBuffer s = new StringBuffer();
/*  76: 92 */     s.append("0.");
/*  77: 93 */     for (int i = 0; i < this.digits - 1; i++) {
/*  78: 94 */       if (this.trailing) {
/*  79: 94 */         s.append('0');
/*  80:    */       } else {
/*  81: 95 */         s.append('#');
/*  82:    */       }
/*  83:    */     }
/*  84: 97 */     s.append('E');
/*  85: 98 */     for (int i = 0; i < this.exp; i++) {
/*  86: 99 */       s.append('0');
/*  87:    */     }
/*  88:101 */     return s.toString();
/*  89:    */   }
/*  90:    */   
/*  91:    */   public String getRevision()
/*  92:    */   {
/*  93:110 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  94:    */   }
/*  95:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.matrix.ExponentialFormat
 * JD-Core Version:    0.7.0.1
 */