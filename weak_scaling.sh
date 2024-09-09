#!/bin/bash

# Each of these make variables correspond to a particular part of the design/codebase and are needed so that the make system can correctly build and make a RTL simulation.

# The SBT_PROJECT is the build.sbt project that holds all of the source files and that will be run during the RTL build.

# The MODEL and VLOG_MODEL are the top-level class names of the design. Normally, these are the same, but in some cases these can differ (if the Chisel class differs than what is emitted in the Verilog).

# The MODEL_PACKAGE is the Scala package (in the Scala code that says package ...) that holds the MODEL class.

# The CONFIG is the name of the class used for the parameter config while the CONFIG_PACKAGE is the Scala package it resides in.

# The GENERATOR_PACKAGE is the Scala package that holds the Generator class that elaborates the design.

# The TB is the name of the Verilog wrapper that connects the TestHarness to VCS/Verilator for simulation.

# Finally, the TOP variable is used to distinguish between the top-level of the design and the TestHarness in our system. For example, in the normal case, the MODEL variable specifies the TestHarness as the top-level of the design. However, the true top-level design, the SoC being simulated, is pointed to by the TOP variable. This separation allows the infrastructure to separate files based on the harness or the SoC top level.

# Common configurations of all these variables are packaged using a SUB_PROJECT make variable. Therefore, in order to simulate a simple Rocket-based example system we can use:

# make SUB_PROJECT=yourproject
# ./simulator-<yourproject>-<yourconfig>

CHIPYARD_DIR=$(realpath .)

set -euo pipefail
SIM_DIR=$CHIPYARD_DIR/sims/verilator/

BIN_DIR=$(realpath generators/hiprep/src/test/resources/Benchmarks/matrix_matrix_mul)

TIME_OUT=10000000000000000000000000000
HIPREP_DIM=$1
TILES=$2
ROW_DIM=$((TILES*HIPREP_DIM))
COL_DIM=$((HIPREP_DIM*16))
CORES=$2
FLOAT=y
EOT=
MAX_OPS=64
FLOAT=y
#EOT=y
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
#DIM_FILE=${LIB_DIR}/hiprep_dim.h

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
HIPREP_DIM=$HIPREP_DIM N_CORES=$CORES FLOAT=$FLOAT EOT=$EOT main_weak.riscv || exit 1;

make -j -C $SIM_DIR CONFIG=HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config \
VERILATOR_THREADS=8 timeout_cycles=${TIME_OUT} run-binary \
BINARY=$BIN_DIR/main_weak.riscv || exit 1
bp_c=$(grep backpressure $SIM_DIR/output/chipyard.TestHarness.HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config/main_weak.out | wc -l)
sent_c=$(grep statistic $SIM_DIR/output/chipyard.TestHarness.HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config/main_weak.out | grep valid | wc -l)
idle_c=$(grep statistic $SIM_DIR/output/chipyard.TestHarness.HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config/main_weak.out | grep none | wc -l)
total_c=$((bp_c + sent_c + idle_c))
total_c_eff=$((total_c / CORES))
config_c=$(grep 'Configuring *' $SIM_DIR/output/chipyard.TestHarness.HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config/main_weak.out | wc -l)
config_c_eff=$((config_c / CORES))
total_t=$((total_c_eff * 1000 / frequency))
config_t=$((config_c_eff * 1000 / frequency))

echo ${CORES},$((HIPREP_DIM * HIPREP_DIM)),$((HIPREP_DIM * HIPREP_DIM * CORES)),$((ROW_DIM*COL_DIM)),${config_c_eff},${config_t},${total_c_eff},${total_t} >> $OUTFILE

rm $SIM_DIR/output/chipyard.TestHarness.HiPReP${HIPREP_DIM}x${HIPREP_DIM}Rocket${TILES}Config/main_weak.out
