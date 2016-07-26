/*   1:    */ package weka.core.pmml.jaxbbindings;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import javax.xml.bind.annotation.XmlAccessType;
/*   6:    */ import javax.xml.bind.annotation.XmlAccessorType;
/*   7:    */ import javax.xml.bind.annotation.XmlAttribute;
/*   8:    */ import javax.xml.bind.annotation.XmlElement;
/*   9:    */ import javax.xml.bind.annotation.XmlElements;
/*  10:    */ import javax.xml.bind.annotation.XmlRootElement;
/*  11:    */ import javax.xml.bind.annotation.XmlType;
/*  12:    */ 
/*  13:    */ @XmlAccessorType(XmlAccessType.FIELD)
/*  14:    */ @XmlType(name="", propOrder={"header", "miningBuildTask", "dataDictionary", "transformationDictionary", "associationModelOrBaselineModelOrClusteringModel", "extension"})
/*  15:    */ @XmlRootElement(name="PMML")
/*  16:    */ public class PMML
/*  17:    */ {
/*  18:    */   @XmlElement(name="Header", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  19:    */   protected Header header;
/*  20:    */   @XmlElement(name="MiningBuildTask", namespace="http://www.dmg.org/PMML-4_1")
/*  21:    */   protected MiningBuildTask miningBuildTask;
/*  22:    */   @XmlElement(name="DataDictionary", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  23:    */   protected DataDictionary dataDictionary;
/*  24:    */   @XmlElement(name="TransformationDictionary", namespace="http://www.dmg.org/PMML-4_1")
/*  25:    */   protected TransformationDictionary transformationDictionary;
/*  26:    */   @XmlElements({@XmlElement(name="TreeModel", namespace="http://www.dmg.org/PMML-4_1", required=true, type=TreeModel.class), @XmlElement(name="AssociationModel", namespace="http://www.dmg.org/PMML-4_1", required=true, type=AssociationModel.class), @XmlElement(name="SupportVectorMachineModel", namespace="http://www.dmg.org/PMML-4_1", required=true, type=SupportVectorMachineModel.class), @XmlElement(name="TimeSeriesModel", namespace="http://www.dmg.org/PMML-4_1", required=true, type=TimeSeriesModel.class), @XmlElement(name="ClusteringModel", namespace="http://www.dmg.org/PMML-4_1", required=true, type=ClusteringModel.class), @XmlElement(name="TextModel", namespace="http://www.dmg.org/PMML-4_1", required=true, type=TextModel.class), @XmlElement(name="NeuralNetwork", namespace="http://www.dmg.org/PMML-4_1", required=true, type=NeuralNetwork.class), @XmlElement(name="BaselineModel", namespace="http://www.dmg.org/PMML-4_1", required=true, type=BaselineModel.class), @XmlElement(name="GeneralRegressionModel", namespace="http://www.dmg.org/PMML-4_1", required=true, type=GeneralRegressionModel.class), @XmlElement(name="NearestNeighborModel", namespace="http://www.dmg.org/PMML-4_1", required=true, type=NearestNeighborModel.class), @XmlElement(name="NaiveBayesModel", namespace="http://www.dmg.org/PMML-4_1", required=true, type=NaiveBayesModel.class), @XmlElement(name="MiningModel", namespace="http://www.dmg.org/PMML-4_1", required=true, type=MiningModel.class), @XmlElement(name="SequenceModel", namespace="http://www.dmg.org/PMML-4_1", required=true, type=SequenceModel.class), @XmlElement(name="Scorecard", namespace="http://www.dmg.org/PMML-4_1", required=true, type=Scorecard.class), @XmlElement(name="RuleSetModel", namespace="http://www.dmg.org/PMML-4_1", required=true, type=RuleSetModel.class), @XmlElement(name="RegressionModel", namespace="http://www.dmg.org/PMML-4_1", required=true, type=RegressionModel.class)})
/*  27:    */   protected List<Object> associationModelOrBaselineModelOrClusteringModel;
/*  28:    */   @XmlElement(name="Extension", namespace="http://www.dmg.org/PMML-4_1", required=true)
/*  29:    */   protected List<Extension> extension;
/*  30:    */   @XmlAttribute(required=true)
/*  31:    */   protected String version;
/*  32:    */   
/*  33:    */   public Header getHeader()
/*  34:    */   {
/*  35:104 */     return this.header;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void setHeader(Header value)
/*  39:    */   {
/*  40:116 */     this.header = value;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public MiningBuildTask getMiningBuildTask()
/*  44:    */   {
/*  45:128 */     return this.miningBuildTask;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setMiningBuildTask(MiningBuildTask value)
/*  49:    */   {
/*  50:140 */     this.miningBuildTask = value;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public DataDictionary getDataDictionary()
/*  54:    */   {
/*  55:152 */     return this.dataDictionary;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setDataDictionary(DataDictionary value)
/*  59:    */   {
/*  60:164 */     this.dataDictionary = value;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public TransformationDictionary getTransformationDictionary()
/*  64:    */   {
/*  65:176 */     return this.transformationDictionary;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void setTransformationDictionary(TransformationDictionary value)
/*  69:    */   {
/*  70:188 */     this.transformationDictionary = value;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public List<Object> getAssociationModelOrBaselineModelOrClusteringModels()
/*  74:    */   {
/*  75:229 */     if (this.associationModelOrBaselineModelOrClusteringModel == null) {
/*  76:230 */       this.associationModelOrBaselineModelOrClusteringModel = new ArrayList();
/*  77:    */     }
/*  78:232 */     return this.associationModelOrBaselineModelOrClusteringModel;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void addAssociationModelOrBaselineModelOrClusteringModes(Object object)
/*  82:    */   {
/*  83:236 */     if (this.associationModelOrBaselineModelOrClusteringModel == null) {
/*  84:237 */       this.associationModelOrBaselineModelOrClusteringModel = new ArrayList();
/*  85:    */     }
/*  86:239 */     this.associationModelOrBaselineModelOrClusteringModel.add(object);
/*  87:    */   }
/*  88:    */   
/*  89:    */   public List<Extension> getExtension()
/*  90:    */   {
/*  91:265 */     if (this.extension == null) {
/*  92:266 */       this.extension = new ArrayList();
/*  93:    */     }
/*  94:268 */     return this.extension;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public String getVersion()
/*  98:    */   {
/*  99:280 */     return this.version;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void setVersion(String value)
/* 103:    */   {
/* 104:292 */     this.version = value;
/* 105:    */   }
/* 106:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.PMML
 * JD-Core Version:    0.7.0.1
 */