#include <iostream>

#include "btree.h"

int main(void) {
  btree<char> tree(4);

  tree.insert('M');
  tree.insert('Y');

  tree.insert('P');
  tree.insert('G');

  tree.insert('T');
  tree.insert('B');
  tree.insert('Z');

  tree.insert('N');
  tree.insert('R');

  tree.insert('S');
  tree.insert('W');

  tree.insert('Q');
  tree.insert('X');

  std::cout << "==============================" << std::endl;
  std::cout << "1 .BTree Content Before Copy Constructor" << std::endl;
  std::cout << tree << std::endl;

  btree<char> tree_copied(tree);

  std::cout << "==============================" << std::endl;
  std::cout << "2. BTree Content After Copy Constructor" << std::endl;
  std::cout << tree << std::endl;

  std::cout << "==============================" << std::endl;
  std::cout << "3. Copy Constructed BTree Content" << std::endl;
  std::cout << tree_copied << std::endl;

  btree<char> tree_copied_second_time = tree_copied;

  std::cout << "==============================" << std::endl;
  std::cout << "4. Copy Constructed BTree Content After Copy Assignment" << std::endl;
  std::cout << tree_copied << std::endl;

  std::cout << "==============================" << std::endl;
  std::cout << "5. Copy Assigned BTree Content" << std::endl;
  std::cout << tree_copied_second_time << std::endl;

  return 0;
}