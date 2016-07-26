/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlElement;
/*   8:    */ import javax.xml.bind.annotation.XmlRootElement;
/*   9:    */ import javax.xml.bind.annotation.XmlType;
/*  10:    */ 
/*  11:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  12:    */ @XmlType(name="", propOrder={"extension", "xCoordinates", "yCoordinates", "boundaryValues"})
/*  13:    */ @XmlRootElement(name="ROCGraph")
/*  14:    */ public class ROCGraph
/*  15:    */ {
/*  16:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  17:    */   protected List<Extension> extension;
/*  18:    */   @XmlElement(name="XCoordinates", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected XCoordinates xCoordinates;
/*  20:    */   @XmlElement(name="YCoordinates", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  21:    */   protected YCoordinates yCoordinates;
/*  22:    */   @XmlElement(name="BoundaryValues", namespace="http://www.dmg.org/PMML-4_1")
/*  23:    */   protected BoundaryValues boundaryValues;
/*  24:    */   
/*  25:    */   public List<Extension> getExtension()
/*  26:    */   {
/*  27: 86 */     if (this.extension == null) {
/*  28: 87 */       this.extension = new ArrayList();
/*  29:    */     }
/*  30: 89 */     return this.extension;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public XCoordinates getXCoordinates()
/*  34:    */   {
/*  35:101 */     return this.xCoordinates;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void setXCoordinates(XCoordinates value)
/*  39:    */   {
/*  40:113 */     this.xCoordinates = value;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public YCoordinates getYCoordinates()
/*  44:    */   {
/*  45:125 */     return this.yCoordinates;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setYCoordinates(YCoordinates value)
/*  49:    */   {
/*  50:137 */     this.yCoordinates = value;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public BoundaryValues getBoundaryValues()
/*  54:    */   {
/*  55:149 */     return this.boundaryValues;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setBoundaryValues(BoundaryValues value)
/*  59:    */   {
/*  60:161 */     this.boundaryValues = value;
/*  61:    */   }
/*  62:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.ROCGraph
 * JD-Core Version:    0.7.0.1
 */