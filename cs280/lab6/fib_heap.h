// fib_heap.h

#ifndef CS280_FIB_HEAP_H
#define CS280_FIB_HEAP_H

template <class Type>
class HeapNode;

template<class Type>
class FibHeap {
public:
  // Constructors
  FibHeap();
  ~FibHeap();
  
  // Inspectors
  inline bool empty() const {return s==0;}
  inline unsigned size() const {return s;}
  const Type& getValue(HeapNode<Type>* p) const;
  const Type& top() const;

  // mutators
  HeapNode<Type>* push(const Type& info);
  void pop();
  void decreaseKey(HeapNode<Type>* h, const Type& info);
  void clear();
  void heapUnion(FibHeap& H1, FibHeap& H2);  // Distorys contents of H1 and H2

  bool checkHeap();  // Ensure the heap is in legal form.
                     // For debugging.

private:
  void consolidate();
  void cutNode(HeapNode<Type>* x, HeapNode<Type>* y);
  void cascadingCut(HeapNode<Type>* x);

  HeapNode<Type>* minNode;
  unsigned s;
};

/*************************************************************************/

#include <math.h>
#include <stack>
#include <vector>
#include <iostream>
#include <assert.h>
#include <stdlib.h>

using std::vector;
using std::stack;
using std::cerr;
using std::endl;

template<class Type>
class HeapNode {
  friend class FibHeap<Type>;
  friend void insertNode<Type>(HeapNode<Type>*,HeapNode<Type>*);
  friend void removeNode<Type>(HeapNode<Type>* h);
  friend void HeapLink<Type>(HeapNode<Type>*, HeapNode<Type>*);

  HeapNode(const Type& info);

private:
  HeapNode<Type>* right;
  HeapNode<Type>* left;
  HeapNode<Type>* firstChild;
  HeapNode<Type>* parent;
  Type info;
  bool mark;
  unsigned degree;
  bool used;
};

template<class Type>
HeapNode<Type>::HeapNode(const Type& info)
{
  left = right = this;
  firstChild = parent = NULL;
  degree = 0;
  mark = false;
  this->info = info;
  used = true;
}

template<class Type>
void insertNode(HeapNode<Type>* h1, HeapNode<Type>* h2)
{
  h2->left = h1->left;
  h1->left = h2;
  h2->right = h1;
  h2->left->right = h2;
}

template<class Type>
void removeNode(HeapNode<Type>* h)
{
  if (h->left == h) {
    cerr << "Problem in removeNode method.  Please tell creator." << endl;
    exit(1);
  }
  h->right->left = h->left;
  h->left->right = h->right;
  h->left = h->right = h;
}

template<class Type>
void HeapLink(HeapNode<Type>* x, HeapNode<Type>* y)
{
  if ((x->info > y->info) || (y->left != y)) {
    cerr << "Problem in HealpLink method.  Please tell creator." << endl;
    exit(1);
  }
  y->parent = x;
  if (x->firstChild==NULL)
    x->firstChild = y;
  else {
    insertNode(x->firstChild, y);
    if (y->info < x->info)
      x->firstChild = x;
  }
  y->mark = false;
  x->degree = x->degree+1;
}

template<class Type>
FibHeap<Type>::FibHeap()
{
  minNode = NULL;
  s = 0;
}

template<class Type>
FibHeap<Type>::~FibHeap()
{
  clear();
}

template<class Type>
void FibHeap<Type>::clear()
{
  minNode = NULL;
  s = 0;
}

template<class Type>
const Type& FibHeap<Type>::getValue(HeapNode<Type>* p) const
{
  return p->info;
}

template<class Type>
const Type& FibHeap<Type>::top() const
{
  return minNode->info;
}

template<class Type>
HeapNode<Type>* FibHeap<Type>::push(const Type& info)
{
  HeapNode<Type>* x = new HeapNode<Type>(info);
  if (minNode == NULL) {
    minNode = x;
    s = 1;
  }
  else {
    insertNode(minNode, x);
    if (x->info < minNode->info)
      minNode = x;
    s++;
  }

  return x;
}

