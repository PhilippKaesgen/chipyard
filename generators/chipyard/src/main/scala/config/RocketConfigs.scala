package chipyard

import org.chipsalliance.cde.config.{Config}
import freechips.rocketchip.prci.{AsynchronousCrossing}
import freechips.rocketchip.subsystem.{InCluster}

// --------------
// Rocket Configs
// --------------

class RocketConfig extends Config(
  new freechips.rocketchip.rocket.WithNHugeCores(1) ++         // single rocket-core
  new chipyard.config.AbstractConfig)

class DualRocketConfig extends Config(
  new freechips.rocketchip.rocket.WithNHugeCores(2) ++
  new chipyard.config.AbstractConfig)

class TinyRocketConfig extends Config(
  new chipyard.harness.WithDontTouchChipTopPorts(false) ++        // TODO FIX: Don't dontTouch the ports
  new testchipip.soc.WithNoScratchpads ++                         // All memory is the Rocket TCMs
  new freechips.rocketchip.subsystem.WithIncoherentBusTopology ++ // use incoherent bus topology
  new freechips.rocketchip.subsystem.WithNBanks(0) ++             // remove L2$
  new freechips.rocketchip.subsystem.WithNoMemPort ++             // remove backing memory
  new freechips.rocketchip.rocket.With1TinyCore ++                // single tiny rocket-core
  new chipyard.config.AbstractConfig)

class QuadRocketConfig extends Config(
  new freechips.rocketchip.rocket.WithNHugeCores(4) ++    // quad-core (4 RocketTiles)
  new chipyard.config.AbstractConfig)

class Cloned64RocketConfig extends Config(
  new freechips.rocketchip.rocket.WithCloneRocketTiles(63, 0) ++ // copy tile0 63 more times
  new freechips.rocketchip.rocket.WithNHugeCores(1) ++            // tile0 is a BigRocket
  new chipyard.config.AbstractConfig)

class RV32RocketConfig extends Config(
  new freechips.rocketchip.rocket.WithRV32 ++            // set RocketTiles to be 32-bit
  new freechips.rocketchip.rocket.WithNHugeCores(1) ++
  new chipyard.config.AbstractConfig)

// DOC include start: l1scratchpadrocket
class ScratchpadOnlyRocketConfig extends Config(
  new chipyard.config.WithL2TLBs(0) ++
  new testchipip.soc.WithNoScratchpads ++                      // remove subsystem scratchpads, confusingly named, does not remove the L1D$ scratchpads
  new freechips.rocketchip.subsystem.WithNBanks(0) ++
  new freechips.rocketchip.subsystem.WithNoMemPort ++          // remove offchip mem port
  new freechips.rocketchip.rocket.WithScratchpadsOnly ++       // use rocket l1 DCache scratchpad as base phys mem
  new freechips.rocketchip.rocket.WithNHugeCores(1) ++
  new chipyard.config.AbstractConfig)
// DOC include end: l1scratchpadrocket

class MMIOScratchpadOnlyRocketConfig extends Config(
  new freechips.rocketchip.subsystem.WithDefaultMMIOPort ++  // add default external master port
  new freechips.rocketchip.subsystem.WithDefaultSlavePort ++ // add default external slave port
  new chipyard.config.WithL2TLBs(0) ++
  new testchipip.soc.WithNoScratchpads ++                      // remove subsystem scratchpads, confusingly named, does not remove the L1D$ scratchpads
  new freechips.rocketchip.subsystem.WithNBanks(0) ++
  new freechips.rocketchip.subsystem.WithNoMemPort ++          // remove offchip mem port
  new freechips.rocketchip.rocket.WithScratchpadsOnly ++       // use rocket l1 DCache scratchpad as base phys mem
  new freechips.rocketchip.rocket.WithNHugeCores(1) ++
  new chipyard.config.AbstractConfig)

class L1ScratchpadRocketConfig extends Config(
  new chipyard.config.WithRocketICacheScratchpad ++         // use rocket ICache scratchpad
  new chipyard.config.WithRocketDCacheScratchpad ++         // use rocket DCache scratchpad
  new freechips.rocketchip.rocket.WithNHugeCores(1) ++
  new chipyard.config.AbstractConfig)

