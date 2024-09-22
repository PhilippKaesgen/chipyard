package chipyard

import freechips.rocketchip.config.{Config}
import freechips.rocketchip.diplomacy.{AsynchronousCrossing}

// --------------
// Rocket Configs
// --------------

class RocketConfig extends Config(
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++         // single rocket-core
  new chipyard.config.AbstractConfig)

class TinyRocketConfig extends Config(
  new chipyard.config.WithTLSerialLocation(
    freechips.rocketchip.subsystem.FBUS,
    freechips.rocketchip.subsystem.PBUS) ++                       // attach TL serial adapter to f/p busses
  new freechips.rocketchip.subsystem.WithIncoherentBusTopology ++ // use incoherent bus topology
  new freechips.rocketchip.subsystem.WithNBanks(0) ++             // remove L2$
  new freechips.rocketchip.subsystem.WithNoMemPort ++             // remove backing memory
  new freechips.rocketchip.subsystem.With1TinyCore ++             // single tiny rocket-core
  new chipyard.config.AbstractConfig)

class MempressRocketConfig extends Config(
  new mempress.WithMemPress ++                                    // use Mempress (memory traffic generation) accelerator
  new freechips.rocketchip.subsystem.WithNBanks(8) ++
  new freechips.rocketchip.subsystem.WithInclusiveCache(nWays=16, capacityKB=2048) ++
  new chipyard.config.WithExtMemIdBits(7) ++                      // use 7 bits for tl like request id
  new freechips.rocketchip.subsystem.WithNMemoryChannels(4) ++
  new chipyard.config.WithSystemBusWidth(128) ++
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)

// DOC include start: FFTRocketConfig
class FFTRocketConfig extends Config(
  new fftgenerator.WithFFTGenerator(numPoints=8, width=16, decPt=8) ++ // add 8-point mmio fft at the default addr (0x2400) with 16bit fixed-point numbers.
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)
// DOC include end: FFTRocketConfig

class HwachaRocketConfig extends Config(
  new chipyard.config.WithHwachaTest ++
  new hwacha.DefaultHwachaConfig ++                              // use Hwacha vector accelerator
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)

// DOC include start: GemminiRocketConfig
class GemminiRocketConfig extends Config(
  new gemmini.DefaultGemminiConfig ++                            // use Gemmini systolic array GEMM accelerator
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)
// DOC include end: GemminiRocketConfig

class FPGemminiRocketConfig extends Config(
  new gemmini.GemminiFP32DefaultConfig ++                         // use FP32Gemmini systolic array GEMM accelerator
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)


// DOC include start: DmiRocket
class dmiRocketConfig extends Config(
  new chipyard.harness.WithSerialAdapterTiedOff ++               // don't attach an external SimSerial
  new chipyard.config.WithDMIDTM ++                              // have debug module expose a clocked DMI port
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)
// DOC include end: DmiRocket

// DOC include start: GCDTLRocketConfig
class GCDTLRocketConfig extends Config(
  new chipyard.example.WithGCD(useAXI4=false, useBlackBox=false) ++          // Use GCD Chisel, connect Tilelink
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)
// DOC include end: GCDTLRocketConfig

// DOC include start: GCDAXI4BlackBoxRocketConfig
class GCDAXI4BlackBoxRocketConfig extends Config(
  new chipyard.example.WithGCD(useAXI4=true, useBlackBox=true) ++            // Use GCD blackboxed verilog, connect by AXI4->Tilelink
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)
// DOC include end: GCDAXI4BlackBoxRocketConfig

class LargeSPIFlashROMRocketConfig extends Config(
  new chipyard.harness.WithSimSPIFlashModel(true) ++        // add the SPI flash model in the harness (read-only)
  new chipyard.config.WithSPIFlash ++                       // add the SPI flash controller
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)

class SmallSPIFlashRocketConfig extends Config(
  new chipyard.harness.WithSimSPIFlashModel(false) ++       // add the SPI flash model in the harness (writeable)
  new chipyard.config.WithSPIFlash(0x100000) ++             // add the SPI flash controller (1 MiB)
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)

class SimAXIRocketConfig extends Config(
  new chipyard.harness.WithSimAXIMem ++                     // drive the master AXI4 memory with a SimAXIMem, a 1-cycle magic memory, instead of default SimDRAM
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)

class SimBlockDeviceRocketConfig extends Config(
  new chipyard.harness.WithSimBlockDevice ++                // drive block-device IOs with SimBlockDevice
  new testchipip.WithBlockDevice ++                         // add block-device module to peripherybus
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)

