/*    1:     */ package org.j_paine.formatter;
/*    2:     */ 
/*    3:     */ class DataMissingOnReadException
/*    4:     */   extends InputFormatException
/*    5:     */ {
/*    6:     */   public DataMissingOnReadException(int paramInt, String paramString1, String paramString2)
/*    7:     */   {
/*    8:1550 */     this("Warning: EOL reading formatted data: idx=" + paramInt + " fmt=" + paramString1);
/*    9:     */   }
/*   10:     */   
/*   11:     */   public DataMissingOnReadException(String paramString)
/*   12:     */   {
/*   13:1556 */     super(paramString);
/*   14:     */   }
/*   15:     */   
/*   16:     */   public DataMissingOnReadException() {}
/*   17:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.DataMissingOnReadException
 * JD-Core Version:    0.7.0.1
 */