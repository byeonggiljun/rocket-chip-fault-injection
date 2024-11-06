package freechips.rocketchip.rocket

import scala.util.Random
import chisel3._
import chisel3.util._
import org.chipsalliance.cde.config.Parameters
import freechips.rocketchip.tile.CoreModule
import freechips.rocketchip.util._

class FaultInjection(implicit p: Parameters) extends CoreModule()(p) {
  val io = IO(new Bundle {
    val randomSel2 = Output(Bool())
    val bitFlipMask = Output(UInt(xLen.W))
    // val bitFlipMask = Output(UInt(32.W))
  })

  val cnt = Module(new Counter)

  when(cnt.io.count === 255.U) {
    val random = new scala.util.Random(System.currentTimeMillis())

    // Generate a random bit (0 or 1)
    io.randomSel2 := (random.nextInt(10) < 5).B

    // val position = chisel3.util.random.LFSR(log2Ceil(xLen))
    val position = random.nextInt(log2Ceil(xLen))

    // io.bitFlipMask := (1.U << position)(32 - 1, 0)
    io.bitFlipMask := 1.U << position
  } .otherwise {
    io.randomSel2 := false.B
    io.bitFlipMask := 0.U
  }
}

class Counter extends Module {
  val io = IO(new Bundle {
    val count = Output(UInt(log2Ceil(256).W))
  })
  val (counter, done) = Counter(true.B, 256)
  io.count := counter
}