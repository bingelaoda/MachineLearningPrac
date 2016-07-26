/*   1:    */ package weka.classifiers.bayes.net.search.fixed;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.bayes.BayesNet;
/*   7:    */ import weka.classifiers.bayes.net.BIFReader;
/*   8:    */ import weka.classifiers.bayes.net.ParentSet;
/*   9:    */ import weka.classifiers.bayes.net.search.SearchAlgorithm;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.RevisionUtils;
/*  13:    */ import weka.core.Utils;
/*  14:    */ 
/*  15:    */ public class FromFile
/*  16:    */   extends SearchAlgorithm
/*  17:    */ {
/*  18:    */   static final long serialVersionUID = 7334358169507619525L;
/*  19: 61 */   String m_sBIFFile = "";
/*  20:    */   
/*  21:    */   public String globalInfo()
/*  22:    */   {
/*  23: 70 */     return "The FromFile reads the structure of a Bayes net from a file in BIFF format.";
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void buildStructure(BayesNet bayesNet, Instances instances)
/*  27:    */     throws Exception
/*  28:    */   {
/*  29: 84 */     BIFReader bifReader = new BIFReader();
/*  30: 85 */     bifReader.processFile(this.m_sBIFFile);
/*  31: 87 */     for (int iAttribute = 0; iAttribute < instances.numAttributes(); iAttribute++)
/*  32:    */     {
/*  33: 88 */       int iBIFAttribute = bifReader.getNode(bayesNet.getNodeName(iAttribute));
/*  34: 89 */       ParentSet bifParentSet = bifReader.getParentSet(iBIFAttribute);
/*  35: 90 */       for (int iBIFParent = 0; iBIFParent < bifParentSet.getNrOfParents(); iBIFParent++)
/*  36:    */       {
/*  37: 91 */         String sParent = bifReader.getNodeName(bifParentSet.getParent(iBIFParent));
/*  38:    */         
/*  39: 93 */         int iParent = 0;
/*  40: 95 */         while ((iParent < instances.numAttributes()) && (!bayesNet.getNodeName(iParent).equals(sParent))) {
/*  41: 96 */           iParent++;
/*  42:    */         }
/*  43: 98 */         if (iParent >= instances.numAttributes()) {
/*  44: 99 */           throw new Exception("Could not find attribute " + sParent + " from BIF file in data");
/*  45:    */         }
/*  46:102 */         bayesNet.getParentSet(iAttribute).addParent(iParent, instances);
/*  47:    */       }
/*  48:    */     }
/*  49:    */   }
/*  50:    */   
/*  51:    */   public void setBIFFile(String sBIFFile)
/*  52:    */   {
/*  53:113 */     this.m_sBIFFile = sBIFFile;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public String getBIFFile()
/*  57:    */   {
/*  58:122 */     return this.m_sBIFFile;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public Enumeration<Option> listOptions()
/*  62:    */   {
/*  63:132 */     Vector<Option> newVector = new Vector();
/*  64:    */     
/*  65:134 */     newVector.addElement(new Option("\tName of file containing network structure in BIF format\n", "B", 1, "-B <BIF File>"));
/*  66:    */     
/*  67:    */ 
/*  68:    */ 
/*  69:138 */     newVector.addAll(Collections.list(super.listOptions()));
/*  70:    */     
/*  71:140 */     return newVector.elements();
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setOptions(String[] options)
/*  75:    */     throws Exception
/*  76:    */   {
/*  77:162 */     setBIFFile(Utils.getOption('B', options));
/*  78:    */     
/*  79:164 */     super.setOptions(options);
/*  80:    */   }
/*  81:    */   
/*  82:    */   public String[] getOptions()
/*  83:    */   {
/*  84:175 */     Vector<String> options = new Vector();
/*  85:    */     
/*  86:177 */     options.add("-B");
/*  87:178 */     options.add("" + getBIFFile());
/*  88:    */     
/*  89:180 */     Collections.addAll(options, super.getOptions());
/*  90:    */     
/*  91:182 */     return (String[])options.toArray(new String[0]);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public String getRevision()
/*  95:    */   {
/*  96:192 */     return RevisionUtils.extract("$Revision: 10154 $");
/*  97:    */   }
/*  98:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.search.fixed.FromFile
 * JD-Core Version:    0.7.0.1
 */