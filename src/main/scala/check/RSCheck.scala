package check

import chisel3._
import chiselFv._
import nutcore.backend.ooo._


class RSCheck extends Module with Formal {
  val rs = Module(new RS(size = 4))
  rs.io := DontCare

  when(rs.io.out.valid) {
    assert(rs.io.out.bits.src1Rdy === true.B)
    assert(rs.io.out.bits.src2Rdy === true.B)
  }
}

object RSCheck extends App {
  //  Check.generateRTL(() => new RSCheck())
  Check.kInduction(() => new RSCheck)
}