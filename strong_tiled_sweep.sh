MAX_TILE=16
MAX_MATRIX_DIM=$((CGRA_DIM*32))
#log 2 when we know that the argument is a power of 2
MAX_TILE_LOG2=0

export _JAVA_OPTIONS="-Xms1G -Xmx16G"

while [ $MAX_TILE -gt 1 ]; do
    MAX_TILE=$((MAX_TILE>>1))
    $((MAX_TILE_LOG2++))
done

for i in $(seq 0 MAX_TILE_LOG2); do
    ./strong_tiled.sh $CGRA_DIM $MAX_MATRIX_DIM $((1<<i))
done