class MulticlockRocketConfig extends Config(
  new freechips.rocketchip.rocket.WithAsynchronousCDCs(8, 3) ++ // Add async crossings between RocketTile and uncore
  new freechips.rocketchip.rocket.WithNHugeCores(1) ++
  // Frequency specifications
  new chipyard.config.WithTileFrequency(1000.0) ++        // Matches the maximum frequency of U540
  new chipyard.clocking.WithClockGroupsCombinedByName(("uncore"   , Seq("sbus", "cbus", "implicit", "clock_tap"), Nil),
                                                      ("periphery", Seq("pbus", "fbus"), Nil)) ++
  new chipyard.config.WithSystemBusFrequency(500.0) ++    // Matches the maximum frequency of U540
  new chipyard.config.WithMemoryBusFrequency(500.0) ++    // Matches the maximum frequency of U540
  new chipyard.config.WithPeripheryBusFrequency(500.0) ++ // Matches the maximum frequency of U540
  //  Crossing specifications
  new chipyard.config.WithFbusToSbusCrossingType(AsynchronousCrossing()) ++ // Add Async crossing between FBUS and SBUS
  new chipyard.config.WithCbusToPbusCrossingType(AsynchronousCrossing()) ++ // Add Async crossing between PBUS and CBUS
  new chipyard.config.WithSbusToMbusCrossingType(AsynchronousCrossing()) ++ // Add Async crossings between backside of L2 and MBUS
  new chipyard.config.AbstractConfig)

class CustomIOChipTopRocketConfig extends Config(
  new chipyard.example.WithBrokenOutUARTIO ++
  new chipyard.example.WithCustomChipTop ++
  new chipyard.example.WithCustomIOCells ++
  new freechips.rocketchip.rocket.WithNHugeCores(1) ++         // single rocket-core
  new chipyard.config.AbstractConfig)

class PrefetchingRocketConfig extends Config(
  new barf.WithHellaCachePrefetcher(Seq(0), barf.SingleStridedPrefetcherParams()) ++   // strided prefetcher, sits in front of the L1D$, monitors core requests to prefetching into the L1D$
  new barf.WithTLICachePrefetcher(barf.MultiNextLinePrefetcherParams()) ++             // next-line prefetcher, sits between L1I$ and L2, monitors L1I$ misses to prefetch into L2
  new barf.WithTLDCachePrefetcher(barf.SingleAMPMPrefetcherParams()) ++                // AMPM prefetcher, sits between L1D$ and L2, monitors L1D$ misses to prefetch into L2
  new chipyard.config.WithTilePrefetchers ++                                           // add TL prefetchers between tiles and the sbus
  new freechips.rocketchip.rocket.WithL1DCacheNonblocking(2) ++                        // non-blocking L1D$, L1 prefetching only works with non-blocking L1D$
  new freechips.rocketchip.rocket.WithNHugeCores(1) ++                                  // single rocket-core
  new chipyard.config.AbstractConfig)

class ClusteredRocketConfig extends Config(
  new freechips.rocketchip.rocket.WithNHugeCores(4, location=InCluster(1)) ++
  new freechips.rocketchip.rocket.WithNHugeCores(4, location=InCluster(0)) ++
  new freechips.rocketchip.subsystem.WithCluster(1) ++
  new freechips.rocketchip.subsystem.WithCluster(0) ++
  new chipyard.config.AbstractConfig)

class FastRTLSimRocketConfig extends Config(
  new freechips.rocketchip.subsystem.WithoutTLMonitors ++
  new freechips.rocketchip.rocket.WithNHugeCores(1) ++
  new chipyard.config.AbstractConfig)

class SV48RocketConfig extends Config(
  new freechips.rocketchip.rocket.WithSV48 ++
  new freechips.rocketchip.rocket.WithNHugeCores(1) ++
  new chipyard.config.AbstractConfig)

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
