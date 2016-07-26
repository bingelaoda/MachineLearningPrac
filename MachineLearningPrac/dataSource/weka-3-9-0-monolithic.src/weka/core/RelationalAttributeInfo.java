/*  1:   */ package weka.core;
/*  2:   */ 
/*  3:   */ public class RelationalAttributeInfo
/*  4:   */   extends NominalAttributeInfo
/*  5:   */ {
/*  6:   */   protected Instances m_Header;
/*  7:   */   
/*  8:   */   public RelationalAttributeInfo(Instances header)
/*  9:   */   {
/* 10:36 */     super(null, null);
/* 11:37 */     this.m_Header = header;
/* 12:   */   }
/* 13:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.RelationalAttributeInfo
 * JD-Core Version:    0.7.0.1
 */