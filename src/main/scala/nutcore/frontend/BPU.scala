package nutcore.frontend

import nutcore.{FuOpType, NutCoreBundle}
import chisel3._


class BPUUpdateReq extends NutCoreBundle {
  val valid         = Output(Bool())
  val pc            = Output(UInt(VAddrBits.W))
  val isMissPredict = Output(Bool())
  val actualTarget  = Output(UInt(VAddrBits.W))
  val actualTaken   = Output(Bool()) // for branch
  val fuOpType      = Output(FuOpType())
  val btbType       = Output(BTBtype())
  val isRVC         = Output(Bool()) // for ras, save PC+2 to stack if is RVC
}

object BTBtype {
  def B = "b00".U // branch

  def J = "b01".U // jump

  def I = "b10".U // indirect

  def R = "b11".U // return

  def apply() = UInt(2.W)
}