package check

import chisel3._
import chisel3.util._
import chiselFv._
import nutcore.{HasNutCoreParameter, HasRegFileParameter, RegFile}


class RFCheck extends Module with Formal with HasRegFileParameter with HasNutCoreParameter {
  val addrW = log2Ceil(NRReg)
  val io    = IO(new Bundle() {
    val wrData = Input(UInt(XLEN.W))
    val wrAddr = Input(UInt(addrW.W))
  })
  val rf    = new RegFile

  val addr = anyconst(addrW)
  val data = Reg(UInt(XLEN.W))
  val flag = initialReg(1, 0)

  flag.io.in := 0.U
  when(addr === io.wrAddr) {
    flag.io.in := 1.U
    when(addr === 0.U) {
      data := 0.U
    }.otherwise {
      data := io.wrData
    }
  }
  rf.write(io.wrAddr, io.wrData)

  when(flag.io.out === 1.U) {
    assert(data === rf.read(addr))
  }

}

object RFCheck extends App {
  Check.kInduction(() => new RFCheck)
}