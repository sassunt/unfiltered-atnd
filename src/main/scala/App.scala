package com.example

import unfiltered.request._
import unfiltered.response._
import org.scala_tools.time.Imports._

class AtndSearcher extends unfiltered.filter.Plan {
  import scala.xml.XML
  import AtndSearch._
  import EventBuilder._

  private val language = List("scala", "haskell", "java")

  def intent = {
    case GET(Path(p)) => p match {
      case "/" => ResponseString("root")
      case Seg(lang :: Nil) if language contains lang =>
        Ok ~> view(Map.empty) {
          <table>
            <tr>
              <th>イベント名</th>
              <th>開催日時</th>
              <th>定員</th>
            </tr>{
              create(XML.load("%s?keyword=%s&ym=%s".format(ATND_URL, lang, yearMonth))).map{ event =>
                <tr>
                  <td><a href={event.url}>{event.title}</a></td>
                  <td>{event.started_at match {
                                          case Some(d) => d toString "yyyy/MM/dd hh:mm"
                                          case None => ""
                                        }
                      }
                  </td>
                  <td>{event.limit}</td>
                </tr>
              }
          }</table>
        }
      case _ => MethodNotAllowed ~> ResponseString("Must be GET")
    }
  }

  def view(params: Map[String, Seq[String]])(body: scala.xml.NodeSeq) = {
    def p(k: String) = params.get(k).flatMap { _.headOption } getOrElse("")
    Html(
     <html>
      <head>
        <link rel="stylesheet" type="text/css" href="/css/app.css" />
        <script type="text/javascript" src="/js/app.js"></script>
      </head>
      <body>
        <div id="container">
          ATNDのイベント(今月)
          { body }
        </div>
      </body>
     </html>
    )
  }
}

object AtndSearch {
  import java.net.URL
  import DateTime.now

  // ATND リクエストURL
  val ATND_URL = "http://api.atnd.org/events/"

  def yearMonth = now.toString("yyyyMM")

  implicit def stringToURL(url: String): URL = new java.net.URL(url)
}

/**
 * Atndのイベント
 *
 */
class Event(
  val id: String,
  val title: String,         // タイトル
  val url: String,           // URL
  val started_at: Option[DateTime],    // イベント開催日時
  val ended_at: Option[DateTime],      // イベント終了日時
  val address: String,       // 開催場所
  val limit: String,            // 定員
  val accepted: String,         // 参加人数
  val waiting: String           // 補欠人数
){
}

object EventBuilder {
  def create(xml: scala.xml.Elem): List[Event] = {
    for( event        <- (xml \\ "event").toList;
         id           <- (event \ "event_id");
         title        <- (event \ "title");
         url          <- (event \ "event_url");
         started_at   <- (event \ "started_at");
         ended_at     <- (event \ "ended_at");
         address      <- (event \ "address");
         limit        <- (event \ "limit");
         accepted     <- (event \ "accepted");
         waiting      <- (event \ "waiting")
    ) yield new Event(id.text,
                      title.text,
                      url.text,
                      isoFmtParse(started_at.text),
                      isoFmtParse(ended_at.text),
                      address.text,
                      limit.text,
                      accepted.text,
                      waiting.text
                     )
  }

  private def isoFmtParse(date: String) = {
    import org.joda.time.format.ISODateTimeFormat
    val dt = ISODateTimeFormat.dateTimeNoMillis
    try {
      Some(dt parseDateTime date)
    } catch {
      case _ => None
    }
  }
}
