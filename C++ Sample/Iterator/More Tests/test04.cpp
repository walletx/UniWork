#include <algorithm>
#include <iostream>
#include <iterator>
#include <string>

#include "btree.h"

int main(void) {
    
  std::cout << "Testing B-Trees" << std::endl;
  std::cout << std::endl;
        
  /***Testing Empty Tree***/

  // Initalise Empty Tree
  btree<int> bti(3);

  // Finding Elements
  bti.find(5);
  bti.find(-10);
  bti.find(10000);

  // Iterator
  for (auto it = bti.begin(); it != bti.end(); ++it) {
      std::cout << *it << std::endl;
  }
  
  for (auto it = bti.rbegin(); it != bti.rend(); ++it) {
      std::cout << *it << std::endl;
  }

  std::cout << "Empty Tree: " << bti << std::endl;
  std::cout << std::endl;

  /***Testing One Elem Tree***/

  // Insert Element
  bti.insert(500);

  // Finding Elements
  bti.find(50);
  bti.find(-2447);
  bti.find(14535);
  bti.find(500);

  // Iterator
  for (auto it = bti.begin(); it != bti.end(); ++it) {
      std::cout << *it << std::endl;
  }
  
  for (auto it = bti.rbegin(); it != bti.rend(); ++it) {
      std::cout << *it << std::endl;
  }

  std::cout << "One Elem Tree: " << bti << std::endl;
  std::cout << std::endl;

  /***Testing One Node Full Tree***/

  // Inserting Elements
  bti.insert(200);
  bti.insert(777);

  // Finding Elements
  bti.find(50);
  bti.find(-2447);
  bti.find(14535);
  bti.find(500);

  // Iterator
  for (auto it = bti.begin(); it != bti.end(); ++it) {
      std::cout << *it << std::endl;
  }
  
  for (auto it = bti.rbegin(); it != bti.rend(); ++it) {
      std::cout << *it << std::endl;
  }

  std::cout << "One Node Full Tree: " << bti << std::endl;
  std::cout << std::endl;

  /***Testing One Child Tree (Last Node w/o Children)***/

  // Insert Element
  bti.insert(654);

  // Iterator
  for (auto it = bti.begin(); it != bti.end(); ++it) {
      std::cout << *it << std::endl;
  }
  
  for (auto it = bti.rbegin(); it != bti.rend(); ++it) {
      std::cout << *it << std::endl;
  }

  std::cout << "One Child Tree: " << bti << std::endl;
  std::cout << std::endl;

  /***Testing Two Children Tree (Last Node w/o Children)***/

  // Testing Duplicates
  bti.insert(200);
  bti.insert(777);
  bti.insert(654);

  // Inserting Elements
  bti.insert(655);
  bti.insert(653);
  bti.insert(100);
  bti.insert(198);

  // Finding Elements
  bti.find(50);
  bti.find(-2447);
  bti.find(198);
  bti.find(655);

  // Iterator
  for (auto it = bti.begin(); it != bti.end(); ++it) {
      std::cout << *it << std::endl;
  }
  
  for (auto it = bti.rbegin(); it != bti.rend(); ++it) {
      std::cout << *it << std::endl;
  }

  std::cout << "Two Children Tree: " << bti << std::endl;
  std::cout << std::endl;

  /***Testing Full Tree (Last Node w/ Children)***/

  // Testing Duplicates
  bti.insert(655);
  bti.insert(653);
  bti.insert(100);
  bti.insert(198);

  // Inserting Elements
  bti.insert(1000);
  bti.insert(999);
  bti.insert(843);
  bti.insert(1001);
  bti.insert(153);
  bti.insert(99);
  bti.insert(199);
  bti.insert(300);
  bti.insert(333);

  // Finding Elements
  bti.find(50);
  bti.find(-2447);
  bti.find(1002);
  bti.find(199);
  bti.find(655);
  bti.find(1001);
  bti.find(1000);
  bti.find(99);
  bti.find(199);
  bti.find(333);
  bti.find(777);

  // Iterator
  for (auto it = bti.begin(); it != bti.end(); ++it) {
      std::cout << *it << std::endl;
  }
  
  for (auto it = bti.rbegin(); it != bti.rend(); ++it) {
      std::cout << *it << std::endl;
  }

  std::cout << "Full Tree: " << bti << std::endl;
  std::cout << std::endl;

  /***Testing Tree of Negative Integers***/

  btree<int> btni(3);

  // Inserting Elements
  btni.insert(-1000);
  btni.insert(-999);
  btni.insert(-843);
  btni.insert(-1001);
  btni.insert(-153);
  btni.insert(-99);
  btni.insert(-199);
  btni.insert(-300);
  btni.insert(-333);
  btni.insert(99);
  btni.insert(199);
  btni.insert(300);
  btni.insert(333);

  // Iterator
  for (auto it = btni.begin(); it != btni.end(); ++it) {
      std::cout << *it << std::endl;
  }
  
  for (auto it = btni.rbegin(); it != btni.rend(); ++it) {
      std::cout << *it << std::endl;
  }

  std::cout << "Negative Int Tree: " << btni << std::endl;
  std::cout << std::endl;

  /***Testing Tree of Strings***/

  btree<std::string> bts(3);

  bts.insert("49");
  bts.insert("43");
  bts.insert("100");
  bts.insert("1001");
  bts.insert("Haggle");
  bts.insert("haggle");
  bts.insert("$%$()");
  bts.insert("The Best");
  bts.insert("");
  bts.insert("MAN OH MAN");
  bts.insert("...");
  bts.insert("///");
  bts.insert("GET A LIFE");

  // Iterator
  for (auto it = bts.begin(); it != bts.end(); ++it) {
      std::cout << *it << std::endl;
  } 
  
  for (auto it = bts.rbegin(); it != bts.rend(); ++it) {
      std::cout << *it << std::endl;
  }

  std::cout << "String Tree: " << bts << std::endl;
  std::cout << std::endl;

  /***Testing Move and Copy***/

  // Empty to Empty Copy Constructor
  btree<int> btcop1(5);
  btree<int> btcop2(btcop1);

  std::cout << "Copy Tree 1: " << btcop1 << std::endl;
  std::cout << "Copy Tree 2: " << btcop2 << std::endl;
  std::cout << std::endl;

  // Full to Empty Copy Constructor
  btcop1.insert(1000);
  btcop1.insert(999);
  btcop1.insert(843);
  btcop1.insert(1001);
  btcop1.insert(153);
  btcop1.insert(99);
  btcop1.insert(199);
  btcop1.insert(300);
  btcop1.insert(333);

  btree<int> btcop3(btcop1);
  btree<int> btcop4 = btcop3;

  std::cout << "Copy Tree 1: " << btcop1 << std::endl;
  std::cout << "Copy Tree 3: " << btcop3 << std::endl;
  std::cout << "Copy Tree 4: " << btcop4 << std::endl;
  std::cout << std::endl;

  // Empty to Empty Copy Assign

  btree<int> btcop5(10);
  btree<int> btcop6(5);
  btcop5 = btcop6;

  std::cout << "Copy Tree 5: " << btcop5 << std::endl;
  std::cout << "Copy Tree 6: " << btcop6 << std::endl;
  std::cout << std::endl;

  // Full to Empty Copy Assign
  btcop3 = btcop4;

  std::cout << "Copy Tree 3: " << btcop3 << std::endl;
  std::cout << "Copy Tree 4: " << btcop4 << std::endl;
  std::cout << std::endl;

  // Full to Full Copy Assign
  btcop6.insert(4);
  btcop6.insert(7);
  btcop6.insert(1);
  btcop6.insert(0);
  btcop6.insert(8);

  btcop3 = btcop6;

  std::cout << "Copy Tree 3: " << btcop3 << std::endl;
  std::cout << "Copy Tree 6: " << btcop6 << std::endl;
  std::cout << std::endl;

  // Empty to Full Copy Assign
  btcop3 = btcop5;

  std::cout << "Copy Tree 3: " << btcop3 << std::endl;
  std::cout << "Copy Tree 5: " << btcop5 << std::endl;
  std::cout << std::endl;

  // Empty to Empty Move Constructor
  btree<int> btmov1(5);
  btree<int> btmov2 = std::move(btmov1);

  std::cout << "Move Tree 1: " << btmov1 << std::endl;
  std::cout << "Move Tree 2: " << btmov2 << std::endl;
  std::cout << std::endl;


  // Full to Empty Move Constructor
  btmov1.insert(1000);
  btmov1.insert(999);
  btmov1.insert(843);
  btmov1.insert(1001);
  btmov1.insert(153);
  btmov1.insert(99);
  btmov1.insert(199);
  btmov1.insert(300);
  btmov1.insert(333);

  btree<int> btmov3 = std::move(btmov1);

  std::cout << "Move Tree 1: " << btmov1 << std::endl;
  std::cout << "Move Tree 3: " << btmov3 << std::endl;
  std::cout << std::endl;

  // Empty to Empty Move Assign
  btree<int> btmov4(10);
  btree<int> btmov5(5);

  btmov4 = std::move(btmov5);

  std::cout << "Move Tree 4: " << btmov4 << std::endl;
  std::cout << "Move Tree 5: " << btmov5 << std::endl;
  std::cout << std::endl;

  // Full to Empty Move Assign

  btmov4 = std::move(btmov3);

  std::cout << "Move Tree 3: " << btmov3 << std::endl;
  std::cout << "Move Tree 4: " << btmov4 << std::endl;
  std::cout << std::endl;

  // Full to Full Move Assign
  btmov5.insert(4);
  btmov5.insert(7);
  btmov5.insert(1);
  btmov5.insert(0);
  btmov5.insert(8);

  btmov4 = std::move(btmov5);

  std::cout << "Move Tree 4: " << btmov4 << std::endl;
  std::cout << "Move Tree 5: " << btmov5 << std::endl;
  std::cout << std::endl;

  // Empty to Full Move Assign
  btmov4 = std::move(btmov1);

  std::cout << "Move Tree 1: " << btmov1 << std::endl;
  std::cout << "Move Tree 4: " << btmov4 << std::endl;
  std::cout << std::endl;

  // /***Testing Possible Error Cases***/

  btree<unsigned int> btui(3);
  
  if (btui.begin() == btui.end()) {
      std::cout << "correct" << std::endl;
  }

  bti.insert(655);
  bti.insert(653);
  bti.insert(-653);

  bti.find(-10);

  btree<int> btis(3);
  
  btis.insert(5);
  btis.insert(10);
  btis.insert(20);
  btis.insert(1);
  btis.insert(2);
  btis.insert(3);
  btis.insert(4);
  btis.insert(8);
  btis.insert(30);
  btis.insert(40);
  btis.insert(50);
  btis.insert(25);
  btis.insert(35);
  
  // Iterator
  for (auto it = btis.begin(); it != btis.end(); ++it) {
      std::cout << *it << std::endl;
  }
  
  for (auto it = btis.rbegin(); it != btis.rend(); ++it) {
      std::cout << *it << std::endl;
  }
  
  std::cout << "Suspicious Tree: " << btis << std::endl;
  std::cout << std::endl;

  return 0;
}
