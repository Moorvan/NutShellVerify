package nutcore

import chisel3._
import chisel3.util._
import nutcore.backend.fu._
import nutcore.backend.ooo.HasBackendConst
import top.Settings

trait HasNutCoreParameter {
  // General Parameter for NutShell
  val XLEN                      = if (Settings.get("IsRV32")) 32 else 64
  val HasMExtension             = true
  val HasCExtension             = Settings.get("EnableRVC")
  val HasDiv                    = true
  val HasIcache                 = Settings.get("HasIcache")
  val HasDcache                 = Settings.get("HasDcache")
  val HasITLB                   = Settings.get("HasITLB")
  val HasDTLB                   = Settings.get("HasDTLB")
  val AddrBits                  = 64 // AddrBits is used in some cases
  val VAddrBits                 = if (Settings.get("IsRV32")) 32 else 39 // VAddrBits is Virtual Memory addr bits
  val PAddrBits                 = 32 // PAddrBits is Phyical Memory addr bits
  val AddrBytes                 = AddrBits / 8 // unused
  val DataBits                  = XLEN
  val DataBytes                 = DataBits / 8
  val EnableVirtualMemory       = if (Settings.get("HasDTLB") && Settings.get("HasITLB")) true else false
  val EnablePerfCnt             = true
  // Parameter for Argo's OoO backend
  val EnableMultiIssue          = Settings.get("EnableMultiIssue")
  val EnableOutOfOrderExec      = Settings.get("EnableOutOfOrderExec")
  val EnableMultiCyclePredictor = false // false unless a customized condition branch predictor is included
  val EnableOutOfOrderMemAccess = false // enable out of order mem access will improve OoO backend's performance
}

trait HasNutCoreConst extends HasNutCoreParameter {
  val CacheReadWidth        = 8
  val ICacheUserBundleWidth = VAddrBits * 2 + 9
  val DCacheUserBundleWidth = 16
  val IndependentBru        = if (Settings.get("EnableOutOfOrderExec")) true else false
}

trait HasNutCoreLog {
  this: RawModule =>
  implicit val moduleName: String = this.name
}

abstract class NutCoreModule extends Module with HasNutCoreParameter with HasNutCoreConst with HasExceptionNO with HasBackendConst with HasNutCoreLog

abstract class NutCoreBundle extends Bundle with HasNutCoreParameter with HasNutCoreConst with HasBackendConst

case class NutCoreConfig(
                          FPGAPlatform: Boolean = true,
                          EnableDebug: Boolean = Settings.get("EnableDebug"),
                          EnhancedLog: Boolean = true
                        )

// Enable EnhancedLog will slow down simulation,
// but make it possible to control debug log using emu parameter

object AddressSpace extends HasNutCoreParameter {
  // (start, size)
  // address out of MMIO will be considered as DRAM
  def mmio = List(
    (0x30000000L, 0x10000000L),  // internal devices, such as CLINT and PLIC
    (Settings.getLong("MMIOBase"), Settings.getLong("MMIOSize")) // external devices
  )

  def isMMIO(addr: UInt) = mmio.map(range => {
    require(isPow2(range._2))
    val bits = log2Up(range._2)
    (addr ^ range._1.U)(PAddrBits-1, bits) === 0.U
  }).reduce(_ || _)
}