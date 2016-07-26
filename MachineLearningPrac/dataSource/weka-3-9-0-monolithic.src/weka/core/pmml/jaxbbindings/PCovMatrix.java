/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   8:    */ import javax.xml.bind.annotation.XmlElement;
/*   9:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  10:    */ import javax.xml.bind.annotation.XmlType;
/*  11:    */ 
/*  12:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  13:    */ @XmlType(name="", propOrder={"extension", "pCovCell"})
/*  14:    */ @XmlRootElement(name="PCovMatrix")
/*  15:    */ public class PCovMatrix
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="PCovCell", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<PCovCell> pCovCell;
/*  21:    */   @XmlAttribute
/*  22:    */   protected String type;
/*  23:    */   
/*  24:    */   public List<Extension> getExtension()
/*  25:    */   {
/*  26: 89 */     if (this.extension == null) {
/*  27: 90 */       this.extension = new ArrayList();
/*  28:    */     }
/*  29: 92 */     return this.extension;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public List<PCovCell> getPCovCell()
/*  33:    */   {
/*  34:118 */     if (this.pCovCell == null) {
/*  35:119 */       this.pCovCell = new ArrayList();
/*  36:    */     }
/*  37:121 */     return this.pCovCell;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public String getType()
/*  41:    */   {
/*  42:133 */     return this.type;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void setType(String value)
/*  46:    */   {
/*  47:145 */     this.type = value;
/*  48:    */   }
/*  49:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.PCovMatrix
 * JD-Core Version:    0.7.0.1
 */