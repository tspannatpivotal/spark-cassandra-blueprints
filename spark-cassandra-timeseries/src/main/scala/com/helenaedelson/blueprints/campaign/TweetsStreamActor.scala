package com.helenaedelson.blueprints.campaign

import akka.actor.Actor
import org.apache.spark.Logging
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{StreamingContext, Seconds}
import org.apache.spark.streaming.twitter.TwitterUtils
import twitter4j.auth.Authorization

/**
 * Basic - just threw code in here to show.
 * Creates a input stream that returns tweets received from Twitter.
 *
 * A Twitter api key is required to be set in your environment: export TWITTER_KEY="yourkey".
 *
 * @param ssc         StreamingContext object
 * @param twitterAuth Twitter4J authentication, or None to use Twitter4J's default OAuth
 *        authorization; this uses the system properties twitter4j.oauth.consumerKey,
 *        twitter4j.oauth.consumerSecret, twitter4j.oauth.accessToken and
 *        twitter4j.oauth.accessTokenSecret
 * @param filters Set of filter strings to get only those tweets that match them
 * @param storageLevel Storage level to use for storing the received objects
 */
class TweetsStreamActor(ssc: StreamingContext, twitterAuth: Authorization, filters: Seq[String] = Nil,
                  storageLevel: StorageLevel = StorageLevel.MEMORY_ONLY) extends Actor with Logging {

  /**
   * Return a new DStream in which each RDD has a single element generated by counting the number
   * of elements in a sliding window over this DStream. Hash partitioning is used to generate
   * the RDDs with Spark's default number of partitions.
   *
   * windowDuration width of the window; must be a multiple of this DStream's
   *                       batching interval
   * slideDuration  sliding interval of the window (i.e., the interval after which
   *                       the new DStream will generate RDDs); must be a multiple of this
   *                       DStream's batching interval
   */
  val stream = TwitterUtils.createStream(ssc, Some(twitterAuth), filters, storageLevel)
    .filter(_.getText.contains("Spark"))
    .countByWindow(windowDuration = Seconds(4), slideDuration = Seconds(2))
    .print()

  ssc.start()


  def receive : Actor.Receive = {
    case e =>
  }
}


