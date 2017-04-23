//COMP6771 Assignment 5 - Pongpol Trisuwan (z3471079)

#ifndef BUCKET_SORT_H
#define BUCKET_SORT_H

#include <vector>


struct BucketSort {
    
    static const unsigned int NUM_BUCKETS = 10;
    	
	// vector of numbers
    // numbers are eventually put into this bucket
	std::vector<unsigned int> numbersToSort;

	void sort(unsigned int numCores);
};


#endif /* BUCKET_SORT_H */