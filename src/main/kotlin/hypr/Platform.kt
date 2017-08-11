package hypr

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.ModelAndView
import spark.Spark.*
import com.squareup.moshi.*
import spark.TemplateEngine
import spark.template.velocity.VelocityTemplateEngine

import org.ektorp.*
import org.ektorp.http.*
import org.ektorp.impl.*
import org.ektorp.support.*
import com.fasterxml.jackson.annotation.*

val logger: Logger = LoggerFactory.getLogger("main")

@JsonIgnoreProperties("id", "revision")
data class Generator(
  @JsonProperty("name") var name: String, 
  @JsonProperty("_id") var id:String,
  @JsonProperty("_rev") var revision: String? = null
) 
fun main(args: Array<String>) {

    val application = Platform(8080)
    application.start()
}


class GeneratorRepository(db: CouchDbConnector) : CouchDbRepositorySupport<Generator>(Generator::class.java, db) {
}

class Platform(val port: Int = 8080) {

    val renderEngine: TemplateEngine
        get() = VelocityTemplateEngine()

    fun start() {
        val httpClient:HttpClient = StdHttpClient.Builder().url("http://localhost:5984").build()

        val dbInstance:CouchDbInstance  = StdCouchDbInstance(httpClient)
        val db:CouchDbConnector = StdCouchDbConnector("generators", dbInstance)

        var repo :GeneratorRepository = GeneratorRepository(db)

        db.createDatabaseIfNotExists()

        port(8080)

        get("/v1/models.json") { req, res ->
          res.type("application/json")
          val results = repo.getAll()
          Gson().toJson(results)
        }
        post("/v1/models.json") { req, res ->
          res.type("application/json")
          repo.add(Generator("Test", "test"))
        }
    }

    fun render(templateName: String,
               model: Any = emptyMap<String, String>()) = renderEngine.render(ModelAndView(model, "templates/$templateName"))

}

