/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.io.PrintWriter;
/*   4:    */ import java.io.StringReader;
/*   5:    */ import java.io.StringWriter;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.List;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.WekaException;
/*  11:    */ import weka.gui.Logger;
/*  12:    */ import weka.gui.ProgrammaticProperty;
/*  13:    */ import weka.gui.beans.StreamThroughput;
/*  14:    */ import weka.knowledgeflow.Data;
/*  15:    */ import weka.knowledgeflow.StepManager;
/*  16:    */ import weka.knowledgeflow.StepManagerImpl;
/*  17:    */ 
/*  18:    */ @KFStep(name="DataGenerator", category="DataGenerators", toolTipText="Weka data generator wrapper", iconPath="")
/*  19:    */ public class DataGenerator
/*  20:    */   extends WekaAlgorithmWrapper
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = -7716707145987484527L;
/*  23:    */   protected Data m_incrementalData;
/*  24:    */   protected StreamThroughput m_flowThroughput;
/*  25:    */   
/*  26:    */   public Class getWrappedAlgorithmClass()
/*  27:    */   {
/*  28: 65 */     return weka.datagenerators.DataGenerator.class;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void setWrappedAlgorithm(Object algo)
/*  32:    */   {
/*  33: 75 */     super.setWrappedAlgorithm(algo);
/*  34: 76 */     this.m_defaultIconPath = "weka/gui/knowledgeflow/icons/DefaultDataSource.gif";
/*  35:    */   }
/*  36:    */   
/*  37:    */   public weka.datagenerators.DataGenerator getDataGenerator()
/*  38:    */   {
/*  39: 85 */     return (weka.datagenerators.DataGenerator)getWrappedAlgorithm();
/*  40:    */   }
/*  41:    */   
/*  42:    */   @ProgrammaticProperty
/*  43:    */   public void setDataGenerator(weka.datagenerators.DataGenerator dataGenerator)
/*  44:    */   {
/*  45: 95 */     setWrappedAlgorithm(dataGenerator);
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void stepInit()
/*  49:    */   {
/*  50:103 */     if (getStepManager().numOutgoingConnectionsOfType("instance") > 0)
/*  51:    */     {
/*  52:104 */       this.m_incrementalData = new Data("instance");
/*  53:    */     }
/*  54:    */     else
/*  55:    */     {
/*  56:106 */       this.m_incrementalData = null;
/*  57:107 */       this.m_flowThroughput = null;
/*  58:    */     }
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void start()
/*  62:    */     throws WekaException
/*  63:    */   {
/*  64:118 */     if (getStepManager().numOutgoingConnections() > 0)
/*  65:    */     {
/*  66:119 */       weka.datagenerators.DataGenerator generator = getDataGenerator();
/*  67:120 */       if (getStepManager().numOutgoingConnectionsOfType("dataSet") > 0)
/*  68:    */       {
/*  69:122 */         getStepManager().processing();
/*  70:123 */         StringWriter output = new StringWriter();
/*  71:    */         try
/*  72:    */         {
/*  73:125 */           generator.setOutput(new PrintWriter(output));
/*  74:126 */           getStepManager().statusMessage("Generating...");
/*  75:127 */           getStepManager().logBasic("Generating data");
/*  76:128 */           weka.datagenerators.DataGenerator.makeData(generator, generator.getOptions());
/*  77:    */           
/*  78:130 */           Instances instances = new Instances(new StringReader(output.toString()));
/*  79:133 */           if (!isStopRequested())
/*  80:    */           {
/*  81:134 */             Data outputData = new Data("dataSet", instances);
/*  82:135 */             getStepManager().outputData(new Data[] { outputData });
/*  83:    */           }
/*  84:    */         }
/*  85:    */         catch (Exception ex)
/*  86:    */         {
/*  87:138 */           throw new WekaException(ex);
/*  88:    */         }
/*  89:140 */         if (isStopRequested()) {
/*  90:141 */           getStepManager().interrupted();
/*  91:    */         } else {
/*  92:143 */           getStepManager().finished();
/*  93:    */         }
/*  94:    */       }
/*  95:    */       else
/*  96:    */       {
/*  97:    */         try
/*  98:    */         {
/*  99:148 */           if (!generator.getSingleModeFlag()) {
/* 100:149 */             throw new WekaException("Generator does not support incremental generation, so cannot be used with outgoing 'instance' connections");
/* 101:    */           }
/* 102:    */         }
/* 103:    */         catch (Exception ex)
/* 104:    */         {
/* 105:154 */           throw new WekaException(ex);
/* 106:    */         }
/* 107:156 */         String stm = getName() + "$" + hashCode() + 99 + "| overall flow throughput -|";
/* 108:    */         
/* 109:158 */         this.m_flowThroughput = new StreamThroughput(stm, "Starting flow...", ((StepManagerImpl)getStepManager()).getLog());
/* 110:    */         try
/* 111:    */         {
/* 112:163 */           getStepManager().logBasic("Generating...");
/* 113:164 */           generator.setDatasetFormat(generator.defineDataFormat());
/* 114:166 */           for (int i = 0; i < generator.getNumExamplesAct(); i++)
/* 115:    */           {
/* 116:167 */             getStepManager().throughputUpdateStart();
/* 117:168 */             if (isStopRequested())
/* 118:    */             {
/* 119:169 */               getStepManager().interrupted();
/* 120:170 */               return;
/* 121:    */             }
/* 122:174 */             Instance inst = generator.generateExample();
/* 123:175 */             this.m_incrementalData.setPayloadElement("instance", inst);
/* 124:176 */             getStepManager().throughputUpdateEnd();
/* 125:    */             
/* 126:178 */             getStepManager().outputData(new Data[] { this.m_incrementalData });
/* 127:179 */             this.m_flowThroughput.updateEnd(((StepManagerImpl)getStepManager()).getLog());
/* 128:    */           }
/* 129:183 */           if (isStopRequested())
/* 130:    */           {
/* 131:184 */             ((StepManagerImpl)getStepManager()).getLog().statusMessage(stm + "remove");
/* 132:    */             
/* 133:186 */             getStepManager().interrupted();
/* 134:187 */             return;
/* 135:    */           }
/* 136:189 */           this.m_flowThroughput.finished(((StepManagerImpl)getStepManager()).getLog());
/* 137:    */           
/* 138:    */ 
/* 139:    */ 
/* 140:193 */           this.m_incrementalData.clearPayload();
/* 141:194 */           getStepManager().throughputFinished(new Data[] { this.m_incrementalData });
/* 142:    */         }
/* 143:    */         catch (Exception ex)
/* 144:    */         {
/* 145:196 */           throw new WekaException(ex);
/* 146:    */         }
/* 147:    */       }
/* 148:    */     }
/* 149:    */   }
/* 150:    */   
/* 151:    */   public Instances outputStructureForConnectionType(String connectionName)
/* 152:    */     throws WekaException
/* 153:    */   {
/* 154:215 */     if (getStepManager().isStepBusy()) {
/* 155:216 */       return null;
/* 156:    */     }
/* 157:219 */     weka.datagenerators.DataGenerator generator = getDataGenerator();
/* 158:    */     try
/* 159:    */     {
/* 160:221 */       return generator.defineDataFormat();
/* 161:    */     }
/* 162:    */     catch (Exception ex)
/* 163:    */     {
/* 164:223 */       throw new WekaException(ex);
/* 165:    */     }
/* 166:    */   }
/* 167:    */   
/* 168:    */   public List<String> getIncomingConnectionTypes()
/* 169:    */   {
/* 170:235 */     return null;
/* 171:    */   }
/* 172:    */   
/* 173:    */   public List<String> getOutgoingConnectionTypes()
/* 174:    */   {
/* 175:245 */     List<String> result = new ArrayList();
/* 176:246 */     if (getStepManager().numOutgoingConnections() == 0)
/* 177:    */     {
/* 178:247 */       result.add("dataSet");
/* 179:    */       try
/* 180:    */       {
/* 181:249 */         if (getDataGenerator().getSingleModeFlag()) {
/* 182:250 */           result.add("instance");
/* 183:    */         }
/* 184:    */       }
/* 185:    */       catch (Exception ex)
/* 186:    */       {
/* 187:253 */         ex.printStackTrace();
/* 188:    */       }
/* 189:    */     }
/* 190:255 */     else if (getStepManager().numOutgoingConnectionsOfType("dataSet") > 0)
/* 191:    */     {
/* 192:257 */       result.add("dataSet");
/* 193:    */     }
/* 194:    */     else
/* 195:    */     {
/* 196:259 */       result.add("instance");
/* 197:    */     }
/* 198:262 */     return result;
/* 199:    */   }
/* 200:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.DataGenerator
 * JD-Core Version:    0.7.0.1
 */