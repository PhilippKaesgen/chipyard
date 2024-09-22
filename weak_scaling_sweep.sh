#!/bin/bash

datum='20sep24_2' 
OUTFILE=weak_float_${datum}.py

export _JAVA_OPTIONS="-Xms1G -Xmx8G"

echo "data=[" >> $OUTFILE
OUTFILE=${OUTFILE} ./weak_scaling.sh 1 1
for t in 1 2 4 8; do
    for h in 3 4 5 6; do
        if [ $h -eq 6 ]; then if [ $t -eq 8 ]; then echo "]" >> $OUTFILE; exit; fi; fi # doesn't compile
        OUTFILE=${OUTFILE} ./weak_scaling.sh $h $t
    done
done
echo "]" >> $OUTFILE
