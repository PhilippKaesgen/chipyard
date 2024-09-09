MAX_TILE_LOG2=4
datum='28aug24' #$(date +"%d-%b-%Y_%H:%M")
#OUTFILE=roofline_float_${datum}_preload_btransposed.csv
#LATEX_TABLE=roofline_float_${datum}_preload_btransposed.tex
OUTFILE=roofline_float_${datum}.csv
LATEX_TABLE=roofline_float_${datum}.tex
#CGRA_DIM=4
#MAX_MATRIX_DIM=$((CGRA_DIM*16))

export _JAVA_OPTIONS="-Xms1G -Xmx16G"

echo "#cores,#pes per tile,#total pes,#matrix entries,#config cycles,config time (ns),#total cycles,total time (ns),arithm intens,flops no config, flops with config" >> $OUTFILE
for t in 1; do # 2 4 8; do
    for h in 4; do #3 4 5 6; do
        #if [ $h -eq 6 ]; then if [ $t -eq 8 ]; then exit; fi; fi # doesn't compile
        echo "\\pgfplotstableread{" >>$LATEX_TABLE
        echo "x y label" >>$LATEX_TABLE
        for m in 48; do #2 4 8 16 32 64 128; do
            OUTFILE=${OUTFILE} LATEX_TABLE=${LATEX_TABLE} ./roofline.sh $h $m $t || exit 1
        done
        echo "}\\rooflinet${t}p$((h*h))">>$LATEX_TABLE
    done
done
