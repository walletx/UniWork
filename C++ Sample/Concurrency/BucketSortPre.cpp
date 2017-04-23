#include "BucketSort.h"

#include <algorithm>
#include <cmath>
#include <iostream>
#include <thread>
#include <mutex>
#include <memory>

//TODO optimise aLessBTer
//TODO optimise the sorting in shared memory
//TODO check cases for when number < threads
//TODO check cases for when threads > buckets

int numDigits(unsigned int num)  {
    
    // return (num < 10 ? 1 : (num < 100 ? 2 : (num < 1000 ? 3 :
    // (num < 10000 ? 4 : (num < 100000 ? 5 : (num < 1000000 ? 6 :
    // (num < 10000000 ? 7 : (num < 100000000 ? 8 : (num < 1000000000 ? 9 :
    // 10)))))))));
    
    unsigned int length = 1;
    unsigned int magnitude = 10;
    unsigned int digits = std::numeric_limits<unsigned int>::digits10 + 1;
    
    while (num >= magnitude) {
        ++length;
        if (length == digits) {break;}
        magnitude *= 10;
    }
    
    // while (num /= 10) {
    //     ++length;
    // }

    return length;
    
}


bool aLessBIter(unsigned int x, unsigned int y) {
    
    if (x == y) return false;
    
    unsigned int digits1 = numDigits(x);
    unsigned int digits2 = numDigits(y);
    bool lessDigits = digits1 < digits2;
        
    if (digits1 == digits2) {return x < y;}

    while (digits1 > digits2) {
        x /= 10;
        --digits1;
    }
        
    while (digits2 > digits1) {
        y /= 10;
        --digits2;
    }    

    if (x == y) {
        return lessDigits;
    } else {
        return x < y;
    }
}

bool aLessB(const unsigned int& x, const unsigned int& y, unsigned int pow) {
    
    if (x == y) return true; // if the two numbers are the same then one is not less than the other
    
    unsigned int a = x;
    unsigned int b = y;
    
    // calculate pow here
    unsigned int power = (unsigned int) std::round(std::pow(10,pow));
    
    // work out the digit we are currently comparing on. 
    if (pow == 0) {
        while (a / 10 > 0) {
            a = a / 10; 
        }   
        while (b / 10 > 0) {
            b = b / 10;
        }
    } else {
        while (a / 10 >= power) {
            a = a / 10;
        }
        while (b / 10 >= power) {
            b = b / 10;
        }
    }

    if (a == b)
        return aLessB(x,y,pow + 1);  // recurse if this digit is the same 
    else
        return a < b;
}

unsigned int msdOf(unsigned int num) {
    
    // unsigned int digits = (num < 10 ? 1 : (num < 100 ? 10 : (num < 1000 ? 100 :
    // (num < 10000 ? 1000 : (num < 100000 ? 10000 : (num < 1000000 ? 100000 :
    // (num < 10000000 ? 1000000 : (num < 100000000 ? 10000000 :
    // (num < 1000000000 ? 100000000 : 1000000000)))))))));
    //
    // return num/digits;
            
    while (num >= 10) {
        num /= 10;
    }

    return num;
}

void BucketSort::checkCorrect(void) {
    
    for (unsigned int i = 0; i < numbersToSort.size() - 1; ++i) {
        if (!aLessB(numbersToSort[i], numbersToSort[i+1], 0)) {
            std::cout << "Wrong sort order at " << numbersToSort[i] << " " << numbersToSort[i+1] << std::endl;
            return;
        }
    }
    std::cout << "All correct!" << std::endl;
}

// TODO: replace this with a parallel version. 
void BucketSort::sort(unsigned int numCores) {
        
    //Vector of vectors (10 shared memory buckets)
    std::vector<std::vector<unsigned int>> shared_buckets (NUM_BUCKETS);
    
    // Vector of mutexes (for shared memory buckets)
    std::vector<std::mutex> bucket_mutexes(10);
    //std::vector<std::unique_ptr<std::lock_guard<std::mutex>>> bucket_mutexes;
    
    // Vector of threads (for easy initialisation)
    std::vector<std::thread> threads;
    //std::vector<std::unique_ptr<std::thread>> threads;
    
    // Set lambda for partitioning and sorting
    const unsigned int set_size = numbersToSort.size();
    
    // Lambda for partitioning number set
    auto partition_lambda = [this, &set_size, &numCores, &shared_buckets, &bucket_mutexes] (unsigned int threadNum) {
        
        // Setting up thread local variables
        unsigned int numToSort;
        std::vector<std::vector<unsigned int>> local_buckets (NUM_BUCKETS, std::vector<unsigned int>());
                        
        // Dividing allocated group into local buckets
        while (threadNum < set_size) {
            numToSort = numbersToSort[threadNum];
            local_buckets[msdOf(numToSort)].push_back(numToSort);
            threadNum += numCores;
        }
        
        // Dumping them in the shared memory buckets
        for (unsigned int i = 0; i < NUM_BUCKETS; ++i) {
            {
                std::lock_guard<std::mutex> bucket_guard(bucket_mutexes[i]);
                shared_buckets[i].insert(shared_buckets[i].end(), local_buckets[i].begin(), local_buckets[i].end());
            }
        }                
    };
        
    // Join threads for partitioning (check if there's a way to call main as well)
    for (unsigned int i = 0; i < numCores - 1; ++i) {
        //std::thread threadToPush{partition_lambda, i};
        //auto thread_ptr = std::make_unique<std::thread>(threadToPush);
        threads.push_back(std::thread(partition_lambda, i));
    }
    
    partition_lambda(numCores - 1);
        
    for (auto&& thread: threads) {
        thread.join();
    }
                
    threads.clear();
    
    // Lambda for sorting the buckets

    auto sorting_lambda = [this, &numCores, &shared_buckets, &bucket_mutexes] (unsigned int threadNum) {

        // Sorting shared buckets
        // for (auto vector: shared_buckets) {
        //     std::sort(vector.begin(), vector.end(), [](const unsigned int& x, const unsigned int& y) {
        //             return aLessB(x,y,0);
        //     });
        // }
        
        while (threadNum < NUM_BUCKETS) {
            std::sort(shared_buckets[threadNum].begin(), shared_buckets[threadNum].end(), [](const unsigned int& x, const unsigned int& y) {
                return aLessBIter(x, y);
            });
            
            threadNum += numCores;
        }
    };
    
    // Join threads for sorting
    for (unsigned int i = 0; i < numCores - 1; ++i) {
        //std::thread threadToPush{partition_lambda, i};
        threads.push_back(std::thread(sorting_lambda, i));
    }
    
    sorting_lambda(numCores - 1);
    
    for (auto&& thread: threads) {
        thread.join();
    }
    
    threads.clear();
        
    // Main will then dump the shared memory buckets back into numbersToSort
    numbersToSort.clear();

    for (unsigned int i = 0; i < NUM_BUCKETS; ++i) {
        numbersToSort.insert(numbersToSort.end(), shared_buckets[i].begin(), shared_buckets[i].end());
    }
}
