#include <iostream>

int main(void) {
	int i = 5;
	const int &r = i;

	std::cout << "i = " << i << std::endl;
	std::cout << "r = " << r << std::endl;

	i = 10;

	std::cout << "i = " << i << std::endl;
	std::cout << "r = " << r << std::endl;

	//r = 6;      // error

	return 0;
}