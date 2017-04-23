
//
//  COMP1927 Assignment 1 - Memory Suballocator
//  allocator.c ... implementation
//
//  Created by Liam O'Connor on 18/07/12.
//  Modified by John Shepherd in August 2014
//  Copyright (c) 2012-2014 UNSW. All rights reserved.
//  Modified by Seah Zhen Hong (z3473920) and Pongpol Trisuwan (z3471079)

#include "allocator.h"

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>


#define HEADER_SIZE    sizeof(struct free_list_header)  
#define MAGIC_FREE     0xDEADBEEF
#define MAGIC_ALLOC    0xBEEFDEAD
#define BYTES 256

typedef unsigned char byte;
typedef u_int32_t vlink_t;
typedef u_int32_t vsize_t;
typedef u_int32_t vaddr_t;

typedef struct free_list_header {
   u_int32_t magic;           // ought to contain MAGIC_FREE
   vsize_t size;              // # bytes in this block (including header)
   vlink_t next;              // memory[] index of next free block
   vlink_t prev;              // memory[] index of previous free block
} free_header_t;

// Global data

static byte *memory = NULL;   // pointer to start of suballocator memory
static vaddr_t free_list_ptr; // index in memory[] of first block in free list
static vsize_t memory_size;   // number of bytes malloc'd in memory[]

int findBestRegion (int n);
int dividingRegion (int bestRegionSize, int n);
void sortingPointers ();
void *allocation(int bestRegionSize);
void sal_merge (free_header_t *currentheader);
int powertwo (int n);
// Input: size - number of bytes to make available to the suballocator
// Output: none              
// Precondition: Size is a power of two.
// Postcondition: `size` bytes are now available to the suballocator
// 
// (If the suballocator is already initialised, this function does nothing, even 
// if it was initialised with different size)

void sal_init(u_int32_t size)
{
	
	if (size <= 0 || memory!= NULL) {
		return;
	}
	//Set size condition to be a power of two
	int testsize = size;
	//Set minimum size to be 32
	int n = 5;

	while(testsize != 1) {
		if (testsize%2 == 1) {
			while(testsize < size) {
				testsize = powertwo(n);
				n++;
			}
			break;
		}
		testsize = testsize/2;
	}

	if (testsize == 1) {
	//Do nothing if the size being passed in is a power of two
	} else {
		size = testsize;
	}
	
	//Giving a pointer to an area of memory to a global variable
	memory = malloc(size);

	//Checking if memory allocation is fine
	if (memory == NULL) {
		fprintf(stderr, "sal_init: insufficient memory");
		abort();
	}

	//Setting header for the newly allocated area of memory
	free_header_t *freeheader_t = (free_header_t *)memory;
	freeheader_t->magic = MAGIC_FREE;
	freeheader_t->size = size;
	freeheader_t->next = 0;
	freeheader_t->prev = 0;

	//Setting the remaining globals
	free_list_ptr = 0;
	memory_size = size;

}

// Input: n - number of bytes requested
// Output: p - a pointer, or NULL
// Precondition: n is < size of memory available to the suballocator
// Postcondition: If a region of size n or greater cannot be found, p = NULL 
//                Else, p points to a location immediately after a header block
//                      for a newly-allocated region of some size >= 
//                      n + header size. 


void *sal_malloc(u_int32_t n)
{
	
	//Check if input is valid if not return NULL
	//Since there's a condition that one area of memory must be free at all times the maximum allocatable size is memory_size/2
	if (n <= 0 || n+HEADER_SIZE > memory_size/2) {
		return NULL;
	}
	
	//Initializing the currentheader
	free_header_t *currentheader = (free_header_t *)&memory[free_list_ptr];
	
	//Checking that the currentheader at free_list_ptr is not allocated
	if (currentheader->magic == MAGIC_ALLOC) {
		fprintf(stderr, "Memory corruption\n");
		abort();
	}
	
	//Finding most suitable region in the free region list
	int bestRegion = findBestRegion(n);
	
	//Check if a region is found if not return NULL
	if (bestRegion > memory_size) {
		return NULL;	
	}
	
	//Dividing the area of bestRegion if necessary
	bestRegion = dividingRegion(bestRegion, n);
	
	//Sorting the pointers after division of area
	sortingPointers();
	
	//Check that after division it isn't the only area left if it is return NULL
	if ((((free_header_t*)&memory[currentheader->next] == currentheader) && (free_list_ptr != 0)) ) {
		return NULL;	
	}
	
	//Allocating the bestRegion
	currentheader = allocation(bestRegion);
	
	//Going through the list and sorting all pointers
	sortingPointers();

	return (void*)currentheader;	
}

