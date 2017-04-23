#include <iostream>

#include "btree.h"

int main(void) {
  btree<char> tree(4);
  bool foundInTree = false;

  tree.insert('M');
  tree.insert('X');

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
  tree.insert('V');

  std::cout << "==============================" << std::endl;
  std::cout << "Find M" << std::endl;
  foundInTree = (tree.find('M') != tree.end());

  if (foundInTree) {
    std::cout << "Found!" << std::endl;
  }
  else{
    std::cout << "Not Found!" << std::endl;
  }

  std::cout << "==============================" << std::endl;
  std::cout << "Find A" << std::endl;
  foundInTree = (tree.find('A') != tree.end());

  if (foundInTree) {
    std::cout << "Found!" << std::endl;
  }
  else{
    std::cout << "Not Found!" << std::endl;
  }

  std::cout << "==============================" << std::endl;
  std::cout << "BTree Content" << std::endl;
  std::cout << tree << std::endl;

  std::cout << "==============================" << std::endl;
  std::cout << "Go through forward iterator ++" << std::endl;

  for (auto itx = tree.begin(); itx != tree.end(); ++itx){
    std::cout << (*itx) << std::endl;
    //std::cout << std::endl;
  }

  std::cout << "==============================" << std::endl;
  std::cout << "Go through (should not run) ++" << std::endl;

  for (auto itx = tree.find('A'); itx != tree.end(); ++itx){
    std::cout << (*itx) << std::endl;
  }

  std::cout << "==============================" << std::endl;
  std::cout << "Go through (some position R) ++" << std::endl;

  for (auto itx = tree.find('R'); itx != tree.end(); ++itx){
    std::cout << (*itx) << std::endl;
  }

  std::cout << "==============================" << std::endl;
  std::cout << "Go through (some position Z) ++" << std::endl;

  for (auto itx = tree.find('Z'); itx != tree.end(); ++itx){
    std::cout << (*itx) << std::endl;
  }

  std::cout << "==============================" << std::endl;
  std::cout << "Go through backward iterator --" << std::endl;

  for (auto itx = tree.rbegin(); itx != tree.rend(); ++itx){
    std::cout << (*itx) << std::endl;
    //std::cout << std::endl;
  }

  std::cout << "==============================" << std::endl;
  std::cout << "Go through (should not run) --" << std::endl;

  for (auto itx = tree.rbegin(); itx != btree<char>::reverse_iterator(tree.find('A')); ++itx){
    std::cout << (*itx) << std::endl;
  }

  std::cout << "==============================" << std::endl;
  std::cout << "Go through (some position R) --" << std::endl;

  for (auto itx = tree.rbegin(); itx != btree<char>::reverse_iterator(tree.find('R')); ++itx){
    std::cout << (*itx) << std::endl;
  }

  std::cout << "==============================" << std::endl;
  std::cout << "Go through (some position Z) --" << std::endl;

  for (auto itx = tree.rbegin(); itx != btree<char>::reverse_iterator(tree.find('Z')); ++itx){
    std::cout << (*itx) << std::endl;
  }

  return 0; 
}

