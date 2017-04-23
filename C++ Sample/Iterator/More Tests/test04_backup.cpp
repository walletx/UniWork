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
  std::cout << "1 .BTree Content Before Move Constructor" << std::endl;
  std::cout << tree << std::endl;

  btree<char> tree_moved(std::move(tree));

  std::cout << "==============================" << std::endl;
  std::cout << "2. BTree Content After Move Constructor" << std::endl;
  std::cout << tree << std::endl;

  std::cout << "==============================" << std::endl;
  std::cout << "3. Move Constructed BTree Content" << std::endl;
  std::cout << tree_moved << std::endl;

  btree<char> tree_moved_second_time = std::move(tree_moved);

  std::cout << "==============================" << std::endl;
  std::cout << "4. Move Constructed BTree Content After Move Assignment" << std::endl;
  std::cout << tree_moved << std::endl;

  std::cout << "==============================" << std::endl;
  std::cout << "5. Move Assigned BTree Content" << std::endl;
  std::cout << tree_moved_second_time << std::endl;

  return 0;
}