class BlockDeviceModelRocketConfig extends Config(
  new chipyard.harness.WithBlockDeviceModel ++              // drive block-device IOs with a BlockDeviceModel
  new testchipip.WithBlockDevice ++                         // add block-device module to periphery bus
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)

// DOC include start: GPIORocketConfig
class GPIORocketConfig extends Config(
  new chipyard.config.WithGPIO ++                           // add GPIOs to the peripherybus
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)
// DOC include end: GPIORocketConfig

class QuadRocketConfig extends Config(
  new freechips.rocketchip.subsystem.WithNBigCores(4) ++    // quad-core (4 RocketTiles)
  new chipyard.config.AbstractConfig)

class RV32RocketConfig extends Config(
  new freechips.rocketchip.subsystem.WithRV32 ++            // set RocketTiles to be 32-bit
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)

class GB1MemoryRocketConfig extends Config(
  new freechips.rocketchip.subsystem.WithExtMemSize((1<<30) * 1L) ++ // use 1GB simulated external memory
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)

// DOC include start: InitZeroRocketConfig
class InitZeroRocketConfig extends Config(
  new chipyard.example.WithInitZero(0x88000000L, 0x1000L) ++   // add InitZero
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)
// DOC include end: InitZeroRocketConfig

class LoopbackNICRocketConfig extends Config(
  new chipyard.harness.WithLoopbackNIC ++                      // drive NIC IOs with loopback
  new icenet.WithIceNIC ++                                     // add an IceNIC
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)

// DOC include start: l1scratchpadrocket
class ScratchpadOnlyRocketConfig extends Config(
  new testchipip.WithSerialPBusMem ++
  new chipyard.config.WithL2TLBs(0) ++
  new freechips.rocketchip.subsystem.WithNBanks(0) ++
  new freechips.rocketchip.subsystem.WithNoMemPort ++          // remove offchip mem port
  new freechips.rocketchip.subsystem.WithScratchpadsOnly ++    // use rocket l1 DCache scratchpad as base phys mem
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)
// DOC include end: l1scratchpadrocket

class MMIOScratchpadOnlyRocketConfig extends Config(
  new freechips.rocketchip.subsystem.WithDefaultMMIOPort ++  // add default external master port
  new freechips.rocketchip.subsystem.WithDefaultSlavePort ++ // add default external slave port
  new ScratchpadOnlyRocketConfig
)

class L1ScratchpadRocketConfig extends Config(
  new chipyard.config.WithRocketICacheScratchpad ++         // use rocket ICache scratchpad
  new chipyard.config.WithRocketDCacheScratchpad ++         // use rocket DCache scratchpad
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)

// DOC include start: mbusscratchpadrocket
class MbusScratchpadRocketConfig extends Config(
  new testchipip.WithBackingScratchpad ++                   // add mbus backing scratchpad
  new freechips.rocketchip.subsystem.WithNoMemPort ++       // remove offchip mem port
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)
// DOC include end: mbusscratchpadrocket

// DOC include start: RingSystemBusRocket
class RingSystemBusRocketConfig extends Config(
  new testchipip.WithRingSystemBus ++                       // Ring-topology system bus
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)
// DOC include end: RingSystemBusRocket

class StreamingPassthroughRocketConfig extends Config(
  new chipyard.example.WithStreamingPassthrough ++          // use top with tilelink-controlled streaming passthrough
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)

// DOC include start: StreamingFIRRocketConfig
class StreamingFIRRocketConfig extends Config (
  new chipyard.example.WithStreamingFIR ++                  // use top with tilelink-controlled streaming FIR
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)
// DOC include end: StreamingFIRRocketConfig

class SmallNVDLARocketConfig extends Config(
  new nvidia.blocks.dla.WithNVDLA("small") ++               // add a small NVDLA
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)

class LargeNVDLARocketConfig extends Config(
  new nvidia.blocks.dla.WithNVDLA("large", true) ++         // add a large NVDLA with synth. rams
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)

class MMIORocketConfig extends Config(
  new freechips.rocketchip.subsystem.WithDefaultMMIOPort ++  // add default external master port
  new freechips.rocketchip.subsystem.WithDefaultSlavePort ++ // add default external slave port
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)

