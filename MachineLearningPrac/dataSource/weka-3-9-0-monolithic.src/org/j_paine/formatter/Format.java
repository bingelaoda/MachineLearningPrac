/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.StringBufferInputStream;
/*   5:    */ import java.util.Vector;
/*   6:    */ 
/*   7:    */ class Format
/*   8:    */   extends FormatUniv
/*   9:    */ {
/*  10:173 */   private Vector elements = new Vector();
/*  11:    */   
/*  12:    */   public Format(String paramString)
/*  13:    */     throws InvalidFormatException
/*  14:    */   {
/*  15:177 */     FormatParser localFormatParser = Parsers.theParsers().format_parser;
/*  16:    */     
/*  17:179 */     FormatParser.ReInit(new StringBufferInputStream(paramString));
/*  18:    */     try
/*  19:    */     {
/*  20:181 */       Format localFormat = FormatParser.Format();
/*  21:182 */       this.elements = localFormat.elements;
/*  22:    */     }
/*  23:    */     catch (ParseException localParseException)
/*  24:    */     {
/*  25:185 */       throw new InvalidFormatException(localParseException.getMessage());
/*  26:    */     }
/*  27:    */     catch (TokenMgrError localTokenMgrError)
/*  28:    */     {
/*  29:188 */       throw new InvalidFormatException(localTokenMgrError.getMessage());
/*  30:    */     }
/*  31:    */   }
/*  32:    */   
/*  33:    */   Format() {}
/*  34:    */   
/*  35:    */   public void addElement(FormatUniv paramFormatUniv)
/*  36:    */   {
/*  37:201 */     this.elements.addElement(paramFormatUniv);
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void write(FormatOutputList paramFormatOutputList, PrintStream paramPrintStream)
/*  41:    */     throws OutputFormatException
/*  42:    */   {
/*  43:208 */     for (int i = 0; i < this.elements.size(); i++)
/*  44:    */     {
/*  45:209 */       FormatUniv localFormatUniv = (FormatUniv)this.elements.elementAt(i);
/*  46:210 */       localFormatUniv.write(paramFormatOutputList, paramPrintStream);
/*  47:    */     }
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void read(FormatInputList paramFormatInputList, InputStreamAndBuffer paramInputStreamAndBuffer, FormatMap paramFormatMap)
/*  51:    */     throws InputFormatException
/*  52:    */   {
/*  53:221 */     for (int i = 0; i < this.elements.size(); i++)
/*  54:    */     {
/*  55:222 */       FormatUniv localFormatUniv = (FormatUniv)this.elements.elementAt(i);
/*  56:223 */       localFormatUniv.read(paramFormatInputList, paramInputStreamAndBuffer, paramFormatMap);
/*  57:    */     }
/*  58:    */   }
/*  59:    */   
/*  60:    */   public String toString()
/*  61:    */   {
/*  62:230 */     String str = "";
/*  63:231 */     for (int i = 0; i < this.elements.size(); i++)
/*  64:    */     {
/*  65:232 */       if (i != 0) {
/*  66:233 */         str = str + ", ";
/*  67:    */       }
/*  68:234 */       str = str + this.elements.elementAt(i).toString();
/*  69:    */     }
/*  70:236 */     return str;
/*  71:    */   }
/*  72:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.Format
 * JD-Core Version:    0.7.0.1
 */