/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigInteger;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.List;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   7:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   8:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   9:    */ import javax.xml.bind.annotation.XmlElement;
/*  10:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  11:    */ import javax.xml.bind.annotation.XmlType;
/*  12:    */ 
/*  13:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  14:    */ @XmlType(name="", propOrder={"extension", "neuron"})
/*  15:    */ @XmlRootElement(name="NeuralLayer")
/*  16:    */ public class NeuralLayer
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlElement(name="Neuron", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  21:    */   protected List<Neuron> neuron;
/*  22:    */   @XmlAttribute
/*  23:    */   protected ACTIVATIONFUNCTION activationFunction;
/*  24:    */   @XmlAttribute
/*  25:    */   protected Double altitude;
/*  26:    */   @XmlAttribute
/*  27:    */   protected NNNORMALIZATIONMETHOD normalizationMethod;
/*  28:    */   @XmlAttribute
/*  29:    */   protected BigInteger numberOfNeurons;
/*  30:    */   @XmlAttribute
/*  31:    */   protected Double threshold;
/*  32:    */   @XmlAttribute
/*  33:    */   protected Double width;
/*  34:    */   
/*  35:    */   public List<Extension> getExtension()
/*  36:    */   {
/*  37: 98 */     if (this.extension == null) {
/*  38: 99 */       this.extension = new ArrayList();
/*  39:    */     }
/*  40:101 */     return this.extension;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public List<Neuron> getNeuron()
/*  44:    */   {
/*  45:127 */     if (this.neuron == null) {
/*  46:128 */       this.neuron = new ArrayList();
/*  47:    */     }
/*  48:130 */     return this.neuron;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public ACTIVATIONFUNCTION getActivationFunction()
/*  52:    */   {
/*  53:142 */     return this.activationFunction;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void setActivationFunction(ACTIVATIONFUNCTION value)
/*  57:    */   {
/*  58:154 */     this.activationFunction = value;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public Double getAltitude()
/*  62:    */   {
/*  63:166 */     return this.altitude;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void setAltitude(Double value)
/*  67:    */   {
/*  68:178 */     this.altitude = value;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public NNNORMALIZATIONMETHOD getNormalizationMethod()
/*  72:    */   {
/*  73:190 */     return this.normalizationMethod;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setNormalizationMethod(NNNORMALIZATIONMETHOD value)
/*  77:    */   {
/*  78:202 */     this.normalizationMethod = value;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public BigInteger getNumberOfNeurons()
/*  82:    */   {
/*  83:214 */     return this.numberOfNeurons;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setNumberOfNeurons(BigInteger value)
/*  87:    */   {
/*  88:226 */     this.numberOfNeurons = value;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public Double getThreshold()
/*  92:    */   {
/*  93:238 */     return this.threshold;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void setThreshold(Double value)
/*  97:    */   {
/*  98:250 */     this.threshold = value;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public Double getWidth()
/* 102:    */   {
/* 103:262 */     return this.width;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public void setWidth(Double value)
/* 107:    */   {
/* 108:274 */     this.width = value;
/* 109:    */   }
/* 110:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.NeuralLayer
 * JD-Core Version:    0.7.0.1
 */