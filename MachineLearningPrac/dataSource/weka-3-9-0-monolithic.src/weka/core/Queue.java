/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ 
/*   6:    */ public class Queue
/*   7:    */   implements Serializable, RevisionHandler
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = -1141282001146389780L;
/*  10:    */   
/*  11:    */   protected class QueueNode
/*  12:    */     implements Serializable, RevisionHandler
/*  13:    */   {
/*  14:    */     private static final long serialVersionUID = -5119358279412097455L;
/*  15:    */     protected QueueNode m_Next;
/*  16:    */     protected Object m_Contents;
/*  17:    */     
/*  18:    */     public QueueNode(Object contents)
/*  19:    */     {
/*  20: 68 */       this.m_Contents = contents;
/*  21: 69 */       next(null);
/*  22:    */     }
/*  23:    */     
/*  24:    */     public QueueNode next(QueueNode next)
/*  25:    */     {
/*  26: 79 */       return this.m_Next = next;
/*  27:    */     }
/*  28:    */     
/*  29:    */     public QueueNode next()
/*  30:    */     {
/*  31: 87 */       return this.m_Next;
/*  32:    */     }
/*  33:    */     
/*  34:    */     public Object contents(Object contents)
/*  35:    */     {
/*  36: 97 */       return this.m_Contents = contents;
/*  37:    */     }
/*  38:    */     
/*  39:    */     public Object contents()
/*  40:    */     {
/*  41:105 */       return this.m_Contents;
/*  42:    */     }
/*  43:    */     
/*  44:    */     public String getRevision()
/*  45:    */     {
/*  46:114 */       return RevisionUtils.extract("$Revision: 8034 $");
/*  47:    */     }
/*  48:    */   }
/*  49:    */   
/*  50:119 */   protected QueueNode m_Head = null;
/*  51:122 */   protected QueueNode m_Tail = null;
/*  52:125 */   protected int m_Size = 0;
/*  53:    */   
/*  54:    */   public final synchronized void removeAllElements()
/*  55:    */   {
/*  56:145 */     this.m_Size = 0;
/*  57:146 */     this.m_Head = null;
/*  58:147 */     this.m_Tail = null;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public synchronized Object push(Object item)
/*  62:    */   {
/*  63:166 */     QueueNode newNode = new QueueNode(item);
/*  64:168 */     if (this.m_Head == null) {
/*  65:169 */       this.m_Head = (this.m_Tail = newNode);
/*  66:    */     } else {
/*  67:171 */       this.m_Tail = this.m_Tail.next(newNode);
/*  68:    */     }
/*  69:173 */     this.m_Size += 1;
/*  70:174 */     return item;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public synchronized Object pop()
/*  74:    */     throws RuntimeException
/*  75:    */   {
/*  76:192 */     if (this.m_Head == null) {
/*  77:193 */       throw new RuntimeException("Queue is empty");
/*  78:    */     }
/*  79:195 */     Object retval = this.m_Head.contents();
/*  80:196 */     this.m_Size -= 1;
/*  81:197 */     this.m_Head = this.m_Head.next();
/*  82:204 */     if (this.m_Head == null) {
/*  83:205 */       this.m_Tail = null;
/*  84:    */     }
/*  85:207 */     return retval;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public synchronized Object peek()
/*  89:    */     throws RuntimeException
/*  90:    */   {
/*  91:221 */     if (this.m_Head == null) {
/*  92:222 */       throw new RuntimeException("Queue is empty");
/*  93:    */     }
/*  94:224 */     return this.m_Head.contents();
/*  95:    */   }
/*  96:    */   
/*  97:    */   public boolean empty()
/*  98:    */   {
/*  99:234 */     return this.m_Head == null;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public int size()
/* 103:    */   {
/* 104:244 */     return this.m_Size;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public String toString()
/* 108:    */   {
/* 109:257 */     String retval = "Queue Contents " + this.m_Size + " elements\n";
/* 110:258 */     QueueNode current = this.m_Head;
/* 111:259 */     if (current == null) {
/* 112:260 */       return retval + "Empty\n";
/* 113:    */     }
/* 114:262 */     while (current != null)
/* 115:    */     {
/* 116:263 */       retval = retval + current.contents().toString() + "\n";
/* 117:264 */       current = current.next();
/* 118:    */     }
/* 119:267 */     return retval;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public String getRevision()
/* 123:    */   {
/* 124:276 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 125:    */   }
/* 126:    */   
/* 127:    */   public static void main(String[] argv)
/* 128:    */   {
/* 129:    */     try
/* 130:    */     {
/* 131:290 */       Queue queue = new Queue();
/* 132:291 */       for (int i = 0; i < argv.length; i++) {
/* 133:292 */         queue.push(argv[i]);
/* 134:    */       }
/* 135:294 */       System.out.println("After pushing command line arguments");
/* 136:295 */       System.out.println(queue.toString());
/* 137:296 */       while (!queue.empty()) {
/* 138:297 */         System.out.println("Pop: " + queue.pop().toString());
/* 139:    */       }
/* 140:    */       try
/* 141:    */       {
/* 142:302 */         queue.pop();
/* 143:303 */         System.out.println("ERROR: pop did not throw exception!");
/* 144:    */       }
/* 145:    */       catch (RuntimeException ex)
/* 146:    */       {
/* 147:307 */         System.out.println("Pop on empty queue correctly gave exception.");
/* 148:    */       }
/* 149:    */     }
/* 150:    */     catch (Exception ex)
/* 151:    */     {
/* 152:310 */       System.out.println(ex.getMessage());
/* 153:    */     }
/* 154:    */   }
/* 155:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Queue
 * JD-Core Version:    0.7.0.1
 */