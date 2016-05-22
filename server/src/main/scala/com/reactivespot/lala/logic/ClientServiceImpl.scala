package com.reactivespot.lala.logic

import com.reactivespot.lala.db.{CompanyDAO, VisitStatDAO}
import com.reactivespot.lala.domain._
import com.reactivespot.lala.service.ClientService
import com.reactivespot.lala.util.GEOUtils
import org.apache.log4j.Logger
import org.joda.time.DateTime


class ClientServiceImpl(
                         companyDAO: CompanyDAO,
                         visitStatDAO: VisitStatDAO
                       )
  extends ClientService {

  val logger = Logger.getLogger(classOf[ClientServiceImpl])

  override def getNearestPlaces(lat: Double, lon: Double): Array[Place] = {
    logger.info(f"Get nearest places to: ($lat, $lon)")

    val places = companyDAO.readCompanies(lat, lon, 100) map { c =>
      Place(c.email, c.name, c.loc.coordinates(0), c.loc.coordinates(1), c.range, c.logo)
    }

    val filteredPlaces = places filter { p =>
      GEOUtils.getDistanceInMeters(Coordinates(p.lat, p.lon), Coordinates(lat, lon)) < p.range
    }

    filteredPlaces.toArray
  }

  override def chose(companyID: String): Unit = {
    logger.info(f"New customer in: $companyID")

    val date = new DateTime()
    val h = date.getHourOfDay
    val d = date.getDayOfWeek

    val stat = visitStatDAO.readVisitStat(companyID, h, d) match {
      case Some(s) => {
        s.copy(nr=s.nr+1)
      }
      case None => VisitStat(companyID, h, d, 1)
    }

    visitStatDAO.updateVisitStat(stat)
  }

  override def heatmapNow(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Array[Heatness] = {
    logger.info("Requesting \"real-time\" heatmap")

    val mLat = (Math.max(lat1, lat2) + Math.min(lat1, lat2)) /2.0
    val mLon = (Math.max(lon1,lon2) + Math.min(lon1, lon2))/2.0
    val r = GEOUtils.getDistanceInMeters(Coordinates(mLat, mLon), Coordinates(lat1, lon1)).toInt

    val now = new DateTime()
    val h = now.getHourOfDay
    val d = now.getDayOfWeek

    val companies = companyDAO.readCompanies(mLat, mLon, r)
    val stats = companies map { c =>
      val empty = VisitStat(c.email, h, d, 0)
      val stat1 = visitStatDAO.readVisitStat(c.email, h-1, d).getOrElse(empty)
      val stat2 = visitStatDAO.readVisitStat(c.email, h, d).getOrElse(empty)

      Heatness(c.loc.coordinates(0), c.loc.coordinates(1), stat1.nr+stat2.nr)
    }

    stats.toArray
  }

  override def heatmapHistory(lat1: Double, lon1: Double, lat2: Double, lon2: Double, h: Int): Array[Heatness] = {
    logger.info(f"Requesting history heatmap in: $h")

    val mLat = (Math.max(lat1, lat2) + Math.min(lat1, lat2)) /2.0
    val mLon = (Math.max(lon1,lon2) + Math.min(lon1, lon2))/2.0
    val r = GEOUtils.getDistanceInMeters(Coordinates(mLat, mLon), Coordinates(lat1, lon1)).toInt

    val now = new DateTime()
    val nowh = now.getHourOfDay

    val companies = companyDAO.readCompanies(mLat, mLon, r)
    val stats = companies map { c =>
      val empty = VisitStat(c.email, h, 0, 0)
      val nr: Int = visitStatDAO.readVisitStat(c.email, h).foldLeft(empty)((a: VisitStat, b: VisitStat)=>VisitStat(a.companyID, a.h, 0, a.nr+b.nr)).nr

      Heatness(c.loc.coordinates(0), c.loc.coordinates(1), nr)
    }

    stats.toArray
  }
}
