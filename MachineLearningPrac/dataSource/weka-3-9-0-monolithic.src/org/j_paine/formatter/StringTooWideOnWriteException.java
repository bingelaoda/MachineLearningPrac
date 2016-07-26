/*    1:     */ package org.j_paine.formatter;
/*    2:     */ 
/*    3:     */ class StringTooWideOnWriteException
/*    4:     */   extends OutputFormatException
/*    5:     */ {
/*    6:     */   public StringTooWideOnWriteException(String paramString1, int paramInt, String paramString2)
/*    7:     */   {
/*    8:1447 */     this("String too wide while writing formatted data:\n  String = \"" + paramString1 + "\"\n" + "  Index  = " + paramInt + "\n" + "  Format = " + paramString2 + " .");
/*    9:     */   }
/*   10:     */   
/*   11:     */   public StringTooWideOnWriteException(String paramString)
/*   12:     */   {
/*   13:1456 */     super(paramString);
/*   14:     */   }
/*   15:     */   
/*   16:     */   public StringTooWideOnWriteException() {}
/*   17:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.StringTooWideOnWriteException
 * JD-Core Version:    0.7.0.1
 */