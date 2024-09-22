datum='20sep24'
OUTFILE=strong_float_${datum}.py
MAX_MATRIX_DIM=480
export _JAVA_OPTIONS="-Xms1G -Xmx8G"

echo "data=[" >> $OUTFILE

for t in 1 2 4 8; do
    for h in 3 4 5 6; do
        if [ $h -eq 6 ]; then if [ $t -eq 8 ]; then echo "]" >> $OUTFILE; exit; fi; fi # doesn't compile
        OUTFILE=$OUTFILE ./strong_tiled.sh $h $MAX_MATRIX_DIM $t || exit 1
    done
done
echo "]" >> $OUTFILE
