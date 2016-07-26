/*   1:    */ package weka.core.stopwords;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.core.Option;
/*   9:    */ import weka.core.Utils;
/*  10:    */ 
/*  11:    */ public class MultiStopwords
/*  12:    */   extends AbstractStopwords
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = -8568762652879773063L;
/*  15: 62 */   protected StopwordsHandler[] m_Stopwords = new StopwordsHandler[0];
/*  16:    */   
/*  17:    */   public String globalInfo()
/*  18:    */   {
/*  19: 69 */     return "Applies the specified stopwords algorithms one after other.\nAs soon as a word has been identified as stopword, the loop is exited.";
/*  20:    */   }
/*  21:    */   
/*  22:    */   public Enumeration<Option> listOptions()
/*  23:    */   {
/*  24: 82 */     Vector<Option> result = new Vector();
/*  25:    */     
/*  26: 84 */     Enumeration<Option> enm = super.listOptions();
/*  27: 85 */     while (enm.hasMoreElements()) {
/*  28: 86 */       result.add(enm.nextElement());
/*  29:    */     }
/*  30: 88 */     result.addElement(new Option("\t" + stopwordsTipText() + "\n" + "\t(default: none)", "stopwords", 1, "-stopwords <classname + options>"));
/*  31:    */     
/*  32:    */ 
/*  33:    */ 
/*  34:    */ 
/*  35: 93 */     return result.elements();
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void setOptions(String[] options)
/*  39:    */     throws Exception
/*  40:    */   {
/*  41:108 */     List<StopwordsHandler> handlers = new ArrayList();
/*  42:    */     String tmpStr;
/*  43:    */     do
/*  44:    */     {
/*  45:110 */       tmpStr = Utils.getOption("stopwords", options);
/*  46:111 */       if (!tmpStr.isEmpty())
/*  47:    */       {
/*  48:112 */         String[] tmpOptions = Utils.splitOptions(tmpStr);
/*  49:113 */         tmpStr = tmpOptions[0];
/*  50:114 */         tmpOptions[0] = "";
/*  51:115 */         handlers.add((StopwordsHandler)Utils.forName(StopwordsHandler.class, tmpStr, tmpOptions));
/*  52:    */       }
/*  53:118 */     } while (!tmpStr.isEmpty());
/*  54:120 */     setStopwords((StopwordsHandler[])handlers.toArray(new StopwordsHandler[handlers.size()]));
/*  55:    */     
/*  56:122 */     super.setOptions(options);
/*  57:    */   }
/*  58:    */   
/*  59:    */   public String[] getOptions()
/*  60:    */   {
/*  61:132 */     List<String> options = new ArrayList(Arrays.asList(super.getOptions()));
/*  62:134 */     for (StopwordsHandler handler : this.m_Stopwords)
/*  63:    */     {
/*  64:135 */       options.add("-stopwords");
/*  65:136 */       options.add(Utils.toCommandLine(handler));
/*  66:    */     }
/*  67:139 */     return (String[])options.toArray(new String[options.size()]);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void setStopwords(StopwordsHandler[] value)
/*  71:    */   {
/*  72:148 */     this.m_Stopwords = value;
/*  73:149 */     reset();
/*  74:    */   }
/*  75:    */   
/*  76:    */   public StopwordsHandler[] getStopwords()
/*  77:    */   {
/*  78:158 */     return this.m_Stopwords;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public String stopwordsTipText()
/*  82:    */   {
/*  83:168 */     return "The stopwords algorithms to apply sequentially.";
/*  84:    */   }
/*  85:    */   
/*  86:    */   protected boolean is(String word)
/*  87:    */   {
/*  88:181 */     boolean result = false;
/*  89:183 */     for (StopwordsHandler handler : this.m_Stopwords) {
/*  90:184 */       if (handler.isStopword(word))
/*  91:    */       {
/*  92:185 */         result = true;
/*  93:186 */         break;
/*  94:    */       }
/*  95:    */     }
/*  96:190 */     return result;
/*  97:    */   }
/*  98:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.stopwords.MultiStopwords
 * JD-Core Version:    0.7.0.1
 */