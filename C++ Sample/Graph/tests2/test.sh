#!/bin/sh

i=1
PATH_IN="tests/"
PATH_OUT="results/"
SUF=".out"
FLAGS="g++ -std=c++14 -Wall -O2 -o"

while [ $i -le 18 ]
do
	if [ $i -eq 8 ] || [ $i -eq 9 ]; then
		for ch in "c" "m"
		do
			t="test"$i$ch
			$FLAGS $t $PATH_IN$t.cpp
			./$t > $t$SUF
			diff $t$SUF $PATH_OUT"result"$i$ch.txt
			ret=$?
			if [ $ret -ne 0 ]; then
				echo "Error in $t"
			else
				echo "$t passed!"
				rm $t$SUF
				rm $t
			fi

		done
	else
		t="test"$i
		$FLAGS $t $PATH_IN$t.cpp
		./$t > $t$SUF
		diff $t$SUF $PATH_OUT"result"$i.txt
		ret=$?
		if [ $ret -ne 0 ]; then
			echo "Error in $t"
		else
			echo "$t passed!"
			rm $t$SUF
			rm $t
		fi
	fi

	i=`expr $i + 1`
done
