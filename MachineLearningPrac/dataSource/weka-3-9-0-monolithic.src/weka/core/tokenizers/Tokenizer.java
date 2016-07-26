/*   1:    */ package weka.core.tokenizers;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.InputStreamReader;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.io.Serializable;
/*   7:    */ import java.util.Enumeration;
/*   8:    */ import java.util.Vector;
/*   9:    */ import weka.core.Option;
/*  10:    */ import weka.core.OptionHandler;
/*  11:    */ import weka.core.RevisionHandler;
/*  12:    */ 
/*  13:    */ public abstract class Tokenizer
/*  14:    */   implements Enumeration<String>, OptionHandler, Serializable, RevisionHandler
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = 7781271062738973996L;
/*  17:    */   
/*  18:    */   public abstract String globalInfo();
/*  19:    */   
/*  20:    */   public Enumeration<Option> listOptions()
/*  21:    */   {
/*  22: 60 */     return new Vector().elements();
/*  23:    */   }
/*  24:    */   
/*  25:    */   public String[] getOptions()
/*  26:    */   {
/*  27: 70 */     return new String[0];
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void setOptions(String[] options)
/*  31:    */     throws Exception
/*  32:    */   {}
/*  33:    */   
/*  34:    */   public abstract boolean hasMoreElements();
/*  35:    */   
/*  36:    */   public abstract String nextElement();
/*  37:    */   
/*  38:    */   public abstract void tokenize(String paramString);
/*  39:    */   
/*  40:    */   public static String[] tokenize(Tokenizer tokenizer, String[] options)
/*  41:    */     throws Exception
/*  42:    */   {
/*  43:132 */     Vector<String> result = new Vector();
/*  44:    */     
/*  45:    */ 
/*  46:135 */     tokenizer.setOptions(options);
/*  47:    */     
/*  48:    */ 
/*  49:138 */     Vector<String> data = new Vector();
/*  50:    */     
/*  51:    */ 
/*  52:141 */     boolean processed = false;
/*  53:142 */     for (int i = 0; i < options.length; i++) {
/*  54:143 */       if (options[i].length() != 0)
/*  55:    */       {
/*  56:144 */         processed = true;
/*  57:145 */         data.add(options[i]);
/*  58:    */       }
/*  59:    */     }
/*  60:150 */     if (!processed)
/*  61:    */     {
/*  62:151 */       BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
/*  63:    */       String line;
/*  64:152 */       while ((line = reader.readLine()) != null) {
/*  65:153 */         data.add(line);
/*  66:    */       }
/*  67:    */     }
/*  68:158 */     for (i = 0; i < data.size(); i++)
/*  69:    */     {
/*  70:159 */       Vector<String> tmpResult = new Vector();
/*  71:160 */       tokenizer.tokenize((String)data.get(i));
/*  72:161 */       while (tokenizer.hasMoreElements()) {
/*  73:162 */         tmpResult.add(tokenizer.nextElement());
/*  74:    */       }
/*  75:165 */       result.addAll(tmpResult);
/*  76:    */     }
/*  77:168 */     return (String[])result.toArray(new String[result.size()]);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static void runTokenizer(Tokenizer tokenizer, String[] options)
/*  81:    */   {
/*  82:    */     try
/*  83:    */     {
/*  84:185 */       String[] result = tokenize(tokenizer, options);
/*  85:186 */       for (int i = 0; i < result.length; i++) {
/*  86:187 */         System.out.println(result[i]);
/*  87:    */       }
/*  88:    */     }
/*  89:    */     catch (Exception e)
/*  90:    */     {
/*  91:190 */       e.printStackTrace();
/*  92:    */     }
/*  93:    */   }
/*  94:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.tokenizers.Tokenizer
 * JD-Core Version:    0.7.0.1
 */