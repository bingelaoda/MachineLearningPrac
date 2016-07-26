/*    1:     */ package org.j_paine.formatter;
/*    2:     */ 
/*    3:     */ class InvalidNumberOnReadException
/*    4:     */   extends InputFormatException
/*    5:     */ {
/*    6:     */   public InvalidNumberOnReadException(String paramString1, int paramInt, String paramString2, String paramString3, String paramString4)
/*    7:     */   {
/*    8:1575 */     this("Invalid number while reading formatted data:\n  Number = \"" + paramString1 + "\"\n" + "  Index  = " + paramInt + "\n" + "  Format = " + paramString2 + "\n" + paramString3 + "\n" + paramString4);
/*    9:     */   }
/*   10:     */   
/*   11:     */   public InvalidNumberOnReadException(String paramString)
/*   12:     */   {
/*   13:1586 */     super(paramString);
/*   14:     */   }
/*   15:     */   
/*   16:     */   public InvalidNumberOnReadException() {}
/*   17:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.InvalidNumberOnReadException
 * JD-Core Version:    0.7.0.1
 */