class MulticlockRocketConfig extends Config(
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  // Frequency specifications
  new chipyard.config.WithTileFrequency(1600.0) ++       // Matches the maximum frequency of U540
  new chipyard.config.WithSystemBusFrequency(800.0) ++   // Ditto
  new chipyard.config.WithMemoryBusFrequency(1000.0) ++  // 2x the U540 freq (appropriate for a 128b Mbus)
  new chipyard.config.WithPeripheryBusFrequency(100) ++  // Retains the default pbus frequency
  new chipyard.config.WithSystemBusFrequencyAsDefault ++ // All unspecified clock frequencies, notably the implicit clock, will use the sbus freq (800 MHz)
  //  Crossing specifications
  new chipyard.config.WithCbusToPbusCrossingType(AsynchronousCrossing()) ++ // Add Async crossing between PBUS and CBUS
  new chipyard.config.WithSbusToMbusCrossingType(AsynchronousCrossing()) ++ // Add Async crossings between backside of L2 and MBUS
  new freechips.rocketchip.subsystem.WithRationalRocketTiles ++   // Add rational crossings between RocketTile and uncore
  new testchipip.WithAsynchronousSerialSlaveCrossing ++ // Add Async crossing between serial and MBUS. Its master-side is tied to the FBUS
  new chipyard.config.AbstractConfig)

class TestChipMulticlockRocketConfig extends Config(
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.WithTestChipBusFreqs ++
  new chipyard.config.AbstractConfig)

class LBWIFRocketConfig extends Config(
  new testchipip.WithSerialTLMem(isMainMemory=true) ++      // set lbwif memory base to DRAM_BASE, use as main memory
  new freechips.rocketchip.subsystem.WithNoMemPort ++       // remove AXI4 backing memory
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig)

// DOC include start: MulticlockAXIOverSerialConfig
class MulticlockAXIOverSerialConfig extends Config(
  new chipyard.config.WithSystemBusFrequencyAsDefault ++
  new chipyard.config.WithSystemBusFrequency(250) ++
  new chipyard.config.WithPeripheryBusFrequency(250) ++
  new chipyard.config.WithMemoryBusFrequency(250) ++
  new chipyard.config.WithFrontBusFrequency(50) ++
  new chipyard.config.WithTileFrequency(500, Some(1)) ++
  new chipyard.config.WithTileFrequency(250, Some(0)) ++

  new chipyard.config.WithFbusToSbusCrossingType(AsynchronousCrossing()) ++
  new testchipip.WithAsynchronousSerialSlaveCrossing ++
  new freechips.rocketchip.subsystem.WithAsynchronousRocketTiles(
    AsynchronousCrossing().depth,
    AsynchronousCrossing().sourceSync) ++

  new chipyard.harness.WithSimAXIMemOverSerialTL ++ // add SimDRAM DRAM model for axi4 backing memory over the SerDes link, if axi4 mem is enabled
  new chipyard.config.WithSerialTLBackingMemory ++ // remove axi4 mem port in favor of SerialTL memory

  new freechips.rocketchip.subsystem.WithNBigCores(2) ++
  new chipyard.config.AbstractConfig)
// DOC include end: MulticlockAXIOverSerialConfig
class L1DTestConfig extends Config(
  // L1 specification
  // 8 words per block, 8B per word, 8 ways, 64 sets => 32KiB
  // CacheBlockBytes = 64 (default: subsystem/BankedL2Params.scala)
  new freechips.rocketchip.subsystem.WithL1DCacheSets(32 * 1024 / 64 /*CacheBlockBytes*/ / 8/*ways*/) ++ 
  new freechips.rocketchip.subsystem.WithL1DCacheWays(8) ++
  new freechips.rocketchip.subsystem.WithNonblockingL1(2) ++
  
  // L2 specification
  new freechips.rocketchip.subsystem.WithInclusiveCache(nWays=16, capacityKB=2048) ++  // L2 U540 
  new freechips.rocketchip.subsystem.WithNBanks(8) ++

  // Memory specification
  new freechips.rocketchip.subsystem.WithNMemoryChannels(4) ++ // Memory
  new chipyard.harness.WithSimAXIMemOverSerialTL ++ // add SimDRAM DRAM model for axi4 backing memory over the SerDes link, if axi4 mem is enabled
  new chipyard.config.WithSerialTLBackingMemory ++ // remove axi4 mem port in favor of SerialTL memory

  new freechips.rocketchip.subsystem.WithCacheBlockBytes(64) ++

  new boom.common.WithNLargeBooms(1) ++                          // large boom config
  //new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new chipyard.config.AbstractConfig
)

