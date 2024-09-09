#!/bin/bash

datum='24jul24' #$(date +"%d-%b-%Y_%H:%M")
OUTFILE=weak_float_new_${datum}.csv

export _JAVA_OPTIONS="-Xms1G -Xmx16G"

echo "#cores,#pes per tile,#total pes,#matrix entries,#config cycles,config time (ns),#total cycles,total time (ns)" >> $OUTFILE
for t in 1 2 4 8; do #1 2 4 8 16; do
    for h in 3 4 5 6; do
        if [ $h -eq 6 ]; then if [ $t -eq 8 ]; then exit; fi; fi # doesn't compile
        OUTFILE=${OUTFILE} ./weak_scaling.sh $h $t
    done
done
