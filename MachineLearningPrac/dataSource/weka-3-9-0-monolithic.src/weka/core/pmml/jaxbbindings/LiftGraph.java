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
/*  12:    */ @XmlType(name="", propOrder={"extension", "xCoordinates", "yCoordinates", "boundaryValues", "boundaryValueMeans"})
/*  13:    */ @XmlRootElement(name="LiftGraph")
/*  14:    */ public class LiftGraph
/*  15:    */ {
/*  16:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  17:    */   protected List<Extension> extension;
/*  18:    */   @XmlElement(name="XCoordinates", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected XCoordinates xCoordinates;
/*  20:    */   @XmlElement(name="YCoordinates", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  21:    */   protected YCoordinates yCoordinates;
/*  22:    */   @XmlElement(name="BoundaryValues", namespace="http://www.dmg.org/PMML-4_1")
/*  23:    */   protected BoundaryValues boundaryValues;
/*  24:    */   @XmlElement(name="BoundaryValueMeans", namespace="http://www.dmg.org/PMML-4_1")
/*  25:    */   protected BoundaryValueMeans boundaryValueMeans;
/*  26:    */   
/*  27:    */   public List<Extension> getExtension()
/*  28:    */   {
/*  29: 90 */     if (this.extension == null) {
/*  30: 91 */       this.extension = new ArrayList();
/*  31:    */     }
/*  32: 93 */     return this.extension;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public XCoordinates getXCoordinates()
/*  36:    */   {
/*  37:105 */     return this.xCoordinates;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void setXCoordinates(XCoordinates value)
/*  41:    */   {
/*  42:117 */     this.xCoordinates = value;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public YCoordinates getYCoordinates()
/*  46:    */   {
/*  47:129 */     return this.yCoordinates;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setYCoordinates(YCoordinates value)
/*  51:    */   {
/*  52:141 */     this.yCoordinates = value;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public BoundaryValues getBoundaryValues()
/*  56:    */   {
/*  57:153 */     return this.boundaryValues;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void setBoundaryValues(BoundaryValues value)
/*  61:    */   {
/*  62:165 */     this.boundaryValues = value;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public BoundaryValueMeans getBoundaryValueMeans()
/*  66:    */   {
/*  67:177 */     return this.boundaryValueMeans;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public void setBoundaryValueMeans(BoundaryValueMeans value)
/*  71:    */   {
/*  72:189 */     this.boundaryValueMeans = value;
/*  73:    */   }
/*  74:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.LiftGraph
 * JD-Core Version:    0.7.0.1
 */