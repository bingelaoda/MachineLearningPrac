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
/*  12:    */ @XmlType(name="", propOrder={"extension", "classLabels", "matrix"})
/*  13:    */ @XmlRootElement(name="ConfusionMatrix")
/*  14:    */ public class ConfusionMatrix
/*  15:    */ {
/*  16:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  17:    */   protected List<Extension> extension;
/*  18:    */   @XmlElement(name="ClassLabels", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected ClassLabels classLabels;
/*  20:    */   @XmlElement(name="Matrix", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  21:    */   protected Matrix matrix;
/*  22:    */   
/*  23:    */   public List<Extension> getExtension()
/*  24:    */   {
/*  25: 82 */     if (this.extension == null) {
/*  26: 83 */       this.extension = new ArrayList();
/*  27:    */     }
/*  28: 85 */     return this.extension;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public ClassLabels getClassLabels()
/*  32:    */   {
/*  33: 97 */     return this.classLabels;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void setClassLabels(ClassLabels value)
/*  37:    */   {
/*  38:109 */     this.classLabels = value;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public Matrix getMatrix()
/*  42:    */   {
/*  43:121 */     return this.matrix;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setMatrix(Matrix value)
/*  47:    */   {
/*  48:133 */     this.matrix = value;
/*  49:    */   }
/*  50:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.ConfusionMatrix
 * JD-Core Version:    0.7.0.1
 */