package check

import chisel3._
import chiselFv._
import chisel3.util._
import nutcore.FuType.XLEN
import nutcore.backend.fu._
import nutcore.backend.ooo.ALUEP


class ALUCheck extends Module with Formal {
  val io  = IO(new Bundle() {
    val a  = Input(UInt(XLEN.W))
    val b  = Input(UInt(XLEN.W))
    val op = Input(UInt(7.W))
  })
  val alu = Module(new ALU())
  alu.io := DontCare

  val a  = io.a
  val b  = io.b
  val op = io.op

  val res = alu.access(true.B, a, b, op)

  switch(op) {
    is(ALUOpType.and) {
      assert(res === (a & b))
    }
    is(ALUOpType.or) {
      assert(res === (a | b))
    }
    is(ALUOpType.add) {
      assert(res === (a + b))
    }
    is(ALUOpType.sub) {
      assert(res === (a - b))
    }
    is(ALUOpType.xor) {
      assert(res === (a ^ b))
    }
  }


}

class ALUEPCheck extends Module with Formal {
  val io  = IO(new Bundle() {
    val a  = Input(UInt(XLEN.W))
    val b  = Input(UInt(XLEN.W))
    val op = Input(UInt(7.W))
  })
  val alu = Module(new ALUEP())
  alu.io := DontCare

  val a  = io.a
  val b  = io.b
  val op = io.op

  val res = alu.fvAccess(true.B, a, b, op)

  switch(op) {
    is(ALUOpType.and) {
      assert(res === (a & b))
    }
    is(ALUOpType.or) {
      assert(res === (a | b))
    }
    is(ALUOpType.add) {
      assert(res === (a + b))
    }
    is(ALUOpType.sub) {
      assert(res === (a - b))
    }
    is(ALUOpType.xor) {
      assert(res === (a ^ b))
    }
  }
}


object ALUCheck extends App {
  //  Check.generateRTL(() => new ALUCheck)
  Check.kInduction(() => new ALUCheck)
//  Check.kInduction(() => new ALUEPCheck)
}