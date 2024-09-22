#!/bin/bash
CHIPYARD_DIR=$(realpath .)

SIM_DIR=$CHIPYARD_DIR/sims/verilator

BIN_DIR=$(realpath generators/hiprep/src/test/resources/Benchmarks/matrix_matrix_mul)

TIME_OUT=10000000000000000000000000000

HIPREP_DIM=$1
MATRIX_DIM=$2
TILES=$3
CORES=$3
FLOAT=y
EOT=
MAX_OPS=64
if [ $HIPREP_DIM -lt 4 ]; then
    frequency=793
elif [ $HIPREP_DIM -eq 4 ]; then
    frequency=752
elif [ $HIPREP_DIM -eq 5 ]; then
    frequency=704
elif [ $HIPREP_DIM -eq 6 ]; then
    frequency=666
elif [ $HIPREP_DIM -eq 9 ]; then
    frequency=555
else
    echo "unsupported hiprep setup"
    exit 1
fi

#DIM_FILE=$LIB_DIR/hiprep_dim.h

if [ -z $1 ]; then
    echo -n "Error: missing arguments: hiprep size, matrix dimension, "
    echo "number of Rocket tiles"
    exit 1
fi
if [ -z $2 ]; then
    echo -n "Error: missing arguments: matrix dimension, "
    echo "number of Rocket tiles"
    exit 1
fi
if [ -z $3 ]; then
    echo "number of Rocket tiles"
    exit 1
fi
if ((TILES == 1)); then
    TILES=
fi

echo Tiles = $TILES, $CORES



make clean -C $BIN_DIR


make -C $BIN_DIR OPERATIONS=$MAX_OPS \
HIPREP_DIM=$HIPREP_DIM ROWS_PARAM=$MATRIX_DIM COLS_PARAM=$MATRIX_DIM N_CORES=$CORES FLOAT=$FLOAT PRELOAD=$PRELOAD EOT=$EOT main_strong.riscv || exit 1;

make -j20 -C $SIM_DIR CONFIG=HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config \
VERILATOR_THREADS=8 timeout_cycles=$TIME_OUT || exit 1

make -j20 -C $SIM_DIR CONFIG=HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config \
VERILATOR_THREADS=8 timeout_cycles=$TIME_OUT run-binary \
BINARY=$BIN_DIR/main_strong.riscv || exit 1
bp_c=$(grep backpressure $SIM_DIR/output/chipyard.TestHarness.HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config/main_strong.out | wc -l)
sent_c=$(grep statistic $SIM_DIR/output/chipyard.TestHarness.HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config/main_strong.out | grep valid | wc -l)
idle_c=$(grep statistic $SIM_DIR/output/chipyard.TestHarness.HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config/main_strong.out | grep none | wc -l)
total_c=$((bp_c + sent_c + idle_c))
total_c_eff=$((total_c / CORES))
total_t=$((total_c_eff * 1000 / frequency)) # time in nanoseconds
echo "(${CORES},$((HIPREP_DIM * HIPREP_DIM)), ${total_t}), # nanoseconds" >> $OUTFILE

rm $SIM_DIR/output/chipyard.TestHarness.HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config/main_strong.out