int findBestRegion (int n) {
	
	//printf("Starting to find best region\n");
	
	//bestRegionSize is initialized to larger than memory_size for loop condition
	int bestRegionSize = memory_size + 1;
	free_header_t *currentheader = (free_header_t *)&memory[free_list_ptr];
	
	//Go through whole list to find the best region size
	do {
		if ((currentheader->size >= n+HEADER_SIZE) && (currentheader->size < bestRegionSize)) { 
		   bestRegionSize = currentheader->size;
		} 
		currentheader = (free_header_t*)&memory[currentheader->next];

	} while (currentheader != (free_header_t *)&memory[free_list_ptr]);
	
	//printf("Best region found\n");
	return bestRegionSize;
}

int dividingRegion (int bestRegionSize, int n) {
	
	//printf("Starting to divide best region\n");
	free_header_t *currentheader = (free_header_t *)&memory[free_list_ptr];
	free_header_t *mainheader = (free_header_t *)&memory[free_list_ptr];
	
	//Find the first region starting from free_list_ptr with the bestRegionSize
	while(currentheader->size != bestRegionSize) {
		currentheader = (free_header_t*)&memory[currentheader->next];
	}
	
	mainheader = currentheader;
	
	//Dividing regions to smallest size possible
	while (bestRegionSize/2 >= n+HEADER_SIZE) {
		
		currentheader = mainheader;
		//Changing the size of the first half of bestRegion
		bestRegionSize = bestRegionSize/2;
		currentheader->size = bestRegionSize;
		//Moving to the header of the second half and initializing it
		currentheader = currentheader + bestRegionSize/16;
		currentheader->magic = MAGIC_FREE;
		currentheader->size = bestRegionSize;	
	}
	
	//printf("Divison done\n");
	return bestRegionSize;	
}

void sortingPointers () {
	
	//printf("Starting to sort out pointers\n");
	
	free_header_t *currentheader = (free_header_t *)&memory[free_list_ptr];
	free_header_t *tempheader = currentheader + currentheader->size/16;
	
	//Condition for sorting out the pointers it goes through the list using two headers to link two area of free_memory
	//Various if-statements for various special cases that may crop up
	
	if((int)currentheader - (int)&memory[0] == memory_size - currentheader->size) {
		
		currentheader->next = free_list_ptr;
		tempheader = (free_header_t *)&memory[free_list_ptr];
		tempheader->prev = ((int)currentheader - (int)&memory[0]);
		
	} else {

		while( (int)tempheader - (int)&memory[0] != memory_size - tempheader->size) {
		
			while(tempheader->magic == MAGIC_ALLOC){
				if((int)tempheader - (int)&memory[0] == memory_size - tempheader->size) {
					break;
				}
				tempheader = tempheader + tempheader->size/16;
			}
			
			currentheader->next = ((int)tempheader - (int)&memory[0]);
			tempheader->prev = ((int)currentheader - (int)&memory[0]);
			
		    if (tempheader->magic == MAGIC_FREE) {
		    	currentheader = (free_header_t*)&memory[currentheader->next];
		    }
	
			if ((int)tempheader - (int)&memory[0] != memory_size - tempheader->size) {
				tempheader = tempheader + tempheader->size/16;
			}
			
		}

//Linking the last free region to the first free region	according to various special cases
if (tempheader->magic == MAGIC_FREE) {
	
	currentheader->next = ((int)tempheader - (int)&memory[0]);
	tempheader->next = free_list_ptr;

	if(currentheader != tempheader) {
		tempheader->prev = ((int)currentheader - (int)&memory[0]);
	}

	currentheader = (free_header_t *)&memory[free_list_ptr];
	currentheader->prev = ((int)tempheader - (int)&memory[0]);
	
} else {
		
		currentheader->next = free_list_ptr;
		tempheader = (free_header_t *)&memory[free_list_ptr];
		tempheader->prev = ((int)currentheader - (int)&memory[0]);
		
       }
    }
}