// architecture block diagram: https://chipyard.readthedocs.io/en/stable/Generators/Rocket-Chip.html
class HiPRePHostConfig(frequency: Float, numTiles: Int = 1) extends Config(

  // Frequency specifications
  new chipyard.config.WithTileFrequency(frequency) ++       // Matches the maximum frequency of U540
  new chipyard.config.WithSystemBusFrequency(1000.0) ++   // Ditto
  new chipyard.config.WithMemoryBusFrequency(1000.0) ++ //1e-6/(1.5e-10)) ++  // 1000MHz for the U540 freq (appropriate for a 128b Mbus)
  //new chipyard.config.WithFrontBusFrequency(1000) ++
  //new chipyard.config.WithControlBusFrequency(1000.0) ++
  new chipyard.config.WithPeripheryBusFrequency(800.0) ++
  new chipyard.config.WithSystemBusFrequencyAsDefault ++
  
  //  Crossing specifications
  new chipyard.config.WithCbusToPbusCrossingType(AsynchronousCrossing()) ++ // Add Async crossing between PBUS and CBUS
  new chipyard.config.WithSbusToMbusCrossingType(AsynchronousCrossing()) ++ 

  // L1 specification
  // 8 words per block, 8B per word, 8 ways, 64 sets => 32KiB
  // CacheBlockBytes = 64 (default: subsystem/BankedL2Params.scala)
  new freechips.rocketchip.subsystem.WithL1DCacheSets(32 * 1024 / 64 /*CacheBlockBytes*/ / 8/*ways*/) ++ 
  new freechips.rocketchip.subsystem.WithL1DCacheWays(8) ++
  new freechips.rocketchip.subsystem.WithNonblockingL1(2) ++
  
  // L2 specification
  new freechips.rocketchip.subsystem.WithInclusiveCache(nWays=16, capacityKB=2048) ++  // L2 U540 
  new freechips.rocketchip.subsystem.WithNBanks(8) ++

  // Memory specification
  new freechips.rocketchip.subsystem.WithNMemoryChannels(4) ++ // Memory
  new chipyard.harness.WithSimAXIMemOverSerialTL ++ // add SimDRAM DRAM model for axi4 backing memory over the SerDes link, if axi4 mem is enabled
  new chipyard.config.WithSerialTLBackingMemory ++ // remove axi4 mem port in favor of SerialTL memory

  new freechips.rocketchip.subsystem.WithCacheBlockBytes(64) ++


  //new testchipip.WithRingSystemBus ++                       // Ring-topology system bus
  new freechips.rocketchip.subsystem.WithNBigCores(numTiles) ++
  new chipyard.config.AbstractConfig
)

class HiPReP3x3Rocket4Config extends Config(
  new hiprep.HiPRePConfig(3,3) ++
  new HiPRePHostConfig(800, 4))

class HiPReP3x3Rocket2Config extends Config(
  new hiprep.HiPRePConfig(3,3) ++
  new HiPRePHostConfig(800, 2))

class HiPReP3x3Rocket8Config extends Config(
  new hiprep.HiPRePConfig(3,3) ++
  new HiPRePHostConfig(800, 8))

class HiPReP3x3Rocket16Config extends Config(
  new hiprep.HiPRePConfig(3,3) ++
  new HiPRePHostConfig(800, 16))

class HiPReP3x3Rocket32Config extends Config(
  new hiprep.HiPRePConfig(3,3) ++
  new HiPRePHostConfig(800, 32))

class HiPReP3x3SynRocketConfig extends Config(
  new hiprep.HiPRePConfig(3,3, 2, 1) ++
  new HiPRePHostConfig(800))

class HiPReP6x6SynRocketConfig extends Config(
  new hiprep.HiPRePConfig(6,6, 2, 1) ++
  new HiPRePHostConfig(666))

class HiPReP9x9SynRocketConfig extends Config(
  new hiprep.HiPRePConfig(9,9, 2, 1) ++
  new HiPRePHostConfig(555))

class HiPReP1x1SynRocketConfig extends Config(
  new hiprep.HiPRePConfig(1,1, 2, 1) ++
  new HiPRePHostConfig(800))

class HiPReP2x2SynRocketConfig extends Config(
  new hiprep.HiPRePConfig(2,2, 2, 1) ++
  new HiPRePHostConfig(800))

class HiPReP4x4SynRocketConfig extends Config(
  new hiprep.HiPRePConfig(4,4, 2, 1) ++
  new HiPRePHostConfig(750))

class HiPReP5x5SynRocketConfig extends Config(
  new hiprep.HiPRePConfig(5,5, 2, 1) ++
  new HiPRePHostConfig(700))

class SimpleHiPReP3x3SynORRocketConfig extends Config(
  new hiprep.SimpleHiPRePConfig(3,3, 2, 2) ++
  new HiPRePHostConfig(800))


class HiPReP3x3SynORRocketConfig extends Config(
  new hiprep.HiPRePConfig(3,3, 2, 2) ++
  new HiPRePHostConfig(800))

