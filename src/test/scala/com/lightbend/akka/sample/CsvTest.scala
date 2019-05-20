package com.lightbend.akka.sample

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.csv.scaladsl.CsvParsing
import akka.stream.scaladsl.{Sink, Source}
import akka.testkit.TestKit
import akka.util.ByteString
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

class CsvTest
    extends TestKit(ActorSystem("CsvTest"))
    with FlatSpecLike
    with Matchers
    with BeforeAndAfterAll
    with Eventually
    with ScalaFutures
    with IntegrationPatience {

  implicit val mat = ActorMaterializer()

  it should "read all lines without final line end" in {
    val data =
      """r1c1,r1c2
        |r2c1,r2c2""".stripMargin

    val result = Source(data.map(ByteString(_)))
      .via(CsvParsing.lineScanner())
      .runWith(Sink.seq)
      .futureValue

    result should have size 2
  }

  it should "read all lines without final line end and last column empty" in {
    val data =
      """r1c1,r1c2
        |r2c1,""".stripMargin

    val result = Source(data.map(ByteString(_)))
      .via(CsvParsing.lineScanner())
      .runWith(Sink.seq)
      .futureValue

    result should have size 2
  }

  override protected def afterAll(): Unit = {
    mat.shutdown()
    TestKit.shutdownActorSystem(system)
    super.afterAll()
  }
}
