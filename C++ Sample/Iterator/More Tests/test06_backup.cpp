#include <iostream>

#include "btree.h"

int main(void) {
  btree<unsigned int> tree(99);
  unsigned int max = 400000;

  for (unsigned int i = 1; i <= max; ++i) {
    tree.insert(i);

    if (i % 100000 == 0) {
    	std::cout << i << std::endl;
    }
  }
  
  std::cout << "==============================" << std::endl;
  std::cout << "1 .BTree Content Before Copy Constructor" << std::endl;
  std::cout << tree << std::endl;

  return 0;
}