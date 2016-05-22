package com.reactivespot.lala.logic

import com.reactivespot.lala.db.CompanyDAO
import com.reactivespot.lala.domain.{SuccessMessage, ErrorMessage, ApiMessage, Company}
import com.reactivespot.lala.service.CompanyService
import org.apache.log4j.Logger


class CompanyServiceImpl(
                          companyDAO: CompanyDAO
                        )
  extends CompanyService {

  val logger = Logger.getLogger(classOf[CompanyServiceImpl])

  override def register(company: Company): ApiMessage = {
    companyDAO.readCompany(company.email) match {
      case Some(c) => ErrorMessage("Email already exists")
      case None => {
        logger.info(f"Registering company with email: ${company.email}")
        companyDAO.createCompany(company)
        SuccessMessage("Registered")
      }
    }
  }

}
