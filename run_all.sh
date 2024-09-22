#!/bin/bash

TIMEOUT=80


CHIPYARD_DIR=$(realpath .)

set -euo pipefail
SIM_DIR=$CHIPYARD_DIR/sims/verilator/

export _JAVA_OPTIONS="-Xms1G -Xmx16G"

LOG_FILE=last_run.log

echo "Generating simulator configurations..."
make --silent -j -C $SIM_DIR CONFIG=HiPRePRocketConfig VERILATOR_THREADS=8 || exit 1
make --silent -j -C $SIM_DIR CONFIG=HiPReP2x2RocketConfig VERILATOR_THREADS=8 || exit 1
make --silent -j -C $SIM_DIR CONFIG=HiPReP3x3RocketConfig VERILATOR_THREADS=8 || exit 1

make -C $HIPREP_DIR/src/main/cpp

TESTS=$(find $HIPREP_DIR/src/test/resources -name "main.c")
for i in $TESTS; do make -C $(dirname $i);  done

echo "Log\n" > $LOG_FILE

#make --silent -j -C $SIM_DIR CONFIG=HiPReP3x3RocketConfig VERILATOR_THREADS=8 run-binary BINARY=$COREMARK_DIR/main.riscv | grep UART -A 2 >> $LOG_FILE || exit 1

for i in $(find $CHIPYARD_DIR/generators/hiprep/src/test/resources/instructions -name main.riscv); do
	echo "$i"
	echo "$i" >> $LOG_FILE || exit 1
	timeout $TIMEOUT make --silent -j -C $SIM_DIR CONFIG=HiPRePRocketConfig VERILATOR_THREADS=8 run-binary BINARY=$i | grep UART -A 2 >> $LOG_FILE || exit 1
done

for i in $(find $CHIPYARD_DIR/generators/hiprep/src/test/resources/RegisterHazards -name main.riscv); do
	echo "$i"
	echo "$i" >> $LOG_FILE || exit 1
	timeout $TIMEOUT make --silent -j -C $SIM_DIR CONFIG=HiPRePRocketConfig VERILATOR_THREADS=8 run-binary BINARY=$i | grep UART -A 2 >> $LOG_FILE || exit 1
done

for i in $(find $CHIPYARD_DIR/generators/hiprep/src/test/resources/AGU -name main.riscv); do
	echo "$i"
	echo "$i" >> $LOG_FILE || exit 1
  timeout $((TIMEOUT * 2)) make --silent -j -C $SIM_DIR CONFIG=HiPReP3x3RocketConfig VERILATOR_THREADS=8 run-binary BINARY=$i | grep UART -A 2 >> $LOG_FILE || exit 1
done

for i in $(find $CHIPYARD_DIR/generators/hiprep/src/test/resources/interPECommunication/basicCommunication -name main.riscv); do
	echo "$i"
	echo "$i" >> $LOG_FILE || exit 1
	timeout $TIMEOUT make --silent -j -C $SIM_DIR CONFIG=HiPReP3x3RocketConfig VERILATOR_THREADS=8 run-binary BINARY=$i | grep UART -A 2 >> $LOG_FILE || exit 1
done
for i in $(find $CHIPYARD_DIR/generators/hiprep/src/test/resources/interPECommunication/prerequesite -name main.riscv); do
	echo "$i"
	echo "$i" >> $LOG_FILE || exit 1
	timeout $TIMEOUT make --silent -j -C $SIM_DIR CONFIG=HiPReP3x3RocketConfig VERILATOR_THREADS=8 run-binary BINARY=$i | grep UART -A 2 >> $LOG_FILE || exit 1
done
for i in $(find $CHIPYARD_DIR/generators/hiprep/src/test/resources/interPECommunication/back_pressure -name main.riscv); do
	echo "$i"
	echo "$i" >> $LOG_FILE || exit 1
	timeout $((TIMEOUT * 2)) make --silent -j -C $SIM_DIR CONFIG=HiPReP3x3RocketConfig VERILATOR_THREADS=8 run-binary BINARY=$i | grep UART -A 2 >> $LOG_FILE || exit 1
done
for i in $(find $CHIPYARD_DIR/generators/hiprep/src/test/resources/interPECommunication/Blocks -name main.riscv); do
	echo "$i"
	echo "$i" >> $LOG_FILE || exit 1
	timeout $((TIMEOUT * 3)) make --silent -j -C $SIM_DIR CONFIG=HiPReP2x2RocketConfig VERILATOR_THREADS=8 run-binary BINARY=$i | grep UART -A 2 >> $LOG_FILE || exit 1
done

echo "$CHIPYARD_DIR/generators/hiprep/src/test/resources/misc/map"
echo "$CHIPYARD_DIR/generators/hiprep/src/test/resources/misc/map" >> $LOG_FILE || exit 1
timeout $((TIMEOUT * 2)) make --silent -j -C $SIM_DIR CONFIG=HiPReP3x3RocketConfig VERILATOR_THREADS=8 run-binary BINARY=$CHIPYARD_DIR/generators/hiprep/src/test/resources/misc/map/main.riscv | grep UART -A 2 >> $LOG_FILE || exit 1

echo "$CHIPYARD_DIR/generators/hiprep/src/test/resources/misc/fir2"
echo "$CHIPYARD_DIR/generators/hiprep/src/test/resources/misc/fir2" >> $LOG_FILE || exit 1
timeout $((TIMEOUT * 2)) make --silent -j -C $SIM_DIR CONFIG=HiPReP3x3RocketConfig VERILATOR_THREADS=8 run-binary BINARY=$CHIPYARD_DIR/generators/hiprep/src/test/resources/misc/fir2/main.riscv | grep UART -A 2 >> $LOG_FILE || exit 1

echo "$CHIPYARD_DIR/generators/hiprep/src/test/resources/AGU/multiCast/advanced"
echo "$CHIPYARD_DIR/generators/hiprep/src/test/resources/AGU/multiCast/advanced" >> $LOG_FILE || exit 1
timeout $TIMEOUT make --silent -j -C $SIM_DIR CONFIG=HiPReP3x3RocketConfig VERILATOR_THREADS=8 run-binary BINARY=$CHIPYARD_DIR/generators/hiprep/src/test/resources/AGU/multiCast/advanced/main.riscv | grep UART -A 2 >> $LOG_FILE || exit 1

echo "$CHIPYARD_DIR/generators/hiprep/src/test/resources/misc/twoconfs"
echo "$CHIPYARD_DIR/generators/hiprep/src/test/resources/misc/twoconfs" >> $LOG_FILE || exit 1
timeout $((TIMEOUT * 4)) make --silent -j -C $SIM_DIR CONFIG=HiPReP3x3RocketConfig VERILATOR_THREADS=8 run-binary BINARY=$CHIPYARD_DIR/generators/hiprep/src/test/resources/misc/twoconfs/main.riscv | grep UART -A 2 >> $LOG_FILE || exit 1

echo "$CHIPYARD_DIR/generators/hiprep/src/test/resources/misc/flow_control_backpressure"
echo "$CHIPYARD_DIR/generators/hiprep/src/test/resources/misc/flow_control_backpressure" >> $LOG_FILE || exit 1
timeout $TIMEOUT make --silent -j -C $SIM_DIR CONFIG=HiPReP3x3RocketConfig VERILATOR_THREADS=8 run-binary BINARY=$CHIPYARD_DIR/generators/hiprep/src/test/resources/misc/flow_control_backpressure/main.riscv | grep UART -A 2 >> $LOG_FILE || exit 1

