/*    1:     */ package org.j_paine.formatter;
/*    2:     */ 
/*    3:     */ class IOExceptionOnReadException
/*    4:     */   extends InputFormatException
/*    5:     */ {
/*    6:     */   public IOExceptionOnReadException(String paramString1, int paramInt, String paramString2)
/*    7:     */   {
/*    8:1685 */     this("IOException while reading formatted data:\nLast line was number " + paramInt + ":\n" + paramString1 + "\n" + paramString2);
/*    9:     */   }
/*   10:     */   
/*   11:     */   public IOExceptionOnReadException(String paramString)
/*   12:     */   {
/*   13:1694 */     super(paramString);
/*   14:     */   }
/*   15:     */   
/*   16:     */   public IOExceptionOnReadException() {}
/*   17:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.IOExceptionOnReadException
 * JD-Core Version:    0.7.0.1
 */