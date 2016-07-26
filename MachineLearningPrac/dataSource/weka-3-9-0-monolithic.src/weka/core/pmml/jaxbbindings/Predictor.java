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
/*  13:    */ @XmlType(name="", propOrder={"extension", "categories", "matrix"})
/*  14:    */ @XmlRootElement(name="Predictor")
/*  15:    */ public class Predictor
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(name="Categories", namespace="http://www.dmg.org/PMML-4_1")
/*  20:    */   protected Categories categories;
/*  21:    */   @XmlElement(name="Matrix", namespace="http://www.dmg.org/PMML-4_1")
/*  22:    */   protected Matrix matrix;
/*  23:    */   @XmlAttribute
/*  24:    */   protected String contrastMatrixType;
/*  25:    */   @XmlAttribute(required=true)
/*  26:    */   protected String name;
/*  27:    */   
/*  28:    */   public List<Extension> getExtension()
/*  29:    */   {
/*  30: 89 */     if (this.extension == null) {
/*  31: 90 */       this.extension = new ArrayList();
/*  32:    */     }
/*  33: 92 */     return this.extension;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public Categories getCategories()
/*  37:    */   {
/*  38:104 */     return this.categories;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setCategories(Categories value)
/*  42:    */   {
/*  43:116 */     this.categories = value;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public Matrix getMatrix()
/*  47:    */   {
/*  48:128 */     return this.matrix;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setMatrix(Matrix value)
/*  52:    */   {
/*  53:140 */     this.matrix = value;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public String getContrastMatrixType()
/*  57:    */   {
/*  58:152 */     return this.contrastMatrixType;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setContrastMatrixType(String value)
/*  62:    */   {
/*  63:164 */     this.contrastMatrixType = value;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String getName()
/*  67:    */   {
/*  68:176 */     return this.name;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setName(String value)
/*  72:    */   {
/*  73:188 */     this.name = value;
/*  74:    */   }
/*  75:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Predictor
 * JD-Core Version:    0.7.0.1
 */