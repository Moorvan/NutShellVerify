package nutcore.backend.ooo

import chisel3._
import chisel3.util._
import nutcore.backend.fu.ALU
import nutcore._


// Out of Order Execution Pipeline for NutShell/Argo

class ExecutionPipelineIO extends NutCoreBundle {
  val in = Flipped(Decoupled(new RenamedDecodeIO))
  val out = Decoupled(new OOCommitIO)
  val mispredictRec = Input(new MisPredictionRecIO)
  val flush = Input(Bool())
}

class ExecutionPipeline extends NutCoreModule {
  val io = IO(new ExecutionPipelineIO)
  def access(uop: Data): Data = {
    this.io.in := uop
    io.out
  }
  def updateBrMask(brMask: UInt) = {
    brMask & (~ (UIntToOH(io.mispredictRec.checkpoint) & Fill(checkpointSize, io.mispredictRec.valid))).asUInt
  }
  io.out.bits.isMMIO := false.B
  io.out.bits.intrNO := 0.U
  io.out.bits.exception := false.B
  io.out.bits.store := false.B
}

class ALUEP extends ExecutionPipeline {
  val alu = Module(new ALU())
  alu.io.in.valid := io.in.valid
  alu.io.in.bits.src1 := io.in.bits.decode.data.src1
  alu.io.in.bits.src2 := io.in.bits.decode.data.src2
  alu.io.in.bits.func := io.in.bits.decode.ctrl.fuOpType
  alu.io.cfIn := io.in.bits.decode.cf
  alu.io.offset := io.in.bits.decode.data.imm
  alu.io.out.ready := io.out.ready

  io.out.bits.decode := io.in.bits.decode
  io.out.bits.decode.cf.redirect.valid := false.B
  io.out.bits.decode.cf.redirect.rtype := DontCare
  io.out.bits.commits := alu.io.out.bits
  io.out.bits.prfidx := io.in.bits.prfDest
  io.out.bits.brMask := io.in.bits.brMask

  io.in.ready := alu.io.in.ready
  io.out.valid := alu.io.out.valid

  def fvAccess(valid: Bool, src1: UInt, src2: UInt, func: UInt): UInt = {
    val in = this.alu.io.in
    in.valid := valid
    in.bits.src1 := src1
    in.bits.src2 := src2
    in.bits.func := func
    io.out.bits.commits
  }
}