template<class Type>
void FibHeap<Type>::pop()
{
  if (minNode == NULL) {
    cerr << "Can't pop a node from an empty heap." << endl;
    exit(1);
  }
  HeapNode<Type>* z = minNode;

  if (z->degree > 0) {
    HeapNode<Type>* firstChild = z->firstChild;
    HeapNode<Type>* lastNode = firstChild->right;
    HeapNode<Type>* p = firstChild;

    for (unsigned i=0; i < z->degree; i++) {
      p->parent = NULL;
      p = p->right;
    }

    firstChild->right = minNode->right;
    minNode->right->left = firstChild;
    lastNode->left = minNode;
    minNode->right = lastNode;
  }
  if (z == z->right) {
    z->used = false;
    s = 0;
    minNode = NULL;
  }
  else {
    minNode = z->left;
    removeNode(z);
    z->firstChild = NULL;
    z->used = false;
    s--;
    consolidate();
  }

}

    
template<class Type>
void FibHeap<Type>::heapUnion(FibHeap<Type>& H1, FibHeap<Type>& H2)
{
  clear();
  HeapNode<Type>* r1 = H1.minNode;
  HeapNode<Type>* r2 = H2.minNode;

  if (r1==NULL) {
    minNode = r2;
    s = H2.s;
  }
  else if (r2==NULL) {
    minNode = r1;
    s = H1.s;
  }
  else {
    HeapNode<Type>* t1 = r1->left;
    HeapNode<Type>* t2 = r2->left;

    r2->left = t1;
    t1->right = r2;
    r1->left = t2;
    t2->right = r1;
    s = H1.s + H2.s;

    if (r1->info < r2->info)
      minNode = r1;
    else
      minNode = r2;
  }

  H1.minNode = NULL;
  H1.s = 0;

  H2.minNode = NULL;
  H2.s = 0;
}

template<class Type>
void FibHeap<Type>::decreaseKey(HeapNode<Type>* x, const Type& info)
{
  if (info > x->info) {
    cerr << "Can't increase a key for a min. fib. heap." << endl;
    exit(1);
  }

  if (x->used == false) {
    cerr << "Can't decrease a node that has already been popped." << endl;
    exit(1);
  }

  x->info = info;
  HeapNode<Type>* y = x->parent;
  if ((y!=NULL) && (x->info < y->info)) {
    cutNode(x,y);
    cascadingCut(y);
  }

  if (x->info < minNode->info)
    minNode = x;
}

template<class Type>
void FibHeap<Type>::consolidate()
{
  if (minNode == NULL)
    return;

  unsigned maxDegree = 0;

  vector<HeapNode<Type>*> D(1);
  HeapNode<Type>* x = minNode;
  bool done = false;
  while (!done) {
    x = minNode;
    if (x->left != x) {
      minNode = minNode->left;
      removeNode(x);
    }
    else {
      done = true;
    }

    unsigned d = x->degree;
    if (D.size() < d+1)
      D.resize((int)pow((double)2, (int)ceil(log10((double)(d+1))/log10((double)2))));

    while (D[d] != NULL) {
      maxDegree = max(maxDegree, d);
      HeapNode<Type>* y = D[d];
      if (y->info < x->info)
	swap(x,y);
      HeapLink(x,y);

      D[d] = NULL;
      d++;
      if (D.size() < d+1)
	D.resize((int)pow((double)2, (int) ceil(log10((double)(d+1))/log10((double)2))));
    } 
    maxDegree = max(maxDegree, d);   
    D[d] = x;
  }

  unsigned i=0;
  while (D[i]==NULL) i++;
  minNode = D[i];
  i++;
  while (i <= maxDegree) {
    if (D[i] != NULL) {
      insertNode(minNode, D[i]);
      if (D[i]->info < minNode->info)
	minNode = D[i];
    }
    i++;
  }
}

template<class Type>
void FibHeap<Type>::cutNode(HeapNode<Type>* x, HeapNode<Type>* y)
{
  if (x->parent != y) {
    cerr << "Problem with cutNode method.  Please report." << endl;
    exit(1);
  }
  if (x->left == x) 
    y->firstChild = NULL;
  else {
    if (y->firstChild == x)
      y->firstChild = x->left;
    removeNode(x);
  }

  x->parent = NULL;
  y->degree = y->degree--;
  insertNode(minNode, x);
  x->mark = false;
}

template<class Type>
void FibHeap<Type>::cascadingCut(HeapNode<Type>* x)
{
  HeapNode<Type>* y = x;
  while (y->parent != NULL) {
    HeapNode<Type>* z = y->parent;
    if (!y->mark) {
      y->mark = true;
      break;
    }
    else {
      cutNode(y,z);
      y = z;
    }
  }
}
      
template<class Type>
bool FibHeap<Type>::checkHeap()
{
  stack<HeapNode<Type>*> S;
  if (minNode == NULL) {
    assert(s==0);
    return true;
  }

  unsigned count = 0;
  Type minValue = minNode->info;

  HeapNode<Type>* q = minNode;
  do {
    assert(q->parent==NULL);
    assert(q->used==true);
    assert(q->right->left==q);
    assert(!(q->info < minNode->info));
    S.push(q);
    count++;
    q = q->right;
  } while (q != minNode);

  while (!S.empty()) {
    HeapNode<Type>* node = S.top();
    S.pop();
    if (node->firstChild==NULL)
      assert(node->degree==0);
    else {
      assert(node->degree > 0);
      HeapNode<Type>* p = node->firstChild;
      unsigned degree=0;
      do {
	assert(p->parent==node);
	assert(p->used==true);
	assert(p->right->left==p);
	assert(!(p->info < minNode->info));
	S.push(p);
	count++;
	degree++;
	p = p->right;
      } while (p!=node->firstChild);
      assert(node->degree==degree);
    }      
  }

  assert(count==s);

  return true;
}


#endif