void *allocation(int bestRegionSize) {
	
	//printf("Starting to allocate\n");
	free_header_t *currentheader = (free_header_t *)&memory[free_list_ptr];
	free_header_t *allocheader = (free_header_t *)&memory[free_list_ptr];
	
    //Find the first allocatable area of free memory starting from free_list_ptr
	while(currentheader->magic != MAGIC_FREE || currentheader->size != bestRegionSize) {
		currentheader = (free_header_t*)&memory[currentheader->next];
	}
	
	//printf("Assigned memory at %d\n",((int)currentheader - (int)&memory[0]));
	//Changing header information for chosen area
	currentheader->magic = MAGIC_ALLOC;
	allocheader = currentheader;
	currentheader = (free_header_t *)&memory[0];
	
	//Change free_list_ptr index if necessary
	while(currentheader->magic != MAGIC_FREE) {

		currentheader = currentheader + currentheader->size/16;
	}
	
	free_list_ptr = ((int)currentheader - (int)&memory[0]);	
	
	//printf("Allocation done\n");
	return (void*)(allocheader + HEADER_SIZE/16);
}

// Input: object, a pointer.
// Output: none
// Precondition: object points to a location immediately after a header block
//               within the suballocator's memory.
// Postcondition: The region pointed to by object can be re-allocated by 
//                sal_malloc


void sal_free(void *object)
{
    free_header_t *currentheader;
    currentheader = (free_header_t *)&memory[free_list_ptr];
    free_header_t *objectheader = object - HEADER_SIZE; 

    if (objectheader->magic == MAGIC_FREE) {
        fprintf(stderr, "Attempt to free non-allocated memory");
        abort();
    }

    objectheader->magic = MAGIC_FREE;
    int regionsize = currentheader->size;
    int currentmemory = (int)currentheader-(int)&memory[0];//memory index of currentheader
    int objectmemory = (int)objectheader-(int)&memory[0];//memory index of objectheader

    //If there is only one free region in the list, merge the object with the region if they are the original pair
    if ((free_header_t*)&memory[currentheader->next] == currentheader) {
    	//if object is the second half of the original pair
    	if (currentheader->size == objectheader->size && (currentmemory%(regionsize*2) == 0) && 
    	   (objectmemory == currentmemory + regionsize)) {
            currentheader->size = regionsize*2;
            objectheader->magic = 0;
            objectheader->size = 0;
            objectheader->next = 0;
            objectheader->prev = 0;
         //if object is the first half of the original pair
      	} else if (currentheader->size == objectheader->size && (objectmemory%(regionsize*2) == 0) && 
      		      (currentmemory == objectmemory + regionsize)) {
            objectheader->size = regionsize*2;
            objectheader->next = objectmemory;
            objectheader->prev = objectmemory;
            currentheader->magic = 0;
            currentheader->size = 0;
            currentheader->next = 0;
            currentheader->prev = 0;
            free_list_ptr = objectmemory;
      	} else {
      	    // Current and object cannot be merged
            objectheader->next = currentheader->next;
            objectheader->prev = currentheader->next;
            currentheader->next = ((int)objectheader - (int)&memory[0]);
            currentheader->prev = currentheader->next;
            //if objectmemory comes before current memory, change freelistptr to objectmemory
            if (currentmemory > objectmemory) {
               free_list_ptr = ((int)objectheader - (int)&memory[0]);
            } 
      	}
  	
  	//else there is more than one region, we need to go through the list to find the right place for object
    } else {    

    	free_header_t *tempheader = currentheader;
    	currentmemory = (int)currentheader-(int)&memory[0];
    	int counter = 0;

       	do {
       			if (currentmemory > objectmemory) {	
       				break;
       			}
         		currentheader = (free_header_t*)&memory[currentheader->next];
         		currentmemory = (int)currentheader-(int)&memory[0];
         		counter++;

        }  while (currentheader != tempheader);
        //if the object is the first free region, change the free_list_ptr to objectheader
        if (currentheader == tempheader && counter == 0) {
        	free_list_ptr = objectmemory;	
        }
        
         objectheader->next = currentmemory;
         objectheader->prev = currentheader->prev;
         ((free_header_t*)&memory[currentheader->prev])->next = objectmemory;
         currentheader->prev = objectmemory;
         currentheader = (free_header_t *)&memory[objectmemory];

		 sal_merge(currentheader);
 
   }

}

