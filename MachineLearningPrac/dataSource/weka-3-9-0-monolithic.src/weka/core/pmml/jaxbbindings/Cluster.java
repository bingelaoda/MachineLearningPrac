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
/*  14:    */ @XmlType(name="", propOrder={"extension", "kohonenMap", "array", "partition", "covariances"})
/*  15:    */ @XmlRootElement(name="Cluster")
/*  16:    */ public class Cluster
/*  17:    */ {
/*  18:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected List<Extension> extension;
/*  20:    */   @XmlElement(name="KohonenMap", namespace="http://www.dmg.org/PMML-4_1")
/*  21:    */   protected KohonenMap kohonenMap;
/*  22:    */   @XmlElement(name="Array", namespace="http://www.dmg.org/PMML-4_1")
/*  23:    */   protected ArrayType array;
/*  24:    */   @XmlElement(name="Partition", namespace="http://www.dmg.org/PMML-4_1")
/*  25:    */   protected Partition partition;
/*  26:    */   @XmlElement(name="Covariances", namespace="http://www.dmg.org/PMML-4_1")
/*  27:    */   protected Covariances covariances;
/*  28:    */   @XmlAttribute
/*  29:    */   protected String id;
/*  30:    */   @XmlAttribute
/*  31:    */   protected String name;
/*  32:    */   @XmlAttribute
/*  33:    */   protected BigInteger size;
/*  34:    */   
/*  35:    */   public List<Extension> getExtension()
/*  36:    */   {
/*  37:101 */     if (this.extension == null) {
/*  38:102 */       this.extension = new ArrayList();
/*  39:    */     }
/*  40:104 */     return this.extension;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public KohonenMap getKohonenMap()
/*  44:    */   {
/*  45:116 */     return this.kohonenMap;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setKohonenMap(KohonenMap value)
/*  49:    */   {
/*  50:128 */     this.kohonenMap = value;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public ArrayType getArray()
/*  54:    */   {
/*  55:140 */     return this.array;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setArray(ArrayType value)
/*  59:    */   {
/*  60:152 */     this.array = value;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public Partition getPartition()
/*  64:    */   {
/*  65:164 */     return this.partition;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void setPartition(Partition value)
/*  69:    */   {
/*  70:176 */     this.partition = value;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public Covariances getCovariances()
/*  74:    */   {
/*  75:188 */     return this.covariances;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void setCovariances(Covariances value)
/*  79:    */   {
/*  80:200 */     this.covariances = value;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public String getId()
/*  84:    */   {
/*  85:212 */     return this.id;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void setId(String value)
/*  89:    */   {
/*  90:224 */     this.id = value;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public String getName()
/*  94:    */   {
/*  95:236 */     return this.name;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void setName(String value)
/*  99:    */   {
/* 100:248 */     this.name = value;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public BigInteger getSize()
/* 104:    */   {
/* 105:260 */     return this.size;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void setSize(BigInteger value)
/* 109:    */   {
/* 110:272 */     this.size = value;
/* 111:    */   }
/* 112:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.Cluster
 * JD-Core Version:    0.7.0.1
 */