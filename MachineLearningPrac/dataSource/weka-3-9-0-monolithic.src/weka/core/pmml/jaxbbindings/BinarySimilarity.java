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
/*  13:    */ @XmlType(name="", propOrder={"extension"})
/*  14:    */ @XmlRootElement(name="binarySimilarity")
/*  15:    */ public class BinarySimilarity
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlAttribute(name="c00-parameter", required=true)
/*  20:    */   protected double c00Parameter;
/*  21:    */   @XmlAttribute(name="c01-parameter", required=true)
/*  22:    */   protected double c01Parameter;
/*  23:    */   @XmlAttribute(name="c10-parameter", required=true)
/*  24:    */   protected double c10Parameter;
/*  25:    */   @XmlAttribute(name="c11-parameter", required=true)
/*  26:    */   protected double c11Parameter;
/*  27:    */   @XmlAttribute(name="d00-parameter", required=true)
/*  28:    */   protected double d00Parameter;
/*  29:    */   @XmlAttribute(name="d01-parameter", required=true)
/*  30:    */   protected double d01Parameter;
/*  31:    */   @XmlAttribute(name="d10-parameter", required=true)
/*  32:    */   protected double d10Parameter;
/*  33:    */   @XmlAttribute(name="d11-parameter", required=true)
/*  34:    */   protected double d11Parameter;
/*  35:    */   
/*  36:    */   public List<Extension> getExtension()
/*  37:    */   {
/*  38: 99 */     if (this.extension == null) {
/*  39:100 */       this.extension = new ArrayList();
/*  40:    */     }
/*  41:102 */     return this.extension;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public double getC00Parameter()
/*  45:    */   {
/*  46:110 */     return this.c00Parameter;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setC00Parameter(double value)
/*  50:    */   {
/*  51:118 */     this.c00Parameter = value;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public double getC01Parameter()
/*  55:    */   {
/*  56:126 */     return this.c01Parameter;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setC01Parameter(double value)
/*  60:    */   {
/*  61:134 */     this.c01Parameter = value;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public double getC10Parameter()
/*  65:    */   {
/*  66:142 */     return this.c10Parameter;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public void setC10Parameter(double value)
/*  70:    */   {
/*  71:150 */     this.c10Parameter = value;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public double getC11Parameter()
/*  75:    */   {
/*  76:158 */     return this.c11Parameter;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void setC11Parameter(double value)
/*  80:    */   {
/*  81:166 */     this.c11Parameter = value;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public double getD00Parameter()
/*  85:    */   {
/*  86:174 */     return this.d00Parameter;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setD00Parameter(double value)
/*  90:    */   {
/*  91:182 */     this.d00Parameter = value;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public double getD01Parameter()
/*  95:    */   {
/*  96:190 */     return this.d01Parameter;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void setD01Parameter(double value)
/* 100:    */   {
/* 101:198 */     this.d01Parameter = value;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public double getD10Parameter()
/* 105:    */   {
/* 106:206 */     return this.d10Parameter;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void setD10Parameter(double value)
/* 110:    */   {
/* 111:214 */     this.d10Parameter = value;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public double getD11Parameter()
/* 115:    */   {
/* 116:222 */     return this.d11Parameter;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void setD11Parameter(double value)
/* 120:    */   {
/* 121:230 */     this.d11Parameter = value;
/* 122:    */   }
/* 123:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.BinarySimilarity
 * JD-Core Version:    0.7.0.1
 */