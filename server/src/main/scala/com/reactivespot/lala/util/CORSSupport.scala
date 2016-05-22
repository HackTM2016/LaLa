package com.reactivespot.lala.util

import spray.http.HttpHeaders._
import spray.http._
import spray.routing._


// see also https://developer.mozilla.org/en-US/docs/Web/HTTP/Access_control_CORS
trait CORSSupport {
  this: HttpService =>

  private val allowHeaders = `Access-Control-Allow-Headers`("Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, Referer, User-Agent, Access-Control-Allow-Headers")
  private val allowOriginHeader = `Access-Control-Allow-Origin`(AllOrigins)
  private val optionsCorsHeaders = List(
    `Access-Control-Allow-Headers`("Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, Referer, User-Agent, Access-Control-Allow-Headers"),
    `Access-Control-Max-Age`(1728000)
  )

  def cors[T]: Directive0 = mapRequestContext { ctx =>

    ctx.withRouteResponseHandling({
      // OPTION request for a resource that responds to other methods
      case Rejected(x) if (ctx.request.method.equals(HttpMethods.OPTIONS) && !x.filter(_.isInstanceOf[MethodRejection]).isEmpty) => {
        val allowedMethods: List[HttpMethod] = x.filter(_.isInstanceOf[MethodRejection]).map(rejection => {
          rejection.asInstanceOf[MethodRejection].supported
        })
        ctx.complete {
          HttpResponse().withHeaders(
            `Access-Control-Allow-Methods`(HttpMethods.OPTIONS, allowedMethods: _*) :: allowOriginHeader ::
              optionsCorsHeaders
          )
        }
      }
    }).withHttpResponseHeadersMapped { headers =>
      allowHeaders :: allowOriginHeader :: headers
    }

  }

  override def timeoutRoute = complete {
    HttpResponse(
      StatusCodes.InternalServerError,
      HttpEntity(ContentTypes.`text/plain(UTF-8)`, "The server was not able to produce a timely response to your request."),
      List(allowOriginHeader)
    )
  }
}