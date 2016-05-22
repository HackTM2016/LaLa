package com.reactivespot.lala.service

import com.reactivespot.lala.domain.{ApiMessage, Company}


trait CompanyService {
  def register(company: Company): ApiMessage
}
