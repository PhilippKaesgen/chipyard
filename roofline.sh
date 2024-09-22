#!/bin/bash

# https://dando18.github.io/posts/2020/04/02/roofline-model


CHIPYARD_DIR=$(realpath .)

set -euo pipefail
SIM_DIR=$CHIPYARD_DIR/sims/verilator

BIN_DIR=$(realpath generators/hiprep/src/test/resources/Benchmarks/matrix_matrix_mul)

TIME_OUT=10000000000000000000000000000
HIPREP_DIM=$1
TILES=$3
ROW_DIM=$((60*TILES))  #$((TILES*HIPREP_DIM))
COL_DIM=60 #$((HIPREP_DIM*2))
CORES=$3
FLOAT=y
PRELOAD=
EOT=
MAX_OPS=$2
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
    echo "number of Rocket tiles"
    exit 1
fi
if ((TILES == 1)); then
    TILES=
fi

echo Tiles = $TILES, $CORES

make clean -C $BIN_DIR

make -C $BIN_DIR ROWS_PARAM=$ROW_DIM COLS_PARAM=$COL_DIM OPERATIONS=$MAX_OPS \
HIPREP_DIM=$HIPREP_DIM N_CORES=$CORES FLOAT=$FLOAT PRELOAD=$PRELOAD EOT=$EOT main_roofline.riscv || exit 1;

make -j20 -C $SIM_DIR CONFIG=HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config VERILATOR_THREADS=8 timeout_cycles=${TIME_OUT} run-binary BINARY=$BIN_DIR/main_roofline.riscv || exit 1
LOGFILE=$SIM_DIR/output/chipyard.TestHarness.HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config/main_roofline.out
bp=$(grep backpressure ${LOGFILE} | wc -l)
sent=$(grep statistic ${LOGFILE} | grep valid | wc -l)
idle=$(grep statistic ${LOGFILE} | grep none | wc -l)
total=$((bp + sent + idle))
eff=$((total / CORES))
#bp_eff=$((bp / CORES))
#sent_eff=$((sent / CORES))
#idle_eff=$((idle / CORES))
config_cycles=$(grep 'Configuring *' ${LOGFILE} | wc -l)
config_cycles_eff=$((config_cycles / CORES))

echo ${LATEX_TABLE}

data_movements_c=$((ROW_DIM*COL_DIM))*4
data_movements_a=$((ROW_DIM*MAX_OPS))*4
data_movements_b=$((COL_DIM*MAX_OPS))*4
data_movements=$((data_movements_c+data_movements_a+data_movements_b))
flops_total=$((ROW_DIM*COL_DIM*(MAX_OPS+MAX_OPS-1)))
arithm_intensity_100=$((flops_total*100/data_movements))
flops_per_sec_no_config=$((flops_total*frequency/(eff-config_cycles_eff)))
flops_per_sec=$((flops_total*frequency/eff))

echo ${CORES},$((HIPREP_DIM*HIPREP_DIM)),$((HIPREP_DIM*HIPREP_DIM*CORES)),$((ROW_DIM*COL_DIM)),${config_cycles_eff},$((config_cycles_eff/frequency)),${eff},$((eff/frequency)),=${arithm_intensity_100}/100,${flops_per_sec_no_config},${flops_per_sec} >> $OUTFILE

echo "${arithm_intensity_100} ${flops_per_sec_no_config} t${CORES}p$((HIPREP_DIM*HIPREP_DIM))" >> ${LATEX_TABLE}
rm $LOGFILE
