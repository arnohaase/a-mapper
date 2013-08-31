package com.ajjpj.amapper.core


/**
 * @author arno
 */
trait AContextExtractor {
  def withContext(context: Map[String, AnyRef], o: AnyRef, tpe: AType): Map[String, AnyRef]
}

object NoContextExtractor extends AContextExtractor {
  def withContext(context: Map[String, AnyRef], o: AnyRef, tpe: AType): Map[String, AnyRef] = context
}

class CompositeContextExtractor(contributions: Seq[PartialFunction[AType, AContextExtractor]]) extends AContextExtractor {
  // volatile is sufficient to guarantee correctness: it trades the potential of redundant initialization for faster
  //  access after initialization ramp-up
  @volatile var cache = Map[AType, AContextExtractor]()

  def withContext(context: Map[String, AnyRef], o: AnyRef, tpe: AType): Map[String, AnyRef] = {
    cache.get(tpe) match {
      case Some(ce) =>
        ce.withContext(context, o, tpe)
      case None =>
        contributions.find(_.isDefinedAt(tpe)) match {
          case Some(ctr) =>
            val ce = ctr(tpe)
            cache += (tpe -> ce)
            ce.withContext(context, o, tpe)
          case None =>
            cache += (tpe -> NoContextExtractor)
            context
        }
    }
  }
}