//This function merge the free regions if the condition is met
void sal_merge (free_header_t *currentheader) {

	int currentmemory = (int)currentheader - (int)&memory[0];
	int counter = 0;
	int mergedone = 0;
	//In the loop, the code determines whether there is a valid adjacent region to be merged with the object that is just being freed 
	//The loop keeps merging as long as the condition is met until the value of the mergedone is 0, 
	//which indicates that no more merging is available and the loop ends.
	do {
		mergedone = 0;
        currentmemory = (int)currentheader - (int)&memory[0];

		if ((currentmemory%(currentheader->size*2)) == 0) {

            if (((free_header_t*)&memory[currentheader->next])->size == currentheader->size && 
            	(currentheader->next == currentmemory+currentheader->size)) {

            	free_header_t *nextheader = (free_header_t *)&memory[currentheader->next];
                currentheader->size = currentheader->size*2 ;
                currentheader->next = nextheader->next;
                ((free_header_t *)&memory[nextheader->next])->prev = currentmemory;
                nextheader->magic = 0;
                nextheader->size = 0;
                nextheader->next = 0;
                nextheader->prev = 0; 
                mergedone = 1;
                //if there is only one region after merging, change the prev of currentheader to itself
                if ((free_header_t*)&memory[currentheader->prev] == nextheader) {
                	currentheader->prev = currentmemory;
                    free_list_ptr = currentmemory;
                	mergedone = 0;
                }
                counter++;
            }

        } else if (((free_header_t *)&memory[currentheader->prev])->size == currentheader->size && 
        		  (currentheader->prev == currentmemory-currentheader->size)) {

            free_header_t *prevheader = (free_header_t *)&memory[currentheader->prev];
            prevheader->size = prevheader->size*2;
            prevheader->next = currentheader->next;
            ((free_header_t *)&memory[currentheader->next])->prev = (int)prevheader-(int)&memory[0];
            
            currentheader->magic = 0;
            currentheader->size = 0;
            currentheader->next = 0;
            currentheader->prev = 0;
            mergedone = 1;

            if ((free_header_t*)&memory[prevheader->prev] == currentheader) {
            	prevheader->prev = (int)prevheader - (int)&memory[0];
            	free_list_ptr = (int)prevheader - (int)&memory[0];
            	mergedone = 0;
            }
            currentheader = prevheader;
            counter++;
        }

    } while (mergedone == 1);    

}

// Stop the allocator, so that it can be init'ed again:
// Precondition: suballocator memory was once allocated by sal_init()
// Postcondition: allocator is unusable until sal_int() executed again

void sal_end(void)
{
	if(memory == NULL) {
		return;
	}
	free(memory);
}

// Precondition: allocator has been sal_init()'d
// Postcondition: allocator stats displayed on stdout

void sal_stats(void)
{
   // Optional, but useful
   printf("\nsal_stats\n\n");
   free_header_t *currheader = (free_header_t *)&memory[free_list_ptr];
   free_header_t *temp = currheader;
   int counter = 0;

   do{
        printf("| %d-%d ", ((free_header_t *)&memory[currheader->next])->prev, 
        	  ((free_header_t *)&memory[currheader->next])->prev + currheader->size);
        currheader = (free_header_t *)&memory[currheader->next];
		counter++;
   } while (currheader != temp);
   
   printf("|\n\n");
   printf("There are %d regions\n\n", counter);
    // we "use" the global variables here
    // just to keep the compiler quiet
   //memory = memory;
   //free_list_ptr = free_list_ptr;
   //memory_size = memory_size;
}

int powertwo(int n) {
	int outcome = 2;
	int counter = 1;
	while (counter != n) {
		outcome = outcome * 2;
		counter++;
	}
	return outcome;
}





