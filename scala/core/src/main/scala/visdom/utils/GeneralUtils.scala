package visdom.utils

import java.math.BigInteger
import java.security.MessageDigest


object GeneralUtils {
    def toInt(stringValue: String): Option[Int] = {
        try {
            Some(stringValue.toInt)
        } catch {
            case _: java.lang.NumberFormatException => None
        }
    }

    val ShaFunction: String = "SHA-512/256"
    val Encoding: String = "UTF-8"
    val MessageDigester: MessageDigest = MessageDigest.getInstance(ShaFunction)

    def getHash(inputString: String): String = {
        val digest = MessageDigester.digest(inputString.getBytes(Encoding))
        String.format(s"%0${digest.length * 2}x", new BigInteger(1, digest))
    }

    def getHash(inputString: String, useHash: Boolean): String = {
        useHash match {
            case true => getHash(inputString)
            case false => inputString
        }
    }

    implicit class EnrichedWithToTuple[A](elements: Seq[A]) {
        def toTuple1: Tuple1[A] = elements match { case Seq(a) => Tuple1(a) }
        def toTuple2: (A, A) = elements match { case Seq(a, b) => (a, b) }
        def toTuple3: (A, A, A) = elements match { case Seq(a, b, c) => (a, b, c) }
        def toTuple4: (A, A, A, A) = elements match { case Seq(a, b, c, d) => (a, b, c, d) }
        def toTuple5: (A, A, A, A, A) = elements match { case Seq(a, b, c, d, e) => (a, b, c, d, e) }
        def toTuple6: (A, A, A, A, A, A) = elements match { case Seq(a, b, c, d, e, f) => (a, b, c, d, e, f) }
    }
}
