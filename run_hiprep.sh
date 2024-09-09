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

export _JAVA_OPTIONS="-Xms1G -Xmx16G"

HIPREP_DIM=3

BIN=$1/main.riscv

make -j -C $SIM_DIR CONFIG=HiPReP${HIPREP_DIM}x${HIPREP_DIM}RocketConfig VERILATOR_THREADS=8 run-binary-debug timeout_cycles=100000000 BINARY=$BIN || exit 1


bp_c=$(grep backpressure $SIM_DIR/output/chipyard.TestHarness.HiPReP${HIPREP_DIM}x${HIPREP_DIM}RocketConfig/main.out | wc -l)
sent_c=$(grep statistic $SIM_DIR/output/chipyard.TestHarness.HiPReP${HIPREP_DIM}x${HIPREP_DIM}RocketConfig/main.out | grep valid | wc -l)
idle_c=$(grep statistic $SIM_DIR/output/chipyard.TestHarness.HiPReP${HIPREP_DIM}x${HIPREP_DIM}RocketConfig/main.out | grep none | wc -l)
total_c=$((bp_c + sent_c + idle_c))
config_c=$(grep 'Configuring *' $SIM_DIR/output/chipyard.TestHarness.HiPReP${HIPREP_DIM}x${HIPREP_DIM}RocketConfig/main.out | wc -l)

echo "bp:       ${bp_c}"
echo "sent:     ${sent_c}"
echo "idle:     ${idle_c}"
echo "total:    ${total_c}"
echo "config:   ${config_c}"
echo "bp ratio: $((bp_c*100/(sent_c+bp_c)))%"
