/*   1:    */ package weka.classifiers.bayes.net;
/*   2:    */ 
/*   3:    */ import java.io.FileReader;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.io.Serializable;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.RevisionHandler;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.TechnicalInformation;
/*  13:    */ import weka.core.TechnicalInformation.Field;
/*  14:    */ import weka.core.TechnicalInformation.Type;
/*  15:    */ import weka.core.TechnicalInformationHandler;
/*  16:    */ 
/*  17:    */ public class ADNode
/*  18:    */   implements Serializable, TechnicalInformationHandler, RevisionHandler
/*  19:    */ {
/*  20:    */   static final long serialVersionUID = 397409728366910204L;
/*  21:    */   static final int MIN_RECORD_SIZE = 0;
/*  22:    */   public VaryNode[] m_VaryNodes;
/*  23:    */   public Instance[] m_Instances;
/*  24:    */   public int m_nCount;
/*  25:    */   public int m_nStartNode;
/*  26:    */   
/*  27:    */   public TechnicalInformation getTechnicalInformation()
/*  28:    */   {
/*  29:104 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  30:105 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Andrew W. Moore and Mary S. Lee");
/*  31:106 */     result.setValue(TechnicalInformation.Field.YEAR, "1998");
/*  32:107 */     result.setValue(TechnicalInformation.Field.TITLE, "Cached Sufficient Statistics for Efficient Machine Learning with Large Datasets");
/*  33:    */     
/*  34:    */ 
/*  35:    */ 
/*  36:111 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Journal of Artificial Intelligence Research");
/*  37:    */     
/*  38:113 */     result.setValue(TechnicalInformation.Field.VOLUME, "8");
/*  39:114 */     result.setValue(TechnicalInformation.Field.PAGES, "67-91");
/*  40:    */     
/*  41:116 */     return result;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public static VaryNode makeVaryNode(int iNode, ArrayList<Integer> nRecords, Instances instances)
/*  45:    */   {
/*  46:129 */     VaryNode _VaryNode = new VaryNode(iNode);
/*  47:130 */     int nValues = instances.attribute(iNode).numValues();
/*  48:    */     
/*  49:    */ 
/*  50:    */ 
/*  51:134 */     ArrayList<Integer>[] nChildRecords = new ArrayList[nValues];
/*  52:135 */     for (int iChild = 0; iChild < nValues; iChild++) {
/*  53:136 */       nChildRecords[iChild] = new ArrayList();
/*  54:    */     }
/*  55:139 */     for (int iRecord = 0; iRecord < nRecords.size(); iRecord++)
/*  56:    */     {
/*  57:140 */       int iInstance = ((Integer)nRecords.get(iRecord)).intValue();
/*  58:141 */       nChildRecords[((int)instances.instance(iInstance).value(iNode))].add(new Integer(iInstance));
/*  59:    */     }
/*  60:146 */     int nCount = nChildRecords[0].size();
/*  61:147 */     int nMCV = 0;
/*  62:148 */     for (int iChild = 1; iChild < nValues; iChild++) {
/*  63:149 */       if (nChildRecords[iChild].size() > nCount)
/*  64:    */       {
/*  65:150 */         nCount = nChildRecords[iChild].size();
/*  66:151 */         nMCV = iChild;
/*  67:    */       }
/*  68:    */     }
/*  69:154 */     _VaryNode.m_nMCV = nMCV;
/*  70:    */     
/*  71:    */ 
/*  72:157 */     _VaryNode.m_ADNodes = new ADNode[nValues];
/*  73:158 */     for (int iChild = 0; iChild < nValues; iChild++) {
/*  74:159 */       if ((iChild == nMCV) || (nChildRecords[iChild].size() == 0)) {
/*  75:160 */         _VaryNode.m_ADNodes[iChild] = null;
/*  76:    */       } else {
/*  77:162 */         _VaryNode.m_ADNodes[iChild] = makeADTree(iNode + 1, nChildRecords[iChild], instances);
/*  78:    */       }
/*  79:    */     }
/*  80:166 */     return _VaryNode;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public static ADNode makeADTree(int iNode, ArrayList<Integer> nRecords, Instances instances)
/*  84:    */   {
/*  85:179 */     ADNode _ADNode = new ADNode();
/*  86:180 */     _ADNode.m_nCount = nRecords.size();
/*  87:181 */     _ADNode.m_nStartNode = iNode;
/*  88:182 */     if (nRecords.size() < 0)
/*  89:    */     {
/*  90:183 */       _ADNode.m_Instances = new Instance[nRecords.size()];
/*  91:184 */       for (int iInstance = 0; iInstance < nRecords.size(); iInstance++) {
/*  92:185 */         _ADNode.m_Instances[iInstance] = instances.instance(((Integer)nRecords.get(iInstance)).intValue());
/*  93:    */       }
/*  94:    */     }
/*  95:    */     else
/*  96:    */     {
/*  97:189 */       _ADNode.m_VaryNodes = new VaryNode[instances.numAttributes() - iNode];
/*  98:190 */       for (int iNode2 = iNode; iNode2 < instances.numAttributes(); iNode2++) {
/*  99:191 */         _ADNode.m_VaryNodes[(iNode2 - iNode)] = makeVaryNode(iNode2, nRecords, instances);
/* 100:    */       }
/* 101:    */     }
/* 102:195 */     return _ADNode;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public static ADNode makeADTree(Instances instances)
/* 106:    */   {
/* 107:205 */     ArrayList<Integer> nRecords = new ArrayList(instances.numInstances());
/* 108:207 */     for (int iRecord = 0; iRecord < instances.numInstances(); iRecord++) {
/* 109:208 */       nRecords.add(new Integer(iRecord));
/* 110:    */     }
/* 111:210 */     return makeADTree(0, nRecords, instances);
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void getCounts(int[] nCounts, int[] nNodes, int[] nOffsets, int iNode, int iOffset, boolean bSubstract)
/* 115:    */   {
/* 116:229 */     if (iNode >= nNodes.length)
/* 117:    */     {
/* 118:230 */       if (bSubstract) {
/* 119:231 */         nCounts[iOffset] -= this.m_nCount;
/* 120:    */       } else {
/* 121:233 */         nCounts[iOffset] += this.m_nCount;
/* 122:    */       }
/* 123:235 */       return;
/* 124:    */     }
/* 125:237 */     if (this.m_VaryNodes != null) {
/* 126:238 */       this.m_VaryNodes[(nNodes[iNode] - this.m_nStartNode)].getCounts(nCounts, nNodes, nOffsets, iNode, iOffset, this, bSubstract);
/* 127:    */     } else {
/* 128:241 */       for (Instance instance : this.m_Instances)
/* 129:    */       {
/* 130:242 */         int iOffset2 = iOffset;
/* 131:243 */         for (int iNode2 = iNode; iNode2 < nNodes.length; iNode2++) {
/* 132:244 */           iOffset2 += nOffsets[iNode2] * (int)instance.value(nNodes[iNode2]);
/* 133:    */         }
/* 134:247 */         if (bSubstract) {
/* 135:248 */           nCounts[iOffset2] -= 1;
/* 136:    */         } else {
/* 137:250 */           nCounts[iOffset2] += 1;
/* 138:    */         }
/* 139:    */       }
/* 140:    */     }
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void print()
/* 144:    */   {
/* 145:261 */     String sTab = new String();
/* 146:262 */     for (int i = 0; i < this.m_nStartNode; i++) {
/* 147:263 */       sTab = sTab + "  ";
/* 148:    */     }
/* 149:265 */     System.out.println(sTab + "Count = " + this.m_nCount);
/* 150:266 */     if (this.m_VaryNodes != null) {
/* 151:267 */       for (int iNode = 0; iNode < this.m_VaryNodes.length; iNode++)
/* 152:    */       {
/* 153:268 */         System.out.println(sTab + "Node " + (iNode + this.m_nStartNode));
/* 154:269 */         this.m_VaryNodes[iNode].print(sTab);
/* 155:    */       }
/* 156:    */     } else {
/* 157:272 */       System.out.println(this.m_Instances);
/* 158:    */     }
/* 159:    */   }
/* 160:    */   
/* 161:    */   public static void main(String[] argv)
/* 162:    */   {
/* 163:    */     try
/* 164:    */     {
/* 165:283 */       Instances instances = new Instances(new FileReader("\\iris.2.arff"));
/* 166:284 */       ADNode ADTree = makeADTree(instances);
/* 167:285 */       int[] nCounts = new int[12];
/* 168:286 */       int[] nNodes = new int[3];
/* 169:287 */       int[] nOffsets = new int[3];
/* 170:288 */       nNodes[0] = 0;
/* 171:289 */       nNodes[1] = 3;
/* 172:290 */       nNodes[2] = 4;
/* 173:291 */       nOffsets[0] = 2;
/* 174:292 */       nOffsets[1] = 1;
/* 175:293 */       nOffsets[2] = 4;
/* 176:294 */       ADTree.print();
/* 177:295 */       ADTree.getCounts(nCounts, nNodes, nOffsets, 0, 0, false);
/* 178:    */     }
/* 179:    */     catch (Throwable t)
/* 180:    */     {
/* 181:298 */       t.printStackTrace();
/* 182:    */     }
/* 183:    */   }
/* 184:    */   
/* 185:    */   public String getRevision()
/* 186:    */   {
/* 187:309 */     return RevisionUtils.extract("$Revision: 10153 $");
/* 188:    */   }
/* 189:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.ADNode
 * JD-Core Version:    0.7.0.1
 */