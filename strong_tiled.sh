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


if [ -z $FLOAT ]; then
    OUTFILE=roofline_int_${HIPREP_DIM}h_${CORES}t.csv
else
    OUTFILE=strong_scaling_tiles_${HIPREP_DIM}h.csv
fi

make clean -C $BIN_DIR

make -C $BIN_DIR N_PARAM=$MATRIX_DIM OPERATIONS=$MAX_OPS \
HIPREP_DIM=$HIPREP_DIM N_CORES=$CORES FLOAT=$FLOAT EOT=$EOT main_roofline.riscv || exit 1;

make -j -C $SIM_DIR CONFIG=HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config \
VERILATOR_THREADS=8 timeout_cycles=$TIME_OUT || exit 1

make -j -C $SIM_DIR CONFIG=HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config \
VERILATOR_THREADS=8 timeout_cycles=$TIME_OUT run-binary \
BINARY=$BIN_DIR/main_roofline.riscv || exit 1
bp=$(grep backpressure $SIM_DIR/output/chipyard.TestHarness.HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config/main_roofline.out | wc -l)
sent=$(grep statistic $SIM_DIR/output/chipyard.TestHarness.HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config/main_roofline.out | grep valid | wc -l)
idle=$(grep statistic $SIM_DIR/output/chipyard.TestHarness.HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config/main_roofline.out | grep none | wc -l)
total=$((bp + sent + idle))
eff=$((total / CORES))
#bp_eff=$((bp / CORES))
#sent_eff=$((sent / CORES))
#idle_eff=$((idle / CORES))
config_cycles=$(grep 'Configuring *' $SIM_DIR/output/chipyard.TestHarness.HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config/main_roofline.out | wc -l)
config_cycles_eff=$((config_cycles / CORES))
data_movements=$((MATRIX_DIM*MATRIX_DIM+(MATRIX_DIM+MATRIX_DIM)*MAX_OPS))
flops=$((MATRIX_DIM*MATRIX_DIM*(MAX_OPS+MAX_OPS-1)))
echo ${CORES},${MAX_OPS},${config_cycles_eff},${eff},=$((flops*100/data_movements))/100,$((flops*frequency/(eff-config_cycles_eff))) >> $OUTFILE

rm $SIM_DIR/output/chipyard.TestHarness.HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config/main_roofline.out