class HiPRePRocketConfig extends Config(
  new hiprep.HiPRePConfig(1,1) ++
  new HiPRePHostConfig(800))

class HiPReP1x1RocketConfig extends Config(
  new hiprep.HiPRePConfig(1,1) ++
  new HiPRePHostConfig(800))

class HiPReP2x2RocketConfig extends Config(
  new hiprep.HiPRePConfig(2,2) ++
  new HiPRePHostConfig(800))

class HiPReP3x3RocketConfig extends Config(
  new hiprep.HiPRePConfig(3,3) ++
  new HiPRePHostConfig(800))

class HiPReP4x4RocketConfig extends Config(
  new hiprep.HiPRePConfig(4,4) ++
  new HiPRePHostConfig(750))

class HiPReP4x4Rocket2Config extends Config(
  new hiprep.HiPRePConfig(4,4) ++
  new HiPRePHostConfig(750,2))

class HiPReP4x4Rocket4Config extends Config(
  new hiprep.HiPRePConfig(4,4) ++
  new HiPRePHostConfig(750,4))

class HiPReP4x4Rocket8Config extends Config(
  new hiprep.HiPRePConfig(4,4) ++
  new HiPRePHostConfig(750,8))

class HiPReP4x4Rocket16Config extends Config(
  new hiprep.HiPRePConfig(4,4) ++
  new HiPRePHostConfig(750,16))

class HiPReP4x4Rocket32Config extends Config(
  new hiprep.HiPRePConfig(4,4) ++
  new HiPRePHostConfig(750,32))

class HiPReP5x5RocketConfig extends Config(
  new hiprep.HiPRePConfig(5,5) ++
  new HiPRePHostConfig(700))

class HiPReP5x5Rocket2Config extends Config(
  new hiprep.HiPRePConfig(5,5) ++
  new HiPRePHostConfig(700,2))

class HiPReP5x5Rocket4Config extends Config(
  new hiprep.HiPRePConfig(5,5) ++
  new HiPRePHostConfig(700,4))

class HiPReP5x5Rocket8Config extends Config(
  new hiprep.HiPRePConfig(5,5) ++
  new HiPRePHostConfig(700,8))

class HiPReP5x5Rocket16Config extends Config(
  new hiprep.HiPRePConfig(5,5) ++
  new HiPRePHostConfig(700, 16))

class HiPReP5x5Rocket32Config extends Config(
  new hiprep.HiPRePConfig(5,5) ++
  new HiPRePHostConfig(700, 32))

class HiPReP6x6RocketConfig extends Config(
  new hiprep.HiPRePConfig(6,6) ++
  new HiPRePHostConfig(666))

class HiPReP6x6Rocket2Config extends Config(
  new hiprep.HiPRePConfig(6,6) ++
  new HiPRePHostConfig(666,2))

class HiPReP6x6Rocket4Config extends Config(
  new hiprep.HiPRePConfig(6,6) ++
  new HiPRePHostConfig(666,4))

class HiPReP6x6Rocket8Config extends Config(
  new hiprep.HiPRePConfig(6,6) ++
  new HiPRePHostConfig(666,8))

class HiPReP6x6Rocket16Config extends Config(
  new hiprep.HiPRePConfig(6,6) ++
  new HiPRePHostConfig(666,16))

class HiPReP6x6Rocket32Config extends Config(
  new hiprep.HiPRePConfig(6,6) ++
  new HiPRePHostConfig(666,32))

class HiPReP7x7RocketConfig extends Config(
  new hiprep.HiPRePConfig(7,7) ++
  new HiPRePHostConfig(666))

class HiPReP8x8RocketConfig extends Config(
  new hiprep.HiPRePConfig(8,8) ++
  new HiPRePHostConfig(666))

class HiPReP9x9RocketConfig extends Config(
  new hiprep.HiPRePConfig(9,9) ++
  new HiPRePHostConfig(555))

class HiPReP9x9Rocket2Config extends Config(
  new hiprep.HiPRePConfig(9,9) ++
  new HiPRePHostConfig(555,2))

class HiPReP9x9Rocket4Config extends Config(
  new hiprep.HiPRePConfig(9,9) ++
  new HiPRePHostConfig(555,4))

class HiPReP9x9Rocket8Config extends Config(
  new hiprep.HiPRePConfig(9,9) ++
  new HiPRePHostConfig(555,8))

class HiPReP9x9Rocket16Config extends Config(
  new hiprep.HiPRePConfig(9,9) ++
  new HiPRePHostConfig(555,16))
