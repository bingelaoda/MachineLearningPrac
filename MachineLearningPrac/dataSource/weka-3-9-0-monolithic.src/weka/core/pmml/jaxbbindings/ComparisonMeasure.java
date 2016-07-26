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
/*  13:    */ @XmlType(name="", propOrder={"extension", "euclidean", "squaredEuclidean", "chebychev", "cityBlock", "minkowski", "simpleMatching", "jaccard", "tanimoto", "binarySimilarity"})
/*  14:    */ @XmlRootElement(name="ComparisonMeasure")
/*  15:    */ public class ComparisonMeasure
/*  16:    */ {
/*  17:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  18:    */   protected List<Extension> extension;
/*  19:    */   @XmlElement(namespace="http://www.dmg.org/PMML-4_1")
/*  20:    */   protected Euclidean euclidean;
/*  21:    */   @XmlElement(namespace="http://www.dmg.org/PMML-4_1")
/*  22:    */   protected SquaredEuclidean squaredEuclidean;
/*  23:    */   @XmlElement(namespace="http://www.dmg.org/PMML-4_1")
/*  24:    */   protected Chebychev chebychev;
/*  25:    */   @XmlElement(namespace="http://www.dmg.org/PMML-4_1")
/*  26:    */   protected CityBlock cityBlock;
/*  27:    */   @XmlElement(namespace="http://www.dmg.org/PMML-4_1")
/*  28:    */   protected Minkowski minkowski;
/*  29:    */   @XmlElement(namespace="http://www.dmg.org/PMML-4_1")
/*  30:    */   protected SimpleMatching simpleMatching;
/*  31:    */   @XmlElement(namespace="http://www.dmg.org/PMML-4_1")
/*  32:    */   protected Jaccard jaccard;
/*  33:    */   @XmlElement(namespace="http://www.dmg.org/PMML-4_1")
/*  34:    */   protected Tanimoto tanimoto;
/*  35:    */   @XmlElement(namespace="http://www.dmg.org/PMML-4_1")
/*  36:    */   protected BinarySimilarity binarySimilarity;
/*  37:    */   @XmlAttribute
/*  38:    */   protected COMPAREFUNCTION compareFunction;
/*  39:    */   @XmlAttribute(required=true)
/*  40:    */   protected String kind;
/*  41:    */   @XmlAttribute
/*  42:    */   protected Double maximum;
/*  43:    */   @XmlAttribute
/*  44:    */   protected Double minimum;
/*  45:    */   
/*  46:    */   public List<Extension> getExtension()
/*  47:    */   {
/*  48:132 */     if (this.extension == null) {
/*  49:133 */       this.extension = new ArrayList();
/*  50:    */     }
/*  51:135 */     return this.extension;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public Euclidean getEuclidean()
/*  55:    */   {
/*  56:147 */     return this.euclidean;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setEuclidean(Euclidean value)
/*  60:    */   {
/*  61:159 */     this.euclidean = value;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public SquaredEuclidean getSquaredEuclidean()
/*  65:    */   {
/*  66:171 */     return this.squaredEuclidean;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public void setSquaredEuclidean(SquaredEuclidean value)
/*  70:    */   {
/*  71:183 */     this.squaredEuclidean = value;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public Chebychev getChebychev()
/*  75:    */   {
/*  76:195 */     return this.chebychev;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void setChebychev(Chebychev value)
/*  80:    */   {
/*  81:207 */     this.chebychev = value;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public CityBlock getCityBlock()
/*  85:    */   {
/*  86:219 */     return this.cityBlock;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setCityBlock(CityBlock value)
/*  90:    */   {
/*  91:231 */     this.cityBlock = value;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public Minkowski getMinkowski()
/*  95:    */   {
/*  96:243 */     return this.minkowski;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void setMinkowski(Minkowski value)
/* 100:    */   {
/* 101:255 */     this.minkowski = value;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public SimpleMatching getSimpleMatching()
/* 105:    */   {
/* 106:267 */     return this.simpleMatching;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void setSimpleMatching(SimpleMatching value)
/* 110:    */   {
/* 111:279 */     this.simpleMatching = value;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public Jaccard getJaccard()
/* 115:    */   {
/* 116:291 */     return this.jaccard;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void setJaccard(Jaccard value)
/* 120:    */   {
/* 121:303 */     this.jaccard = value;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public Tanimoto getTanimoto()
/* 125:    */   {
/* 126:315 */     return this.tanimoto;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void setTanimoto(Tanimoto value)
/* 130:    */   {
/* 131:327 */     this.tanimoto = value;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public BinarySimilarity getBinarySimilarity()
/* 135:    */   {
/* 136:339 */     return this.binarySimilarity;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public void setBinarySimilarity(BinarySimilarity value)
/* 140:    */   {
/* 141:351 */     this.binarySimilarity = value;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public COMPAREFUNCTION getCompareFunction()
/* 145:    */   {
/* 146:363 */     if (this.compareFunction == null) {
/* 147:364 */       return COMPAREFUNCTION.ABS_DIFF;
/* 148:    */     }
/* 149:366 */     return this.compareFunction;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public void setCompareFunction(COMPAREFUNCTION value)
/* 153:    */   {
/* 154:379 */     this.compareFunction = value;
/* 155:    */   }
/* 156:    */   
/* 157:    */   public String getKind()
/* 158:    */   {
/* 159:391 */     return this.kind;
/* 160:    */   }
/* 161:    */   
/* 162:    */   public void setKind(String value)
/* 163:    */   {
/* 164:403 */     this.kind = value;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public Double getMaximum()
/* 168:    */   {
/* 169:415 */     return this.maximum;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public void setMaximum(Double value)
/* 173:    */   {
/* 174:427 */     this.maximum = value;
/* 175:    */   }
/* 176:    */   
/* 177:    */   public Double getMinimum()
/* 178:    */   {
/* 179:439 */     return this.minimum;
/* 180:    */   }
/* 181:    */   
/* 182:    */   public void setMinimum(Double value)
/* 183:    */   {
/* 184:451 */     this.minimum = value;
/* 185:    */   }
/* 186:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.ComparisonMeasure
 * JD-Core Version:    0.7.0.1
 */