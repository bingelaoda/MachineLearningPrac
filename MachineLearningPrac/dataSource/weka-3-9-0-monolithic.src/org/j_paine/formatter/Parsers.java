/*    1:     */ package org.j_paine.formatter;
/*    2:     */ 
/*    3:     */ import java.io.StringBufferInputStream;
/*    4:     */ 
/*    5:     */ class Parsers
/*    6:     */ {
/*    7:1729 */   static boolean already_created = false;
/*    8:1730 */   static Parsers parsers = null;
/*    9:1732 */   FormatParser format_parser = null;
/*   10:1733 */   NumberParser number_parser = null;
/*   11:     */   
/*   12:     */   static Parsers theParsers()
/*   13:     */   {
/*   14:1738 */     if (!already_created)
/*   15:     */     {
/*   16:1739 */       parsers = new Parsers();
/*   17:1740 */       already_created = true;
/*   18:     */     }
/*   19:1742 */     return parsers;
/*   20:     */   }
/*   21:     */   
/*   22:     */   private Parsers()
/*   23:     */   {
/*   24:1748 */     this.format_parser = new FormatParser(new StringBufferInputStream(""));
/*   25:1749 */     this.number_parser = new NumberParser(new StringBufferInputStream(""));
/*   26:     */   }
/*   27:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.Parsers
 * JD-Core Version:    0.7.0.1
 */