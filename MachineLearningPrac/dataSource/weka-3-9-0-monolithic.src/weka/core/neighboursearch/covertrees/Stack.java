/*   1:    */ package weka.core.neighboursearch.covertrees;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collection;
/*   6:    */ import java.util.List;
/*   7:    */ import weka.core.RevisionHandler;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ 
/*  10:    */ public class Stack<T>
/*  11:    */   implements Serializable, RevisionHandler
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = 5604056321825539264L;
/*  14:    */   public int length;
/*  15:    */   public ArrayList<T> elements;
/*  16:    */   
/*  17:    */   public Stack()
/*  18:    */   {
/*  19: 55 */     this.length = 0;
/*  20: 56 */     this.elements = new ArrayList();
/*  21:    */   }
/*  22:    */   
/*  23:    */   public Stack(int capacity)
/*  24:    */   {
/*  25: 65 */     this.length = 0;
/*  26: 66 */     this.elements = new ArrayList(capacity);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public T last()
/*  30:    */   {
/*  31: 75 */     return this.elements.get(this.length - 1);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public T element(int i)
/*  35:    */   {
/*  36: 85 */     return this.elements.get(i);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void set(int i, T e)
/*  40:    */   {
/*  41: 95 */     this.elements.set(i, e);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public List<T> subList(int beginIdx, int uptoLength)
/*  45:    */   {
/*  46:106 */     return this.elements.subList(beginIdx, uptoLength);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void clear()
/*  50:    */   {
/*  51:111 */     this.elements.clear();
/*  52:112 */     this.length = 0;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void addAll(Collection<? extends T> c)
/*  56:    */   {
/*  57:121 */     this.elements.addAll(c);
/*  58:122 */     this.length = c.size();
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void replaceAllBy(Stack<T> s)
/*  62:    */   {
/*  63:133 */     this.elements.clear();
/*  64:134 */     this.elements.addAll(s.elements);
/*  65:135 */     this.length = this.elements.size();
/*  66:    */   }
/*  67:    */   
/*  68:    */   public T pop()
/*  69:    */   {
/*  70:144 */     this.length -= 1;
/*  71:145 */     return this.elements.remove(this.length);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void push(T new_ele)
/*  75:    */   {
/*  76:154 */     this.length += 1;
/*  77:155 */     this.elements.add(new_ele);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void push(Stack<T> v, T new_ele)
/*  81:    */   {
/*  82:165 */     this.length += 1;
/*  83:166 */     v.elements.add(new_ele);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public String getRevision()
/*  87:    */   {
/*  88:176 */     return RevisionUtils.extract("$Revision: 10203 $");
/*  89:    */   }
/*  90:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.covertrees.Stack
 * JD-Core Version:    0.7.0.1
 */