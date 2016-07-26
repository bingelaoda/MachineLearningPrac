/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.math.BigInteger;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.List;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   7:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   8:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   9:    */ import javax.xml.bind.annotation.XmlElement;
/*  10:    */ import javax.xml.bind.annotation.XmlElements;
/*  11:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  12:    */ import javax.xml.bind.annotation.XmlType;
/*  13:    */ 
/*  14:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  15:    */ @XmlType(name="", propOrder={"extension", "setReference", "extensionAndDelimiterAndTime", "time"})
/*  16:    */ @XmlRootElement(name="Sequence")
/*  17:    */ public class Sequence
/*  18:    */ {
/*  19:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  20:    */   protected List<Extension> extension;
/*  21:    */   @XmlElement(name="SetReference", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  22:    */   protected SetReference setReference;
/*  23:    */   @XmlElements({@XmlElement(name="SetReference", namespace="http://www.dmg.org/PMML-4_1", required=true, type=SetReference.class), @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true, type=Extension.class), @XmlElement(name="Delimiter", namespace="http://www.dmg.org/PMML-4_1", required=true, type=Delimiter.class), @XmlElement(name="Time", namespace="http://www.dmg.org/PMML-4_1", required=true, type=Time.class)})
/*  24:    */   protected List<Object> extensionAndDelimiterAndTime;
/*  25:    */   @XmlElement(name="Time", namespace="http://www.dmg.org/PMML-4_1")
/*  26:    */   protected Time time;
/*  27:    */   @XmlAttribute(required=true)
/*  28:    */   protected String id;
/*  29:    */   @XmlAttribute
/*  30:    */   protected BigInteger numberOfSets;
/*  31:    */   @XmlAttribute
/*  32:    */   protected BigInteger occurrence;
/*  33:    */   @XmlAttribute
/*  34:    */   protected Double support;
/*  35:    */   
/*  36:    */   public List<Extension> getExtension()
/*  37:    */   {
/*  38:108 */     if (this.extension == null) {
/*  39:109 */       this.extension = new ArrayList();
/*  40:    */     }
/*  41:111 */     return this.extension;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public SetReference getSetReference()
/*  45:    */   {
/*  46:123 */     return this.setReference;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setSetReference(SetReference value)
/*  50:    */   {
/*  51:135 */     this.setReference = value;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public List<Object> getExtensionAndDelimiterAndTime()
/*  55:    */   {
/*  56:164 */     if (this.extensionAndDelimiterAndTime == null) {
/*  57:165 */       this.extensionAndDelimiterAndTime = new ArrayList();
/*  58:    */     }
/*  59:167 */     return this.extensionAndDelimiterAndTime;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public Time getTime()
/*  63:    */   {
/*  64:179 */     return this.time;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setTime(Time value)
/*  68:    */   {
/*  69:191 */     this.time = value;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public String getId()
/*  73:    */   {
/*  74:203 */     return this.id;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setId(String value)
/*  78:    */   {
/*  79:215 */     this.id = value;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public BigInteger getNumberOfSets()
/*  83:    */   {
/*  84:227 */     return this.numberOfSets;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void setNumberOfSets(BigInteger value)
/*  88:    */   {
/*  89:239 */     this.numberOfSets = value;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public BigInteger getOccurrence()
/*  93:    */   {
/*  94:251 */     return this.occurrence;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void setOccurrence(BigInteger value)
/*  98:    */   {
/*  99:263 */     this.occurrence = value;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public Double getSupport()
/* 103:    */   {
/* 104:275 */     return this.support;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void setSupport(Double value)
/* 108:    */   {
/* 109:287 */     this.support = value;
/* 110:    */   }
/* 111:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Sequence
 * JD-Core Version:    0.7.0.1
 */