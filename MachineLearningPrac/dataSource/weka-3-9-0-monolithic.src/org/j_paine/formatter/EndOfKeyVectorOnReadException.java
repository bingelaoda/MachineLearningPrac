/*    1:     */ package org.j_paine.formatter;
/*    2:     */ 
/*    3:     */ class EndOfKeyVectorOnReadException
/*    4:     */   extends InputFormatException
/*    5:     */ {
/*    6:     */   public EndOfKeyVectorOnReadException(int paramInt, String paramString1, String paramString2)
/*    7:     */   {
/*    8:1631 */     this("End of key vector while reading formatted data:\n  Index  = " + paramInt + "\n" + "  Format = " + paramString1 + "\n" + paramString2 + "\n");
/*    9:     */   }
/*   10:     */   
/*   11:     */   public EndOfKeyVectorOnReadException(String paramString)
/*   12:     */   {
/*   13:1640 */     super(paramString);
/*   14:     */   }
/*   15:     */   
/*   16:     */   public EndOfKeyVectorOnReadException() {}
/*   17:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.EndOfKeyVectorOnReadException
 * JD-Core Version:    0.7.0.1
 */