//COMP6771 Assignment 4 - Pongpol Trisuwan (z3471079)

#ifndef BTREE_ITERATOR_H
#define BTREE_ITERATOR_H

#include <iterator>

/**
* You MUST implement the btree iterators as (an) external class(es) in this file.
* Failure to do so will result in a total mark of 0 for this deliverable.
**/

// iterator related interface stuff here; would be nice if you called your
// iterator class btree_iterator (and possibly const_btree_iterator)

template<typename T> class btree;
template<typename T> class btree_iterator;
template<typename T> class const_btree_iterator;

template<typename T> class btree_iterator {

    public:
        
        // Declared as friend to access members for equality overload
        friend class const_btree_iterator<T>;
        
        typedef std::bidirectional_iterator_tag iterator_category;
        typedef std::ptrdiff_t difference_type;
        typedef T value_type;
        typedef T* pointer;
        typedef T& reference;
        
        reference operator*() const;
        pointer operator->() const;
        
        // Prefix and Postfix increments
        btree_iterator& operator ++();
        btree_iterator& operator --();
        btree_iterator operator ++(int);
        btree_iterator operator --(int);
        
        // Equality operators
        bool operator==(const btree_iterator<T>& other) const;
        bool operator!=(const btree_iterator<T>& other) const;
        bool operator==(const const_btree_iterator<T>& other) const;
        bool operator!=(const const_btree_iterator<T>& other) const;
        
        // Constructors
        btree_iterator();
        btree_iterator(typename btree<T>::Node *pntee);
        btree_iterator(typename btree<T>::Node *pntee, int nindex);
        btree_iterator(typename btree<T>::Node *pntee, int nindex, const btree<T> *tree_ptr);
        
        // For copy control
        btree_iterator(const btree_iterator<T>& rhs);
        btree_iterator& operator=(const btree_iterator<T>& rhs);
        
    private:
        
        typename btree<T>::Node *pointee = nullptr;
        int node_index = 0;
        const btree<T> *tree = nullptr;

};

template<typename T> class const_btree_iterator {

    public:
        
        // Declared as friend to access members for equality overload
        friend class btree_iterator<T>;
        
        typedef std::bidirectional_iterator_tag iterator_category;
        typedef std::ptrdiff_t difference_type;
        typedef T value_type;
        typedef const T* pointer;
        typedef const T& reference;
        
        reference operator*() const;
        pointer operator->() const;
        
        // Prefix and Postfix increments
        const_btree_iterator& operator ++();
        const_btree_iterator& operator --();
        const_btree_iterator operator ++(int);
        const_btree_iterator operator --(int);
        
        // Equality operators
        bool operator==(const const_btree_iterator<T>& other) const;
        bool operator!=(const const_btree_iterator<T>& other) const;
        bool operator==(const btree_iterator<T>& other) const;
        bool operator!=(const btree_iterator<T>& other) const;
        
        // Constructors
        const_btree_iterator();
        const_btree_iterator(typename btree<T>::Node *pntee);
        const_btree_iterator(typename btree<T>::Node *pntee, int nindex);
        const_btree_iterator(typename btree<T>::Node *pntee, int nindex, const btree<T> *tree_ptr);
        
        // For non-const to const conversion and copy control
        const_btree_iterator(const btree_iterator<T>& rhs);
        const_btree_iterator(const const_btree_iterator<T>& rhs);
        const_btree_iterator& operator=(const btree_iterator<T>& rhs);
        const_btree_iterator& operator=(const const_btree_iterator<T>& rhs);
        
    private:
        
        typename btree<T>::Node *pointee = nullptr;
        int node_index = 0;
        const btree<T> *tree = nullptr;

};

#include "btree_iterator.tem"

#endif
