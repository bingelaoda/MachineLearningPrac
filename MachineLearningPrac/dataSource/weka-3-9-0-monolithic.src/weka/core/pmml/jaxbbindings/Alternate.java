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
/*  12:    */ @XmlType(name="", propOrder={"anyDistribution", "gaussianDistribution", "poissonDistribution", "uniformDistribution", "extension"})
/*  13:    */ @XmlRootElement(name="Alternate")
/*  14:    */ public class Alternate
/*  15:    */ {
/*  16:    */   @XmlElement(name="AnyDistribution", namespace="http://www.dmg.org/PMML-4_1")
/*  17:    */   protected AnyDistribution anyDistribution;
/*  18:    */   @XmlElement(name="GaussianDistribution", namespace="http://www.dmg.org/PMML-4_1")
/*  19:    */   protected GaussianDistribution gaussianDistribution;
/*  20:    */   @XmlElement(name="PoissonDistribution", namespace="http://www.dmg.org/PMML-4_1")
/*  21:    */   protected PoissonDistribution poissonDistribution;
/*  22:    */   @XmlElement(name="UniformDistribution", namespace="http://www.dmg.org/PMML-4_1")
/*  23:    */   protected UniformDistribution uniformDistribution;
/*  24:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  25:    */   protected List<Extension> extension;
/*  26:    */   
/*  27:    */   public AnyDistribution getAnyDistribution()
/*  28:    */   {
/*  29: 72 */     return this.anyDistribution;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void setAnyDistribution(AnyDistribution value)
/*  33:    */   {
/*  34: 84 */     this.anyDistribution = value;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public GaussianDistribution getGaussianDistribution()
/*  38:    */   {
/*  39: 96 */     return this.gaussianDistribution;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void setGaussianDistribution(GaussianDistribution value)
/*  43:    */   {
/*  44:108 */     this.gaussianDistribution = value;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public PoissonDistribution getPoissonDistribution()
/*  48:    */   {
/*  49:120 */     return this.poissonDistribution;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setPoissonDistribution(PoissonDistribution value)
/*  53:    */   {
/*  54:132 */     this.poissonDistribution = value;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public UniformDistribution getUniformDistribution()
/*  58:    */   {
/*  59:144 */     return this.uniformDistribution;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setUniformDistribution(UniformDistribution value)
/*  63:    */   {
/*  64:156 */     this.uniformDistribution = value;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public List<Extension> getExtension()
/*  68:    */   {
/*  69:182 */     if (this.extension == null) {
/*  70:183 */       this.extension = new ArrayList();
/*  71:    */     }
/*  72:185 */     return this.extension;
/*  73:    */   }
/*  74:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Alternate
 * JD-Core Version:    0.7.0.1
 */