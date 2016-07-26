/*   1:    */ package weka.core.tokenizers;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.core.Option;
/*   6:    */ import weka.core.Utils;
/*   7:    */ 
/*   8:    */ public abstract class CharacterDelimitedTokenizer
/*   9:    */   extends Tokenizer
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = -3091468793633408477L;
/*  12: 41 */   protected String m_Delimiters = " \r\n\t.,;:'\"()?!";
/*  13:    */   
/*  14:    */   public Enumeration<Option> listOptions()
/*  15:    */   {
/*  16: 50 */     Vector<Option> result = new Vector();
/*  17:    */     
/*  18: 52 */     result.addElement(new Option("\tThe delimiters to use\n\t(default ' \\r\\n\\t.,;:'\"()?!').", "delimiters", 1, "-delimiters <value>"));
/*  19:    */     
/*  20:    */ 
/*  21:    */ 
/*  22: 56 */     return result.elements();
/*  23:    */   }
/*  24:    */   
/*  25:    */   public String[] getOptions()
/*  26:    */   {
/*  27: 66 */     Vector<String> result = new Vector();
/*  28:    */     
/*  29: 68 */     result.add("-delimiters");
/*  30: 69 */     result.add(getDelimiters());
/*  31:    */     
/*  32: 71 */     return (String[])result.toArray(new String[result.size()]);
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void setOptions(String[] options)
/*  36:    */     throws Exception
/*  37:    */   {
/*  38: 85 */     String tmpStr = Utils.getOption("delimiters", options);
/*  39: 86 */     if (tmpStr.length() != 0) {
/*  40: 87 */       setDelimiters(tmpStr);
/*  41:    */     } else {
/*  42: 89 */       setDelimiters(" \r\n\t.,;:'\"()?!");
/*  43:    */     }
/*  44:    */   }
/*  45:    */   
/*  46:    */   public String getDelimiters()
/*  47:    */   {
/*  48: 99 */     return this.m_Delimiters;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setDelimiters(String value)
/*  52:    */   {
/*  53:113 */     this.m_Delimiters = Utils.unbackQuoteChars(value);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public String delimitersTipText()
/*  57:    */   {
/*  58:123 */     return "Set of delimiter characters to use in tokenizing (\\r, \\n and \\t can be used for carriage-return, line-feed and tab)";
/*  59:    */   }
/*  60:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.tokenizers.CharacterDelimitedTokenizer
 * JD-Core Version:    0.7.0.1
 */