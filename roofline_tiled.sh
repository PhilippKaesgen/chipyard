datum='15sep24'
OUTFILE=roofline_float_${datum}.csv
LATEX_TABLE=roofline_float_${datum}.tex


export _JAVA_OPTIONS="-Xms1G -Xmx8G"

echo "#cores,#pes per tile,#total pes,#matrix entries,#config cycles,config time (ns),#total cycles,total time (ns),arithm intens,flops no config, flops with config" >> $OUTFILE
for t in 1 2 4 8; do
    for h in 3 4 5 6; do
        if [ $h -eq 6 ]; then if [ $t -eq 8 ]; then exit; fi; fi # doesn't compile
        echo "\\pgfplotstableread{" >>$LATEX_TABLE
        echo "x y label" >>$LATEX_TABLE
        for m in 2 4 8 16 32 64 128; do
            OUTFILE=${OUTFILE} LATEX_TABLE=${LATEX_TABLE} ./roofline.sh $h $m $t || exit 1
        done
        echo "}\\rooflinet${t}p$((h*h))">>$LATEX_TABLE
    done
done
